package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Security;
import play.db.jpa.Model;

/**
 * 会员分组信息表
 * 
 * @description 
 *
 * @author jiayijian
 * @createDate 2017年3月31日
 */
@Entity
public class t_group_menbers extends Model {

	/** 创建时间 */
	public Date time;

	/** 分组名称 */
	public String name;

	/**会员人数 */
	public int menber_count;

	/** 上次修改时间' */
	public Date last_edit_time;
	
	/**加密ID*/
	@Transient
	public String sign;

	public String getSign() {
		
		return Security.addSign(this.id, Constants.USER_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
	}
}
