package fy;

import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shove.Convert;

import common.constants.RemarkPramKey;
import common.enums.Client;
import common.enums.ServiceType;
import common.utils.Factory;
import common.utils.HttpUtil;
import common.utils.JPAUtil;
import common.utils.LoggerUtil;
import common.utils.OrderNoUtil;
import common.utils.ResultInfo;
import controllers.common.BaseController;
import models.common.entity.t_user_fund;
import models.core.entity.t_bid;
import play.Logger;
import services.common.BankCardUserService;
import services.common.ConversionService;
import services.common.UserFundService;
import services.common.UserInfoService;
import services.common.UserService;
import services.core.BidService;
import services.core.BillService;
import services.core.DebtService;
import services.core.InvestService;

public class FyPaymentCallBackService extends FyPaymentService{
	
	protected static UserFundService userFundService = Factory.getService(UserFundService.class);
	
	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	
	protected static FyPaymentService fyPaymentService = Factory.getSimpleService(FyPaymentService.class);
	
	protected static BidService bidService = Factory.getService(BidService.class);
	
	protected static InvestService investService = Factory.getService(InvestService.class);
	
	protected static BillService billService = Factory.getService(BillService.class);
	
	protected static DebtService debtService = Factory.getService(DebtService.class);
	
	protected static ConversionService conversionService = Factory.getService(ConversionService.class);
	
	protected static BankCardUserService bankCardUserService = Factory.getService(BankCardUserService.class);
	
	protected static UserService userService = Factory.getService(UserService.class);
	
	/**
	 * 充值回调业务处理
	 * @param cbParams 第三方返回的数据
	 * @param remarkParams 发起请求时入库参数
	 * @return
	 */
	public ResultInfo netSave(Map<String, String> cbParams, Map<String, String> remarkParams) {
		ResultInfo result = new ResultInfo();
		
		int clientInt = Convert.strToInt(remarkParams.get(RemarkPramKey.CLIENT), Client.PC.code);
		
		if (clientInt == Client.ANDROID.code || clientInt == Client.IOS.code || clientInt == Client.WECHAT.code) {
			//校验第三方返回的表单数据
			result = FyUtils.checkFormSign(cbParams, "快捷充值", FyPayType.NETSAVE, "amt", "login_id", "mchnt_cd", "mchnt_txn_ssn", "resp_code");
		} else {
			//校验第三方返回的表单数据
			result = FyUtils.checkFormSign(cbParams, "网银充值", FyPayType.NETSAVE, "amt", "login_id", "mchnt_cd", "mchnt_txn_ssn", "rem", "resp_code");
		}
		
		if (result.code < 1) {
			
			return result;
		}
		
		long userId = Long.parseLong(remarkParams.get(RemarkPramKey.USER_ID));
		double rechargeAmt = Double.parseDouble(remarkParams.get(RemarkPramKey.RECHARGE_AMT));
		String serviceOrderNo = remarkParams.get(RemarkPramKey.SERVICE_ORDER_NO);
		
		result = userFundService.doRecharge(userId, rechargeAmt, serviceOrderNo);
		
		if (result.code < 1) {
			
			return result;
		}
		
		result.code = 1;
		result.msg = "充值成功";
		
		return result;
	}
	
	/**
	 * 提现回调业务处理
	 * @param cbParams 第三方返回的数据
	 * @param remarkParams 发起请求时入库参数
	 * @return
	 */
	public ResultInfo withdrawal(Map<String, String> cbParams, Map<String, String> remarkParams) {
		ResultInfo result = new ResultInfo();
		
		//校验第三方返回的表单数据
		result = FyUtils.checkFormSign(cbParams, "提现", FyPayType.CASH, "amt", "login_id", "mchnt_cd", "mchnt_txn_ssn", "resp_code");
		
		if (result.code < 1) {
			
			return result;
		}
		
		long userId = Long.parseLong(remarkParams.get(RemarkPramKey.USER_ID));
		double withdrawalAmt = Double.parseDouble(remarkParams.get(RemarkPramKey.WITHDRAWAL_AMT));
		String serviceOrderNo = remarkParams.get(RemarkPramKey.SERVICE_ORDER_NO);
		
		/*
		 * 第三方收取的手续费扣除模式：false-内扣；true-外扣
		 */
		boolean chargeMode = true;  //富友不支持内扣，默认为外扣
		
		result = userFundService.doWithdrawal(userId, withdrawalAmt, 0.00D, 0.00D, serviceOrderNo, chargeMode);
		
		if (result.code < 1) {
			
			return result;
		}
		
		result.code = 1;
		result.msg = "提现成功";
		
		return result;
	}
	
