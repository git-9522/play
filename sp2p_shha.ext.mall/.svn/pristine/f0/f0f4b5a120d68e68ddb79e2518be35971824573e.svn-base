package models.ext.mall.entiey;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Security;
import play.db.jpa.Model;

/**
 * 积分商城-实物兑换记录
 *
 * @author jiayijian
 * @createDate 2017年3月18日
 */
@Entity
public class t_mall_exchange extends Model{
	
	/** 添加时间 */
	public Date time;
	
	/** 用户ID */
	public long user_id;
	
	/** 商品ID */
	public long goods_id;
	
	/** 商品名称 */
	public String name;
	
	/**商品类型：\r\n0-实物；\r\n1-虚拟；\r\n2-抽奖 */
	public int type;
	
	/**物品属性：\r\n0-默认值(实物)；\r\n1-红包（元）；\r\n2-现金卷（元）；\r\n3-加息卷（%） **/
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
	
	/**商品图片 **/
	public String image_url;
	
	/**商品描述 **/
	public String description;
	
	/**消耗积分 */
	public int spend_scroe;
	
	/**兑换数量 **/
	public int exchange_num;
	
	/**状态：0-待发货，1-已发货 **/
	public int status;
	
	/**发货时间 **/
	public Date delivery_time;
	
	/**收货人 **/
	public String receiver_name;
	
	/**联系电话**/
	public String mobile;
	
	/**收货地址**/
	public String address;
	
	/**快递公司**/
	public String express_company;
	
	/**快递单号**/
	public String tracking_number;
	
	/**唯一键**/
	public String _key;
	
	@Transient
	public String sign;
	public String getSign () {
		
		return Security.addSign(this.id, Constants.MALL_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
	}
	
	/**
	 * 发货状态
	 *
	 * @author jiayijian
	 * @createDate 2017年2月24日
	 */
	public enum Status {
		/** 0-待发货 */
		TOBEDELIVERED(0, "待发货"),
		
		/**1-已发货 */
		DELIVERED(1, "已发货");
		
		public int code;
		
		public String value;  
		
		private Status(int code, String value) {
			this.code = code;
			this.value = value;
		}

		public static Status getEnum(int code) {
			Status[] statusArray = Status.values();
			
			for (Status status: statusArray) {
				if (status.code == code) {

					return status;
				}
			}
			
			return null;
		}
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
		VIRTUAL(1, "虚拟");
		
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
		/**0-默认值 （实物）*/
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
