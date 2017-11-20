package controllers.payment.fy;

import java.util.Map;

import com.shove.Convert;

import common.constants.Constants;
import common.constants.RemarkPramKey;
import common.constants.WxPageType;
import common.enums.Client;
import common.enums.ServiceType;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.ResultInfo;
import controllers.payment.PaymentBaseCtrl;
import fy.FyPayType;
import fy.FyPaymentCallBackService;
import fy.FyPaymentService;
import fy.FyUtils;
import models.core.entity.t_bid;
import services.core.BidService;

public class FyPaymentCallBackCtrl extends PaymentBaseCtrl{
	
	private static FyPaymentCallBackCtrl instance = null;
	
	private static BidService bidService = Factory.getService(BidService.class);
	
	private static FyPaymentService fyPaymentService = Factory.getSimpleService(FyPaymentService.class);
	
	private static FyPaymentCallBackService fyPaymentCallBackService = Factory.getSimpleService(FyPaymentCallBackService.class);

	private FyPaymentCallBackCtrl(){
		
	}
	
	public static FyPaymentCallBackCtrl getInstance(){
		if (instance == null){
			synchronized (FyPaymentCallBackCtrl.class){
				if (instance == null){
					instance = new FyPaymentCallBackCtrl();
				}
			}
		}
		
		return instance;
	}
	
	/**
	 * 充值回调
	 */
	public static void returnRecharge() {
		ResultInfo result = new ResultInfo();
		
		//获取回调参数
		Map<String, String> cbParams = params.allSimple();
		
		//打印回调日志信息
		fyPaymentService.printCallBackData("网银充值", cbParams, ServiceType.RECHARGE, FyPayType.NETSAVE);
		
		//查询托管请求备注参数
		Map<String, String> remarkParams = fyPaymentService.queryRequestParams(cbParams.get("mchnt_txn_ssn"));
		
		if (remarkParams == null) {
			flash.error("查询托管请求备注参数失败");
			
			error500();
		}
		
		//业务调用
		result = fyPaymentCallBackService.netSave(cbParams, remarkParams);
		
		//取得发起请求的客户端类型
		Client client = Client.getEnum(Convert.strToInt(remarkParams.get(RemarkPramKey.CLIENT), Client.PC.code));
		
		switch (client) {
		case PC : {
			if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
				flash.success("充值成功");
				redirect("front.account.MyAccountCtrl.homePre");
			} else {
				LoggerUtil.info(true, result.msg);
				flash.error(result.msg);
				redirect("front.account.MyAccountCtrl.rechargePre");
			}
			
			break;
		}
		case ANDROID :
		case IOS : {
			if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
				result.code = 1;
				result.msg = "充值成功";
			} else {
				LoggerUtil.info(true, result.msg);
			}
			redirectApp(result);
			
			break;
		}
		case WECHAT : {
			if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
				result.msg = "充值成功";
				redirect("wechat.WechatBaseController.toResultPage", WxPageType.PAGE_COMMUNAL_SUCC, result.msg);
			} else {
				LoggerUtil.info(true, result.msg);
				redirect("wechat.WechatBaseController.toResultPage", WxPageType.PAGE_COMMUNAL_FAIL, result.msg);
			}
			
