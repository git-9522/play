package daos.ext.cps;


import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import daos.base.BaseDao;
import models.ext.cps.entity.t_cps_user;

/**
 * CPS用户具体实现的dao
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年3月15日
 */
public class CpsUserDao extends BaseDao<t_cps_user> {

	protected CpsUserDao() {

	}
	
	/**
	 * 将某个推广记录的可领取返减少0
	 *
	 * @param cpsId 推广记录id
	 * @param recivable_rebate 减少的金额
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月16日
	 */
	public boolean rebateMinus(long cpsId,double recivable_rebate) {
		
		String sql = "UPDATE t_cps_user SET recivable_rebate=recivable_rebate-:amt WHERE id=:cpsId and recivable_rebate >= :amt";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("amt", recivable_rebate);
		condition.put("cpsId", cpsId);
		
		return (updateBySQL(sql,condition) == 1);
	}
	
	/**
	 * 投标时，更新cps_ser
	 *
	 * @param userId 用户user_id
	 * @param investAmt 投标金额 
	 * @param investRebate 返佣金额
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年3月16日
	 */
	public int updateCpsUserRecord(long userId, double investAmt, double investRebate) {
		
		String sql = "UPDATE t_cps_user SET total_invest=total_invest+:investAmt,total_rebate = total_rebate + :investRebate,recivable_rebate=recivable_rebate + :investRebate WHERE user_id= :userId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("investAmt", investAmt);
		condition.put("investRebate", investRebate);
		condition.put("userId", userId);
		
		return updateBySQL(sql,condition);
	}
	
	/**
	 * 查询累计投资总额 累计推广总额
	 *
	 * @param userName 会员昵称
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年3月16日
	 */
	public Map<String, Object> findTotalCpsAmount(String userName) {
		StringBuffer sql=new StringBuffer("SELECT IFNULL(SUM(tcu.total_invest),0) AS total_invest, IFNULL(SUM(tcu.total_rebate),0) AS total_rebate FROM t_cps_user tcu,t_user tu WHERE tcu.user_id = tu.id ");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		if (StringUtils.isNotBlank(userName)) {
			sql.append(" AND tu.name LIKE :userName ");
			condition.put("userName", "%" + userName + "%");
		}
		
		return  super.findMapBySQL(sql.toString(), condition);
	}
	
	/**
	 * 根据被推广人，查询推广关系
	 *
	 * @param userId 被推广人id
	 * @return
	 *
	 * @author jiayijian
	 * @createDate 2017年3月28日
	 */
	public t_cps_user findCpsUserbyUser(long userId){
		return this.findByColumn(" user_id = ? ", userId);
	} 
	

}
