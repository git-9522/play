package hf;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.shove.JSONUtils;

import chinapnr.SecureLink;
import common.enums.BusiType;
import common.enums.Client;
import common.enums.ServiceType;
import common.enums.TradeType;
import common.utils.DateUtil;
import common.utils.JPAUtil;
import common.utils.LoggerUtil;
import common.utils.OrderNoUtil;
import common.utils.ResultInfo;
import controllers.common.BaseController;
import models.entity.t_payment_call_back;
import models.entity.t_payment_request;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import play.Logger;
import play.libs.Codec;
import services.PaymentService;

public class HfPaymentService extends PaymentService {

	/**
	 * 添加请求第三方操作记录
	 *
	 * @param userId
	 * @param serviceType
	 * @param payType
	 * @param params
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月7日
	 */
	public long addRequestRecord(long userId, String serviceOrderNo, ServiceType serviceType, String orderNo,
			HfPayType payType) {
		boolean isSave = false;
		t_payment_request pr = new t_payment_request();

		if (payType.isAddRequestRecord) {
			JPAUtil.transactionBegin();

			JPAUtil.transactionCommit();
		}

		return isSave ? pr.id : -1;
	}

	/**
	 * 打印、保存接口请求参数
	 *
	 * @param requestMark
	 *            托管请求唯一标识
	 * @param userId
	 *            关联用户id
	 * @param serviceOrderNo
	 *            业务订单号
	 * @param orderNo
	 *            交易订单号
	 * @param serviceType
	 *            消息类型
	 * @param payType
	 *            汇付操作类型
	 * @param params
	 *            参数
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月7日
	 */
	public void printRequestData(String requestMark, long userId, String serviceOrderNo, String orderNo,
			ServiceType serviceType, HfPayType payType, Map<String, String> reqParams) {
		StringBuffer info = new StringBuffer();
		info.append("******************" + payType.value + "请求开始******************");
		info.append("\n托管业务类型：" + serviceType.value);
		info.append("\n接口类型：" + payType.value);

		for (Entry<String, String> entry : reqParams.entrySet()) {
			info.append("\n" + entry.getKey() + "--" + entry.getValue());
		}

		info.append("\n******************" + payType.value + "请求结束******************");

		Logger.info(info.toString());

		if (payType.isAddRequestRecord) {
			JPAUtil.transactionBegin();

			t_payment_request pr = new t_payment_request();

			pr.time = new Date();
			pr.user_id = userId;
			pr.service_order_no = serviceOrderNo;
			pr.setService_type(serviceType);
			pr.order_no = orderNo;
			pr.setPay_type(payType);
			pr.setStatus(t_payment_request.Status.FAILED); // 先失败，后成功
			pr.ayns_url = reqParams.containsKey("BgRetUrl") ? reqParams.get("BgRetUrl") : "";
			pr.req_params = new Gson().toJson(reqParams);
			pr.mark = requestMark;

			paymentRequstDao.save(pr);

			JPAUtil.transactionCommit();
		}

	}

	/**
	 * 打印、保存接口回调参数
	 *
	 * @param cbParams
	 * @param serviceType
	 * @param payType
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月7日
	 */
	public void printCallBackData(String desc, Map<String, String> cbParams, ServiceType serviceType,
			HfPayType payType) {
		StringBuffer info = new StringBuffer();
		info.append("******************【" + desc + "】开始******************");
		if (serviceType != null) {
			info.append("\n托管业务类型：" + serviceType.value);
		}
		info.append("\n接口类型：" + payType.value);

		for (Entry<String, String> entry : cbParams.entrySet()) {
			try {
				info.append("\n" + entry.getKey() + "--" + java.net.URLDecoder.decode(entry.getValue(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				LoggerUtil.error(false, e, "回调参数解码失败");
			}
		}

		info.append("******************【" + desc + "】结束******************");

		Logger.info(info.toString());

		if (payType.isAddRequestRecord && payType.isAddCallBackRecord) {
			JPAUtil.transactionBegin();

			t_payment_call_back pcb = new t_payment_call_back();
			pcb.time = new Date();
			if (payType.code != HfPayType.BOSACCTACTIVATE.code) {
				pcb.request_mark = cbParams.get("MerPriv");
			}
			pcb.request_mark = cbParams.get("MerPriv");
			pcb.cb_params = new Gson().toJson(cbParams);
			paymentCallBackDao.save(pcb);

			JPAUtil.transactionCommit();
		}

	}

	/**
	 * 用户开户接口，参数拼装
	 *
	 * @param userMobile
	 *            手机号
	 * @param userEmail
	 *            邮箱
	 * @param merPriv
	 *            备注参数
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月9日
	 */
	public LinkedHashMap<String, String> userRegister(String userMobile, String userEmail, String merPriv) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

		map.put("Version", HfConsts.VERSION1); // 版本号
		map.put("CmdId", HfConsts.CMD_USERREGISTER); // 消息类型
		map.put("MerCustId", HfConsts.MERCUSTID); // 客户商户号
		map.put("BgRetUrl", BaseController.getBaseURL() + "payment/chinapnr/userRegisterAyns"); // 商户后台应答地址
		map.put("RetUrl", BaseController.getBaseURL() + "payment/chinapnr/userRegisterSyn"); // 页面返回URL
		if (!StringUtils.isBlank(userMobile)) {
			map.put("UsrMp", userMobile); // 手机号
		}
		if (!StringUtils.isBlank(userEmail)) {
			map.put("UsrEmail", userEmail); // 用户Email
		}

		map.put("MerPriv", merPriv);

		return createSignMap(map);
	}

	/**
	 * 网银充值
	 *
	 * @param usrCustId
	 *            托管账号
	 * @param ordId
	 *            交易订单号
	 * @param transAmt
	 *            交易金额
	 * @param merPriv
	 *            备注参数
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月9日
	 */
	public LinkedHashMap<String, String> netSave(String usrCustId, String ordId, double transAmt, String merPriv) {

		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("Version", HfConsts.VERSION1);// 版本号
		map.put("CmdId", HfConsts.CMD_NETSAVE);// 消息类型
		map.put("MerCustId", HfConsts.MERCUSTID);// 商户客户号
		map.put("UsrCustId", usrCustId); // 用户客户号
		map.put("OrdId", ordId);// 订单号
		map.put("OrdDate", HfUtils.getFormatDate("yyyyMMdd"));// 订单日期
		map.put("TransAmt", HfUtils.formatAmount(transAmt));// 交易金额
		map.put("BgRetUrl", BaseController.getBaseURL() + "payment/chinapnr/netSaveAyns"); // 商户应答地址
		map.put("RetUrl", BaseController.getBaseURL() + "payment/chinapnr/netSaveSyn"); // 页面返回URL
		map.put("MerPriv", merPriv);

		return createSignMap(map);
	}

	/**
	 * 用户绑卡
	 *
	 * @param usrCustId
	 *            托管账户
	 * @param merPriv
	 *            备注参数
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月9日
	 */
	public LinkedHashMap<String, String> userBindCard(String usrCustId, String merPriv) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

		map.put("Version", HfConsts.VERSION1); // 版本号
		map.put("CmdId", HfConsts.CMD_USERBINDCARD); // 消息类型
		map.put("MerCustId", HfConsts.MERCUSTID); // 客户商户号
		map.put("UsrCustId", usrCustId); // 用户唯一标识
		map.put("BgRetUrl", BaseController.getBaseURL() + "payment/chinapnr/userBindCardAyns"); // 商户后台应答地址
		// map.put("RetUrl", ""); // 汇付绑卡无需同步回调地址
		map.put("MerPriv", merPriv);

		return createSignMap(map);
	}

	/**
	 * 用户提现
	 *
	 * @param paymentAccount
	 * @param orderNo
	 * @param withdrawalAmt
	 * @param withdrawalFee
	 * @param bankAccount
	 * @param valueOf
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月11日
	 */
	public Map<String, String> cash(int client, String usrCustId, String ordId, double transAmt, double servFee,
			String openAcctId, String cashType, String merPriv) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		JSONArray array = new JSONArray();
		JSONObject json = new JSONObject();

		if (HfConsts.USER_PAY_WITHDRAW_FEE) { // 汇付向用户收取手续费
			json.put("FeeObjFlag", "U"); // 用户支付取现手续费
			json.put("CashChl", cashType);
			// json.put("CashChl", "GENERAL"); //取现渠道由用户自己决定，这里就不传了
		} else {
			json.put("FeeObjFlag", "M"); // 平台垫付取现手续费
			json.put("FeeAcctId", HfConsts.TRANSFEROUTACCTID);

			json.put("CashChl", cashType); // 平台垫付，为了防止平台垫付过多手续费，这里选择一般取现
		}

		array.add(json);

		String reqExt = array.toString();

		map.put("Version", HfConsts.VERSION2); // 版本号
		map.put("CmdId", HfConsts.CMD_CASH); // 消息类型
		map.put("MerCustId", HfConsts.MERCUSTID); // 商户客户号
		map.put("OrdId", ordId); // 订单号
		map.put("UsrCustId", usrCustId); // 用户客户号
		map.put("TransAmt", HfUtils.formatAmount(transAmt)); // 提现金额
		map.put("ServFee", HfUtils.formatAmount(servFee)); // 商户收取服务费金额
		map.put("ServFeeAcctId", HfConsts.SERVFEEACCTID); // 商户子账户
		map.put("RetUrl", BaseController.getBaseURL() + "payment/chinapnr/cashSyn"); // 商户回调地址
		map.put("BgRetUrl", BaseController.getBaseURL() + "payment/chinapnr/cashAyns"); // 商户后台应答地址
		map.put("OpenAcctId", openAcctId); // 开户银行账号

		map.put("ReqExt", reqExt); // 请求扩展参数
		map.put("MerPriv", merPriv); // 商户私有域
		if (Client.isAppEnum(client) || client == Client.WECHAT.code) {
			map.put("PageType", "1"); // 商户私有域
		}

		return createSignMap(map);
	}

