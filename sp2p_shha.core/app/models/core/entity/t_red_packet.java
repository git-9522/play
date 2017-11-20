package models.core.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Security;
import play.db.jpa.Model;

/**
 * 实体:红包表
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月14日
 */
@Entity
public class t_red_packet extends Model {
	
	public t_red_packet() {
		time = new Date();
		is_use = false;
	}
	
	/** 创建时间 */
	public Date time;
	
	/** 红包类型：1.开户、2.批量 、5.活动 */
	public int type;
	
	/** 金额 */
	public Double amount;
	
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
	
	public RedpacketType getType() {
		RedpacketType typeuse = RedpacketType.getEnum(type);
		
		return typeuse;
	}
	
	public void setStatus(RedpacketType type) {
		this.type = type.code;
	}
	
	/**
	 * 枚举:红包的状态
	 *
	 * @description 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月14日
	 */
	public enum RedpacketType {
		/** 1:开户 */
		REGISTER(1, "开户"),
		
		/** 2:批量 */
		BATCH(2, "批量发放"),
		
		/** 3:兑换 */
		EXCHANGE(3, "兑换"),
		
		/** 4:抽奖 */
		LOTTERY(4, "抽奖"),
		
		/** 5:抽奖 */
		ACTIVITY(5, "领取活动");
		
		public int code;
		
		public String value;
		
		private RedpacketType(int code, String value) {
			this.code = code;
			this.value = value;
		}

		public static RedpacketType getEnum(int code) {
			RedpacketType[] dts = RedpacketType.values();
			
			for (RedpacketType dt: dts) {
				if (dt.code == code) {
					
					return dt;
				}
			}
			
			return null;
		}
	}
	
	/** 是否启用：true.启用、false.不启用 */
	public boolean is_use;
	
	@Transient
	public String sign;
	
	public String getSign() {
		
		return Security.addSign(this.id, Constants.RED_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
	}
	
}
