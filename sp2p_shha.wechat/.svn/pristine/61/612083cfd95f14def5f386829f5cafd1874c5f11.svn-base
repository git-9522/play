package controllers.wechat.front.account;

import models.common.bean.CurrUser;
import models.core.entity.t_addrate_user;
import models.core.entity.t_cash_user;
import models.core.entity.t_red_packet_user;
import common.utils.Factory;
import common.utils.PageBean;
import play.mvc.With;
import services.common.RechargeUserService;
import services.core.CashUserService;
import services.core.RateService;
import services.core.RedpacketUserService;
import controllers.wechat.WechatBaseController;
import controllers.wechat.interceptor.AccountWxInterceptor;

/**
 * 微信 -我的账户-我的奖励
 * 
 * @author liudong
 * @createDate 2016年6月13日
 */
@With({AccountWxInterceptor.class})
public class MyRewardCtrl extends WechatBaseController {

	protected static RechargeUserService rechargeUserService = Factory.getService(RechargeUserService.class);

	protected static RateService rateService = Factory.getService(RateService.class);

	protected static CashUserService cashUserService = Factory.getService(CashUserService.class);
	
	protected static RedpacketUserService redpacketUserService = Factory.getService(RedpacketUserService.class);
	
	/**
	 * 账户-我的奖励
	 *
	 * @author liudong
	 * @createDate 2016年6月13日
	 */
	public static void awardPre(){
		render();
	}

	/**
	 * 
	 * 展示红包（使用中，未使用，过期）
	 * 
	 * @return
	 * @throws Exception
	 * 
	 */
	public static void showMyRedPacketPre(int currPage, int pagesize) throws Exception {

		if (currPage <= 0) {
			currPage = 1;
		}
		if (pagesize <= 0) {
			pagesize = 100;
		}

		CurrUser currUser = getCurrUser();

		PageBean<t_red_packet_user> used = redpacketUserService.pageOfUserRed(currUser.id, t_red_packet_user.RedpacketStatus.USED.code, currPage, pagesize);
		PageBean<t_red_packet_user> unused = redpacketUserService.pageOfUserRed(currUser.id, t_red_packet_user.RedpacketStatus.UNUSED.code, currPage, pagesize);
		PageBean<t_red_packet_user> expired = redpacketUserService.pageOfUserRed(currUser.id, t_red_packet_user.RedpacketStatus.EXPIRED.code, currPage, pagesize);

		render(used, unused, expired);
	}

	/**
	 * 
	 * 展示加息券（使用中，未使用，过期）
	 * 
	 * @return
	 * @throws Exception
	 * 
	 */
	public static void showMyRatesPre(int currPage, int pagesize) throws Exception {

		if (currPage <= 0) {
			currPage = 1;
		}
		if (pagesize <= 0) {
			pagesize = 100;
		}

		CurrUser currUser = getCurrUser();

		PageBean<t_addrate_user> used = rateService.pageOfUserRate(currUser.id, t_addrate_user.RateStatus.USED.code, currPage, pagesize);
		PageBean<t_addrate_user> unused = rateService.pageOfUserRate(currUser.id, t_addrate_user.RateStatus.UNUSED.code, currPage, pagesize);
		PageBean<t_addrate_user> expired = rateService.pageOfUserRate(currUser.id, t_addrate_user.RateStatus.EXPIRED.code, currPage, pagesize);

		render(used, unused, expired);
	}

	/**
	 * 
	 * 展示现金券（使用中，未使用，过期）
	 * 
	 * @return
	 * @throws Exception
	 * 
	 */
	public static void showMyCashPre(int currPage, int pagesize) throws Exception {
		
		if (currPage <= 0) {
			currPage = 1;
		}
		if (pagesize <= 0) {
			pagesize = 100;
		}

		CurrUser currUser = getCurrUser();

		PageBean<t_cash_user> used = cashUserService.pageOfUserCash(currUser.id, t_cash_user.CashStatus.USED.code, currPage, pagesize);
		PageBean<t_cash_user> unused = cashUserService.pageOfUserCash(currUser.id, t_cash_user.CashStatus.UNUSED.code, currPage, pagesize);
		PageBean<t_cash_user> expired = cashUserService.pageOfUserCash(currUser.id, t_cash_user.CashStatus.EXPIRED.code, currPage, pagesize);

		render(used, unused, expired);
	}
	
}
