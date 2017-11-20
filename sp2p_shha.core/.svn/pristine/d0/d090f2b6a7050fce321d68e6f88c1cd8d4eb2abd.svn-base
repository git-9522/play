package daos.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daos.base.BaseDao;
import models.common.entity.t_user;
import models.core.entity.t_cash_task;
import models.core.entity.t_cash_task.TaskStatus;

/**
 * 现金券任务表Dao
 * 
 * @author pc
 *
 */
public class CashTaskDao extends BaseDao<t_cash_task> {
	
	protected CashTaskDao() {}

	
	/**
	 * 查询未发放现金券的任务
	 * 
	 * @return
	 */
	public t_cash_task queryUnSendTask() {
		String querySQL = "select id, name, identification, total_number, is_send_number, last_user_id,current_user_id, status, "
				+ "send_type, user_id_str, cash_id from t_cash_task where status=:status";
		
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("status", TaskStatus.EXECUTE.code);
		
		return findBySQL(querySQL, args);
	}


	/**
	 * 更新群发现金券任务的数量
	 * 
	 * @param taskId 任务id
	 */
	public int updateCashTaskCount(long taskId) {
		String updateSQL = "UPDATE t_cash_task SET is_send_number = is_send_number +1 where id=:taskId and total_number >= is_send_number+1";
		
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("taskId", taskId);
		
		return updateBySQL(updateSQL, args);
	}


	/**
	 * 更新现金券任务
	 *
	 * @param current_user_id 当前发放任务中最后的Id
	 * @return
	 *
	 */
	public int updateCashTaskUser(long current_user_id, long taskId) {
		String sql = "update t_cash_task set current_user_id = :current_user_id where id = :id";
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("current_user_id", current_user_id);
		args.put("id", taskId);
		
		return super.updateBySQL(sql, args);
	}


	/**
	 * 
	 * 更新现金券任务状态
	 * @param id 任务Id
	 * @return
	 *
	 */
	public int updateCashTaskStatus(Long taskId) {
		String sql = "UPDATE t_cash_task SET status=:status where id=:taskId and is_send_number = total_number";
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("taskId", taskId);
		args.put("status", TaskStatus.COMPLETE.code);
		return super.updateBySQL(sql, args);
	}
	
	
	/**
	 * 查询分批次发送用户列表
	 * 
	 * @return
	 */
	public List<t_user> findUserList(long currtId, long taskId) {
		String sql = "SELECT tu.* FROM  t_user tu, t_cash_task tct where tu.id >:currtId AND tct.id =:taskId AND tu.id <= tct.last_user_id AND tct.current_user_id < tct.last_user_id limit 1000";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("currtId", currtId);
		condition.put("taskId", taskId);
		return findListBeanBySQL(sql, t_user.class, condition);
	}
	
}
