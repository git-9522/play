package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Security;
import play.db.jpa.Model;

@Entity
public class t_reversal_record extends Model {

	/**
	 * 创建时间
	 */
	public Date time = new Date();
	
	/**
	 * 用户id
	 */
	public long user_id;
	
	/**
	 * 用户名
	 */
	public String user_name;
	
	/**
	 * 投资id
	 */
	public long invest_id;
	
	/**
	 * 投资金额
	 */
	public double invest_money;
	
	/**
	 * 标的期数
	 */
	public int period;
	
	/**
	 * 奖励比例
	 */
	public double scale;
	
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