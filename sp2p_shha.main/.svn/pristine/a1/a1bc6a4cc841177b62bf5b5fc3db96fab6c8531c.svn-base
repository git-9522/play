package controllers.front.account;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.shove.Convert;

import common.annotation.SimulatedCheck;
import common.constants.Constants;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.ResultInfo;
import common.utils.StrUtil;
import controllers.common.FrontBaseController;
import controllers.common.interceptor.AccountInterceptor;
import controllers.common.interceptor.SimulatedInterceptor;
import models.common.entity.t_corp_info;
import models.common.entity.t_user_info;
import play.mvc.With;
import services.common.CorpInfoService;
import services.common.UserInfoService;

/**
 * 前台-账户中心-会员信息控制器
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2015年12月17日
 */
@With({AccountInterceptor.class,SimulatedInterceptor.class})
public class MyInfoCtrl extends FrontBaseController {

	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);

	protected static CorpInfoService corpInfoService = Factory.getService(CorpInfoService.class);
	
	/**
	 * 前台-账户中心-进入个人信息
	 *
	 *
	 * @author DaiZhengmiao
	 * @throws ParseException 
	 * @createDate 2016年1月7日
	 */
	@SuppressWarnings("deprecation")
	public static void toUserInfoPre() throws ParseException {
		long userId = getCurrUser().id;
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		Date time=userInfo.start_time;
		 int year=-1;
		int month=-1;
		if(time!=null){
			String yearStr=(""+time).substring(0,4);
			year=Convert.strToInt(yearStr, 0);
			month = time.getMonth(); 
		}
		render(userInfo,year,month);
	}

	public static void toCorpInfoPre() {
		long userId = getCurrUser().id;
		
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		
		t_corp_info corpInfo = corpInfoService.queryByUserId(userId);
		
		render(userInfo, corpInfo);
	}
	
	/**
	 * 修改基本信息
	 * 
	 * @description 使用editUserInfomation()方法替代
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月24日
	 * @param education
	 * @param marital
	 */
	@Deprecated
	public static void updateBasicInfo(int education, int marital){
		ResultInfo result = new ResultInfo();
		long userId = getCurrUser().id;
		
		result = userInfoService.updateUserBasicInfo(userId, education, marital);
		if (result.code < 1) {
			result.msg = "基本信息修改失败";
			
			renderJSON(result);
		}
		result.msg = "基本信息修改成功";
		
		renderJSON(result);
	}
	
	/**
	 * 修改资产信息
	 * 
	 * @description 使用editUserInfomation()方法替代
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月24日
	 * @param workExperience
	 * @param annualIncome
	 * @param netAsset
	 * @param car
	 * @param house
	 */
	@Deprecated
	public static void changeAssetsInfo(int workExperience, int annualIncome, int netAsset, int car, int house){
		ResultInfo result = new ResultInfo();
		long userId = getCurrUser().id;
		
		result = userInfoService.updateUserAssetsInfo(userId, workExperience, annualIncome, netAsset, car, house);
		if (result.code < 1) {
			result.msg = "资产信息修改失败";
			
			renderJSON(result);
		}
		result.msg = "资产信息修改成功";
		
		renderJSON(result);
	}
	
	/**
	 * 修改紧急联系人信息
	 * 
	 * @description 使用editUserInfomation()方法替代
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月24日
	 * @param EmergencyContactType
	 * @param EmergencyContactName
	 * @param EmergencyContactMobile
	 */
	@Deprecated
	public static void updateEmergencyContact(String userSign, int emergencyContactType, String emergencyContactName, String emergencyContactMobile){
		ResultInfo result = new ResultInfo();
		long userId = getCurrUser().id;

		if(!StrUtil.isMobileNum(emergencyContactMobile)){
			result.code = -1;
			result.msg = "紧急联系人信息修改失败";
			
			renderJSON(result);
		}
		
		result = userInfoService.updateEmergencyContact(userId, emergencyContactName, emergencyContactType, emergencyContactMobile);
		if (result.code < 1) {
			result.code = -1;
			
			renderJSON(result);
		}
		result.code = 1;
		result.msg = "紧急联系人信息修改成功";
		
		renderJSON(result);
	}
	
	
	/**
	 * 修改会员信息
	 * @description
	 * 
	 * @author songjia
	 * @createDate 2016年03月30日
	 * @param education
	 * @param marital
	 * @param workExperience
	 * @param annualIncome
	 * @param netAsset
	 * @param car
	 * @param house
	 * @param userSign
	 * @param emergencyContactType
	 * @param emergencyContactName
	 * @param emergencyContactMobile
	 */
	@SimulatedCheck
	public static void editUserInfomation(double registeredFund,String startTime,int education, int marital, int workExperience, int annualIncome, int netAsset, int car, int house, int emergencyContactType, String emergencyContactName, String emergencyContactMobile) {
		String provId=params.get("provId");
		String areaId=params.get("areaId");
		String workUnit=params.get("workUnit");
		Date newStartTime=null;
		if(StringUtils.isNotBlank(startTime)){
		newStartTime=DateUtil.strToDate(startTime, Constants.DATE_PLUGIN6);
	}
		
		ResultInfo result = new ResultInfo();
		long userId = getCurrUser().id;

		if(!StrUtil.isMobileNum(emergencyContactMobile)){
			result.code = -1;
			result.msg = "紧急联系人手机号码有误";
			
			renderJSON(result);
		}
		
		result = userInfoService.updateUserInformation(provId,areaId,workUnit,registeredFund,newStartTime,userId, education, marital, workExperience, annualIncome, netAsset, car, house, emergencyContactType, emergencyContactName, emergencyContactMobile);
		if (result.code < 1) {
			result.code = -1;
			
			renderJSON(result);
		}
		result.code = 1;
		result.msg = "会员信息修改成功";
		
		renderJSON(result);
	}
	
}
