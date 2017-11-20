package service.wechat;

import java.util.HashMap;
import java.util.Map;

import common.utils.Factory;
import common.utils.PageBean;
import dao.wechat.InvestWechatDao;
import models.core.bean.UserInvestRecord;
import models.core.entity.t_bill_invest;
import models.wechat.bean.BidDetailsBean;
import models.wechat.bean.BidLoanUserBean;
import models.wechat.bean.InvestBidBean;
import models.wechat.bean.InvestRecordBean;
import models.wechat.bean.RepaymentRecordBean;
import models.wechat.bean.UserInvestBasic;
import models.wechat.bean.UserInvestDetail;
import services.core.InvestService;

/**
 * 微信投资service
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年5月4日
 */
public class InvestWechatService extends InvestService {

	private InvestWechatDao investWechatDao = null;
	
	protected InvestWechatService() {
		investWechatDao = Factory.getDao(InvestWechatDao.class);
	}

	/***
	 * 项目详情
	 * @param bidId
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-5-5
	 */
	public BidDetailsBean investInfo(long bidId) {
		
		return  investWechatDao.investInfo(bidId);
	}
	
	/***
	 * 借款详情
	 * @param bidId
	 *
	 * @author luzhiwei
	 * @createDate 2016-5-5
	 */
	public BidLoanUserBean findInvestBidsUserInfo(long bidId) {
		
		return  investWechatDao.findInvestBidsUserInfo(bidId);
	}
	
	/**
	 * 根据理财id查询理财相关信息
	 *
	 * @param investId 理财id
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年6月12日
	 */
	public UserInvestDetail findUserInvestDetail(long investId){
		return investWechatDao.findUserInvestDetail(investId);
	}
	
	/***
	 * 投标记录
	 * @param bidId
	 * @return
	 *
	 * @author luzhiwei
	 * @createDate 2016-5-5
	 */
	public PageBean<InvestRecordBean> pageOfInvestRecord(int currPage,int pageSize,long bidId) {

		return  investWechatDao.pageOfInvestBidsRecord(currPage,pageSize,bidId);
	}
	
	/**
	 * 回款计划
	 * @param currPage
	 * @param currPage
	 * @param bidId
	 * @return
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-5-9
	 */
	public PageBean<RepaymentRecordBean> pageOfRepaymentRecord(int currPage,int pageSize,long bidId) {
	
		return  investWechatDao.pageOfRepaymentRecord(currPage,pageSize,bidId);
	}

	/**
	 * 微信-我的理财
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
	public PageBean<UserInvestRecord> pageOfAccountInvest(int currPage, int pageSize, long userId) {
		return  investWechatDao.pageOfAccountInvest(currPage, pageSize, userId);
	}
	
	/**
	 * 微信-我的理财
	 *
	 * @param currPage 当前第几页
	 * @param pageSize 每页显示的记录数
	 * @param userId 用户id
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年6月12日
	 */
	public PageBean<UserInvestBasic> pageOfUserInvest(int currPage, int pageSize, long userId){
		if (currPage < 1) {
			currPage = 1;
		}
		if (pageSize < 1) {
			pageSize = 5;
		}
		String querySQL = "SELECT ti.id AS id, ti.user_id AS user_id, ti.bid_id AS bid_id, tb.title AS title,(SELECT COUNT(1) FROM t_bill_invest tbi WHERE tbi.invest_id = ti.id AND tbi.status IN ("+t_bill_invest.Status.RECEIVED.code+","+t_bill_invest.Status.TRANSFERED.code+") AND tbi.user_id =ti.user_id) AS alreadRepay,(SELECT COUNT(1) FROM t_bill_invest tbi WHERE tbi.invest_id = ti.id AND tbi.user_id =ti.user_id ) AS totalPay FROM t_bid tb, t_invest ti WHERE tb.id = ti.bid_id AND ti.user_id = :userId ORDER BY ti.id DESC";
		String countSQL = "SELECT COUNT(1) FROM t_invest ti WHERE ti.user_id= :userId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);
		
		return investWechatDao.pageOfBeanBySQL(currPage, pageSize, countSQL, querySQL, UserInvestBasic.class, condition);
	}
	
	/**
	 * 散标列表
	 * @param currPage
	 * @param pageSize
	 * @return
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-5-5
	 */
	
	public PageBean<InvestBidBean> pageOfInvestBids (int currPage,int pageSize) {
		
		return	 investWechatDao.pageOfInvestBids(currPage, pageSize);
	}
	
}
