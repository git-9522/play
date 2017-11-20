package daos.common;

import java.security.InvalidParameterException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.shove.Convert;

import common.constants.Constants;
import common.enums.NoticeScene;
import daos.base.BaseDao;
import models.common.entity.t_user_info;

/**
 * 用户基本信息dao实现
 * 
 * @description
 *
 * @author ChenZhipeng
 * @createDate 2015年12月21日
 */
public class UserInfoDao extends BaseDao<t_user_info> {
	protected UserInfoDao() {
	}
	
	/**
	 * 查询相应时间理财会员数目的数据
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
		String sql = null;
		String hour = null;
		Map<String, Object> condition = new HashMap<String, Object>();
		if (type == Constants.YESTERDAY) {
			sql = "SELECT COUNT(1) FROM t_user t, t_user_info tui WHERE t.id = tui.user_id AND (tui.member_type =:memberType1 OR tui.member_type =:memberType2) AND TO_DAYS(:nowTime) - TO_DAYS(tui.invest_member_time) = 1 AND HOUR(tui.invest_member_time) <:hour";
			if (endTime.contains(":")) {
				hour = endTime.substring(0, endTime.indexOf(":"));
				if("00".equals(hour)){
					hour = "24";
				}
			}
			
			condition.put("memberType1", t_user_info.MemberType.FINANCIAL_MEMBER.code);
			condition.put("memberType2", t_user_info.MemberType.COMPOSITE_MEMBERS.code);
			condition.put("nowTime", new Date());
			condition.put("hour", hour);
		}else{ 
			sql="SELECT COUNT(1) FROM t_user t, t_user_info tui WHERE t.id = tui.user_id AND (tui.member_type =:memberType1 OR tui.member_type =:memberType2) AND tui.invest_member_time BETWEEN :startTime AND :endTime ";
			condition.put("memberType1", t_user_info.MemberType.FINANCIAL_MEMBER.code);
			condition.put("memberType2", t_user_info.MemberType.COMPOSITE_MEMBERS.code);
			condition.put("startTime", startTime);
			condition.put("endTime", endTime);
		}
		
		return findSingleIntBySQL(sql, 0, condition);
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
		String sql = "SELECT add_base_info_schedule FROM t_user_info WHERE user_id = :userId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);
		int schedule = Convert.strToInt(findSingleBySQL(sql, condition).toString(), 0);
		return schedule;
	}
	
	/**
	 * 获取用户信用额度
	 *
	 * @param userId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月14日
	 */
	public double findUserCreditLine(long userId) {
		
		String sql = "SELECT credit_line FROM t_user_info WHERE user_id=:user_id";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("user_id", userId);
		
		return super.findSingleDoubleBySQL(sql, 0.00, params);
	}

