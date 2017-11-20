package controllers.wechat.wx;

import play.Logger;
import service.wechat.WeChatGongZhongService;
import service.wechat.WeChatMenuService;
import service.wechat.WeChatReceiveMessageService;
import service.wechat.WeChatWebOAuthManageService;

import com.shove.gateway.weixin.gongzhong.vo.weboauth.OauthAccessToken;
import common.constants.WxPageType;
import common.utils.Factory;

import controllers.wechat.WechatBaseController;
import controllers.wechat.front.WechatHomeCtrl;

/**
 * 微信接入、消息接收处理控制器
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年5月5日
 */
public class WxCtrl extends WechatBaseController {
	
	/** 注入 WeChatMenuService */
	protected static WeChatMenuService weChatMenuService = Factory.getService(WeChatMenuService.class);
	
	/**
	 *  微信界面和用户进行沟通入口,包括用户发送消息，点击click事件
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年5月5日
	 */
	public static void checkSignature() throws Exception {
		Logger.info("接入ctrl");
		WeChatReceiveMessageService weChatReceiveMessageService = new WeChatReceiveMessageService();
		// 执行方法
		WeChatGongZhongService.execute(weChatReceiveMessageService);
	}

	/**
	 * 授权回调地址
	 * 
	 * @param
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年5月9日
	 */
	public static void userOAuth() {
		String code = params.get("code");
		String state = params.get("state");

		if (null != state && state.contains("#")) {
			state = state.split("#")[0];
		} 
		// 同意授权
		if (!"authdeny".equals(code)) {
			try {
				OauthAccessToken oauthAccessToken = null;
				//获取用户授权信息
				oauthAccessToken = WeChatWebOAuthManageService.getAccessToken(code);	
				if(oauthAccessToken == null){
					toResultPage(WxPageType.PAGE_FAIL, "您的链接已失效，请重新点击获取");
				}
				
				String refresh_token =  oauthAccessToken.getRefresh_token();
				if(refresh_token == null){
					toResultPage(WxPageType.PAGE_FAIL, "您的链接已失效，请重新点击获取");
				}
				
				int opt = Integer.parseInt(state);
				//进入微信绑定页面
				if(opt == 31){
					WechatHomeCtrl.toBindWxPre(refresh_token);				
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			toResultPage(WxPageType.PAGE_FAIL, "链接错误，请重新点击获取");
		}
		toResultPage(WxPageType.PAGE_FAIL, "您不同意授权");
		
	}

}


