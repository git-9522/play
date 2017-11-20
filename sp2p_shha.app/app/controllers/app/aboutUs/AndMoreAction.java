package controllers.app.aboutUs;

import java.util.HashMap;
import java.util.Map;

import models.common.entity.t_help_center;
import models.common.entity.t_user;
import models.common.entity.t_user_info;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import service.AboutUsService;
import services.common.HelpCenterService;
import services.common.SettingService;
import services.common.UserInfoService;

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
 * @description 更多模块接口
 *
 *
 *
 */
public class AndMoreAction {
	
	private static UserInfoService userInfoService = Factory.getService(UserInfoService.class);

	private static AboutUsService aboutAppService = Factory.getService(AboutUsService.class);
	
	private static HelpCenterService helpCenterService = Factory.getService(HelpCenterService.class);
	
	/***
	 * 
	 * 更多模块（opt=426）
	 * @param parameters
	 * @return
	 * @description 
	 *
	 */
	public static String andMore(Map<String, String> parameters){
		
		JSONObject json = new JSONObject();
		String userIdSign = parameters.get("userId");
		
		ResultInfo userIdSignDecode = Security.decodeSign(userIdSign, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (userIdSignDecode.code < 1) {
			json.put("name", "");
		}else{
			long userId = Long.parseLong(userIdSignDecode.obj.toString());
			t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
			
			json.put("name", userInfo.name);
			json.put("photo", userInfo.photo);
		}
		
		String contactUs = aboutAppService.findContactUs();
		JSONObject obj = JSONObject.fromObject(contactUs);
		
		json.put("tel", obj.get("companyTel"));
		json.put("code", 1);
		json.put("msg", "成功");
		return json.toString();
	}
	
	/***
	 * 
	 * 帮助中心（opt=427）
	 * @param parameters
	 * @return
	 * @description 
	 *
	 */
	public static String goHelp(Map<String, String> parameters){
		
		int currPage = Convert.strToInt(parameters.get("currPage"), 1);
		
		int pageSize = Convert.strToInt(parameters.get("pageSize"), 8);
		
		PageBean<t_help_center> helpList = helpCenterService.pageOfHelpCenterBack(currPage, pageSize, null, null);
		
		Map map = new HashMap<String, Object>();
		map.put("code", 1);
		map.put("msg", "成功");
		map.put("helpList", helpList);
		
		return JSONObject.fromObject(map).toString();
	}
}
