package hf;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.pattern.LogEvent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shove.Convert;

import common.constants.ConfConst;
import common.constants.ModuleConst;
import common.constants.RemarkPramKey;
import common.constants.SettingKey;
import common.enums.Bank;
import common.enums.Client;
import common.enums.ServiceType;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.JPAUtil;
import common.utils.LoggerUtil;
import common.utils.OrderNoUtil;
import common.utils.ResultInfo;
import common.utils.number.Arith;
import models.common.entity.t_corp_info;
import models.common.entity.t_corp_info.Status;
import models.common.entity.t_deal_platform.DealRemark;
import models.common.entity.t_deal_user;
import models.common.entity.t_deal_user.OperationType;
import models.common.entity.t_exp_gold_account_user;
import models.common.entity.t_recharge_user;
import models.common.entity.t_score_user;
import models.common.entity.t_user_fund;
import models.common.entity.t_user_info;
import models.core.entity.t_bid;
import models.core.entity.t_invest;
import models.core.entity.t_invest_log;
import models.core.entity.t_red_packet;
import models.core.entity.t_red_packet_transfer;
import payment.impl.PaymentProxy;
import play.Logger;
import play.db.jpa.JPA;
import play.db.jpa.JPAPlugin;
import services.common.ConversionService;
import services.common.CorpInfoService;
import services.common.DealPlatformService;
import services.common.DealUserService;
import services.common.ExpGoldAccountUserService;
import services.common.ExpGoldShowRecordService;
import services.common.RechargeUserService;
import services.common.ScoreUserService;
import services.common.SettingService;
import services.common.UserFundService;
import services.common.UserInfoService;
import services.common.UserService;
import services.core.BidService;
import services.core.BillInvestService;
import services.core.BillService;
import services.core.DebtService;
import services.core.InvestLogService;
import services.core.InvestService;
import services.core.RedPacketTransferService;
import services.core.RedpacketService;
import services.ext.cps.CpsService;

public class HfPaymentCallBackService extends HfPaymentService {

	protected static UserFundService userFundService = Factory.getService(UserFundService.class);

	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);

	protected static BidService bidService = Factory.getService(BidService.class);

	protected static InvestService investService = Factory.getService(InvestService.class);
	/** 记录投资记录（发放奖励） **/
	protected static InvestLogService investLogService = Factory.getService(InvestLogService.class);

	protected static BillService billService = Factory.getService(BillService.class);

	protected static ConversionService conversionService = Factory.getService(ConversionService.class);

	protected static HfPaymentService hfPaymentService = Factory.getSimpleService(HfPaymentService.class);

	protected static DealPlatformService dealPlatformService = Factory.getService(DealPlatformService.class);

	protected static DebtService debtService = Factory.getService(DebtService.class);

	protected static RedpacketService redpacketService = Factory.getService(RedpacketService.class);

	protected static RedPacketTransferService redPacketTransferService = Factory
			.getService(RedPacketTransferService.class);

	protected static SettingService settingService = Factory.getService(SettingService.class);

	protected static ScoreUserService scoreUserService = Factory.getService(ScoreUserService.class);

	protected static RechargeUserService rechargeUserService = Factory.getService(RechargeUserService.class);

	protected static CpsService cpsService = Factory.getService(CpsService.class);

	protected static BillInvestService billInvestService = Factory.getService(BillInvestService.class);

	protected static UserService userService = Factory.getService(UserService.class);

	protected static DealUserService dealUserService = Factory.getService(DealUserService.class);

	protected static CorpInfoService corpInfoService = Factory.getService(CorpInfoService.class);
