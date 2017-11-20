package controllers.wechat.front.account;

import models.wechat.bean.ReceiveBillDetailBean;
import models.wechat.bean.ReceiveBillPlanBean;
import play.mvc.With;
import service.wechat.BillWechatInvestService;
import common.constants.WxPageType;
import common.utils.Factory;
import common.utils.PageBean;
import controllers.wechat.WechatBaseController;
import controllers.wechat.interceptor.AccountWxInterceptor;

/**
 * 微信-账户中心-回款计划
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年5月4日
 */
@With({AccountWxInterceptor.class})
public class MyReceiveBillCtrl extends WechatBaseController {
	
	protected static BillWechatInvestService billWechatInvestService = Factory.getService(BillWechatInvestService.class);
	

	/***
	 * 账户中心 回款计划
	 *
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-5-10
	 */
	public static void toReceiveBillPre(){
		
		render();
	}
		
	/***
	 * 回款计划 列表
	 * @param currPage
	 * @param pageSize
	 * @description 
	 * @author luzhiwei
	 * @createDate 2016-05-10
	 */
	public static void pageOfReceiveBillPre (int currPage,int pageSize) {
		
		if (currPage < 1) {
			currPage = 1;
		}
		if (pageSize < 5) {
			pageSize = 5;
		}
        
		PageBean<ReceiveBillPlanBean> pageBean = billWechatInvestService.pageOfInvestReceiveBill(getCurrUser().id,currPage, pageSize);
	
	    render(pageBean);
	}
	
	/***
	 * 回款计划 账单 详情
	 * @param billId 理财账单ID
	 * @description 
	 * @author luzhiwei
	 * @createDate 2016-05-10
	 */
	public static void receiveBillDetailPre (long billId) {
		ReceiveBillDetailBean bill = billWechatInvestService.findInvestReceiveBillDeatil(billId);
		if (bill == null) {

			toResultPage(WxPageType.PAGE_FAIL, "账单不存在");
		}
		
	    render(bill);
	}
}
