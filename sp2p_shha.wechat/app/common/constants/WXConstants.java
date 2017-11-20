package common.constants;

import play.Play;

/**
 * 微信常用变量
 * 
 * @author liudong
 * @createDate 2016年5月5日
 */
public final class WXConstants {
	
	private WXConstants() {
	}
	
	/** 微信token */
	public static final String TOKEN = Play.configuration.getProperty("wechat.token");
	
	/** 微信appid */
	public static final String APPID = Play.configuration.getProperty("wechat.appId");
	
	/** 微信appSecret */
	public static final String APPSECRET = Play.configuration.getProperty("wechat.appSecret");
	
	/** 微信授权页面回调路径 */
	public static final String REDIRECT_URI = Play.configuration.getProperty("application.baseUrl")+"/wx/userOAuth";
	
	/** 微信端注册页面路径 */
	public static final String WECHAT_REGISTER_URI = Play.configuration.getProperty("application.baseUrl") + "/wx/loginAndRegiste/register.html";
	
	/** 微信端投标页面路径 */
	public static final String WECHAT_INVEST_URI = Play.configuration.getProperty("application.baseUrl") + "/wx/invest/toWechatInvest.html";
	
	/** 文本消息 */
	public static final String MESSAGE_TEXT = "text";
	
	/** 图文消息 */
	public static final String MESSAGE_NEWS = "news";
	
	/** 图片消息 */
	public static final String MESSAGE_IMAGE = "image";
	
	/** 语音消息 */
	public static final String MESSAGE_VOICE = "voice";
	
	/** 音频消息 */
	public static final String MESSAGE_MUSIC = "music";
	
	/** 视频消息 */
	public static final String MESSAGE_VIDEO = "video";
	
	/** 链接消息 */
	public static final String MESSAGE_LINK = "link";
	
	/** 地理位置消息 */
	public static final String MESSAGE_LOCATION = "location";
	
	/** 事件消息 */
	public static final String MESSAGE_EVNET = "event";
	
	/** 关注事件 */
	public static final String EVENT_SUBSCRIBE = "subscribe";
	
	/** 取消关注事件  */
	public static final String EVENT_UNSUBSCRIBE = "unsubscribe";
	
	/** 点击事件 */
	public static final String EVENT_CLICK = "CLICK";
	
	/** 视图事件 */
	public static final String EVENT_VIEW = "VIEW";
	
	/** 图文事件 */
	public static final String EVENT_SCANCODE= "scancode_push";
	
	/** 事件推送群发结果  */
	public static final String EVENT_MASSSENDJOBFINISH = "MASSSENDJOBFINISH";
	
	/** 微信菜单加密id标识:微信菜单  */
	public static final String WECHAT_MENU="wxm";
}
