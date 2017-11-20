package daos.common;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import daos.base.BaseDao;
import models.common.entity.t_exp_gold_show_record;

public class ExpGoldShowRecordDao extends BaseDao<t_exp_gold_show_record>{
	
	protected ExpGoldShowRecordDao(){};
	
	/**
	 * 插入用户体验金转账记录
	 *
	 * @param user_id 用户id
	 * @param time 时间
	 * @param remark 备注
	 * @param amount 金额
	 * @return
	 *
	 * @author yuechuanyang
	 * @createDate 2017年10月17日
	 */
	public int saveExpGoldShowRecord(long user_id,double amount,Date time,String remark){
		String sql = "INSERT INTO t_exp_gold_show_record (user_id,amount,time,remark) values(:user_id,:amount,:time,:remark)";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("user_id", user_id);
		condition.put("time", time);
		condition.put("remark", remark);
		condition.put("amount", amount);
		return this.updateBySQL(sql, condition);
	}
}
