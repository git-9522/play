package controllers.front;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import models.common.bean.CurrUser;
import models.common.entity.t_information;
import models.common.entity.t_score_user;
import models.common.entity.t_send_code;
import models.common.entity.t_template_notice;
import models.common.entity.t_user;

import org.apache.commons.lang.StringUtils;

import play.cache.Cache;
import play.db.jpa.JPA;
import play.libs.Codec;
import play.mvc.Scope.Session;
import services.common.AdvertisementService;
import services.common.BankCardUserService;
import services.common.CreditLevelService;
import services.common.DealUserService;
import services.common.InformationService;
import services.common.NoticeService;
import services.common.ScoreUserService;
import services.common.SendCodeRecordsService;
import services.common.UserFundService;
import services.common.UserInfoService;
import services.common.UserService;
import services.core.InvestService;

import com.shove.Convert;
import com.shove.security.Encrypt;
import common.constants.CacheKey;
import common.constants.ConfConst;
import common.constants.Constants;
import common.constants.ModuleConst;
import common.constants.SettingKey;
import common.enums.Client;
import common.enums.NoticeScene;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.ResultInfo;
import common.utils.SMSUtil;
import common.utils.Security;
import common.utils.StrUtil;
import common.utils.captcha.CaptchaUtil;

import controllers.common.FrontBaseController;
import controllers.front.account.MyAccountCtrl;

/**
 * 前台-登陆注册控制器
 *
 * @description 包含退出、忘记密码
 *
 * @author ChenZhipeng
 * @createDate 2015年12月16日
 */
public class LoginAndRegisteCtrl extends FrontBaseController {

	protected static DealUserService dealUserService = Factory.getService(DealUserService.class);

	protected static BankCardUserService bankCardUserService = Factory.getService(BankCardUserService.class);

	protected static CreditLevelService creditLevelService = Factory.getService(CreditLevelService.class);

	protected static InvestService investService = Factory.getService(InvestService.class);