protected static ExpGoldAccountUserService expGoldAccountUserService = Factory.getService(ExpGoldAccountUserService.class);
	
	protected static ExpGoldShowRecordService expGoldShowRecordService = Factory.getService(ExpGoldShowRecordService.class);

	/**
	 * 开户回调
	 *
	 * @param cbParams
	 *            回调参数
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月8日
	 */
	public ResultInfo userRegister(Map<String, String> cbParams, Map<String, String> remarkParams) {
		ResultInfo result = new ResultInfo();

		// 签名，状态码，仿重单处理;
		result = this.checkSign(cbParams, HfPayType.USERREGISTER);
		if (result.code < 1) {

			return result;
		}

		long userId = Long.parseLong(remarkParams.get(RemarkPramKey.USER_ID));
		String paymentAccount = cbParams.get("UsrCustId");
		String realName = cbParams.get("UsrName");
		String idNumber = cbParams.get("IdNo");
		String email = cbParams.get("UsrEmail");
		String hfName = cbParams.get("UsrId");

		result = userFundService.doCreateAccount(userId, paymentAccount, realName, idNumber, email, hfName);

		if (result.code < 1) {

			return result;
		}

		JPAPlugin.closeTx(false);
		JPAPlugin.startTx(false);

		/** CPS推广-开户奖励积分 */
		cpsService.grantCpsOpenAccountScore(userId);

		/** 发送红包 */
		redpacketService.sendRegisterRedPacketToUser(userId, t_red_packet.RedpacketType.REGISTER.code, "开户红包");

		result.code = 1;
		result.msg = "开户成功";

		return result;
	}

	/**
	 * 充值回调
	 *
	 * @param cbParams
	 *            回调参数
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月8日
	 */
	public ResultInfo netSave(Map<String, String> cbParams, Map<String, String> remarkParams) {
		ResultInfo result = new ResultInfo();

		// 签名，状态码，仿重单处理;
		result = this.checkSign(cbParams, HfPayType.NETSAVE);
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

		// 积分商城
		if (ModuleConst.EXT_MALL) {

			double score = Convert.strToDouble(settingService.findSettingValueByKey(SettingKey.FIRST_RECHARGE), 0);

			if (score > 0) {

				// 首次充值
				int row = rechargeUserService.countDealOfUser(userId, t_recharge_user.Status.SUCCESS);
				if (row == 1) {

					/** 添加用户积分记录 */
					Map<String, String> summaryPrams = new HashMap<String, String>();
					summaryPrams.put("score", (int) score + "");
					result = scoreUserService.addScoreRecord(1, userId, (int) score,
							t_score_user.OperationType.FIRST_RECHARGE, summaryPrams);

					if (result.code < 1) {

						JPA.setRollbackOnly();
						result.code = -1;
						result.msg = "添加积分记录失败";

						return result;
					}
				}
			}
		}

		result.code = 1;
		result.msg = "充值成功";

		return result;
	}

	/**
	 * 商户充值回调
	 *
	 * @param cbParams
	 *            回调参数
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月8日
	 */
	public ResultInfo merNetSave(Map<String, String> cbParams) {
		ResultInfo result;

		// 签名，状态码，仿重单处理;
		result = this.checkSign(cbParams, HfPayType.DIRECTRECHARGE);
		if (result.code < 1) {

			return result;
		}

		result.code = 1;
		result.msg = "充值成功";

		return result;
	}

	/**
	 * 商户提现回调
	 *
	 * @param cbParams
	 *            回调参数
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月8日
	 */
	public ResultInfo merCash(Map<String, String> cbParams) {
		ResultInfo result;

		// 提现对账处理
		if (cbParams.containsKey("RespType") && "Cash".equals(cbParams.get("RespType"))) {
			cbParams.put("CmdId", "Cash");
		}

		// 签名，状态码，仿重单处理;
		result = this.checkSign(cbParams, HfPayType.CASH);
		if (result.code < 1) {

			return result;
		}

		Map<String, String> remarkParams = this.queryRequestParams(cbParams.get("MerPriv"));
		if (remarkParams == null) {
			result.code = -1;
			result.msg = "查询托管请求备注参数失败";

			return result;
		}

		// donothing

		result.code = 1;
		result.msg = "提现成功";

		return result;
	}

	/**
	 * 用户绑卡回调
	 *
	 * @param cbParams
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月9日
	 */
	public ResultInfo userBindCard(Map<String, String> cbParams) {
		ResultInfo result;

		// 签名，状态码，仿重单处理;
		result = this.checkSign(cbParams, HfPayType.USERBINDCARD);
		if (result.code < 1) {

			return result;
		}

		Map<String, String> remarkParams = this.queryRequestParams(cbParams.get("MerPriv"));
		if (remarkParams == null) {
			result.code = -1;
			result.msg = "查询托管请求备注参数失败";

			return result;
		}

		long userId = Long.parseLong(remarkParams.get(RemarkPramKey.USER_ID));

		String bankCode = cbParams.get("OpenBankId");
		String account = cbParams.get("OpenAcctId");
		String bankName = HfUtils.getBankName(bankCode);

		result = userFundService.doBindCard(userId, bankName, bankCode, account);
		if (result.code < 1) {

			return result;
		}

		// 积分商城
		if (ModuleConst.EXT_MALL) {

			double score = Convert.strToDouble(settingService.findSettingValueByKey(SettingKey.BIND_CARD_SCORE), 0);

			if (score > 0) {
				// 增加积分
				int rows = userFundService.updateUserScoreBalanceAdd(userId, score);
				if (rows <= 0) {
					JPA.setRollbackOnly();
					result.code = -1;
					result.msg = "绑卡成功获得积分失败";

					return result;
				}

				// 及时查询用户的可用积分
				double scoreBalance = userFundService.findUserScoreBalance(userId);

				/** 添加用户积分记录 */
				Map<String, String> summaryPrams = new HashMap<String, String>();
				summaryPrams.put("score", (int) score + "");
				boolean addDeal = scoreUserService.addScoreUserInfo(userId, score, scoreBalance,
						t_score_user.OperationType.BIND_CARD_SCORE, summaryPrams);

				if (!addDeal) {
					JPA.setRollbackOnly();
					result.code = -1;
					result.msg = "绑卡成功获得积分失败";

					return result;
				}

				userService.flushUserCache(userId);
			}

		}

		result.code = 1;
		result.msg = "用户绑卡成功";

		return result;
	}

	/**
	 * 提现回调业务处理
	 *
	 * @param cbParams
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月11日
	 */
	public ResultInfo withdrawal(Map<String, String> cbParams, Map<String, String> remarkParams) {
		ResultInfo result;

		// 提现对账处理
		if (cbParams.containsKey("RespType") && "Cash".equals(cbParams.get("RespType"))) {
			cbParams.put("CmdId", "Cash");
		}

		// 签名，状态码，仿重单处理;
		result = this.checkSign(cbParams, HfPayType.CASH);
		if (result.code < 1) {

			return result;
		}

		long userId = Long.parseLong(remarkParams.get(RemarkPramKey.USER_ID));
		double withdrawalAmt = Double.parseDouble(remarkParams.get(RemarkPramKey.WITHDRAWAL_AMT));
		double withdrawalFee = Double.parseDouble(remarkParams.get(RemarkPramKey.WITHDRAWAL_FEE)); // 平台收取用户的提现手续费
		String serviceOrderNo = remarkParams.get(RemarkPramKey.SERVICE_ORDER_NO);

		// 第一条回调日志
		Map<String, String> returnDate = this.queryFirstCBParamsByMark(cbParams.get("MerPriv"));

		String realTransAmt = returnDate.get("RealTransAmt"); // 实际到账金额为可选参数，第一次回调有该参数，后面几次可能没有，所以从数据库中查询
		String rtransAmt = returnDate.get("TransAmt"); // 取现金额
		String feeAmt = returnDate.get("FeeAmt"); // 汇付收取手续费金额，异步回调该参数可能没有，所以从数据库中查询

		if (StringUtils.isBlank(realTransAmt) || StringUtils.isBlank(rtransAmt) || StringUtils.isBlank(feeAmt)) {
			result.code = -1;
			result.msg = "回调参数有误，无法处理提现业务！";

			return result;
		}

		/*
		 * 手续费扣除模式：false-内扣；true-外扣
		 */
		boolean chargeMode = false; // 默认内扣
		if (realTransAmt.equals(rtransAmt)) { // 实际到账=取现金额，则为外扣模式
			chargeMode = true;
		}

		/*
		 * 用户支付提现手续费
		 */
		double userPayWithdrawalFee = 0;

		if (HfConsts.USER_PAY_WITHDRAW_FEE) { // 汇付向用户收取手续费
			double hfWithdrawalFee = Double.parseDouble(feeAmt); // 汇付收取手续费

			userPayWithdrawalFee = withdrawalFee + hfWithdrawalFee;
		} else {// 平台垫付取现手续费
			userPayWithdrawalFee = withdrawalFee;
		}

		result = userFundService.doWithdrawal(userId, withdrawalAmt, userPayWithdrawalFee, withdrawalFee,
				serviceOrderNo, chargeMode);
		if (result.code < 1) {

			return result;
		}

		result.code = 1;
		result.msg = "提现成功";

		return result;
	}

	/**
	 * 标的录入业务处理
	 *
	 * @param cbParams
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月11日
	 */
	public ResultInfo addBidInfo(Map<String, String> cbParams) {
		ResultInfo result;

		// 签名，状态码，仿重单处理;
		result = this.checkSign(cbParams, HfPayType.ADDBIDINFO);
		if (result.code < 1) {

			return result;
		}

		Map<String, String> addBidInfoReqParams = this.queryRequestParams(cbParams.get("MerPriv"));
		if (addBidInfoReqParams == null) {
			result.code = -1;
			result.msg = "查询托管请求备注参数失败";

			return result;
		}

		t_bid bid = new Gson().fromJson(addBidInfoReqParams.get(RemarkPramKey.BID), t_bid.class);
		String serviceOrderNo = addBidInfoReqParams.get(RemarkPramKey.SERVICE_ORDER_NO);

		// 无需保证金
		if (bid.bail <= 0) {
			bid.service_order_no = serviceOrderNo;
			bid.trust_order_no = ""; // 未冻结保证金，不保存托管交易订单号
			return bidService.doCreateBid(bid, serviceOrderNo);
		}

		/** 冻结保证金开始 */
		String paymentAccount = addBidInfoReqParams.get(RemarkPramKey.PAYMENT_ACCOUNT);

		// 交易订单号
		String orderNo = OrderNoUtil.getNormalOrderNo(ServiceType.BID_CREATE); // 保证金冻结交易订单号

		// 请求唯一标识
		String freezeRequestMark = UUID.randomUUID().toString();

		// 冻结保证金参数组装
		LinkedHashMap<String, String> reqParams = hfPaymentService.usrFreezeBg(orderNo, paymentAccount, bid.bail,
				freezeRequestMark);

		// 备注参数（全部以“r_”开头）
		Map<String, String> remarkParams = new LinkedHashMap<String, String>();
		remarkParams.putAll(reqParams);
		remarkParams.put(RemarkPramKey.BID, new Gson().toJson(bid));

		hfPaymentService.printRequestData(freezeRequestMark, bid.user_id, serviceOrderNo, orderNo,
				ServiceType.BID_CREATE, HfPayType.USRFREEZEBG, remarkParams);

		Map<String, String> respParams = HfUtils.httpPost(reqParams);

		hfPaymentService.printCallBackData("冻结保证金响应", respParams, ServiceType.BID_CREATE, HfPayType.USRFREEZEBG);

		result = this.freezeBail(respParams);

		return result;
	}

	/**
	 * 冻结保证金回调业务
	 *
	 * @param respParams
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月11日
	 */
	public ResultInfo freezeBail(Map<String, String> cbParams) {
		ResultInfo result;

		// 签名，状态码，仿重单处理;
		result = this.checkSign(cbParams, HfPayType.USRFREEZEBG);
		if (result.code < 1) {

			return result;
		}

		Map<String, String> remarkParams = this.queryRequestParams(cbParams.get("MerPriv"));
		if (remarkParams == null) {
			result.code = -1;
			result.msg = "查询托管请求备注参数失败";

			return result;
		}

		t_bid bid = new Gson().fromJson(remarkParams.get(RemarkPramKey.BID), t_bid.class);
		String serviceOrderNo = remarkParams.get(RemarkPramKey.SERVICE_ORDER_NO);

		// 保存业务订单号和汇付返回的冻结订单号
		bid.service_order_no = serviceOrderNo;
		bid.trust_order_no = cbParams.get("TrxId");

		return bidService.doCreateBid(bid, serviceOrderNo);

	}

	/**
	 * 主动投标回调业务
	 *
	 * @param cbParams
	 * @param remarkParams
	 *            备注参数
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月12日
	 */
	public ResultInfo invest(Map<String, String> cbParams, Map<String, String> remarkParams) {
		ResultInfo result;

		// 签名，状态码，仿重单处理;
		result = this.checkSign(cbParams, HfPayType.INITIATIVETENDER);
		if (result.code < 1) {
			// 超额投标判断
			if (hfPaymentService.isOverBidAmount(remarkParams.get(RemarkPramKey.SERVICE_ORDER_NO))) {
				result.code = ResultInfo.OVER_BID_AMOUNT;
				result.msg = "投标失败,本次投资已超额";
			}

			return result;
		}

		String freezeTrxId = cbParams.get("FreezeTrxId");

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
		long rateId = Convert.strToLong(remarkParams.get(RemarkPramKey.RATE_ID), 0L);
		double addRate = Convert.strToDouble(remarkParams.get(RemarkPramKey.ADD_RATE), 0.0);

		result = investService.doInvest(userId, bidId, investAmt, serviceOrderNo, freezeTrxId, client, investType,
				redPacketId, redPacketAmt, cashId, cashAmt, rateId, addRate);

		if (result.code < 1) {
			if (result.code == ResultInfo.OVER_BID_AMOUNT) { // 超标处理
				result = doUserUnFreeze(userId, ServiceType.INVEST, HfPayType.USRUNFREEZE, serviceOrderNo, freezeTrxId);

				if (result.code > 0 || result.code != ResultInfo.ALREADY_RUN) { // 解冻成功
					result.code = ResultInfo.OVER_BID_AMOUNT;
					result.msg = "投标失败,本次投资已超额";

					JPAUtil.transactionCommit(); // 资金已经解冻，事务提交
				}

				return result;
			}

			return result;
		}

		// 获取当前投资成功的记录
		t_invest thisinvest = null;
		if (result.obj != null) {
			try {
				thisinvest = (t_invest) result.obj;
			} catch (Exception e) {
				Logger.error(e.getMessage());
			}
		}
		// 首次的投资
		int firstInvestRow = investService.countInvestOfUser(userId);
		
			
		int InvestCount = investService.countInvestOfUser(userId,true);  //过滤掉债权转让
		//记录到投资记录表（发放奖励）
		if(thisinvest!=null){
			t_invest_log invest_log=new t_invest_log(thisinvest, InvestCount==1);
			investLogService.saveLog(invest_log);
		    if(InvestCount ==2 ){
			//查询用户体验金账户信息（条件为可用）
		    	ExpGoldSend(userId, thisinvest.amount,client);
		    }
		}
		// 积分商城
		if (ModuleConst.EXT_MALL) {

			double score = Convert.strToDouble(settingService.findSettingValueByKey(SettingKey.FIRST_INVEST), 0);

			if (score > 0) {

				if (firstInvestRow == 1) {

					/** 添加用户积分记录 */
					Map<String, String> summaryPrams = new HashMap<String, String>();
					summaryPrams.put("score", (int) score + "");
					result = scoreUserService.addScoreRecord(1, userId, (int) score,
							t_score_user.OperationType.FIRST_INVEST, summaryPrams);

					if (result.code < 1) {

						JPA.setRollbackOnly();
						result.code = -1;
						result.msg = "添加积分记录失败";

						return result;
					}
				}
			}

			// 投资赚积分
			double invest_score = Convert.strToDouble(settingService.findSettingValueByKey(SettingKey.INVEST_SCORE), 0);

			if (invest_score > 0) {

				t_bid bid = bidService.findByID(bidId);
				if (bid == null) {
					result.code = -1;
					result.msg = "该借款标不存在,[bidId:" + bidId + "]";

					return result;
				}

				/* 计算公式：投资积分= （投资金额 /设置参数）* 标的月数 */
				double investScore = Arith.round(Arith.mul(Arith.div(investAmt, invest_score, 24), bid.period), 0);

				/** 添加用户积分记录 */
				Map<String, String> summaryPrams = new HashMap<String, String>();
				summaryPrams.put("score", (int) investScore + "");
				result = scoreUserService.addScoreRecord(1, userId, (int) investScore,
						t_score_user.OperationType.DO_INVEST, summaryPrams);

				if (result.code < 1) {

					JPA.setRollbackOnly();
					result.code = -1;
					result.msg = "添加积分记录失败";

					return result;
				}
			}
		}

		if (firstInvestRow == 1) {

			/** 发放被推广用户-首次投资奖励积分 **/
			cpsService.grantCpsFirstInvestScore(userId);
		}

		// 将投标业务先提交，避免主业务执行失败
		JPAPlugin.closeTx(false);
		JPAPlugin.startTx(false);

		if (ModuleConst.EXT_REDPACKET && redPacketId > 0L && redPacketAmt > 0.0) {
			result = redPacketTransferService.addRedPacketTransferRecord(userId, redPacketId, redPacketAmt);
			if (result.code < 1) {

				return result;
			}

			// 先将添加转账记录的事务提交，避免记录丢失
			JPAPlugin.closeTx(false);
			JPAPlugin.startTx(false);

			t_red_packet_transfer transfer = (t_red_packet_transfer) result.obj;
			result = PaymentProxy.getInstance().merchantTransfer(Client.PC.code, serviceOrderNo, userId, transfer);
			if (result.code < 1) {

				return result;
			}
		}
		result.code = 1;
		result.msg = "投标成功";

		return result;
	}

	/**
	 * 批量还款，回调业务
	 * 
	 * @param respParams
	 * @param desc
	 * @param error
	 */
	public ResultInfo batchRepayment(Map<String, String> cbParams) {
		ResultInfo result;

		// 签名，状态码，仿重单处理;
		result = this.checkSign(cbParams, HfPayType.BATCHREPAYMENT);
		if (result.code < 1) {

			return result;
		}

		String requestMark = cbParams.get("MerPriv");

		Map<String, String> remarkParams = this.queryRequestParams(requestMark);
		if (remarkParams == null) {
			result.code = -1;
			result.msg = "查询托管请求备注参数失败";

			return result;
		}

		long billId = Long.parseLong(remarkParams.get(RemarkPramKey.BILL_ID));
		List<Map<String, Double>> billInvestFeeList = new Gson().fromJson(
				remarkParams.get(RemarkPramKey.BILL_INVEST_FEE_LIST), new TypeToken<List<Map<String, Double>>>() {
				}.getType());
		String serviceOrderNo = remarkParams.get(RemarkPramKey.SERVICE_ORDER_NO);

		return billService.doRepayment(billId, billInvestFeeList, serviceOrderNo);
	}

	/**
	 * 垫付后还款回调业务
	 * 
	 * @param cbParams
	 * @param respParams
	 * @param desc
	 * @param error
	 */
	public ResultInfo advanceRepayment(Map<String, String> cbParams) {
		ResultInfo result;

		// 签名，状态码，仿重单处理;
		result = this.checkSign(cbParams, HfPayType.BATCHREPAYMENT);
		if (result.code < 1) {

			return result;
		}

		Map<String, String> remarkParams = this.queryRequestParams(cbParams.get("MerPriv"));
		if (remarkParams == null) {
			result.code = -1;
			result.msg = "查询托管请求备注参数失败";

			return result;
		}

		long billId = Long.parseLong(remarkParams.get(RemarkPramKey.BILL_ID));
		double loanOverdueFine = Double.parseDouble(remarkParams.get(RemarkPramKey.LOAN_OVERDUE_FINE));
		String serviceOrderNo = remarkParams.get(RemarkPramKey.SERVICE_ORDER_NO);

		return billService.doAdvanceRepayment(serviceOrderNo, billId, loanOverdueFine);
	}

	/**
	 * 本息垫付批量还款，回调业务
	 * 
	 * @param respParams
	 */
	public ResultInfo advance(Map<String, String> cbParams) {
		ResultInfo result;

		// 签名，状态码，仿重单处理;
		result = this.checkSign(cbParams, HfPayType.BATCHREPAYMENT);
		if (result.code < 1) {

			return result;
		}

		String requestMark = cbParams.get("MerPriv");

		Map<String, String> remarkParams = this.queryRequestParams(requestMark);
		if (remarkParams == null) {
			result.code = -1;
			result.msg = "查询托管请求备注参数失败";

			return result;
		}

		long billId = Long.parseLong(remarkParams.get(RemarkPramKey.BILL_ID));
		List<Map<String, Double>> billInvestFeeList = new Gson().fromJson(
				remarkParams.get(RemarkPramKey.BILL_INVEST_FEE_LIST), new TypeToken<List<Map<String, Double>>>() {
				}.getType());
		String serviceOrderNo = remarkParams.get(RemarkPramKey.SERVICE_ORDER_NO);

		return billService.doPrincipalAdvance(billId, serviceOrderNo, billInvestFeeList);
	}

	/**
	 * 线下收款批量还款，回调业务
	 * 
	 * @param respParams
	 */
	public ResultInfo offlineReceive(Map<String, String> cbParams) {
		ResultInfo result;

		// 签名，状态码，仿重单处理;
		result = this.checkSign(cbParams, HfPayType.BATCHREPAYMENT);
		if (result.code < 1) {

			return result;
		}

		String requestMark = cbParams.get("MerPriv");

		Map<String, String> remarkParams = this.queryRequestParams(requestMark);
		if (remarkParams == null) {
			result.code = -1;
			result.msg = "查询托管请求备注参数失败";

			return result;
		}

		long billId = Long.parseLong(remarkParams.get(RemarkPramKey.BILL_ID));
		List<Map<String, Double>> billInvestFeeList = new Gson().fromJson(
				remarkParams.get(RemarkPramKey.BILL_INVEST_FEE_LIST), new TypeToken<List<Map<String, Double>>>() {
				}.getType());
		String serviceOrderNo = remarkParams.get(RemarkPramKey.SERVICE_ORDER_NO);

		return billService.doOfflineReceive(billId, billInvestFeeList, serviceOrderNo);
	}

	/**
	 * 奖励兑换
	 *
	 * @param cbParams
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年2月22日
	 */
	public ResultInfo conversion(Map<String, String> cbParams) {
		ResultInfo result;

		// 签名，状态码，仿重单处理;
		result = this.checkSign(cbParams, HfPayType.TRANSFER);
		if (result.code < 1) {

			return result;
		}

		String requestMark = cbParams.get("MerPriv");

		Map<String, String> remarkParams = this.queryRequestParams(requestMark);
		if (remarkParams == null) {
			result.code = -1;
			result.msg = "查询托管请求备注参数失败";

			return result;
		}

		long conversionId = Long.parseLong(remarkParams.get("r_conversion_id"));
		String serviceOrderNo = remarkParams.get("service_order_no");

		return conversionService.doConversion(serviceOrderNo, conversionId);
	}

	/**
	 * 自动投标计划回调业务
	 *
	 * @param cbParams
	 * @return
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年03月24日
	 */
	public ResultInfo autoInvestSignature(Map<String, String> cbParams) {
		ResultInfo result;

		// 签名，状态码，仿重单处理;
		result = this.checkSign(cbParams, HfPayType.AUTOTENDERPLAN);
		if (result.code < 1) {

			return result;
		}

		Map<String, String> remarkParams = this.queryRequestParams(cbParams.get("MerPriv"));
		if (remarkParams == null) {
			result.code = -1;

			result.msg = "查询托管请求备注参数失败";
			return result;
		}

		long userId = Long.parseLong(remarkParams.get(RemarkPramKey.USER_ID));

		result = investService.updateAutoInvestStatus(userId);
		if (result.code < 1) {

			return result;
		}

		result.code = 1;
		result.msg = "自动投标签约成功";

		return result;
	}

	/**
	 * 自动投标回调业务
	 *
	 * @param cbParams
	 * @param remarkParams
	 *            备注参数
	 * 
	 * @return
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年03月25日
	 */
	public ResultInfo autoInvest(Map<String, String> cbParams, Map<String, String> remarkParams) {
		ResultInfo result;

		// 签名，状态码，仿重单处理;
		result = this.checkSign(cbParams, HfPayType.AUTOTENDER);
		if (result.code < 1) {
			// 超额投标判断
			if (hfPaymentService.isOverBidAmount(remarkParams.get(RemarkPramKey.SERVICE_ORDER_NO))) {
				result.code = ResultInfo.OVER_BID_AMOUNT;
				result.msg = "投标失败,本次投资已超额";
			}

			return result;
		}

		String freezeTrxId = cbParams.get("FreezeTrxId");

		long userId = Long.parseLong(remarkParams.get(RemarkPramKey.USER_ID));
		long bidId = Long.parseLong(remarkParams.get(RemarkPramKey.BID_ID));
		double investAmt = Double.parseDouble(remarkParams.get(RemarkPramKey.INVEST_AMT));
		int client = Integer.parseInt(remarkParams.get(RemarkPramKey.CLIENT));
		int investType = Integer.parseInt(remarkParams.get(RemarkPramKey.INVEST_TYPE));
		String serviceOrderNo = remarkParams.get(RemarkPramKey.SERVICE_ORDER_NO);

		result = investService.doInvest(userId, bidId, investAmt, serviceOrderNo, freezeTrxId, client, investType, 0L,
				0.0, 0L, 0.0, 0L, 0.0);
		if (result.code < 1) {
			if (result.code == ResultInfo.OVER_BID_AMOUNT) { // 超标处理
				result = doUserUnFreeze(userId, ServiceType.AUTO_INVEST, HfPayType.USRUNFREEZE, serviceOrderNo,
						freezeTrxId);

				if (result.code > 0 || result.code != ResultInfo.ALREADY_RUN) { // 解冻成功
					result.code = ResultInfo.OVER_BID_AMOUNT;
					result.msg = "投标失败,本次投资已超额";

					JPAUtil.transactionCommit(); // 资金已经解冻，事务提交
				}

				return result;
			}

			return result;
		}

		result.code = 1;
		result.msg = "投标成功";

		return result;
	}

	/**
	 * 债权转让(资金托管业务回调)
	 *
	 * @param cbParams
	 * @return 失败会将debtId返回
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月29日
	 */
	public ResultInfo debtTransfer(Map<String, String> cbParams, Map<String, String> remarkParams) {

		ResultInfo result = this.checkSign(cbParams, hf.HfPayType.CREDITASSIGN);
		if (result.code < 1) {

			return result;
		}

		long debtId = Long.valueOf(remarkParams.get(RemarkPramKey.DEBT_ID));
		String serviceOrderNo = remarkParams.get(RemarkPramKey.SERVICE_ORDER_NO);
		double debtFee = Double.valueOf(remarkParams.get(RemarkPramKey.DEBT_FEE));
		long toUserId = Long.valueOf(remarkParams.get(RemarkPramKey.TO_USER_ID));

		return debtService.doDebtTransfer(serviceOrderNo, debtId, toUserId, debtFee);
	}

	/**
	 * 商户转账
	 *
	 * @param cbParams
	 * 
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年2月22日
	 */
	public ResultInfo merchantTransfer(Map<String, String> cbParams) {
		ResultInfo result;

		// 签名，状态码，仿重单处理;
		result = this.checkSign(cbParams, HfPayType.TRANSFER);

		if (result.code < 1) {

			return result;
		}

		String requestMark = cbParams.get("MerPriv");

		Map<String, String> remarkParams = this.queryRequestParams(requestMark);

		if (remarkParams == null) {
			result.code = -1;
			result.msg = "查询托管请求备注参数失败";

			return result;
		}

		long transferId = Convert.strToLong(remarkParams.get(RemarkPramKey.RED_PACKET_TRANSFER_ID), 0L);
		String orderNo = remarkParams.get(RemarkPramKey.ORDER_NO);
		int tempState = Convert.strToInt(remarkParams.get("r_tempState"), 0);// 定时任务，临时标记

		return redPacketTransferService.doTransfer(transferId, orderNo, tempState);
	}

	/**
	 * 商户转账(投标奖励、加息卷)
	 *
	 * @param cbParams
	 * 
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年2月22日
	 */
	public ResultInfo sendRate(Map<String, String> cbParams) {
		ResultInfo result;

		// 签名，状态码，仿重单处理;
		result = this.checkSign(cbParams, HfPayType.TRANSFER);

		if (result.code < 1) {

			return result;
		}

		String requestMark = cbParams.get("MerPriv");

		Map<String, String> remarkParams = this.queryRequestParams(requestMark);

		if (remarkParams == null) {
			result.code = -1;
			result.msg = "查询托管请求备注参数失败";

			return result;
		}

		long billInvestId = Convert.strToLong(remarkParams.get(RemarkPramKey.BILL_INVEST_ID), 0L);
		String orderNo = remarkParams.get(RemarkPramKey.ORDER_NO);

		return billInvestService.doSendRate(orderNo, billInvestId);
	}

	public ResultInfo sendSmsCode(Map<String, String> respParams) {

		ResultInfo result;

		// 签名，状态码，仿重单处理;
		result = this.checkSign(respParams, HfPayType.SENDSMSCODE);

		if (result.code < 1) {

			return result;
		}

		String smsSeq = respParams.get("SmsSeq");

		if (StringUtils.isBlank(smsSeq)) {
			result.code = -1;
			result.msg = "短信序号为空";
		}

		// 可以做到放到session中

		result.code = 1;
		result.msg = "短信验证码发送成功";
		result.obj = smsSeq;
		if (!ConfConst.IS_CHECK_MSG_CODE) {
			result.obj = "AAAAAAAA";
		}
		return result;
	}

	public ResultInfo userRegisterByBos(Map<String, String> cbParams, Map<String, String> remarkParams) {
		ResultInfo result = new ResultInfo();

		// 签名，状态码，仿重单处理;
		result = this.checkSign(cbParams, HfPayType.USERREGISTER);
		if (result.code < 1) {

			return result;
		}

		String regStat = cbParams.get("RegStat");
		if (!"S".equals(regStat)) {
			// 开户失败
			result.code = -1;
			result.msg = "开户失败！";
			return result;
		}

		long userId = Long.parseLong(remarkParams.get(RemarkPramKey.USER_ID));
		String cardId = remarkParams.get(RemarkPramKey.CARD_ID);
		String bankId = remarkParams.get(RemarkPramKey.BANK_ID);
		String provId = remarkParams.get(RemarkPramKey.PROV_ID);
		String areaId = remarkParams.get(RemarkPramKey.AREA_ID);

		String paymentAccount = cbParams.get("UsrCustId");
		String realName = cbParams.get("UsrName");
		String idNumber = cbParams.get("IdNo");
		String email = cbParams.get("UsrEmail");
		String hfUserName = cbParams.get("UsrId");
		String mobile = cbParams.get("UsrMp");

		result = userFundService.doCreateAccount(userId, paymentAccount, realName, idNumber, email, hfUserName);

		if (result.code < 1) {

			return result;
		}

		String bankName = Bank.getBank(bankId).name;

		result = userFundService.doBindCard(userId, bankName, bankId, cardId, provId, areaId, mobile);

		if (result.code < 1) {

			return result;
		}

		JPAPlugin.closeTx(false);
		JPAPlugin.startTx(false);

		/** CPS推广-开户奖励积分 */
		cpsService.grantCpsOpenAccountScore(userId);

		/** 发送红包 */
		redpacketService.sendRegisterRedPacketToUser(userId, t_red_packet.RedpacketType.REGISTER.code, "开户红包");

		result.code = 1;
		result.msg = "开户成功";

		return result;
	}

	/**
	 * 
	 */
	public ResultInfo bosAcctActivate(Map<String, String> cbParams, Map<String, String> remarkParams) {
		ResultInfo result = new ResultInfo();
		result = this.checkSign(cbParams, HfPayType.BOSACCTACTIVATE);

		if (result.code < 1) {
			return result;
		}

		String paymentAccount = cbParams.get("UsrCustId");

		long userId = userFundService.findUserIdByPaymentAccount(paymentAccount);

		result = userFundService.doAcctived(userId);

		if (result.code < 1) {
			return result;
		}

		String cardId = cbParams.get("CardId");
		String bankId = cbParams.get("BankId");
		String mobile = cbParams.get("UsrMp");
		String provId = cbParams.get("ProvId");
		String areaId = cbParams.get("AreaId");

		String bankName = "";
		if (bankId != null) {
			Bank bank = Bank.getBank(bankId);
			if (bank != null) {
				bankName = Bank.getBank(bankId).name;
			}
		}

		result = userFundService.doBindCard(userId, bankName, bankId, cardId, provId, areaId, mobile);

		return result;
	}

	/**
	 * @param respParams
	 *            回调参数
	 * @param reqParams
	 *            请求参数
	 * @return
	 */
	public ResultInfo quickBinding(Map<String, String> respParams, Map<String, String> reqParams) {

		ResultInfo result;

		// 签名，状态码，仿重单处理;
		result = this.checkSign(respParams, HfPayType.QUICKBINDING);

		if (result.code < 1) {

			return result;
		}

		String paymentAccount = respParams.get("UsrCustId");
		String cardId = respParams.get("OpenAcctId");
		String bankId = reqParams.get("BankId");
		String provId = reqParams.get("ProvId");
		String areaId = reqParams.get("AreaId");
		String mobile = reqParams.get("UsrMp");

		long userId = userFundService.findUserIdByPaymentAccount(paymentAccount);

		String bankName = Bank.getBank(bankId).name;

		result = userFundService.doBindCard(userId, bankName, bankId, cardId, provId, areaId, mobile);

		return result;
	}

	public ResultInfo directRecharge(Map<String, String> cbParams, Map<String, String> remarkParams) {

		ResultInfo result = new ResultInfo();

		// 签名，状态码，仿重单处理;
		result = this.checkSign(cbParams, HfPayType.DIRECTRECHARGE);
		if (result.code < 1) {

			return result;
		}

		String usrCustId = cbParams.get("UsrCustId");

		long userId = userFundService.findUserIdByPaymentAccount(usrCustId);
		double rechargeAmt = Double.parseDouble(cbParams.get("TransAmt"));
		String serviceOrderNo = cbParams.get("OrdId");

		result = userFundService.doRecharge(userId, rechargeAmt, serviceOrderNo);

		if (result.code < 1) {

			return result;
		}

		// 积分商城
		if (ModuleConst.EXT_MALL) {

			double score = Convert.strToDouble(settingService.findSettingValueByKey(SettingKey.FIRST_RECHARGE), 0);

			if (score > 0) {

				// 首次充值
				int row = rechargeUserService.countDealOfUser(userId, t_recharge_user.Status.SUCCESS);
				if (row == 1) {

					/** 添加用户积分记录 */
					Map<String, String> summaryPrams = new HashMap<String, String>();
					summaryPrams.put("score", (int) score + "");
					result = scoreUserService.addScoreRecord(1, userId, (int) score,
							t_score_user.OperationType.FIRST_RECHARGE, summaryPrams);

					if (result.code < 1) {

						JPA.setRollbackOnly();
						result.code = -1;
						result.msg = "添加积分记录失败";

						return result;
					}
				}
			}
		}

		result.code = 1;
		result.msg = "充值成功";

		return result;
	}

	public ResultInfo queryPersionInformation(Map<String, String> respParams, long userId) {

		ResultInfo result;
		// 签名，状态码，仿重单处理;
		result = this.checkSign(respParams, HfPayType.QUERYUSRINFO);

		if (result.code < 1) {

			return result;
		}

		String usrCustId = respParams.get("UsrCustId");
		String usrId = respParams.get("UsrId");
		String certId = respParams.get("CertId");

		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);

		if (userFund == null) {
			result.code = -1;
			result.msg = "查询用户账户信息失败";
			return result;
		}

		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);

		if (userInfo == null) {
			result.code = -1;
			result.msg = "查询用户信息失败";
			return result;
		}

		userFund.payment_account = usrCustId;

		userInfo.id_number = certId;

		userInfo.hf_name = usrId;

		userInfoService.update(userInfo);

		if (userInfoService.update(userInfo) && userFundService.update(userFund)) {
			result.code = 1;
			result.msg = "成功";
		}

		result.code = -1;
		result.msg = "失败";
		return result;
	}

	public ResultInfo transfer(Map<String, String> respParams) {
		// 签名，状态码，仿重单处理;
		return this.checkSign(respParams, HfPayType.TRANSFER);
	}

	public ResultInfo repayment(Map<String, String> cbParams) {
		ResultInfo result;

		// 签名，状态码，仿重单处理;
		result = this.checkSign(cbParams, HfPayType.REPAYMENT);
		if (result.code < 1) {
			return result;
		}

		String serviceOrderNo = cbParams.get("OrdId");

		String requestMark = cbParams.get("MerPriv");

		Map<String, String> remarkParams = this.queryRequestParams(requestMark);
		if (remarkParams == null) {
			result.code = -1;
			result.msg = "查询托管请求备注参数失败";

			return result;
		}

		long billId = Long.parseLong(remarkParams.get(RemarkPramKey.BILL_ID));
		long billInvestId = Long.parseLong(remarkParams.get(RemarkPramKey.BILL_INVEST_ID));
		Map<String, Double> billInvestFee = new Gson().fromJson(remarkParams.get(RemarkPramKey.BILL_INVEST_FEE),
				new TypeToken<Map<String, Double>>() {
				}.getType());

		return billService.doRepayment(billId, billInvestId, billInvestFee, serviceOrderNo);
	}

	public ResultInfo autoTenderCancle(Map<String, String> respParams, Map<String, String> reqParams) {

		ResultInfo result;

		// 签名，状态码，仿重单处理;
		result = this.checkSign(respParams, HfPayType.AUTOTENDERCANCLE);

		if (result.code < 1) {
			return result;
		}

		String ordId = respParams.get("UnFreezeOrdId");
		String paymentAccount = respParams.get("UsrCustId");
		double transAmt = Double.parseDouble(respParams.get("TransAmt"));
		String isUnFreeze = respParams.get("IsUnFreeze");
		long investId = Long.parseLong(reqParams.get(RemarkPramKey.INVEST_ID));

		long userId = userFundService.findUserIdByPaymentAccount(paymentAccount);

		if ("Y".equals(isUnFreeze)) {
			// 解冻资金
			t_invest invest = this.investService.findByID(investId);
			if (invest == null) {
				result.code = -1;
				result.msg = "无法找到投标信息";
				return result;
			}

			long bidId = invest.bid_id;

			t_bid bid = this.bidService.findByID(bidId);
			if (bid == null) {
				result.code = -1;
				result.msg = "无法找到标的信息";
				return result;
			}

			if (bid.getStatus().code != t_bid.Status.FUNDRAISING.code
					&& bid.getStatus().code != t_bid.Status.AUDITING.code) {
				result.code = -1;
				result.msg = "标的状态错误";
				return result;
			}

			bid.has_invested_amount = bid.has_invested_amount - transAmt;
			bid.invest_count = bid.invest_count - 1;
			double schedule = Arith.divDown(Arith.mul(bid.has_invested_amount, 100.0), bid.amount, 2);
			bid.loan_schedule = schedule;
			bid.real_invest_expire_time = null;
			bid.status = 1;

			// 更新标的信息
			boolean flag = bidService.update(bid);

			if (!flag) {
				LoggerUtil.info(true, "更新标的信息失败");
				result.code = -1;
				result.msg = "更新标的信息失败";
				return result;
			}

			// 删除投标信息
			int out = investService.delInvest(investId);

			if (out == -99) {
				result.code = -1;
				result.msg = "删除投标信息失败";
				return result;
			}

			// 处理资金记录
			t_user_fund userFund = userFundService.queryUserFundByUserId(userId); // 刷新用户资金

			flag = userFundService.userFundUnFreeze(userId, transAmt);

			if (!flag) {
				result.code = -1;
				result.msg = "增加资金错误";
				return result;
			}

			Map<String, String> summaryPrams = new HashMap<String, String>();
			summaryPrams.put("bidNo", bid.bid_no);
			flag = dealUserService.addDealUserInfo(ordId, // 订单号
					userId, transAmt, // 实际投资金额需要扣除掉使用的现金券金额
					userFund.balance + transAmt, userFund.freeze - transAmt, t_deal_user.OperationType.INVEST_UNFREEZE,
					summaryPrams);

			if (!flag) {
				result.code = -1;
				result.msg = "添加交易记录失败！";
				return result;
			}
			result.code = 1;
			result.msg = "解冻成功";
		} else if ("N".equals(isUnFreeze)) {
			// 不解冻资金无需操作数据
			result.code = 1;
			result.msg = "解冻成功";
		} else {
			// 解冻资金类型错误
			result.code = -1;
			result.msg = "是否解冻资金类型错误";
		}

		return result;
	}

	public ResultInfo corpRegister(Map<String, String> respParams, Map<String, String> reqParams) {

		ResultInfo result;

		// 签名，状态码，仿重单处理;
		result = this.checkSign(respParams, HfPayType.CORPREGISTER);

		if (result.code < 1) {
			return result;
		}

		String usrId = respParams.get("UsrId");
		String usrName = respParams.get("UsrName");
		String usrCustId = respParams.get("UsrCustId");
		String auditStat = respParams.get("AuditStat");
		String openBankId = respParams.get("OpenBankId");
		String cardId = respParams.get("CardId");
		long userId = Long.parseLong(reqParams.get(RemarkPramKey.USER_ID));
		String instuCode = reqParams.get(RemarkPramKey.INSTU_CODE);
		String busiCode = reqParams.get(RemarkPramKey.BUSI_CODE);
		String taxCode = reqParams.get(RemarkPramKey.TAX_CODE);
		String guarType = reqParams.get(RemarkPramKey.GUAR_TYPE);
		String guarCorpEarnestAmt = reqParams.get(RemarkPramKey.GUAR_CORP_EARNEST_AMT);

		boolean flagGuarType = false;

		if ("Y".equals(guarType)) {
			flagGuarType = true;
		}

		t_corp_info corpInfo = new t_corp_info(userId, instuCode, busiCode, taxCode, flagGuarType,
				Double.parseDouble(guarCorpEarnestAmt), Status.getEnumByValue(auditStat).code);

		// 保存企业信息
		result = corpInfoService.saveOrUpdate(corpInfo);

		if (result.code != 1) {
			return result;
		}

		result = userFundService.doCreateCorpAccount(userId, usrCustId, usrId, usrName);

		if (result.code != 1) {
			return result;
		}

		String bankName = Bank.getBank(openBankId).name;

		result = userFundService.doBindCard(userId, bankName, openBankId, cardId, "", "", "");

		if (result.code < 1) {

			return result;
		}

		return result;
	}

	public ResultInfo corpRegisterQuery(Map<String, String> respParams) {

		ResultInfo result;

		// 签名，状态码，仿重单处理;
		result = this.checkSign(respParams, HfPayType.CORPREGISTERQUERY);

		return result;
	}
	/**
	 * 发放体验金奖励
	 * @return
	 */
	public ResultInfo ExpGoldSend(Long userId,double investAmt,int client){
		ResultInfo result=new ResultInfo();
		// 首次的投资
		int investCount = investService.countInvestOfUser(userId,true);
				//查询投资次数  TODO
		    if(investCount >=2 ){
			//查询用户体验金账户信息（条件为可用）
			t_exp_gold_account_user exp = expGoldAccountUserService.queryExpGoldAccountUserByUserId(userId);
			if(exp != null){
				double expGoldAmount = exp.getAmount();
				//转账
				double transferAccountsAmount = Double.parseDouble(HfUtils.formatAmount(expGoldAmount + investAmt*0.001));
				String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.TRANSFER); 
				ResultInfo res = PaymentProxy.getInstance().transfer(Client.PC.code, serviceOrderNo, userId, transferAccountsAmount);
				if(res.code == 1){
					JPAUtil.transactionBegin();
					//修改体验金账户（状态为不可用）
					expGoldAccountUserService.updateExpGoldAccountUserByUserId(userId, 1);
					JPAUtil.transactionCommit();
							//插入体验金转现记录
					expGoldShowRecordService.saveExpGoldShowRecord(userId, transferAccountsAmount, new Date(), "");
					JPAUtil.transactionBegin();
					// 处理平台记录
					dealPlatformService.addPlatformDeal(userId, transferAccountsAmount, DealRemark.EXPERIENCE_TRANSFER,null);
				
					// 处理个人记录
					JPAUtil.transactionBegin();
					t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
					userFund.balance = userFund.balance + transferAccountsAmount;
					userFundService.update(userFund);
					JPAUtil.transactionCommit();
					// 更新用户签名
					JPAUtil.transactionBegin();
					userFundService.userFundSignUpdate(userId);
					dealUserService.addDealUserInfo(serviceOrderNo, userId, transferAccountsAmount, userFund.balance,
							userFund.freeze, OperationType.EXPERIENCE_TRANSFER, null);
					JPAUtil.transactionCommit();
					result.code=1;
					result.msg="发放体验金成功！";
					
				}	
				
			}else{
				result.code=-10;
				result.msg="发放体验金失败！体验金未领取";
				
			}
		}else{
			result.code=-11;
			result.msg="发放体验金失败！不符合发放条件";
		}
		return result;
	}

}