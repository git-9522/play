package controllers.app.expGold;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import common.constants.ConfConst;
import common.constants.Constants;
import common.enums.ServiceType;
import common.utils.Factory;
import common.utils.JPAUtil;
import common.utils.OrderNoUtil;
import common.utils.ResultInfo;
import common.utils.Security;
import hf.HfPaymentCallBackService;
import models.common.entity.t_exp_gold_account_user;
import models.common.entity.t_experience_gold;
import models.common.entity.t_user;
import models.common.entity.t_user_fund;
import models.common.entity.t_deal_platform.DealRemark;
import models.common.entity.t_deal_user.OperationType;
import models.core.entity.t_invest;
import net.sf.json.JSONObject;
import payment.impl.PaymentProxy;
import services.common.DealPlatformService;
import services.common.DealUserService;
import services.common.ExpGoldAccountUserService;
import services.common.ExpGoldShowRecordService;
import services.common.ExpGoldUserRecordService;
import services.common.ExperienceGoldService;
import services.common.UserFundService;
import services.common.UserService;
import services.core.InvestService;

public class ExpGoldAction {
	
    protected static UserService userService = Factory.getService(UserService.class);
	
	protected static ExperienceGoldService experienceGoldService = Factory.getService(ExperienceGoldService.class);
	
	protected static ExpGoldAccountUserService expGoldAccountUserService = Factory.getService(ExpGoldAccountUserService.class);
	
	protected static ExpGoldShowRecordService expGoldShowRecordService = Factory.getService(ExpGoldShowRecordService.class);
	
	protected static ExpGoldUserRecordService expGoldUserRecordService = Factory.getService(ExpGoldUserRecordService.class);

	protected static InvestService investService = Factory.getService(InvestService.class);

	protected static UserFundService userFundService = Factory.getService(UserFundService.class);

