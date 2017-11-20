package daos.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import common.constants.Constants;
import common.utils.PageBean;
import daos.base.BaseDao;
import models.core.bean.RateRecord;
import models.core.entity.t_addrate_user;

/**
 * 用户加息卷记录DAO
 *
 * @description 
 *
 * @author jiayijian
 * @createDate 2017年04月06日
 */
public class RateUserDao extends BaseDao<t_addrate_user>{
	
	protected RateUserDao(){}
	/**
	 * 后台加息券发放记录分页
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
	public PageBean<RateRecord> pageOfUserRate(int showType, 
			int currPage, int pageSize, int exports, String userName, 
			int orderType, int orderValue,String startTime,String endTime) {
		
		StringBuffer querySQL = new StringBuffer("SELECT r.id AS id, u.`name` AS NAME, r.time AS time, r.use_rule AS use_rule, IFNUll(invest.amount, 0.00) AS invest_amount, r.`status` AS STATUS, r.end_time AS end_time, r.rate AS rate, r.send_id AS send_id, r.type AS type FROM ( t_addrate_user r LEFT JOIN t_user u ON r.user_id = u.id ) LEFT JOIN t_invest invest ON r.id = invest.rate_id WHERE 1 = 1 ");
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(1) FROM ( t_addrate_user r LEFT JOIN t_user u ON r.user_id = u.id ) LEFT JOIN t_invest invest ON r.id = invest.rate_id WHERE 1 = 1 ");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		
		switch (showType) {
			case 1:{//未使用
				querySQL.append(" and  r.status = :status1 ");
				countSQL.append(" and  r.status = :status1 ");
				condition.put("status1", t_addrate_user.RateStatus.UNUSED.code);
				break;
			}
			case 2:{//已使用
				querySQL.append(" and  r.status = :status2 ");
				countSQL.append(" and  r.status = :status2 ");
				condition.put("status2", t_addrate_user.RateStatus.USED.code);
				break;
			}
			case 3:{//已过期
				querySQL.append(" AND NOW() > r.end_time ");
				countSQL.append(" AND NOW() > r.end_time ");
				break;
			}
			default:{//所有
				break;
			}
		}
		
		//按借款人姓名模糊查询
		if (StringUtils.isNotBlank(userName)) {
			querySQL.append(" AND u.name LIKE :userName ");
			countSQL.append(" AND u.name LIKE :userName ");
			condition.put("userName", "%" + userName + "%");
		}
		
		if(StringUtils.isNotBlank(startTime)){
			
			querySQL.append(" AND r.time >=:startTime ");
			countSQL.append(" AND r.time >= :startTime ");
			condition.put("startTime", startTime);
		}
		
		if(StringUtils.isNotBlank(endTime)){
			
			querySQL.append(" AND r.time <= :endTime ");
			countSQL.append(" AND r.time <= :endTime ");
			condition.put("endTime", endTime);
		}
		
		if (orderType == 2) {
			querySQL.append(" ORDER BY r.time ");
			
			if (orderValue == 0) {
				querySQL.append(" DESC ");
			}
		} else {
			querySQL.append(" ORDER BY r.id ");
			
			if (orderValue == 0) {
				querySQL.append(" DESC ");
			}
		}
		
		if (exports == Constants.EXPORT) {
			PageBean<RateRecord> page = new PageBean<RateRecord>();
			page.page = findListBeanBySQL(querySQL.toString(), RateRecord.class, condition);
			
			return page;
		}
		
		return pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), RateRecord.class, condition);
	}
	
	/**
	 * 将所有满足过期条件的加息券状态更新
	 * 
	 * @return
	 */
	public int updateAllExpiredRateStatus() {
		
		String updateSQL = "update t_addrate_user set status=:status where id in (select * from (select id from t_addrate_user where end_time<=now() and status=:oldStatus) temp)";
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("status", t_addrate_user.RateStatus.EXPIRED.code);
		args.put("oldStatus", t_addrate_user.RateStatus.UNUSED.code);
		
		return super.updateBySQL(updateSQL, args);
	}
	
	/**
	 * 查询用户可以使用的加息券
	 * 
	 * @param userId
	 * @param amount
	 * @return
	 */
	public List<t_addrate_user> findCanUseRate(long userId, double amount,int bidPeriod) {
		
		String sql = " select * from t_addrate_user where user_id = :userId and use_rule <= :amount and ( case when :bidPeriod > 0 then bid_period = :bidPeriod or bid_period = 0 ELSE bid_period = 0 end ) and status = :status and end_time > now() order by rate desc,use_rule desc, end_time desc, id desc";
		
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("userId", userId);
		args.put("amount", amount);
		args.put("bidPeriod", bidPeriod);
		args.put("status", t_addrate_user.RateStatus.UNUSED.code);
		
		return super.findListBySQL(sql, args);
	}
	
	/**
	 * 修改用户加息券状态
	 * 
	 * @param rateId 
	 * @param oldStatus 
	 * @param newStatus 
	 */
	public int updateUserRateStatus(long rateId, int oldStatus, int newStatus) {
		String sql = " update t_addrate_user set status = :newStatus where id = :rateId and status = :oldStatus ";
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("newStatus", newStatus);
		map.put("rateId", rateId);
		map.put("oldStatus", oldStatus);
		
		return super.updateBySQL(sql, map);
	}
	

	/**
	 * 锁定加息券
	 * @param rateId
	 * @param status
	 * @param endStatus
	 * @return
	 */
	public int updateUserRateLockTime(long rateId , int oldStatus , int newStatus ){
		String sql = " update t_addrate_user set status = :newStatus, lock_time = now() where id = :rateId and status = :oldStatus ";
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("newStatus", newStatus);
		map.put("rateId", rateId);
		map.put("oldStatus", oldStatus);
		
		return super.updateBySQL(sql, map);
	}
	
	/**
	 * 将锁定过了24小时的加息券进行解锁
	 * 
	 * @param oldStatus 
	 * @param newStatus 
	 */
	public int updateUserRateLockStatus(int oldStatus, int newStatus) {
		String sql = " update t_addrate_user set status = :newStatus, lock_time = null  where  status = :oldStatus and ADDDATE(NOW(),INTERVAL -20 MINUTE) >= lock_time  ";
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("newStatus", newStatus);
		map.put("oldStatus", oldStatus);
		
		return super.updateBySQL(sql, map);
	}
}
