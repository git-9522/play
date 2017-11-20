package controllers.payment.hf;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import hf.HfConsts;
import hf.HfPayType;
import hf.HfPaymentCallBackService;
import models.core.entity.t_bid;
import models.entity.t_payment_request;
import play.Logger;
import services.core.BidService;

public class HfPaymentCallBackCtrl extends PaymentBaseCtrl {

	private static HfPaymentCallBackService hfPaymentCallBackService = Factory
			.getSimpleService(HfPaymentCallBackService.class);

	private static HfPaymentCallBackCtrl instance = null;

	private static BidService bidService = Factory.getService(BidService.class);

	private HfPaymentCallBackCtrl() {

	}

	public static HfPaymentCallBackCtrl getInstance() {
		if (instance == null) {
			synchronized (HfPaymentCallBackCtrl.class) {
				if (instance == null) {
					instance = new HfPaymentCallBackCtrl();
				}
			}
		}

		return instance;
	}

	/**
	 * 开户同步
	 */
	public static void userRegisterSyn() {
		ResultInfo result = null;

		// 获取回调参数
		Map<String, String> cbParams = hfPaymentCallBackService.getRespParams(params);

		// 打印、保存接口回调参数
		hfPaymentCallBackService.printCallBackData("开户同步回调", cbParams, ServiceType.REGIST, HfPayType.USERREGISTER);

		Map<String, String> remarkParams = hfPaymentCallBackService.queryRequestParams(cbParams.get("MerPriv"));
		if (remarkParams == null) {
			flash.error("查询托管请求备注参数失败");

			redirect("front.account.MySecurityCtrl.securityPre");
		}

		result = hfPaymentCallBackService.userRegister(cbParams, remarkParams);

		Client client = Client.getEnum(Convert.strToInt(remarkParams.get(RemarkPramKey.CLIENT), Client.PC.code));

		switch (client) {
		case PC: {
			if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
				flash.success("资金托管开户成功");
			} else {
				LoggerUtil.info(true, result.msg);
				flash.error(result.msg);
			}
			redirect("front.account.MySecurityCtrl.securityPre");
			break;
		}
		case ANDROID:
		case IOS: {
			if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
				result.code = 1;
				result.msg = "资金托管开户成功";
			} else {
				LoggerUtil.info(true, result.msg);
			}
			redirectApp(result);
			break;
		}
		case WECHAT: {
			if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
				redirect("wechat.WechatBaseController.toResultPage", WxPageType.PAGE_COMMUNAL_SUCC, "资金托管开户成功");
			} else {
				LoggerUtil.info(true, result.msg);
				redirect("wechat.WechatBaseController.toResultPage", WxPageType.PAGE_COMMUNAL_FAIL, result.msg);
			}

