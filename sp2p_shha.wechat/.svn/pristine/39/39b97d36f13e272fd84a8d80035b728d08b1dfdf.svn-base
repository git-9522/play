package controllers.wechat.front.account;

import models.common.entity.t_user_info;
import play.mvc.With;
import services.common.UserInfoService;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.shove.Convert;

import common.constants.Constants;
import common.constants.WxPageType;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.ResultInfo;
import common.utils.StrUtil;

import controllers.wechat.WechatBaseController;
import controllers.wechat.interceptor.AccountWxInterceptor;

/**
 * 微信-账户中心-会员信息
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年5月4日
 */
@With({AccountWxInterceptor.class})
public class MyInfoCtrl extends WechatBaseController {
	
	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	
	/**
	 * 前台-账户中心-进入个人信息
	 *
	 *
	 * @author Songjia
	 * @createDate 2016年5月11日
	 */
	public static void toUserInfoPre() {
		long userId = getCurrUser().id;
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		int year=0;
		int month=0;
		if(null!=userInfo&&null!=userInfo.start_time){
			Date time=userInfo.start_time;
			year=Convert.strToInt((time+"").substring(0, 4),0);
			month=time.getMonth();
		}
		
		render(userInfo,year,month);
	}
	
	/**
	 * 修改会员信息
	 *
	 * @param education                 教育状况
	 * @param marital                   婚姻状况
	 * @param workExperience            工作年限
	 * @param annualIncome              年收入
	 * @param netAsset                  资产估值
	 * @param car                       车产状况
	 * @param house                     房产状况
	 * @param emergencyContactType      联系人关系
	 * @param emergencyContactName      联系人姓名
	 * @param emergencyContactMobile    联系人电话
	 *
	 * @author Songjia
	 * @createDate 2016年5月11日
	 */
	public static void saveOrUpdateInformation(String provId,String areaId,String workUnit,double registeredFund,String startTime,int education, int marital, int workExperience, int annualIncome, int netAsset, int car, int house, int emergencyContactType, String emergencyContactName, String emergencyContactMobile) {
		ResultInfo result = new ResultInfo();
		long userId = getCurrUser().id;
		Date newStartTime=null;
		if(StringUtils.isNotBlank(startTime)){
		newStartTime=DateUtil.strToDate(startTime, Constants.DATE_PLUGIN6);
	}
		if (!StrUtil.isMobileNum(emergencyContactMobile)) {
			result.code = -1;
			flash.error("联系人手机号码有误");
			toUserInfoPre();
		}
		
		result = userInfoService.updateUserInformation(provId,areaId, workUnit, registeredFund,newStartTime,userId, education, marital, workExperience, annualIncome, netAsset, car, house, emergencyContactType, emergencyContactName, emergencyContactMobile);
		if (result.code < 1) {
			result.code = -1;
			
			toResultPage(WxPageType.PAGE_COMMUNAL_FAIL, result.msg);
		}
		result.code = 1;
		toResultPage(WxPageType.PAGE_COMMUNAL_SUCC, "会员信息修改成功");
	}
}