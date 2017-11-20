package controllers.front;

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
import controllers.common.FrontBaseController;
import hf.HfUtils;
import models.common.bean.CurrUser;
import models.common.bean.InvestLottery;
import models.common.entity.t_invest_reward;
import models.common.entity.t_moon_cake_lottery_record;
import models.common.entity.t_reversal_record;
import models.common.entity.t_reversal_reward;
import models.common.entity.t_reversal_rule;
import models.common.entity.t_user_fund;
import models.core.entity.t_bid;
import models.core.entity.t_invest;
import models.core.entity.t_red_packet;
import models.core.entity.t_red_packet.RedpacketType;
import play.Logger;
import services.common.InvestLotteryService;
import services.common.InvestRewardService;
import services.common.MoonCakeLotteryRecordService;
import services.common.ReversalRecordService;
import services.common.ReversalRewardService;
import services.common.ReversalRuleService;
import services.common.SettingService;
import services.common.UserFilterService;
import services.common.UserFundService;
import services.core.BidService;
import services.core.InvestService;
import services.core.RedpacketService;
import services.core.RedpacketUserService;

public class ActivityCtrl extends FrontBaseController {

	protected static RedpacketService redpacketService = Factory.getService(RedpacketService.class);

	protected static SettingService settingService = Factory.getService(SettingService.class);

	protected static RedpacketUserService redpacketUserService = Factory.getService(RedpacketUserService.class);

	protected static InvestService investService = Factory.getService(InvestService.class);

	protected static InvestLotteryService investLotteryService = Factory.getService(InvestLotteryService.class);

	protected static InvestRewardService investRewardService = Factory.getService(InvestRewardService.class);

	protected static ReversalRecordService reversalRecordService = Factory.getService(ReversalRecordService.class);

	protected static ReversalRuleService reversalRuleService = Factory.getService(ReversalRuleService.class);

	protected static ReversalRewardService reversalRewardService = Factory.getService(ReversalRewardService.class);

	protected static BidService bidService = Factory.getService(BidService.class);

	protected static UserFundService userFundService = Factory.getService(UserFundService.class);

	protected static MoonCakeLotteryRecordService moonCakeLotteryRecordService = Factory
			.getService(MoonCakeLotteryRecordService.class);

//	protected static AwardNumberRecordService awardnumberrecordservice = Factory
//			.getService(AwardNumberRecordService.class);

