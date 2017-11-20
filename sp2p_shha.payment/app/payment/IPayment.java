package payment;

import java.util.List;
import java.util.Map;

import common.enums.ServiceType;
import common.utils.ResultInfo;
import models.common.entity.t_conversion_user;
import models.core.entity.t_bid;
import models.core.entity.t_bill;
import models.core.entity.t_bill_invest;
import models.core.entity.t_invest;
import models.entity.t_payment_request;

/**
 * Sp2p需要对接第三方的业务接口
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2016年1月5日
 */
public interface IPayment extends ICustodian {
	
	/**
	 * 第三方开户
	 *
	 * @param client 客户端
	 * @param serviceOrderNo 业务订单号
	 * @param userId 关联用户ID 
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月5日
	 */
	public ResultInfo regist(int client, String serviceOrderNo, long userId, Object... obj);
	
	/**
	 * 用户充值
	 *
	 * @param client 客户端
	 * @param serviceOrderNo 业务订单号
	 * @param userId 关联用户ID 
	 * @param rechargeAmt 充值金额
	 * @param obj
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月8日
	 */
	public ResultInfo recharge(int client, String serviceOrderNo, long userId, double rechargeAmt, Object... obj);
	
	/**
	 * 用户绑卡
	 *
	 * @param client 客户端
	 * @param serviceOrderNo 业务订单号
	 * @param userId 关联用户ID
	 * @param obj
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月9日
	 */
	public ResultInfo userBindCard(int client, String serviceOrderNo, long userId, Object... obj);
	
	/**
	 * 用户充值
	 *
	 * @param client 客户端
	 * @param serviceOrderNo 业务订单号
	 * @param userId 关联用户ID
	 * @param withdrawalAmt 提现金额
	 * @param bankAccount 银行卡号
	 * @param obj
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月11日
	 */
	public ResultInfo withdrawal(int client, String serviceOrderNo, long userId, double withdrawalAmt, String bankAccount, String cashType, Object... obj) ;
	
	/**
	 * 标的登记
	 *
	 * @param client 客户端
	 * @param serviceOrderNo 业务订单号
	 * @param bid 标的信息
	 * @param bidCreateFrom 标的发布的位置
	 * 						common.constants.Constants.BID_CREATE_FROM_FRONT
	 * 						common.constants.Constants.BID_CREATE_FROM_BACK
	 * 						common.constants.Constants.BID_CREATE_FROM_OTHER
	 * @param obj
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月11日
	 */
	public ResultInfo bidCreate(int client, String serviceOrderNo, t_bid bid, int bidCreateFrom, Object... obj) ;
	
	/**
	 * 标的失败（审核不通过，系统流标）
	 *
	 * @param client
	 * @param serviceOrderNo
	 * @param bid
	 * @param serviceType
	 * @param obj
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月13日
	 */
	public ResultInfo bidFailed(int client, String serviceOrderNo, t_bid bid, ServiceType serviceType, Object... obj);
	
	/**
	 * 投标
	 *
	 * @param client 客户端
	 * @param serviceOrderNo 业务订单号
	 * @param userId 投资人用户ID
	 * @param bid 借款标信息
	 * @param investAmt 投标金额
	 * @param obj
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月12日
	 */
	public ResultInfo invest(int client, int investType, String serviceOrderNo, long userId, t_bid bid, double investAmt, Object... obj);
	
	/**
	 * 放款
	 *
	 * @param client 客户端
	 * @param serviceOrderNo 业务订单号
	 * @param releaseSupervisorId 管理员ID
	 * @param bid 标的信息
	 * @param obj
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月12日
	 */
	public ResultInfo bidSuditSucc(int client, String serviceOrderNo, long releaseSupervisorId, t_bid bid, Object... obj );
	
	/**
	 * 正常还款
	 *
	 * @param client 客户端
	 * @param serviceOrderNo 业务订单号
	 * @param bill 借款账单
	 * @param obj
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月12日
	 */
	public ResultInfo repayment(int client, String serviceOrderNo, t_bill bill,  List<Map<String, Double>> billInvestFeeList, Object... obj) ;
	
	/**
	 * 垫付后还款
	 *
	 * @param client 客户端
	 * @param serviceOrderNo 业务订单号
	 * @param bill 借款账单信息
	 * @param obj
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月13日
	 */
	public ResultInfo advanceRepayment(int client, String serviceOrderNo, t_bill bill, Object... obj) ;
	
	/** 
	 * 垫付
	 *
	 * @param client
	 * @param serviceOrderNo
	 * @param bill
	 * @param billInvestFeeList 
	 * @param obj
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月13日
	 */
	public ResultInfo advance(int client, String serviceOrderNo, t_bill bill, List<Map<String, Double>> billInvestFeeList, Object... obj);
	
	/**
	 * 线下收款
	 *
	 * @param client
	 * @param serviceOrderNo
	 * @param bill
	 * @param billInvestFeeList
	 * @param obj
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月13日
	 */
	public ResultInfo offlineReceive(int client, String serviceOrderNo, t_bill bill, List<Map<String, Double>> billInvestFeeList, Object... obj);
	
	/**
	 * 奖励兑换
	 *
	 * @param client
	 * @param serviceOrderNo
	 * @param conversion
	 * @param obj
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年2月22日
	 */
	public ResultInfo conversion(int client, String serviceOrderNo, t_conversion_user conversion, Object... obj);
	
	/**
	 * 实时查询用户绑定的银行卡
	 *
	 * @param client
	 * @param userId
	 * @param obj
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月19日
	 */
	public ResultInfo queryBindedBankCard (int client, long userId, Object... obj);
	