	/**
	 * 标的信息录入请求第三方后平台业务处理
	 * @param cbParams 第三方返回的数据
	 * @return
	 */
	public ResultInfo addBidInfo(Map<String, String> cbParams){
		ResultInfo result = new ResultInfo();
		
		//查询请求时入库的参数
		Map<String, String> remarkParams = this.queryRequestParams(cbParams.get("mchnt_txn_ssn"));
		
		if (remarkParams == null){
			result.code = -1;
			result.msg = "查询托管请求备注参数失败";
			
			return result;
		}
		
		t_bid bid = new Gson().fromJson(remarkParams.get(RemarkPramKey.BID), t_bid.class);
		String serviceOrderNo = remarkParams.get(RemarkPramKey.SERVICE_ORDER_NO);
		
		result = bidService.doCreateBid(bid, serviceOrderNo);
		
		return result;
	}
	
	/**
	 * 主动投标请求第三方后平台业务处理
	 * @param cbParams 第三方返回的数据
	 * @return
	 */
	public ResultInfo invest(Map<String, String> cbParams){
		ResultInfo result = new ResultInfo();
		
		//查询请求时入库的参数
		Map<String, String> remarkParams = this.queryRequestParams(cbParams.get("mchnt_txn_ssn"));
		
		if (remarkParams == null){
			result.code = -1;
			result.msg = "查询托管请求备注参数失败";
			
			return result;
		}
		
		//投资时第三方返回的流水号（可以理解为投标冻结订单号）
		String contract_no = cbParams.get("contract_no");

		long userId = Long.parseLong(remarkParams.get(RemarkPramKey.USER_ID));
		long bidId = Long.parseLong(remarkParams.get(RemarkPramKey.BID_ID));
		double investAmt = Double.parseDouble(remarkParams.get(RemarkPramKey.INVEST_AMT));
		int client = Integer.parseInt(remarkParams.get(RemarkPramKey.CLIENT));
		int investType = Integer.parseInt(remarkParams.get(RemarkPramKey.INVEST_TYPE));
		String serviceOrderNo = remarkParams.get(RemarkPramKey.SERVICE_ORDER_NO);
		double redPacketAmt = Convert.strToDouble(remarkParams.get(RemarkPramKey.RED_PACKET_AMT), 0.0);
		long redPacketId = Convert.strToLong(remarkParams.get(RemarkPramKey.RED_PACKET_ID), 0L);
		double cashAmt = Convert.strToDouble(remarkParams.get(RemarkPramKey.CASH_AMT), 0.0);
		long cashId = Convert.strToLong(remarkParams.get(RemarkPramKey.CASH_ID), 0L);
		
		//投标（执行）
		result = investService.doInvest(userId, bidId, investAmt, serviceOrderNo, contract_no, 
				client, investType, redPacketId, redPacketAmt, cashId, cashAmt, 0L, 0.0);
		
		if (result.code < 1) {
			LoggerUtil.info(true, "投资回调处理业务失败-事务必须回滚");
			if (result.code == ResultInfo.OVER_BID_AMOUNT) { //超标处理
				result = doUserUnFreeze(userId, bidId, investAmt, ServiceType.USER_UNFREEZE, serviceOrderNo, contract_no);
				
				if (result.code > 0 || result.code != ResultInfo.ALREADY_RUN) { //解冻成功
					
					result.code = ResultInfo.OVER_BID_AMOUNT;
					result.msg = "投标失败，本次投资已超额";
					
					JPAUtil.transactionCommit(); //资金已经解冻，事务提交
				}
				
				return result;
			}
			
			return result;
		}
		
		return result;
	}
	
	/**
	 * 本息垫付后还款请求第三方后平台业务处理
	 * @param cbParams 第三方返回的数据
	 * @return
	 */
	public ResultInfo advanceRepayment(Map<String, String> cbParams){
		ResultInfo result = new ResultInfo();
		
		String requestMark = cbParams.get("mchnt_txn_ssn");

		Map<String, String> remarkParams = this.queryRequestParams(requestMark);
		
		if (remarkParams == null){
			result.code = -1;
			result.msg = "查询托管请求备注参数失败";
			
			return result;
		}
		
		long billId = Long.parseLong(remarkParams.get(RemarkPramKey.BILL_ID));
		double loanOverdueFine = Double.parseDouble(remarkParams.get(RemarkPramKey.LOAN_OVERDUE_FINE));
		String serviceOrderNo = remarkParams.get(RemarkPramKey.SERVICE_ORDER_NO);
		
		result = billService.doAdvanceRepayment(serviceOrderNo, billId, loanOverdueFine);
		
		return result;
	}
	
