package dao.wechat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.wechat.bean.InvestBillBean;
import models.wechat.bean.InvestBillDetailsBean;
import common.utils.PageBean;

import models.core.entity.t_bill_invest;
import models.wechat.bean.ReceiveBillDetailBean;
import models.wechat.bean.ReceiveBillPlanBean;
import daos.core.BillInvestDao;


/**
 * 微信理财账单DAO
 *
 * @description
 *
 * @author Songjia
 * @createDate 2016年5月11日
 */
public class BillWechatInvestDao extends BillInvestDao{

	/**
	 * 不分页查询 ，用户该笔投资的理财账单
	 *
	 * @param investId  投资记录ID
	 * @return
	 *
	 * @author Songjia
	 * @createDate 2016年5月11日
	 */
	public List<InvestBillBean> queryMyBillInvestFrontWx(long investId) {
		String sql = "SELECT tbi.id AS id, tbi.time AS time, ( tbi.receive_corpus + tbi.receive_interest ) AS corpus_interest, tbi.receive_time AS receive_time FROM t_bill_invest tbi, t_bid tb WHERE tbi.bid_id = tb.id AND invest_id =:investId ";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("investId", investId);
		
		return this.findListBeanBySQL(sql, InvestBillBean.class, condition);
	}
	
	
	/**
	 * 微信查询理财账单详情
	 *
	 * @param investBillId 理财账单ID
	 * @return
	 *
	 * @author Songjia
	 * @createDate 2016年5月11日
	 */
	public InvestBillDetailsBean findInvestBillById(long investBillId) {
		String sql = "SELECT tbi.id AS id, tbi.time AS time,tbi.user_id as user_id, tbi.receive_corpus AS receive_corpus, tbi.receive_interest AS receive_interest, (tbi.receive_corpus + tbi.receive_interest) AS corpus_interest,tbi.invest_id AS invest_id, tbi.period AS period, tbi.receive_time AS receive_time, tbi.real_receive_time AS real_receive_time, tbi.status AS status,tb.service_fee_rule AS service_fee_rule, (SELECT COUNT(id)FROM t_bill WHERE bid_id = tbi.bid_id) AS totalPeriod FROM t_bill_invest tbi, t_bid tb WHERE tbi.bid_id = tb.id AND tbi.id =:investBillId ";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("investBillId", investBillId);
		
		return this.findBeanBySQL(sql, InvestBillDetailsBean.class, condition);
	}
		
	/***
	 * 
	 * 账户中心 回款计划
	 * @param userId 用户ID
	 * @param currPage
	 * @param pageSize
	 * @return
	 *
	 * @author luzhiwei
	 * @createDate 2016-05-10
	 */
	public PageBean<ReceiveBillPlanBean> pageOfInvestReceiveBill(long userId, int currPage, int pageSize) {
		String sqlCount = "SELECT COUNT(1) FROM t_bill_invest tbi WHERE  tbi.user_id =:user_id AND tbi.status =:status";
		String querySQL = " SELECT tbi.id AS id, tbi.time AS time, tbi.receive_time AS receiveTime,  tbi.receive_corpus AS receiveCorpus, tbi.receive_interest AS receiveInterest FROM t_bill_invest tbi WHERE  tbi.user_id =:user_id AND tbi.status =:status ORDER BY tbi.receive_time  ";

		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("user_id", userId);
		condition.put("status", t_bill_invest.Status.NO_RECEIVE.code);
		
		return super.pageOfBeanBySQL(currPage, pageSize, sqlCount, querySQL,ReceiveBillPlanBean.class, condition);
	}
		
	/***
	 * 
	 * 账户中心 回款计划 详情
	 * @return
	 * @param billId 理财账单ID
	 * @author luzhiwei
	 * @createDate 2016-05-10
	 */
	public ReceiveBillDetailBean findInvestReceiveBillDeatil(long billId) {
		String querySQL = " SELECT tbi.id AS billInvestNo, tbi.time AS time, tbi.receive_time AS receiveTime, tbi.user_id AS userId, tb.service_fee_rule AS serviceFeeRule, tbi.period AS period,(SELECT COUNT(id) FROM t_bill WHERE bid_id = tbi.bid_id) AS totalPeriod,  tbi.receive_corpus AS receiveCorpus, tbi.receive_interest AS receiveInterest, tbi.status AS status FROM t_bill_invest tbi, t_bid tb WHERE tbi.bid_id = tb.id AND tbi.id =:billId AND tbi.status =:status   ";

		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("billId",billId);
		condition.put("status", t_bill_invest.Status.NO_RECEIVE.code);
		
		return super.findBeanBySQL(querySQL,ReceiveBillDetailBean.class, condition);
	}
}
