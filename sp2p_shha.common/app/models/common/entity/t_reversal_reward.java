package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Security;
import play.db.jpa.Model;

@Entity
public class t_reversal_reward extends Model {
	
	public Date time;
	
	public int period;
	
	public double scale;

	/**
	 * 
	 */
	public int level;

	@Transient
	public String sign;
	
	public String getSign () {
		return Security.addSign(this.id, Constants.MALL_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
	}
	
}