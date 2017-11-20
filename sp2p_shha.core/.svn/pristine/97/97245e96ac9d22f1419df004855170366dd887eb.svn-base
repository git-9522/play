package models.core.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 群发红包定时任务数据实体
 * @author yanpengfei
 * @createDate 2016年12月15日
 */
@Entity
public class t_red_packet_sending extends Model {

	/** 目标用户id */
	public long user_id;
	
	/** 添加时间 */
	public Date time;
	
	/** 名称 */
	public String name;
	
	/** 红包金额 */
	public double amount;
	
	/** 最低投资（投资金额必须大于或等于该值） */
	public double use_rule;
	
	/** 使用规则:借款期限(月)，0代表无限制 */
	public int bid_period;
	
	/** 红包有效时长，单位为天 */
	public int end_time;
	
	/** 是否发送站内信 */
	public boolean is_send_letter;
	
	/** 是否发送邮件 */
	public boolean is_send_email;
	
	/** 是否发送短信 */
	public boolean is_send_sms;
	
	/** 是否发送，0.未发送，1.已发送 */
	public boolean is_send;
	
	/** 发送时间 */
	public Date send_time;
	
	/** 任务表关联（任务标识+用户id） */
	public String task_sign;
}