	/**
	 * 债权转让请求第三方后平台业务处理
	 * @param cbParams 第三方返回的数据
	 * @return
	 */
	public ResultInfo debtTransfer(Map<String, String> cbParams){
		ResultInfo result = new ResultInfo();
		
		String requestMark = cbParams.get("mchnt_txn_ssn");

		Map<String, String> remarkParams = this.queryRequestParams(requestMark);
		
		if (remarkParams == null){
			result.code = -1;
			result.msg = "查询托管请求备注参数失败";
			
			return result;
		}
		
		long debtId = Long.valueOf(remarkParams.get(RemarkPramKey.DEBT_ID));
		String serviceOrderNo = remarkParams.get(RemarkPramKey.SERVICE_ORDER_NO);
		double debtFee = Double.valueOf(remarkParams.get(RemarkPramKey.DEBT_FEE));
		long toUserId = Long.valueOf(remarkParams.get(RemarkPramKey.TO_USER_ID));
		
		result = debtService.doDebtTransfer(serviceOrderNo, debtId, toUserId, debtFee);
		
		return result;
	}
	
	/**
	 * 还款请求第三方后平台业务处理
	 * @param cbParams 第三方返回的数据
	 * @return
	 */
	public ResultInfo repayment(Map<String, String> cbParams){
		ResultInfo result = new ResultInfo();
		
		String requestMark = cbParams.get("mchnt_txn_ssn");

		Map<String, String> remarkParams = this.queryRequestParams(requestMark);
		
		if (remarkParams == null){
			result.code = -1;
			result.msg = "查询托管请求备注参数失败";
			
			return result;
		}
		
		long billId = Long.parseLong(remarkParams.get(RemarkPramKey.BILL_ID));
		List<Map<String, Double>> billInvestFeeList = new Gson().fromJson(remarkParams.get(RemarkPramKey.BILL_INVEST_FEE_LIST),
					new TypeToken<List<Map<String, Double>>>(){}.getType());
		
		String serviceOrderNo = remarkParams.get(RemarkPramKey.SERVICE_ORDER_NO);
		
		result = billService.doRepayment(billId, billInvestFeeList, serviceOrderNo);

		return result;
	}
	
	/**
	 * 本金垫付请求第三方后平台业务处理
	 * @param cbParams 第三方返回的数据
	 * @return
	 */
	@Deprecated
	public ResultInfo advance(Map<String, String> cbParams){
		ResultInfo result = new ResultInfo();
		
		String requestMark = cbParams.get("mchnt_txn_ssn");

		Map<String, String> remarkParams = this.queryRequestParams(requestMark);
		
		if (remarkParams == null){
			result.code = -1;
			result.msg = "查询托管请求备注参数失败";
			
			return result;
		}
		
		long billId = Long.parseLong(remarkParams.get(RemarkPramKey.BILL_ID));
		List<Map<String, Double>> billInvestFeeList = new Gson().fromJson(remarkParams.get(RemarkPramKey.BILL_INVEST_FEE_LIST),
					new TypeToken<List<Map<String, Double>>>(){}.getType());
		
		String serviceOrderNo = remarkParams.get(RemarkPramKey.SERVICE_ORDER_NO);
		
		result = billService.doPrincipalAdvance(billId, serviceOrderNo, billInvestFeeList);
		
		return result;
	}
	
	/**
	 * 奖励兑换请求第三方后平台业务处理
	 * @param cbParams 第三方返回的数据
	 * @return
	 */
	public ResultInfo conversion(Map<String, String> cbParams){
		ResultInfo result = new ResultInfo();
		
		String requestMark = cbParams.get("mchnt_txn_ssn");
		
		Map<String, String> remarkParams = this.queryRequestParams(requestMark);
		
		if (remarkParams == null){
			result.code = -1;
			result.msg = "查询托管请求备注参数失败";
			
			return result;
		}
		
		long conversionId = Long.parseLong(remarkParams.get(RemarkPramKey.CONVERSION_ID));
		String serviceOrderNo = remarkParams.get(RemarkPramKey.SERVICE_ORDER_NO);
		
		result = conversionService.doConversion(serviceOrderNo, conversionId);
		
		return result;
	}
	
