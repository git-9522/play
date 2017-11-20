package services.common;

import java.util.Date;

import common.utils.Factory;
import daos.common.ExpGoldShowRecordDao;
import models.common.entity.t_exp_gold_show_record;
import services.base.BaseService;

public class ExpGoldShowRecordService extends BaseService<t_exp_gold_show_record>{
	
    protected static ExpGoldShowRecordDao expGoldShowRecordDao = Factory.getDao(ExpGoldShowRecordDao.class); 
	
	protected ExpGoldShowRecordService() {
		super.basedao = this.expGoldShowRecordDao;
	}
	
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
		return expGoldShowRecordDao.saveExpGoldShowRecord(user_id, amount, time, remark);
	}
}
