package models.common.entity;

import javax.persistence.Entity;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Security;
import play.db.jpa.Model;

/**
 * 用户表(资产信息)
 *  
 * @description 
 *
 * @author ChenZhipeng
 * @createDate 2015年12月15日
 */
@Entity
public class t_user_fund extends Model {

	/** 用户ID(取t_users表的ID) */
	public long user_id;
	
	/** 用户昵称(冗余自t_users表) */
	public String name;
	
	/** 第三方托管账户 */
	public String payment_account;

	/** 可用余额 */
	public double balance;

	/** 冻结金额 */
	public double freeze;

	/** 虚拟资产(红包+体验金) */
	public double visual_balance;

	/** 用户资金验签 */
	public String fund_sign;
	
	/**加密ID*/
	@Transient
	public String sign;

	public String getSign() {
		return Security.addSign(this.user_id, Constants.USER_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
	}
	
	/** 可用积分 */
	public double score_balance;
	
	/** 是否激活 */
	public boolean is_actived;
	
	/** 是否企业用户  true 企业用户， false 个人用户 */
	public boolean is_corp;
	
}