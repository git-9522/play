package controllers.wechat.front;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.shove.Convert;

import common.constants.ConfConst;
import common.constants.Constants;
import common.constants.SettingKey;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.ResultInfo;
import common.utils.Security;
import controllers.wechat.WechatBaseController;
import hf.HfPaymentCallBackService;
import hf.HfUtils;
import models.common.bean.CurrUser;
import models.common.bean.InvestLottery;
import models.common.entity.t_experience_gold;
import models.common.entity.t_invest_reward;
import models.common.entity.t_reversal_record;
import models.common.entity.t_reversal_reward;
import models.common.entity.t_reversal_rule;
import models.common.entity.t_user;
import models.common.entity.t_user_fund;
import models.core.entity.t_bid;
import models.core.entity.t_invest;
import services.common.ExpGoldAccountUserService;
import services.common.ExpGoldShowRecordService;
import services.common.ExpGoldUserRecordService;
import services.common.ExperienceGoldService;
import services.common.InvestLotteryService;
import services.common.InvestRewardService;
import services.common.ReversalRecordService;
import services.common.ReversalRewardService;
import services.common.ReversalRuleService;
import services.common.SettingService;
import services.common.UserFundService;
import services.common.UserService;
import services.core.BidService;
import services.core.InvestService;

public class ActivityCtrl extends WechatBaseController {
	
	protected static SettingService settingService = Factory.getService(SettingService.class);
	
	protected static InvestService investService = Factory.getService(InvestService.class);
	
	protected static InvestLotteryService investLotteryService = Factory.getService(InvestLotteryService.class);
	
	protected static InvestRewardService investRewardService = Factory.getService(InvestRewardService.class);
	
	protected static ReversalRecordService reversalRecordService = Factory.getService(ReversalRecordService.class);
	
	protected static ReversalRuleService reversalRuleService = Factory.getService(ReversalRuleService.class);

	protected static ReversalRewardService reversalRewardService = Factory.getService(ReversalRewardService.class);

	protected static BidService bidService = Factory.getService(BidService.class);

	protected static UserFundService userFundService = Factory.getService(UserFundService.class);
	
	//===============体验金
	protected static UserService userService = Factory.getService(UserService.class);
	
	protected static ExperienceGoldService experienceGoldService = Factory.getService(ExperienceGoldService.class);
	
	protected static ExpGoldAccountUserService expGoldAccountUserService = Factory.getService(ExpGoldAccountUserService.class);
	
	protected static ExpGoldShowRecordService expGoldShowRecordService = Factory.getService(ExpGoldShowRecordService.class);
	
	protected static ExpGoldUserRecordService expGoldUserRecordService = Factory.getService(ExpGoldUserRecordService.class);

	private static HfPaymentCallBackService hfPaymentCallBackService = Factory.getSimpleService(HfPaymentCallBackService.class);
	//========体验金结束
	public static void enterInvestLotteryPre(int type) {
		
		if(type < 0 || type > 1) {
			flash.error("设备类型异常");
			render();
		}
		
		long theUserId = 0;
		
		if(type == 0) {
			// app
			String userId = params.get("userId");
			ResultInfo userIdSignDecode = Security.decodeSign(userId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
			if (userIdSignDecode.code < 1) {
				flash.error("用户id异常");
				render();
			}
			theUserId = Long.parseLong(userIdSignDecode.obj.toString());
		} else {
			// wx
			try {
				theUserId = getCurrUser().id;
			} catch (Exception e) {
				flash.error("未登录，请去登录");
				LoginAndRegisteCtrl.loginPre();
			}
		}
		
		if(theUserId <= 0) {
			flash.error("用户不存在");
			render();
		}
		
		int lottery_is_use = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.LOTTERY_IS_USE), 0);
		
		if(lottery_is_use == 0) {
			flash.error("活动未开启");
			render();
		}
		
		String lottery_start_time = settingService.findSettingValueByKey(SettingKey.LOTTERY_START_TIME);
		
		String lottery_end_time = settingService.findSettingValueByKey(SettingKey.LOTTERY_END_TIME);
		
		double lottery_invest = Convert.strToDouble(settingService.findSettingValueByKey(SettingKey.LOTTERY_INVEST), 0);
		
