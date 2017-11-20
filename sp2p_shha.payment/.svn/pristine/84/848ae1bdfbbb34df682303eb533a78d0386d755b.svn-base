package fy;


import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.XML;

import com.google.gson.Gson;
import com.shove.Convert;

import common.constants.RemarkPramKey;
import common.enums.BusiType;
import common.enums.Client;
import common.enums.ServiceType;
import common.enums.TradeType;
import common.utils.Factory;
import common.utils.HttpUtil;
import common.utils.OrderNoUtil;
import common.utils.ResultInfo;
import controllers.common.BaseController;
import controllers.payment.fy.FyPaymentCallBackCtrl;
import controllers.payment.fy.FyPaymentRequestCtrl;
import models.common.entity.t_conversion_user;
import models.common.entity.t_user_fund;
import models.core.entity.t_bid;
import models.core.entity.t_bill;
import models.core.entity.t_bill_invest;
import models.core.entity.t_invest;
import models.entity.t_payment_request;
import payment.IPayment;
import play.Logger;
import services.common.UserFundService;
import services.core.BidService;
import services.core.BillInvestService;
import services.core.BillService;
import services.core.DebtService;
import services.core.InvestService;

public class FyPayment implements IPayment{
	
	private static FyPaymentService fyPaymentService = Factory.getSimpleService(FyPaymentService.class);
	
	private static UserFundService userFundService = Factory.getService(UserFundService.class);
	
	private static InvestService investService = Factory.getService(InvestService.class);
	
	private static BidService bidService = Factory.getService(BidService.class);
	
	private static BillInvestService billInvestService = Factory.getService(BillInvestService.class);
	
	private static BillService billService = Factory.getService(BillService.class);
	
	private static DebtService debtService = Factory.getService(DebtService.class);
	
	private static FyPaymentCallBackService fyPaymentCallBackService = Factory.getSimpleService(FyPaymentCallBackService.class);

	@Override
	public ResultInfo regist(int client, String serviceOrderNo, long userId,
			Object... obj) {
		ResultInfo result = new ResultInfo();
		
		//交易订单号
		String orderNo = serviceOrderNo;
		
		//拼装参数
		Map<String,String> reqParams = fyPaymentService.userRegister((String)obj[0], (String)obj[1], (String)obj[2], (String)obj[3], 
				(String)obj[4], (String)obj[5], (String)obj[6], (String)obj[7], "", orderNo);
		
		//用于入库的map
		LinkedHashMap<String, String> inpParams = new LinkedHashMap<String, String>();
		inpParams.putAll(reqParams);
		inpParams.put(RemarkPramKey.USER_ID, userId + "");
		inpParams.put(RemarkPramKey.USER_MOBILE, (String)obj[2]);
		inpParams.put("r_bank_type", (String)obj[5]);
		inpParams.put("r_bank_nm", (String)obj[6]);
		inpParams.put("r_bank_acct", (String)obj[7]);
		
		//打印提交日志
		fyPaymentService.printRequestData("主动投标", userId, serviceOrderNo, orderNo, ServiceType.REGIST, FyPayType.USERREGISTER, 
				inpParams, BaseController.getBaseURL() + FyConsts.userRegisterRepair);
		
		//post表单提交
		String xmlData = HttpUtil.postMethod(FyConsts.post_url + FyConsts.register, reqParams, "UTF-8");
		
		//将xml转换为map
		Map<String, String> cbParams = FyUtils.parseXmlToMap(xmlData);
		
		//打印回调日志
		fyPaymentService.printCallBackData("用户开户", cbParams, ServiceType.REGIST, FyPayType.USERREGISTER);
		
		//校验签名
		result = FyUtils.checkSign(cbParams, "用户开户", FyPayType.USERREGISTER.value, FyPayType.USERREGISTER);
		
		if (result.code < 1 && result.code != ResultInfo.ALREADY_RUN) {
			
			return result;
		}
		
		result = fyPaymentCallBackService.userRegister(cbParams);
		
		//app 接口处理
		if(Client.isAppEnum(client)){
			
			result.obj = "";
			return result;
		}
		
		return result;
	}

	@Override
	public ResultInfo recharge(int client, String serviceOrderNo, long userId,
			double rechargeAmt, Object... obj) {
		ResultInfo result = new ResultInfo();
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		if (userFund == null) {
			result.code = -1;
			result.msg = "查询用户信息失败";
			
			return result;
		}
		
		//交易订单号
		String orderNo = serviceOrderNo;
		
		//拼装参数
		Map<String, String> reqParams = new LinkedHashMap<String, String>();
		//构建发送给第三方的表单
		String html = "";
		
		reqParams = fyPaymentService.recharge(userFund.payment_account, rechargeAmt, orderNo);
		
		if (client == Client.ANDROID.code || client == Client.IOS.code || client == Client.WECHAT.code) {
			//reqParams = fyPaymentService.fastRecharge(userFund.payment_account, rechargeAmt, orderNo);
			html = FyUtils.createHtml(reqParams, FyConsts.post_url + FyConsts.appRecharge);
		} else {
			html = FyUtils.createHtml(reqParams, FyConsts.post_url + FyConsts.recharge);
		}
		
		//备注参数（全部以"r_"开头）
		reqParams.put(RemarkPramKey.USER_ID, userId + "");
		reqParams.put(RemarkPramKey.RECHARGE_AMT, rechargeAmt + "");
		reqParams.put(RemarkPramKey.CLIENT, client + "");
		
		/*if (client == Client.ANDROID.code || client == Client.IOS.code || client == Client.WECHAT.code) {
			//打印提交日志
			fyPaymentService.printRequestData("", userId, serviceOrderNo, orderNo, ServiceType.RECHARGE, FyPayType.FASTSAVE, 
					reqParams, BaseController.getBaseURL() + FyConsts.fastRechargeRepair);
		} else {*/
			//打印提交日志
			fyPaymentService.printRequestData("", userId, serviceOrderNo, orderNo, ServiceType.RECHARGE, FyPayType.NETSAVE, 
					reqParams, BaseController.getBaseURL() + FyConsts.rechargeRepair);
		//}
		
		//如果是app端发起的请求则将构建的表单交由app提交
		if (Client.isAppEnum(client)) {
			result.code = 1;
			result.msg = "";
			result.obj = html;
			
			return result;
		}
		
		//提交表单
		FyPaymentRequestCtrl.getInstance().submitForm(html, client);
		
		return result;
	}

	@Override
	public ResultInfo userBindCard(int client, String serviceOrderNo,
			long userId, Object... obj) {
		//富友无该接口故空实现，在用户开通资金托管账号时就已完成绑卡
		return null;
	}

	@Override
	public ResultInfo withdrawal(int client, String serviceOrderNo,
			long userId, double withdrawalAmt, String bankAccount, String cashType,
			Object... obj) {
		ResultInfo result = new ResultInfo();
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		if (userFund == null) {
			result.code = -1;
			result.msg = "查询用户信息失败";
			
			return result;
		}
		
		//交易订单号
		String orderNo = serviceOrderNo;
		
		//拼装参数
		Map<String, String> reqParams = fyPaymentService.withdraw(userFund.payment_account, withdrawalAmt, orderNo);
		
		//构建发送给第三方的表单
		String html = "";
		
		if (client == Client.ANDROID.code || client == Client.IOS.code || client == Client.WECHAT.code) {
			html = FyUtils.createHtml(reqParams, FyConsts.post_url + FyConsts.appWithdraw);
		} else {
			html = FyUtils.createHtml(reqParams, FyConsts.post_url + FyConsts.withdraw);
		}
		
		//备注参数（全部以“r_”开头）
		reqParams.put(RemarkPramKey.USER_ID, userId + "");
		reqParams.put(RemarkPramKey.WITHDRAWAL_AMT, withdrawalAmt + "");
		reqParams.put(RemarkPramKey.CLIENT, client + "");
		
		//打印提交日志
		fyPaymentService.printRequestData("", userId, serviceOrderNo, orderNo, ServiceType.WITHDRAW, FyPayType.CASH, 
				reqParams, BaseController.getBaseURL() + FyConsts.withdrawRepair);
		
		//如果是app端发起的请求则将构建的表单交由app提交
		if (Client.isAppEnum(client)) {
			result.code = 1;
			result.msg = "";
			result.obj = html;
			
			return result;
		}
		
		//提交表单
		FyPaymentRequestCtrl.getInstance().submitForm(html, client);
		
		return result;
	}

