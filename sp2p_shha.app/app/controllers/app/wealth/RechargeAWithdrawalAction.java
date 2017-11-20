package controllers.app.wealth;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;
import play.cache.Cache;

import org.apache.commons.lang.StringUtils;

import service.AccountAppService;
import services.common.BankCardUserService;
import services.common.UserInfoService;

import com.shove.Convert;
import common.constants.ConfConst;
import common.constants.Constants;
import common.constants.PaymentConst;
import common.enums.Bank;
import common.enums.BusiType;
import common.enums.CashType;
import common.enums.Client;
import common.enums.DeviceType;
import common.enums.TradeType;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.StrUtil;
import common.utils.UserValidataUtil;
import models.common.entity.t_bank_card_user;

/**
 * 充值提现
 *
 * @description 充值，提现，充值记录，提现记录
 *
 * @author DaiZhengmiao
 * @createDate 2016年6月29日
 */
public class RechargeAWithdrawalAction {

	private static AccountAppService accountAppService = Factory.getService(AccountAppService.class);
	
	private static BankCardUserService bankCardUserService = Factory.getService(BankCardUserService.class);
	
	/**
	 * APP进入提现页面
	 *
	 * @param parameters
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年4月7日
	 */
	public static String withdrawalPre(Map<String, String> parameters) throws Exception{
		JSONObject json = new JSONObject();
		
		String userIdSign = parameters.get("userId");
		ResultInfo result = Security.decodeSign(userIdSign, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
			 return json.toString();
		}
		long userId = Long.parseLong(result.obj.toString());
		
		if (!UserValidataUtil.checkPaymentAccount(userId)) {
			json.put("code", ResultInfo.NOT_PAYMENT_ACCOUNT);
			json.put("msg", "您还没有进行资金存管开户!");
			 
			return json.toString();
		}

		if (!UserValidataUtil.checkActivited(userId)) {
			json.put("code", ResultInfo.NO_ACTIVITED);
			json.put("msg", "您还没有激活存管账户!");
			 
			return json.toString();
		}
		
		//富有必须绑定邮箱
		if (ConfConst.CURRENT_TRUST_TYPE.equals(PaymentConst.TRUST_TYPE_FY)) {
			if (!UserValidataUtil.checkBindEmail(userId)) {
				 json.put("code", ResultInfo.NOT_BIND_EMAIL);
				 json.put("msg", "请绑定邮箱");
				 
				 return json.toString();
			}
		}
				
		if (!UserValidataUtil.checkRealName(userId)) {
			json.put("code", ResultInfo.NOT_REAL_NAME);
			json.put("msg", "您还没有进行实名认证!");
			 
			return json.toString();
		}
		if (!UserValidataUtil.checkBankCard(userId)) {
			json.put("code", ResultInfo.NOT_BIND_BANKCARD);
			json.put("msg", "您还没有进行绑卡!");
			 
			return json.toString();
		}
		
		return accountAppService.withdrawalPre(userId);
	}
	
	/**
	 * 我的财富-提现（OPT=214）
	 *
	 * @param parameters
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年3月31日
	 */
	public static String withdrawal(Map<String, String> parameters) {
		JSONObject json = new JSONObject();
		
		String userIdSign= parameters.get("userId");
		String amount = parameters.get("amount");
		String bankAccount = parameters.get("bankAccount");
		String cashType = parameters.get("cashType");
		String deviceType = parameters.get("deviceType");
		
		ResultInfo result = Security.decodeSign(userIdSign, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
			 return json.toString();
		}
		
		if(StringUtils.isBlank(cashType)) {
			json.put("code", -1);
			json.put("msg", "提现类型不能为空");
			return json.toString();
		}
		
		CashType type = CashType.getCashTypeByCode(cashType);
		
		if(type == null) {
			json.put("code", -1);
			json.put("msg", "请选择正确的提现类型");
			return json.toString();
		}
		
		if (!StrUtil.isNumericalValue(amount)) {
			json.put("code", -1);
			json.put("msg", "请输入正确的提现金额!");
			
			return json.toString();
		}
		if (StringUtils.isBlank(bankAccount)) {
			json.put("code", -1);
			json.put("msg", "卡号不能为空!");
			
			return json.toString();
		}

		if (DeviceType.getEnum(Convert.strToInt(deviceType, -99))==null) {
			json.put("code", -1);
			json.put("msg", "请使用移动客户端操作");
			
			return json.toString();
		}
		
		long userId = Long.parseLong(result.obj.toString());
		double withdrawalAmt = Double.parseDouble(amount);
		
		if (withdrawalAmt <= 0) {
			json.put("code", -1);
			json.put("msg", "提现金额必须大于0!");
			
			return json.toString();
		}	
		if (!UserValidataUtil.checkPaymentAccount(userId)) {
			json.put("code", ResultInfo.NOT_PAYMENT_ACCOUNT);
			json.put("msg", "您还没有进行资金存管开户!");
			 
			return json.toString();
		}
		if (!UserValidataUtil.checkRealName(userId)) {
			json.put("code", ResultInfo.NOT_REAL_NAME);
			json.put("msg", "您还没有进行实名认证!");
			 
			return json.toString();
		}
		if (!UserValidataUtil.checkBankCard(userId)) {
			 json.put("code", ResultInfo.NOT_BIND_BANKCARD);
			 json.put("msg", "请您先绑卡");
			 return json.toString();
		}
		
		result = accountAppService.withdrawal(userId, withdrawalAmt, bankAccount, cashType, Integer.parseInt(deviceType));
	   
		if (result.code < 0) {
			LoggerUtil.info(true, result.msg);
			json.put("code", result.code);
			json.put("msg", result.msg);

			return json.toString();
		}

		json.put("code", result.code);
		json.put("msg", result.msg);

		if (ConfConst.IS_TRUST) {
			
			json.put("html", result.obj.toString());
		}
		
		return json.toString();
	}

