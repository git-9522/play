package controllers.wechat.front;

import org.apache.commons.lang.StringUtils;

import common.utils.Factory;
import common.utils.ResultInfo;
import common.utils.StrUtil;
import play.mvc.Controller;
import services.common.UserInfoService;
import services.common.UserService;

/**
 * 微信wap异步校验控制器
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年5月4日
 */
public class WechatAjaxValideCtrl  extends Controller {

	protected static UserService userService = Factory.getService(UserService.class);
	
	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	
	/**
	 * 手机号码校验
	 * @param mobile 手机号码
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月9日
	 *
	 */
	public static void checkUserMobile(String mobile){
		if (StringUtils.isBlank(mobile)) {
			renderJSON(false);
		}
		
		/* 判断手机号码是否有效 */
		if (!StrUtil.isMobileNum(mobile)) {
			renderJSON(false);
		}
		
		/* 判断手机号码是否存在 */
		if (userService.isMobileExists(mobile)) {
			renderJSON(false);
		}
		
		renderJSON(true);
	}
	
	/**
	 * 判断手机号码是否存在
	 * @param mobile
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月11日
	 *
	 */
	public static void userMobileExists(String mobile){
		if (StringUtils.isBlank(mobile)) {

			renderJSON(false);
		}
		
		/* 判断手机号码是否存在 */
		if (!userService.isMobileExists(mobile)) {
			renderJSON(false);
		}
		
		renderJSON(true);
	}
	
	/**
	 * 判断邀请码是否存在
	 * @param recommendCode 邀请码(推广码,目前是会员的id)
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月9日
	 *
	 */
	public static void checkRecommendCode(String recommendCode){
		if (StringUtils.isBlank(recommendCode)) {
			renderJSON(false);
		}
		boolean flag = false;
		if (StrUtil.isMobileNum(recommendCode)) {
			/* 判断手机号码是否存在 */
			if (common.constants.ModuleConst.EXT_CPS) {
				flag = userService.isMobileExists(recommendCode);
			}
		}else {
			if (common.constants.ModuleConst.EXT_WEALTHCIRCLE) {
				service.ext.wealthcircle.WealthCircleService wealthCircleService = Factory.getService(service.ext.wealthcircle.WealthCircleService.class);
				ResultInfo result = wealthCircleService.isWealthCircleCodeUseful(recommendCode);
				if(result.code == 1){
					flag = true;
				}
			}
		}
		
		renderJSON(flag);
	}
	
	/**
	 * 校验邮箱是否存在(存在返回false，否则返回true)
	 *
	 * @param email 待校验的邮箱
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年5月11日
	 */
	public static void checkEmail(String email){
		if (userInfoService.isEailExists(email)) {
			
			renderJSON(false);
		}
        
    	renderJSON(true);
	}
	
	/**
	 * 校验汇付用户号是否存在(存在返回false，否则返回true)
	 */
	public static void checkHfName(String hfName) {
		ResultInfo result = userInfoService.checkHfname(hfName);
		if(result.code == 1) {
			renderJSON(true);
		} else {
			renderJSON(false);
		}
	}
	
}