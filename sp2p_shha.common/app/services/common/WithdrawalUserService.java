package services.common;

import java.util.Date;

import common.enums.Client;
import common.utils.Factory;
import common.utils.PageBean;
import daos.common.WithdrawalUserDao;
import models.common.entity.t_withdrawal_user;
import services.base.BaseService;

public class WithdrawalUserService extends BaseService<t_withdrawal_user> {

	protected WithdrawalUserDao withdrawalUserDao = Factory.getDao(WithdrawalUserDao.class);
	
	protected WithdrawalUserService() {
		super.basedao = this.withdrawalUserDao;
		
	}
	
	/**
	 * 添加提现记录
	 * @param userId 用户Id
	 * @param serviceOrderNo 业务订单号
	 * @param withdrawalAmt 提现金额
	 * @param bankAccount 银行卡号
	 * @param summary 备注
	 * @param client 登录端
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月19日
	 *
	 */
	public boolean addUserWithdrawal(long userId, String serviceOrderNo,
			double withdrawalAmt, String bankAccount, String summary,
			Client client) {
		t_withdrawal_user withdrawalUser = new t_withdrawal_user();
		
		withdrawalUser.time = new Date();
		withdrawalUser.order_no = serviceOrderNo;
		withdrawalUser.user_id = userId;
		withdrawalUser.amount = withdrawalAmt;
		withdrawalUser.bank_account = bankAccount;
		withdrawalUser.summary = summary;
		withdrawalUser.setStatus(t_withdrawal_user.Status.FAILED);  //先失败后成功
		withdrawalUser.setClient(client);
		
		return withdrawalUserDao.save(withdrawalUser);
	}

	/**
	 * 提现成功，更新相关数据
	 * @param serviceOrderNo
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月19日
	 *
	 */
	public int updateWithdrawalToSuccess(String serviceOrderNo) {

		return withdrawalUserDao.updateWithdrawalToSuccess(serviceOrderNo);
	}

	/**
	 * 提现记录
	 * @param currPage
	 * @param pageSize
	 * @param userId
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月16日
	 *
	 */
	public PageBean<t_withdrawal_user> pageOfDealUser(int currPage,
			int pageSize, long userId) {

		return withdrawalUserDao.pageOfWithdrawal(currPage, pageSize, userId);
	}
}
