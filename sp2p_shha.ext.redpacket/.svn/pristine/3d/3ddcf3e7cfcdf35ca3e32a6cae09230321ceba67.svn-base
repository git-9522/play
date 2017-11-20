package controllers.front.account;

import common.utils.Factory;
import common.utils.PageBean;
import controllers.common.FrontBaseController;
import controllers.common.interceptor.AccountInterceptor;
import models.common.bean.CurrUser;
import models.core.entity.t_red_packet_user;
import play.mvc.With;
import services.common.NoticeService;
import services.common.UserInfoService;
import services.core.RedpacketService;
import services.core.RedpacketUserService;

/**
 * 前台-我的财富-我的奖励-红包
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年2月15日
 */
@With(AccountInterceptor.class)
public class MyRedpacketCtrl extends FrontBaseController {

	protected static RedpacketService redpacketService = Factory.getService(RedpacketService.class);
	
	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	
	protected static NoticeService noticeService = Factory.getService(NoticeService.class);
	
	protected static RedpacketUserService redpacketUserService = Factory.getService(RedpacketUserService.class);
	
	/**
	 * 前台-我的财富-我的奖励-红包
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月15日
	 */
	public static void showMyRedPacketPre() {
		render();
	}
	
	 /**
	  * 前台-我的财富-我的奖励-红包(未使用)
	  * 
	  * @param currPage
	  */
	public static void showUnusedRedPacketPre(int currPage) {
		if (currPage <= 0) {
			currPage = 1;
		}
		
		CurrUser currUser = getCurrUser();
		PageBean<t_red_packet_user> pageBean = redpacketUserService.pageOfUserRed(currUser.id, t_red_packet_user.RedpacketStatus.UNUSED.code, currPage, 8);
		render(pageBean);
	}
	
	/**
	  * 前台-我的财富-我的奖励-红包(已使用)
	  * 
	  * @param currPage
	  */
	public static void showUsedRedPacketPre(int currPage) {
		if (currPage <= 0) {
			currPage = 1;
		}
		
		CurrUser currUser = getCurrUser();
		PageBean<t_red_packet_user> pageBean = redpacketUserService.pageOfUserRed(currUser.id, t_red_packet_user.RedpacketStatus.USED.code, currPage, 8);
		render(pageBean);
	}
	
	/**
	  * 前台-我的财富-我的奖励-红包(过期)
	  * 
	  * @param currPage
	  */
	public static void showExpiredRedPacketPre(int currPage) {
		if (currPage <= 0) {
			currPage = 1;
		}
		
		CurrUser currUser = getCurrUser();
		PageBean<t_red_packet_user> pageBean = redpacketUserService.pageOfUserRed(currUser.id, t_red_packet_user.RedpacketStatus.EXPIRED.code, currPage, 8);
		render(pageBean);
	}
	
}
