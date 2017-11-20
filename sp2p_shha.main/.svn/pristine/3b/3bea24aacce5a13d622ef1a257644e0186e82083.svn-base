package controllers.front.account;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.common.bean.CurrUser;
import models.common.entity.t_bank_card_user;
import models.common.entity.t_corp_info;
import models.common.entity.t_pay_pro_city;
import models.common.entity.t_send_code;
import models.common.entity.t_template_notice;
import models.common.entity.t_user;
import models.common.entity.t_user_fund;
import models.common.entity.t_user_info;
import models.common.entity.t_corp_info.Status;

import org.apache.commons.lang.StringUtils;

import payment.impl.PaymentProxy;
import play.Logger;
import play.cache.Cache;
import play.mvc.Scope.Session;
import play.mvc.With;
import services.common.BankCardUserService;
import services.common.CorpInfoService;
import services.common.NoticeService;
import services.common.SendCodeRecordsService;
import services.common.UserFundService;
import services.common.UserInfoService;
import services.common.UserService;

import com.shove.security.Encrypt;

import common.annotation.PaymentAccountCheck;
import common.annotation.RealNameCheck;
import common.annotation.SimulatedCheck;
import common.constants.CacheKey;
import common.constants.ConfConst;
import common.constants.Constants;
import common.constants.PaymentConst;
import common.constants.SettingKey;
import common.enums.Area;
import common.enums.Bank;
import common.enums.BusiType;
import common.enums.Client;
import common.enums.NoticeScene;
import common.enums.Province;
import common.enums.ServiceType;
import common.utils.Factory;
import common.utils.IDCardValidate;
import common.utils.JPAUtil;
import common.utils.LoggerUtil;
import common.utils.OrderNoUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.SMSUtil;
import common.utils.Security;
import common.utils.StrUtil;
import controllers.common.FrontBaseController;
import controllers.common.interceptor.AccountInterceptor;
import controllers.common.interceptor.SimulatedInterceptor;
import controllers.front.LoginAndRegisteCtrl;
import daos.common.CorpInfoDao;

/**
 * 前台-账户中心-安全中心控制器
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2015年12月17日
 */
@With({AccountInterceptor.class,SimulatedInterceptor.class})
public class MySecurityCtrl extends FrontBaseController {

	protected static UserService userService = Factory.getService(UserService.class);
	
	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	
	protected static UserFundService userFundService = Factory.getService(UserFundService.class);
	
	protected static NoticeService noticeService = Factory.getService(NoticeService.class);
	
	protected static BankCardUserService bankCardUserService = Factory.getService(BankCardUserService.class);

	protected static SendCodeRecordsService sendCodeRecordsService = Factory.getService(SendCodeRecordsService.class);
	
	protected static CorpInfoService corpInfoService = Factory.getService(CorpInfoService.class);
	
	/**
	 * 前台-我的财富-安全中心
	 * 
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月13日
	 *
	 */
	public static void securityPre(){
		
		if(getCurrUser() == null){
		
			LoginAndRegisteCtrl.loginPre();
		}
		
		long userId = getCurrUser().id;
		
		
		t_user user = userService.findByID(userId);
		
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		
		List<t_bank_card_user> cardList = bankCardUserService.queryCardByUserId(userId);
		
		if(userFund.is_corp) {
			// 企业用户
			t_corp_info corpInfo = corpInfoService.queryByUserId(userId);
			render(user, userInfo, userFund, cardList, corpInfo);
		} else {
			render(user, userInfo, userFund, cardList);
		}
		
	}
	
	/**
	 * 修改手机step：1
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月13日
	 *
	 */
	@SimulatedCheck
	public static void updateUserMobilePre(){
		t_user_info userInfo = userInfoService.findUserInfoByUserId(getCurrUser().id);
		
		render(userInfo);
	}
	
	/**
	 * 修改手机step：2
	 * @param mobile
	 * @param smsCode
	 * @param scene 场景
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月13日
	 *
	 */
	public static void updateMobileSecond(String mobile, String smsCode, String scene){
		checkAuthenticity();
		
		/* 验证信息 */
		if (StringUtils.isBlank(mobile)) {
			flash.error("请输入手机号");
			
			updateUserMobilePre();
		}
		
		if (!userService.isMobileExists(mobile)) {
			flash.error("手机号不存在");
			
			updateUserMobilePre();
		}
		
		/* 获取缓存中的短信验证码 */
    	Object obj = Cache.get(mobile + scene);
		if (obj == null) {
			flash.error("短信验证码已失效");
			
			updateUserMobilePre();
		}
		String codeStr = obj.toString();
		/* 校验短信验证码 */
		if(ConfConst.IS_CHECK_MSG_CODE){
			if (!codeStr.equals(smsCode)) {
				flash.error("短信验证码不正确");
				
				updateUserMobilePre();
			}
		}
		
		/* 清除缓存中的验证码 */
		String encryString = Session.current().getId();
    	Object cache = Cache.get(mobile + encryString + scene);
    	if(cache != null){
    		Cache.delete(mobile + encryString + scene);
    		Cache.delete(mobile + scene);
    	}
		
		render(codeStr);
	}
	