	/**
	 * 标的信息录入
	 *
	 * @param proId
	 * @param borrCustId
	 * @param borrTotAmt
	 * @param yearRate
	 * @param retAmt
	 * @param retDate
	 * @param MerPriv
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月11日
	 */
	public LinkedHashMap<String, String> addBidInfo(String proId, String borrCustId, double borrTotAmt, double yearRate,
			double retAmt, String retDate, String MerPriv) {

		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("Version", HfConsts.VERSION1); // 版本号
		map.put("CmdId", HfConsts.CMD_ADDBIDINFO); // 消息类型
		map.put("MerCustId", HfConsts.MERCUSTID); // 商户客户号
		map.put("ProId", proId); // 项目ID
		map.put("BorrCustId", borrCustId); // 借款人ID
		map.put("BorrTotAmt", HfUtils.formatAmount(borrTotAmt)); // 借款总金额
		map.put("YearRate", HfUtils.formatAmount(yearRate)); // 年利率
		map.put("RetType", "99"); // 还款方式
		map.put("BidStartDate", HfUtils.getFormatDate("yyyyMMddhhmmss"));// 投标开始时间

		Date bidEndDate = DateUtil.addYear(new Date(), 1);
		map.put("BidEndDate", HfUtils.getFormatDate("yyyyMMddhhmmss", bidEndDate)); // 投标截止时间
		map.put("RetAmt", HfUtils.formatAmount(retAmt)); // 应还款总金额
		map.put("RetDate", retDate); // 应还款日期
		map.put("ProArea", "1100"); // 项目所在地
		map.put("BgRetUrl", BaseController.getBaseURL() + "payment/chinapnr/addBidInfoAyns");// 商户后台应答地址
		map.put("MerPriv", MerPriv);

		return createSignMap(map);
	}

	/**
	 * 资金冻结
	 *
	 * @param ordId
	 * @param usrCustId
	 * @param transAmt
	 * @param merPriv
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月11日
	 */
	public LinkedHashMap<String, String> usrFreezeBg(String ordId, String usrCustId, double transAmt, String merPriv) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("Version", HfConsts.VERSION1);
		map.put("CmdId", HfConsts.CMD_USRFREEZE);
		map.put("MerCustId", HfConsts.MERCUSTID);
		map.put("UsrCustId", usrCustId);
		map.put("OrdId", ordId);
		map.put("OrdDate", HfUtils.getFormatDate("yyyyMMdd"));
		map.put("TransAmt", HfUtils.formatAmount(transAmt));
		map.put("BgRetUrl", BaseController.getBaseURL() + "payment/chinapnr/freezeBailAmountAyns");
		map.put("MerPriv", merPriv);

