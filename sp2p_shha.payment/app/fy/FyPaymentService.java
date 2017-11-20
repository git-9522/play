package fy;


import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.XML;

import com.google.gson.Gson;

import common.enums.ServiceType;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.JPAUtil;
import common.utils.LoggerUtil;
import common.utils.OrderNoUtil;
import common.utils.ResultInfo;
import controllers.common.BaseController;
import models.common.entity.t_user_fund;
import models.core.entity.t_bill;
import models.core.entity.t_bill_invest;
import models.entity.t_payment_call_back;
import models.entity.t_payment_request;
import play.Logger;
import services.PaymentService;
import services.common.UserFundService;

public class FyPaymentService  extends PaymentService{
	
	private static UserFundService userFundService = Factory.getService(UserFundService.class);
	
	/**
	 * 添加请求第三方操作记录
	 * @param userId
	 * @param serviceOrderNo
	 * @param serviceType
	 * @param orderNo
	 * @param payType
	 * @return
	 */
	public long addRequestRecord(long userId, String serviceOrderNo, ServiceType serviceType, String orderNo, FyPayType payType){
		boolean isSave = false;
		t_payment_request pr = new t_payment_request();
		
		if (payType.isAddRequestRecord){
			JPAUtil.transactionBegin();
			
			JPAUtil.transactionCommit();
		}
		
		return isSave ? pr.id : -1;
	}
	
	/**
	 * 打印提交日志
	 * @param requestMark
	 * @param userId
	 * @param serviceOrderNo
	 * @param orderNo
	 * @param serviceType
	 * @param payType
	 * @param reqParams
	 * @param bgRetUrl 补单地址
	 */
	public static void printRequestData(String requestMark, long userId, String serviceOrderNo, String orderNo, 
			ServiceType serviceType, FyPayType payType, Map<String, String> reqParams, String bgRetUrl){
		StringBuffer info = new StringBuffer();
		info.append("******************" + payType.value + "请求开始******************");
		info.append("\n托管业务类型：" + serviceType.value);
		info.append("\n接口类型：" + payType.value);

		for (Entry<String, String> entry : reqParams.entrySet()){		
			info.append("\n" + entry.getKey() + "--" + entry.getValue());
		}
		
		info.append("\n******************" + payType.value + "请求结束******************");
		
		LoggerUtil.info(false, info.toString());
		
		if (payType.isAddRequestRecord){
			JPAUtil.transactionBegin();
			
			t_payment_request pr = new t_payment_request();
			
			pr.time = new Date();
			pr.user_id = userId;
			pr.service_order_no = serviceOrderNo;
			pr.setService_type(serviceType);
			pr.order_no = orderNo;
			pr.setPay_type(payType);
			pr.setStatus(t_payment_request.Status.FAILED);  //先失败，后成功
			pr.ayns_url = bgRetUrl; //第三方回调地址（富友不区分同异步回调，该异步地址主要用于补单）
			pr.req_params = new Gson().toJson(reqParams);
			pr.mark = orderNo;
			
			paymentRequstDao.save(pr);
			
			JPAUtil.transactionCommit();
		}
	}
	
