package services.fddcontract;

import common.utils.Factory;
import daos.common.AdvertisementDao;
import daos.fddcontract.FddUserCaDao;
import models.common.entity.t_fdd_user_ca;
import services.base.BaseService;

public class FddUserCaService extends BaseService<t_fdd_user_ca>{
	
	protected static FddUserCaDao fddUserCaDao = Factory.getDao(FddUserCaDao.class); 
	
	public int saveFddUserCa(long user_id,String customer_id,String customer_name,String email,String mobile,String id_card){
		return fddUserCaDao.saveFddUserCa(user_id, customer_id, customer_name, email, mobile, id_card);
	}
	
	public t_fdd_user_ca selectFddUserCa(long user_id,String customer_name,String email,String mobile,String id_card){
		return fddUserCaDao.selectFddUserCa(user_id, customer_name, email, mobile, id_card);
	}
}
