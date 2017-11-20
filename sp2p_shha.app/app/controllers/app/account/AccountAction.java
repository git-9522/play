package controllers.app.account;

import java.util.List;
import java.util.Map;

import models.common.entity.t_bank_card_user;
import models.common.entity.t_send_code;
import models.common.entity.t_user;
import models.common.entity.t_user_fund;
import models.common.entity.t_user_info;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import payment.impl.PaymentProxy;
import play.cache.Cache;
import play.mvc.Scope.Session;
import service.AccountAppService;
import services.common.BankCardUserService;
import services.common.NoticeService;
import services.common.SendCodeRecordsService;
import services.common.UserFundService;
import services.common.UserInfoService;

import com.shove.Convert;
import com.shove.security.Encrypt;

import common.constants.ConfConst;
import common.constants.Constants;
import common.constants.ModuleConst;
import common.constants.PaymentConst;
import common.constants.SmsScene;
import common.enums.Area;
import common.enums.Bank;
import common.enums.BusiType;
import common.enums.Client;
import common.enums.DeviceType;
import common.enums.Province;
import common.enums.ServiceType;
import common.utils.Factory;
import common.utils.IDCardValidate;
import common.utils.LoggerUtil;
import common.utils.OrderNoUtil;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.StrUtil;
import common.utils.UserValidataUtil;
import controllers.common.BaseController;

/**
 * 用户账户(1)[OPT:1XX]
 *
 * @description 包含账户登录[OPT:11x]和注册相关信息[OPT:12x]
 *
 * @author DaiZhengmiao
 * @createDate 2016年6月29日
 */
public class AccountAction {

	private static AccountAppService userAppService = Factory.getService(AccountAppService.class);

	private static SendCodeRecordsService sendCodeRecordsService = Factory.getService(SendCodeRecordsService.class);

	private static UserInfoService userInfoService = Factory.getService(UserInfoService.class);

	private static NoticeService noticeService = Factory.getService(NoticeService.class);

	private static UserFundService userFundService = Factory.getService(UserFundService.class);
	
	private static BankCardUserService bankCardUserService = Factory.getService(BankCardUserService.class);

	/**
	 * 
	 * 发送验证码(OPT=111)
	 * 
	 * @return
	 * @author luzhiwei
	 * @createDate 2016-3-31
	 */
	public static String sendCode(Map<String, String> parameters) {

		JSONObject json = new JSONObject();

		String mobile = parameters.get("mobile");
		String scene = parameters.get("scene");// 场景

		/* 验证手机号是否符合规范 */
		if (!StrUtil.isMobileNum(mobile)) {
			json.put("code", -1);
			json.put("msg", "手机号不符合规范");

			return json.toString();
		}

		if (SmsScene.APP_REGISTER.equals(scene)) {
			if (userAppService.isMobileExists(mobile)) {
				json.put("code", -1);
				json.put("msg", "该手机号已被占用");

				return json.toString();
			}
		} else if (SmsScene.APP_FORGET_PWD.equals(scene)) {
			if (!userAppService.isMobileExists(mobile)) {
				json.put("code", -1);
				json.put("msg", "手机号未注册");

				return json.toString();
			}
		} else {
			json.put("code", -1);
			json.put("msg", "场景类型不符合规范");

			return json.toString();
		}

		/* 根据手机号码查询短信发送条数 */
		List<t_send_code> recordByMobile = sendCodeRecordsService.querySendRecordsByMobile(mobile);
		if (recordByMobile.size() >= ConfConst.SEND_SMS_MAX_MOBILE) {
			json.put("code", -1);
			json.put("msg", "该手机号码单日短信发送已达上限");

			return json.toString();
		}

		/* 根据IP查询短信发送条数 */
		List<t_send_code> recordByIp = sendCodeRecordsService.querySendRecordsByIp(BaseController.getIp());
		if (recordByIp.size() >= ConfConst.SEND_SMS_MAX_IP) {
			json.put("code", -1);
			json.put("msg", "该IP单日短信发送已达上限");

			return json.toString();
		}

		/* 将手机号码存入缓存，用于判断60S中内同一手机号不允许重复发送验证码 */
		String encryString = Session.current().getId();
		Object cache = Cache.get(mobile + encryString + scene);
		if (null == cache) {
			Cache.set(mobile + encryString + scene, mobile, Constants.CACHE_TIME_SECOND_60);
		} else {
			String isOldMobile = cache.toString();
			if (isOldMobile.equals(mobile)) {
				json.put("code", -1);
				json.put("msg", "短信已发送");

				return json.toString();
			}
		}

		return userAppService.sendCode(mobile, scene);
	}

