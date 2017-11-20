package services.common;

import java.util.Date;
import java.util.List;

import common.utils.Factory;
import common.utils.ResultInfo;
import daos.common.SignInRecordDao;
import models.common.bean.SignInUserRecord;
import models.common.entity.t_sign_in_record;
import services.base.BaseService;

/**
 * 签到记录service
 *
 * @author YanPengFei
 * @createDate 2017年2月24日
 */
public class SignInRecordService extends BaseService<t_sign_in_record> {

	protected SignInRecordDao signInRecordDao = null;
	
	protected SignInRecordService() {
		signInRecordDao = Factory.getDao(SignInRecordDao.class);
		super.basedao = signInRecordDao;
	}
	
	/**
	 * 查询当天签到记录
	 * 
	 * @param userId
	 */
	public t_sign_in_record querySignInToday(long userId) {
		
		return signInRecordDao.querySignInToday(userId);
	}
	
	/**
	 * 查询昨天签到记录
	 * 
	 * @param userId
	 * @return
	 */
	public t_sign_in_record querySignInYesterday(long userId) {
		
		return signInRecordDao.querySignInYesterday(userId);
	}
	
	/**
	 * 添加签到记录
	 * 
	 * @param userId
	 * @param signInDate
	 * @param number
	 * @param score 
	 * @param extraScore 
	 * @return
	 */
	public ResultInfo addSignInRecord(long userId, Date signInDate, int number, double score, double extraScore) {
		ResultInfo result = new ResultInfo();
		
		if (userId <= 0L) {
			result.code = -1;
			result.msg = "用户数据异常";
			
			return result;
		}
		
		if (null == signInDate) {
			result.code = -1;
			result.msg = "签到日期数据异常";
			
			return result;
		}
		
		if (number < 1) {
			result.code = -1;
			result.msg = "连续签到次数数据异常";
			
			return result;
		}
		
		t_sign_in_record record = new t_sign_in_record();
		record.time = new Date();
		record.user_id = userId;
		record.sign_in_date = signInDate;
		record.setStatus(t_sign_in_record.Status.ALREADY_SIGN_IN);
		record.number = number;
		record.score = score;
		record.extra_score = extraScore;
		
		if (!signInRecordDao.save(record)) {
			result.code = -1;
			result.msg = "签到失败，请刷新页面重新签到";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "签到成功";
		
		return result;
	}
	
	/**
	 * 查询用户签到记录(用于前台签到日历展示)
	 * 
	 * @param userId 
	 * @param currentDate 
	 * @return
	 */
	public List<SignInUserRecord> listOfSignInUserRecord(long userId, String currentDate) {
		
		return signInRecordDao.listOfSignInUserRecord(userId, currentDate);
	}
	
}
