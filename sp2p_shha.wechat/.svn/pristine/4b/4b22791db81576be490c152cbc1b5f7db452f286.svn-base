package service.wechat;

import java.util.HashMap;
import java.util.Map;

import common.utils.Factory;
import common.utils.PageBean;
import dao.wechat.BidWechatDao;
import models.core.bean.FrontMyLoanBid;
import models.core.entity.t_bill;
import models.wechat.bean.UserLoanBasic;
import models.wechat.bean.UserLoanDetail;
import services.core.BidService;

/**
 * 微信标的service
 *
 * @description
 *
 * @author Songjia
 * @createDate 2016年5月11日
 */
public class BidWechatService extends BidService{
	protected BidWechatDao bidWechatDao;
	
	protected BidWechatService() {
		bidWechatDao = Factory.getDao(BidWechatDao.class);
		super.basedao = bidWechatDao;
	}
	
	/**
	 * 根据bidId查询bid的详细信息
	 *
	 * @param bidId 标记id
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年6月12日
	 */
	public UserLoanDetail findUserLoanDetail(long bidId){
		return bidWechatDao.findUserLoanDetail(bidId);
	}
	
	/**
	 * 微信-我的借款
	 *
	 * @param currPage
	 * @param pageSize
	 * @param userId 
	 * @return
	 *
	 * @author Songjia
	 * @createDate 2016年5月7日
	 */
	@Deprecated
	public PageBean<FrontMyLoanBid> pageOfAccountLoan(int currPage, int pageSize, long userId) {
		return bidWechatDao.pageOfAccountLoan(currPage, pageSize, userId);
	}
	
	/**
	 * 分页查询用户的借款(微信端)
	 *
	 * @param currPage 当前第几页
	 * @param pageSize 每页显示的记录数
	 * @param userId 用户id
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年6月13日
	 */
	public PageBean<UserLoanBasic> pageOfMyLoan(int currPage, int pageSize, long userId) {
		if (currPage < 1) {
			currPage = 1;
		}
		if (pageSize < 1) {
			pageSize = 5;
		}
		String querySQL = "SELECT bid.id AS id, bid.title AS title, bid.user_id AS user_id,(SELECT COUNT(1) FROM t_bill WHERE bid_id=bid.id AND status IN ("+t_bill.Status.NORMAL_REPAYMENT.code+","+t_bill.Status.ADVANCE_PRINCIIPAL_REPAYMENT.code+","+t_bill.Status.OUT_LINE_RECEIVE.code+","+t_bill.Status.OUT_LINE_PRINCIIPAL_RECEIVE.code+")) AS has_repayment_bill,  (SELECT COUNT(1) FROM t_bill WHERE bid_id=bid.id) AS total_repayment_bill FROM t_bid bid WHERE bid.user_id = :userId ORDER BY id DESC ";
		String countSQL = "SELECT COUNT(1) FROM t_bid bid WHERE bid.user_id= :userId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);
		
		return bidWechatDao.pageOfBeanBySQL(currPage, pageSize, countSQL, querySQL, UserLoanBasic.class, condition);
	}
	
}
