package services.core;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.itextpdf.text.DocumentException;

import common.FeeCalculateUtil;
import common.constants.Constants;
import common.constants.SettingKey;
import common.enums.NoticeScene;
import common.enums.PactType;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.NoUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.StrUtil;
import common.utils.number.Arith;
import common.utils.number.NumberFormat;
import common.utils.pdf.DefaultPdfWatermark;
import common.utils.pdf.PDFUtil;
import daos.core.BidDao;
/*import fddcontract.FddConst;
import fddcontract.FddContract;*/
import models.common.entity.t_deal_platform;
import models.common.entity.t_deal_user;
import models.common.entity.t_event_supervisor;
import models.common.entity.t_event_supervisor.Event;
import models.common.entity.t_pact;
import models.common.entity.t_template_pact;
import models.common.entity.t_user;
import models.common.entity.t_user_fund;
import models.common.entity.t_user_info;
import models.core.bean.BackFinanceBid;
import models.core.bean.BackRiskBid;
import models.core.bean.BidInvestRecord;
import models.core.bean.FrontBid;
import models.core.bean.FrontMyLoanBid;
import models.core.bean.MyPrerogativeRecord;
import models.core.bean.PactBid;
import models.core.entity.t_audit_subject;
import models.core.entity.t_audit_subject_bid;
import models.core.entity.t_bid;
import models.core.entity.t_bid.Status;
import models.core.entity.t_bill;
import models.core.entity.t_invest;
import models.core.entity.t_product;
import models.core.entity.t_product.RepaymentType;
/*import models.fddcontract.bean.Customer;*/
import play.Play;
import services.base.BaseService;
import services.common.DealPlatformService;
import services.common.DealUserService;
import services.common.NoticeService;
import services.common.PactService;
import services.common.SettingService;
import services.common.SupervisorService;
import services.common.UserFundService;
import services.common.UserInfoService;
import services.common.UserService;

/**
 * 标的Service
 *
 * @description 
 *
 * @author yaoyi
 * @createDate 2016年3月8日
 */
public class BidService extends BaseService<t_bid> {

	protected BidDao bidDao;

	protected static AuditSubjectService auditsubjectservice = Factory.getService(AuditSubjectService.class);
	
	protected static ProductService productservice = Factory.getService(ProductService.class);
	
	protected static AuditSubjectBidService auditsujbectbidservice = Factory.getService(AuditSubjectBidService.class);
	
	protected static UserService userservice = Factory.getService(UserService.class);
	
	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	
	protected static UserFundService userFundService = Factory.getService(UserFundService.class);
	
	protected static DealUserService dealuserservice = Factory.getService(DealUserService.class);
	
	protected static DealPlatformService dealPlatformService = Factory.getService(DealPlatformService.class);
	
	protected static InvestService investservice = Factory.getService(InvestService.class);
	
	protected static NoticeService noticeservice = Factory.getService(NoticeService.class);
	
	protected static BillService billservice = Factory.getService(BillService.class);
	
	protected static BillInvestService billinvestservice = Factory.getService(BillInvestService.class);
	
	protected static SupervisorService supervisorService = Factory.getService(SupervisorService.class);
	
	protected static PactService pactService = Factory.getService(PactService.class);
	
	protected static SettingService settingService = Factory.getService(SettingService.class);
	
	protected static RedpacketUserService redpacketService = Factory.getService(RedpacketUserService.class);
	
	
	protected BidService() {
		bidDao = Factory.getDao(BidDao.class);
		super.basedao = bidDao;
	}
	
