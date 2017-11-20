
package payment.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import common.constants.ConfConst;
import common.constants.PaymentConst;
import common.enums.BusiType;
import common.enums.ServiceType;
import common.enums.TradeType;
import common.utils.ResultInfo;
import fy.FyConsts;
import fy.FyPayment;
import hf.HfConsts;
import hf.HfPayment;
import models.common.entity.t_conversion_user;
import models.core.entity.t_bid;
import models.core.entity.t_bill;
import models.core.entity.t_bill_invest;
import models.core.entity.t_invest;
import models.entity.t_payment_request;
import payment.IPayment;
import play.Logger;

public class PaymentProxy implements IPayment {

	private IPayment payment = null;

	private static PaymentProxy proxy = new PaymentProxy();

	private PaymentProxy() {
	}

	public static PaymentProxy getInstance() {
		return proxy;
	}

	/**
	 * 托管网关初始化。项目启动时，需要调用此方法
	 * 
	 * @author huangyunsong
	 * @createDate 2016年1月7日
	 */
	public void init() {
		if (!ConfConst.IS_TRUST) { // 普通网关
			return;
		}

		Logger.info("####支付网关初始化...");
		if (ConfConst.CURRENT_TRUST_TYPE.equals(PaymentConst.TRUST_TYPE_HF)) {
			Logger.info("####汇付支付网关加载中...");
			payment = new HfPayment();
			HfConsts.initSupport();
		} else if (ConfConst.CURRENT_TRUST_TYPE.equals(PaymentConst.TRUST_TYPE_FY)) {
			Logger.info("####富有支付网关加载中...");
			payment = new FyPayment();
			FyConsts.initSupport();
		}
		Logger.info("####支付网关初始化完毕...");

	}

	@Override
	public ResultInfo regist(int client, String serviceOrderNo, long userId, Object... obj) {

		return payment.regist(client, serviceOrderNo, userId, obj);
	}

	@Override
	public ResultInfo recharge(int client, String serviceOrderNo, long userId, double rechargeAmt, Object... obj) {

		return payment.recharge(client, serviceOrderNo, userId, rechargeAmt, obj);
	}

	@Override
	public ResultInfo userBindCard(int client, String serviceOrderNo, long userId, Object... obj) {

		return payment.userBindCard(client, serviceOrderNo, userId, obj);
	}

	@Override
	public ResultInfo withdrawal(int client, String serviceOrderNo, long userId, double withdrawalAmt,
			String bankAccount, String cashType, Object... obj) {

		return payment.withdrawal(client, serviceOrderNo, userId, withdrawalAmt, bankAccount, cashType, obj);
	}

	@Override
	public ResultInfo bidCreate(int client, String serviceOrderNo, t_bid bid, int bidCreateFrom, Object... obj) {

		return payment.bidCreate(client, serviceOrderNo, bid, bidCreateFrom, obj);
	}

	@Override
	public ResultInfo bidFailed(int client, String serviceOrderNo, t_bid bid, ServiceType serviceType, Object... obj) {

		return payment.bidFailed(client, serviceOrderNo, bid, serviceType, obj);
	}

	@Override
	public ResultInfo invest(int client, int investType, String serviceOrderNo, long userId, t_bid bid,
			double investAmt, Object... obj) {

		return payment.invest(client, investType, serviceOrderNo, userId, bid, investAmt, obj);
	}

	@Override
	public ResultInfo bidSuditSucc(int client, String serviceOrderNo, long releaseSupervisorId, t_bid bid,
			Object... obj) {

		return payment.bidSuditSucc(client, serviceOrderNo, releaseSupervisorId, bid, obj);
	}

	@Override
	public ResultInfo repayment(int client, String serviceOrderNo, t_bill bill,
			List<Map<String, Double>> billInvestFeeList, Object... obj) {

		return payment.repayment(client, serviceOrderNo, bill, billInvestFeeList, obj);
	}

	@Override
	public ResultInfo advanceRepayment(int client, String serviceOrderNo, t_bill bill, Object... obj) {

		return payment.advanceRepayment(client, serviceOrderNo, bill, obj);
	}

	@Override
	public ResultInfo advance(int client, String serviceOrderNo, t_bill bill,
			List<Map<String, Double>> billInvestFeeList, Object... obj) {

		return payment.advance(client, serviceOrderNo, bill, billInvestFeeList, obj);
	}

