package daos.core;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.constants.Constants;
import common.utils.DateUtil;
import common.utils.PageBean;
import daos.base.BaseDao;
import models.common.entity.t_user_info;
import models.core.bean.BidInvestRecord;
import models.core.bean.CpsUserInvest;
import models.core.bean.UserInvestRecord;
import models.core.entity.t_bill_invest;
import models.core.entity.t_invest;

/**
 * 投资 接口实现类
 *
 * @description
 *
 * @author liudong
 * @createDate 2015年12月15日
 */
public class InvestDao extends BaseDao<t_invest> {

	protected InvestDao() {
	}

	public double findTotalInvest(Long userId) {
		String querySQL = " SELECT IFNULL(SUM(i.amount),0) FROM t_invest i  where i.user_id=:userId  AND i.debt_id = 0 ";

		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);

		return findSingleDoubleBySQL(querySQL, 0, condition);
	}

	/**
	 * 查询最新的理财信息
	 * 
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月29日
	 *
	 */
	public double findBidInvestFee(long bidId) {

		String sql = "select sum(invest_fee) from t_invest where bid_id=:bid_id";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("bid_id", bidId);

		return super.findSingleDoubleBySQL(sql, 0.00, params);
	}

	/**
	 * 获取投资金额
	 * 
	 * @param startTime
	 * @param endTime
	 * @param type
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月25日
	 *
	 */
	public double findTotalInvestByDate(String startTime, String endTime, int type) {
		String sql = null;
		String hour = null;
		Map<String, Object> condition = new HashMap<String, Object>();
		if (type == Constants.YESTERDAY) {
			sql = "SELECT SUM(amount) AS invest_money FROM t_invest WHERE TO_DAYS(:nowTime) - TO_DAYS(time) = 1 AND HOUR(time) <:hour AND debt_id=0";
			if (endTime.contains(":")) {
				hour = endTime.substring(0, endTime.indexOf(":"));
				if ("00".equals(hour)) {
					hour = "24";
				}
			}

			condition.put("nowTime", new Date());
			condition.put("hour", hour);
		} else {
			sql = "SELECT SUM(amount) AS invest_money FROM t_invest WHERE time>=:startTime AND time <=:endTime AND debt_id=0";

			condition.put("startTime", startTime);
			condition.put("endTime", endTime);
		}

		return findSingleDoubleBySQL(sql, 0.00, condition);
	}

	/**
	 * 查询一个借款标的所有投资记录
	 *
	 * @param bidId
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月20日
	 */
	public List<BidInvestRecord> listOfBidInvestRecords(long bidId) {
		/**
		 * SELECT ti.id AS id, ti.bid_id AS bid_id, tu.name AS name, ti.client
		 * AS client, ti.time AS time, ti.amount AS amount, ti.invest_type as
		 * invest_type FROM t_user tu, t_invest ti WHERE tu.id = ti.user_id AND
		 * ti.bid_id =: bidId
		 */
		String sql = "SELECT ti.id AS id, ti.bid_id AS bid_id, tu.name AS name, ti.client AS client, ti.time AS time, ti.amount AS amount, ti.invest_type FROM t_user tu, t_invest ti WHERE tu.id = ti.user_id AND ti.bid_id =:bidId ";

		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("bidId", bidId);

		return super.findListBeanBySQL(sql, BidInvestRecord.class, condition);

	}

	/**
	 * 查询一个借款标的所有投资用户信息
	 *
	 * @param bidId
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月20日
	 */
	public List<t_user_info> listOfBidInvestUsers(long bidId) {
		/**
		 * SELECT ti.id AS id, ti.bid_id AS bid_id, tu.name AS name, ti.client
		 * AS client, ti.time AS time, ti.amount AS amount, ti.invest_type as
		 * invest_type FROM t_user tu, t_invest ti WHERE tu.id = ti.user_id AND
		 * ti.bid_id =: bidId
		 */
		String sql="SELECT * FROM t_user_info WHERE user_id IN( SELECT user_id FROM t_invest WHERE bid_id=:bidId)";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("bidId", bidId);
		return super.findListBeanBySQL(sql, t_user_info.class, condition);

	}

	/**
	 * 查询用户每周投资信息(剔除已转让的账单)
	 * 
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月29日
	 *
	 */
	public List<Map<String, Object>> queryNewInvestList() {
		String sql = "SELECT ti.time, tu.photo, ti.amount, tb.id FROM t_user_info tu, t_invest ti, t_bid tb WHERE tu.user_id = ti.user_id AND ti.bid_id = tb.id AND ti.debt_id=0 ORDER BY ti.time DESC LIMIT 5";

		return this.findListMapBySQL(sql, null);
	}

	/**
	 * 周排行榜
	 *
	 * @return
	 *
	 * @author Songjia
	 * @createDate 2016年4月27日
	 */
	public List<Map<String, Object>> queryWeekInvestList(int count) {
		Map<String, Object> condition = new HashMap<String, Object>();
		String endTime = DateUtil.getPreviousSunday();// 本周开始时间
		String startTime = DateUtil.getCurrentMonday();// 本周结束时间
		String sql = "SELECT tu.name,sum(ti.amount) AS amount FROM t_user tu,t_invest ti WHERE tu.id = ti.user_id AND ti.time BETWEEN :startTime AND :endTime and debt_id = 0 GROUP BY tu.id ORDER BY amount DESC LIMIT :count";
		condition.put("startTime", startTime);
		condition.put("endTime", endTime);
		condition.put("count", count);
		return this.findListMapBySQL(sql, condition);
	}

	/**
	 * 投资记录查询，针对一个标的投资情况[标的详情的下拉列表]
	 *
	 * @author yaoyi
	 * @createDate 2015年12月16日
	 * 
	 * @param currPage
	 *            当前页
	 * @param pageSize
	 *            每页条数
	 * @param bidId
	 *            标的Id
	 * 
	 * @return PageBean
	 * 
	 */
	public PageBean<BidInvestRecord> pageOfBidInvestDetail(int currPage, int pageSize, long bidId) {

		String querySQL = "SELECT invest.id, invest.bid_id, user.name, invest.client, invest.time, invest.amount, invest.invest_type FROM t_invest invest LEFT JOIN t_user user ON invest.user_id=user.id WHERE invest.bid_id=:bidId AND invest.debt_id=0 ORDER BY invest.id DESC";
		String countSQL = "SELECT COUNT(1) FROM t_invest WHERE bid_id=:bidId AND debt_id=0";

		Map<String, Object> conditionArgs = new HashMap<String, Object>();
		conditionArgs.put("bidId", bidId);

		return super.pageOfBeanBySQL(currPage, pageSize, countSQL, querySQL, BidInvestRecord.class, conditionArgs);
	}

	/**
	 * 投资记录查询，针对某一个用户的投资情况
	 * 
	 * @description 已转让的状态也当做完成状态
	 *
	 * @param currPage
	 *            当前页
	 * @param pageSize
	 *            每页条数
	 * @param userId
	 *            用户id
	 *
	 * @author liudong
	 * @createDate 2015年12月16日
	 */
	public PageBean<UserInvestRecord> pageOfUserInvestRecord(int currPage, int pageSize, long userId) {
		/**
		 * SELECT ti.id AS id, ti.user_id AS user_id, ti.bid_id AS bid_id,
		 * ti.amount AS amount, tb.title AS title, tb.apr AS apr, tb.period AS
		 * period, tb.period_unit AS period_unit, tb.repayment_type AS
		 * repayment_type, tb.release_time AS release_time, tb.status AS status,
		 * (SELECT COUNT(1) FROM t_bill_invest tbi WHERE tbi.invest_id = ti.id
		 * AND tbi.status IN (:status) AND tbi.user_id =ti.user_id) AS
		 * alreadRepay, (SELECT COUNT(1) FROM t_bill_invest tbi WHERE
		 * tbi.invest_id = ti.id AND tbi.user_id =ti.user_id ) AS totalPay,
		 * (ti.reward_amount + ti.rate_amount) AS add_amount FROM t_bid tb,
		 * t_invest ti WHERE tb.id = ti.bid_id AND ti.user_id =:userId;
		 */
		String sql = "SELECT ti.id AS id,ti.user_id AS user_id,ti.bid_id AS bid_id,ti.amount AS amount,tb.title AS title,tb.apr AS apr,tb.period AS period,tb.period_unit AS period_unit,tb.repayment_type AS repayment_type,tb.release_time AS release_time,tb.status AS status,(SELECT COUNT(1) FROM t_bill_invest tbi WHERE tbi.invest_id = ti.id AND tbi.status IN ("
				+ t_bill_invest.Status.RECEIVED.code + "," + t_bill_invest.Status.TRANSFERED.code
				+ ") AND tbi.user_id =ti.user_id) AS alreadRepay,(SELECT COUNT(1) FROM t_bill_invest tbi WHERE tbi.invest_id = ti.id AND tbi.user_id =ti.user_id ) AS totalPay,(ti.reward_amount + ti.rate_amount) AS add_amount FROM t_bid tb,t_invest ti WHERE tb.id = ti.bid_id AND ti.user_id =:userId ORDER BY ti.id DESC";

		String sqlCount = "SELECT COUNT(1) FROM t_bid tb,t_invest ti WHERE tb.id = ti.bid_id AND ti.user_id =:userId";

		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);

		return pageOfBeanBySQL(currPage, pageSize, sqlCount, sql, UserInvestRecord.class, condition);
	}

	/**
	 * 投资记录查询，针对某一个用户的投资情况(cps)
	 * 
	 * @description 已转让的状态也当做完成状态
	 *
	 * @param currPage
	 *            当前页
	 * @param pageSize
	 *            每页条数
	 * @param userId
	 *            用户id
	 *
	 * @author liudong
	 * @createDate 2015年12月16日
	 */
	public PageBean<CpsUserInvest> pageOfCpsUserInvestRecord(int currPage, int pageSize, long userId) {
		/**
		 * SELECT ti.id AS id, ti.time AS time, ti.amount AS investAmt, tb.title
		 * AS bidTitle, tu.`name` AS userName FROM t_bid tb, t_invest ti, t_user
		 * tu WHERE tb.id = ti.bid_id AND ti.user_id = tu.id AND ti.user_id =
		 * :userId;
		 */
		String sql = "SELECT ti.id AS id, ti.time AS time, ti.amount AS investAmt, tb.title AS bidTitle, tu.`name` AS userName FROM t_bid tb, t_invest ti, t_user tu WHERE tb.id = ti.bid_id AND ti.user_id = tu.id AND ti.user_id = :userId ORDER BY ti.id DESC";

		String sqlCount = "SELECT COUNT(1) FROM t_bid tb, t_invest ti, t_user tu WHERE tb.id = ti.bid_id AND ti.user_id = tu.id AND ti.user_id = :userId";

		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);

		return pageOfBeanBySQL(currPage, pageSize, sqlCount, sql, CpsUserInvest.class, condition);
	}

	/**
	 * 月排行榜
	 *
	 * @return
	 *
	 * @author YanPengFei
	 * @createDate 2017年2月28日
	 */
	public List<Map<String, Object>> queryMonthInvestList(int count) {
		String startTime = DateUtil.getMonthFirstDay(); // 本月开始时间
		String endTime = DateUtil.getMonthLastDay(); // 本月结束时间
		String sql = "SELECT tu.name,sum(ti.amount) AS amount FROM t_user tu,t_invest ti WHERE tu.id = ti.user_id AND ti.time BETWEEN :startTime AND :endTime and debt_id = 0 GROUP BY tu.id ORDER BY amount DESC LIMIT :count";

		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("startTime", startTime);
		condition.put("endTime", endTime);
		condition.put("count", count);
		return this.findListMapBySQL(sql, condition);
	}

	/**
	 * 总排行榜
	 *
	 * @return
	 *
	 * @author YanPengFei
	 * @createDate 2017年2月28日
	 */
	public List<Map<String, Object>> queryTotalInvestList(int count) {
		Map<String, Object> condition = new HashMap<String, Object>();
		String sql = "SELECT tu.name,sum(ti.amount) AS amount FROM t_user tu,t_invest ti WHERE tu.id = ti.user_id and debt_id = 0 GROUP BY tu.id ORDER BY amount DESC LIMIT :count";
		condition.put("count", count);
		return this.findListMapBySQL(sql, condition);
	}

	/**
	 * 查询用户是否有投资记录
	 * 
	 * @param userId
	 * @return
	 */
	public long queryIsHaveInvestRecord(long userId) {
		// 查询用户有没有投资记录
		String sql = "select ti.id from t_invest ti where ti.user_id = :userId";
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("userId", userId);

		return this.findSingleLongBySQL(sql, 0L, args);
	}

	/**
	 * 查询用户所有有投资记录
	 * 
	 * @param userId
	 * @return 
	 * @author yuechuanyang
	 * @createDate 2017年10月17日
	 */
	public List<t_invest> queryInvestRecordByUserId(long userId) {
		//查询用户有没有投资记录
		String sql = "select * from t_invest ti where ti.user_id = :userId  AND debt_id = 0 order by time";
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("userId", userId);
		return this.findListBySQL(sql, args);
	}
	
	/**
	 * 统计用户投资次数
	 *
	 * @param userid
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月17日
	 */
	public int countInvestOfUser(long userId) {
		
		return this.countByColumn(" user_id=? ", userId);
	}
	

	/**
	 * 统计用户投资次数
	 *
	 * @param userid
	 * param isFilteDebt  是否过滤债券转让
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月17日
	 */
	public int countInvestOfUser(long userId,boolean isFilteDebt ) {
		if(isFilteDebt){
			return this.countByColumn(" user_id=?  AND debt_id = 0 ", userId);
		}else{
			
			return this.countByColumn(" user_id=?  ", userId);
		}
		
	}

	/**
	 * 
	 * 计算总利息（用于统计到总收益上）
	 * 
	 * @param userId
	 */
	public double calculateTotalInterest(long userId) {
		String sql = "select sum(reward_amount + rate_amount) from t_invest WHERE user_id = :userId";
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("userId", userId);

		Object object = this.findSingleBySQL(sql, args);
		if (object == null) {
			return 0;
		}
		return new Double(object.toString());
	}

	public double calulateInvestMoneyInDates(long userId, Date startDate, Date endDate) {
		String start = DateUtil.dateToString(startDate, "yyyy-MM-dd");
		String end = DateUtil.dateToString(endDate, "yyyy-MM-dd");
		String sql = "SELECT SUM(amount) from t_invest WHERE user_id = :userId AND DATE_FORMAT(time, '%Y-%m-%d') >= :start AND DATE_FORMAT(time, '%Y-%m-%d') <= :end";
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("userId", userId);
		args.put("start", start);
		args.put("end", end);
		Object object = this.findSingleBySQL(sql, args);
		if (object == null) {
			return 0;
		}
		return new Double(object.toString());
	}

	public long calulateInvestMoneyInDates(String start, String end) {
		String sql = "SELECT SUM(TRUNCATE(amount / 100, 0) * 100) from t_invest WHERE time >= :start AND time < :end";
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("start", start);
		args.put("end", end);
		Object object = this.findSingleBySQL(sql, args);
		if (object == null) {
			return 0;
		}
		return new Long(object.toString());
	}

	public long calulateInvestMoneyInDatesWithPeriod(long userId, String start, String end, int period) {
		String sql = "SELECT SUM(i.amount) from t_invest i LEFT JOIN t_bid b ON i.bid_id = b.id WHERE i.time >= :start AND i.time < :end AND b.period = :period AND i.user_id = :userId";
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("start", start);
		args.put("end", end);
		args.put("period", period);
		args.put("userId", userId);
		Object object = this.findSingleBySQL(sql, args);
		if (object == null) {
			return 0;
		}
		return new Double(object.toString()).longValue();
	}

}
