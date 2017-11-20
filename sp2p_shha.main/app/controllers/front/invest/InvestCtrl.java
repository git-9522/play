package controllers.front.invest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import common.FeeCalculateUtil;
import common.annotation.LoginCheck;
import common.annotation.SimulatedCheck;
import common.annotation.SubmitCheck;
import common.annotation.SubmitOnly;
import common.constants.ConfConst;
import common.constants.Constants;
import common.enums.Client;
import common.enums.ServiceType;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.OrderNoUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.number.NumberFormat;
import controllers.common.FrontBaseController;
import controllers.common.SubmitRepeat;
import controllers.common.interceptor.SimulatedInterceptor;
import controllers.common.interceptor.UserStatusInterceptor;
import models.common.bean.CurrUser;
import models.common.bean.UserFundInfo;
import models.common.entity.t_corp_info;
import models.common.entity.t_information;
import models.common.entity.t_user_fund;
import models.common.entity.t_user_info;
import models.core.bean.BidInvestRecord;
import models.core.bean.BidItemOfSupervisorSubject;
import models.core.bean.DebtTransfer;
import models.core.bean.DebtTransferDetail;
import models.core.bean.FrontBid;
import models.core.bean.FrontProduct;
import models.core.bean.InvestReceive;
import models.core.entity.t_addrate_user;
import models.core.entity.t_agencies;
import models.core.entity.t_bid;
import models.core.entity.t_bid_item_supervisor;
import models.core.entity.t_cash_user;
import models.core.entity.t_invest.InvestType;
import models.core.entity.t_product;
import models.core.entity.t_product.ProductType;
import models.core.entity.t_red_packet_user;
import payment.impl.PaymentProxy;
import play.mvc.With;
import services.common.AdvertisementService;
import services.common.CorpInfoService;
import services.common.InformationService;
import services.common.UserFundService;
import services.common.UserInfoService;
import services.core.AgencyService;
import services.core.AuditSubjectBidService;
import services.core.BidItemSupervisorService;
import services.core.BidService;
import services.core.BillInvestService;
import services.core.BillService;
import services.core.CashUserService;
import services.core.DebtService;
import services.core.GuaranteeModeService;
import services.core.InvestService;
import services.core.ProductService;
import services.core.RateService;
import services.core.RedpacketUserService;

/**
 * 前台-理财-投资控制器
 *
 * @description
 *
 * @author huangyunsong
 * @createDate 2015年12月17日
 */
@With({ UserStatusInterceptor.class, SubmitRepeat.class, SimulatedInterceptor.class })
public class InvestCtrl extends FrontBaseController {

	protected static InvestService investService = Factory.getService(InvestService.class);

	protected static BidService bidService = Factory.getService(BidService.class);

	protected static BillService billService = Factory.getService(BillService.class);

	protected static BillInvestService billInvestService = Factory.getService(BillInvestService.class);

