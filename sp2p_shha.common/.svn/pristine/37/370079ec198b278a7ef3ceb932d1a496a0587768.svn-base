package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Security;
import play.db.jpa.Model;

@Entity
public class t_reversal_rule extends Model {
	
	/**
	 * 创建时间
	 */
	public Date time;
	
	/**
	 * 概率
	 */
	public double probability;
	
	/**
	 * 最小投资金额
	 */
	public double min;
	
	/**
	 * 最大投资金额
	 */
	public double max;
	
	/**
	 * 1 - 5
	 * 当 t_reversal_reward的level一致相关联
	 */
	public int level;
	
	@Transient
	public String sign;
	
	public String getSign () {
		return Security.addSign(this.id, Constants.MALL_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
	}
	
}