package daos.fddcontract;

import java.util.HashMap;
import java.util.Map;

import daos.base.BaseDao;
import models.common.entity.t_fdd_contract;
import models.common.entity.t_fdd_user_ca;



public class FddContractDao extends BaseDao<t_fdd_contract>{
	
	/**
	 * 保存法大大合同交易号
	 */
	public int saveFddContract(long user_id,String transaction_id,String contract_id,String customer_id,long bid_id,String download_url,String viewpdf_url){
		String sql = "INSERT INTO t_fdd_contract (user_id,transaction_id,contract_id,customer_id,bid_id,download_url,viewpdf_url) values (:user_id,:transaction_id,:contract_id,:customer_id,:bid_id,:download_url,:viewpdf_url)";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("user_id", user_id);
		param.put("transaction_id", transaction_id);
		param.put("contract_id", contract_id);
		param.put("customer_id", customer_id);
		param.put("bid_id", bid_id);
		param.put("download_url", download_url);
		param.put("viewpdf_url", viewpdf_url);
		return super.updateBySQL(sql, param);
	}
	
	public t_fdd_contract getFddContract(long user_id,long bid_id){
		String sql = "SELECT * FROM t_fdd_contract WHERE user_id = :user_id AND bid_id = :bid_id";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("user_id", user_id);
		param.put("bid_id", bid_id);
		return super.findBySQL(sql, param);	
	}
}