		if(lottery_invest <= 0) {
			flash.error("活动未开启");
			render();
		}

		Date startDate = DateUtil.strToDate(lottery_start_time, "MM/dd/yyyy");
		
		Date endDate = DateUtil.strToDate(lottery_end_time, "MM/dd/yyyy");
		
		if(!DateUtil.isBetweenDate(startDate, endDate)) {
			flash.error("活动未开启");
			render();
		}
		
		// 在指定日期内的投标金额
		double sum = investService.calulateInvestMoneyInDates(theUserId, startDate, endDate);
		
		// 可以抽奖次数
		int size = (int) (sum / lottery_invest);
		
		// 已抽次数
		int count = investLotteryService.calulateInvestLotteryInDates(theUserId, startDate, endDate);	

		int remains = size - count > 0 ? size - count : 0;
		
		List<t_invest_reward> rewards = investRewardService.findListByColumn("is_use = ?", true);
		
		List<InvestLottery> investLotteries = investLotteryService.queryLatestLotteries(10);
		
		render(remains, rewards, investLotteries);
	}
	
	public static void doLottery() {
		
		ResultInfo result = new ResultInfo();
		
		int type = Integer.parseInt(params.get("type"));
		
		if(type < 0 || type > 1) {
			result.code = -1;
			result.msg = "设备类型异常";
			renderJSON(result);
		}
		
		long theUserId = 0;
		
		if(type == 0) {
			// app
			String userId = params.get("userId");
			ResultInfo userIdSignDecode = Security.decodeSign(userId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
			if (userIdSignDecode.code < 1) {
				result.code = -1;
				result.msg = "用户id异常";
				renderJSON(result);
			}
			theUserId = Long.parseLong(userIdSignDecode.obj.toString());
		} else {
			// wx
			try {
				theUserId = getCurrUser().id;
			} catch (Exception e) {
				result.code = -999;
				result.msg = "未登录，请去登录";
				renderJSON(result);
			}
		}
		
		if(theUserId == 0) {
			result.code = -1;
			result.msg = "用户id非法";
			renderJSON(result);
		}
		
		int lottery_is_use = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.LOTTERY_IS_USE), 0);
		
		if(lottery_is_use == 0) {
			result.code = -1;
			result.msg = "活动未开启";
			renderJSON(result);
		}
		
		String lottery_start_time = settingService.findSettingValueByKey(SettingKey.LOTTERY_START_TIME);
		
		String lottery_end_time = settingService.findSettingValueByKey(SettingKey.LOTTERY_END_TIME);
		
		double lottery_invest = Convert.strToDouble(settingService.findSettingValueByKey(SettingKey.LOTTERY_INVEST), 0);

		if(lottery_invest <= 0) {
			result.code = -1;
			result.msg = "活动未开启";
			renderJSON(result);
		}
		
		Date startDate = DateUtil.strToDate(lottery_start_time, "MM/dd/yyyy");
		
		Date endDate = DateUtil.strToDate(lottery_end_time, "MM/dd/yyyy");
		
		if(!DateUtil.isBetweenDate(startDate, endDate)) {
			result.code = -1;
			result.msg = "活动未开启";
			renderJSON(result);
		}
		
		// 在指定日期内的投标金额
		double sum = investService.calulateInvestMoneyInDates(theUserId, startDate, endDate);
		
		// 可以抽奖次数
		int size = (int) (sum / lottery_invest);
		
		// 已抽次数
		int count = investLotteryService.calulateInvestLotteryInDates(theUserId, startDate, endDate);	

		int remains = size - count > 0 ? size - count : 0;
		
		if(remains <= 0) {
			result.code = -1;
			result.msg = "可抽奖次数不足";
			renderJSON(result);
		}
		
		result = investRewardService.doLottery(theUserId);
		
		renderJSON(result);
	}
	
	public static void getRerversalInfo() {
		
		ResultInfo result = new ResultInfo();
		
		int reversal_is_use = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.REVERSAL_IS_USE), 0);
		
		if(reversal_is_use == 0) {
			result.code = -1;
			result.msg = "活动未开启";
			renderJSON(result);
		}
		
		String reversal_start_time = settingService.findSettingValueByKey(SettingKey.REVERSAL_START_TIME);
		
		String reversal_end_time = settingService.findSettingValueByKey(SettingKey.REVERSAL_END_TIME);

		Date startDate = DateUtil.strToDate(reversal_start_time, "MM/dd/yyyy");
		
		Date endDate = DateUtil.strToDate(reversal_end_time, "MM/dd/yyyy");
		
		if(!DateUtil.isBetweenDate(startDate, endDate)) {
			result.code = -1;
			result.msg = "活动未开启";
			renderJSON(result);
		}
		
		double reversal_invest = Convert.strToDouble(settingService.findSettingValueByKey(SettingKey.REVERSAL_INVEST), 0);
		
		if(reversal_invest <= 0 ) {
			result.code = -1;
			result.msg = "活动参数错误";
			renderJSON(result);
		}
		
		String reversal_rule = settingService.findSettingValueByKey(SettingKey.REVERSAL_RULE);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("reversal_is_use", reversal_is_use);
		map.put("reversal_start_time", DateUtil.dateToString(startDate, "yyyy-MM-dd"));
		map.put("reversal_end_time", DateUtil.dateToString(endDate, "yyyy-MM-dd"));
		map.put("reversal_invest", reversal_invest);
		map.put("reversal_rule", reversal_rule);
		
		result.code = 1;
		result.msg = "获取活动信息成功";
		result.obj = map;
		renderJSON(result);
	}

	public static void getRerversalInvest(int type) {
		
		ResultInfo result = new ResultInfo();
		
		if(type < 0 || type > 1) {
			result.code = -1;
			result.msg = "设备类型异常";
			renderJSON(result);
		}
		
		long userId = 0L;
		
		if(type == 0) {
			// APP
			String userIdObj = params.get("userId");
			result = Security.decodeSign(userIdObj, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
			if (result.code < 1) {
				result.code = -1;
				result.msg = "用户id异常";
				renderJSON(result);
			}
			userId = Long.parseLong(result.obj.toString());
		} else {
			// WECHAT OR WEB
			CurrUser currUser = getCurrUser();
			
			if(currUser == null) {
				result.code = ResultInfo.NOT_LOGIN;
				result.msg = "未登录请登录";
				renderJSON(result);
			}
			
			userId = currUser.id;
		}
		
		int reversal_is_use = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.REVERSAL_IS_USE), 0);
		
		if(reversal_is_use == 0) {
			result.code = -1;
			result.msg = "活动未开启";
			renderJSON(result);
		}
		
		String reversal_start_time = settingService.findSettingValueByKey(SettingKey.REVERSAL_START_TIME);
		
		String reversal_end_time = settingService.findSettingValueByKey(SettingKey.REVERSAL_END_TIME);

		Date startDate = DateUtil.strToDate(reversal_start_time, "MM/dd/yyyy");
		
		Date endDate = DateUtil.strToDate(reversal_end_time, "MM/dd/yyyy");
		
		if(!DateUtil.isBetweenDate(startDate, endDate)) {
			result.code = -1;
			result.msg = "活动未开启";
			renderJSON(result);
		}

		double reversal_invest = Convert.strToDouble(settingService.findSettingValueByKey(SettingKey.REVERSAL_INVEST), 0);
		
		if(reversal_invest <= 0 ) {
			result.code = -1;
			result.msg = "活动参数错误";
			renderJSON(result);
		}
		
		List<Map<String, Object>> list = reversalRecordService.findInvestInfo(userId, 
				startDate, endDate, reversal_invest);
		
		result.code = 1;
		result.msg = "获取投资信息成功";
		result.obj = list;
		renderJSON(result);
	}
	
	public static void doRerversalLottery(int type, long investId) {
		ResultInfo result = new ResultInfo();
		
		if(type < 0 || type > 1) {
			result.code = -1;
			result.msg = "设备类型异常";
			renderJSON(result);
		}
		
		long userId = 0L;
		
		if(type == 0) {
			// APP
			String userIdObj = params.get("userId");
			result = Security.decodeSign(userIdObj, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
			if (result.code < 1) {
				result.code = -1;
				result.msg = "用户id异常";
				renderJSON(result);
			}
			userId = Long.parseLong(result.obj.toString());
		} else {
			// WECHAT OR WEB
			CurrUser currUser = getCurrUser();
			
			if(currUser == null) {
				result.code = ResultInfo.NOT_LOGIN;
				result.msg = "未登录请登录";
				renderJSON(result);
			}
			
			userId = currUser.id;
		}
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		
		if(userFund == null) {
			result.code = -1;
			result.msg = "无法找到账户信息";
			renderJSON(result);
		}
		
		int reversal_is_use = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.REVERSAL_IS_USE), 0);
		
		if(reversal_is_use == 0) {
			result.code = -1;
			result.msg = "活动未开启";
			renderJSON(result);
		}
		
		String reversal_start_time = settingService.findSettingValueByKey(SettingKey.REVERSAL_START_TIME);
		
		String reversal_end_time = settingService.findSettingValueByKey(SettingKey.REVERSAL_END_TIME);

		Date startDate = DateUtil.strToDate(reversal_start_time, "MM/dd/yyyy");
		
		Date endDate = DateUtil.strToDate(reversal_end_time, "MM/dd/yyyy");
		
		if(!DateUtil.isBetweenDate(startDate, endDate)) {
			result.code = -1;
			result.msg = "活动未开启";
			renderJSON(result);
		}
		
		double reversal_invest = Convert.strToDouble(settingService.findSettingValueByKey(SettingKey.REVERSAL_INVEST), 0);
		
		if(reversal_invest <= 0 ) {
			result.code = -1;
			result.msg = "活动参数错误";
			renderJSON(result);
		}
		
		if(investId == 0) {
			result.code = -1;
			result.msg = "投资信息id非法";
			renderJSON(result);
		}
		
		t_invest invest = investService.findByID(investId);
		
		if(invest == null) {
			result.code = -1;
			result.msg = "无法获取投资信息";
			renderJSON(result);
		}
		
		t_reversal_record record = reversalRecordService.findByInvestId(investId);
		
		if(record != null) {
			result.code = -1;
			result.msg = "该笔投资已经翻过牌";
			renderJSON(result);
		}
		
		// 根据投资金额查询以及标的期限查询概率数组
		List<t_reversal_rule> rules = reversalRuleService.findByAmout(invest.amount);
		
		if(rules == null || rules.isEmpty()) {
			result.code = -1;
			result.msg = "无法翻牌，找不到翻牌数据";
			renderJSON(result);
		}

		int index = -1;
		
		try {
			Double[] pros = new Double[rules.size()];
			int  sum = 0;//概率数组的总概率精度 
			
			for(int i = 0; i < pros.length; i++) {
				pros[i] = rules.get(i).probability * 100;
				sum += pros[i];
			}
			
			int randomNum = new Random().nextInt(sum) + 1;//随机生成1到sum的整数
			
			double sumOne = 0;//概率转换为概率总和上的比例区间，随机数在区间内为中奖，
			
			for(int i = 0; i < pros.length; i++){//概率数组循环 
				//sumTwo区间结束
				double sumTwo = sumOne + pros[i];
				
				if(sumOne < randomNum && randomNum <= sumTwo){//中奖
					index = i;
					break;
				}else{
					sumOne = sumTwo;
				}
			}
		} catch (Exception e) {
			result.code = -1;
			result.msg = "无法翻牌，出现异常";
			renderJSON(result);
		}
		
		if(index < 0 || index >= rules.size()) {
			result.code = -1;
			result.msg = "无法翻牌，出现异常";
			renderJSON(result);
		}
		
		t_reversal_rule resultRule = rules.get(index);
		
		// 获取标的信息
		t_bid bid = bidService.findByID(invest.bid_id);
		if(bid == null) {
			result.code = -1;
			result.msg = "无法获取标的信息";
			renderJSON(result);
		}
		
		// 获取翻牌奖励
		t_reversal_reward reward = reversalRewardService.findByLevelAndPeriod(resultRule.level, bid.period);
		if(reward == null) {
			result.code = -1;
			result.msg = "无法获取翻牌奖励信息";
			renderJSON(result);
		}
		
		double awardMoney = invest.amount * reward.scale / 100;
		
		// 生成翻牌记录
		t_reversal_record newRecord = new t_reversal_record();
		newRecord.user_id = userId;
		newRecord.user_name = userFund.name;
		newRecord.invest_id = investId;
		newRecord.invest_money = invest.amount;
		newRecord.period = bid.period;
		newRecord.scale = reward.scale;		
		newRecord.award_money = Double.parseDouble(HfUtils.formatAmount(awardMoney));
		
		result = reversalRecordService.insert(newRecord);
		
		if(result.code != 1) {
			result.msg = "添加翻牌记录失败";
			renderJSON(result);
		} else {
			result.msg = "翻牌成功";
			result.obj = newRecord.award_money;
			renderJSON(result);
		}
	}
	
	public static void enterRerversalLotteryPre(int type) {
		
		boolean flag = false;
		
		if(type < 0 || type > 1) {
			flash.error("设备类型异常");
			render(flag);
		}
		
		long theUserId = 0;
		
		if(type == 0) {
			// app
			String userId = params.get("userId");
			ResultInfo userIdSignDecode = Security.decodeSign(userId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
			if (userIdSignDecode.code < 1) {
				flash.error("用户id异常");
				render(flag);
			}
			theUserId = Long.parseLong(userIdSignDecode.obj.toString());
		} else {
			// wx
			try {
				theUserId = getCurrUser().id;
			} catch (Exception e) {
				flash.error("未登录，请去登录");
				LoginAndRegisteCtrl.loginPre();
			}
		}
		
		if(theUserId <= 0) {
			flash.error("用户不存在");
			render(flag);
		}
		
		flag = true;
		render(flag);
	}

	/**
	 * 体验金入口
	 */
	public static void expGoldPre(){
		render();
	}
	/**
	 * 领取体验金
	 * @description 
	 *
	 * @author yuechuanyang
	 * @createDate 2017年10月17日
	 */
	public static void receiveExpGoldPre(int client){
		t_experience_gold exp = experienceGoldService.queryExperienceGold();
		double amount = exp.getAmount();
		long exp_gold_id = exp.getId();
		double invest_amount = exp.getInvest_amount();
		double seven_day_rate = exp.getSeven_day_rate();
		ResultInfo result = new ResultInfo();
		if(getCurrUser() == null){
			result.code = -1;
			result.msg = "用户未登录，请先登录！";
			renderJSON(result);
		}
		long userId = getCurrUser().id;
		//根据id查询用户信息
		t_user user = userService.findUserById(userId);
		if(user == null){
			result.code = -2;
			result.msg = "不存在该用户！";
			renderJSON(result);
		}
		//判断是否开户7天内
		long time = new Date().getTime() - user.time.getTime();
		if(time > 7*24*60*60*1000){
			result.code = -3;
			result.msg = "您的账户开户超过7天，不属于新手账户！";
			renderJSON(result);
		}
		//查询领取次数
		long receiveTimes = expGoldUserRecordService.getCountOfExpGoldUserRecord(userId);
		if(receiveTimes > 0){
			result.code = -4;
			result.msg = "您已经领取过体验金了！";
			renderJSON(result);
		}
		//判断用户是否投资过2次(查询投资次数)
		int investCount = investService.countInvestOfUser(userId,true);
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
			result=hfPaymentCallBackService.ExpGoldSend(userId, secInvestAmount,client);
		}else{//没有2次投资，存入体验金账户，如有复投，投资时的金额的1/1000 + 体验金转入账户
			//插入领取记录
			expGoldUserRecordService.saveExpGoldUserRecord(userId, exp_gold_id, create_time, end_time, "", amount);
			//插入体验金账户
			expGoldAccountUserService.saveExpGoldAccountUser(userId, amount, create_time, end_time, 0, "",invest_amount,seven_day_rate);
			result.code = 1;
			result.msg = "体验金领取成功，并存入体验金账户，复投时可转入账户！";
		}
		renderJSON(result);
	}

	
	/**
	 * 查询体验金
	 * @description 
	 *
	 * @author yuechuanyang
	 * @createDate 2017年10月17日
	 */
	public static void getExpGold(){
		t_experience_gold exp = experienceGoldService.queryExperienceGold();
		ResultInfo result = new ResultInfo();
		result.code = 1;
		result.msg = "查询成功！";
		result.obj = exp;
		renderJSON(result);
	}
	
}