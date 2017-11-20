package daos.core;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import common.constants.Constants;
import common.utils.Factory;
import common.utils.PageBean;
import daos.base.BaseDao;
import models.core.bean.BackFinanceBid;
import models.core.bean.BackRiskBid;
import models.core.bean.FrontBid;
import models.core.bean.FrontMyLoanBid;
import models.core.bean.MyPrerogativeRecord;
import models.core.bean.PactBid;
import models.core.entity.t_bid;
import models.core.entity.t_bid.Status;
import models.core.entity.t_bill;
import models.core.entity.t_product.ProductType;
import models.core.entity.t_product.ProductWebType;

/**
 * 标的表的DAO
 *
 * @description
 *
 * @author yaoyi
 * @createDate 2016年3月7日
 */
public class BidDao extends BaseDao<t_bid> {

	public ProductDao productdao = Factory.getDao(ProductDao.class);

	/**
	 * 初审更新标的状态
	 *
	 * @param bidId
	 *            标的id
	 * @param status
	 *            新状态
	 * @param pre_release_time
	 *            预发布时间
	 * @param invest_expire_time
	 *            满标时间
	 * @param preauditor_supervisor_id
	 *            初审审核人
	 * @param preaudit_time
	 *            初审时间
	 * @param preaudit_suggest
	 *            审核意见
	 * @param nowstatus
	 *            原状态
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月22日
	 */
	public int preAuditBidStatus(long bidId, int status, Date pre_release_time, Date invest_expire_time,
			long preauditor_supervisor_id, Date preaudit_time, String preaudit_suggest, List<Integer> nowstatus) {

		String updSql = "UPDATE t_bid SET status=:status, pre_release_time=:pre_release_time, invest_expire_time=:invest_expire_time, preauditor_supervisor_id=:preauditor_supervisor_id, preaudit_time=:preaudit_time, preaudit_suggest=:preaudit_suggest WHERE id=:id AND status in(:nowstatus)";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", status);
		params.put("pre_release_time", pre_release_time);
		params.put("invest_expire_time", invest_expire_time);
		params.put("preauditor_supervisor_id", preauditor_supervisor_id);
		params.put("preaudit_time", preaudit_time);
		params.put("preaudit_suggest", preaudit_suggest);
		params.put("id", bidId);
		params.put("nowstatus", nowstatus);

		return super.updateBySQL(updSql, params);
	}

	/**
	 * 截标
	 *
	 * @param bidId
	 *            标的id
	 * @param status
	 *            新状态
	 * @param auditor_supervisor_id
	 *            复审审核人
	 * @param audit_time
	 *            复审时间
	 * @param audit_suggest
	 *            复审意见
	 * @param nowstatus
	 *            原状态
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月26日
	 */
	public int auditBidCutoff(long bidId, int status, long auditor_supervisor_id, Date audit_time, String audit_suggest,
			List<Integer> nowstatus) {

		String updSql = "UPDATE t_bid SET amount=has_invested_amount,loan_schedule=100,real_invest_expire_time = :expireTime,status=:status, auditor_supervisor_id=:auditor_supervisor_id, audit_time=:audit_time, audit_suggest=:audit_suggest WHERE id=:id AND status in(:nowstatus)";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", status);
		params.put("auditor_supervisor_id", auditor_supervisor_id);
		params.put("audit_time", audit_time);
		params.put("audit_suggest", audit_suggest);
		params.put("expireTime", new Date());
		params.put("id", bidId);
		params.put("nowstatus", nowstatus);

		return super.updateBySQL(updSql, params);
	}

	/**
	 * 复审
	 *
	 * @param bidId
	 *            标的id
	 * @param status
	 *            新状态
	 * @param auditor_supervisor_id
	 *            复审审核人
	 * @param audit_time
	 *            复审时间
	 * @param audit_suggest
	 *            复审意见
	 * @param nowstatus
	 *            原状态
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月26日
	 */
	public int auditBidStatus(long bidId, int status, long auditor_supervisor_id, Date audit_time, String audit_suggest,
			List<Integer> nowstatus) {

		String updSql = "UPDATE t_bid SET status=:status, auditor_supervisor_id=:auditor_supervisor_id, audit_time=:audit_time, audit_suggest=:audit_suggest WHERE id=:id AND status in(:nowstatus)";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", status);
		params.put("auditor_supervisor_id", auditor_supervisor_id);
		params.put("audit_time", audit_time);
		params.put("audit_suggest", audit_suggest);
		params.put("id", bidId);
		params.put("nowstatus", nowstatus);

		return super.updateBySQL(updSql, params);
	}

	/**
	 * 更新标的标题
	 *
	 * @param bidId
	 * @param newTitle
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月23日
	 */
	public int updateBidTitle(long bidId, String newTitle) {

		String sql = "UPDATE t_bid SET title=:title WHERE id=:bid_id";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("title", newTitle);
		params.put("bid_id", bidId);

		return super.updateBySQL(sql, params);
	}

	/**
	 * 更新标的描述
	 *
	 * @param bidId
	 * @param newDescription
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月23日
	 */
	public int updateBidDescription(long bidId, String newDescription) {

		String sql = "UPDATE t_bid SET description=:description WHERE id=:bid_id";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("description", newDescription);
		params.put("bid_id", bidId);

		return super.updateBySQL(sql, params);
	}

	/**
	 * 放款
	 *
	 * @param release_supervisor_id
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月23日
	 */
	public int updateBidRelease(long release_supervisor_id, long bidId) {

		String sql = "UPDATE t_bid SET status=:wait_releasing, release_supervisor_id=:release_supervisor_id, release_time=:release_time WHERE id=:bid_id and status=:repaying";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("wait_releasing", t_bid.Status.REPAYING.code);
		params.put("release_supervisor_id", release_supervisor_id);
		params.put("release_time", new Date());
		params.put("bid_id", bidId);
		params.put("repaying", t_bid.Status.WAIT_RELEASING.code);

		return super.updateBySQL(sql, params);
	}

