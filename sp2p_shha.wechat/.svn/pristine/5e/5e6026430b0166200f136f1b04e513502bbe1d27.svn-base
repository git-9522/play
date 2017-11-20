package dao.wechat;

import java.util.HashMap;
import java.util.Map;

import models.common.entity.t_deal_user;
import models.wechat.bean.WXUserFundInfo;
import daos.common.UserDao;

/**
 * 微信userdao拓展
 * 
 * @author liudong
 * @createDate 2016年5月9日
 */
public class UserWechatDao extends UserDao {
	
	/**
	 * 微信-查询用户资产信息
	 * (包含用户昵称)
	 * 
	 * @param
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年5月9日
	 */
	public WXUserFundInfo findUserFundInfo(String openId) {
		/**
		  SQL:
			SELECT 
				t.id AS id,
				t.name AS name,
				tuf.balance AS balance,
				(IFNULL((SELECT SUM(tbi.receive_interest) FROM t_bill_invest tbi WHERE tbi.user_id = t.id AND tbi.status = 1),0) - 
				IFNULL((SELECT SUM(tdu.amount) FROM t_deal_user tdu WHERE tdu.operation_type =:invest_service_fee AND tdu.user_id = t.id),0) + 
				IFNULL((SELECT SUM(tdu.amount) FROM t_deal_user tdu WHERE tdu.operation_type =:conversion AND tdu.user_id = t.id),0)) AS total_income,
				tuf.freeze AS freeze,tuf.visual_balance AS reward,
				(SELECT IFNULL(SUM(tbi.receive_corpus + tbi.receive_interest),0) FROM t_bill_invest tbi WHERE t.id = tbi.user_id AND tbi.status = 0) AS no_receive,
				(SELECT IFNULL(SUM(tb.repayment_corpus + tb.repayment_interest),0) FROM t_bill tb WHERE t.id = tb.user_id AND tb.status IN (0, 1)) AS no_repayment,
				(SELECT COUNT(1) FROM t_bill_invest tbi WHERE t.id = tbi.user_id AND tbi.status = 0) AS no_payment_count,
				(SELECT COUNT(1) FROM t_invest ti,t_bid tb WHERE ti.user_id = t.id AND tb.id = ti.bid_id AND tb.status IN (4, 5) AND ti.debt_id=0) AS invest_count 
			FROM 
				t_user t INNER JOIN t_user_fund tuf ON t.id = tuf.user_id 
				INNER JOIN t_wechat_bind twb ON tuf.user_id = twb.user_id
			WHERE 
				twb.open_id =:openId  
		 */
		String sql = "SELECT t.id AS id,t.name AS name, tuf.balance AS balance,(IFNULL((SELECT SUM(tbi.receive_interest) FROM t_bill_invest tbi WHERE tbi.user_id = t.id AND tbi.status = 1),0) - IFNULL((SELECT SUM(tdu.amount) FROM t_deal_user tdu WHERE tdu.operation_type =:invest_service_fee AND tdu.user_id = t.id),0) + IFNULL((SELECT SUM(tdu.amount) FROM t_deal_user tdu WHERE tdu.operation_type =:conversion AND tdu.user_id = t.id),0)) AS total_income,tuf.freeze AS freeze,tuf.visual_balance AS reward,(SELECT IFNULL(SUM(tbi.receive_corpus + tbi.receive_interest),0) FROM t_bill_invest tbi WHERE t.id = tbi.user_id AND tbi.status = 0) AS no_receive,(SELECT IFNULL(SUM(tb.repayment_corpus + tb.repayment_interest),0) FROM t_bill tb WHERE t.id = tb.user_id AND tb.status IN (0, 1)) AS no_repayment,(SELECT COUNT(1) FROM t_bill_invest tbi WHERE t.id = tbi.user_id AND tbi.status = 0) AS no_payment_count,(SELECT COUNT(1) FROM t_invest ti, t_bid tb WHERE ti.user_id = t.id AND tb.id = ti.bid_id AND tb.status IN (4, 5) AND ti.debt_id=0) AS invest_count FROM t_user t INNER JOIN t_user_fund tuf ON t.id = tuf.user_id INNER JOIN t_wechat_bind twb ON tuf.user_id = twb.user_id WHERE twb.open_id =:openId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("invest_service_fee", t_deal_user.OperationType.INVEST_SERVICE_FEE.code);
		condition.put("conversion", t_deal_user.OperationType.CONVERSION.code);
		condition.put("openId", openId);
		
		return findBeanBySQL(sql, WXUserFundInfo.class, condition);
	}

}