	/**
	 * 根据邮箱查询用户基本信息
	 * 
	 * @param email email地址
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月22日
	 *
	 */
	public t_user_info findUserInfoByEmail(String email) {
		String sql = "SELECT * FROM t_user_info WHERE email =:email";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("email", email);

		return findBySQL(sql, condition);
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
	 *
	 */
	public int updateEmergencyContact(long userId, String name, int type, String mobile) {
		String sql = "UPDATE t_user_info SET emergency_contact_type =:type, emergency_contact_name =:name, emergency_contact_mobile =:mobile WHERE user_id =:userId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("type", type);
		condition.put("name", name);
		condition.put("mobile", mobile);
		condition.put("userId", userId);
		
		return updateBySQL(sql, condition);
	}
	

	/**
	 * 修改用户资产信息
	 * 
	 * @param userId 用户Id
	 * @param workExperience 工作经验
	 * @param annualIncome 年收入
	 * @param netAsset 资产估值
	 * @param car 车产
	 * @param house 房产
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月24日
	 *
	 */
	public int updateUserAssetsInfo(long userId, int workExperience,
			int annualIncome, int netAsset, int car, int house) {
		String sql = "UPDATE t_user_info SET work_experience =:workExperience, annual_income=:annualIncome,net_asset=:netAsset,car=:car,house=:house WHERE user_id =:userId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("workExperience", workExperience);
		condition.put("annualIncome", annualIncome);
		condition.put("netAsset", netAsset);
		condition.put("car", car);
		condition.put("house", house);
		condition.put("userId", userId);

		return updateBySQL(sql, condition);
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
	 *
	 */
	public int updateUserBasicInfo(long userId, int education, int marital) {
		String sql = "UPDATE t_user_info SET education =:education, marital=:marital WHERE user_id =:userId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("education", education);
		condition.put("marital", marital);
		condition.put("userId", userId);

		return updateBySQL(sql, condition);
	}
	
	/**
	 * 修改邮箱地址
	 * 
	 * @param userId 用户id
	 * @param email email地址
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月22日
	 *
	 */
	public int updateUserEmail(long userId, String email) {
		String sql = "UPDATE t_user_info SET email =:email, is_email_verified =:emailVerified WHERE user_id =:userId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("email", email);
		condition.put("emailVerified", true);
		condition.put("userId", userId);

		return updateBySQL(sql, condition);
	}
	
	/**
	 * 修改会员信息的手机号码
	 * @param userId
	 * @param mobile
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月7日
	 *
	 */
	public int updateUserInfoMobile(long userId, String mobile) {
		String sql = "UPDATE t_user_info SET mobile =:mobile WHERE user_id =:userId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("mobile", mobile);
		condition.put("userId", userId);

		return updateBySQL(sql, condition);
	}
	
	/**
	 * 更新用户信息完成进度
	 * @param userId
	 * @param schedule
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月29日
	 *
	 */
	public int updateUserInfoSchedule(long userId, int schedule){
		String sql = "UPDATE t_user_info SET add_base_info_schedule =:schedule WHERE user_id =:userId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("schedule", schedule);
		condition.put("userId", userId);
		
		return updateBySQL(sql, condition);
	}
	

	/**
	 * 更新会员类型
	 *
	 * @param userId 用户ID
	 * @param newMemberType 新类型
	 * @param oldMemberType 旧类型
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月28日
	 */
	public boolean updateUserMemberType(long userId, t_user_info.MemberType newMemberType, t_user_info.MemberType oldMemberType) {
		if (!t_user_info.MemberType.FINANCIAL_MEMBER.equals(newMemberType)
				&& !t_user_info.MemberType.BORROW_MEMBER.equals(newMemberType)) {
			throw new InvalidParameterException("新的会员类型必须是借款会员或者理财会员");
		}
		
		StringBuffer sql = new StringBuffer("UPDATE t_user_info SET member_type = :newMemberType ");
		
		switch (newMemberType) {
		case FINANCIAL_MEMBER:{
			if (t_user_info.MemberType.NEW_MEMBER.equals(oldMemberType)) {  //新会员-》理财会员
				sql.append(", invest_member_time = :time ");
				break;
			}
			if (t_user_info.MemberType.FINANCIAL_MEMBER.equals(oldMemberType) 
					|| t_user_info.MemberType.COMPOSITE_MEMBERS.equals(oldMemberType)) { //已经是理财会员或复合会员
				
				return true;
			} 
			if (t_user_info.MemberType.BORROW_MEMBER.equals(oldMemberType)) { //借款会员-》复合会员
				newMemberType = t_user_info.MemberType.COMPOSITE_MEMBERS;
				sql.append(", invest_member_time = :time ");
				break;
			}
			
			return false;
		}
		case BORROW_MEMBER:{
			if (t_user_info.MemberType.NEW_MEMBER.equals(oldMemberType)) {  //新会员-》借款会员
				sql.append(", loan_member_time = :time ");
				break;
			}
			if (t_user_info.MemberType.BORROW_MEMBER.equals(oldMemberType) 
					|| t_user_info.MemberType.COMPOSITE_MEMBERS.equals(oldMemberType)) { //已经是借款会员或复合会员
				
				return true;
			} 
			if (t_user_info.MemberType.FINANCIAL_MEMBER.equals(oldMemberType)) { //理财会员-》复合会员
				newMemberType = t_user_info.MemberType.COMPOSITE_MEMBERS;
				sql.append(", loan_member_time = :time ");
				break;
			}
			
			return false;
		}
		default:
			return false;
		}
		
		sql.append(" WHERE user_id = :userId");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("newMemberType", newMemberType.code);
		params.put("time", new Date());
		params.put("userId", userId);
		
		int row = updateBySQL(sql.toString(), params);
		
		if (row < 1) {
			
			return false;
		}
		
		return true;
	}

	
	/**
	 * 更新会员信息
	 * 改版添加：
	 * @param provId：省级编号
	 * @param areaId: 市级编号
	 * @param workUnit:工作单位
	 * @param registeredFund：注册资金
	 * @param startTime：成立时间
	 *   =====改版添加完毕
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
	 * @param userId
	 * @return
	 *
	 * @author Songjia
	 * @createDate 2016年3月31日
	 */
	public int updateUserInformation(String provId,String areaId,String workUnit,double registeredFund,Date startTime,long userId, int education, int marital, int workExperience, int annualIncome, int netAsset, int car,
			int house, int emergencyContactType, String emergencyContactName, String emergencyContactMobile) {
		
		String sql = "UPDATE t_user_info SET prov_id=:provId, area_id=:areaId ,work_unit=:workUnit,registered_fund=:registeredFund,education =:education, marital=:marital, work_experience =:workExperience, annual_income=:annualIncome, net_asset=:netAsset, car=:car, house=:house, emergency_contact_type =:emergencyContactType, emergency_contact_name =:emergencyContactName, emergency_contact_mobile =:emergencyContactMobile  ";
		String sql2=" WHERE user_id =:userId";
		Map<String, Object> condition = new HashMap<String, Object>();
		
		condition.put("provId", provId);
		condition.put("areaId", areaId);
		condition.put("workUnit", workUnit);
		condition.put("registeredFund", registeredFund);
		if(startTime!=null){
			sql+=" ,start_time =:startTime ";
		condition.put("startTime", startTime);	
		}
		
		
		condition.put("education", education);
		condition.put("marital", marital);
		condition.put("workExperience", workExperience);
		condition.put("annualIncome", annualIncome);
		condition.put("netAsset", netAsset);
		condition.put("car", car);
		condition.put("house", house);
		condition.put("emergencyContactType", emergencyContactType);
		condition.put("emergencyContactName", emergencyContactName);
		condition.put("emergencyContactMobile", emergencyContactMobile);
		condition.put("userId", userId);
		
		
		

		return updateBySQL(sql+sql2, condition);
	}

	
	/**
	 * 查询接受系统消息的所有用户的user_id
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
		
		if (scene.maskable) { //用户是否可设置个该场景是否接收
			/**
				 SELECT 
				 	tui.user_id,
				 	IFNULL(( SELECT nsu.msg FROM t_notice_setting_user nsu WHERE nsu.user_id = tui.user_id AND nsu.msg=true AND nsu.scene =:scene), 1) AS msg 
				 FROM 
				 	t_user_info tui 
				 WHERE 1=1
			 */
			StringBuffer sql = new StringBuffer("SELECT tui.user_id,IFNULL(( SELECT nsu.msg FROM t_notice_setting_user nsu WHERE nsu.user_id = tui.user_id AND nsu.msg=true AND nsu.scene =:scene), 1) AS msg FROM t_user_info tui WHERE 1=1 ");			
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("scene", scene.code);
			if(memberType > -1 && memberType < 3){
				sql.append(" AND member_type =:member_type ");
				condition.put("member_type", memberType);
			}
			
			return super.findListMapBySQL(sql.toString(), condition);
		}else{
			StringBuffer sql = new StringBuffer("SELECT user_id, 1 AS msg FROM t_user_info WHERE 1=1 ");
			Map<String, Object> condition = new HashMap<String, Object>();
			if(memberType > -1 && memberType < 3){
				sql.append(" AND member_type =:member_type ");
				condition.put("member_type", memberType);
			}
			
			return super.findListMapBySQL(sql.toString(), condition);
		}
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
	public List<Map<String, Object>> queryUserNoticeSmsSetting(int memberType,NoticeScene scene) {
		
		if (scene.maskable) { //用户是否可设置个该场景是否接收
			/**
			SELECT 
				tui.mobile,
				IFNULL(( SELECT nsu.sms FROM t_notice_setting_user nsu WHERE nsu.user_id = tui.user_id AND nsu.sms=true AND nsu.scene =68), 1) AS sms 
			FROM 
				t_user_info tui 
			WHERE 
				tui.mobile<>''
			 */
			StringBuffer sql = new StringBuffer("SELECT tui.mobile,IFNULL(( SELECT nsu.sms FROM t_notice_setting_user nsu WHERE nsu.user_id = tui.user_id AND nsu.sms=true AND nsu.scene =:scene), 1) AS sms FROM t_user_info tui WHERE tui.mobile<>''  ");			
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("scene", scene.code);
			if(memberType > -1 && memberType < 3){
				sql.append(" AND member_type =:member_type ");
				condition.put("member_type", memberType);
			}
			
			return super.findListMapBySQL(sql.toString(), condition);
		}else{
			StringBuffer sql = new StringBuffer("SELECT mobile, 1 AS sms FROM t_user_info WHERE 1=1 ");
			Map<String, Object> condition = new HashMap<String, Object>();
			if(memberType > -1 && memberType < 3){
				sql.append(" AND member_type =:member_type ");
				condition.put("member_type", memberType);
			}
			
			return super.findListMapBySQL(sql.toString(), condition);
		}
	}
	
	/**
	 * 查询接受短信的所有用户的邮件地址
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
		if (scene.maskable) { //用户是否可设置个该场景是否接收
			/**
			 SELECT 
				tui.email,
				IFNULL(( SELECT nsu.email FROM t_notice_setting_user nsu WHERE nsu.user_id = tui.user_id AND nsu.email=true AND nsu.scene =:scene), 1) AS setting 
			 FROM 
				t_user_info tui 
			 WHERE tui.email<>''  		
			 */
			StringBuffer sql = new StringBuffer("SELECT tui.email,IFNULL(( SELECT nsu.email FROM t_notice_setting_user nsu WHERE nsu.user_id = tui.user_id AND nsu.email=true AND nsu.scene =:scene), 1) AS setting FROM t_user_info tui WHERE tui.email<>''  ");			
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("scene", scene.code);
			if(memberType > -1 && memberType < 3){
				sql.append(" AND member_type =:member_type ");
				condition.put("member_type", memberType);
			}
			
			return super.findListMapBySQL(sql.toString(), condition);
		}else{
			StringBuffer sql = new StringBuffer("SELECT email, 1 AS setting FROM t_user_info WHERE email<>'' ");
			Map<String, Object> condition = new HashMap<String, Object>();
			if(memberType > -1 && memberType < 3){
				sql.append(" AND member_type =:member_type ");
				condition.put("member_type", memberType);
			}
			
			return super.findListMapBySQL(sql.toString(), condition);
		}
	}
	
	/**
	 * 根据身份证号码查询用户基本信息
	 * 
	 * @param idNumber 身份证号码
	 * @return
	 *
	 * @author YanPengFei
	 * @createDate 2016年08月01日
	 *
	 */
	public t_user_info findUserInfoByIdNumber(String idNumber) {
		String sql = "SELECT * FROM t_user_info WHERE id_number =:idNumber";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("idNumber", idNumber);

		return findBySQL(sql, condition);
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
	 *
	 */
	public int updateRealName(long userId, String realityName, String idNumber) {
		String sql = "UPDATE t_user_info SET reality_name =:realityName, id_number=:idNumber WHERE user_id =:userId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("realityName", realityName);
		condition.put("idNumber", idNumber);
		condition.put("userId", userId);

		return updateBySQL(sql, condition);
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
	public int updateControlLotteryTimes(int type,long userId,Date lotteryDay) {
		
		String sql = "";
		Map<String, Object> condition = new HashMap<String, Object>();	
		if(type == 1){
			
			sql = "UPDATE t_user_info SET lottery_times = lottery_times + 1,lottery_day = :lotteryDay WHERE user_id =:userId ";
			
			condition.put("userId", userId);
			condition.put("lotteryDay", lotteryDay);
		}else if(type == 2){
			
			sql = "UPDATE t_user_info SET lottery_times = 0,lottery_day = :lotteryDay WHERE user_id =:userId ";
			
			condition.put("userId", userId);
			condition.put("lotteryDay", lotteryDay);
		}else{
			
			sql = "UPDATE t_user_info SET lottery_times = 1,lottery_day = :lotteryDay WHERE user_id =:userId ";
			
			condition.put("userId", userId);
			condition.put("lotteryDay", lotteryDay);
		}

		return updateBySQL(sql, condition);
	}

	public List<t_user_info> queryIdNumbers() {
		String sql = "SELECT i.* FROM t_user_info i LEFT JOIN t_user_fund f ON i.user_id = f.user_id WHERE i.hf_name = '' AND f.payment_account != '' AND i.id_number != ''";
		return findListBySQL(sql, null);
	}
	public int getHfname(String hfName) {
		return countByColumn("hf_name = ?", hfName);
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
		String sql = "SELECT * FROM t_user_info WHERE user_id = (SELECT user_id FROM t_bid WHERE id = :bid)";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("bid", bid);
		return super.findBySQL(sql, params);
	}
}
