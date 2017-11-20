package controllers.back.risk;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.common.entity.t_event_supervisor;
import models.common.entity.t_event_supervisor.Event;
import models.common.entity.t_user;
import models.common.entity.t_user_fund;
import models.common.entity.t_user_info;
import models.core.bean.BackRiskBid;
import models.core.bean.BidInvestRecord;
import models.core.bean.BidItemOfSupervisorSubject;
import models.core.bean.BidItemOfUserSubject;
import models.core.bean.FrontProduct;
import models.core.entity.t_agencies;
import models.core.entity.t_bid;
import models.core.entity.t_bid_item_user.BidItemAuditStatus;
import models.core.entity.t_guarantee_mode;
import models.core.entity.t_product;
import models.core.entity.t_product.RepaymentType;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.commons.lang.StringUtils;

import payment.impl.PaymentProxy;
import play.Play;
import play.cache.Cache;
import play.db.jpa.Blob;
import play.mvc.With;
import services.common.UserFundService;
import services.common.UserInfoService;
import services.common.UserService;
import services.core.AgencyService;
import services.core.AuditSubjectBidService;
import services.core.BidItemSupervisorService;
import services.core.BidItemUserService;
import services.core.BidService;
import services.core.BillService;
import services.core.GuaranteeModeService;
import services.core.InvestService;
import services.core.ProductService;

import com.shove.Convert;

import common.annotation.SubmitCheck;
import common.annotation.SubmitOnly;
import common.constants.CacheKey;
import common.constants.ConfConst;
import common.constants.Constants;
import common.enums.Client;
import common.enums.ServiceType;
import common.utils.CropImage;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.ImageMarkUtil;
import common.utils.LoggerUtil;
import common.utils.NoUtil;
import common.utils.OrderNoUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.excel.ExcelUtils;
import common.utils.file.FileUtil;
import common.utils.jsonAxml.JsonDateValueProcessor;
import common.utils.jsonAxml.JsonDoubleValueProcessor;
import controllers.common.BackBaseController;
import controllers.common.ImagesController;
import controllers.common.SubmitRepeat;
import daos.core.GuaranteeModeDao;

/**
 * 后台-风控-理财项目管理控制器
 *
 * @description
 *
 * @author huangyunsong
 * @createDate 2015年12月18日
 */
@With({ SubmitRepeat.class })
public class LoanMngCtrl extends BackBaseController {
	/**
	 * 获取水印图片绝对路径
	 */
	private static final String path = Play.getFile("public" + File.separator + "images" + File.separator + "watermark"+File.separator +"watermark.png").getAbsolutePath();
	
	protected static BidService bidservice = Factory.getService(BidService.class);

	protected static ProductService productService = Factory.getService(ProductService.class);

	protected static UserFundService userfundservice = Factory.getService(UserFundService.class);

	protected static UserService userservice = Factory.getService(UserService.class);

	protected static UserInfoService userinfoservice = Factory.getService(UserInfoService.class);

	protected static AuditSubjectBidService auditsubjectbidservice = Factory.getService(AuditSubjectBidService.class);

	protected static BidItemSupervisorService biditemsupervisorservice = Factory
			.getService(BidItemSupervisorService.class);

	protected static BidItemUserService bidItemUserService = Factory.getService(BidItemUserService.class);

	protected static BillService billService = Factory.getService(BillService.class);

	protected static InvestService investService = Factory.getService(InvestService.class);

	protected static AgencyService agencyService = Factory.getService(AgencyService.class);
	
