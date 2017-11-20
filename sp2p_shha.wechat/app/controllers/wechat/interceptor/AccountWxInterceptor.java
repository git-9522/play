package controllers.wechat.interceptor;

import models.common.bean.CurrUser;

import org.apache.commons.lang.StringUtils;

import play.mvc.Before;
import play.mvc.Controller;

import common.annotation.PaymentAccountCheck;
import common.annotation.RealNameCheck;
import common.constants.WxPageType;
import common.utils.ResultInfo;

import controllers.common.FrontBaseController;

/**
 * 微信-账户中心拦截器
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年5月10日
 */
public class AccountWxInterceptor extends Controller {

	/**
	 * 登陆拦截
	 */
	@Before(priority=11)
	static void checkLogin(){
		CurrUser user = FrontBaseController.getCurrUser();
		if (user != null) {
			return;
		}

		boolean isAjax = request.isAjax();
		if (isAjax) {

			ResultInfo result = new ResultInfo();
			result.code = ResultInfo.NOT_LOGIN;
			result.msg = "没有登录，或者登录状态已经失效!请重新登录!";

			renderJSON(result);
		} else {
			redirect("wechat.front.LoginAndRegisteCtrl.loginPre");
		}
	}
	
	/**
	 * 资金托管账户开户拦截
	 */
	@Before(priority=12)
	static void checkPaymentAccount(){
		PaymentAccountCheck checkAction = getActionAnnotation(PaymentAccountCheck.class);
		
		/* 不拦截 */
		if(checkAction == null){ 
			
			return;
		}
		
		CurrUser user = FrontBaseController.getCurrUser();
		if (user == null) {
			
			checkLogin();
		}
		
		if (StringUtils.isNotBlank(user.payment_account)) {
			
			return;
		}
		
		boolean isAjax = request.isAjax();
		if(isAjax){
			ResultInfo result = new ResultInfo();
			result.code = ResultInfo.NOT_PAYMENT_ACCOUNT;
			result.msg = "未开通资金托管账户";
			
			renderJSON(result);
		} else{
			redirect("wechat.WechatBaseController.toResultPage",WxPageType.PAGE_FAIL_PAYMENT,"你还没有开通资金托管!");
		}
	}
	
	/**
	 * 用户未实名认证拦截器
	 */
	@Before(priority=13)
	static void checkRealName(){
		RealNameCheck realNameCheck = getActionAnnotation(RealNameCheck.class);
		if (realNameCheck == null) {
			
			return;
		}
		
		/* 获取登录当前对象 */
		CurrUser user = FrontBaseController.getCurrUser();
		if (user == null) {
			
			checkLogin();
		}
		
		if (user.is_real_name) {
			
			return;
		}
		
		boolean isAjax = request.isAjax();
		if (isAjax) {
			ResultInfo result = new ResultInfo();
			result.code = ResultInfo.NOT_REAL_NAME;
			result.msg = "未实名认证";

			renderJSON(result);
		} else {

			redirect("wechat.WechatBaseController.toResultPage", WxPageType.PAGE_FAIL_PAYMENT, "你还没有进行实名认证!");
		}
	}
	
}