	/**
	 * 自动投标请求第三方后平台业务处理
	 * @param cbParams 第三方返回的数据
	 * @return
	 */
	public ResultInfo autoInvest(Map<String, String> cbParams){
		ResultInfo result = new ResultInfo();
		
		String requestMark = cbParams.get("mchnt_txn_ssn");
		
		Map<String, String> remarkParams = this.queryRequestParams(requestMark);
		
		if (remarkParams == null){
			result.code = -1;
			result.msg = "查询托管请求备注参数失败";
			
			return result;
		}
		
		String freezeTrxId = cbParams.get("contract_no");
		long userId = Long.parseLong(remarkParams.get(RemarkPramKey.USER_ID));
		long bidId = Long.parseLong(remarkParams.get(RemarkPramKey.BID_ID));
		double investAmt = Double.parseDouble(remarkParams.get(RemarkPramKey.INVEST_AMT));
		int client = Integer.parseInt(remarkParams.get(RemarkPramKey.CLIENT));
		int investType = Integer.parseInt(remarkParams.get(RemarkPramKey.INVEST_TYPE));
		String serviceOrderNo = remarkParams.get(RemarkPramKey.SERVICE_ORDER_NO);
		
		result = investService.doInvest(userId, bidId, investAmt, serviceOrderNo, freezeTrxId, 
				client, investType, 0L, 0.0, 0L, 0.0, 0L, 0.0);
		
		if (result.code < 1) {
			
			return result;
		}
		
		return result;
	}
	
	/**
	 * 用户开户请求第三方后平台业处理
	 * @param cbParams 第三方返回的数据
	 * @return
	 */
	public ResultInfo userRegister(Map<String, String> cbParams){
		ResultInfo result = new ResultInfo();
		
		String requestMark = cbParams.get("mchnt_txn_ssn");
		
		Map<String, String> remarkParams = this.queryRequestParams(requestMark);
		
		if (remarkParams == null){
			result.code = -1;
			result.msg = "查询托管请求备注参数失败";
			
			return result;
		}
		
		String userMobile = remarkParams.get(RemarkPramKey.USER_MOBILE);
		long userId = Convert.strToLong(remarkParams.get(RemarkPramKey.USER_ID), 0L);
		String bankType = remarkParams.get("r_bank_type");
		String bankNm = remarkParams.get("r_bank_nm");
		String bankAcct = remarkParams.get("r_bank_acct");
		
		boolean flag = bankCardUserService.addUserCard(userId, bankNm, bankType, bankAcct);
		
		if (!flag) {
			LoggerUtil.error(true, "添加用户银行卡失败");
			
			result.code = -1;
			result.msg = "添加用户银行卡失败";
			
			return result;
		}
		
		/*
		 * 富友接口下用户的真实姓名、身份证号码、email都已经录入到数据库
		 * 在业务层中通过userId查询到的userInfo中的上述三个字段均不为空
		 * 所以这里传空字符串是减少一次不必要的赋值过程
		 */
		result = userFundService.doCreateAccount(userId, userMobile, "", "", "", "");
		
		if (result.code < 1) {
			LoggerUtil.error(true, "执行资金托管开户时：%s", result.msg);
			
			return result;
		}
		
		return result;
	}
	
	/**
	 * 放款请求第三方后平台业处理
	 * @param cbParams 第三方返回的数据
	 * @return
	 */
	public ResultInfo bidSuditSucc(Map<String, String> cbParams){
		ResultInfo result = new ResultInfo();
		
		String requestMark = cbParams.get("mchnt_txn_ssn");
		
		Map<String, String> remarkParams = this.queryRequestParams(requestMark);
		
		if (remarkParams == null){
			result.code = -1;
			result.msg = "查询托管请求备注参数失败";
			
			return result;
		}
		
		t_bid bid = new Gson().fromJson(remarkParams.get(RemarkPramKey.BID), t_bid.class);
		String serviceOrderNo = remarkParams.get(RemarkPramKey.SERVICE_ORDER_NO);
		
		//借款服务费纠偏
		bidService.updateLoanServiceFee(bid.id, bid.loan_fee);
		
		//财务放款（执行）
		result = bidService.doRelease(bid.id, 1, serviceOrderNo);
		
		return result;
	}
	
