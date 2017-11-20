package hf;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shove.Convert;

import common.constants.ModuleConst;
import common.constants.RemarkPramKey;
import common.enums.BusiType;
import common.enums.Client;
import common.enums.ServiceType;
import common.enums.TradeType;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.OrderNoUtil;
import common.utils.ResultInfo;
import common.utils.number.Arith;
import controllers.payment.hf.HfPaymentCallBackCtrl;
import controllers.payment.hf.HfPaymentRequestCtrl;
import models.common.entity.t_bank_card_user;
import models.common.entity.t_conversion_user;
import models.common.entity.t_corp_info;
import models.common.entity.t_corp_info.Status;
import models.common.entity.t_user_fund;
import models.common.entity.t_user_info;
import models.core.entity.t_bid;
import models.core.entity.t_bill;
import models.core.entity.t_bill_invest;
import models.core.entity.t_invest;
import models.core.entity.t_product;
import models.core.entity.t_red_packet_transfer;
import models.entity.t_payment_request;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import payment.IPayment;
import play.Logger;
import services.common.CorpInfoService;
import services.common.UserFundService;
import services.common.UserInfoService;
import services.common.UserService;
import services.core.BidService;
import services.core.BillInvestService;
import services.core.BillService;
import services.core.DebtService;
import services.core.InvestService;

public class HfPayment implements IPayment {
	
	private static HfPaymentService hfPaymentService = Factory.getSimpleService(HfPaymentService.class);
	
	private static HfPaymentCallBackService hfPaymentCallBackService = Factory.getSimpleService(HfPaymentCallBackService.class);

	private static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	
	private static UserFundService userFundService = Factory.getService(UserFundService.class);
	
	private static InvestService investService = Factory.getService(InvestService.class);
	
	private static BidService bidService = Factory.getService(BidService.class);
	
	private static BillInvestService billInvestService = Factory.getService(BillInvestService.class);
	
	private static BillService billService = Factory.getService(BillService.class);
	
	private static DebtService debtService = Factory.getService(DebtService.class);
	
	private static CorpInfoService corpInfoService = Factory.getService(CorpInfoService.class);
	
	private static UserService userService = Factory.getService(UserService.class);
	
	@Override
	public ResultInfo regist(int client, String serviceOrderNo, long userId, Object... obj) {
		ResultInfo result = new ResultInfo();
		
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);   
		if (userInfo == null) {
			result.code = -1;
			result.msg = "查询用户信息失败";
			
			return result;
		}
		
		//交易订单号
		String orderNo = "";  //开户无需订单号
		
		//托管请求唯一标识
		String requestMark = UUID.randomUUID().toString();
		
		//接口参数拼装
		Map<String,String> reqParams = hfPaymentService.userRegister(userInfo.mobile, userInfo.email, requestMark);
		
		//生成表单
		String html = HfUtils.createFormHtml(reqParams, HfConsts.CHINAPNR_URL);

		//备注参数（全部以“r_”开头）
		reqParams.put(RemarkPramKey.USER_ID, userId+"");
		reqParams.put(RemarkPramKey.CLIENT, client+"");
		
		hfPaymentService.printRequestData(requestMark, userId, serviceOrderNo, orderNo, 
				ServiceType.REGIST, HfPayType.USERREGISTER, reqParams);
	    //app 接口处理
		if(Client.isAppEnum(client)){
			result.code = 1;
			result.msg = "";
			
			result.obj = html;
			return result;
		}
		// 表单提交
		HfPaymentRequestCtrl.getInstance().submitForm(html, client);
		