	protected static GuaranteeModeService guaranteeModeService=Factory.getService(GuaranteeModeService.class);
	/**
	 * 后台-风控-理财项目-显示借款标
	 *
	 * @param showType
	 *            筛选类型 0-所有;1-初审中;2-借款中;3-满标审核;4-还款中;5-已经结束;6-失败
	 * @param currPage
	 * @param pageSize
	 * @param exports
	 *            1:导出 default：不导出
	 * @param numNo
	 *            编号
	 * @param projectName
	 *            项目名称
	 * @param userName
	 *            借款人
	 *
	 * @author yaoyi
	 * @createDate 2016年1月6日
	 */
	public static void showBidPre(int showType, int currPage, int pageSize) {
		String numNo = params.get("numNo");
		String projectName = params.get("projectName");
		String userName = params.get("userName");
		String agencyName = params.get("agencyName");

		String orderTypeStr = params.get("orderType");
		int orderType = Convert.strToInt(orderTypeStr, 0);// 0-编号；3-借款金额；4-年利率；5-期限；6-筹款进度；7-发售时间
		if (orderType != 3 && orderType != 4 && orderType != 5 && orderType != 6 && orderType != 7) {
			orderType = 0;
		}
		renderArgs.put("orderType", orderType);

		String orderValueStr = params.get("orderValue");
		int orderValue = Convert.strToInt(orderValueStr, 0);// 0,降序，1,升序
		if (orderValue < 0 || orderValue > 1) {
			orderValue = 0;
		}
		renderArgs.put("orderValue", orderValue);

		int exports = Convert.strToInt(params.get("exports"), 0);
		if (showType < 0 || showType > 7) {
			showType = 0;
		}

		PageBean<BackRiskBid> pageBean = bidservice.pageOfBidRisk(showType, currPage, pageSize, exports, orderType,
				orderValue, userName, numNo, projectName, agencyName);

		// 导出
		if (exports == Constants.EXPORT) {
			List<BackRiskBid> list = pageBean.page;

			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(Constants.DATE_FORMATE));
			jsonConfig.registerJsonValueProcessor(Double.class,
					new JsonDoubleValueProcessor(Constants.FINANCE_FORMATE_NORMAL));
			JSONArray arrList = JSONArray.fromObject(list, jsonConfig);

			for (Object obj : arrList) {
				JSONObject bid = (JSONObject) obj;

				if (StringUtils.isNotBlank(bid.getString("period_unit"))) {
					bid.put("periods", bid.getString("period")
							+ t_product.PeriodUnit.valueOf(bid.getString("period_unit")).getShowValue());
				}

				if (StringUtils.isNotBlank(bid.getString("status"))) {
					bid.put("status", t_bid.Status.valueOf(bid.getString("status")).value);
				}
			}

			String fileName = "理财项目列表";
			switch (showType) {
			case 1:
				fileName = "初审中理财项目列表";
				break;
			case 2:
				fileName = "借款中理财项目列表";
				break;
			case 3:
				fileName = "满标审核理财项目列表";
				break;
			case 4:
				fileName = "还款中理财项目列表";
				break;
			case 5:
				fileName = "已结束理财项目列表";
				break;
			case 6:
				fileName = "失败理财项目列表";
				break;
			default:
				fileName = "理财项目列表";
				break;
			}

			File file = ExcelUtils.export(fileName, arrList,
					new String[] { "编号", "项目", "借款人", "项目金额", "年利率", "期限", "筹款进度", "发售时间", "状态" },
					new String[] { "bid_no", "title", "name", "amount", "apr", "periods", "loan_schedule",
							"pre_release_time", "status" });

			renderBinary(file, fileName + ".xls");
		}

		double totalAmt = bidservice.findTotalBidAmountRisk(showType, userName, numNo, projectName);

