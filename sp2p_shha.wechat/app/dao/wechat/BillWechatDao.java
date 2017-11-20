package dao.wechat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daos.core.BillDao;
import models.wechat.bean.LoanBillBean;
import models.wechat.bean.LoanBillDetailsBean;

/**
 * 微信借款账单DAO
 *
 * @description
 *
 * @author Songjia
 * @createDate 2016年5月11日
 */
public class BillWechatDao extends BillDao{

	/**
	 * 微信借款账单查询
	 *
	 * @param bidId  标的ID
	 * @return
	 *
	 * @author Songjia
	 * @createDate 2016年5月11日
	 */
	public List<LoanBillBean> queryMyBillInvestFrontWx(long bidId) {
		String sql = "SELECT tbi.id AS id, tbi.time AS time, ( tbi.repayment_corpus + tbi.repayment_interest ) AS corpus_interest, tbi.repayment_time AS repayment_time FROM t_bill tbi WHERE tbi.bid_id =:bidId ORDER BY id";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("bidId", bidId);
		
		return this.findListBeanBySQL(sql, LoanBillBean.class, condition);
	}


	/**
	 * 借款账单详情查询
	 *
	 * @param billId 借款账单ID
	 * @return
	 *
	 * @author Songjia
	 * @createDate 2016年5月11日
	 */
	public LoanBillDetailsBean findLoanBillById(long billId) {
		String sql = "SELECT tbi.id AS id, tbi.time AS time, tbi.period AS period, tbi.repayment_corpus AS repayment_corpus, tbi.repayment_interest AS repayment_interest, ( tbi.repayment_corpus + tbi.repayment_interest ) AS corpus_interest, tbi.repayment_time AS repayment_time, tbi.real_repayment_time AS real_repayment_time, tbi.status AS status, tbi.repayment_time AS repayment_time FROM t_bill tbi WHERE tbi.id =:billId";
		Map<String, Object>conditionArgs = new HashMap<String, Object>();
		conditionArgs.put("billId", billId);
		
		return super.findBeanBySQL(sql, LoanBillDetailsBean.class, conditionArgs);
	}
}