	@Override
	public ResultInfo bidCreate(int client, String serviceOrderNo, t_bid bid,
			int bidCreateFrom, Object... obj) {
		ResultInfo result = new ResultInfo();
		
		if (bid == null) {
			throw new RuntimeException("bid is null");
		}
		
		//借款标编号，借款标唯一标识
		bid.mer_bid_no = OrderNoUtil.getBidNo();
		//业务订单号，标的登记操作唯一标识
		bid.service_order_no = serviceOrderNo;
		
		//不冻结保证金则直接执行平台发标业务
		if (bid.bail <= 0){
			return bidService.doCreateBid(bid, serviceOrderNo);
		}
		
		//保证金冻结订单号(由平台生成)
		bid.trust_order_no = OrderNoUtil.getNormalOrderNo(ServiceType.BID_CREATE);
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(bid.user_id);
		
		if (userFund == null) {
			result.code = -1;
			result.msg = "查询借款人资金信息失败";
			
			return result;
		}
		
		//拼装参数
		Map<String, String> reqParams = fyPaymentService.freeze(userFund.payment_account, bid.bail, bid.trust_order_no, FyPayType.USRFREEZEBG.value);
		
		//用于入库的map
		LinkedHashMap<String, String> temp = new LinkedHashMap<String, String>();
		temp.putAll(reqParams);
		temp.put(RemarkPramKey.PAYMENT_ACCOUNT, userFund.payment_account);
		temp.put(RemarkPramKey.BID, new Gson().toJson(bid));
		temp.put(RemarkPramKey.CLIENT, client + "");
		
		//打印提交日志，流水号就是保证金冻结交易订单号
		fyPaymentService.printRequestData("", userFund.user_id, serviceOrderNo, bid.trust_order_no, ServiceType.BID_CREATE, FyPayType.USRFREEZEBG, 
				temp, BaseController.getBaseURL() + FyConsts.bidCreateRepair);
		
		//向第三方提交表单
		String data = HttpUtil.postMethod(FyConsts.post_url + FyConsts.freeze, reqParams, "UTF-8");
		
		//将第三方返回的xml转换成map
		Map<String, String> dataMap = FyUtils.parseXmlToMap(data);
		
		//打印回调日志信息
		fyPaymentService.printCallBackData("资金冻结", dataMap, ServiceType.BID_CREATE, FyPayType.USRFREEZEBG);
		
		//校验签名、状态码判断、防止重单
		result = FyUtils.checkSign(dataMap, "资金冻结", FyPayType.USRFREEZEBG.value, FyPayType.USRFREEZEBG);
		
		//业务若已被执行则跳出不再继续后续步骤
		if (result.code < 1 && result.code != ResultInfo.ALREADY_RUN) {
			
			return result;
		}
		
		//标的信息录入请求第三方后平台业务处理
		result = fyPaymentCallBackService.addBidInfo(dataMap);
		
		//标的登记成功，页面跳转
		FyPaymentCallBackCtrl.getInstance().addBidInfoWS(result, bid, bidCreateFrom);
		
		//app 接口处理
		if(Client.isAppEnum(client)){
			
			result.obj = "";
			return result;
		}
		
		return result;
	}

	@Override
	public ResultInfo bidFailed(int client, String serviceOrderNo, t_bid bid,
			ServiceType serviceType, Object... obj) {
		ResultInfo result = new ResultInfo();
		
		//查询借款标投资记录
		List<t_invest> investList = investService.queryBidInvest(bid.id);
		//冻结订单号
		String freezeTrxId = "";
		
		//查询借款人资金信息
		t_user_fund userFund = userFundService.queryUserFundByUserId(bid.user_id);
		
		if (userFund == null) {
			result.code = -1;
			result.msg = "查询借款人资金信息失败";
			
			return result;
		}
		
		//借款人保证金解冻
		if (bid.bail > 0) {
			String orderNo = OrderNoUtil.getSpecialOrderNo(bid.time, ServiceType.USER_UNFREEZE, OrderNoUtil.SPECIAL_UNFREEZE_BAIL, bid.id);
			
			//组装参数
			Map<String, String> reqParams = fyPaymentService.unFreeze(userFund.payment_account, bid.bail, orderNo, FyPayType.USRUNFREEZE.value);
			
			//打印提交日志
			fyPaymentService.printRequestData("", userFund.user_id, serviceOrderNo, 
					orderNo, serviceType, FyPayType.USRUNFREEZE, reqParams, BaseController.getBaseURL() + FyConsts.bidFailedRepair);
			
			//表单提交
			String xmlData = HttpUtil.postMethod(FyConsts.post_url + FyConsts.unFreeze, reqParams, "UTF-8");
			
			//将第三方返回的xml转换成map
			Map<String, String> cbParams = FyUtils.parseXmlToMap(xmlData);
			
			//打印回调日志信息
			fyPaymentService.printCallBackData("解冻借款人发标保证金", cbParams, serviceType, FyPayType.USRUNFREEZE);
			
			//校验签名、状态码判断、防止重单
			result = FyUtils.checkSign(cbParams, "解冻借款人发标保证金", FyPayType.USRUNFREEZE.value, FyPayType.USRUNFREEZE);
			
			if (result.code < 1 && result.code != ResultInfo.ALREADY_RUN) {
				
				//解冻失败继续查一次，确认是否解冻成功
				t_payment_request pr = fyPaymentService.queryRequest(orderNo);
				
				if (pr == null) {
					result.code = -1;
					result.msg = "解冻保证金失败";
					return result;
				}
				
				String queryOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.QUERYTXN);
				
				result = queryTxn(Client.PC.code, queryOrderNo, pr);
				
				if (result.code > 0){
					@SuppressWarnings("unchecked")
					Map<String, String> map = (Map)(result.obj);
					
					if(!map.get("resp_code").equals("0000")){
						result.code = -1;
						result.msg = "解冻保证金失败";
						return result;
					}
				
				}
				return result;
			}
		}
		
		if (investList != null && investList.size() > 0) {
			//投资人投资金额解冻
			for (t_invest invest : investList) {
				String investUnfreezeOrderNo = OrderNoUtil.getNormalOrderNo(serviceType);
				
				freezeTrxId = invest.trust_order_no;
				
				//查询借款人资金信息
				t_user_fund investUserFund = userFundService.queryUserFundByUserId(invest.user_id);
				
				if (userFund == null){
					result.code = -1;
					result.msg = "查询借款人资金信息失败";
					
					return result;
				}
				
				//组装参数
				Map<String, String> reqParams = fyPaymentService.preAuthCancel(investUserFund.payment_account, userFund.payment_account, 
						invest.amount, freezeTrxId, investUnfreezeOrderNo, FyPayType.TENDERCANCLE.value);
				
				//打印提交日志
				fyPaymentService.printRequestData("", investUserFund.user_id, serviceOrderNo, investUnfreezeOrderNo, serviceType, 
						FyPayType.TENDERCANCLE, reqParams, BaseController.getBaseURL() + FyConsts.bidFailedRepair);
				
				//表单提交
				String xmlData = HttpUtil.postMethod(FyConsts.post_url + FyConsts.preAuthCancel, reqParams, "UTF-8");
				
				//将第三方返回的xml转换成map
				Map<String, String> cbParams = FyUtils.parseXmlToMap(xmlData);
				
				//打印回调日志信息
				fyPaymentService.printCallBackData("借款标撤销解冻投资人冻结资金", cbParams, serviceType, FyPayType.TENDERCANCLE);
				
				//校验签名、状态码判断、防止重单
				result = FyUtils.checkSign(cbParams, "借款标撤销解冻投资人冻结资金", FyPayType.TENDERCANCLE.value, FyPayType.TENDERCANCLE);
				
				if (result.code < 1 && result.code != ResultInfo.ALREADY_RUN) {
					
					return result;
				}
			}
		}
		
