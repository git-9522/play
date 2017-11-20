package controllers.back.spread;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.shove.Convert;

import common.constants.ConfConst;
import common.constants.Constants;
import common.constants.SettingKey;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.StrUtil;
import common.utils.excel.ExcelUtils;
import common.utils.jsonAxml.JsonDateValueProcessor;
import common.utils.jsonAxml.JsonDoubleValueProcessor;
import controllers.common.BackBaseController;
import models.common.bean.InvestLottery;
import models.common.entity.t_event_supervisor;
import models.common.entity.t_event_supervisor.Event;
import models.common.entity.t_invest_reward;
import models.common.entity.t_reversal_record;
import models.common.entity.t_reversal_reward;
import models.common.entity.t_reversal_rule;
import models.core.entity.t_red_packet;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import play.Logger;
import services.common.InvestLotteryService;
import services.common.InvestRewardService;
import services.common.ReversalRecordService;
import services.common.ReversalRewardService;
import services.common.ReversalRuleService;
import services.core.RedpacketService;

public class ActivityCtrl extends BackBaseController {
	
	protected static RedpacketService redpacketService = Factory.getService(RedpacketService.class);
	
	protected static InvestRewardService investRewardService = Factory.getService(InvestRewardService.class);
	
	protected static InvestLotteryService investLotteryService = Factory.getService(InvestLotteryService.class);
	
	protected static ReversalRuleService reversalRuleService = Factory.getService(ReversalRuleService.class);

	protected static ReversalRewardService reversalRewardService = Factory.getService(ReversalRewardService.class);
	
	protected static ReversalRecordService reversalRecordService = Factory.getService(ReversalRecordService.class);
	