	protected static UserFundService userFundService = Factory.getService(UserFundService.class);

	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);

	protected static AuditSubjectBidService auditSujbectBidService = Factory.getService(AuditSubjectBidService.class);

	protected static BidItemSupervisorService bidItemSupervisorService = Factory
			.getService(BidItemSupervisorService.class);

	protected static InformationService informationService = Factory.getService(InformationService.class);

	protected static AdvertisementService advertisementService = Factory.getService(AdvertisementService.class);

	protected static DebtService debtService = Factory.getService(DebtService.class);

	protected static RedpacketUserService redpacketUserService = Factory.getService(RedpacketUserService.class);

	protected static CashUserService cashUserService = Factory.getService(CashUserService.class);

	protected static ProductService productService = Factory.getService(ProductService.class);

	protected static RateService rateService = Factory.getService(RateService.class);

	protected static AgencyService agencyService = Factory.getService(AgencyService.class);

	protected static GuaranteeModeService guaranteeModeService=Factory.getService(GuaranteeModeService.class);
	
	protected static CorpInfoService corpInfoService=Factory.getService(CorpInfoService.class);
	
	/**
	 * 理财首页
	 *
	 * @author yaoyi
	 * @createDate 2016年1月7日
	 */
	public static void toInvestPre(int currPage, int pageSize) {
		List<FrontProduct> productList = productService.queryProductIsUse(true);

		int frontBidsNum = bidService.findFrontBidsNum();

		render(productList, frontBidsNum);
	}

	/**
	 * 理财首页-散标投资
	 *
	 * @param currPage
	 * @param pageSize
	 * @param productId
	 *            产品id
	 * @param status
	 *            状态：1.借款中、4.还款中
	 * @param orderType
	 *            排序方式：0.默认(id)、1.年化利率、2.投资期限、3.发标时间
	 * @param orderValue
	 *            排序规则：0.降序、1.升序
	 *
	 * @param guaranteModeId
	 *            保障方式  99 全部  1、车贷，2、房贷，3、企业贷
	 * @param webType
	 *            前台产品类型 99 全部 1、新虹投  2、彩虹投 3、预虹投 4、智能收益
	 * @param period
	 *            期数 1,3,6,12
	 * @param time
	 *            发布时间  0 、全部  1、今天 2、一星期前  3、一月前  4、半年前 5、一年前
	 * @param vip
	 *            vip最低等级限制  0、全部  1、V1+ ……  8、V8+
	 * @author DaiZhengmiao
	 * @createDate 2016年3月22日
	 */
	public static void showBidsPre(int currPage, int pageSize, long productId, int status, int orderType,
			int orderValue, int guaranteModeId, int webType, int period, int time, int vip) {
		if (currPage < 1) {
			currPage = 1;
		}

		int countDebtInAuction = debtService.countDebtInAuction();

		if (countDebtInAuction > 0) {
			pageSize = 5;
		} else {
			pageSize = 8;
		}

		if (productId < 0) {
			productId = 0;
		}

		if (orderType < 0) {
			orderType = 0;
		}

		PageBean<FrontBid> pageBean = bidService.pageOfBidWebInvest(currPage, pageSize, webType, status, orderType,
				orderValue, guaranteModeId,  period,  vip);
		Date now = new Date();

		render(pageBean, now, productId, status, orderType, orderValue);
	}
	/**
	 * 理财首页-转让
	 *
	 * @author yaoyi
	 * @createDate 2016年1月7日
	 */
	public static void toDebtPre(int currPage, int pageSize) {
		List<FrontProduct> productList = productService.queryProductIsUse(true);

		int findFrontDebtsNum = debtService.findFrontDebtsNum();

		render(productList, findFrontDebtsNum);
	}

	/**
	 * 理财首页-债权转让
	 *
	 * @param currPage
	 * @param pageSize
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月22日
	 */
	public static void showTransfersPre(int currPage, int pageSize, int status) {
		if (currPage < 1) {
			currPage = 1;

		}
		if (pageSize < 1 || pageSize > 5) {
			pageSize = 5;
		}

		PageBean<DebtTransfer> pageBean = debtService.pageOfDebtTransfer(currPage, pageSize, status);

		renderArgs.put("sysNowTime", new Date());// 服务器时间传到客户端

		render(pageBean, status);
	}

	/**
	 * 投标页面
	 *
	 * @param bidId
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月28日
	 */
	@SubmitCheck
	public static void investPre(long bidId) {
		t_bid bid = bidService.findByID(bidId);
		if (bid == null) {

			error404();
		}
		//根据用户id查询企业信息
		t_corp_info corpInfo = corpInfoService.queryByUserId(bid.user_id);
		//借款人信息
		t_user_info loanUserInfo = userInfoService.findUserInfoByUserId(bid.user_id);
		//借款人认证信息
		t_user_fund  userFund=userFundService.queryUserFundByUserId(bid.user_id);
		CurrUser currUser = getCurrUser();
		if (currUser != null) {
			double balance = userFundService.findUserBalance(currUser.id);
			int countRedPacket = redpacketUserService.countUserRedPacket(currUser.id,
					t_red_packet_user.RedpacketStatus.UNUSED.code);
			int countCash = cashUserService.countUserCash(currUser.id, t_cash_user.CashStatus.UNUSED.code);
			double totalRedPacket = redpacketUserService.totalUserRedPacket(currUser.id,
					t_red_packet_user.RedpacketStatus.UNUSED.code);
			renderArgs.put("balance", balance);
			renderArgs.put("countRedPacket", countRedPacket);
			renderArgs.put("countCash", countCash);
			renderArgs.put("totalRedPacket", totalRedPacket);
			renderArgs.put("rewardRedPacket", Security.addSign(Constants.REWARD_TYPE_RED_PACKET,
					Constants.REWARD_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES));
			renderArgs.put("rewardCash", Security.addSign(Constants.REWARD_TYPE_CASH, Constants.REWARD_ID_SIGN,
					ConfConst.ENCRYPTION_KEY_DES));
		}
		// 判断是否是2017年7月1号以后的才展示信息披露(初审时间) TODO 正式改成7月1号
		List<BidItemOfSupervisorSubject> bidItemOfSupervisorSubjects = new ArrayList<BidItemOfSupervisorSubject>();
		//借款人信息
		if (bid.time.getTime()>DateUtil.strToDate("2017-08-22", "yyyy-MM-dd").getTime()) {
			bidItemOfSupervisorSubjects = auditSujbectBidService.queryBidRefSubjectSupervisor(bidId);
		}
		Date nowTime = new Date();
		if (bid.pre_release_time != null && nowTime.before(bid.pre_release_time)) {// 是否预发售状态
			renderArgs.put("preRelease", true);
		}
		renderArgs.put("sysNowTime", nowTime.getTime());// 服务器时间传到客户端

		t_information investDeal = informationService.findInvestDeal();
		// 是否是机构标
		t_agencies agencies = null;
		if (bid.is_agency) {
			agencies = agencyService.findByID(bid.agency_id);
		}
		String guaranteeMode="";
		if(bid.guarantee_mode_id!=null){
			guaranteeMode=guaranteeModeService.getGuaranteeMode(bid.guarantee_mode_id);
		}
		//进入锁定日期
		   GregorianCalendar lockTime=new GregorianCalendar(); 
		      
		   lockTime.setTime(bid.time); 
		   lockTime.add(5,bid.invest_period);
		//到期推出日期
		   GregorianCalendar expireTime=new GregorianCalendar();  
		   expireTime.setTime(lockTime.getTime());
		   expireTime.add(2,bid.period);
		   //判断轮播图片是否上传
		   int isBidItem = 0;
		   for(BidItemOfSupervisorSubject bidItemOfSupervisorSubject:bidItemOfSupervisorSubjects){
			   if(null!=bidItemOfSupervisorSubject&&bidItemOfSupervisorSubject.bid_item_supervisor.size()>0){
				   isBidItem = 1;
				   break;
			   } 
		   }
		render(bid,guaranteeMode,isBidItem, agencies,lockTime,expireTime, loanUserInfo, bidItemOfSupervisorSubjects, investDeal,userFund,corpInfo);
	}

	/**
	 * 用户借款协议
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月19日
	 */
	public static void investDealPre() {
		t_information investDeal = informationService.findInvestDeal();
		if (investDeal == null) {

			error404();
		}

		render(investDeal);
	}

	/**
	 * 投标操作
	 *
	 * @param bidSign
	 *            标的签名
	 * @param investAmt
	 *            投标金额（按金额购买）
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月28日
	 */
	@SubmitOnly
	@LoginCheck
	@SimulatedCheck
	public static void invest(String bidSign, double investAmt) {
		ResultInfo result = Security.decodeSign(bidSign, Constants.BID_ID_SIGN, Constants.VALID_TIME,
				ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			error500();
		}

		long bidId = Long.parseLong(result.obj.toString());
		t_bid bid = bidService.findByID(bidId);
		if (bid == null) {
			error500();
		}

		if (investAmt % 1 != 0) {
			flash.error("请输入正整数的投标金额");
			investPre(bidId);
		}

		// 按份数购买时，投资金额=投资份数*每份的金额
		if (t_product.BuyType.PURCHASE_BY_COPY.equals(bid.getBuy_type())) {
			investAmt = new Double(Double.toString(investAmt)).intValue() * bid.min_invest_amount;
		}

		t_product tProduct = productService.queryProduct(bid.product_id);
		if (tProduct == null) {
			flash.error("借款标关联的产品数据异常");
			investPre(bidId);
		}

		CurrUser currUser = getCurrUser();
		if (currUser == null) {
			flash.error("请先登录");
			investPre(bidId);
		}

		long userId = currUser.id;
		
		long investId = investService.queryIsHaveInvestRecord(userId);
		
		/* 用于限制新手单笔投资不得大于100000 */
		if(!currUser.is_old && investId <= 0 && investAmt > 100000) {
			flash.error("用户首次投资限额不得超过10万元");
			investPre(bidId);
		}

		// 若用户选择投资的是新手标则要进行控制
		if (tProduct.getType().code == ProductType.NEWBIE.code) {
			if (currUser.is_old) {
				flash.error("新手标仅限新注册的用户");
				investPre(bidId);
			}

			if (investId > 0L) {
				flash.error("新手标仅限未成功投资过的用户");
				investPre(bidId);
			}
		}
		
		if(tProduct.getType().code == ProductType.OLD_CUSTOMER.code) {
			
			if (investId <= 0L) {
				flash.error("老用户专享仅限已成功投资过的用户");
				investPre(bidId);
			}
		}

		if(tProduct.id == 6) {
			if(investAmt % 10000 != 0) {
				flash.error("预约标投资金额只能为10000的整数倍");
				investPre(bidId);
			}
		}
		
		long redPacketId = 0L; // 红包ID
		double redPacketAmt = 0.0; // 红包金额
		String redPacketIdStr = params.get("redPacketSign"); // 红包ID加密串

		if (StringUtils.isNotBlank(redPacketIdStr)) {
			result = Security.decodeSign(redPacketIdStr, Constants.RED_ID_SIGN, Constants.VALID_TIME,
					ConfConst.ENCRYPTION_KEY_DES);
			if (result.code >= 1) {
				redPacketId = Long.parseLong(result.obj.toString());
			}
		}

		long cashId = 0L; // 现金券ID
		double cashAmt = 0.0; // 现金券金额
		String cashIdStr = params.get("cashSign"); // 现金券ID加密串

		if (StringUtils.isNotBlank(cashIdStr)) {
			result = Security.decodeSign(cashIdStr, Constants.CASH_ID_SIGN, Constants.VALID_TIME,
					ConfConst.ENCRYPTION_KEY_DES);
			if (result.code >= 1) {
				cashId = Long.parseLong(result.obj.toString());
			}
		}

		if (redPacketId > 0L && cashId > 0L) {
			flash.error("红包及现金券不能同时使用");
			investPre(bidId);
		}

		if (redPacketId > 0L) {
			result = investService.investRedPacket(userId, redPacketId, investAmt, bid.period,
					bid.getPeriod_unit().code);
			if (result.code < 1) {
				LoggerUtil.info(true, result.msg);
				flash.error(result.msg);
				investPre(bidId);
			}

			t_red_packet_user redPacketUser = (t_red_packet_user) result.obj;
			redPacketAmt = redPacketUser.amount;
		}

		if (cashId > 0L) {
			// 投标使用现金券校验
			result = investService.investCash(userId, cashId, investAmt);

			if (result.code < 1) {
				LoggerUtil.info(true, result.msg);
				flash.error(result.msg);
				investPre(bidId);
			}

			t_cash_user cashUser = (t_cash_user) result.obj;
			cashAmt = cashUser.amount;
		}

		long rateId = 0L; // 加息券ID
		double addRate = 0.0; // 加息券利率
		String rateIdStr = params.get("rateSign"); // 加息券ID加密串

		if (StringUtils.isNotBlank(rateIdStr)) {
			result = Security.decodeSign(rateIdStr, Constants.RATE_ID_SIGN, Constants.VALID_TIME,
					ConfConst.ENCRYPTION_KEY_DES);
			if (result.code >= 1) {
				rateId = Long.parseLong(result.obj.toString());
			}
		}

		if (rateId > 0L) {
			// 投标使用加息券校验
			result = investService.investRate(userId, rateId, investAmt, bid.period, bid.getPeriod_unit().code);

			if (result.code < 1) {
				LoggerUtil.info(true, result.msg);
				flash.error(result.msg);
				investPre(bidId);
			}

			t_addrate_user rateUser = (t_addrate_user) result.obj;
			addRate = rateUser.rate;
		}

		String investPassword = params.get("investPassword"); // 投资密码

		// 投标准备
		result = investService.invest(userId, bid, investAmt, cashAmt, investPassword);
		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
			flash.error(result.msg);
			investPre(bidId);
		}

		int rows = 0;

		// 仅使用红包时才修改红包状态
		if (redPacketId > 0L && cashId <= 0L) {
			rows = redpacketUserService.updateRedPacketLockTime(redPacketId,
					t_red_packet_user.RedpacketStatus.UNUSED.code, t_red_packet_user.RedpacketStatus.USING.code);
			if (rows <= 0) {
				LoggerUtil.info(true, "修改红包状态失败");
				flash.error("修改红包状态失败");
				investPre(bidId);
			}
		} else if (redPacketId <= 0L && cashId > 0L) { // 仅使用现金券时才修改现金券状态
			rows = cashUserService.updateUserCashLockTime(cashId, t_cash_user.CashStatus.UNUSED.code,
					t_cash_user.CashStatus.USING.code);
			if (rows <= 0) {
				LoggerUtil.info(true, "修改现金券状态失败");
				flash.error("修改现金券状态失败");
				investPre(bidId);
			}
		}

		if (rateId > 0L) {// 仅使用加息券时才修改加息券状态

			rows = rateService.updateUserRateLockTime(rateId, t_addrate_user.RateStatus.UNUSED.code,
					t_addrate_user.RateStatus.USING.code);
			if (rows <= 0) {
				LoggerUtil.info(true, "修改加息券状态失败");
				flash.error("修改加息券状态失败");
				investPre(bidId);
			}
		}

		// 业务流水号
		String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.INVEST);

		if (ConfConst.IS_TRUST) {
			result = PaymentProxy.getInstance().invest(Client.PC.code, InvestType.PC.code, serviceOrderNo, userId, bid,
					investAmt, redPacketAmt, redPacketId, cashAmt, cashId, rateId, addRate);
			if (result.code < 1) {
				LoggerUtil.info(true, result.msg);
				flash.error(result.msg);
				investPre(bidId);
			}
		}

		if (!ConfConst.IS_TRUST) {
			result = investService.doInvest(userId, bidId, investAmt, serviceOrderNo, "", Client.PC.code,
					InvestType.PC.code, redPacketId, redPacketAmt, cashId, cashAmt, rateId, addRate);
			if (result.code < 1) {
				LoggerUtil.info(true, result.msg);
				flash.error(result.msg);
				investPre(bidId);
			}
		}

		investSuccessPre(bidId, investAmt);
	}

	/**
	 * 投标成功后，进入投标页面
	 *
	 * @param bidId
	 * @param investAmt
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年5月31日
	 */
	public static void investSuccessPre(long bidId, double investAmt) {
		t_bid bid = bidService.findByID(bidId);// 投标成功后的标的信息
		flash.put("loan_schedule", bid.loan_schedule);
		flash.put("invest_amount", investAmt);
		flash.put("period", bid.period);
		flash.put("period_unit", bid.getPeriod_unit().getShowValue());
		flash.put("sign", bidId);
		double income = FeeCalculateUtil.getLoanSumInterest(investAmt, bid.apr, bid.period, bid.getPeriod_unit(),
				bid.getRepayment_type());
		flash.put("income", NumberFormat.round(income, 2));

		flash.put("investBidSuccess", true);
		investPre(bidId);
	}

	/**
	 * 标的还款计划
	 *
	 * @param val
	 * @param scale
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月30日
	 */
	public static void queryRepaymentBillPlanPre(int currPage, int pageSize, long bidId) {

		PageBean<Map<String, Object>> page = billService.pageOfRepaymentBill(currPage, pageSize, bidId);
		renderJSON(page);
	}

	/**
	 * 获取标的投标记录
	 *
	 * @param bidId
	 *
	 * @author yaoyi
	 * @createDate 2016年1月14日
	 */
	public static void investRecordPre(int currPage, int pageSize, long bidId) {

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
	public static void repaymentRecordPre(int currPage, int pageSize, long bidId) {

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
	 * 债权转让界面
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月22日
	 */
	@SubmitCheck
	public static void transferPre(long debeId) {
		if (debeId <= 0) {

			error404();
		}

		DebtTransferDetail detail = debtService.findDebtDetailById(debeId);
		if (detail == null) {

			error404();
		}

		CurrUser currUser = getCurrUser();
		if (currUser != null) {
			double balance = userFundService.findUserBalance(currUser.id);
			renderArgs.put("balance", balance);
		}

		renderArgs.put("sysNowTime", new Date());// 服务器时间传到客户端

		render(detail);
	}

	/**
	 * 投资的还款计划
	 *
	 * @param currPage
	 * @param pageSize
	 * @param investId
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年4月11日
	 */
	public static void repaymentInvestRecordPre(int currPage, int pageSize, long investId) {

		if (currPage < 1) {
			currPage = 1;
		}
		if (pageSize < 1) {
			pageSize = 5;
		}
		PageBean<InvestReceive> pageBean = billInvestService.pageOfRepaymentBill(currPage, pageSize, investId);

		render(pageBean);
	}

	/**
	 * 进行债权竞价
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月28日
	 */
	@SubmitOnly
	@LoginCheck
	@SimulatedCheck
	public static void buyDebt(String debtSign) {
		checkAuthenticity();
		ResultInfo result = Security.decodeSign(debtSign, Constants.DEBT_TRANSFER_ID_SIGN, Constants.VALID_TIME,
				ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {

			flash.error(result.msg);
			toInvestPre(0, 0);
		}
		long debtId = Long.parseLong((String) result.obj);
		CurrUser currUser = getCurrUser();

		// 准备
		result = debtService.debtTransfer(debtId, currUser.id);
		if (result.code < 1) {
			flash.error(result.msg);
			transferPre(debtId);
		}

		// t_debt_tansfer的订单号
		String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.DEBT_TRANSFER);

		if (ConfConst.IS_TRUST) {
			// 资金托管
			PaymentProxy.getInstance().debtTransfer(Client.PC.code, serviceOrderNo, debtId, currUser.id);

			transferPre(debtId);
		}

		// 进入页面
		transferPre(debtId);
	}

	/**
	 * 可以使用的奖励记录
	 * 
	 * @param bidPeriod
	 *            红包使用规则:借款期限(月)，0代表无限制
	 * @param investAmount
	 */
	public static void rewardRecord(double investAmount, String rewardTypeSign, int bidPeriod) {
		CurrUser currUser = getCurrUser();

		Map<String, Object> rewardMap = null;

		ResultInfo result = new ResultInfo();

		if (bidPeriod < 0) {
			result.code = -1;
			result.msg = "参数错误";

			renderJSON(result);
		}

		rewardMap = new HashMap<String, Object>();

		List<t_red_packet_user> redPacketList = redpacketUserService.findCanUseRedPacket(currUser.id, investAmount,
				bidPeriod);
		rewardMap.put("redPacketList", dealRedPacket(redPacketList));

		List<t_cash_user> cashList = cashUserService.findCanUseCash(currUser.id, investAmount, bidPeriod);
		rewardMap.put("cashList", dealCash(cashList));

		result.code = 1;
		result.msg = "";
		result.obj = rewardMap;

		renderJSON(result);
	}

	private static List<t_red_packet_user> dealRedPacket(List<t_red_packet_user> redPacketList) {
		List<t_red_packet_user> redPacketListNew = new ArrayList<t_red_packet_user>();

		for (t_red_packet_user redPacket : redPacketList) {
			redPacket.sign = redPacket.getSign();
			redPacketListNew.add(redPacket);
		}

		return redPacketListNew;
	}

	private static List<t_cash_user> dealCash(List<t_cash_user> cashList) {
		List<t_cash_user> cashListNew = new ArrayList<t_cash_user>();

		for (t_cash_user cash : cashList) {
			cash.sign = cash.getSign();
			cashListNew.add(cash);
		}

		return cashListNew;
	}

	/**
	 * 可以使用的加息券记录
	 * 
	 * @param bidPeriod
	 *            红包使用规则:借款期限(月)，0代表无限制
	 * @param investAmount
	 */
	public static void rateRecord(double investAmount, int bidPeriod) {
		CurrUser currUser = getCurrUser();
		ResultInfo result = new ResultInfo();
		Map<String, Object> rewardMap = new HashMap<String, Object>();

		if (bidPeriod < 0) {
			result.code = -1;
			result.msg = "参数错误";

			renderJSON(result);
		}

		List<t_addrate_user> rateList = rateService.findCanUseRate(currUser.id, investAmount, bidPeriod);
		rewardMap.put("code", 1);
		rewardMap.put("rateList", dealRate(rateList));

		result.code = 1;
		result.msg = "";
		result.obj = rewardMap;

		renderJSON(result);
	}

	private static List<t_addrate_user> dealRate(List<t_addrate_user> rateList) {
		List<t_addrate_user> rateListNew = new ArrayList<t_addrate_user>();

		if (rateList == null) {
			return rateListNew;
		}

		for (t_addrate_user rate : rateList) {
			rate.sign = rate.getSign();
			rateListNew.add(rate);
		}

		return rateListNew;
	}
}
