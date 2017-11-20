package daos.core;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daos.base.BaseDao;
import models.core.entity.t_addrate_sending;

/**
 * 加息卷批量发放记录DAO
 *
 * @description 
 *
 * @author jiayijian
 * @createDate 2017年04月06日
 */
public class RateSendingDao extends BaseDao<t_addrate_sending>{
	
	protected RateSendingDao(){}
	
	/**
	 * 查询未发放的加息券
	 * 
	 * @return
	 */
	public List<t_addrate_sending> queryUnSendRate() {
		
		String querySQL = "select * from t_addrate_sending where status = :status and send_time is null limit 1000";
		Map<String,Object> args = new HashMap<String,Object>();
		args.put("status", t_addrate_sending.SendStatus.UNSEND.code);
		
		return findListBySQL(querySQL, args);
	}
	
	/**
	 * 更新群发加息券的状态
	 * 
	 * @param sendId 群发加息券id
	 */
	public int updateRateSendStatus(long sendId) {
		String updateSQL = "update t_addrate_sending set status = :status , send_time=:sendTime where id=:sendId";
		
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("sendId", sendId);
		args.put("status", t_addrate_sending.SendStatus.HASSEND.code);
		args.put("sendTime", new Date());
		
		return updateBySQL(updateSQL, args);
	}
}
