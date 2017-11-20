package controllers.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;
import play.Logger;

import com.shove.Convert;
import com.shove.gateway.GeneralRestGateway;
import com.shove.gateway.GeneralRestGatewayInterface;

import common.constants.AppConstants;
import common.constants.ConfConst;
import common.constants.Constants;
import common.enums.InformationMenu;
import common.utils.LoggerUtil;
import common.utils.ResultInfo;
import common.utils.Security;
import controllers.app.Invest.DebtAction;
import controllers.app.Invest.ExpBidAction;
import controllers.app.Invest.InvestAction;
import controllers.app.aboutUs.AboutUsAction;
import controllers.app.aboutUs.AndMoreAction;
import controllers.app.account.AccountAction;
import controllers.app.expGold.ExpGoldAction;
import controllers.app.notice.ActivityCenterAction;
import controllers.app.notice.AreaAction;
import controllers.app.notice.FrequentlyAskedQuestionsAction;
import controllers.app.notice.NoticeAction;
import controllers.app.wealth.IntegralMallAction;
import controllers.app.wealth.MyDealAction;
import controllers.app.wealth.MyExpBidAction;
import controllers.app.wealth.MyFundAction;
import controllers.app.wealth.MyInfoAction;
import controllers.app.wealth.MyReceiveBillAction;
import controllers.app.wealth.MySecurityAction;
import controllers.app.wealth.RechargeAWithdrawalAction;
import controllers.common.BaseController;
import models.common.entity.t_advertisement.Location;

public class AppController extends BaseController implements GeneralRestGatewayInterface{

	/**
	 * app端请求服务器入口
	 *
	 * @throws IOException
	 *
	 * @author yaoyi
	 * @createDate 2016年3月30日
	 */
	public static void index() throws IOException{
		StringBuilder errorDescription = new StringBuilder();
		AppController app = new AppController();
		
		int code =GeneralRestGateway.handle(ConfConst.ENCRYPTION_APP_KEY_MD5, 3000, app, errorDescription);
    	if(code < 0) {
    		Logger.error("%s", errorDescription);
    	}
	}
	
	
	
	/**
	 * 扫描二维码下载
	 *
	 * @author yaoyi
	 * @createDate 2016年3月30日
	 */
	public static void download(){
		render();
	}
	
	/**
	 * ios微信扫码下载提示页面
	 *
	 * @author huangyunsong
	 * @createDate 2016年5月17日
	 */
	public static void iosTip(String path){
		render(path);
	}
	

	
	@Override
	public String delegateHandleRequest(Map<String, String> parameters, StringBuilder errorDescription) throws RuntimeException {
		String result = null;
		long timestamp = new Date().getTime();
		LoggerUtil.info(false, "客户端请求(%s):【%s】",timestamp+"", JSONObject.fromObject(parameters));
		switch(Integer.parseInt(parameters.get("OPT"))){
			case AppConstants.APP_LOGIN:
				try{
					result = AccountAction.logining(parameters);//123
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "用户登录时：%s", e.getMessage());
					result = errorHandling();
				}
				break;
			
			case AppConstants.APP_SEND_CODE:
				try{
					result = AccountAction.sendCode(parameters);//111
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "发送短信验证码时：%s", e.getMessage());
					result = errorHandling();
				}
				break;
				
