package controllers.front.account;

import java.io.File;
import java.sql.Struct;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import models.common.bean.CurrUser;
import models.common.bean.SignInUserRecord;
import models.common.bean.UserFundInfo;
import models.common.bean.UserMessage;
import models.common.entity.t_bank_card_user;
import models.common.entity.t_notice_setting_user;
import models.common.entity.t_score_user;
import models.common.entity.t_sign_in_record;
import models.common.entity.t_sign_in_rule;
import models.common.entity.t_user_fund;
import models.core.bean.InvestReceive;
import payment.impl.PaymentProxy;
import play.cache.Cache;
import play.db.jpa.JPA;
import play.db.jpa.JPAPlugin;
import play.mvc.With;
import play.mvc.Scope.Session;
import services.PaymentService;
import services.common.BankCardUserService;
import services.common.CreditLevelService;
import services.common.NoticeService;
import services.common.ScoreUserService;
import services.common.SignInRecordService;
import services.common.SignInRuleService;
import services.common.UserFundService;
import services.common.UserInfoService;
import services.common.UserService;
import services.common.WithdrawalUserService;
import services.core.BillInvestService;
import services.core.InvestService;

import com.shove.Convert;

import common.FeeCalculateUtil;
import common.annotation.PaymentAccountCheck;
import common.annotation.RealNameCheck;
import common.annotation.SimulatedCheck;
import common.annotation.SubmitCheck;
import common.annotation.SubmitOnly;
import common.constants.CacheKey;
import common.constants.ConfConst;
import common.constants.Constants;
import common.constants.PaymentConst;
import common.constants.SettingKey;
import common.constants.ext.SignInKey;
import common.enums.Bank;
import common.enums.BusiType;
import common.enums.CashType;
import common.enums.Client;
import common.enums.NoticeScene;
import common.enums.NoticeType;
import common.enums.ServiceType;
import common.enums.TradeType;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.OrderNoUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.StrUtil;
import common.utils.file.FileUtil;
import controllers.common.FrontBaseController;
import controllers.common.SubmitRepeat;
import controllers.common.interceptor.AccountInterceptor;
import controllers.common.interceptor.SimulatedInterceptor;
import hf.HfPaymentService;

/**
 * 前台-账户中心-账户首页控制器
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2015年12月17日
 */
@With({AccountInterceptor.class, SubmitRepeat.class,SimulatedInterceptor.class})
public class MyAccountCtrl extends FrontBaseController {
	
	protected static NoticeService noticeService = Factory.getService(NoticeService.class);
	
	protected static UserService userService = Factory.getService(UserService.class);
	
	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	
	protected static UserFundService userFundService = Factory.getService(UserFundService.class);
	
	protected static BankCardUserService bankCardUserService = Factory.getService(BankCardUserService.class);
	
	protected static CreditLevelService creditLevelService = Factory.getService(CreditLevelService.class);
	
	protected static BillInvestService billInvestService = Factory.getService(BillInvestService.class);
	
	protected static WithdrawalUserService withdrawalUserService = Factory.getService(WithdrawalUserService.class);
	
	protected static SignInRecordService signInRecordService = Factory.getService(SignInRecordService.class);
	
	protected static SignInRuleService signInRuleService = Factory.getService(SignInRuleService.class);
	
	protected static ScoreUserService scoreUserService = Factory.getService(ScoreUserService.class);

	protected static InvestService investService = Factory.getService(InvestService.class);
	
	private static PaymentService paymentService = Factory.getSimpleService(PaymentService.class);

	/**
	 * 我的财富-账户首页
	 * @description 
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月25日
	 */
	public static void homePre(){
		/* 账户首页页面标记 */
		boolean isHome = true;
		
		render(isHome);
	}
	
