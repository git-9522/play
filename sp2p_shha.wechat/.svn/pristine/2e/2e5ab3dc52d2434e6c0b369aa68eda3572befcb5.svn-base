package controllers.wechat.front.account;

import models.wechat.bean.DealUserBean;
import models.wechat.bean.DealUserDetailBean;
import play.mvc.With;
import service.wechat.DealWechatUserService;

import common.constants.WxPageType;
import common.utils.Factory;
import common.utils.PageBean;

import controllers.wechat.WechatBaseController;
import controllers.wechat.interceptor.AccountWxInterceptor;

/**
 * 微信-账户中心-交易记录控制器
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年5月4日
 */
@With({AccountWxInterceptor.class})
public class MyDealCtrl extends WechatBaseController {
	
	protected static DealWechatUserService dealWechatService = Factory.getService(DealWechatUserService.class);
	
	/**
	 * 跳转交易记录页面
	 *
	 *
	 * @author Songjia
	 * @createDate 2016年5月11日
	 */
	public static void toDealRecordsPre(){
		
		render();
	}
	
	/**
	 * 交易记录列表
	 *
	 * @param currPage
	 * @param pageSize
	 *
	 * @author Songjia
	 * @createDate 2016年5月11日
	 */
	public static void listOfDealRecordsPre(int currPage, int pageSize){
		if (currPage < 1) {
			currPage = 1;
		}
		if (pageSize < 8) {
			pageSize = 8;
		}
		long userId = getCurrUser().id;
		PageBean<DealUserBean> pageBean = dealWechatService.pageOfWechatDealUser(currPage, pageSize, userId);
	
		render(pageBean);
	}
	
	
	/**
	 * 交易记录详情
	 *
	 * @param dealRecordId 交易记录ID
	 *
	 * @author Songjia
	 * @createDate 2016年5月11日
	 */
	public static void dealRecordDetailsPre(long dealRecordId) {
		DealUserDetailBean dealRecord =  dealWechatService.findDealRecorddetail(dealRecordId);
		if (dealRecord == null) {
			toResultPage(WxPageType.PAGE_COMMUNAL_FAIL, "系统异常，请联系管理员");
		}
		render(dealRecord);	
		
	}
}