	protected static UserService userService = Factory.getService(UserService.class);

	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);

	/** 广告图片 */
	protected static AdvertisementService advertisementService = Factory.getService(AdvertisementService.class);

	protected static InformationService informationService = Factory.getService(InformationService.class);

	protected static NoticeService noticeService = Factory.getService(NoticeService.class);

	protected static SendCodeRecordsService sendCodeRecordsService = Factory.getService(SendCodeRecordsService.class);

	protected static UserFundService userFundService = Factory.getService(UserFundService.class);

	protected static ScoreUserService scoreUserService = Factory.getService(ScoreUserService.class);

	/**
	 * 注册页面
	 * 
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月9日
	 *
	 */
	public static void registerPre() {
		/* 验证码 */
		String randomId = Codec.UUID();

		/* 禁止该页面进行高速缓存 */
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Expires", "0");

		/* 获取平台协议标题 */
		t_information platformRegister = informationService.findRegisterDeal();

		/* cps 邀请码 推广 */
		String recommendCode = Encrypt.decrypt3DES(params.get("recommendCode"), ConfConst.ENCRYPTION_KEY_DES);

		render(randomId, platformRegister, recommendCode);
	}

	/**
	 * 用户注册
	 * 
	 * @description
	 * 
	 * @param mobile
	 *            手机号码
	 * @param password
	 *            密码
	 * @param confirmPassword
	 *            确认密码
	 * @param randomId
	 *            验证码密文
	 * @param code
	 *            验证码
	 * @param smsCode
	 *            短信验证码
	 * @param recommendCode
	 *            邀请码
	 * @param scene
	 *            场景
	 * @param readandagree
	 *            协议勾选
	 * 
	 * @author Chenzhipeng
	 * @createDate 2015年12月16日
	 */
	public static void registering(String mobile, String password, String randomId, String code, String smsCode,
			String recommendCode, String scene, boolean readandagree, boolean isValideConfirmPw,
			String confirmPassword) {
		ResultInfo result = new ResultInfo();

		/* 回显数据 */
		flash.put("mobile", mobile);
		flash.put("code", code);
		flash.put("smsCode", smsCode);
		flash.put("recommendCode", recommendCode);

		if (StringUtils.isBlank(mobile)) {
			flash.error("请填写手机号");

			registerPre();
		}

		/* 验证手机号是否符合规范 */
		if (!StrUtil.isMobileNum(mobile)) {
			flash.error("手机号不符合规范");

			registerPre();
		}

		/* 判断手机号码是否存在 */
		if (userService.isMobileExists(mobile)) {
			flash.error("手机号码已存在");

			registerPre();
		}

		if (StringUtils.isBlank(code)) {
			flash.error("请输入验证码");

			registerPre();
		}

		/* 短信验证码验证 */
		if (StringUtils.isBlank(smsCode)) {
			flash.error("请输入短信验证码");

			registerPre();
		}
		Object obj = Cache.get(mobile + scene);
		if (obj == null) {
			flash.error("短信验证码已失效");

			registerPre();
		}
		String codeStr = obj.toString();

		/** 短信验证码 验证 */
		if (ConfConst.IS_CHECK_MSG_CODE) {
			if (!codeStr.equals(smsCode)) {
				flash.error("短信验证码错误");

				registerPre();
			} else {
				// 清除缓存中的短信验证码
				Cache.delete(mobile + scene);
				/* 清除缓存中的验证码 */
				String encryString = Session.current().getId();
				Object cache = Cache.get(mobile + encryString + scene);
				if (cache != null) {
					Cache.delete(mobile + encryString + scene);
					Cache.delete(mobile + scene);
				}
			}
		}

		/* 验证密码是否符合规范 */
		if (!StrUtil.isValidPassword(password)) {
			flash.error("密码不符合规范");

			registerPre();
		}

		if (isValideConfirmPw) {
			/* 验证登录密码与重复密码是否相等 */
			if (!password.equals(confirmPassword)) {
				flash.error("重复密码请和登录密码保持一致");

				registerPre();
			}
		}

		int flagOfRecommendCode = 0;
		/* 验证邀请码 */
		if (StringUtils.isNotBlank(recommendCode)) {

			if (StrUtil.isMobileNum(recommendCode)) {// CPS邀请码是用户的手机号
				if (common.constants.ModuleConst.EXT_CPS) { // CPS模块是否存在
					/* 判断手机号码是否存在 */
					flagOfRecommendCode = userService.isMobileExists(recommendCode) ? 1 : -1;
				}
			} else {
				if (common.constants.ModuleConst.EXT_WEALTHCIRCLE) {// 财富圈邀请码
					service.ext.wealthcircle.WealthCircleService wealthCircleService = Factory
							.getService(service.ext.wealthcircle.WealthCircleService.class);
					ResultInfo result2 = wealthCircleService.isWealthCircleCodeUseful(recommendCode);
					if (result2.code == 1) {
						flagOfRecommendCode = 2;
					}
				}
			}
			if (flagOfRecommendCode < 0) {
				flash.error("推广码不存在");

				registerPre();
			}
		}

		/* 协议是否勾选 */
		if (!readandagree) {
			flash.error("协议未勾选");

			registerPre();
		}

		/* 自动生成用户名 */
		String userName = userService.userNameFactory(mobile);

		/* 密码加密 */
		password = Encrypt.MD5(password + ConfConst.ENCRYPTION_KEY_MD5);
		result = userService.registering(userName, mobile, password, Client.PC, getIp());
		if (result.code < 1) {
			flash.error(result.msg);
			LoggerUtil.info(true, result.msg);

			registerPre();
		} else {
			// ext_redpacket 红包数据生成 start
			/*
			 * if(ModuleConst.EXT_REDPACKET){ t_user user = (t_user) result.obj;
			 * services.ext.redpacket.RedpacketService redpacketService =
			 * Factory.getService(services.ext.redpacket.RedpacketService.class)
			 * ; ResultInfo result2 = redpacketService.creatRedpacket(user.id);
			 * //此处不能使用result if(result2.code < 1){ LoggerUtil.info(true,
			 * "注册成功，生成红包相关数据时出错，数据回滚，%s", result2.msg);
			 * 
			 * flash.error(result2.msg); registerPre(); } }
			 */
			// end

			// experienceBid 体验金发放 start
			if (ModuleConst.EXT_EXPERIENCEBID) {
				t_user user = (t_user) result.obj;
				service.ext.experiencebid.ExperienceBidAccountService experienceBidAccountService = Factory
						.getService(service.ext.experiencebid.ExperienceBidAccountService.class);
				ResultInfo result3 = experienceBidAccountService.createExperienceAccount(user.id); // 此处不能使用result
				if (result3.code < 1) {
					LoggerUtil.info(true, "注册成功，发放体验金到账户时，%s", result3.msg);

					flash.error(result3.msg);
					registerPre();
				}
			}
			// end

			// cps账户开户
			if (ModuleConst.EXT_CPS) {
				t_user user = (t_user) result.obj;
				services.ext.cps.CpsService cpsService = Factory.getService(services.ext.cps.CpsService.class);

				ResultInfo result4 = null;
				if (flagOfRecommendCode == 1) {
					result4 = cpsService.createCps(recommendCode, user.id);
				} else {
					result4 = cpsService.createCps(null, user.id);
				}

				if (result4.code < 1) {
					LoggerUtil.info(true, "注册成功，生成cps推广相关数据时出错，数据回滚，%s", result4.msg);

					flash.error(result4.msg);
					registerPre();
				}
			}
			// end

			// 财富圈账号数据创建
			if (ModuleConst.EXT_WEALTHCIRCLE) {
				t_user user = (t_user) result.obj;
				service.ext.wealthcircle.WealthCircleService wealthCircleService = Factory
						.getService(service.ext.wealthcircle.WealthCircleService.class);
				ResultInfo result5 = null;

				if (flagOfRecommendCode == 2) {
					result5 = wealthCircleService.creatWealthCircle(recommendCode, user.id);
				} else {
					result5 = wealthCircleService.creatWealthCircle(null, user.id);
				}
				if (result5.code < 1) {
					LoggerUtil.info(true, "注册成功，生成财富圈推广相关数据时出错，数据回滚，%s", result5.msg);

					flash.error(result5.msg);
					registerPre();
				}
			}
			// end
			// 积分商城
			if (ModuleConst.EXT_MALL) {

				t_user user = (t_user) result.obj;
				double score = Convert.strToDouble(settingService.findSettingValueByKey(SettingKey.REGISTER_SCORE), 0);

				if (score > 0) {

					// 增加积分
					int rows = userFundService.updateUserScoreBalanceAdd(user.id, score);
					if (rows <= 0) {
						JPA.setRollbackOnly();
						flash.error("注册成功 获得积分");
						LoggerUtil.info(true, "注册成功 获得积分时出错，数据回滚");
						registerPre();
					}

					// 及时查询用户的可用积分
					double scoreBalance = userFundService.findUserScoreBalance(user.id);

					/** 添加用户积分记录 */
					Map<String, String> summaryPrams = new HashMap<String, String>();
					summaryPrams.put("score", (int) score + "");
					boolean addDeal = scoreUserService.addScoreUserInfo(user.id, score, scoreBalance,
							t_score_user.OperationType.REGISTER_SCORE, summaryPrams);

					if (!addDeal) {
						JPA.setRollbackOnly();
						flash.error("添加积分记录失败");
						registerPre();
					}
				}

			}
			t_user user = (t_user) result.obj;
			userService.flushUserCache(user.id);

			MyAccountCtrl.homePre();
			// registerSuccessPre();
		}
	}

	/**
	 * 发送短信验证码（注册）
	 * 
	 * @description
	 * 
	 * @param mobile
	 *            手机号码
	 * @param randomId
	 * @param code
	 * 
	 * @author Chenzhipeng
	 * @createDate 2015年12月22日
	 */
	public static void sendCodeOfRegister(String mobile, String randomId, String code, String scene) {
		ResultInfo result = new ResultInfo();

		if (StringUtils.isBlank(mobile)) {
			result.code = -2;
			result.msg = "手机号不能为空";

			renderJSON(result);
		}

		if (userService.isMobileExists(mobile)) {
			result.code = -2;
			result.msg = "该手机号已被占用";

			renderJSON(result);
		}

		if (StringUtils.isBlank(code)) {
			result.code = -3;
			result.msg = "验证码不能为空";

			renderJSON(result);
		}

		/* 根据randomId取得对应的验证码值 */
		String RandCode = CaptchaUtil.getCode(randomId);

		/* 验证码失效 */
		if (RandCode == null) {
			result.code = -3;
			result.msg = "验证码已失效";

			renderJSON(result);
		} else {
			Cache.delete(randomId);
		}

		/* 验证码错误 */
		if (ConfConst.IS_CHECK_MSG_CODE) {
			if (!code.equalsIgnoreCase(RandCode)) {
				result.code = -3;
				result.msg = "验证码不正确";

				renderJSON(result);
			}
		}

		/* 根据手机号码查询短信发送条数 */
		List<t_send_code> recordByMobile = sendCodeRecordsService.querySendRecordsByMobile(mobile);
		if (recordByMobile.size() >= ConfConst.SEND_SMS_MAX_MOBILE) {
			result.code = -4;
			result.msg = "该手机号码单日短信发送已达上限";

			renderJSON(result);
		}

		/* 根据IP查询短信发送条数 */
		List<t_send_code> recordByIp = sendCodeRecordsService.querySendRecordsByIp(getIp());
		if (recordByIp.size() >= ConfConst.SEND_SMS_MAX_IP) {
			result.code = -4;
			result.msg = "该IP单日短信发送已达上限";

			renderJSON(result);
		}

		/* 将手机号码存入缓存，用于判断60S中内同一手机号不允许重复发送验证码 */
		String encryString = Session.current().getId();
		Object cache = Cache.get(mobile + encryString + scene);
		if (null == cache) {
			Cache.set(mobile + encryString + scene, mobile, Constants.CACHE_TIME_SECOND_60);
		} else {
			String isOldMobile = cache.toString();
			if (isOldMobile.equals(mobile)) {
				result.code = -4;
				result.msg = "短信已发送";

				renderJSON(result);
			}
		}

		/* 查询短信验证码模板 */
		List<t_template_notice> noticeList = noticeService.queryTemplatesByScene(NoticeScene.SECRITY_CODE);

		if (noticeList.size() < 0) {
			result.code = -4;
			result.msg = "短信发送失败";

			renderJSON(result);
		}
		String content = noticeList.get(0).content;
		String smsAccount = settingService.findSettingValueByKey(SettingKey.SERVICE_SMS_ACCOUNT);
		String smsPassword = settingService.findSettingValueByKey(SettingKey.SERVICE_SMS_PASSWORD);

		/* 发送短信验证码 */
		SMSUtil.sendCode(smsAccount, smsPassword, mobile, content, Constants.CACHE_TIME_MINUS_30, scene,
				ConfConst.IS_CHECK_MSG_CODE);

		/* 添加一条短信发送控制记录 */
		sendCodeRecordsService.addSendCodeRecords(mobile, getIp());

		result.code = 1;
		result.msg = "短信验证码发送成功";

		renderJSON(result);
	}

	/**
	 * 发送短信验证码（忘记密码）
	 * 
	 * @description
	 * 
	 * @param mobile
	 *            手机号码
	 * @param randomId
	 * @param code
	 * 
	 * @author Chenzhipeng
	 * @createDate 2015年12月22日
	 */
	public static void sendCodeOfForgetPWD(String mobile, String randomId, String code, String scene) {
		ResultInfo result = new ResultInfo();

		if (StringUtils.isBlank(mobile)) {
			result.code = -2;
			result.msg = "手机号不能为空";

			renderJSON(result);
		}

		if (!userService.isMobileExists(mobile)) {
			result.code = -2;
			result.msg = "手机号未注册";

			renderJSON(result);
		}

		if (StringUtils.isBlank(code)) {
			result.code = -3;
			result.msg = "验证码不能为空";

			renderJSON(result);
		}

		/* 根据randomId取得对应的验证码值 */
		String RandCode = CaptchaUtil.getCode(randomId);

		/* 验证码失效 */
		if (RandCode == null) {
			result.code = -3;
			result.msg = "验证码已失效";

			renderJSON(result);
		} else {
			Cache.delete(randomId);
		}

		/* 验证码错误 */
		if (!code.equalsIgnoreCase(RandCode)) {
			result.code = -3;
			result.msg = "验证码不正确";

			renderJSON(result);
		}

		/* 根据手机号码查询短信发送条数 */
		List<t_send_code> recordByMobile = sendCodeRecordsService.querySendRecordsByMobile(mobile);
		if (recordByMobile.size() >= ConfConst.SEND_SMS_MAX_MOBILE) {
			result.code = -4;
			result.msg = "该手机号码单日短信发送已达上限";

			renderJSON(result);
		}

		/* 根据IP查询短信发送条数 */
		List<t_send_code> recordByIp = sendCodeRecordsService.querySendRecordsByIp(getIp());
		if (recordByIp.size() >= ConfConst.SEND_SMS_MAX_IP) {
			result.code = -4;
			result.msg = "该IP单日短信发送已达上限";

			renderJSON(result);
		}

		/* 将手机号码存入缓存，用于判断60S中内同一手机号不允许重复发送验证码 */
		String encryString = Session.current().getId();
		Object cache = Cache.get(mobile + encryString + scene);
		if (null == cache) {
			Cache.set(mobile + encryString + scene, mobile, Constants.CACHE_TIME_SECOND_60);
		} else {
			String isOldMobile = cache.toString();
			if (isOldMobile.equals(mobile)) {
				result.code = -4;
				result.msg = "短信已发送";

				renderJSON(result);
			}
		}

		/* 查询短信验证码模板 */
		List<t_template_notice> noticeList = noticeService.queryTemplatesByScene(NoticeScene.SECRITY_CODE);

		if (noticeList.size() < 0) {
			result.code = -4;
			result.msg = "短信发送失败";

			renderJSON(result);
		}
		String content = noticeList.get(0).content;
		String smsAccount = settingService.findSettingValueByKey(SettingKey.SERVICE_SMS_ACCOUNT);
		String smsPassword = settingService.findSettingValueByKey(SettingKey.SERVICE_SMS_PASSWORD);

		/* 发送短信验证码 */
		SMSUtil.sendCode(smsAccount, smsPassword, mobile, content, Constants.CACHE_TIME_MINUS_30, scene,
				ConfConst.IS_CHECK_MSG_CODE);

		/* 添加一条短信发送控制记录 */
		sendCodeRecordsService.addSendCodeRecords(mobile, getIp());

		result.code = 1;
		result.msg = "短信验证码发送成功";

		renderJSON(result);
	}

	/**
	 * 注册成功页面
	 *
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月15日
	 */
	public static void registerSuccessPre() {

		render();
	}

	/**
	 * 用户登入页面
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月28日
	 *
	 */
	public static void loginPre() {
		String encryString = Session.current().getId();
		Integer loginCount = (Integer) Cache.get(CacheKey.LOGINCOUNT_ + encryString);
		flash.put("loginCount", loginCount == null ? 0 : loginCount);

		/* 获取后台设置的登录页面背景图片 */
		String loginPic = advertisementService.findLoginPic();
		if (StringUtils.isBlank(loginPic)) {
			loginPic = "/public/front/images/login.jpg";
		}

		/* 随机码 */
		String randomId = Codec.UUID();

		/* 禁止该页面进行高速缓存 */
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Expires", "0");

		render(randomId, loginPic);
	}

	/**
	 * 用户登录
	 * 
	 * @description
	 * 
	 * @param loginName
	 *            登录名
	 * @param password
	 *            登录密码
	 * @param code
	 *            验证码
	 * @param randomId
	 *            验证码密文
	 * 
	 * @author Chenzhipeng
	 * @createDate 2015年12月16日
	 */
	public static void logining(String mobile, String password, String code, String randomId) {
		/* 获取当前sessionid */
		String encryString = Session.current().getId();

		/* 用于回显 */
		flash.put("name", mobile);

		/* 验证信息 */
		if (StringUtils.isBlank(mobile)) {
			flash.error("手机号不能为空");

			loginPre();
		}
		if (!StrUtil.isMobileNum(mobile)) {
			flash.error("手机号格式不正确");

			loginPre();
		}
		if (!userService.isMobileExists(mobile)) {
			flash.error("手机号未注册");

			loginPre();
		}

		if (StringUtils.isBlank(password)) {
			flash.error("密码不能为空");

			loginPre();
		}

		/* 获取登录次数 */
		Integer loginCount = (Integer) Cache.get(CacheKey.LOGINCOUNT_ + encryString);
		loginCount = loginCount == null ? 0 : loginCount;
		Cache.set(CacheKey.LOGINCOUNT_ + encryString, ++loginCount, Constants.CACHE_TIME_MINUS_30);

		// 登陆失败次数大于2时，需要校验验证码
		if (loginCount > 2) {
			if (StringUtils.isBlank(code)) {
				flash.error("验证码不能为空");

				loginPre();
			}
			if (StringUtils.isBlank(randomId)) {
				flash.error("验证码不正确");

				loginPre();
			}
			String randCode = CaptchaUtil.getCode(randomId);

			/* 图形验证码验证码 校验 */
			if (ConfConst.IS_CHECK_PIC_CODE) {
				if (!code.equalsIgnoreCase(randCode)) {
					flash.put("password", password);
					flash.error("验证码不正确");

					loginPre();
				}
			}
		}

		int securityLockTimes = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.SECURITY_LOCK_TIMES),
				3);
		int securityLockTime = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.SECURITY_LOCK_TIME),
				30);
		// String pwdEncrypt = Encrypt.MD5(password +
		// ConfConst.ENCRYPTION_KEY_MD5);
		ResultInfo result = userService.logining(mobile, password, Client.PC, getIp(), securityLockTimes,
				securityLockTime);

		if (result.code < 1) {
			if (result.code == ResultInfo.ERROR_SQL) {// 正常登录失败，值也小于1，但是数据不能回滚
				LoggerUtil.info(true, result.msg);
			}

			flash.error(result.msg);

			loginPre();
		}
		/* 清除缓存中的登录次数 */
		Cache.delete(CacheKey.LOGINCOUNT_ + encryString);

		MyAccountCtrl.homePre();

	}

	/**
	 * 用户登出
	 * 
	 * @description
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月17日
	 */
	public static void loginOutPre() {
		ResultInfo result = new ResultInfo();
		CurrUser currUser = getCurrUser();
		if (currUser == null) {
			loginPre();
		}

		result = userService.updateUserLoginOutInfo(currUser.id, Client.PC.code, getIp());
		if (result.code < 0) {
			flash.put("msg", "退出系统时出现异常");

			loginPre();
		}
		flash.put("msg", "成功退出");

		loginPre();
	}

	/**
	 * 忘记密码页面(第一步)
	 * 
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月9日
	 *
	 */
	public static void forgetPwdFirstPre() {
		/* 随机码 */
		String randomId = Codec.UUID();

		render(randomId);
	}

	/**
	 * 忘记密码页面(第二步)
	 * 
	 * @param mobile
	 *            手机号码
	 * @param code
	 *            验证码
	 * @param randomId
	 * @param smsCode
	 *            短信验证码
	 * @param scene
	 *            场景
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月12日
	 *
	 */
	public static void forgetPwdSecond(String mobile, String code, String randomId, String smsCode, String scene) {

		/* 验证信息 */
		if (StringUtils.isBlank(mobile)) {
			flash.error("请输入手机号");

			forgetPwdFirstPre();
		}

		/* 回显手机号码 */
		flash.put("mobile", mobile);

		if (!userService.isMobileExists(mobile)) {
			flash.error("手机号码不存在");

			forgetPwdFirstPre();
		}

		if (StringUtils.isBlank(code)) {
			flash.error("请输入验证码");

			forgetPwdFirstPre();
		}

		/* 短信验证码验证 */
		if (StringUtils.isBlank(smsCode)) {
			flash.error("请输入短信验证码");

			forgetPwdFirstPre();
		}

		/* 获取缓存中的短信验证码 */
		Object obj = Cache.get(mobile + scene);
		if (obj == null) {
			flash.error("短信验证码已失效");

			forgetPwdFirstPre();
		}
		String codeStr = obj.toString();

		/* 校验短信验证码 */
		if (ConfConst.IS_CHECK_MSG_CODE) {
			if (!codeStr.equals(smsCode)) {
				flash.error("短信验证码不正确");

				forgetPwdFirstPre();
			}
		}

		/* 清除缓存中的图形验证码 */
		String encryString = Session.current().getId();
		Object cache = Cache.get(mobile + encryString + scene);
		if (cache != null) {
			Cache.delete(mobile + encryString + scene);
		}

		String smsCodeSign = Encrypt.encrypt3DES(smsCode, ConfConst.ENCRYPTION_KEY_DES);

		render(mobile, smsCodeSign);
	}

	/**
	 * 忘记密码页面(第三步)
	 * 
	 * @param mobile
	 *            手机号码
	 * @param password
	 *            密码
	 * @param confirmPassword
	 *            确认密码
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月12日
	 *
	 */
	public static void forgetPwdThird(String mobile, String password, String smsCodeSign, String scene) {

		String smsCode = Encrypt.decrypt3DES(smsCodeSign, ConfConst.ENCRYPTION_KEY_DES);
		if (StringUtils.isBlank(smsCode)) {
			flash.error("已超时，请重新操作");

			forgetErrorPre();
		}

		/* 获取缓存中的短信验证码 */
		String codeStr = "";
		Object obj = Cache.get(mobile + scene);
		if (obj == null) {
			flash.error("短信验证码已失效");

			forgetPwdFirstPre();
		} else {
			codeStr = obj.toString();
			// 清除缓存中的短信验证码
			Cache.delete(mobile + scene);
		}

		/* 校验短信验证码 */
		if (ConfConst.IS_CHECK_MSG_CODE) {
			if (!codeStr.equals(smsCode)) {
				flash.error("短信验证码不正确");

				forgetPwdFirstPre();
			}
		}

		long userId = userService.queryIdByMobile(mobile);
		if (userId <= 0) {
			flash.error("修改密码失败");

			forgetErrorPre();
		}

		if (StringUtils.isBlank(password)) {
			flash.error("请重新输入密码");

			forgetErrorPre();
		}

		if (!StrUtil.isValidPassword(password, 6, 20)) {
			flash.error("密码格式不正确");

			forgetErrorPre();
		}

		t_user user = userService.findByID(userId);
		// 用户密码加密无key
		if (user.is_no_key) {

			password = Encrypt.MD5(password);

			// 加密串字母大写
			password = password.toUpperCase();
		} else {

			password = Encrypt.MD5(password + ConfConst.ENCRYPTION_KEY_MD5);
		}

		/* 密码加密 */
		// password = Encrypt.MD5(password + ConfConst.ENCRYPTION_KEY_MD5);
		ResultInfo result = userService.updatePassword(userId, password);
		if (result.code < 0) {
			flash.error(result.msg);

			forgetErrorPre();
		}

		render();
	}

	public static void forgetErrorPre() {

		render();
	}

	/**
	 * 用户注册协议
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月19日
	 */
	public static void registerDealPre() {
		t_information registerDeal = informationService.findRegisterDeal();
		if (registerDeal == null) {

			error404();
		}

		render(registerDeal);
	}

	/**
	 * 激活用户邮箱
	 * 
	 * @param su
	 *            用户Id加密
	 * @param se
	 *            用户手机加密
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月15日
	 *
	 */
	public static void confirmActiveEmailPre(String su, String se) {
		ResultInfo result = new ResultInfo();

		/* 解密用户Id */
		result = Security.decodeSign(su, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			flash.put("msg", "抱歉，验证超时，邮箱绑定失败");

			render();
		}
		long userId = Long.parseLong(result.obj.toString());

		/* 解密用户email */
		result = Security.decodeEmailSign(se, Constants.MSG_EMAIL_SIGN, Constants.VALID_TIME,
				ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			flash.put("msg", "抱歉，验证超时，邮箱绑定失败");

			render();
		}
		String email = result.obj.toString();

		/* 激活并修改用户邮箱 */
		result = userInfoService.updateUserEmail(userId, email);
		if (result.code < 1) {
			flash.put("msg", result.msg);

			render();
		}

		// 积分商城
		if (ModuleConst.EXT_MALL) {

			double score = Convert.strToDouble(settingService.findSettingValueByKey(SettingKey.BIND_MAILBOX_SCORE), 0);

			if (score > 0) {

				// 增加积分
				int rows = userFundService.updateUserScoreBalanceAdd(userId, score);
				if (rows <= 0) {
					JPA.setRollbackOnly();

					flash.put("msg", "绑定邮箱获得积分失败");
					render();
				}

				// 及时查询用户的可用积分
				double scoreBalance = userFundService.findUserScoreBalance(userId);

				/** 添加用户积分记录 */
				Map<String, String> summaryPrams = new HashMap<String, String>();
				summaryPrams.put("score", (int) score + "");
				boolean addDeal = scoreUserService.addScoreUserInfo(userId, score, scoreBalance,
						t_score_user.OperationType.BIND_MAILBOX_SCORE, summaryPrams);

				if (!addDeal) {
					JPA.setRollbackOnly();

					flash.put("msg", "绑定邮箱获得积分失败");
					render();
				}

				userService.flushUserCache(userId);
			}
		}

		render();
	}

	/**
	 * 校验手机验证码是否正确
	 * 
	 * @param mobile
	 * @param randomId
	 * @param code
	 * @param smsCode
	 * @param type
	 * @param scene
	 *            场景
	 * @author Chenzhipeng
	 * @createDate 2016年1月14日
	 *
	 */
	public static void checkSmsCode(String mobile, String randomId, String code, String smsCode, int type,
			String scene) {
		ResultInfo result = new ResultInfo();

		/* 验证信息 */
		if (StringUtils.isBlank(mobile)) {
			result.code = -1;
			result.msg = "手机号不能为空";

			renderJSON(result);
		}

		boolean isType = userService.isMobileExists(mobile);

		if (type == 1) {
			if (!isType) {
				result.code = -1;
				result.msg = "手机号未注册";

				renderJSON(result);
			}
		} else if (type == 2) {
			if (isType) {
				result.code = -1;
				result.msg = "该手机号已被占用";

				renderJSON(result);
			}
		}

		if (StringUtils.isBlank(code)) {
			result.code = -1;
			result.msg = "验证码不能为空";

			renderJSON(result);
		}

		/* 获取缓存中的短信验证码 */
		Object obj = Cache.get(mobile + scene);
		if (obj == null) {
			result.code = -1;
			result.msg = "短信验证码已失效";

			renderJSON(result);
		}
		String SMScode = obj.toString();

		/* 短信验证码 校验 */
		if (ConfConst.IS_CHECK_MSG_CODE) {
			if (!SMScode.equals(smsCode)) {
				result.code = -1;
				result.msg = "短信验证码不正确";

				renderJSON(result);
			}
		}

		result.code = 1;

		renderJSON(result);
	}

	/**
	 * 校验手机验证码是否正确
	 * 
	 * @param mobile
	 * @param randomId
	 * @param code
	 * @param smsCode
	 * @param type
	 * @param scene
	 *            场景
	 * @author Chenzhipeng
	 * @createDate 2016年1月14日
	 *
	 */
	public static void checkRecommendCode() {
		/* cps 邀请码 推广 */
		ResultInfo result = new ResultInfo();
		result.code=1;
		result.msg="正确";
		String recommendCode ="";
		recommendCode=Encrypt.decrypt3DES(params.get("recommendCode"), ConfConst.ENCRYPTION_KEY_DES);
		result.obj=recommendCode;
		if(StringUtils.isBlank(recommendCode)){
			result.code=-1;
			result.msg="错误";
		}
		renderJSON(result);
	}
}
