package controllers.front.account;

import models.common.bean.CurrUser;
import models.core.entity.t_addrate_user;

import common.utils.Factory;
import common.utils.PageBean;

import play.mvc.With;
import services.core.RateService;
import controllers.common.FrontBaseController;
import controllers.common.interceptor.AccountInterceptor;
import controllers.common.interceptor.SimulatedInterceptor;
/**
 * 前台-我的奖励-加息卷
 *
 * @description 
 *
 * @author jiayijian
 * @createDate 2017年04月06日
 */
@With({AccountInterceptor.class,SimulatedInterceptor.class})
public class MyRateCtrl extends FrontBaseController{
	
	protected static RateService rateService = Factory.getService(RateService.class);
	
	 /**
	  * 前台-我的财富-我的奖励-加息卷(未使用)
	  * 
	  * @param currPage
	  */
	public static void showMyRatesPre(){
		
		CurrUser currUser = getCurrUser();
		PageBean<t_addrate_user> pageBean = rateService.pageOfUserRate(currUser.id, t_addrate_user.RateStatus.UNUSED.code, 1, 8);
		render(pageBean);
	}
	
	 /**
	  * 前台-我的财富-我的奖励-加息卷(未使用)
	  * 
	  * @param currPage
	  */
	public static void showUnusedRatePre(int currPage) {
		if (currPage <= 0) {
			currPage = 1;
		}
		
		CurrUser currUser = getCurrUser();
		PageBean<t_addrate_user> pageBean = rateService.pageOfUserRate(currUser.id, t_addrate_user.RateStatus.UNUSED.code, currPage, 8);
		render(pageBean);
	}
	
	/**
	  * 前台-我的财富-我的奖励-加息卷(已使用)
	  * 
	  * @param currPage
	  */
	public static void showUsedRatePre(int currPage) {
		if (currPage <= 0) {
			currPage = 1;
		}
		
		CurrUser currUser = getCurrUser();
		PageBean<t_addrate_user> pageBean = rateService.pageOfUserRate(currUser.id, t_addrate_user.RateStatus.USED.code, currPage, 8);
		render(pageBean);
	}
	
	/**
	  * 前台-我的财富-我的奖励-加息卷(过期)
	  * 
	  * @param currPage
	  */
	public static void showExpiredRatePre(int currPage) {
		if (currPage <= 0) {
			currPage = 1;
		}
		
		CurrUser currUser = getCurrUser();
		PageBean<t_addrate_user> pageBean = rateService.pageOfUserRate(currUser.id, t_addrate_user.RateStatus.EXPIRED.code, currPage, 8);
		render(pageBean);
	} 

}