		switch (serviceType) {
		case BID_FLOW: //系统自动流标
			result = bidService.doBidFlow(serviceOrderNo, bid);
			break;
		case BID_PRE_AUDIT_FAIL:{ //初审不通过
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
	public ResultInfo invest(int client, int investType, String serviceOrderNo,
			long userId, t_bid bid, double investAmt, Object... obj) {
		ResultInfo result = new ResultInfo();
		
		//查询理财人资金信息
		t_user_fund investUserFund = userFundService.queryUserFundByUserId(userId);
		
		if (investUserFund == null) {
			result.code = -1;
			result.msg = "查询理财人资金信息失败";
			
			return result;
		}
		
		//查询借款人资金信息
		t_user_fund loanUserInfo = userFundService.queryUserFundByUserId(bid.user_id);
		
		if (loanUserInfo == null){
			result.code = -1;
			result.msg = "查询借款人资金信息失败";
			
			return result;
		}
		
		double redPacketAmt = 0.00D;
		long redPacketId = 0L;
		
		if (obj.length >= 2) {
			redPacketAmt = (Double)obj[0];
			redPacketId = (Long)obj[1];
		}
		
		//交易订单号
		String orderNo = serviceOrderNo;
		
		//组装参数
		Map<String, String> reqParams = fyPaymentService.preAuth(investUserFund.payment_account, loanUserInfo.payment_account, 
				investAmt - redPacketAmt, orderNo, FyPayType.INITIATIVETENDER.value);
		
		//用于入库的map
		LinkedHashMap<String, String> inpParams = new LinkedHashMap<String, String>();
		inpParams.putAll(reqParams);
		inpParams.put(RemarkPramKey.USER_ID, userId + "");
		inpParams.put(RemarkPramKey.BID_ID, bid.id + "");
		inpParams.put(RemarkPramKey.INVEST_AMT, investAmt + "");
		inpParams.put(RemarkPramKey.CLIENT, client + "");
		inpParams.put(RemarkPramKey.INVEST_TYPE, investType + "");
		inpParams.put(RemarkPramKey.RED_PACKET_AMT, redPacketAmt + "");
		inpParams.put(RemarkPramKey.RED_PACKET_ID, redPacketId + "");
		
		//打印提交日志
		fyPaymentService.printRequestData("", userId, serviceOrderNo, 
				orderNo, ServiceType.INVEST, FyPayType.INITIATIVETENDER, inpParams, BaseController.getBaseURL() + FyConsts.investRepair);
		
		//提交表单
		String xmlData = HttpUtil.postMethod(FyConsts.post_url + FyConsts.preAuth, reqParams, "UTF-8");
		
		//将xml转换为map
		Map<String, String> cbParams = FyUtils.parseXmlToMap(xmlData);
		
		//打印回调日志信息
		fyPaymentService.printCallBackData("主动投标", cbParams, ServiceType.INVEST, FyPayType.INITIATIVETENDER);
		
		//签名校验、状态码判断、防止重单
		result = FyUtils.checkSign(cbParams, "主动投标", FyPayType.INITIATIVETENDER.value, FyPayType.INITIATIVETENDER);
		
		if (result.code < 1 && result.code != ResultInfo.ALREADY_RUN) {
			
			return result;
		}
		
		//主动投标请求第三方后平台业务处理
		result = fyPaymentCallBackService.invest(cbParams);
		
		//app 接口处理
		if(Client.isAppEnum(client)){

			result.obj = "";
			return result;
		}
		
		return result;
	}

	@Override
	public ResultInfo bidSuditSucc(int client, String serviceOrderNo,
			long releaseSupervisorId, t_bid bid, Object... obj) {
		ResultInfo result = new ResultInfo();
		
		if (bid == null) {
			throw new RuntimeException("bid is null");
		}
		
		//查询借款人资金信息
		t_user_fund loanUserFund = userFundService.queryUserFundByUserId(bid.user_id);
		
		if (loanUserFund == null) {
			result.code = -1;
			result.msg = "查询借款人资金信息失败";
			
			return result;
		}
		
		/** 解冻保证金 */
		if (bid.bail > 0) {
			String orderNo = OrderNoUtil.getSpecialOrderNo(bid.time, ServiceType.USER_UNFREEZE, OrderNoUtil.SPECIAL_UNFREEZE_BAIL, bid.id);
			
			//组装参数
			Map<String, String> reqParams = fyPaymentService.unFreeze(loanUserFund.payment_account, bid.bail, orderNo, FyPayType.USRUNFREEZE.value);
			
			//打印提交日志
			FyPaymentService.printRequestData("", bid.user_id, serviceOrderNo, orderNo, 
					ServiceType.USER_UNFREEZE, FyPayType.USRUNFREEZE, reqParams, BaseController.getBaseURL() + FyConsts.bidSuditSuccRepair);
			
			//提交表单
			String xmlData = HttpUtil.postMethod(FyConsts.post_url + FyConsts.unFreeze, reqParams, "UTF-8");
			
			//将xml转换为map
			Map<String, String> cbParams = FyUtils.parseXmlToMap(xmlData);
			
			//打印回调日志信息
			fyPaymentService.printCallBackData("解冻发标冻结保证金", cbParams, ServiceType.USER_UNFREEZE, FyPayType.USRUNFREEZE);
			
			//签名校验、状态码判断、防止重单
			result = FyUtils.checkSign(cbParams, "解冻发标冻结保证金", FyPayType.USRUNFREEZE.value, FyPayType.USRUNFREEZE);
			
			if (result.code < 1 && result.code != ResultInfo.ALREADY_RUN) {
				
				t_payment_request pr = fyPaymentService.queryRequest(orderNo);
				
				if (pr == null) {
					result.code = -1;
					result.msg = "解冻保证金失败";
					return result;
				}
				
				String queryOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.QUERYTXN);
				
				result = queryTxn(Client.PC.code, queryOrderNo, pr);
				
				if (result.code > 0){
					@SuppressWarnings("unchecked")
					Map<String, String> map = (Map)(result.obj);
					
					if(!map.get("resp_code").equals("0000")){
						
						result.code = -1;
						result.msg = "解冻保证金失败";
						return result;
					}
				
				}
				return result;
			}
		}
		
		//查询标的投资记录
		List<t_invest> invests = investService.queryBidInvest(bid.id);
		
		if (invests == null || invests.size() == 0) {
			result.code = -1;
			result.msg = "查询投资列表失败";

			return result;
		}
		
		//借款人第三方账号
		String inCustId = loanUserFund.payment_account;
		t_user_fund investUserFund = null;
		int successCount = 0;
		double loanServiceFee = 0;
		double redpacketAmountSum = 0.00D; //若有投资人使用了红包，则需要将红包金额都加起来一次性转给借款人
		
		for (t_invest invest : invests) {
			redpacketAmountSum += invest.red_packet_amount;
		}
		
		for (t_invest invest : invests) {
			//查询投资人资金信息
			investUserFund = userFundService.queryUserFundByUserId(invest.user_id);
			
			if (investUserFund == null) {
				result.code = -1;
				result.msg = "查询投资人资金信息失败";
				
				return result;
			}
			
			//交易订单号
			String orderNo = OrderNoUtil.getNormalOrderNo(ServiceType.BID_AUDIT_SUCC);
			//投资人第三方账号
			String outCusId = investUserFund.payment_account;
			//成功投资富友返回的冻结订单号
			String freezeOrdId = invest.trust_order_no;
			//借款服务费
			double fee = invest.loan_service_fee_divide;
			
			//组装参数
			Map<String, String> reqParams = fyPaymentService.bidAuditSucc(outCusId, inCustId, invest.amount - invest.red_packet_amount, freezeOrdId, 
					orderNo, FyPayType.LOANS.value);
			
			//打印提交日志
			fyPaymentService.printRequestData("", bid.user_id, serviceOrderNo, 
					orderNo, ServiceType.BID_AUDIT_SUCC, FyPayType.LOANS, reqParams, BaseController.getBaseURL() + FyConsts.bidSuditSuccRepair);
			
			//提交表单
			String xmlData = HttpUtil.postMethod(FyConsts.post_url + FyConsts.transferBu, reqParams, "UTF-8");
			
			//将xml转换为map
			Map<String, String> cbParams = FyUtils.parseXmlToMap(xmlData);
			
			//打印回调日志信息
			fyPaymentService.printCallBackData("财务放款", cbParams, ServiceType.BID_AUDIT_SUCC, FyPayType.LOANS);
			
			//签名校验、状态码判断、防止重单
			result = FyUtils.checkSign(cbParams, "财务放款", FyPayType.LOANS.value, FyPayType.LOANS);
			
			if (result.code < 1 && result.code != ResultInfo.ALREADY_RUN) {
				
				return result;
			}
			
			//每次成功执行上述流程则将放款成功count+1
			successCount++;
			
			//计算借款服务费
			loanServiceFee += fee;
		}
		
		/** 收取借款服务费 */
		if (loanServiceFee > 0) {
			//交易订单号
			String feeOrderNo = OrderNoUtil.getSpecialOrderNo(bid.time, ServiceType.TRANSFER, OrderNoUtil.SPECIAL_SERVICE_FEE, bid.id);
			
			//组装参数
			Map<String, String> feeReqParams = fyPaymentService.transferBmu(inCustId, FyConsts.mchnt_name, loanServiceFee, feeOrderNo, 
					FyPayType.MERCHANTANDPERSIONTRANSFER.value);
					
			//用于入库的map
			LinkedHashMap<String, String> inpParams = new LinkedHashMap<String, String>();
			inpParams.putAll(feeReqParams);
			inpParams.put(RemarkPramKey.SERVICE_FEE_RULE, bid.service_fee_rule);
			inpParams.put(RemarkPramKey.BID, new Gson().toJson(bid));
			inpParams.put(RemarkPramKey.SERVICE_ORDER_NO, serviceOrderNo);
			
			//打印提交日志
			fyPaymentService.printRequestData("", bid.user_id, serviceOrderNo, feeOrderNo, ServiceType.TRANSFER, 
					FyPayType.MERCHANTANDPERSIONTRANSFER, inpParams, BaseController.getBaseURL() + FyConsts.bidSuditSuccRepair);
			
			//提交表单
			String feeXmlData = HttpUtil.postMethod(FyConsts.post_url + FyConsts.transferBmu, feeReqParams, "UTF-8");
			
			//将xml转换为map
			Map<String, String> feeCbParams = FyUtils.parseXmlToMap(feeXmlData);
			
			//打印回调日志信息
			fyPaymentService.printCallBackData("借款人支付借款服务费", feeCbParams, ServiceType.TRANSFER, FyPayType.MERCHANTANDPERSIONTRANSFER);
			
			//签名校验、状态码判断、防止重单
			result = FyUtils.checkSign(feeCbParams, "借款人支付借款服务费", FyPayType.MERCHANTANDPERSIONTRANSFER.value, FyPayType.MERCHANTANDPERSIONTRANSFER);
			
			if (result.code < 1 && result.code != ResultInfo.ALREADY_RUN) {
				
				return result;
			}
		}
		
		/** 平台向借款人支付帮投资人垫付的部分(红包) */
		if (redpacketAmountSum > 0) {
			//交易订单号
			String redpacketOrderNo = OrderNoUtil.getSpecialOrderNo(bid.time, ServiceType.TRANSFER, OrderNoUtil.SPECIAL_PLATFORM_ADVANCE_REDPACKET, bid.id);
			
			//组装参数
			Map<String, String> redpacketReqParams = fyPaymentService.transferBmu(FyConsts.mchnt_name, inCustId, redpacketAmountSum, redpacketOrderNo, 
					FyPayType.MERCHANTANDPERSIONTRANSFER.value);
			
			//打印提交日志
			fyPaymentService.printRequestData("", bid.user_id, serviceOrderNo, redpacketOrderNo, ServiceType.TRANSFER, 
					FyPayType.MERCHANTANDPERSIONTRANSFER, redpacketReqParams, BaseController.getBaseURL() + FyConsts.bidSuditSuccRepair);
			
			//提交表单
			String redpacketXmlData = HttpUtil.postMethod(FyConsts.post_url + FyConsts.transferBmu, redpacketReqParams, "UTF-8");
			
			//将xml转换为map
			Map<String, String> redpacketCbParams = FyUtils.parseXmlToMap(redpacketXmlData);
			
			//打印回调日志信息
			fyPaymentService.printCallBackData("平台向借款人垫付投资人使用的红包", redpacketCbParams, ServiceType.TRANSFER, FyPayType.MERCHANTANDPERSIONTRANSFER);
			
			//签名校验、状态码判断、防止重单
			result = FyUtils.checkSign(redpacketCbParams, "平台向借款人垫付投资人使用的红包", FyPayType.MERCHANTANDPERSIONTRANSFER.value, 
					FyPayType.MERCHANTANDPERSIONTRANSFER);
			
			if (result.code < 1 && result.code != ResultInfo.ALREADY_RUN) {
				
				return result;
			}
		}
		
		//全部放款成功才算成功
		if (successCount != invests.size()) {
			result.code = -1;
			result.msg = "放款失败";
			
			return result;
		}
		
		//借款服务费纠偏
		bidService.updateLoanServiceFee(bid.id, loanServiceFee);
		
		//财务放款（执行）
		result = bidService.doRelease(bid.id, releaseSupervisorId, serviceOrderNo);
		
		return result;
	}

	@Override
	public ResultInfo repayment(int client, String serviceOrderNo, t_bill bill, 
			List<Map<String, Double>> billInvestFeeList, Object... obj) {
		ResultInfo result = new ResultInfo();
		
		if (bill == null) {
			throw new RuntimeException("bill is null");
		}
		
		//查询借款用户信息
		t_user_fund loanUserFund = userFundService.queryUserFundByUserId(bill.user_id);
		
		if (loanUserFund == null) {
			result.code = -1;
			result.msg = "查询借款人资金信息失败";
			
			return result;
		}
		
		//借款用户第三方账号
		String outCusId = loanUserFund.payment_account;
		
		//查询待还的理财账单
		List<t_bill_invest> billInvestList = billInvestService.queryNOReceiveInvestBills(bill.bid_id, bill.period);
		
		if (billInvestList == null || billInvestList.size() <= 0) {
			result.code = -1;
			result.msg = "查询待还的理财账单失败";
			
			return result;
		}
		
		//查询还款提交给第三方需要的参数
		List<LinkedHashMap<String, String>> list = fyPaymentService.queryRepaymentData(bill, billInvestList, outCusId, billInvestFeeList);
		
		if (list == null || list.size() <= 0) {
			result.code = -1;
			result.msg = "查询还款提交给第三方需要的参数失败";
			
			return result;
		}
		
		Map<String, String> serParams = new HashMap<String, String>();
		
		for (int i = 0; i < list.size(); i++) {
			LinkedHashMap<String, String> map = list.get(i);
			String type = map.get("type");
			/*  
			 *  由于还款和理财服务费的map均存于list中
			 *  而富友接口文档中并不存在该参数，因此这里只是用于平台逻辑处理
			 *  故使用过后需要从map中移除
			 */
			map.remove("type");
			String out_cust_no = map.get("out_cust_no");
			String in_cust_no = map.get("in_cust_no");
			double amt = Convert.strToDouble(map.get("amt"), 0.00D);
			String mchnt_txn_ssn = map.get("mchnt_txn_ssn");
			
			//还款
			if ("R".equals(type) && amt > 0) {
				//组装参数
				Map<String, String> reqParams = fyPaymentService.transferBu(out_cust_no, in_cust_no, amt, mchnt_txn_ssn, FyPayType.REPAYMENT.value);
				
				//备注参数（全部以“r_”开头）
				Map<String, String> inpParams = new LinkedHashMap<String, String>();
				inpParams.putAll(reqParams);
				inpParams.put(RemarkPramKey.BILL_ID, bill.id + "");
				inpParams.put(RemarkPramKey.BILL_INVEST_FEE_LIST, new Gson().toJson(billInvestFeeList));
				
				//打印提交参数
				fyPaymentService.printRequestData("", bill.user_id, serviceOrderNo, mchnt_txn_ssn, ServiceType.REPAYMENT, 
						FyPayType.REPAYMENT, inpParams, BaseController.getBaseURL() + FyConsts.repaymentRepair);
				
				//提交表单
				String xmlData = HttpUtil.postMethod(FyConsts.post_url + FyConsts.transferBu, reqParams, "UTF-8");
				
				//将xml转换为map
				Map<String, String> cbParams = FyUtils.parseXmlToMap(xmlData);
				
				//打印回调日志信息
				fyPaymentService.printCallBackData("借款人还款", cbParams, ServiceType.REPAYMENT, FyPayType.REPAYMENT);
				
				result = FyUtils.checkSign(cbParams, "借款人还款", FyPayType.REPAYMENT.value, FyPayType.REPAYMENT);
				
				if (result.code < 1 && result.code != ResultInfo.ALREADY_RUN) {
					
					return result;
				}
				
				//最后一笔还款成功后将第三方回调参数获取到，用于平台业务处理（最后一个map可能是还款也可能是理财管理费）
				if (i + 1 >= list.size()) {
					serParams = cbParams;
				}
			}

			//理财管理费
			if ("RM".equals(type) && amt > 0) {
				//组装参数
				Map<String, String> feeReqParams = fyPaymentService.transferBmu(out_cust_no, in_cust_no, amt, mchnt_txn_ssn, 
						FyPayType.MERCHANTANDPERSIONTRANSFER.value);
				
				//备注参数（全部以“r_”开头）
				Map<String, String> feeInpParams = new LinkedHashMap<String, String>();
				feeInpParams.putAll(feeReqParams);
				feeInpParams.put(RemarkPramKey.BILL_ID, bill.id + "");
				feeInpParams.put(RemarkPramKey.BILL_INVEST_FEE_LIST, new Gson().toJson(billInvestFeeList));
				
				//打印提交参数
				fyPaymentService.printRequestData("", Convert.strToLong(out_cust_no, 0L), serviceOrderNo, mchnt_txn_ssn, ServiceType.TRANSFER, 
						FyPayType.MERCHANTANDPERSIONTRANSFER, feeInpParams, BaseController.getBaseURL() + FyConsts.repaymentRepair);
				
				//提交表单
				String feeXmlData = HttpUtil.postMethod(FyConsts.post_url + FyConsts.transferBmu, feeReqParams, "UTF-8");
				
				//将xml转换为map
				Map<String, String> feeCbParams = FyUtils.parseXmlToMap(feeXmlData);
				
				//打印回调日志信息
				fyPaymentService.printCallBackData("理财人支付理财服务费", feeCbParams, ServiceType.TRANSFER, FyPayType.MERCHANTANDPERSIONTRANSFER);
				
				//签名校验、状态码判断、防止重单
				result = FyUtils.checkSign(feeCbParams, "理财人支付理财服务费", FyPayType.MERCHANTANDPERSIONTRANSFER.value, FyPayType.MERCHANTANDPERSIONTRANSFER);
				
				if (result.code < 1 && result.code != ResultInfo.ALREADY_RUN) {
					
					return result;
				}
				
				//最后一笔理财管理费收取成功后将第三方回调参数获取到，用于平台业务处理（最后一个map可能是还款也可能是理财管理费）
				if (i + 1 >= list.size()) {
					serParams = feeCbParams;
				}
			}
		}
		
		//还款请求第三方后平台业务处理
		result = fyPaymentCallBackService.repayment(serParams);
		
		//app 接口处理
		if(Client.isAppEnum(client)){

			result.obj = "";
			return result;
		}
		
		return result;
	}

	@Override
	public ResultInfo advanceRepayment(int client, String serviceOrderNo,
			t_bill bill, Object... obj) {
		ResultInfo result = new ResultInfo();
		
		if (bill == null) {
			throw new RuntimeException("bill is null");
		}
		
		//查询借款用户信息
		t_user_fund loanUserFund = userFundService.queryUserFundByUserId(bill.user_id);
		
		if (loanUserFund == null) {
			result.code = -1;
			result.msg = "查询借款人资金信息失败";
			
			return result;
		}
		
		//本息垫付后还款查询参数
		Map<String, String> map = fyPaymentService.queryAdvanceRepayment(bill, loanUserFund.payment_account);
		
		String out_cust_no = map.get("out_cust_no");
		String in_cust_no = map.get("in_cust_no");
		double amount = Convert.strToDouble(map.get("amt"), 0.00);
		String orderno = map.get("mchnt_txn_ssn");
		String rem = map.get("rem");
		
		//组装参数
		Map<String, String> reqParams = fyPaymentService.transferBmu(out_cust_no, in_cust_no, amount, orderno, rem);
		
		//备注参数（全部以“r_”开头）
		Map<String, String> inpParams = new LinkedHashMap<String, String>();
		inpParams.putAll(reqParams);
		inpParams.put(RemarkPramKey.USER_ID, bill.user_id + "");
		inpParams.put(RemarkPramKey.BILL_ID, bill.id + "");
		inpParams.put(RemarkPramKey.LOAN_OVERDUE_FINE, bill.overdue_fine + "");
		inpParams.put(RemarkPramKey.CLIENT, client + "");
		
		//打印提交日志
		fyPaymentService.printRequestData("", bill.user_id, serviceOrderNo, orderno, ServiceType.REPAYMENT_AFER_ADVANCE, 
				FyPayType.MERCHANTANDPERSIONTRANSFER, inpParams, BaseController.getBaseURL() + FyConsts.advanceRepaymentRepair);
		
		//提交表单
		String xmlData = HttpUtil.postMethod(FyConsts.post_url + FyConsts.transferBmu, reqParams, "UTF-8");
		
		//将xml转换为map
		Map<String, String> cbParams = FyUtils.parseXmlToMap(xmlData);
		
		//打印回调日志信息
		fyPaymentService.printCallBackData("本息垫付后还款", cbParams, ServiceType.REPAYMENT_AFER_ADVANCE, FyPayType.MERCHANTANDPERSIONTRANSFER);
		
		//签名校验、状态码判断、防止重单
		result = FyUtils.checkSign(cbParams, "本息垫付后还款", FyPayType.MERCHANTANDPERSIONTRANSFER.value, FyPayType.MERCHANTANDPERSIONTRANSFER);
		
		if (result.code < 1 && result.code != ResultInfo.ALREADY_RUN) {
			
			return result;
		}
		
		//本息垫付后还款请求第三方后平台业务处理
		result = fyPaymentCallBackService.advanceRepayment(cbParams);
		
		return result;
	}

	@Override
	public ResultInfo advance(int client, String serviceOrderNo, t_bill bill,
			List<Map<String, Double>> billInvestFeeList, Object... obj) {
		ResultInfo result = new ResultInfo();
		
		if (bill == null) {
			throw new RuntimeException("bill is null");
		}
		
		//查询待还的理财账单
		List<t_bill_invest> billInvestList = billInvestService.queryNOReceiveInvestBills(bill.bid_id, bill.period);
		
		if (billInvestList == null || billInvestList.size() <= 0) {
			result.code = -1;
			result.msg = "查询待还的理财账单失败";
			
			return result;
		}
		
		//平台本息垫付查询参数
		List<Map<String, String>> pDetailsList = fyPaymentService.queryAdvanceOrOfflineReceive(billInvestFeeList, billInvestList, ServiceType.ADVANCE);
		
		if (pDetailsList == null || pDetailsList.size() <= 0) {
			result.code = -1;
			result.msg = "查询还款提交给第三方需要的参数失败";
			
			return result;
		}
		
		for (int i = 0; i < pDetailsList.size(); i++) {
			Map<String, String> map = pDetailsList.get(i);
			String out_cust_no = map.get("out_cust_no");
			String in_cust_no = map.get("in_cust_no");
			double amount = Convert.strToDouble(map.get("amt"), 0);
			String orderno  = map.get("mchnt_txn_ssn");
			String rem = map.get("rem");
			
			//组装参数
			Map<String, String> reqParams = fyPaymentService.transferBmu(out_cust_no, in_cust_no, amount, orderno, rem);
			
			//打印提交日志
			fyPaymentService.printRequestData("", bill.user_id, serviceOrderNo, 
					orderno, ServiceType.ADVANCE, FyPayType.REPAYMENT, reqParams, BaseController.getBaseURL() + FyConsts.advanceRepair);
			
			//提交表单
			String xmlData = HttpUtil.postMethod(FyConsts.post_url + FyConsts.transferBmu, reqParams, "UTF-8");
			
			//将xml转换为map
			Map<String, String> cbParams = FyUtils.parseXmlToMap(xmlData);
			
			//打印回调日志信息
			fyPaymentService.printCallBackData("本息垫付", cbParams, ServiceType.ADVANCE, FyPayType.REPAYMENT);
			
			//签名校验、状态码判断、防止重单
			result = FyUtils.checkSign(cbParams, "本息垫付", FyPayType.REPAYMENT.value, FyPayType.REPAYMENT);
			
			if (result.code < 1 && result.code != ResultInfo.ALREADY_RUN) {
				
				return result;
			}
		}
		
		//本息垫付业务逻辑执行
		result = billService.doPrincipalAdvance(bill.id, serviceOrderNo, billInvestFeeList);
		
		return result;
	}

	@Override
	public ResultInfo offlineReceive(int client, String serviceOrderNo,
			t_bill bill, List<Map<String, Double>> billInvestFeeList,
			Object... obj) {
		ResultInfo result = new ResultInfo();
		
		if (bill == null) {
			throw new RuntimeException("bill is null");
		}
		
		//查询待还的理财账单
		List<t_bill_invest> billInvestList = billInvestService.queryNOReceiveInvestBills(bill.bid_id, bill.period);
		
		if (billInvestList == null || billInvestList.size() <= 0){
			result.code = -1;
			result.msg = "查询待还的理财账单失败";
			
			return result;
		}
		
		//平台线下收款查询参数
		List<Map<String, String>> pDetailsList = fyPaymentService.queryAdvanceOrOfflineReceive(billInvestFeeList, billInvestList, 
				ServiceType.OFFLINE_RECEIVE);
		
		if (pDetailsList == null || pDetailsList.size() <= 0) {
			result.code = -1;
			result.msg = "查询还款提交给第三方需要的参数失败";
			
			return result;
		}
		
		for (int i = 0; i < pDetailsList.size(); i++) {
			Map<String, String> map = pDetailsList.get(i);
			String out_cust_no = map.get("out_cust_no");
			String in_cust_no = map.get("in_cust_no");
			double amount = Convert.strToDouble(map.get("amt"), 0);
			String orderno  = map.get("mchnt_txn_ssn");
			String rem = map.get("rem");
			
			//组装参数
			Map<String, String> reqParams = fyPaymentService.transferBmu(out_cust_no, in_cust_no, amount, orderno, rem);
			
			//打印提交日志
			fyPaymentService.printRequestData("", bill.user_id, serviceOrderNo, 
					orderno, ServiceType.OFFLINE_RECEIVE, FyPayType.REPAYMENT, reqParams, BaseController.getBaseURL() + FyConsts.offlineReceiveRepair);
			
			//提交表单
			String xmlData = HttpUtil.postMethod(FyConsts.post_url + FyConsts.transferBmu, reqParams, "UTF-8");
			
			//将xml转换为map
			Map<String, String> cbParams = FyUtils.parseXmlToMap(xmlData);
			
			//打印回调日志信息
			fyPaymentService.printCallBackData("线下收款", cbParams, ServiceType.OFFLINE_RECEIVE, FyPayType.REPAYMENT);
			
			//签名校验、状态码判断、防止重单
			result = FyUtils.checkSign(cbParams, "线下收款", FyPayType.REPAYMENT.value, FyPayType.REPAYMENT);
			
			if (result.code < 1 && result.code != ResultInfo.ALREADY_RUN) {
				
				return result;
			}
		}
		
		//线下收款业务逻辑执行
		result = billService.doOfflineReceive(bill.id, billInvestFeeList, serviceOrderNo);
		
		return result;
	}

	@Override
	public ResultInfo conversion(int client, String serviceOrderNo,
			t_conversion_user conversion, Object... obj) {
		ResultInfo result = new ResultInfo();
		
		//被奖励用户信息
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
		
		//被奖励用户第三方账号
		String paymentAccount = userInfo.payment_account;
		
		//统一奖励兑换交易订单号必须不变，防止重复兑换
		String orderNo = OrderNoUtil.getSpecialOrderNo(conversion.time, ServiceType.CONVERSION, OrderNoUtil.SPECIAL_CONVERSION, conversion.id);
		
		//组装参数
		Map<String, String> reqParams = fyPaymentService.transferBmu(FyConsts.mchnt_name, paymentAccount, conversion.amount, orderNo, 
				FyPayType.MERCHANTANDPERSIONTRANSFER.value);
		
		//备注参数（全部以“r_”开头）
		Map<String,String> inpParams = new LinkedHashMap<String, String>();
		inpParams.putAll(reqParams);
		inpParams.put(RemarkPramKey.CONVERSION_ID, conversion.id + "");
		
		fyPaymentService.printRequestData("", conversion.user_id, serviceOrderNo, orderNo, ServiceType.CONVERSION, 
				FyPayType.MERCHANTANDPERSIONTRANSFER, inpParams, "");
		
		//提交表单
		String xmlData = HttpUtil.postMethod(FyConsts.post_url + FyConsts.transferBmu, reqParams, "UTF-8");
		
		//将xml转换为map
		Map<String, String> cbParams = FyUtils.parseXmlToMap(xmlData);
		
		//打印回调日志信息
		fyPaymentService.printCallBackData("奖励兑换", cbParams, ServiceType.CONVERSION, FyPayType.MERCHANTANDPERSIONTRANSFER);
		
		//签名校验、状态码判断、防止重单
		result = FyUtils.checkSign(cbParams, "奖励兑换", FyPayType.MERCHANTANDPERSIONTRANSFER.value, FyPayType.MERCHANTANDPERSIONTRANSFER);
		
		if (result.code < 1 && result.code != ResultInfo.ALREADY_RUN) {
			
			return result;
		}
		
		//奖励兑换请求第三方后平台业务处理
		result = fyPaymentCallBackService.conversion(cbParams);
		
		//app 接口处理
		if(Client.isAppEnum(client)){
			
			result.obj = "";
			return result;
		}
		
		return result;
	}

	@Override
	public ResultInfo queryBindedBankCard(int client, long userId,
			Object... obj) {
		//富友无该接口故空实现
		return null;
	}

	@Override
	public ResultInfo queryMerchantBalance(int client, Object... obj) {
		//富友无该接口故空实现
		ResultInfo result = new ResultInfo();
		result.msg = "";
		result.code = -2;
		
		return result;
	}

	@Override
	public ResultInfo merchantRecharge(int client, String serviceOrderNo,
			double rechargeAmt, String type, String bankId, Object... obj) {
		//富友无该接口故空实现，商户充值登陆富友官网进行充值即可
		return null;
	}

	@Override
	public ResultInfo merchantWithdrawal(int client, String serviceOrderNo,
			double withdrawalAmt, String type, Object... obj) {
		//富友无该接口故空实现，商户提现登陆富友官网进行提现即可
		return null;
	}

	@Override
	public ResultInfo queryFundInfo(int client, String account) {
		ResultInfo result = new ResultInfo();
		
		if (StringUtils.isBlank(account)) {
			
			return result;
		}
		
		//订单号（富友接口查询用户余额时需要传订单号，一个订单号对应一个用户的查询）
		String orderNo = OrderNoUtil.getNormalOrderNo(ServiceType.QUERY_BALANCE_BG);
		
		//参数组装
		Map<String, String> reqParams = fyPaymentService.queryAmount(account, orderNo, "");
		
		//提交表单
		String xmlData = HttpUtil.postMethod(FyConsts.post_url + FyConsts.balanceAction, reqParams, "UTF-8");
		
		//将xml转换为map
		Map<String, String> cbParams = FyUtils.parseXmlToMap(xmlData);
		
		//打印回调日志信息
		fyPaymentService.printCallBackData("余额查询", cbParams, ServiceType.QUERY_BALANCE_BG, FyPayType.QUERYBALANCEBG);
		
		//签名校验、状态码判断、防止重单
		result = FyUtils.checkSign(cbParams, "余额查询", FyPayType.QUERYBALANCEBG.value, FyPayType.QUERYBALANCEBG);
		
		if (result.code < 1) {
			
			return result;
		}
		
		org.json.JSONObject plain;
		org.json.JSONObject plain1;
		org.json.JSONObject results;
		org.json.JSONObject res;
		Map<String, Object> maps = new HashMap<String, Object>();
		
		try {
			plain = XML.toJSONObject(cbParams.get("plain"));
			plain1 = plain.getJSONObject("plain");
			results = plain1.getJSONObject("results");
			res = results.getJSONObject("result");
			
			maps.put("pBlance", FyUtils.formatAmountToYuan(Convert.strToDouble(res.getString("ca_balance"), 0D)));
			maps.put("pFreeze", FyUtils.formatAmountToYuan(Convert.strToDouble(res.getString("cf_balance"), 0D)));
		} catch (JSONException e) {
			Logger.error(e, "解析第三方返回数据时：%s", e.getMessage());
		}
	
		result.code = 1;
		result.obj = maps;
		
		return result;
	}

	@Override
	public String getInterfaceDes(int interfaceType) {
		
		FyPayType fpt = FyPayType.getEnum(interfaceType);
		
		return fpt==null?"":fpt.value;
	}

	@Override
	public int getInterfaceType(Enum interfaceType) {
		
		FyPayType fpt = (FyPayType) interfaceType;
		
		return fpt==null?0:fpt.code;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultInfo debtTransfer(int clint, String serviceOrderNo,
			Long debtId, Long userId) {
		ResultInfo result = new ResultInfo();
		
		//查询请求时入库的参数
		Map<String, String> remarkParams = fyPaymentService.queryRequestParams(serviceOrderNo);
		
		if (remarkParams != null && remarkParams.size() > 0) {
			result.code = -1;
			result.msg = "该债权无法购买，请联系平台客服";
			
			return result;
		}
		
		//查询债权转让信息
		result = debtService.findTransferInfo(debtId, userId);
		
		if (result.code < 1) {

			return result;
		}
		
		Map<String, Object> map = (Map<String, Object>) result.obj;
		
		//出售债权用户
		t_user_fund sellCustUser = userFundService.queryUserFundByUserId(Long.valueOf(map.get("fromUserId") + ""));
		//购买债权用户
		t_user_fund buyCustUser = userFundService.queryUserFundByUserId(userId);
		
		String sellCustId = sellCustUser.payment_account; //出售债权用户资金托管账号
		double creditDealAmt = Convert.strToDouble(map.get("pPayAmt").toString(), 0.00); //债权成交金额
		double fee = Convert.strToDouble(map.get("managefee").toString(), 0.00); //出售债权用户需支付手续费
		
		String buyCustId = buyCustUser.payment_account; //购买债权用户资金托管账号
		
		//债权转让交易订单号
		String orderNo = serviceOrderNo;
		
		//组装参数
		Map<String, String> reqParams = fyPaymentService.transferBu(buyCustId, sellCustId, creditDealAmt, orderNo, FyPayType.CREDITASSIGN.value);
		
		//备注参数（全部以“r_”开头）
		Map<String, String> inpParams = new LinkedHashMap<String, String>();
		inpParams.putAll(reqParams);
		inpParams.put(RemarkPramKey.DEBT_ID, String.valueOf(debtId));
		inpParams.put(RemarkPramKey.DEBT_FEE, String.valueOf(fee));
		inpParams.put(RemarkPramKey.TO_USER_ID, String.valueOf(userId));
		inpParams.put(RemarkPramKey.SERVICE_ORDER_NO, serviceOrderNo);
		
		//打印提交日志
		fyPaymentService.printRequestData("", sellCustUser.user_id, serviceOrderNo, orderNo, ServiceType.DEBT_TRANSFER, 
				FyPayType.CREDITASSIGN, inpParams, BaseController.getBaseURL() + FyConsts.debtTransferRepair);
		
		//提交表单
		String xmlData = HttpUtil.postMethod(FyConsts.post_url + FyConsts.transferBu, reqParams, "UTF-8");
		
		//将xml转换为map
		Map<String, String> cbParams = FyUtils.parseXmlToMap(xmlData);
		
		//打印回调日志信息
		fyPaymentService.printCallBackData("债权转让", cbParams, ServiceType.DEBT_TRANSFER, FyPayType.CREDITASSIGN);
		
		//签名校验、状态码判断、防止重单
		result = FyUtils.checkSign(cbParams, "债权转让", FyPayType.CREDITASSIGN.value, FyPayType.CREDITASSIGN);
		
		if (result.code < 1 && result.code != ResultInfo.ALREADY_RUN) {
			
			return result;
		}
		
		if (fee > 0) {
			//收取债权转让服务费交易订单号
			String feeOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.TRANSFER);
			
			//组装参数
			Map<String, String> feeReqParams = fyPaymentService.transferBmu(sellCustId, FyConsts.mchnt_name, fee, feeOrderNo, FyPayType.MERCHANTANDPERSIONTRANSFER.value);
			
			//打印提交日志
			fyPaymentService.printRequestData("", userId, serviceOrderNo, feeOrderNo, ServiceType.TRANSFER, 
					FyPayType.MERCHANTANDPERSIONTRANSFER, feeReqParams, BaseController.getBaseURL() + FyConsts.debtTransferRepair);
			
			//提交表单
			String feeXmlData = HttpUtil.postMethod(FyConsts.post_url + FyConsts.transferBmu, feeReqParams, "UTF-8");
			
			//将xml转换为map
			Map<String, String> feeCbParams = FyUtils.parseXmlToMap(feeXmlData);
			
			//打印回调日志信息
			fyPaymentService.printCallBackData("债权转让服务费", feeCbParams, ServiceType.TRANSFER, FyPayType.MERCHANTANDPERSIONTRANSFER);
			
			//签名校验、状态码判断、防止重单
			result = FyUtils.checkSign(feeCbParams, "债权转让服务费", FyPayType.MERCHANTANDPERSIONTRANSFER.value, FyPayType.MERCHANTANDPERSIONTRANSFER);
			
			if (result.code < 1 && result.code != ResultInfo.ALREADY_RUN) {
				
				return result;
			}
		}
		
		//债权转让请求第三方后平台业务处理
		result = fyPaymentCallBackService.debtTransfer(cbParams);
				
		return result;
	}

	@Override
	public ResultInfo autoInvestSignature(int client, String serviceOrderNo,
			long userId, Object... obj) {
		//富友无该接口故空实现，富友自动投标无需经过此步骤
		return null;
	}

	@Override
	public ResultInfo autoInvest(int client, int investType,
			String serviceOrderNo, long userId, t_bid bid, double investAmt,
			Object... obj) {
		ResultInfo result = new ResultInfo();
		
		//查询理财人信息
		t_user_fund investUserInfo = userFundService.queryUserFundByUserId(userId);
		
		if (investUserInfo == null) {
			result.code = -1;
			result.msg = "查询理财人资金信息失败";
			
			return result;
		}
		
		//理财人第三方托管账号
		String outCusId = investUserInfo.payment_account;
		
		//查询借款人信息
		t_user_fund loanUserInfo = userFundService.queryUserFundByUserId(bid.user_id);
		
		if (loanUserInfo == null) {
			result.code = -1;
			result.msg = "查询借款人资金信息失败";
			
			return result;
		}
		
		//借款人第三方托管账号
		String inCusId = loanUserInfo.payment_account;
		
		//交易订单号
		String orderNo = serviceOrderNo;
		
		//组装参数
		Map<String, String> reqParams = fyPaymentService.preAuth(outCusId, inCusId, investAmt, orderNo, FyPayType.AUTOTENDER.value);
		
		//备注参数（全部以“r_”开头）
		Map<String, String> inpParams = new LinkedHashMap<String, String>();
		inpParams.putAll(reqParams);
		inpParams.put(RemarkPramKey.USER_ID, userId + "");
		inpParams.put(RemarkPramKey.BID_ID, bid.id + "");
		inpParams.put(RemarkPramKey.INVEST_AMT, investAmt + "");
		inpParams.put(RemarkPramKey.CLIENT, client + "");
		inpParams.put(RemarkPramKey.INVEST_TYPE, investType + "");
		
		//打印提交日志
		fyPaymentService.printRequestData("", userId, serviceOrderNo, 
				orderNo, ServiceType.AUTO_INVEST, FyPayType.AUTOTENDER, inpParams, BaseController.getBaseURL() + FyConsts.autoInvestRepair);
		
		//提交表单
		String xmlData = HttpUtil.postMethod(FyConsts.post_url + FyConsts.preAuth, reqParams, "UTF-8");
		
		//将xml转换为map
		Map<String, String> cbParams = FyUtils.parseXmlToMap(xmlData);
		
		//打印回调日志信息
		fyPaymentService.printCallBackData("自动投标", cbParams, ServiceType.AUTO_INVEST, FyPayType.AUTOTENDER);
		
		//签名校验、状态码判断、防止重单
		result = FyUtils.checkSign(cbParams, "自动投标", FyPayType.AUTOTENDER.value, FyPayType.AUTOTENDER);
		
		if (result.code < 1 && result.code != ResultInfo.ALREADY_RUN) {
			
			return result;
		}
		
		//自动投标请求第三方后平台业务处理
		result = fyPaymentCallBackService.autoInvest(cbParams);
		
		return result;
	}
	
	@Override
	public ResultInfo queryPersionInformation(int client,
			String serviceOrderNo, long userId, String mobile, String idNumber,
			String bankAcct, Object... obj) {
		ResultInfo result = new ResultInfo();
		
		//流水号(传递给富友的，若是单一业务则流水号直接使用平台的业务订单号)
		String orderNo = serviceOrderNo;
		
		//富友需要的参数组装
		Map<String, String> reqParams = fyPaymentService.queryPersionInformation(orderNo, mobile, idNumber, bankAcct);
		
		//用于入库的map
		LinkedHashMap<String, String> inpParams = new LinkedHashMap<String, String>();
		inpParams.putAll(reqParams);
		inpParams.put(RemarkPramKey.USER_ID, userId + "");
		inpParams.put(RemarkPramKey.USER_MOBILE, mobile);
		inpParams.put("r_user_id_number", idNumber);
		inpParams.put("r_user_bank_acct", bankAcct);
		
		//打印提交的数据并入库
		fyPaymentService.printRequestData("", userId, serviceOrderNo, orderNo, ServiceType.QUERY_PERSION_INFORMATION, FyPayType.QUERYPERSIONINFORMATION, 
				inpParams, "");
		
		//向富友提交表单
		String xmlData = HttpUtil.postMethod(FyConsts.post_url + FyConsts.queryPersionInformation, reqParams, "UTF-8");
		
		//将获得的xml转换为map
		Map<String, String> cbParams = FyUtils.parseXmlToMap(xmlData);
		
		//打印回调的数据并入库
		fyPaymentService.printCallBackData("查询用户信息", cbParams, ServiceType.QUERY_PERSION_INFORMATION, FyPayType.QUERYPERSIONINFORMATION);
		
		//签名校验、状态码判断、防止重单
		result = FyUtils.checkSign(cbParams, "查询用户信息", FyPayType.QUERYPERSIONINFORMATION.value, FyPayType.QUERYPERSIONINFORMATION);
		
		if (result.code < 1 && result.code != ResultInfo.ALREADY_RUN) {
			
			return result;
		}
		
		result = fyPaymentCallBackService.queryPersionInformation(cbParams);
		
		return result;
	}

	@Override
	public ResultInfo fastRecharge(int client, String serviceOrderNo,
			long userId, double rechargeAmt, Object... obj) {
		ResultInfo result = new ResultInfo();
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		if (userFund == null) {
			result.code = -1;
			result.msg = "查询用户信息失败";
			
			return result;
		}
		
		//交易订单号
		String orderNo = serviceOrderNo;
		
		//拼装参数
		Map<String, String> reqParams = new LinkedHashMap<String, String>();
		//构建发送给第三方的表单
		String html = "";
		
		reqParams = fyPaymentService.fastRecharge(userFund.payment_account, rechargeAmt, orderNo);
		
		if (client == Client.ANDROID.code || client == Client.IOS.code || client == Client.WECHAT.code) {
			html = FyUtils.createHtml(reqParams, FyConsts.post_url + FyConsts.appFastRecharge);
		} else {
			html = FyUtils.createHtml(reqParams, FyConsts.post_url + FyConsts.fastRecharge);
		}
		
		//备注参数（全部以"r_"开头）
		reqParams.put(RemarkPramKey.USER_ID, userId + "");
		reqParams.put(RemarkPramKey.RECHARGE_AMT, rechargeAmt + "");
		reqParams.put(RemarkPramKey.CLIENT, client + "");
		
		//打印提交日志
		fyPaymentService.printRequestData("", userId, serviceOrderNo, orderNo, ServiceType.RECHARGE, FyPayType.FASTSAVE, 
				reqParams, BaseController.getBaseURL() + FyConsts.fastRechargeRepair);
		
		//如果是app端发起的请求则将构建的表单交由app提交
		if (Client.isAppEnum(client)) {
			result.code = 1;
			result.msg = "";
			result.obj = html;
			
			return result;
		}
		
		//提交表单
		FyPaymentRequestCtrl.getInstance().submitForm(html, client);
		
		return result;
	}

	@Override
	public ResultInfo changeUserMobile(int client, String serviceOrderNo,
			long userId, String acct, Object... obj) {
		ResultInfo result = new ResultInfo();
		
		//流水号(传递给富友的，若是单一业务则流水号直接使用平台的业务订单号)
		String orderNo = serviceOrderNo;
		
		//富友需要的参数组装
		Map<String, String> reqParams = fyPaymentService.queryChangeUserMobile(orderNo, acct);
		
		//构建发送给第三方的表单
		String html = FyUtils.createHtml(reqParams, FyConsts.post_url + FyConsts.changeUserMobile);
		
		//备注参数（全部以"r_"开头）
		reqParams.put(RemarkPramKey.USER_ID, userId + "");
		reqParams.put(RemarkPramKey.PAYMENT_ACCOUNT, acct);
		reqParams.put(RemarkPramKey.CLIENT, client + "");
		
		//打印提交的数据并入库
		fyPaymentService.printRequestData("", userId, serviceOrderNo, orderNo, ServiceType.CHANGE_USER_MOBILE, FyPayType.CHANGEUSERMOBILE, 
				reqParams, "");
		
		//如果是app端发起的请求则将构建的表单交由app进行提交
		if (Client.isAppEnum(client)) {
			result.code = 1;
			result.msg = "";
			result.obj = html;
			
			return result;
		}
		
		//提交表单(浏览器页面跳转至富友)
		FyPaymentRequestCtrl.getInstance().submitForm(html, client);
		
		return result;
	}
	
	@Override
	public ResultInfo queryCztx(int client, String serviceOrderNo, t_payment_request pr, Object... obj) {
		ResultInfo result = new ResultInfo();
		
		//流水号(传递给富友的，若是单一业务则流水号直接使用平台的业务订单号)
		String orderNo = serviceOrderNo;
		
		//查询用户资金信息
		t_user_fund userFund = userFundService.queryUserFundByUserId(pr.user_id);
		
		if (userFund == null){
			result.code = -1;
			result.msg = "查询用户资金信息失败";
			
			return result;
		}
		
		String cust_no = userFund.payment_account;//待查询的用户帐号
		String txn_ssn = pr.mark; //待查询的交易流水号
		
		if(StringUtils.isBlank(txn_ssn) || StringUtils.isBlank(cust_no)){
			result.code = -1;
			result.msg = "查询流水号或用户为空";
			return result;
		}
		
		FyPayType fyPayType = FyPayType.getEnum(pr.getPay_type_code());
		
		if (StringUtils.isBlank(fyPayType.busiTp)) {
			result.code = -1;
			result.msg = "该接口不支持查询";
			return result;
		}
		
		//富友需要的参数组装
		Map<String, String> reqParams = fyPaymentService.queryCztx(orderNo, fyPayType.busiTp, txn_ssn, cust_no);
		
		//用于入库的map
		LinkedHashMap<String, String> inpParams = new LinkedHashMap<String, String>();
		inpParams.putAll(reqParams);
		inpParams.put("r_request_id", pr.id+"");
		
		//打印提交的数据并入库
		fyPaymentService.printRequestData("", pr.user_id, serviceOrderNo, orderNo, ServiceType.QUERYTXN, FyPayType.QUERYCZTX, 
				inpParams, "");
		
		//向富友提交表单
		String xmlData = HttpUtil.postMethod(FyConsts.post_url + FyConsts.querycztxAction, reqParams, "UTF-8");
		
		//将获得的xml转换为map
		Map<String, String> cbParams = FyUtils.parseXmlToMap(xmlData);
		
		//打印回调的数据并入库
		fyPaymentService.printCallBackData("查询充值提现信息", cbParams, ServiceType.QUERYTXN, FyPayType.QUERYCZTX);
		
		//签名校验、状态码判断、防止重单
		result = FyUtils.checkSign(cbParams, "查询充值提现信息", FyPayType.QUERYCZTX.value, FyPayType.QUERYCZTX);
		
		if (result.code < 1 && result.code != ResultInfo.ALREADY_RUN) {
			
			return result;
		}
		
		//是否回调预授权合同号
		boolean flag = false;
		
		if (FyPayType.INITIATIVETENDER.equals(fyPayType)||FyPayType.AUTOTENDER.equals(fyPayType))
			flag = true;
		
		Map<String, String> maps = fyPaymentService.findParams(cbParams, result, flag);
		
		if (result.code < 1) {
			
			return result;
		}
	
		//打印查询的数据并入库
		fyPaymentService.printQueryData("保存查询的充值提现信息", maps, fyPayType);
				
		result.code = 1;
		result.obj = maps;
		return result;
	}

	@Override
	public ResultInfo queryTxn(int client, String serviceOrderNo, t_payment_request pr, Object... obj) {
		ResultInfo result = new ResultInfo();
		
		//流水号(传递给富友的，若是单一业务则流水号直接使用平台的业务订单号)
		String orderNo = serviceOrderNo;
		
		//查询用户资金信息
		t_user_fund userFund = userFundService.queryUserFundByUserId(pr.user_id);
		
		if (userFund == null){
			result.code = -1;
			result.msg = "查询用户资金信息失败";
			
			return result;
		}
		
		String cust_no = userFund.payment_account;//待查询的用户帐号
		String txn_ssn = pr.mark; //待查询的交易流水号
		
		if(StringUtils.isBlank(txn_ssn) || StringUtils.isBlank(cust_no)){
			result.code = -1;
			result.msg = "查询流水号或用户为空";
			return result;
		}
		
		FyPayType fyPayType = FyPayType.getEnum(pr.getPay_type_code());
		
		if (fyPayType == null) {
			result.code = -1;
			result.msg = "该接口不支持查询";
			return result;
		}
		
		if (StringUtils.isBlank(fyPayType.busiTp)) {
			result.code = -1;
			result.msg = "该接口不支持查询";
			return result;
		}
		
		//富友需要的参数组装
		Map<String, String> reqParams = fyPaymentService.queryTxn(orderNo, fyPayType.busiTp, txn_ssn, cust_no);
		
		//用于入库的map
		LinkedHashMap<String, String> inpParams = new LinkedHashMap<String, String>();
		inpParams.putAll(reqParams);
		inpParams.put("r_request_id", pr.id+"");
		
		//打印提交的数据并入库
		fyPaymentService.printRequestData("", pr.user_id, serviceOrderNo, orderNo, ServiceType.QUERYTXN, FyPayType.QUERYTXN, 
				inpParams, "");
		
		//向富友提交表单
		String xmlData = HttpUtil.postMethod(FyConsts.post_url + FyConsts.queryTxnAction, reqParams, "UTF-8");
		
		//将获得的xml转换为map
		Map<String, String> cbParams = FyUtils.parseXmlToMap(xmlData);
		
		//打印回调的数据并入库
		fyPaymentService.printCallBackData("查询交易信息", cbParams, ServiceType.QUERYTXN, FyPayType.QUERYTXN);
		
		//签名校验、状态码判断、防止重单
		result = FyUtils.checkSign(cbParams, "查询交易信息", FyPayType.QUERYTXN.value, FyPayType.QUERYTXN);
		
		if (result.code < 1 && result.code != ResultInfo.ALREADY_RUN) {
			
			return result;
		}
		
		//是否回调预授权合同号
		boolean flag = false;
		
		if (FyPayType.INITIATIVETENDER.equals(fyPayType)||FyPayType.AUTOTENDER.equals(fyPayType))
			flag = true;
				
		Map<String, String> maps = fyPaymentService.findParams(cbParams, result, flag);
		
		if (result.code < 1) {
			
			return result;
		}
	
		//打印查询的数据并入库
		fyPaymentService.printQueryData("保存查询的交易信息", maps, fyPayType);
				
		result.code = 1;
		result.obj = maps;
		return result;
	}

	@Override
	public ResultInfo merchantTransfer(int client, String serviceOrderNo,
			long userId, Object... obj) {
		
		return null;
	}

	@Override
	public ResultInfo tenderCancle(int client, String serviceOrderNo,
			t_invest invest, Object... obj) {
		return null;
	}

	@Override
	public ResultInfo singleRepayment(int client, String serviceOrderNo,
			t_bill bill, List<Map<String, Double>> billInvestFeeList,
			Object... obj) {
		return null;
	}

	@Override
	public ResultInfo sendRate(int client, String serviceOrderNo,
			t_bill_invest invest, Object... obj) {
		return null;
	}

	@Override
	public ResultInfo sendSmsCode(int client, String serviceOrderNo, long userId, String cardId, BusiType busiType, String mobile,
			Object... obj) {
		return null;
	}

	@Override
	public ResultInfo userRegist(int client, String serviceOrderNo, long userId, String hfName, String realName,
			String idNumber, String mobile, String cardId, String bankId, String provId, String areaId, String smsCode,
			String smsSeq, Object... obj) {
		return null;
	}

	@Override
	public ResultInfo bosAcctActivate(int client, String serviceOrderNo, long userId, Object... obj) {
		return null;
	}

	@Override
	public ResultInfo quickBinding(int client, String serviceOrderNo, long userId, String cardId,
			String bankId, String provId, String areaId, String mobile, String smsCode, String smsSeq, String orgSmsExt, Object... obj) {
		return null;
	}

	@Override
	public ResultInfo directRecharge(int client, String serviceOrderNo, long userId, TradeType tradeType, String cardId,
			double transAmt, String smsCode, String smsSeq, String singId) {
		return null;
	}

	@Override
	public ResultInfo transfer(int client, String serviceOrderNo, long userId, double transAmt, Object... obj) {
		return null;
	}

	@Override
	public ResultInfo queryTransStat(int client, String serviceOrderNo, String queryTransType) {
		return null;
	}

	@Override
	public ResultInfo trfReconciliation(int client, Date beginDate, Date endDate, String PageNum, String PageSize) {
		return null;
	}

	@Override
	public ResultInfo reconciliation(int client, Date beginDate, Date endDate, String PageNum, String PageSize,
			String queryTransType) {
		return null;
	}

	@Override
	public ResultInfo cashReconciliation(int client, Date beginDate, Date endDate, String PageNum, String PageSize) {
		return null;
	}

	@Override
	public ResultInfo saveReconciliation(int client, Date beginDate, Date endDate, String PageNum, String PageSize) {
		return null;
	}

	@Override
	public ResultInfo autoTenderCancle(int client, String serviceOrderNo, long userId, double transAmt,
			Map<String, String> unFreezeParam, long investId) {
		return null;
	}

	@Override
	public ResultInfo corpRegister(int client, String serviceOrderNo, long userId, String usrId, String usrName,
			String instuCode, String busiCode, String taxCode, String guarType,
			Double guarCorpEarnestAmt) {
		return null;
	}

	@Override
	public ResultInfo corpRegisterQuery(int client, long userId, String busiCode) {
		return null;
	}

	@Override
	public ResultInfo usrUnFreeze(int client, long userId, String ordNo, String freezeTrxId) {
		return null;
	}

}