	/**
	 * 更新投标进度、已投金额、加入人次
	 *
	 * @param bidId
	 *            标的id
	 * @param schedule
	 *            投标进度
	 * @param investAmt
	 *            投资金额
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月28日
	 */
	public int updateBidschedule(long bidId, double investAmt, double schedule) {
		String sql = "UPDATE t_bid SET loan_schedule = :schedule , has_invested_amount = has_invested_amount + :investAmt, invest_count = invest_count + 1 WHERE id= :bidId AND amount >= has_invested_amount + :investAmt";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("schedule", schedule);
		condition.put("investAmt", investAmt);
		condition.put("bidId", bidId);
		return this.updateBySQL(sql, condition);
	}

	/**
	 * 满标，更新标的信息
	 *
	 * @param bidId
	 *            标的ID
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月28日
	 */
	public int updateBidEnd(long bidId) {
		String sql = "UPDATE t_bid SET last_repay_time = :endTime, status = :status WHERE id = :bidId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("endTime", new Date());
		condition.put("status", t_bid.Status.REPAYED.code);
		condition.put("bidId", bidId);
		return this.updateBySQL(sql, condition);
	}

	/**
	 * 满标，更新标的信息
	 *
	 * @param bidId
	 *            标的ID
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月28日
	 */
	public int updateBidExpire(long bidId) {
		String sql = "UPDATE t_bid SET real_invest_expire_time = :expireTime, status = :status WHERE id = :bidId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("expireTime", new Date());
		condition.put("status", t_bid.Status.AUDITING.code);
		condition.put("bidId", bidId);
		return this.updateBySQL(sql, condition);
	}

	/**
	 * 更新标的状态(借款中->流标)
	 *
	 * @param bidId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月30日
	 */
	public int updateBidStatusFlow(long bidId) {

		String sql = "UPDATE t_bid SET status=:status1 WHERE id=:bidId AND status=:status2";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("bidId", bidId);
		params.put("status1", t_bid.Status.FLOW.code);
		params.put("status2", t_bid.Status.FUNDRAISING.code);

		return super.updateBySQL(sql, params);
	}

	/**
	 * 更新借款服务费
	 *
	 * @param bidId
	 * @param loanServiceFee
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月12日
	 */
	public int updateLoanServiceFee(Long bidId, double loanServiceFee) {
		String sql = "UPDATE t_bid SET loan_fee = :loanFee WHERE id = :bidId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("loanFee", loanServiceFee);
		params.put("bidId", bidId);

		return updateBySQL(sql, params);
	}

	/**
	 * 查询放款项目数量
	 * 
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月8日
	 *
	 */
	public int findReleasedBidsNum() {
		String sql = "SELECT COUNT(1) FROM t_bid tb WHERE tb.status IN(:status)";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", t_bid.Status.LOAN);

		return findSingleIntBySQL(sql, 0, params);
	}

	/**
	 * 查询放款项目总额
	 * 
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月8日
	 *
	 */
	public double findTotalBorrowAmount() {

		String sql = "SELECT SUM(bid.amount) FROM t_bid bid WHERE bid.status IN (:status)";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", t_bid.Status.LOAN);

		return findSingleDoubleBySQL(sql, 0.00, params);
	}

	/**
	 * 查找合同相关bid信息
	 *
	 * @param bidId
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月22日
	 */
	public PactBid findPactBidById(long bidId) {
		String sql = "SELECT b.id as id,b.title as title,b.description as description,u.name as name ,ui.reality_name as reality_name,ui.id_number as id_number,b.amount as amount,b.apr as apr,b.period_unit as period_unit,b.period as period,b.release_time as release_time,b.repayment_type as repayment_type, b.service_fee_rule as service_fee_rule FROM t_bid b INNER JOIN t_user u ON b.user_id = u.id INNER JOIN t_user_info ui on u.id = ui.user_id WHERE b.id = :bidId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("bidId", bidId);

		PactBid pactBid = this.findBeanBySQL(sql, PactBid.class, params);

		return pactBid;
	}

	/**
	 * 统计标的借款总额
	 * 
	 * @param showType
	 *            筛选类型 0-所有;1-初审中;2-借款中;3-满标审核;4-还款中;5-已经结束;6-失败
	 * @param loanName
	 *            借款人昵称
	 * @param numNo
	 *            编号
	 * @param projectName
	 *            项目名称
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月9日
	 */
	public double findTotalBidAmount(List<Integer> statusList, String loanName, String numNo, String projectName) {
		StringBuffer sql = new StringBuffer(
				"SELECT SUM(tb.amount) FROM t_bid tb LEFT JOIN t_user tu ON tb.user_id = tu.id WHERE 1=1 ");

		Map<String, Object> condition = new HashMap<String, Object>();
		if (statusList != null && statusList.size() != 0) {
			sql.append(" AND tb.status IN(:status) ");
			condition.put("status", statusList);
		}

		/* 按借款人昵称 模糊查询 */
		if (StringUtils.isNotBlank(loanName)) {
			sql.append(" AND tu.name LIKE :loanName ");
			condition.put("loanName", "%" + loanName + "%");
		}

		/* 按编号 模糊查询 */
		if (StringUtils.isNotBlank(numNo)) {
			sql.append(" AND tb.id LIKE :id ");
			condition.put("id", "%" + numNo + "%");
		}

		/* 按项目名称 模糊查询 */
		if (StringUtils.isNotBlank(projectName)) {
			sql.append(" AND tb.title LIKE :title ");
			condition.put("title", "%" + projectName + "%");
		}

		return findSingleDoubleBySQL(sql.toString(), 0.00, condition);
	}