			break;
		}
		default:
			break;
		}

	}

	/**
	 * 开户异步
	 */
	public static void userRegisterAyns() {
		ResultInfo result = null;

		// 获取回调参数
		Map<String, String> cbParams = hfPaymentCallBackService.getRespParams(params);

		// 打印、保存接口回调参数
		hfPaymentCallBackService.printCallBackData("开户异步回调", cbParams, ServiceType.REGIST, HfPayType.USERREGISTER);

		Map<String, String> remarkParams = hfPaymentCallBackService.queryRequestParams(cbParams.get("MerPriv"));
		if (remarkParams == null) {
			LoggerUtil.info(true, "查询托管请求备注参数失败");

			return;
		}

		result = hfPaymentCallBackService.userRegister(cbParams, remarkParams);
		// 回调业务成功执行,打印第三方需要的成功标示
		if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
			renderText(HfConsts.RESP_PREFIX + cbParams.get(HfConsts.TRXID));
		} else {
			// 打印日志，事务回滚
			LoggerUtil.info(true, result.msg);
		}
	}

	/**
	 * 充值同步回调
	 */
	public static void netSaveSyn() {
		ResultInfo result = null;

		// 获取参数
		Map<String, String> cbParams = hfPaymentCallBackService.getRespParams(params);

		// 打印、保存接口回调参数
		hfPaymentCallBackService.printCallBackData("充值同步回调", cbParams, ServiceType.RECHARGE, HfPayType.NETSAVE);

		Map<String, String> remarkParams = hfPaymentCallBackService.queryRequestParams(cbParams.get("MerPriv"));
		if (remarkParams == null) {
			flash.error("查询托管请求备注参数失败");
			redirect("front.account.MyAccountCtrl.rechargePre");
		}

		// 业务调用
		result = hfPaymentCallBackService.netSave(cbParams, remarkParams);

		Client client = Client.getEnum(Convert.strToInt(remarkParams.get(RemarkPramKey.CLIENT), Client.PC.code));

		switch (client) {
		case PC: {
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
		case ANDROID:
		case IOS: {
			if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
				result.code = 1;
				result.msg = "充值成功";
			} else {
				LoggerUtil.info(true, result.msg);
			}
			redirectApp(result);
			break;
		}
		case WECHAT: {
			if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
				result.msg = "充值成功";
				redirect("wechat.WechatBaseController.toResultPage", WxPageType.PAGE_COMMUNAL_SUCC, result.msg);
			} else {
				LoggerUtil.info(true, result.msg);
				redirect("wechat.WechatBaseController.toResultPage", WxPageType.PAGE_COMMUNAL_FAIL, result.msg);
			}
			break;
		}

		default:
			break;
		}

	}

	/**
	 * 充值异步回调
	 */
	public static void netSaveAyns() {
		ResultInfo result = null;

		// 获取参数
		Map<String, String> cbParams = hfPaymentCallBackService.getRespParams(params);

		// 打印、保存接口回调参数
		hfPaymentCallBackService.printCallBackData("充值异步回调", cbParams, ServiceType.RECHARGE, HfPayType.NETSAVE);

		Map<String, String> remarkParams = hfPaymentCallBackService.queryRequestParams(cbParams.get("MerPriv"));

		if (remarkParams == null) {
			LoggerUtil.info(true, "查询托管请求备注参数失败");

			return;
		}

		// 业务调用
		result = hfPaymentCallBackService.netSave(cbParams, remarkParams);

		// 回调业务成功执行,打印第三方需要的成功标示
		if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {

			renderText(HfConsts.RESP_PREFIX + cbParams.get(HfConsts.TRXID));
		} else {
			LoggerUtil.info(true, result.msg);
		}
	}

	/**
	 * 商户充值同步回调
	 */
	public static void merNetSaveSyn() {
		ResultInfo result = null;

		// 获取参数
		Map<String, String> cbParams = hfPaymentCallBackService.getRespParams(params);

		// 打印、保存接口回调参数
		hfPaymentCallBackService.printCallBackData("商户充值同步回调", cbParams, ServiceType.MERCHANT_RECHARGE,
				HfPayType.DIRECTRECHARGE);

		// 业务调用
		result = hfPaymentCallBackService.merNetSave(cbParams);
		if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
			flash.success("商户充值成功");
			redirect("back.finance.MerchantMngCtrl.toMerchantPre");
		} else {
			flash.error(result.msg);
			LoggerUtil.info(true, result.msg);
			redirect("back.finance.MerchantMngCtrl.toMerchantRechargePre");
		}
	}

	/**
	 * 商户充值异步回调
	 */
	public static void merNetSaveAyns() {
		ResultInfo result = null;

		// 获取参数
		Map<String, String> cbParams = hfPaymentCallBackService.getRespParams(params);

		// 打印、保存接口回调参数
		hfPaymentCallBackService.printCallBackData("商户充值异步回调", cbParams, ServiceType.MERCHANT_RECHARGE,
				HfPayType.DIRECTRECHARGE);

		// 业务调用
		result = hfPaymentCallBackService.merNetSave(cbParams);
		// 回调业务成功执行,打印第三方需要的成功标示
		if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {

			renderText(HfConsts.RESP_PREFIX + cbParams.get(HfConsts.TRXID));
		} else {
			LoggerUtil.info(true, result.msg);
		}
	}

	/**
	 * 绑卡异步
	 */
	public static void userBindCardAyns() {
		ResultInfo result = null;

		// 获取参数
		Map<String, String> cbParams = hfPaymentCallBackService.getRespParams(params);

		// 打印、保存接口回调参数
		hfPaymentCallBackService.printCallBackData("绑卡异步回调", cbParams, ServiceType.BIND_CARD, HfPayType.USERBINDCARD);

		// 业务调用
		result = hfPaymentCallBackService.userBindCard(cbParams);
		if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {

			renderText(HfConsts.RESP_PREFIX + cbParams.get(HfConsts.TRXID));
		} else {
			LoggerUtil.info(true, result.msg);
		}
	}

	/**
	 * 提现同步
	 */
	public static void cashSyn() {
		ResultInfo result = null;

		// 获取参数
		Map<String, String> cbParams = hfPaymentCallBackService.getRespParams(params);

		// 打印、保存接口回调参数
		hfPaymentCallBackService.printCallBackData("提现同步回调", cbParams, ServiceType.WITHDRAW, HfPayType.CASH);

		Map<String, String> remarkParams = hfPaymentCallBackService.queryRequestParams(cbParams.get("MerPriv"));
		if (remarkParams == null) {
			flash.error("查询托管请求备注参数失败");

			redirect("front.account.MyAccountCtrl.withdrawPre");
		}

		// 业务调用
		result = hfPaymentCallBackService.withdrawal(cbParams, remarkParams);

		Client client = Client.getEnum(Convert.strToInt(remarkParams.get(RemarkPramKey.CLIENT), Client.PC.code));

		switch (client) {
		case PC: {
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
		case IOS: {
			if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
				result.code = 1;
				result.msg = "提现成功";
			} else {
				LoggerUtil.info(true, result.msg);
			}
			redirectApp(result);
			break;
		}
		case WECHAT: {
			if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
				result.msg = "提现成功";
				redirect("wechat.WechatBaseController.toResultPage", WxPageType.PAGE_COMMUNAL_SUCC, result.msg);
			} else {
				LoggerUtil.info(true, result.msg);
				redirect("wechat.WechatBaseController.toResultPage", WxPageType.PAGE_COMMUNAL_FAIL, result.msg);
			}
			break;
		}

		default:
			break;
		}

	}

	/**
	 * 提现异步
	 */
	public static void cashAyns() {
		// 特殊业务处理，提现异步回调线程休眠10秒, 为了确保，同步回调先回调。
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			LoggerUtil.error(true, e, "提现异步回调线程休眠10秒，失败");
		}

		ResultInfo result = null;

		// 获取参数
		Map<String, String> cbParams = hfPaymentCallBackService.getRespParams(params);

		// 打印、保存接口回调参数
		hfPaymentCallBackService.printCallBackData("提现异步回调", cbParams, ServiceType.WITHDRAW, HfPayType.CASH);

		Map<String, String> remarkParams = hfPaymentCallBackService.queryRequestParams(cbParams.get("MerPriv"));
		if (remarkParams == null) {
			LoggerUtil.info(true, "查询托管请求备注参数失败");

			return;
		}

		// 业务调用
		result = hfPaymentCallBackService.withdrawal(cbParams, remarkParams);
		// 回调业务成功执行,打印第三方需要的成功标示
		if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
			renderText(HfConsts.RESP_PREFIX + cbParams.get(HfConsts.ORDID));
		} else {
			LoggerUtil.info(true, result.msg);
		}
	}

	/**
	 * 商户提现同步回调
	 */
	public static void merCashSyn() {
		ResultInfo result = null;

		// 获取参数
		Map<String, String> cbParams = hfPaymentCallBackService.getRespParams(params);

		// 打印、保存接口回调参数
		hfPaymentCallBackService.printCallBackData("商户提现同步回调", cbParams, ServiceType.MERCHANT_WITHDRAWAL,
				HfPayType.CASH);

		// 业务调用
		result = hfPaymentCallBackService.merCash(cbParams);
		if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
			flash.success("商户提现成功");
			redirect("back.finance.MerchantMngCtrl.toMerchantPre");
		} else {
			flash.error(result.msg);
			LoggerUtil.info(true, result.msg);
			redirect("back.finance.MerchantMngCtrl.toMerchantWithdrawalPre");
		}
	}

	/**
	 * 商户提现异步回调
	 */
	public static void merCashAyns() {
		ResultInfo result = null;

		// 获取参数
		Map<String, String> cbParams = hfPaymentCallBackService.getRespParams(params);

		// 打印、保存接口回调参数
		hfPaymentCallBackService.printCallBackData("商户提现异步回调", cbParams, ServiceType.MERCHANT_WITHDRAWAL,
				HfPayType.CASH);

		// 业务调用
		result = hfPaymentCallBackService.merCash(cbParams);
		// 回调业务成功执行,打印第三方需要的成功标示
		if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {

			renderText(HfConsts.RESP_PREFIX + cbParams.get(HfConsts.ORDID));
		} else {
			LoggerUtil.info(true, result.msg);
		}
	}

	/**
	 * 标的登记成功，页面跳转
	 *
	 * @param bid
	 * @param bidCreateFrom
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月19日
	 */
	public void addBidInfoWS(ResultInfo result, t_bid bid, int bidCreateFrom) {

		if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
			if (bidCreateFrom == Constants.BID_CREATE_FROM_FRONT) {
				// 标的发布成功，数据库中才有标的信息，取出标的id
				long bidId = bidService.findIdByMerBidNo(bid.mer_bid_no, -1L);
				if (bidId == -1) {
					Logger.info("===========bid.mer_bid_no=" + bid.mer_bid_no + ";bid.product_id" + bid.product_id);
					flash.success("项目发布成功");
					redirect("front.LoanCtrl.toLoanPre");
				}

				redirect("front.LoanCtrl.uploadBidItemPre", bidId);
			}

			if (bidCreateFrom == Constants.BID_CREATE_FROM_BACK) {
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
	 * 标的登记异步回调
	 */
	public static void addBidInfoAyns() {
		ResultInfo result = null;

		// 获取参数
		Map<String, String> cbParams = hfPaymentCallBackService.getRespParams(params);

		// 打印、保存接口回调参数
		hfPaymentCallBackService.printCallBackData("标的登记异步回调", cbParams, ServiceType.BID_CREATE, HfPayType.ADDBIDINFO);

		// 业务调用
		result = hfPaymentCallBackService.addBidInfo(cbParams);
		// 回调业务成功执行,打印第三方需要的成功标示
		if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
			renderText(HfConsts.RESP_PREFIX + cbParams.get(HfConsts.PROID));
		} else {
			LoggerUtil.info(true, result.msg);
		}
	}

	/**
	 * 冻结保证金异步回调
	 */
	public static void freezeBailAmountAyns() {
		ResultInfo result = null;

		// 获取参数
		Map<String, String> cbParams = hfPaymentCallBackService.getRespParams(params);

		// 打印、保存接口回调参数
		hfPaymentCallBackService.printCallBackData("冻结保证金异步回调", cbParams, ServiceType.BID_CREATE,
				HfPayType.USRFREEZEBG);

		// 业务调用
		result = hfPaymentCallBackService.freezeBail(cbParams);
		// 回调业务成功执行,打印第三方需要的成功标示
		if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
			renderText(HfConsts.RESP_PREFIX + cbParams.get(HfConsts.ORDID));
		} else {
			LoggerUtil.info(true, result.msg);
		}
	}

	/**
	 * 主动投标同步
	 */
	public static void initiativeTenderSyn() {
		ResultInfo result = null;

		// 获取参数
		Map<String, String> cbParams = hfPaymentCallBackService.getRespParams(params);

		// 打印、保存接口回调参数
		hfPaymentCallBackService.printCallBackData("主动投标同步回调", cbParams, ServiceType.INVEST,
				HfPayType.INITIATIVETENDER);

		// 业务调用
		Map<String, String> remarkParams = hfPaymentCallBackService.queryRequestParams(cbParams.get("MerPriv"));
		if (remarkParams == null) {
			flash.error("查询托管请求备注参数失败");

			redirect("front.invest.InvestCtrl.toInvestPre");
		}

		result = hfPaymentCallBackService.invest(cbParams, remarkParams);
		long bidId = Long.parseLong(remarkParams.get(RemarkPramKey.BID_ID));
		double investAmt = Double.parseDouble(remarkParams.get(RemarkPramKey.INVEST_AMT));

		Client client = Client.getEnum(Convert.strToInt(remarkParams.get(RemarkPramKey.CLIENT), Client.PC.code));

		switch (client) {
		case PC: {
			if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {// 主动投标已经成功执行

				result.code = 1;
				result.msg = "投标成功";
				redirect("front.invest.InvestCtrl.investSuccessPre", bidId, investAmt);
			} else {
				flash.error(result.msg);
				LoggerUtil.info(true, result.msg);
				redirect("front.invest.InvestCtrl.investPre", bidId); // 投标失败
			}
			break;
		}
		case ANDROID:
		case IOS: {
			if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
				result.code = 1;
				result.msg = "投标成功";
			} else {
				LoggerUtil.info(true, result.msg);
			}
			redirectApp(result);
			break;
		}
		case WECHAT: {
			if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {// 主动投标已经成功执行
				result.code = 1;
				result.msg = "投标成功";
				redirect("wechat.WechatBaseController.toResultPage", WxPageType.PAGE_COMMUNAL_SUCC, result.msg);
			} else {
				LoggerUtil.info(true, result.msg);
				redirect("wechat.WechatBaseController.toResultPage", WxPageType.PAGE_COMMUNAL_FAIL, result.msg); // 投标失败
			}
			break;
		}
		default:
			break;
		}

	}

	/**
	 * 主动投标异步
	 */
	public static void initiativeTenderAyns() {
		ResultInfo result = null;

		// 获取参数
		Map<String, String> cbParams = hfPaymentCallBackService.getRespParams(params);

		// 打印、保存接口回调参数
		hfPaymentCallBackService.printCallBackData("主动投标异步回调", cbParams, ServiceType.INVEST,
				HfPayType.INITIATIVETENDER);

		// 业务调用
		String requestMark = cbParams.get("MerPriv");
		Map<String, String> remarkParams = hfPaymentCallBackService.queryRequestParams(requestMark);
		if (remarkParams == null) {
			LoggerUtil.info(true, "查询托管请求备注参数失败");

			return;
		}

		result = hfPaymentCallBackService.invest(cbParams, remarkParams);
		// 回调业务成功执行,打印第三方需要的成功标示
		if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
			renderText(HfConsts.RESP_PREFIX + cbParams.get(HfConsts.ORDID));
		} else {
			LoggerUtil.info(true, result.msg);
		}
	}

	/**
	 * 解冻资金异步
	 */
	public static void usrUnFreezeAyns() {
		// 获取参数
		Map<String, String> cbParams = hfPaymentCallBackService.getRespParams(params);

		// 解冻不做任何业务,直接打印第三方需要的成功标示
		renderText(HfConsts.RESP_PREFIX + cbParams.get(HfConsts.ORDID));
	}

	/**
	 * 放款异步回调
	 */
	public static void loansAyns() {
		// 获取参数
		Map<String, String> resultMap = hfPaymentCallBackService.getRespParams(params);

		// 业务调用（单笔放款，无异步业务）
		renderText(HfConsts.RESP_PREFIX + resultMap.get(HfConsts.ORDID));
	}

	/**
	 * 批量还款异步通知
	 */
	public static void batchRepaymentAyns() {
		ResultInfo result = null;

		// 获取参数
		Map<String, String> cbParams = hfPaymentCallBackService.getRespParams(params);

		// 打印、保存接口回调参数
		hfPaymentCallBackService.printCallBackData("批量还款异步回调", cbParams, ServiceType.REPAYMENT,
				HfPayType.BATCHREPAYMENT);

		// 业务调用
		result = hfPaymentCallBackService.repayment(cbParams);
		// 回调业务成功执行,打印第三方需要的成功标示
		if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
			renderText(HfConsts.RESP_PREFIX + cbParams.get(HfConsts.BATCHID));
		} else {
			LoggerUtil.info(true, result.msg);
		}
	}

	/**
	 * 垫付后还款异步回调
	 */
	public static void advanceRepaymentAyns() {
		ResultInfo result = null;

		// 获取参数
		Map<String, String> cbParams = hfPaymentCallBackService.getRespParams(params);

		// 打印、保存接口回调参数
		hfPaymentCallBackService.printCallBackData("垫付后还款异步回调", cbParams, ServiceType.REPAYMENT_AFER_ADVANCE,
				HfPayType.BATCHREPAYMENT);

		// 业务调用
		result = hfPaymentCallBackService.advanceRepayment(cbParams);
		// 回调业务成功执行,打印第三方需要的成功标示
		if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
			renderText(HfConsts.RESP_PREFIX + cbParams.get(HfConsts.BATCHID));
		} else {
			LoggerUtil.info(true, result.msg);
		}
	}

	/**
	 * 本息垫付异步回调
	 */
	public static void advanceAyns() {
		ResultInfo result = null;

		// 获取参数
		Map<String, String> resultMap = hfPaymentCallBackService.getRespParams(params);

		// 打印、保存接口回调参数
		hfPaymentCallBackService.printCallBackData("本息垫付异步回调", resultMap, ServiceType.ADVANCE,
				HfPayType.BATCHREPAYMENT);

		// 业务调用
		result = hfPaymentCallBackService.advance(resultMap);

		// 回调业务成功执行,打印第三方需要的成功标示
		if (result.code >= 0 || result.code == ResultInfo.ALREADY_RUN) {
			renderText(HfConsts.RESP_PREFIX + resultMap.get(HfConsts.BATCHID));
		} else {
			LoggerUtil.info(true, result.msg);
		}
	}

	/**
	 * 线下收款异步回调
	 */
	public static void offlineReceiveAyns() {
		ResultInfo result = null;

		// 获取参数
		Map<String, String> resultMap = hfPaymentCallBackService.getRespParams(params);

		// 打印、保存接口回调参数
		hfPaymentCallBackService.printCallBackData("线下收款异步回调", resultMap, ServiceType.OFFLINE_RECEIVE,
				HfPayType.BATCHREPAYMENT);

		// 业务调用
		result = hfPaymentCallBackService.offlineReceive(resultMap);

		// 回调业务成功执行,打印第三方需要的成功标示
		if (result.code >= 0 || result.code == ResultInfo.ALREADY_RUN) {
			renderText(HfConsts.RESP_PREFIX + resultMap.get(HfConsts.BATCHID));
		} else {
			LoggerUtil.info(true, result.msg);
		}
	}

	/**
	 * 奖励兑换异步回调
	 */
	public static void conversionAyns() {
		ResultInfo result = null;

		// 获取参数
		Map<String, String> cbParams = hfPaymentCallBackService.getRespParams(params);

		// 打印、保存接口回调参数
		hfPaymentCallBackService.printCallBackData("奖励兑换异步回调", cbParams, ServiceType.CONVERSION, HfPayType.TRANSFER);

		// 业务调用
		result = hfPaymentCallBackService.conversion(cbParams);

		// 回调业务成功执行,打印第三方需要的成功标示
		if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
			renderText(HfConsts.RESP_PREFIX + cbParams.get(HfConsts.ORDID));
		} else {
			LoggerUtil.info(true, result.msg);
		}
	}

	/**
	 * 最高竞价者确认成交(债权转让)同步回调
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月29日
	 */
	public static void debtorTransferSyn() {
		ResultInfo result = null;

		// 获取参数
		Map<String, String> cbParams = hfPaymentCallBackService.getRespParams(params);

		// 打印、保存接口回调参数
		hfPaymentCallBackService.printCallBackData("债权转让同步回调", cbParams, ServiceType.DEBT_TRANSFER,
				HfPayType.CREDITASSIGN);

		String requestMark = cbParams.get("MerPriv");
		Map<String, String> remarkParams = hfPaymentCallBackService.queryRequestParams(requestMark);
		if (remarkParams == null) {
			flash.error("查询托管请求备注参数失败");

			redirect("controllers.front.invest.InvestCtrl.toInvestPre");
		}

		// 业务调用
		result = hfPaymentCallBackService.debtTransfer(cbParams, remarkParams);

		Client client = Client.getEnum(Convert.strToInt(remarkParams.get(RemarkPramKey.CLIENT), Client.PC.code));
		long debtId = Long.parseLong(remarkParams.get(RemarkPramKey.DEBT_ID));

		switch (client) {
		case PC: {
			if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
				flash.success("债权已经成交");

				redirect("front.account.MyFundCtrl.accountManagePre", 2);
			} else {
				flash.error(result.msg);

				LoggerUtil.info(true, result.msg);
				redirect("controllers.front.invest.InvestCtrl.transferPre", debtId);
			}
		}
		case ANDROID:
		case IOS: {
			if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
				result.code = 1;
				result.msg = "债权已经成交";
			} else {
				LoggerUtil.info(true, result.msg);
			}
			redirectApp(result);
			break;
		}
		case WECHAT: {
			if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
				flash.success("债权已经成交");

				redirect("wechat.WechatBaseController.toResultPage", WxPageType.PAGE_COMMUNAL_SUCC, "购买债权成功");
			} else {
				LoggerUtil.info(true, result.msg);
				redirect("wechat.WechatBaseController.toResultPage", WxPageType.PAGE_COMMUNAL_FAIL, result.msg);
			}

			break;
		}
		default: {//

			break;
		}

		}

	}

	/**
	 * 最高竞价者确认成交(债权转让)异步回调
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月29日
	 */
	public static void debtorTransferAyns() {
		ResultInfo result = null;

		// 获取参数
		Map<String, String> cbParams = hfPaymentCallBackService.getRespParams(params);

		// 打印、保存接口回调参数
		hfPaymentCallBackService.printCallBackData("债权转让异步回调", cbParams, ServiceType.DEBT_TRANSFER,
				HfPayType.CREDITASSIGN);

		String requestMark = cbParams.get("MerPriv");
		Map<String, String> remarkParams = hfPaymentCallBackService.queryRequestParams(requestMark);
		if (remarkParams == null) {
			LoggerUtil.info(true, "查询托管请求备注参数失败");

			return;
		}

		// 业务调用
		result = hfPaymentCallBackService.debtTransfer(cbParams, remarkParams);
		if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
			renderText(HfConsts.RESP_PREFIX + cbParams.get(HfConsts.ORDID));
		} else {
			LoggerUtil.info(true, result.msg);
		}
	}

	/**
	 * 自动投标计划同步回调
	 */
	public static void autoInvestSignature() {

		ResultInfo result = null;

		// 获取回调参数
		Map<String, String> cbParams = hfPaymentCallBackService.getRespParams(params);

		// 打印、保存接口回调参数
		hfPaymentCallBackService.printCallBackData("自动投标计划同步回调", cbParams, ServiceType.AUTO_INVEST_SIGN,
				HfPayType.AUTOTENDERPLAN);

		result = hfPaymentCallBackService.autoInvestSignature(cbParams);
		if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
			flash.success("自动投标签约成功");
		} else {
			flash.error(result.msg);
			LoggerUtil.info(true, result.msg);
		}

		redirect("front.account.MyFundCtrl.accountManagePre", 4);
	}

	/**
	 * 自动投标异步
	 */
	public static void autoTenderAyns() {
		ResultInfo result = null;

		// 获取参数
		Map<String, String> cbParams = hfPaymentCallBackService.getRespParams(params);

		// 打印、保存接口回调参数
		hfPaymentCallBackService.printCallBackData("自动投标异步回调", cbParams, ServiceType.AUTO_INVEST, HfPayType.AUTOTENDER);

		// 业务调用
		String requestMark = cbParams.get("MerPriv");
		Map<String, String> remarkParams = hfPaymentCallBackService.queryRequestParams(requestMark);
		if (remarkParams == null) {
			LoggerUtil.info(true, "查询托管请求备注参数失败");

			return;
		}

		result = hfPaymentCallBackService.autoInvest(cbParams, remarkParams);
		// 回调业务成功执行,打印第三方需要的成功标示
		if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
			renderText(HfConsts.RESP_PREFIX + cbParams.get(HfConsts.ORDID));
		} else {
			LoggerUtil.info(true, result.msg);
		}
	}

	/**
	 * 商户转账异步回调
	 */
	public static void merchantTransferAyns() {
		ResultInfo result = null;

		// 获取参数
		Map<String, String> cbParams = hfPaymentCallBackService.getRespParams(params);

		// 打印、保存接口回调参数
		hfPaymentCallBackService.printCallBackData("商户转账异步回调", cbParams, ServiceType.TRANSFER, HfPayType.TRANSFER);

		// 业务调用
		result = hfPaymentCallBackService.merchantTransfer(cbParams);

		// 回调业务成功执行,打印第三方需要的成功标示
		if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
			renderText(HfConsts.RESP_PREFIX + cbParams.get(HfConsts.ORDID));
		} else {
			LoggerUtil.info(true, result.msg);
		}
	}

	/**
	 * 商户转账异步回调(投标奖励、加息卷)
	 */
	public static void sendRateAyns() {
		ResultInfo result = null;

		// 获取参数
		Map<String, String> cbParams = hfPaymentCallBackService.getRespParams(params);

		// 打印、保存接口回调参数
		hfPaymentCallBackService.printCallBackData("商户转账异步回调", cbParams, ServiceType.TRANSFER, HfPayType.TRANSFER);

		// 业务调用
		result = hfPaymentCallBackService.sendRate(cbParams);

		// 回调业务成功执行,打印第三方需要的成功标示
		if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
			renderText(HfConsts.RESP_PREFIX + cbParams.get(HfConsts.ORDID));
		} else {
			LoggerUtil.info(true, result.msg);
		}
	}

	/**
	 * 投标撤销异步通知
	 */
	public static void TenderCancleAyns() {
		// 获取参数
		Map<String, String> cbParams = hfPaymentCallBackService.getRespParams(params);

		// 打印、保存接口回调参数
		hfPaymentCallBackService.printCallBackData("投标撤销", cbParams, ServiceType.TENDER_CANCLE, HfPayType.TENDERCANCLE);
	}

	/**
	 * 单笔还款异步通知
	 */
	public static void repaymentAyns() {

		ResultInfo result = null;
		// 获取参数
		Map<String, String> resultMap = hfPaymentCallBackService.getRespParams(params);

		hfPaymentCallBackService.printCallBackData("正常还款响应", resultMap, ServiceType.REPAYMENT, HfPayType.REPAYMENT);

		result = hfPaymentCallBackService.repayment(resultMap);

		if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
			renderText(HfConsts.RESP_PREFIX + resultMap.get(HfConsts.ORDID));
		} else {
			// 打印日志，事务回滚
			LoggerUtil.info(true, result.msg);
		}
	}

	/**
	 * 托管开户同步
	 */
	public static void userRegisterByBosSyn() {
		ResultInfo result = null;

		// 获取回调参数
		Map<String, String> cbParams = hfPaymentCallBackService.getRespParams(params);

		// 打印、保存接口回调参数
		hfPaymentCallBackService.printCallBackData("开户同步回调", cbParams, ServiceType.REGIST, HfPayType.USERREGISTER);

		Map<String, String> remarkParams = hfPaymentCallBackService.queryRequestParams(cbParams.get("MerPriv"));
		if (remarkParams == null) {
			flash.error("查询托管请求备注参数失败");

			redirect("front.account.MySecurityCtrl.securityPre");
		}

		result = hfPaymentCallBackService.userRegisterByBos(cbParams, remarkParams);

		Client client = Client.getEnum(Convert.strToInt(remarkParams.get(RemarkPramKey.CLIENT), Client.PC.code));

		switch (client) {
		case PC: {
			if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
				flash.success("资金托管开户成功");
			} else {
				LoggerUtil.info(true, result.msg);
				flash.error(result.msg);
			}
			redirect("front.account.MySecurityCtrl.securityPre");
			break;
		}
		case ANDROID:
		case IOS: {
			if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
				result.code = 1;
				result.msg = "资金托管开户成功";
			} else {
				LoggerUtil.info(true, result.msg);
			}
			redirectApp(result);
			break;
		}
		case WECHAT: {
			if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
				redirect("wechat.WechatBaseController.toResultPage", WxPageType.PAGE_COMMUNAL_SUCC, "资金托管开户成功");
			} else {
				LoggerUtil.info(true, result.msg);
				redirect("wechat.WechatBaseController.toResultPage", WxPageType.PAGE_COMMUNAL_FAIL, result.msg);
			}

			break;
		}
		default:
			break;
		}

	}

	/**
	 * 托管开户异步
	 */
	public static void userRegisterByBosAyns() {
		ResultInfo result = null;

		// 获取回调参数
		Map<String, String> cbParams = hfPaymentCallBackService.getRespParams(params);

		// 打印、保存接口回调参数
		hfPaymentCallBackService.printCallBackData("开户异步回调", cbParams, ServiceType.REGIST, HfPayType.USERREGISTER);

		Map<String, String> remarkParams = hfPaymentCallBackService.queryRequestParams(cbParams.get("MerPriv"));
		if (remarkParams == null) {
			LoggerUtil.info(true, "查询托管请求备注参数失败");

			return;
		}

		result = hfPaymentCallBackService.userRegisterByBos(cbParams, remarkParams);
		// 回调业务成功执行,打印第三方需要的成功标示
		if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
			renderText(HfConsts.RESP_PREFIX + cbParams.get(HfConsts.TRXID));
		} else {
			// 打印日志，事务回滚
			LoggerUtil.info(true, result.msg);
		}
	}

	/**
	 * 托管激活异步
	 */
	public static void bosAcctActivateAyns() {
		ResultInfo result = null;

		// 获取回调参数
		Map<String, String> cbParams = hfPaymentCallBackService.getRespParams(params);

		// 打印保存接口回调参数
		hfPaymentCallBackService.printCallBackData("账户激活异步", cbParams, ServiceType.BOSACCTACTIVATE,
				HfPayType.BOSACCTACTIVATE);

		/** 获取请求 */
		t_payment_request request = hfPaymentCallBackService.queryRequestByOrderNo(cbParams.get("OrdId"));

		Map<String, String> remarkParams = new Gson().fromJson(request.req_params,
				new TypeToken<LinkedHashMap<String, String>>() {
				}.getType());

		if (remarkParams == null) {
			LoggerUtil.info(true, "查询托管请求备注参数失败");
			return;
		}

		result = hfPaymentCallBackService.bosAcctActivate(cbParams, remarkParams);

		// 回调业务成功执行,打印第三方需要的成功标示
		if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
			renderText(HfConsts.RESP_PREFIX + cbParams.get(HfConsts.ORDID));
		} else {
			// 打印日志，事务回滚
			LoggerUtil.info(true, result.msg);
		}
	}

	/**
	 * 托管激活同步
	 */
	public static void bosAcctActivateSyn() {
		ResultInfo result = null;

		// 获取回调参数
		Map<String, String> cbParams = hfPaymentCallBackService.getRespParams(params);

		// 打印保存接口回调参数
		hfPaymentCallBackService.printCallBackData("账户激活异步", cbParams, ServiceType.BOSACCTACTIVATE,
				HfPayType.BOSACCTACTIVATE);

		/** 获取请求 */
		t_payment_request request = hfPaymentCallBackService.queryRequestByOrderNo(cbParams.get("OrdId"));

		Map<String, String> remarkParams = new Gson().fromJson(request.req_params,
				new TypeToken<LinkedHashMap<String, String>>() {
				}.getType());

		if (remarkParams == null) {
			flash.error("查询托管请求备注参数失败");
			redirect("front.account.MySecurityCtrl.securityPre");
		}

		result = hfPaymentCallBackService.bosAcctActivate(cbParams, remarkParams);

		Client client = Client.getEnum(Convert.strToInt(remarkParams.get(RemarkPramKey.CLIENT), Client.PC.code));

		switch (client) {
		case PC: {
			if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
				flash.success("账户激活成功");
			} else {
				LoggerUtil.info(true, result.msg);
				flash.error(result.msg);
			}
			redirect("front.account.MySecurityCtrl.securityPre");
			break;
		}
		case ANDROID:
		case IOS: {
			if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
				result.code = 1;
				result.msg = "账户激活成功";
			} else {
				LoggerUtil.info(true, result.msg);
			}
			redirectApp(result);
			break;
		}
		case WECHAT: {
			if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
				redirect("wechat.WechatBaseController.toResultPage", WxPageType.PAGE_COMMUNAL_SUCC, "账户激活成功");
			} else {
				LoggerUtil.info(true, result.msg);
				redirect("wechat.WechatBaseController.toResultPage", WxPageType.PAGE_COMMUNAL_FAIL, result.msg);
			}

			break;
		}
		default:
			break;
		}
	}

	/**
	 * 托管充值同步
	 */
	public static void directRechargeSyn() {
		ResultInfo result = null;

		// 获取参数
		Map<String, String> cbParams = hfPaymentCallBackService.getRespParams(params);

		// 打印、保存接口回调参数
		hfPaymentCallBackService.printCallBackData("托管充值同步回调", cbParams, ServiceType.RECHARGE,
				HfPayType.DIRECTRECHARGE);

		/** 获取请求 */
		t_payment_request request = hfPaymentCallBackService.queryRequestByOrderNo(cbParams.get("OrdId"));

		Map<String, String> remarkParams = new Gson().fromJson(request.req_params,
				new TypeToken<LinkedHashMap<String, String>>() {
				}.getType());

		if (remarkParams == null) {
			flash.error("查询托管请求备注参数失败");
			redirect("front.account.MyAccountCtrl.rechargePre");
		}

		// 业务调用
		result = hfPaymentCallBackService.directRecharge(cbParams, remarkParams);

		Client client = Client.getEnum(Convert.strToInt(remarkParams.get(RemarkPramKey.CLIENT), Client.PC.code));

		switch (client) {
		case PC: {
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
		case ANDROID:
		case IOS: {
			if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
				result.code = 1;
				result.msg = "充值成功";
			} else {
				LoggerUtil.info(true, result.msg);
			}
			redirectApp(result);
			break;
		}
		case WECHAT: {
			if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
				result.msg = "充值成功";
				redirect("wechat.WechatBaseController.toResultPage", WxPageType.PAGE_COMMUNAL_SUCC, result.msg);
			} else {
				LoggerUtil.info(true, result.msg);
				redirect("wechat.WechatBaseController.toResultPage", WxPageType.PAGE_COMMUNAL_FAIL, result.msg);
			}
			break;
		}

		default:
			break;
		}
	}

	/**
	 * 托管充值异步
	 */
	public static void directRechargeAyns() {
		ResultInfo result = null;

		// 获取参数
		Map<String, String> cbParams = hfPaymentCallBackService.getRespParams(params);

		// 打印、保存接口回调参数
		hfPaymentCallBackService.printCallBackData("托管充值异步回调", cbParams, ServiceType.RECHARGE,
				HfPayType.DIRECTRECHARGE);

		/** 获取请求 */
		t_payment_request request = hfPaymentCallBackService.queryRequestByOrderNo(cbParams.get("OrdId"));

		Map<String, String> remarkParams = new Gson().fromJson(request.req_params,
				new TypeToken<LinkedHashMap<String, String>>() {
				}.getType());

		if (remarkParams == null) {
			LoggerUtil.info(true, "查询托管请求备注参数失败");
			return;
		}

		// 业务调用
		result = hfPaymentCallBackService.directRecharge(cbParams, remarkParams);

		// 回调业务成功执行,打印第三方需要的成功标示
		if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {

			renderText(HfConsts.RESP_PREFIX + cbParams.get(HfConsts.TRXID));
		} else {
			LoggerUtil.info(true, result.msg);
		}
	}

	public static void quickBindingAyns() {
		ResultInfo result = null;

		// 获取参数
		Map<String, String> cbParams = hfPaymentCallBackService.getRespParams(params);

		// 打印、保存接口回调参数
		hfPaymentCallBackService.printCallBackData("绑卡异步回调", cbParams, ServiceType.QUICKBINDING,
				HfPayType.QUICKBINDING);

		Map<String, String> remarkParams = hfPaymentCallBackService.queryRequestParams(cbParams.get("MerPriv"));

		// 业务调用
		result = hfPaymentCallBackService.quickBinding(cbParams, remarkParams);

		if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
			renderText(HfConsts.RESP_PREFIX + cbParams.get(HfConsts.TRXID));
		} else {
			LoggerUtil.info(true, result.msg);
		}
	}

	public static void autoTenderCancleAyns() {
		ResultInfo result = null;

		// 获取参数
		Map<String, String> cbParams = hfPaymentCallBackService.getRespParams(params);

		// 打印、保存接口回调参数
		hfPaymentCallBackService.printCallBackData("绑卡异步回调", cbParams, ServiceType.QUICKBINDING,
				HfPayType.QUICKBINDING);

		Map<String, String> remarkParams = hfPaymentCallBackService.queryRequestParams(cbParams.get("MerPriv"));

		if (remarkParams == null) {
			LoggerUtil.info(true, "查询托管请求备注参数失败");

			return;
		}

		result = hfPaymentCallBackService.autoTenderCancle(cbParams, remarkParams);

		if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
			renderText(HfConsts.RESP_PREFIX + cbParams.get(HfConsts.TRXID));
		} else {
			LoggerUtil.info(true, result.msg);
		}
	}

	public static void transferAyns() {
		// 获取参数
		Map<String, String> cbParams = hfPaymentCallBackService.getRespParams(params);

		// 不做任何业务,直接打印第三方需要的成功标示
		renderText(HfConsts.RESP_PREFIX + cbParams.get(HfConsts.ORDID));
	}

	public static void corpRegisterAyns() {
		ResultInfo result;

		// 获取参数
		Map<String, String> cbParams = hfPaymentCallBackService.getRespParams(params);

		// 打印、保存接口回调参数
		hfPaymentCallBackService.printCallBackData("企业开户异步回调", cbParams, ServiceType.CORPREGISTER,
				HfPayType.CORPREGISTER);

		Map<String, String> remarkParams = hfPaymentCallBackService.queryRequestParams(cbParams.get("MerPriv"));

		// 业务调用
		result = hfPaymentCallBackService.corpRegister(cbParams, remarkParams);

		if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
			renderText(HfConsts.RESP_PREFIX + cbParams.get(HfConsts.TRXID));
		} else {
			// 打印日志，事务回滚
			LoggerUtil.info(true, result.msg);
		}

	}

}