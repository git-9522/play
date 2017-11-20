package models.wechat.entity;

import javax.persistence.Entity;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.WXConstants;
import common.utils.Security;
import play.db.jpa.Model;

/**
 * 菜单实体类
 * 
 * @author liudong
 * @createDate 2016年5月5日
 */
@Entity
public class t_wechat_menu extends Model{
	
	/** 菜单名称  */
	public String name;
	
	/**菜单类型*/
	private int type;
	
	/** 菜单结构 */
	public String menu_structure;
	
	/** click的事件key值  */
	public String _key;
	
	/** view菜单的转发的url地址 */
	public String url;
	
	/** 菜单ID */
	public Integer parent_id;
	
	@Transient
	public String sign;

	public String getSign() {
		String signID = Security.addSign(id, WXConstants.WECHAT_MENU, ConfConst.ENCRYPTION_KEY_DES);
		
		return signID;
	}
	
	public MenuType getType() {
		MenuType menuType = MenuType.getEnum(this.type);
		return menuType;
	}

	public void setType(MenuType menuType) {
		this.type = menuType.code;
	}

	
	/**
	 * 枚举类型： 菜单类型
	 * 
	 * @author liudong
	 * @createDate 2016年5月5日
	 */
	public enum MenuType{
		
		/** 1-click事件  */
		CLICK(1,"click"),
		
		/** 2-view事件 */
		VIEW(2,"view"),
		
		/** 3-扫码推事件 */
		SCANCODE_PUSH(3,"scancode_push"),
		
		/** 4-扫码推事件且弹框 */
		SCANCODE_WAITMSG(4,"scancode_waitmsg"),
		
		/** 5-弹出系统拍照事件 */
		PIC_SYSPHOTO(5,"pic_sysphoto"),
		
		/** 6-弹出拍照或者相册发图事件 */
		PIC_PHOTO_OR_ALBUM(6,"pic_photo_or_album"),
		
		/** 7-弹出微信相册发图器事件 */
		PIC_WEIXIN(7,"pic_weixin"),
		
		/** 8-弹出地理位置事件 */
		LOCATION_SELECT(8,"location_select"),
		
		/** 9-下发消息事件 */
		MEDIA_ID(9,"media_id"),
		
		/** 10-跳转图文消息事件 */
		VIEW_LIMITED(10,"view_limited"),
		;
		
		public int code;
		public String value;
		
		private MenuType(int code, String value) {
			this.code = code;
			this.value = value;
		}
		
		public static MenuType getEnum(int code){
			MenuType[] types = MenuType.values();
			for (MenuType type: types) {
				if (type.code == code) {

					return type;
				}
			}
			
			return null;
		}
	}

}