	/**
	 * 我的财富-账户首页-回款计划
	 * @param currPage
	 * @param pageSize
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月21日
	 *
	 */
	public static void listOfNoReceiveBillPre(int currPage,int pageSize){
		if (pageSize < 1) {
			pageSize = 5;
		}
		long userId = getCurrUser().id;
		PageBean<InvestReceive> investReceive = billInvestService.pageOfInvestReceive(currPage, pageSize, userId);
		
		render(investReceive);
	}
	
	/**
	 * 我的财富-头部资产信息
	 * 
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月20日
	 *
	 */
	public static void userHeadFundInfo(){
		long userId = getCurrUser().id;
		UserFundInfo userFundInfo = userService.findUserFundInfo(userId);
		
		double totalInterest = investService.calculateTotalInterest(userId);
		
		userFundInfo.total_assets += totalInterest;

		render(userFundInfo);
	}
	
	/**
	 * 进入消息页面
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月8日
	 */
	public static void showNoticePre() {
		render();
	}
	
	/**
	 * 前台-账户中心-账户首页-消息-消息
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月18日
	 */
	public static void listOfNoticsPre(int currPage,int pageSize) {
		long userId = getCurrUser().id;
		
		PageBean<UserMessage> page = noticeService.pageOfAllUserMessages(currPage,pageSize,userId);
		
		int unreadMsgCount = noticeService.countUnreadMsg(userId);//未读消息总数
		render(page, unreadMsgCount);
	}
	
	/**
	 * 标记用户的某个站内信为已读
	 *
	 * @param msgId
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月25日
	 */
	public static void editUserMsgStatus(String sign){
		ResultInfo result = Security.decodeSign(sign, Constants.MSG_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			error();
		}
		
		long msgId = Long.parseLong(result.obj.toString());
		long userId = getCurrUser().id;
		
		boolean isUpdated = noticeService.updateUserMsgStatus(userId, msgId);
		if (!isUpdated) {
			result.code = -1;
			result.msg = "没有更新成功!";
			
			renderJSON(result);
		}
		
		result.code = 1;
		result.msg = "该条信息已读状态已更新!";
		
		renderJSON(result);
	}
	
	/**
	 * 删除某条站内信
	 *
	 * @param sign
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月18日
	 */
	@SimulatedCheck
	public static void delUserMsg(String sign) {
		ResultInfo result = Security.decodeSign(sign, Constants.MSG_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			error();
		}
		
		long msgId = Long.parseLong(result.obj.toString());
		long userId = getCurrUser().id;
		
		result = noticeService.deleteUserMsg(userId, msgId);
		if (result.code < 1) {
			LoggerUtil.error(true, "消息删除失败:【%s】", msgId+"");
			
			renderJSON(result);
		}
		
		renderJSON(result);
	}
	
	
	/**
	 * 前台-账户中心-账户首页-消息-消息设置
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月18日
	 */
	public static void showUserNoticSettingsPre() {
		
		Long user_id =getCurrUser().id;		
		
		List<t_notice_setting_user> noticSettings = noticeService.queryAllNoticSettingsByUser(user_id);
		
		render(noticSettings);
	}
	
	/**
	 * 前台-账户中心-账户首页-消息-消息设置-更改用户的消息接收设置
	 *
	 * @param sceneCode
	 * @param type
	 * @param flag
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月18日
	 */
	public static void editNoticeSetting(String sceneCode, String noticeType, boolean noticeflag) {
		ResultInfo result = new ResultInfo();
		NoticeScene scene =  NoticeScene.getEnum(Convert.strToInt(sceneCode, -1));
		if (scene == null) {
			result.code = -1;
			result.msg = "你输入的含有非法的参数";

			renderJSON(result);
		}
		
		NoticeType typeOfNotice = NoticeType.getEnum(Convert.strToInt(noticeType, -1));
		if (typeOfNotice == null) {
			result.code = -1;
			result.msg = "你输入的含有非法的参数";

			renderJSON(result);
		}
		
		//获取当前登录用户的id
		Long user_id =getCurrUser().id;		
		
		boolean flagOf = noticeService.saveOrUpdateUserNoticeSetting(user_id, scene, typeOfNotice, noticeflag);
		if ( !flagOf ) {
			result.code = -2;
			result.msg = "设置失败，请稍后再试";
			
			renderJSON(result);
		} else {
			result.code = 1;
			result.msg = scene.value+"设置成功!";
			
			renderJSON(result);
		}
	}
	