	/***
	 * 
	 * 注册准备（opt=124）
	 * 
	 * @param parameters
	 * @return
	 * @description
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-14
	 */
	public static String registerPre(Map<String, String> parameters) {

		return userAppService.registerPre();
	}

	/***
	 * 
	 * 用户注册（OPT=113）
	 * 
	 * @param parameters
	 * @return
	 * @description
	 *
	 * @author luzhiwei
	 * @createDate 2016-3-31
	 */
	public static String registering(Map<String, String> parameters) throws Exception {
		JSONObject json = new JSONObject();

		String mobile = parameters.get("mobile");// 手机号
		String password = parameters.get("password");// 密码
		String verificationCode = parameters.get("verificationCode");// 手机验证码
		String invitationCode = parameters.get("invitationCode");// 邀请码
		String deviceType = parameters.get("deviceType");// 设备类型

		if (StringUtils.isBlank(mobile)) {
			json.put("code", -1);
			json.put("msg", "请填写手机号");

			return json.toString();
		}

		/* 验证手机号是否符合规范 */
		if (!StrUtil.isMobileNum(mobile)) {
			json.put("code", -1);
			json.put("msg", "手机号不符合规范");

			return json.toString();
		}

		/* 判断手机号码是否存在 */
		if (userAppService.isMobileExists(mobile)) {
			json.put("code", -1);
			json.put("msg", "手机号码已存在");

			return json.toString();
		}

		if (StringUtils.isBlank(verificationCode)) {
			json.put("code", -1);
			json.put("msg", "请输入短信验证码");

			return json.toString();
		}

		Object obj = Cache.get(mobile + SmsScene.APP_REGISTER);
		if (obj == null) {
			json.put("code", -1);
			json.put("msg", "短信验证码已失效");

			return json.toString();
		}
		String codeStr = obj.toString();

		/** 短信验证码 验证 */
		if (ConfConst.IS_CHECK_MSG_CODE) {
			if (!codeStr.equals(verificationCode)) {
				json.put("code", -1);
				json.put("msg", "短信验证码错误");

				return json.toString();
			}
		}
		if (StringUtils.isBlank(password)) {
			json.put("code", -1);
			json.put("msg", "请填写密码");

			return json.toString();
		}

		password = Encrypt.decrypt3DES(password, ConfConst.ENCRYPTION_APP_KEY_DES);// 解密密码

		/* 验证密码是否符合规范 */
		if (!StrUtil.isValidPassword(password)) {
			json.put("code", -1);
			json.put("msg", "密码不符合规范");

			return json.toString();
		}

		int flagOfRecommendCode = 0;
		/* 验证邀请码 */
		if (StringUtils.isNotBlank(invitationCode)) {

			if (StrUtil.isMobileNum(invitationCode)) {// CPS邀请码是用户的手机号
				if (common.constants.ModuleConst.EXT_CPS) {// CPS模块是否存在
					/* 判断手机号码是否存在 */
					flagOfRecommendCode = userAppService.isMobileExists(invitationCode) ? 1 : -1;
				}
			} else {
				if (common.constants.ModuleConst.EXT_WEALTHCIRCLE) {// 财富圈邀请码
					service.ext.wealthcircle.WealthCircleService wealthCircleService = Factory
							.getService(service.ext.wealthcircle.WealthCircleService.class);
					ResultInfo result2 = wealthCircleService.isWealthCircleCodeUseful(invitationCode);
					if (result2.code < 0) {
						json.put("code", -1);
						json.put("msg", result2.msg);

						return json.toString();
					} else if (result2.code == 1) {
						flagOfRecommendCode = 2;
					}
				}
			}
			if (flagOfRecommendCode < 0) {
				json.put("code", -1);
				json.put("msg", "推广码不存在");

				return json.toString();
			}
		}

		if (DeviceType.getEnum(Convert.strToInt(deviceType, -99)) == null) {
			json.put("code", -1);
			json.put("msg", "请使用移动客户端操作");

			return json.toString();
		}

		ResultInfo result = new ResultInfo();

		/* 自动生成用户名 */
		String userName = userAppService.userNameFactory(mobile);
		Client client = Client.getEnum(Integer.valueOf(deviceType));
		/* 密码加密 */
		password = Encrypt.MD5(password + ConfConst.ENCRYPTION_KEY_MD5);
		result = userAppService.registering(userName, mobile, password, client, BaseController.getIp());
		if (result.code < 1) {
			LoggerUtil.info(true, "注册时：%s", result.msg);

			json.put("code", -1);
			json.put("msg", result.msg);

			return json.toString();
		} else {
			t_user user = (t_user) result.obj;
			// ext_redpacket 红包数据生成 start
			/*
			 * if(ModuleConst.EXT_REDPACKET){
			 * services.ext.redpacket.RedpacketService redpacketService =
			 * Factory.getService(services.ext.redpacket.RedpacketService.class)
			 * ; ResultInfo resultRed =
			 * redpacketService.creatRedpacket(user.id); //此处不能使用result
			 * if(resultRed.code < 1){ LoggerUtil.info(true,
			 * "注册成功，生成红包相关数据时出错，数据回滚，%s", resultRed.msg); json.put("code", -1);
			 * json.put("msg", resultRed.msg);
			 * 
			 * return json.toString(); } }
			 */
			// end

			// experienceBid 体验金发放 start
			if (ModuleConst.EXT_EXPERIENCEBID) {
				service.ext.experiencebid.ExperienceBidAccountService experienceBidAccountService = Factory
						.getService(service.ext.experiencebid.ExperienceBidAccountService.class);
				ResultInfo resultExperBid = experienceBidAccountService.createExperienceAccount(user.id); // 此处不能使用result
				if (resultExperBid.code < 1) {
					LoggerUtil.info(true, "注册成功，发放体验金到账户时，%s", resultExperBid.msg);
					json.put("code", -1);
					json.put("msg", resultExperBid.msg);

					return json.toString();
				}
			}
			// end
			// cps账户开户
			if (ModuleConst.EXT_CPS) {
				services.ext.cps.CpsService cpsService = Factory.getService(services.ext.cps.CpsService.class);
				ResultInfo resultCps = cpsService.createCps(invitationCode, user.id);
				if (resultCps.code < 1) {
					LoggerUtil.info(true, "注册成功，生成cps推广相关数据时出错，数据回滚，%s", resultCps);
					json.put("code", -1);
					json.put("msg", resultCps.msg);

					return json.toString();
				}
			}

			// 财富圈账号数据创建
			if (ModuleConst.EXT_WEALTHCIRCLE) {
				service.ext.wealthcircle.WealthCircleService wealthCircleService = Factory
						.getService(service.ext.wealthcircle.WealthCircleService.class);
				ResultInfo wealthResultInfo = null;

				if (flagOfRecommendCode == 2) {
					wealthResultInfo = wealthCircleService.creatWealthCircle(invitationCode, user.id);
				} else {
					wealthResultInfo = wealthCircleService.creatWealthCircle(null, user.id);
				}
				if (wealthResultInfo.code < 1) {
					LoggerUtil.info(true, "注册成功，生成财富圈推广相关数据时出错，数据回滚，%s", wealthResultInfo.msg);
					json.put("code", -1);
					json.put("msg", wealthResultInfo.msg);

					return json.toString();
				}
			}

			// end
			String html = "";

			// 富有接口要先去登记邮箱、身份认证、绑定银行卡
			if (!ConfConst.CURRENT_TRUST_TYPE.equals(PaymentConst.TRUST_TYPE_FY)) {
				// 业务订单号 开户操作
				String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.REGIST);

				ResultInfo resultRegist = PaymentProxy.getInstance().regist(client.code, serviceOrderNo, user.id);

				if (resultRegist.code < 0) {
					LoggerUtil.info(true, resultRegist.msg);
					json.put("code", -1);
					json.put("msg", result.msg);

					return json.toString();
				}

				html = resultRegist.obj.toString();
			}

			/* 清除缓存中的验证码 */
			String encryString = Session.current().getId();
			Object cache = Cache.get(mobile + encryString + SmsScene.APP_REGISTER);
			if (cache != null) {
				Cache.delete(mobile + encryString + SmsScene.APP_REGISTER);
				Cache.delete(mobile + SmsScene.APP_REGISTER);
			}
			json.put("userId", user.appSign);
			json.put("html", html);
			json.put("code", 1);
			json.put("msg", "注册成功");

			return json.toString();
		}

	}

	/***
	 * 
	 * 查询平台logo
	 * 
	 * @param parameters
	 * @return
	 * @description
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-14
	 */
	public static String findPlatformIconFilename(Map<String, String> parameters) {

		return userAppService.findPlatformIconFilename();
	}

	/**
	 * 用户登录（OPT=123）
	 *
	 * @param parameters
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年3月31日
	 */
	public static String logining(Map<String, String> parameters) {
		JSONObject json = new JSONObject();

		String mobile = parameters.get("mobile");
		String password = parameters.get("password");
		String deviceType = parameters.get("deviceType");

		if (!StrUtil.isMobileNum(mobile)) {
			json.put("code", -1);
			json.put("msg", "请输入正确的用户名!");

			return json.toString();
		}
		if (StringUtils.isBlank(password)) {
			json.put("code", -1);
			json.put("msg", "请输入密码!");

			return json.toString();
		}
		password = Encrypt.decrypt3DES(password, ConfConst.ENCRYPTION_APP_KEY_DES);// 解密密码

		if (DeviceType.getEnum(Convert.strToInt(deviceType, -99)) == null) {
			json.put("code", -1);
			json.put("msg", "请使用移动客户端操作");

			return json.toString();
		}

		return userAppService.logining(mobile, password, deviceType);
	}

	/**
	 * 修改登录密码（OPT=122）
	 *
	 * @param parameters
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年3月31日
	 */
	public static String updateUserPwd(Map<String, String> parameters) {
		JSONObject json = new JSONObject();

		String mobile = parameters.get("mobile");
		String newPassword = parameters.get("newPassword");
		String smsCodeSign = parameters.get("smsCodeSign");
		String scene = parameters.get("scene");

		if (!StrUtil.isMobileNum(mobile)) {
			json.put("code", -1);
			json.put("msg", "请输入正确手机号!");

			return json.toString();
		}
		if (StringUtils.isBlank(newPassword)) {
			json.put("code", -1);
			json.put("msg", "请输入新密码!");

			return json.toString();
		}

		String smsCode = Encrypt.decrypt3DES(smsCodeSign, ConfConst.ENCRYPTION_KEY_DES);
		if (StringUtils.isBlank(smsCode)) {
			json.put("code", -1);
			json.put("msg", "短信验证码签名无效");

			return json.toString();
		}

		/* 获取缓存中的短信验证码 */
		String codeStr = "";
		Object obj = Cache.get(mobile + scene);
		if (obj == null) {

			json.put("code", -1);
			json.put("msg", "短信验证码已失效");

			return json.toString();
		} else {
			codeStr = obj.toString();
			// 清除缓存中的短信验证码
			Cache.delete(mobile + scene);
			String encryString = Session.current().getId();
			Cache.delete(mobile + encryString + scene);
		}

		/* 校验短信验证码 */
		if (ConfConst.IS_CHECK_MSG_CODE) {
			if (!codeStr.equals(smsCode)) {
				json.put("code", -1);
				json.put("msg", "短信验证码不正确");

				return json.toString();
			}
		}

		newPassword = Encrypt.decrypt3DES(newPassword, ConfConst.ENCRYPTION_APP_KEY_DES);// 解密密码
		if (!StrUtil.isValidPassword(newPassword)) {
			json.put("code", -1);
			json.put("msg", "密码格式错误!");

			return json.toString();
		}
		long userId = userAppService.queryIdByMobile(mobile);
		if (userId <= 0) {
			json.put("code", -1);
			json.put("msg", "没有该用户!");

			return json.toString();
		}

		t_user user = userAppService.findByID(userId);

		// 用户密码加密无key
		if (user.is_no_key) {

			newPassword = Encrypt.MD5(newPassword);

			// 加密串字母大写
			newPassword = newPassword.toUpperCase();
		} else {

			newPassword = Encrypt.MD5(newPassword + ConfConst.ENCRYPTION_KEY_MD5);
		}

		/** 密码加密 */
		// newPassword = Encrypt.MD5(newPassword +
		// ConfConst.ENCRYPTION_KEY_MD5);
		String oldPassWord = userAppService.findUserOldPassWord(userId);
		if (newPassword.equals(oldPassWord)) {
			json.put("code", -1);
			json.put("msg", "新密码不能与旧密码相同");

			return json.toString();
		}
		return userAppService.updateUserPwd(mobile, userId, newPassword);
	}

	/**
	 * 验证验证码（OPT=121）
	 *
	 * @param parameters
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年3月31日
	 */
	public static String verificationCode(Map<String, String> parameters) {
		JSONObject json = new JSONObject();

		String verificationCode = parameters.get("verificationCode");
		String mobile = parameters.get("mobile");
		String scene = parameters.get("scene");

		if (StringUtils.isBlank(verificationCode)) {
			json.put("code", -1);
			json.put("msg", "验证码不能为空!");

			return json.toString();
		}
		if (StringUtils.isBlank(mobile)) {
			json.put("code", -1);
			json.put("msg", "手机号码不能为空!");

			return json.toString();
		}

		if (!SmsScene.APP_REGISTER.equals(scene) && !SmsScene.APP_FORGET_PWD.equals(scene)) {
			json.put("code", -1);
			json.put("msg", "参数scene错误!");

			return json.toString();
		}

		return userAppService.verificationCode(verificationCode, mobile, scene);
	}

	/**
	 * 开户准备
	 * 
	 * @param parameters
	 * @return
	 */
	public static String createAccountPre(Map<String, String> parameters) {
		JSONObject json = new JSONObject();
		String signId = parameters.get("userId");
		String deviceType = parameters.get("deviceType");// 设备类型

		ResultInfo result = Security.decodeSign(signId, Constants.USER_ID_SIGN, Constants.VALID_TIME,
				ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			json.put("code", ResultInfo.LOGIN_TIME_OUT);
			json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			return json.toString();
		}

		if (DeviceType.getEnum(Convert.strToInt(deviceType, -99)) == null) {
			json.put("code", -1);
			json.put("msg", "请使用移动客户端操作");

			return json.toString();
		}

		long userId = Long.parseLong(result.obj.toString());

		return userAppService.createAccountPre(userId, Convert.strToInt(deviceType, -99));
	}

	/**
	 * 资金存管开户
	 *
	 * @param parameters
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年4月12日
	 */
	public static String createAccount(Map<String, String> parameters) {
		JSONObject json = new JSONObject();
		String signId = parameters.get("userId");
		String deviceType = parameters.get("deviceType");// 设备类型

		ResultInfo result = Security.decodeSign(signId, Constants.USER_ID_SIGN, Constants.VALID_TIME,
				ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			json.put("code", ResultInfo.LOGIN_TIME_OUT);
			json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			return json.toString();
		}

		long userId = Long.parseLong(result.obj.toString());

		if (DeviceType.getEnum(Convert.strToInt(deviceType, -99)) == null) {
			json.put("code", -1);
			json.put("msg", "请使用移动客户端操作");

			return json.toString();
		}
		
		/*
		 * String hfName, String realName, String idNumber, String mobile,
		 * String cardId, String bankId, String provId, String areaId, String
		 * smsCode
		 */

		if (!ConfConst.CURRENT_TRUST_TYPE.equals(PaymentConst.TRUST_TYPE_FY)) {
			// 汇付
			String hfName = parameters.get("hfName");
			String realName = parameters.get("realName");
			String idNumber = parameters.get("idNumber");
			String mobile = parameters.get("mobile");
			String cardId = parameters.get("cardId");
			String bankId = parameters.get("bankId");
			String provId = parameters.get("provId");
			String areaId = parameters.get("areaId");
			String smsCode = parameters.get("smsCode");

			result = userInfoService.checkHfname(hfName);

			if (result.code != 1) {
				json.put("code", result.code);
				json.put("msg", result.msg);
				return json.toString();
			}

			if (StringUtils.isBlank(realName)) {
				json.put("code", -1);
				json.put("msg", "请填写真实姓名");
				return json.toString();
			}

			if (StringUtils.isBlank(idNumber) || idNumber.length() < 18) {
				json.put("code", -1);
				json.put("msg", "请填写正确的身份证号");
				return json.toString();
			}

			if (StringUtils.isBlank(mobile)) {
				json.put("code", -1);
				json.put("msg", "请填写手机号");
				return json.toString();
			}

			/* 验证手机号是否符合规范 */
			if (!StrUtil.isMobileNum(mobile)) {
				json.put("code", -1);
				json.put("msg", "手机号不符合规范");
				return json.toString();
			}

			Object obj = Cache.get(mobile + BusiType.REGISTER.value);

			if (obj == null) {
				json.put("code", -1);
				json.put("msg", "短信验证码已失效");
				return json.toString();
			}

			String smsSeq = obj.toString();

			if (StringUtils.isBlank(cardId) || cardId.length() < 16) {
				json.put("code", -1);
				json.put("msg", "请填写正确的银行卡号");
				return json.toString();
			}

			Bank bank = Bank.getBank(bankId);

			if (bank == null) {
				json.put("code", -1);
				json.put("msg", "银行代号错误");
				return json.toString();
			}

			Province province = Province.getProvByCode(provId);

			if (province == null) {
				json.put("code", -1);
				json.put("msg", "省份代号错误");
				return json.toString();
			}

			Area area = Area.getAreaByCode(areaId);

			if (area == null) {
				json.put("code", -1);
				json.put("msg", "城市代号错误");
				return json.toString();
			}

			if (StringUtils.isBlank(smsCode)) {
				json.put("code", -1);
				json.put("msg", "短信验证码不能为空");
				return json.toString();
			}

			return userAppService.createAccount(userId, hfName, realName, idNumber, mobile, cardId, bankId, provId,
					areaId, smsCode, smsSeq, Convert.strToInt(deviceType, -99));
		} else {

			// 富有接口
			String provNum = parameters.get("provNum");
			String cityNum = parameters.get("cityNum");
			String bankType = parameters.get("bankType");
			String bankName = parameters.get("bankName");
			String bankNum = parameters.get("bankNum");

			if (!UserValidataUtil.checkBindEmail(userId)) {
				json.put("code", ResultInfo.NOT_BIND_EMAIL);
				json.put("msg", "请绑定邮箱");

				return json.toString();
			}

			if (!UserValidataUtil.checkRealName(userId)) {
				json.put("code", ResultInfo.NOT_REAL_NAME);
				json.put("msg", "请实名认证");

				return json.toString();
			}

			if (StringUtils.isBlank(provNum)) {
				json.put("code", -1);
				json.put("msg", "请选择省份!");

				return json.toString();
			}

			if (StringUtils.isBlank(cityNum)) {
				json.put("code", -1);
				json.put("msg", "请选择城市!");

				return json.toString();
			}

			if (StringUtils.isBlank(bankType)) {
				json.put("code", -1);
				json.put("msg", "请选择银行!");

				return json.toString();
			}

			if (StringUtils.isBlank(bankName)) {
				json.put("code", -1);
				json.put("msg", "支行名称不能为空!");

				return json.toString();
			}

			if (StringUtils.isBlank(bankNum)) {
				json.put("code", -1);
				json.put("msg", "银行卡号不能为空!");

				return json.toString();
			}

			return userAppService.createAccount(userId, provNum, cityNum, bankType, bankName, bankNum,
					Convert.strToInt(deviceType, -99));
		}
	}

	/**
	 * 准备绑定邮箱
	 * 
	 * @param parameters
	 * @return
	 */
	public static String bindEmailPre(Map<String, String> parameters) {
		JSONObject json = new JSONObject();
		String signId = parameters.get("userId");

		ResultInfo result = Security.decodeSign(signId, Constants.USER_ID_SIGN, Constants.VALID_TIME,
				ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			json.put("code", ResultInfo.LOGIN_TIME_OUT);
			json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			return json.toString();
		}

		long userId = Long.parseLong(result.obj.toString());

		if (!UserValidataUtil.checkBindEmail(userId)) {
			json.put("code", ResultInfo.NOT_BIND_EMAIL);
			json.put("msg", "请绑定邮箱");

			return json.toString();
		}

		json.put("code", 1);
		json.put("msg", "已绑定邮箱");

		return json.toString();
	}

	/**
	 * 绑定邮箱
	 * 
	 * @param parameters
	 * @return
	 */
	public static String bindEmail(Map<String, String> parameters) {
		JSONObject json = new JSONObject();
		String signId = parameters.get("userId");
		String email = parameters.get("email");
		String baseUrl = parameters.get("baseUrl");

		ResultInfo result = Security.decodeSign(signId, Constants.USER_ID_SIGN, Constants.VALID_TIME,
				ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			json.put("code", ResultInfo.LOGIN_TIME_OUT);
			json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			return json.toString();
		}

		long userId = Long.parseLong(result.obj.toString());

		/* 判断是否是有效邮箱 */
		if (!StrUtil.isEmail(email)) {
			json.put("code", -1);
			json.put("msg", "邮箱格式错误");
			return json.toString();
		}

		/* 判断邮箱是否存在 */
		if (userInfoService.isEailExists(email)) {
			json.put("code", -2);
			json.put("msg", "邮箱已存在");
			return json.toString();
		}

		/* 用户Id进行加密 */
		String userSign = Security.addSign(userId, Constants.USER_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);

		/* 邮箱进行加密 */
		String emailStr = Security.addEmailSign(email, Constants.MSG_EMAIL_SIGN, ConfConst.ENCRYPTION_KEY_DES);

		/* 获取修改邮箱URL */
		String url = baseUrl + "loginAndRegiste/confirmactiveemail.html?su=" + userSign + "&se=" + emailStr;

		/* 用户名 */
		String userName = userInfoService.findUserInfoByUserId(userId).name;

		/* 发送邮件激活 */
		result = noticeService.sendReBindEmail(email, userName, url);
		if (result.code < 1) {
			LoggerUtil.error(true, result.msg);
			json.put("code", -3);
			json.put("msg", result.msg);
			return json.toString();
		}

		json.put("code", 1);
		json.put("msg", "绑定邮箱成功");
		return json.toString();
	}

	/**
	 * 实名认证准备
	 * 
	 * @param parameters
	 * @return
	 */
	public static String rnAuthPre(Map<String, String> parameters) {
		JSONObject json = new JSONObject();
		String signId = parameters.get("userId");

		ResultInfo result = Security.decodeSign(signId, Constants.USER_ID_SIGN, Constants.VALID_TIME,
				ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			json.put("code", ResultInfo.LOGIN_TIME_OUT);
			json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			return json.toString();
		}

		long userId = Long.parseLong(result.obj.toString());

		if (!UserValidataUtil.checkRealName(userId)) {
			json.put("code", ResultInfo.NOT_REAL_NAME);
			json.put("msg", "请实名认证");

			return json.toString();
		}

		json.put("code", 1);
		json.put("msg", "已实名认证");

		return json.toString();
	}

	/**
	 * 实名认证
	 * 
	 * @param parameters
	 * @return
	 */
	public static String rnAuth(Map<String, String> parameters) {
		JSONObject json = new JSONObject();
		String signId = parameters.get("userId");
		String realName = parameters.get("realName");
		String idNumber = parameters.get("idNumber");

		ResultInfo result = Security.decodeSign(signId, Constants.USER_ID_SIGN, Constants.VALID_TIME,
				ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			json.put("code", ResultInfo.LOGIN_TIME_OUT);
			json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			return json.toString();
		}

		long userId = Long.parseLong(result.obj.toString());

		if (StringUtils.isBlank(realName)) {
			json.put("code", -1);
			json.put("msg", "真实姓名不能为空");

			return json.toString();
		}

		if (StringUtils.isBlank(idNumber)) {
			json.put("code", -2);
			json.put("msg", "身份证号码不能为空");

			return json.toString();
		}

		if (!"".equals(IDCardValidate.chekIdCard(0, idNumber))) {
			json.put("code", -3);
			json.put("msg", "请输入正确的身份证号");

			return json.toString();

		}

		if (userInfoService.isIdNumberExists(idNumber)) {
			json.put("code", -4);
			json.put("msg", "该身份证号码已经存在");

			return json.toString();
		}

		if (realName.length() > 20) {
			json.put("code", -5);
			json.put("msg", "真实姓名不能超过二十个字符");

			return json.toString();
		}

		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		userInfo.reality_name = realName;
		userInfo.id_number = idNumber;

		result = userInfoService.updateUserInfo(userInfo);

		if (result.code < 1) {
			LoggerUtil.error(true, "用户实名认证时：%s", result.msg);

			json.put("code", -6);
			json.put("msg", result.msg);

			return json.toString();
		}

		json.put("code", 1);
		json.put("msg", "实名认证成功");

		return json.toString();
	}

	/**
	 * 查询城市列表
	 * 
	 * @param parameters
	 * @return
	 */
	public static String queryCity(Map<String, String> parameters) {
		JSONObject json = new JSONObject();
		String signId = parameters.get("userId");
		String provinceId = parameters.get("provinceId");

		ResultInfo result = Security.decodeSign(signId, Constants.USER_ID_SIGN, Constants.VALID_TIME,
				ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			json.put("code", ResultInfo.LOGIN_TIME_OUT);
			json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			return json.toString();
		}

		return userAppService.queryCity(provinceId);
	}

	public static String bosAcctActivate(Map<String, String> parameters) {
		JSONObject json = new JSONObject();
		String signId = parameters.get("userId");
		String deviceType = parameters.get("deviceType");// 设备类型;

		ResultInfo result = Security.decodeSign(signId, Constants.USER_ID_SIGN, Constants.VALID_TIME,
				ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			json.put("code", ResultInfo.LOGIN_TIME_OUT);
			json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			return json.toString();
		}

		if (DeviceType.getEnum(Convert.strToInt(deviceType, -99)) == null) {
			json.put("code", -1);
			json.put("msg", "请使用移动客户端操作");
			return json.toString();
		}

		long userId = Long.parseLong(result.obj.toString());

		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		if (userFund == null) {
			json.put("code", -1);
			json.put("msg", "获取用户信息失败");
			return json.toString();
		}

		if (StringUtils.isBlank(userFund.payment_account)) {
			json.put("code", ResultInfo.NOT_PAYMENT_ACCOUNT);
			json.put("msg", "您还没有进行资金存管开户!");
			return json.toString();
		}

		if (userFund.is_actived) {
			json.put("code", -1);
			json.put("msg", "已激活");
			return json.toString();
		}
		
		String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.BOSACCTACTIVATE);
		
		result = PaymentProxy.getInstance().bosAcctActivate(Convert.strToInt(deviceType, -99), serviceOrderNo, userId);
		
		json.put("code", result.code);
		json.put("msg", result.msg);
		if(result.code == 1) {
			json.put("html", result.obj.toString());
		}
		return json.toString();
		
	}
	
	/**
	 * 存管换绑卡
	 * @param parameters
	 * @return
	 */
	public static String quickBinding(Map<String, String> parameters) {
		JSONObject json = new JSONObject();
		String signId = parameters.get("userId");
		String deviceType = parameters.get("deviceType");// 设备类型;
		
		ResultInfo result = Security.decodeSign(signId, Constants.USER_ID_SIGN, Constants.VALID_TIME,
				ConfConst.ENCRYPTION_APP_KEY_DES);
		
		if (result.code < 1) {
			json.put("code", ResultInfo.LOGIN_TIME_OUT);
			json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			return json.toString();
		}
		
		long userId = Long.parseLong(result.obj.toString());
		
		if (DeviceType.getEnum(Convert.strToInt(deviceType, -99)) == null) {
			json.put("code", -1);
			json.put("msg", "请使用移动客户端操作");

			return json.toString();
		}
		
		String mobile = parameters.get("mobile");
		String cardId = parameters.get("cardId");
		String bankId = parameters.get("bankId");
		String provId = parameters.get("provId");
		String areaId = parameters.get("areaId");
		String orginSmsCode = parameters.get("orginSmsCode");
		String smsCode = parameters.get("smsCode");

		if (StringUtils.isBlank(orginSmsCode)) {
			json.put("code", -1);
			json.put("msg", "原绑定卡短信验证码不能为空");
			return json.toString();
		}
		
		if (StringUtils.isBlank(cardId) || cardId.length() < 16) {
			json.put("code", -1);
			json.put("msg", "请填写正确的银行卡号");
			return json.toString();
		}
		
		List<t_bank_card_user> bankCards = bankCardUserService.queryCardByUserId(userId);
		
		if(bankCards == null || bankCards.isEmpty()) {
			json.put("code", -1);
			json.put("msg", "未绑定过银行卡");
			return json.toString();
		}
		
		if(cardId.equals(bankCards.get(0).account)) {
			json.put("code", -1);
			json.put("msg", "旧卡与新卡相同");
			return json.toString();
		}
		
		Object orgin = Cache.get(bankCards.get(0).mobile + BusiType.ORGIN_BIND.value);

		if (orgin == null) {
			json.put("code", -1);
			json.put("msg", "原绑定卡短信验证码已失效");
			return json.toString();
		}
		
		String orginSmsSeq = orgin.toString();
		
		if (StringUtils.isBlank(mobile)) {
			json.put("code", -1);
			json.put("msg", "请填写手机号");
			return json.toString();
		}

		/* 验证手机号是否符合规范 */
		if (!StrUtil.isMobileNum(mobile)) {
			json.put("code", -1);
			json.put("msg", "手机号不符合规范");
			return json.toString();
		}

		Bank bank = Bank.getBank(bankId);

		if (bank == null) {
			json.put("code", -1);
			json.put("msg", "银行代号错误");
			return json.toString();
		}

		Province province = Province.getProvByCode(provId);

		if (province == null) {
			json.put("code", -1);
			json.put("msg", "省份代号错误");
			return json.toString();
		}

		Area area = Area.getAreaByCode(areaId);

		if (area == null) {
			json.put("code", -1);
			json.put("msg", "城市代号错误");
			return json.toString();
		}
		
		if (StringUtils.isBlank(smsCode)) {
			json.put("code", -1);
			json.put("msg", "新绑定卡短信验证码不能为空");
			return json.toString();
		}
		
		Object newSms = Cache.get(mobile + BusiType.NEW_BIND.value);

		if (newSms == null) {
			json.put("code", -1);
			json.put("msg", "原绑定卡短信验证码已失效");
			return json.toString();
		}
		
		String smsSeq = newSms.toString();
		
		String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.QUICKBINDING);
		
		result = PaymentProxy.getInstance().quickBinding(Convert.strToInt(deviceType, -99), serviceOrderNo,
				userId, cardId, bankId, provId, areaId, mobile, smsCode, smsSeq, orginSmsCode + orginSmsSeq);
		

		json.put("code", result.code);
		json.put("msg", result.msg);
		return json.toString();
	}

}