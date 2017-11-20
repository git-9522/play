package models.core.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Security;
import play.db.jpa.Model;

/**
 * 
 * 实体：加息券
 * 
 * @author pc
 *
 */
@Entity
public class t_addrate extends Model {

	/** 创建时间 */
	public Date time;
	
	/** 加息类型：1.开户、2.兑换、3.抽奖 */
	public int type;
	
	/** 加息利率 **/
	public double rate;
	
	/** 最低投资（投资金额必须大于或等于该值） */
	public double use_rule;
	
	/** 使用规则:借款期限(月)，0代表无限制 */
	public int bid_period;
	
	/** 到期时间 */
	public int end_time;
	
	/** 是否发送站内信 */
	public boolean is_send_letter;
	
	/** 是否发送邮件 */
	public boolean is_send_email;
	
	/** 是否发送短信 */
	public boolean is_send_sms;
	
	/** 是否启用：true.启用、false.不启用 */
	public boolean is_use;
	
	@Transient
	public String sign;
	
	public String getSign() {
		
		return Security.addSign(this.id, Constants.RATE_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
	}
	
}
