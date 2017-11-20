package dao.wechat;

import java.util.HashMap;
import java.util.Map;

import models.core.bean.DebtTransferDetail;
import models.wechat.bean.UserDebtDetail;
import models.wechat.bean.WechatDebtTransferDetail;
import daos.core.DebtTransferDao;

/**
 * 债权转让dao-微信端
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年6月13日
 */
public class DebtTransferWechatDao extends DebtTransferDao {
	
	protected DebtTransferWechatDao() {
	}

	/**
	 * 根据债权id查询债权的详细信息
	 *
	 * @param debtId
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年6月13日
	 */
	public UserDebtDetail findUserDebtDetail(long debtId) {
		String querySQL = "SELECT dt.id AS id, dt.time AS time, dt.invest_id AS invest_id, dt.user_id AS user_id,dt.transaction_user_id as transaction_user_id, dt.title AS title, dt.debt_amount AS debt_amount, dt.debt_principal AS debt_principal, dt.transfer_price AS transfer_price, dt.transfer_period AS transfer_period, dt.status AS status, dt.start_time AS start_time, dt.end_time AS end_time, dt.transaction_time AS transaction_time FROM t_debt_transfer dt  WHERE dt.id= :debtId";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("debtId", debtId);
		
		return findBeanBySQL(querySQL, UserDebtDetail.class, condition);
	}
	/**
	 * 根据债权id查询债权的详细信息
	 *
	 * @param debtId 债权id
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月25日
	 */
	public WechatDebtTransferDetail wechatFindDebtDetailById(long debtId) {
		
		/*
		 SELECT 
				dt.id as id,
				dt.time as time,
				dt.start_time as start_time,
				dt.end_time as end_time,
				dt.invest_id as invest_id,
				b.id as bid_id,
				dt.user_id as user_id,
				b.user_id as loan_user_id,
				u2.name as user_name,
				dt.title as title,
				dt.debt_amount as debt_amount,
				dt.debt_principal as debt_principal,
				dt.transfer_price as transfer_price,
				dt.transfer_period as period,
				dt.status as status,
				(SELECT bi.repayment_time FROM t_bill bi WHERE bi.bid_id=i.bid_id AND bi.status in (0,1) ORDER BY bi.repayment_time LIMIT 1) AS receive_time,
				b.time AS bid_time
			FROM 
				t_debt_transfer dt
				INNER JOIN t_invest i ON dt.invest_id = i.id
				INNER JOIN t_bid b ON b.id = i.bid_id
				INNER JOIN t_user u ON b.user_id = u.id
				INNER JOIN t_user u2 ON u2.id = dt.user_id
			WHERE dt.id=:debutID
		 */
		StringBuffer querySQL = new StringBuffer(" SELECT b.apr AS apr,b.reward_rate AS reward_rate, dt.id as id, dt.time as time,dt.start_time as start_time,dt.end_time as end_time, dt.invest_id as invest_id, b.id as bid_id, dt.user_id as user_id, b.user_id as loan_user_id, u2.name as user_name, dt.title as title, dt.debt_amount as debt_amount, dt.debt_principal as debt_principal, dt.transfer_price as transfer_price, dt.transfer_period as period, dt.status as status, (SELECT bi.repayment_time FROM t_bill bi WHERE bi.bid_id=i.bid_id AND bi.status in (0,1) ORDER BY bi.repayment_time LIMIT 1) AS receive_time,  b.time AS bid_time FROM t_debt_transfer dt INNER JOIN t_invest i ON dt.invest_id = i.id INNER JOIN t_bid b ON b.id = i.bid_id INNER JOIN t_user u ON b.user_id = u.id INNER JOIN t_user u2 ON u2.id = dt.user_id WHERE  dt.id=:debutID ");
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("debutID", debtId);
		
		return findBeanBySQL(querySQL.toString(), WechatDebtTransferDetail.class, condition);
	}
}
