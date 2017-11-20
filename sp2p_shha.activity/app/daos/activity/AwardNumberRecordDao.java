package daos.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daos.base.BaseDao;
import models.activity.entity.t_award_number_record;

/**
 * 投资送 IPHONE DAO
 * @Title AwardNumberRecordDao 
 * @Description 投资送 IPHONE DAO
 * @author hjs_djk
 * @createDate 2017年10月23日 上午10:52:15
 */
public class AwardNumberRecordDao extends BaseDao<t_award_number_record> {

	/** 查询用户奖号总数 */
	public int getAwardNumberaAmount(long userId) {
		String sql = "SELECT SUM(count) FROM t_award_number_record t where t.user_id =:userId";
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put("userId", userId);

		return this.findSingleIntBySQL(sql, -1, filter);
	}

	/**
	 * 获取用户累计年化率
	 * 
	 * @param startTime
	 * @param endTime
	 * @param userId
	 * @return
	 */
	public double getAnnualMoney(String startTime, String endTime, long userId) {
		/* AND time =(SELECT MIN(time) FROM t_invest WHERE user_id=:userId)) */
		String sql = "SELECT SUM(ti.`amount`/12*tb.`period`) AS annual_money  FROM `t_invest` AS ti, `t_bid` AS tb  WHERE ti.id !=(SELECT t.id FROM t_invest AS t WHERE user_id=:userId  AND ti.debt_id=0  ORDER BY t.time LIMIT 1)       AND  ti.`bid_id`=tb.`id`  AND  ti.`time` >=:startTime and ti.`time`<=:endTime AND ti.user_id=:userId AND ti.debt_id=0  GROUP BY ti.`user_id` ";
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put("startTime", startTime);
		filter.put("endTime", endTime);
		filter.put("userId", userId);
		return this.findSingleDoubleBySQL(sql, -1, filter);

	}

	/**
	 * 获取最大奖号
	 * 
	 * @param period
	 *            摇奖期数
	 */
	public String getAwardNumberMax(int period) {
		String sql = "SELECT MAX(t.max_award_number) from t_award_number_record t WHERE t.periods=:period";
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put("period", period);

		return this.findSingleStringBySQL(sql, "-1", filter);
	}

	/**
	 * 获取最大摇奖期数
	 * 
	 */
	@SuppressWarnings("unchecked")
	public int getPeriodsMax() {
		String sql = "SELECT MAX(periods) FROM t_award_number_record ";

		return this.findSingleIntBySQL(sql, -1, new HashMap());
	}

	/**
	 * 列表
	 * 
	 * @return
	 */
	public List<Map<String, Object>> getAwardNumberList() {
		String sql = "SELECT id, t.user_name AS user_name,t.amount AS amount, min_award_number , max_award_number , t.count, t.time  ,t.periods AS periods FROM t_award_number_record t   ORDER BY  periods desc,id DESC";
		return this.findListMapBySQL(sql, new HashMap<String, Object>());

	}
}
