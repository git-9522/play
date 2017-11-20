package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 体验金转现记录
 *
 * @author yuechuanyang
 * @createDate 2017年10月17日
 */
@Entity
public class t_exp_gold_show_record extends Model{
	
	/** 用户id */
	private long user_id;
	
	/** 转现金额 */
	private double amount;
	
	/** 转现时间 */
	private Date time;
	
	/** 描述 */
	private String remark;

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
