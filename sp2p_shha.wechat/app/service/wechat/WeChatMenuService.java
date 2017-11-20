package service.wechat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.shove.JSONUtils;
import com.shove.gateway.weixin.gongzhong.utils.GongZhongUtils;
import com.shove.gateway.weixin.gongzhong.vo.menu.Menu;
import com.shove.gateway.weixin.gongzhong.vo.menu.SubMenu;

import common.WeChatUtil;
import common.utils.Factory;
import common.utils.ResultInfo;
import dao.wechat.WeChatMenuDao;
import models.wechat.entity.t_wechat_menu;
import net.sf.json.JSONObject;
import services.base.BaseService;

/**
 * 微信菜单service
 * 
 * @author liudong
 * @createDate 2016年5月5日
 */
public class WeChatMenuService extends BaseService<t_wechat_menu> {
	
	protected static WeChatMenuDao weChatMenuDao = Factory.getDao(WeChatMenuDao.class);

	protected WeChatMenuService(){
		super.basedao = weChatMenuDao;
	}

	/** 查询微信菜单url */
	private static final String MENU_GET = "https://api.weixin.qq.com/cgi-bin/menu/get?";
	
	/** 创建微信菜单url */
	private static final String MENU_CREATE = "https://api.weixin.qq.com/cgi-bin/menu/create?";
	
	/** 删除微信菜单url */
	private static final String MENU_DELETE = "https://api.weixin.qq.com/cgi-bin/menu/delete?";
	
	/**
	 * 查询微信菜单
	 * 
	 * @param
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年5月5日
	 */
	public List<Menu> getMenu() {
		String result = GongZhongUtils.sendPost(MENU_GET, "access_token=" + WeChatGongZhongService.getAccessToken());
		JSONObject obj = JSONObject.fromObject(result).getJSONObject("menu");

		return JSONUtils.toList(obj.getJSONArray("button"), Menu.class);
	}

	/**
	 * 删除微信菜单
	 * 
	 * @param
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年5月5日
	 */
	public void deleteMenu() {
		GongZhongUtils.sendPost(MENU_DELETE,"access_token=" + WeChatGongZhongService.getAccessToken());
	}

	/**
	 * 创建微信菜单
	 * 
	 * @param
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年5月5日
	 */
	public JSONObject createMenu() {
		List<Menu> menus = getMenuList();
		
		if ((menus == null) || (menus.size() <= 0)) {
			throw new RuntimeException("菜单不能为空");
		}

		if (menus.size() > 3) {
			throw new RuntimeException("一级菜单数组，个数应为1~3个");
		}

		JSONObject obj = new JSONObject();
		obj.put("button", menus);
		JSONObject json = WeChatUtil.doPostStr(MENU_CREATE + "access_token=" + WeChatGongZhongService.getAccessToken(), obj.toString());
		return json;
	}
	
	/**
	 * 从数据库读取菜单数据封装成Menu对象
	 * 
	 * @param
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年5月6日
	 */
	public List<Menu> getMenuList() {

		/** 创建提交到微信服务器上的根菜单集合 */
		List<Menu> menusList = new ArrayList<Menu>();
		String querySQL = "SELECT * FROM t_wechat_menu WHERE parent_id =:parent_id";
		
		/** 查询一级菜单 */
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("parent_id", "-1");
		
		List<t_wechat_menu> parentMenuList = weChatMenuDao.findListBySQL(querySQL, condition);
		
		if (null != parentMenuList) {
			for (t_wechat_menu menu : parentMenuList) {
				
				Map<String, Object> condition2 = new HashMap<String, Object>();
				condition2.put("parent_id", menu.menu_structure);
				
				List<t_wechat_menu> subMenuList = weChatMenuDao.findListBySQL(querySQL, condition2);
				if (null != subMenuList) {

					/** 创建提交到微信服务器上面的根菜单对象,以及子菜单集合 */
					Menu weiXinParentMenuList = new Menu();
					weiXinParentMenuList.setName(menu.name);
					if (menu.getType().code == 1) {
						weiXinParentMenuList.setType("view");
						weiXinParentMenuList.setUrl(menu.url);
					} else {
						weiXinParentMenuList.setType("click");
					}

					/** 二级菜单拼接 */
					List<SubMenu> weiXinSubMenuList = new ArrayList<SubMenu>();
					for (t_wechat_menu wechat_menu : subMenuList) {
						SubMenu subMenu = new SubMenu();
						subMenu.setName(wechat_menu.name);
						if (wechat_menu.getType().code == 2) {
							subMenu.setType("view");
							subMenu.setUrl(wechat_menu.url);
						} else {
							subMenu.setType("click");
							subMenu.setKey(wechat_menu._key);
						}

						weiXinSubMenuList.add(subMenu);
					}
					
					if(weiXinSubMenuList != null){
						weiXinParentMenuList.setSub_button(weiXinSubMenuList);
					}

					/** 最后添加到menusList中*/
					menusList.add(weiXinParentMenuList);
				}
			}
		}
		return menusList;
	}
	
	/**
	 * 后台微信菜单查询
	 *
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年5月11日
	 */
	public List<t_wechat_menu> queryWechatMenus(){
		
		return weChatMenuDao.findAll();
	}
	
	/**
	 * 后台微信菜单名称修改
	 * 
	 * @param id
	 * @param name 菜单名称
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年5月11日
	 */
	public ResultInfo updateWechatMenu(long id,String name){
		ResultInfo result = new ResultInfo();
		int row = weChatMenuDao.updateWechatMenu(id, name);
		if(row == 1){
			JSONObject json = createMenu();
			if(!"0".equals(json.getString("code"))){
				result.code=-1;
				result.msg = "微信端修改菜单失败";
				return result;
			}
			
			result.code=1;
			result.msg="菜单名称修改成功";
			
			return result;
		}
		
		result.code=-1;
		result.msg="修改数据库菜单失败";
		
		return result;
	}
	
}