	@Override
	public ResultInfo offlineReceive(int client, String serviceOrderNo, t_bill bill,
			List<Map<String, Double>> billInvestFeeList, Object... obj) {

		return payment.offlineReceive(client, serviceOrderNo, bill, billInvestFeeList, obj);
	}

	@Override
	public ResultInfo conversion(int client, String serviceOrderNo, t_conversion_user conversion, Object... obj) {

		return payment.conversion(client, serviceOrderNo, conversion);
	}

	@Override
	public ResultInfo queryBindedBankCard(int client, long userId, Object... obj) {

		return payment.queryBindedBankCard(client, userId, obj);
	}

	@Override
	public ResultInfo queryMerchantBalance(int client, Object... obj) {

		return payment.queryMerchantBalance(client, obj);
	}

	@Override
	public ResultInfo merchantRecharge(int client, String serviceOrderNo, double rechargeAmt, String type, String bankId, Object... obj) {

		return payment.merchantRecharge(client, serviceOrderNo, rechargeAmt, type, bankId, obj);
	}

	@Override
	public ResultInfo merchantWithdrawal(int client, String serviceOrderNo, double withdrawalAmt, String type, Object... obj) {

		return payment.merchantWithdrawal(client, serviceOrderNo, withdrawalAmt, type, obj);
	}

	@Override
	public ResultInfo queryFundInfo(int client, String account) {

		return payment.queryFundInfo(client, account);
	}

	@Override
	public String getInterfaceDes(int interfaceType) {

		return payment.getInterfaceDes(interfaceType);
	}

	@Override
	public int getInterfaceType(Enum interfaceType) {

		return payment.getInterfaceType(interfaceType);
	}

	@Override
	public ResultInfo debtTransfer(int clint, String serviceOrderNo, Long debtId, Long userId) {

		return payment.debtTransfer(clint, serviceOrderNo, debtId, userId);
	}

	@Override
	public ResultInfo autoInvestSignature(int client, String serviceOrderNo, long userId, Object... obj) {

		return payment.autoInvestSignature(client, serviceOrderNo, userId, obj);
	}

	@Override
	public ResultInfo autoInvest(int client, int investType, String serviceOrderNo, long userId, t_bid bid,
			double investAmt, Object... obj) {

		return payment.autoInvest(client, investType, serviceOrderNo, userId, bid, investAmt, obj);
	}

	@Override
	public ResultInfo queryPersionInformation(int client, String serviceOrderNo, long userId, String mobile,
			String idNumber, String bankAcct, Object... obj) {

		return payment.queryPersionInformation(client, serviceOrderNo, userId, mobile, idNumber, bankAcct, obj);
	}

	@Override
	public ResultInfo fastRecharge(int client, String serviceOrderNo, long userId, double rechargeAmt, Object... obj) {

		return payment.fastRecharge(client, serviceOrderNo, userId, rechargeAmt, obj);
	}

	@Override
	public ResultInfo changeUserMobile(int client, String serviceOrderNo, long userId, String acct, Object... obj) {

		return payment.changeUserMobile(client, serviceOrderNo, userId, acct, obj);
	}

	@Override
	public ResultInfo queryTxn(int client, String serviceOrderNo, t_payment_request pr, Object... obj) {
		return payment.queryTxn(client, serviceOrderNo, pr, obj);
	}

	@Override
	public ResultInfo queryCztx(int client, String serviceOrderNo, t_payment_request pr, Object... obj) {
		return payment.queryCztx(client, serviceOrderNo, pr, obj);
	}

	@Override
	public ResultInfo merchantTransfer(int client, String serviceOrderNo, long userId, Object... obj) {

		return payment.merchantTransfer(client, serviceOrderNo, userId, obj);
	}

	@Override
	public ResultInfo tenderCancle(int client, String serviceOrderNo, t_invest invest, Object... obj) {

		return payment.tenderCancle(client, serviceOrderNo, invest, obj);
	}

	@Override
	public ResultInfo singleRepayment(int client, String serviceOrderNo, t_bill bill,
			List<Map<String, Double>> billInvestFeeList, Object... obj) {

		return payment.singleRepayment(client, serviceOrderNo, bill, billInvestFeeList, obj);
	}