	/**
	 * 修改手机step：3
	 * @param mobile
	 * @param scene 场景
	 * 
	 * @author Chenzhipeng
	 * @createDate 2016年1月14日
	 *
	 */
	public static void updateMobileThird(String mobile, String scene){
		checkAuthenticity();
		
		ResultInfo result = new ResultInfo();
		long userId =  getCurrUser().id;
		
		result = userService.updateUserMobile(userId, mobile);
		if (result.code < 1) {
			flash.error("手机号码修改失败");
			
			securityPre();
		}
		flash.success("手机号码修改成功");
		/* 清除缓存中的验证码 */
		String encryString = Session.current().getId();
    	Object cache = Cache.get(mobile + encryString + scene);
    	if(cache != null){
    		Cache.delete(mobile + encryString + scene);
    		Cache.delete(mobile + scene);
    	}
    	
		securityPre();
	}
	
	/**
	 * 校验手机验证码是否正确
	 * @param mobile
	 * @param smsCode
	 * @param type
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月14日
	 *
	 */
	public static void checkSmsCode(String mobile, String smsCode, int type, String scene){
		ResultInfo result = new ResultInfo();
		
		/* 验证信息 */
		if (StringUtils.isBlank(mobile)) {
			result.code = -1;
			result.msg = "手机号不能为空";
			
			renderJSON(result);
		}
		
		boolean isType = userService.isMobileExists(mobile);
		if (type != 1 ) {
			isType = !isType;
		}
		
		if (!isType) {
			result.code = -1;
			result.msg = "该手机号已被占用";
			
			renderJSON(result);
		}
		
		/* 获取缓存中的短信验证码 */
    	Object obj = Cache.get(mobile + scene);
		if (obj == null) {
			result.code = -1;
			result.msg = "短信验证码已失效";
			
			renderJSON(result);
		}
		String code = obj.toString();
		/* 校验短信验证码 */
		if(ConfConst.IS_CHECK_MSG_CODE){
			if (!code.equals(smsCode)) {
				result.code = -1;
				result.msg = "短信验证码不正确";
				
				renderJSON(result);
			}
		}
		
		result.code = 1;
		
		renderJSON(result);
	}
	
	/**
	 * 修改用户邮箱页面
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月15日
	 *
	 */
	@SimulatedCheck
	@PaymentAccountCheck
	public static void updateUserEmailPre(){
		
		render();
	}
	
