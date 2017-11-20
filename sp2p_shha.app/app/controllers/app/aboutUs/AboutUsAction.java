	package controllers.app.aboutUs;

import java.util.Date;
import java.util.Map;

import models.common.entity.t_sign_in_record;
import models.common.entity.t_user_info;
import models.core.bean.FrontBid;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import service.AboutUsService;
import services.common.SettingService;
import services.common.SignInRecordService;
import services.common.UserInfoService;
import services.core.BidService;

import com.shove.Convert;

import common.constants.ConfConst;
import common.constants.Constants;
import common.constants.SettingKey;
import common.enums.DeviceType;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;

/**
 * 更多(4)模块[OPT:4XX]
 *
 * @description 包含公司介绍、获取icon(2)、联系我们(3)，app版本信息等
 *
 * @author DaiZhengmiao
 * @createDate 2016年6月29日
 */
public class AboutUsAction {
	private static AboutUsService aboutAppService = Factory.getService(AboutUsService.class);
	private static SettingService settingService = Factory.getService(SettingService.class);
	private static BidService bidService = Factory.getService(BidService.class);
	private static SignInRecordService signInRecordService = Factory.getService(SignInRecordService.class);
	private static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	
	/***
	 * 
	 * 公司介绍（opt=411）
	 * @param parameters
	 * @return
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-8
	 */
	public static String aboutUs(Map<String, String> parameters){

		return aboutAppService.findAboutUs();
	}
	
	/***
	 * 
	 * 联系我们（opt=402）
	 * @param parameters
	 * @return
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-8
	 */
	public static String contactUs(Map<String, String> parameters){

		return aboutAppService.findContactUs();
	}
	
	/***
	 * 
	 * 平台注册协议（OPT=112）
	 * @return
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-3-31
	 */
	public static String registerProtocol(Map<String, String> parameters){
		
		return aboutAppService.findRegisterProtocol();
	}
	
	/***
	 * 获取APP版本信息
	 *
	 * @param parameters
	 * @return
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-26
	 */
	public static String getPlatformInfo(Map<String, String> parameters) {
        JSONObject json = new JSONObject();
		
		String deviceTypeStr = parameters.get("deviceType");
		
		if (DeviceType.getEnum(Convert.strToInt(deviceTypeStr, -99)) == null) {
			json.put("code", -1);
			json.put("msg", "请使用移动客户端操作");
			
			return json.toString();
		}
		
		int deviceType = Integer.parseInt(deviceTypeStr);
		
		/**ios 版本*/
		String iosVersion = settingService.findSettingValueByKey(SettingKey.IOS_NEW_VERSION);
		
		/**ios 升级类型*/
		String iosType = settingService.findSettingValueByKey(SettingKey.IOS_PROMOTION_TYPE);
		boolean iosPromotionType = (StringUtils.isNotBlank(iosType) && iosType.equals("1")) ? true : false;
		
		/**android 版本*/
		String androidVersion = settingService.findSettingValueByKey(SettingKey.ANDROID_NEW_VERSION);
	
		/**android 升级类型*/
		String androidType = settingService.findSettingValueByKey(SettingKey.ANDROID_PROMOTION_TYPE);
		boolean androidPromotionType = (StringUtils.isNotBlank(androidType) && androidType.equals("1")) ? true : false;

		json.put("version", deviceType == DeviceType.DEVICE_ANDROID.code ? androidVersion : iosVersion);
		json.put("promotionType", deviceType == DeviceType.DEVICE_ANDROID.code ? androidPromotionType : iosPromotionType);
		json.put("apk_path", ConfConst.ANDROID_DOWNLOAD_URL);
		json.put("ios_path", ConfConst.IOS_DOWNLOAD_URL);
		json.put("code", 1);
		json.put("msg", "查询成功");
		return json.toString();
	}
	
	/***
	 * 
	 * 安全保障（OPT=424）
	 * @return
	 * @description 
	 *
	 */
	public static String Guarantee(){
		
		JSONObject json = new JSONObject();
		
		json.put("security", "/wx/home/security.html");
		json.put("careful", "/wx/home/careful.html");
		json.put("saveWorry", "/wx/home/saveWorry.html");
		json.put("intimate", "/wx/home/intimate.html");
		json.put("safely", "/wx/home/safely.html");
		json.put("code", 1);
		json.put("msg", "查询成功");
		
		return json.toString();
	}

	/***
	 * 
	 * 新手指南（OPT=425）
	 * @return
	 * @description 
	 *
	 */
	public static String guide(Map<String, String> parameters){
		
		JSONObject json = new JSONObject();
		
		String signId = parameters.get("userId");
		
		ResultInfo result = Security.decodeSign(signId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			json.put("task", "/wx/home/novicerules.html");
			json.put("isRegister", false);
			json.put("isSignIn", false);
			json.put("isRnAuth", false);
			json.put("code", 1);
			json.put("msg", result.msg);
			return json.toString();
		}
		
		json.put("isRegister", true);
		long userId = Long.parseLong(result.obj.toString());
		
		//判断当天是否签到
		t_sign_in_record record = signInRecordService.querySignInToday(userId);
		if (record == null) {
			json.put("isSignIn", false);
		}else{
			json.put("isSignIn", true);
		}
		
		//判断是否实名认证
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		if (StringUtils.isBlank(userInfo.reality_name) || StringUtils.isBlank(userInfo.id_number)) {
			json.put("isRnAuth", false);
		}else{
			json.put("isRnAuth", true);
		}
		
		json.put("task", "/wx/home/novicerules.html");
		json.put("code", 1);
		json.put("msg", "查询成功");
		
		return json.toString();
	}

	
	/***
	 * 
	 * 新手福利（OPT=428）
	 * @return
	 * @description 
	 *
	 */
	public static String novice(){
		
		JSONObject json = new JSONObject();
		
		//新手投资
		PageBean<FrontBid> pageOfNewbieBis = bidService.pageOfNewbieBidInvest(1, 2);
		Date sysNowTime = new Date();
		
		json.put("code", 1);
		json.put("msg", "成功");
		json.put("newbieBids", pageOfNewbieBis.page);
		json.put("nowTime", sysNowTime);
		
		return json.toString();
	}
	

	
}