		return result;
	}
	
	@Override
	public ResultInfo recharge(int client, String serviceOrderNo, long userId, double rechargeAmt, Object... obj) {
		ResultInfo result = new ResultInfo();
		
		t_user_fund userInfo = userFundService.queryUserFundByUserId(userId);   
		if (userInfo == null) {
			result.code = -1;
			result.msg = "查询用户资金信息失败";
			
			return result;
		}
		
		String paymentAccount = userInfo.payment_account;

		//交易订单号
		String orderNo = serviceOrderNo;  //业务单一，直接使用业务订单号
		
		//托管请求唯一标识
		String requestMark = UUID.randomUUID().toString();
		
		//接口参数拼装
		Map<String,String> reqParams = hfPaymentService.netSave(paymentAccount, orderNo, rechargeAmt, requestMark);
		
		//生成表单
		String html = HfUtils.createFormHtml(reqParams, HfConsts.CHINAPNR_URL);

		//备注参数（全部以“r_”开头）
		reqParams.put(RemarkPramKey.USER_ID, userId+"");
		reqParams.put(RemarkPramKey.RECHARGE_AMT, rechargeAmt+"");
		reqParams.put(RemarkPramKey.CLIENT, client+"");
		
		hfPaymentService.printRequestData(requestMark, userId, serviceOrderNo, orderNo, 
				ServiceType.RECHARGE, HfPayType.NETSAVE,  reqParams);
		
	    //app 接口处理
		if(Client.isAppEnum(client)){
			result.code = 1;
			result.msg = "";
			
			result.obj = html;
			return result;
		}
		// 表单提交
		HfPaymentRequestCtrl.getInstance().submitForm(html, client);
		
		return result;
	}
	
	@Override
	public ResultInfo userBindCard(int client, String serviceOrderNo, long userId, Object... obj) {
		ResultInfo result = new ResultInfo();
		
		t_user_fund userInfo = userFundService.queryUserFundByUserId(userId);   
		if (userInfo == null) {
			result.code = -1;
			result.msg = "查询用户资金信息失败";
			
			return result;
		}
		
		String paymentAccount = userInfo.payment_account;

		//交易订单号
		String orderNo = "";  //绑卡无需订单号 
		
		//托管请求唯一标识
		String requestMark = UUID.randomUUID().toString();
		
		//接口参数拼装
		Map<String,String> reqParams = hfPaymentService.userBindCard(paymentAccount, requestMark);
		
		//生成表单
		String html = HfUtils.createFormHtml(reqParams, HfConsts.CHINAPNR_URL);

		//备注参数（全部以“r_”开头）
		reqParams.put(RemarkPramKey.USER_ID, userId+"");
		reqParams.put(RemarkPramKey.CLIENT, client+"");
		
		hfPaymentService.printRequestData(requestMark, userId, serviceOrderNo, orderNo, 
				ServiceType.BIND_CARD, HfPayType.USERBINDCARD, reqParams);
		
	    //app 接口处理
		if(Client.isAppEnum(client)){
			result.code = 1;
			result.msg = "";
			
			result.obj = html;
			return result;
		}
		
		// 表单提交
		HfPaymentRequestCtrl.getInstance().submitForm(html, client);
		
		return result;
	}

	@Override
	public ResultInfo withdrawal(int client, String serviceOrderNo, long userId, double withdrawalAmt,
			String bankAccount, String cashType, Object... obj) {
		ResultInfo result = new ResultInfo();
		
		t_user_fund userInfo = userFundService.queryUserFundByUserId(userId);   
		if (userInfo == null) {
			result.code = -1;
			result.msg = "查询用户资金信息失败";
			
			return result;
		}
		
		String paymentAccount = userInfo.payment_account;

		//交易订单号
		String orderNo = serviceOrderNo;  
		
		//托管请求唯一标识
		String requestMark = UUID.randomUUID().toString();
		
		double withdrawalFee = hfPaymentService.queryServFee(withdrawalAmt, cashType);
		
		//提现手续费
//		double withdrawalFee = FeeCalculateUtil.getWithdrawalFee(withdrawalAmt);
		/* 优化：不收取后台设置的手续费 */
		// double withdrawalFee = 0;
		
		//接口参数拼装
		Map<String,String> reqParams = hfPaymentService.cash(client, paymentAccount, orderNo, withdrawalAmt, withdrawalFee, bankAccount, cashType, requestMark);
		
		//生成表单
		String html = HfUtils.createFormHtml(reqParams, HfConsts.CHINAPNR_URL);
		
		Logger.info(html);

		//备注参数（全部以“r_”开头）
		reqParams.put(RemarkPramKey.USER_ID, userId+"");
		reqParams.put(RemarkPramKey.WITHDRAWAL_AMT, withdrawalAmt+"");
		reqParams.put(RemarkPramKey.WITHDRAWAL_FEE, withdrawalFee+"");
		reqParams.put(RemarkPramKey.CLIENT, client+"");
		
		hfPaymentService.printRequestData(requestMark, userId, serviceOrderNo, orderNo, 
				ServiceType.WITHDRAW, HfPayType.CASH, reqParams);
		
		 //app 接口处理
		if(Client.isAppEnum(client)){
			result.code = 1;
			result.msg = "";
			
			result.obj = html;
			return result;
		}
		
		// 表单提交
		HfPaymentRequestCtrl.getInstance().submitForm(html, client);
		
		return result;
	}
	
	@Override
	public ResultInfo bidCreate(int client, String serviceOrderNo, t_bid bid, int bidCreateFrom, Object... obj) {
		ResultInfo result = new ResultInfo();
		
		if (bid == null) {
			throw new RuntimeException("bid is null");
		}
		
		t_user_fund userInfo = userFundService.queryUserFundByUserId(bid.user_id);   
		if (userInfo == null) {
			result.code = -1;
			result.msg = "查询借款人资金信息失败";
			
			return result;
		}
		
		String paymentAccount = userInfo.payment_account;

		//借款标编号，唯一标识
		bid.mer_bid_no = OrderNoUtil.getBidNo();
		String orderNo = "";  //标的登记无需交易订单号
		
		//托管请求唯一标识
		String requestMark = UUID.randomUUID().toString();
		
		//计算还款日期
		Date pubTime = bid.time;
		t_product.PeriodUnit periodUnit = bid.getPeriod_unit();  //借款期限类型1:月;2:日;
		int period = bid.period;  //借款期限
		Date repaymentTime = null;
		switch (periodUnit) {
		case DAY:
			repaymentTime = DateUtil.addDay(pubTime, period);
			break;
		case MONTH:
			repaymentTime = DateUtil.addMonth(pubTime, period);
			break;
		}
		//还款日期
		String retDate = HfUtils.getFormatDate("yyyyMMdd", repaymentTime);
		
		//接口参数拼装
		Map<String,String> reqParams = hfPaymentService.addBidInfo(bid.mer_bid_no, paymentAccount, bid.amount,
				bid.apr, bid.amount, retDate, requestMark);
		
		//备注参数（全部以“r_”开头）
		Map<String,String> remarkParams = new LinkedHashMap<String, String>();
		remarkParams.putAll(reqParams);
		remarkParams.put(RemarkPramKey.USER_ID, bid.user_id+"");
		remarkParams.put(RemarkPramKey.PAYMENT_ACCOUNT, paymentAccount);
		remarkParams.put(RemarkPramKey.BID, new Gson().toJson(bid));
		
		hfPaymentService.printRequestData(requestMark, bid.user_id, serviceOrderNo, orderNo, 
				ServiceType.BID_CREATE, HfPayType.ADDBIDINFO, remarkParams);
		
		//请求第三方
		Map<String, String> respParams = HfUtils.httpPost(reqParams);
		
		hfPaymentService.printCallBackData("标的登记响应", respParams, ServiceType.BID_CREATE, HfPayType.ADDBIDINFO);
		
		result = hfPaymentCallBackService.addBidInfo(respParams);

		HfPaymentCallBackCtrl.getInstance().addBidInfoWS(result, bid, bidCreateFrom);
		
		return result;
	}
	
	@Override
	public ResultInfo bidFailed(int client, String serviceOrderNo, t_bid bid, ServiceType serviceType, Object... obj) {
		ResultInfo result = new ResultInfo();
		
		List<t_invest> investList = investService.queryBidInvest(bid.id);
		String freezeTrxId = "";  //冻结订单号
		
		//借款人保证金解冻
		if (StringUtils.isNotBlank(bid.trust_order_no)) {
			
			freezeTrxId = bid.trust_order_no;
			result = hfPaymentService.doUserUnFreeze(bid.user_id, serviceType, HfPayType.USRUNFREEZE, serviceOrderNo, freezeTrxId);
			if (result.code < 1 && result.code != ResultInfo.ALREADY_RUN) {
				
				return result; 
			}
		}
		
		//投资人资金解冻
		for (t_invest invest : investList) {
			
			freezeTrxId = invest.trust_order_no;
			result = hfPaymentService.doUserUnFreeze(invest.user_id, serviceType, HfPayType.USRUNFREEZE, serviceOrderNo, freezeTrxId);
			if (result.code < 1 && result.code != ResultInfo.ALREADY_RUN) {
				
				return result;
			}
		}
		
		switch (serviceType) {
		case BID_FLOW:  //系统自动流标
			result = bidService.doBidFlow(serviceOrderNo, bid);
			break;
		case BID_PRE_AUDIT_FAIL:{  //初审不通过
			String suggest = obj[0].toString();
			long supervisorId = (Long) obj[1];
			result = bidService.doPreAuditBidNotThrough(serviceOrderNo, bid.id, suggest, supervisorId);
			break;
		}
		case BID_AUDIT_FAIL:{ //复审不通过
			String suggest = obj[0].toString();
			long supervisorId = (Long) obj[1];
			result = bidService.doAuditBidNotThrough(serviceOrderNo, bid.id, suggest, supervisorId);
			break;
		}
		default:
			break;
		}
		
		return result;
	}
	
	@Override
	public ResultInfo invest(int client, int investType, String serviceOrderNo, long userId, t_bid bid, double investAmt, Object... obj) {
		ResultInfo result = new ResultInfo();
		
		t_user_fund investUserInfo = userFundService.queryUserFundByUserId(userId);   
		if (investUserInfo == null) {
			result.code = -1;
			result.msg = "查询理财人资金信息失败";
			
			return result;
		}
		
		String paymentAccount = investUserInfo.payment_account;
		
		t_user_fund loanUserInfo = userFundService.queryUserFundByUserId(bid.user_id);   
		if (loanUserInfo == null) {
			result.code = -1;
			result.msg = "查询借款人资金信息失败";
			
			return result;
		}
		
		//借款人信息
		List<Map<String, String>> borrowerDetails = new LinkedList<Map<String, String>>();		
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("BorrowerCustId", loanUserInfo.payment_account); //借款人第三方唯一标示
		map.put("BorrowerAmt", HfUtils.formatAmount(investAmt) + ""); //借款金额
		map.put("BorrowerRate", HfConsts.BORROWERRATE);
		map.put("ProId", bid.mer_bid_no); //标的号即项目号
		borrowerDetails.add(map);
		
		//交易订单号
		String orderNo = serviceOrderNo;
		
		//冻结订单号
		String freezeOrdId = OrderNoUtil.getNormalOrderNo(ServiceType.INVEST);
		
		//托管请求唯一标识
		String requestMark = UUID.randomUUID().toString();
		Map<String, String> reqParams = new HashMap<String, String>();
		double redPacketAmt = 0.0;
		long redPacketId = 0L;
		double cashAmt = 0.0;
		long cashId = 0L;
		long rateId = 0L;
		double addRate = 0.0;
		
		if (obj.length >= 5) {
			redPacketAmt = Convert.strToDouble(obj[0].toString(), 0.0);
			redPacketId = Convert.strToLong(obj[1].toString(), 0L);
			cashAmt = Convert.strToDouble(obj[2].toString(), 0.0);
			cashId = Convert.strToLong(obj[3].toString(), 0L);
			rateId = Convert.strToLong(obj[4].toString(), 0L);
			addRate = Convert.strToDouble(obj[5].toString(), 0.0);
		}
		
		if (redPacketId > 0L && cashId > 0L) {
			result.code = -1;
			result.msg = "红包及现金券不能同时使用";
			
			return result;
		}
		
		//仅使用现金券不使用红包
		if (cashId > 0L && redPacketId <= 0L) {
			//接口参数拼装
			reqParams = hfPaymentService.investRedPacket(orderNo, investAmt, paymentAccount, 
					new Gson().toJson(borrowerDetails), freezeOrdId, requestMark, cashAmt);
		} else if ((cashId <= 0L && redPacketId > 0L) || (cashId <= 0L && redPacketId <= 0L)) { //①仅使用红包；②红包和现金券都不使用
			reqParams = hfPaymentService.invest(orderNo, investAmt, paymentAccount, 
					new Gson().toJson(borrowerDetails), freezeOrdId, requestMark);
		}
		
		//生成表单
		String html = HfUtils.createFormHtml(reqParams, HfConsts.CHINAPNR_URL);

		//备注参数（全部以“r_”开头）
		reqParams.put(RemarkPramKey.USER_ID, userId + "");
		reqParams.put(RemarkPramKey.BID_ID, bid.id + "");
		reqParams.put(RemarkPramKey.INVEST_AMT, investAmt + "");
		reqParams.put(RemarkPramKey.CLIENT, client + "");
		reqParams.put(RemarkPramKey.INVEST_TYPE, investType + "");
		reqParams.put(RemarkPramKey.RED_PACKET_AMT, redPacketAmt + "");
		reqParams.put(RemarkPramKey.RED_PACKET_ID, redPacketId + "");
		reqParams.put(RemarkPramKey.CASH_AMT, cashAmt + "");
		reqParams.put(RemarkPramKey.CASH_ID, cashId + "");
		reqParams.put(RemarkPramKey.RATE_ID, rateId + "");
		reqParams.put(RemarkPramKey.ADD_RATE, addRate + "");
		
		hfPaymentService.printRequestData(requestMark, userId, serviceOrderNo, orderNo, 
				ServiceType.INVEST, HfPayType.INITIATIVETENDER, reqParams);
		
		//app 接口处理
		if (Client.isAppEnum(client)) {
			result.code = 1;
			result.msg = "";
			result.obj = html;
			
			return result;
		}
		
		// 表单提交
		HfPaymentRequestCtrl.getInstance().submitForm(html, client);
		
		return result;
	}
	
	@Override
	public ResultInfo bidSuditSucc(int client, String serviceOrderNo, long releaseSupervisorId,  t_bid bid, Object... obj) {
		ResultInfo result = new ResultInfo();
		
		if (bid == null) {
			throw new RuntimeException("bid is null");
		}

		/** 解冻保证金 */
		if (bid.bail > 0) {
			String freezeTrxId = bid.trust_order_no;  //汇付返回的冻结订单号
			result = hfPaymentService.doUserUnFreeze(bid.user_id, ServiceType.BID_AUDIT_SUCC, HfPayType.USRUNFREEZE, serviceOrderNo, freezeTrxId);
			if (result.code < 1 && result.code != ResultInfo.ALREADY_RUN) {
				
				return result;
			}
		}
		
		List<t_invest> invests = investService.queryBidInvest(bid.id);
		if (invests == null || invests.size() == 0) {
			result.code = -1;
			result.msg = "查询投资列表失败";

			return result;
		}
		
		//借款人第三方唯一标示
		t_user_fund loanUserFund = userFundService.queryUserFundByUserId(bid.user_id);
		if (loanUserFund == null) {
			result.code = -1;
			result.msg = "查询借款人资金信息失败";
			
			return result;
		}
		String inCustId = loanUserFund.payment_account;
		String subOrdDate = HfUtils.getFormatDate("yyyyMMdd");
		
		t_user_fund investUserFund = null;
		int successCount = 0;
		double loanServiceFee = 0;
		for (t_invest invest : invests) {
			investUserFund = userFundService.queryUserFundByUserId(invest.user_id);   
			if (investUserFund == null) {
				result.code = -1;
				result.msg = "查询投资人资金信息失败";
				
				return result;
			}
			
			//交易订单号
			String orderNo = OrderNoUtil.getNormalOrderNo(ServiceType.BID_AUDIT_SUCC);
			
			//托管请求唯一标识
			String requestMark = UUID.randomUUID().toString();
			
			//投资人第三方唯一标示
			String paymentAccount = investUserFund.payment_account;
			//投标流水号，即投标请求第三方的流水号
			String subOrdId = invest.service_order_no;
			//投资汇付返回的冻结订单号
			String freezeOrdId = invest.trust_order_no;
			//解冻订单号
			String unFreezeOrdId = freezeOrdId + "1";
			//扩展域
			String reqExt = "{\"ProId\":\""+bid.mer_bid_no+"\"}";
			
			if (invest.cash_amount > 0.0 && invest.cash_id > 0L) {
				reqExt = "{\"LoansVocherAmt\":\""+HfUtils.formatAmount(invest.cash_amount)+"\"";
				reqExt = reqExt + ",\"ProId\":\""+bid.mer_bid_no+"\"}";
			}
			//手续费
			double fee = invest.loan_service_fee_divide;
			
			if (invest.cash_amount > 0.0 && invest.cash_id > 0L) {
				reqExt = "{\"LoansVocherAmt\":\""+HfUtils.formatAmount(invest.cash_amount)+"\"";
				reqExt = reqExt + ",\"ProId\":\""+bid.mer_bid_no+"\"}";
			}
			
			//手续费子账号分配
			JSONArray divDetailAarray = new JSONArray();
			JSONObject divDetailsJson = new JSONObject();
			divDetailsJson.put("DivCustId", HfConsts.MERCUSTID);
			divDetailsJson.put("DivAcctId", HfConsts.SERVFEEACCTID);
			divDetailsJson.put("DivAmt", HfUtils.formatAmount(fee));
			divDetailAarray.add(divDetailsJson);
			String divDetails = divDetailAarray.toString();
			
			LinkedHashMap<String, String> reqParams = hfPaymentService.bidAuditSucc(orderNo, paymentAccount, invest.amount, invest.loan_service_fee_divide,
					subOrdId, subOrdDate, inCustId, unFreezeOrdId, freezeOrdId, divDetails, reqExt,bid.mer_bid_no, requestMark);
			
			hfPaymentService.printRequestData(requestMark, bid.user_id, serviceOrderNo, orderNo,
					ServiceType.BID_AUDIT_SUCC, HfPayType.LOANS, reqParams);
			
			//请求第三方
			Map<String, String> respParams = HfUtils.httpPost(reqParams);
			
			hfPaymentService.printCallBackData("放款响应", respParams, ServiceType.BID_AUDIT_SUCC, HfPayType.LOANS);
			
			//签名，状态码，仿重单处理;
			result = hfPaymentService.checkSign(respParams, HfPayType.LOANS);
			if(result.code > 0){
				successCount++;
			}
			
			//计算借款服务费
			loanServiceFee += fee;
		}

		//全部放款成功才算成功
		if (successCount != invests.size()) {
			result.code = -1;
			result.msg = "放款失败"; 
			
			return result;
		}
				
		//借款服务费纠偏
		bidService.updateLoanServiceFee(bid.id, loanServiceFee);
		
		result = bidService.doRelease(bid.id, releaseSupervisorId, serviceOrderNo);
		if (result.code < 1) {
			
			return result;
		}
		
		/**  需求变更：放款成功后，发放cps费，财富圈返佣    add v9.3.2 */
		//投标成功发送 CPS 佣金
		if(ModuleConst.EXT_CPS){
			services.ext.cps.CpsService cpsService = Factory.getService(services.ext.cps.CpsService.class);
			cpsService.giveCpsCommission(bid.id);
		}
		//end
		
		//投标成功发送 财富圈 佣金
		if(ModuleConst.EXT_WEALTHCIRCLE){
			service.ext.wealthcircle.WealthCircleService wealthCircleService = Factory.getService(service.ext.wealthcircle.WealthCircleService.class);
			wealthCircleService.giveWcCommission(bid.id);
		}
		//end
		
		result.code = 1;
		result.msg = "放款成功";
		
		return result;
	}
	
	@Override
	public ResultInfo repayment(int client, String serviceOrderNo, t_bill bill, List<Map<String, Double>> billInvestFeeList, Object... obj) throws RuntimeException {
		ResultInfo result = new ResultInfo();
		
		if (bill == null) {
			throw new RuntimeException("bill is null");
		}
		
		t_bid bid = bidService.findByID(bill.bid_id);
		if (bid == null) {
			result.code = -1;
			result.msg = "查询借款标信息失败";
			
			return result;
		}
		
		t_user_fund loanUserFund = userFundService.queryUserFundByUserId(bill.user_id);
		if (loanUserFund == null) {
			result.code = -1;
			result.msg = "查询借款人资金信息失败";
			
			return result;
		}
		
		//交易订单号
		//String repaymentOrderNo = serviceOrderNo;
		
		//托管请求唯一标识
		//String requestMark = UUID.randomUUID().toString();
		
		List<t_bill_invest> billInvestList = billInvestService.queryNOReceiveInvestBills(bill.bid_id, bill.period);
		if (billInvestList == null || billInvestList.size() <= 0) {
			result.code = -1;
			result.msg = "查询待还的理财账单失败";
			
			return result;
		}
		
		//JSONObject batchRepaymentJson = new JSONObject();
		//JSONArray investArray = new JSONArray();

		int listSize = billInvestList.size();
		
		int errorCount = 0;
		
		StringBuffer s = new StringBuffer("");
		
		for(int i = 0; i < listSize; i++) {
			//托管请求唯一标识
			String merPriv = UUID.randomUUID().toString();
			t_bill_invest billInvest = billInvestList.get(i);
			Map<String, Double> billInvestFee = billInvestFeeList.get(i);
			String orderNo = OrderNoUtil.getSpecialOrderNo(billInvest.time, ServiceType.REPAYMENT,
					OrderNoUtil.SPECIAL_BILL_INVEST, billInvest.id);
			t_invest invest = investService.findByID(billInvest.invest_id);
			t_user_fund investUserFund = userFundService.queryUserFundByUserId(billInvest.user_id);
			if (investUserFund == null) {
				errorCount++;
				s.append("编号：" + billInvest.id + "理财账单还款失败【借款人用户资产查询失败】\n");
				continue;
			}
			double principalAmt = billInvest.receive_corpus;
			double interestAmt = billInvest.receive_interest + billInvestFee.get("overdueFine");
			double fee = billInvestFee.get("investServiceFee");
			
			String divDetails = "";
			
			if(fee != 0){
				JSONArray details = new JSONArray();
				JSONObject divJson = new JSONObject();
				divJson.put("DivCustId", HfConsts.MERCUSTID);
				divJson.put("DivAcctId", HfConsts.SERVFEEACCTID);
				divJson.put("DivAmt", HfUtils.formatAmount(fee));
				details.add(divJson);
				divDetails = details.toString();
			}
			Map<String, String> requestMap = hfPaymentService.repayment_3(bid.mer_bid_no, orderNo,
					invest.service_order_no, invest.time, loanUserFund.payment_account, principalAmt, interestAmt, fee,
					investUserFund.payment_account, divDetails, merPriv, null);
			
			
			
			Map<String,String> remarkParams = new LinkedHashMap<String, String>();
			remarkParams.putAll(requestMap);
			remarkParams.put(RemarkPramKey.BILL_ID, bill.id+"");
			remarkParams.put(RemarkPramKey.BILL_INVEST_ID, billInvest.id+"");
			remarkParams.put(RemarkPramKey.BILL_INVEST_FEE, new Gson().toJson(billInvestFeeList.get(i)));
			
			hfPaymentService.printRequestData(merPriv, bill.user_id, orderNo, orderNo,
					ServiceType.REPAYMENT, HfPayType.REPAYMENT, remarkParams);
			
			Map<String, String> respParams = HfUtils.httpPost(requestMap);
			
			hfPaymentService.printCallBackData("正常还款响应", respParams, ServiceType.REPAYMENT, HfPayType.REPAYMENT);
			
			result = hfPaymentCallBackService.repayment(respParams);
			
			if (result.code < 1) {
				s.append("编号：" + billInvest.id + "理财账单还款失败【" + result.msg + "】\n");
			}
			s.append("编号：" + billInvest.id + "理财账单还款成功\n");
		}
		
		result.msg = "还款请求结束，共还账单：" + listSize + "，成功：" + (listSize - errorCount) + "失败：" + errorCount + "\n" + s.toString(); 
		
		if(errorCount == listSize) {
			// 全部失败
			result.code = 999;
		} else {
			if(errorCount > 0) {
				// 部分失败
				result.code = 555;
			} else {
				// 全部成功
				result.code = 1;
			}
		}
		
		return result;
		/*for (int i = 0; i < listSize; i++) {
			t_bill_invest billInvest = billInvestList.get(i);
			Map<String, Double> billInvestFee = billInvestFeeList.get(i);
			//投资人收益
			double pInAmt = billInvest.receive_interest + billInvest.receive_corpus + billInvestFee.get("overdueFine");
			//投资管理费
			double pInFee = billInvestFee.get("investServiceFee");
			
			//投资人信息
			t_user_fund investUserFund = userFundService.queryUserFundByUserId(billInvest.user_id);
			if (investUserFund == null) {
				result.code = -1;
				result.msg = "查询理财人信息失败";
				
				return result;
			}
			
			t_invest invest = investService.findByID(billInvest.invest_id);
			if (invest == null) {
				result.code = -1;
				result.msg = "查询投资信息失败";
				
				return result;
			}
			
			*//** 特殊订单号：同一笔还款，订单号一致 *//*
			String orderNo = OrderNoUtil.getSpecialOrderNo(billInvest.time, ServiceType.REPAYMENT, OrderNoUtil.SPECIAL_BILL_INVEST, billInvest.id);
			
			JSONObject investUserJson = new JSONObject();
			investUserJson.put("InCustId", investUserFund.payment_account);
			investUserJson.put("OrdId", orderNo);
			investUserJson.put("SubOrdId", invest.service_order_no);
			investUserJson.put("TransAmt", HfUtils.formatAmount(pInAmt));
				
			if(pInFee != 0){
				investUserJson.put("FeeObjFlag", "I");
				investUserJson.put("Fee", HfUtils.formatAmount(pInFee));
				
				JSONArray divDetails = new JSONArray();
				JSONObject divJson = new JSONObject();
				divJson.put("DivCustId", HfConsts.MERCUSTID);
				divJson.put("DivAcctId", HfConsts.SERVFEEACCTID);
				divJson.put("DivAmt", HfUtils.formatAmount(pInFee));
				divDetails.add(divJson);
				
				investUserJson.put("DivDetails", divDetails);
			}
			
			investArray.add(investUserJson);
		}*/
		
		// batchRepaymentJson.put("InDetails", investArray);

		// String inDetails = batchRepaymentJson.toString();
		// String merOrdDate = HfUtils.getFormatDate("yyyyMMdd");
		
		//接口参数拼装
		/*Map<String,String> reqParams = hfPaymentService.repayment(loanUserFund.payment_account, repaymentOrderNo,
				merOrdDate, inDetails, bid.mer_bid_no, requestMark);*/
				
		//备注参数（全部以“r_”开头）
		/*Map<String,String> remarkParams = new LinkedHashMap<String, String>();
		remarkParams.putAll(reqParams);
		remarkParams.put(RemarkPramKey.BILL_ID, bill.id+"");
		remarkParams.put(RemarkPramKey.BILL_INVEST_FEE_LIST, new Gson().toJson(billInvestFeeList));
		
		hfPaymentService.printRequestData(requestMark, bill.user_id, serviceOrderNo, repaymentOrderNo,
				ServiceType.REPAYMENT, HfPayType.BATCHREPAYMENT, remarkParams);*/
		
		//请求第三方
		//Map<String, String> respParams = HfUtils.httpPost(reqParams);
		
		
		// return result;
	}
	
	@Override
	public ResultInfo advanceRepayment(int client, String serviceOrderNo, t_bill bill, Object... obj) {
		ResultInfo result = new ResultInfo();
		
		if (bill == null) {
			throw new RuntimeException("bill is null");
		}
		
		t_user_fund loanUserFund = userFundService.queryUserFundByUserId(bill.user_id);
		if (loanUserFund == null) {
			result.code = -1;
			result.msg = "查询借款人资金信息失败";
			
			return result;
		}
		
		t_bid bid = bidService.findByID(bill.bid_id);
		if (bid == null) {
			result.code = -1;
			result.msg = "查询借款标信息失败";
			
			return result;
		}
		
		//交易订单号
		String repaymentOrderNo = serviceOrderNo;
		
		//托管请求唯一标识
		String requestMark = UUID.randomUUID().toString();
		
		List<t_bill_invest> billInvestList = billInvestService.queryReceiveInvestBills(bill.bid_id, bill.period);
		if (billInvestList == null || billInvestList.size() <= 0) {
			result.code = -1;
			result.msg = "查询已还的理财账单失败";
			
			return result;
		}
		
		JSONObject batchRepaymentJson = new JSONObject();
		JSONArray investArray = new JSONArray();
		
		int listSize = billInvestList.size();
		double loanOverdueFine = 0;  //结算逾期罚息费
		for (int i = 0; i < listSize; i++) {
			t_bill_invest billInvest = billInvestList.get(i);

			//垫付人收款
			double pInAmt = billInvest.receive_interest + billInvest.receive_corpus + billInvest.overdue_fine;

			// 投资信息
			t_invest invest = investService.findByID(billInvest.invest_id);
			if (invest == null) {
				result.code = -1;
				result.msg = "查询投资信息失败";
				
				return result;
			}
			
			/** 特殊订单号：同一笔还款，订单号一致 */
			String orderNo = OrderNoUtil.getSpecialOrderNo(billInvest.time, ServiceType.REPAYMENT_AFER_ADVANCE, OrderNoUtil.SPECIAL_BILL_INVEST, billInvest.id);
			
			JSONObject investUserJson = new JSONObject();
			investUserJson.put("InCustId", HfConsts.MERCUSTID);  //垫付还款，收款人为原垫付方，即商户
			investUserJson.put("InAcctId", HfConsts.TRANSFEROUTACCTID);  //商户子账户
			investUserJson.put("OrdId", orderNo);
			investUserJson.put("SubOrdId", invest.service_order_no);
			investUserJson.put("TransAmt", HfUtils.formatAmount(pInAmt));

			investArray.add(investUserJson);
			
			loanOverdueFine += billInvest.overdue_fine;  //结算逾期罚息费
		}
		
		batchRepaymentJson.put("InDetails", investArray);
		
		String inDetails = batchRepaymentJson.toString();
		String merOrdDate = HfUtils.getFormatDate("yyyyMMdd");
		
		//接口参数拼装
		Map<String,String> reqParams = hfPaymentService.advanceRepayment(loanUserFund.payment_account, repaymentOrderNo,
				merOrdDate, inDetails, bid.mer_bid_no, requestMark);
		
		//备注参数（全部以“r_”开头）
		Map<String,String> remarkParams = new LinkedHashMap<String, String>();
		remarkParams.putAll(reqParams);
		remarkParams.put(RemarkPramKey.BILL_ID, bill.id+"");
		remarkParams.put(RemarkPramKey.LOAN_OVERDUE_FINE, loanOverdueFine+"");  //保存罚息费，回调使用
		
		hfPaymentService.printRequestData(requestMark, bill.user_id, serviceOrderNo, repaymentOrderNo,
				ServiceType.REPAYMENT_AFER_ADVANCE, HfPayType.BATCHREPAYMENT, remarkParams);
		
		//请求第三方
		Map<String, String> respParams = HfUtils.httpPost(reqParams);
		
		hfPaymentService.printCallBackData("垫付后还款响应", respParams, ServiceType.REPAYMENT_AFER_ADVANCE, HfPayType.BATCHREPAYMENT);
		
		result = hfPaymentCallBackService.advanceRepayment(respParams);
		if (result.code < 1) {
			
			return result;
		}
		
		result.code = 1;
		result.msg = "垫付后还款成功";
		
		return result;
	}
	
	@Override
	public ResultInfo advance(int client, String serviceOrderNo, t_bill bill, List<Map<String, Double>> billInvestFeeList, Object... obj) {
		ResultInfo result = new ResultInfo();
		
		if (bill == null) {
			throw new RuntimeException("bill is null");
		}

		t_bid bid = bidService.findByID(bill.bid_id);
		if (bid == null) {
			result.code = -1;
			result.msg = "查询借款标信息失败";
			
			return result;
		}
		
		List<t_bill_invest> billInvestList = billInvestService.queryNOReceiveInvestBills(bill.bid_id, bill.period);
		if (billInvestList == null || billInvestList.size() <= 0) {
			result.code = -1;
			result.msg = "查询待还的理财账单失败";
			
			return result;
		}
		
		//托管请求唯一标识
		String requestMark = UUID.randomUUID().toString();

		JSONObject batchRepaymentJson = new JSONObject();
		JSONArray investArray = new JSONArray();
		
		//借款人
		t_user_fund loanUserInfo = userFundService.queryUserFundByUserId(bill.user_id);   
		if (loanUserInfo == null) {
			result.code = -1;
			result.msg = "查询借款人资金信息失败";
			
			return result;
		}
		
		int listSize = billInvestList.size();
		for (int i=0; i<listSize; i++) {
			
			t_bill_invest billInvest = billInvestList.get(i);
			Map<String, Double> billInvestFee = billInvestFeeList.get(i);
			
			t_user_fund investUserFund = userFundService.queryUserFundByUserId(billInvest.user_id);
			if (investUserFund == null) {
				result.code = -1;
				result.msg = "获取理财人信息失败";
				
				return result;
			}
			
			t_invest invest = investService.findByID(billInvest.invest_id);
			if (invest == null) {
				result.code = -1;
				result.msg = "查询理财人投资信息失败";
				
				return result;
			}
			
			//交易订单号
			String orderNo = OrderNoUtil.getSpecialOrderNo(billInvest.time, ServiceType.ADVANCE, OrderNoUtil.SPECIAL_BILL_INVEST, billInvest.id);
			
			//投资人收益
			double pInAmt = billInvest.receive_interest + billInvest.receive_corpus + billInvestFee.get("overdueFine");
			//投资管理费
			double pInFee = billInvestFee.get("investServiceFee");
			
			JSONObject investUserJson = new JSONObject();
			investUserJson.put("InCustId", investUserFund.payment_account);
			investUserJson.put("OrdId", orderNo);
			investUserJson.put("SubOrdId", invest.service_order_no);
			investUserJson.put("TransAmt", HfUtils.formatAmount(pInAmt));
			investUserJson.put("DzBorrCustId", loanUserInfo.payment_account);  // 被垫资人客户号
				
			if(pInFee != 0){
				investUserJson.put("FeeObjFlag", "I");
				investUserJson.put("Fee", HfUtils.formatAmount(pInFee));
				
				JSONArray divDetails = new JSONArray();
				JSONObject divJson = new JSONObject();
				divJson.put("DivCustId", HfConsts.MERCUSTID);
				divJson.put("DivAcctId", HfConsts.SERVFEEACCTID);
				divJson.put("DivAmt", HfUtils.formatAmount(pInFee));
				divDetails.add(divJson);
				
				investUserJson.put("DivDetails", divDetails);
			}
			
			investArray.add(investUserJson);
		}
		
		batchRepaymentJson.put("InDetails", investArray);
		
		String inDetails = batchRepaymentJson.toString();
		String merOrdDate = HfUtils.getFormatDate("yyyyMMdd");

		// 参数组装
		LinkedHashMap<String, String> paramMap = hfPaymentService.advance(serviceOrderNo, merOrdDate, inDetails, bid.mer_bid_no, requestMark);
		
		//备注参数（全部以“r_”开头）
		Map<String,String> remarkParams = new LinkedHashMap<String, String>();
		remarkParams.putAll(paramMap);
		remarkParams.put(RemarkPramKey.BILL_ID, bill.id+"");
		remarkParams.put(RemarkPramKey.BILL_INVEST_FEE_LIST, new Gson().toJson(billInvestFeeList));
		remarkParams.put(RemarkPramKey.SERVICE_ORDER_NO, serviceOrderNo);
		
		hfPaymentService.printRequestData(requestMark, bill.user_id, serviceOrderNo, serviceOrderNo,
				ServiceType.ADVANCE, HfPayType.BATCHREPAYMENT, remarkParams);
		
		//请求第三方
		Map<String, String> respParams = HfUtils.httpPost(paramMap);
		
		hfPaymentService.printCallBackData("本息垫付响应", respParams, ServiceType.ADVANCE, HfPayType.BATCHREPAYMENT);
		//签名，状态码，仿重单处理;
		result = hfPaymentService.checkSign(respParams, HfPayType.BATCHREPAYMENT);
		if (result.code < 1) {
			
			return result;
		}
		
		// 本息垫付业务逻辑
		billService.doPrincipalAdvance(bill.id, serviceOrderNo, billInvestFeeList);
		
		result.code = 1;
		result.msg = "垫付成功";
		
		return result;
	}
	
	@Override
	public ResultInfo offlineReceive(int client, String serviceOrderNo,t_bill bill, List<Map<String, Double>> billInvestFeeList, Object... obj) {
		ResultInfo result = new ResultInfo();
		
		if (bill == null) {
			throw new RuntimeException("bill is null");
		}
		
		t_bid bid = bidService.findByID(bill.bid_id);
		if (bid == null) {
			result.code = -1;
			result.msg = "查询借款标信息失败";
			
			return result;
		}

		List<t_bill_invest> billInvestList = billInvestService.queryNOReceiveInvestBills(bill.bid_id, bill.period);
		if (billInvestList == null || billInvestList.size() <= 0) {
			result.code = -1;
			result.msg = "查询待还的理财账单失败";
			
			return result;
		}
		
		//托管请求唯一标识
		String requestMark = UUID.randomUUID().toString();

		JSONObject batchRepaymentJson = new JSONObject();
		JSONArray investArray = new JSONArray();
		
		//借款人
		t_user_fund loanUserInfo = userFundService.queryUserFundByUserId(bill.user_id);   
		if (loanUserInfo == null) {
			result.code = -1;
			result.msg = "查询借款人资金信息失败";
			
			return result;
		}
		
		int listSize = billInvestList.size();
		for (int i=0; i<listSize; i++) {
			
			t_bill_invest billInvest = billInvestList.get(i);
			Map<String, Double> billInvestFee = billInvestFeeList.get(i);
			
			t_user_fund investUserFund = userFundService.queryUserFundByUserId(billInvest.user_id);
			if (investUserFund == null) {
				result.code = -1;
				result.msg = "获取理财人信息失败";
				
				return result;
			}
			
			t_invest invest = investService.findByID(billInvest.invest_id);
			if (invest == null) {
				result.code = -1;
				result.msg = "查询理财人投资信息失败";
				
				return result;
			}
			
			//交易订单号
			String orderNo = OrderNoUtil.getSpecialOrderNo(billInvest.time, ServiceType.OFFLINE_RECEIVE, OrderNoUtil.SPECIAL_BILL_INVEST, billInvest.id);
			
			//投资人收益
			double pInAmt = billInvest.receive_interest + billInvest.receive_corpus + billInvestFee.get("overdueFine");
			//投资管理费
			double pInFee = billInvestFee.get("investServiceFee");
			
			JSONObject investUserJson = new JSONObject();
			investUserJson.put("InCustId", investUserFund.payment_account);
			investUserJson.put("OrdId", orderNo);
			investUserJson.put("SubOrdId", invest.service_order_no);
			investUserJson.put("TransAmt", HfUtils.formatAmount(pInAmt));
			investUserJson.put("DzBorrCustId", loanUserInfo.payment_account);  // 被垫资人客户号
				
			if(pInFee != 0){
				investUserJson.put("FeeObjFlag", "I");
				investUserJson.put("Fee", HfUtils.formatAmount(pInFee));
				
				JSONArray divDetails = new JSONArray();
				JSONObject divJson = new JSONObject();
				divJson.put("DivCustId", HfConsts.MERCUSTID);
				divJson.put("DivAcctId", HfConsts.SERVFEEACCTID);
				divJson.put("DivAmt", HfUtils.formatAmount(pInFee));
				divDetails.add(divJson);
				
				investUserJson.put("DivDetails", divDetails);
			}
			
			investArray.add(investUserJson);
		}
		batchRepaymentJson.put("InDetails", investArray);
		
		String inDetails = batchRepaymentJson.toString();
		String merOrdDate = HfUtils.getFormatDate("yyyyMMdd");

		// 参数组装
		LinkedHashMap<String, String> paramMap = hfPaymentService.offlineReceive(serviceOrderNo, merOrdDate, inDetails, bid.mer_bid_no, requestMark);
		
		//备注参数（全部以“r_”开头）
		Map<String,String> remarkParams = new LinkedHashMap<String, String>();
		remarkParams.putAll(paramMap);
		remarkParams.put(RemarkPramKey.BILL_ID, bill.id+"");
		remarkParams.put(RemarkPramKey.BILL_INVEST_FEE_LIST, new Gson().toJson(billInvestFeeList));
		remarkParams.put(RemarkPramKey.SERVICE_ORDER_NO, serviceOrderNo);
		
		hfPaymentService.printRequestData(requestMark, bill.user_id, serviceOrderNo, serviceOrderNo,
				ServiceType.OFFLINE_RECEIVE, HfPayType.BATCHREPAYMENT, remarkParams);
		
		//请求第三方
		Map<String, String> respParams = HfUtils.httpPost(paramMap);
		
		hfPaymentService.printCallBackData("线下收款响应", respParams, ServiceType.OFFLINE_RECEIVE, HfPayType.BATCHREPAYMENT);
		
		//签名，状态码，仿重单处理;
		result = hfPaymentService.checkSign(respParams, HfPayType.BATCHREPAYMENT);
		if (result.code < 1) {
			
			return result;
		}
		
		// 线下收款业务逻辑
		result = billService.doOfflineReceive(bill.id, billInvestFeeList, serviceOrderNo);
		if (result.code < 1) {
			
			return result;
		}
		
		result.code = 1;
		result.msg = "线下收款成功";
		
		return result;
	}
	
	@Override
	public ResultInfo conversion(int client, String serviceOrderNo, t_conversion_user conversion, Object... obj) {
		ResultInfo result = new ResultInfo();
		
		t_user_fund userInfo = userFundService.queryUserFundByUserId(conversion.user_id);   
		if (userInfo == null) {
			result.code = -1;
			result.msg = "查询借款人资金信息失败";
			
			return result;
		}
		
		if (StringUtils.isBlank(userInfo.payment_account)) {
			result.code = -1;
			result.msg = "该用户未开通资金托管账户";
			
			return result;
		}
		
		String paymentAccount = userInfo.payment_account;

		//统一兑换申请交易订单号必须不变,防止重复兑换
		String orderNo = OrderNoUtil.getSpecialOrderNo(conversion.time, ServiceType.CONVERSION, OrderNoUtil.SPECIAL_CONVERSION, conversion.id);
		
		//托管请求唯一标识
		String requestMark = UUID.randomUUID().toString();
		
		//接口参数拼装
		Map<String,String> reqParams = hfPaymentService.conversion(orderNo, conversion.amount, paymentAccount, requestMark);
		
		//备注参数（全部以“r_”开头）
		Map<String,String> remarkParams = new LinkedHashMap<String, String>();
		remarkParams.putAll(reqParams);
		remarkParams.put(RemarkPramKey.CONVERSION_ID, conversion.id+"");
		
		hfPaymentService.printRequestData(requestMark, conversion.user_id, serviceOrderNo, orderNo,
				ServiceType.CONVERSION, HfPayType.TRANSFER, remarkParams);
		
		//请求第三方
		Map<String, String> respParams = HfUtils.httpPost(reqParams);
		
		hfPaymentService.printCallBackData("奖励兑换及时响应", respParams, ServiceType.CONVERSION, HfPayType.TRANSFER);

		return hfPaymentCallBackService.conversion(respParams);
	}

	@Override
	public ResultInfo queryBindedBankCard(int client, long userId, Object... obj) {
		ResultInfo result = new ResultInfo();
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		if (userFund == null) {
			result.code = -1;
			result.msg = "获取用户资金信息失败";
			
			return result;
		}
		
		String usrCustId = userFund.payment_account;
		
		// 参数组装
		LinkedHashMap<String, String> reqParams = hfPaymentService.queryBindedBankCard(usrCustId);

		//请求第三方
		Map<String, String> respParams = HfUtils.httpPost(reqParams);
		if (respParams == null) {
			result.code = -1;
			result.msg = "请求超时";
			
			return result;
		}

		hfPaymentService.printCallBackData("查询用户绑定的银行卡响应", respParams, null, HfPayType.QUERYCARDINFO);

		if (!HfConsts.SUCCESS_CODE.equals(respParams.get("RespCode"))) {
			result.code = -1;
			result.msg = "查询用户绑定的银行卡失败";
			
			return result;
		}

		String usrCardInfolist = respParams.get("UsrCardInfolist");
		if (StringUtils.isBlank(usrCardInfolist)) {
			result.code = -1;
			result.msg = "该用户未绑定提现银行卡";
			
			return result;
		}

		List<Map<String, String>> UsrCardInfoList = new Gson().fromJson(usrCardInfolist,
				new TypeToken<List<Map<String, String>>>(){}.getType());
		
		if (UsrCardInfoList == null || UsrCardInfoList.size() == 0) {
			
		}
		
		List<t_bank_card_user> cardList = new ArrayList<t_bank_card_user>();
		
		for (Map<String, String> map : UsrCardInfoList) {
			t_bank_card_user bcu = new t_bank_card_user();
			bcu.id = userId;
			bcu.account = map.get("CardId");
			bcu.bank_code = map.get("BankId");
			bcu.bank_name = HfUtils.getBankName(map.get("BankId"));
			
			cardList.add(bcu);
		}

		result.code = 1;
		result.obj = cardList;
		
		return result;
	}
	
	@Override
	public ResultInfo queryMerchantBalance (int client, Object... obj) {
		ResultInfo result = new ResultInfo();

		// 参数组装
		LinkedHashMap<String, String> reqParams = hfPaymentService.queryAccts();

		//请求第三方
		Map<String, String> respParams = HfUtils.httpPost(reqParams);
		if (respParams == null) {
			result.code = -1;
			result.msg = "请求超时";
			
			return result;
		}

		hfPaymentService.printCallBackData("查询商户账户可用余额响应", respParams, null, HfPayType.QUERYACCTS);

		if (!HfConsts.SUCCESS_CODE.equals(respParams.get("RespCode"))) {
			result.code = -1;
			result.msg = "查询商户子账户信息失败";
			
			return result;
		}
		
		List<Map<String, String>> acctDetails = new Gson().fromJson(respParams.get("AcctDetails"),
				new TypeToken<List<Map<String, String>>>(){}.getType());
		
		/*Map<String, Object> maps = new HashMap<String, Object>();
		for (Map<String, String> acctDetail : acctDetails) {
			if (hf.HfConsts.TRANSFEROUTACCTID.equals(acctDetail.get("SubAcctId"))) {
				maps.put("balance", acctDetail.get("AcctBal").replace(",", ""));
				maps.put("totoal", acctDetail.get("AvlBal").replace(",", ""));
				maps.put("freeze", acctDetail.get("FrzBal").replace(",", ""));
			}
		}*/

		result.code = 1;
		result.obj = acctDetails;
		
		return result;
	}
	
	@Override
	public ResultInfo merchantRecharge (int client, String serviceOrderNo, double rechargeAmt, String type, String bankId, Object... obj) {
		ResultInfo result = new ResultInfo();
		
		//交易订单号
		String orderNo = serviceOrderNo;  
		
		//托管请求唯一标识
		String requestMark = UUID.randomUUID().toString();
		
		//接口参数拼装
		Map<String,String> reqParams = hfPaymentService.merchantRecharge(orderNo, HfConsts.MERCUSTID, rechargeAmt, requestMark, type, bankId);
		
		//生成表单
		String html = HfUtils.createFormHtml(reqParams, HfConsts.CHINAPNR_URL);

		//备注参数（全部以“r_”开头）
		reqParams.put(RemarkPramKey.RECHARGE_AMT, rechargeAmt+"");
		
		hfPaymentService.printRequestData(requestMark, -1, serviceOrderNo, orderNo,
				ServiceType.MERCHANT_RECHARGE, HfPayType.NETSAVE, reqParams);
		
		// 表单提交
		HfPaymentRequestCtrl.getInstance().submitForm(html, client);
		
		return result;
	}
	
	@Override
	public ResultInfo merchantWithdrawal (int client, String serviceOrderNo, double withdrawalAmt, String type, Object... obj) {
		ResultInfo result = new ResultInfo();
		
		//交易订单号
		String orderNo = serviceOrderNo;  
		
		//托管请求唯一标识
		String requestMark = UUID.randomUUID().toString();
		
		//接口参数拼装
		Map<String,String> reqParams = hfPaymentService.merchantWithdrawal(orderNo, HfConsts.MERCUSTID, withdrawalAmt, requestMark);
		
		//生成表单
		String html = HfUtils.createFormHtml(reqParams, HfConsts.CHINAPNR_URL);
		
		//备注参数（全部以“r_”开头）
		reqParams.put(RemarkPramKey.RECHARGE_AMT, withdrawalAmt+"");
		
		hfPaymentService.printRequestData(requestMark, -1, serviceOrderNo, orderNo,
				ServiceType.MERCHANT_WITHDRAWAL, HfPayType.CASH, reqParams);
		
		// 表单提交
		HfPaymentRequestCtrl.getInstance().submitForm(html, client);
		
		return result;
	}
	
	@Override
	public ResultInfo queryFundInfo(int client, String account) {
		ResultInfo result = new ResultInfo();
		
		if(StringUtils.isBlank(account)){
			
			return result;
		}
		
		// 参数组装
		LinkedHashMap<String, String> reqParams = hfPaymentService.queryFundInfo(account);
		
		//请求第三方
		Map<String, String> respParams = HfUtils.httpPost(reqParams);
		if (respParams == null) {
			result.code = -1;
			result.msg = "请求超时";
			
			return result;
		}

		hfPaymentService.printCallBackData("查询用户账户余额响应", respParams, null, HfPayType.QUERYBALANCEBG);
		
		result = hfPaymentService.checkSign(respParams, HfPayType.QUERYBALANCEBG);
		if (result.code < 1) {
			
			return result;
		}
		
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("pBlance", respParams.get("AvlBal").replace(",", ""));  //用户可用余额
		maps.put("pFreeze", respParams.get("FrzBal").replace(",", ""));  //用户冻结金额
	
		result.code = 1;
		result.obj = maps;
		
		return result;
	
	}
	
	@Override
	public String getInterfaceDes(int interfaceType) {
		HfPayType hpt = HfPayType.getEnum(interfaceType);
		
		if(hpt == null){
			return "";
		}
		
		return hpt.value;
	}

	@Override
	public int getInterfaceType(Enum interfaceType) {
		HfPayType hpt = (HfPayType) interfaceType;
	
		if(hpt == null){
			return 0;
		}
		
		return hpt.code;
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public ResultInfo debtTransfer(int client, String serviceOrderNo,Long debtId, Long userId) {
		//数据组装
		ResultInfo result = debtService.findTransferInfo(debtId, userId);
		if (result.code < 1) {

			return result;
		}
		
		Map<String, Object> map = (Map<String, Object>) result.obj;
		t_bid bid = (t_bid) map.get("bid");
		
		t_user_fund sellCustUser = userFundService.queryUserFundByUserId(Long.valueOf(map.get("fromUserId")+""));
		t_user_fund borrowerCustUser = userFundService.queryUserFundByUserId(bid.user_id);
		t_user_fund buyCustUser = userFundService.queryUserFundByUserId(userId);
		
		String sellCustId = sellCustUser.payment_account;//转让者资金托管账号
		double cretAmt2 = Convert.strToDouble(map.get("pCretAmt2").toString(), 0.00);//转让金额（只算本金），债权转让金额不可超过可转让金额（即投资金额）
		double creditDealAmt = Convert.strToDouble(map.get("pPayAmt").toString(), 0.00);//成交金额
		double fee = Convert.strToDouble(map.get("managefee").toString(), 0.00) ;//转让方手续费
		String bidOrdId = String.valueOf(map.get("pCreMerBillNo"));//流水号
		String bidOrdDate = String.valueOf(map.get("orderDate"));
		String borrowerCustId = borrowerCustUser.payment_account;//借款人账号
		double printAmt = Convert.strToDouble(map.get("printAmt").toString(), 0.00);  //已还金额(不包含线下收款和本金垫付)
		String proId = bid.mer_bid_no;  //借款标编号
		
		String bidDetails = "{\"BidDetails\":[{\"BidOrdId\":\""+bidOrdId+"\","
				+ "\"BidOrdDate\":\""+bidOrdDate+"\",\"BidCreditAmt\":\""+HfUtils.formatAmount(cretAmt2)+"\","
				+ "\"BorrowerDetails\":[{\"BorrowerCustId\":\""+borrowerCustId+"\","
						+ "\"BorrowerCreditAmt\":\""+HfUtils.formatAmount(cretAmt2)+"\",\"PrinAmt\":\""+ HfUtils.formatAmount(printAmt) +"\","
								+ "\"ProId\":\""+proId+"\"}]}]}";
		
		String buyCustId = buyCustUser.payment_account;//最高竞价者资金托管账号
		
		//交易订单号
		String orderNo = serviceOrderNo;  
		
		//托管请求唯一标识
		String requestMark = UUID.randomUUID().toString();
		
		//数据拼装 组装参数
		Map<String,String> maps = hfPaymentService.debtTransfer(serviceOrderNo, sellCustId, cretAmt2, creditDealAmt, bidDetails, fee, buyCustId, requestMark);
		
		String html = HfUtils.createFormHtml(maps, HfConsts.CHINAPNR_URL);
		
		//回调所需参数 //备注参数
		maps.put(RemarkPramKey.DEBT_ID, String.valueOf(debtId)); //供回调使用
		maps.put(RemarkPramKey.DEBT_FEE, String.valueOf(fee)); //供回调使用
		maps.put(RemarkPramKey.TO_USER_ID, String.valueOf(userId)); //供回调使用
		maps.put(RemarkPramKey.CLIENT, client+"");

		hfPaymentService.printRequestData(requestMark, userId, serviceOrderNo, orderNo,ServiceType.DEBT_TRANSFER, HfPayType.CREDITASSIGN, maps);
	
		
		result.code = 1;
		result.msg ="已经请求第三方的，等待第三方进行处理";
		//app 接口处理
		if(Client.isAppEnum(client)){
			result.code = 1;
			result.msg = "";
			
			result.obj = html;
			
			return result;
		}
		// 表单提交
		HfPaymentRequestCtrl.getInstance().submitForm(html, client);
		return result;
	}

	
	@Override
	public ResultInfo autoInvestSignature(int client, String serviceOrderNo, long userId, Object... obj) {
		ResultInfo result = new ResultInfo();
		
		if(!HfConsts.TENDERPLANSUPPORT){
			result.code = -1;
			result.msg = "商户尚未与汇付天下签约自动投标功能";

			return result;
		}
		
		t_user_fund userInfo = userFundService.queryUserFundByUserId(userId);   
		if (userInfo == null) {
			result.code = -1;
			result.msg = "查询用户资金信息失败";
			
			return result;
		}
		
		String paymentAccount = userInfo.payment_account;
		
		if(StringUtils.isBlank(paymentAccount)){
			result.code = -2;
			result.msg = "您还未开通托管账户，无法签约";

			return result;
		}
		
		//交易订单号
		String orderNo = "";  
		
		//托管请求唯一标识
		String requestMark = UUID.randomUUID().toString();
		
		//接口参数拼装
		Map<String,String> reqParams = hfPaymentService.autoInvestSignature(paymentAccount, requestMark);
		
		//生成表单
		String html = HfUtils.createFormHtml(reqParams, HfConsts.CHINAPNR_URL);

		//备注参数（全部以“r_”开头）
		reqParams.put(RemarkPramKey.USER_ID, userId+"");
		
		hfPaymentService.printRequestData(requestMark, userId, serviceOrderNo, orderNo,
				ServiceType.AUTO_INVEST_SIGN, HfPayType.AUTOTENDERPLAN, reqParams);
		
		// 表单提交
		HfPaymentRequestCtrl.getInstance().submitForm(html, client);
		
		return result;
	}
	
	@Override
	public ResultInfo autoInvest(int client, int investType, String serviceOrderNo, long userId, t_bid bid, double investAmt, Object... obj) {
		ResultInfo result = new ResultInfo();
		
		t_user_fund investUserInfo = userFundService.queryUserFundByUserId(userId);   
		if (investUserInfo == null) {
			result.code = -1;
			result.msg = "查询理财人资金信息失败";
			
			return result;
		}
		
		String paymentAccount = investUserInfo.payment_account;
		
		t_user_fund loanUserInfo = userFundService.queryUserFundByUserId(bid.user_id);   
		if (loanUserInfo == null) {
			result.code = -1;
			result.msg = "查询借款人资金信息失败";
			
			return result;
		}
		
		//借款人信息
		List<Map<String, String>> borrowerDetails = new LinkedList<Map<String, String>>();		
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("BorrowerCustId", loanUserInfo.payment_account); //借款人第三方唯一标示
		map.put("BorrowerAmt", HfUtils.formatAmount(investAmt)+""); //借款金额
		map.put("BorrowerRate", HfConsts.BORROWERRATE);
		map.put("ProId", bid.mer_bid_no); //标的号即项目号
		borrowerDetails.add(map);	
		
		//交易订单号
		String orderNo = serviceOrderNo;  
		
		//冻结订单号
		String freezeOrdId = OrderNoUtil.getNormalOrderNo(ServiceType.AUTO_INVEST);
		
		//托管请求唯一标识
		String requestMark = UUID.randomUUID().toString();
		
		//接口参数拼装
		Map<String,String> reqParams = hfPaymentService.autoInvest(orderNo, investAmt, paymentAccount, 
				new Gson().toJson(borrowerDetails), freezeOrdId, requestMark);
		

		//备注参数（全部以“r_”开头）
		reqParams.put(RemarkPramKey.USER_ID, userId+"");
		reqParams.put(RemarkPramKey.BID_ID, bid.id+"");
		reqParams.put(RemarkPramKey.INVEST_AMT, investAmt+"");
		reqParams.put(RemarkPramKey.CLIENT, client+"");
		reqParams.put(RemarkPramKey.INVEST_TYPE, investType+"");
		
		hfPaymentService.printRequestData(requestMark, userId, serviceOrderNo, orderNo,
				ServiceType.AUTO_INVEST, HfPayType.AUTOTENDER, reqParams);
		
		//请求第三方
		Map<String, String> respParams = HfUtils.httpPost(reqParams);
		hfPaymentService.printCallBackData("自动投标响应", respParams, ServiceType.AUTO_INVEST, HfPayType.AUTOTENDER);
		
		//业务调用
		Map<String, String> remarkParams = hfPaymentCallBackService.queryRequestParams(requestMark);
		
		result = hfPaymentCallBackService.autoInvest(respParams, remarkParams);
		
		return result;
	}

	@Override
	public ResultInfo queryPersionInformation(int client,
			String serviceOrderNo, long userId, String mobile, String idNumber,
			String bankAcct, Object... obj) {
		
		//交易订单号
		String orderNo = "";  //查询用户信息无需订单号
		
		//托管请求唯一标识
		String requestMark = UUID.randomUUID().toString();
		
		//接口参数拼装
		Map<String,String> reqParams = hfPaymentService.queryPersionInformation(idNumber);
		
		//保存请求日志
		hfPaymentService.printRequestData(requestMark, userId, serviceOrderNo, orderNo, 
				ServiceType.QUERY_PERSION_INFORMATION, HfPayType.QUERYUSRINFO, reqParams);
		
		//同步请求
		Map<String, String> respParams = HfUtils.httpPost(reqParams);
		
		hfPaymentService.printCallBackData("发送短信验证码响应", respParams, ServiceType.QUERY_PERSION_INFORMATION, HfPayType.QUERYUSRINFO);
		
		return hfPaymentCallBackService.queryPersionInformation(respParams, userId);
	}

	@Override
	public ResultInfo fastRecharge(int client, String serviceOrderNo,
			long userId, double rechargeAmt, Object... obj) {
		
		return null;
	}

	@Override
	public ResultInfo changeUserMobile(int client, String serviceOrderNo,
			long userId, String acct, Object... obj) {

		return null;
	}
	
	@Override
	public ResultInfo queryCztx(int client, String serviceOrderNo,
			t_payment_request pr, Object... obj) {
		return null;
	}

	@Override
	public ResultInfo queryTxn(int client, String serviceOrderNo,
			t_payment_request pr, Object... obj) {
		return null;
	}

	@Override
	public ResultInfo merchantTransfer(int client, String serviceOrderNo,
			long userId, Object... obj) {
		ResultInfo result = new ResultInfo();
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		if (userFund == null) {
			result.code = -1;
			result.msg = "查询用户资金信息失败";
			
			return result;
		}
		
		if (StringUtils.isBlank(userFund.payment_account)) {
			result.code = -1;
			result.msg = "该用户未开通资金托管账户";
			
			return result;
		}
		
		t_red_packet_transfer transfer = null;
		if (obj.length >= 1) {
			transfer = (t_red_packet_transfer) obj[0];
		}
		
		//定时任务，临时标记
		int TempState = 0;
		
		if (obj.length >= 2) {
			TempState = (Integer) obj[1];
		}
		
		if (transfer == null) {
			result.code = -1;
			result.msg = "无转账记录";
			
			return result;
		}
		
		if(TempState == 6){
			
			String orderNo = OrderNoUtil.getSpecialOrderNo(transfer.time, ServiceType.TRANSFER, OrderNoUtil.SPECIAL_TRANSFER, transfer.id);
			
			t_payment_request pr = hfPaymentService.queryRequestByOrderNo(orderNo);
			if (pr != null) {
				
				serviceOrderNo = pr.service_order_no;
			}
			//修改交易订单号
			transfer.time = DateUtil.minusDay(transfer.time, 1);
		}
		
		String paymentAccount = userFund.payment_account;
		//转账交易订单号必须不变,防止重复转账
		String orderNo = OrderNoUtil.getSpecialOrderNo(transfer.time, ServiceType.TRANSFER, OrderNoUtil.SPECIAL_TRANSFER, transfer.id);
		//托管请求唯一标识
		String requestMark = UUID.randomUUID().toString();
		
		//接口参数拼装
		Map<String,String> reqParams = hfPaymentService.merchantTransfer(orderNo, transfer.amount, paymentAccount, requestMark);
		
		//备注参数（全部以“r_”开头）
		Map<String,String> remarkParams = new LinkedHashMap<String, String>();
		remarkParams.putAll(reqParams);
		remarkParams.put(RemarkPramKey.RED_PACKET_TRANSFER_ID, transfer.id + ""); //红包转账记录ID
		remarkParams.put(RemarkPramKey.ORDER_NO, orderNo); //交易订单号
		remarkParams.put("r_tempState", TempState+""); //定时任务，临时标记
		
		hfPaymentService.printRequestData(requestMark, transfer.user_id, serviceOrderNo, orderNo,
				ServiceType.TRANSFER, HfPayType.TRANSFER, remarkParams);
		
		//请求第三方
		Map<String, String> respParams = HfUtils.httpPost(reqParams);
		
		hfPaymentService.printCallBackData("商户转账及时响应", respParams, ServiceType.TRANSFER, HfPayType.TRANSFER);
		
		return hfPaymentCallBackService.merchantTransfer(respParams);
	}
	
	@Override
	public ResultInfo tenderCancle(int client, String serviceOrderNo, t_invest invest, Object... obj) {
		ResultInfo result = new ResultInfo();
		
		String orderNo = OrderNoUtil.getNormalOrderNo(ServiceType.TENDER_CANCLE);
		
		//托管请求唯一标识
		String requestMark = UUID.randomUUID().toString();
		
		t_user_fund userInfo = userFundService.queryUserFundByUserId(invest.user_id);
		if (userInfo == null) {
			result.code = -1;
			result.msg = "查询借款人资金信息失败";
			
			return result;
		}

		//接口参数拼装
		Map<String,String> reqParams = hfPaymentService.tenderCancleBid(invest.service_order_no, invest.trust_order_no, orderNo, invest.amount, 
				userInfo.payment_account, requestMark);
		//生成表单
		String html = HfUtils.createFormHtml(reqParams, HfConsts.CHINAPNR_URL);
		//备注参数（全部以“r_”开头）
		reqParams.put(RemarkPramKey.INVEST_TYPE, invest.id + "");
		hfPaymentService.printRequestData(requestMark, invest.id, serviceOrderNo, invest.service_order_no, ServiceType.TENDER_CANCLE, 
				HfPayType.TENDERCANCLE, reqParams);
		
		// 表单提交
		HfPaymentRequestCtrl.getInstance().submitForm(html, client);
		
		return null;
	}

	@Override
	public ResultInfo singleRepayment(int client, String serviceOrderNo, t_bill bill, List<Map<String, Double>> billInvestFeeList, Object... obj) {
		ResultInfo result = new ResultInfo();
		
		if (bill == null) {
			throw new RuntimeException("bill is null");
		}
		
		t_bid bid = bidService.findByID(bill.bid_id);
		if (bid == null) {
			result.code = -1;
			result.msg = "查询借款标信息失败";
			
			return result;
		}
		
		t_user_fund loanUserFund = userFundService.queryUserFundByUserId(bill.user_id);
		if (loanUserFund == null) {
			result.code = -1;
			result.msg = "查询借款人资金信息失败";
			
			return result;
		}

		//托管请求唯一标识
		String requestMark = UUID.randomUUID().toString();
		
		List<t_bill_invest> billInvestList = billInvestService.queryNOReceiveInvestBills(bill.bid_id, bill.period);
		if (billInvestList == null || billInvestList.size() <= 0) {
			result.code = -1;
			result.msg = "查询待还的理财账单失败";
			
			return result;
		}

		int successCount = 0;
		int listSize = billInvestList.size();
		
		for (int i = 0; i < listSize; i++) {
			t_bill_invest billInvest = billInvestList.get(i);
			Map<String, Double> billInvestFee = billInvestFeeList.get(i);
			
			//投资人应收本金
			double receiveCorpus = billInvest.receive_corpus;
			//投资人应收利息
			double receiveInterest = billInvest.receive_interest;
			//投资人应收逾期罚息
			double overdueFine = billInvestFee.get("overdueFine");
			//投资人收益
			double pInAmt = billInvest.receive_interest + billInvest.receive_corpus + billInvestFee.get("overdueFine");
			//投资管理费
			double pInFee = billInvestFee.get("investServiceFee");
			
			//投资人信息
			t_user_fund investUserFund = userFundService.queryUserFundByUserId(billInvest.user_id);
			if (investUserFund == null) {
				result.code = -1;
				result.msg = "查询理财人信息失败";
				
				return result;
			}
			
			t_invest invest = investService.findByID(billInvest.invest_id);
			if (invest == null) {
				result.code = -1;
				result.msg = "查询投资信息失败";
				
				return result;
			}
			
			/** 特殊订单号：同一笔还款，订单号一致 */
			String orderNo = OrderNoUtil.getSpecialOrderNo(billInvest.time, ServiceType.REPAYMENT, OrderNoUtil.SPECIAL_BILL_INVEST, billInvest.id);
			
			JSONObject investUserJson = new JSONObject();
			investUserJson.put("InCustId", investUserFund.payment_account);
			investUserJson.put("OrdId", orderNo);
			investUserJson.put("SubOrdId", invest.service_order_no);
			investUserJson.put("TransAmt", HfUtils.formatAmount(pInAmt));
				
			String divDetails = "";
			if (pInFee != 0) {
				JSONArray divDetailAarray = new JSONArray();
				JSONObject divDetailsJson = new JSONObject();
				divDetailsJson.put("DivCustId", HfConsts.MERCUSTID);
				divDetailsJson.put("DivAcctId", HfConsts.SERVFEEACCTID);
				divDetailsJson.put("DivAmt", HfUtils.formatAmount(pInFee));
				divDetailAarray.add(divDetailsJson);
				divDetails = divDetailAarray.toString();
			}
			
			LinkedHashMap<String, String> reqParams = hfPaymentService.repayment_3(bid.mer_bid_no, orderNo, serviceOrderNo, invest.time, 
					loanUserFund.payment_account, receiveCorpus, receiveInterest + overdueFine, pInFee, 
					investUserFund.payment_account, divDetails, requestMark, null);
			
			hfPaymentService.printRequestData(requestMark, billInvest.user_id, serviceOrderNo, orderNo,
					ServiceType.REPAYMENT, HfPayType.BATCHREPAYMENT_, reqParams);
			
			//请求第三方
			Map<String, String> respParams = HfUtils.httpPost(reqParams);
			hfPaymentService.printCallBackData("正常还款响应", respParams, ServiceType.REPAYMENT, HfPayType.BATCHREPAYMENT_);
			
			//签名，状态码，仿重单处理;
			result = hfPaymentService.checkSign(respParams, HfPayType.BATCHREPAYMENT_);
			if (result.code > 0) {
				successCount++;
			}
		}
		
		if (successCount != listSize) {
			result.code = -1;
			result.msg = "还款失败";
			
			return result;
		}
		
		//本地业务执行
		
		result.code = 1;
		result.msg = "还款成功";
		
		return result;
	}

	@Override
	public ResultInfo sendRate(int client, String serviceOrderNo,
			t_bill_invest billInvest, Object... obj) {
		ResultInfo result = new ResultInfo();
		
		if (billInvest == null) {
			result.code = -1;
			result.msg = "不存在理财记录";
			
			return result;
		}
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(billInvest.user_id);
		if (userFund == null) {
			result.code = -1;
			result.msg = "查询用户资金信息失败";
			
			return result;
		}
		
		if (StringUtils.isBlank(userFund.payment_account)) {
			result.code = -1;
			result.msg = "该用户未开通资金托管账户";
			
			return result;
		}
		
		//转账总额（标的奖励+加息卷金额）
		double realAddAmount = Arith.add(billInvest.add_invest, billInvest.reward_invest);
		
		String paymentAccount = userFund.payment_account;
		//转账交易订单号必须不变,防止重复转账
		String orderNo = serviceOrderNo;
		//托管请求唯一标识
		String requestMark = UUID.randomUUID().toString();
		
		//接口参数拼装
		Map<String,String> reqParams = hfPaymentService.sendRate(orderNo, realAddAmount, paymentAccount, requestMark);
		
		//备注参数（全部以“r_”开头）
		Map<String,String> remarkParams = new LinkedHashMap<String, String>();
		remarkParams.putAll(reqParams);
		remarkParams.put(RemarkPramKey.BILL_INVEST_ID, billInvest.id + ""); //理财账单记录ID
		remarkParams.put(RemarkPramKey.ORDER_NO, orderNo); //交易订单号
		
		hfPaymentService.printRequestData(requestMark, billInvest.user_id, serviceOrderNo, orderNo,
				ServiceType.TRANSFER, HfPayType.TRANSFER, remarkParams);
		
		//请求第三方
		Map<String, String> respParams = HfUtils.httpPost(reqParams);
		
		hfPaymentService.printCallBackData("商户转账及时响应", respParams, ServiceType.TRANSFER, HfPayType.TRANSFER);
		
		return hfPaymentCallBackService.sendRate(respParams);
	}

	@Override
	public ResultInfo sendSmsCode(int client, String serviceOrderNo, long userId, String cardId, BusiType busiType, String mobile,
			Object... obj) {
		
		ResultInfo result = new ResultInfo();
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);   
		if (userFund == null) {
			result.code = -1;
			result.msg = "查询用户信息失败";
			
			return result;
		}
		
		//交易订单号
		String orderNo = "";  //开户无需订单号
		
		//托管请求唯一标识
		String requestMark = UUID.randomUUID().toString();
		
		//接口参数拼装
		Map<String,String> reqParams = hfPaymentService.sendSmsCode(userFund.payment_account, busiType, cardId, mobile);

		//保存请求日志
		hfPaymentService.printRequestData(requestMark, userId, serviceOrderNo, orderNo, 
				ServiceType.SENDSMSCODE, HfPayType.SENDSMSCODE, reqParams);
		
		//同步请求
		Map<String, String> respParams = HfUtils.httpPost(reqParams);
		
		hfPaymentService.printCallBackData("发送短信验证码响应", respParams, ServiceType.SENDSMSCODE, HfPayType.SENDSMSCODE);
		
		return hfPaymentCallBackService.sendSmsCode(respParams);
		
	}

	@Override
	public ResultInfo userRegist(int client, String serviceOrderNo, long userId, String hfName, String realName,
			String idNumber, String mobile, String cardId, String bankId, String provId, String areaId, String smsCode,
			String smsSeq, Object... obj) {
		ResultInfo result = new ResultInfo();
		
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);   
		if (userInfo == null) {
			result.code = -1;
			result.msg = "查询用户信息失败";
			
			return result;
		}
		
		//交易订单号
		String orderNo = "";  //开户无需订单号
		
		//托管请求唯一标识
		String requestMark = UUID.randomUUID().toString();
		
		//接口参数拼装
		Map<String,String> reqParams = hfPaymentService.userRegisterByBos(hfName, realName, idNumber, mobile, 
				cardId, bankId, provId, areaId, smsCode, smsSeq, requestMark);
		
		//生成表单
		String html = HfUtils.createFormHtml(reqParams, HfConsts.CHINAPNR_URL);

		//备注参数（全部以“r_”开头）
		reqParams.put(RemarkPramKey.USER_ID, userId+"");
		reqParams.put(RemarkPramKey.CLIENT, client+"");
		reqParams.put(RemarkPramKey.CARD_ID, cardId);
		reqParams.put(RemarkPramKey.BANK_ID, bankId);
		reqParams.put(RemarkPramKey.PROV_ID, provId);
		reqParams.put(RemarkPramKey.AREA_ID, areaId);
		
		hfPaymentService.printRequestData(requestMark, userId, serviceOrderNo, orderNo, 
				ServiceType.REGIST, HfPayType.USERREGISTER, reqParams);
	    //app 接口处理
		if(Client.isAppEnum(client)){
			result.code = 1;
			result.msg = "";
			
			result.obj = html;
			return result;
		}
		// 表单提交
		HfPaymentRequestCtrl.getInstance().submitForm(html, client);
		
		return result;
	}

	@Override
	public ResultInfo bosAcctActivate(int client, String serviceOrderNo, long userId,
		    Object... obj) {
		ResultInfo result = new ResultInfo();
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);   
		if (userFund == null) {
			result.code = -1;
			result.msg = "查询用户信息失败";
			return result;
		}
		
		if(userFund.is_actived) {
			result.code = -1;
			result.msg = "账户已激活";
			return result;
		}
		
		//交易订单号
		String orderNo = serviceOrderNo;  //账户激活需订单号
				
		//托管请求唯一标识
		String requestMark = UUID.randomUUID().toString();
				
		//接口参数拼装
		Map<String,String> reqParams = hfPaymentService.bosAcctActivate(userFund.payment_account, orderNo);
				
		//生成表单
		String html = HfUtils.createFormHtml(reqParams, HfConsts.CHINAPNR_URL);
		
		//备注参数（全部以“r_”开头）
		reqParams.put(RemarkPramKey.CLIENT, client+"");
		
		hfPaymentService.printRequestData(requestMark, userId, serviceOrderNo, orderNo, 
				ServiceType.BOSACCTACTIVATE, HfPayType.BOSACCTACTIVATE, reqParams);
		
	    //app 接口处理
		if(Client.isAppEnum(client)){
			result.code = 1;
			result.msg = "激活请求处理中";
			
			result.obj = html;
			return result;
		}
		// 表单提交
		HfPaymentRequestCtrl.getInstance().submitForm(html, client);
		
		return result;
		
	}

	@Override
	public ResultInfo quickBinding(int client, String serviceOrderNo, long userId, String cardId,
			String bankId, String provId, String areaId, String mobile, String smsCode, String smsSeq, String orgSmsExt, Object... obj) {
		
		ResultInfo result = new ResultInfo();
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);   
		if (userFund == null) {
			result.code = -1;
			result.msg = "查询用户信息失败";
			return result;
		}
		
		if(!userFund.is_actived) {
			result.code = -1;
			result.msg = "账户未激活";
			return result;
		}
		
		//交易订单号
		String orderNo = serviceOrderNo;  //换绑卡无需订单
				
		//托管请求唯一标识
		String requestMark = UUID.randomUUID().toString();
		
		Map<String,String> reqParams = hfPaymentService.quickBinding(orderNo, userFund.payment_account, 
				cardId, bankId, provId, areaId, smsCode, smsSeq, orgSmsExt, mobile, requestMark);
		
		//备注参数（全部以“r_”开头）
		reqParams.put(RemarkPramKey.CLIENT, client+"");
		
		//保存请求日志
		hfPaymentService.printRequestData(requestMark, userId, serviceOrderNo, orderNo, 
				ServiceType.QUICKBINDING, HfPayType.QUICKBINDING, reqParams);
		
		//同步请求
		Map<String, String> respParams = HfUtils.httpPost(reqParams);
		
		hfPaymentService.printCallBackData("发送短信验证码响应", respParams, ServiceType.QUICKBINDING, HfPayType.QUICKBINDING);
		
		return hfPaymentCallBackService.quickBinding(respParams, reqParams);
	}

	@Override
	public ResultInfo directRecharge(int client, String serviceOrderNo, long userId, TradeType tradeType, String bankId,
			double transAmt, String smsCode, String smsSeq, String singId) {
		
		ResultInfo result = new ResultInfo();
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);   
		if (userFund == null) {
			result.code = -1;
			result.msg = "查询用户信息失败";
			return result;
		}
		
		if(!userFund.is_actived) {
			result.code = -1;
			result.msg = "账户未激活";
			return result;
		}
		
		//交易订单号
		String orderNo = serviceOrderNo;  //充值需要订单
				
		//托管请求唯一标识
		String requestMark = UUID.randomUUID().toString();
		
		//接口参数拼装
		Map<String,String> reqParams = hfPaymentService.directRecharge(userFund.payment_account, orderNo, tradeType, bankId, transAmt, smsSeq, smsCode);

		reqParams.put(RemarkPramKey.CLIENT, client+"");
		
		hfPaymentService.printRequestData(requestMark, userId, serviceOrderNo, orderNo, 
				ServiceType.RECHARGE, HfPayType.DIRECTRECHARGE,  reqParams);
		
		String html = HfUtils.createFormHtml(reqParams, HfConsts.CHINAPNR_URL);
		 //app 接口处理
		if(Client.isAppEnum(client)){
			result.code = 1;
			result.msg = "";
			
			result.obj = html;
			return result;
		}
		// 表单提交
		HfPaymentRequestCtrl.getInstance().submitForm(html, client);
		
		return result;
				
	}

	@Override
	public ResultInfo transfer(int client, String serviceOrderNo, long userId, double transAmt,
			Object... obj) {
		
		ResultInfo result = new ResultInfo();
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		if (userFund == null) {
			result.code = -1;
			result.msg = "查询用户资金信息失败";
			
			return result;
		}
		
		if (StringUtils.isBlank(userFund.payment_account)) {
			result.code = -1;
			result.msg = "该用户未开通资金托管账户";
			
			return result;
		}
		
		//转账交易订单号必须不变,防止重复转账
		String orderNo = serviceOrderNo;
		
		//托管请求唯一标识
		String requestMark = UUID.randomUUID().toString();
		
		//接口参数拼装
		Map<String,String> reqParams = hfPaymentService.transfer(orderNo, transAmt, userFund.payment_account, requestMark);
		
		hfPaymentService.printRequestData(requestMark, userId, serviceOrderNo, orderNo,
				ServiceType.TRANSFER, HfPayType.TRANSFER, reqParams);
		
		//请求第三方
		Map<String, String> respParams = HfUtils.httpPost(reqParams);
		
		hfPaymentService.printCallBackData("商户转账及时响应", respParams, ServiceType.TRANSFER, HfPayType.TRANSFER);
		
		return hfPaymentCallBackService.transfer(respParams);
	}

	@Override
	public ResultInfo queryTransStat(int client, String serviceOrderNo, String queryTransType) {
		ResultInfo result = new ResultInfo();
		
		if(!queryTransType.equals("LOANS") && !queryTransType.equals("REPAYMENT") &&
				!queryTransType.equals("TENDER") && !queryTransType.equals("CASH") &&
				!queryTransType.equals("FREEZE")) {
			result.code = -1;
			result.msg = "交易查询类型错误";
			return result;
		}
		
		//接口参数拼装
		Map<String,String> reqParams = hfPaymentService.queryTransStat(serviceOrderNo, queryTransType);
		
		// 不保存请求
		HfUtils.httpPost(reqParams);
		
		result.code = 1;
		result.msg = "success";
		
		return result;
	}

	@Override
	public ResultInfo trfReconciliation(int client, Date beginDate, Date endDate, String pageNum, String pageSize) {
		ResultInfo result = new ResultInfo();
		
		//接口参数拼装
		Map<String,String> reqParams = hfPaymentService.trfReconciliation(beginDate, endDate, pageNum, pageSize);
		
		// 不保存请求
		HfUtils.httpPost(reqParams);
		
		result.code = 1;
		result.msg = "success";
		
		return result;
	}

	@Override
	public ResultInfo reconciliation(int client, Date beginDate, Date endDate, String pageNum, String pageSize,
			String queryTransType) {
		ResultInfo result = new ResultInfo();
		
		if(!queryTransType.equals("LOANS") && !queryTransType.equals("REPAYMENT")) {
			result.code = -1;
			result.msg = "交易查询类型错误";
			return result;
		}
		
		//接口参数拼装
		Map<String,String> reqParams = hfPaymentService.reconciliation(beginDate, endDate, pageNum, pageSize, queryTransType);
		
		// 不保存请求
		HfUtils.httpPost(reqParams);
		
		result.code = 1;
		result.msg = "success";
		
		return result;
	}

	@Override
	public ResultInfo cashReconciliation(int client, Date beginDate, Date endDate, String pageNum, String pageSize) {
		ResultInfo result = new ResultInfo();
		
		//接口参数拼装
		Map<String,String> reqParams = hfPaymentService.cashReconciliation(beginDate, endDate, pageNum, pageSize);
		
		for(String key : reqParams.keySet()) {
			System.out.println(key + "=" + reqParams.get(key));
		}
		
		// 不保存请求
		HfUtils.httpPost(reqParams);
		
		result.code = 1;
		result.msg = "success";
		
		return result;
	}

	@Override
	public ResultInfo saveReconciliation(int client, Date beginDate, Date endDate, String pageNum, String pageSize) {
		ResultInfo result = new ResultInfo();
		
		//接口参数拼装
		Map<String,String> reqParams = hfPaymentService.saveReconciliation(beginDate, endDate, pageNum, pageSize);
		
		// 不保存请求
		HfUtils.httpPost(reqParams);
		
		result.code = 1;
		result.msg = "success";
		
		return result;
	}

	@Override
	public ResultInfo autoTenderCancle(int client, String serviceOrderNo, long userId, double transAmt,
			Map<String, String> unFreezeParam, long investId) {
		
		ResultInfo result = new ResultInfo();
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);   
		if (userFund == null) {
			result.code = -1;
			result.msg = "查询用户信息失败";
			return result;
		}
		
		if(!userFund.is_actived) {
			result.code = -1;
			result.msg = "账户未激活";
			return result;
		}
		
		//交易订单号
		String orderNo = serviceOrderNo;  //充值需要订单
				
		//托管请求唯一标识
		String requestMark = UUID.randomUUID().toString();
		
		//接口参数拼装
		Map<String,String> reqParams = hfPaymentService.autoTenderCancle(userFund.payment_account, orderNo, transAmt, requestMark, unFreezeParam);

		reqParams.put(RemarkPramKey.CLIENT, client+"");
		reqParams.put(RemarkPramKey.INVEST_ID, "" + investId);
		
		//保存请求日志
		hfPaymentService.printRequestData(requestMark, userId, serviceOrderNo, orderNo, 
				ServiceType.AUTOTENDERCANCLE, HfPayType.AUTOTENDERCANCLE, reqParams);
		
		//同步请求
		Map<String, String> respParams = HfUtils.httpPost(reqParams);
		
		//保存回调接口
		hfPaymentService.printCallBackData("后台投标撤销", respParams, ServiceType.AUTOTENDERCANCLE, HfPayType.AUTOTENDERCANCLE);
		
		return hfPaymentCallBackService.autoTenderCancle(respParams, reqParams);
	}

	@Override
	public ResultInfo corpRegister(int client, String serviceOrderNo, long userId, String usrId, String usrName,
			String instuCode, String busiCode, String taxCode, String guarType,	Double guarCorpEarnestAmt) {
		
		ResultInfo result = new ResultInfo();
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);   
		if (userFund == null) {
			result.code = -1;
			result.msg = "查询用户信息失败";
			return result;
		}
		
		if(StringUtils.isNotBlank(userFund.payment_account)) {
			result.code = -1;
			result.msg = "已开过户";
			return result;
		}
		
		t_corp_info corpInfo = corpInfoService.queryByUserId(userId);
		
		if(corpInfo != null) {
			if(corpInfo.getStatus().code == Status.AUDITING.code || corpInfo.getStatus().code == Status.RPOCESS.code) {
				result.code = -1;
				result.msg = "开户审核中或开户中";
				return result;
			}
		}
		
		//托管请求唯一标识
		String merPriv = UUID.randomUUID().toString();
				
		//接口参数拼装
		Map<String,String> reqParams = hfPaymentService.doCorpRegister(usrId, usrName, instuCode, busiCode, taxCode, merPriv, guarType, guarCorpEarnestAmt);
				
		//生成表单
		String html = HfUtils.createFormHtml(reqParams, HfConsts.CHINAPNR_URL);

		//备注参数（全部以“r_”开头）
		reqParams.put(RemarkPramKey.USER_ID, userId+"");
		reqParams.put(RemarkPramKey.CLIENT, client+"");
		reqParams.put(RemarkPramKey.INSTU_CODE, instuCode);
		reqParams.put(RemarkPramKey.BUSI_CODE, busiCode);
		reqParams.put(RemarkPramKey.TAX_CODE, taxCode);
		reqParams.put(RemarkPramKey.GUAR_TYPE, guarType);
		reqParams.put(RemarkPramKey.GUAR_CORP_EARNEST_AMT, guarCorpEarnestAmt + "");
			
		boolean guarTypeFlag = "Y".equals(guarType);
		
		// 保存企业开户信息
		t_corp_info newCorpInfo = new t_corp_info(userId, instuCode, busiCode, taxCode, guarTypeFlag, guarCorpEarnestAmt, Status.INIT.code);
		
		if(corpInfo != null) {
			newCorpInfo.id = corpInfo.id;
		}
		
		result = corpInfoService.saveOrUpdate(newCorpInfo);
		
		if(result.code != 1) {
			return result;
		}
		
		userFund.is_corp = true;
		
		if(!userFundService.update(userFund)) {
			result.code = -1;
			result.msg = "更新用户信息失败";
			return result;
		}
		
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		
		userInfo.reality_name = usrName;
		
		userInfoService.update(userInfo);
		
		userService.flushUserCache(userId);
		
		hfPaymentService.printRequestData(merPriv, userId, serviceOrderNo, null, 
				ServiceType.CORPREGISTER, HfPayType.CORPREGISTER, reqParams);
		
	    //app 接口处理
		if(Client.isAppEnum(client)){
			result.code = 1;
			result.msg = "";
			result.obj = html;
			return result;
		}
		// 表单提交
		HfPaymentRequestCtrl.getInstance().submitForm(html, client);
		
		return result;
	}

	@Override
	public ResultInfo corpRegisterQuery(int client, long userId, String busiCode) {
		
		ResultInfo result = new ResultInfo();
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);   
		if (userFund == null) {
			result.code = -1;
			result.msg = "查询用户信息失败";
			return result;
		}
		
		if(!userFund.is_corp) {
			result.code = -1;
			result.msg = "未进行过企业开户";
			return result;
		}
		
		t_corp_info corpInfo = corpInfoService.queryByUserId(userId);
		
		if(corpInfo == null) {
			result.code = -1;
			result.msg = "查询企业开户信息失败";
			return result;
		}
		
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);

		if(userInfo == null) {
			result.code = -1;
			result.msg = "查询用户信息失败";
			return result;
		}
		
		//接口参数拼装
		Map<String,String> reqParams = hfPaymentService.doCorpRegisterQuery(busiCode);
		
		Map<String,String> respParams = HfUtils.httpPost(reqParams);
		
		result = hfPaymentCallBackService.corpRegisterQuery(respParams);

		if(result.code != 1) {
			return result;
		}
		
		String usrCustId = respParams.get("UsrCustId");
		String usrId = respParams.get("UsrId");
		String auditStat = respParams.get("AuditStat");
		busiCode = respParams.get("BusiCode");
				
		corpInfo.busi_code = busiCode;
		corpInfo.setStatus(Status.getEnumByValue(auditStat));
		
		result = corpInfoService.saveOrUpdate(corpInfo);
		
		if(result.code != 1) {
			return result;
		}
		
		userFund.payment_account = usrCustId;
		
		if(Status.SUCCESS.value.equals(auditStat)) {
			userFund.is_actived = true;
		}
		
		if(!userFundService.update(userFund)) {
			result.code = -1;
			result.msg = "更新用户信息失败";
			return result;
		}
		
		userInfo.hf_name = usrId;
		
		if(!userInfoService.update(userInfo)) {
			result.code = -1;
			result.msg = "更新用户信息失败";
		} else {
			result.code = 1;
			result.msg = "查询企业开户状态成功";
			result.obj = Status.getEnumByValue(auditStat).name;
		}
		
		userService.flushUserCache(userId);
		
		return result;
		
	}

	@Override
	public ResultInfo usrUnFreeze(int client, long userId, String ordNo, String freezeTrxId) {
		ResultInfo result = new ResultInfo();
		
		String orderNo = OrderNoUtil.getNormalOrderNo(ServiceType.INVEST);  //全新流水号
		
		//托管请求唯一标识
		String requestMark = UUID.randomUUID().toString();

		//接口参数拼装
		Map<String,String> reqParams = hfPaymentService.userUnFreeze(orderNo, freezeTrxId, requestMark);
				
		hfPaymentService.printRequestData(requestMark, userId, ordNo, orderNo,
				ServiceType.INVEST, HfPayType.USRUNFREEZE, reqParams);
		
		//请求第三方
		Map<String, String> respParams = HfUtils.httpPost(reqParams);
		
		hfPaymentService.printCallBackData("解冻响应", respParams, ServiceType.INVEST, HfPayType.USRUNFREEZE);
		
		result = hfPaymentService.checkSign(respParams, HfPayType.USRUNFREEZE);
		if (result.code < 1) {
			
			return result;
		}
		
		result.code = 1;
		result.msg = "解冻成功";
		
		return result;
	}
	
}