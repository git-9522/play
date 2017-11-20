package services.common;

import java.util.Date;

import common.enums.Client;
import common.utils.Factory;
import common.utils.PageBean;
import daos.common.RechargeUserDao;
import models.common.entity.t_recharge_user;
import services.base.BaseService;

public class RechargeUserService extends BaseService<t_recharge_user> {

	protected RechargeUserDao rechargeUserDao = Factory.getDao(RechargeUserDao.class);
	
	protected RechargeUserService() {
		super.basedao = this.rechargeUserDao;
		
	}
	
	/**
	 * 添加充值记录
	 * @param userId 用户Id
	 * @param serviceOrderNo 业务订单号
	 * @param rechargeAmt 提现金额
	 * @param summary 备注
	 * @param client 登录端
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月19日
	 *
	 */
	public boolean addUserRecharge(long userId, String serviceOrderNo,
			double rechargeAmt, String summary, Client client) {
		t_recharge_user rechargeUser = new t_recharge_user();
		
		rechargeUser.time = new Date();
		rechargeUser.order_no = serviceOrderNo;
		rechargeUser.user_id = userId;
		rechargeUser.amount = rechargeAmt;
		rechargeUser.summary = summary;
		rechargeUser.setStatus(t_recharge_user.Status.FAILED);  //先失败后成功
		rechargeUser.setClient(client);
		
		return rechargeUserDao.save(rechargeUser);
	}
	
	/**
	 * 充值记录
	 * @param currPage
	 * @param pageSize
	 * @param userId
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月16日
	 *
	 */
	public PageBean<t_recharge_user> pageOfDealUser(int currPage,int pageSize, long userId) {

		return rechargeUserDao.pageOfRecharge(currPage, pageSize, userId);
	}

	/**
	 * 统计用户充值次数
	 *
	 * @param userid
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月17日
	 */
	public int countDealOfUser(long userid,t_recharge_user.Status status){
		int count = rechargeUserDao.countByColumn(" user_id=? and status=?", userid,status.code);
		
		return count;
	}
	
	/**
	 * 充值成功，更新相关数据
	 * 
	 * @param serviceOrderNo
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月19日
	 *
	 */
	public int updateRechargeToSuccess(String serviceOrderNo) {

		return rechargeUserDao.updateRechargeToSuccess(serviceOrderNo);
	}
	
	/**
	 * 获取充值金额数据
	 * @param startTime
	 * @param endTime
	 * @param type
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月23日
	 *
	 */
	public double findTotalRechargeByDate(String startTime, String endTime,
			int type) {

		return rechargeUserDao.findTotalRechargeByDate(startTime, endTime, type);
	}

}
