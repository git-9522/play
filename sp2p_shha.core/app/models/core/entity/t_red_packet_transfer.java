package models.core.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 实体:红包转账表
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年2月14日
 */
@Entity
public class t_red_packet_transfer extends Model {

	/** 用户ID */
	public long user_id;
	
	/** 用户红包ID */
	public long red_packet_user_id;
	
	/** 创建时间 */
	public Date time;
	
	/** 红包金额 */
	public double amount;
	
	/** 转账状态：0.未转账、1.转账中、2.已转账 */
	private int status;
	
	/** 完成时间 */
	public Date complete_time;
	
	/** 订单号 */
	public String order_no;
	
	public Status getStatus() {
		Status status = Status.getEnum(this.status);
		
		return status;
	}
	
	public void setStatus(Status status) {
		this.status = status.code;
	}

	/**
	 * 枚举：转账状态
	 *
	 * @description 
	 *
	 * @author YanPengFei
	 * @createDate 2017年2月20日
	 */
	public enum Status {
		
		/** 0:未转账 */
		NO_TRANSFER(0, "未转账"),
		
		/** 1:转账中 */
		TRANSFERING(1, "转账中"),
		
		/** 2:已转账 */
		TRANSFERED(2, "已转账");
		
		public int code;
		
		public String value;  
		
		private Status(int code, String value) {
			this.code = code;
			this.value = value;
		}

		public static Status getEnum(int code) {
			Status[] statusArray = Status.values();
			
			for (Status status: statusArray) {
				if (status.code == code) {
					return status;
				}
			}
			
			return null;
		}
	}
	
}
