package controllers.common;

import java.io.File;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import common.constants.CacheKey;
import common.constants.Constants;
import common.utils.CropImage;
import common.utils.Factory;
import common.utils.ResultInfo;
import models.common.bean.CurrSupervisor;
import models.common.entity.t_supervisor.LockStatus;
import play.Logger;
import play.Play;
import play.cache.Cache;
import play.db.jpa.Blob;
import play.mvc.Before;
import play.mvc.Scope.Session;
import services.common.RightService;
import services.common.SupervisorService;


/**	
 * 后台控制器基类(拦截器优先级参数：6~10)
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2015年12月21日
 */
public class BackBaseController extends BaseController {

	protected static SupervisorService supervisorService = Factory.getService(SupervisorService.class);
	
	protected static RightService rightService = Factory.getService(RightService.class);
	
	/**
	 * 登录拦截
	 */
	@Before(unless = {
			"back.BackLoginCtrl.toBackLoginPre",
			"back.BackLoginCtrl.login",
			"back.BackLoginCtrl.logoutPre"
			},priority=6)
	static void checkLogin() {
		Long supervisorId = getCurrentSupervisorId();
		if (supervisorId == null) {
			boolean isAjax = request.isAjax();
			if (isAjax) {
				ResultInfo result = new ResultInfo();
				result.code = ResultInfo.NOT_LOGIN;
				result.msg = "没有登录，或者登录状态已经失效!请重新登录!";
				
				renderJSON(result);
			} else {
				
				redirect("back.BackLoginCtrl.toBackLoginPre");
			}
		}
	}
	
	/**
	 * 刷新后台登录用户凭证
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月27日
	 */
	@Before(priority=8)
	static void flashLoginToken() {
		/** 更新用户登陆凭证 */
		CurrSupervisor currSupervisor = getCurrSupervisor();
		if (currSupervisor != null) {
			Cache.safeSet(CacheKey.LOGIN_SUPERVISOR_ID+Session.current().getId(), currSupervisor.id, Constants.CACHE_TIME_MINUS_30);
			Cache.safeSet(CacheKey.LOGIN_SUPERVISOR + currSupervisor.id, currSupervisor, Constants.CACHE_TIME_MINUS_30);
		}
	}
	
	/**
	 * 权限拦截
	 */
	@Before(unless = {
			"back.BackLoginCtrl.toBackLoginPre",//进入后台登录页面
			"back.BackLoginCtrl.login",//登录操作
			"back.BackLoginCtrl.logoutPre",//登出操作
			"back.BackHomeCtrl.backHomePre",//进入首页
			"back.BackHomeCtrl.showEchartsDataPre",//后台Echarts数据加载
			},priority=9)
	static void checkRight() {
		
		String action = request.action;
		
		RightService rightService = Factory.getService(RightService.class);
		List<Long> actionsIds = rightService.queryIdsByAction(action);
		
		List<Long> rights = null;
		
		CurrSupervisor currSupervisor = getCurrSupervisor();
		if(currSupervisor == null){
			checkLogin();//直接拦截掉
		}
		rights = currSupervisor.rights;

		boolean hasRight = false;
		if (actionsIds == null || actionsIds.size() == 0) {
			hasRight = false;
		} else {
			if (rights == null || rights.size() < 1) {
				hasRight = false;
			} else {
				for(Long rig : rights){
					for(Long rig2 : actionsIds  ){
						if(rig.longValue() == rig2.longValue()){
							hasRight = true;
							break;
						}
					}
				}
			}
		}
		
		if (!hasRight) {
			Logger.info("权限拦截【action:%s,userId:%s】", action,currSupervisor.id+"");
			boolean isAjax = request.isAjax();
			if(isAjax){
				ResultInfo result = new ResultInfo();
				result.code = ResultInfo.NOT_RIGHT;
				result.msg = "没有权限执行该操作!";
				
				renderJSON(result);
			} else{
				renderArgs.put("noRight", true);
				toNoRightPage(action);
			}
			
			return;
		}
		
	}
	