	/**
	 * 修改、绑定邮箱
	 * @description 
	 * 
	 * @param userSign 加密的用户id
	 * @param email 邮箱地址
	 * 
	 * @author Chenzhipeng
	 * @createDate 2015年12月22日
	 */
	@PaymentAccountCheck
	public static void updateEmailSuccess(String email, String userName){
		checkAuthenticity();
		
		ResultInfo result = new ResultInfo();
		
		long userId = getCurrUser().id;
		/* 判断是否是有效邮箱 */
		if (!StrUtil.isEmail(email)) {
			flash.put("msg","邮箱格式错误");
			
			render();
		}
		
		/* 判断邮箱是否存在 */
		if (userInfoService.isEailExists(email)) {
			flash.put("msg","邮箱已存在");
			
			render();
		}
		
		/* 用户Id进行加密 */
		String userSign = Security.addSign(userId, Constants.USER_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
		
		/* 邮箱进行加密 */
		String emailStr = Security.addEmailSign(email, Constants.MSG_EMAIL_SIGN, ConfConst.ENCRYPTION_KEY_DES);
		
		/* 获取修改邮箱URL */
		String url = getBaseURL() + "loginAndRegiste/confirmactiveemail.html?su=" + userSign +"&se="+emailStr;

		/* 发送邮件激活 */
		result = noticeService.sendReBindEmail(email, userName, url);
		if (result.code < 1) {
			LoggerUtil.error(true, result.msg);
			flash.put("msg",result.msg);
			
			render();
		}
		
		render();
	}
	
	/**
	 * 银行卡列表 
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月15日
	 *
	 */
	@PaymentAccountCheck
	public static void updateUserBankCardPre(){
		long userId = getCurrUser().id;
		
		/*银行卡同步*/
		//实时查询绑定银行卡
		ResultInfo result = PaymentProxy.getInstance().queryBindedBankCard(Client.PC.code, userId);
		
		if(result.obj != null){
			
			List<t_bank_card_user> bankList = (List<t_bank_card_user>) result.obj;
			if (bankList != null) {
				
				bankCardUserService.delBank(userId);
				
				for (t_bank_card_user bcu : bankList) {
					bankCardUserService.addUserCard(userId, bcu.bank_name, bcu.bank_code, bcu.account);
				}
				
				/* 刷新当前用户缓存 */
				userService.flushUserCache(userId);
			}
		}
		
		
		List<t_bank_card_user> listOfUserBankCard = bankCardUserService.queryCardByUserId(userId);
		/* 一个用户最多只能绑定10张银行卡 */
		boolean isHaveCard = false;
		if(listOfUserBankCard.size() < 10){
			isHaveCard = true;
		}
		boolean isFYPayment = true;
		if (!ConfConst.CURRENT_TRUST_TYPE.equals(PaymentConst.TRUST_TYPE_FY)) {
			isFYPayment = false;
		}
		
		render(isHaveCard, isFYPayment);
	}
	
	/**
	 * 银行卡列表
	 * @param currPage
	 * @param pageSize
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年3月2日
	 *
	 */
	@PaymentAccountCheck
	public static void listOfUserBankCardPre(int currPage, int pageSize){
		if(pageSize<1){
			pageSize=5;
		}
		long userId = getCurrUser().id;
		PageBean<t_bank_card_user> cardPageBean = bankCardUserService.pageOfUserCard(currPage, pageSize, userId);
		
		render(cardPageBean);
	}
	
	/**
	 * 设置默认银行卡
	 * @description 
	 * 
	 * @param id 银行卡id
	 * @param userSign 用户id
	 * 
	 * @author Chenzhipeng
	 * @createDate 2015年12月23日
	 */
	@SimulatedCheck
	public static void setDefaultBankCard(long id){
		ResultInfo result = new ResultInfo();
		long userId = getCurrUser().id;
		int isFlag = bankCardUserService.updateUserCardStatus(id, userId);
		if (isFlag < 1) {
			result.code = -1;
			result.msg = "设置默认银行卡失败";
			
			renderJSON(result);
		}
		result.code = 1;
		result.msg = "设置默认银行卡成功";
		
		renderJSON(result);
	}

	/**
	 * 修改密码页面
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月15日
	 *
	 */
	@SimulatedCheck
	public static void updateUserPasswordPre(){
		
		render();
	}
	
	/**
	 * 重置登录密码
	 * @description 
	 * 
	 * @param oldPassword 旧密码
	 * @param password 登录密码
	 * @param confirmPassword 重复密码
	 * @param userSign 用户加密Id
	 * 
	 * @author Chenzhipeng
	 * @createDate 2015年12月18日
	 */
	public static void restPassword(String oldPassword,String password){
		checkAuthenticity();
		
		ResultInfo result = new ResultInfo();
		long userId =  getCurrUser().id;
		
		if (StringUtils.isBlank(oldPassword)) {
			flash.error("密码不能为空");
			
			updateUserPasswordPre();
		}
		
		t_user user = userService.findByID(userId);
	
		//用户密码加密无key 
		if(user.is_no_key){
			
			oldPassword = Encrypt.MD5(oldPassword);
			
			//加密串字母大写
			oldPassword = oldPassword.toUpperCase();
		}else{
			
			oldPassword = Encrypt.MD5(oldPassword + ConfConst.ENCRYPTION_KEY_MD5);
		}
		
		if (!oldPassword.equalsIgnoreCase(user.password)) {
			flash.error("密码不正确");
			
			updateUserPasswordPre();
		}
		
		/* 验证密码是否符合规范 */
		if (!StrUtil.isValidPassword(password)) {
			flash.error("密码不符合规范");
			
			updateUserPasswordPre();
		}
		
		/*密码加密*/
		password = Encrypt.MD5(password + ConfConst.ENCRYPTION_KEY_MD5);
		
		/*新密码与旧密码不能相同*/
		if(password.equals(user.password)){
			flash.error("新密码不能与旧密码相同");
			
			updateUserPasswordPre();
		}
		
		result = userService.updatePassword(userId, password);
		if (result.code < 1) {
			flash.error(result.msg);
			
			updateUserPasswordPre();
		}
		flash.success("密码修改成功");
		
		LoginAndRegisteCtrl.loginOutPre();
	}
	
	/**
	 * 第三方开户
	 *
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月9日
	 */
	public static void createAccountPre() {
		long userId = getCurrUser().id;
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		if (userFund == null) {
			flash.error("获取用户信息失败");

			securityPre();
		}
		
		if(userFund.is_corp) {
			// 企业用户开户动作
			flash.error("你曾经进行过企业开户");
			securityPre();
		} else {
			if (StringUtils.isNotBlank(userFund.payment_account)) {
				flash.error("你已开通资金托管");
				securityPre();
			}
		}

		// 不会走
		//如果是富友资金托管还需要先绑定邮箱，再进行实名认证，最后还需要填写银行卡信息
		if (ConfConst.CURRENT_TRUST_TYPE.equals(PaymentConst.TRUST_TYPE_FY)) {
			t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
			
			//绑定邮箱
			if (StringUtils.isBlank(userInfo.email)) {
				redirect("front.account.MySecurityCtrl.updateUserEmailPre");
			}
			
			//进行实名认证
			if (StringUtils.isBlank(userInfo.reality_name) || StringUtils.isBlank(userInfo.id_number)) {
				redirect("front.account.MySecurityCtrl.updateRealNamePre");
			}
			
			//绑定银行卡
			redirect("front.account.MySecurityCtrl.fyBindBankCardPre");
		}
		
		/* 原开户页面
		//业务订单号
		String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.REGIST);
		
		ResultInfo result = PaymentProxy.getInstance().regist(Client.PC.code, serviceOrderNo, userId);
		
		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
			flash.error(result.msg);
		}
		*/
		render();
	}
	
	/**
	 * 托管开户处理页面
	 */
	public static void doCreateAccount(String hfName, String realName, String idNumber, String mobile,
			String cardId, String bankId, String provId, String areaId, String smsCode) {
		long userId = getCurrUser().id;
		
		if(!StrUtil.isMobileNum(mobile)) {
			flash.error("手机号码不准确");
			createAccountPre();
		}
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		if (userFund == null) {
			flash.error("获取用户信息失败");
			securityPre();
		}
		
		if(userFund.is_corp) {
			// 企业用户开户动作
			flash.error("你曾经进行过企业开户");
			securityPre();
		} else {
			if (StringUtils.isNotBlank(userFund.payment_account)) {
				flash.error("你已开通资金托管");
				securityPre();
			}
		}
		
		if(StringUtils.isBlank(hfName)) {
			flash.error("存管用户名不能空");
			createAccountPre();
		}
		
		ResultInfo result = userInfoService.checkHfname(hfName);
		
		if(result.code != 1) {
			flash.error("不能使用此存管用户名");
			createAccountPre();
		}
		
		if(StringUtils.isBlank(realName)) {
			flash.error("真实姓名不能空");
			createAccountPre();
		}
		
		if(StringUtils.isBlank(idNumber)) {
			flash.error("身份证号码不能为空");
			createAccountPre();
		}

		if(idNumber.length() != 18) {
			flash.error("身份证号码长度必须为18位");
			createAccountPre();
		}
		
		if(StringUtils.isBlank(cardId)) {
			flash.error("银行卡号不能为空");
			createAccountPre();
		}
		
		if(cardId.length() < 16) {
			flash.error("银行卡号不能小于16位");
			createAccountPre();
		}
		
		if(StringUtils.isBlank(bankId)) {
			flash.error("开户银行不能为空");
			createAccountPre();
		}
		
		Bank bank = Bank.getBank(bankId);
		
		if(bank == null) {
			flash.error("开户银行错误");
			createAccountPre();
		}
		
		if(StringUtils.isBlank(provId)) {
			flash.error("开户银行所在省市不能空");
			createAccountPre();
		}
		
		Province province = Province.getProvByCode(provId);
		
		if(province == null) {
			flash.error("开户银行所在省市错误");
			createAccountPre();
		}
		
		if(StringUtils.isBlank(areaId)) {
			flash.error("开户银行所在城市不能空");
			createAccountPre();
		}
		
		Area area = Area.getAreaByCode(areaId);
		
		if(area == null) {
			flash.error("开户银行所在城市错误");
			createAccountPre();
		}
		
		if(StringUtils.isBlank(smsCode)) {
			flash.error("短信验证码不能空");
			createAccountPre();
		}
		
		// 业务订单号
		String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.REGIST);
		
		String sessionId = Session.current().getId();
		Object obj = Cache.get(CacheKey.SMSCODE_ + sessionId);
		if(obj == null) {
			flash.error("验证码已经过期，请重新获取");
			createAccountPre();
		}
		
		Map<String, Object> map = (Map<String, Object>) obj;
		BusiType busiType = (BusiType) map.get("busiType");
		if(busiType.code != BusiType.REGISTER.code) {
			flash.error("短信seq验证错误");
			createAccountPre();
		}
		String smsSeq = map.get("smsSeq").toString();
		
		result = PaymentProxy.getInstance().userRegist(Client.PC.code, serviceOrderNo, userId, hfName, realName, idNumber, mobile, cardId, bankId, provId, areaId, smsCode, smsSeq);
		
		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
			flash.error(result.msg);
		}
		
