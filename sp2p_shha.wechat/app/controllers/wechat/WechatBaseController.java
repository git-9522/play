package controllers.wechat;

import java.util.HashMap;
import java.util.Map;

import models.common.bean.CurrUser;

import org.apache.commons.lang.StringUtils;

import play.cache.Cache;
import play.mvc.Before;
import play.mvc.Scope.Session;

import common.constants.CacheKey;
import common.constants.Constants;
import common.constants.WxPageType;

import controllers.common.BaseController;

/**
 * 微信控制器基类(拦截器优先级参数：6~10)
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年5月6日
 */
public class WechatBaseController extends BaseController {
	
	public static void toResultPage(int pageType,String msg) {
		
		String templateName = "";
		Map<String, String> par = new HashMap<String, String>();
		
		switch (pageType) {
			case WxPageType.PAGE_FAIL:{
				templateName = "/common/resultPage/fail.html";
				par.put("msg", msg);
				break;
			}
			case WxPageType.PAGE_FAIL_PAYMENT:{
				templateName = "/common/resultPage/fail_payment.html";
				par.put("msg", "资金托管还没有开户");
				break;
			}
			case WxPageType.PAGE_FAIL_BINDCAED:{
				templateName = "/common/resultPage/fail_bindcard.html";
				par.put("msg", "还没有绑定银行卡");
				break;
			}
			case WxPageType.PAGE_COMMUNAL_FAIL:{
				templateName = "/common/resultPage/fail_communal.html";
				par.put("msg", msg);
				break;
			}
			case WxPageType.PAGE_SUCC:{
				templateName = "/common/resultPage/succ.html";
				par.put("msg", msg);
				break;
			}
			case WxPageType.PAGE_SUCC_PAYMENT:{
				templateName = "/common/resultPage/succ_payment.html";
				par.put("msg", msg);
				break;
			}
			
			case WxPageType.PAGE_COMMUNAL_SUCC:{
				templateName = "/common/resultPage/succ_communal.html";
				par.put("msg", msg);
				break;
			}
			case WxPageType.PAGE_UPDATE_PASSWORD_SUCC:{
				templateName = "/common/resultPage/succ_update_password.html";
				par.put("msg", msg);
				break;
			}
			case WxPageType.PAGE_FAIL_ACTIVITED:{
				// TODO
				templateName = "/common/resultPage/succ_update_password.html";
				par.put("msg", msg);
				break;
			}
			default:
				break;
		}
		System.out.println(par);
		renderTemplate(templateName, par);
	}
	

	/**
	 * 刷新用户登录凭证
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年5月6日
	 */
	@Before(priority=6) 
	protected static void flushUserToken() {
	
		/** 更新用户登陆凭证 */
		CurrUser currUser = getCurrUser();
		if (currUser != null) {
			String sessionId = Session.current().getId();
			/* 刷新用户凭证有效时间 */
			Cache.set(CacheKey.FRONT_ + sessionId, currUser.id, Constants.CACHE_TIME_MINUS_30);
			/* 刷新用户信息缓存时间 */
			Cache.set(CacheKey.USER_ + currUser.id, currUser, Constants.CACHE_TIME_MINUS_30);
		}
		renderArgs.put("currUser", currUser);
	}
	
	/**
	 * 获取当前登陆用户信息
	 *
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年5月6日
	 */
	public static CurrUser getCurrUser() {
		if (Session.current() == null) {
			
			return null;
		}

		String sessionId = Session.current().getId();
		if(StringUtils.isBlank(sessionId)) {
			
			return null;
		}
		
		Object userId = Cache.get(CacheKey.FRONT_ + sessionId);	
		if(userId == null){
			
			return null;
		}
		
		CurrUser currUser = (CurrUser)Cache.get(CacheKey.USER_ + userId);
		if(currUser == null) {
			
			return null;
		}
		
		return currUser;
	}
	
}
