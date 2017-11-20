package daos.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daos.base.BaseDao;
import models.common.bean.SignInUserRecord;
import models.common.entity.t_sign_in_record;

/**
 * 签到记录dao
 *
 * @author YanPengFei
 * @createDate 2017年2月24日
 */
public class SignInRecordDao extends BaseDao<t_sign_in_record> {

	protected SignInRecordDao() {}
	
	/**
	 * 当天是否签到
	 * 
	 * @param userId
	 */
	public t_sign_in_record querySignInToday(long userId) {
		String sql = "select id, time, user_id, sign_in_date, status, number, score, extra_score from t_sign_in_record where user_id = :userId and sign_in_date = current_date()";
		
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("userId", userId);
		
		return super.findBySQL(sql, args);
	}
	
	/**
	 * 查询昨天签到记录
	 * 
	 * @param userId
	 * @return
	 */
	public t_sign_in_record querySignInYesterday(long userId) {
		String sql = "select id, time, user_id, sign_in_date, status, number, score, extra_score from t_sign_in_record where user_id = :userId and sign_in_date = date_sub(current_date(), interval 1 day)";
		
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("userId", userId);
		
		return super.findBySQL(sql, args);
	}
	
	/**
	 * 查询用户签到记录(用于前台签到日历展示)
	 * 
	 * @param userId 
	 * @param currentDate 
	 * @return
	 */
	public List<SignInUserRecord> listOfSignInUserRecord(long userId, String currentDate) {
		String sql = "select id, time, user_id, score, extra_score from t_sign_in_record where user_id = :userId and date_format(time, '%Y-%m') = :currentDate";
		
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("userId", userId);
		args.put("currentDate", currentDate);
		
		return findListBeanBySQL(sql, SignInUserRecord.class, args);
	}
	
}
