package controllers.front.account;

import models.common.bean.CurrUser;
import models.core.entity.t_cash_user;
import common.utils.Factory;
import common.utils.PageBean;
import play.mvc.With;
import services.common.NoticeService;
import services.common.RechargeUserService;
import services.common.UserInfoService;
import services.core.CashService;
import services.core.CashUserService;
import controllers.common.FrontBaseController;
import controllers.common.interceptor.AccountInterceptor;

/**
 * 前台-我的财富-我的奖励-现金券
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年2月15日
 */
@With(AccountInterceptor.class)
public class MyCashCtrl extends FrontBaseController {

	protected static CashService cashService = Factory.getService(CashService.class);
	
	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	
	protected static RechargeUserService rechargeUserService = Factory.getService(RechargeUserService.class);
	
	protected static NoticeService noticeService = Factory.getService(NoticeService.class);
	
	protected static CashUserService cashUserService = Factory.getService(CashUserService.class);
	
	/**
	 * 前台-我的财富-我的奖励-现金券
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月15日
	 */
	public static void showMyCashPre() {
		render();
	}
	
	 /**
	  * 前台-我的财富-我的奖励-现金券(未使用)
	  * 
	  * @param currPage
	  */
	public static void showUnusedCashPre(int currPage) {
		if (currPage <= 0) {
			currPage = 1;
		}
		
		CurrUser currUser = getCurrUser();
		PageBean<t_cash_user> pageBean = cashUserService.pageOfUserCash(currUser.id, t_cash_user.CashStatus.UNUSED.code, currPage, 8);
		render(pageBean);
	}
	
	/**
	  * 前台-我的财富-我的奖励-现金券(已使用)
	  * 
	  * @param currPage
	  */
	public static void showUsedCashPre(int currPage) {
		if (currPage <= 0) {
			currPage = 1;
		}
		
		CurrUser currUser = getCurrUser();
		PageBean<t_cash_user> pageBean = cashUserService.pageOfUserCash(currUser.id, t_cash_user.CashStatus.USED.code, currPage, 8);
		render(pageBean);
	}
	
	/**
	  * 前台-我的财富-我的奖励-现金券(过期)
	  * 
	  * @param currPage
	  */
	public static void showExpiredCashPre(int currPage) {
		if (currPage <= 0) {
			currPage = 1;
		}
		
		CurrUser currUser = getCurrUser();
		PageBean<t_cash_user> pageBean = cashUserService.pageOfUserCash(currUser.id, t_cash_user.CashStatus.EXPIRED.code, currPage, 8);
		render(pageBean);
	} 
	
}
