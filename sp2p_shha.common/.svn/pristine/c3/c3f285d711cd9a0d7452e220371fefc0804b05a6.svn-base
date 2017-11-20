package services.common;

import java.util.Date;

import common.utils.Factory;
import daos.common.ExpGoldUserRecordDao;
import models.common.entity.t_exp_gold_user_record;
import services.base.BaseService;

public class ExpGoldUserRecordService extends BaseService<t_exp_gold_user_record>{
	
    protected static ExpGoldUserRecordDao expGoldUserRecordDao = Factory.getDao(ExpGoldUserRecordDao.class); 
	
	protected ExpGoldUserRecordService() {
		super.basedao = this.expGoldUserRecordDao;
	}
	
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
		return expGoldUserRecordDao.getCountOfExpGoldUserRecord(user_id);
	}
	
	
	/**
	 * 插入用户体验金领取记录表
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
		return expGoldUserRecordDao.saveExpGoldUserRecord(user_id, exp_gold_id, create_time, end_time, remark, amount);
	}
}
