package daos.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daos.base.BaseDao;
import models.core.entity.t_red_packet_sending;

/**
 * 群发红包定时任务Dao
 * @author yanpengfei
 * @createDate 2016年12月15日
 */
public class RedPacketSendingDao extends BaseDao<t_red_packet_sending> {

	protected RedPacketSendingDao() {}
	
	/**
	 * 查询未发放的红包
	 * 
	 * @return
	 */
	public List<t_red_packet_sending> queryUnSendRedPacket() {
		String querySQL = "select id, user_id, time, name, amount, use_rule,bid_period, end_time, "
		+ "is_send_letter, is_send_email, is_send_sms, is_send, send_time, task_sign from t_red_packet_sending where is_send=0 and send_time is null limit 1000";
		
		return findListBySQL(querySQL, null);
	}
	
	/**
	 * 更新群发红包的状态
	 * 
	 * @param sendId 群发红包id
	 */
	public int updateRedPacketSendStatus(long sendId) {
		String updateSQL = "update t_red_packet_sending set is_send=1, send_time=now() where id=:sendId";
		
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("sendId", sendId);
		
		return updateBySQL(updateSQL, args);
	}
	
}
