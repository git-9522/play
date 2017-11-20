package controllers.wechat.front;

import models.ext.experience.bean.experienceBidInvestRecord;
import models.ext.experience.entity.t_experience_bid;
import models.ext.experience.entity.t_experience_bid_setting;
import play.mvc.With;
import service.ext.experiencebid.ExperienceBidAccountService;
import service.ext.experiencebid.ExperienceBidInvestService;
import service.ext.experiencebid.ExperienceBidService;
import service.ext.experiencebid.ExperienceBidSettingService;

import common.annotation.LoginCheck;
import common.annotation.SubmitCheck;
import common.annotation.SubmitOnly;
import common.constants.WxPageType;
import common.enums.Client;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;

import controllers.common.SubmitRepeat;
import controllers.wechat.WechatBaseController;
import controllers.wechat.interceptor.UserStatusWxInterceptor;

/**
 * 微信 -体验标控制器
 * 
 * @description 
 *
 * @author liudong
 * @createDate 2016年6月12日
 */
@With({UserStatusWxInterceptor.class, SubmitRepeat.class})
public class ExperiencebidWechatCtrl extends WechatBaseController {
	
	/** 注入体验标相关服务 */
	protected static ExperienceBidService experienceBidService = Factory.getService(ExperienceBidService.class);
	
	/** 注入体验标投资相关信息 */
	public static ExperienceBidInvestService experienceBidInvestService = Factory.getService(ExperienceBidInvestService.class);
	
	/** 注入体验账户service */
	public static ExperienceBidAccountService experienceBidAccountService = Factory.getService(ExperienceBidAccountService.class);

	public static ExperienceBidSettingService experienceBidSettingService = Factory.getService(ExperienceBidSettingService.class);
	
	/**
	 * 微信-体验标详情
	 *
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年6月12日
	 */
	@SubmitCheck
	public static void experienceBidWXPre(){
		t_experience_bid experienceBid = experienceBidService.findExperienceBidFront();
		if (experienceBid == null) {
			
			toResultPage(WxPageType.PAGE_COMMUNAL_FAIL, "没有可投体验标，请稍后再试");
		} else {
			
			renderArgs.put("experienceBid", experienceBid);
		}
		
		if(getCurrUser()!=null){
			double visualBalance = experienceBidAccountService.findUserExperienceGold(getCurrUser().id);
			renderArgs.put("visualBalance", visualBalance);
		}
		
		render();
	}
	
	/**
	 * 微信-体验标-借款详情
	 *
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年6月13日
	 */
	public static void experienceBidLoanDetailsPre(){
		t_experience_bid_setting experienceBidSetting = experienceBidSettingService.findByKey("content");
		
		String bidDetail = experienceBidSetting==null?"":experienceBidSetting._value;
		
		render(bidDetail);
	}
	
	/**
	 * 微信-体验标-投标记录
	 *
	 * @param experienceBidId 体验标id
	 * @param currPage 
	 * @param pageSize
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年6月13日
	 */
	public static void experienceBidRecordPre(long experienceBidId, int currPage, int pageSize){
		if(currPage < 1){
			currPage = 1;
		}
		if(pageSize < 1){
			pageSize = 5;
		}
		
		PageBean<experienceBidInvestRecord> pageBean = experienceBidInvestService.pageOfExperienceBidInvestRecord(experienceBidId, currPage, pageSize);
		
		render(pageBean);
	}
	
	/**
	 * 理财-投体验标
	 */
	@LoginCheck
	@SubmitOnly
	public static void investExperienceBid(double investAmt, long experienceBidId){
		checkAuthenticity();
		if(investAmt<=0 || investAmt%1000 != 0){
			flash.error("体验标投标金额只可为1000的正整数倍!");
			
			experienceBidWXPre();
		}
		
		ResultInfo result = experienceBidService.investExperienceBid(investAmt, experienceBidId, getCurrUser().id, Client.WECHAT);
		if(result.code < 1){
			flash.error(result.msg);
			LoggerUtil.error(true, "体验账户投体验标的失败：%s", result.msg);
			
			experienceBidWXPre();
		}
		
		toResultPage(WxPageType.PAGE_COMMUNAL_SUCC, "购买成功");
	}
}