	public static void enterRedPacketPagePre() {

		Map<String, Object> map = new HashMap<String, Object>();

		int rp_is_use = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.RP_IS_USE), 0);

		if (rp_is_use == 0) {
			map.put("flag", false);
			renderArgs.put("map", map);
			render();
		}

		String rp_start_time = settingService.findSettingValueByKey(SettingKey.RP_START_TIME);

		String rp_end_time = settingService.findSettingValueByKey(SettingKey.RP_END_TIME);

		Date startDate = DateUtil.strToDate(rp_start_time, "MM/dd/yyyy");

		Date endDate = DateUtil.strToDate(rp_end_time, "MM/dd/yyyy");

		if (!DateUtil.isBetweenDate(startDate, endDate)) {
			map.put("flag", false);
			renderArgs.put("map", map);
			render();
		}

		String rp_daily_count = settingService.findSettingValueByKey(SettingKey.RP_DAILY_COUNT);

		String rp_rule = settingService.findSettingValueByKey(SettingKey.RP_RULE);

		List<t_red_packet> packets = redpacketService.findAllRedPacketRules(RedpacketType.ACTIVITY.code, true);

		map.put("flag", true);

		map.put("startDate", startDate);

		map.put("endDate", endDate);

		map.put("rp_daily_count", rp_daily_count);

		map.put("rp_rule", rp_rule);

		map.put("packets", packets);

		renderArgs.put("map", map);
		render();

	}

	public static void saveRedPacket(long id) {

		ResultInfo result = new ResultInfo();

		int rp_is_use = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.RP_IS_USE), 0);

		if (rp_is_use == 0) {
			result.code = -1;
			result.msg = "活动没有开启";
			renderJSON(result);
		}

		String rp_start_time = settingService.findSettingValueByKey(SettingKey.RP_START_TIME);

		String rp_end_time = settingService.findSettingValueByKey(SettingKey.RP_END_TIME);

		Date startDate = DateUtil.strToDate(rp_start_time, "MM/dd/yyyy");

		Date endDate = DateUtil.strToDate(rp_end_time, "MM/dd/yyyy");

		if (!DateUtil.isBetweenDate(startDate, endDate)) {
			result.code = -1;
			result.msg = "活动没有开启";
			renderJSON(result);
		}

		CurrUser currUser = getCurrUser();

		if (currUser == null) {
			result.code = ResultInfo.NOT_LOGIN;
			result.msg = "未登录请登录";
			renderJSON(result);
		}

		int defaultCount = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.RP_DAILY_COUNT), 0);

		int count = redpacketUserService.queryDailyGainRedPacketCount(currUser.id, RedpacketType.ACTIVITY.code);

		if (count >= defaultCount) {
			result.code = -1;
			result.msg = "今日领取剩余领取次数为0";
			renderJSON(result);
		}

		t_red_packet packet = redpacketService.findByID(id);

		if (packet == null || !packet.is_use) {
			result.code = -1;
			result.msg = "此红包已经下架了";
			renderJSON(result);
		}

		result = redpacketUserService.insertBypacket(packet, currUser.id, currUser.name);

		result.msg = "领取红包成功，今日剩余次数为 : " + (defaultCount - count - 1);

		renderJSON(result);

	}

	public static void enterInvestLotteryPre() {

		int lottery_is_use = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.LOTTERY_IS_USE), 0);

		if (lottery_is_use == 0) {
			flash.error("活动未开启");
			render();
		}

		String lottery_start_time = settingService.findSettingValueByKey(SettingKey.LOTTERY_START_TIME);

		String lottery_end_time = settingService.findSettingValueByKey(SettingKey.LOTTERY_END_TIME);

		double lottery_invest = Convert.strToDouble(settingService.findSettingValueByKey(SettingKey.LOTTERY_INVEST), 0);

		if (lottery_invest <= 0) {
			flash.error("活动未开启");
			render();
		}

		Date startDate = DateUtil.strToDate(lottery_start_time, "MM/dd/yyyy");

		Date endDate = DateUtil.strToDate(lottery_end_time, "MM/dd/yyyy");

		if (!DateUtil.isBetweenDate(startDate, endDate)) {
			flash.error("活动未开启");
			render();
		}

		CurrUser currUser = LoginAndRegisteCtrl.getCurrUser();

		if (currUser == null) {
			flash.error("用户未登录");
			LoginAndRegisteCtrl.loginPre();
		}

		// 在指定日期内的投标金额
		double sum = investService.calulateInvestMoneyInDates(currUser.id, startDate, endDate);

		// 可以抽奖次数
		int size = (int) (sum / lottery_invest);

		// 已抽次数
		int count = investLotteryService.calulateInvestLotteryInDates(currUser.id, startDate, endDate);

		int remains = size - count > 0 ? size - count : 0;

		List<t_invest_reward> rewards = investRewardService.findListByColumn("is_use = ?", true);

		List<InvestLottery> investLotteries = investLotteryService.queryLatestLotteries(10);

		render(remains, rewards, investLotteries);
	}

	public static void doLottery() {
		ResultInfo result = new ResultInfo();

		long userId = LoginAndRegisteCtrl.getCurrUser().id;

		int lottery_is_use = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.LOTTERY_IS_USE), 0);

		if (lottery_is_use == 0) {
			result.code = -1;
			result.msg = "未登录";
			renderJSON(result);
		}

		String lottery_start_time = settingService.findSettingValueByKey(SettingKey.LOTTERY_START_TIME);

		String lottery_end_time = settingService.findSettingValueByKey(SettingKey.LOTTERY_END_TIME);

		double lottery_invest = Convert.strToDouble(settingService.findSettingValueByKey(SettingKey.LOTTERY_INVEST), 0);

		if (lottery_invest <= 0) {
			result.code = -1;
			result.msg = "活动未开启";
			renderJSON(result);
		}

		Date startDate = DateUtil.strToDate(lottery_start_time, "MM/dd/yyyy");

		Date endDate = DateUtil.strToDate(lottery_end_time, "MM/dd/yyyy");

		if (!DateUtil.isBetweenDate(startDate, endDate)) {
			result.code = -1;
			result.msg = "活动未开启";
			renderJSON(result);
		}

		CurrUser currUser = LoginAndRegisteCtrl.getCurrUser();

		if (currUser == null) {
			result.code = ResultInfo.NOT_LOGIN;
			result.msg = "未登录请登录";
			renderJSON(result);
		}

		// 在指定日期内的投标金额
		double sum = investService.calulateInvestMoneyInDates(currUser.id, startDate, endDate);

		// 可以抽奖次数
		int size = (int) (sum / lottery_invest);

		// 已抽次数
		int count = investLotteryService.calulateInvestLotteryInDates(currUser.id, startDate, endDate);

		if ((size - count) <= 0) {
			result.code = -1;
			result.msg = "可抽奖次数不足";
			renderJSON(result);
		}

		result = investRewardService.doLottery(userId);

		renderJSON(result);
	}

	public static void getRerversalInfo() {

		ResultInfo result = new ResultInfo();

		int reversal_is_use = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.REVERSAL_IS_USE), 0);

		if (reversal_is_use == 0) {
			result.code = -1;
			result.msg = "活动未开启";
			renderJSON(result);
		}

		String reversal_start_time = settingService.findSettingValueByKey(SettingKey.REVERSAL_START_TIME);

		String reversal_end_time = settingService.findSettingValueByKey(SettingKey.REVERSAL_END_TIME);

		Date startDate = DateUtil.strToDate(reversal_start_time, "MM/dd/yyyy");

		Date endDate = DateUtil.strToDate(reversal_end_time, "MM/dd/yyyy");

		if (!DateUtil.isBetweenDate(startDate, endDate)) {
			result.code = -1;
			result.msg = "活动未开启";
			renderJSON(result);
		}

		double reversal_invest = Convert.strToDouble(settingService.findSettingValueByKey(SettingKey.REVERSAL_INVEST),
				0);

		if (reversal_invest <= 0) {
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

		if (type < 0 || type > 1) {
			result.code = -1;
			result.msg = "设备类型异常";
			renderJSON(result);
		}

		long userId = 0L;

		if (type == 0) {
			// APP
			String userIdObj = params.get("userId");
			result = Security.decodeSign(userIdObj, Constants.USER_ID_SIGN, Constants.VALID_TIME,
					ConfConst.ENCRYPTION_APP_KEY_DES);
			if (result.code < 1) {
				result.code = -1;
				result.msg = "用户id异常";
				renderJSON(result);
			}
			userId = Long.parseLong(result.obj.toString());
			System.out.println(userId);
		} else {
			// WECHAT OR WEB
			CurrUser currUser = getCurrUser();

			if (currUser == null) {
				result.code = ResultInfo.NOT_LOGIN;
				result.msg = "未登录请登录";
				renderJSON(result);
			}

			userId = currUser.id;
		}

		int reversal_is_use = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.REVERSAL_IS_USE), 0);

		if (reversal_is_use == 0) {
			result.code = -1;
			result.msg = "活动未开启";
			renderJSON(result);
		}

		String reversal_start_time = settingService.findSettingValueByKey(SettingKey.REVERSAL_START_TIME);

		String reversal_end_time = settingService.findSettingValueByKey(SettingKey.REVERSAL_END_TIME);

		Date startDate = DateUtil.strToDate(reversal_start_time, "MM/dd/yyyy");

		Date endDate = DateUtil.strToDate(reversal_end_time, "MM/dd/yyyy");

		if (!DateUtil.isBetweenDate(startDate, endDate)) {
			result.code = -1;
			result.msg = "活动未开启";
			renderJSON(result);
		}

		double reversal_invest = Convert.strToDouble(settingService.findSettingValueByKey(SettingKey.REVERSAL_INVEST),
				0);

		if (reversal_invest <= 0) {
			result.code = -1;
			result.msg = "活动参数错误";
			renderJSON(result);
		}

		List<Map<String, Object>> list = reversalRecordService.findInvestInfo(userId, startDate, endDate,
				reversal_invest);
		/**
		 * 筛选线下用户、标的周期，不享受线上翻牌子活动
		 */
		/*
		 * if (list != null && list.size() > 0) { boolean period1 =
		 * userFilterService.isOfflineUser(userId, 1); boolean period3 =
		 * userFilterService.isOfflineUser(userId, 3); boolean period6 =
		 * userFilterService.isOfflineUser(userId, 6); Iterator<Map<String,
		 * Object>> it = list.iterator(); while (it.hasNext()){ int period =
		 * Integer.parseInt(it.next().get("period").toString()); if (period1 &&
		 * period == 1) { it.remove(); } if (period3 && period == 3) {
		 * it.remove(); } if (period6 && period == 6) { it.remove(); } }
		 * 
		 * }
		 *//** 筛选线下用户、标的周期，不享受线上翻牌子活动完毕 */
		result.code = 1;
		result.msg = "获取投资信息成功";
		result.obj = list;
		renderJSON(result);
	}

	/** 翻牌 */
	public static void doRerversalLottery(int type, long investId) {
		ResultInfo result = new ResultInfo();

		if (type < 0 || type > 1) {
			result.code = -1;
			result.msg = "设备类型异常";
			renderJSON(result);
		}

		long userId = 0L;

		if (type == 0) {
			// APP
			String userIdObj = params.get("userId");
			result = Security.decodeSign(userIdObj, Constants.USER_ID_SIGN, Constants.VALID_TIME,
					ConfConst.ENCRYPTION_APP_KEY_DES);
			if (result.code < 1) {
				result.code = -1;
				result.msg = "用户id异常";
				renderJSON(result);
			}
			userId = Long.parseLong(result.obj.toString());
		} else {
			// WECHAT OR WEB
			CurrUser currUser = getCurrUser();

			if (currUser == null) {
				result.code = ResultInfo.NOT_LOGIN;
				result.msg = "未登录请登录";
				renderJSON(result);
			}

			userId = currUser.id;
		}

		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);

		if (userFund == null) {
			result.code = -1;
			result.msg = "无法找到账户信息";
			renderJSON(result);
		}

		int reversal_is_use = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.REVERSAL_IS_USE), 0);

		if (reversal_is_use == 0) {
			result.code = -1;
			result.msg = "活动未开启";
			renderJSON(result);
		}

		String reversal_start_time = settingService.findSettingValueByKey(SettingKey.REVERSAL_START_TIME);

		String reversal_end_time = settingService.findSettingValueByKey(SettingKey.REVERSAL_END_TIME);

		Date startDate = DateUtil.strToDate(reversal_start_time, "MM/dd/yyyy");

		Date endDate = DateUtil.strToDate(reversal_end_time, "MM/dd/yyyy");

		if (!DateUtil.isBetweenDate(startDate, endDate)) {
			result.code = -1;
			result.msg = "活动未开启";
			renderJSON(result);
		}

		double reversal_invest = Convert.strToDouble(settingService.findSettingValueByKey(SettingKey.REVERSAL_INVEST),
				0);

		if (reversal_invest <= 0) {
			result.code = -1;
			result.msg = "活动参数错误";
			renderJSON(result);
		}

		if (investId == 0) {
			result.code = -1;
			result.msg = "投资信息id非法";
			renderJSON(result);
		}

		t_invest invest = investService.findByID(investId);

		if (invest == null) {
			result.code = -1;
			result.msg = "无法获取投资信息";
			renderJSON(result);
		}

		t_reversal_record record = reversalRecordService.findByInvestId(investId);

		if (record != null) {
			result.code = -1;
			result.msg = "该笔投资已经翻过牌";
			renderJSON(result);
		}

		// 根据投资金额查询以及标的期限查询概率数组
		List<t_reversal_rule> rules = reversalRuleService.findByAmout(invest.amount);

		if (rules == null || rules.isEmpty()) {
			result.code = -1;
			result.msg = "无法翻牌，找不到翻牌数据";
			renderJSON(result);
		}

		int index = -1;

		try {
			Double[] pros = new Double[rules.size()];
			int sum = 0;// 概率数组的总概率精度

			for (int i = 0; i < pros.length; i++) {
				pros[i] = rules.get(i).probability * 100;
				sum += pros[i];
			}

			int randomNum = new Random().nextInt(sum) + 1;// 随机生成1到sum的整数

			double sumOne = 0;// 概率转换为概率总和上的比例区间，随机数在区间内为中奖，

			for (int i = 0; i < pros.length; i++) {// 概率数组循环
				// sumTwo区间结束
				double sumTwo = sumOne + pros[i];

				if (sumOne < randomNum && randomNum <= sumTwo) {// 中奖
					index = i;
					break;
				} else {
					sumOne = sumTwo;
				}
			}
		} catch (Exception e) {
			result.code = -1;
			result.msg = "无法翻牌，出现异常";
			renderJSON(result);
		}

		if (index < 0 || index >= rules.size()) {
			result.code = -1;
			result.msg = "无法翻牌，出现异常";
			renderJSON(result);
		}

		t_reversal_rule resultRule = rules.get(index);

		// 获取标的信息
		t_bid bid = bidService.findByID(invest.bid_id);
		if (bid == null) {
			result.code = -1;
			result.msg = "无法获取标的信息";
			renderJSON(result);
		}

		// 获取翻牌奖励
		t_reversal_reward reward = reversalRewardService.findByLevelAndPeriod(resultRule.level, bid.period);
		if (reward == null) {
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

		if (result.code != 1) {
			result.msg = "添加翻牌记录失败";
			renderJSON(result);
		} else {
			result.msg = "翻牌成功";
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("awardMoney", newRecord.award_money);
			map.put("index", index);
			result.obj = map;
			renderJSON(result);
		}
	}

	/**
	 * 为了app
	 */
	public static void enterRerversalLotteryPre(int type) {
		render();
	}

	public static void qqh(int type) {

		ResultInfo result = new ResultInfo();

		if (type < 0 || type > 1) {
			result.code = -1;
			result.msg = "设备类型异常";
			renderJSON(result);
		}

		long sum = investService.calulateInvestMoneyInDates("2017-08-25", "2017-09-01");

		result.code = 1;
		result.msg = "success";
		result.obj = sum;

		renderJSON(result);
	}

	public static void moonCakeLottery(int type) {
		ResultInfo result = new ResultInfo();

		if (type < 0 || type > 1) {
			result.code = -1;
			result.msg = "设备类型异常";
			renderJSON(result);
		}

		long userId = 0L;

		if (type == 0) {
			// APP
			String userIdObj = params.get("userId");
			result = Security.decodeSign(userIdObj, Constants.USER_ID_SIGN, Constants.VALID_TIME,
					ConfConst.ENCRYPTION_APP_KEY_DES);
			if (result.code < 1) {
				result.code = -1;
				result.msg = "用户id异常";
				renderJSON(result);
			}
			userId = Long.parseLong(result.obj.toString());
		} else {
			// WECHAT OR WEB
			CurrUser currUser = getCurrUser();

			if (currUser == null) {
				result.code = ResultInfo.NOT_LOGIN;
				result.msg = "未登录请登录";
				renderJSON(result);
			}

			userId = currUser.id;
		}
		Map<String, Object> map = new HashMap<String, Object>();

		// 1月标累计投资金额
		long result1 = investService.calulateInvestMoneyInDatesWithPeriod(userId, "2017-09-01", "2017-09-31", 1);

		// 1月标累计抽奖次数
		int count1 = moonCakeLotteryRecordService.calulateLotterySizeWithPeriod(userId, 1);

		// 3月标累计投资金额
		long result3 = investService.calulateInvestMoneyInDatesWithPeriod(userId, "2017-09-01", "2017-09-31", 3);

		// 3月标累计抽奖次数
		int count3 = moonCakeLotteryRecordService.calulateLotterySizeWithPeriod(userId, 3);

		// 6月标累计投资金额
		long result6 = investService.calulateInvestMoneyInDatesWithPeriod(userId, "2017-09-01", "2017-09-31", 6);

		// 6月标累计抽奖次数
		int count6 = moonCakeLotteryRecordService.calulateLotterySizeWithPeriod(userId, 6);
		// 筛选线下用户、标的周期，不享受线上彩虹大宝箱活动
		// map.put("period1", userFilterService.isOfflineUser(userId, 1) ? 0 :
		// (result1 / 1000 - count1 < 0 ? 0 : result1 / 1000 - count1));
		// map.put("period3", userFilterService.isOfflineUser(userId, 3) ? 0 :
		// (result3 / 1000 - count3 < 0 ? 0 : result3 / 1000 - count3));
		// map.put("period6", userFilterService.isOfflineUser(userId, 6) ? 0 :
		// (result6 / 1000 - count6 < 0 ? 0 : result6 / 1000 - count6));
		map.put("period1", (result1 / 1000 - count1 < 0 ? 0 : result1 / 1000 - count1));
		map.put("period3", (result3 / 1000 - count3 < 0 ? 0 : result3 / 1000 - count3));
		map.put("period6", (result6 / 1000 - count6 < 0 ? 0 : result6 / 1000 - count6));

		result.code = 1;
		result.msg = "success";
		result.obj = map;

		renderJSON(result);

	}

	public static void doMoonCakeLottery(int type, int period) {
		ResultInfo result = new ResultInfo();

		if (type < 0 || type > 1) {
			result.code = -1;
			result.msg = "设备类型异常";
			renderJSON(result);
		}

		long userId = 0L;

		if (type == 0) {
			// APP
			String userIdObj = params.get("userId");
			result = Security.decodeSign(userIdObj, Constants.USER_ID_SIGN, Constants.VALID_TIME,
					ConfConst.ENCRYPTION_APP_KEY_DES);
			if (result.code < 1) {
				result.code = -1;
				result.msg = "用户id异常";
				renderJSON(result);
			}
			userId = Long.parseLong(result.obj.toString());
		} else {
			// WECHAT OR WEB
			CurrUser currUser = getCurrUser();

			if (currUser == null) {
				result.code = ResultInfo.NOT_LOGIN;
				result.msg = "未登录请登录";
				renderJSON(result);
			}

			userId = currUser.id;
		}

		if (period != 1 && period != 3 && period != 6) {
			result.code = -1;
			result.msg = "标的期限错误";
			renderJSON(result);
		}

		// period月标累计投资金额
		long resultInvest = investService.calulateInvestMoneyInDatesWithPeriod(userId, "2017-09-01", "2017-09-31",
				period);

		// period月标累计抽奖次数
		int count = moonCakeLotteryRecordService.calulateLotterySizeWithPeriod(userId, period);

		if (resultInvest / 1000 - count <= 0) {
			result.code = -1;
			result.msg = "没有抽奖次数";
			renderJSON(result);
		}

		int index = -1;

		try {
			Double[] pros = { 1.0, 2.0, 3.0, 78.0, 98.0, 137.0, 681.0 };

			int randomNum = new Random().nextInt(1000) + 1;// 随机生成1到sum的整数

			double sumOne = 0;// 概率转换为概率总和上的比例区间，随机数在区间内为中奖，

			for (int i = 0; i < 7; i++) {// 概率数组循环
				// sumTwo区间结束
				double sumTwo = sumOne + pros[i];

				if (sumOne < randomNum && randomNum <= sumTwo) {// 中奖
					index = i;
					break;
				} else {
					sumOne = sumTwo;
				}
			}
		} catch (Exception e) {
			result.code = -1;
			result.msg = "抽奖出现异常";
			renderJSON(result);
		}

		if (index < 0 || index >= 7) {
			result.code = -1;
			result.msg = "抽奖出现异常";
			renderJSON(result);
		}

		// // 1月标奖励金额
		// double[] reward1 = {308.0, 208.0, 108.0, 8.08, 3.08, 2.08, 1.08};
		// // 3月标奖励金额
		// double[] reward3 = {918.0, 618.0, 318.0, 31.8, 9.18, 6.18, 3.18};
		// // 6月标奖励金额
		// double[] reward6 = {1888.0, 1268.0, 658.0, 58.8, 18.8, 11.88, 6.68};

		// 1月标奖励金额
		double[] reward1 = { 188.0, 88.0, 18.0, 8.0, 3.0, 2.0, 1.0 };

		// 3月标奖励金额
		double[] reward3 = { 358.0, 188.0, 78.0, 28.0, 8.0, 6.0, 3.0 };

		// 6月标奖励金额
		double[] reward6 = { 518.0, 288.0, 118.0, 68.0, 18.0, 10.0, 8.0 };

		double reward = 0.0;

		switch (period) {
		case 1:
			reward = reward1[index];
			break;
		case 3:
			reward = reward3[index];
			break;
		case 6:
			reward = reward6[index];
			break;
		}

		// 生成中奖记录
		t_moon_cake_lottery_record newRecord = new t_moon_cake_lottery_record(userId, period, reward);

		result = moonCakeLotteryRecordService.insert(newRecord);

		if (result.code != 1) {
			result.msg = "添加抽奖记录失败";
			renderJSON(result);
		} else {
			result.msg = "抽奖成功";
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("index", index);
			map.put("awardMoney", newRecord.award_money);
			result.obj = map;
			renderJSON(result);
		}

	}

