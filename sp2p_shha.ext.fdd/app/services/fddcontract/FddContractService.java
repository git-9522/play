package services.fddcontract;

import common.utils.Factory;
import daos.fddcontract.FddContractDao;
import models.common.entity.t_fdd_contract;
import services.base.BaseService;

public class FddContractService extends BaseService<t_fdd_contract>{
	
	protected static FddContractDao fddContractDao = Factory.getDao(FddContractDao.class); 
	
	public FddContractService(){}
	
	public int saveFddContract(long user_id,String transaction_id,String contract_id,String customer_id,long bidId,String downloadUrl,String viewpdf_url){
		return fddContractDao.saveFddContract(user_id, transaction_id, contract_id, customer_id,bidId,downloadUrl,viewpdf_url);
	}
	
	public t_fdd_contract getFddContract(long user_id,long bid_id){
		return fddContractDao.getFddContract(user_id, bid_id);
	}
}
