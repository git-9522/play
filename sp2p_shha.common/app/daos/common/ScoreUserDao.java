package daos.common;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import common.constants.Constants;
import common.utils.PageBean;
import daos.base.BaseDao;
import models.common.bean.ScoreRecord;
import models.common.bean.UserScoreRecord;
import models.common.entity.t_score_user;

public class ScoreUserDao extends BaseDao<t_score_user> {

	protected ScoreUserDao () {}
	
	/**
	 * 分页查询积分记录
	 * 
	 * @param showType
	 * @param currPage
	 * @param pageSize
	 * @param exports
	 * @param userName
	 * @param orderType
	 * @param orderValue
	 * @return
	 */
	public PageBean<ScoreRecord> pageOfScoreRecord(int showType, 
			int currPage, int pageSize, int exports, String userName, 
			int orderType, int orderValue) {

		StringBuffer querySQL = new StringBuffer("SELECT su.id AS id, u.name AS user_name, su.deal_type AS deal_type, su.time AS time, su.score AS score, su.summary AS summary FROM t_score_user su LEFT JOIN t_user u ON su.user_id = u.id WHERE 1 = 1 ");
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(1) FROM t_score_user su LEFT JOIN t_user u ON su.user_id = u.id WHERE 1 = 1 ");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		
		switch (showType) {
			case 1:{
				
				break;
			}
			case 2:{
				
				break;
			}
			case 3:{
				
				break;
			}
			default:{ //所有
				break;
			}
		}
		
		//按借款人姓名模糊查询
		if (StringUtils.isNotBlank(userName)) {
			querySQL.append(" AND u.name LIKE :userName ");
			countSQL.append(" AND u.name LIKE :userName ");
			condition.put("userName", "%" + userName + "%");
		}
		
		if (orderType == 2) { //借款金额
			querySQL.append(" ORDER BY time ");
			if(orderValue == 0){
				querySQL.append(" DESC ");
			}
		}
		else { //编号
			querySQL.append(" ORDER BY su.id ");
			
			if (orderValue == 0) {
				querySQL.append(" DESC ");
			}
		}
		
		if (exports == Constants.EXPORT) {
			PageBean<ScoreRecord> page = new PageBean<ScoreRecord>();
			page.page = findListBeanBySQL(querySQL.toString(), ScoreRecord.class, condition);
			return page;
		}
		
		return pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), ScoreRecord.class, condition);
	}
	
	
	/**
	 * 分页查询积分记录
	 * 
	 * @param showType
	 * @param currPage
	 * @param pageSize
	 * @param exports
	 * @param userName
	 * @param orderType
	 * @param orderValue
	 * @return
	 */
	public PageBean<UserScoreRecord> pageOfUserScoreRecord(int currPage, int pageSize, long userId) {

		StringBuffer querySQL = new StringBuffer("SELECT su.id AS id, u.name AS user_name, su.deal_type AS deal_type, su.time AS time, su.score AS score, su.summary AS summary FROM t_score_user su LEFT JOIN t_user u ON su.user_id = u.id WHERE 1 = 1 ");
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(1) FROM t_score_user su LEFT JOIN t_user u ON su.user_id = u.id WHERE 1 = 1 ");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		
		querySQL.append(" AND u.id LIKE :userId ");
		countSQL.append(" AND u.id LIKE :userId ");
		condition.put("userId", userId);
		
		querySQL.append(" order by id desc ");
		
		return pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), UserScoreRecord.class, condition);
	}
	
}
