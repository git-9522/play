package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 体验金类型
 *
 * @author yuechuanyang
 * @createDate 2017年10月17日
 */
@Entity
public class t_experience_gold extends Model{
	
	/** 创建时间 */
	private Date time;
	
	/** 金额 */
	private double amount;
	
	/** 描述 */
	private String remark;

	private int is_use;
	
	private double invest_amount;
	
	private double seven_day_rate;
	
	
	
	public double getInvest_amount() {
		return invest_amount;
	}

	public void setInvest_amount(double invest_amount) {
		this.invest_amount = invest_amount;
	}

	public double getSeven_day_rate() {
		return seven_day_rate;
	}

	public void setSeven_day_rate(double seven_day_rate) {
		this.seven_day_rate = seven_day_rate;
	}

	public int getIs_use() {
		return is_use;
	}

	public void setIs_use(int is_use) {
		this.is_use = is_use;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
}