	/**
	 * 实时查询商户账户可用余额
	 *
	 * @param client
	 * @param obj
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月26日
	 */
	public ResultInfo queryMerchantBalance (int client, Object... obj);
	
	/**
	 * 商户充值
	 *
	 * @param client
	 * @param obj
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月26日
	 */
	public ResultInfo merchantRecharge (int client, String serviceOrderNo, double rechargeAmt, String type, String bankId, Object... obj);
	
	/**
	 * 商户提现
	 *
	 * @param client 
	 * @param obj
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月26日
	 */
	public ResultInfo merchantWithdrawal (int client, String serviceOrderNo, double withdrawalAmt, String type, Object... obj);
	
	/**
	 * 托管账户资金信息查询
	 *
	 * @param client 客户端
	 * @param account 
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年2月15日
	 */
	public ResultInfo queryFundInfo(int client, String account);
	
	/**
	 * 根据接口类型代号获取对应的接口描述
	 *
	 * @param interfaceType
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月6日
	 */
	public String getInterfaceDes(int code) ;
	
	/**
	 * 获取接口类型的描述
	 *
	 * @param interfaceType
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月6日
	 */
	public int getInterfaceType(Enum interfaceType) ;

	/**
	 * 债权转让
	 *
	 * @param clint 客户端
	 * @param serviceOrderNo 业务订单号
	 * @param debtId 债权id
	 * @param userId 用户id(最高竞价者)
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月29日
	 */
	public ResultInfo debtTransfer(int clint,String serviceOrderNo,Long debtId,Long userId);
	
	/**
	 * 自动投标签约
	 *
	 * @param client 客户端
	 * @param serviceOrderNo 业务订单号
	 * @param userId 投资人用户ID
	 * @param obj
	 * @return
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年3月24日
	 */
	public ResultInfo autoInvestSignature(int client, String serviceOrderNo, long userId,  Object... obj);
	
	/**
	 * 自动投标
	 *
	 * @param client 客户端
	 * @param serviceOrderNo 业务订单号
	 * @param userId 投资人用户ID
	 * @param bid 借款标信息
	 * @param investAmt 投标金额
	 * @param obj
	 * @return
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年4月25日
	 */
	public ResultInfo autoInvest(int client, int investType, String serviceOrderNo, long userId, t_bid bid, double investAmt, Object... obj);
	
	/**
	 * 用户信息查询
	 * 
	 * @param client 请求发起的客户端类型
	 * @param serviceOrderNo 业务订单号(用于平台内部)
	 * @param userId 用户ID
	 * @param mobile 手机号码
	 * @param idNumber 身份证号码
	 * @param bankAcct 银行卡号码
	 * @param obj
	 * @return
	 */
	public ResultInfo queryPersionInformation(int client, String serviceOrderNo, long userId, String mobile, String idNumber, String bankAcct, 
			Object... obj);
	
	/**
	 * 快速充值
	 *
	 * @param client 发起请求的客户端
	 * @param serviceOrderNo 平台业务订单号
	 * @param userId 关联用户的ID 
	 * @param rechargeAmt 充值金额
	 * @param obj
	 * @return
	 *
	 * @author YanPengFei
	 * @createDate 2016年08月25日
	 */
	public ResultInfo fastRecharge(int client, String serviceOrderNo, long userId, double rechargeAmt, Object... obj);
	
	/**
	 * 更换用户手机号
	 * @param client 请求发起的客户端类型
	 * @param serviceOrderNo 业务订单号(用于平台内部)
	 * @param userId 用户ID
	 * @param acct 第三方账号
	 * @param obj
	 * @return
	 */
	public ResultInfo changeUserMobile(int client, String serviceOrderNo, long userId, String acct, Object... obj);
	
	/**
	 * 充值提现查询
	 * 
	 * @param client 请求发起的客户端类型
	 * @param serviceOrderNo 业务订单号(用于平台内部)
	 * @param pr 请求记录
	 * @param obj
	 * @return
	 */
	public ResultInfo queryCztx(int client, String serviceOrderNo, t_payment_request pr, Object... obj);
	
	/**
	 * 交易查询
	 * 
	 * @param client 请求发起的客户端类型
	 * @param serviceOrderNo 业务订单号(用于平台内部)
	 * @param pr 请求记录
	 * @param obj
	 * @return
	 */
	public ResultInfo queryTxn(int client, String serviceOrderNo, t_payment_request pr, Object... obj);
	
	/**
	 * 商户转账
	 * 
	 * @param client
	 * @param serviceOrderNo
	 * @param userId
	 * @param obj
	 * 
	 * @return
	 */
	public ResultInfo merchantTransfer(int client, String serviceOrderNo, long userId, Object... obj);
	
	/**
	 * 投标的撤销
	 * 
	 * @param client
	 * @param serviceOrderNo
	 * @param invest
	 * @param obj
	 * @return
	 */
	public ResultInfo tenderCancle(int client, String serviceOrderNo, t_invest invest,  Object... obj);
	
	
	/**
	 * 单笔还款
	 * 
	 * @param client
	 * @param bidId
	 * @param serviceOrderNo
	 * @param obj
	 * @return
	 */
	public ResultInfo singleRepayment(int client, String serviceOrderNo, t_bill bill, List<Map<String, Double>> billInvestFeeList, Object... obj);
	
	/**
	 * 加息券-商户给用户转账
	 * 
	 * @param client
	 * @param serviceOrderNo
	 * @param userId
	 * @param obj
	 * 
	 * @return
	 */
	public ResultInfo sendRate(int client, String serviceOrderNo,t_bill_invest invest, Object... obj);
	
	/**
	 * 转账-商户给用户转账
	 */
	public ResultInfo transfer(int client, String serviceOrderNo, long userId, double transAmt, Object... obj);
	
}