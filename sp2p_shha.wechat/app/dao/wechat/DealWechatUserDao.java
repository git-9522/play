package dao.wechat;

import java.util.HashMap;
import java.util.Map;

import models.wechat.bean.DealUserBean;
import models.wechat.bean.DealUserDetailBean;

import common.utils.PageBean;

import daos.common.DealUserDao;

/**
 * 微信交易记录Dao
 *
 * @description 
 *
 * @author Songjia
 * @createDate 2016年5月5日
 */
public class DealWechatUserDao extends DealUserDao{

	/**
	 * 微信端交易记录分页查询
	 *
	 * @param currPage
	 * @param pageSize
	 * @param userId
	 * @return
	 *
	 * @author Songjia
	 * @createDate 2016年5月6日
	 */
	public PageBean<DealUserBean> pageOfWechatDealUser(int currPage, int pageSize, long userId) {
		String querySQL = "SELECT id as id, time as time, deal_type as dealType, amount as amount FROM t_deal_user WHERE user_id=:userId order by id desc ";
		String countSQL = "SELECT COUNT(1) FROM t_deal_user WHERE user_id=:userId  ";
		Map<String, Object> conditionArgs = new HashMap<String, Object>();
		conditionArgs.put("userId", userId);
		return pageOfBeanBySQL(currPage, pageSize, countSQL, querySQL, DealUserBean.class, conditionArgs);
	}
	
	
	/**
	 * 微信端交易记录详情查询
	 *
	 * @param dealRecordId 交易记录ID
	 * @return
	 *
	 * @author Songjia
	 * @createDate 2016年5月6日
	 */
	public DealUserDetailBean dealRecordDetail(long dealRecordId) {
		String querySQL = "SELECT id, time as time,deal_type as dealType ,amount as amount, balance, freeze, order_no as orderNo, summary as summary FROM t_deal_user WHERE id=:dealRecordId";
		
		Map<String, Object>conditionArgs = new HashMap<String, Object>();
		conditionArgs.put("dealRecordId", dealRecordId);
		
		return findBeanBySQL(querySQL, DealUserDetailBean.class, conditionArgs);
	}
	
}
