package models.common.bean;

import java.io.Serializable;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Security;
import common.utils.StrUtil;
import models.common.entity.t_bank_card_user;

/**
 * 登陆用户缓存信息
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2016年1月14日
 */
public class CurrUser implements Serializable{
	
	/** 用户ID */
	public long id;
	
	/** 用户名称 */
	public String name;
	
	/** 用户头像 */
	public String photo;
	
	/** 信用等级 */
	public String credit_level;
	
	/** 信用等级图标 */
	public String credit_level_picture;
	
	/** 资金托管账户  */
	public String payment_account = "";
	
	/** 实名认证 */
	public boolean is_real_name = false;
	
	/** 手机认证 */
	public boolean is_mobile = false;
	
	/** 邮箱认证 */
	public boolean is_email_verified = false;
	
	/** 银行卡绑定 */
	public boolean is_bank_card = false;
	
	/** 是否激活 托管有效 */
	public boolean is_actived = false;
	
	/** 绑定银行卡信息 托管有效 */
	public t_bank_card_user bankUser;
	
	/** 是否企业用户 */
	public boolean is_corp;
	
	/**加密ID*/
	public String sign;

	public String getSign() {
		return Security.addSign(this.id, Constants.USER_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
	}

	/** 认证进度:每完成一个认证，增加25%的进度 */
	public int schedule = 0;
	
	public int getSchedule(){
		int sche = 0;
		if (this.is_real_name) {
			sche += 25;
		}
		
		if (this.is_mobile) {
			sche += 25;
		}
		
		if (this.is_email_verified) {
			sche += 25;
		}
		
		if (this.is_bank_card) {
			sche += 25;
		}
		
		return sche;
	}
	
	/** 可用积分 */
	public double score_balance;
	
	/** 是否老用户 */
	public boolean is_old;
	
	public String getTelName() {
		String tName = this.name.substring(2);
		
		return StrUtil.asterisk(tName, 3, 2, 6);
	}
}
