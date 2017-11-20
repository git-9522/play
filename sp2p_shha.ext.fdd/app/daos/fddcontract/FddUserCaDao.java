package daos.fddcontract;

import java.util.HashMap;
import java.util.Map;

import daos.base.BaseDao;
import models.common.entity.t_fdd_user_ca;




public class FddUserCaDao extends BaseDao<t_fdd_user_ca>{
	
	/**
	 * 保存法大大customerid
	 */
	public int saveFddUserCa(long user_id,String customer_id,String customer_name,String email,String mobile,String id_card){
		String sql = "INSERT INTO t_fdd_user_ca (user_id,customer_id,customer_name,email,mobile,id_card) values (:user_id,:customer_id,:customer_name,:email,:mobile,:id_card)";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("user_id", user_id);
		param.put("customer_id", customer_id);
		param.put("customer_name", customer_name);
		param.put("email", email);
		param.put("mobile", mobile);
		param.put("id_card", id_card);
		return super.updateBySQL(sql, param);
	}
	
	/**
	 * 保存法大大customerid
	 */
	public t_fdd_user_ca selectFddUserCa(long user_id,String customer_name,String email,String mobile,String id_card){
		String sql = "SELECT * FROM t_fdd_user_ca WHERE customer_name = :customer_name AND user_id = :user_id AND id_card = :id_card AND email = :email AND mobile = :mobile";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("user_id", user_id);
		param.put("customer_name", customer_name);
		param.put("email", email);
		param.put("mobile", mobile);
		param.put("id_card", id_card);
		return super.findBySQL(sql, param);
	}
}
