package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 签到记录表
 * 
 * @author YanPengFei
 */
@Entity
public class t_sign_in_record extends Model {

	/** 添加时间 */
	public Date time;
	
	/** 用户ID */
	public long user_id;
	
	/** 签到日期 */
	public Date sign_in_date;
	
	/** 状态：0.未签到、1.已签到 */
	private int status;
	
	public enum Status {
		/** 1:未签到 */
		UN_SIGN_IN(1, "未签到"),
		
		/** 2:已签到 */
		ALREADY_SIGN_IN(2, "已签到");
		
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
	
	public Status getStatus() {
		Status status = Status.getEnum(this.status);
		
		return status;
	}
	
	public void setStatus(Status status) {
		this.status = status.code;
	}
	
	/** 连续签到次数 */
	public int number;
	
	/** 赠送积分 */
	public double score;
	
	/** 额外加成积分 */
	public double extra_score;
	
}