	/**
	 * 解冻投资人资金
	 *
	 * @param userId 关联用户ID
	 * @param serviceType 平台业务类型
	 * @param payType 第三方接口类型
	 * @param serviceOrderNo 业务订单号
	 * @param freezeTrxId 冻结订单号
	 * @return
	 *
	 * @author YanPengFei
	 * @createDate 2016年08月03日
	 */
	public ResultInfo doUserUnFreeze(long userId, long bidId, double investAmt, ServiceType serviceType, 
			String serviceOrderNo, String freezeTrxId) {
		ResultInfo result = new ResultInfo();
		
		//查询理财人资金信息
		t_user_fund investUserFund = userFundService.queryUserFundByUserId(userId);
		
		if (investUserFund == null) {
			result.code = -1;
			result.msg = "查询理财人资金信息失败";
			
			return result;
		}
		
		//查询借款标信息
		t_bid bid = bidService.findByID(bidId);
		
		if (bid == null) {
			result.code = -1;
			result.msg = "获取借款标信息失败";
			
			return result;
		}
		
		//查询借款人资金信息
		t_user_fund loanUserInfo = userFundService.queryUserFundByUserId(bid.user_id);
		
		if (loanUserInfo == null) {
			result.code = -1;
			result.msg = "查询借款人资金信息失败";
			
			return result;
		}
		
		String orderNo = OrderNoUtil.getNormalOrderNo(serviceType);  //生成流水号
		
		//接口参数拼装
		Map<String,String> reqParams = fyPaymentService.preAuthCancel(investUserFund.payment_account, loanUserInfo.payment_account, 
				investAmt, freezeTrxId, orderNo, FyPayType.TENDERCANCLE.value);
				
		//打印提交日志
		fyPaymentService.printRequestData("", investUserFund.user_id, serviceOrderNo, orderNo, serviceType, 
				FyPayType.TENDERCANCLE, reqParams, "");
		
		//表单提交
		String xmlData = HttpUtil.postMethod(FyConsts.post_url + FyConsts.preAuthCancel, reqParams, "UTF-8");
		
		//将第三方返回的xml转换成map
		Map<String, String> cbParams = FyUtils.parseXmlToMap(xmlData);
		
		//打印回调日志信息
		fyPaymentService.printCallBackData("解冻投资人资金", cbParams, serviceType, FyPayType.TENDERCANCLE);
		
		//校验签名、状态码判断、防止重单
		result = FyUtils.checkSign(cbParams, "解冻投资人资金", FyPayType.TENDERCANCLE.value, FyPayType.TENDERCANCLE);
		
		if (result.code < 1 && result.code != ResultInfo.ALREADY_RUN) {
			
			return result;
		}
		
		result.code = 1;
		result.msg = "解冻投资人资金成功";
		
		return result;
	}
	
	/**
	 * 查询用户信息请求第三方后平台业务处理
	 * @param cbParams 第三方返回的数据
	 * @return
	 */
	public ResultInfo queryPersionInformation(Map<String, String> cbParams) {
		ResultInfo result = new ResultInfo();
		
		//获得流水号(由平台生成并发送至第三方的)
		String requestMark = cbParams.get("mchnt_txn_ssn");
		
		Map<String, String> remarkParams = this.queryRequestParams(requestMark);
		
		if (remarkParams == null) {
			result.code = -1;
			result.msg = "查询托管请求备注参数失败";
			
			return result;
		}
		
		//将第三方返回的用户信息数据写入到数据库
		long userId = Convert.strToLong(remarkParams.get(RemarkPramKey.USER_ID), 0L);
		String platformMobile = remarkParams.get(RemarkPramKey.USER_MOBILE);
		
		Map<String, String> plainParams = FyUtils.parseXmlToMapUsedToPlain(cbParams.get("plain"));
		String email = plainParams.get("email");
		//String mobile = plainParams.get("mobile_no"); //用户手机号码
		String realName = plainParams.get("cust_nm"); //用户真实姓名
		String idNumber = plainParams.get("certif_id"); //用户身份证号码
		String bankName = plainParams.get("bank_nm"); //开户行支行名称
		String bankType = plainParams.get("parent_bank_id"); //开户行行别
		String banknNum = plainParams.get("capAcntNo"); //银行卡账号
		
		//将用户填写的银行卡信息入库(银行卡数据以第三方返回的为准)
		boolean flag = bankCardUserService.addUserCard(userId, bankName, bankType, banknNum);
		
		if (!flag) {
			LoggerUtil.error(true, "添加用户银行卡失败");
			
			result.code = -1;
			result.msg = "添加用户银行卡失败";
			
			return result;
		}
		
		//若第三方返回的数据是空的，则默认取平台存储的数据(在业务方法中实现)
		result = userFundService.doCreateAccount(userId, platformMobile, realName, idNumber, email, "");
		
		if (result.code < 1) {
			LoggerUtil.error(true, "执行资金托管开户时：%s", result.msg);
			
			return result;
		}
		
		result.code = 1;
		
		return result;
	}
	
