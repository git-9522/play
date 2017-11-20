package models.ext.mall.entiey;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Security;
import models.ext.mall.entiey.t_mall_rewards.RewardType;
import play.db.jpa.Model;

/**
 * 积分商城-抽奖中奖记录
 *
 * @author jiayijian
 * @createDate 2017年3月18日
 */
@Entity
public class t_mall_lottery extends Model{
	
	/** 添加时间 */
	public Date time;
	
	/** 用户ID */
	public long user_id;
	
	/** 奖品ID */
	public long reward_id;
	
	/** 奖品名称 */
	public String name;
	
	/**奖品类型：\r\n0-倍数积分；\r\n1-指定积分；\r\n2-红包；\r\n3-现金卷；\r\n4-加息卷*/
	public int type;
	public RewardType getRewardType() {
		return RewardType.getEnum(this.type);
	}
	
	/**奖品类型参数值 **/
	public double type_value;
	
	/**虚拟物品的最低投资限制**/
	public double min_invest_amount;
	
	/**有虚拟物品的效期限 **/
	public int limit_day;
	
	/**商品图片 **/
	public String image_url;
	
	/**消耗积分 */
	public int spend_scroe;
	
	@Transient
	public String sign;
	public String getSign () {
		
		return Security.addSign(this.id, Constants.MALL_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
	}
}