	private static void toNoRightPage (String action) {
		
		String menu = null;
		String title = null;
		String crumbs = null;

		if(action.startsWith("back.webOperation")){
			
			menu = "common/back/webOperationMain.html";
			title = "运维";
			crumbs = "运维";
		} else if (action.startsWith("back.UserMngCtrl")) {
			
			menu = "common/back/userMain.html";
			title = "会员";
			crumbs = "会员";
		} else if (action.startsWith("back.risk")) {
			
			menu = "common/back/riskMain.html";
			title = "风控";
			crumbs = "风控";
		} else if (action.startsWith("back.finance")) {
			
			menu = "common/back/financeMain.html";
			title = "财务";
			crumbs = "财务";
		} else if (action.startsWith("back.spread.RedpacketCtrl")) {
			
			menu = "back/spread/RedpacketCtrl/redpacketsMain.html";
			title = "推广 | 红包";
			crumbs = "推广>红包";
		} else if (action.startsWith("back.spread.ExperienceBidCtrl")) {
			
			menu = "back/spread/ExperienceBidCtrl/experienceBidMain.html";
			title = "推广 | 体验标";
			crumbs = "推广>体验标";
		} else if (action.startsWith("back.spread.CpsSettingCtrl") || action.startsWith("back.spread.CpsRecordCtrl")) {
			
			menu = "back/spread/cpsMain.html";
			title = "推广 | CPS";
			crumbs = "推广>CPS";
		} else if (action.startsWith("back.spread.WealthCircleSettingCtrl") || action.startsWith("back.spread.WealthCicleRecordCtrl")) {
			
			menu = "back/spread/wealthCircleMain.html";
			title = "推广 | 财富圈";
			crumbs = "推广>财富圈";
		} else if (action.startsWith("back.setting")) {
			
			menu = "common/back/settingMain.html";
			title = "设置";
			crumbs = "设置";
		}  else if (action.startsWith("back.appversion")) {
			
			menu = "common/back/appMain.html";
			title = "APP";
			crumbs = "APP";
		}  else if (action.startsWith("wechat.back")) {
			menu = "wechat/back/WechatCtrl/weixinMain.html";
			title = "微信";
			crumbs = "微信";
		}   else if (action.startsWith("back.BackStatisticsCtrl")) {
			menu = "common/back/UsersStatisticsMain.html";
			title = "统计";
			crumbs = "会员统计";
		} 
		
		else {
			
			menu = "common/supervisorMain.html";
			title = "首页";
			crumbs = "首页";
		}
		
		renderTemplate("common/errors/noRight.html", menu, title, crumbs);
	}
	
	/**
	 * 图片裁剪
	 *
	 * @param imgSrc
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param imageType  图片格式
	 * @param createNewImg 裁剪后是否生成新图片
	 *
	 * @author Songjia
	 * @createDate 2016年4月9日
	 */
	public static void imageCrop(String imgSrc, int x, int y, int w, int h, String imageType, boolean createNewImg){
		ResultInfo result = new ResultInfo();
		
		if (StringUtils.isBlank(imgSrc)) {
			result.code = -1;
			result.msg = "图片路径错误!";
			
			renderJSON(result);
		}
		
		if(x <= 0 && y <= 0 && w <= 0 && h <= 0 ) {
			result.code = -1;
			result.msg = "请选择裁剪尺寸!";
			
			renderJSON(result);
		}
		//处理图片重载多出的字段
		if (imgSrc.contains("?t=")) {
			imgSrc = imgSrc.substring(0, imgSrc.indexOf("?t="));
		}
		String absoluteUrl = null;
		Blob blob = new Blob();
		
		if(imgSrc.startsWith("/public/")) {
			absoluteUrl = Play.applicationPath.getAbsolutePath() + imgSrc;
		} else {
			String uuid = ImagesController.compatibleUuid(imgSrc);
			absoluteUrl = blob.getStore()+File.separator+uuid;
		}
		
		CropImage cropImage = new CropImage(x, y, w, h);
		result = cropImage.crop(absoluteUrl, imageType, createNewImg);
		if(result.code < 0){
			result.code = -1;
			result.msg = "裁剪失败";
			
			renderJSON(result);
		}
		
		result.code = 1;
		result.msg = "裁剪成功";
		
		renderJSON(result);
	}

	/**
	 * 获取当前登录管理员的ID
	 *
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月31日
	 */
	public static Long getCurrentSupervisorId(){
		
		CurrSupervisor currSupervisor =  getCurrSupervisor();
		
		if(currSupervisor != null){
			
			return currSupervisor.id;
		}
		
		return null;
	}
	
	/**
	 * 获取当前登录管理员的相关信息
	 *
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月3日
	 */
	public static CurrSupervisor getCurrSupervisor(){
		if (Session.current() == null) {
			
			return null;
		}

		String sessionId = Session.current().getId();
		if(StringUtils.isBlank(sessionId)) {
			
			return null;
		}
		
		Object supervisorId = Cache.get(CacheKey.LOGIN_SUPERVISOR_ID+sessionId);	
		if(supervisorId == null){
			
			return null;
		}
		
		CurrSupervisor currUser = (CurrSupervisor)Cache.get(CacheKey.LOGIN_SUPERVISOR + supervisorId);
		if(currUser == null) {
			
			return null;
		}
		
		if(LockStatus.STATUS_2_LOCKEDBYSYS.equals(currUser.getLock_status())){
			Cache.safeDelete(CacheKey.LOGIN_SUPERVISOR_ID+session.current().getId());
			Cache.safeDelete(CacheKey.LOGIN_SUPERVISOR + currUser.id);
			
			return null;
		}
		
		return currUser;
	}
	
	/**
	 * 获取当前登录管理员的name
	 *
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月31日
	 */
	public static String getCurrentSupervisorName(){
		CurrSupervisor currSupervisor = getCurrSupervisor();
		if(currSupervisor == null){
		
			return null;
		}
		String supervisorName = currSupervisor.reality_name;
		
		return supervisorName;
	}
	
}