	/**
	 * 快速充值回调业务处理
	 * @param cbParams 第三方返回的数据
	 * @param remarkParams 发起请求时入库的参数
	 * @return
	 */
	public ResultInfo fastSave(Map<String, String> cbParams, Map<String, String> remarkParams) {
		ResultInfo result = new ResultInfo();
		
		//校验第三方返回的表单数据
		result = FyUtils.checkFormSign(cbParams, "快速充值", FyPayType.FASTSAVE, "amt", "login_id", "mchnt_cd", "mchnt_txn_ssn", "resp_code");
		
		if (result.code < 1) {
			
			return result;
		}
		
		long userId = Long.parseLong(remarkParams.get(RemarkPramKey.USER_ID));
		double rechargeAmt = Double.parseDouble(remarkParams.get(RemarkPramKey.RECHARGE_AMT));
		String serviceOrderNo = remarkParams.get(RemarkPramKey.SERVICE_ORDER_NO);
		
		result = userFundService.doRecharge(userId, rechargeAmt, serviceOrderNo);
		
		if (result.code < 1) {
			
			return result;
		}
		
		result.code = 1;
		result.msg = "充值成功";
		
		return result;
	}

	 /**
		 * 更换手机号码回调业务处理
		 * @param cbParams 第三方返回的数据
		 * @param remarkParams 发起请求时入库的参数
		 * @return
		 */
		public ResultInfo changeUserMobile(Map<String, String> cbParams, Map<String, String> remarkParams) {
			ResultInfo result = new ResultInfo();
			
			String loginId = cbParams.get("login_id"); //用户在第三方的账号
			String newMobile = cbParams.get("new_mobile"); //用户修改后的新手机号码
			
			long userId = Long.parseLong(remarkParams.get(RemarkPramKey.USER_ID));
			String acct = remarkParams.get(RemarkPramKey.PAYMENT_ACCOUNT); //发起更换手机号码请求的用户第三方账号
			
			if (!acct.endsWith(loginId)) {
				result.code = -1;
				result.msg = "第三方回调数据异常";
				
				return result;
			}
			
			//更新用户信息表的手机号码
			result = userInfoService.updateUserMobile(userId, newMobile);
			
			if (result.code < 1) {
				
				return result;
			}
			
			//更新用户表的手机号码(冗余字段,用来登录)
			result = userService.updateUserMobile(userId, newMobile);
			
			if (result.code < 1) {
				
				return result;
			}
			
			result.code = 1;
			result.msg = "更换手机号码成功";
			
			return result;
		}
		
		/**
		 * 第三方账户管理系统充值，平台业务处理
		 * @param cbParams 第三方回调数据
		 * @return
		 */
		public ResultInfo thirdPartyRechargeDispose(Map<String, String> cbParams) {
			ResultInfo result = new ResultInfo();
			
			if (cbParams == null || cbParams.size() <= 0) {
				result.code = -1;
				result.msg = "回调数据异常";
				
				return result;
			}
			
			String orderNo = cbParams.get("mchnt_txn_ssn"); //流水号
			String mobile = cbParams.get("mobile_no"); //手机号码
			long userId = userFundService.findUserIdByPaymentAccount(mobile); //根据资管账号查询用户ID
			
			if (userId <= 0) {
				result.code = -1;
				result.msg = "用户数据异常";
				
				return result;
			}
			
			//向资管交易请求表中插入一条数据
			fyPaymentService.printRequestData("", userId, orderNo, orderNo, ServiceType.RECHARGE, FyPayType.NETSAVE, 
					cbParams, BaseController.getBaseURL() + FyConsts.rechargeNotifyRepair);
			
			//校验第三方返回的数据
			result = FyUtils.checkThirdPartyNotifyFormSign(cbParams, "充值通知", FyPayType.NETSAVE, 
					"amt", "mchnt_cd", "mchnt_txn_dt", "mchnt_txn_ssn", "mobile_no", "remark");
			
			if (result.code < 1) {
				Logger.info("校验第三方返回的数据时：%s", result.msg);
				
				return result;
			}
			
			String amountStr = cbParams.get("amt"); //充值金额
			double amount = Double.parseDouble(amountStr) / 100;
			
			if (amount <= 0.00) {
				result.code = -1;
				result.msg = "充值金额数据异常";
				
				return result;
			}
			
			//平台充值业务执行
			result = userFundService.doRecharge(userId, amount, orderNo);
			
			if (result.code < 1) {
				LoggerUtil.error(true, "第三方账户管理系统充值，平台执行业务处理时：%s", result.msg);
				
				result.code = -1;
				result.msg = "充值失败";
				
				return result;
			}
			
			result.code = 1;
			result.msg = "充值成功";
			
			return result;
		}
		