		return createSignMap(map);
	}

	/**
	 * 资金解冻
	 * 
	 * @param ordId
	 *            订单号
	 * @param trxId
	 *            解冻号
	 * @param error
	 * @return
	 */
	public LinkedHashMap<String, String> userUnFreeze(String ordId, String trxId, String merPriv) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("Version", HfConsts.VERSION1);
		map.put("CmdId", HfConsts.CMD_USRUNFREEZE);
		map.put("MerCustId", HfConsts.MERCUSTID);
		map.put("OrdId", ordId);
		map.put("OrdDate", HfUtils.getFormatDate("yyyyMMdd"));
		map.put("TrxId", trxId);
		map.put("BgRetUrl", BaseController.getBaseURL() + "payment/chinapnr/usrUnFreezeAyns");
		map.put("MerPriv", merPriv);

		return createSignMap(map);
	}

	/**
	 * 交易类接口-主动投标
	 * 
	 * @param ordId
	 *            订单号
	 * @param transAmt
	 *            交易金额
	 * @param usrCustId
	 *            投资人用户客户号
	 * @param borrowerDetails
	 *            借款人信息
	 * @param freezeOrdId
	 *            冻结订单号
	 * @param merPriv
	 *            备注参数
	 * @return
	 */
	public LinkedHashMap<String, String> invest(String ordId, double transAmt, String usrCustId, String borrowerDetails,
			String freezeOrdId, String merPriv) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

		map.put("Version", HfConsts.VERSION2); // 版本号
		map.put("CmdId", HfConsts.CMD_INITIATIVETENDER); // 消息类型
		map.put("MerCustId", HfConsts.MERCUSTID); // 商户客户号
		map.put("OrdId", ordId); // 订单号
		map.put("OrdDate", HfUtils.getFormatDate("yyyyMMdd")); // 订单日期
		map.put("TransAmt", HfUtils.formatAmount(transAmt)); // 交易金额
		map.put("UsrCustId", usrCustId); // 用户客户号
		map.put("MaxTenderRate", HfConsts.MAXTENDERRATE); // 最大投资手续费率
		map.put("BorrowerDetails", borrowerDetails);// 借款人信息
		map.put("RetUrl", BaseController.getBaseURL() + "payment/chinapnr/initiativeTenderSyn"); // 商户后台应答地址
		map.put("BgRetUrl", BaseController.getBaseURL() + "payment/chinapnr/initiativeTenderAyns"); // 页面返回URL
		map.put("IsFreeze", "Y");// 是否冻结
		map.put("FreezeOrdId", freezeOrdId); // 冻结订单号
		map.put("MerPriv", merPriv);

		return createSignMap(map);
	}

	/**
	 * 解冻资金
	 *
	 * @param userId
	 *            关联用户ID
	 * @param serviceType
	 *            业务类型
	 * @param payType
	 *            接口类型
	 * @param serviceOrderNo
	 *            业务订单号
	 * @param freezeTrxId
	 *            汇付返回的冻结订单号
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月12日
	 */
	public ResultInfo doUserUnFreeze(long userId, ServiceType serviceType, HfPayType payType, String serviceOrderNo,
			String freezeTrxId) {
		ResultInfo result = new ResultInfo();

		String orderNo = OrderNoUtil.getNormalOrderNo(serviceType); // 全新流水号

		// 托管请求唯一标识
		String requestMark = UUID.randomUUID().toString();

		// 接口参数拼装
		Map<String, String> reqParams = this.userUnFreeze(orderNo, freezeTrxId, requestMark);

		this.printRequestData(requestMark, userId, serviceOrderNo, orderNo, serviceType, payType, reqParams);

		// 请求第三方
		Map<String, String> respParams = HfUtils.httpPost(reqParams);

		this.printCallBackData("解冻响应", respParams, serviceType, payType);

		result = this.checkSign(respParams, HfPayType.USRUNFREEZE);
		if (result.code < 1) {

			return result;
		}

		result.code = 1;
		result.msg = "解冻成功";

		return result;
	}

	/**
	 * 放款
	 * 
	 * @param orderNo
	 *            订单号
	 * @param outCustId
	 *            出账客户号
	 * @param transAmt
	 *            交易金额
	 * @param fee
	 *            扣款手续费
	 * @param subOrdId
	 *            投标订单号
	 * @param subOrdDate
	 *            投标订单日期
	 * @param inCustId
	 *            入账客户号
	 * @param unFreezeOrdId
	 *            解冻号
	 * @param freezeTrxId
	 *            冻结号
	 * @param divDetails
	 *            分账账户
	 * @param reqExt
	 *            扩展
	 * @return
	 */
	public LinkedHashMap<String, String> bidAuditSucc(String orderNo, String outCustId, double transAmt, double fee,
			String subOrdId, String subOrdDate, String inCustId, String unFreezeOrdId, String freezeTrxId,
			String divDetails, String reqExt, String proId, String merPriv) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("Version", HfConsts.VERSION2); // 版本号
		map.put("CmdId", HfConsts.CMD_LOANS); // 消息类型
		map.put("MerCustId", HfConsts.MERCUSTID); // 商户客户号
		map.put("OrdId", orderNo); // 订单号
		map.put("OrdDate", HfUtils.getFormatDate("yyyyMMdd", new Date())); // 订单日期
		map.put("OutCustId", outCustId); // 出账客户号
		map.put("TransAmt", HfUtils.formatAmount(transAmt)); // 交易金额
		map.put("Fee", HfUtils.formatAmount(fee)); // 扣款手续费
		map.put("SubOrdId", subOrdId); // 订单号
		map.put("SubOrdDate", subOrdDate); // 订单日期
		map.put("InCustId", inCustId); // 入账客户号
		map.put("IsDefault", "N"); // 是否默认Y--默认取现到银行卡 N--默认不取现到银行卡
		map.put("IsUnFreeze", "Y"); // 是否解冻
		map.put("UnFreezeOrdId", unFreezeOrdId); // 解冻订单号
		map.put("FreezeTrxId", freezeTrxId); // 冻结标识
		map.put("BgRetUrl", BaseController.getBaseURL() + "payment/chinapnr/loansAyns"); // 商户后台应答地址
		map.put("ReqExt", reqExt); // 入参扩展域
		if (fee > 0) {
			map.put("FeeObjFlag", "I"); // 手续费收取对象标志(I/O)
			map.put("DivDetails", divDetails); // 分账客户串
		}
		map.put("proId", proId); // 标的号
		map.put("MerPriv", merPriv); // 私有域
		return createSignMap(map);
	}

	/**
	 * 用户批量还款接口
	 * 
	 * @param outCustId
	 *            出账商户号
	 * @param batchId
	 *            批量还款订单号
	 * @param merOrdDate
	 *            批量还款日期
	 * @param inDetails
	 *            还款账单列表
	 * @param proId
	 *            借款流水号
	 * @param merPriv
	 *            订单流水号
	 * @return
	 */
	public LinkedHashMap<String, String> repayment(String outCustId, String batchId, String merOrdDate,
			String inDetails, String proId, String merPriv) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

		map.put("Version", HfConsts.VERSION2); // 版本号
		map.put("CmdId", HfConsts.CMD_BATCHREPAYMENT); // 请求指令
		map.put("MerCustId", HfConsts.MERCUSTID); // 客户商户号
		map.put("OutCustId", outCustId); // 出账客户号
		map.put("BatchId", batchId); // 批量订单流水号
		map.put("MerOrdDate", merOrdDate); // 操做时间
		map.put("BgRetUrl", BaseController.getBaseURL() + "payment/chinapnr/batchRepaymentAyns");
		map.put("InDetails", inDetails); // 入账列表
		map.put("ProId", proId); // 标的号即项目号
		map.put("MerPriv", merPriv); // 订单流水号

		return createSignMap(map);
	}

	/**
	 * 垫付还款---用户批量还款接口
	 * 
	 * @param outCustId
	 *            出账商户号
	 * @param batchId
	 *            批量还款订单号
	 * @param merOrdDate
	 *            批量还款日期
	 * @param inDetails
	 *            还款账单列表
	 * @param proId
	 *            借款流水号
	 * @param merPriv
	 *            订单流水号
	 * @return
	 */
	public LinkedHashMap<String, String> advanceRepayment(String outCustId, String batchId, String merOrdDate,
			String inDetails, String proId, String merPriv) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

		map.put("Version", HfConsts.VERSION2); // 版本号
		map.put("CmdId", HfConsts.CMD_BATCHREPAYMENT); // 请求指令
		map.put("MerCustId", HfConsts.MERCUSTID); // 客户商户号
		map.put("OutCustId", outCustId); // 出账客户号
		map.put("BatchId", batchId); // 批量订单流水号
		map.put("MerOrdDate", merOrdDate); // 操做时间
		map.put("BgRetUrl", BaseController.getBaseURL() + "payment/chinapnr/advanceRepaymentAyns");
		map.put("InDetails", inDetails); // 入账列表
		map.put("ProId", proId); // 标的号即项目号
		map.put("MerPriv", merPriv); // 订单流水号

		return createSignMap(map);
	}

	/**
	 * 本息垫付批量还款接口
	 * 
	 * @param batchId
	 *            批量还款订单号
	 * @param merOrdDate
	 *            批量还款日期
	 * @param inDetails
	 *            还款账单列表
	 * @param proId
	 *            借款流水号
	 * @param merPriv
	 *            订单流水号
	 * @return
	 */
	public LinkedHashMap<String, String> advance(String batchId, String merOrdDate, String inDetails, String proId,
			String merPriv) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

		map.put("Version", HfConsts.VERSION2); // 版本号
		map.put("CmdId", HfConsts.CMD_BATCHREPAYMENT); // 请求指令
		map.put("MerCustId", HfConsts.MERCUSTID); // 客户商户号
		map.put("OutCustId", HfConsts.MERCUSTID); // 出账客户号
		map.put("OutAcctId", HfConsts.TRANSFEROUTACCTID); // 出账子账户
		map.put("BatchId", batchId); // 批量订单流水号
		map.put("MerOrdDate", merOrdDate); // 操做时间
		map.put("BgRetUrl", BaseController.getBaseURL() + "payment/chinapnr/advanceAyns");
		map.put("InDetails", inDetails); // 入账列表
		map.put("ProId", proId); // 标的号即项目号
		map.put("MerPriv", merPriv); // 订单流水号

		return createSignMap(map);
	}

	/**
	 * 线下收款批量还款接口
	 * 
	 * @param batchId
	 *            批量还款订单号
	 * @param merOrdDate
	 *            批量还款日期
	 * @param inDetails
	 *            还款账单列表
	 * @param proId
	 *            借款流水号
	 * @param merPriv
	 *            订单流水号
	 * @return
	 */
	public LinkedHashMap<String, String> offlineReceive(String batchId, String merOrdDate, String inDetails,
			String proId, String merPriv) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

		map.put("Version", HfConsts.VERSION2); // 版本号
		map.put("CmdId", HfConsts.CMD_BATCHREPAYMENT); // 请求指令
		map.put("MerCustId", HfConsts.MERCUSTID); // 客户商户号
		map.put("OutCustId", HfConsts.MERCUSTID); // 出账客户号
		map.put("OutAcctId", HfConsts.TRANSFEROUTACCTID); // 出账子账户
		map.put("BatchId", batchId); // 批量订单流水号
		map.put("MerOrdDate", merOrdDate); // 操做时间
		map.put("BgRetUrl", BaseController.getBaseURL() + "payment/chinapnr/offlineReceiveAyns");
		map.put("InDetails", inDetails); // 入账列表
		map.put("ProId", proId); // 标的号即项目号
		map.put("MerPriv", merPriv); // 订单流水号

		return createSignMap(map);
	}

	/**
	 * 用户支付接口
	 * 
	 * @param ordId
	 *            订单号
	 * @param usrCustId
	 *            用户商户号
	 * @param transAmt
	 *            交易金额
	 * @return
	 */
	public LinkedHashMap<String, String> usrAcctPay(String ordId, String usrCustId, double transAmt, String retUrl,
			String bgRetUrl, String merPriv) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

		map.put("Version", HfConsts.VERSION1); // 版本号
		map.put("CmdId", HfConsts.CMD_USRACCPAY); // 交易类型
		map.put("OrdId", ordId); // 订单号
		map.put("UsrCustId", usrCustId); // 用户客户号
		map.put("MerCustId", HfConsts.MERCUSTID); // 商户客户号
		map.put("TransAmt", HfUtils.formatAmount(transAmt)); // 交易金额
		map.put("InAcctId", HfConsts.SERVFEEACCTID); // 入账子账户
		map.put("InAcctType", "MERDT"); // 入账账户类型
		map.put("RetUrl", retUrl); // 页面返回URL
		map.put("BgRetUrl", bgRetUrl); // 商户后台应答地址
		map.put("MerPriv", merPriv);

		return createSignMap(map);
	}

	/**
	 * 转账,商户转用户 （商户用）
	 * 
	 * @param ordId
	 *            订单号
	 * @param transAmt
	 *            交易金额
	 * @param inCustId
	 *            入账账户
	 * @return
	 */
	public LinkedHashMap<String, String> doTransfer(String ordId, double transAmt, String bgRetUrl, String inCustId,
			String merPriv) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("Version", HfConsts.VERSION1); // 版本号
		map.put("OrdId", ordId);
		map.put("CmdId", HfConsts.CMD_TRANSFER); // 消息类型
		map.put("OutCustId", HfConsts.MERCUSTID);
		map.put("OutAcctId", HfConsts.SERVFEEACCTID);
		map.put("TransAmt", HfUtils.formatAmount(transAmt));
		map.put("InCustId", inCustId);
		map.put("BgRetUrl", bgRetUrl);
		map.put("MerPriv", merPriv); // 请求操作唯一标识，通常为交易流水号

		return createSignMap(map);
	}

	/**
	 * 查询用户绑定银行卡
	 *
	 * @param usrCustId
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月19日
	 */
	public LinkedHashMap<String, String> queryBindedBankCard(String usrCustId) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("Version", HfConsts.VERSION1);// 版本号
		map.put("CmdId", HfConsts.CMD_QUERYCARDINFO);// 消息类型
		map.put("MerCustId", HfConsts.MERCUSTID);// 商户客户号
		map.put("UsrCustId", usrCustId); // 用户客户号

		return createSignMap(map);
	}

	/**
	 * 用户账户信息查询
	 *
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月26日
	 */
	public LinkedHashMap<String, String> queryBalanceBg(String usrCustId) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("Version", HfConsts.VERSION1);// 版本号
		map.put("CmdId", HfConsts.CMD_QUERYBALANCEBG);// 消息类型
		map.put("MerCustId", HfConsts.MERCUSTID);// 商户客户号
		map.put("UsrCustId", usrCustId);

		return createSignMap(map);
	}

	/**
	 * 商户账户信息查询
	 *
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月26日
	 */
	public LinkedHashMap<String, String> queryAccts() {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("Version", HfConsts.VERSION1);// 版本号
		map.put("CmdId", HfConsts.CMD_QUERYACCTS);// 消息类型
		map.put("MerCustId", HfConsts.MERCUSTID);// 商户客户号

		return createSignMap(map);
	}

	/**
	 * 商户充值-网银充值
	 * 
	 * @param usrCustId
	 *            用户客户号
	 * @param ordId
	 *            订单号
	 * @param transAmt
	 *            交易金额
	 * @return
	 */
	public LinkedHashMap<String, String> merchantRecharge(String ordId, String usrCustId, Double transAmt,
			String merPriv, String type, String bankId) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("Version", HfConsts.VERSION1); // 版本号
		map.put("CmdId", HfConsts.CMD_DIRECTRECHARGE); // 消息类型
		map.put("MerCustId", HfConsts.MERCUSTID); // 商户客户号
		map.put("UsrCustId", usrCustId); // 用户客户号
		map.put("OrdId", ordId); // 订单号
		map.put("OrdDate", HfUtils.getFormatDate("yyyyMMdd")); // 订单日期
		map.put("GateBusiId", "B2C"); // 订单日期
		map.put("BankId", bankId); // 订单日期
		map.put("TransAmt", HfUtils.formatAmount(transAmt)); // 交易金额
		map.put("SubAcctId", type); // 交易金额
		map.put("RetUrl", BaseController.getBaseURL() + "payment/chinapnr/merNetSaveSyn"); // 页面返回URL
		map.put("BgRetUrl", BaseController.getBaseURL() + "payment/chinapnr/merNetSaveAyns"); // 商户应答地址
		return createSignMap(map);
	}

	/**
	 * 商户提现
	 * 
	 * @param usrCustId
	 *            用户客户号
	 * @param ordId
	 *            订单号
	 * @param transAmt
	 *            交易金额
	 * @return
	 */
	public LinkedHashMap<String, String> merchantWithdrawal(String ordId, String usrCustId, Double transAmt,
			String merPriv) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

		// [{"FeeObjFlag":"M/U","FeeAcctId":"MDT000001","CashChl":"FAST|GENERAL|IMMEDIATE"}]
		JSONArray array = new JSONArray();
		JSONObject json = new JSONObject();
		json.put("FeeObjFlag", "M");
		json.put("FeeAcctId", HfConsts.TRANSFEROUTACCTID);
		array.add(json);

		String ReqExt = array.toString();

		map.put("Version", HfConsts.VERSION2); // 版本号
		map.put("CmdId", HfConsts.CMD_CASH); // 消息类型
		map.put("MerCustId", HfConsts.MERCUSTID); // 商户客户号
		map.put("OrdId", ordId); // 订单号
		map.put("UsrCustId", usrCustId); // 用户客户号
		map.put("TransAmt", HfUtils.formatAmount(transAmt)); // 交易金额
		map.put("ServFee", HfUtils.formatAmount(0.00)); // 商户收取服务费金额
		map.put("ServFeeAcctId", HfConsts.SERVFEEACCTID); // 商户子账户
		map.put("RetUrl", BaseController.getBaseURL() + "payment/chinapnr/merCashSyn"); // 页面返回URL
		map.put("BgRetUrl", BaseController.getBaseURL() + "payment/chinapnr/merCashAyns"); // 商户后台应答地址
		map.put("MerPriv", merPriv); // 商户私有域
		map.put("ReqExt", ReqExt);

		return createSignMap(map);
	}

	/**
	 * 奖励兑换 - 转账,商户转用户 （商户用）
	 * 
	 * @param ordId
	 *            订单号
	 * @param transAmt
	 *            交易金额
	 * @param inCustId
	 *            入账账户
	 * @return
	 */
	public LinkedHashMap<String, String> conversion(String ordId, double transAmt, String inCustId, String merPriv) {

		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("Version", HfConsts.VERSION1); // 版本号
		map.put("OrdId", ordId);
		map.put("CmdId", HfConsts.CMD_TRANSFER); // 消息类型
		map.put("OutCustId", HfConsts.MERCUSTID);
		map.put("OutAcctId", HfConsts.SERVFEEACCTID);
		map.put("TransAmt", HfUtils.formatAmount(transAmt));
		map.put("InCustId", inCustId);
		map.put("BgRetUrl", BaseController.getBaseURL() + "payment/chinapnr/conversionAyns");
		map.put("MerPriv", merPriv);

		return createSignMap(map);
	}

	/**
	 * 账户资金信息查询
	 *
	 * @param usrCustId
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年2月15日
	 */
	public LinkedHashMap<String, String> queryFundInfo(String usrCustId) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("Version", HfConsts.VERSION1);
		map.put("CmdId", HfConsts.CMD_QUERYBALANCEBG);
		map.put("MerCustId", HfConsts.MERCUSTID);
		map.put("UsrCustId", usrCustId);

		return createSignMap(map);
	}

	/**
	 * 获取生成签名后的Map集合
	 *
	 * @param parmas
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月7日
	 */
	public static LinkedHashMap<String, String> createSignMap(LinkedHashMap<String, String> parmas) {
		String sign = createSign(parmas);
		parmas.put("ChkValue", sign);

		return parmas;
	}

	/**
	 * 生成签名
	 *
	 * @param parmas
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月7日
	 */
	private static String createSign(Map<String, String> parmas) {
		// 接口类型
		String cmdId = (String) parmas.get("CmdId");

		String[] chkKeys = HfConsts.getChkValueKeys(cmdId);
		StringBuffer buffer = new StringBuffer();
		SecureLink sl = new SecureLink();
		if (chkKeys != null) {

			// 拼接签名原串
			for (String key : chkKeys) {
				String value = (String) (parmas.get(key) == null ? "" : parmas.get(key));
				buffer.append(value);
			}

			String value = buffer.toString();

			Logger.info("签名串原形");
			Logger.info(value);

			// 批量还款接口传输参数需要加密
			if (HfConsts.CMD_BATCHREPAYMENT.equals(cmdId) || HfConsts.CMD_SENDSMSCODE.equals(cmdId)
					|| HfConsts.CMD_BOSACCTACTIVATE.equals(cmdId) || HfConsts.CMD_QUICKBINDING.equals(cmdId)
					|| HfConsts.CMD_DIRECTRECHARGE.equals(cmdId) || HfConsts.CMD_REPAYMENT.equals(cmdId)
					|| HfConsts.CMD_AUTOTENDERCANCLE.equals(cmdId) || HfConsts.CMD_CREDITASSIGN.equals(cmdId)) {
				value = Codec.hexMD5(buffer.toString());
			}

			try {
				sl.SignMsg(HfConsts.MERID, HfConsts.MER_PRI_KEY_PATH, value.getBytes("utf-8"));
			} catch (Exception e) {

				LoggerUtil.error(true, e, "生成签名失败");
			}
		}

		return sl.getChkValue();
	}

	/**
	 * 签名,状态码，防重复处理校验
	 *
	 * @param cdParams
	 *            回调参数
	 * @param payType
	 *            接口类型
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月7日
	 */
	public static ResultInfo checkSign(Map<String, String> cdParams, HfPayType payType) {
		ResultInfo result = new ResultInfo();

		// 第一步:连接超时
		if (cdParams == null) {
			result.code = -1;
			result.msg = payType.value + "连接超时";

			return result;
		}

		// 第二步:签名判断
		String cmdId = cdParams.get("CmdId");

		// 获取需要签名的字段
		String[] keys = HfConsts.getRespChkValueKeys(cmdId);

		StringBuffer buffer = new StringBuffer();
		for (String key : keys) {
			if (StringUtils.isBlank(cdParams.get(key)))
				continue;

			try {
				buffer.append(URLDecoder.decode(cdParams.get(key).trim(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				LoggerUtil.error(false, e, "回调参数解码失败");
			}
		}

		Logger.info("签名原串--" + buffer.toString());

		String res = buffer.toString();

		// 第三方签名串
		String chkValue = cdParams.get("ChkValue").trim();
		if (HfConsts.CMD_SENDSMSCODE.equals(cmdId) || HfConsts.CMD_BOSACCTACTIVATE.equals(cmdId)
				|| HfConsts.CMD_QUICKBINDING.equals(cmdId) || HfConsts.CMD_DIRECTRECHARGE.equals(cmdId)
				|| HfConsts.CMD_REPAYMENT.equals(cmdId) || HfConsts.CMD_AUTOTENDERCANCLE.equals(cmdId)
				|| HfConsts.CMD_CREDITASSIGN.equals(cmdId)) {
			res = Codec.hexMD5(res);
		}
		boolean verify = HfUtils.verifyByRSA(res, chkValue);
		if (!verify) {
			result.code = -1;
			result.msg = payType.value + "签名验证失败";

			return result;
		}

		// 第三步:状态码判断;根据不同接口，不同处理
		String respCode = cdParams.get("RespCode");
		String respDesc = "";
		try {
			respDesc = URLDecoder.decode(cdParams.get("RespDesc"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LoggerUtil.error(false, e, "回调参数RespDesc解码失败");
		}

		switch (payType) {
		case LOANS: {
			// 345重复的放款请求, 393投资人出账户金额大于本应出款金额 ，当做成功放款处理
			if (!(HfConsts.SUCCESS_CODE.equals(respCode) || "345".equals(respCode) || "393".equals(respCode))) {
				result.code = -1;
				result.msg = payType.value + respDesc;

				return result;
			}
			break;
		}
		case USRUNFREEZE: {
			if (!(HfConsts.SUCCESS_CODE.equals(respCode) || "107".equals(respCode))) { // 107重复交易，则冻结资金已经被解冻
				result.code = -1;
				result.msg = payType.value + respDesc;

				return result;
			}
			break;
		}
		default: {
			if (!HfConsts.SUCCESS_CODE.equals(respCode)) {
				result.code = -1;
				result.msg = payType.value + respDesc;

				return result;
			}

			break;
		}
		}

		// 根据特殊的支付类型做特殊操作
		if (HfPayType.BATCHREPAYMENT.equals(payType)) {
			// 解析批量还款返回参数
			result = resolveBatchRepaymentResp(cdParams);
			if (result.code < 1) {

				return result;
			}
		}

		if (payType.isAddRequestRecord) {
			// 第四步，防止重单
			t_payment_request pr = null;
			if (payType.code == HfPayType.DIRECTRECHARGE.code || payType.code == HfPayType.BOSACCTACTIVATE.code) {
				String ordId = cdParams.get("OrdId");
				pr = paymentRequstDao.findByColumn("order_no = ?", ordId);
			} else {
				String requestMark = cdParams.get("MerPriv").toString();
				pr = paymentRequstDao.findByColumn("mark = ?", requestMark);
			}

			if (pr == null) {
				result.code = -1;
				result.msg = payType.value + "查询托管请求记录失败";

				return result;
			}
			int row = 0;

			if (payType.code == HfPayType.DIRECTRECHARGE.code || payType.code == HfPayType.BOSACCTACTIVATE.code) {
				row = paymentRequstDao.updateReqStatusToSuccessByOrdId(cdParams.get("OrdId").toString());
			} else {
				if (HfPayType.CORPREGISTER.code == payType.code && !"Y".equals(cdParams.get("AuditStat"))) {
					// 当为企业开户时，且企业开户状态不为Y时，模拟更新成功
					row = 1;
				} else {
					row = paymentRequstDao.updateReqStatusToSuccess(cdParams.get("MerPriv").toString());
				}
			}

			if (row > 0) {
				result.code = 1;
				result.msg = payType.value + "更新请求状态成功";

				return result;
			}

			if (row == 0) {
				result.code = ResultInfo.ALREADY_RUN;
				result.msg = payType.value + "已成功执行";

				return result;
			}

			if (row < 0) {
				result.code = ResultInfo.ERROR_SQL;
				result.msg = payType.value + "更新请求状态时，数据库异常";

				return result;
			}
		}

		result.code = 1;
		result.msg = payType.value + "更新请求状态成功";

		return result;
	}

	/**
	 * 解析批量还款请求
	 * 
	 * @param result
	 * @return
	 */
	private static ResultInfo resolveBatchRepaymentResp(Map<String, String> paramMap) {

		ResultInfo result = new ResultInfo();

		if (!HfConsts.SUCCESS_CODE.equals(paramMap.get("RespCode"))) {
			result.code = -1;
			result.msg = "还款失败";

			return result;
		}

		// 失败条数
		int failNum = Integer.valueOf(paramMap.get("FailNum"));
		if (failNum <= 0) {
			result.code = 1;
			result.msg = "还款成功";

			return result;
		}

		// 失败条数大于0时，排出掉重复还款的情况
		JSONArray array = JSONArray.fromObject(paramMap.get("ErrMsg"));
		for (Object obj : array) {
			JSONObject jsonObj = JSONObject.fromObject(obj);
			// 排出重复还款的情况
			if (HfConsts.REPAYMENT_ED.equals(jsonObj.get("ItemCode").toString())) {
				failNum--;
			}
		}
		if (failNum > 0) {
			result.code = -1;
			result.msg = "部分还款失败,失败条数" + failNum;

			return result;
		}

		result.code = 1;
		result.msg = "还款成功";

		return result;
	}

	/** 是否超额投标 **/
	public boolean isOverBidAmount(String serviceOrderNo) {

		List<t_payment_request> list = paymentRequstDao.findListByColumn("service_order_no = ?", serviceOrderNo);
		if (list != null && list.size() >= 2) { // 正常投标只有一次请求（主动投标），超额投标会有两次请求（主动投标，解冻投标金额）

			return true;
		}

		return false;
	}

	/**
	 * 自动投标签约
	 * 
	 * @param usrCustId
	 *            投资人用户客户号
	 * @param merPriv
	 *            备注参数
	 * @return
	 */
	public LinkedHashMap<String, String> autoInvestSignature(String usrCustId, String merPriv) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

		map.put("Version", HfConsts.VERSION2); // 版本号
		map.put("CmdId", HfConsts.CMD_AUTOTENDERPLAN); // 消息类型
		map.put("MerCustId", HfConsts.MERCUSTID); // 商户客户号
		map.put("UsrCustId", usrCustId); // 用户客户号
		map.put("TenderPlanType", HfConsts.TENDERPLANTYPE);
		map.put("RetUrl", BaseController.getBaseURL() + "payment/chinapnr/autoInvestSignature");
		map.put("MerPriv", merPriv);

		return createSignMap(map);
	}

	/**
	 * 交易类接口-自动投标
	 * 
	 * @param ordId
	 *            订单号
	 * @param transAmt
	 *            交易金额
	 * @param usrCustId
	 *            投资人用户客户号
	 * @param borrowerDetails
	 *            借款人信息
	 * @param freezeOrdId
	 *            冻结订单号
	 * @param merPriv
	 *            备注参数
	 * @return
	 */
	public LinkedHashMap<String, String> autoInvest(String ordId, double transAmt, String usrCustId,
			String borrowerDetails, String freezeOrdId, String merPriv) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

		map.put("Version", HfConsts.VERSION2); // 版本号
		map.put("CmdId", HfConsts.CMD_AUTOTENDER); // 消息类型
		map.put("MerCustId", HfConsts.MERCUSTID); // 商户客户号
		map.put("OrdId", ordId); // 订单号
		map.put("OrdDate", HfUtils.getFormatDate("yyyyMMdd")); // 订单日期
		map.put("TransAmt", HfUtils.formatAmount(transAmt)); // 交易金额
		map.put("UsrCustId", usrCustId); // 用户客户号
		map.put("MaxTenderRate", HfConsts.MAXTENDERRATE); // 最大投资手续费率
		map.put("BorrowerDetails", borrowerDetails);// 借款人信息
		map.put("BgRetUrl", BaseController.getBaseURL() + "payment/chinapnr/autoTenderAyns"); // 商户后台应答地址
		map.put("IsFreeze", "Y");// 是否冻结
		map.put("FreezeOrdId", freezeOrdId); // 冻结订单号
		map.put("MerPriv", merPriv);

		return createSignMap(map);
	}

	public static void main(String[] args) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("Version", "30"); // 版本号 update 2017-09-19
		map.put("CmdId", "CreditAssign"); // 消息类型
		map.put("MerCustId", "6000060007160887"); // 商户客户号
		map.put("SellCustId", "6000060007356649"); // 转让人客户号
		map.put("CreditAmt", "198800.00"); // 转让金额
		map.put("CreditDealAmt", "198800.00"); // 承接金额
		map.put("BidDetails",
				"{\"BidDetails\":[{\"BidOrdId\":\"170824330102683\",\"BidOrdDate\":\"20170824\",\"BidCreditAmt\":\"198800.00\",\"BorrowerDetails\":[{\"BorrowerCustId\":\"6000060007319627\",\"BorrowerCreditAmt\":\"198800.00\",\"PrinAmt\":\"0.00\",\"ProId\":\"170823102602\"}]}]}"); // 债权转让明细
		map.put("Fee", "198.80"); // 扣款手续费
		map.put("BuyCustId", "6000060007390291"); // 承接人客户号
		map.put("OrdId", "170919470104726"); // 订单号
		map.put("OrdDate", "20170919"); // 订单日期
		map.put("BgRetUrl", "http://test.hjs360.cn/payment/chinapnr/debtorTransferSyn"); // 商户后台应答地址
		map.put("RetUrl", "http://test.hjs360.cn/payment/chinapnr/debtorTransferSyn"); // 页面返回URL
		map.put("MerPriv", "14c227ee-3abf-4152-a506-5ba99e4aa487"); // 接口请求唯一标识，用于回调时获取日志参数
		map.put("DivDetails", "[{\"DivAcctId\":\"MDT000001\",\"DivAmt\":\"" + "198.80" + "\"}]");
		System.out.println(JSONUtils.toJSONString(createSignMap(map)));
	}

	/**
	 * 债权转让数据拼接
	 *
	 * @param serviceOrderNo
	 *            业务订单号
	 * @param sellCustId
	 *            转让人客户号
	 * @param cretAmt
	 *            转让金额(本金)
	 * @param creditDealAmt
	 *            承接金额(成交价)
	 * @param bidDetails
	 *            (标记详细信息)
	 * @param fee
	 *            转让服务费
	 * @param buyCustId
	 *            承接人客户号
	 * @param requestMark
	 *            请求唯一标识
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月30日
	 */
	public Map<String, String> debtTransfer(String serviceOrderNo, String sellCustId, double cretAmt,
			double creditDealAmt, String bidDetails, double fee, String buyCustId, String requestMark) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

		map.put("Version", HfConsts.VERSION3); // 版本号 update 2017-09-19
		map.put("CmdId", HfConsts.CMD_CREDITASSIGN); // 消息类型
		map.put("MerCustId", HfConsts.MERCUSTID); // 商户客户号
		map.put("SellCustId", sellCustId); // 转让人客户号
		map.put("CreditAmt", HfUtils.formatAmount(cretAmt)); // 转让金额
		map.put("CreditDealAmt", HfUtils.formatAmount(creditDealAmt)); // 承接金额
		map.put("BidDetails", bidDetails); // 债权转让明细
		map.put("Fee", HfUtils.formatAmount(fee)); // 扣款手续费
		map.put("BuyCustId", buyCustId); // 承接人客户号
		map.put("OrdId", serviceOrderNo); // 订单号
		map.put("OrdDate", HfUtils.getFormatDate("yyyyMMdd")); // 订单日期
		map.put("BgRetUrl", BaseController.getBaseURL() + "payment/chinapnr/debtorTransferAyns"); // 商户后台应答地址
		map.put("RetUrl", BaseController.getBaseURL() + "payment/chinapnr/debtorTransferSyn"); // 页面返回URL
		map.put("MerPriv", requestMark); // 接口请求唯一标识，用于回调时获取日志参数

		if (fee != 0) {
			map.put("DivDetails", "[{\"DivAcctId\":\"" + HfConsts.SERVFEEACCTID + "\",\"DivAmt\":\""
					+ HfUtils.formatAmount(fee) + "\"}]");
		}

		return createSignMap(map);
	}

	/**
	 * 红包投资
	 * 
	 * @param ordId
	 * @param transAmt
	 * @param usrCustId
	 * @param borrowerDetails
	 * @param freezeOrdId
	 * @param merPriv
	 * @return
	 */
	public LinkedHashMap<String, String> investRedPacket(String ordId, double transAmt, String usrCustId,
			String borrowerDetails, String freezeOrdId, String merPriv, double redPacketAmt) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

		map.put("Version", HfConsts.VERSION2); // 版本号
		map.put("CmdId", HfConsts.CMD_INITIATIVETENDER); // 消息类型
		map.put("MerCustId", HfConsts.MERCUSTID); // 商户客户号
		map.put("OrdId", ordId); // 订单号
		map.put("OrdDate", HfUtils.getFormatDate("yyyyMMdd")); // 订单日期
		map.put("TransAmt", HfUtils.formatAmount(transAmt)); // 交易金额
		map.put("UsrCustId", usrCustId); // 用户客户号
		map.put("MaxTenderRate", HfConsts.MAXTENDERRATE); // 最大投资手续费率
		map.put("BorrowerDetails", borrowerDetails);// 借款人信息
		map.put("RetUrl", BaseController.getBaseURL() + "payment/chinapnr/initiativeTenderSyn"); // 商户后台应答地址
		map.put("BgRetUrl", BaseController.getBaseURL() + "payment/chinapnr/initiativeTenderAyns"); // 页面返回URL
		map.put("IsFreeze", "Y");// 是否冻结
		map.put("FreezeOrdId", freezeOrdId); // 冻结订单号
		map.put("ReqExt", "{\"Vocher\":{" + "\"AcctId\":\"" + HfConsts.SERVFEEACCTID + "\"," + "\"VocherAmt\":" + "\""
				+ HfUtils.formatAmount(redPacketAmt) + "\"}}");
		map.put("MerPriv", merPriv);

		return createSignMap(map);
	}

	/**
	 * 商户转账
	 * 
	 * @param ordId
	 *            订单号
	 * @param transAmt
	 *            交易金额
	 * @param inCustId
	 *            入账账户
	 * @param merPriv
	 *            扩展参数
	 * 
	 * @return
	 */
	public LinkedHashMap<String, String> merchantTransfer(String ordId, double transAmt, String inCustId,
			String merPriv) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("Version", HfConsts.VERSION1); // 版本号
		map.put("CmdId", HfConsts.CMD_TRANSFER); // 消息类型
		map.put("OrdId", ordId);
		map.put("OutCustId", HfConsts.MERCUSTID);
		map.put("OutAcctId", HfConsts.SERVFEEACCTID);
		map.put("TransAmt", HfUtils.formatAmount(transAmt));
		map.put("InCustId", inCustId);
		map.put("BgRetUrl", BaseController.getBaseURL() + "payment/chinapnr/merchantTransferAyns");
		map.put("MerPriv", merPriv);

		return createSignMap(map);
	}

	/**
	 * 商户转账(投标奖励、加息卷)
	 * 
	 * @param ordId
	 *            订单号
	 * @param transAmt
	 *            交易金额
	 * @param inCustId
	 *            入账账户
	 * @param merPriv
	 *            扩展参数
	 * 
	 * @return
	 */
	public LinkedHashMap<String, String> sendRate(String ordId, double transAmt, String inCustId, String merPriv) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("Version", HfConsts.VERSION1); // 版本号
		map.put("CmdId", HfConsts.CMD_TRANSFER); // 消息类型
		map.put("OrdId", ordId);
		map.put("OutCustId", HfConsts.MERCUSTID);
		map.put("OutAcctId", HfConsts.SERVFEEACCTID);
		map.put("TransAmt", HfUtils.formatAmount(transAmt));
		map.put("InCustId", inCustId);
		map.put("BgRetUrl", BaseController.getBaseURL() + "payment/chinapnr/sendRateAyns");
		map.put("MerPriv", merPriv);

		return createSignMap(map);
	}

	/**
	 * 投标的撤销
	 * 
	 * @param ordId
	 * @param trxId
	 * @param serviceOrderNo
	 * @param amount
	 * @param usrCustId
	 * @param requestMark
	 * @return
	 */
	public Map<String, String> tenderCancleBid(String ordId, String trxId, String serviceOrderNo, double amount,
			String usrCustId, String requestMark) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("Version", HfConsts.VERSION2); // 版本号
		map.put("CmdId", HfConsts.CMD_TENDERCANCLE); // 消息类型
		map.put("MerCustId", HfConsts.MERCUSTID); // 商户客户号
		map.put("OrdId", ordId); // 订单号
		map.put("OrdDate", HfUtils.getFormatDate("yyyyMMdd")); // 订单时间
		map.put("TransAmt", HfUtils.formatAmount(amount)); // 交易金额
		map.put("UsrCustId", usrCustId); // 用户客户号
		map.put("IsUnFreeze", "Y"); // 是否解冻
		map.put("UnFreezeOrdId", serviceOrderNo); // 解冻订单号
		map.put("FreezeTrxId", trxId); // 冻结标识

		map.put("RetUrl", BaseController.getBaseURL() + "payment/chinapnr/TenderCancleAyns"); // 页面返回
																								// URL
		map.put("BgRetUrl", BaseController.getBaseURL() + "payment/chinapnr/TenderCancleAyns"); // 商户后台应答地址
		map.put("MerPriv", requestMark); // 商户私有域

		return createSignMap(map);
	}

	/**
	 * 自动还款接口
	 * 
	 * @param bidProId
	 *            标的订单号
	 * @param OrdId
	 *            订单号
	 * @param investProId
	 *            投资订单号
	 * @param investOrdDate
	 *            投资订单日期
	 * @param outCustId
	 *            出账子账号
	 * @param principalAmt
	 *            还款本金
	 * @param interestAmt
	 *            还款利息
	 * @param fee
	 *            扣款手续费
	 * @param inCustId
	 *            入账客户号
	 * @param divDetails
	 *            分账客户串
	 * @param merPriv
	 * @param dzObject
	 *            企业垫资/代偿对象
	 * @return
	 */
	public LinkedHashMap<String, String> repayment_3(String bidProId, String OrdId, String investProId,
			Date investOrdDate, String outCustId, double principalAmt, double interestAmt, double fee, String inCustId,
			String divDetails, String merPriv, String dzObject) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

		map.put("Version", HfConsts.VERSION3); // 版本号
		map.put("CmdId", HfConsts.CMD_REPAYMENT); // 请求指令
		map.put("MerCustId", HfConsts.MERCUSTID); // 客户商户号
		map.put("ProId", bidProId); // 标的订单号
		map.put("OrdId", OrdId); // 订单号
		map.put("OrdDate", HfUtils.getFormatDate("yyyyMMdd", new Date())); // 订单日期
		map.put("OutCustId", outCustId);
		map.put("SubOrdId", investProId); // 投资订单号
		map.put("SubOrdDate", HfUtils.getFormatDate("yyyyMMdd", investOrdDate)); // 投资订单日期
		map.put("OutCustId", outCustId);
		map.put("OutAcctId", HfConsts.SERVFEEACCTID); // 出账子账号
		map.put("PrincipalAmt", HfUtils.formatAmount(principalAmt)); // 还款本金
		map.put("InterestAmt", HfUtils.formatAmount(interestAmt)); // 还款利息
		map.put("Fee", HfUtils.formatAmount(fee)); // 扣款手续费
		map.put("InCustId", inCustId); // 扣款手续费
		map.put("InAcctId", HfConsts.SERVFEEACCTID); // 入账子账户

		if (fee > 0) {
			map.put("FeeObjFlag", "O"); // 手续费收取对象标志(I/O)
			map.put("DivDetails", divDetails); // 分账客户串
		}

		if (dzObject != null && !"".equals(dzObject)) {
			map.put("DzObject", dzObject); // 企业垫资/代偿对象
			map.put("OutAcctId", HfConsts.TRANSFEROUTACCTID); // 商户出账子账户
		}

		map.put("BgRetUrl", BaseController.getBaseURL() + "payment/chinapnr/repaymentAyns");
		map.put("MerPriv", merPriv); // 订单流水号

		return createSignMap(map);
	}

	/* ----------------------- 托管接口 ----------------------- */

	/**
	 * 
	 * @param paymengAccount
	 * @param busiType
	 * @param mobile
	 * @return
	 */
	public LinkedHashMap<String, String> sendSmsCode(String paymengAccount, BusiType busiType, String cardId,
			String mobile) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("Version", HfConsts.VERSION1);
		map.put("CmdId", HfConsts.CMD_SENDSMSCODE);
		map.put("MerCustId", HfConsts.MERCUSTID);
		if (busiType.code != 0) {
			map.put("UsrCustId", paymengAccount);
		}
		map.put("BusiType", busiType.value);
		if (busiType.code != 1) {
			map.put("OpenAcctId", cardId);
		}
		map.put("UsrMp", mobile);
		if (busiType.code == 2) {
			map.put("SmsTempType", "O");
		} else if (busiType.code == 3) {
			map.put("SmsTempType", "N");
		}
		return createSignMap(map);
	}

	/**
	 * 托管注册
	 * 
	 * @param mobile
	 *            手机号
	 * @param cardId
	 *            银行卡号
	 * @param bankId
	 *            银行代号
	 * @param provId
	 *            省份代号
	 * @param areaId
	 *            城市代号
	 * @param smsCode
	 *            短信验证码
	 * @param smsSeq
	 *            短信序列号
	 * @param merPriv
	 *            私有欲
	 * @return
	 */
	public LinkedHashMap<String, String> userRegisterByBos(String hfName, String realName, String idNumber,
			String mobile, String cardId, String bankId, String provId, String areaId, String smsCode, String smsSeq,
			String merPriv) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

		map.put("Version", HfConsts.VERSION1); // 版本号
		map.put("CmdId", HfConsts.CMD_USERREGISTER); // 消息类型
		map.put("MerCustId", HfConsts.MERCUSTID); // 客户商户号
		map.put("BgRetUrl", BaseController.getBaseURL() + "payment/chinapnr/userRegisterByBosAyns"); // 商户后台应答地址
		map.put("RetUrl", BaseController.getBaseURL() + "payment/chinapnr/userRegisterByBosSyn"); // 页面返回URL
		map.put("UsrId", hfName); // 汇付用户名
		map.put("UsrName", realName); // 真实姓名
		map.put("IdType", "00");
		map.put("IdNo", idNumber);
		map.put("UsrMp", mobile);
		map.put("MerPriv", merPriv);
		map.put("CardId", cardId);
		map.put("BankId", bankId);
		map.put("ProvId", provId);
		map.put("AreaId", areaId);
		map.put("SmsCode", smsCode);
		map.put("SmsSeq", smsSeq);
		return createSignMap(map);
	}

	public Map<String, String> bosAcctActivate(String payment_account, String ordId) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

		map.put("Version", HfConsts.VERSION4); // 版本号
		map.put("CmdId", HfConsts.CMD_BOSACCTACTIVATE); // 消息类型
		map.put("MerCustId", HfConsts.MERCUSTID); // 客户商户号
		map.put("UsrCustId", payment_account); // 客户商户号
		map.put("OrdId", ordId);
		map.put("OrdDate", HfUtils.getFormatDate("yyyyMMdd"));
		map.put("BgRetUrl", BaseController.getBaseURL() + "payment/chinapnr/bosAcctActivateAyns"); // 商户后台应答地址
		map.put("RetUrl", BaseController.getBaseURL() + "payment/chinapnr/bosAcctActivateSyn"); // 页面返回URL
		return createSignMap(map);
	}

	public Map<String, String> quickBinding(String orderNo, String payment_account, String cardId, String bankId,
			String provId, String areaId, String smsCode, String smsSeq, String orgSmsExt, String mobile,
			String requestMark) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("Version", HfConsts.VERSION1); // 版本号
		map.put("CmdId", HfConsts.CMD_QUICKBINDING); // 消息类型
		map.put("MerCustId", HfConsts.MERCUSTID); // 商户商户号
		map.put("UsrCustId", payment_account); // 客户商户号
		map.put("OrdId", orderNo);
		map.put("TradeType", "REBIND");
		map.put("BankId", bankId);
		map.put("OpenAcctId", cardId);
		map.put("UsrMp", mobile);
		map.put("ProvId", provId);
		map.put("AreaId", areaId);
		map.put("SmsCode", smsCode);
		map.put("SmsSeq", smsSeq);
		map.put("OrgSmsExt", orgSmsExt);
		map.put("BgRetUrl", BaseController.getBaseURL() + "payment/chinapnr/quickBindingAyns"); // 商户后台应答地址
		map.put("MerPriv", requestMark);
		return createSignMap(map);
	}

	public Map<String, String> directRecharge(String payment_account, String orderNo, TradeType tradeType,
			String bankId, double transAmt, String smsSeq, String smsCode) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		// TODO
		map.put("Version", HfConsts.VERSION1); // 版本号
		map.put("CmdId", HfConsts.CMD_DIRECTRECHARGE); // 消息类型
		map.put("MerCustId", HfConsts.MERCUSTID); // 客户商户号
		map.put("UsrCustId", payment_account); // 客户商户号
		map.put("OrdId", orderNo);
		map.put("OrdDate", HfUtils.getFormatDate("yyyyMMdd"));
		map.put("GateBusiId", tradeType.value);
		if (tradeType.code == TradeType.B2C.code || tradeType.code == TradeType.B2B.code) {
			map.put("BankId", bankId);
		}
		map.put("TransAmt", HfUtils.formatAmount(transAmt));
		if (tradeType.code == TradeType.QP.code) {
			// 如果是快捷支付，无需填入卡号只需要短信
			map.put("SmsSeq", smsSeq);
			map.put("SmsCode", smsCode);
		}
		map.put("RetUrl", BaseController.getBaseURL() + "payment/chinapnr/directRechargeSyn");
		map.put("BgRetUrl", BaseController.getBaseURL() + "payment/chinapnr/directRechargeAyns"); // 商户后台应答地址
		return createSignMap(map);
	}

	public Map<String, String> queryPersionInformation(String idNumber) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("Version", HfConsts.VERSION1); // 版本号
		map.put("CmdId", HfConsts.CMD_QUERYUSRINFO); // 消息类型
		map.put("MerCustId", HfConsts.MERCUSTID); // 客户商户号
		map.put("CertId", idNumber); // 客户商户号
		return createSignMap(map);
	}

	public Map<String, String> queryTransStat(String serviceOrderNo, String queryTransType) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("Version", HfConsts.VERSION1); // 版本号
		map.put("CmdId", HfConsts.CMD_QUERYTRANSSTAT); // 消息类型
		map.put("MerCustId", HfConsts.MERCUSTID); // 客户商户号
		map.put("OrdId", serviceOrderNo); // 客户商户号
		map.put("OrdDate", HfUtils.getFormatDate("yyyyMMdd"));
		map.put("QueryTransType", queryTransType);
		return createSignMap(map);
	}

	public Map<String, String> trfReconciliation(Date beginDate, Date endDate, String pageNum, String pageSize) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("Version", HfConsts.VERSION1); // 版本号
		map.put("CmdId", HfConsts.CMD_TRFRECONCILIATION); // 消息类型
		map.put("MerCustId", HfConsts.MERCUSTID); // 客户商户号
		map.put("BeginDate", HfUtils.getFormatDate("yyyyMMdd", beginDate));
		map.put("EndDate", HfUtils.getFormatDate("yyyyMMdd", endDate));
		map.put("PageNum", pageNum);
		map.put("PageSize", pageSize);
		return createSignMap(map);
	}

	public Map<String, String> reconciliation(Date beginDate, Date endDate, String pageNum, String pageSize,
			String queryTransType) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("Version", HfConsts.VERSION1); // 版本号
		map.put("CmdId", HfConsts.CMD_RECONCILIATION); // 消息类型
		map.put("MerCustId", HfConsts.MERCUSTID); // 客户商户号
		map.put("BeginDate", HfUtils.getFormatDate("yyyyMMdd", beginDate));
		map.put("EndDate", HfUtils.getFormatDate("yyyyMMdd", endDate));
		map.put("PageNum", pageNum);
		map.put("PageSize", pageSize);
		map.put("QueryTransType", queryTransType);
		return createSignMap(map);
	}

	public Map<String, String> cashReconciliation(Date beginDate, Date endDate, String pageNum, String pageSize) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("Version", HfConsts.VERSION2); // 版本号
		map.put("CmdId", HfConsts.CMD_CASHRECONCILIATION); // 消息类型
		map.put("MerCustId", HfConsts.MERCUSTID); // 客户商户号
		map.put("BeginDate", HfUtils.getFormatDate("yyyyMMdd", beginDate));
		map.put("EndDate", HfUtils.getFormatDate("yyyyMMdd", endDate));
		map.put("PageNum", pageNum);
		map.put("PageSize", pageSize);
		return createSignMap(map);
	}

	public Map<String, String> saveReconciliation(Date beginDate, Date endDate, String pageNum, String pageSize) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("Version", HfConsts.VERSION1); // 版本号
		map.put("CmdId", HfConsts.CMD_SAVERECONCILIATION); // 消息类型
		map.put("MerCustId", HfConsts.MERCUSTID); // 客户商户号
		map.put("BeginDate", HfUtils.getFormatDate("yyyyMMdd", beginDate));
		map.put("EndDate", HfUtils.getFormatDate("yyyyMMdd", endDate));
		map.put("PageNum", pageNum);
		map.put("PageSize", pageSize);
		return createSignMap(map);
	}

	public Map<String, String> autoTenderCancle(String paymentAccount, String orderNo, double transAmt,
			String requestMark, Map<String, String> unFreezeParam) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("Version", HfConsts.VERSION3); // 版本号
		map.put("CmdId", HfConsts.CMD_AUTOTENDERCANCLE); // 消息类型
		map.put("MerCustId", HfConsts.MERCUSTID); // 客户商户号
		map.put("OrdId", orderNo);
		map.put("OrdDate", HfUtils.getFormatDate("yyyyMMdd"));
		map.put("TransAmt", HfUtils.formatAmount(transAmt));
		map.put("UsrCustId", paymentAccount);
		if (unFreezeParam == null || unFreezeParam.size() == 0) {
			map.put("IsUnFreeze", "N");
		} else {
			map.put("IsUnFreeze", "Y");
			map.putAll(unFreezeParam);
		}
		map.put("BgRetUrl", BaseController.getBaseURL() + "payment/chinapnr/autoTenderCancleAyns");
		map.put("MerPriv", requestMark);
		return createSignMap(map);
	}

	public LinkedHashMap<String, String> transfer(String ordId, double transAmt, String inCustId, String merPriv) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("Version", HfConsts.VERSION1); // 版本号
		map.put("CmdId", HfConsts.CMD_TRANSFER); // 消息类型
		map.put("OrdId", ordId);
		map.put("OutCustId", HfConsts.MERCUSTID);
		map.put("OutAcctId", HfConsts.SERVFEEACCTID);
		map.put("TransAmt", HfUtils.formatAmount(transAmt));
		map.put("InCustId", inCustId);
		map.put("BgRetUrl", BaseController.getBaseURL() + "payment/chinapnr/transferAyns");
		map.put("MerPriv", merPriv);

		return createSignMap(map);
	}

	public LinkedHashMap<String, String> doCorpRegister(String usrId, String usrName, String instuCode, String busiCode,
			String taxCode, String merPriv, String guarType, Double guarCorpEarnestAmt) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("Version", HfConsts.VERSION1); // 版本号
		map.put("CmdId", HfConsts.CMD_CORPREGISTER); // 消息类型
		map.put("MerCustId", HfConsts.MERCUSTID);
		map.put("UsrId", usrId);
		map.put("UsrName", usrName);
		if (StringUtils.isNotBlank(instuCode)) {
			map.put("InstuCode", instuCode);
		}
		map.put("BusiCode", busiCode);
		if (StringUtils.isNotBlank(taxCode)) {
			map.put("TaxCode", taxCode);
		}
		map.put("MerPriv", merPriv);
		map.put("GuarType", guarType);
		map.put("BgRetUrl", BaseController.getBaseURL() + "payment/chinapnr/corpRegisterAyns");
		if (guarCorpEarnestAmt != null && guarCorpEarnestAmt > 0) {
			map.put("ReqExt", "{\"GuarCorpEarnestAmt\":\"" + HfUtils.formatAmount(guarCorpEarnestAmt) + "\"}");
		}
		return createSignMap(map);
	}

	public Map<String, String> doCorpRegisterQuery(String busiCode) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("Version", HfConsts.VERSION1); // 版本号
		map.put("CmdId", HfConsts.CMD_CORPREGISTERQUERY); // 消息类型
		map.put("MerCustId", HfConsts.MERCUSTID);
		map.put("BusiCode", busiCode);
		return createSignMap(map);
	}

}
