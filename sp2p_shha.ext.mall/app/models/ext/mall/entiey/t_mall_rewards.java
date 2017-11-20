package models.ext.mall.entiey;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Security;
import play.db.jpa.Model;

/**
 * 积分商城-奖品
 *
 * @author jiayijian
 * @createDate 2017年3月18日
 */
@Entity
public class t_mall_rewards extends Model{
	
	/** 添加时间 */
	public Date time;
	
	/** 奖品名称 */
	public String name;
	
	/**奖品类型：\r\n0-倍数积分；\r\n1-指定积分；\r\n2-红包；\r\n3-现金卷；\r\n4-加息卷；\r\n5-实物*/
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
	
	/**中奖概率 **/
	public double probability;
	
	/**商品图片 **/
	public String image_url;
	
	/**已抽中数量 **/
	public int reward_num;
	
	/**启用标识：\r\n0-下架\r\n1-上架 **/
	public boolean is_use;
	
	/**上次修改时间**/
	public Date last_edit_time;
	
	/** 借款期限(月)，0代表无限制 */
	// 2017-5-23 menghuijia
	public int bid_period = 0;
	
	@Transient
	public String sign;
	public String getSign () {
		
		return Security.addSign(this.id, Constants.MALL_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
	}
	
	/**
	 * 奖品类型
	 *
	 * @author jiayijian
	 * @createDate 2017年2月24日
	 */
	public enum RewardType {
		/** 0-倍数积分 */
		MULTIPLESCORE(0, "倍数积分"),
		
		/**1- 指定积分 */
		SPECIFYSCORE(1, "指定积分"),
		
		/**2- 红包 */
		REDPACKET(2, "红包"),
		
		/**3- 现金卷 */
		CASH(3, "现金卷"),
		
		/**4- 加息卷 */
		RATE(4, "加息卷"),
		
		/**5 - 实物 */
		ENTITY(5, "实物");
		
		public int code;
		
		public String value;  
		
		private RewardType(int code, String value) {
			this.code = code;
			this.value = value;
		}

		public static RewardType getEnum(int code) {
			RewardType[] typeArray = RewardType.values();
			
			for (RewardType type: typeArray) {
				if (type.code == code) {

					return type;
				}
			}
			
			return null;
		}
	}
}