	/**
	 * 查询提现记录（OPT=215）
	 *
	 * @param parameters
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年3月31日
	 */
	public static String pageOfWithdraw(Map<String, String> parameters) {
		JSONObject json = new JSONObject();
		
		String signId = parameters.get("userId");
		String currPage = parameters.get("currPage");
		String pageSize = parameters.get("pageSize");
		if (StringUtils.isBlank(signId)) {
			json.put("code", ResultInfo.LOGIN_TIME_OUT);
			json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			
			return json.toString();
		}
		if (!StrUtil.isNumeric(currPage) || !StrUtil.isNumeric(pageSize)) {
			json.put("code", -1);
			json.put("msg", "分页参数错误!");
			
			return json.toString();
		}
		int curr = Integer.parseInt(currPage);
		int page = Integer.parseInt(pageSize);
		if (curr < 1) {
			curr = 1;
		}
		if (page < 1) {
			page = 10;
		}
		
		ResultInfo result = Security.decodeSign(signId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			json.put("code", -1);
			json.put("msg", result.msg);
			
			return json.toString();
		}
		
		return accountAppService.pageOfWithdrawRecord(Long.parseLong(result.obj.toString()), curr, page);
	}
	
	/***
	 * 充值准备（opt=216）
	 *
	 * @param parameters
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-12
	 */	
	public static String rechargePre(Map<String, String> parameters){
		JSONObject json = new JSONObject();

		String signId = parameters.get("userId");

		ResultInfo result = Security.decodeSign(signId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 return json.toString();
		}
		
		long userId = Long.parseLong(result.obj.toString());
		
		if (!UserValidataUtil.checkPaymentAccount(userId)) {
			json.put("code", ResultInfo.NOT_PAYMENT_ACCOUNT);
			json.put("msg", "您还没有进行资金存管开户!");
			 
			return json.toString();
		}

		if (!UserValidataUtil.checkActivited(userId)) {
			json.put("code", ResultInfo.NO_ACTIVITED);
			json.put("msg", "您还没有激活资金存管账户!");
			 
			return json.toString();
		}
		
		if (!UserValidataUtil.checkRealName(userId)) {
			 json.put("code", ResultInfo.NOT_REAL_NAME);
			 json.put("msg", "请实名认证");
			 return json.toString();
		}

		return accountAppService.rechargePre();    
	}
	