	/**
	 * 显示每日活动设置
	 */
	public static void showActivityPre() {
		// 基本信息
		int rp_is_use = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.RP_IS_USE), 0);
		String rp_start_time = settingService.findSettingValueByKey(SettingKey.RP_START_TIME);
		String rp_end_time = settingService.findSettingValueByKey(SettingKey.RP_END_TIME);
		String rp_daily_count = settingService.findSettingValueByKey(SettingKey.RP_DAILY_COUNT);
		String rp_rule = settingService.findSettingValueByKey(SettingKey.RP_RULE);

		renderArgs.put("rp_is_use", rp_is_use);
		renderArgs.put("rp_start_time", rp_start_time);
		renderArgs.put("rp_end_time", rp_end_time);
		renderArgs.put("rp_daily_count", rp_daily_count);
		renderArgs.put("rp_rule", rp_rule);

		render();
	}

	/**
	 * 编辑每日活动
	 */
	public static void editActivity() {

		checkAuthenticity();

		String rp_start_time = params.get("rp_start_time");
		String rp_end_time = params.get("rp_end_time");
		String rp_daily_count = params.get("rp_daily_count");
		String rp_rule = params.get("rp_rule");

		if (StringUtils.isBlank(rp_start_time) || StringUtils.isBlank(rp_end_time)) {
			flash.error("请输入活动时间");
			showActivityPre();
		} else {
			Date startDate = DateUtil.strToDate(rp_start_time, "MM/dd/yyyy");
			Date endDate = DateUtil.strToDate(rp_end_time, "MM/dd/yyyy");
			if(startDate.after(endDate)) {
				flash.error("开始时间不能大于结束时间");
				showActivityPre();
			}
		}

		if ((!StrUtil.isNumericPositiveInt(rp_daily_count)) || StrUtil.isNumLess(rp_daily_count, 0)
				|| StrUtil.isNumMore(rp_daily_count, 100)) {
			flash.error("每日领取红包次数不准确");
			showActivityPre();
		}

		if (StringUtils.isBlank(rp_rule)) {
			flash.error("请输入活动规则");
			showActivityPre();
		}

		Map<String, String> infos = new HashMap<String, String>();
		infos.put(SettingKey.RP_START_TIME, rp_start_time);
		infos.put(SettingKey.RP_END_TIME, rp_end_time);
		infos.put(SettingKey.RP_DAILY_COUNT, rp_daily_count);
		infos.put(SettingKey.RP_RULE, rp_rule);

		boolean flag = settingService.updateSettings(infos);
		if (!flag) {

			flash.error("更新活动规则失败了");
			showActivityPre();
		}
		
		boolean display = settingService.updateSetting(SettingKey.RP_IS_USE, "0");
		if (!display) {
			flash.error("更新活动规则失败了");
			showActivityPre();
		}

		long supervisorId = getCurrentSupervisorId();
		
		supervisorService.addSupervisorEvent(supervisorId, Event.RP_ACTIVITY, null);

		flash.success("更新活动规则成功");
		showActivityPre();
	}

	public static void openActivity(boolean flag) {
		
		ResultInfo result = new ResultInfo();

		String _value = "0";
		if (flag) {
			_value = "1";
		}

		// 开启条件：概率总和等于100%
		if (flag) {
			// 判断有没有活动红包
			int count = redpacketService.findAllRedPacketRuleCount(t_red_packet.RedpacketType.ACTIVITY.code, true);
			if(count <= 0) {
				result.msg = "活动红包数量>0才能开启";
				renderJSON(result);
			}
		}

		boolean display = settingService.updateSetting(SettingKey.RP_IS_USE, _value);
		if (!display) {
			result.msg = "数据没有更新";

			renderJSON(result);
		}

		result.code = 1;
		result.msg = "活动状态已更新";

		long supervisor_id = getCurrentSupervisorId();
		supervisorService.addSupervisorEvent(supervisor_id, Event.RP_ACTIVITY, null);

		renderJSON(result);
	}
	
	public static void showActivityRedPacketsPre(int currPage, int pageSize) {
		// 序号
		String numNo = params.get("numNo");
		
		PageBean<t_red_packet> pageBean = redpacketService.pageOfRedPacket(currPage, pageSize, t_red_packet.RedpacketType.ACTIVITY.code, null, numNo);
		
		render(pageBean, numNo);
	}
	
	public static void addActivityRedPacketPre() {
		render();
	}
	
	public static void addActivityRedPacket(t_red_packet packet) {
		checkAuthenticity();
		
		boolean tag = false;
		
		if(packet.id != null && packet.id != 0L) {
			tag = true;
		} else {
			flash("amount", packet.amount);
			flash("use_rule", packet.use_rule);
			flash("end_time", packet.end_time);
			flash("bid_period", packet.bid_period);
			flash("is_send_email", packet.is_send_email);
			flash("is_send_letter", packet.is_send_letter);
			flash("is_send_sms", packet.is_send_sms);
		}
		
		ResultInfo result = new ResultInfo();
		
		packet.type = t_red_packet.RedpacketType.ACTIVITY.code;
		
		t_red_packet redPacket = redpacketService.insert(packet);
		
		if(redPacket == null){
			result.msg = "保存到数据库出错";
			LoggerUtil.info(true,"保存红包信息：%s", result.msg);
			flash.error(result.msg);
			if(tag) {
				editActivityRedPacketPre(packet.id);
			} else {
				addActivityRedPacketPre();
			}
		}
		
		
		boolean display = settingService.updateSetting(SettingKey.RP_IS_USE, "0");
		
		if (!display) {
			result.msg = "数据没有更新";
			LoggerUtil.info(true,"关闭领取红包活动时：%s", result.msg);
			flash.error(result.msg);
			if(tag) {
				editActivityRedPacketPre(packet.id);
			} else {
				addActivityRedPacketPre();
			}
		}
		
		/** 添加管理员事件 */
		Map<String, String>param = new HashMap<String, String>();
		param.put("supervisor", ""+getCurrentSupervisorId());
		param.put("rewardId", ""+redPacket.id);
		param.put("rewardName", ""+redPacket.amount);
		boolean flag = supervisorService.addSupervisorEvent(getCurrentSupervisorId(), t_event_supervisor.Event.RP_ACTIVITY, param);
		
		if(!flag){
			
			flash.error("保存管理员事件失败");
			LoggerUtil.error(true, "保存管理员事件失败");
			showActivityRedPacketsPre(0,0);
		}
		
		
		flash.success("保存活动红包成功");
		showActivityRedPacketsPre(0,0);
	}
	
	public static void editActivityRedPacketPre(long id) {
		t_red_packet packet = redpacketService.findByID(id);
		if(packet == null) {
			flash.error("该红包信息错误");
			showActivityPre();
		} else {
			flash("id", packet.id);
			flash("amount", packet.amount);
			flash("use_rule", packet.use_rule);
			flash("end_time", packet.end_time);
			flash("bid_period", packet.bid_period);
			flash("is_send_email", packet.is_send_email);
			flash("is_send_letter", packet.is_send_letter);
			flash("is_send_sms", packet.is_send_sms);
			flash("time", packet.time);
			render();
		}
	}
	
	public static void updateActivityRedPacketStatus(long id, String amount, boolean isUse) {
		
		ResultInfo res = new ResultInfo();
		
		if(id < 0){
			res.code = -1;
			res.msg = "参数错误";
			renderJSON(res);
		}
		
		boolean upd= redpacketService.updateStatus(id, isUse);
		
		if(!upd){
			LoggerUtil.error(true, "更新红包上下架状态失败!");
			res.code = -1;
			res.msg = "更新奖品上下架状态失败!";
			renderJSON(res);
		}
		
		boolean display = settingService.updateSetting(SettingKey.RP_IS_USE, "0");
		
		if (!display) {
			res.msg = "数据没有更新";
			LoggerUtil.info(true,"关闭领取红包活动时：%s", res.msg);
			res.code = -1;
			renderJSON(res);
		}
		
		/** 添加管理员事件 */
		Map<String, String>param = new HashMap<String, String>();
		param.put("supervisor", ""+getCurrentSupervisorId());
		param.put("rewardId", ""+id);
		param.put("rewardName", amount);
		boolean flag = supervisorService.addSupervisorEvent(getCurrentSupervisorId(), t_event_supervisor.Event.RP_ACTIVITY, param);
		
		if(!flag){
			LoggerUtil.error(true, "保存管理员事件失败!");
			res.code = -1;
			res.msg = "保存管理员事件失败";
			
			renderJSON(res);
		}
		
		res.code = 1;
		res.msg = "";
		res.obj = isUse;
		
		renderJSON(res);
	}
	
	public static void delActivityRedPacket(String sign) {
		ResultInfo result = Security.decodeSign(sign, Constants.RED_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			renderJSON(result);
		}
		
		long id = Long.parseLong((String) result.obj);
		t_red_packet rd = redpacketService.findByID(id);
		
		if(rd == null){
			
			result.code=-1;
			result.msg="该活动红包不存在";
			
			renderJSON(result);
		}
		
		boolean delFlag = redpacketService.delete(id);
		if(!delFlag){
			result.code=-1;
			result.msg="删除失败";
			renderJSON(result);
		}else{
			boolean display = settingService.updateSetting(SettingKey.RP_IS_USE, "0");
			if (!display) {
				result.msg = "数据没有更新";
				LoggerUtil.info(true,"关闭领取红包活动时：%s", result.msg);
				result.code = -1;
				renderJSON(result);
			}
			//管理员事件
			long supervisorId = getCurrentSupervisorId();
			Map<String, String> supervisorEventParam = new HashMap<String, String>(); 
			supervisorEventParam.put("rewardId", rd.id+"");  
			supervisorEventParam.put("rewardName", ""+rd.amount);
			supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.RP_ACTIVITY, supervisorEventParam);
		}
		
		result.code=1;
		result.msg="删除成功";
		
		renderJSON(result);
	}
	
	public static void showLotteryActivityPre() {
		
		/* 基本信息 */
		int lottery_is_use = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.LOTTERY_IS_USE), 0);
		String lottery_start_time = settingService.findSettingValueByKey(SettingKey.LOTTERY_START_TIME);
		String lottery_end_time = settingService.findSettingValueByKey(SettingKey.LOTTERY_END_TIME);
		double lottery_invest = Convert.strToDouble(settingService.findSettingValueByKey(SettingKey.LOTTERY_INVEST), 0);
		String lottery_rule = settingService.findSettingValueByKey(SettingKey.LOTTERY_RULE);
		
		renderArgs.put("lottery_is_use", lottery_is_use);
		renderArgs.put("lottery_start_time", lottery_start_time);
		renderArgs.put("lottery_end_time", lottery_end_time);
		renderArgs.put("lottery_invest", lottery_invest);
		renderArgs.put("lottery_rule", lottery_rule);

		render();
	}
	
	public static void showLotteryActivttyRewardPre(int currPage, int pageSize) {
		int exports = Convert.strToInt(params.get("exports"), 0);
		
		String id = params.get("id");
		String name = params.get("name");
		
		PageBean<t_invest_reward> pageBean = investRewardService.pageOfBackRewards(currPage, pageSize, id, name, exports);
		
		render(pageBean, id, name);
	}
	
	public static void editLotteryActivity() {
		checkAuthenticity();
		String lottery_start_time = params.get("lottery_start_time");
		String lottery_end_time = params.get("lottery_end_time");
		String lottery_invest = params.get("lottery_invest");
		String lottery_rule = params.get("lottery_rule");

		if (StringUtils.isBlank(lottery_start_time) || StringUtils.isBlank(lottery_end_time)) {
			flash.error("请输入活动时间");
			showLotteryActivityPre();
		} else {
			Date startDate = DateUtil.strToDate(lottery_start_time, "MM/dd/yyyy");
			Date endDate = DateUtil.strToDate(lottery_end_time, "MM/dd/yyyy");
			if(startDate.after(endDate)) {
				flash.error("开始时间不能大于结束时间");
				showLotteryActivityPre();
			}
		}

		if (!StrUtil.isNumericalValue(lottery_invest) || StrUtil.isNumLess(lottery_invest, 0.01F)
				|| StrUtil.isNumMore(lottery_invest, 1000F)) {
			flash.error("设置投资抽奖金额");
			showLotteryActivityPre();
		}

		if (StringUtils.isBlank(lottery_rule)) {
			flash.error("请输入活动规则");
			showLotteryActivityPre();
		}

		Map<String, String> infos = new HashMap<String, String>();
		infos.put(SettingKey.LOTTERY_START_TIME, lottery_start_time);
		infos.put(SettingKey.LOTTERY_END_TIME, lottery_end_time);
		infos.put(SettingKey.LOTTERY_INVEST, lottery_invest);
		infos.put(SettingKey.LOTTERY_RULE, lottery_rule);
		
		Logger.info(infos.size() + "");

		boolean flag = settingService.updateSettings(infos);
		if (!flag) {

			flash.error("更新活动规则失败了");
			showActivityPre();
		}
		
		boolean display = settingService.updateSetting(SettingKey.LOTTERY_IS_USE, "0");
		if (!display) {
			flash.error("更新活动规则失败了");
			showActivityPre();
		}

		long supervisorId = getCurrentSupervisorId();
		
		supervisorService.addSupervisorEvent(supervisorId, Event.LOTTERY_ACTIVITY, null);

		flash.success("更新活动规则成功");
		showLotteryActivityPre();
	}
	
	public static void openLotteryActivity(boolean flag) {
		ResultInfo result = new ResultInfo();

		String _value = "0";
		
		if (flag) {
			_value = "1";
		}

		// 开启条件：概率总和等于100%
		if (flag) {
			// 判断有没有活动红包
			int count = investRewardService.queryRewardCount(true);
			if(count <= 0) {
				result.msg = "抽奖商品数量>8才能开启";
				renderJSON(result);
			}
			
			double probability = investRewardService.queryRewardProbability(true);
			
			if(probability != 100) {
				result.msg = "抽奖商品概率总和必须 = 100才能开启";
				renderJSON(result);
			}
		}

		boolean display = settingService.updateSetting(SettingKey.LOTTERY_IS_USE, _value);
		if (!display) {
			result.msg = "数据没有更新";

			renderJSON(result);
		}

		result.code = 1;
		result.msg = "活动状态已更新";

		long supervisor_id = getCurrentSupervisorId();
		supervisorService.addSupervisorEvent(supervisor_id, Event.LOTTERY_ACTIVITY, null);

		renderJSON(result);
	}
	
	public static void addLotteryActivttyRewardPre() {
		render();
	}

	public static void editLotteryActivttyRewardPre(long id) {
		if(id < 0){
			flash.error("奖品ID参数错误");
			showLotteryActivttyRewardPre(1,10);
		}
		
		t_invest_reward rewards = investRewardService.findByID(id);
		
		if(rewards == null){
			flash.error("没有找到该奖品");
			showLotteryActivttyRewardPre(1,10);
		}
		render(rewards);
	}
	
	public static void addLotteryActivttyReward(t_invest_reward rewards) {
		checkAuthenticity();
		flash.put("name", rewards.name);
		flash.put("value", (Double)rewards.value);
		flash.put("probability", rewards.probability);
		
		ResultInfo result = new ResultInfo();
		
		result = investRewardService.insert(rewards);
		
		if(result.code < 1){
			LoggerUtil.info(true,"保存奖品参数时：%s", result.msg);
			flash.error(result.msg);
			addLotteryActivttyRewardPre();
		}
		
		/** 添加管理员事件 */
		Map<String, String> param = new HashMap<String, String>();
		param.put("supervisor", "" + getCurrentSupervisorId());
		param.put("rewardId", "" + rewards.id);
		param.put("rewardName", rewards.name);
		
		boolean flag = supervisorService.addSupervisorEvent(getCurrentSupervisorId(), t_event_supervisor.Event.REWARDS_ADD, param);
		
		if(!flag){
			flash.error("保存管理员事件失败");
			LoggerUtil.error(true, "保存管理员事件失败");
			showLotteryActivttyRewardPre(1,10);
		}
		
		flash.success("保存奖品成功");
		showLotteryActivttyRewardPre(1,10);
	}
	
	public static void editLotteryActivttyReward(t_invest_reward rewards) {
		checkAuthenticity();
		ResultInfo result = new ResultInfo();
		if(rewards.id < 0){
			flash.error("奖品ID参数错误");
			editLotteryActivttyRewardPre(rewards.id);
		}
		
		t_invest_reward rd = investRewardService.findByID(rewards.id);
		
		if(rd == null){
			flash.error("没有找到该奖品");
			editLotteryActivttyRewardPre(rewards.id);
		}
		
		result = investRewardService.update(rewards, rd);
		
		if(result.code < 1){
			LoggerUtil.info(true,"保存奖品参数时：%s", result.msg);
			flash.error(result.msg);
			editLotteryActivttyRewardPre(rewards.id);
		}
		
		String _value = "0";
		boolean display = settingService.updateSetting(SettingKey.LOTTERY_IS_USE, _value);
		if (!display) {
			result.msg = "数据没有更新";
			LoggerUtil.info(true,"关闭抽奖时：%s", result.msg);
			flash.error(result.msg);
			editLotteryActivttyRewardPre(rewards.id);
		}
		
		/** 添加管理员事件 */
		Map<String, String>param = new HashMap<String, String>();
		param.put("supervisor", ""+getCurrentSupervisorId());
		param.put("rewardId", ""+rd.id);
		param.put("rewardName", rd.name);
		boolean flag = supervisorService.addSupervisorEvent(getCurrentSupervisorId(), t_event_supervisor.Event.INVEST_LOTTERY_ADD_REWARD, param);
		
		if(!flag){
			
			flash.error("保存管理员事件失败");
			LoggerUtil.error(true, "保存管理员事件失败");
			editLotteryActivttyRewardPre(rewards.id);
		}
		
		flash.success(result.msg);
		showLotteryActivttyRewardPre(1, 10);
	}
	
	public static void updateRewardStatus(long id, boolean isUse, String name) {
		ResultInfo res = new ResultInfo();
		
		if(id < 1){
			res.code = -1;
			res.msg = "参数错误";
			renderJSON(res);
		}
		
		Event ev = isUse ? Event.INVEST_LOTTERY_REWARDS_DISABLED : Event.INVEST_LOTTERY_REWARDS_ENABLE;
		
		boolean upd= investRewardService.updateRewardsStatus(id, !isUse);
		if(!upd){
			LoggerUtil.error(true, "更新奖品上下架状态失败!");
			res.code = -1;
			res.msg = "更新奖品上下架状态失败!";
			renderJSON(res);
		}
		
		/** 添加管理员事件 */
		Map<String, String>param = new HashMap<String, String>();
		param.put("supervisor", "" + getCurrentSupervisorId());
		param.put("rewardId", "" + id);
		param.put("rewardName", name);
		boolean flag = supervisorService.addSupervisorEvent(getCurrentSupervisorId(), ev, param);
		
		if(!flag){
			LoggerUtil.error(true, "保存管理员事件失败!");
			res.code = -1;
			res.msg = "保存管理员事件失败";
			
			renderJSON(res);
		}
		
		res.code = 1;
		res.msg = "";
		res.obj = !isUse;
		
		renderJSON(res);
	}
	
	public static void delRewards(String sign) {
		ResultInfo result = Security.decodeSign(sign, Constants.MALL_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		
		if (result.code < 1) {
			renderJSON(result);
		}
		long id = Long.parseLong((String) result.obj);
		
		t_invest_reward rd = investRewardService.findByID(id);
		
		if(rd == null){
			result.code=-1;
			result.msg="该奖品不存在";
			renderJSON(result);
		}
		
		boolean delFlag = investRewardService.del(id);
		
		if(!delFlag){
			result.code=-1;
			result.msg="删除失败";
			renderJSON(result);
		}else{
			//管理员事件
			long supervisorId = getCurrentSupervisorId();
			Map<String, String> supervisorEventParam = new HashMap<String, String>(); 
			supervisorEventParam.put("rewardId", rd.id+"");  
			supervisorEventParam.put("rewardName", rd.name);
			supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.REWARDS_DELETE, supervisorEventParam);
		}
		
		result.code=1;
		result.msg="删除成功";
		
		renderJSON(result);
	}
	
	public static void showInvestLotteryPre(int currPage, int pageSize) {
		int exports = Convert.strToInt(params.get("exports"), 0);
		
		String id = params.get("id");
		String name = params.get("name");
		String mobile = params.get("mobile");
		
		PageBean<InvestLottery> pageBean = investLotteryService.pageOfBackRewards(currPage, pageSize, id, name, mobile, exports);
		
		render(pageBean, id, name, mobile);
	}
	
	public static void showReversalActivityPre() {
		int reversal_is_use = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.REVERSAL_IS_USE), 0);
		String reversal_start_time = settingService.findSettingValueByKey(SettingKey.REVERSAL_START_TIME);
		String reversal_end_time = settingService.findSettingValueByKey(SettingKey.REVERSAL_END_TIME);
		double reversal_invest = Convert.strToDouble(settingService.findSettingValueByKey(SettingKey.REVERSAL_INVEST), 0);
		String reversal_rule = settingService.findSettingValueByKey(SettingKey.REVERSAL_RULE);
		
		renderArgs.put("reversal_is_use", reversal_is_use);
		renderArgs.put("reversal_start_time", reversal_start_time);
		renderArgs.put("reversal_end_time", reversal_end_time);
		renderArgs.put("reversal_invest", reversal_invest);
		renderArgs.put("reversal_rule", reversal_rule);
		render();
	}
	
	public static void editReversalActivity() {
		checkAuthenticity();
		String reversal_start_time = params.get("reversal_start_time");
		String reversal_end_time = params.get("reversal_end_time");
		String reversal_invest = params.get("reversal_invest");
		String reversal_rule = params.get("reversal_rule");
		
		if (StringUtils.isBlank(reversal_start_time) || StringUtils.isBlank(reversal_end_time)) {
			flash.error("请输入活动时间");
			showReversalActivityPre();
		} else {
			Date startDate = DateUtil.strToDate(reversal_start_time, "MM/dd/yyyy");
			Date endDate = DateUtil.strToDate(reversal_end_time, "MM/dd/yyyy");
			if(startDate.after(endDate)) {
				flash.error("开始时间不能大于结束时间");
				showReversalActivityPre();
			}
		}

		if (!StrUtil.isNumericalValue(reversal_invest) || StrUtil.isNumLess(reversal_invest, 0.01F)
				|| StrUtil.isNumMore(reversal_invest, 1000F)) {
			flash.error("设置投资抽奖金额错误");
			showReversalActivityPre();
		}

		if (StringUtils.isBlank(reversal_rule)) {
			flash.error("请输入活动规则");
			showReversalActivityPre();
		}

		Map<String, String> infos = new HashMap<String, String>();
		infos.put(SettingKey.REVERSAL_START_TIME, reversal_start_time);
		infos.put(SettingKey.REVERSAL_END_TIME, reversal_end_time);
		infos.put(SettingKey.REVERSAL_INVEST, reversal_invest);
		infos.put(SettingKey.REVERSAL_RULE, reversal_rule);
		
		Logger.info(infos.size() + "");

		boolean flag = settingService.updateSettings(infos);
		if (!flag) {
			flash.error("更新活动规则失败了");
			showReversalActivityPre();
		}
		
		boolean display = settingService.updateSetting(SettingKey.LOTTERY_IS_USE, "0");
		if (!display) {
			flash.error("更新活动规则失败了");
			showReversalActivityPre();
		}

		long supervisorId = getCurrentSupervisorId();
		
		supervisorService.addSupervisorEvent(supervisorId, Event.LOTTERY_ACTIVITY, null);

		flash.success("更新活动规则成功");
		showReversalActivityPre();
	}
	
	public static void openReversalActivity(boolean flag) {
		ResultInfo result = new ResultInfo();

		String _value = "0";
		
		if (flag) {
			_value = "1";
		}

		// 开启条件：概率总和等于100%
		if (flag) {
			// TODO 逻辑判断
			List<Map<String, Object>> list = reversalRuleService.findAllProbability();
			for(Map<String, Object> map : list) {
				double sum = Double.parseDouble(map.get("probability").toString());
				if(sum != 100) {
					result.msg = "单笔最小投资金额为\" " + map.get("min").toString() + " \"的概率之和不为100";
					result.code = -1;
					renderJSON(result);
				}
			}
		}
		
		boolean display = settingService.updateSetting(SettingKey.REVERSAL_IS_USE, _value);
		if (!display) {
			result.msg = "数据没有更新";

			renderJSON(result);
		}

		result.code = 1;
		result.msg = "活动状态已更新";

		long supervisor_id = getCurrentSupervisorId();
		supervisorService.addSupervisorEvent(supervisor_id, Event.REVERSAL_ACTIVITY, null);

		renderJSON(result);
	}
	
	public static void showReversalRulesPre(int currPage, int pageSize) {
		String id = params.get("id");
		String level = params.get("level");
		
		PageBean<t_reversal_rule> pageBean = reversalRuleService.pageOfBackRules(currPage, pageSize, id, level);
		
		render(pageBean, id, level);
	}
	
	public static void addReversalRulePre() {
		render();
	}
	
	public static void addReversalRule(t_reversal_rule rule) {
		checkAuthenticity();
		flash.put("level", rule.level);
		flash.put("probability", rule.probability);
		flash.put("min", rule.min);
		flash.put("max", rule.max);
		
		ResultInfo result = new ResultInfo();
		
		result = reversalRuleService.check(rule);
		
		if(result.code != 1) {
			flash.error(result.msg);
			addReversalRulePre();
		}
		
		result = reversalRuleService.insert(rule);
		
		if(result.code < 1){
			LoggerUtil.info(true,"保存翻牌规则时：%s", result.msg);
			flash.error(result.msg);
			addReversalRulePre();
		}
		
		/** 添加管理员事件 */
		Map<String, String> param = new HashMap<String, String>();
		param.put("supervisor", "" + getCurrentSupervisorId());
		
		boolean flag = supervisorService.addSupervisorEvent(getCurrentSupervisorId(), t_event_supervisor.Event.REVERSAL_RULE_ADD, param);
		
		if(!flag){
			flash.error("保存管理员事件失败");
			LoggerUtil.error(true, "保存管理员事件失败");
			showReversalRulesPre(1,10);
		}
		
		flash.success("保存翻牌规则成功");
		showReversalRulesPre(1,10);
	}
	
	public static void editReversalRulePre(String sign) {
		ResultInfo result = Security.decodeSign(sign, Constants.MALL_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		
		if (result.code < 1) {
			renderJSON(result);
		}
		
		long id = Long.parseLong((String) result.obj);
		
		t_reversal_rule rule = reversalRuleService.findByID(id);
		
		render(rule);
	}
	
	public static void editReversalRule(t_reversal_rule rule) {
		checkAuthenticity();
		
		ResultInfo result = new ResultInfo();
		
		result = reversalRuleService.check(rule);
		
		if(result.code != 1) {
			flash.error(result.msg);
			if(result.code == -100) {
				showReversalRulesPre(1, 10);
			} else {
				editReversalRulePre(rule.getSign());
			}
		}
		
		result = reversalRuleService.update(rule);
		
		if(result.code < 1){
			LoggerUtil.info(true,"修改翻牌规则时：%s", result.msg);
			flash.error(result.msg);
			editReversalRulePre(rule.getSign());
		}
		
		/** 添加管理员事件 */
		Map<String, String> param = new HashMap<String, String>();
		param.put("supervisor", "" + getCurrentSupervisorId());
		
		boolean flag = supervisorService.addSupervisorEvent(getCurrentSupervisorId(), t_event_supervisor.Event.REVERSAL_RULE_EDIT, param);
		
		if(!flag){
			flash.error("保存管理员事件失败");
			LoggerUtil.error(true, "保存管理员事件失败");
			showReversalRulesPre(1,10);
		}
		
		flash.success("保存翻牌规则成功");
		showReversalRulesPre(1,10);
	}
	
	public static void delReversalRule(String sign) {
		ResultInfo result = Security.decodeSign(sign, Constants.MALL_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		
		if (result.code < 1) {
			renderJSON(result);
		}
		
		long id = Long.parseLong((String) result.obj);
		
		t_reversal_rule rule = reversalRuleService.findByID(id);
		
		if(rule == null){
			result.code=-1;
			result.msg="该翻牌规则不存在";
			renderJSON(result);
		}
		
		List<t_reversal_reward> rewards = reversalRewardService.queryByLevel(rule.level);
		
		if(rewards != null && !rewards.isEmpty()) {
			result.code=-1;
			result.msg="该翻牌规则关联着奖励";
			renderJSON(result);
		}
		
		boolean delFlag = reversalRuleService.del(id);
		
		if(!delFlag){
			result.code=-1;
			result.msg="删除失败";
			renderJSON(result);
		}else{
			//管理员事件
			long supervisorId = getCurrentSupervisorId();
			Map<String, String> supervisorEventParam = new HashMap<String, String>(); 
			supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.REVERSAL_RULE_DEL, supervisorEventParam);
		}
		
		result.code=1;
		result.msg="删除成功";
		
		renderJSON(result);
	}
	
	public static void showReversalRewardsPre(int currPage, int pageSize) {
		String id = params.get("id");
		String level = params.get("level");
		String period = params.get("period");
		
		PageBean<t_reversal_reward> pageBean = reversalRewardService.pageOfBackRewards(currPage, pageSize, id, level, period);
		
		render(pageBean, id, level, period);
	}
	
	public static void addReversalRewardPre() {
		render();
	}
	
	public static void addReversalReward(t_reversal_reward reward) {
		checkAuthenticity();
		flash.put("level", reward.level);
		flash.put("scale", reward.scale);
		flash.put("period", reward.period);
		
		ResultInfo result = new ResultInfo();
		
		result = reversalRewardService.insert(reward);
		
		if(result.code < 1){
			LoggerUtil.info(true,"保存翻牌奖励时：%s", result.msg);
			flash.error(result.msg);
			addReversalRulePre();
		}
		
		/** 添加管理员事件 */
		Map<String, String> param = new HashMap<String, String>();
		param.put("supervisor", "" + getCurrentSupervisorId());
		
		boolean flag = supervisorService.addSupervisorEvent(getCurrentSupervisorId(), t_event_supervisor.Event.REVERSAL_REWARD_ADD, param);
		
		if(!flag){
			flash.error("保存管理员事件失败");
			LoggerUtil.error(true, "保存管理员事件失败");
			showReversalRulesPre(1,10);
		}
		
		flash.success("保存翻牌奖励成功");
		showReversalRewardsPre(1,10);
	}
	
	public static void editReversalRewardPre(String sign) {
		ResultInfo result = Security.decodeSign(sign, Constants.MALL_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		
		if (result.code < 1) {
			renderJSON(result);
		}
		
		long id = Long.parseLong((String) result.obj);
		
		t_reversal_reward reward = reversalRewardService.findByID(id);
		
		render(reward);
	}
	
	public static void editReversalReward(t_reversal_reward reward) {
		checkAuthenticity();
		
		ResultInfo result = new ResultInfo();
		
		if(reward.id == null || reward.id == 0) {
			flash.error("该翻牌奖励不存在");
			showReversalRewardsPre(1, 10);
		}

		t_reversal_reward r = reversalRewardService.findByID(reward.id);
		
		if(r == null) {
			flash.error("该翻牌奖励不存在");
			showReversalRewardsPre(1, 10);
		}
		
		if(r.level != reward.level) {
			flash.error("规则等级不能修改");
			editReversalRewardPre(r.getSign());
		}
		
		reward.time = r.time;
		
		result = reversalRewardService.update(reward);
		
		if(result.code < 1){
			LoggerUtil.info(true,"修改翻牌奖励时：%s", result.msg);
			flash.error(result.msg);
			editReversalRewardPre(r.getSign());
		}
		
		/** 添加管理员事件 */
		Map<String, String> param = new HashMap<String, String>();
		param.put("supervisor", "" + getCurrentSupervisorId());
		
		boolean flag = supervisorService.addSupervisorEvent(getCurrentSupervisorId(), t_event_supervisor.Event.REVERSAL_REWARD_EDIT, param);
		
		if(!flag){
			flash.error("保存管理员事件失败");
			LoggerUtil.error(true, "保存管理员事件失败");
			showReversalRewardsPre(1,10);
		}
		
		flash.success("保存翻牌规则成功");
		showReversalRewardsPre(1,10);
	}
	
	public static void delReversalReward(String sign) {
		ResultInfo result = Security.decodeSign(sign, Constants.MALL_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		
		if (result.code < 1) {
			renderJSON(result);
		}
		
		long id = Long.parseLong((String) result.obj);
		
		t_reversal_reward reward = reversalRewardService.findByID(id);
		
		if(reward == null){
			result.code=-1;
			result.msg="该翻牌奖励不存在";
			renderJSON(result);
		}
		
		boolean delFlag = reversalRewardService.del(id);
		
		if(!delFlag){
			result.code=-1;
			result.msg="删除失败";
			renderJSON(result);
		}else{
			//管理员事件
			long supervisorId = getCurrentSupervisorId();
			Map<String, String> supervisorEventParam = new HashMap<String, String>(); 
			supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.REVERSAL_REWARD_DEL, supervisorEventParam);
		}
		
		result.code=1;
		result.msg="删除成功";
		
		renderJSON(result);
	}
	
	@SuppressWarnings("unchecked")
	public static void showReversalRecordsPre(int showType, int currPage, int pageSize) {
		String userName = params.get("userName");
		String awardMoney = params.get("awardMoney");
		int exports = Convert.strToInt(params.get("exports"), 0);
		
		if(showType < 0 || showType > 2) {
			showType = 0;
		}
		
		PageBean<t_reversal_record> pageBean = reversalRecordService.pageOfBackRecords(showType, currPage, pageSize, userName, awardMoney, exports);
		
		//导出
		if(exports == Constants.EXPORT){
			List<t_reversal_record> list = pageBean.page;
			 
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(Constants.DATE_TIME_FORMATE));
			jsonConfig.registerJsonValueProcessor(Double.class, new JsonDoubleValueProcessor(Constants.FINANCE_FORMATE_NORMAL));
			JSONArray arrList = JSONArray.fromObject(list, jsonConfig);
			
			for (Object obj : arrList) {
				JSONObject record = (JSONObject)obj;	
				
				if(record.getString("status") == "true"){
					record.put("status", "未发放");
				}else{
					record.put("status", "已发放");
				}
			}
			
			String fileName="翻牌记录";
			switch (showType) {
				case 1: 
					fileName = "翻牌未发放记录";
					break;
				case 2:
					fileName = "翻牌已发放记录";
					break;
				default:
					fileName = "翻牌记录";
					break;
			}
						
			File file = ExcelUtils.export(fileName,
					arrList,
					new String[] {
							"编号", "翻牌时间", "用户编号", "用户名", "投资编号", "投资金额", "标的期数", "奖励比例", "奖励金额", "状态", "发放时间"
					},
					new String[] {
							"id", "time", "user_id", "user_name", "invest_id", "invest_money", "scale", "award_money", "status", "delivery_time"
					}
				);
			   
			renderBinary(file, fileName+".xls");
		}
		
		render(pageBean, showType, userName, awardMoney);
	}
	
}