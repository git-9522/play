package dao.wechat;

import java.util.HashMap;
import java.util.Map;

import models.core.bean.FrontMyLoanBid;
import models.core.entity.t_bill;
import models.wechat.bean.UserLoanDetail;

import common.utils.PageBean;

import daos.core.BidDao;

/**
 * 微信标的DAO
 *
 * @description
 *
 * @author Songjia
 * @createDate 2016年5月11日
 */
public class BidWechatDao extends BidDao{

	/**
	 * 根据bidId查询bid的详细信息
	 *
	 * @param bidId 标记id
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年6月12日
	 */
	public UserLoanDetail findUserLoanDetail(long bidId) {
		String querySQL = "SELECT bid.id AS id,bid.user_id AS user_id, bid.title AS title, bid.amount AS amount, bid.apr AS apr, bid.period_unit AS period_unit, bid.period AS period, bid.repayment_type AS repayment_type, bid.release_time AS release_time, bid.status AS status, (SELECT COUNT(1) FROM t_bill WHERE bid_id=bid.id AND status IN ("+t_bill.Status.NORMAL_REPAYMENT.code+","+t_bill.Status.ADVANCE_PRINCIIPAL_REPAYMENT.code+","+t_bill.Status.OUT_LINE_RECEIVE.code+","+t_bill.Status.OUT_LINE_PRINCIIPAL_RECEIVE.code+")) AS has_repayment_bill,  (SELECT COUNT(1) FROM t_bill WHERE bid_id=bid.id) AS total_repayment_bill FROM t_bid bid WHERE bid.id= :bidId ";
		
		Map<String, Object> conditionArgs = new HashMap<String, Object>();
		conditionArgs.put("bidId", bidId);
		
		return findBeanBySQL(querySQL, UserLoanDetail.class, conditionArgs);
	}
	
	/**
	 * 微信-我的借款
	 *
	 * @param currPage
	 * @param pageSize
	 * @param userId  用户ID
	 * @return
	 *
	 * @author Songjia
	 * @createDate 2016年5月11日
	 */
	public PageBean<FrontMyLoanBid> pageOfAccountLoan(int currPage, int pageSize, long userId) {
		String querySQL = "SELECT bid.id AS id, bid.title AS title, bid.amount AS amount, bid.apr AS apr, bid.period_unit AS period_unit, bid.period AS period, bid.repayment_type AS repayment_type, bid.release_time AS release_time, bid.status AS status, (SELECT COUNT(1) FROM t_bill WHERE bid_id=bid.id AND status IN ("+t_bill.Status.NORMAL_REPAYMENT.code+","+t_bill.Status.ADVANCE_PRINCIIPAL_REPAYMENT.code+","+t_bill.Status.OUT_LINE_RECEIVE.code+","+t_bill.Status.OUT_LINE_PRINCIIPAL_RECEIVE.code+")) AS has_repayment_bill,  (SELECT COUNT(1) FROM t_bill WHERE bid_id=bid.id) AS total_repayment_bill,  (SELECT COUNT(DISTINCT(bid_audit_subject_id)) FROM t_bid_item_user WHERE bid_id=bid.id) AS has_upload_item,  (SELECT COUNT(1) FROM t_audit_subject_bid WHERE bid_id=bid.id) AS total_upload_item  FROM t_bid bid WHERE bid.user_id=:user_id ORDER BY id DESC ";
		String countSQL = "SELECT COUNT(1) FROM t_bid WHERE user_id=:user_id";
		
		Map<String, Object> conditionArgs = new HashMap<String, Object>();
		conditionArgs.put("user_id", userId);
		
		return super.pageOfBeanBySQL(currPage, pageSize, countSQL, querySQL, FrontMyLoanBid.class, conditionArgs);
	}

}
