package controllers.wechat.back;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.common.entity.t_event_supervisor;
import models.wechat.entity.t_wechat_menu;
import net.sf.json.JSONObject;
import service.wechat.WeChatMenuService;
import service.wechat.WeixinConsultationService;

import common.constants.ConfConst;
import common.constants.Constants;
import common.constants.WXConstants;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.ResultInfo;
import common.utils.Security;

import controllers.common.BackBaseController;

/**
 * 后台-微信
 *
 * @description 
 *
 * @author liudong
 * @createDate 2016年5月11日
 */
public class WechatCtrl extends BackBaseController  {
	
	protected static WeixinConsultationService weixinConsultationService = Factory.getService(WeixinConsultationService.class);
	
	protected static  WeChatMenuService  weChatMenuService = Factory.getService(WeChatMenuService.class);
	
	/**
	 * 进入编辑微信欢迎语
	 *
	 * @return 
	 *
	 * @author liudong
	 * @createDate 2016年5月11日
	 */
	public static void toEditWechatPre() {
		//获取微信关注欢迎语
		String content = weixinConsultationService.findWeixinConsultation("weixin_welcome");
		
		render(content);
	}
	
	/**
	 * 编辑微信欢迎语
	 *
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年5月11日
	 */
	public static void editWechat(String content) {
		ResultInfo result = new ResultInfo();
		result = weixinConsultationService.updateWeixinConsultation("weixin_welcome", content);
		if (result.code < 1) {
			flash.error(result.msg);
		}else{
			//添加管理员事件
			long supervisorId = getCurrentSupervisorId();
			supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.WECHAT_WELOCOME_EDIT, null);
		}

		renderJSON(result);
	}
	
	/**
	 * 微信菜单
	 *
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年5月11日
	 */
	public static void showWechatMenuPre() {
		List<t_wechat_menu> list = weChatMenuService.queryWechatMenus();
		
		render(list);
	}
	
	/**
	 * 进入微信菜单编辑
	 *
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年5月11日
	 */
	public static void toEditWechatMenuPre(String sign) {
		ResultInfo result = new ResultInfo();
		
		//解密sign
		result = Security.decodeSign(sign, WXConstants.WECHAT_MENU, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			error404();
		}
		long id = Long.parseLong((String) result.obj);
		t_wechat_menu menu = weChatMenuService.findByID(id);
		
		render(menu);
	}
	
	/**
	 * 微信菜单编辑
	 *
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年5月11日
	 */
	public static void editWechatMenu(String sign,String name) {
		ResultInfo result = new ResultInfo();
		
		//解密sign
		result = Security.decodeSign(sign, WXConstants.WECHAT_MENU, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			error404();
		}
		long id = Long.parseLong((String) result.obj);
		result = weChatMenuService.updateWechatMenu(id, name);
		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
			flash.error(result.msg);
			
			toEditWechatMenuPre(sign);
		}
		
		//添加管理员事件
		long supervisorId = getCurrentSupervisorId();
		Map<String, String> supervisorEventParam = new HashMap<String, String>(); 
		supervisorEventParam.put("menu_id", id+"");  
		supervisorEventParam.put("menu_name", name);
		supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.WECHAT_MENU_EDIT, supervisorEventParam);
		
		showWechatMenuPre();
	}
	

	/**
	 * 初始化创建菜单
	 * 
	 * @param
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年5月6日
	 */
	public static void createMenu() {
		JSONObject json = weChatMenuService.createMenu();
		
		if("0".equals(json.get("code").toString())){
			//添加管理员事件
			long supervisorId = getCurrentSupervisorId();
			supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.WECHAT_MENU_CREATE, null);
		}

		renderJSON(json);;
	}


}