	/***
	 * 充值接口（OPT=211）
	 * @param parameters
	 * @return
	 * @throws Exception
	 *
	 * @author luzhiwei
	 * @createDate 2016-3-31
	 */
	public static String recharge(Map<String, String> parameters) throws Exception{
		JSONObject json = new JSONObject();
		
		String signId = parameters.get("userId");
		String amount = parameters.get("amount");
		String deviceType = parameters.get("deviceType");//设备类型
		String type = parameters.get("tradeType");
		
		ResultInfo result = Security.decodeSign(signId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
			 return json.toString();
		}
	    
    	if(!StrUtil.isNumeric(amount)){
	   		 json.put("code",-1);
			 json.put("msg", "充值金额不正确");
			 
			 return json.toString();
    	}
    	
		long userId = Long.parseLong(result.obj.toString());
		
		if (!UserValidataUtil.checkPaymentAccount(userId)) {
			json.put("code", ResultInfo.NOT_PAYMENT_ACCOUNT);
			json.put("msg", "您还没有进行资金存管开户!");
			 
			return json.toString();
		}
		
		//富有必须绑定邮箱
		if (ConfConst.CURRENT_TRUST_TYPE.equals(PaymentConst.TRUST_TYPE_FY)) {
			if (!UserValidataUtil.checkBindEmail(userId)) {
				 json.put("code", ResultInfo.NOT_BIND_EMAIL);
				 json.put("msg", "请绑定邮箱");
				 
				 return json.toString();
			}
		}
		
		if (!UserValidataUtil.checkRealName(userId)) {
			 json.put("code", ResultInfo.NOT_REAL_NAME);
			 json.put("msg", "请实名认证");
			 
			 return json.toString();
		}

		if (!UserValidataUtil.checkBankCard(userId)) {
			 json.put("code", ResultInfo.NOT_BIND_BANKCARD);
			 json.put("msg", "请您先绑卡");
			 
			 return json.toString();
		}
		
		if (DeviceType.getEnum(Convert.strToInt(deviceType, -99))==null) {
			json.put("code", -1);
			json.put("msg", "请使用移动客户端操作");
			
			return json.toString();
		}
		
		if (StringUtils.isBlank(type)) {
			json.put("code", -1);
			json.put("msg", "充值类型不能为空");
			return json.toString();
		}
		
		int code = 0;
		
		try {
			code = Integer.parseInt(type);
		} catch (Exception e) {
			json.put("code", -1);
			json.put("msg", "充值类型错误");
			return json.toString();
		}
		
		TradeType tradeType = TradeType.getTradeTypeByCode(code);
		
		if(tradeType == null) {
			json.put("code", -1);
			json.put("msg", "充值类型错误");
			return json.toString();
		}
		
		String smsCode = "";
		String smsSeq = "";
		String bankId = "";
		
		if(code == TradeType.QP.code) {
			// 快捷充值
			smsCode = parameters.get("smsCode");
			if(StringUtils.isBlank(smsCode)) {
				json.put("code", -1);
				json.put("msg", "短信验证码不能为空");
				return json.toString();
			}
			
			List<t_bank_card_user> bankUsers = bankCardUserService.queryCardByUserId(userId);
			
			if(bankUsers == null || bankUsers.isEmpty()) {
				json.put("code", -1);
				json.put("msg", "短信验证码不能为空");
				return json.toString();
			}
			
			Object obj = Cache.get(bankUsers.get(0).mobile + BusiType.CHARGE.value);
			
			if(obj == null) {
				json.put("code", -1);
				json.put("msg", "短信验证码失效");
				return json.toString();
			}
			smsSeq = obj.toString();
		} else if(code == TradeType.B2C.code) {
			// 网银充值
			bankId = parameters.get("bankId");
			
			Bank bank = Bank.getBank(bankId);
			if(bank == null) {
				json.put("code", -1);
				json.put("msg", "银行代号错误");
				return json.toString();
			}
			
		} else {
			json.put("code", -1);
			json.put("msg", "充值类型错误");
			return json.toString();
		}
			
		result = accountAppService.directRecharge(userId, Double.parseDouble(amount), tradeType, smsCode, smsSeq, bankId, Client.getEnum(Convert.strToInt(deviceType, -99)));

		if (result.code < 0) {
			LoggerUtil.info(true, result.msg);
			json.put("code", result.code);
			json.put("msg", result.msg);

			return json.toString();
		}

		json.put("code", result.code);
		json.put("msg", result.msg);

		if (ConfConst.IS_TRUST) {

			json.put("html", result.obj.toString());
		}

		return json.toString();
	}
	
	/***
	 * 充值记录接口（OPT=212）
	 * @param parameters
	 * @return
	 * @throws Exception
	 *
	 * @author luzhiwei
	 * @createDate 2016-3-31
	 */
	public static String pageOfRecharge (Map<String, String> parameters) throws Exception{
		JSONObject json = new JSONObject();
		
		String signId = parameters.get("userId");
		String currentPageStr = parameters.get("currPage");
		String pageNumStr = parameters.get("pageSize");
	
		ResultInfo result = Security.decodeSign(signId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
			 return json.toString();
		}

		if(!StrUtil.isNumeric(currentPageStr)||!StrUtil.isNumeric(pageNumStr)){
			 json.put("code",-1);
			 json.put("msg", "分页参数不正确");
			 
			 return json.toString();
		}
		
		int currPage = Convert.strToInt(currentPageStr, 1);
		int pageSize = Convert.strToInt(pageNumStr, 10);
		
		if (currPage < 1) {
			currPage = 1;
		}
		if (pageSize < 1) {
			pageSize = 10;
		}
		
		
		long userId = Long.parseLong(result.obj.toString());

		return accountAppService.pageOfRechargeRecord(userId, currPage, pageSize);
	}
}
