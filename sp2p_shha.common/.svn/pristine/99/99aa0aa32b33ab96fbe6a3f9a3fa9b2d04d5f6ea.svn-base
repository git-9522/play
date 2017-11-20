package daos.common;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import common.constants.Constants;
import common.utils.PageBean;
import daos.base.BaseDao;
import models.common.entity.t_deal_user;
import models.common.entity.t_recharge_user;

/**
 * 充值记录dao实现
 * 
 * @description 
 *
 * @author ChenZhipeng
 * @createDate 2015年12月21日
 */
public class RechargeUserDao extends BaseDao<t_recharge_user> {

	protected RechargeUserDao() {}
	
	/**
	 * 获取充值金额数据
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
	public double findTotalRechargeByDate(String startTime, String endTime,
			int type) {
		String sql = null;
		String hour = null;
		Map<String, Object> condition = new HashMap<String, Object>();
		if (type == Constants.YESTERDAY) {
			sql = "SELECT IFNULL(SUM(amount),0.00) FROM t_recharge_user WHERE status =:status AND TO_DAYS(:nowTime) - TO_DAYS(time) = 1 AND HOUR(time) <:hour";
			if (endTime.contains(":")) {
				hour = endTime.substring(0, endTime.indexOf(":"));
				if("00".equals(hour)){
					hour = "24";
				}
			}
			
			condition.put("status", t_recharge_user.Status.SUCCESS.code);
			condition.put("nowTime", new Date());
			condition.put("hour", hour);
		}else{
			sql="SELECT IFNULL(SUM(amount),0.00) FROM t_recharge_user WHERE status =:status AND time>=:startTime AND time <=:endTime ";
			
			condition.put("status", t_recharge_user.Status.SUCCESS.code);
			condition.put("startTime", startTime);
			condition.put("endTime", endTime);
		}
		
		return findSingleDoubleBySQL(sql, 0.00, condition);
	}
	
	/**
	 * 充值记录表
	 * 
	 * @param currPage
	 * @param pageSize
	 * @param userId
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月21日
	 */
	public PageBean<t_recharge_user> pageOfRecharge (int currPage, int pageSize, long userId) {
		
		return super.pageOfByColumn(currPage, pageSize, "user_id = ? ORDER BY id DESC", userId);
	}

	/**
	 * 充值成功，更新相关数据
	 *
	 * @param orderNo
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月8日
	 */
	public int updateRechargeToSuccess(String orderNo) {
		String sql = "UPDATE t_recharge_user SET status=:status, completed_time=:time, summary=:summary WHERE order_no = :orderNo AND status <> :status";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", t_recharge_user.Status.SUCCESS.code);
		params.put("time", new Date());
		params.put("orderNo", orderNo);
		params.put("summary", t_deal_user.OperationType.RECHARGE.value);
		
 		return updateBySQL(sql, params);
	}
}
