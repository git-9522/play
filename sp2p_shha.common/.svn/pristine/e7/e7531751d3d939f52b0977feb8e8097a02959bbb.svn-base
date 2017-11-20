package services.common;

import common.utils.Factory;
import common.utils.PageBean;
import daos.common.UsersStatisticsDao;
import models.common.bean.UserStatistics;
import models.common.entity.t_user_info;
import services.base.BaseService;

public class UsersStatisticsService extends BaseService<t_user_info>{
	
	protected UsersStatisticsDao usersStatisticsDao=Factory.getDao(UsersStatisticsDao.class);
	protected UsersStatisticsService(){
		super.basedao= usersStatisticsDao;
	}
	
	public PageBean<UserStatistics> findUserStatistics(int showType, int currPage, int pageSize, int orderType, int orderValue, String userName, int exports){
		
		return usersStatisticsDao.findUserStatistics(showType, currPage, pageSize, orderType,orderValue,  userName, exports);
	}

}
