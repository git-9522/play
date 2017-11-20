package daos.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daos.base.BaseDao;
import models.core.entity.t_cash_sending;

/**
 * 群发现金券定时任务Dao
 * @author yanpengfei
 * @createDate 2016年12月15日
 */
public class CashSendingDao extends BaseDao<t_cash_sending> {

	protected CashSendingDao() {}
	
	/**
	 * 查询未发放的现金券
	 * 
	 * @return
	 */
	public List<t_cash_sending> queryUnSendCash() {
		String querySQL = "select id, user_id, time, amount, use_rule, bid_period, end_time, is_send_letter, is_send_email, is_send_sms, is_send, send_time, task_sign "
				+ "from t_cash_sending where is_send=0 and send_time is null limit 1000";
		
		return findListBySQL(querySQL, null);
	}
	
	/**
	 * 更新群发现金券的状态
	 * 
	 * @param sendId 群发现金券id
	 */
	public int updateCashSendStatus(long sendId) {
		String updateSQL = "update t_cash_sending set is_send=1, send_time=now() where id=:sendId";
		
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("sendId", sendId);
		
		return updateBySQL(updateSQL, args);
	}
	
}
