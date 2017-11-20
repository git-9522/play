package models.core.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Security;
import models.core.entity.t_cash.CashType;
import play.db.jpa.Model;

/**
 * 实体:用户现金券表
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年2月14日
 */
@Entity
public class t_cash_user extends Model {
	
	/** 创建时间 */
	public Date time;
	
	/** 使用规则 */
	public double use_rule;
	
	/** 使用规则:借款期限(月)，0代表无限制 */
	public int bid_period;
	
	/** 到期时间 */
	public Date end_time;
	
	/** 现金券ID */
	public long cash_id;
	
	/** 现金券唯一标识 */
	public String mark;
	
	/** 用户的id */
	public Long user_id;
	
	/** 现金券金额 */
	public double amount;
	
	/** 现金券状态：0-未使用、1-使用中、2-已使用、3-已过期 */
	private int status;
	
	/** 锁定时间*/
	public Date lock_time ;
	
	public CashStatus getStatus() {
		CashStatus statuse = CashStatus.getEnum(status);
		
		return statuse;
	}
	
	public void setStatus(CashStatus status) {
		this.status = status.code;
	}

	/**
	 * 枚举:现金券的状态
	 *
	 * @description 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月14日
	 */
	public enum CashStatus {

		/** 0:未使用 */
		UNUSED(0, "未使用"),
		
		/** 1:使用中 */
		USING(1, "使用中"),
		
		/** 2:已使用 */
		USED(2, "已使用"),
		
		/** 3:已过期 */
		EXPIRED(3, "已过期");
		
		public int code;
		
		public String value;  
		
		private CashStatus(int code, String value) {
			this.code = code;
			this.value = value;
		}

		public static CashStatus getEnum(int code) {
			CashStatus[] dts = CashStatus.values();
			
			for (CashStatus dt: dts) {
				if (dt.code == code) {
					
					return dt;
				}
			}
			
			return null;
		}
	}
	
	@Transient
	public String sign;
	
	public String getSign() {
		
		return Security.addSign(this.id, Constants.CASH_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
	}
	
	/** 现金券类型 */
	public int type;
	
	public String getCashType(){
		
		return CashType.getEnum(this.type).value;
	}
	
	@Transient
	public String app_sign;
	
	public String getApp_sign() {
		
		return Security.addSign(this.id, Constants.CASH_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);
	}
	
}
