package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Security;
import play.db.jpa.Model;

/**
 * 投资抽奖奖品
 * 
 * @description
 *
 * @author ChenZhipeng
 * @createDate 2017年6月24日
 */
@Entity
public class t_invest_reward extends Model {

	/**
	 * 创建时间
	 */
	public Date time;
	
	/**
	 * 奖品名称
	 */
	public String name;
	
	/**
	 * 奖品价值
	 */
	public double value;
	
	/**
	 * 中奖概率
	 */
	public double probability;

	/**
	 * 是否启用
	 */
	public boolean is_use;
	
	/**
	 * 上次修改时间
	 */
	public Date last_edit_time;
	
	@Transient
	public String sign;
	public String getSign () {
		return Security.addSign(this.id, Constants.MALL_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
	}
}