		/**
		 * 第三方账户管理系统提现，平台业务处理
		 * @param cbParams 第三方回调数据
		 * @return
		 */
		public ResultInfo thirdPartyWithdrawDispose(Map<String, String> cbParams) {
			ResultInfo result = new ResultInfo();
			
			if (cbParams == null || cbParams.size() <= 0) {
				result.code = -1;
				result.msg = "回调数据异常";
				
				return result;
			}
			
			String orderNo = cbParams.get("mchnt_txn_ssn"); //流水号
			String mobile = cbParams.get("mobile_no"); //手机号码
			long userId = userFundService.findUserIdByPaymentAccount(mobile); //根据资管账号查询用户ID
			
			if (userId <= 0) {
				result.code = -1;
				result.msg = "用户数据异常";
				
				return result;
			}
			
			//向资管交易请求表中插入一条数据
			fyPaymentService.printRequestData("", userId, orderNo, orderNo, ServiceType.WITHDRAW, FyPayType.CASH, 
					cbParams, BaseController.getBaseURL() + FyConsts.withdrawNotifyRepair);
			
			//校验第三方返回的数据
			result = FyUtils.checkThirdPartyNotifyFormSign(cbParams, "提现通知", FyPayType.CASH, 
					"amt", "mchnt_cd", "mchnt_txn_dt", "mchnt_txn_ssn", "mobile_no", "remark");
			
			if (result.code < 1) {
				
				return result;
			}
			
			String amountStr = cbParams.get("amt"); //提现金额
			double amount = Double.parseDouble(amountStr) / 100;
			
			if (amount <= 0.00) {
				result.code = -1;
				result.msg = "提现金额数据异常";
				
				return result;
			}
			
			//第三方提现收取手续费的扣除模式：false为内扣、true为外扣
			//富友不支持内扣，默认为外扣
			boolean mode = true; 
			
			//平台提现业务执行
			result = userFundService.doWithdrawal(userId, amount, 0.00, 0.00, orderNo, mode);
			
			if (result.code < 1) {
				LoggerUtil.error(true, "第三方账户管理系统提现，平台执行业务处理时：%s", result.msg);
				
				result.code = -1;
				result.msg = "提现失败";
				
				return result;
			}
			
			result.code = 1;
			result.msg = "提现成功";
			
			return result;
		}
		
		/**
		 * 提现退票，平台业务处理
		 * @param cbParams 第三方回调数据
		 * @return
		 */
		public ResultInfo thirdPartyWithdrawRefundDispose(Map<String, String> cbParams) {
			ResultInfo result = new ResultInfo();
			
			if (cbParams == null || cbParams.size() <= 0) {
				result.code = -1;
				result.msg = "回调数据异常";
				
				return result;
			}
			
			String orderNo = cbParams.get("mchnt_txn_ssn"); //流水号
			String mobile = cbParams.get("mobile_no"); //手机号码
			long userId = userFundService.findUserIdByPaymentAccount(mobile); //根据资管账号查询用户ID
			
			if (userId <= 0) {
				result.code = -1;
				result.msg = "用户数据异常";
				
				return result;
			}
			
			//向资管交易请求表中插入一条数据
			fyPaymentService.printRequestData("", userId, orderNo, orderNo, ServiceType.WITHDRAW_REFUND, FyPayType.WITHDRAW_REFUND, 
					cbParams, BaseController.getBaseURL() + FyConsts.withdrawRefundNotifyRepair);
			
			//校验第三方返回的数据
			result = FyUtils.checkThirdPartyNotifyFormSign(cbParams, "提现退票通知", FyPayType.WITHDRAW_REFUND, 
					"amt", "mchnt_cd", "mchnt_txn_dt", "mchnt_txn_ssn", "mobile_no", "remark");
			
			if (result.code < 1) {
				
				return result;
			}
			
			String amountStr = cbParams.get("amt"); //提现退票金额
			double amount = Double.parseDouble(amountStr) / 100;
			
			if (amount <= 0.00) {
				result.code = -1;
				result.msg = "提现退票金额数据异常";
				
				return result;
			}
			
			//第三方提现收取手续费的扣除模式：false为内扣、true为外扣
			//富友不支持内扣，默认为外扣
			boolean mode = true;
			
			//平台执行提现退票业务
			result = userFundService.doWithdrawalRefund(userId, amount, 0.00, orderNo, mode);
			
			if (result.code < 1) {
				LoggerUtil.error(true, "提现退票，平台执行业务处理时：%s", result.msg);
				
				result.code = -1;
				result.msg = "提现退票失败";
				
				return result;
			}
			
			result.code = 1;
			result.msg = "提现退票成功";
			
			return result;
		}
		
