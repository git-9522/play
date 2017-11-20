package payment;

import java.util.Date;
import java.util.Map;

import common.enums.BusiType;
import common.enums.TradeType;
import common.utils.ResultInfo;

public interface ICustodian {

	/**
	 * 发送短信验证码
	 * 
	 * @param client
	 *            客户端
	 * @param serviceOrderNo
	 *            订单号
	 * @param userId
	 *            用户id
	 * @param bankId
	 *            银行卡号
	 * @param busiType
	 *            业务类型
	 * @param mobile
	 *            电话号码
	 * @param obj
	 * @return
	 */
	public ResultInfo sendSmsCode(int client, String serviceOrderNo, long userId, String cardId, BusiType busiType,
			String mobile, Object... obj);

	/**
	 * 用户开户
	 * 
	 * @param client
	 *            客户端
	 * @param serviceOrderNo
	 *            订单号
	 * @param userId
	 *            用户id
	 * @param mobile
	 *            电话
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
	 * @param obj
	 * @return
	 */
	public ResultInfo userRegist(int client, String serviceOrderNo, long userId, String hfName, String realName,
			String idNumber, String mobile, String cardId, String bankId, String provId, String areaId, String smsCode,
			String smsSeq, Object... obj);

	/**
	 * 托管账户激活
	 * 
	 * @param client
	 *            客户端
	 * @param serviceOrderNo
	 *            订单号
	 * @param userId
	 *            用户id
	 * @param mobile
	 *            电话
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
	 * @param obj
	 * @return
	 */
	public ResultInfo bosAcctActivate(int client, String serviceOrderNo, long userId, Object... obj);

	/**
	 * 托管换绑卡
	 * 
	 * @param client
	 *            客户端
	 * @param serviceOrderNo
	 *            订单号
	 * @param userId
	 *            用户id
	 * @param cardId
	 *            银行卡号
	 * @param bankId
	 *            银行代号
	 * @param provId
	 *            省份代号
	 * @param areaId
	 *            城市代号
	 * @param mobile
	 *            手机号
	 * @param smsCode
	 *            短信验证码
	 * @param smsSeq
	 *            短信序列号
	 * @param orgSmsExt
	 * 			     原绑定卡验证码 + 序列号
	 * @param obj
	 * @return
	 */
	public ResultInfo quickBinding(int client, String serviceOrderNo, long userId, String cardId,
			String bankId, String provId, String areaId, String mobile, String smsCode, String smsSeq,
			String orgSmsExt, Object... obj);

	/**
	 * 托管充值
	 * 
	 * @param client
	 *            客户端
	 * @param serviceOrderNo
	 *            订单号
	 * @param userId
	 *            用户id
	 * @param gateBusiId
	 *            B2C--个人网银，B2B--企业网银，QP--快捷支付，AWS--代扣签约充值
	 * @param bankId
	 *            银行代号
	 * @param transAmt
	 * @param smsCode
	 *            当gateBusiId为QP时，必输
	 * @param smsSeq
	 *            当gateBusiId为QP时，必输
	 * @param singId
	 *            当gateBusiId为AWS时，必输
	 * @return
	 */
	public ResultInfo directRecharge(int client, String serviceOrderNo, long userId, TradeType tradeType, String bankId,
			double transAmt, String smsCode, String smsSeq, String singId);

	
	/**
	 * 交易状态查询接口
	 */
	public ResultInfo queryTransStat(int client, String serviceOrderNo, String queryTransType);
	
	/**
	 * 商户扣款对账
	 */
	public ResultInfo trfReconciliation(int client, Date beginDate, Date endDate, String pageNum, String pageSize);
	
	/**
	 * 放还款对账接口
	 */
	public ResultInfo reconciliation(int client, Date beginDate, Date endDate, String pageNum, String pageSize, String queryTransType);
	
	/**
	 * 取现对账接口
	 */
	public ResultInfo cashReconciliation(int client, Date beginDate, Date endDate, String pageNum, String pageSize);
	
	/**
	 * 充值对账接口
	 */
	public ResultInfo saveReconciliation(int client, Date beginDate, Date endDate, String pageNum, String pageSize);

	/**
	 * 后台解冻接口
	 */
	public ResultInfo autoTenderCancle(int client, String serviceOrderNo, long userId, double transAmt, Map<String, String> unFreezeParam, long investId);
	
	/**
	 * 企业开户
	 */
	public ResultInfo corpRegister(int client, String serviceOrderNo, long userId, String usrId, String usrName, String instuCode, 
			String busiCode, String taxCode, String guarType, Double guarCorpEarnestAmt);
	
	/**
	 * 企业开户状态查询
	 */
	public ResultInfo corpRegisterQuery(int client, long userId, String busiCode);
	
	public ResultInfo usrUnFreeze(int client, long userId, String ordNo, String freezeTrxId);
	
}