		render(pageBean, totalAmt, showType, userName, numNo, projectName, agencyName);
	}

	/**
	 * 后台-风控-理财项目-借款标列表-借款标详情
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月28日
	 */
	public static void showBidDetailPre(long bidId) {

		if (bidId < 1) {

			showBidPre(0, 1, 10);
		}

		t_bid tb = bidservice.findByID(bidId);
		if (tb == null) {
			flash.error("没有查询到该借款项目");

			showBidPre(0, 1, 10);
		}

		t_user tu = userservice.findUserById(tb.user_id);
		t_user_info tui = userinfoservice.findUserInfoByUserId(tb.user_id);
		t_user_fund tuf = userfundservice.queryUserFundByUserId(tb.user_id);

		Map<String, Object> item = auditsubjectbidservice.queryBidRefSubject(bidId);
		List<BidItemOfUserSubject> userLoop = (List<BidItemOfUserSubject>) item.get("userItem");
		List<BidItemOfSupervisorSubject> supervisorLoop = (List<BidItemOfSupervisorSubject>) item.get("supervisorItem");

		// 详情页面，资料审核功能关闭
		boolean closeAudit = true;
		renderArgs.put("sysNowTime", new Date().getTime());// 服务器时间传到客户端
		List<t_product> t_product = productService.findAll();
		renderArgs.put("t_product", t_product);
		render(tb, tu, tui, tuf, userLoop, supervisorLoop, closeAudit);
	}

	/**
	 * 后台-风控-理财项目-借款标列表-进入标记初审页面
	 *
	 * @param bidId
	 *
	 * @author yaoyi
	 * @createDate 2015年12月28日
	 */
	@SubmitCheck
	public static void toPreAuditBidPre(long bidId) {

		if (bidId < 1) {

			showBidPre(0, 1, 10);
		}

		t_bid tb = bidservice.findByID(bidId);
		if (tb == null) {
			flash.error("没有查询到该借款项目");

			showBidPre(0, 1, 10);
		}

		t_user tu = userservice.findUserById(tb.user_id);
		t_user_info tui = userinfoservice.findUserInfoByUserId(tb.user_id);
		t_user_fund tuf = userfundservice.queryUserFundByUserId(tb.user_id);

		Map<String, Object> item = auditsubjectbidservice.queryBidRefSubject(bidId);
		List<BidItemOfUserSubject> userLoop = (List<BidItemOfUserSubject>) item.get("userItem");
		List<BidItemOfSupervisorSubject> supervisorLoop = (List<BidItemOfSupervisorSubject>) item.get("supervisorItem");
		List<t_product> t_product = productService.findAll();
		renderArgs.put("t_product", t_product);
		render(tb, tu, tui, tuf, userLoop, supervisorLoop);
	}

	/**
	 * 后台-风控-理财项目-借款标列表-进入标记复审页面
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月28日
	 */
	@SubmitCheck
	public static void toAuditBidPre(long bidId) {

		if (bidId < 1) {

			showBidPre(0, 1, 10);
		}

		t_bid tb = bidservice.findByID(bidId);
		if (tb == null) {
			flash.error("没有查询到该借款项目");

			showBidPre(0, 1, 10);
		}

		t_user tu = userservice.findUserById(tb.user_id);
		t_user_info tui = userinfoservice.findUserInfoByUserId(tb.user_id);
		t_user_fund tuf = userfundservice.queryUserFundByUserId(tb.user_id);

		Map<String, Object> item = auditsubjectbidservice.queryBidRefSubject(bidId);
		List<BidItemOfUserSubject> userLoop = (List<BidItemOfUserSubject>) item.get("userItem");
		List<BidItemOfSupervisorSubject> supervisorLoop = (List<BidItemOfSupervisorSubject>) item.get("supervisorItem");

		renderArgs.put("sysNowTime", new Date().getTime());// 服务器时间传到客户端
		List<t_product> t_product = productService.findAll();
		renderArgs.put("t_product", t_product);
		render(tb, tu, tui, tuf, userLoop, supervisorLoop);
	}

	/**
	 * 后台-风控-理财项目-借款标列表-进入发布借款标(理财项目)界面
	 *
	 * @author yaoyi
	 * @createDate 2015年12月21日
	 */
	public static void createBidPre(long productId ,long guaranteeModeId) {
		List<FrontProduct> tpList = productService.queryProductIsUse(true);
		List<t_guarantee_mode> gmList = guaranteeModeService.getAll();
		if (tpList == null || tpList.size() == 0) {
			render();
		}

		if (productId < 1) {
			productId = tpList.get(0).id;
		}

		// 所有启用的合作机构
		List<t_agencies> agList = agencyService.queryAgencyIsUse(true);

		// 回显数据用到的处理方式
		t_bid bid = (t_bid) Cache.get(CacheKey.BID_ + session.getId());
		if (bid != null) {
			renderArgs.put("bid", bid);
			Cache.delete(CacheKey.BID_ + session.getId());
		}

		render(tpList, agList,gmList, productId,guaranteeModeId);
	}

	/**
	 * ajax获取发标时关联用户
	 *
	 * @param key
	 *
	 * @author yaoyi
	 * @createDate 2016年1月9日
	 */
	public static void queryRefUser(int currPage, int pageSize, String key) {

		if (currPage < 1) {
			currPage = 1;
		}
		if (pageSize < 1) {
			pageSize = 10;
		}
		flash.put("searchKey", key);

		// 限制最大长度
		if (StringUtils.isNotBlank(key) && key.length() > 16) {
			key = key.substring(0, 16);
		}

		PageBean<Map<String, Object>> pageBean = userservice.queryCreateBidRefUser(currPage, pageSize, key);

		render(pageBean);
	}

	/**
	 * ajax获取标的信息
	 *
	 * @param productId
	 *
	 * @author yaoyi
	 * @createDate 2016年1月7日
	 */
	public static void getBidInfo(long productId) {
		ResultInfo res = new ResultInfo();

		if (productId < 1) {
			res.code = -1;
			res.msg = "参数错误";

			renderJSON(res);
		}
		t_product tp = productService.queryProduct(productId);
		Map<String, Object> tpMap = new HashMap<String, Object>();
		tpMap.put("code", 1);
		tpMap.put("min_amount", tp.min_amount);
		tpMap.put("max_amount", tp.max_amount);
		tpMap.put("min_apr", tp.min_apr);
		tpMap.put("max_apr", tp.max_apr);
		tpMap.put("period_unit_code", tp.getPeriod_unit().code);
		tpMap.put("period_unit", tp.getPeriod_unit().value);
		tpMap.put("service_fee_rule", tp.service_fee_rule);
		List<Integer> periodList = new ArrayList<Integer>();
		for (int i = tp.min_period; i <= tp.max_period; i++) {
			periodList.add(i);
		}
		tpMap.put("period", periodList);
		List<Integer> repaymentTypeListCode = new ArrayList<Integer>();
		List<String> repaymentTypeListValue = new ArrayList<String>();
		for (String r : tp.repayment_type.split(",")) {
			repaymentTypeListCode.add(Integer.parseInt(r.trim()));
			repaymentTypeListValue.add(RepaymentType.getEnum(Integer.parseInt(r.trim())).value);
		}
		tpMap.put("repayment_type_code", repaymentTypeListCode);
		tpMap.put("repayment_type_value", repaymentTypeListValue);

		res.code = 1;
		res.msg = "";
		res.obj = tpMap;

		renderJSON(res);
	}

	/**
	 * 后台发标
	 * 
	 * @param agencyId
	 *            合作机构id
	 * @param productId
	 * @param guaranteeModeId
	 * @param guaranteeMeasures
	 * @param user_id
	 * @param amount
	 * @param apr
	 * @param period
	 * @param repayment_type
	 * @param invest_period
	 * @param name
	 * @param description
	 *
	 * @author yaoyi
	 * @createDate 2015年12月28日
	 */
	public static void createBid(long agencyId, long productId,long guaranteeModeId, long userId, double amount, double apr, int period,
			int repayment_type, int invest_period, String name,String guaranteeMeasures, String description,String repaymentSource) {
		checkAuthenticity();

		t_product product = productService.queryProduct(productId);
		if (product == null) {
			flash.error("没有找到借款产品!");

			error404();
		}
		t_user_fund userFund = userfundservice.queryUserFundByUserId(userId);
		if (userFund == null) {
			flash.error("没有找到该借款人");

			error404();
		}

		ResultInfo result = checkCreateBidParams(agencyId, product, amount, apr, period, repayment_type, invest_period,
				name, description);
		if (result.code < 1) {
			flash.error(result.msg);

			addBidInfoToFlash(agencyId, userId, userFund.name, amount, apr, period, repayment_type, invest_period, name,
					description);
			createBidPre(productId,guaranteeModeId);
		}

		int client = Client.PC.code;

		ResultInfo res = bidservice.createBid(agencyId, amount, apr, period, repayment_type, invest_period, name,
				description, product, userFund, client,guaranteeModeId,guaranteeMeasures,repaymentSource);
		if (res.code < 1) {
			LoggerUtil.error(true, res.msg);
			flash.error(res.msg);

			addBidInfoToFlash(agencyId, userId, userFund.name, amount, apr, period, repayment_type, invest_period, name,
					description);
			createBidPre(productId,guaranteeModeId);
		}

		/** 添加管理员事件 */
		Map<String, String> param = new HashMap<String, String>();
		param.put("supervisor", getCurrentSupervisorName());
		param.put("bid_no", "");// 管理员事件bid_no不做获取
		param.put("bid_name", name);
		supervisorService.addSupervisorEvent(getCurrentSupervisorId(), t_event_supervisor.Event.INVEST_ADD, param);

		t_bid bid = (t_bid) res.obj;

		// 业务订单号
		String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.BID_CREATE);

		// 托管，标的登记
		if (ConfConst.IS_TRUST) {
			PaymentProxy.getInstance().bidCreate(Client.PC.code, serviceOrderNo, bid, Constants.BID_CREATE_FROM_BACK);
		}

		res = bidservice.doCreateBid((t_bid) res.obj, serviceOrderNo);
		if (res.code < 0) {
			flash.error(res.msg);
			LoggerUtil.error(true, res.msg);

			addBidInfoToFlash(agencyId, userId, userFund.name, amount, apr, period, repayment_type, invest_period, name,
					description);
			createBidPre(productId,guaranteeModeId);
		}

		flash.success("项目创建成功!");
		showBidPre(0, 1, 10);
	}

	/**
	 * 添加发标信息到flash
	 *
	 * @param agencyId
	 *            合作机枪id
	 * @param userId
	 *            用户id
	 * @param userName
	 *            标的关联用户名
	 * @param amount
	 *            借款金额
	 * @param apr
	 *            借款年利率
	 * @param period
	 *            借款期限
	 * @param repayment_type
	 *            还款方式
	 * @param invest_period
	 *            投资期限
	 * @param name
	 *            标的名称
	 * @param description
	 *            标的描述
	 *
	 * @author yaoyi
	 * @createDate 2016年2月25日
	 */
	private static void addBidInfoToFlash(long agencyId, long userId, String userName, double amount, double apr,
			int period, int repayment_type, int invest_period, String name, String description) {

		t_bid bid = new t_bid();
		bid.user_id = userId;
		flash.put("userName", userName);
		flash.put("agencyId", agencyId);
		bid.amount = amount;
		bid.apr = apr;
		bid.period = period;
		bid.setRepayment_type(RepaymentType.getEnum(repayment_type));
		bid.invest_period = invest_period;
		bid.title = name;
		bid.description = description;
		Cache.set(CacheKey.BID_ + session.getId(), bid, Constants.CACHE_TIME_SECOND_60);

	}

	/**
	 * 后台发标时，参数的检查
	 *
	 * @param agencyId
	 *            合作机构id
	 * @param product
	 *            借款产品对象
	 * @param amount
	 *            借款金额
	 * @param apr
	 *            借款年利率
	 * @param period
	 *            期限
	 * @param repayment_type
	 *            还款方式
	 * @param invest_period
	 *            投标期限
	 * @param name
	 *            标的名称
	 * @param description
	 *            标的描述
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月25日
	 */
	private static ResultInfo checkCreateBidParams(long agencyId, t_product product, double amount, double apr,
			int period, int repayment_type, int invest_period, String name, String description) {
		ResultInfo result = new ResultInfo();

		if (amount > product.max_amount || amount < product.min_amount) {
			result.code = -1;
			result.msg = "借款项目金额范围在[" + product.min_amount + "~" + product.max_amount + "]元之间!";

			return result;
		}
		if (apr > product.max_apr || apr < product.min_apr) {
			result.code = -1;
			result.msg = "借款项目年利率在[" + product.min_apr + "~" + product.max_apr + "]之间!";

			return result;
		}
		if (period > product.max_period || period < product.min_period) {
			result.code = -1;
			result.msg = "借款项目期限在[" + product.min_period + "~" + product.max_period + "]天之间!";

			return result;
		}

		List<RepaymentType> productRepaymentType = product.getProductRepaymentTypeList();
		if (!productRepaymentType.contains(RepaymentType.getEnum(repayment_type))) {
			result.code = -1;
			result.msg = "还款方式错误!";

			return result;
		}

		if (invest_period < 1 || invest_period > 10) {
			result.code = -1;
			result.msg = "筹款时间只能在[1~10]天之间!";

			return result;
		}

		if (StringUtils.isBlank(name) || name.length() < 3 || name.length() > 15) {
			result.code = -1;
			result.msg = "项目名称长度在[3~15]之间!";

			return result;

		}
		if (StringUtils.isBlank(description) || description.length() < 20 || description.length() > 500) {
			result.code = -1;
			result.msg = "项目描述长度在[20~500]之间!";

			return result;
		}

		if (agencyId < 0) {

			result.code = -1;
			result.msg = "合作机构错误";

			return result;
		}
		if (agencyId > 0) {

			t_agencies agency = agencyService.findByID(agencyId);

			if (agency == null) {

				result.code = -1;
				result.msg = "合作机构错误";

				return result;
			}
		}

		result.code = 1;
		result.msg = "";

		return result;
	}

	/**
	 * 标的初审(初审中 -> 借款中)
	 *
	 * @param bidSign
	 * @param suggest
	 * @param pre_release_time
	 *
	 * @author yaoyi
	 * @createDate 2015年12月22日
	 */
	@SubmitOnly
	public static void preAuditBidThrough(String bidSign, String suggest, String pre_release_time) {

		long bidId = decodeSign(bidSign, false);
		if (bidId < 0) {
			flash.error("标的信息有误!");

			showBidPre(0, 1, 10);
		}

		if (StringUtils.isBlank(suggest) || suggest.length() < 20 || suggest.length() > 300) {
			flash.error("风控建议长度位于20~300位之间!");

			toPreAuditBidPre(bidId);
		}

		long supervisor_id = getCurrentSupervisorId();
		Date pre = DateUtil.strToDate(pre_release_time, Constants.DATE_PLUGIN);

		ResultInfo res = bidservice.preAuditBidThrough(bidId, suggest, pre, supervisor_id);
		if (res.code < 0) {
			flash.error(res.msg);
			LoggerUtil.error(true, res.msg);

			toPreAuditBidPre(bidId);
		}

		/** 添加管理员事件 */
		Map<String, String> param = new HashMap<String, String>();
		param.put("supervisor", getCurrentSupervisorName());
		param.put("bid_no", ((t_bid) res.obj).getBid_no());
		param.put("bid_name", ((t_bid) res.obj).title);
		boolean saveEvent = supervisorService.addSupervisorEvent(getCurrentSupervisorId(), Event.INVEST_PREADUIT_PASS,
				param);

		if (!saveEvent) {
			flash.error("保存管理员事件失败");

			toPreAuditBidPre(bidId);
		}

		flash.success("审核成功,已将项目置为[%s]!", t_bid.Status.FUNDRAISING.value);
		showBidPre(0, 1, 10);
	}

	/**
	 * 标的初审(初审中 -> 初审不通过)
	 *
	 * @param bidSign
	 * @param suggest
	 * @param pre_release_time
	 *
	 * @author yaoyi
	 * @createDate 2015年12月28日
	 */
	@SubmitOnly
	public static void preAuditBidNotThrough(String bidSign, String suggest) {
		ResultInfo result = new ResultInfo();

		long bidId = decodeSign(bidSign, false);
		t_bid bid = bidservice.findByID(bidId);
		if (bid == null) {
			flash.error("标的信息有误!");

			showBidPre(0, 1, 10);
		}

		if (StringUtils.isBlank(suggest) || suggest.length() < 20 || suggest.length() > 300) {
			flash.error("风控建议长度位于20~300位之间!");

			toPreAuditBidPre(bidId);
		}

		long supervisorId = getCurrentSupervisorId();
		result = bidservice.preAuditBidNotThrough(bid, supervisorId);
		if (result.code < 1) {
			flash.error(result.msg);
			LoggerUtil.info(true, result.msg);

			toPreAuditBidPre(bidId);
		}

		// 业务流水号
		String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.BID_PRE_AUDIT_FAIL);

		if (ConfConst.IS_TRUST) {
			result = PaymentProxy.getInstance().bidFailed(Client.PC.code, serviceOrderNo, bid,
					ServiceType.BID_PRE_AUDIT_FAIL, suggest, supervisorId);
			if (result.code < 1) {
				LoggerUtil.info(true, result.msg);
				flash.error(result.msg);

				toPreAuditBidPre(bidId);
			}

			flash.success("审核成功,已将项目置为[%s]!", t_bid.Status.PREAUDIT_NOT_THROUGH.value);
			showBidPre(0, 1, 10);
		}

		if (!ConfConst.IS_TRUST) {

			result = bidservice.doPreAuditBidNotThrough(serviceOrderNo, bidId, suggest, supervisorId);
			if (result.code < 1) {
				LoggerUtil.info(true, result.msg);
				flash.error(result.msg);

				toPreAuditBidPre(bidId);
			}
		}

		flash.success("审核成功,已将项目置为[%s]!", t_bid.Status.PREAUDIT_NOT_THROUGH.value);
		showBidPre(0, 1, 10);
	}

	/**
	 * 标的复审->截标
	 *
	 * @param bidSign
	 * @param suggest
	 *
	 * @author jiayijian
	 * @createDate 2017年3月27日
	 */
	@SubmitOnly
	public static void auditBidCutoff(String bidSign, String suggest) {
		long bidId = decodeSign(bidSign, false);
		if (bidId < 0) {
			flash.error("标的信息有误!");

			showBidPre(0, 1, 10);
		}

		if (StringUtils.isBlank(suggest) || suggest.length() < 20 || suggest.length() > 300) {
			flash.error("风控建议长度位于20~300位之间!");

			toAuditBidPre(bidId);
		}

		long supervisorId = getCurrentSupervisorId();
		ResultInfo res = bidservice.auditBidCutoff(bidId, suggest, supervisorId);
		if (res.code < 0) {
			flash.error(res.msg);
			LoggerUtil.error(true, res.msg);

			toAuditBidPre(bidId);
		}

		/** 添加管理员事件 */
		Map<String, String> param = new HashMap<String, String>();
		param.put("supervisor", getCurrentSupervisorName());
		param.put("bid_no", ((t_bid) res.obj).getBid_no());
		param.put("bid_name", ((t_bid) res.obj).title);
		boolean saveEvent = supervisorService.addSupervisorEvent(getCurrentSupervisorId(), Event.INVEST_ADUIT_CUTOFF,
				param);
		if (!saveEvent) {
			flash.error("保存管理员事件失败");
			LoggerUtil.error(true, "保存管理员事件失败");

			toAuditBidPre(bidId);
		}

		flash.success("审核成功,已将项目置为[%s]!", t_bid.Status.AUDITING.value);
		showBidPre(0, 1, 10);
	}

	/**
	 * 标的复审->通过
	 *
	 * @param bidSign
	 * @param suggest
	 *
	 * @author yaoyi
	 * @createDate 2015年12月28日
	 */
	@SubmitOnly
	public static void auditBidThrough(String bidSign, String suggest) {
		long bidId = decodeSign(bidSign, false);
		if (bidId < 0) {
			flash.error("标的信息有误!");

			showBidPre(0, 1, 10);
		}

		if (StringUtils.isBlank(suggest) || suggest.length() < 20 || suggest.length() > 300) {
			flash.error("风控建议长度位于20~300位之间!");

			toAuditBidPre(bidId);
		}

		long supervisorId = getCurrentSupervisorId();
		ResultInfo res = bidservice.auditBidThrough(bidId, suggest, supervisorId);
		if (res.code < 0) {
			flash.error(res.msg);
			LoggerUtil.error(true, res.msg);

			toAuditBidPre(bidId);
		}

		/** 添加管理员事件 */
		Map<String, String> param = new HashMap<String, String>();
		param.put("supervisor", getCurrentSupervisorName());
		param.put("bid_no", ((t_bid) res.obj).getBid_no());
		param.put("bid_name", ((t_bid) res.obj).title);
		boolean saveEvent = supervisorService.addSupervisorEvent(getCurrentSupervisorId(), Event.INVEST_ADUIT_PASS,
				param);
		if (!saveEvent) {
			flash.error("保存管理员事件失败");
			LoggerUtil.error(true, "保存管理员事件失败");

			toAuditBidPre(bidId);
		}

		flash.success("审核成功,已将项目置为[%s]!", t_bid.Status.WAIT_RELEASING.value);
		showBidPre(0, 1, 10);
	}

	/**
	 * 标的复审->不通过
	 *
	 * @param bidSign
	 * @param suggest
	 *
	 * @author yaoyi
	 * @createDate 2015年12月28日
	 */
	@SubmitOnly
	public static void auditBidNotThrough(String bidSign, String suggest) {
		ResultInfo result = new ResultInfo();
		long bidId = decodeSign(bidSign, false);

		t_bid bid = bidservice.findByID(bidId);
		if (bid == null) {
			flash.error("标的信息有误!");

			showBidPre(0, 1, 10);
		}

		if (StringUtils.isBlank(suggest) || suggest.length() < 20 || suggest.length() > 300) {
			flash.error("风控建议长度位于20~300位之间!");

			toAuditBidPre(bidId);
		}

		long supervisorId = getCurrentSupervisorId();

		result = bidservice.auditBidNotThrough(bid, supervisorId);
		if (result.code < 1) {
			flash.error(result.msg);
			LoggerUtil.info(true, result.msg);

			toPreAuditBidPre(bidId);
		}

		// 业务流水号
		String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.BID_PRE_AUDIT_FAIL);

		if (ConfConst.IS_TRUST) {
			result = PaymentProxy.getInstance().bidFailed(Client.PC.code, serviceOrderNo, bid,
					ServiceType.BID_AUDIT_FAIL, suggest, supervisorId);
			if (result.code < 1) {
				LoggerUtil.info(true, result.msg);
				flash.error(result.msg);

				toPreAuditBidPre(bidId);
			}

			flash.success("审核成功,已将项目置为[%s]!", t_bid.Status.AUDIT_NOT_THROUGH.value);
			showBidPre(0, 1, 10);
		}

		if (!ConfConst.IS_TRUST) {

			result = bidservice.doAuditBidNotThrough(serviceOrderNo, bid.id, suggest, supervisorId);
			if (result.code < 0) {
				flash.error(result.msg);
				LoggerUtil.error(true, result.msg);

				toAuditBidPre(bidId);
			}
		}

		flash.success("审核成功,已将标置为[%s]!", t_bid.Status.AUDIT_NOT_THROUGH.value);
		showBidPre(0, 1, 10);
	}

	/**
	 * 后台编辑标的标题
	 *
	 * @param sign
	 *            标的ID加签为sign
	 * @param newTitle
	 *
	 * @author yaoyi
	 * @createDate 2015年12月23日
	 */
	public static void editBidTitle(String bidSign, String newStr) {
		ResultInfo res = new ResultInfo();

		long bidId = decodeSign(bidSign, true);

		if (StringUtils.isBlank(newStr) || newStr.length() < 3 || newStr.length() > 15) {
			res.code = -1;
			res.msg = "借款标题应该为3~15位";

			renderJSON(res);
		}

		res = bidservice.editBidTitle(bidId, newStr);
		if (res.code < 1) {

			renderJSON(res);
		}

		/** 添加管理员事件 */
		Map<String, String> supervisorParam = new HashMap<String, String>();
		supervisorParam.put("bid_no", "" + bidId);
		supervisorParam.put("bid_name", (String) res.obj);
		boolean saveEvent = supervisorService.addSupervisorEvent(getCurrentSupervisorId(),
				Event.INVEST_EDIT_PROJECTNAME, supervisorParam);
		if (!saveEvent) {
			LoggerUtil.error(true, "保存管理员事件失败");

			res.code = -1;
			res.msg = "保存管理员事件失败!";
			renderJSON(res);
		}

		res.code = 1;
		res.msg = "";
		renderJSON(res);
	}

	/**
	 * 编辑借款说明
	 *
	 * @param sign
	 * @param newDescription
	 *
	 * @author yaoyi
	 * @createDate 2015年12月23日
	 */
	public static void editBidDescription(String bidSign, String newStr) {
		ResultInfo res = new ResultInfo();

		long bidId = decodeSign(bidSign, true);

		if (StringUtils.isBlank(newStr) || newStr.length() < 20 || newStr.length() > 500) {
			res.code = -1;
			res.msg = "借款说明应该位于20~500字之间";

			renderJSON(res);
		}

		res = bidservice.editBidDescription(bidId, newStr);
		if (res.code < 1) {

			renderJSON(res);
		}

		/** 添加管理员事件 */
		Map<String, String> supervisorParam = new HashMap<String, String>();
		supervisorParam.put("bid_no", "" + bidId);
		supervisorParam.put("bid_name", (String) res.obj);
		boolean saveEvent = supervisorService.addSupervisorEvent(getCurrentSupervisorId(),
				Event.INVEST_EDIT_DESCRIPTION, supervisorParam);
		if (!saveEvent) {
			LoggerUtil.error(true, "保存管理员事件失败");

			res.code = -1;
			res.msg = "保存管理员事件失败!";
			renderJSON(res);
		}

		res.code = 1;
		res.msg = "";
		renderJSON(res);
	}

	/**
	 * 获取标的投标记录
	 *
	 * @param bidId
	 *
	 * @author yaoyi
	 * @createDate 2016年1月14日
	 */
	public static void investRecordPre(int currPage, int pageSize, String bidSign) {

		long bidId = decodeSign(bidSign, true);
		if (currPage < 1) {
			currPage = 1;
		}
		if (pageSize < 1) {
			pageSize = 5;
		}
		PageBean<BidInvestRecord> pageBean = investService.pageOfBidInvestDetail(currPage, pageSize, bidId);

		render(pageBean);
	}

	/**
	 * 获取标的还款计划
	 *
	 * @param bidId
	 *
	 * @author yaoyi
	 * @createDate 2016年1月14日
	 */
	public static void repaymentRecordPre(int currPage, int pageSize, String bidSign) {

		long bidId = decodeSign(bidSign, true);
		if (currPage < 1) {
			currPage = 1;
		}
		if (pageSize < 1) {
			pageSize = 5;
		}
		PageBean<Map<String, Object>> pageBean = billService.pageOfRepaymentBill(currPage, pageSize, bidId);

		render(pageBean);
	}

	/**
	 * 后台-风控-标的初审，管理员实时上传图片
	 *
	 * @param imgFile
	 * @param fileName
	 *
	 * @author yaoyi
	 * @createDate 2016年1月15日
	 */
	public static void imagesUpload(File imgFile, String bidSign, long bidAuditSubjectId, String fileName) {
		response.contentType = "text/html";

		ResultInfo result = new ResultInfo();
		long bidId = decodeSign(bidSign, true);

		if (imgFile == null) {
			result.code = -1;
			result.msg = "请选择要上传的图片";

			renderJSON(result);
		}
		if (StringUtils.isBlank(fileName) || fileName.length() > 32) {
			result.code = -1;
			result.msg = "图片名称长度应该位于1~32位之间";

			renderJSON(result);
		}
		/*String watermarkPath="/public/front/images/watermark/watermark.png";*/
		/*String watermarkPath="C:\\Users\\admin\\Desktop\\watermark.png";*/
		//调用加水印工具
		ImageMarkUtil.setImageMarkOptions(0.2f,0,80 );
		try {
			ImageMarkUtil.waterMarkByImg( path,imgFile.getAbsolutePath(), imgFile.getAbsolutePath());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result = FileUtil.uploadImgags(imgFile);
		
		if (result.code < 0) {

			renderJSON(result);
		}

		Map<String, Object> fileInfo = (Map<String, Object>) result.obj;
		fileInfo.put("imgName", fileName);

		boolean saveflag = biditemsupervisorservice.saveBidItemSupervisor(getCurrentSupervisorId(), bidId,
				bidAuditSubjectId, fileName, (String) fileInfo.get("staticFileName"));
		if (!saveflag) {
			result.code = -1;
			result.msg = "图片保存失败";

			renderJSON(result);
		}

		/** 添加管理员事件 */
		Map<String, String> supervisorParam = new HashMap<String, String>();
		supervisorParam.put("bid_no", NoUtil.getBidNo(bidId));
		supervisorParam.put("bid_name", bidservice.findBidNameById(bidId));
		supervisorParam.put("subject", auditsubjectbidservice.findAuditSubjectName(bidAuditSubjectId));
		supervisorParam.put("filename", fileName);
		boolean saveEvent = supervisorService.addSupervisorEvent(getCurrentSupervisorId(),
				Event.INVEST_SUBJECT_FILE_UPLOAD, supervisorParam);
		if (!saveEvent) {
			LoggerUtil.error(true, "保存管理员事件失败");

			result.code = -1;
			result.msg = "保存管理员事件失败!";
			renderJSON(result);
		}
		
		renderJSON(result);
	}

	/**
	 * 后台-标的初审-管理员上传科目资料，刷新div
	 *
	 * @param bidId
	 *
	 * @author yaoyi
	 * @createDate 2016年1月19日
	 */
	public static void itemSupervisorPre(String bidSign) {

		long bidId = decodeSign(bidSign, true);

		Map<String, Object> item = auditsubjectbidservice.queryBidRefSubject(bidId);
		List<BidItemOfSupervisorSubject> supervisorLoop = (List<BidItemOfSupervisorSubject>) item.get("supervisorItem");

		render(supervisorLoop);
	}

	/**
	 * 管理员删除上传的审核资料
	 *
	 * @param itemId
	 *
	 * @author yaoyi
	 * @createDate 2016年1月19日
	 */
	public static void delItemSupervisor(long itemId) {

		ResultInfo res = biditemsupervisorservice.delBidItemSupervisor(itemId);

		if (res.code < 1) {
			LoggerUtil.error(true, "管理员删除上传的审核科目资料，id为[%s]", itemId);

			renderJSON(res);
		}

		/** 添加管理员事件 */
		Map<String, String> param = (Map<String, String>) res.obj;
		boolean saveEvent = supervisorService.addSupervisorEvent(getCurrentSupervisorId(),
				Event.INVEST_SUBJECT_FILE_REMOVE, param);
		if (!saveEvent) {
			LoggerUtil.error(true, "保存管理员事件失败");

			res.code = -1;
			res.msg = "保存管理员事件失败!";
			renderJSON(res);
		}

		res.code = 1;
		res.msg = "";

		renderJSON(res);
	}

	/**
	 * 管理员审核用户上传的科目
	 *
	 * @param itemId
	 *
	 * @author yaoyi
	 * @createDate 2016年1月19日
	 */
	public static void auditBidItemUser(long itemId, int status) {
		ResultInfo result = new ResultInfo();

		if (status != BidItemAuditStatus.NO_PASS.code && status != BidItemAuditStatus.PASS.code) {
			result.code = -1;
			result.msg = "审核状态有误";

			renderJSON(result);
		}

		BidItemAuditStatus statusEnum = BidItemAuditStatus.getEnum(status);
		result = bidItemUserService.auditBidItemUser(itemId, statusEnum);

		if (result.code < 1) {
			LoggerUtil.error(true, "管理员审核用户上传的科目资料，id为[%s]", itemId);

			renderJSON(result);
		}

		/** 添加管理员事件 */
		Map<String, String> param = (Map<String, String>) result.obj;
		boolean saveEvent = supervisorService.addSupervisorEvent(getCurrentSupervisorId(),
				status == BidItemAuditStatus.PASS.code ? Event.INVEST_SUBJECT_AUDIT_PASS
						: Event.INVEST_SUBJECT_AUDIT_REJECT,
				param);
		if (!saveEvent) {
			LoggerUtil.error(true, "保存管理员事件失败");

			result.code = -1;
			result.msg = "保存管理员事件失败!";
			renderJSON(result);
		}

		result.code = 1;
		result.msg = "";

		renderJSON(result);
	}

	/**
	 * 解标的的签
	 *
	 * @param bidSign
	 * @param async
	 *            是否来自异步的Ajax请求
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月23日
	 */
	private static long decodeSign(String bidSign, boolean async) {
		ResultInfo result = Security.decodeSign(bidSign, Constants.BID_ID_SIGN, Constants.VALID_TIME,
				ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			flash.error("签名失败");

			showBidPre(0, 1, 10);
			if (async) {
				renderText("");
			} else {
				error404();
			}
		}

		return Convert.strToLong(result.obj + "", -1);
	}
}
