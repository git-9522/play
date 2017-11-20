package daos.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import common.constants.Constants;
import common.utils.PageBean;
import daos.base.BaseDao;
import models.core.bean.CashRecord;
import models.core.entity.t_cash_user;
import models.core.entity.t_red_packet_user;

/**
 * 用户现金券dao
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年3月7日
 */
public class CashUserDao extends BaseDao<t_cash_user> {
	
	protected CashUserDao() {}
	
	public PageBean<CashRecord> pageOfUserCash(int showType, 
			int currPage, int pageSize, int exports, String userName, 
			int orderType, int orderValue) {
		StringBuffer querySQL = new StringBuffer("SELECT cash.id AS id, user.name AS name, cash.time AS time, cash.use_rule AS employ_rule, IFNUll(invest.amount, 0.00) AS invest_amount, cash.status AS status, cash.end_time AS end_time, cash.amount AS amount, cash.cash_id AS cash_id, cash.type AS type FROM ( t_cash_user cash LEFT JOIN t_user user ON cash.user_id = user.id) LEFT JOIN t_invest invest ON cash.id = invest.cash_id WHERE 1 = 1 ");
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(1) FROM ( t_cash_user cash LEFT JOIN t_user user ON cash.user_id = user.id) LEFT JOIN t_invest invest ON cash.id = invest.cash_id WHERE 1 = 1 ");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		
		switch (showType) {
			case 1:{//未使用
				querySQL.append(" and  status = :status1 ");
				countSQL.append(" and  status = :status1 ");
				condition.put("status1", t_cash_user.CashStatus.UNUSED.code);
				break;
			}
			case 2:{//已使用
				querySQL.append(" and  status = :status2 ");
				countSQL.append(" and  status = :status2 ");
				condition.put("status2", t_cash_user.CashStatus.USED.code);
				break;
			}
			case 3:{//已过期
				querySQL.append(" AND NOW() > end_time ");
				countSQL.append(" AND NOW() > end_time ");
				break;
			}
			default:{//所有
				break;
			}
		}
		
		//按借款人姓名模糊查询
		if (StringUtils.isNotBlank(userName)) {
			querySQL.append(" AND name LIKE :userName ");
			countSQL.append(" AND name LIKE :userName ");
			condition.put("userName", "%" + userName + "%");
		}
		
		if (orderType == 2) {
			querySQL.append(" ORDER BY time ");
			
			if (orderValue == 0) {
				querySQL.append(" DESC ");
			}
		} else {
			querySQL.append(" ORDER BY id ");
			
			if (orderValue == 0) {
				querySQL.append(" DESC ");
			}
		}
		
		if (exports == Constants.EXPORT) {
			PageBean<CashRecord> page = new PageBean<CashRecord>();
			page.page = findListBeanBySQL(querySQL.toString(), CashRecord.class, condition);
			
			return page;
		}
		
		return pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), CashRecord.class, condition);
	}
	
	/**
	 * 通过到期时间查询所有满足过期条件的现金券
	 * 
	 * @return
	 */
	public List<t_cash_user> findAllExpiredCashByEndTime() {
		
		return super.findListByColumn("end_time<=now() and status=0");
	}
	
	/**
	 * 将所有满足过期条件的现金券状态更新
	 * 
	 * @return
	 */
	public int updateAllExpiredCashStatus() {
		String updateSQL = "update t_cash_user set status=3 where id in (select * from (select id from t_cash_user where end_time<=now() and status=0) temp)";
		
		return super.updateBySQL(updateSQL, null);
	}
	
	/**
	 * 修改用户现金券状态
	 * 
	 * @param cashId 
	 * @param oldStatus 
	 * @param newStatus 
	 */
	public int updateUserCashStatus(long cashId, int oldStatus, int newStatus) {
		String sql = " update t_cash_user set status = :newStatus where id = :cashId and status = :oldStatus ";
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("newStatus", newStatus);
		map.put("cashId", cashId);
		map.put("oldStatus", oldStatus);
		
		return super.updateBySQL(sql, map);
	}
	
	/**
	 * 锁定现金券
	 * 
	 * @param cashId 
	 * @param oldStatus 
	 * @param newStatus 
	 */
	public int updateUserCashLockTime(long cashId, int oldStatus, int newStatus) {
		String sql = " update t_cash_user set status = :newStatus, lock_time = now() where id = :cashId and status = :oldStatus ";
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("newStatus", newStatus);
		map.put("cashId", cashId);
		map.put("oldStatus", oldStatus);
		
		return super.updateBySQL(sql, map);
	}
	
	/**
	 * 将锁定过了24小时的现金券进行解锁
	 * 
	 * @param oldStatus 
	 * @param newStatus 
	 */
	public int updateUserCashLockStatus(int oldStatus, int newStatus) {
		String sql = " update t_cash_user set status = :newStatus, lock_time = null  where  status = :oldStatus and ADDDATE(NOW(),INTERVAL -20 MINUTE) >= lock_time  ";
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("newStatus", newStatus);
		map.put("oldStatus", oldStatus);
		
		return super.updateBySQL(sql, map);
	}
	
	/**
	 * 查询用户可以使用的现金券
	 * 
	 * @param userId
	 * @param amount
	 * @return
	 */
	public List<t_cash_user> findCanUseCash(long userId, double amount, int bidPeriod) {
		String sql = "select * from t_cash_user where user_id = :userId and use_rule <= :amount and ( case when :bidPeriod > 0 then bid_period = :bidPeriod or bid_period = 0 ELSE bid_period = 0 end ) and status = :status and end_time > now() order by use_rule desc, end_time desc, id desc";
		
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("userId", userId);
		args.put("amount", amount);
		args.put("bidPeriod", bidPeriod);
		args.put("status", t_red_packet_user.RedpacketStatus.UNUSED.code);
		
		return super.findListBySQL(sql, args);
		
	}
	
	/**
	 * 统计用户现金券数量
	 * 
	 * @param userId
	 * @param status
	 * @return
	 */
	public int countUserCash(long userId, int status) {
		
		return super.countByColumn("user_id = ? and status = ?", userId, status);
	}
	
}
