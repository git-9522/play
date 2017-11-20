package services.common;

import common.utils.Factory;
import common.utils.PageBean;
import daos.common.UserFilterDao;
import models.common.entity.t_offline_user_filter;
import services.base.BaseService;

public class UserFilterService extends BaseService<t_offline_user_filter> {

	
	protected static UserFilterDao userFilterDao = Factory.getDao(UserFilterDao.class);

	public UserFilterService() {
		super.basedao = userFilterDao;
	}
	//添加推广
	public boolean addOfflineUserFilter(String spreaderMobile ,int period1,int period3,int period6) {
				
		return userFilterDao.addOfflineUserFilter( spreaderMobile , period1, period3, period6);
	}

	/** 通过id修改推广人 */
	public int updateOfflineUserFilter(String spreaderMobile ,int period1,int period3,int period6,Long id) {
		return userFilterDao.updateOfflineUserFilter(spreaderMobile ,period1,period3,period6,id);
	}

	/** 通过id删除推广人 */
	public int delOfflineUserFilter(Long id) {
		return userFilterDao.delOfflineUserFilter(id);
	}

	/** 通过手机号查询推广人 */
	public t_offline_user_filter findById(Long id) {

		return userFilterDao.findById(id);
	}
	
	/**
	 * 是不是线下用户
	 * @param userId
	 * @return true：是   false：否
	 *         
	 */
	public boolean isOfflineUser(Long userId ,int period){
		
		return userFilterDao.isOfflineUser(userId ,period);
	}
	/**通过手机号判断是不是推广人*/
	public boolean isSpreader(String spreaderMobile){
	
		return userFilterDao.isSpreader(spreaderMobile);	
	}
	/**通过手机号判断推广人是否存在*/
	public boolean isAlreadyExist(String spreaderMobile){
	
		return userFilterDao.isAlreadyExist(spreaderMobile);	
	}
	
	/**查询所有*/
	public PageBean<t_offline_user_filter> getAll(int currPage,int pageSize){
		
		return userFilterDao.pageOfAll(currPage==0?1:currPage, pageSize==0?10:pageSize);
	}
}
