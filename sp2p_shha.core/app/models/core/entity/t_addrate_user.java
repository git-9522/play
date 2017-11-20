package models.core.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Security;
import play.db.jpa.Model;

/**
 * 实体:用户加息卷记录
 * 
 * @author jiayijian
 * @createDate 2017年04月06日
 */
@Entity
public class t_addrate_user extends Model{
	
	/** 创建时间 */
	public Date time;
	
	/** 加息卷来原id:0-批量直接发放，其它来自批量定时任务或积分兑换 **/
	public long send_id;
	
	/** 关联用户ID **/
	public long user_id;
	
	/** 加息利率 **/
	public double rate;
	
	/** 加息券类型：1.批量、2.兑换 **/
	public int type;
	
	/** 加息券状态：\r\n0.未使用\r\n、1.使用中\r\n、2.已使用\r\n、3.已过期 **/
	public int status;
	
	/** 锁定时间 */
	public Date lock_time;
	
	/** 最低投资（投资金额必须大于或等于该值） **/
	public double use_rule;
	
	/** 借款期限(月)，0代表无限制 */
	public int bid_period;
	
	/** 到期时间*/
	public Date end_time;
	
	/** 加息券唯一标示 **/
	public String mark;
	
	public RateStatus getRateStatus() {
		RateStatus status = RateStatus.getEnum(this.status);
		
		return status;
	}
	
	public void setRateStatus(RateStatus rateStatus) {
		this.status = rateStatus.code;
	}
	
	public RateType getRateType() {
		RateType status = RateType.getEnum(this.type);
		
		return status;
	}
	
	public void setRateType(RateType rateType) {
		this.type = rateType.code;
	}
	
	/**
	 * 枚举:加息卷的状态
	 *
	 * @description 
	 *
	 * @author jiayijian
	 * @createDate 2017年4月5日
	 */
	public enum RateStatus {
		
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
		
		private RateStatus(int code, String value) {
			this.code = code;
			this.value = value;
		}

		public static RateStatus getEnum(int code){
			RateStatus[] dts = RateStatus.values();
			for (RateStatus dt: dts) {
				if (dt.code == code) {
					return dt;
				}
			}
			
			return null;
		}
	}
	
	/**
	 * 枚举:加息卷的类型
	 *
	 * @description 
	 *
	 * @author jiayijian
	 * @createDate 2017年4月5日
	 */
	public enum RateType {
		
		/** 1:批量 */
		BATCH(1, "批量"),
		
		/** 2:兑换 */
		EXCHANGE(2, "兑换"),
		
		/** 3:抽奖 */
		LOTTERY(3, "抽奖");
		
		public int code;
		
		public String value;  
		
		private RateType(int code, String value) {
			this.code = code;
			this.value = value;
		}

		public static RateType getEnum(int code){
			RateType[] dts = RateType.values();
			for (RateType dt: dts) {
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
		
		return Security.addSign(this.id, Constants.RATE_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
	}
	
	@Transient
	public String app_sign;
	
	public String getApp_sign() {
		
		return Security.addSign(this.id, Constants.RATE_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);
	}

}
