package controllers.common;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import common.constants.CacheKey;
import common.constants.Constants;
import common.utils.Factory;
import models.common.bean.CurrUser;
import play.cache.Cache;
import play.mvc.Before;
import play.mvc.Scope.Session;
import services.common.ColumnService;


/**
 * 前台控制器基类(拦截器优先级参数：6~10)
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2015年12月21日
 */
public class FrontBaseController extends BaseController {
	
	protected static ColumnService columnService = Factory.getService(ColumnService.class);
	
	/**
	 * 更新前台栏目名称同时刷新用户登录凭证(包括模拟登陆标志)
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月14日
	 */
	@Before(priority=6) 
	protected static void frontSetting() {
		/** 前台栏目名称 */
		Map<String, String> columMap = columnService.queryAllColumsMap();
		renderArgs.put("columns", columMap);
		
		/** 更新用户登陆凭证 */
		CurrUser currUser = getCurrUser();
		if (currUser != null) {
			String sessionId = Session.current().getId();
			/* 刷新用户凭证有效时间 */
			Cache.set(CacheKey.FRONT_ + sessionId, currUser.id, Constants.CACHE_TIME_MINUS_30);
			/* 刷新用户信息缓存时间 */
			Cache.set(CacheKey.USER_ + currUser.id, currUser, Constants.CACHE_TIME_MINUS_30);
			
			Object isSimulated = Cache.get(CacheKey.SIMULATED_ + sessionId);	
			if(isSimulated != null){
				Cache.set(CacheKey.SIMULATED_ + sessionId, isSimulated, Constants.CACHE_TIME_MINUS_30);
				renderArgs.put("isSimulated", true);
			}
			
		}
		renderArgs.put("currUser", currUser);
		renderArgs.put("signInDate", new Date());
	}
	
	/**
	 * 获取当前登陆用户信息
	 *
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月14日
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
