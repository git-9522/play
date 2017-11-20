package daos.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daos.base.BaseDao;
import models.activity.bean.InvestAnnual;
import models.activity.bean.InvestHbbSumAnnual;
import models.activity.entity.t_hbb_around;

/**
 * @Title HbbAroundDao
 * @Description 10月活动 虹宝宝全国之旅
 * @author hjs_djk
 * @createDate 2017年9月21日 下午3:20:53
 */
public class HbbAroundDao extends BaseDao<t_hbb_around> {

	public HbbAroundDao() {

	}

	/**
	 * 获取截止当前 活动时间内用户投资的年化金额
	 * 
	 * @return
	 */
	public List<InvestAnnual> queryUserInvertAnnuali(String startTime, String endTime) {
		String sql = "SELECT ti.`user_id` as id ,ti.`user_id` as user_id ,count(1) as count ,SUM(ti.`amount`/12*tb.`period`) AS annual_money  FROM `t_invest` AS ti, `t_bid` AS tb  WHERE ti.`bid_id`=tb.`id` AND ti.debt_id=0 AND ti.`time` >=:start_time and  ti.`time`<=:end_time GROUP BY ti.`user_id` ";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("start_time", startTime);
		condition.put("end_time", endTime);
		return this.findListBeanBySQL(sql, InvestAnnual.class, condition);
	}
	
	/**
	 * 获取截止当前 活动时间内用户投资的年化金额
	 * 
	 * @return
	 */
	public List<InvestAnnual> queryUserInvertAnnuali(String str,String startTime, String endTime) {
		String sql = "SELECT ti.`user_id` as id ,ti.`user_id` as user_id ,count(1) as count ,SUM(ti.`amount`/12*tb.`period`) AS annual_money  FROM `t_invest` AS ti, `t_bid` AS tb  WHERE ti.`bid_id`=tb.`id` AND ti.debt_id=0  AND ti.`user_id` IN (SELECT `user_id` FROM `t_user_info` AS tui WHERE  tui.`id_number` LIKE :pre ) AND ti.`time`>= :start_time and ti.`time`<=:end_time GROUP BY ti.`user_id` ";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("start_time",startTime);
		condition.put("end_time",endTime);
		condition.put("pre", str+"%");
		return this.findListBeanBySQL(sql, InvestAnnual.class, condition);
	}
	

	/**
	 * 获取截止当前 活动时间内所有用户已经发放的所有奖励和加油费
	 * 
	 * @return
	 */
	public List<InvestHbbSumAnnual> queryUserHbbSumMoney(String startTime, String endTime) {
		String sql = "SELECT th.`user_id` as id ,th.user_id, SUM(th.this_count) as counts,SUM(th.add_trip) as trips,SUM(th.value) as money from t_hbb_around   as  th where th.`time`>= :start_time and th.`time`<=:end_time  group by th.user_id";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("start_time",startTime);
		condition.put("end_time",endTime);
		return this.findListBeanBySQL(sql, InvestHbbSumAnnual.class, condition);
	}

}
