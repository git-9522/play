package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 体验金领取记录
 *
 * @author yuechuanyang
 * @createDate 2017年10月17日
 */
@Entity
public class t_exp_gold_user_record extends Model{
	
	/** 用户id */
	private long user_id;
	
	/** 体验金id */
	private long exp_gold_id;
	
	/** 领取时间 */
	private Date create_time;
	
	/** 体验金过期时间 */
	private Date end_time;
	
	/** 备注 */
	private String remark;
	
	/** 体验金金额 */
	private double amount;

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public long getExp_gold_id() {
		return exp_gold_id;
	}

	public void setExp_gold_id(long exp_gold_id) {
		this.exp_gold_id = exp_gold_id;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public Date getEnd_time() {
		return end_time;
	}

	public void setEnd_time(Date end_time) {
		this.end_time = end_time;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	
}