			break;
		}
		
		default :
			
			break;
		}
	}
	
	/**
	 * 提现回调
	 */
	public static void returnWithdraw() {
		ResultInfo result = new ResultInfo();
		
		//获取回调参数
		Map<String, String> cbParams = params.allSimple();
		
		//打印回调日志信息
		fyPaymentService.printCallBackData("提现", cbParams, ServiceType.WITHDRAW, FyPayType.CASH);
		
		//查询托管请求备注参数
		Map<String, String> remarkParams = fyPaymentService.queryRequestParams(cbParams.get("mchnt_txn_ssn"));
		
		if (remarkParams == null) {
			flash.error("查询托管请求备注参数失败");
			
			error500();
		}
		
		//业务调用
		result = fyPaymentCallBackService.withdrawal(cbParams, remarkParams);
		
		//取得发起请求的客户端类型
		Client client = Client.getEnum(Convert.strToInt(remarkParams.get(RemarkPramKey.CLIENT), Client.PC.code));
		
		switch (client) {
		case PC : {
			if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
				flash.success("提现成功");
				redirect("front.account.MyAccountCtrl.homePre");
			} else {
				flash.error(result.msg);
				LoggerUtil.info(true, result.msg);  
				redirect("front.account.MyAccountCtrl.withdrawPre");	
			}
			
			break;
		}
		case ANDROID:
		case IOS : {
			if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
				result.code = 1;
				result.msg = "提现成功";
			} else {
				LoggerUtil.info(true, result.msg);
			}
			redirectApp(result);
			
			break;
		}
		case WECHAT : {
			if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
				result.msg = "提现成功";
				redirect("wechat.WechatBaseController.toResultPage", WxPageType.PAGE_COMMUNAL_SUCC, result.msg);
			} else {
				LoggerUtil.info(true, result.msg);  
				redirect("wechat.WechatBaseController.toResultPage", WxPageType.PAGE_COMMUNAL_FAIL, result.msg);
			}
			
			break;
		}
	
		default :
			
			break;
		}
	}
	
	/**
	 * 标的信息录入回调（用于后台日志补单）
	 */
	public static void returnBidCreate(){
		ResultInfo result = new ResultInfo();
		
		//获取回调参数
		Map<String, String> cbParams = params.allSimple();
		
		//打印回调日志信息
		fyPaymentService.printCallBackData("资金冻结", cbParams, ServiceType.BID_CREATE, FyPayType.USRFREEZEBG);
		
		//校验签名、状态码判断、防止重单
		result = FyUtils.checkSign(cbParams, "资金冻结", FyPayType.USRFREEZEBG.value, FyPayType.USRFREEZEBG);
		
		if (result.code < 1 && result.code != ResultInfo.ALREADY_RUN){
			
			return;
		}
		
		//标的信息录入请求第三方后平台业务处理
		result = fyPaymentCallBackService.addBidInfo(cbParams);
		
		if (result.code < 1){
			LoggerUtil.info(true, result.msg);
		}
	}
	
	/**
	 * 标的登记成功，页面跳转
	 * @param result 
	 * @param bid 
	 * @param bidCreateFrom 标的发布方式（前台/后台）
	 */
	public void addBidInfoWS(ResultInfo result, t_bid bid, int bidCreateFrom){
		if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN){
			if (bidCreateFrom == Constants.BID_CREATE_FROM_FRONT){
				//标的发布成功，数据库中才有标的信息，取出标的id
				long bidId = bidService.findIdByMerBidNo(bid.mer_bid_no, -1L);
				
				if (bidId == -1){
					LoggerUtil.info(false, "===========bid.mer_bid_no=%s;bid.product_id=%d;===========", bid.mer_bid_no, bid.product_id);
					flash.success("项目发布成功");
					redirect("front.LoanCtrl.toLoanPre");
				}
	
				redirect("front.LoanCtrl.uploadBidItemPre", bidId);
			}
			
			if (bidCreateFrom == Constants.BID_CREATE_FROM_BACK){
				flash.success("项目发布成功");
				redirect("back.risk.LoanMngCtrl.showBidPre");
			}
		} else {
			long productId = bid.product_id;
			flash.error(result.msg);
			LoggerUtil.info(true, result.msg);
			
			redirect("front.LoanCtrl.toLoanPre", productId);
		}
	}
	
	/**
	 * 用户开户回调（用于后台日志补单）
	 */
	public static void returnUserRegister(){
		ResultInfo result = new ResultInfo();
		
		//获取回调参数
		Map<String, String> cbParams = params.allSimple();
		
		//打印回调日志
		fyPaymentService.printCallBackData("用户开户", cbParams, ServiceType.REGIST, FyPayType.USERREGISTER);
		
		//签名校验、状态码判断、防止重单
		result = FyUtils.checkSign(cbParams, "用户开户", FyPayType.USERREGISTER.value, FyPayType.USERREGISTER);
		
		if (result.code < 1 && result.code != ResultInfo.ALREADY_RUN){
			
			return;
		}
		
		//用户开户请求第三方后平台业处理
		result = fyPaymentCallBackService.userRegister(cbParams);
		
		if (result.code < 1){
			LoggerUtil.info(true, result.msg);
		}
	}
	
	/**
	 * 放款回调（用于后台日志补单）
	 */
	public static void returnBidSuditSucc(){
		ResultInfo result = new ResultInfo();
		
		//获取回调参数
		Map<String, String> cbParams = params.allSimple();
		
		//打印回调日志信息
		fyPaymentService.printCallBackData("借款服务费", cbParams, ServiceType.TRANSFER, FyPayType.MERCHANTANDPERSIONTRANSFER);
		
		//校验签名、状态码判断、防止重单
		result = FyUtils.checkSign(cbParams, "借款服务费", FyPayType.MERCHANTANDPERSIONTRANSFER.value, FyPayType.MERCHANTANDPERSIONTRANSFER);
		
		if (result.code < 1 && result.code != ResultInfo.ALREADY_RUN){
			
			return;
		}
		
		//放款请求第三方后平台业务处理
		result = fyPaymentCallBackService.bidSuditSucc(cbParams);
		
		if (result.code < 1){
			LoggerUtil.info(true, result.msg);
		}
	}
	
	/**自动投标补单（用于后台日志补单） */
	public static void returnAutoInvest() {
		ResultInfo result = new ResultInfo();
		
		//获取回调参数
		Map<String, String> cbParams = params.allSimple();
		
		//打印回调日志信息
		fyPaymentService.printCallBackData("自动投标", cbParams, ServiceType.AUTO_INVEST, FyPayType.AUTOTENDER);
		
		//签名校验、状态码判断、防止重单
		result = FyUtils.checkSign(cbParams, "自动投标", FyPayType.AUTOTENDER.value, FyPayType.AUTOTENDER);
		
		if (result.code < 1 && result.code != ResultInfo.ALREADY_RUN) {
			
			return;
		}
		
		//自动投标请求第三方后平台业务处理
		result = fyPaymentCallBackService.autoInvest(cbParams);
		
		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
		}
	}
	
	/**本息垫付后还款补单 （用于后台日志补单）*/
	public static void returnAdvanceRepayment() {
		ResultInfo result = new ResultInfo();
		
		//获取回调参数
		Map<String, String> cbParams = params.allSimple();
		
		//打印回调日志信息
		fyPaymentService.printCallBackData("本息垫付后还款", cbParams, ServiceType.REPAYMENT_AFER_ADVANCE, FyPayType.MERCHANTANDPERSIONTRANSFER);
		
		//签名校验、状态码判断、防止重单
		result = FyUtils.checkSign(cbParams, "本息垫付后还款", FyPayType.MERCHANTANDPERSIONTRANSFER.value, FyPayType.MERCHANTANDPERSIONTRANSFER);
		
		if (result.code < 1 && result.code != ResultInfo.ALREADY_RUN) {
			
			return;
		}
		
		//本息垫付后还款请求第三方后平台业务处理
		result = fyPaymentCallBackService.advanceRepayment(cbParams);
		
		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
		}
	}
	
	/**手动投标补单（用于后台日志补单） */
	public static void returnInvest() {
		ResultInfo result = new ResultInfo();
		
		//获取回调参数
		Map<String, String> cbParams = params.allSimple();
		
		//打印回调日志信息
		fyPaymentService.printCallBackData("主动投标", cbParams, ServiceType.INVEST, FyPayType.INITIATIVETENDER);
		
		//签名校验、状态码判断、防止重单
		result = FyUtils.checkSign(cbParams, "主动投标", FyPayType.INITIATIVETENDER.value, FyPayType.INITIATIVETENDER);
		
		if (result.code < 1 && result.code != ResultInfo.ALREADY_RUN) {
			
			return;
		}
		
		//手动投标请求第三方后平台业务处理
		result = fyPaymentCallBackService.invest(cbParams);
		
		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
		}
	}
	
	/**
	 * 快速充值回调
	 */
	public static void returnFastRecharge() {
		ResultInfo result = new ResultInfo();
		
		//获取回调参数
		Map<String, String> cbParams = params.allSimple();
		
		//打印回调日志信息
		fyPaymentService.printCallBackData("快速充值", cbParams, ServiceType.RECHARGE, FyPayType.FASTSAVE);
		
		//查询托管请求备注参数
		Map<String, String> remarkParams = fyPaymentService.queryRequestParams(cbParams.get("mchnt_txn_ssn"));
		
		if (remarkParams == null) {
			flash.error("查询托管请求备注参数失败");
			
			error500();
		}
		
		//业务调用
		result = fyPaymentCallBackService.fastSave(cbParams, remarkParams);
		
		//取得发起请求的客户端类型
		Client client = Client.getEnum(Convert.strToInt(remarkParams.get(RemarkPramKey.CLIENT), Client.PC.code));
		
		switch (client) {
		case PC : {
			if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
				flash.success("充值成功");
				redirect("front.account.MyAccountCtrl.homePre");
			} else {
				LoggerUtil.info(true, result.msg);
				flash.error(result.msg);
				redirect("front.account.MyAccountCtrl.fastRechargePre");
			}
			
			break;
		}
		case ANDROID :
		case IOS : {
			if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
				result.code = 1;
				result.msg = "充值成功";
			} else {
				LoggerUtil.info(true, result.msg);
			}
			redirectApp(result);
			
			break;
		}
		case WECHAT : {
			if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
				result.msg = "充值成功";
				redirect("wechat.WechatBaseController.toResultPage", WxPageType.PAGE_COMMUNAL_SUCC, result.msg);
			} else {
				LoggerUtil.info(true, result.msg);
				redirect("wechat.WechatBaseController.toResultPage", WxPageType.PAGE_COMMUNAL_FAIL, result.msg);
			}
			
			break;
		}
		
		default :
			
			break;
		}
	}
	
	/**
	 * 第三方账户管理系统充值通知
	 * 在第三方账户管理系统上进行充值操作，第三方发起回调给平台
	 */
	public static void thirdPartyRechargeNotify() {
		//获取回调参数
		Map<String, String> cbParams = params.allSimple();
		
		if (cbParams == null || cbParams.size() <= 0) {
			
			return ;
		}
		
		//打印回调参数
		fyPaymentService.printCallBackData("充值通知", cbParams, ServiceType.RECHARGE, FyPayType.NETSAVE);
		
		String mchnt_cd = cbParams.get("mchnt_cd"); //商户代码
		String mchnt_txn_ssn = cbParams.get("mchnt_txn_ssn"); //流水号
		String signature = cbParams.get("signature"); //签名数据
		
		//查询托管请求参数
		Map<String, String> remarkParams = fyPaymentService.queryRequestParams(cbParams.get("mchnt_txn_ssn"));
		
		if (remarkParams != null) {
			//响应给第三方，平台已成功接收请求
			renderText(FyUtils.notifyThirdParty("0000", mchnt_cd, mchnt_txn_ssn, signature));
		} else {
			//进行平台业务处理
			ResultInfo result = fyPaymentCallBackService.thirdPartyRechargeDispose(cbParams);
			
			if (result.code >= 1) {
				//业务执行成功，响应给第三方，平台已成功接收请求
				renderText(FyUtils.notifyThirdParty("0000", mchnt_cd, mchnt_txn_ssn, signature));
			}
		}
	}
	
	/**
	 * 第三方账户管理系统提现通知
	 * 在第三方账户管理系统上进行提现操作，第三方发起回调给平台
	 */
	public static void thirdPartyWithdrawNotify() {
		//获取回调参数
		Map<String, String> cbParams = params.allSimple();
		
		if (cbParams == null || cbParams.size() <= 0) {
			
			return ;
		}
		
		//打印回调参数
		fyPaymentService.printCallBackData("提现通知", cbParams, ServiceType.WITHDRAW, FyPayType.CASH);
		
		String mchnt_cd = cbParams.get("mchnt_cd"); //商户代码
		String mchnt_txn_ssn = cbParams.get("mchnt_txn_ssn"); //流水号
		String signature = cbParams.get("signature"); //签名数据
		
		//查询托管请求参数
		Map<String, String> remarkParams = fyPaymentService.queryRequestParams(cbParams.get("mchnt_txn_ssn"));
		
		if (remarkParams != null) {
			//响应给第三方，平台已成功接收请求
			renderText(FyUtils.notifyThirdParty("0000", mchnt_cd, mchnt_txn_ssn, signature));
		} else {
			//进行平台业务处理
			ResultInfo result = fyPaymentCallBackService.thirdPartyWithdrawDispose(cbParams);
			
			if (result.code >= 1) {
				//业务执行成功，响应给第三方，平台已成功接收请求
				renderText(FyUtils.notifyThirdParty("0000", mchnt_cd, mchnt_txn_ssn, signature));
			}
		}
	}
	
	/**
	 * 提现退票通知
	 */
	public static void thirdPartyWithdrawRefundNotify() {
		//获取回调参数
		Map<String, String> cbParams = params.allSimple();
		
		if (cbParams == null || cbParams.size() <= 0) {
			
			return ;
		}
		
		//打印回调参数
		fyPaymentService.printCallBackData("提现退票通知", cbParams, ServiceType.WITHDRAW_REFUND, FyPayType.WITHDRAW_REFUND);
		
		String mchnt_cd = cbParams.get("mchnt_cd"); //商户代码
		String mchnt_txn_ssn = cbParams.get("mchnt_txn_ssn"); //流水号
		String signature = cbParams.get("signature"); //签名数据
		
		//查询托管请求参数
		Map<String, String> remarkParams = fyPaymentService.queryRequestParams(cbParams.get("mchnt_txn_ssn"));
		
		if (remarkParams != null) {
			//响应给第三方，平台已成功接收请求
			renderText(FyUtils.notifyThirdParty("0000", mchnt_cd, mchnt_txn_ssn, signature));
		} else {
			//进行平台业务处理
			ResultInfo result = fyPaymentCallBackService.thirdPartyWithdrawRefundDispose(cbParams);
			
			if (result.code >= 1) {
				//业务执行成功，响应给第三方，平台已成功接收请求
				renderText(FyUtils.notifyThirdParty("0000", mchnt_cd, mchnt_txn_ssn, signature));
			}
		}
	}
	
	/**
	 * 第三方账户管理系统用户修改信息通知
	 * 在第三方账户管理系统上进行修改信息操作，第三方发起回调给平台
	 */
	public static void thirdPartyUserModifyInfoNotify() {
		//获取回调参数
		Map<String, String> cbParams = params.allSimple();
		
		if (cbParams == null || cbParams.size() <= 0) {
			
			return ;
		}
		
		//打印回调参数
		fyPaymentService.printCallBackData("用户修改信息通知", cbParams, ServiceType.USER_MODIFY_INFO, FyPayType.USER_MODIFY_INFO);
		
		String mchnt_cd = cbParams.get("mchnt_cd"); //商户代码
		String mchnt_txn_ssn = cbParams.get("mchnt_txn_ssn"); //流水号
		String signature = cbParams.get("signature"); //签名数据
		
		//查询托管请求参数
		Map<String, String> remarkParams = fyPaymentService.queryRequestParams(cbParams.get("mchnt_txn_ssn"));
		
		if (remarkParams != null) {
			//响应给第三方，平台已成功接收请求
			renderText(FyUtils.notifyThirdParty("0000", mchnt_cd, mchnt_txn_ssn, signature));
		} else {
			//进行平台业务处理
			ResultInfo result = fyPaymentCallBackService.thirdPartyUserModifyInfoDispose(cbParams);
			
			if (result.code >= 1) {
				//业务执行成功，响应给第三方，平台已成功接收请求
				renderText(FyUtils.notifyThirdParty("0000", mchnt_cd, mchnt_txn_ssn, signature));
			}
		}
	}
	
	/**
	 * 第三方账户管理系统用户注销通知
	 * 在第三方账户管理系统上进行注销操作，第三方发起回调给平台
	 */
	public static void thirdPartyUserLogoutNotify() {
		//获取回调参数
		Map<String, String> cbParams = params.allSimple();
		
		if (cbParams == null || cbParams.size() <= 0) {
			
			return ;
		}
		
		//打印回调参数
		fyPaymentService.printCallBackData("用户注销通知", cbParams, ServiceType.USER_LOGOUT, FyPayType.USER_LOGOUT);
		
		String mchnt_cd = cbParams.get("mchnt_cd"); //商户代码
		String mchnt_txn_ssn = cbParams.get("mchnt_txn_ssn"); //流水号
		String signature = cbParams.get("signature"); //签名数据
		
		//查询托管请求参数
		Map<String, String> remarkParams = fyPaymentService.queryRequestParams(cbParams.get("mchnt_txn_ssn"));
		
		if (remarkParams != null) {
			//响应给第三方，平台已成功接收请求
			renderText(FyUtils.notifyThirdParty("0000", mchnt_cd, mchnt_txn_ssn, signature));
		} else {
			//进行平台业务处理
			ResultInfo result = fyPaymentCallBackService.thirdPartyUserLogoutDispose(cbParams);
			
			if (result.code >= 1) {
				//业务执行成功，响应给第三方，平台已成功接收请求
				renderText(FyUtils.notifyThirdParty("0000", mchnt_cd, mchnt_txn_ssn, signature));
			}
		}
	}
}
