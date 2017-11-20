package daos.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daos.base.BaseDao;
import models.activity.entity.t_invert_11_activity6_log;
import models.core.entity.t_invest;

/**
 * 11月 活动6（DAO）（复投参与）
 * 
 * @Title Invert11Actity6Dao
 * @Description 千里姻缘一线牵、俊男美女任您选
 * @author hjs_djk
 * @createDate 2017年10月23日 下午6:47:58
 */
public class Invert11Actity6Dao extends BaseDao<t_invert_11_activity6_log> {

	public Invert11Actity6Dao() {

	}

	/**
	 * 查询活动时间内的未发放奖励的投资
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List<t_invest> queryList(String startTime, String endTime, Long user_id) {
		String sql = "SELECT * FROM t_invest as ti where ti.time>=:start_time and ti.time<=:end_time and ti.id not in(select t11ac6.invest_id from  t_invert_11_activity6_log as t11ac6) and ti.id !=(SELECT t.id FROM t_invest AS t WHERE user_id=:userId  ORDER BY t.time LIMIT 1) order by ti.time asc";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("start_time", startTime);
		condition.put("end_time", endTime);
		condition.put("userId", user_id);
		return this.findListBeanBySQL(sql, t_invest.class, condition);

	}
	
	
	/**
	 *  查询活动时间内  指定投资记录是否发放奖励
	 * @param startTime
	 * @param endTime
	 * @param user_id
	 * @param id
	 * @return
	 * 	  true  以发放   false 未发放
	 */	
	public boolean checkCount(String startTime, String endTime, Long user_id,Long id) {
		String sql = "SELECT count(*) FROM t_invest as ti where ti.time>=:start_time and ti.time<=:end_time and ti.id not in(select t11ac6.invest_id from  t_invert_11_activity6_log as t11ac6) and ti.id !=(SELECT t.id FROM t_invest AS t WHERE user_id=:userId  ORDER BY t.time LIMIT 1) and ti.id=:id";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("start_time", startTime);
		condition.put("end_time", endTime);
		condition.put("userId", user_id);
		condition.put("id", id);
		return this.findSingleIntBySQL(sql, 0, condition)==0;

	}


	/**
	 * 获取当前用户上次抽到的历史人物
	 * 
	 * @param user_id
	 * @return
	 */
	public t_invert_11_activity6_log findNewLog(Long user_id) {
		String sql = "SELECT * from t_invert_11_activity6_log where user_id=:userId order by time  desc limit 1";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", user_id);
		return this.findBySQL(sql, condition);
	}
	
	/**
	 *  查询投资记录
	 * @param user_id
	 * @return
	 */
	public List<Map<String, Object>> findAllInverstList(String startTime, String endTime,Long user_id) {
		String sql = "SELECT ti.id ,ti.amount,ta.id as logid ,ta.cid FROM t_invest as ti left join t_invert_11_activity6_log as  ta on ti.id=ta.invest_id where ti.user_id=:userId and ti.time>=:start_time and ti.time<=:end_time  and ti.id !=(SELECT t.id FROM t_invest AS t WHERE user_id=:userId AND t.debt_id=0  ORDER BY t.time LIMIT 1)  AND ti.debt_id=0  order by ta.id";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", user_id);
		condition.put("start_time", startTime);
		condition.put("end_time", endTime);
		return this.findListMapBySQL(sql, condition);
	}

}
