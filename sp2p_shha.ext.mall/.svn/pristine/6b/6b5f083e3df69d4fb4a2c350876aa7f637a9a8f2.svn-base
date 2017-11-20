package models.ext.mall.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Security;
import models.ext.mall.entiey.t_mall_exchange;

/**
 * 我的财富-资产管理-实物领取
 *
 * @description 
 *
 * @author jiayijian
 * @createDate 2017年3月16日
 */
@Entity
public class UserExchanges implements Serializable{
	
	@Id
	/** 记录id */
	public long id;
	
	/** 商品名称 */
	public String goods_name;
	
	/**兑换数量 **/
	public int exchange_num;
	
	/**消耗积分 */
	public int spend_scroe;
	
	/** 兑换日期 */
	public Date exchange_time;
	
	/**收货地址**/
	public String address;
	
	/**商品图片 **/
	public String image_url;
	
	/**状态：0-待发货，1-已发货 **/
	public int status;
	public t_mall_exchange.Status getStatus() {
		return t_mall_exchange.Status.getEnum(this.status);
	}
	
	@Transient
	public String sign;
	public String getSign () {
		
		return Security.addSign(this.id, Constants.MALL_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
	}

}
