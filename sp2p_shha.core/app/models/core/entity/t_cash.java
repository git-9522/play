package models.core.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 实体:现金券表
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月14日
 */
@Entity
public class t_cash extends Model {
	
	/** 创建时间 */
	public Date time;
	
	/** 现金券类型：1.批量 */
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
	
	public CashType getType() {
		CashType typeuse = CashType.getEnum(type);
		
		return typeuse;
	}
	
	public void setType(CashType type) {
		this.type = type.code;
	}
	
	/**
	 * 枚举:现金券的状态
	 *
	 * @description 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月14日
	 */
	public enum CashType {
		/** 1:批量 */
		BATCH(1, "批量"),
		
		/** 2:兑换 */
		EXCHANGE(2, "兑换"),
		
		/** 3:抽奖 */
		LOTTERY(3, "抽奖");
		
		public int code;
		
		public String value;
		
		private CashType(int code, String value) {
			this.code = code;
			this.value = value;
		}

		public static CashType getEnum(int code) {
			CashType[] dts = CashType.values();
			
			for (CashType dt: dts) {
				if (dt.code == code) {
					
					return dt;
				}
			}
			
			return null;
		}
	}
	
}