		/**
		 * 第三方账户管理系统用户修改信息，平台业务处理
		 * @param cbParams 第三方回调数据
		 * @return
		 */
		public ResultInfo thirdPartyUserModifyInfoDispose(Map<String, String> cbParams) {
			ResultInfo result = new ResultInfo();
			
			if (cbParams == null || cbParams.size() <= 0) {
				result.code = -1;
				result.msg = "回调数据异常";
				
				return result;
			}
			
			String orderNo = cbParams.get("mchnt_txn_ssn"); //流水号
			String mobile = cbParams.get("mobile_no"); //手机号码
			long userId = userFundService.findUserIdByPaymentAccount(mobile); //根据资管账号查询用户ID
			
			if (userId <= 0) {
				result.code = -1;
				result.msg = "用户数据异常";
				
				return result;
			}
			
			//向资管交易请求表中插入一条数据
			fyPaymentService.printRequestData("", userId, orderNo, orderNo, ServiceType.USER_MODIFY_INFO, FyPayType.USER_MODIFY_INFO, 
					cbParams, BaseController.getBaseURL() + FyConsts.userModifyInfoNotifyRepair);
			
			//校验第三方返回的数据
			result = FyUtils.checkThirdPartyNotifyFormSign(cbParams, "用户修改信息通知", FyPayType.USER_MODIFY_INFO, "bank_nm", "capAcntNo", "certif_id", 
			"city_id", "cust_nm", "email", "mchnt_cd", "mchnt_txn_ssn", "mobile_no", "parent_bank_id", "resp_code", "user_id_from");
			
			if (result.code < 1) {
				
				return result;
			}
			
			String bankCode = cbParams.get("parent_bank_id"); //开户行行别
			String bankName = cbParams.get("bank_nm"); //开户行支行名称
			String bankAccount = cbParams.get("capAcntNo"); //帐号
			
			//平台执行用户修改信息业务
			int row = bankCardUserService.updateUserBankInfo(bankName, bankCode, bankAccount, userId);
			
			if (row <= 0) {
				LoggerUtil.error(true, "第三方账户管理系统用户修改信息，平台执行业务处理失败");
				
				result.code = -1;
				result.msg = "用户修改信息失败";
				
				return result;
			}
			
			result.code = 1;
			result.msg = "用户修改信息成功";
			
			return result;
		}
		
		/**
		 * 第三方账户管理系统用户注销，平台业务处理
		 * @param cbParams 第三方回调数据
		 * @return
		 */
		public ResultInfo thirdPartyUserLogoutDispose(Map<String, String> cbParams) {
			ResultInfo result = new ResultInfo();
			
			if (cbParams == null || cbParams.size() <= 0) {
				result.code = -1;
				result.msg = "回调数据异常";
				
				return result;
			}
			
			String orderNo = cbParams.get("mchnt_txn_ssn"); //流水号
			String paymentAccount = cbParams.get("login_id"); //用户资管账号
			long userId = userFundService.findUserIdByPaymentAccount(paymentAccount); //根据资管账号查询用户ID
			String stateStr = cbParams.get("state"); //状态：1：申请注销、2：已同意注销、3：已驳回注销
			int state = Convert.strToInt(stateStr, 0);
			
			if (userId <= 0) {
				result.code = -1;
				result.msg = "用户数据异常";
				
				return result;
			}
			
			if (state != 2) {
				result.code = -1;
				result.msg = "用户注销状态异常";
				
				return result;
			}
			
			//向资管交易请求表中插入一条数据
			fyPaymentService.printRequestData("", userId, orderNo, orderNo, ServiceType.USER_LOGOUT, FyPayType.USER_LOGOUT, 
					cbParams, BaseController.getBaseURL() + FyConsts.userLogoutNotifyRepair);
			
			//校验第三方返回的数据
			result = FyUtils.checkThirdPartyNotifyFormSign(cbParams, "用户注销通知", FyPayType.USER_LOGOUT, 
					"login_id", "mchnt_cd", "mchnt_txn_dt", "mchnt_txn_ssn", "state");
			
			if (result.code < 1) {
				
				return result;
			}
			
			//平台业务执行
			int row = userFundService.doUserLogout(userId);
			
			if (row <= 0) {
				LoggerUtil.error(true, "第三方账户管理系统用户注销，平台执行业务处理失败");
				
				result.code = -1;
				result.msg = "用户注销失败";
				
				return result;
			}
			
			result.code = 1;
			result.msg = "用户注销成功";
			
			return result;
		}
}
