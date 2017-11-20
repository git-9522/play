package daos.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import common.constants.Constants;
import common.utils.PageBean;
import daos.base.BaseDao;
import models.core.bean.RedpacketRecord;
import models.core.entity.t_red_packet_user;

/**
 * 用户红包dao
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年3月7日
 */
public class RedpacketUserDao extends BaseDao<t_red_packet_user> {

	protected RedpacketUserDao() {}

	public PageBean<RedpacketRecord> pageOfUserRedpacket(int showType,
			int currPage, int pageSize, int exports, String userName,
			int orderType, int orderValue) {

		StringBuffer querySQL = new StringBuffer("SELECT red.id AS id ,user.name AS name,red.time AS time,red.use_rule AS employ_rule,IFNUll(invest.amount,0.00) AS invest_amount,red.status AS status,red.end_time AS end_time, red.amount AS amount, red.red_packet_id AS red_packet_id, red.type AS type FROM ( t_red_packet_user red LEFT JOIN t_user user ON red.user_id = user.id) LEFT JOIN t_invest invest ON red.id = invest.red_packet_id WHERE 1 = 1 ");
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(1) FROM ( t_red_packet_user red LEFT JOIN t_user user ON red.user_id = user.id) LEFT JOIN t_invest invest ON red.id = invest.red_packet_id WHERE 1 = 1 ");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		
		switch (showType) {
			case 1:{//未使用
				querySQL.append(" and  red.status = :status1 ");
				countSQL.append(" and  red.status = :status1 ");
				condition.put("status1", t_red_packet_user.RedpacketStatus.UNUSED.code);
				break;
			}
			case 2:{//已使用
				querySQL.append(" and  red.status = :status2 ");
				countSQL.append(" and  red.status = :status2 ");
				condition.put("status2", t_red_packet_user.RedpacketStatus.USED.code);
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
		if(StringUtils.isNotBlank(userName)){
			querySQL.append(" AND user.name LIKE :userName ");
			countSQL.append(" AND user.name LIKE :userName ");
			condition.put("userName", "%"+userName+"%");
		}
		
		if(orderType == 2){ //借款金额
			querySQL.append(" ORDER BY red.time ");
			if(orderValue == 0){
				querySQL.append(" DESC ");
			}
		} 
		else { //编号
			querySQL.append(" ORDER BY red.id ");
			if(orderValue == 0){
				querySQL.append(" DESC ");
			}
		}
		
		if(exports == Constants.EXPORT){
			PageBean<RedpacketRecord> page = new PageBean<RedpacketRecord>();
			page.page = findListBeanBySQL(querySQL.toString(), RedpacketRecord.class, condition);
			return page;
		}
		
		return pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), RedpacketRecord.class, condition);
	}
	
	/**
	 * 通过到期时间查询所有满足过期条件的红包
	 * 
	 * @return
	 */
	public List<t_red_packet_user> findAllExpiredRedPacketByEndTime() {
		
		return super.findListByColumn("end_time<=now() and status=0");
	}
	
	/**
	 * 将所有满足过期条件的红包状态更新
	 * 
	 * @return
	 */
	public int updateAllExpiredRedPacketStatus() {
		String updateSQL = "update t_red_packet_user set status=3 where id in (select * from (select id from t_red_packet_user where end_time<=now() and status=0) temp)";
		
		return super.updateBySQL(updateSQL, null);
	}
	
	/**
	 * 修改用户红包状态
	 * 
	 * @param redPacketId 用户红包ID
	 * @param oldStatus 红包旧状态
	 * @param newStatus 红包新状态
	 */
	public int updateUserRedPacketStatus(long redPacketId, int oldStatus, int newStatus) {
		String sql = " update t_red_packet_user set status = :newStatus where id = :redPacketId and status = :oldStatus ";
		
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("newStatus", newStatus);
		map.put("redPacketId", redPacketId);
		map.put("oldStatus", oldStatus);
		
		return super.updateBySQL(sql, map);
	}
	
	/**
	 * 锁定红包
	 * @param redPacketId
	 * @param status
	 * @param endStatus
	 * @return
	 */
	public int updateRedPacketLockTime(long redPacketId , int status , int endStatus){
		String sql = " update t_red_packet_user set status = :endStatus , lock_time = now() where id = :redPacketId and status = :status " ;
		Map<String,Object> map = new HashMap<String, Object>() ;
		map.put("endStatus",endStatus ) ;
		map.put("redPacketId",redPacketId ) ;
		map.put("status",status ) ;
		
		return super.updateBySQL(sql, map) ;
	}
	
	/**
	 * 将锁定过了24小时的红包进行解锁
	 * 
	 * @param status
	 * @return
	 */
	public int updateRePacketLockStatus( int status , int endStatus ){
		String sql = " update t_red_packet_user r set r.status = :endStatus , r.lock_time = null where r.status = :status and ADDDATE(NOW(),INTERVAL -20 MINUTE) >= r.lock_time ; " ;
		Map<String,Object> map = new HashMap<String, Object>() ;
		map.put("status",status ) ;
		map.put("endStatus",endStatus ) ;
		
		return super.updateBySQL(sql, map) ;
	}
	
	
	/**
	 * 查询用户可以使用的红包
	 * 
	 * @param bidPeriod 红包使用规则:借款期限(月)，0代表无限制 
	 * @param userId
	 * @param amount
	 * @return
	 */
	public List<t_red_packet_user> findCanUseRedPacket(long userId, double amount,int bidPeriod) {
		
		String sql = " select * from t_red_packet_user where user_id = :userId and use_rule <= :amount and ( case when :bidPeriod > 0 then bid_period = :bidPeriod or bid_period = 0 ELSE bid_period = 0 end ) and status = :status and end_time > now() order by amount desc,use_rule desc, end_time desc, id desc";
		
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("userId", userId);
		args.put("amount", amount);
		args.put("bidPeriod", bidPeriod);
		args.put("status", t_red_packet_user.RedpacketStatus.UNUSED.code);
		
		return super.findListBySQL(sql, args);
	}
	
	/**
	 * 统计用户红包数量
	 * 
	 * @param userId
	 * @param status
	 * @return
	 */
	public int countUserRedPacket(long userId, int status) {
		
		return super.countByColumn("user_id = ? and status = ?", userId, status);
	}
	
	/**
	 * 统计用户红包金额
	 * 
	 * @param userId
	 * @param status
	 * @return
	 */
	public double totalUserRedPacket(long userId, int status) {
		String sql = "select ifnull(sum(amount), 0.00) from t_red_packet_user where user_id = :userId and status = :status";
		
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("userId", userId);
		args.put("status", status);
		
		return super.findSingleDoubleBySQL(sql, 0.00, args);
	}

	public int queryDailyGainRedPacketCount(long user_id, int type) {
		String sql = "SELECT COUNT(1) FROM t_red_packet_user WHERE type = :type AND user_id = :user_id AND DATE_FORMAT(time,'%Y-%m-%d') = CURDATE()";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("user_id", user_id);
		params.put("type", type);
		return this.countBySQL(sql, params);
	}
	
}
