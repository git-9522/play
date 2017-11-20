package models.core.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 实体:加息卷批量发放记录
 * 
 * @author jiayijian
 * @createDate 2017年04月06日
 */
@Entity
public class t_addrate_sending extends Model{
	
	/** 目标用户id */
	public long user_id;
	
	/** 创建时间 */
	public Date time;
	
	/** 加息利率 **/
	public double rate;
	
	/** 最低投资（投资金额必须大于或等于该值） **/
	public double use_rule;
	
	/** 借款期限(月)，0代表无限制 */
	public int bid_period;
	
	/** 加息券有效时长，单位为天 */
	public int end_time;
	
	/** 是否发送站内信  **/
	public boolean is_send_letter;
	
	/** 否发送邮件  **/
	public boolean is_send_email;
	
	/** 是否发送短信  **/
	public boolean is_send_sms;
	
	/** 是否发送加息券：0.未发送、1.已发送**/
	public int status;
	
	/** 加息券发送时间*/
	public Date send_time;
	
	/** 任务表关联（任务标识+用户id） */
	public String task_sign;
	
	public SendStatus getSendStatus() {
		SendStatus status = SendStatus.getEnum(this.status);
		
		return status;
	}
	
	/**
	 * 枚举:加息卷的状态
	 *
	 * @description 
	 *
	 * @author jiayijian
	 * @createDate 2017年4月5日
	 */
	public enum SendStatus {
		
		/** 0:未发送 */
		UNSEND(0, "未发送"),
		
		/** 1:已发送 */
		HASSEND(2, "已发送");
		
		
		public int code;
		
		public String value;  
		
		private SendStatus(int code, String value) {
			this.code = code;
			this.value = value;
		}

		public static SendStatus getEnum(int code){
			SendStatus[] dts = SendStatus.values();
			for (SendStatus dt: dts) {
				if (dt.code == code) {
					return dt;
				}
			}
			
			return null;
		}
	}
	
}
