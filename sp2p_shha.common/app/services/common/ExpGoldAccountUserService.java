package services.common;


import java.util.Date;

import common.utils.Factory;
import daos.common.ExpGoldAccountUserDao;
import models.common.entity.t_exp_gold_account_user;
import services.base.BaseService;

public class ExpGoldAccountUserService extends BaseService<t_exp_gold_account_user>{
	
    protected static ExpGoldAccountUserDao expGoldAccountUserDao = Factory.getDao(ExpGoldAccountUserDao.class); 
	
	protected ExpGoldAccountUserService() {
		super.basedao = this.expGoldAccountUserDao;
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
	public t_exp_gold_account_user queryExpGoldAccountUserByUserId(long user_id){
		return expGoldAccountUserDao.queryExpGoldAccountUserByUserId(user_id);
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
		return expGoldAccountUserDao.queryAllExpGoldAccountUserByUserId(user_id);
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
		return expGoldAccountUserDao.saveExpGoldAccountUser(user_id, amount, create_time, end_time, is_use, remark,invest_amount,seven_day_rate);
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
		 return expGoldAccountUserDao.updateExpGoldAccountUserByUserId(user_id, is_use);
	 }
}