	/**
	 * 通过标的Id返回标的名称
	 *
	 * @param bidId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年3月2日
	 */
	public String findBidNameById(long bidId) {

		String sql = "SELECT title FROM t_bid WHERE id=:id";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", bidId);

		return super.findSingleStringBySQL(sql, "", params);
	}

	/**
	 * 通过标的编号查找标的ID
	 *
	 * @param merBidNo
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年3月3日
	 */
	public long findIdByMerBidNo(String merBidNo, long defaultVal) {
		String sql = "SELECT id FROM t_bid WHERE mer_bid_no = :merBidNo";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("merBidNo", merBidNo);

		return super.findSingleLongBySQL(sql, defaultVal, params);
	}

	/**
	 * 前台-理财
	 * 
	 * @param currPage
	 * @param pageSize
	 * @param productId
	 *            借款产品ID
	 * @param status
	 *            借款标状态
	 * @param orderType
	 *            排序方式
	 * @param orderValue
	 *            排序规则
	 * @return
	 * 
	 * @author yaoyi
	 * @createDate 2016年1月14日
	 */
	public PageBean<FrontBid> pageOfBidInvest(int currPage, int pageSize, long productId, int status, int orderType,
			int orderValue) {
		StringBuffer querySQL = new StringBuffer(
				"SELECT b.product_id,b.guarantee_mode_id,b.id,b.title,b.min_invest_amount, b.is_invest_reward,b.reward_rate,b.apr,b.period_unit,b.has_invested_amount,b.period,b.amount,b.loan_schedule,b.status,b.pre_release_time FROM t_bid b inner join t_product p on b.product_id = p.id WHERE 1=1 ");
		StringBuffer countSQL = new StringBuffer(
				"SELECT COUNT(b.id) FROM t_bid b inner join t_product p on b.product_id = p.id WHERE 1=1 ");
		// querySQL.append(" and p.type <> :productType ");
		// countSQL.append(" and p.type <> :productType ");

		Map<String, Object> conditionArgs = new HashMap<String, Object>();
		// conditionArgs.put("productType", ProductType.NEWBIE.code);
		int product_id = 0;
		try {
			product_id = Integer.parseInt(String.valueOf(productId));
		} catch (Exception e) {
			product_id = 0;
		}
		if (product_id > 0) {
			switch (product_id) {
			case 88: {
				// 普通标（彩虹投）
				querySQL.append(" and p.type in :type ");
				countSQL.append(" and p.type in :type ");
				conditionArgs.put("type", ProductWebType.getProductWebTypeList(ProductWebType.OLD_BID.code));
				break;
			}
			default: {
				querySQL.append(" and b.product_id = :productId ");
				countSQL.append(" and b.product_id = :productId ");
				conditionArgs.put("productId", productId);
				break;
			}
			}
		}

		switch (status)

		{
		case 1: { // 正在筹集
			querySQL.append(" and b.status = :status ");
			countSQL.append(" and b.status = :status ");
			conditionArgs.put("status", t_bid.Status.FUNDRAISING.code);
			break;
		}
		case 88: { // 即将开启
			querySQL.append(" and b.status = :status and pre_release_time >= now() ");
			countSQL.append(" and b.status = :status and pre_release_time >= now() ");
			conditionArgs.put("status", t_bid.Status.FUNDRAISING.code);
			break;
		}
		case 4: { // 正在回款
			querySQL.append(" and b.status = :status ");
			countSQL.append(" and b.status = :status ");
			conditionArgs.put("status", t_bid.Status.REPAYING.code);
			break;
		}
		default: {
			querySQL.append(" and b.status in :status ");
			countSQL.append(" and b.status in :status ");
			conditionArgs.put("status", t_bid.Status.PROCESS);
			break;
		}
		}

		switch (orderType) {
		case 1: { // 年化利率
			querySQL.append(" order by b.apr ");
			break;
		}
		case 2: { // 投资期限
			querySQL.append(" order by (CASE b.period_unit WHEN 2 THEN (30 * b.period) ELSE b.period END) ");
			break;
		}
		case 0: {
			querySQL.append(" order by b.status, b.pre_release_time");
			break;
		}
		case 3: { // 发标时间
			querySQL.append(" order by b.pre_release_time ");
			break;
		}
		default: {
			querySQL.append(" order by b.loan_schedule ");
			break;
		}
		}

		if (orderType == 0) {
			querySQL.append(" desc ");
		} else {
			if (orderValue == 0) {
				querySQL.append(" desc ");
			} else {
				querySQL.append(" asc ");
			}
		}

		return super.pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), FrontBid.class,
				conditionArgs);
	}

	/**
	 * 前台-理财（新）
	 * 
	 * @param currPage
	 * @param pageSize
	 * @param productWebId
	 *            借款前台产品ID（新虹投，彩虹投，预虹投，智能收益）
	 * @param status
	 *            借款标状态
	 * @param orderType
	 *            排序方式
	 * @param orderValue
	 *            排序规则
	 * @return
	 * 
	 * @author hjs-djk
	 * @createDate 2017年10月11日
	 */
	public PageBean<FrontBid> pageOfWebBidInvest(int currPage, int pageSize, long productWebId, int status,
			int orderType, int orderValue, int guaranteModeId, int period, int vip) {
		StringBuffer querySQL = new StringBuffer(
				"SELECT b.product_id,b.id,b.guarantee_mode_id,b.title,b.min_invest_amount, b.is_invest_reward,b.reward_rate,b.apr,b.period_unit,b.has_invested_amount,b.period,b.amount,b.loan_schedule,b.status,b.pre_release_time FROM t_bid b inner join t_product p on b.product_id = p.id WHERE 1=1 and p.is_use=1");
		StringBuffer countSQL = new StringBuffer(
				"SELECT COUNT(b.id) FROM t_bid b inner join t_product p on b.product_id = p.id WHERE 1=1 ");
		// querySQL.append(" and p.type <> :productType ");
		// countSQL.append(" and p.type <> :productType ");

		Map<String, Object> conditionArgs = new HashMap<String, Object>();
		// conditionArgs.put("productType", ProductType.NEWBIE.code);
		int productweb_id = 0;
		try {
			productweb_id = Integer.parseInt(String.valueOf(productWebId));
		} catch (Exception e) {
			productweb_id = 0;
		}
		if (productweb_id > 0) {
			List<Integer> ps = ProductWebType.getProductWebTypeList(productweb_id);
			if (ps == null) {
				querySQL.append(" and p.type in :type ");
				countSQL.append(" and p.type in :type ");
				conditionArgs.put("type", ProductWebType.ALL_BID_LIST);
			} else {
				querySQL.append(" and p.type in :type ");
				countSQL.append(" and p.type in :type ");
				conditionArgs.put("type", ps);
			}
		}
		// 期数
		switch (period) {
		case 1://1个月以内 包含1月标
			querySQL.append(" and ((b.period = 1  and  b.period_unit=2) or (b.period <=30  and  b.period_unit=1)) ");
			countSQL.append(" and ((b.period = 1  and  b.period_unit=2) or (b.period <=30  and  b.period_unit=1)) ");
			break;
		case 2://2~6个月
			querySQL.append(" and  b.period>=2 and b.period<=6 and b.period_unit=2 ");
			countSQL.append(" and  b.period>=2 and b.period<=6 and b.period_unit=2 ");
			break;
		case 7://7~12个月
			querySQL.append(" and  b.period>=7 and b.period<=12 and b.period_unit=2 ");
			countSQL.append(" and  b.period>=7 and b.period<=12 and b.period_unit=2 ");
			break;
		case 13://12个月以上
			querySQL.append(" and  b.period>12  and b.period_unit=2 ");
			countSQL.append(" and  b.period>12  and b.period_unit=2 ");
			break;
		default:
			break;
		}
		// 保障方式
		switch (guaranteModeId) {
		case 1:
		case 2:
		case 3:
			querySQL.append(" and b.guarantee_mode_id = :guaranteModeId ");
			countSQL.append(" and b.guarantee_mode_id = :guaranteModeId ");
			conditionArgs.put("guaranteModeId", guaranteModeId);
			break;
		default:
			break;
		}
		// 状态
		switch (status)

		{
		case 1: { // 正在筹集
			querySQL.append(" and b.status = :status ");
			countSQL.append(" and b.status = :status ");
			conditionArgs.put("status", t_bid.Status.FUNDRAISING.code);
			break;
		}
		case 88: { // 即将开启
			querySQL.append(" and b.status = :status and pre_release_time >= now() ");
			countSQL.append(" and b.status = :status and pre_release_time >= now() ");
			conditionArgs.put("status", t_bid.Status.FUNDRAISING.code);
			break;
		}
		case 4: { // 正在回款
			querySQL.append(" and b.status = :status ");
			countSQL.append(" and b.status = :status ");
			conditionArgs.put("status", t_bid.Status.REPAYING.code);
			break;
		}
		default: {
			querySQL.append(" and b.status in :status ");
			countSQL.append(" and b.status in :status ");
			conditionArgs.put("status", t_bid.Status.PROCESS);
			break;
		}
		}

		switch (orderType) {
		case 1: { // 年化利率
			querySQL.append(" order by b.apr ");
			break;
		}
		case 2: { // 投资期限
			querySQL.append(" order by (CASE b.period_unit WHEN 2 THEN (30 * b.period) ELSE b.period END) ");
			break;
		}
		case 0: {
			querySQL.append(" order by b.status, b.pre_release_time");
			break;
		}
		case 3: { // 发标时间
			querySQL.append(" order by b.pre_release_time ");
			break;
		}
		default: {
			querySQL.append(" order by b.loan_schedule ");
			break;
		}
		}
		//vip TODO  后期VIP功能一起开发
		if (orderType == 0) {
			querySQL.append(" desc ");
		} else {
			if (orderValue == 0) {
				querySQL.append(" desc ");
			} else {
				querySQL.append(" asc ");
			}
		}

		return super.pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), FrontBid.class,
				conditionArgs);
	}

	/**
	 * 我的借款查询
	 *
	 * @param userId
	 * @param pageSize
	 * @param currPage
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月25日
	 */
	public PageBean<t_bid> pageOfMyLoan(long userId, int pageSize, int currPage) {

		String countSQL = "SELECT COUNT(id) FROM t_bid WHERE user_id=:userId";
		String querySQL = "SELECT * FROM t_bid WHERE user_id=:userId";
		Map<String, Object> conditionArgs = new HashMap<String, Object>();
		conditionArgs.put("userId", userId);

		return super.pageOfBySQL(currPage, pageSize, countSQL, querySQL, conditionArgs);
	}

	/**
	 * 后台-风控页面-标的列表
	 *
	 * @param showType
	 *            筛选类型 0-所有;1-初审中;2-借款中;3-满标审核;4-还款中;5-已经结束;6-失败
	 * @param currPage
	 * @param pageSize
	 * @param exports
	 *            1:导出 default：不导出
	 * @param numNo
	 *            编号
	 * @param projectName
	 *            项目名称
	 * @param userName
	 *            借款人
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月4日
	 */
	public PageBean<BackRiskBid> pageOfBidRisk(int showType, int currPage, int pageSize, int exports, int orderType,
			int orderValue, String userName, String numNo, String projectName, String agencyName) {

		/**
		 * SELECT bid.id AS id, bid.title AS title, user.name AS name,
		 * bid.amount AS amount, bid.apr AS apr, bid.period_unit AS period_unit,
		 * bid.period AS period, bid.loan_schedule AS loan_schedule,
		 * bid.pre_release_time AS pre_release_time, bid.status AS status,
		 * ta.`name` AS agencyName FROM t_bid bid LEFT JOIN t_user user ON
		 * bid.user_id = user .id LEFT JOIN t_agencies ta ON ta.id =
		 * bid.agency_id WHERE 1 = 1
		 */
		StringBuffer querySQL = new StringBuffer(
				"SELECT bid.id AS id, bid.title AS title, user.name AS name, bid.amount AS amount, bid.apr AS apr, bid.period_unit AS period_unit, bid.period AS period, bid.loan_schedule AS loan_schedule, bid.pre_release_time AS pre_release_time, bid.status AS status,ta.`name` AS agencyName FROM t_bid bid LEFT JOIN t_user user ON bid.user_id=user.id LEFT JOIN t_agencies ta ON ta.id = bid.agency_id WHERE 1=1 ");
		StringBuffer countSQL = new StringBuffer(
				"SELECT COUNT(bid.id) FROM t_bid bid LEFT JOIN t_user user ON bid.user_id=user.id LEFT JOIN t_agencies ta ON ta.id = bid.agency_id WHERE 1=1 ");

		Map<String, Object> conditionArgs = new HashMap<String, Object>();
		switch (showType) {
		case 0:// 所有
			break;
		case 1: {// 初审中
			querySQL.append(" AND bid.status=:status ");
			countSQL.append(" AND bid.status=:status ");
			conditionArgs.put("status", Status.PREAUDITING.code);
			break;
		}
		case 2: {// 借款中
			querySQL.append(" AND bid.status=:status ");
			countSQL.append(" AND bid.status=:status");
			;
			conditionArgs.put("status", Status.FUNDRAISING.code);
			break;
		}
		case 3: {// 满标审核
			querySQL.append(" AND bid.status=:status");
			countSQL.append(" AND bid.status=:status");
			conditionArgs.put("status", Status.AUDITING.code);
			break;
		}
		case 4: {// 还款中
			querySQL.append(" AND bid.status=:status");
			countSQL.append(" AND bid.status=:status");
			conditionArgs.put("status", Status.REPAYING.code);
			break;
		}
		case 5: {// 已经结束
			querySQL.append(" AND bid.status=:status");
			countSQL.append(" AND bid.status=:status");
			conditionArgs.put("status", Status.REPAYED.code);
			break;
		}
		case 6: {// 失败
			querySQL.append(" AND bid.status IN (:status)");
			countSQL.append(" AND bid.status IN (:status)");
			conditionArgs.put("status", t_bid.Status.FAIL);
			break;
		}
		case 7: {
			querySQL.append(" AND bid.status=:status");
			countSQL.append(" AND bid.status=:status");
			conditionArgs.put("status", t_bid.Status.PARTIAL_NORMAL_REPAYMENT.code);
			break;
		}
		default:
			break;
		}

		/** 按编号搜索 */
		if (StringUtils.isNotBlank(numNo)) {
			querySQL.append(" AND bid.id LIKE :bidId");
			countSQL.append(" AND bid.id LIKE :bidId");
			conditionArgs.put("bidId", "%" + numNo + "%");
		}

		/** 按项目名称搜索 */
		if (StringUtils.isNotBlank(projectName)) {
			querySQL.append(" AND bid.title LIKE :title");
			countSQL.append(" AND bid.title LIKE :title");
			conditionArgs.put("title", "%" + projectName + "%");
		}

		/** 按借款人搜索 */
		if (StringUtils.isNotBlank(userName)) {
			querySQL.append(" AND user.name LIKE :userName");
			countSQL.append(" AND user.name LIKE :userName");
			conditionArgs.put("userName", "%" + userName + "%");
		}

		/** 按借机构搜索 */
		if (StringUtils.isNotBlank(agencyName)) {
			querySQL.append(" AND ta.name LIKE :agencyName");
			countSQL.append(" AND ta.name LIKE :agencyName");
			conditionArgs.put("agencyName", "%" + agencyName + "%");
		}

		switch (orderType) {
		case 3: {
			querySQL.append(" ORDER BY bid.amount ");
			if (orderValue == 0) {
				querySQL.append(" DESC ");
			}
			break;
		}
		case 4: {
			querySQL.append(" ORDER BY bid.apr ");
			if (orderValue == 0) {
				querySQL.append(" DESC ");
			}
			break;
		}
		case 5: {
			if (orderValue == 0) {
				querySQL.append(" ORDER BY bid.period_unit , bid.period ");
			} else {
				querySQL.append(" ORDER BY bid.period_unit DESC, bid.period DESC ");
			}
			break;
		}
		case 6: {
			querySQL.append(" ORDER BY bid.loan_schedule ");
			if (orderValue == 0) {
				querySQL.append(" DESC ");
			}
			break;
		}
		case 7: {
			querySQL.append(" ORDER BY bid.pre_release_time ");
			if (orderValue == 0) {
				querySQL.append(" DESC ");
			}
			break;
		}
		default: {
			querySQL.append(" ORDER BY bid.id ");
			if (orderValue == 0) {
				querySQL.append(" DESC ");
			}
			break;
		}
		}

		if (exports == Constants.EXPORT) {
			PageBean<BackRiskBid> page = new PageBean<BackRiskBid>();
			page.page = this.findListBeanBySQL(querySQL.toString(), BackRiskBid.class, conditionArgs);
			return page;
		}

		return pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), BackRiskBid.class,
				conditionArgs);
	}

	/**
	 * 后台-财务页面-标的列表
	 *
	 * @param showType
	 *            筛选类型 0-所有;1-待放款;2-已放款(还款中,已还款)
	 * @param currPage
	 * @param pageSize
	 * @param exports
	 *            1：导出 default:不导出
	 * @param loanName
	 *            借款人
	 * @param orderType
	 *            排序栏目 0：编号 2：借款金额 7：放款时间
	 * @param orderValue
	 *            排序规则 0,降序，1,升序
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月13日
	 */
	public PageBean<BackFinanceBid> pageOfBidFinance(int showType, int currPage, int pageSize, int exports,
			String loanName, int orderType, int orderValue) {

		/**
		 * SELECT bid.id AS id, bid.title AS title, user.name AS name,
		 * bid.amount AS amount, bid.status AS status, bid.release_time AS
		 * release_time, supervisor1.reality_name AS pre_audit_supervisor,
		 * supervisor2.reality_name AS audit_supervisor,
		 * supervisor3.reality_name AS release_supervisor FROM ( ( ( t_bid bid
		 * LEFT JOIN t_user user ON bid.user_id = user.id ) LEFT JOIN
		 * t_supervisor supervisor1 ON bid.preauditor_supervisor_id =
		 * supervisor1.id ) LEFT JOIN t_supervisor supervisor2 ON
		 * bid.auditor_supervisor_id = supervisor2.id ) LEFT JOIN t_supervisor
		 * supervisor3 ON bid.release_supervisor_id = supervisor3.id WHERE 1 = 1
		 */
		StringBuffer querySQL = new StringBuffer(
				"SELECT bid.id AS id, bid.title AS title, user.name AS name, bid.amount AS amount, bid.status AS status, bid.release_time AS release_time, supervisor1.reality_name AS pre_audit_supervisor, supervisor2.reality_name AS audit_supervisor, supervisor3.reality_name AS release_supervisor FROM (((t_bid bid LEFT JOIN t_user user ON bid.user_id=user.id) LEFT JOIN t_supervisor supervisor1 ON bid.preauditor_supervisor_id=supervisor1.id) LEFT JOIN t_supervisor supervisor2 ON bid.auditor_supervisor_id=supervisor2.id) LEFT JOIN t_supervisor supervisor3 ON bid.release_supervisor_id=supervisor3.id WHERE ");
		StringBuffer countSQL = new StringBuffer(
				"SELECT COUNT(1) FROM t_bid bid LEFT JOIN t_user user ON bid.user_id = user.id WHERE  ");

		Map<String, Object> condition = new HashMap<String, Object>();
		switch (showType) {
		case 0: {// 所有
			querySQL.append(
					"  (bid.status=:status1 OR bid.status=:status2 OR bid.status=:status3 OR bid.status=:status4) ");
			countSQL.append(
					"  (bid.status=:status1 OR bid.status=:status2 OR bid.status=:status3 OR bid.status=:status4) ");
			condition.put("status1", t_bid.Status.WAIT_RELEASING.code);
			condition.put("status2", t_bid.Status.REPAYING.code);
			condition.put("status3", t_bid.Status.REPAYED.code);
			condition.put("status4", t_bid.Status.PARTIAL_NORMAL_REPAYMENT.code);
			break;
		}
		case 1: {// 待放款
			querySQL.append("  bid.status=:status ");
			countSQL.append("  bid.status=:status ");
			condition.put("status", t_bid.Status.WAIT_RELEASING.code);
			break;
		}
		case 2: {// 已放款
			querySQL.append("  (bid.status=:status1 OR bid.status=:status2 OR bid.status=:status3) ");
			countSQL.append("  (bid.status=:status1 OR bid.status=:status2 OR bid.status=:status3) ");
			condition.put("status1", t_bid.Status.REPAYING.code);
			condition.put("status2", t_bid.Status.REPAYED.code);
			condition.put("status3", t_bid.Status.PARTIAL_NORMAL_REPAYMENT.code);
			break;
		}
		}

		// 按借款人姓名模糊查询
		if (StringUtils.isNotBlank(loanName)) {
			querySQL.append(" AND user.name LIKE :loanName ");
			countSQL.append(" AND user.name LIKE :loanName ");
			condition.put("loanName", "%" + loanName + "%");
		}

		if (orderType == 2) { // 借款金额
			querySQL.append(" ORDER BY amount ");
			if (orderValue == 0) {
				querySQL.append(" DESC ");
			}
		} else if (orderType == 7) { // 放款时间
			querySQL.append(" ORDER BY release_time ");
			if (orderValue == 0) {
				querySQL.append(" DESC ");
			}
		} else { // 编号
			querySQL.append(" ORDER BY id ");
			if (orderValue == 0) {
				querySQL.append(" DESC ");
			}
		}

		if (exports == Constants.EXPORT) {
			PageBean<BackFinanceBid> page = new PageBean<BackFinanceBid>();
			page.page = findListBeanBySQL(querySQL.toString(), BackFinanceBid.class, condition);
			return page;
		}

		return pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), BackFinanceBid.class,
				condition);
	}

	/**
	 * 前台-我的财富-我的借款
	 *
	 * @param currPage
	 * @param pageSize
	 * @param userId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月13日
	 */
	public PageBean<FrontMyLoanBid> pageOfBidFront(int currPage, int pageSize, long userId) {

		/**
		 * SELECT bid.id AS id, bid.title AS title, bid.amount AS amount,
		 * bid.apr AS apr, bid.period_unit AS period_unit, bid.period AS period,
		 * bid.repayment_type AS repayment_type, bid.release_time AS
		 * release_time, bid.status AS status, (SELECT COUNT(1) FROM t_bill
		 * WHERE bid_id=bid.id AND status IN
		 * ("+t_bill.Status.NORMAL_REPAYMENT.code+","+t_bill.Status.ADVANCE_PRINCIIPAL_REPAYMENT.code+","+t_bill.Status.OUT_LINE_RECEIVE.code+","+t_bill.Status.OUT_LINE_PRINCIIPAL_RECEIVE.code+"))
		 * AS has_repayment_bill, (SELECT COUNT(1) FROM t_bill WHERE
		 * bid_id=bid.id) AS total_repayment_bill, (SELECT
		 * COUNT(DISTINCT(bid_audit_subject_id)) FROM t_bid_item_user WHERE
		 * bid_id=bid.id) AS has_upload_item, (SELECT COUNT(1) FROM
		 * t_audit_subject_bid WHERE bid_id=bid.id) AS total_upload_item FROM
		 * t_bid bid WHERE bid.user_id=:user_id ORDER BY id DESC
		 */
		String querySQL = "SELECT  bid.id AS id, bid.title AS title,  bid.amount AS amount,  bid.apr AS apr,  bid.period_unit AS period_unit, bid.period AS period, bid.repayment_type AS repayment_type,  bid.release_time AS release_time,  bid.status AS status,  (SELECT COUNT(1) FROM t_bill WHERE bid_id=bid.id AND status IN ("
				+ t_bill.Status.NORMAL_REPAYMENT.code + "," + t_bill.Status.ADVANCE_PRINCIIPAL_REPAYMENT.code + ","
				+ t_bill.Status.OUT_LINE_RECEIVE.code + "," + t_bill.Status.OUT_LINE_PRINCIIPAL_RECEIVE.code
				+ ")) AS has_repayment_bill,  (SELECT COUNT(1) FROM t_bill WHERE bid_id=bid.id) AS total_repayment_bill,  (SELECT COUNT(DISTINCT(bid_audit_subject_id)) FROM t_bid_item_user WHERE bid_id=bid.id) AS has_upload_item,  (SELECT COUNT(1) FROM t_audit_subject_bid WHERE bid_id=bid.id) AS total_upload_item  FROM t_bid bid WHERE bid.user_id=:user_id ORDER BY id DESC ";
		String countSQL = "SELECT COUNT(1) FROM t_bid WHERE user_id=:user_id";

		Map<String, Object> conditionArgs = new HashMap<String, Object>();
		conditionArgs.put("user_id", userId);

		return super.pageOfBeanBySQL(currPage, pageSize, countSQL, querySQL, FrontMyLoanBid.class, conditionArgs);
	}

	/**
	 * 后台-首页-待办事项的统计
	 *
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月14日
	 */
	public Map<String, Object> backCountBidInfo() {
		/**
		 * SELECT (SELECT COUNT(1) FROM t_bid WHERE status =0) AS preAuditing,
		 * (SELECT COUNT(1) FROM t_bid WHERE status =2) AS auditing, (SELECT
		 * COUNT(1) FROM t_bid WHERE status =3) AS waitReleasing FROM DUAL
		 */
		String countSQL = "SELECT IFNULL((SELECT COUNT(1) FROM t_bid WHERE status=:preAuditing), 0) AS preAuditing, IFNULL((SELECT COUNT(1) FROM t_bid WHERE status=:auditing), 0) AS auditing, IFNULL((SELECT COUNT(1) FROM t_bid WHERE status=:waitReleasing), 0) AS waitReleasing FROM DUAL";
		Map<String, Object> conditionArgs = new HashMap<String, Object>();
		conditionArgs.put("preAuditing", Status.PREAUDITING.code);
		conditionArgs.put("auditing", Status.AUDITING.code);
		conditionArgs.put("waitReleasing", Status.WAIT_RELEASING.code);

		return super.findMapBySQL(countSQL, conditionArgs);
	}

	/**
	 * 查询流标的Bid(条件：筹款中；过了投标期限；没有满标)
	 *
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月30日
	 */
	public List<t_bid> queryBidIsFlow() {

		String sql = "SELECT * FROM t_bid WHERE status=:status AND invest_expire_time < :nowTime AND loan_schedule < 100";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", t_bid.Status.FUNDRAISING.code);
		params.put("nowTime", new Date());

		return super.findListBySQL(sql, params);
	}

	/**
	 * 查询标的进度
	 *
	 * @param bidId
	 * @return
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年03月25日
	 */
	public double findBidSchedule(long bidId) {
		String sql = "select loan_schedule from t_bid where id =:bid_id";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("bid_id", bidId);

		return super.findSingleDoubleBySQL(sql, 0.00, params);
	}

	/**
	 * 查询待奖励标的
	 * 
	 * @param rewardGrantType
	 * @return
	 */
	public List<Map<String, Object>> selectRewardBidId(int rewardGrantType) {
		String sql = "select IFNULL( id , 0 ) as bid_id from t_bid where reward_grant_type = :type and status in(:sta);";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", rewardGrantType);
		params.put("sta", t_bid.Status.LOAN);

		return super.findListMapBySQL(sql, params);

	}

	/**
	 * 修改当前标的奖励状态
	 * 
	 * @param bidId
	 * @param type
	 * @param endType
	 * @return
	 */
	public int updateBidRewardGrantType(long bidId, int type, int endType) {
		String sql = " update t_bid set reward_grant_type = :endType where id= :bidId and reward_grant_type = :type ";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", type);
		params.put("endType", endType);
		params.put("bidId", bidId);

		return super.updateBySQL(sql, params);
	}

	/**
	 * 前台-理财
	 * 
	 * @param currPage
	 * @param pageSize
	 * @return
	 * 
	 * @author yaoyi
	 * @createDate 2016年1月14日
	 */
	public PageBean<FrontBid> pageOfBidInvest(int currPage, int pageSize) {
		String querySQL = "SELECT b.product_id,b.guarantee_mode_id,b.id,b.title,b.is_invest_reward,b.has_invested_amount,b.reward_rate,b.apr,b.period_unit,b.period,b.amount,b.loan_schedule,b.status,b.pre_release_time, b.min_invest_amount FROM t_bid b "
				+ "inner join t_product p on b.product_id = p.id WHERE p.type <> :productType and b.status IN (:statusList) "
				+ "ORDER BY CASE WHEN b.status > 1 THEN 2 ELSE 1 END, b.pre_release_time DESC";
		String countSQL = "SELECT COUNT(b.id) FROM t_bid b inner join t_product p on b.product_id = p.id WHERE p.type <> :productType and b.status IN (:statusList)";

		Map<String, Object> conditionArgs = new HashMap<String, Object>();
		conditionArgs.put("statusList", Status.PROCESS);
		conditionArgs.put("productType", ProductType.NEWBIE.code);

		return super.pageOfBeanBySQL(currPage, pageSize, countSQL, querySQL, FrontBid.class, conditionArgs);
	}

	/**
	 * 前台-新手理财
	 * 
	 * @param currPage
	 * @param pageSize
	 * @return
	 * 
	 * @author YanPengFei
	 * @createDate 2017年3月2日
	 */
	public PageBean<FrontBid> pageOfNewbieBidInvest(int currPage, int pageSize) {
		String querySQL = "SELECT b.product_id,b.guarantee_mode_id, b.id,b.min_invest_amount, b.has_invested_amount, b.title,b.is_invest_reward,b.reward_rate,b.apr,b.period_unit,b.period,b.amount,b.loan_schedule,b.status,b.pre_release_time,b.min_invest_amount,b.invest_copies,b.buy_type FROM t_bid b "
				+ "inner join t_product p on b.product_id = p.id WHERE p.type = :productType and b.status IN (:statusList) "
				+ "ORDER BY CASE WHEN b.status > 1 THEN 2 ELSE 1 END, b.pre_release_time DESC";
		String countSQL = "SELECT COUNT(b.id) FROM t_bid b inner join t_product p on b.product_id = p.id WHERE p.type = :productType and b.status IN (:statusList)";

		Map<String, Object> conditionArgs = new HashMap<String, Object>();
		conditionArgs.put("statusList", Status.PROCESS);
		conditionArgs.put("productType", ProductType.NEWBIE.code);

		return super.pageOfBeanBySQL(currPage, pageSize, countSQL, querySQL, FrontBid.class, conditionArgs);
	}

	/**
	 * 查询前台项目数量
	 * 
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月8日
	 */
	public int findFrontBidsNum() {
		String sql = "SELECT COUNT(tb.id) FROM t_bid tb inner join t_product tp on tb.product_id = tp.id WHERE tb.status IN (:status) and tp.type <> :productType";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", t_bid.Status.PROCESS);
		params.put("productType", ProductType.NEWBIE.code);

		return findSingleIntBySQL(sql, 0, params);
	}

	/**
	 * 前台- 分页查询 某个用户的投资特权记录
	 * 
	 * @param currPage
	 *            当前页
	 * @param pageSize
	 *            每页条数
	 * @param userId
	 *            用户userId
	 * @return
	 *
	 * @author liudong
	 * @createDate 2015年12月17日
	 */
	public PageBean<MyPrerogativeRecord> pageOfUserPrerogativeRecord(int currPage, int pageSize, long userId) {

		/***
		 * 
		 * SELECT bid.id AS bid_id, bid.title AS title, bid.amount AS amount,
		 * bid.period AS period, bid.period_unit AS period_unit, bid.apr AS apr,
		 * bid.reward_rate AS reward_rate, bid.repayment_type AS repayment_type,
		 * bid.time AS time, bid.has_invested_amount AS hasInvestedAmount, bid.
		 * STATUS AS STATUS, bid.invest_password AS invest_password FROM t_bid
		 * bid, t_group_menbers gm, t_group_menbers_user gmu WHERE bid.group_id
		 * = gm.id AND gm.id = gmu.group_id AND gmu.user_id = :userId
		 * 
		 */

		StringBuffer countSQL = new StringBuffer(
				"SELECT COUNT(1) FROM t_bid bid, t_group_menbers gm, t_group_menbers_user gmu WHERE bid.group_id = gm.id AND gm.id = gmu.group_id AND gmu.user_id = :userId ");
		StringBuffer querySQL = new StringBuffer(
				"SELECT bid.id AS bid_id, bid.title AS title, bid.amount AS amount,bid.reward_rate AS reward_rate, bid.period AS period, bid.period_unit AS period_unit, bid.apr AS apr, bid.repayment_type AS repayment_type, bid.time AS time, bid.has_invested_amount AS hasInvestedAmount, bid. STATUS AS STATUS, bid.invest_password AS invest_password FROM t_bid bid, t_group_menbers gm, t_group_menbers_user gmu WHERE bid.group_id = gm.id AND gm.id = gmu.group_id AND gmu.user_id = :userId ");

		Map<String, Object> args = new HashMap<String, Object>();
		args.put("userId", userId);

		countSQL.append(" AND bid.status = :status ");
		querySQL.append(" AND bid.status = :status ");
		args.put("status", t_bid.Status.FUNDRAISING.code);

		querySQL.append(" order by bid.id desc");

		return super.pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(),
				MyPrerogativeRecord.class, args);
	}
}