	/**
	 * 前台充值页面
	 * 
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月14日
	 *
	 */
	@PaymentAccountCheck
	@RealNameCheck
	@SubmitCheck
	public static void rechargePre(){
		CurrUser currUser = getCurrUser();
		boolean isHome = false;
		if(!currUser.is_actived) {
			MySecurityCtrl.securityPre();
		}
		// 未绑卡
		/*if (!currUser.is_bank_card) {
			redirect("front.account.MyAccountCtrl.bankCardBindGuidePre");
		}*/
		
		/* 最高充值金额 */
		int rechargeHighest = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.RECHARGE_AMOUNT_MAX), 0);
		
		/* 最低充值金额 */
		int rechargeLowest = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.RECHARGE_AMOUNT_MIN), 0);
		
		t_bank_card_user bankUser = currUser.bankUser;
		
		render(rechargeHighest, rechargeLowest, bankUser,isHome);
	}
	
	/**
	 * 用户充值
	 *
	 * @param userSign 用户签名
	 * @param rechargeAmt
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月8日
	 */
	@PaymentAccountCheck
	@RealNameCheck
	@SimulatedCheck
	@SubmitOnly
	public static void recharge(double rechargeAmt){
		checkAuthenticity();
		
		CurrUser currUser = getCurrUser();
		// 未绑卡
		if (!currUser.is_bank_card) {
			redirect("front.account.MyAccountCtrl.bankCardBindGuidePre");
		}

		/* 用户Id */
		long userId = getCurrUser().id;

		//业务订单号
		String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.RECHARGE);
		
		ResultInfo result = userFundService.recharge(userId, serviceOrderNo, rechargeAmt, "账户直充", Client.PC);
		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
			flash.error(result.msg);
			
			rechargePre();
		}
		
		/* ----------------- 托管添加开始 -----------------*/
		int type = Integer.parseInt(params.get("tradeType"));
		TradeType tradeType = TradeType.getTradeTypeByCode(type);
		if(tradeType == null) {
			flash.error("交易类型错误");
			rechargePre();
		}
		String smsCode = params.get("smsCode");
		String smsSeq = "";
		if(tradeType.code == TradeType.QP.code) {
			String sessionId = Session.current().getId();
			Map<String, Object> map = (Map<String, Object>) Cache.get(CacheKey.SMSCODE_ + sessionId);
			BusiType busiType = (BusiType) map.get("busiType");
			if(busiType.code != BusiType.CHARGE.code) {
				flash.error("短信seq验证错误");
				rechargePre();
			}
			smsSeq = map.get("smsSeq").toString();
		}
		String bankId = params.get("bankId");
		if(tradeType.code == TradeType.B2C.code || tradeType.code == TradeType.B2B.code) {
			Bank bank = Bank.getBank(bankId);
			if(bank == null) {
				flash.error("充值银行选择错误");
				rechargePre();
			}
		}
		/* ----------------- 托管添加结束 -----------------*/
		
		if (ConfConst.IS_TRUST) {
			result = PaymentProxy.getInstance().directRecharge(Client.PC.code, serviceOrderNo, userId, tradeType, bankId, rechargeAmt, smsCode, smsSeq, null);
			// 原充值方法
			// result = PaymentProxy.getInstance().d(Client.PC.code, serviceOrderNo, userId, rechargeAmt);
			if (result.code < 1) {
				LoggerUtil.info(true, result.msg);
				flash.error(result.msg);
				
				rechargePre();
			}
			
			return;
		}
		
		//普通网关模式
		result = userFundService.doRecharge(userId, rechargeAmt, serviceOrderNo);
		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
			flash.error(result.msg);
			
			rechargePre();
		}
		
		flash.error("充值成功");
		homePre();
	}
	
	/**
	 * 前台提现页面
	 * 
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月14日
	 *
	 */
	@PaymentAccountCheck
	@RealNameCheck
	@SubmitCheck
	public static void withdrawPre(){
		CurrUser currUser = getCurrUser();
		
		if(!currUser.is_actived) {
			MySecurityCtrl.securityPre();
		}
		
		// 未绑卡
		/*if (!currUser.is_bank_card) {
			redirect("front.account.MyAccountCtrl.bankCardBindGuidePre");
		}*/
		
		List<t_bank_card_user> bankList = bankCardUserService.queryCardByUserId(currUser.id);
		
		t_user_fund userFundInfo = userFundService.queryUserFundByUserId(currUser.id);
		
		/* 提现手续费起点 */
		String withdrawFeePoint = settingService.findSettingValueByKey(SettingKey.WITHDRAW_FEE_POINT);

		/* 提现手续费率 */
		String withdrawFeeRate = settingService.findSettingValueByKey(SettingKey.WITHDRAW_FEE_RATE);
		
		/* 最低提现手续费 */
		String withdrawFeeMin = settingService.findSettingValueByKey(SettingKey.WITHDRAW_FEE_MIN);
		
		/* 可用余额 */
		double balance = userFundInfo.balance;
		
		double withdrawMaxRate = Constants.WITHDRAW_MAXRATE;
		
		boolean isFYPayment = true;
		if (!ConfConst.CURRENT_TRUST_TYPE.equals(PaymentConst.TRUST_TYPE_FY)) {
			isFYPayment = false;
		}
		boolean isHome=false;
		render(bankList, balance ,withdrawMaxRate , withdrawFeePoint, withdrawFeeRate, withdrawFeeMin, isFYPayment,isHome);
	}
	
	/**
	 * 用户提现
	 *
	 * @param userSign 用户签名
	 * @param withdrawalAmt 提现金额
	 * @param bankAccount 银行卡号
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月9日
	 */
	@SubmitOnly
	@PaymentAccountCheck
	@RealNameCheck
	@SimulatedCheck
	public static void withdrawal(double withdrawalAmt, String bankAccount, String cashType){
		checkAuthenticity();
		
		CurrUser currUser = getCurrUser();
		// 未绑卡
		if (!currUser.is_bank_card) {
			redirect("front.account.MyAccountCtrl.bankCardBindGuidePre");
		}
		
		CashType type = CashType.getCashTypeByCode(cashType);
		
		if(type == null) {
			flash.error("取现类型错误，请重新选择！");
			withdrawPre();
		}
		
		//业务订单号
		String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.WITHDRAW);
		
		ResultInfo result = userFundService.withdrawal(currUser.id, serviceOrderNo, withdrawalAmt, bankAccount, Client.PC);
		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
			flash.error(result.msg);

			withdrawPre();
		}
		
		if (ConfConst.IS_TRUST) {
			result = PaymentProxy.getInstance().withdrawal(Client.PC.code, serviceOrderNo, currUser.id, withdrawalAmt, bankAccount, cashType);
			if (result.code < 1) {
				LoggerUtil.info(true, result.msg);
				flash.error(result.msg);
				withdrawPre();
			}
			
			return;
		}
		
		//提现手续费
		double withdrawalFee = FeeCalculateUtil.getWithdrawalFee(withdrawalAmt);
		
		//普通网关模式
		result = userFundService.doWithdrawal(currUser.id, withdrawalAmt, withdrawalFee, withdrawalFee, serviceOrderNo, false);
		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
			flash.error(result.msg);

			withdrawPre();
		}
		
		flash.error("提现成功");
		
		homePre();
	}

	/**
	 * 上传用户头像
	 * @description 
	 *
	 * @param photoUrl 图片路径
	 * 
	 * @author Chenzhipeng
	 * @createDate 2015年12月25日
	 */
	@SimulatedCheck
	public static void updatePhoto(File imgFile, String fileName){
		
		response.contentType="text/html";
		ResultInfo result = new ResultInfo();
		if (imgFile == null) {
			result.code = -1;
			result.msg = "请选择要上传的图片";
			
			renderJSON(result);
		}
		if(StringUtils.isBlank(fileName) || fileName.length() > 32){
			result.code = -1;
			result.msg = "图片名称长度应该位于1~32位之间";
			
			renderJSON(result);
		}
		
		result = FileUtil.uploadImgags(imgFile);
		if (result.code < 0) {

			renderJSON(result);
		}
		
		Map<String, Object> fileInfo = (Map<String, Object>) result.obj;
		fileInfo.put("imgName", fileName);
		String filename = (String) fileInfo.get("staticFileName");
		
		long userId = getCurrUser().id;
		
		result = userService.updateUserPhoto(userId, filename);
		if (result.code < 1) {
			
			renderJSON(result);
		}
		
		userService.flushUserCache(userId);
		result.obj = filename;
		
		renderJSON(result);
	}

	/**
	 * 统计用户的未读消息数
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月27日
	 */
	public static void getNoticeNum(){
		CurrUser user = getCurrUser();
		int num = noticeService.countUserUnreadMSGs(user.id);
		
		ResultInfo result = new ResultInfo();
		result.code = 1;
		result.msg = "查询成功";
		result.obj = num;
		
		renderJSON(result);
	}
	
	/**
	 * 资金托管开户引导页
	 * @author Chenzhipeng
	 * @createDate 2016年1月30日
	 *
	 */
	public static void paymentAccountGuidePre(){
		
		render();
	}
	
	/**
	 * 模拟登录不能执行数据库信息修改操作
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年5月31日
	 */
	public static void simulatedLoginPre(){
		
		render();
	}
	
	/**
	 * 银行卡绑定引导页
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月30日
	 *
	 */
	public static void bankCardBindGuidePre(){
		
		render();
	}
	
	/**
	 * 实名认证引导页
	 * @author Chenzhipeng
	 * @createDate 2016年1月30日
	 *
	 */
	public static void realNameGuidePre(){
		
		render();
	}
	
	
	
	
	/**
	 * 将站内信标记为已读
	 *
	 * @author songjia
	 * @createDate 2016年2月18日
	 */
	public static void markMsgAsRead() {
		ResultInfo result = new ResultInfo();
		
		long userId = getCurrUser().id;
		int row = noticeService.updateUserAllMsgStatus(userId);
		if(row == ResultInfo.ERROR_SQL) {
			result.code = ResultInfo.ERROR_SQL;
			result.msg = "标记站内信为已读不成功!";
			
			renderJSON(result);
		}
		result.code = 1;
		result.msg = "信息已全部标记为已读!";
		
		renderJSON(result);
	}
	
	/**
	 * 前台快速充值页面
	 * 
	 *
	 * @author YanPengFei
	 * @createDate 2016年8月25日
	 *
	 */
	@PaymentAccountCheck
	@RealNameCheck
	@SubmitCheck
	public static void fastRechargePre(){
		CurrUser currUser = getCurrUser();
		// 未绑卡
		if (!currUser.is_bank_card) {
			redirect("front.account.MyAccountCtrl.bankCardBindGuidePre");
		}
		
		/* 最高充值金额 */
		int rechargeHighest = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.RECHARGE_AMOUNT_MAX), 0);
		
		/* 最低充值金额 */
		int rechargeLowest = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.RECHARGE_AMOUNT_MIN), 0);
		
		render(rechargeHighest, rechargeLowest);
	}
	
	/**
	 * 用户快速充值
	 *
	 * @param userSign 用户签名
	 * @param rechargeAmt
	 *
	 * @author YanPengFei
	 * @createDate 2016年8月25日
	 */
	@PaymentAccountCheck
	@RealNameCheck
	@SimulatedCheck
	@SubmitOnly
	public static void fastRecharge(double rechargeAmt) {
		checkAuthenticity();
		
		CurrUser currUser = getCurrUser();
		// 未绑卡
		if (!currUser.is_bank_card) {
			redirect("front.account.MyAccountCtrl.bankCardBindGuidePre");
		}

		/* 用户Id */
		long userId = getCurrUser().id;

		//业务订单号
		String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.RECHARGE);
		
		ResultInfo result = userFundService.recharge(userId, serviceOrderNo, rechargeAmt, "快速充值", Client.PC);
		
		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
			flash.error(result.msg);
			
			rechargePre();
		}
		
		if (ConfConst.IS_TRUST) {
			result = PaymentProxy.getInstance().fastRecharge(Client.PC.code, serviceOrderNo, userId, rechargeAmt);
			
			if (result.code < 1) {
				LoggerUtil.info(true, result.msg);
				flash.error(result.msg);
				
				rechargePre();
			}
			
			return;
		}
		
		//普通网关模式
		result = userFundService.doRecharge(userId, rechargeAmt, serviceOrderNo);
		
		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
			flash.error(result.msg);
			
			rechargePre();
		}
		
		flash.error("充值成功");
		homePre();
	}
	
	/**
	 * 签到
	 */
	public static void signIn() {
		ResultInfo result = new ResultInfo();
		long userId = getCurrUser().id;
		String currentDate = DateUtil.dateToString(new Date(), Constants.DATE_PLUGIN6);
		List<SignInUserRecord> signInUserRecord = signInRecordService.listOfSignInUserRecord(userId, currentDate);
		
		//先查询是否已有当天的签到记录，若已经签到则不能再进行签到
		t_sign_in_record record = signInRecordService.querySignInToday(userId);
		if (record != null) {
			int number = record.number; //连续签到次数
			double extraScore = record.extra_score; //额外加成积分
			
			render(signInUserRecord, number, extraScore);
		}
		
		Date signInDate = DateUtil.dateToDate(new Date(), Constants.DATE_PLUGIN5);
		int number = 1;
		String key = null;
		t_sign_in_rule rule = null;
		
		//获取所有的签到规则
		Map<String, t_sign_in_rule> rulesMap = signInRuleService.findAllRulesMap();
		if (rulesMap == null || rulesMap.size() <= 0) {
			JPA.setRollbackOnly();
			flash.error("签到失败，请刷新页面重新签到");
			render(signInUserRecord);
		}
		
		//在没有当天签到记录的前提下，继续查询是否有昨天的签到记录
		record = signInRecordService.querySignInYesterday(userId);
		
		//若没有昨天的签到记录，则本次签到为连续第1天签到，否则就是连续第number+1(number+1<=7)天签到
		if (record == null) {
			//根据签到次数匹配签到规则的key
			key = matchSignInKey(number);
			
			if (rulesMap.containsKey(key)) {
				rule = rulesMap.get(key);
			} else {
				JPA.setRollbackOnly();
				flash.error("签到失败，请刷新页面重新签到");
				render(signInUserRecord);
			}
			
			if (rule == null) {
				JPA.setRollbackOnly();
				flash.error("签到失败，请刷新页面重新签到");
				render(signInUserRecord);
			}
			
			//添加签到记录
			result = signInRecordService.addSignInRecord(userId, signInDate, number, rule.score, rule.extra_score);
			if (result.code < 1) {
				JPA.setRollbackOnly();
				flash.error(result.msg);
				render(signInUserRecord);
			}
		} else {
			number = record.number;
			if (number < 1) {
				flash.error("签到失败，请刷新页面重新签到");
				render(signInUserRecord);
			}
			
			//在昨天连续签到的基础上+1
			number += 1;
			//若算上本次连续签到超过了7天，则重置为连续签到一天
			if (number > 7) {
				number = 1;
			}
			
			//根据签到次数匹配签到规则的key
			key = matchSignInKey(number);
			
			if (rulesMap.containsKey(key)) {
				rule = rulesMap.get(key);
			} else {
				JPA.setRollbackOnly();
				flash.error("签到失败，请刷新页面重新签到");
				render(signInUserRecord);
			}
			
			if (rule == null) {
				JPA.setRollbackOnly();
				flash.error("签到失败，请刷新页面重新签到");
				render(signInUserRecord);
			}
			
			//添加签到记录
			result = signInRecordService.addSignInRecord(userId, signInDate, number, rule.score, rule.extra_score);
			if (result.code < 1) {
				JPA.setRollbackOnly();
				flash.error(result.msg);
				render(signInUserRecord);
			}
		}
		
		//签到赠送的积分为：正常赠送积分+额外加成积分
		double score = rule.score + rule.extra_score;
		
		//添加签到记录成功后，进行积分的奖励
		int rows = userFundService.updateUserScoreBalanceAdd(userId, score);
		if (rows <= 0) {
			JPA.setRollbackOnly();
			flash.error("签到失败，请刷新页面重新签到");
			render(signInUserRecord);
		}
		
		//及时查询用户的可用积分
		double scoreBalance = userFundService.findUserScoreBalance(userId);
		
		/** 添加用户积分记录 */
		Map<String, String> summaryPrams = new HashMap<String, String>();
		summaryPrams.put("day", number + "");
		summaryPrams.put("score", (int) rule.score + "");
		summaryPrams.put("extra_score", (int) rule.extra_score + "");
		boolean addDeal = scoreUserService.addScoreUserInfo(
				userId, 
				score, 
				scoreBalance, 
				t_score_user.OperationType.SIGNIN, 
				summaryPrams);

		if (!addDeal) {
			JPA.setRollbackOnly();
			flash.error("添加积分记录失败");
			render(signInUserRecord);
		}
		
		//将上述签到的操作提交
		JPAPlugin.closeTx(false);
		JPAPlugin.startTx(false);
		
		//查询最新的签到记录
		signInUserRecord = signInRecordService.listOfSignInUserRecord(userId, currentDate);
		
		renderArgs.put("number", number); //连续签到天数
		renderArgs.put("extraScore", rule.extra_score); //额外加成积分
		
		userService.flushUserCache(userId);
		
		render(signInUserRecord);
	}
	
	/**
	 * 根据签到次数匹配签到规则的key
	 * 
	 * @param number
	 * @return
	 */
	private static String matchSignInKey(int number) {
		if (number < 1 || number > 7) {
			
			return null;
		}
		
		String key = null;
		
		switch (number) {
			case 1 :
				key = SignInKey.ONE;
				break;
			case 2 :
				key = SignInKey.TWO;
				break;
			case 3 :
				key = SignInKey.THREE;
				break;
			case 4 :
				key = SignInKey.FOUR;
				break;
			case 5 :
				key = SignInKey.FIVE;
				break;
			case 6 :
				key = SignInKey.SIX;
				break;
			case 7 :
				key = SignInKey.SEVEN;
				break;
			default :
				break;
		}
		
		return key;
	}

	public static void getServFee(double withdrawalAmt, String cashType) {
		ResultInfo result = new ResultInfo();
		
		if(CashType.getCashTypeByCode(cashType) == null) {
			result.code = -1;
			result.msg = "取现类型错误";
			renderJSON(result);
		}
		
		double servFee = paymentService.queryServFee(withdrawalAmt, cashType);
		
		result.code = 1;
		result.msg = "成功";
		result.obj = servFee;
		
		renderJSON(result);
	}
	
}