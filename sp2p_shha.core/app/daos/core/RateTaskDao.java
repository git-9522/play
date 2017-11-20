package daos.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daos.base.BaseDao;
import models.common.entity.t_user;
import models.core.entity.t_addrate_task;
import models.core.entity.t_addrate_task.TaskStatus;

/**
 * 加息券任务表Dao
 * 
 * @author pc
 *
 */
public class RateTaskDao extends BaseDao<t_addrate_task> {

	protected RateTaskDao() {}
	
	/**
	 * 查询未发放的加息券的任务
	 * 
	 * @return
	 */
	public t_addrate_task queryUnSendTask() {
		String querySQL = "select id, name, identification, total_number, is_send_number, last_user_id,current_user_id, status, "
				+ "send_type, user_id_str, addrate_id from t_addrate_task where status=:status";
		
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("status", TaskStatus.EXECUTE.code);
		
		return findBySQL(querySQL, args);
	}
	
	/**
	 * 查询分批次发送用户列表
	 * 
	 * @return
	 */
	public List<t_user> findUserList(long currtId, long taskId) {
		String sql = "SELECT tu.* FROM  t_user tu, t_addrate_task tat where tu.id >:currtId AND tat.id =:taskId AND tu.id <= tat.last_user_id AND tat.current_user_id < tat.last_user_id limit 1000";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("currtId", currtId);
		condition.put("taskId", taskId);
		return findListBeanBySQL(sql, t_user.class, condition);
	}

	
	/**
	 * 更新群发加息券任务的数量
	 * 
	 * @param taskId 任务id
	 */
	public int updateRateTaskCount(long taskId) {
		String updateSQL = "UPDATE t_addrate_task SET is_send_number = is_send_number +1 where id=:taskId and total_number >= is_send_number+1";
		
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("taskId", taskId);
		
		return updateBySQL(updateSQL, args);
	}

	/**
	 * 更新加息券任务
	 *
	 * @param current_user_id 当前发放任务中最后的Id
	 * @return
	 *
	 */
	public int updateRateTaskUser(long current_user_id, long taskId) {
		String sql = "update t_addrate_task set current_user_id = :current_user_id where id = :id";
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("current_user_id", current_user_id);
		args.put("id", taskId);
		
		return super.updateBySQL(sql, args);
	}

	/**
	 * 
	 * 更新加息券任务状态
	 * @param id 任务Id
	 * @return
	 *
	 */
	public int updateRateTaskStatus(Long taskId) {
		String sql = "UPDATE t_addrate_task SET status=:status where id=:taskId and is_send_number = total_number";
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("taskId", taskId);
		args.put("status", TaskStatus.COMPLETE.code);
		
		return super.updateBySQL(sql, args);
	}
	
	
	
	
	
	
}
