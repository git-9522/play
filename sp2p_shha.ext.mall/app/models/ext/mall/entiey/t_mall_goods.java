package models.ext.mall.entiey;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Security;
import play.db.jpa.Model;

/**
 * 积分商城-积分商品
 *
 * @author jiayijian
 * @createDate 2017年3月18日
 */
@Entity
public class t_mall_goods extends Model{
	
	/** 添加时间 */
	public Date time;
	
	/** 商品名称 */
	public String name;
	
	/**商品类型：\r\n0-实物；\r\n1-虚拟 ；\r\n0-抽奖*/
	public int type;
	public GoodsType getGoodsType() {
		return GoodsType.getEnum(this.type);
	}
	
	/**消耗积分 */
	public int spend_scroe;
	
	/**库存数量是否无限制 **/
	public boolean is_unlimited_inven;
	
	/**库存数量 */
	public int inventory;
	
	/**个人兑换数量是否无限制 **/
	public boolean is_unlimited_exc_max;
	
	/**个人兑换数量上限 **/
	public int exchange_maximum;
	
	/**商品图片 **/
	public String image_url;
	
	/**商品描述 **/
	public String description;
	
	/**已兑换数量 **/
	public int exchanged_num;
	
	/**物品属性：\r\n0-默认值；\r\n1-红包（元）；\r\n2-现金卷（元）；\r\n3-加息卷（%） **/
	public int attribute;
	public GoodsAttr getGoodsAttr() {
		return GoodsAttr.getEnum(this.attribute);
		
	}
	/**物品属性值 **/
	public double attribute_value;
	
	/**虚拟物品的最低投资限制**/
	public double min_invest_amount;
	
	/**有虚拟物品的效期限 **/
	public int limit_day;
	
	/**启用标识：\r\n0-下架\r\n1-上架 **/
	public boolean is_use;
	
	/**上次修改时间**/
	public Date last_edit_time;
	
	/**最少投资限制*/
	public int min_invest_count;
	
	@Transient
	public String sign;
	public String getSign () {
		
		return Security.addSign(this.id, Constants.MALL_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
	}
	
	/**app加密ID*/
	@Transient
	public String appSign;
	
	public String getAppSign() {
		
		return Security.addSign(this.id, Constants.MALL_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);
	}
	
	/**
	 * 商品类型
	 *
	 * @author YanPengFei
	 * @createDate 2017年2月24日
	 */
	public enum GoodsType {
		/** 0-实物 */
		ENTITY(0, "实物"),
		
		/**1- 虚拟 */
		VIRTUAL(1, "虚拟"),
		
		/**2- 抽奖 */
		DRAW(2, "抽奖");
		
		public int code;
		
		public String value;  
		
		private GoodsType(int code, String value) {
			this.code = code;
			this.value = value;
		}

		public static GoodsType getEnum(int code) {
			GoodsType[] doodsTypeArray = GoodsType.values();
			
			for (GoodsType goodsType: doodsTypeArray) {
				if (goodsType.code == code) {

					return goodsType;
				}
			}
			
			return null;
		}
	}
	
	/**
	 * 物品属性
	 *
	 * @author YanPengFei
	 * @createDate 2017年2月24日
	 */
	public enum GoodsAttr {
		/**0-默认值（实物） */
		DEFAULT(0, "实物"),
		
		/** 1-红包 */
		REDPACKET(1, "红包"),
		
		/** 2-现金卷 */
		CASH(2, "现金卷"),
		
		/** 3-加息卷 */
		VOLUME(3, "加息卷");
		
		public int code;
		
		public String value;  
		
		private GoodsAttr(int code, String value) {
			this.code = code;
			this.value = value;
		}

		public static GoodsAttr getEnum(int code) {
			GoodsAttr[] goodsAttrArray = GoodsAttr.values();
			
			for (GoodsAttr goodsAttr: goodsAttrArray) {
				if (goodsAttr.code == code) {

					return goodsAttr;
				}
			}
			
			return null;
		}
	}

}
