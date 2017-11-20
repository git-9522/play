package services.common;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.shove.Convert;

import common.constants.ConfConst;
import common.constants.Constants;
import common.enums.Client;
import common.enums.Gender;
import common.enums.NoticeScene;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.ResultInfo;
import daos.common.UserDao;
import daos.common.UserInfoDao;
import models.common.entity.t_user;
import models.common.entity.t_user_info;
import services.base.BaseService;

public class UserInfoService extends BaseService<t_user_info> {
	
	protected UserInfoDao userInfoDao = Factory.getDao(UserInfoDao.class);
	
	protected static UserDao userDao = Factory.getDao(UserDao.class);
	
	protected static UserService userService = Factory.getService(UserService.class);
	
	protected static NoticeService noticeService = Factory.getService(NoticeService.class);
	
	protected UserInfoService(){
		super.basedao = userInfoDao;
	}

	/**
	 * 判断一个邮箱是否已经存在，存在则返回true，不存在返回false
	 * @param email
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月25日
	 */
	public boolean isEailExists(String email) {
		t_user_info userInfo = userInfoDao.findUserInfoByEmail(email);
		if (userInfo != null) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * 获取用户信用等级
	 *
	 * @param userId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月14日
	 */
	public double findUserCreditLine(long userId){
		
		return userInfoDao.findUserCreditLine(userId);
	}
	
	/**
	 * 根据user_id查询t_user_info
	 * 
	 * @param userId
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月22日
	 */
	public t_user_info findUserInfoByUserId(long userId) {
		
		return userInfoDao.findByColumn("user_id = ?", userId);
	}
	
	/**
	 * 查询相应时间理财会员数目的数据
	 * 
	 * @param startTime
	 * @param endTime
	 * @param type
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月23日
	 *
	 */
	public int findFinancialUserCount(String startTime, String endTime, int type) {

		return userInfoDao.findFinancialUserCount(startTime, endTime, type);
	}
	
	/**
	 * 获取用户基本信息编辑进度
	 * 
	 * @param userId
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月29日
	 *
	 */
	public int findUserBasicSchedule(long userId){
		
		return userInfoDao.findUserBasicSchedule(userId);
	}
	
	/**
	 * 查询接受消息的所有用户的user_id
	 * 
	 * @param memberType 会员类型  -1-全部会员  0-新会员   1-理财会员    2-借款会员
	 * @param scene 通知场景
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年4月5日
	 *
	 */
	public List<Map<String, Object>> queryUserNoticeMsgSetting(int memberType,NoticeScene scene) {	
		
		return userInfoDao.queryUserNoticeMsgSetting(memberType, scene);
	}
	
	/**
	 * 查询接受短信的所有用户的mobile
	 * 
	 * @param memberType 会员类型  -1-全部会员  0-新会员   1-理财会员    2-借款会员
	 * @param scene 通知场景
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年4月5日
	 *
	 */
	public List<Map<String, Object>> queryUserNoticeSmsSetting(int memberType,NoticeScene scene){	
		
		return userInfoDao.queryUserNoticeSmsSetting(memberType, scene);
	}
	
	/**
	 * 查询接受邮件的所有用户的邮件地址
	 * 
	 * @param memberType 会员类型  -1-全部会员  0-新会员   1-理财会员    2-借款会员
	 * @param scene 通知场景
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年4月5日
	 *
	 */
	public List<Map<String, Object>> queryUserNoticeEmailSetting(int memberType,NoticeScene scene) {
		
		return userInfoDao.queryUserNoticeEmailSetting(memberType, scene);
	}
	
	/**
	 * 添加用户基本信息
	 * 
	 * @param userId 用户id
	 * @param client 注册入口
	 * @param mobile 手机号码
	 * @param name 用户名
	 * @return
	 * 
	 * @author Chenzhipeng
	 * @createDate 2015年12月19日
	 */
	public boolean addUserInfo(long userId, Client client, String mobile, String name) {
		t_user_info userInfo = new t_user_info();
		userInfo.user_id = userId;
		userInfo.setClient(client.PC);
		userInfo.mobile = mobile;
		userInfo.name = name;
		userInfo.credit_line = Convert.strToDouble(ConfConst.CREDIT_LINE, 0.00);
		userInfo.credit_score = Convert.strToInt(ConfConst.CREDIT_SCORE, 0);
		userInfo.photo = "/public/common/defaultUser.png";
		
		return userInfoDao.save(userInfo);
	}
	
	/**
	 * 修改用户邮箱地址
	 * 
	 * @param userId
	 * @param email
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月22日
	 */
	public ResultInfo updateUserEmail(long userId, String email) {
		ResultInfo result = new ResultInfo();
		
		t_user_info userInfo = this.findUserInfoByUserId(userId);
		if (userInfo == null) {
			result.code = -1;
			result.msg = "查询用户信息失败";
			
			return result;
		}
		
		/* 判断邮箱是否被其他用户绑定 */
		if (isEailExists(email) && email.equals(userInfo.email)) {
			result.code = -1;
			result.msg = "邮箱已被其他用户绑定";
			
			return result;
		}
		
		int i = userInfoDao.updateUserEmail(userId, email);
		if (i < 0) {
			result.code = -1;
			result.msg = "邮箱修改失败";
			return result;
		}
		
		/** 发送通知  */
		Map<String, Object> noticeParams = new HashMap<String, Object>();
		noticeParams.put("user_name", userInfo.name);
		noticeService.sendSysNotice(userId, NoticeScene.BIND_EMAIL_SUCC, noticeParams);
		
		result.code = 1;
		result.msg = "邮箱修改成功";
		/* 刷新用户缓存信息 */
		userService.flushUserCache(userId);
		
		return result;
	}
	
	/**
	 * 修改用户真实姓名、出生日期、性别
	 * （通过判断身份证号码获取性别和出生日期）
	 * 
	 * @param userId 用户Id
	 * @param realityName 真实姓名
	 * @param idNumber 身份证号码
	 * @return
	 * 
	 * @author Chenzhipeng
	 * @createDate 2015年12月24日
	 */
	public ResultInfo updateUserInfo(t_user_info userInfo) {
		ResultInfo result = new ResultInfo();
		
		if (StringUtils.isBlank(userInfo.reality_name)) {
			result.code = -1;
			result.msg= "真实姓名不能为空";
			
			return result;
		}
		
		if (StringUtils.isBlank(userInfo.id_number)) {
			result.code = -1;
			result.msg= "身份证号码不能为空";
			
			return result;
		}
		/* 通过身份证号码获取出生日期 */
		userInfo.birthday = DateUtil.strToDate(userInfo.id_number.substring(6, 14), "yyyyMMdd");
		/* 通过身份证号码获取性别 */
		int sex = Convert.strToInt(userInfo.id_number.substring(16, 17), 0);
		if(sex % 2 == 0){
			userInfo.setSex(Gender.FEMALE);
		}else{
			userInfo.setSex(Gender.MALE);
		}
		
		boolean isFlag = userInfoDao.save(userInfo);
		if (!isFlag) {
			result.code = -1;
			result.msg= "用户实名认证信息添加失败";
			
			return result;
		}
		result.code = 1;
		result.msg= "用户实名认证信息添加成功";
		
		return result;
	}
	
	/**
	 * 修改用户基本信息
	 * 
	 * @param userId 用户Id
	 * @param education 学历
	 * @param marital  婚姻
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月24日
	 */
	public ResultInfo updateUserBasicInfo(long userId, int education, int marital) {
		ResultInfo result = new ResultInfo();
		
		int i = userInfoDao.updateUserBasicInfo(userId, education, marital);
		if (i < 1) {
			result.code = -1;
			result.msg = "用户信息修改失败";
			
			return result;
		}
		/* 获取当前用户的信息完成进度 */
		int schedule = checkUserBasicSchedule(userId);
		i = userInfoDao.updateUserInfoSchedule(userId, schedule);
		if (i < 1) {
			result.code = -1;
			result.msg = "用户信息修改失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "用户信息修改成功";
		
		return result;
	}
	
	/**
	 * 修改用户资产信息
	 * 
	 * @param userId 用户Id
	 * @param workExperience 工作经验
	 * @param annualIncome 年收入
	 * @param netAsset 净资产
	 * @param car 车产
	 * @param house 房产
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月24日
	 */
	public ResultInfo updateUserAssetsInfo(long userId, int workExperience,
			int annualIncome, int netAsset, int car, int house) {
		ResultInfo result = new ResultInfo();

		int i = userInfoDao.updateUserAssetsInfo(userId, workExperience, annualIncome, netAsset, car, house);
		if (i < 0) {
			result.code = -1;
			result.msg = "用户资产信息修改失败";
			
			return result;
		}
		/* 获取当前用户的信息完成进度 */
		int schedule = checkUserBasicSchedule(userId);
		i = userInfoDao.updateUserInfoSchedule(userId, schedule);
		if (i < 0) {
			result.code = -1;
			result.msg = "用户资产信息修改失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "用户资产信息修改成功";
		
		return result;
	}
	
	/**
	 * 修改用户紧急联系人
	 * 
	 * @param userId 用户Id
	 * @param name 紧急联系人姓名
	 * @param type 紧急联系人类型
	 * @param mobile 紧急联系人手机号码
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月24日
	 */
	public ResultInfo updateEmergencyContact(long userId, String name, int type,
			String mobile) {
		ResultInfo result = new ResultInfo();
		
		/* 判断否是为本人的手机号码 */
		t_user user = userService.findUserById(userId);
		if (user.mobile.equals(mobile)) {
			result.code = -1;
			result.msg = "不能使用本人的手机号码作为紧急联系人手机号";
			
			return result;
		}
		
		int i = userInfoDao.updateEmergencyContact(userId, name, type, mobile);
		if (i < 0) {
			result.code = -1;
			result.msg = "紧急联系人信息修改失败";
			
			return result;
		}
		
		/* 获取当前用户的信息完成进度 */
		int schedule = checkUserBasicSchedule(userId);
		i = userInfoDao.updateUserInfoSchedule(userId, schedule);
		if (i < 0) {
			result.code = -1;
			result.msg = "紧急联系人信息修改失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "紧急联系人信息修改成功";
		
		return result;
	}
	
	/**
	 * 更新会员类型
	 *
	 * @param userId 用户ID
	 * @param memberType 会员类型枚举
	 *
	 * @author yaoyi
	 * @createDate 2015年12月25日
	 */
	public boolean updateUserMemberType(long userId, t_user_info.MemberType memberType) {
		t_user_info userInfo = findUserInfoByUserId(userId);
		if (userInfo == null) {
			LoggerUtil.info(true, "更新会员类型时，用户信息不存在。[userId:%s]", userId);
			
			return false;
		}

		return userInfoDao.updateUserMemberType(userId, memberType, userInfo.getMember_type());
	}

	/**
	 * 修改用户信息中手机号码
	 * 
	 * @param userId
	 * @param mobile
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月7日
	 *
	 */
	public ResultInfo updateUserMobile(long userId, String mobile) {
		ResultInfo result = new ResultInfo();
		int i = userInfoDao.updateUserInfoMobile(userId, mobile);
		if (i < 0) {
			result.code = -1;
			result.msg = "手机号码修改失败";
			return result;
		}
		result.code = 1;
		result.msg ="手机号码修改成功";
		
		return result;
	}
	
	/**
	 * 获取当前用户信息完成进度
	 * 
	 * @param userId
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月29日
	 *
	 */
	private int checkUserBasicSchedule(long userId){
		t_user_info userInfo = findUserInfoByUserId(userId);
		int schedule = 0;
		
		/* 基本信息进度 */
		if(userInfo.getEducation() != null && userInfo.getMarital() != null){
			schedule += 1;
		}
		
		/* 资产信息进度 */
		if(userInfo.getWork_experience() != null && userInfo.getAnnual_income() != null && userInfo.getNet_asset() != null && userInfo.getCar() != null 
					&& userInfo.getHouse() != null){
			schedule += 1;
		}
		
		/* 紧急联系人信息进度 */
		if(userInfo.getEmergency_contact_type() != null && StringUtils.isNotBlank(userInfo.emergency_contact_name) && StringUtils.isNotBlank(userInfo.emergency_contact_mobile)){
			schedule += 1;
		}
		
		return schedule;
	}
	
	
	/**
	 * 更新用户会员信息
	 *
	 * @param userId
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
	 * @return
	 *
	 * @author songjia
	 * @createDate 2016年3月31日
	 */
	public ResultInfo updateUserInformation(String provId,String areaId,String workUnit,double registeredFund,Date startTime,long userId, int education, int marital, int workExperience, int annualIncome, int netAsset,
			int car, int house, int emergencyContactType, String emergencyContactName, String emergencyContactMobile) {
		ResultInfo result = new ResultInfo();
		
		int i = userInfoDao.updateUserInformation(provId,areaId,workUnit,registeredFund,startTime,userId, education, marital, workExperience, annualIncome, netAsset, car, house, emergencyContactType, emergencyContactName, emergencyContactMobile);
		if (i < 0) {
			result.code = -1;
			result.msg = "会员信息修改失败";
			
			return result;
		}
		
		/* 获取当前用户的信息完成进度 */
		int schedule = checkUserBasicSchedule(userId);
		i = userInfoDao.updateUserInfoSchedule(userId, schedule);
		if (i < 0) {
			result.code = -1;
			result.msg = "会员信息修改失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "会员信息修改修改成功";
		
		return result;
	}
	
	/**
	 * 清除用户实名认证信息
	 * 
	 * @param userId 用户ID
	 * @param realityName 真实姓名
	 * @param idNumber 身份证号码
	 * @return
	 *
	 * @author YanPengFei
	 * @createDate 2016年07月07日
	 */
	public ResultInfo updateRealName(long userId, String realityName, String idNumber) {
		ResultInfo result = new ResultInfo();
		
		int i = userInfoDao.updateRealName(userId, realityName, idNumber);
		
		if (i < 1) {
			result.code = -1;
			result.msg = "用户实名信息清除失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "用户实名信息清除成功";
		
		return result;
	}
	
	/**
	 * 判断一个身份证号码是否已经存在，存在则返回true，不存在返回false
	 * @param idNumber
	 * @return
	 *
	 * @author YanPengFei
	 * @createDate 2016年08月01日
	 */
	public boolean isIdNumberExists(String idNumber) {
		t_user_info userInfo = userInfoDao.findUserInfoByIdNumber(idNumber);
		
		if (userInfo != null) {
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * 
	 *  更新抽奖次数控制
	 * 
	 * @param type 1：当天抽奖增加抽奖数次；0：非当天抽奖重置抽奖次数为1；2：非当天抽奖重置抽奖次数为0
	 * @param userId 用户
	 * @param lotteryDay 抽奖日期
	 * @return
	 *
	 * @author jiayijian
	 * @createDate 2017年2月7日
	 *
	 */
	public ResultInfo updateControlLotteryTimes(int type,long userId,Date lotteryDay) {
		
		ResultInfo result = new ResultInfo();
		
		int row = userInfoDao.updateControlLotteryTimes(type, userId, lotteryDay);
		
		if (row < 1) {
			result.code = -1;
			result.msg = "更新抽奖次数控制失败";
			return result;
		}
		
		result.code = 1;
		result.msg = "更新抽奖次数控制成功";
		
		return result;
	}

	public List<t_user_info> queryIdNumbers() {
		return userInfoDao.queryIdNumbers();
	}

	public boolean update(t_user_info userInfo) {
		return userInfoDao.save(userInfo);
	}
	
	public ResultInfo checkHfname(String hfName) {
		ResultInfo result = new ResultInfo();
		
		if(StringUtils.isBlank(hfName)) {
			result.code = -1;
			result.msg = "用户名不能为空";
			return result;
		}
		
		int count = userInfoDao.getHfname(Constants.HF_NAME_PREFIX + hfName);
		if(count > 0) {
			result.code = -1;
			result.msg = "用户名已被占用";
		} else {
			result.code = 1;
			result.msg = "用户名可以使用";
		}
		return result;
	}
	
	
	/**
	 * 查询借款人信息
	 * 
	 * @param bid
	 * @return
	 *
	 * @author yuechuanyang
	 * @createDate 2017年11月1日
	 */
	public t_user_info getBorrowerInfo(long bid){
		return userInfoDao.getBorrowerInfo(bid);
	}
}