	/**
	 * 发布借款标
	 *
	 * @param agencyId 合作机构id
	 * @param amount 借款金额
	 * @param apr 年利率
	 * @param period 期限
	 * @param repayment_type 还款方式
	 * @param expire_time 到期时间
	 * @param name 标的名称
	 * @param description 描述
	 * @param product 产品
	 * @param userFund 用户资金表对象
	 * @param client 客户端
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月26日
	 */
	public ResultInfo createBid(long agencyId,double amount, double apr, int period, int repayment_type, int expire_time, 
			String name, String description, t_product product, t_user_fund userFund, int client,long guaranteeModeId,String guaranteeMeasures,String repaymentSource) {
		ResultInfo result = new ResultInfo();
		
		/** 用户资金签名校验 */
		result = userFundService.userFundSignCheck(userFund.user_id);
		if (result.code < 1) {
			
			return result;
		}

		t_bid bid = new t_bid();
		bid.amount = amount;
		
		/** 算出保证金 */
		bid.bail = FeeCalculateUtil.loanBailFee(amount, product.bail_scale);
		if(bid.bail < 0){
			result.code = -1;
			result.msg = "系统在算保证金的时候出现错误!";
			
			return result;
		}
		
		/** 算出服务费:最大为借款金额的50% */
		bid.loan_fee = FeeCalculateUtil.loanServiceFee(amount, product.getPeriod_unit().code, period, product.service_fee_rule);
		if(bid.loan_fee < 0){
			result.code = -1;
			result.msg = "系统在算借款服务费的时候出现错误!";
			
			return result;
		}

		/** 用户是否可以发这个属性的标,返回需冻结金额 */
		double credit_line = userInfoService.findUserCreditLine(userFund.user_id);
		loanTypeIsPassByUser(product.getType().code, bid.amount, bid.bail, credit_line, userFund.user_id, userFund.balance, result);
		if(result.code < 0){
			
			return result;
		}
		
		/** 冻结保证金不能大于借款金额 */
		if(bid.bail > amount){
			result.code = -1;
			result.msg = "冻结保证金大于了借款金额!";
			
			return result;
		}
		
		/** 可用余额需大于或等于保证金 */
		if(bid.bail > userFund.balance) {
			result.code = -1;
			result.msg = "您的可用余额为:" + NumberFormat.format(userFund.balance, Constants.FINANCE_FORMATE_NORMAL) + "元,不足发布借款,无法达到系统需冻结的保证金。";
			
			return result;
		}
		
		//判定是否是机构标 
		if(agencyId > 0){
			bid.is_agency = true;
			bid.agency_id = agencyId;
		}
		
		//投资密码
		bid.is_invest_password=product.is_invest_password;
		bid.invest_password=product.invest_password;
		bid.group_id=product.group_id;
		
		//投资奖励
		bid.is_invest_reward=product.is_invest_reward;
		bid.reward_rate=product.reward_rate;
		
		bid.setStatus(t_bid.Status.PREAUDITING);//审核中
		bid.guarantee_mode_id=guaranteeModeId;//保障方式id
		bid.guarantee_measures=guaranteeMeasures;//保障措施
		bid.repayment_source=repaymentSource;//还款来源
		
		bid.time = new Date();
		bid.user_id = userFund.user_id;
		bid.product_id = product.id;
		bid.title = name;
		bid.amount = amount;
		bid.apr = apr;
		bid.setPeriod_unit(product.getPeriod_unit());
		bid.period = period;
		bid.service_fee_rule = product.service_fee_rule;
		bid.setRepayment_type(RepaymentType.getEnum(repayment_type));
		bid.setBuy_type(product.getBuy_type());
		bid.description = description;
		bid.client = client;
		bid.invest_period = expire_time;
		if (t_product.BuyType.PURCHASE_BY_COPY.equals(bid.getBuy_type())) {//如果是按份数购买,最低投标金额字段就是每一份的价格。
			bid.min_invest_amount = new BigDecimal(Double.toString(amount))
			.divide(new BigDecimal(Double.toString(product.invest_copies)), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
		} else {
			bid.min_invest_amount = product.min_invest_amount;
		}
		bid.invest_copies = product.invest_copies;
		
		result.code = 1;
		result.msg = "";
		result.obj = bid;
		
		return result;
	}
	
	/**
	 * 执行创建标的
	 *
	 * @param tb 标的信息
	 * @param serviceOrderNo 业务订单号
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月28日
	 */
	public ResultInfo doCreateBid(t_bid bid, String serviceOrderNo){
		ResultInfo res = new ResultInfo();
		if(bid == null){
			res.code = -1;
			res.msg = "标的信息不能为空";
			
			return res;
		}
		
		/** 标的信息入库 */
		if(!bidDao.save(bid)){
			res.code = -1;
			res.msg = "标的信息保存失败";
			
			return res;
		}
		
		t_product tp = productservice.queryProduct(bid.product_id);
		if(tp == null){
			res.code = -1;
			res.msg = "产品信息不存在";
			
			return res;
		}
		
		/** 添加标的需要上传的资料 */
		res = addBidAuditSubject(bid.id, tp.audit_subjects);

		/** 修改用户为借款会员 */
		boolean updMem = userInfoService.updateUserMemberType(bid.user_id, t_user_info.MemberType.BORROW_MEMBER);
		if(!updMem){
			res.code = -1;
			res.msg = "修改用户为借款会员失败!";
			
			return res;
		}
		
		t_user user = userservice.findByID(bid.user_id);
		/** 如果保证金大于0，则冻结用户资金 */
		if (bid.bail>0) {
			boolean addAmt = userFundService.userFundFreeze(bid.user_id, bid.bail);
			if(!addAmt){
				res.code = -1;
				res.msg = "冻结用户资金失败!";
				
				return res;
			}
		
			/** 冻结借款保证金发送通知 */
			Map<String, Object> paramAmt = new HashMap<String, Object>();
			paramAmt.put("user_name", user.name);
			paramAmt.put("bid_no", bid.bid_no);
			paramAmt.put("bid_name", bid.title);
			paramAmt.put("bail", bid.bail);
			noticeservice.sendSysNotice(bid.user_id, NoticeScene.BAIL_FREEZE, paramAmt);
		
			userFundService.userFundSignUpdate(bid.user_id);
		
			t_user_fund tuf = userFundService.queryUserFundByUserId(bid.user_id);
			
			Map<String, String>param = new HashMap<String, String>();
			param.put("bidNo", bid.getBid_no());
			boolean b = dealuserservice.addDealUserInfo(serviceOrderNo, 
					bid.user_id, 
					bid.bail, 
					tuf.balance,
					tuf.freeze,
					t_deal_user.OperationType.BAIL_FREEZE,
					param);
			if(!b){
				res.code = -1;
				res.msg = "添加交易记录失败!";
	
				return res;
			}
		}
		
		/** 发送邮件、站内信、短信通知用户 */
		Map<String, Object> content = new HashMap<String, Object>();
		content.put("user_name", user.name);
		content.put("bid_no", bid.bid_no);
		content.put("bid_name", bid.title);
		noticeservice.sendSysNotice(bid.user_id, NoticeScene.BID_APPLAY_SUCC, content);
			
		res.code = 1;
		res.obj = bid;
		return res;
	}
	
	/**
	 * 生成借款合同(理财人和借款人共用一份合同)
	 *
	 * @param bidId 标的id
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月19日
	 */
	/*public ResultInfo creatBidPact(long bidId) {
		ResultInfo result = new ResultInfo();
		
		DecimalFormat myformat = new DecimalFormat();
		myformat.applyPattern("0.00");
		
		Map<String, String> contentParam = new HashMap<String, String>();
		String now = DateUtil.dateToString(new Date(), "yyyy年MM月dd日");
		PactBid pactBid = findPactBidById(bidId);
		t_template_pact temp = pactService.findByType(PactType.BID);
	
		
		//合同名称:pact_name
		contentParam.put("pact_name", temp.name);
		
		//合同编号:pact_no
		String pact_no = NoUtil.getPactNo(pactBid.id, new Date());
		contentParam.put("pact_no", pact_no);
		
		//借款人真实姓名loan_real_name
		contentParam.put("loan_real_name", pactBid.reality_name.substring(0, 1) + "某某");

		//借款人平台用户名:loan_name
		contentParam.put("loan_name", StrUtil.asterisk(pactBid.name, 4, 4, 5));
		
		//借款人平台用户名:id_number
		contentParam.put("id_number", StrUtil.asterisk(pactBid.id_number, 5, 5, 8));
		
		//资金借出方:invest_list(table,资金借出方(网站用户名),借出金额)
		List<BidInvestRecord> investRecords = investservice.listOfBidInvestRecords(bidId);
		StringBuffer invest_list_buffer = new StringBuffer("<table cellpadding='0' cellspacing='0' border='1' width='100%' style='text-align: center;'>")
			.append("<tr><th width='50%'>资金借出方(网站用户名)</th><th width='50%'>借出金额</th></tr>");
		for (BidInvestRecord record : investRecords) {
			invest_list_buffer.append("<tr><td>")
				.append(StrUtil.asterisk(record.name, 4, 4, 5))
				.append("</td><td>")
				.append(myformat.format(record.amount))
				.append("</td></tr>");
		}
		invest_list_buffer.append("</table>");
		contentParam.put("invest_list", invest_list_buffer.toString());
		
		//合同签订时间
		contentParam.put("date_sign", now);
		
		//借款用途:purpose_name
		contentParam.put("purpose_name", pactBid.description);
				
		//借款开始日期:release_date
		contentParam.put("release_date", DateUtil.dateToString(pactBid.release_time, "yyyy年MM月dd日"));
					
		//借款到期日:last_repay_time
		contentParam.put("last_repay_time", DateUtil.dateToString(pactBid.getLast_repay_time(), "yyyy年MM月dd日"));
			
		//放款方式:repayment_type
		contentParam.put("repayment_type", pactBid.getRepayment_type().value);
		
		//借款金额:bid_amount
		contentParam.put("bid_amount", myformat.format(pactBid.amount));
		
		//借款金额大写:bid_camount
		contentParam.put("bid_camount", pactBid.getCmount());
		
		//借款期限:period_num
		contentParam.put("period_num", pactBid.period+"");
			
		//借款期限单位:period_unit
		contentParam.put("period_unit", pactBid.getPeriod_unit().getShowValue());
					
		//借款年利率:apr
		contentParam.put("apr", myformat.format(pactBid.apr));
		
		//借款服务费的key：借款金额百分比loan_amount_rate
		contentParam.put("loan_amount_rate", pactBid.getLoan_amount_rate());
		
		//借款服务费的key：减去的借款期数sub_period
		contentParam.put("sub_period", pactBid.getSub_period());
				
		// 借款服务费的key：减去借款期数后，借款金额的百分比sub_loan_amount_rate
		contentParam.put("sub_loanAmount_rate", pactBid.getSub_loan_amount_rate());
		
		//理财服务费的key：百分比invest_amount_rate
		contentParam.put("invest_amount_rate", pactBid.getInvest_amount_rate());
		
		//逾期罚息的key：百分比overdue_amount_rate
		contentParam.put("overdue_amount_rate", pactBid.getOverdue_amount_rate());
		
		//转让服务费率:transfer_fee_rate
		double percent = Double.valueOf(settingService.findSettingValueByKey(SettingKey.TRANSFER_FEE_RATE));
		contentParam.put("transfer_fee_rate",  myformat.format(percent));
		
		//借款人签署时间
		contentParam.put("date_real_name", now);		
		
//		//出借人网名列表invest_name_list
//		StringBuffer invest_name_list_buffer = new StringBuffer("<table cellpadding='0' cellspacing='0' border='1' width='50%' style='text-align: center;'>")
//		.append("<tr><th width='33%'>真实姓名</th><th width='33%'>平台注册用户名</th><th width='33%'>身份证号</th></tr>");
//		for (BidInvestRecord record : investRecords) {
//			invest_name_list_buffer.append("<tr><td>")
//				.append(StrUtil.asterisk(record.name, 4, 4, 5))
//				.append("</td></tr>");
//			invest_name_list_buffer.append("<tr><td>")
//			.append(StrUtil.asterisk(record.name, 4, 4, 5))
//			.append("</td></tr>");
//		}
		
		//出借人信息列表
		List<t_user_info> invertUsers=investservice.listOfBidInvestUsers(bidId);
		StringBuffer invest_name_list_buffer = new StringBuffer("<table cellpadding='0' cellspacing='0' border='1' width='50%' style='text-align: center;'>")
		.append("<tr><th width='33%'>真实姓名</th><th width='33%'>平台注册用户名</th><th width='33%'>身份证号</th></tr>");
		for (t_user_info userinfo : invertUsers) {
			invest_name_list_buffer.append("<tr><td>")
				.append(StrUtil.asterisk(userinfo.reality_name, 1, 0, 2))
				.append("</td><td>")
				.append(StrUtil.asterisk(userinfo.name, 3, 0, 5))
				.append("</td><td>")
				.append(StrUtil.asterisk(userinfo.id_number, 3, 0, 15))
				.append("</td></tr>");
		}
	
		invest_name_list_buffer.append("</table>");
		contentParam.put("invest_name_list", invest_name_list_buffer.toString());
		//借款人签署时间
		contentParam.put("date_invest", now);
		
		//平台名称:plateform_name
		String plateform_name = settingService.findSettingValueByKey(SettingKey.PLATFORM_NAME);
		contentParam.put("plateform_name", plateform_name);
		
		//平台签署时间:date_plateform
		contentParam.put("date_plateform", now);
		
		//还款计划表:repay_list(table)
		List<t_bill> t_bills = billservice.findBillByBidId(bidId);
		StringBuffer repay_list_buffer = new StringBuffer("<table cellpadding='0' cellspacing='0' border='1' width='100%' style='text-align: center;'>")
					.append("<tr><th width='25%'>还款日期</th><th width='25%'>应还本息(元)</th><th width='25%'>本金(元)</th><th width='25%'>利息(元)</th></tr>");
		double totalrepayment_corpus = 0.0;
		double totalrepayment_interest = 0.0;
		for(t_bill bill : t_bills){
			repay_list_buffer.append("<tr><td>")
				.append(DateUtil.dateToString(bill.repayment_time, "yyyy年MM月dd日"))
				.append("</td><td>")
				.append(myformat.format(bill.repayment_corpus+bill.repayment_interest))
				.append("</td><td>")
				.append(myformat.format(bill.repayment_corpus))
				.append("</td><td>")
				.append(myformat.format(bill.repayment_interest))
				.append("</td></tr>");
			totalrepayment_corpus += bill.repayment_corpus;
			totalrepayment_interest += bill.repayment_interest;
		}
		repay_list_buffer.append("<tr><th>合计</th><td>")
			.append(myformat.format(totalrepayment_corpus+totalrepayment_interest))
			.append("</td><td>")
			.append(myformat.format(totalrepayment_corpus))
			.append("</td><td>")
			.append(myformat.format(totalrepayment_interest))
			.append("</td></tr>");
		
		repay_list_buffer.append("</table>");
		contentParam.put("repay_list", repay_list_buffer.toString());
		
		
		temp.content = StrUtil.replaceByMap(temp.content, contentParam);
		
		t_pact pact = new t_pact();
		pact.time = new Date();
		pact.pid = bidId;
		pact.setType(PactType.BID);
		pact.content = temp.content;
		pact.image_url = temp.image_url;
		result = pactService.createPact(pact);

		String image_url = pact.image_url;
			
		if (image_url.startsWith("/")) {
			image_url = image_url.substring(1);
		}
		File file = Play.getFile(image_url);
		String imgString = "";
		if(file.exists()){
			imgString = file.getAbsolutePath();
		} else {
			imgString = PDFUtil.WATERMARKIMAGEPATH;
		}
		File expFile = null;
		try {
			expFile = PDFUtil.exportHTMLPdfWithWatermark(pact.content, null, new DefaultPdfWatermark(imgString));
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<Customer> list = new ArrayList<Customer>();
		for(t_user_info tui : invertUsers){
			Customer cus = new Customer();
			cus.setUser_id(tui.user_id);
			cus.setCustomer_name(tui.reality_name);
			cus.setClient_role(FddConst.CLIENT_ROLE_INV);
			cus.setEmail(tui.email);
			cus.setIdent_type(FddConst.IDENT_TYPE);
			cus.setId(tui.id_number);
			cus.setMobile(tui.mobile);
			list.add(cus);
		}
		t_user_info tb = new t_user_info();
		tb = userInfoService.getBorrowerInfo(bidId);
		Customer borrower= new Customer();
		borrower.setUser_id(tb.user_id);
		borrower.setCustomer_name(tb.reality_name);
		borrower.setClient_role(FddConst.CLIENT_ROLE_BOW);
		borrower.setEmail(tb.email);
		borrower.setIdent_type(FddConst.IDENT_TYPE);
		borrower.setId(tb.id_number);
		borrower.setMobile(tb.mobile);
		list.add(borrower);
		try {
			FddContract.class.newInstance().electronicContract(bidId,expFile, list, pact_no, temp.name, ".pdf");
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return result;
	}
*/


	/**
	 * 编辑借款标题
	 *
	 * @param bidId 标的Id
	 * @param newTitle 新标题
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月23日
	 */
	public ResultInfo editBidTitle(long bidId, String newTitle){
		ResultInfo res = new ResultInfo();
		
		t_bid tb = bidDao.findByID(bidId);
		if(tb == null){
			res.code = -1;
			res.msg = "没有查询到标的信息";
			
			return res;
		}
		
		//保存旧项目名称用于返回
		res.obj = tb.title;
		
		int i = bidDao.updateBidTitle(bidId, newTitle);
		if(i != 1){
			res.code = -1;
			res.msg = "更新失败";
			
			return res;
		}
		
		res.code = 1;
		res.msg = "";
		
		return res;
	}
	
	/**
	 * 编辑借款描述
	 *
	 * @param bidId 标的id
	 * @param newDescription 新描述
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月23日
	 */
	public ResultInfo editBidDescription(long bidId, String newDescription){
		ResultInfo res = new ResultInfo();
		
		t_bid tb = bidDao.findByID(bidId);
		if(tb == null){
			res.code = -1;
			res.msg = "没有查询到标的信息";
			
			return res;
		}
		
		int i = bidDao.updateBidDescription(bidId, newDescription);
		if(i != 1){
			res.code = -1;
			res.msg = "更新失败";
			
			return res;
		}
		
		res.obj = tb.title;
		
		res.code = 1;
		res.msg = "";
		
		return res;
	}
	
	
	/**
	 * 标的初审通过
	 *
	 * @param bidId 标的id
	 * @param suggest 初审建议
	 * @param pre_release_time 初审时间
	 * @param supervisor_id 初审管理员
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月22日
	 */
	public ResultInfo preAuditBidThrough(long bidId, String suggest, Date pre_release_time, long supervisor_id) {
		ResultInfo result = new ResultInfo();
		
		t_bid bid = super.findByID(bidId);
		if(bid == null){
			result.code = -1;
			result.msg = "查询借款标的失败";
			
			return result;
		}
		if(!t_bid.Status.PREAUDITING.equals(bid.getStatus())){
			result.code = -1;
			result.msg = "非法审核状态";
			
			return result;
		}
		
		/** 修改状态->初审通过*/
		Date invest_expire_time = DateUtil.addDay(pre_release_time, bid.invest_period);//满标时间=预发布时间+投标期限
		int status = t_bid.Status.FUNDRAISING.code;
		List<Integer>nowstatus = new ArrayList<Integer>();
		nowstatus.add(t_bid.Status.PREAUDITING.code);
		
		int i = bidDao.preAuditBidStatus(bid.id, status, pre_release_time, invest_expire_time, supervisor_id, new Date(), suggest, nowstatus);
		if(i != 1){
			result.code = -1;
			result.msg = "审核失败";
			
			return result;
		}
		
		result.code = 1;
		result.obj = bid;
		
		t_user tu = userservice.findByID(bid.user_id);
		/** 发送邮件、站内信、短信通知用户 */
		Map<String, Object> content = new HashMap<String, Object>();
		content.put("user_name", tu.name);
		content.put("bid_no", bid.bid_no);
		content.put("bid_name", bid.title);
		/** 初审通过  发送通知 */
		noticeservice.sendSysNotice(bid.user_id, NoticeScene.BID_PREAUTID_PASS, content);
		
		return result;
	}
	
	/**
	 * 标的初审不通过(准备)
	 *
	 * @param bid
	 * @param supervisorId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月28日
	 */
	public ResultInfo preAuditBidNotThrough(t_bid bid, long supervisorId) {
		ResultInfo result = new ResultInfo();

		if(!t_bid.Status.PREAUDITING.equals(bid.getStatus())){
			result.code = -1;
			result.msg = "非法审核状态";
			
			return result;
		}
		
		/** 添加管理员事件 */
		Map<String, String>param = new HashMap<String, String>();
		param.put("bid_no", bid.bid_no);
		param.put("bid_name", bid.title);
		boolean saveEvent = supervisorService.addSupervisorEvent(supervisorId, Event.INVEST_PREADUIT_REJECT, param);
		if(!saveEvent){
			result.code = -1;
			result.msg = "初审不通过，添加管理员事件失败";
			return result;
		}
		
		result.code = 1;
		result.msg = "初审不通过准备完毕";

		return result;
	}
	
	/**
	 * 标的初审不通过(执行)
	 *
	 * @param serviceOrderNo
	 * @param bidId
	 * @param suggest
	 * @param supervisorId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月28日
	 */
	public ResultInfo doPreAuditBidNotThrough(String serviceOrderNo, long bidId, String suggest, long supervisorId) {
		ResultInfo result = new ResultInfo();
		
		t_bid bid = super.findByID(bidId);
		if(bid == null){
			result.code = -1;
			result.msg = "查询借款标的失败";
			
			return result;
		}
		
		/** 修改状态->初审不通过 */
		int status = t_bid.Status.PREAUDIT_NOT_THROUGH.code;
		List<Integer> nowstatus = new ArrayList<Integer>();
		nowstatus.add(t_bid.Status.PREAUDITING.code);
		int i = bidDao.preAuditBidStatus(bid.id, status, null, null, supervisorId, new Date(), suggest, nowstatus);
		if(i != 1){
			result.code = -1;
			result.msg = "审核失败";
			
			return result;
		}
		
		/** 返还冻结保证金 */
		if (bid.bail > 0) {
			result = relieveUserBailFund(serviceOrderNo, bid.getBid_no(), bid.user_id, bid.bail, bid.title);
			if(result.code < 0){
				
				return result;
			}
		}
		
		/** 返还投资人投资金额 */
		result = returnInvestUserFund(serviceOrderNo, bid.id, bid.getBid_no(), bid.title);
		if(result.code < 1){
			LoggerUtil.error(false, "标->借款中->流标[id:%s]，返还投资人投资金额失败，事务回滚", bid.id);
			
			return result;
		}
		
		t_user tu = userservice.findByID(bid.user_id);
		/** 发送邮件、站内信、短信通知用户 */
		Map<String, Object> content = new HashMap<String, Object>();
		content.put("user_name", tu.name);
		content.put("bid_no", bid.bid_no);
		content.put("bid_name", bid.title);
		/** 初审不通过 发送通知  */
		noticeservice.sendSysNotice(bid.user_id, NoticeScene.BID_PREAUTID_REJECT, content);
		
		result.code = 1;
		result.msg = "初审不通过成功";
		
		return result;
	}
	
	/**
	 * 标的复审->截标
	 *
	 * @param bidId 标的id
	 * @param suggest 复审建议
	 * @param supervisor_id 复审管理员
	 * @return
	 *
	 * @author jiayijian
	 * @createDate 2015年12月22日
	 */
	public ResultInfo auditBidCutoff(long bidId, String suggest, long supervisor_id) {
		ResultInfo res = new ResultInfo();
		
		t_bid tb = super.findByID(bidId);
		if(tb == null){
			res.code = -1;
			res.msg = "查询借款标的失败";
			
			return res;
		}
		
		if(tb.loan_schedule == 100.00 ||  tb.has_invested_amount >= tb.amount || tb.has_invested_amount <= 0){
			
			res.code = -1;
			res.msg = "标的已满";
			
			return res;
		}
		
		//借款中(初审通过)
		if(t_bid.Status.FUNDRAISING.code != tb.getStatus().code){
			res.code = -1;
			res.msg = "非法审核状态";
			
			return res;
		}
		//更新满标时间, 更新标的状态为（复审中）
		int status = t_bid.Status.AUDITING.code;
		List<Integer>nowstatus = new ArrayList<Integer>();
		nowstatus.add(t_bid.Status.FUNDRAISING.code);
		int i = bidDao.auditBidCutoff(bidId, status, supervisor_id, new Date(), suggest, nowstatus);
		if(i != 1){
			res.code = -1;
			res.msg = "审核失败";
			
			return res;
		}		
		
		t_user tu = userservice.findByID(tb.user_id);
		/** 发送邮件、站内信、短信通知用户 */
		Map<String, Object> content = new HashMap<String, Object>();
		content.put("user_name", tu.name);
		content.put("bid_no", tb.bid_no);
		content.put("bid_name", tb.title);
		noticeservice.sendSysNotice(tb.user_id, NoticeScene.BID_FULL, content);
		
		res.code = 1;
		res.obj = tb;
		return res;
	}
	
	/**
	 * 标的复审->通过
	 *
	 * @param bidId 标的id
	 * @param suggest 复审建议
	 * @param supervisor_id 复审管理员
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月22日
	 */
	public ResultInfo auditBidThrough(long bidId, String suggest, long supervisor_id) {
		ResultInfo res = new ResultInfo();
		
		t_bid tb = super.findByID(bidId);
		if(tb == null){
			res.code = -1;
			res.msg = "查询借款标的失败";
			
			return res;
		}
		
		if(tb.loan_schedule != 100.00 || tb.amount != tb.has_invested_amount){
			res.code = -1;
			res.msg = "还没有满标";
			
			return res;
		}
		
		//满标->复审通过
		if(t_bid.Status.AUDITING.code != tb.getStatus().code){
			res.code = -1;
			res.msg = "非法审核状态";
			
			return res;
		}
		
		int status = t_bid.Status.WAIT_RELEASING.code;
		List<Integer>nowstatus = new ArrayList<Integer>();
		nowstatus.add(t_bid.Status.AUDITING.code);
		int i = bidDao.auditBidStatus(bidId, status, supervisor_id, new Date(), suggest, nowstatus);
		if(i != 1){
			res.code = -1;
			res.msg = "审核失败";
			
			return res;
		}		
		
		t_user tu = userservice.findByID(tb.user_id);
		/** 发送邮件、站内信、短信通知用户 */
		Map<String, Object> content = new HashMap<String, Object>();
		content.put("user_name", tu.name);
		content.put("bid_no", tb.bid_no);
		content.put("bid_name", tb.title);
		noticeservice.sendSysNotice(tb.user_id, NoticeScene.BID_AUTID_PASS, content);
		
		res.code = 1;
		res.obj = tb;
		return res;
	}
	
	/**
	 * 标的复审->不通过(准备)
	 *
	 * @param bid 标的对象
	 * @param supervisorId 管理员
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年2月19日
	 */
	public ResultInfo auditBidNotThrough(t_bid bid, long supervisorId) {
		ResultInfo result = new ResultInfo();
		
		//借款中->复审不通过;	满标->复审不通过
		if(!t_bid.Status.AUDITING.equals(bid.getStatus()) && !t_bid.Status.FUNDRAISING.equals(bid.getStatus())){
			result.code = -1;
			result.msg = "非法审核状态";
			
			return result;
		}
		
		/** 添加管理员事件 */
		Map<String, String>param = new HashMap<String, String>();
		param.put("bid_no", bid.bid_no);
		param.put("bid_name", bid.title);
		
		boolean saveEvent = supervisorService.addSupervisorEvent(supervisorId, Event.INVEST_ADUIT_REJECT, param);
		if(!saveEvent){
			result.code = -1;
			result.msg = "保存管理员事件失败";
			
			return result;
		}

		result.code = 1;
		result.msg = "复审不通过准备完毕";

		return result;
	}
	
	
	/**
	 * 标的复审->不通过(执行)
	 *
	 * @param serviceOrderNo 订单号
	 * @param bidId 标的id
	 * @param suggest 复审建议
	 * @param supervisor_id 复审管理员
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月22日
	 */
	public ResultInfo doAuditBidNotThrough(String serviceOrderNo, long bidId, String suggest, long supervisor_id) {
		ResultInfo result = new ResultInfo();
		
		t_bid bid = super.findByID(bidId);
		if(bid == null){
			result.code = -1;
			result.msg = "查询借款标的失败";
			
			return result;
		}
		
		int status = t_bid.Status.AUDIT_NOT_THROUGH.code;
		List<Integer>nowstatus = new ArrayList<Integer>();
		nowstatus.add(t_bid.Status.AUDITING.code);
		nowstatus.add(t_bid.Status.FUNDRAISING.code);
		int i = bidDao.auditBidStatus(bid.id, status, supervisor_id, new Date(), suggest, nowstatus);
		if(i != 1){
			result.code = -1;
			result.msg = "审核失败";
			
			return result;
		}		
		
		/** 返还借款人冻结保证金 */
		if (bid.bail > 0) {
			result = relieveUserBailFund(serviceOrderNo, bid.getBid_no(), bid.user_id, bid.bail, bid.title);
			if(result.code < 0){
				
				return result;
			}
		}
		
		/** 返还投资人投资金额 */
		result = returnInvestUserFund(serviceOrderNo, bid.id, bid.getBid_no(), bid.title); 
		if(result.code < 0){
			
			return result;
		}
		
		t_user tu = userservice.findByID(bid.user_id);
		/** 发送邮件、站内信、短信通知用户 */
		Map<String, Object> content = new HashMap<String, Object>();
		content.put("user_name", tu.name);
		content.put("bid_no", bid.bid_no);
		content.put("bid_name", bid.title);
		noticeservice.sendSysNotice(bid.user_id, NoticeScene.BID_AUTID_REJECT, content);
		
		result.code = 1;
		result.obj = bid;
		return result;
	}
	
	private ResultInfo loanTypeIsPassByUser(int type, double amount, double bail, double credit_line, long userId, double balance, ResultInfo res){
		double value = 0.00;
		
		if(type == t_product.ProductType.ORDINARY.code || type == t_product.ProductType.NEWBIE.code){//普通、新手(冻结的是保证金)
			
			value = bail;
			
		}/*else if(type == t_product.ProductType.CREDIT.code){//信用(信用额度要大于借款金额)
			
			if(credit_line >= amount){
				value = bail;
			}else{
				res.msg = "亲、您所借款的金额超过了自己的信用额度哦!";
				res.code = -1;
				
				return res;
			}
			
		}else if(type == t_product.ProductType.WORTH.code){//净值
			
			*//** 用户待收 *//*
			double receive = billinvestservice.getUserReceive(userId);
			if(receive < 0){
				res.code = -1;
				res.msg = "查询用户待收金额失败";
				
				return res;
			}
			*//** 用户待还 *//*
			double pay = billservice.getUserPay(userId);
			if(pay < 0){
				res.code = -1;
				res.msg = "查询用户待还金额失败";
				
				return res;
			}
			*//** 借款金额+保证金 *//*
			double netValue = Arith.add(amount, bail);
			//** (可用余额+待收-待还) * 70% *//*
			double netValue2 = Arith.mul(Arith.sub(Arith.add(balance, receive), pay), 0.7);
			//** 借款金额+保证金<(可用余额+待收-待还)* 70% *//*
			if (netValue > netValue2) {
				res.code = -1;
				res.msg = "可用余额还不能发布净值标";

				return res;
			}
			value = bail;
			
		}*/else{
			res.code = -1;
			res.msg = "产品类型不对";
		}
		
		res.code = 1;
		res.obj = value;
		
		return res;
	}
	
	/**
	 * 为标添加审核科目
	 *
	 * @param bid_id 标的id
	 * @param audit_subjects 逗号分割的id字符串
	 * @param res
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月21日
	 */
	private ResultInfo addBidAuditSubject(long bid_id, String audit_subjects){
		ResultInfo res = new ResultInfo();
		
		if(StringUtils.isBlank(audit_subjects)){
			res.code = -1;
			res.msg = "获取审核科目为空，请确认该借款产品是否已经添加审核科目";
			
			return res;
		}
		
		t_audit_subject_bid tasb = null;
		String[] subjects = audit_subjects.split(",");
		for(int i=0; i<subjects.length; i++){
			if(!StrUtil.isNumeric(subjects[i])){
				res.code = -1;
				res.msg = "获取借款产品审核科目时，发现借款产品审核科目参数不合法";
				
				return res;
			}
			
			t_audit_subject tas = auditsubjectservice.findByID(Long.parseLong(subjects[i]));
			tasb = new t_audit_subject_bid(bid_id, tas.name, tas.description);
			auditsujbectbidservice.save(tasb);
		}
		
		res.code = 1;
		res.msg = "添加审核科目成功";
		
		
		return res;

	}
	
	/**
	 * 扣除投资人的投资冻结金额
	 *
	 * @param invests 投资列
	 * @param bid 标的对象
	 * @param serviceOrderNo 订单号
	 *
	 * @author yaoyi
	 * @createDate 2015年12月23日
	 */
	private ResultInfo investUserAmountProcess(List<t_invest> invests, t_bid bid, String serviceOrderNo){
		ResultInfo result = new ResultInfo();
		
		t_user_fund investUserFund = null;
		boolean isInvestSignCheckSuccess;
		boolean isAddDeal = true;

		//通知场景参数
		Map<String, Object> sceneParame = new HashMap<String, Object>();
		sceneParame.put("bid_no", bid.bid_no);
		sceneParame.put("bid_name", bid.title);
		sceneParame.put("period_num", bid.period);
		sceneParame.put("period_unit", bid.getPeriod_unit().getShowValue());
		sceneParame.put("apr", bid.apr);
		sceneParame.put("repayment_type", bid.getRepayment_type().value);
		
		Map<String, String> summaryParam = new HashMap<String, String>();
		summaryParam.put("bidNo", bid.bid_no);
		
		for (t_invest invest : invests) {
			//签名校验
			isInvestSignCheckSuccess = true;
			result = userFundService.userFundSignCheck(invest.user_id);
			if (result.code < 1) {
				isInvestSignCheckSuccess = false;
			}
			
			boolean minusFund = userFundService.userFundMinusFreezeAmt(invest.user_id, invest.amount - invest.cash_amount);// 减去用户冻结的投标资金
			if(!minusFund){
				result.code = -1;
				result.msg = "扣除理财人冻结金额失败!";
				
				return result;
			}
			
			if (isInvestSignCheckSuccess) {
				userFundService.userFundSignUpdate(invest.user_id);
			}
			
			/** 添加交易记录 */
			investUserFund = userFundService.queryUserFundByUserId(invest.user_id);
			isAddDeal = dealuserservice.addDealUserInfo(serviceOrderNo, 
					invest.user_id, 
					invest.amount, 
					investUserFund.balance, 
					investUserFund.freeze, 
					t_deal_user.OperationType.RELEASE_INVEST, 
					summaryParam);
			if(!isAddDeal){
				result.code = -1;
				result.msg = "添加扣除服务费记录失败!";
				
				return result;
			}
			
			/** 给投资者:发送通知 */
			sceneParame.put("user_name", investUserFund.name);
			sceneParame.put("amount", invest.amount);
			noticeservice.sendSysNotice(invest.user_id, NoticeScene.INVEST_INTEREST, sceneParame);
		}
		
		result.code = 1;
		result.msg = "扣除投资人的投资冻结金额成功";
		
		return result;
	}
	
	private void createBidBill(t_bid tb, ResultInfo res){
		
 		double monthRate = 0;//月利率
 		double monPay = 0;//每个月要还的金额
 		double amount = 0;
 		double monPayInterest = 0;// 每个月利息（如果是天标的话，就是所有的利息）
 		double monPayAmount = 0;//每个月本金
 		double totalAmount = 0;//总共要还的金额
 		double payRemain = 0;//剩余要还的金额
 		double payAmount = 0;//加起来付了多少钱
 		double totalInterest = 0;//总利息
 		
 		int deadline = tb.period; //借款标期限
 		double borrowSum = tb.amount; //借款金额
 		int period_unit = tb.getPeriod_unit().code;//借款期限单位
 		monthRate = Double.valueOf(tb.apr * 0.01)/12.0;//通过年利率得到月利率
 		
 		if(t_product.RepaymentType.AND_OTHER_INFORMATION.equals(tb.getRepayment_type())){//等本等息
			if(period_unit == t_product.PeriodUnit.MONTH.code){//月标
				
				monPay = Double.valueOf(Arith.mul(borrowSum, monthRate) * Math.pow((1 + monthRate), deadline))/Double.valueOf(Math.pow((1 + monthRate), deadline) - 1);//每个月要还的本金和利息
				monPay = Arith.round(monPay, 2);
				amount = borrowSum;
				totalAmount = Arith.mul(monPay, deadline);//总共要还的金额
				payRemain = Arith.round(totalAmount, 2);
				
				for(int n=1; n<=deadline; n++){
					monPayInterest = Arith.round(Arith.mul(amount, monthRate), 2);// 每个月利息
					monPayAmount = Arith.round(Arith.sub(monPay, monPayInterest), 2);// 每个月本金
					amount = Arith.round(Arith.sub(amount, monPayAmount), 2);
					
					if (n == deadline) {
						monPay = payRemain;
						monPayAmount = borrowSum - payAmount;
						monPayInterest = monPay - monPayAmount;
					}
					payAmount += monPayAmount;
					payRemain = Arith.sub(payRemain, monPay);
					
					t_bill tbill = new t_bill();
					tbill.time = new Date();
					tbill.user_id = tb.user_id;
					tbill.bid_id = tb.id;
					tbill.title = tb.title;
					tbill.period = n;
					tbill.setStatus(t_bill.Status.NORMAL_NO_REPAYMENT);
					tbill.repayment_time =  DateUtil.addMonth(new Date(), n);
					tbill.repayment_corpus = monPayAmount;
					tbill.repayment_interest = monPayInterest;
					if(!billservice.createBill(tbill)){
						res.code = -1;
						res.msg = "保存借款账单失败!";
						
						return;
					}
				}
				
				billinvestservice.createBillInvest(tb.id, tb.getRepayment_type().code, res);
				
				if(tb.is_invest_reward){//是否开启投标奖励 
					res = billinvestservice.addRewardAmtToBillInvest(tb.id);
				}
				//投资使用加息卷的收益金额，分配到每一期账单
				res = billinvestservice.addRateAmtToBillInvest(tb.id);
				
				return;
			}else{//天标
				
				addDayBills(tb.title, deadline, borrowSum, monthRate, tb.id, 
						tb.getRepayment_type().code, tb.user_id, res);//生成天标借款账单和投资账单
				
				if(tb.is_invest_reward){//是否开启投标奖励 
					res = billinvestservice.addRewardAmtToBillInvest(tb.id);
				}
				
				//投资使用加息卷的收益金额，分配到每一期账单
				res = billinvestservice.addRateAmtToBillInvest(tb.id);
				
				return;
			}
		}
		
		/** 先息后本 */
		if (t_product.RepaymentType.AFTER_THE_REST.equals(tb.getRepayment_type())) {
        	monPayInterest = Arith.round(Arith.mul(borrowSum, monthRate), 2); 
        	double totalPayInterest = 0; 
        	if(period_unit == t_product.PeriodUnit.MONTH.code){//月标
        		t_bill tbill = null;
        		
        		for(int n=1; n<=deadline; n++){
        			tbill = new t_bill();
					if(n == deadline){
					    monPayAmount = borrowSum;
					    /** 利息公式：  */
					    double realPayInterest = Arith.round(Arith.mul(Arith.mul(borrowSum, monthRate), deadline), 2);//真正要还的利息金额 
						monPayInterest = realPayInterest - totalPayInterest;//最后一期纠偏要还的利息 
					}else{
						monPayAmount = 0.00;
					}
					totalPayInterest += monPayInterest;
					tbill.time = new Date();
					tbill.user_id = tb.user_id;
					tbill.bid_id = tb.id;
					tbill.title = tb.title;
					tbill.period = n;
					tbill.setStatus(t_bill.Status.NORMAL_NO_REPAYMENT);
					tbill.repayment_time =  DateUtil.addMonth(new Date(), n);
					tbill.repayment_corpus = monPayAmount;
					tbill.repayment_interest = monPayInterest;
					if(!billservice.createBill(tbill)){
						res.code = -1;
						res.msg = "保存借款账单失败!";
						
						return;
					}
        		}
        		billinvestservice.createBillInvest(tb.id, tb.getRepayment_type().code, res);
				
        		if(tb.is_invest_reward){//是否开启投标奖励 
					res = billinvestservice.addRewardAmtToBillInvest(tb.id);
				}
        		
        		//投资使用加息卷的收益金额，分配到每一期账单
				res = billinvestservice.addRateAmtToBillInvest(tb.id);
				return;
        	} else {
        		addDayBills(tb.title, deadline, borrowSum, monthRate, tb.id, 
						tb.getRepayment_type().code, tb.user_id, res);//生成天标借款账单和投资账单
				
        		if(tb.is_invest_reward){//是否开启投标奖励 
					res = billinvestservice.addRewardAmtToBillInvest(tb.id);
				}
        		
        		//投资使用加息卷的收益金额，分配到每一期账单
				res = billinvestservice.addRateAmtToBillInvest(tb.id);
				return;
        	}
		}
		/** 一次性还款 */
		if (t_product.RepaymentType.LUMP_SUM_REPAYMENT.equals(tb.getRepayment_type())) {
			if(period_unit == t_product.PeriodUnit.MONTH.code){//月标
				t_bill tbill = new t_bill();
				monPayInterest = Arith.mul(borrowSum, monthRate);
				totalInterest = monPayInterest * deadline;
				totalAmount = borrowSum + totalInterest;
				
				tbill.time = new Date();
				tbill.user_id = tb.user_id;
				tbill.bid_id = tb.id;
				tbill.title = tb.title;
				tbill.period = 1;
				tbill.setStatus(t_bill.Status.NORMAL_NO_REPAYMENT);
				tbill.repayment_time =  DateUtil.addMonth(new Date(), deadline);
				tbill.repayment_corpus = borrowSum;
				tbill.repayment_interest = totalInterest;
				if(!billservice.createBill(tbill)){
					res.code = -1;
					res.msg = "保存借款账单失败!";
					
					return;
				}
				billinvestservice.createBillInvest(tb.id, tb.getRepayment_type().code, res);
				
				if(tb.is_invest_reward){//是否开启投标奖励 
					res = billinvestservice.addRewardAmtToBillInvest(tb.id);
				}
				
				//投资使用加息卷的收益金额，分配到每一期账单
				res = billinvestservice.addRateAmtToBillInvest(tb.id);
				return;
			} else {
	    		addDayBills(tb.title, deadline, borrowSum, monthRate, tb.id, 
						tb.getRepayment_type().code, tb.user_id, res);//生成天标借款账单和投资账单
				
	    		if(tb.is_invest_reward){//是否开启投标奖励 
					res = billinvestservice.addRewardAmtToBillInvest(tb.id);
				}
	    		
	    		//投资使用加息卷的收益金额，分配到每一期账单
				res = billinvestservice.addRateAmtToBillInvest(tb.id);
	    		
				return;
	    	}
		}
		
	}
	
	/**
	 * 天标的账单生成
	 *
	 * @param title
	 * @param deadline 
	 * @param borrowSum
	 * @param monthRate
	 * @param bidId
	 * @param repaymentId
	 * @param userId
	 * @param res
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月24日
	 */
	private ResultInfo addDayBills(String title, int deadline, double borrowSum, double monthRate,
 			long bidId, int repaymentId, long userId, ResultInfo res){
		
		String orderNo = "";
		
		double monPayInterest = Arith.div(Arith.mul(Arith.mul(borrowSum, monthRate), deadline), 30, 2);//天标的总利息
		t_bill tbill = new t_bill();
		/** 生成借款账单 */
		tbill.time = new Date();
		tbill.bill_no = orderNo;
		tbill.user_id = userId;
		tbill.bid_id = bidId;
		tbill.title = title;
		tbill.period = 1;
		tbill.setStatus(t_bill.Status.NORMAL_NO_REPAYMENT);
		tbill.repayment_time =  DateUtil.addDay(new Date(), deadline);
		tbill.repayment_corpus = borrowSum;
		tbill.repayment_interest = monPayInterest;
		if(!billservice.createBill(tbill)){
			res.code = -1;
			res.msg = "生成天标借款账单失败!";
			
			return res;
		}
		
		billinvestservice.createBillInvest(bidId, repaymentId, res);
	    
		return res;
	}

	/**
	 * 已全部还款，更新借款标状态和最后还款时间
	 *
	 * @param bidId 借款标ID
	 * @param schedule 投标进度
	 * @param investAmt 投资金额
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月28日
	 */
	public boolean bidEnd(long bidId) {
		int row = bidDao.updateBidEnd(bidId);
		
		if (row < 1) {
			
			return false;
		}
		
		return true;
	}
	
	/**
	 * 满标，更新标的状态和满标时间
	 *
	 * @param bidId 标的id
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月28日
	 */
	public ResultInfo bidExpire(long bidId) {
		ResultInfo result = new ResultInfo();
		
		int row = bidDao.updateBidExpire(bidId);
		
		if (row < 1) {
			result.code=-1;
			result.msg="更新满标失败";
			LoggerUtil.error(true, "更新标的状态和满标时间失败。【bidId:%s】", bidId);
			
			return result;
		}
		
		result.code=1;
		result.msg="更新满标成功";
		
		t_bid bid = bidDao.findByID(bidId);
		t_user user = userservice.findByID(bid.user_id);	
		
		/** 满标发送借款通知  */
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("user_name", user.name);
		param.put("bid_no", bid.bid_no);
		param.put("bid_name", bid.title);
		noticeservice.sendSysNotice(user.id, NoticeScene.BID_FULL, param);
		
		return result;
	}


	/**
	 * 流标(执行)
	 *
	 * @param bid
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月23日
	 */
	public ResultInfo doBidFlow(String serviceOrderNo, t_bid bid) {
		ResultInfo result = new ResultInfo();
		
		int row = 0;
		
		/** 修改状态 */
		row = bidDao.updateBidStatusFlow(bid.id);
		
		if(row < 1){
			result.code = -1;
			result.msg = "修改标的状态失败!";
			
			return result;
		}
		
		/** 返还借款人冻结保证金 */
		if (bid.bail > 0) {
			result = relieveUserBailFund(serviceOrderNo, bid.bid_no, bid.user_id, bid.bail, bid.title);
			if(result.code < 1){
				
				return result;
			}
		}
		
		/** 返还投资人投资金额 */
		result = returnInvestUserFund(serviceOrderNo, bid.id, bid.getBid_no(), bid.title);
		if(result.code < 1){
			LoggerUtil.error(false, "标->借款中->流标[id:%s]，返还投资人投资金额失败，事务回滚", bid.id);
			
			return result;
		}
		
		t_user user = userservice.findByID(bid.user_id);

		/** 流标 给借款人  发送通知 */
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("user_name", user.name);
		param.put("bid_no", bid.bid_no);
		param.put("bid_name", bid.title);
		noticeservice.sendSysNotice(user.id, NoticeScene.BID_FLOW, param);
		
		result.code = 1;
		result.msg = "审核成功,已将标置为[流标]!";
		return result;
	}
	
	

	
	
	/**
	 * 解冻借款保证金
	 *
	 * @param bid_no
	 * @param userId
	 * @param bail 解冻的金额
	 * @param res
	 *
	 * @author yaoyi
	 * @createDate 2015年12月29日
	 */
	private ResultInfo relieveUserBailFund(String serviceOrderNo, String bid_no, long userId, double bail, String title){
		ResultInfo result = new ResultInfo();
		
		boolean fundSignChecked = true;  //资金签名是否通过
		result = userFundService.userFundSignCheck(userId);
		if (result.code < 1) {
			fundSignChecked = false;
		}
		
		boolean unFreeze = userFundService.userFundUnFreeze(userId, bail);
		if(!unFreeze){
			result.code = -1;
			result.msg = "解冻用户资金失败";
			
			return result;
		}
		
		if (fundSignChecked) {
			result = userFundService.userFundSignUpdate(userId);
			if (result.code < 1) {
				
				return result;
			}
		}
		
		t_user_fund tuf = userFundService.queryUserFundByUserId(userId);
		
		/** 添加交易记录 */
		tuf = userFundService.queryUserFundByUserId(tuf.user_id);
		Map<String, String>params = new HashMap<String, String>();
		params.put("bidNo", bid_no);
		boolean minusFund = dealuserservice.addDealUserInfo(
				serviceOrderNo,
				tuf.user_id,
				bail, 
				tuf.balance, 
				tuf.freeze, 
				t_deal_user.OperationType.BAIL_UNFREEZE, 
				params);
		if(!minusFund){
			result.code = -1;
			result.msg = "解冻保证金添加记录失败!";
			
			return result;
		}
		
		/** 发送邮件、站内信、短信通知用户 */
		Map<String, Object> content = new HashMap<String, Object>();
		content.put("user_name", tuf.name);
		content.put("bid_no", bid_no);
		content.put("bid_name", title);
		content.put("bail", bail);
		noticeservice.sendSysNotice(tuf.user_id, NoticeScene.BAIL_THAW, content);
		
		result.code = 1;
		return result;
	}
	
	/**
	 * 返还用户投标金额
	 *
	 * @param serviceOrderNo 订单号
	 * @param bidId
	 * @param bid_no
	 * @param title
	 *
	 * @author yaoyi
	 * @createDate 2015年12月29日
	 */
	private ResultInfo returnInvestUserFund(String serviceOrderNo, long bidId, String bid_no, String title){
		ResultInfo result = new ResultInfo();
		
		List<t_invest> investList = investservice.queryBidInvest(bidId);// 查询用户针对这个标所投的金额
		
		t_bid bid = bidDao.findByID(bidId);
		
		if(investList == null || investList.size() == 0){
			result.code = 1;
			
			return result;
		}
		
		for(t_invest invest : investList){
			
			boolean fundSignChecked = true;  //资金签名是否通过
			result = userFundService.userFundSignCheck(invest.user_id);
			if (result.code < 1) {
				fundSignChecked = false;
			}
			
			boolean unFreeze = userFundService.userFundUnFreeze(invest.user_id, invest.amount - invest.cash_amount);// 返还投标用户冻结资金
			if(!unFreeze){
				result.code = -1;
				result.msg = "返还投资人保证金失败!";
				
				return result;
			}
			
			if (fundSignChecked) {
				result = userFundService.userFundSignUpdate(invest.user_id);
				if (result.code < 1) {
					
					return result;
				}
			}
			
			/** 添加交易记录 */
			t_user_fund userFund = userFundService.queryUserFundByUserId(invest.user_id);
			Map<String, String>params = new HashMap<String, String>();
			params.put("bidNo", bid_no);
			boolean addDeal = dealuserservice.addDealUserInfo(
					serviceOrderNo, 
					userFund.user_id, 
					invest.amount - invest.cash_amount, 
					userFund.balance, 
					userFund.freeze, 
					t_deal_user.OperationType.INVEST_UNFREEZE, params);
			if (!addDeal) { 
				result.code = -1;
				result.msg = "解冻投标金额添加记录失败!";
				
				return result;
			}
			
			/** 筹款失败 发送通知 */
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("user_name", userFund.name);
			param.put("bid_no", bid.bid_no);
			param.put("bid_name", bid.title);
			param.put("amount", invest.amount);
			noticeservice.sendSysNotice(userFund.user_id, NoticeScene.INVEST_FAIL, param);
		}
		
		result.code = 1;
		result.msg = "";
		return result;
	}

	/**
	 * 财务放款（准备）
	 *
	 * @param bid
	 * @param supervisorId
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月12日
	 */
	public ResultInfo release(t_bid bid, long supervisorId) {
		ResultInfo result = new ResultInfo();
		
		if (!t_bid.Status.WAIT_RELEASING.equals(bid.getStatus())) {
			result.code = -1;
			result.msg = "状态非法。当前标的状态为："+bid.getStatus().value;
		
			return result;
		}
		
		result = userFundService.userFundSignCheck(bid.user_id);
		if (result.code < 1) {
			result.code = -1;
			result.msg = "借款人资金异常，无法执行放款";
		
			return result;
		}
		
		//添加管理员事件
		Map<String, String> supervisorEventParam = new HashMap<String, String>();
		supervisorEventParam.put("bid_no", bid.bid_no);
		supervisorEventParam.put("bid_name", bid.title);
		boolean isAdd = supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.RELEASE, supervisorEventParam);
		if (!isAdd) {
			result.code = -1;
			result.msg = "添加管理员事件失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "放款准备完毕";
		
		return result;
	}
	
	/**
	 * 财务放款(执行)
	 *
	 * @param bidId 标的ID
	 * @param releaseSupervisorId 管理员Id
	 * @param serviceOrderNo 业务订单号
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月23日
	 */
	public ResultInfo doRelease(long bidId, long releaseSupervisorId, String serviceOrderNo){
		ResultInfo result = new ResultInfo();

		t_bid bid = findByID(bidId);
		if (bid == null) {
			result.code = -1;
			result.msg = "标的不存在";
			
			return result;
		}
		
		int row = bidDao.updateBidRelease(releaseSupervisorId, bidId);
		if(row < 1){
			result.code = -1;
			result.msg = "更新标的状态失败";
			
			return result;
		}
		
		/* 关闭自动投标 */
		result = investservice.closeAutoInvest(bid.user_id);
		if(result.code  < 1) {
			return result;
		}
		
		/** 借款人资金操作 */
		boolean isLoanSignCheckSuccess = true;
		result = userFundService.userFundSignCheck(bid.user_id);
		if (result.code < 1) {
			isLoanSignCheckSuccess = false;
		}
		
		//借款人资金信息
		t_user_fund loanUserFund = null;
		//添加交易记录是否成功
		boolean isAddDeal = false;  
		//交易记录备注参数
		Map<String, String> summaryParam = new HashMap<String, String>();

		/** 解冻保证金 */
		if (bid.bail > 0) {
			boolean unFreeze = userFundService.userFundUnFreeze(bid.user_id, bid.bail);
			if(!unFreeze){
				result.code = -1;
				result.msg = "解冻保证金失败!";
				
				return result;
			}
			
			//刷新借款人资金信息
			loanUserFund = userFundService.queryUserFundByUserId(bid.user_id);
			summaryParam.clear();
			summaryParam.put("bidNo", bid.bid_no);
			isAddDeal = dealuserservice.addDealUserInfo(serviceOrderNo, 
					bid.user_id,
					bid.bail, 
					loanUserFund.balance,
					loanUserFund.freeze,
					t_deal_user.OperationType.BAIL_UNFREEZE,
					summaryParam);
			if(!isAddDeal){
				result.code = -1;
				result.msg = "返还保证金、添加交易记录失败!";

				return result;
			}
			

			/** 解冻借款保证金 发送通知 */
			Map<String, Object> paramFund = new HashMap<String, Object>();
			paramFund.put("user_name", loanUserFund.name);
			paramFund.put("bid_no", bid.bid_no);
			paramFund.put("bid_name", bid.title);
			paramFund.put("bail", bid.bail);
			noticeservice.sendSysNotice(loanUserFund.user_id, NoticeScene.BAIL_THAW, paramFund);
		}
		
		/** 借款人收款 */
		double addAmt = bid.amount - bid.loan_fee;
		boolean addFund = userFundService.userFundAdd(bid.user_id, addAmt);
		if(!addFund){
			result.code = -1;
			result.msg = "增加借款人资金失败!";
			
			return result;
		}
		
		/** 如果资金未异常 更新借借款人签名。 */
		if (isLoanSignCheckSuccess) {
			userFundService.userFundSignUpdate(bid.user_id);
		}
		
		/** 添加交易记录 */
		loanUserFund = userFundService.queryUserFundByUserId(bid.user_id);
		summaryParam.clear();
		summaryParam.put("bidNo", bid.bid_no);
		isAddDeal = dealuserservice.addDealUserInfo(serviceOrderNo,
				bid.user_id,
				bid.amount, 
				loanUserFund.balance + bid.loan_fee,  //此时不计借款服务费
				loanUserFund.freeze,
				t_deal_user.OperationType.RELEASE_BID,
				summaryParam);
		if(!isAddDeal){
			result.code = -1;
			result.msg = "借款人收款、添加交易记录失败!";

			return result;
		}
		
		/** 添加扣除借款服务费记录 */
		isAddDeal = dealuserservice.addDealUserInfo(serviceOrderNo,
				bid.user_id,
				bid.loan_fee,
				loanUserFund.balance,
				loanUserFund.freeze, 
				t_deal_user.OperationType.LOAN_SERVICE_FEE,
				summaryParam);
		if(!isAddDeal){
			result.code = -1;
			result.msg = "添加扣除借款服务费记录失败!";

			return result;
		}

		Map<String, Object> dealRemarkParam = new HashMap<String, Object>();
		dealRemarkParam.put("bid_no", bid.bid_no);
		boolean addDealPlat = dealPlatformService.addPlatformDeal(
				bid.user_id, 
				bid.loan_fee, 
				t_deal_platform.DealRemark.BID_FEE, dealRemarkParam);
		if (!addDealPlat) {
			result.code = -1;
			result.msg = "添加平台收支记录失败";

			return result;

		}
		
		/** 理财人资金操作 */
		List<t_invest> invests = investservice.queryBidInvest(bidId);//获得用户对这个标所投的金额
		if (invests == null || invests.size() == 0) {
			result.code = -1;
			result.msg = "获得用户对标所投的金额有误!";

			return result;
		}
		
		/** 减去投资人的投标冻结金额 */
		result = investUserAmountProcess(invests, bid, serviceOrderNo);
		if(result.code < 1){
			
			return result;
		}
		
		/** 生成借款账单 */
		createBidBill(bid, result);
		if(result.code < 1){
			
			return result;
		}
	
		/** 生成借款合同(借款人) */
		/*result = creatBidPact(bidId);
		if (result.code < 1) {
			result.msg = "生成合同失败!";

			return result;
		}
		*/
		/** 给借款人通知 */
		
		Map<String, Object> sceneParame = new HashMap<String, Object>();
		sceneParame.put("user_name", loanUserFund.name);
		sceneParame.put("bid_no", bid.bid_no);
		sceneParame.put("bid_name", bid.title);
		sceneParame.put("amount", bid.amount);
		sceneParame.put("period_num", bid.period);
		sceneParame.put("period_unit", bid.getPeriod_unit().getShowValue());
		sceneParame.put("repayment_type", bid.getRepayment_type().value);	
		sceneParame.put("loan_fee", bid.loan_fee);
		sceneParame.put("balance", addAmt);
		noticeservice.sendSysNotice(bid.user_id, NoticeScene.BID_INTEREST, sceneParame);
		
		result.code = 1;
		result.msg = "放款成功";
		
		return result;
	}

	/**
	 * 更新借款服务费
	 *
	 * @param bidId
	 * @param loanServiceFee
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月12日
	 */
	public int updateLoanServiceFee(Long bidId, double loanServiceFee) {
		
		return bidDao.updateLoanServiceFee(bidId, loanServiceFee);
	}
	/**
	 * 更新投标进度、已投金额、加入人次
	 *
	 * @param bidId 借款标ID
	 * @param schedule 投标进度
	 * @param investAmt 投资金额
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月28日
	 */
	public ResultInfo updateBidScheduleAndInvestAmt(long bidId, double amount,double schedule) {
		ResultInfo result = new ResultInfo();
		
		int row = bidDao.updateBidschedule(bidId, amount, schedule);
		
		if (row == 0) {
			result.code = ResultInfo.OVER_BID_AMOUNT;
			result.msg = "超额投资，请解冻投资金额";
			LoggerUtil.info(true, "超额投标。【bidId:%s】", bidId);
			
			return result;
		}
		
		if (row < 0) {
			result.code = -1;
			result.msg = "更新投标进度和已投金额,异常";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "更新投标进度和已投金额成功";
		
		return result;
	}
	/**
	 * 获取产品对象
	 *
	 * @param productId 借款产品id
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月26日
	 */
	public t_product queryProduct(long productId){
		
		return productservice.findByID(productId);
	}
	/**
	 * 查询放款项目数目
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月8日
	 *
	 */
	public int queryReleasedBidsNum() {

		return bidDao.findReleasedBidsNum();
	}

	/**
	 * 查询放款项目总额
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月8日
	 *
	 */
	public double queryTotalBorrowAmount() {

		return bidDao.findTotalBorrowAmount();
	}
	
	/**
	 * 查询流标的Bid(条件：筹款中；过了投标期限；没有满标)
	 *
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月30日
	 */
	public List<t_bid> queryBidIsFlow() {
		
		return bidDao.queryBidIsFlow();
	}
	
	/**
	 * 后台-风控页面-标的列表
	 *
	 * @param currPage
	 * @param pageSize
	 * @param showType 筛选类型  0-所有;1-初审中;2-借款中;3-满标审核;4-还款中;5-已经结束;6-失败
	 * @param exports 1:导出  default：不导出
	 * @param numNo 编号
	 * @param projectName 项目名称
	 * @param userName 借款人
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月4日
	 */
	public PageBean<BackRiskBid> pageOfBidRisk(int showType, int currPage, int pageSize, int exports,int orderType,int orderValue,String userName,String numNo,String projectName,String agencyName) {
		
		return bidDao.pageOfBidRisk(showType, currPage, pageSize, exports, orderType, orderValue, userName, numNo, projectName,agencyName);
	}
	
	/**
	 * 后台-财务页面-标的列表
	 *
	 * @param showType 筛选类型  0-所有;1-待放款;2-已放款(还款中,已还款)
	 * @param currPage
	 * @param pageSize
	 * @param exports 1：导出  default:不导出
	 * @param loanName 借款人
	 * @param orderType 排序栏目  0：编号   2：借款金额  7：放款时间
	 * @param orderValue 排序规则 0,降序，1,升序
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月4日
	 */
	public PageBean<BackFinanceBid> pageOfBidFinance(int showType, int currPage, int pageSize,int exports, String loanName, int orderType, int orderValue) {
		
		return bidDao.pageOfBidFinance(showType, currPage, pageSize, exports, loanName, orderType, orderValue);
	}
	/**
	 * 前台-我的财富-我的借款
	 *
	 * @param userId 用户id
	 * @param pageSize 页大小
	 * @param currPage 当前页
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月25日
	 */
	public PageBean<t_bid> pageOfMyLoan(long userId, int pageSize, int currPage) {
		
		return bidDao.pageOfMyLoan(userId, pageSize, currPage);
	}
	/**
	 * 我的财富-投资管理-我的借款列表
	 *
	 * @param currPage
	 * @param pageSize
	 * @param userId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月13日
	 */
	public PageBean<FrontMyLoanBid> pageOfBidFront(int currPage, int pageSize, long userId){
		
		return bidDao.pageOfBidFront(currPage, pageSize, userId);
	}

	/**
	 * 前台-理财
	 *
	 * @param currPage
	 * @param pageSize
	 * @param productId 借款产品ID
	 * @param status 借款标状态
	 * @param orderType 排序方式
	 * @param orderValue 排序规则
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月13日
	 */
	public PageBean<FrontBid> pageOfBidInvest(int currPage, int pageSize, long productId, int status, int orderType, int orderValue) {
		
		return bidDao.pageOfBidInvest(currPage, pageSize, productId, status, orderType, orderValue);
	}
	
	/**
	 * 前台-理财(新)
	 *
	 * @param currPage
	 * @param pageSize
	 * @param productId 前台借款产品ID
	 * @param status 借款标状态
	 * @param orderType 排序方式
	 * @param orderValue 排序规则
	 * @return
	 *
	 * @author hjs-djk
	 * @createDate 2017年10月11日
	 */
	public PageBean<FrontBid> pageOfBidWebInvest(int currPage, int pageSize, long productId, int status, int orderType, int orderValue, int guaranteModeId,  int period, int vip) {
		
		return bidDao.pageOfWebBidInvest(currPage, pageSize, productId, status, orderType, orderValue,  guaranteModeId, period,   vip);
	}
	
	/**
	 * 后台-首页-待办事项的统计
	 *
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月14日
	 */
	public Map<String, Object> backCountBidInfo(){
		
		return bidDao.backCountBidInfo();
	}
	
	/**
	 * 查找合同相关bid信息
	 *
	 * @param bidId 标的id
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月22日
	 */
	public PactBid findPactBidById(long bidId) {
		PactBid pactBid = bidDao.findPactBidById(bidId);
		return pactBid;
	}
	
	/**
	 * 统计标的借款总额
	 *
	 * @param statusList
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月9日
	 */
	public double findTotalBidAmount(List<Integer> statusList){
		
		return bidDao.findTotalBidAmount(statusList, null, null,null);
	}
	
	/**
	 * 后台-风控-统计标的借款总额
	 *
	 * @param showType 筛选类型  0-所有;1-初审中;2-借款中;3-满标审核;4-还款中;5-已经结束;6-失败
	 * @param numNo 编号
	 * @param projectName 项目名称
	 * @param userName 借款人
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月9日
	 */
	public double findTotalBidAmountRisk(int showType, String userName, String numNo, String projectName){
		
		List<Integer> statusList = new ArrayList<Integer>();
		switch (showType) {
			case 0://所有
				break;
			case 1:{//初审中
				statusList.add(Status.PREAUDITING.code);
				break;
			}
			case 2:{//借款中
				statusList.add(Status.FUNDRAISING.code);
				break;
			}
			case 3:{//满标审核
				statusList.add(Status.AUDITING.code);
				break;
			}
			case 4:{//还款中
				statusList.add(Status.REPAYING.code);
				break;
			}
			case 5:{//已经结束
				statusList.add(Status.REPAYED.code);
				break;
			}
			case 6:{//失败
				statusList = t_bid.Status.FAIL;
				break;
			}
			default:
				break;
		}
		
		return bidDao.findTotalBidAmount(statusList, userName, numNo, projectName);
	}
	
	/**
	 * 后台-财务统计标的借款总额
	 *
	 * @param showType 筛选类型  0-所有;1-待放款;2-已放款(还款中,已还款)
	 * @param loanName 借款人昵称
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月9日
	 */
	public double findTotalBidAmountFinance(int showType, String loanName){
		
		List<Integer> statusList = new ArrayList<Integer>();
		switch (showType) {
			case 0:{//所有
				statusList.add(t_bid.Status.WAIT_RELEASING.code);
				statusList.add(t_bid.Status.REPAYING.code);
				statusList.add(t_bid.Status.REPAYED.code);
				break;
			}
			case 1:{//待放款
				statusList.add(t_bid.Status.WAIT_RELEASING.code);
				break;
			}
			case 2:{//2-已放款
				statusList.add(t_bid.Status.REPAYING.code);
				statusList.add(t_bid.Status.REPAYED.code);
				break;
			}
		}
		
		return bidDao.findTotalBidAmount(statusList,loanName,null,null);
	}
	
	/**
	 * 通过标的编号查找标的ID
	 *
	 * @param merBidNo
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年3月3日
	 */
	public long findIdByMerBidNo (String merBidNo, long defaultVal) {
		
		return bidDao.findIdByMerBidNo(merBidNo, defaultVal);
	}

	/**
	 * 通过标的id查询标的title
	 *
	 * @param bidId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年3月2日
	 */
	public String findBidNameById(long bidId){
		
		return bidDao.findBidNameById(bidId);
	}
	
	/**
	 * 查询所有符合自动投标条件的标的
	 *
	 * @return
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年03月25日
	 */
	public List<t_bid> queryAllBidList(){
		
		return super.findListByColumn("loan_schedule < 95 AND status = 1 and pre_release_time <= ?", new Date());
	}
	
	/**
	 * 查询标的进度
	 *
	 * @param bidId
	 * @return
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年03月25日
	 */
	public double findBidSchedule(long bidId) {
		
		return bidDao.findBidSchedule(bidId);
	}
	
	/**
	 * 查询当前待奖励标的ID
	 * @param rewardGrantType
	 * @return
	 */
	public List<Map<String ,Object>> selectRewardBidId(int rewardGrantType){
		
		return bidDao.selectRewardBidId(rewardGrantType);
	}
	
	/**
	 * 修改当前标的奖励状态
	 * @param bidId
	 * @param type 当前的状态
	 * @param endType 修改后的状态
	 * @return
	 */
	public int updateBidRewardGrantType(long bidId , int type ,int endType){
		
		return bidDao.updateBidRewardGrantType(bidId, type, endType);
	}
	
	/**
	 * 前台-理财
	 *
	 * @param currPage
	 * @param pageSiz
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月13日
	 */
	public PageBean<FrontBid> pageOfBidInvest(int currPage, int pageSize) {
		
		return bidDao.pageOfBidInvest(currPage, pageSize);
	}
	
	/**
	 * 前台-新手理财
	 * 
	 * @param currPage
	 * @param pageSize
	 * @return
	 * 
	 * @author YanPengFei
	 * @createDate 2017年3月2日
	 */
	public PageBean<FrontBid> pageOfNewbieBidInvest(int currPage, int pageSize) {
		
		return bidDao.pageOfNewbieBidInvest(currPage, pageSize);
	}
	
	/**
	 * 查询前台项目数量
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月8日
	 */
	public int findFrontBidsNum() {
		
		return bidDao.findFrontBidsNum();
	}
	
	/**
	 * 前台- 分页查询  某个用户的投资特权记录 
	 * 
	 * @param currPage 当前页
	 * @param pageSize 每页条数
	 * @param userId 用户userId
	 * @return
	 *
	 * @author liudong
	 * @createDate 2015年12月17日
	 */
	public PageBean<MyPrerogativeRecord> pageOfUserPrerogativeRecord(int currPage, int pageSize,long userId) {
		return bidDao.pageOfUserPrerogativeRecord(currPage, pageSize, userId);
	}
	
	@SuppressWarnings("unchecked")
	public boolean update(t_bid bid) {
		return this.basedao.save(bid);
	}
}
