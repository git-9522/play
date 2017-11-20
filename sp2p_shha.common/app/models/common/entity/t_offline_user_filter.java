package models.common.entity;

import javax.persistence.Entity;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Security;
import play.db.jpa.Model;
/**
 * 线下用户筛选，标的周期，屏蔽活动
 * @author admin
 *
 */
@Entity
public class t_offline_user_filter extends Model {
	/**推广人手机*/
	public String spreader_mobile;
	/**一月标*/
	public int period1;
	/**三月标*/
	public int period3;
	/**六月标*/
	public int period6;
	
	
	/**加密ID*/
	@Transient
	public String sign;

	public String getSign() {
		
		return Security.addSign(this.id, Constants.USER_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
	}
}
