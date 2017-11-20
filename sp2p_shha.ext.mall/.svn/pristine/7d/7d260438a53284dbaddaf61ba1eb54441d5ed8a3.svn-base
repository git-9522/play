package models.ext.mall.entiey;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Security;
import play.db.jpa.Model;

/**
 * 积分商城-兑换收货地址
 *
 * @author jiayijian
 * @createDate 2017年3月18日
 */
@Entity
public class t_mall_address extends Model{
	
	/** 添加时间 */
	public Date time;
	
	/** 用户id */
	public long user_id;
	
	/** 收货人姓名 */
	public String receiver;
	
	/** 电话号码 */
	public String tel;
	
	/** 省市地址 */
	public String area;
	
	/** 详细地址 */
	public String address;
	
	/** 状态：0-初始值，1-默认地址 */
	public int status;
	@Transient
	public String getAllAddress(){
		String address = "";
		return address+this.area+"  "+this.address+"  "+this.receiver+"电话   "+this.tel;
	}

	@Transient
	public String getDetailAddress(){
		String address = "";
		return address+this.area+"  "+this.address;
	}
	
	@Transient
	public String sign;
	public String getSign () {
		
		return Security.addSign(this.id, Constants.MALL_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
	}
	
	/**app加密ID*/
	@Transient
	public String appSign;
	
	public String getAppSign() {
		
		return Security.addSign(this.id, Constants.MALL_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);
	}
}
