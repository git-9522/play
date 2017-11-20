package services.common;

import common.utils.Factory;
import daos.common.AmountTotalDao;
import models.common.bean.AmountTotal;
import models.common.entity.t_user_info;
import services.base.BaseService;
/**
 * 
 * @author admin
 * 统计会员
 */
public class AmountTotalService extends BaseService<t_user_info> {

	protected AmountTotalDao amountTotalDao=Factory.getDao(AmountTotalDao.class);
	
	public AmountTotalService(){	
		super.basedao=amountTotalDao;
	}
	public AmountTotal amountTotal(int showType,String userName){
		
		return amountTotalDao.amountTotal(showType ,userName);
	}
}