	/**
	 * 打印回调日志信息
	 * @param desc
	 * @param cbParams
	 * @param serviceType
	 * @param payType
	 */
	public static void printCallBackData(String desc, Map<String, String> cbParams, ServiceType serviceType, FyPayType payType){
		StringBuffer info = new StringBuffer();
		
		info.append("******************【" + desc + "】回调开始******************");
		
		if (serviceType != null){
			info.append("\n托管业务类型：" + serviceType.value);
		}
		
		info.append("\n接口类型：" + payType.value);

		for (Entry<String, String> entry : cbParams.entrySet()){		
			try {
				info.append("\n" + entry.getKey() + "--" + java.net.URLDecoder.decode(entry.getValue(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				LoggerUtil.error(false, e, "回调参数解码失败");
			}
		}
		
		info.append("******************【" + desc + "】回调结束******************");
		
		LoggerUtil.info(false, info.toString());
		
		if (payType.isAddRequestRecord && payType.isAddCallBackRecord){
			JPAUtil.transactionBegin();
			
			t_payment_call_back pcb = new t_payment_call_back();
			pcb.time = new Date();
			pcb.request_mark = cbParams.get("mchnt_txn_ssn");
			pcb.cb_params = new Gson().toJson(cbParams);
			paymentCallBackDao.save(pcb);
			
			JPAUtil.transactionCommit();
		}
	}
	
	/** 是否超额投标 **/
	public boolean isOverBidAmount(String serviceOrderNo){
		List<t_payment_request> list = paymentRequstDao.findListByColumn("service_order_no = ?", serviceOrderNo);
		
		if (list != null && list.size() >= 2){  //正常投标只有一次请求（主动投标），超额投标会有两次请求（主动投标，解冻投标金额）
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * 开户
	 * @param cust_nm 平台用户真实姓名
	 * @param certif_id 身份证号码
	 * @param mobile_no 手机号码
	 * @param email 邮箱
	 * @param city_id 开户行所属市
	 * @param parent_bank_id 开户行类别
	 * @param bank_nm 支行名称
	 * @param cap_acnt_no 银行卡号
	 * @param request_mark 托管请求唯一标识（富友接口暂不使用，传空字符串）
	 * @param order_no 交易订单号
	 * @return
	 */
	public LinkedHashMap<String, String> userRegister(String cust_nm, String certif_id, String mobile_no, String email, String city_id, 
			String parent_bank_id, String bank_nm, String cap_acnt_no, String request_mark, String order_no) {
		LinkedHashMap<String, String> xmlMap = new LinkedHashMap<String, String>();
		
		xmlMap.put("mchnt_cd", FyConsts.mchnt_cd); //商户代码
		xmlMap.put("mchnt_txn_ssn", order_no); //流水号
		xmlMap.put("capAcntNm", ""); //户名（富友文档提示该字段留空）
		xmlMap.put("password", ""); //提现密码
		xmlMap.put("lpassword", ""); //登录密码
		xmlMap.put("cust_nm", cust_nm); //客户姓名
		xmlMap.put("certif_id", certif_id); //身份证号码
		xmlMap.put("mobile_no", mobile_no); //手机号码
		xmlMap.put("email", email); //邮箱
		xmlMap.put("city_id", city_id); //开户行地区代码
		xmlMap.put("parent_bank_id", parent_bank_id); //开户行行别
		xmlMap.put("bank_nm", bank_nm); //开户行支行名称
		xmlMap.put("capAcntNo", cap_acnt_no); //银行卡账号
		xmlMap.put("rem", request_mark); //备注
		xmlMap.put("signature", FyUtils.createSign(xmlMap));
		
		return xmlMap;
	}
	
	/**
	 * 充值
	 * @param pIpsAcctNo 充值用户第三方账号
	 * @param pTrdAmt 充值金额
	 * @param pOrderNo 交易订单号
	 * @return
	 */
	public LinkedHashMap<String, String> recharge(String pIpsAcctNo, double pTrdAmt, String pOrderNo){
		LinkedHashMap<String, String> xmlMap = new LinkedHashMap<String, String>();
		
		xmlMap.put("mchnt_cd", FyConsts.mchnt_cd);
		xmlMap.put("mchnt_txn_ssn", pOrderNo);
		xmlMap.put("login_id", pIpsAcctNo);
		xmlMap.put("amt", FyUtils.formatAmountToFen(pTrdAmt));
		xmlMap.put("page_notify_url", BaseController.getBaseURL() + "payment/fy/returnRecharge");
		xmlMap.put("back_notify_url", BaseController.getBaseURL() + "payment/fy/returnRecharge");
		xmlMap.put("signature", FyUtils.createSign(xmlMap));
		
		return xmlMap;
	}
	
	/**
	 * 提现
	 * @param pIpsAcctNo 提现用户第三方账号
	 * @param pAmt 提现金额
	 * @param pOrderNo 交易订单号
	 * @return
	 */
	public LinkedHashMap<String, String> withdraw(String pIpsAcctNo, double pAmt, String pOrderNo) {
		LinkedHashMap<String, String> xmlMap = new LinkedHashMap<String, String>();
		
		xmlMap.put("mchnt_cd", FyConsts.mchnt_cd);
		xmlMap.put("mchnt_txn_ssn", pOrderNo);
		xmlMap.put("login_id", pIpsAcctNo);
		xmlMap.put("amt", FyUtils.formatAmountToFen(pAmt));
		xmlMap.put("page_notify_url", BaseController.getBaseURL()+ "payment/fy/returnWithdraw");
		xmlMap.put("back_notify_url", BaseController.getBaseURL()+ "payment/fy/returnWithdraw");
		xmlMap.put("signature", FyUtils.createSign(xmlMap));
		
		return xmlMap;
	}
	
	/**
	 * 冻结
	 * @param cust_no 借款人富友账号
	 * @param amount 冻结金额
	 * @param order_no 冻结流水号
	 * @param rem 操作接口描述
	 * @return
	 */
	public LinkedHashMap<String, String> freeze(String cust_no, double amount, String order_no, String rem){
		LinkedHashMap<String, String> xmlMap = new LinkedHashMap<String, String>();
		
		xmlMap.put("mchnt_cd", FyConsts.mchnt_cd);
		xmlMap.put("mchnt_txn_ssn", order_no);
		xmlMap.put("cust_no", cust_no);
		xmlMap.put("amt", FyUtils.formatAmountToFen(amount));
		xmlMap.put("rem", rem);
		xmlMap.put("signature", FyUtils.createSign(xmlMap));
		
		return xmlMap;
	}
	
	/**
	 * 预授权接口（投标，自动投标）
	 * @param out_cust_no 出账账户
	 * @param in_cust_no 进账账户
	 * @param amount 预授权金额
	 * @param order_no 流水号
	 * @param rem 备注
	 * @return
	 */
	public static Map<String, String> preAuth(String out_cust_no, String in_cust_no, double amount, String order_no, String rem){
		LinkedHashMap<String, String> xmlMap = new LinkedHashMap<String, String>();

		xmlMap.put("mchnt_cd", FyConsts.mchnt_cd); //商户代码
		xmlMap.put("mchnt_txn_ssn", order_no); //流水号
		xmlMap.put("out_cust_no", out_cust_no); //出账账户
		xmlMap.put("in_cust_no", in_cust_no); //进账账户
		xmlMap.put("amt", FyUtils.formatAmountToFen(amount)); //预授权金额
		xmlMap.put("rem", rem); //备注
		xmlMap.put("signature", FyUtils.createSign(xmlMap));
		
		return xmlMap;
	}
	
	/**
	 * 预授权撤销接口
	 * @param out_cust_no 当时投标时支出金额的账号（投资人第三方账号）
	 * @param in_cust_no 收到投标金额的账号（借款人第三方账号）
	 * @param amount 解冻金额
	 * @param contract_no 投资时第三方返回的流水号
	 * @param order_no 本次请求的流水号
	 * @param rem 备注
	 * @return
	 */
	public static Map<String, String> preAuthCancel(String out_cust_no, String in_cust_no, double amount, String contract_no, 
			String order_no, String rem){
		LinkedHashMap<String, String> xmlMap = new LinkedHashMap<String, String>();
		
		xmlMap.put("mchnt_cd", FyConsts.mchnt_cd); //商户代码
		xmlMap.put("mchnt_txn_ssn", order_no); //流水号
		xmlMap.put("out_cust_no", out_cust_no); //出账账户
		xmlMap.put("in_cust_no", in_cust_no); //进账账户
		xmlMap.put("contract_no", contract_no); //投资时第三方返回的流水号
		xmlMap.put("rem", rem); //备注
		xmlMap.put("signature", FyUtils.createSign(xmlMap));
		
		return xmlMap;
	}
	
	/**
	 * 满标放款
	 * @param out_cust_no 投资人第三方账号
	 * @param in_cust_no 借款人第三方账号
	 * @param amount 借款金额
	 * @param contract_no 投资时第三方返回的流水号
	 * @param order_no 本次请求的流水号
	 * @param rem 备注
	 * @return
	 */
	public static Map<String, String> bidAuditSucc(String out_cust_no, String in_cust_no, double amount, String contract_no, 
			String order_no, String rem){
		LinkedHashMap<String, String> xmlMap = new LinkedHashMap<String, String>();
		
		xmlMap.put("mchnt_cd", FyConsts.mchnt_cd); //商户代码
		xmlMap.put("mchnt_txn_ssn", order_no); //流水号
		xmlMap.put("out_cust_no", out_cust_no); //出账账户
		xmlMap.put("in_cust_no", in_cust_no); //进账账户
		xmlMap.put("contract_no", contract_no); //投资时第三方返回的流水号
		xmlMap.put("amt", FyUtils.formatAmountToFen(amount)); //转账金额
		xmlMap.put("rem", rem); //备注
		xmlMap.put("signature", FyUtils.createSign(xmlMap));
		
		return xmlMap;
	}
			
	/**
	 * 余额查询（后台资金对账）
	 * @param cust_no 待查询的第三方账号
	 * @param order_no 流水号
	 * @param rem 备注
	 * @return
	 */
	public static Map<String, String> queryAmount(String cust_no, String order_no, String rem){
		LinkedHashMap<String, String> xmlMap = new LinkedHashMap<String, String>();
		
		Date dt = new Date();
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		String mchnt_txn_dt = df.format(dt);
		
		xmlMap.put("mchnt_cd", FyConsts.mchnt_cd); //商户代码
		xmlMap.put("mchnt_txn_ssn", order_no); //流水号
		xmlMap.put("mchnt_txn_dt", mchnt_txn_dt); //交易日期
		xmlMap.put("cust_no", cust_no); //待查询的第三方帐户
		xmlMap.put("signature", FyUtils.createSign(xmlMap));
		
		return xmlMap;
	}
	
	/**
	 * 划拨（个人与个人） （应用：还款，投标奖励）
	 * @param out_cust_no 出账账号
	 * @param in_cust_no 进账账号
	 * @param amount 交易金额
	 * @param order_no 流水号
	 * @param rem 备注
	 * @return
	 */
	public static Map<String, String> transferBu(String out_cust_no, String in_cust_no, double amount, String order_no, String rem){
		LinkedHashMap<String, String> xmlMap = new LinkedHashMap<String, String>();
		
		xmlMap.put("mchnt_cd", FyConsts.mchnt_cd); //商户代码
		xmlMap.put("mchnt_txn_ssn", order_no); //流水号
		xmlMap.put("out_cust_no", out_cust_no); //出账账户
		xmlMap.put("in_cust_no", in_cust_no); //进账账户
		xmlMap.put("contract_no", ""); //预授权合同号
		xmlMap.put("amt", FyUtils.formatAmountToFen(amount)); //划拨金额
		xmlMap.put("rem", rem); //备注
		xmlMap.put("signature", FyUtils.createSign(xmlMap));
		
		return xmlMap;
	}
	
	/**
	 * 转账（商户与个人） （应用：借款管理费，理财管理费，cps奖励发放等各项杂费及商户的各项奖励）
	 * @param out_cust_no 出账账号
	 * @param in_cust_no 进账账号
	 * @param amount 交易金额
	 * @param order_no 流水号
	 * @param rem 备注
	 * @return
	 */
	public static Map<String, String> transferBmu(String out_cust_no, String in_cust_no, double amount, String order_no, String rem) {
		LinkedHashMap<String, String> xmlMap = new LinkedHashMap<String, String>();
		
		xmlMap.put("mchnt_cd", FyConsts.mchnt_cd); //商户代码
		xmlMap.put("mchnt_txn_ssn", order_no); //流水号
		xmlMap.put("out_cust_no", out_cust_no); //出账账户
		xmlMap.put("in_cust_no", in_cust_no); //进账账户
		xmlMap.put("contract_no", ""); //预授权合同号
		xmlMap.put("amt", FyUtils.formatAmountToFen(amount)); //划拨金额
		xmlMap.put("rem", rem); //备注
		xmlMap.put("signature", FyUtils.createSign(xmlMap));
		
		return xmlMap;
	}
	
	/**
	 * 解冻保证金
	 * @param cust_no 借款人账号
	 * @param amount 冻结金额
	 * @param order_no 冻结流水号
	 * @param rem 备注
	 * @return
	 */
	public static Map<String, String> unFreeze(String cust_no, double amount, String order_no, String rem){
		LinkedHashMap<String, String> xmlMap = new LinkedHashMap<String, String>();
		
		xmlMap.put("mchnt_cd", FyConsts.mchnt_cd);
		xmlMap.put("mchnt_txn_ssn", order_no);
		xmlMap.put("cust_no", cust_no);
		xmlMap.put("amt", FyUtils.formatAmountToFen(amount));
		xmlMap.put("rem", rem);
		xmlMap.put("signature", FyUtils.createSign(xmlMap));
		
		return xmlMap;
	}
	
	/**
	 * 查询还款提交给第三方需要的参数
	 * @param bill 账单数据实体对象
	 * @param billInvestList 待还的理财账单
	 * @param outCusId 借款用户第三方账号
	 * @param billInvestFeeList 理财服务费集合
	 * @return
	 */
	public static List<LinkedHashMap<String, String>> queryRepaymentData(t_bill bill, List<t_bill_invest> billInvestList, 
			String outCusId, List<Map<String, Double>> billInvestFeeList){
		List<LinkedHashMap<String, String>> pDetails = new LinkedList<LinkedHashMap<String, String>>();
		
		for (int i = 0; i < billInvestList.size(); i++){
			t_bill_invest billInvest = billInvestList.get(i);
			Map<String, Double> billInvestFee = billInvestFeeList.get(i);
			//投资人收益
			double pInAmt = billInvest.receive_interest + billInvest.receive_corpus + billInvestFee.get("overdueFine");
			//理财服务费
			double pInFee = billInvestFee.get("investServiceFee");
			
			//投资人信息
			t_user_fund investUserFund = userFundService.queryUserFundByUserId(billInvest.user_id);
			
			if (investUserFund == null){
				
				return null;
			}
			
			//还款参数
			LinkedHashMap<String, String> repaymentMap = new LinkedHashMap<String, String>();
			repaymentMap.put("mchnt_cd", FyConsts.mchnt_cd); //商户代码
			repaymentMap.put("mchnt_txn_ssn", OrderNoUtil.getSpecialOrderNo(billInvest.time, ServiceType.REPAYMENT, OrderNoUtil.SPECIAL_BILL_INVEST, billInvest.id)); //流水号
			repaymentMap.put("out_cust_no", outCusId); //出账账户
			repaymentMap.put("in_cust_no", investUserFund.payment_account); //进账账户
			repaymentMap.put("contract_no", ""); //预授权合同号
			repaymentMap.put("amt", pInAmt + ""); //划拨金额
			repaymentMap.put("rem", FyPayType.REPAYMENT.value); //备注
			repaymentMap.put("type", "R");
			pDetails.add(repaymentMap);
			
			if (pInFee > 0){
				//理财服务费参数
				LinkedHashMap<String, String> rMap = new LinkedHashMap<String, String>();
				rMap.put("mchnt_cd", FyConsts.mchnt_cd); //商户代码
				rMap.put("mchnt_txn_ssn", OrderNoUtil.getSpecialOrderNo(billInvest.time, ServiceType.TRANSFER, OrderNoUtil.SPECIAL_BILL_INVEST, billInvest.id)); //流水号
				rMap.put("out_cust_no", investUserFund.payment_account); //出账账户
				rMap.put("in_cust_no", FyConsts.mchnt_name); //进账账户
				rMap.put("contract_no", ""); //预授权合同号
				rMap.put("amt", pInFee + ""); //划拨金额
				rMap.put("rem", FyPayType.MERCHANTANDPERSIONTRANSFER.value); //备注
				rMap.put("type", "RM");
				pDetails.add(rMap);
			}
		}
			
		return pDetails;
	}
	
	/**
	 * 本息垫付/线下收款查询（商户转账给投资人）
	 * @param billInvestFeeList 
	 * @param billInvestList 
	 * @param serviceType 
	 * @return
	 */
	public static List<Map<String, String>> queryAdvanceOrOfflineReceive(List<Map<String, Double>> billInvestFeeList, 
			List<t_bill_invest> billInvestList, ServiceType serviceType){
		List<Map<String, String>> pDetails = new LinkedList< Map<String, String>>();
		
		for (int i = 0; i < billInvestList.size(); i++){
			t_bill_invest billInvest = billInvestList.get(i);
			Map<String, Double> billInvestFee = billInvestFeeList.get(i);
			
			//交易订单号
			String orderNo = "";
			
			if (ServiceType.ADVANCE.value.equals(serviceType.value)){
				orderNo = OrderNoUtil.getSpecialOrderNo(billInvest.time, ServiceType.ADVANCE, OrderNoUtil.SPECIAL_BILL_INVEST, billInvest.id);
			} else if (ServiceType.OFFLINE_RECEIVE.value.equals(serviceType.value)){
				orderNo = OrderNoUtil.getSpecialOrderNo(billInvest.time, ServiceType.OFFLINE_RECEIVE, OrderNoUtil.SPECIAL_BILL_INVEST, billInvest.id);
			}
			
			//投资人收益
			double pInAmt = billInvest.receive_interest + billInvest.receive_corpus + billInvestFee.get("overdueFine");
			//投资管理费
			double pInFee = billInvestFee.get("investServiceFee");
			
			//查询投资用户信息
			t_user_fund investUserFund = userFundService.queryUserFundByUserId(billInvest.user_id);
			
			if (investUserFund == null){
				
				return null;
			}
			
			//本金垫付/线下收款列表
			LinkedHashMap<String, String> rMap = new LinkedHashMap<String, String>();
			rMap.put("mchnt_cd", FyConsts.mchnt_cd); //商户代码
			rMap.put("mchnt_txn_ssn", orderNo); //流水号
			rMap.put("out_cust_no", FyConsts.mchnt_name); //出账账户
			rMap.put("in_cust_no", investUserFund.payment_account); //进账账户
			rMap.put("contract_no", ""); //预授权合同号
			rMap.put("amt", (pInAmt - pInFee) + ""); //划拨金额
			rMap.put("rem", FyPayType.REPAYMENT.value); //备注
			pDetails.add(rMap);
		}
		
		return pDetails;
	}
	
	/**
	 * 本息垫付后还款查询（借款人转账给商户）
	 * @param bill 
	 * @param out_cust_no 出账账号
	 * @return
	 */
	public static Map<String, String> queryAdvanceRepayment(t_bill bill, String out_cust_no){
		//固定订单号
		String orderNo = OrderNoUtil.getSpecialOrderNo(bill.time, ServiceType.REPAYMENT_AFER_ADVANCE, OrderNoUtil.SPECIAL_BILL, bill.id);
		
		//获取交易金额信息	
		double transAmt = bill.repayment_corpus + bill.repayment_interest + bill.overdue_fine;
		
		//本息垫付后还款
		LinkedHashMap<String, String> rMap = new LinkedHashMap<String, String>();
		rMap.put("mchnt_cd", FyConsts.mchnt_cd); //商户代码
		rMap.put("mchnt_txn_ssn", orderNo); //流水号
		rMap.put("out_cust_no", out_cust_no); //出账账户
		rMap.put("in_cust_no", FyConsts.mchnt_name); //进账账户
		rMap.put("contract_no", ""); //预授权合同号
		rMap.put("amt", transAmt + ""); //划拨金额
		rMap.put("rem", FyPayType.MERCHANTANDPERSIONTRANSFER.value); //备注
		
		return rMap;
	}
	
	/**
	 * 快速充值
	 * @param pIpsAcctNo 充值用户的第三方账号
	 * @param pTrdAmt 充值金额
	 * @param pOrderNo 交易订单号
	 * @return
	 */
	public LinkedHashMap<String, String> fastRecharge(String pIpsAcctNo, double pTrdAmt, String pOrderNo) {
		LinkedHashMap<String, String> xmlMap = new LinkedHashMap<String, String>();
		
		xmlMap.put("mchnt_cd", FyConsts.mchnt_cd);
		xmlMap.put("mchnt_txn_ssn", pOrderNo);
		xmlMap.put("login_id", pIpsAcctNo);
		xmlMap.put("amt", FyUtils.formatAmountToFen(pTrdAmt));
		xmlMap.put("page_notify_url", BaseController.getBaseURL() + "payment/fy/returnFastRecharge");
		xmlMap.put("back_notify_url", BaseController.getBaseURL() + "payment/fy/returnFastRecharge");
		xmlMap.put("signature", FyUtils.createSign(xmlMap));
		
		return xmlMap;
	}
	
	/**
	 * 查询用户信息
	 * @param order_no 流水号
	 * @param mobile 用户手机号码
	 * @param id_number 用户身份证号码
	 * @param bank_acct 用户银行卡号码
	 * @return
	 */
	public static Map<String, String> queryPersionInformation(String order_no, String mobile, String id_number, String bank_acct) {
		LinkedHashMap<String, String> xmlMap = new LinkedHashMap<String, String>();
		
		Date dt = new Date();
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		String mchnt_txn_dt = df.format(dt);
		
		xmlMap.put("mchnt_cd", FyConsts.mchnt_cd); //商户代码
		xmlMap.put("mchnt_txn_ssn", order_no); //流水号
		xmlMap.put("mchnt_txn_dt", mchnt_txn_dt); //交易日期
		xmlMap.put("user_ids", mobile); //待查询的登录帐户列表
		//xmlMap.put("user_idNos", id_number); //待查询的登录身份证列表
		//xmlMap.put("user_bankCards", bank_acct); //待查询的登录银行卡列表
		xmlMap.put("signature", FyUtils.createSign(xmlMap));
		
		return xmlMap;
	}
	
	/**
	 * 更换用户手机号码
	 * @param order_no 流水号
	 * @param acct 用户第三方账号
	 * @return
	 */
	public static Map<String, String> queryChangeUserMobile(String order_no, String acct) {
		LinkedHashMap<String, String> xmlMap = new LinkedHashMap<String, String>();
		
		xmlMap.put("mchnt_cd", FyConsts.mchnt_cd); //商户代码
		xmlMap.put("mchnt_txn_ssn", order_no); //流水号
		xmlMap.put("login_id", acct); //交易名
		xmlMap.put("page_notify_url", BaseController.getBaseURL() + "payment/fy/returnChangeUserMobile"); //商户返回地址
		xmlMap.put("signature", FyUtils.createSign(xmlMap));
		
		return xmlMap;
	}
	
	/**
	 * 打印查询的信息
	 * @param desc
	 * @param cbParams
	 * @param serviceType
	 * @param payType
	 */
	public static void printQueryData(String desc, Map<String, String> cbParams, FyPayType payType){
		StringBuffer info = new StringBuffer();
		
		info.append("******************【" + desc + "】开始******************");
		
		
		info.append("\n接口类型：" + payType.value);

		for (Entry<String, String> entry : cbParams.entrySet()){		
			try {
				info.append("\n" + entry.getKey() + "--" + java.net.URLDecoder.decode(entry.getValue(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				LoggerUtil.error(false, e, "查询参数解码失败");
			}
		}
		
		info.append("\n******************【" + desc + "】结束******************");
		
		LoggerUtil.info(false, info.toString());
		
		if (payType.isAddRequestRecord && payType.isAddCallBackRecord){
			JPAUtil.transactionBegin();
			
			t_payment_call_back pcb = new t_payment_call_back();
			pcb.time = new Date();
			pcb.request_mark = cbParams.get("mchnt_txn_ssn");
			pcb.cb_params = new Gson().toJson(cbParams);
			pcb.setData_type(t_payment_call_back.DataType.QUERY);
			paymentCallBackDao.save(pcb);
			
			JPAUtil.transactionCommit();
		}
	}
			
    	
	/**
	 * 根据流水号查询充值提现状态
	 * @param order_no 流水号
	 * @param busi_tp 交易类型 
	 * PW11 ：充值 
	 * PWTX： 提现 
	 * PWTP： 退票
	 * @param txn_ssn 交易流水
	 * @param cust_no 用户
	 * @return
	 */
	public static Map<String, String> queryCztx(String order_no, String busi_tp, String txn_ssn, String cust_no) {
		LinkedHashMap<String, String> xmlMap = new LinkedHashMap<String, String>();
		
		Date now = new Date();
		Date begain = DateUtil.addDay(now, -30);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		String start_time = df.format(begain);
		String end_time = df.format(now);
		
		xmlMap.put("ver", "0.49"); //文档版本号
		xmlMap.put("mchnt_cd", FyConsts.mchnt_cd); //商户代码
		xmlMap.put("mchnt_txn_ssn", order_no); //流水号
		xmlMap.put("busi_tp", busi_tp); //交易类型
		xmlMap.put("txn_ssn", txn_ssn); //交易流水
		xmlMap.put("start_time", start_time); //起始时间
		xmlMap.put("end_time", end_time); //截止时间
		xmlMap.put("cust_no", cust_no); //用户
		xmlMap.put("txn_st", ""); //交易状态
		xmlMap.put("page_no", ""); //页码
		xmlMap.put("page_size", ""); //每页条数
		
		xmlMap.put("signature", FyUtils.createSign(xmlMap));
		
		return xmlMap;
	}
	
	/**
	 * 交易查询
	 * @param order_no 流水号
	 * @param busi_tp 交易类型
	 * PWPC 转账
	 * PW13 预授权
	 * PWCF 预授权撤销
	 * PW03 划拨
	 * PW14 转账冻结
	 * PW15 划拨冻结
	 * PWDJ 冻结
	 * PWJD 解冻
	 * PW19 冻结付款到冻结
	 * @param txn_ssn 交易流水
	 * @param cust_no 用户
	 * @return
	 */
	public static Map<String, String> queryTxn(String order_no, String busi_tp, String txn_ssn, String cust_no) {
		LinkedHashMap<String, String> xmlMap = new LinkedHashMap<String, String>();
		
		Date now = new Date();
		Date begain = DateUtil.addDay(now, -30);
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		
		String start_time = df.format(begain);
		String end_time = df.format(now);
		
		xmlMap.put("mchnt_cd", FyConsts.mchnt_cd); //商户代码
		xmlMap.put("mchnt_txn_ssn", order_no); //流水号
		xmlMap.put("busi_tp", busi_tp); //交易类型
		xmlMap.put("start_day", start_time); //起始时间
		xmlMap.put("end_day", end_time); //截止时间
		xmlMap.put("txn_ssn", txn_ssn); //交易流水
		xmlMap.put("cust_no", cust_no); //用户
		xmlMap.put("txn_st", ""); //交易状态
		xmlMap.put("remark", ""); //交易备注
		xmlMap.put("page_no", ""); //页码
		xmlMap.put("page_size", ""); //每页条数
		
		xmlMap.put("signature", FyUtils.createSign(xmlMap));
		
		return xmlMap;
	}
	
	
	
	/**
	 * 组装查询回调日志
	 * @param cbParams
	 * @param is_contract_no 是否获取预授权合同号
	 * @return
	 */
	public static Map<String, String> findParams(Map<String, String> cbParams, ResultInfo result, boolean is_contract_no){
		
		LinkedHashMap<String, String> maps = new LinkedHashMap<String, String>();
		
		if("0".equals(cbParams.get("total_number"))){
			result.code = -1;
			result.msg = "无查询结果";
			return null;
		}
		
		org.json.JSONObject plain;
		org.json.JSONObject plain1;
		org.json.JSONObject results;
		org.json.JSONObject res;
		
		try {
			plain = XML.toJSONObject(cbParams.get("plain"));
			plain1 = plain.getJSONObject("plain");
			results = plain1.getJSONObject("results");
			res = results.getJSONObject("result");
			
			maps.put("data_type", "1");
			maps.put("resp_code", res.getString("txn_rsp_cd"));
			maps.put("amt", res.getString("txn_amt"));
			maps.put("mchnt_txn_ssn", res.getString("mchnt_ssn"));
			
			if(is_contract_no)
				maps.put("contract_no", res.getString("contract_no"));
			
		} catch (JSONException e) {
			Logger.error(e, "解析第三方返回数据时：%s", e.getMessage());
			result.code = -1;
			result.msg = "查询失败";
			return null;
		}
		
		result.code = 1;
		result.msg = "查询成功";
		return maps;
	}
}