	protected static DealUserService dealUserService = Factory.getService(DealUserService.class);
	protected static DealPlatformService dealPlatformService = Factory.getService(DealPlatformService.class);
	
	
	/**
	 * APP-查询体验金账户信息
	 * @description 
	 *
	 * @author yuechuanyang
	 * @createDate 2017年10月17日
	 */
	public static String  queryAppExpGoldAccountUserByUserId(Map<String, String> parameters){
		String userIdSign = parameters.get("userId");
		JSONObject json = new JSONObject();
		
		ResultInfo userIdSignDecode = Security.decodeSign(userIdSign, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (userIdSignDecode.code < 1) {
			json.put("code", ResultInfo.LOGIN_TIME_OUT);
			json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			
			return json.toString();
		}
		long userId = Long.parseLong(userIdSignDecode.obj.toString());
		t_exp_gold_account_user exp = expGoldAccountUserService.queryAllExpGoldAccountUserByUserId(userId);
		ResultInfo result = new ResultInfo();
		if(exp == null){
			result.code = -2;
			result.msg = "用户未领取过体验金";
		}else{
			result.code = 1;
			result.msg = "查询成功";
			result.obj = exp;
		}
		JSONObject jo = JSONObject.fromObject(result);
		return jo.toString();
	}
	
	
	/**
	 * 领取体验金
	 * @description 
	 *
	 * @author yuechuanyang
	 * @createDate 2017年10月17日
	 */
	public static String appReceiveExpGold(Map<String, String> parameters){
		String userIdSign = parameters.get("userId");
		JSONObject json = new JSONObject();
		ResultInfo userIdSignDecode = Security.decodeSign(userIdSign, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (userIdSignDecode.code < 1) {
			json.put("code", ResultInfo.LOGIN_TIME_OUT);
			json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			
			return json.toString();
		}
		long userId = Long.parseLong(userIdSignDecode.obj.toString());
		int client = Integer.valueOf(parameters.get("client"));
		t_experience_gold exp = experienceGoldService.queryExperienceGold();
		double amount = exp.getAmount();
		long exp_gold_id = exp.getId();
		double invest_amount = exp.getInvest_amount();
		double seven_day_rate = exp.getSeven_day_rate();
		ResultInfo result = new ResultInfo();
		//根据id查询用户信息
		t_user user = userService.findUserById(userId);
		if(user == null){
			result.code = -2;
			result.msg = "不存在该用户！";
			return JSONObject.fromObject(result).toString();
		}
		//判断是否开户7天内
		long time = new Date().getTime() - user.time.getTime();
		if(time > 7*24*60*60*1000){
			result.code = -2;
			result.msg = "您的账户开户超过7天，不属于新手账户！";
			return JSONObject.fromObject(result).toString();
		}
		//查询领取次数
		long receiveTimes = expGoldUserRecordService.getCountOfExpGoldUserRecord(userId);
		if(receiveTimes > 0){
			result.code = -2;
			result.msg = "您已经领取过体验金了！";
			return JSONObject.fromObject(result).toString();
		}
		//判断用户是否投资过2次(查询投资次数)
		int investCount =investService.countInvestOfUser(userId,true);
		Date create_time = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(create_time);
		cal.add(Calendar.DATE, 15);
		Date end_time = cal.getTime();
		if(investCount >= 2){//查过两次投资直接按照第二次投资时的金额的1/1000 + 体验金转入账户
			//查询第二次投资金额
			List<t_invest> list = investService.queryInvestRecordByUserId(userId);
			double secInvestAmount = list.get(1).amount;
			//转账
			double transferAccountsAmount = amount + secInvestAmount*0.001;
			String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.TRANSFER); 
			ResultInfo res = PaymentProxy.getInstance().transfer(client, serviceOrderNo, userId, transferAccountsAmount);
			if(res.code == 1){
				//增加账户可用余额
				userFundService.userFundAdd(userId, transferAccountsAmount);
				//插入领取记录
				expGoldUserRecordService.saveExpGoldUserRecord(userId, exp_gold_id, create_time, end_time, "", amount);
				//插入体验金账户（状态为不可用）
				expGoldAccountUserService.saveExpGoldAccountUser(userId, amount, create_time, end_time, 1, "",invest_amount,seven_day_rate);
				//插入体验金转现记录
				expGoldShowRecordService.saveExpGoldShowRecord(userId, transferAccountsAmount, create_time, "");
				// 处理平台记录
				dealPlatformService.addPlatformDeal(userId, transferAccountsAmount, DealRemark.EXPERIENCE_TRANSFER,null);
				// 处理个人记录
				t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
				userFund.balance = userFund.balance + transferAccountsAmount;
				userFundService.update(userFund);
				userFundService.userFundSignUpdate(userId);
				// 更新用户签名
				dealUserService.addDealUserInfo(serviceOrderNo, userId, transferAccountsAmount, userFund.balance,
						userFund.freeze, OperationType.EXPERIENCE_TRANSFER, null);
				result.code = 1;
				result.msg = "体验金领取成功，并直接转入账户余额！";
				return JSONObject.fromObject(result).toString();
			}else{
				result.code = -1;
				result.msg = "转账失败！";
				return JSONObject.fromObject(result).toString();
			}
//			result= hfPaymentCallBackService.ExpGoldSend(userId, secInvestAmount,client);
		}else{//没有2次投资，存入体验金账户，如有复投，投资时的金额的1/1000 + 体验金转入账户
			//插入领取记录
			expGoldUserRecordService.saveExpGoldUserRecord(userId, exp_gold_id, create_time, end_time, "", amount);
			//插入体验金账户
			expGoldAccountUserService.saveExpGoldAccountUser(userId, amount, create_time, end_time, 0, "",invest_amount,seven_day_rate);
			result.code = 1;
			result.msg = "体验金领取成功，并存入体验金账户，复投时可转入账户！";
			
		}
		return JSONObject.fromObject(result).toString();
	}
	
	
	
	/**
	 * 体验金转现
	 * @description 
	 * @author yuechuanyang
	 * @createDate 2017年10月17日
	 */
	public static String showExpGold(Map<String, String> parameters){
		double secInvestAmount = Double.valueOf(parameters.get("secInvestAmount"));
		int client = Integer.valueOf(parameters.get("client"));
		String userIdSign = parameters.get("userId");
		JSONObject json = new JSONObject();
		ResultInfo userIdSignDecode = Security.decodeSign(userIdSign, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (userIdSignDecode.code < 1) {
			json.put("code", ResultInfo.LOGIN_TIME_OUT);
			json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			
			return json.toString();
		}
		long userId = Long.parseLong(userIdSignDecode.obj.toString());
		ResultInfo result = new ResultInfo();
		//查询投资次数
		int investCount = investService.countInvestOfUser(userId);
		if(investCount == 1){
			//查询用户体验金账户信息（条件为可用）
			t_exp_gold_account_user exp = expGoldAccountUserService.queryExpGoldAccountUserByUserId(userId);
			if(exp != null){
				double expGoldAmount = exp.getAmount();
				//转账
				double transferAccountsAmount = expGoldAmount + secInvestAmount*0.001;
				String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.TRANSFER); 
				ResultInfo res = PaymentProxy.getInstance().transfer(client, serviceOrderNo, userId, transferAccountsAmount);
				if(res.code == 1){
					//增加账户可用余额
					userFundService.userFundAdd(userId, transferAccountsAmount);
					//修改体验金账户（状态为不可用）
					expGoldAccountUserService.updateExpGoldAccountUserByUserId(userId, 1);
					//插入体验金转现记录
					expGoldShowRecordService.saveExpGoldShowRecord(userId, transferAccountsAmount, new Date(), "");
					result.code = 1;
					result.msg = "体验金成功转入账户余额！";
					return JSONObject.fromObject(result).toString();
				}else{
					result.code = -1;
					result.msg = "转账失败！";
					return JSONObject.fromObject(result).toString();
				}			
			}else{
				result.code = -1;
				result.msg = "体验金不可用或已过期";
				return JSONObject.fromObject(result).toString();
			}
		}else{
			result.code = -1;
			result.msg = "不是第二次投资";
			return JSONObject.fromObject(result).toString();
		}	
	}
}
