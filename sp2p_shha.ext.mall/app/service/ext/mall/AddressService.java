package service.ext.mall;

import java.util.Date;
import java.util.List;

import common.utils.Factory;
import common.utils.ResultInfo;
import daos.ext.mall.AddressDao;
import models.ext.mall.entiey.t_mall_address;
import play.db.jpa.JPA;
import services.base.BaseService;

/**
 * 积分商城-收货地址Service
 *
 * @description 
 *
 * @author jiayijian
 * @createDate 2017年3月18日
 */
public class AddressService extends BaseService<t_mall_address>{
	
	protected AddressDao addressDao = Factory.getDao(AddressDao.class);
	
	protected AddressService() {
		super.basedao = addressDao;
	}
	
	/**
	 * 添加地址
	 * @param userId
	 * @param receiver 收货人姓名
	 * @param tel 电话号码
	 * @param province 省
	 * @param city 市
	 * @param address 详细地址
	 * @return
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public ResultInfo addAddress(long userId,String receiver,String tel,String province,String city,String address){
		ResultInfo result = new ResultInfo();
		//状态：0-初始值，1-默认地址
		int status = 0;
		
		t_mall_address da = addressDao.findDefaultAddress(userId);
		if(da == null){
			status =1;
		}
		
		t_mall_address newAddress = new t_mall_address();
		
		newAddress.time = new Date();
		newAddress.user_id = userId;
		newAddress.receiver = receiver;
		newAddress.tel = tel;
		newAddress.area = province+city;
		newAddress.address = address;
		newAddress.status = status;
		
		if(!addressDao.save(newAddress)){
			result.code = -1;
			result.msg = "添加地址失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "添加地址成功";
		result.obj = newAddress;
		return result;
	}
	/**
	 * 查询用户默认地址
	 * @param userId
	 * @return
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public t_mall_address findDefaultAddress(long userId){
		
		return addressDao.findDefaultAddress(userId);
	}
	
	/**
	 * 设置默认地址
	 * @param userId
	 * @param addressId
	 * @return
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public ResultInfo setDefaultAddress(long userId,long addressId){
		ResultInfo result = new ResultInfo();
		
		t_mall_address address = addressDao.findByID(addressId);
		
		if(address == null){
			
			result.code = -1;
			result.msg ="该地址不存在";
			return  result;
		}
		
		if(address.user_id != userId){
			
			result.code = -1;
			result.msg ="地址id错误";
			return  result;
		}
		
		int rowold = addressDao.updateAddressStatus(userId, 0);
		
		if(rowold < 1){
			result.code = -1;
			result.msg = "更新地址状态失败";
			JPA.setRollbackOnly();
			return  result;
		}
		
		int row = addressDao.setDefaultAddress(userId,addressId);
		
		if(row < 1){
			result.code = -1;
			result.msg = "设置默认地址失败";
			JPA.setRollbackOnly();
			return  result;
		}
		
		result.code = 1;
		result.msg = "设置默认地址成功";
		
		return  result;
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
		
		return addressDao.findAddressByUserId(userId);
	}
	
	
	/**
	 * 删除地址
	 * @param userId
	 * @param addressId
	 * @return
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public ResultInfo delAddress(long userId,long addressId){
		ResultInfo result = new ResultInfo();
		t_mall_address address = addressDao.findByID(addressId);
		
		if(address == null){
			result.code = -1;
			result.msg = "该地址不存在";
			return  result;
		}
		
		if(address.user_id != userId){
			result.code = -1;
			result.msg = "地址id错误";
			return  result;
		}
		
		if(address.status == 1){
			result.code = -1;
			result.msg = "默认地址不能删除";
			return  result;
		}
		
		int row = addressDao.delAddress(addressId);
		
		if(row < 1){
			result.code = -1;
			result.msg = "删除地址失败";
			
			JPA.setRollbackOnly();
			return  result;
		}
		
		result.code = 1;
		result.msg = "删除地址成功";
		
		return  result;
	}
	
	
	
	

}