		securityPre();
	}
	
	public static void createCorpAccountPre() {
		long userId = getCurrUser().id;
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		
		if (userFund == null) {
			flash.error("获取用户信息失败");
			securityPre();
		}
		
		if(userFund.is_corp) {
			// 如果企业用户的
			t_corp_info corpInfo = corpInfoService.queryByUserId(userId);
			if(Status.inProcesses(corpInfo.getStatus().code)) {
				flash.error("企业开户进行中或已成功");
				securityPre();
			}
		} else {
			if(StringUtils.isNotBlank(userFund.payment_account)) {
				flash.error("您已个人用户开户已成功");
				securityPre();
			}
		}

		render();
	}
	
	public static void doCreateCorpAccount(String usrId, String usrName, String instuCode, String busiCode,
			String taxCode, String guarType, double guarCorpEarnestAmt) {
		long userId = getCurrUser().id;
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		
		if (userFund == null) {
			flash.error("获取用户信息失败");
			securityPre();
		}
		
		if(userFund.is_corp) {
			// 如果企业用户的
			t_corp_info corpInfo = corpInfoService.queryByUserId(userId);
			if(Status.inProcesses(corpInfo.getStatus().code)) {
				flash.error("企业开户进行中或已成功");
				securityPre();
			}
		} else {
			if(StringUtils.isNotBlank(userFund.payment_account)) {
				flash.error("您已个人用户开户已成功");
				securityPre();
			}
		}
		
		if(StringUtils.isBlank(usrId)) {
			flash.error("存管用户名不能空");
			createCorpAccountPre();
		}
		
		ResultInfo result = userInfoService.checkHfname(usrId);
		
		if(result.code != 1) {
			flash.error("不能使用此存管用户名");
			createCorpAccountPre();
		}
		
		if(StringUtils.isBlank(usrName)) {
			flash.error("企业名称不能为空");
			createCorpAccountPre();
		}
		
		if(StringUtils.isBlank(busiCode)) {
			flash.error("营业执照编号不能为空");
			createCorpAccountPre();
		}
		
		if(StringUtils.isBlank(instuCode)) {
			flash.error("组织机构代码不能为空");
			createCorpAccountPre();
		}
		
		if(StringUtils.isBlank(taxCode)) {
			flash.error("税务登记号不能为空");
			createCorpAccountPre();
		}

		if(!"Y".equals(guarType) && !"N".equals(guarType)) {
			flash.error("担保类型错误");
			createCorpAccountPre();
		}
		
		if(guarCorpEarnestAmt <= 0) {
			flash.error("企业用户备案金或注册资金填写错误");
			createCorpAccountPre();
		}
		
		// 业务订单号
		String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.CORPREGISTER);
		
		result = PaymentProxy.getInstance().corpRegister(Client.PC.code, serviceOrderNo,
				userId, usrId, usrName, instuCode, busiCode, taxCode, guarType, guarCorpEarnestAmt);
		
		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
			flash.error(result.msg);
		}
		
		securityPre();
	}
	
	public static void corpRegisterQuery() {
		ResultInfo result = new ResultInfo();
		
		long userId = getCurrUser().id;
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		
		if (userFund == null) {
			result.code = -1;
			result.msg = "获取用户信息失败";
			renderJSON(result);
		}
		
		t_corp_info corpInfo = corpInfoService.queryByUserId(userId);
		
		if(corpInfo == null) {
			if(userFund.is_corp) {
				result.code = -1;
				result.msg = "获取企业开户信息失败";
				renderJSON(result);
			} else {
				result.code = -1;
				result.msg = "未曾进行企业开户";
				renderJSON(result);
			}
		}
		
		result = PaymentProxy.getInstance().corpRegisterQuery(Client.PC.code, userId, corpInfo.busi_code);
		
		renderJSON(result);
		
	}
	
	public static void bosAcctActivatePre() {
		long userId = getCurrUser().id;
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		if (userFund == null) {
			flash.error("获取用户信息失败");
			securityPre();
		}
		
		if(StringUtils.isBlank(userFund.payment_account)) {
			flash.error("还未开户，请去开户");
			securityPre();
		}
		
		if(userFund.is_actived) {
			flash.error("已激活");
			securityPre();
		}
		
		String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.BOSACCTACTIVATE);
		
		ResultInfo result = PaymentProxy.getInstance().bosAcctActivate(Client.PC.code, serviceOrderNo, userId);
		
		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
			flash.error(result.msg);
		}
		
		securityPre();
	}
	
	public static void quickBindingPre(int step) {

		if(step < 0 || step > 1) {
			flash.error("出错了，请重试");
			securityPre();
		}
		
		CurrUser currUser = getCurrUser();
		
		if(currUser.is_corp) {
			flash.error("企业用户不能换绑卡");
			securityPre();
		}
		
		long userId = currUser.id;
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		if (userFund == null) {
			flash.error("获取用户信息失败");
			securityPre();
		}
		
		if(StringUtils.isBlank(userFund.payment_account)) {
			flash.error("还未开户，请去开户");
			securityPre();
		}
		
		if(!userFund.is_actived) {
			flash.error("还未激活，请去激活");
			securityPre();
		}
		
		if(step == 0) {
			t_bank_card_user bankCard = bankCardUserService.queryCardByUserId(userId).get(0);
			render(bankCard, step);
		} else {
			// 1.check smsCode
			String orginSmsCode = params.get("orginSmsCode");
			if(StringUtils.isBlank(orginSmsCode)) {
				flash.error("验证码不能为空");
				Logger.error("换绑卡，原银行卡短信验证码为空");
				quickBindingPre(0);
			}
			// 2.check Session
			String sessionId = Session.current().getId();
			Object obj = Cache.get(CacheKey.ORGIN_SMSCODE_ + sessionId);
			if(obj == null) {
				flash.error("验证码失效，请重新获取");
				Logger.error("session 失效");
				quickBindingPre(0);
			}
			Map<String, Object> map = (Map<String, Object>) obj;
			// 3.reset Session
			map.put("orginSmsCode", orginSmsCode);
			Cache.set(CacheKey.ORGIN_SMSCODE_ + sessionId, map, Constants.CACHE_TIME_MINUS_30);
			render(step);
		}
	}
	
	public static void doQuickBinding(String mobile, String cardId, String bankId, String provId,
			String areaId, String smsCode) {
		long userId = getCurrUser().id;
		
		if(!StrUtil.isMobileNum(mobile)) {
			flash.error("手机号码不准确");
			securityPre();
		}
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		if (userFund == null) {
			flash.error("获取用户信息失败");
			securityPre();
		}
		
		if (!StringUtils.isNotBlank(userFund.payment_account)) {
			flash.error("你未开通资金托管");
			securityPre();
		}
		
		String sessionId = Session.current().getId();
		
		Object obj = Cache.get(CacheKey.ORGIN_SMSCODE_ + sessionId);
		
		if(obj == null) {
			flash.error("验证码已过期，请重新获取");
			quickBindingPre(0);
		}
		
		Map<String, String> orginSms = (Map<String, String>) obj;
		
		obj = Cache.get(CacheKey.SMSCODE_ + sessionId);

		if(obj == null) {
			flash.error("验证码已过期，请重新获取");
			quickBindingPre(1);
		}
		
		Map<String, Object> sms = (Map<String, Object>) obj;
		
		// check session sms type
		BusiType busiType = (BusiType) sms.get("busiType");
		if(busiType.code != BusiType.NEW_BIND.code) {
			flash.error("短信验证码失效，请重新获取");
			quickBindingPre(1);
		}
		
		String smsSeq = sms.get("smsSeq").toString();
		
		String orgSmsExt = orginSms.get("orginSmsCode") + orginSms.get("orginSmsSeq");
		
		/* 业务处理 */
		// 业务订单号
		String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.QUICKBINDING);
		
		ResultInfo result = PaymentProxy.getInstance().quickBinding(Client.PC.code, serviceOrderNo,
				userId, cardId, bankId, provId, areaId, mobile, smsCode, smsSeq, orgSmsExt);
		
		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
			flash.error(result.msg);
		}
		
		securityPre();
	}
	
	/**
	 * 实名认证页面跳转
	 */
	@PaymentAccountCheck
	@SimulatedCheck
	public static void certificationPre(){

		render();
	}
	
	/**
	 * 实名认证
	 *
	 * @author huangyunsong
	 * @createDate 2016年5月31日
	 */
	@PaymentAccountCheck
	@SimulatedCheck
	public static void checkRealName(String realName, String idNumber) {
		checkAuthenticity();
		
		flash.put("realName", realName);
		flash.put("idNumber", idNumber);

		if (StringUtils.isBlank(realName)) {
			flash.error("真实姓名不能为空");

			certificationPre();
		}

		if (StringUtils.isBlank(idNumber)) {
			flash.error("身份证不能为空");
			
			certificationPre();
		}

		if(!"".equals(IDCardValidate.chekIdCard(0, idNumber))) {
			flash.error("请输入正确的身份证号");
			
			certificationPre();
		}
	
		t_user_info userInfo = userInfoService.findUserInfoByUserId(getCurrUser().id);
		if (userInfo == null) {
			flash.error("用户信息不存在");
			
			certificationPre();
		}
		
		userInfo.reality_name = realName;
		userInfo.id_number = idNumber;
		
		ResultInfo result = userInfoService.updateUserInfo(userInfo);
		if (result.code < 0) {
			flash.error("保存用户信息失败");
			
			certificationPre();
		}
		
		//刷新用户缓存信息
		userService.flushUserCache(getCurrUser().id);

		flash.success("实名认证成功！");
		securityPre();
	}
	
	
	/**
	 * 绑定银行卡
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月9日
	 */
	@SimulatedCheck
	@PaymentAccountCheck
	@RealNameCheck
	public static void bindCardPre() {	
		ResultInfo result;

		long userId = getCurrUser().id;

		//业务订单号
		String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.BIND_CARD);
		
		result = PaymentProxy.getInstance().userBindCard(Client.PC.code, serviceOrderNo, userId);
		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
			flash.error(result.msg);

			updateUserBankCardPre();
		}
	}
	
	/**
	 * 同步绑定银行卡
	 *
	 * @author huangyunsong
	 * @createDate 2016年2月15日
	 */
	public static void flushBindCardPre() {
		long userId = getCurrUser().id;
		
		//实时查询绑定银行卡
		ResultInfo result = PaymentProxy.getInstance().queryBindedBankCard(Client.PC.code, userId);
					
		List<t_bank_card_user> bankList = (List<t_bank_card_user>) result.obj;
		if (bankList == null) {
			
			updateUserBankCardPre();
		}
		
		bankCardUserService.delBank(userId);
		
		for (t_bank_card_user bcu : bankList) {
			bankCardUserService.addUserCard(userId, bcu.bank_name, bcu.bank_code, bcu.account);
		}
		
		/* 刷新当前用户缓存 */
		userService.flushUserCache(userId);

		updateUserBankCardPre();
	}
	
	/**
	 * 发送手机验证码（修改绑定手机）
	 * @description 
	 * 
	 * @param mobile 手机号码
	 * @param type 验证码发送场景（1：发送至原手机，2：发送至新手机）
	 * @param scene 短信发送场景
	 * 
	 * @author Chenzhipeng
	 * @createDate 2015年12月18日
	 */
	public static void sendCode(String mobile,int type, String scene){
		ResultInfo result = new ResultInfo();
		
		if (StringUtils.isBlank(mobile)) {
			result.code = -1;
			result.msg = "手机号不能为空";
			
			renderJSON(result);
		}
		
		if (!StrUtil.isMobileNum(mobile)) {
			result.code = -1;
			result.msg = "手机号格式不正确";
			
			renderJSON(result);
		}
		
		boolean isType = userService.isMobileExists(mobile);
		if (type != 1 ) {
			isType = !isType;
		}
		
		if (!isType) {
			result.code = -1;
			result.msg = "该手机号已被占用";
			
			renderJSON(result);
		}
		
    	/* 根据手机号码查询短信发送条数 */
    	List<t_send_code> recordByMobile = sendCodeRecordsService.querySendRecordsByMobile(mobile);
    	if(recordByMobile.size() >= ConfConst.SEND_SMS_MAX_MOBILE){
			result.code = -2;
			result.msg = "该手机号码单日短信发送已达上限";

			renderJSON(result);
    	}
    	
    	/* 根据IP查询短信发送条数 */
    	List<t_send_code> recordByIp = sendCodeRecordsService.querySendRecordsByIp(getIp());
    	if(recordByIp.size() >= ConfConst.SEND_SMS_MAX_IP){
			result.code = -2;
			result.msg = "该IP单日短信发送已达上限";

			renderJSON(result);
    	}
		
		/* 设置手机号码60S内无法重复发送验证短信 */
		String encryString = Session.current().getId();
    	Object cache = Cache.get(mobile + encryString + scene);
    	if(null == cache){
			Cache.set(mobile + encryString + scene, mobile, Constants.CACHE_TIME_SECOND_60);
    	}else{
    		String isOldMobile = cache.toString();
    		if (isOldMobile.equals(mobile)) {
    			result.code = -2;
    			result.msg = "短信已发送";

    			renderJSON(result);
    		}
    	}
    	
    	/* 获取短信验证码模板 */
		List<t_template_notice> noticeList = noticeService.queryTemplatesByScene(NoticeScene.SECRITY_CODE);

		if(noticeList.size() < 0){
			result.code = -2;
			result.msg = "短信发送失败";

			renderJSON(result);
    	}
		String content = noticeList.get(0).content;
		
		String smsAccount = settingService.findSettingValueByKey(SettingKey.SERVICE_SMS_ACCOUNT);
		String smsPassword = settingService.findSettingValueByKey(SettingKey.SERVICE_SMS_PASSWORD);
		
		/* 发送短信验证码 */
		SMSUtil.sendCode(smsAccount, smsPassword, mobile, content, Constants.CACHE_TIME_MINUS_30, scene, ConfConst.IS_CHECK_MSG_CODE);
		/* 添加一条短信发送控制记录 */
		sendCodeRecordsService.addSendCodeRecords(mobile, getIp());
		
		result.code = 1;
		result.msg = "短信验证码发送成功";
		
		renderJSON(result);
	}
	
	/**
	 * 富友绑定银行卡
	 */
	public static void fyBindBankCardPre() {
		List<Map<String, Object>> list = JPAUtil.getList("select * from t_pay_pro_city group by prov_num");
		render(list);
	}
	
	/**
	 * 实名认证执行（富友接口）
	 * @param realName
	 * @param idNumber
	 */
	public static void updateRealName(String realName, String idNumber) {
		ResultInfo result = new ResultInfo();
		
		if (StringUtils.isBlank(realName)) {
			result.code = -1;
			result.msg = "真实姓名不能为空";
			
			renderJSON(result);
		}
		
		if (StringUtils.isBlank(idNumber)) {
			result.code = -1;
			result.msg = "身份证号码不能为空";
			
			renderJSON(result);
		}
		
		if (!"".equals(IDCardValidate.chekIdCard(0, idNumber))) {
			result.code = -1;
			result.msg = "请输入正确的身份证号";
			
			renderJSON(result);
		}
		
		if (userInfoService.isIdNumberExists(idNumber)) {
			result.code = -1;
			result.msg = "该身份证号码已经存在";
			
			renderJSON(result);
		}
		
		if (realName.length() > 20) {
			result.code = -1;
			result.msg = "真实姓名不能超过二十个字符";
			
			renderJSON(result);
		}
		
		t_user_info userInfo = userInfoService.findUserInfoByUserId(getCurrUser().id);
		userInfo.reality_name = realName;
		userInfo.id_number = idNumber;
		
		result = userInfoService.updateUserInfo(userInfo);
		
		if (result.code < 1) {
			LoggerUtil.error(true, "用户实名认证时：%s", result.msg);
			
			renderJSON(result);
		}
		
		userService.flushUserCache(getCurrUser().id);
		
		result.code = 1;
		result.msg = "实名认证成功";
		
		renderJSON(result);
	}
	
	/**
	 * 通过省编号查找属于该省的所有市
	 * @param prov_num
	 */
	public static void findByCity(String prov_num) {
		List<Map<String, Object>> list = t_pay_pro_city.find("prov_num = ? ", prov_num).fetch();
		renderJSON(list);
	}
	
	/**
	 * 开通富友托管账户
	 * @param prov_num 开户行所属省
	 * @param city_num 开户行所属市
	 * @param bank_type 开户行类别
	 * @param bank_name 支行名称
	 * @param bank_num 银行卡号
	 */
	public static void createFyAccount(String prov_num, String city_num, String bank_type, String bank_name, String bank_num) {
		ResultInfo result = new ResultInfo();
		
		long userId = getCurrUser().id;
		
		//业务订单号
		String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.REGIST);
		
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		
		result = PaymentProxy.getInstance().regist(Client.PC.code, serviceOrderNo, userId, 
				userInfo.reality_name, userInfo.id_number, userInfo.mobile, userInfo.email, 
				city_num, bank_type, bank_name, bank_num);
		
		if (result.code < 1) {
			//根据错误信息来确定是清除实名信息还是银行卡信息
			String respCode = (String) result.obj;
			
			//已开户当做开户成功处理
			if (respCode.endsWith("5343") || respCode.endsWith("5891"))
			{
				serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.QUERY_PERSION_INFORMATION);
				
				result = PaymentProxy.getInstance().queryPersionInformation(Client.PC.code, serviceOrderNo, userId, 
						userInfo.mobile, userInfo.id_number, bank_num);
				
				if (result.code < 1) {
					LoggerUtil.error(true, "进行用户信息查询时：%s", result.msg);
					
					flash.error(result.msg);
					
					securityPre();
				}
			}
			//用户的实名信息在富友校验出现异常时，清除掉平台录入的实名信息
			else if (respCode.endsWith("5851") || respCode.endsWith("5852") || respCode.endsWith("1008")) 
			{
              flash.error(result.msg);

				result = userInfoService.updateRealName(userId, "", "");
				
				userService.flushUserCache(userId);
				
				securityPre();
			}
			//因其它错误引发开户失败
			else
			{ 
              flash.error(result.msg);

				userService.flushUserCache(userId);
				
				securityPre();
			}
		}
		
		flash.success("开通富友托管账户成功");
		
		securityPre();
	}
	
	/**
	 * 进入实名认证页面（富友接口）
	 */
	public static void updateRealNamePre() {
		t_user_info userInfo = userInfoService.findUserInfoByUserId(getCurrUser().id);
		
		render(userInfo);
	}
	
	/**
	 * 更换手机号码
	 */
	public static void changeUserMobilePre() {
		CurrUser currUser = getCurrUser();
		
		if (currUser == null) {
			flash.error("用户数据异常");
			
			securityPre();
		}
		
		//业务订单号
		String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.CHANGE_USER_MOBILE);
		
		ResultInfo result = PaymentProxy.getInstance().changeUserMobile(Client.PC.code, serviceOrderNo, currUser.id, currUser.payment_account);
		
		if (result.code < 1) {
			LoggerUtil.error(true, "更换手机号码时：%s", result.msg);
			
			flash.error("更换手机号码失败");
			
			securityPre();
		}
		
		flash.error("更换手机号码成功");
		
		securityPre();
	}
	
	public static void sendSmsCode(int type) {

		ResultInfo result = new ResultInfo();
		
		CurrUser currUser = getCurrUser();
		
		if(currUser == null){
			
			result.code = -1;
			result.msg = "请先登录";
			renderJSON(result);
		}
		
		BusiType busiType = BusiType.getTypeByCode(type);
		
		if(busiType == null) {
			result.code = -1;
			result.msg = "发送短信验证码类型错误";
			renderJSON(result);
		}
		
		String cardId = params.get("cardId");
		String mobile = params.get("mobile");
		
		if(!StrUtil.isMobileNum(mobile)) {
			result.code = -1;
			result.msg = "手机号码格式错误";
			renderJSON(result);
		}
		
		if(busiType.code != 1 && !StrUtil.isBankAccount(cardId)) {
			result.code = -1;
			result.msg = "银行卡号格式错误";
			renderJSON(result);
		}
		
		// 业务订单号
		String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.SENDSMSCODE);
		
		result = PaymentProxy.getInstance().sendSmsCode(Client.PC.code, serviceOrderNo, currUser.id, cardId, busiType, mobile);
		
		if(result.code == 1) {
			// 放入session中
			String encryString = Session.current().getId();
			if(busiType.code == BusiType.ORGIN_BIND.code) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("orginSmsSeq", result.obj);
				map.put("busiType", busiType);
				Cache.set(CacheKey.ORGIN_SMSCODE_ + encryString, map, Constants.CACHE_TIME_MINUS_30);
			} else {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("smsSeq", result.obj);
				map.put("busiType", busiType);
				Cache.set(CacheKey.SMSCODE_ + encryString, map, Constants.CACHE_TIME_MINUS_30);
			}
			// 清除序列号
			result.obj = null;
		}
		
		renderJSON(result);
		
	}
	
	public static void getAreas(String code) {
		Area[] areas = Province.getAreasByCode(code);
		StringBuilder s = new StringBuilder("");
		if(areas != null) {
			for(Area area : areas) {
				s.append("<option value='" + area.code + "'>" + area.name + "</option>");
			}
		}
		renderHtml(s);
	}
	
	public static void checkHfName(String hfName) {
		renderJSON(userInfoService.checkHfname(hfName));
	}
}
