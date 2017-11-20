package daos.ext.mall;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daos.base.BaseDao;
import models.ext.mall.entiey.t_mall_address;

/**
 * 积分商城-收货地址Dao
 *
 * @description 
 *
 * @author jiayijian
 * @createDate 2017年3月18日
 */
public class AddressDao extends BaseDao<t_mall_address>{
	
	protected AddressDao(){};
	
	/**
	 * 查询用户默认地址
	 * @param userId
	 * @return
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public t_mall_address findDefaultAddress(long userId){
		
		return this.findByColumn(" user_id = ? and status = 1", userId);
	}
	
	/**
	 * 设置默认地址
	 * @param userId
	 * @param addressId
	 * @return
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public int setDefaultAddress(long userId,long addressId){
		
		String sql = "update t_mall_address set status = :status where user_id = :userId and id = :addressId ";
		Map<String,Object> args = new HashMap<String,Object>();
		args.put("userId", userId);
		args.put("addressId", addressId);
		args.put("status", 1);//状态：0-初始值，1-默认地址
		
		return this.updateBySQL(sql, args);
		
	}
	
	/**
	 * 更新地址状态
	 * @param userId
	 * @param  status 状态：0-初始值，1-默认地址
	 * @return
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public int updateAddressStatus(long userId,int status){
		
		String sql = "update t_mall_address set status = :status where user_id = :userId ";
		Map<String,Object> args = new HashMap<String,Object>();
		args.put("userId", userId);
		args.put("status", status);//状态：0-初始值，1-默认地址
		
		return this.updateBySQL(sql, args);
		
	}
	
	/**
	 * 删除地址
	 * @param addressId
	 * @return
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public int delAddress(long addressId){
		return this.delete(addressId);
	}
	
	/**
	 * 查询用户所有地址
	 * @param userId
	 * @param addressId
	 * @return
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public List<t_mall_address> findAddressByUserId(long userId){
		
		String sql = "select * from t_mall_address where user_id =:userId";
		
		Map<String,Object> args = new HashMap<String,Object>();
		args.put("userId", userId);
		
		return this.findListBySQL(sql, args);
	}

}
