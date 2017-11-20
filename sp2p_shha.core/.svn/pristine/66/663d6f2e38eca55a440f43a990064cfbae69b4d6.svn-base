package models.core.entity;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.DateUtil;
import common.utils.Security;
import models.core.entity.t_red_packet.RedpacketType;
import play.db.jpa.Model;

/**
 * 实体:用户红包表
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年2月14日
 */
@Entity
public class t_red_packet_user extends Model {
	
	public t_red_packet_user() {
		
	}

	public t_red_packet_user(t_red_packet packet) {
		this.time = new Date();
		this.use_rule = packet.use_rule;
		this.bid_period = packet.bid_period;
		this.end_time = DateUtil.addDay(new Date(), packet.end_time);
		this.red_packet_id = packet.id;
		this.amount = packet.amount;
		this.status = t_red_packet_user.RedpacketStatus.UNUSED.code;
		this.type = RedpacketType.ACTIVITY.code;
	}

	/** 创建时间 */
	public Date time;
	
	/** 使用规则 */
	public double use_rule;
	
	/** 使用规则:借款期限(月)，0代表无限制 */
	public int bid_period;
	
	/** 到期时间 */
	public Date end_time;
	
	/** 红包ID */
	public long red_packet_id;
	
	/** 红包唯一标示 */
	public String mark;
	
	/** 用户的id */
	public Long user_id;
	
	/** 红包的金额 */
	public double amount;
	
	/** 红包状态：0-未使用、1-使用中、2-已使用、3-已过期 */
	private int status;
	
	/** 锁定时间*/
	public Date lock_time ;
	
	public RedpacketStatus getStatus() {
		RedpacketStatus status = RedpacketStatus.getEnum(this.status);
		
		return status;
	}
	
	public void setStatus(RedpacketStatus status) {
		this.status = status.code;
	}

	/**
	 * 枚举:红包的状态
	 *
	 * @description 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月14日
	 */
	public enum RedpacketStatus {
		
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
		
		private RedpacketStatus(int code, String value) {
			this.code = code;
			this.value = value;
		}

		public static RedpacketStatus getEnum(int code){
			RedpacketStatus[] dts = RedpacketStatus.values();
			for (RedpacketStatus dt: dts) {
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
		
		return Security.addSign(this.id, Constants.RED_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
	}
	
	@Transient
	public String app_sign;
	
	public String getApp_sign() {
		
		return Security.addSign(this.id, Constants.RED_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);
		
	}
	
	/** 红包名称 */
	public String name;
	
	/** 红包类型 */
	public int type;
	
	public String getRedType(){
		return RedpacketType.getEnum(this.type).value;
	}
	
}