//	/**
//	 * 领号记录列表
//	 */
//	public static void awardNumberListPre() {
//		List<Map<String, Object>> awardNumberList = awardnumberrecordservice.getAwardNumberList();
//		renderJSON(awardNumberList);
//	}

	public static void getRedPacketSelf(int type, int period, double amount) {

		ResultInfo result = new ResultInfo();

		if (type < 0 || type > 1) {
			result.code = -1;
			result.msg = "设备类型异常";
			renderJSON(result);
		}

		long userId = 0L;

		if (type == 0) {
			// APP
			String userIdObj = params.get("userId");
			System.out.println("userId=============" + userId);
			Logger.info("userId=============" + userId);
			result = Security.decodeSign(userIdObj, Constants.USER_ID_SIGN, Constants.VALID_TIME,
					ConfConst.ENCRYPTION_APP_KEY_DES);
			if (result.code < 1) {
				result.code = -1;
				result.msg = "用户id异常";
				renderJSON(result);
			}
			userId = Long.parseLong(result.obj.toString());
		} else {
			// WECHAT OR WEB
			CurrUser currUser = getCurrUser();

			if (currUser == null) {
				result.code = ResultInfo.NOT_LOGIN;
				result.msg = "未登录请登录";
				renderJSON(result);
			}
			userId = currUser.id;
		}
		Date startDate = DateUtil.strToDate("2017-09-30 23:59:59", "yyyy-MM-dd HH:mm:ss");
		Date endDate = DateUtil.strToDate("2017-11-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
		long thistime = new Date().getTime();
		if ((startDate.getTime() > thistime) || (endDate.getTime() < thistime)) {
			result.code = -1;
			result.msg = "活动未开启";
			renderJSON(result);
		}

		if (period != 1 && period != 3 && period != 6) {
			result.code = -1;
			result.msg = "标的期限错误";
			renderJSON(result);
		}

		int size = redpacketUserService.findSelfRedPacket(userId, period);

		if (size > 0) {
			result.code = -1;
			result.msg = "今天" + period + "月标红包已经领取过了";
			renderJSON(result);
		}

		if (amount < 100) {
			result.code = -1;
			result.msg = "投资金额错误";
			renderJSON(result);
		}

		result = redpacketUserService.createRedPacketSelf(userId, period, amount);
		renderJSON(result);
	}

	public static void getCountRedPacketSelf() {
		renderJSON(redpacketUserService.getCountRedPacketSelf());
	}

}