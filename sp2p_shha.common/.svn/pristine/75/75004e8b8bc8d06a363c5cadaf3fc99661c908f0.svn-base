package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Security;
import play.db.jpa.Model;

@Entity
public class t_moon_cake_lottery_record extends Model {
	
	public t_moon_cake_lottery_record() {
		
	}
	

	public t_moon_cake_lottery_record(long user_id, int period, double award_money) {
		this.time = new Date();
		this.user_id = user_id;
		this.period = period;
		this.award_money = award_money;
	}

	/**
	 * 创建时间
	 */
	public Date time = new Date();
	
	/**
	 * 用户id
	 */
	public long user_id;
	
	/**
	 * 标的期数
	 */
	public int period;
	
	/**
	 * 奖励金额
	 */
	public double award_money;
	
	/**
	 * 状态
	 */
	public boolean status = false;
	
	/**
	 * 发放时间
	 */
	public Date delivery_time;

	@Transient
	public String sign;
	
	public String getSign () {
		return Security.addSign(this.id, Constants.MALL_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
	}
	
}