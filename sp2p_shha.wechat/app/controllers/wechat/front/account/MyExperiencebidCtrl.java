package controllers.wechat.front.account;

import models.ext.experience.bean.experienceUserInvestRecord;
import models.ext.experience.entity.t_experience_bid_account;
import play.mvc.With;
import service.ext.experiencebid.ExperienceBidAccountService;
import service.ext.experiencebid.ExperienceBidInvestService;

import common.constants.WxPageType;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;

import controllers.wechat.WechatBaseController;
import controllers.wechat.interceptor.AccountWxInterceptor;

/**
 * 微信 -我的账户-我的奖励
 * 
 * @author liudong
 * @createDate 2016年6月13日
 */
@With({AccountWxInterceptor.class})
public class MyExperiencebidCtrl extends WechatBaseController {

	/** 注入体验标投资相关信息 */
	public static ExperienceBidInvestService experienceBidInvestService = Factory.getService(ExperienceBidInvestService.class);
	
	/** 注入体验账户service */
	public static ExperienceBidAccountService experienceBidAccountService = Factory.getService(ExperienceBidAccountService.class);

	/**
	 * 微信-我的奖励-我的体验金账户信息
	 *
	 * @author yaoyi
	 * @createDate 2016年2月18日
	 */
	public static void showMyExperienceGoldWXPre(){

		t_experience_bid_account experienceBidAccount = experienceBidAccountService.findUserExperienceAccount(getCurrUser().id);
		
		PageBean<experienceUserInvestRecord> pageBean = experienceBidInvestService.pageOfExperienceUserInvestRecord(getCurrUser().id, 1, 5);
		
		render(experienceBidAccount, pageBean);
	}
	
	/**
	 * 微信-我的奖励-体验金投标记录
	 *
	 * @author yaoyi
	 * @createDate 2016年2月18日
	 */
	public static void showMyExperienceGoldInvestPre(int currPage, int pageSize){
		if(currPage < 1){
			currPage = 1;
		}
		if(pageSize < 1){
			pageSize = 5;
		}
		
		PageBean<experienceUserInvestRecord> pageBean = experienceBidInvestService.pageOfExperienceUserInvestRecord(getCurrUser().id, currPage, pageSize);
		
		render(pageBean);
	}
	
	/**
	 * 微信-我的奖励-体验金-体验金领取
	 *
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年6月14日
	 */
	public static void getExperienceGold(){
		ResultInfo result = new ResultInfo();

		result = experienceBidAccountService.checkCanGetGold(getCurrUser().id);
		
		if(result.code < 1){
			
			renderJSON(result);
		}
		
	    result = experienceBidAccountService.getExperienceGold(getCurrUser().id);
	    
	    if(result.code < 1){
			LoggerUtil.error(true, "体验金领取失败:%s", result.msg);
			
			renderJSON(result);
		}
	    
		result.code = 1;
		result.msg = "体验金领取成功";
	    
		renderJSON(result);
	}
	
	/**
	 * 微信-我的奖励-体验金-兑换
	 *
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年6月14日
	 */
	public static void applayConversion() {
		ResultInfo result = new ResultInfo();
		result = experienceBidAccountService.applayConversion(getCurrUser().id);
		if (result.code < 1) {

			LoggerUtil.info(true, result.msg);
			
			toResultPage(WxPageType.PAGE_COMMUNAL_FAIL, "申请兑换失败");
		}
		
		toResultPage(WxPageType.PAGE_COMMUNAL_SUCC, "兑换申请成功，平台正在处理！");
	}

}
