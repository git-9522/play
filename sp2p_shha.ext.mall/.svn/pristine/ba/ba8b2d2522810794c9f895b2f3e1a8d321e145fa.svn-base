package models.ext.mall.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Security;

/**
 * 后台-积分商城-实物商品兑换记录
 *
 * @description 
 *
 * @author jiayijian
 * @createDate 2017年3月16日
 */
@Entity
public class FrontLottery implements Serializable{
	
	@Id
	/** 记录id */
	public long id;
	
	/** 用户名 */
	public String user_name;
	
	/** 奖品名称 */
	public String reward_name;
	
	/** 中奖时间 */
	public Date time;
	
	@Transient
	public String sign;
	public String getSign () {
		
		return Security.addSign(this.id, Constants.MALL_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
	}

}