	@Override
	public ResultInfo sendRate(int client, String serviceOrderNo, t_bill_invest invest, Object... obj) {
		return payment.sendRate(client, serviceOrderNo, invest, obj);
	}

	@Override
	public ResultInfo sendSmsCode(int client, String serviceOrderNo, long userId, String cardId, BusiType busiType,
			String mobile, Object... obj) {
		return payment.sendSmsCode(client, serviceOrderNo, userId, cardId, busiType, mobile, obj);
	}

	@Override
	public ResultInfo userRegist(int client, String serviceOrderNo, long userId, String hfName, String realName,
			String idNumber, String mobile, String cardId, String bankId, String provId, String areaId, String smsCode,
			String smsSeq, Object... obj) {
		return payment.userRegist(client, serviceOrderNo, userId, hfName, realName, idNumber, mobile, cardId, bankId,
				provId, areaId, smsCode, smsSeq, obj);
	}

	@Override
	public ResultInfo bosAcctActivate(int client, String serviceOrderNo, long userId, Object... obj) {
		return payment.bosAcctActivate(client, serviceOrderNo, userId, obj);
	}

	@Override
	public ResultInfo quickBinding(int client, String serviceOrderNo, long userId, String cardId,
			String bankId, String provId, String areaId, String mobile, String smsCode, String smsSeq, String orgSmsExt, Object... obj) {
		return payment.quickBinding(client, serviceOrderNo, userId, cardId, bankId, provId, areaId, mobile,
				smsCode, smsSeq, orgSmsExt, obj);
	}

	@Override
	public ResultInfo directRecharge(int client, String serviceOrderNo, long userId, TradeType tradeType, String bankId,
			double transAmt, String smsCode, String smsSeq, String singId) {
		return payment.directRecharge(client, serviceOrderNo, userId, tradeType, bankId, transAmt, smsCode, smsSeq,
				singId);
	}

	@Override
	public ResultInfo transfer(int client, String serviceOrderNo, long userId, double transAmt, 
			Object... obj) {
		return payment.transfer(client, serviceOrderNo, userId, transAmt, obj);
	}

	@Override
	public ResultInfo queryTransStat(int client, String serviceOrderNo, String queryTransType) {
		return payment.queryTransStat(client, serviceOrderNo, queryTransType);
	}

	@Override
	public ResultInfo trfReconciliation(int client, Date beginDate, Date endDate, String pageNum, String pageSize) {
		return payment.trfReconciliation(client, beginDate, endDate, pageNum, pageSize);
	}

	@Override
	public ResultInfo reconciliation(int client, Date beginDate, Date endDate, String pageNum, String pageSize,
			String queryTransType) {
		return payment.reconciliation(client, beginDate, endDate, pageNum, pageSize, queryTransType);
	}

	@Override
	public ResultInfo cashReconciliation(int client, Date beginDate, Date endDate, String pageNum, String pageSize) {
		return payment.cashReconciliation(client, beginDate, endDate, pageNum, pageSize);
	}

	@Override
	public ResultInfo saveReconciliation(int client, Date beginDate, Date endDate, String pageNum, String pageSize) {
		return payment.saveReconciliation(client, beginDate, endDate, pageNum, pageSize);
	}

	@Override
	public ResultInfo autoTenderCancle(int client, String serviceOrderNo, long userId, double transAmt,
			Map<String, String> unFreezeParam, long investId) {
		return payment.autoTenderCancle(client, serviceOrderNo, userId, transAmt, unFreezeParam, investId);
	}

	@Override
	public ResultInfo corpRegister(int client, String serviceOrderNo, long userId, String usrId, String usrName,
			String instuCode, String busiCode, String taxCode, String guarType,
			Double guarCorpEarnestAmt) {
		return payment.corpRegister(client, serviceOrderNo, userId, usrId, usrName, instuCode, busiCode, taxCode, 
				guarType, guarCorpEarnestAmt);
	}

	@Override
	public ResultInfo corpRegisterQuery(int client, long userId, String busiCode) {
		return payment.corpRegisterQuery(client, userId, busiCode);
	}

	@Override
	public ResultInfo usrUnFreeze(int client, long userId, String ordNo, String freezeTrxId) {
		return payment.usrUnFreeze(client, userId, ordNo, freezeTrxId);
	}

}