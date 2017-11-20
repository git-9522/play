package controllers.app;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.shove.Convert;

import common.constants.ConfConst;
import common.constants.Constants;
import common.enums.BusiType;
import common.enums.CashType;
import common.enums.Client;
import common.enums.DeviceType;
import common.enums.ServiceType;
import common.utils.Factory;
import common.utils.OrderNoUtil;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.StrUtil;
import hf.HfPaymentService;
import net.sf.json.JSONObject;
import payment.impl.PaymentProxy;
import play.cache.Cache;
import services.common.UserInfoService;

public class CommonAction {
	
	private static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	
	private static HfPaymentService hfPaymentService = Factory.getSimpleService(HfPaymentService.class);

	/**
	 *  HF发送短信验证码(OPT=701)
	 */
	public static String sendSmsCode(Map<String, String> parameters) {

		JSONObject json = new JSONObject();

		String signId = parameters.get("userId");
		String deviceType = parameters.get("deviceType");//设备类型

		ResultInfo result = Security.decodeSign(signId, Constants.USER_ID_SIGN, Constants.VALID_TIME,
				ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			json.put("code", ResultInfo.LOGIN_TIME_OUT);
			json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			return json.toString();
		}
		
		if(DeviceType.getEnum(Convert.strToInt(deviceType, -99)) == null){
			 json.put("code", -1);
			 json.put("msg", "请使用移动客户端操作");
			 
			 return json.toString();
		}
		
		long userId = Long.parseLong(result.obj.toString());

		int type = 0;

		try {
			type = Integer.parseInt(parameters.get("type"));
		} catch (Exception e) {
			json.put("code", -1);
			json.put("msg", "汇付短信验证码错误");
			return json.toString();
		}

		BusiType busiType = BusiType.getTypeByCode(type);

		if (busiType == null) {
			json.put("code", -1);
			json.put("msg", "发送短信验证码类型错误");
			return json.toString();
		}

		String cardId = parameters.get("cardId");
		String mobile = parameters.get("mobile");

		if (!StrUtil.isMobileNum(mobile)) {
			json.put("code", -1);
			json.put("msg", "手机号码格式错误");
			return json.toString();
		}

		if (busiType.code != 1 && !StrUtil.isBankAccount(cardId)) {
			json.put("code", -1);
			json.put("msg", "银行卡号格式错误");
			return json.toString();
		}
		
		String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.SENDSMSCODE);
		
		result = PaymentProxy.getInstance().sendSmsCode(Convert.strToInt(deviceType, -99), serviceOrderNo, userId, cardId, busiType, mobile);

		if (result.code != 1) {
			json.put("code", -1);
			json.put("msg", result.msg);
			return json.toString();
		} else {
			// 存入缓存
			Cache.set(mobile + busiType.value, result.obj, Constants.CACHE_TIME_MINUS_2);
			json.put("code", result.code);
			json.put("msg", result.msg);
			return json.toString();
		}
		
	}
	
	/**
	 * 校验汇付用户号(OPT=702)
	 */
	public static String checkHfName(Map<String, String> parameters) {
		
		JSONObject json = new JSONObject();

		String hfName = parameters.get("hfName");
		
		ResultInfo result = userInfoService.checkHfname(hfName);
		
		json.put("code", result.code);
		json.put("msg", result.msg);
		return json.toString();
	}
	
	/**
	 * 获取提现手续费 705
	 */
	public static String queryServFee(Map<String, String> parameters) {
		JSONObject json = new JSONObject();

		String amount = parameters.get("amount");
		String cashType = parameters.get("cashType");
		
		CashType type = CashType.getCashTypeByCode(cashType);
		
		if(type == null) {
			json.put("code", -1);
			json.put("msg", "提现类型错误");
			return json.toString();
		}
		
		double transAmt = 0;
		
		try {
			transAmt = Double.parseDouble(amount);
		} catch (Exception e) {
			json.put("code", -1);
			json.put("msg", "提现金额错误");
			return json.toString();
		}
		
		double serverFee = hfPaymentService.queryServFee(transAmt, cashType);
		
		json.put("code", 1);
		json.put("msg", "success");
		json.put("obj", serverFee);
		return json.toString();
		
	}
	
}