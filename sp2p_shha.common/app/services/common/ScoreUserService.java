package services.common;

import java.util.Date;
import java.util.Map;

import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import daos.common.ScoreUserDao;
import models.common.bean.ScoreRecord;
import models.common.bean.UserScoreRecord;
import models.common.entity.t_score_user;
import play.db.jpa.JPA;
import services.base.BaseService;

public class ScoreUserService extends BaseService<t_score_user> {

	protected ScoreUserDao scoreUserDao = null;
	
	protected static UserFundService userFundService = Factory.getService(UserFundService.class);
	
	protected static UserService userService = Factory.getService(UserService.class);
	
	protected ScoreUserService() {
		scoreUserDao = Factory.getDao(ScoreUserDao.class);
		super.basedao = scoreUserDao;
	}
	
	/**
	 * 后台积分记录
	 *
	 * @param showType
	 * @param currPage
	 * @param pageSize
	 * @param exports
	 * @param userName
	 * @param orderType
	 * @param orderValue
	 *
	 * @return
	 */
	public PageBean<ScoreRecord> queryScoreRecordList(int showType, 
			int currPage, int pageSize, int exports, String userName, 
			int orderType, int orderValue) {
		
		return scoreUserDao.pageOfScoreRecord(showType, currPage, pageSize, exports, userName, orderType, orderValue);
	}
	
	/**
	 * 前台用户积分记录
	 *
	 * @param currPage
	 * @param pageSize
	 * @param userId
	 *
	 * @return
	 */
	public PageBean<UserScoreRecord> pageOfUserScoreRecord(int currPage, int pageSize, long userId) {
		
		return scoreUserDao.pageOfUserScoreRecord(currPage, pageSize, userId);
	}
	
	/**
	 * 添加积分记录
	 * 
	 * @param userId
	 * @param score
	 * @param balance
	 * @param operationType
	 * @param summaryParam
	 * @return
	 */
	public boolean addScoreUserInfo(long userId, double score,
			double balance, t_score_user.OperationType operationType,
			Map<String, String> summaryParam) {
		t_score_user scoreUser = new t_score_user();
		scoreUser.time = new Date();
		scoreUser.user_id = userId;
		scoreUser.score = score;
		scoreUser.balance = balance;
		scoreUser.setOperation_type(operationType, summaryParam);

		return scoreUserDao.save(scoreUser);
	}
	
	/**
	 * 添加积分记录（增减）
	 * @param type 操作类型：1-增加积分; 2-减少积分
	 * @param userId 
	 * @param score 积分
	 * @param operationType
	 * @param summaryParam
	 * @return
	 */
	public ResultInfo addScoreRecord(int type,long userId,int score,t_score_user.OperationType operationType,
			Map<String, String> summaryPrams){
		ResultInfo result = new ResultInfo();
		
		if(type < 1|| type > 2){
			
			result.code = -1;
			result.msg = "操作类型错误";
			
			return result;
		}
		
		if(type == 1){
			
			//增加积分
			int rows = userFundService.updateUserScoreBalanceAdd(userId, score);
			if (rows <= 0) {
				
				JPA.setRollbackOnly();
				result.code = -1;
				result.msg = "增加用户可用积分失败";
				
				return result;
			}
		}else{
			
			//减少积分
			int rows = userFundService.updateUserScoreBalanceMinus(userId, score);
			if (rows <= 0) {
				
				JPA.setRollbackOnly();
				result.code = -1;
				result.msg = "减少用户可用积分失败";
				
				return result;
			}
		}
		//及时查询用户的可用积分
		double scoreBalance = userFundService.findUserScoreBalance(userId);
		boolean addDeal = this.addScoreUserInfo(
				userId, 
				score, 
				scoreBalance, 
				operationType, 
				summaryPrams);
		if (!addDeal) {
			JPA.setRollbackOnly();
			result.code = -1;
			result.msg = "添加积分记录失败";
			
			return result;
		} 
		
		result.code = 1;
		result.msg = "添加积分记录成功";
		
		userService.flushUserCache(userId);
		
		return result;
	}
	
}