			case AppConstants.APP_REGISTER_PROTOCOL:
				try{
					result = AboutUsAction.registerProtocol(parameters);//112
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "加载注册协议时：%s", e.getMessage());
					result = errorHandling();
				}
				break;			
			case AppConstants.APP_REGISTERING:
				try{
					result = AccountAction.registering(parameters);//113
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "会员注册时：%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_PAYACCOUNT_OPEN_PRE:
				try{
					result = AccountAction.createAccountPre(parameters);//128
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "会员开户准备时：%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.APP_QUERY_CITY:
				try{
					result = AccountAction.queryCity(parameters);//129
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询城市列表时：%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_PAYACCOUNT_OPEN:
				try{
					result = AccountAction.createAccount(parameters);//114
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "会员开户时：%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.APP_REGISTER_PRE:
				try{
					result = AccountAction.registerPre(parameters);//115
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "准备注册时：%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_BIND_EMAIL_PRE:
				try{
					result = AccountAction.bindEmailPre(parameters);//124
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "准备绑定邮箱时：%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_BIND_EMAIL:
				try{
					parameters.put("baseUrl", getBaseURL()); //获取到baseUrl
					
					result = AccountAction.bindEmail(parameters);//125
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "绑定邮箱时：%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_RNAUTH_PRE:
				try{
					result = AccountAction.rnAuthPre(parameters);//126
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "准备实名认证时：%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_RNAUTH:
				try{
					result = AccountAction.rnAuth(parameters);//127
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "实名认证时：%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.APP_USER_BANK_LIST:
				try{
					result = MySecurityAction.listUserBankCard(parameters);//221
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询会员银行卡列表时：%s", e.getMessage());
					result = errorHandling();
				}
				break;	
				
			case AppConstants.APP_BIND_CARD:
				try{
					result = MySecurityAction.bindCard(parameters);//222
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "会员绑卡时：%s", e.getMessage());
					result = errorHandling();
				}
				break;		
			case AppConstants.APP_SET_DEFAULT_BANKCARD:
				try{
					result = MySecurityAction.setDefaultBankCard(parameters);//223
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "设置默认卡时：%s", e.getMessage());
					result = errorHandling();
				}
				break;		
				
			case AppConstants.APP_UPDATE_PWD:
				try{
					result = AccountAction.updateUserPwd(parameters);//122
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "用户更改密码时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_VERIFICATION_CODE:
				try{
					result = AccountAction.verificationCode(parameters);//121
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "验证验证码时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_WITHDRAWAL:
				try{
					result = RechargeAWithdrawalAction.withdrawal(parameters);//214
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "提现时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_WITHDRAWAL_PRE:
				try{
					result = RechargeAWithdrawalAction.withdrawalPre(parameters);//213
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "准备提现时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_WITHDRAWAL_RECORD:
				try{
					result = RechargeAWithdrawalAction.pageOfWithdraw(parameters);//215
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询提现记录时:%s", e.getMessage());
					result = errorHandling();
				}
				break;

			case AppConstants.APP_USER_INFO:
				try{
					result = MyInfoAction.userInfomation(parameters);//251
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询个人基本信息时:%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.APP_RECHARGE_PRE:
				try{
					result = RechargeAWithdrawalAction.rechargePre(parameters);//216
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "准备充值时:%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.APP_RECHARGE:
				try{
					result = RechargeAWithdrawalAction.recharge(parameters);//211
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "充值时:%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.APP_RECHARGE_RECORDS:
				try{
					result = RechargeAWithdrawalAction.pageOfRecharge (parameters);//212
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询充值交易记录时:%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.APP_MESSAGE:
				try{
					result = MyInfoAction.pageOfUserMessage(parameters);//252
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询消息列表时:%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.APP_RETUEN_MONEY_PLAN:
				try{
					result = MyReceiveBillAction.pageOfReceiveBill(parameters);//242
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询回款计划时:%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.APP_MYINVEST:
				try{
					result = MyFundAction.pageOfMyInvest(parameters);//231
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询我的投资时:%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.APP_INVEST_BILL:
				try{
					result = MyFundAction.listOfInvestBill(parameters);//232
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询理财账单详情时:%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.APP_INVEST_BILL_INFO:
				try{
					result = MyFundAction.investBillInfo(parameters);//237
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询理财账单详情时:%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.APP_LOAN:
				try{
					result = MyFundAction.pageOfMyLoan(parameters);//233
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询我的借款时:%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.APP_LOAN_BILL:
				try{
					result = MyFundAction.listOfLoanBill(parameters);//234
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询借款账单时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_LOAN_BILL_INFO:
				try{
					result = MyFundAction.findLoanBill(parameters);//238
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询借款账单详情时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_LOAN_REPAYMENT:
				try{
					result = MyFundAction.repayment(parameters);//235
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "还款时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_BID_PACT:
				try{
					result = MyFundAction.showBidPact(parameters);//236
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询理财协议时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_DEBT://我的受让/转让
				try{
					result = MyFundAction.pageOfDebt(parameters);//239
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "我的受让:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_DEBT_PACT://转让协议
				try{
					result = MyFundAction.showDebtPact(parameters);//2312
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "我的受让:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_APPLAY_DEBT_PRE:
				try{
					result = MyFundAction.applyDebtPre(parameters);//2313
				}catch(Exception e){
					LoggerUtil.error(true, "债权申请准备:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_APPLAY_DEBT:
				try{
					result = MyFundAction.applyDebtTransfer(parameters);//2314
				}catch(Exception e){
					LoggerUtil.error(true, "债权申请:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_USER_DEAL_RECORD:
				try{
					result = MyDealAction.pageOfUserDealRecords(parameters);//241
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询交易记录时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_USER_INFO_DETAIL:
				try{
					result = MyInfoAction.toUserInfo(parameters);//253
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询用户信息时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_USER_INFO_UPDATE:
				try{
					result = MyInfoAction.updateUserInfo(parameters);//254
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "保存用户信息时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_USER_PHOTO_UPDATE:
				try{
					result = MyInfoAction.updatePhoto(parameters);//255
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "保存用户信息时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			//新版
			case AppConstants.APP_PROVINCE:
				try{
					result =AreaAction.getAllProvince();//256
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "获取省级时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_AREA:
				try{
					result =AreaAction.getAreaByProvinceId(parameters);//257
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "获取市级时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
				
			case AppConstants.APP_SECURITY:
				try{
					result = MySecurityAction.userSecurity(parameters);//261
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询安全中心时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_UPDATE_PWDBYOLD:
				try{
					result = MySecurityAction.userUpdatePwdbyold(parameters);//262
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "通过原密码更新密码时:%s", e.getMessage());
					result = errorHandling();
				}
				break;			
			case AppConstants.APP_UPDATE_EMAIL:
				try{
					result = MySecurityAction.updateEmail(parameters);//263
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "修改邮箱时:%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.APP_EXP_BID_MYACCOUNT:
				try{
					result = MyExpBidAction.myExpBidAccount(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询体验金账户信息时:%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.APP_EXP_BID_MYINVEST:
				try{
					result = MyExpBidAction.pageOfMyExpBidInvest(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询我的体验标投资记录时:%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.APP_EXP_BID_GOLD_GET:
				try{
					result = MyExpBidAction.getExperienceGold(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "领取体验金时:%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.APP_EXP_BID_INCOME_CONVERSION:
				try{
					result = MyExpBidAction.applayConversion(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "兑换体验金收益时:%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.APP_INVEST_BIDS:
				try{
					result = InvestAction.pageOfInvestBids(parameters);//311
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询理财产品列表时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_INVEST_BID_INFORMATION:
				try{
					result = InvestAction.investBidInformation(parameters);//312
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询理财标详情时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_INVEST:
				try{
					result = InvestAction.invest(parameters);//321
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "投标时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_INVEST_BIDS_DETAILS:
				try{
					result = InvestAction.investBidDeatils(parameters);//322
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询借款标详情时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_INVEST_BIDS_REPAYMENT_PLAN:
				try{
					result = InvestAction.listOfRepaymentBill(parameters);//323
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询标回款计划时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
				
			case AppConstants.APP_INVEST_BIDS_RECORDS:
				try{
					result = InvestAction.investBidsRecord(parameters);//324
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询投标记录时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_DEBTS:
				try{
					result = DebtAction.pageOfDebts(parameters);//331
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "分页查询债权时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_DEBT_DETAIL:
				try{
					result = DebtAction.debtDetail(parameters);//332
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询债权转让信息详情:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_DEBT_BILLS:
				try{
					result = DebtAction.paymentsOfDebt(parameters);//333
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询债权回款计划:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_BUY_DEBT:
				try{
					result = DebtAction.buyDebt(parameters);//334
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "购买债权:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_EXP_BID:
				try{
					result = ExpBidAction.experienceBid(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询体验标信息:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_EXP_BID_DETATIL:
				try{
					result = ExpBidAction.experienceBidDetail();
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询体验标借款详情:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_EXP_BID_INVEST_RECORD:
				try{
					result = ExpBidAction.expBidInvestRecord(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询体验标投资记录:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_EXP_BID_INVEST:
				try{
					result = ExpBidAction.investExpBid(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "购买体验标:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_COMPANY_INFO:
				try{
					result = AboutUsAction.aboutUs(parameters);//411
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询公司介绍时:%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.APP_CONTACT_US:
				try{
					result = AboutUsAction.contactUs(parameters);//421
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询联系我们时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_PLATFORM_ICON:
				try{
					result = AccountAction.findPlatformIconFilename(parameters);//124
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询平台logo时：%s", e.getMessage());
					result = errorHandling();
				}
				break;		
				
			case AppConstants.APP_VERSION:
				try{
					result = AboutUsAction.getPlatformInfo(parameters);//423
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询APP版本时：%s", e.getMessage());
					result = errorHandling();
				}
				break;				
			case AppConstants.APP_INDEX:
				try{
					result = HomeAction.index(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询APP首页时：%s", e.getMessage());
					result = errorHandling();
				}
				break;				
			case AppConstants.APP_MALL://611
				try{
					result = IntegralMallAction.showMall();
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询APP积分商城时：%s", e.getMessage());
					result = errorHandling();
				}
				break;				
			case AppConstants.APP_REDPACKET://612
				try{
					result = IntegralMallAction.showMyRedPacket(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询APP红包时：%s", e.getMessage());
					result = errorHandling();
				}
				break;				
			case AppConstants.APP_ADDRATE://613
				try{
					result = IntegralMallAction.showMyRates(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询APP加息券时：%s", e.getMessage());
					result = errorHandling();
				}
				break;				
			case AppConstants.APP_CASH://614
				try{
					result = IntegralMallAction.showMyCash(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询APP现金券时：%s", e.getMessage());
					result = errorHandling();
				}
				break;				
			case AppConstants.APP_ADDADDRESS://622
				try{
					result = IntegralMallAction.addAddress(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "添加APP收货地址时：%s", e.getMessage());
					result = errorHandling();
				}
				break;				
			case AppConstants.APP_ADDRESSLIST://621
				try{
					result = IntegralMallAction.addressList(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询APP地址详情时：%s", e.getMessage());
					result = errorHandling();
				}
				break;				
			case AppConstants.APP_GUARANTEE://424
				try{
					result = AboutUsAction.Guarantee();
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询APP安全保障时：%s", e.getMessage());
					result = errorHandling();
				}
				break;				
			case AppConstants.APP_GUIDE://425
				try{
					result = AboutUsAction.guide(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询APP新手指南时：%s", e.getMessage());
					result = errorHandling();
				}
				break;				
			case AppConstants.APP_ADDRESSLISTREADY://624
				try{
					result = IntegralMallAction.addAddressReady();
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "初始化APP新增地址时：%s", e.getMessage());
					result = errorHandling();
				}
				break;				
			case AppConstants.APP_SHOWMALLGOODS://631
				try{
					result = IntegralMallAction.showMallGoods(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询APP积分商品详情时：%s", e.getMessage());
					result = errorHandling();
				}
				break;				
			case AppConstants.APP_EXCHANGEGOODS://632
				try{
					result = IntegralMallAction.exchangeGoods(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询APP积分商品详情时：%s", e.getMessage());
					result = errorHandling();
				}
				break;				
			case AppConstants.APP_ANDMORE://426
				try{
					result = AndMoreAction.andMore(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询更多模块时：%s", e.getMessage());
					result = errorHandling();
				}
				break;				
			case AppConstants.APP_SIGNIN://512
				try{
					result = HomeAction.signIn(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "APP签到时：%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_HELP://427
				try{
					result = AndMoreAction.goHelp(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询帮助中心时：%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_NOVICE://428
				try{
					result = AboutUsAction.novice();
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询新手福利时：%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_HF_SENDSMSCODE://701
				try{
					result = CommonAction.sendSmsCode(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "上海银行存管发送短信验证码时：%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_CHECK_HF_NAME://702
				try{
					result = CommonAction.checkHfName(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "检查汇付用户号时：%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_BOSACCTACTIVATE://703
				try{
					result = AccountAction.bosAcctActivate(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "上海银行存管账户激活时：%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_QUICKBINDING://704
				try{
					result = AccountAction.quickBinding(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "上海银行存管账户激活时：%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_QUERY_SERVERFEE://705
				try{
					result = CommonAction.queryServFee(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "上海银行存管账户激活时：%s", e.getMessage());
					result = errorHandling();
				}
				break;
				
			  case AppConstants.APP_NOTICE://800
					try{
						result = NoticeAction.getInformation();
					}catch(Exception e){
						e.printStackTrace();
						LoggerUtil.error(true, "查询首页公告时：%s", e.getMessage());
						result = errorHandling();
					}
					break;
			  case AppConstants.APP_NOTICE_INFO://801
					try{
						result = NoticeAction.getInformationById(parameters);
					}catch(Exception e){
						e.printStackTrace();
						LoggerUtil.error(true, "查询首页公告详情时：%s", e.getMessage());
						result = errorHandling();
					}
					break;
			  case AppConstants.APP_NOTICE_LIST://802
					try{
						
						result = NoticeAction.getInformationList(parameters);
					}catch(Exception e){
						e.printStackTrace();
						LoggerUtil.error(true, "查询首页资讯列表时：%s", e.getMessage());
						result = errorHandling();
					}
					break;
			  case AppConstants.APP_ACTIVITY_CENTER://900
					try{
						
						result = ActivityCenterAction.activityList();
					}catch(Exception e){
						e.printStackTrace();
						LoggerUtil.error(true, "查询活动列表时：%s", e.getMessage());
						result = errorHandling();
					}
					break;
			  case AppConstants.APP_FREQUENTLY_ASKED_QUESTIONS://901
					try{
						
						result = FrequentlyAskedQuestionsAction.FrequentlyAskedQuestionsList(parameters);
					}catch(Exception e){
						e.printStackTrace();
						LoggerUtil.error(true, "查询常见问题列表时：%s", e.getMessage());
						result = errorHandling();
					}
					break;
			  case AppConstants.APP_OPERATION_REPORT://1000
					try{
						
						result = ActivityCenterAction.operationReportList(parameters);
					}catch(Exception e){
						e.printStackTrace();
						LoggerUtil.error(true, "查询活动列表时：%s", e.getMessage());
						result = errorHandling();
					}
					break;
						case AppConstants.APP_REC_EXP_GOLD://1101
				try{
					result = ExpGoldAction.appReceiveExpGold(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "领取体验金时：%s", e.getMessage());
					result = errorHandling();
				}
				break;
				
			case AppConstants.APP_QUERY_EXP_GOLD_ACCOUNT://1102
				try{
					result = ExpGoldAction.queryAppExpGoldAccountUserByUserId(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询用户体验金账户时：%s", e.getMessage());
					result = errorHandling();
				}
				break;
				
			case AppConstants.APP_SHOW_EXP_GOLD://1103
				try{
					result = ExpGoldAction.showExpGold(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询用户体验金账户时：%s", e.getMessage());
					result = errorHandling();
				}
				break;
				
			case AppConstants.APP_SCORE_RECORD://1201
				try{
					result = MyDealAction.listOfScoreRecordsPre(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询用户积分记录时：%s", e.getMessage());
					result = errorHandling();
				}
				break;
					
		}
	
		LoggerUtil.info(false, "服务器响应(%s):【%s】",timestamp+"", result);
		return result;
	}
	
	public static void index2(){
		//首页
/*		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put("OPT", 511+"");
		String signID = Security.addSign(358L, Constants.INVEST_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);
		String userId= Security.addSign(33L, Constants.USER_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);
		parameters.put("userId",userId );
		String result = new AppController().delegateHandleRequest(parameters, null);
		System.out.println(result);
		renderJSON(result);*/
		
		/*
		AppConstants.APP_APPLAY_DEBT_PRE债权转让申请准备
		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put("OPT", AppConstants.APP_APPLAY_DEBT_PRE+"");
		String signID = Security.addSign(358L, Constants.INVEST_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);
		parameters.put("investId", signID);
		
		String result = new AppController().delegateHandleRequest(parameters, null);
		System.out.println(result);
		renderJSON(result);
		*/
		
		/*
		//AppConstants.APP_APPLAY_DEBT债权转让申请
		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put("OPT", AppConstants.APP_APPLAY_DEBT+"");
		String signID = Security.addSign(358L, Constants.INVEST_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);
		parameters.put("investId", signID);
		parameters.put("title", "358转让啦");
		parameters.put("period", "2");
		parameters.put("transferPrice", "4500");
		
		String result = new AppController().delegateHandleRequest(parameters, null);
		System.out.println(result);
		renderJSON(result);
		*/
		/*
		//AppConstants.APP_MYINVEST我的投资列表
		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put("OPT", AppConstants.APP_MYINVEST+"");
		String signID = Security.addSign(189L, Constants.USER_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);
		parameters.put("userId", signID);
		parameters.put("currPage", "1");
		parameters.put("pageSize", "5");
		
		String result = new AppController().delegateHandleRequest(parameters, null);
		System.out.println(result);
		renderJSON(result);
		*/
		/*
		//AppConstants.APP_LOAN我的投资列表
		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put("OPT", AppConstants.APP_LOAN+"");
		String signID = Security.addSign(188L, Constants.USER_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);
		parameters.put("userId", signID);
		parameters.put("currPage", "1");
		parameters.put("pageSize", "5");
		
		String result = new AppController().delegateHandleRequest(parameters, null);
		System.out.println(result);
		renderJSON(result);
		*/
		
		/*
		//AppConstants.APP_INDEBT我的受让
		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put("OPT", AppConstants.APP_DEBT+"");
		String signID = Security.addSign(189L, Constants.USER_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);
		parameters.put("userId", signID);
		parameters.put("debtOf", "1");
		parameters.put("currPage", "1");
		parameters.put("pageSize", "5");
		
		String result = new AppController().delegateHandleRequest(parameters, null);
		System.out.println(result);
		renderJSON(result);
		*/
		/*
		//AppConstants.APP_DEBT_PACT转让协议
		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put("OPT", AppConstants.APP_DEBT_PACT+"");
		String signID = Security.addSign(37L, Constants.DEBT_TRANSFER_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);
		parameters.put("debtId", signID);

		
		String result = new AppController().delegateHandleRequest(parameters, null);
		System.out.println(result);
		renderJSON(result);
		*/
		
		//AppConstants.APP_DEBTS债权转让列表
/*		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put("OPT", AppConstants.APP_DEBTS+"");
		String signID = Security.addSign(37L, Constants.DEBT_TRANSFER_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);
		parameters.put("currPage", "1");
		parameters.put("pageSize", "20");

		
		String result = new AppController().delegateHandleRequest(parameters, null);
		System.out.println(result);
		renderJSON(result);
		*/
		
		//AppConstants.APP_DEBT_DETAIL债权转让详细信息
/*		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put("OPT", AppConstants.APP_DEBT_DETAIL+"");
		String signID = Security.addSign(4L, Constants.DEBT_TRANSFER_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);
		parameters.put("debtId", signID);

		
		String result = new AppController().delegateHandleRequest(parameters, null);
		System.out.println(result);
		renderJSON(result);*/
		
	
		
		/*//AppConstants.APP_DEBT_DETAIL债权回款计划
		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put("OPT", AppConstants.APP_DEBT_BILLS+"");
		String signID = Security.addSign(357L, Constants.INVEST_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);
		parameters.put("investId", signID);

		
		String result = new AppController().delegateHandleRequest(parameters, null);
		System.out.println(result);
		renderJSON(result);
		*/
		//AppConstants.APP_BUY_DEBT购买债权
	/*	Map<String,String> parameters = new HashMap<String,String>();
		parameters.put("OPT", AppConstants.APP_BUY_DEBT+"");
		String debtId = Security.addSign(1L, Constants.DEBT_TRANSFER_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);
		String userId = Security.addSign(13L, Constants.USER_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);//17700000001
		parameters.put("debtId", debtId);
		parameters.put("userId", userId);
		parameters.put("deviceType", "2");
		
		String result = new AppController().delegateHandleRequest(parameters, null);
		System.out.println(result);
		renderJSON(result);*/
		
		// AppConstants.APP_INVEST_BIDS 理财产品列表
	/*Map<String,String> parameters = new HashMap<String,String>();
		parameters.put("OPT", AppConstants.APP_INVEST_BIDS+"");
	
		parameters.put("pageSize","5");
		parameters.put("currPage", "1");
		String result=new AppController().delegateHandleRequest(parameters, null);
		System.out.println(result);
		renderJSON(result);
		*/
		//AppConstants.APP_INVEST_BID_INFORMATION 标的详情 312
	/*	Map<String,String> parameters = new HashMap<String,String>();
		parameters.put("OPT", AppConstants.APP_INVEST_BID_INFORMATION+"");
		String debtId =Security.addSign(93L, Constants.BID_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);//64144CC04707DB56D64F8DC34A7E2197019A23B0F0410D5Ca3e8b33d";//
		String userId = Security.addSign(13L, Constants.USER_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);//17700000001
		parameters.put("bidIdSign",debtId);
		parameters.put("userId", userId);
		String result=new AppController().delegateHandleRequest(parameters, null);
		System.out.println(result);
		renderJSON(result);*/
	
	
		
		//AppConstants.APP_NOTICE 首页公告接口800
	/*	Map<String,String> parameters = new HashMap<String,String>();
			parameters.put("OPT", AppConstants.APP_NOTICE+"");
			String result=new NoticeAction().getInformation();
			System.out.println(result);
			renderJSON(result);*/
		
		//AppConstants.APP_NOTICE 首页公告详情接口801
		/*	Map<String,String> parameters = new HashMap<String,String>();
			String informationIdSign="D2EA5FDA87BC1D012A95D87E141A7CE185723880B13258A79390F33595CA72B82d4efe8a";//Security.addSign(31, Constants.INFORMATION_ID_SIGN,  ConfConst.ENCRYPTION_KEY_DES);
			parameters.put("informationId",informationIdSign);
			parameters.put("OPT", AppConstants.APP_NOTICE_INFO+"");
			String result=new NoticeAction().getInformationById(parameters);
			System.out.println(result);
			renderJSON(result);*/
		
		//AppConstants.APP_NOTICE 首页公告列表接口802
		/*Map<String,String> parameters = new HashMap<String,String>();
		List<String> informationMenus=new ArrayList<String>();
		informationMenus.add(InformationMenu.INFO_BULLETIN.code);
		parameters.put("currPage", "1");
		parameters.put("pageSize", "5");
		parameters.put("informationMenu", "3");
		parameters.put("OPT", AppConstants.APP_NOTICE_LIST+"");
		String result=new AppController().delegateHandleRequest(parameters, null);
		System.out.println(result);
		renderJSON(result);*/
		
		//AppConstants.APP_USER_INFO 个人基本信息
/*		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put("deviceType", "2");
		
		parameters.put("userId",Security.addSign(13, Constants.USER_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES));
		parameters.put("OPT", AppConstants.APP_USER_INFO +"");
		String result=new AppController().delegateHandleRequest(parameters, null);
		System.out.println(result);
		renderJSON(result);*/
		
		//加息卷
	/*	Map<String,String> parameters = new HashMap<String,String>();
		parameters.put("deviceType", "2");
		int pageSize=Convert.strToInt(parameters.get("pageSize"), 15);
		int currPage=Convert.strToInt(parameters.get("currPage"), 1);
		parameters.put("userId",Security.addSign(13, Constants.USER_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES) );
		parameters.put("OPT", 613+"");
		String result=new AppController().delegateHandleRequest(parameters, null);
		System.out.println(result);
		renderJSON(result);*/
		
		//活动列表
		/*Map<String,String> parameters = new HashMap<String,String>();
		parameters.put("OPT", 900+"");
		String result=new AppController().delegateHandleRequest(parameters, null);
		System.out.println(result);
		renderJSON(result);*/
		
		//运营报告
/*		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put("OPT", "1000");
		parameters.put("pageSize", "10");
		parameters.put("currPage", "1");
		String result=new AppController().delegateHandleRequest(parameters, null);
		System.out.println(result);
		renderJSON(result);*/
		
		//借款人详情
/*		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put("OPT", AppConstants.APP_INVEST_BIDS_DETAILS+"");
		String debtId = Security.addSign(22L, Constants.BID_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);
		parameters.put("bidIdSign",debtId);
		String result=new AppController().delegateHandleRequest(parameters, null);
		System.out.println(result);
		renderJSON(result);*/
		
		//AppConstants.APP_INVEST:投标接口
/*		Map<String,String> parameters = new HashMap<String,String>();
		String debtId = Security.addSign(95L, Constants.BID_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);
		parameters.put("OPT", AppConstants.APP_INVEST+"");
		parameters.put("userId",Security.addSign(13, Constants.USER_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES) );
		parameters.put("rateId","1B5EBE4EBACDF9F7A20CE00DD4E9B742BB8CB3CE887ADC4C5B9A79DF5EF966561a4d88e0");
		parameters.put("bribeId","E84F35705BEC17C8D8E26C2713496DFE6FCE7C7FCE7B859FC5C47DBDA02C3A183bfc240d");
		parameters.put("investAmt","2000");
		parameters.put("bidIdSign",debtId);
		parameters.put("deviceType", "3");
		String result=new AppController().delegateHandleRequest(parameters, null);
		System.out.println(result);
		renderJSON(result);*/
		
        //常见问题列表
/*		Map<String, String > parameters=new HashMap<String, String>();
		
		parameters.put("OPT","901");
		parameters.put("pageSize","6");
		parameters.put("currPage", "1");
		parameters.put("column_no","9");
		String result=new AppController().delegateHandleRequest(parameters,null);
		System.out.println(result);
		renderJSON(result);*/
		//安全中心
/*		Map<String, String > parameters=new HashMap<String, String>();
		
		parameters.put("OPT","261");
		parameters.put("userId",Security.addSign(13,Constants.USER_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES));
		String result=new AppController().delegateHandleRequest(parameters,null);
		System.out.println(result);
		renderJSON(result);*/
		
		// 253 客户端获取会员信息接口
/*    	Map<String, String > parameters=new HashMap<String, String>();
		
		parameters.put("OPT","253");
		parameters.put("userId",Security.addSign(13,Constants.USER_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES));
		String result=new AppController().delegateHandleRequest(parameters,null);
		System.out.println(result);
		renderJSON(result);*/
		
		//254客户端保存会员信息接口
/*	Map<String, String > parameters=new HashMap<String, String>();
		
		parameters.put("OPT","254");
		parameters.put("userId",Security.addSign(13,Constants.USER_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES));
		parameters.put("car","1");
		parameters.put("area_id","1200");
		parameters.put("work_unit","虹奥金融股份有限公司");
		parameters.put("house","1");
		parameters.put("education","1");
		parameters.put("prov_id","0012");
		parameters.put("registered_fund","22222");
		parameters.put("annualIncome","3");
		parameters.put("emergencyContactName","紧急联系人名");
		parameters.put("workExperience","4");
		parameters.put("start_time","2017-10");
		parameters.put("emergencyContactMobile","13888888888");
		parameters.put("emergencyContactType","3");
		parameters.put("marital","2");
		parameters.put("netAsset","1");
		
		String result=new AppController().delegateHandleRequest(parameters,null);
		System.out.println(result);
		renderJSON(result);*/
		
		//256 获取省级
	/*	Map<String, String > parameters=new HashMap<String, String>();
		parameters.put("OPT","256");
		String result=new AppController().delegateHandleRequest(parameters,null);
		System.out.println(result);
		renderJSON(result);*/
		
		//257 获取市级
/*		Map<String, String > parameters=new HashMap<String, String>();
		parameters.put("OPT","257");
		parameters.put("code","0014");
		String result=new AppController().delegateHandleRequest(parameters,null);
		System.out.println(result);
		renderJSON(result);*/
		
		//252 用户消息
	/*	Map<String, String > parameters=new HashMap<String, String>();
		parameters.put("OPT","252");
		parameters.put("userId",Security.addSign(13,Constants.USER_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES));
		parameters.put("pageSize","3");
		parameters.put("currPage","1");
		String result=new AppController().delegateHandleRequest(parameters,null);
		System.out.println(result);
		renderJSON(result);*/
		//APP签到
	/*	Map<String, String > parameters=new HashMap<String, String>();
		parameters.put("OPT","512");
		parameters.put("userId",Security.addSign(13,Constants.USER_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES));
		String result=new AppController().delegateHandleRequest(parameters,null);
		System.out.println(result);
		renderJSON(result);*/
		
		Map<String, String > parameters=new HashMap<String, String>();
		parameters.put("OPT","1201");
		parameters.put("pageSize","10");
		parameters.put("currPage","1");
		parameters.put("userId",Security.addSign(13,Constants.USER_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES));
		String result=new AppController().delegateHandleRequest(parameters,null);
		System.out.println(result);
		renderJSON(result);
		
	}
	
	/***
	 * 
	 * 程序异常 信息统一提示
	 * @return
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-25
	 */
	private String errorHandling(){
		JSONObject json = new JSONObject();
		
		json.put("code", ResultInfo.ERROR_500);
		json.put("msg", "系统繁忙，请稍后再试");
		
		return json.toString();
		
	}

}
