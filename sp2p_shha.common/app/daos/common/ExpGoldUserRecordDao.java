package daos.common;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import daos.base.BaseDao;
import models.common.entity.t_exp_gold_user_record;

public class ExpGoldUserRecordDao extends BaseDao<t_exp_gold_user_record>{
	
	protected ExpGoldUserRecordDao(){};
	
	/**
	 * 统计用户体验金领取次数
	 *
	 * @param user_id 用户id
	 * @return
	 *
	 * @author yuechuanyang
	 * @createDate 2017年10月17日
	 */
	public int getCountOfExpGoldUserRecord(long user_id){
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("user_id", user_id);
		String sql = "SELECT COUNT(*) FROM t_exp_gold_user_record WHERE user_id = :user_id";
		return this.countBySQL(sql,condition);
	}
	
	
	/**
	 * 插入用户体验金领取记录表
	 *
	 * @param user_id 用户id
	 * @param exp_gold_id 体验金id
	 * @param create_time 创建时间
	 * @param end_time 过期时间
	 * @param remark 备注
	 * @param amount 金额
	 * @return
	 *
	 * @author yuechuanyang
	 * @createDate 2017年10月17日
	 */
	public int saveExpGoldUserRecord(long user_id,long exp_gold_id,Date create_time,Date end_time,String remark,double amount){
		String sql = "INSERT INTO t_exp_gold_user_record (user_id,exp_gold_id,create_time,end_time,remark,amount) values(:user_id,:exp_gold_id,:create_time,:end_time,:remark,:amount)";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("user_id", user_id);
		condition.put("exp_gold_id", exp_gold_id);
		condition.put("create_time", create_time);
		condition.put("end_time", end_time);
		condition.put("remark", remark);
		condition.put("amount", amount);
		return this.updateBySQL(sql, condition);
	}
}
