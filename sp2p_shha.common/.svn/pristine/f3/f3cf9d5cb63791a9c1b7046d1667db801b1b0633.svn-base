package daos.common;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import daos.base.BaseDao;
import models.common.entity.t_exp_gold_account_user;

public class ExpGoldAccountUserDao extends BaseDao<t_exp_gold_account_user>{
	
	protected ExpGoldAccountUserDao(){};
	
	/**
	 * 查询用户体验金账户信息
	 *
	 * @param user_id 用户id
	 * @return
	 *
	 * @author yuechuanyang
	 * @createDate 2017年10月17日
	 */
    public t_exp_gold_account_user queryExpGoldAccountUserByUserId(long user_id){
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("user_id", user_id);
		condition.put("end_time", new Date());
		String sql = "SELECT * FROM t_exp_gold_account_user WHERE user_id = :user_id AND is_use = 0 AND end_time > :end_time";
		return this.findBySQL(sql, condition);
	}
    
    
    /**
	 * 查询用户体验金账户信息
	 *
	 * @param user_id 用户id
	 * @return
	 *
	 * @author yuechuanyang
	 * @createDate 2017年10月17日
	 */
    public t_exp_gold_account_user queryAllExpGoldAccountUserByUserId(long user_id){
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("user_id", user_id);
		String sql = "SELECT * FROM t_exp_gold_account_user WHERE user_id = :user_id";
		return this.findBySQL(sql, condition);
	}
    
    
    /**
	 * 修改体验金账户状态
	 *
	 * @param user_id 用户id
	 * @return
	 *
	 * @author yuechuanyang
	 * @createDate 2017年10月17日
	 */
    public int updateExpGoldAccountUserByUserId(long user_id,int is_use){
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("user_id", user_id);
		condition.put("is_use", is_use);
		String sql = "UPDATE t_exp_gold_account_user SET is_use = :is_use WHERE user_id = :user_id";
		return this.updateBySQL(sql, condition);
	}
    
    /**
	 * 插入用户体验金账户
	 *
	 * @param user_id 用户id
	 * @param amount 金额
	 * @param create_time 创建时间
	 * @param end_time 过期时间
	 * @param is_use 是否可用
	 * @param remark 备注
	 * @return
	 *
	 * @author yuechuanyang
	 * @createDate 2017年10月17日
	 */
	public int saveExpGoldAccountUser(long user_id,double amount,Date create_time,Date end_time,int is_use,String remark,double invest_amount,double seven_day_rate){
		String sql = "INSERT INTO t_exp_gold_account_user (user_id,amount,create_time,end_time,is_use,remark,invest_amount,seven_day_rate) values(:user_id,:amount,:create_time,:end_time,:is_use,:remark,:invest_amount,:seven_day_rate)";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("user_id", user_id);
		condition.put("amount", amount);
		condition.put("create_time", create_time);
		condition.put("end_time", end_time);
		condition.put("is_use", is_use);
		condition.put("remark", remark);
		condition.put("invest_amount", invest_amount);
		condition.put("seven_day_rate", seven_day_rate);
		return this.updateBySQL(sql, condition);
	} 
}
