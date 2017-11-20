package controllers.back.mall;

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
import common.utils.file.FileUtil;
import controllers.common.BackBaseController;
import models.common.entity.t_event_supervisor;
import models.common.entity.t_event_supervisor.Event;
import models.ext.mall.entiey.t_mall_rewards;
import service.ext.mall.RewardsService;
/**
 * 积分商城-抽奖奖品
 *
 * @description 
 *
 * @author jiayijian
 * @createDate 2017年3月16日
 */
public class RewardsCtrl extends BackBaseController{
	
	protected static RewardsService rewardsService = Factory.getService(RewardsService.class);
	
	/**
	 * 积分商城-查询奖品
	 * 
	 * @rightID 1105001
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public static void showRewardsPre(int currPage, int pageSize) {
		
		int exports = Convert.strToInt(params.get("exports"), 0);
		
		String numNo = params.get("numNo");
		String rewardName = params.get("rewardName");
		
		
		PageBean<t_mall_rewards> pageBean = rewardsService.pageOfBackRewards(currPage, pageSize, numNo, rewardName, exports);
		
		
		render(pageBean,numNo,rewardName);
	}

	/**
	 * 添加奖品页面
	 * 
	 * @rightID 1105002
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public static void toAddRewardsPre(){
		
		render();
	}
	
	/**
	 * 添加奖品
	 * 
	 * @rightID 1105002
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public static void addRewards(t_mall_rewards rewards){
		checkAuthenticity();
		flash.put("name", rewards.name);
		flash.put("image_url", rewards.image_url);
		flash.put("type", rewards.type);
		flash.put("type_value", (Double)rewards.type_value);
		flash.put("min_invest_amount", rewards.min_invest_amount);
		flash.put("limit_day", rewards.limit_day);
		flash.put("probability", rewards.probability);
		
	
		ResultInfo result = new ResultInfo();
		
		result = rewardsService.checkRewardsValue(rewards);
		
		if(result.code < 1){
			
			LoggerUtil.info(false,"校验奖品参数时：%s", result.msg);
			flash.error(result.msg);
			toAddRewardsPre();
		}
		
		result = rewardsService.addReward(rewards);
		
		if(result.code < 1){
			
			LoggerUtil.info(true,"保存奖品参数时：%s", result.msg);
			flash.error(result.msg);
			toAddRewardsPre();
		}
		
		t_mall_rewards rd = (t_mall_rewards)result.obj;
		
		String _value = "0";
		boolean display = settingService.updateSetting(SettingKey.ACTIVITY_IS_USE, _value);
		if (!display) {
			result.msg = "数据没有更新";
			
			LoggerUtil.info(true,"关闭抽奖时：%s", result.msg);
			flash.error(result.msg);
			toAddRewardsPre();
		}
		
		/** 添加管理员事件 */
		Map<String, String>param = new HashMap<String, String>();
		param.put("supervisor", ""+getCurrentSupervisorId());
		param.put("rewardId", ""+rd.id);
		param.put("rewardName", rd.name);
		
		boolean flag = supervisorService.addSupervisorEvent(getCurrentSupervisorId(), t_event_supervisor.Event.REWARDS_ADD, param);
		
		if(!flag){
			
			flash.error("保存管理员事件失败");
			LoggerUtil.error(true, "保存管理员事件失败");
			showRewardsPre(1,10);
		}
		
		
		flash.success("保存奖品成功");
		showRewardsPre(1,10);
	}
	
	/**
	 * 编辑奖品页面
	 * 
	 * @rightID 1105003
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public static void toEditRewardsPre(long rewardId){
		
		if(rewardId < 0){
			flash.error("奖品ID参数错误");
			
			showRewardsPre(1,10);
		}
		
		t_mall_rewards reward = rewardsService.findByID(rewardId);
		
		if(reward == null){
			flash.error("没有找到该奖品");
			
			showRewardsPre(1,10);
		}
		render(reward);
	}
		
	/**
	 * 编辑奖品
	 * 
	 * @rightID 1105003
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public static void editRewards(t_mall_rewards rewards,long rewardId){
		checkAuthenticity();
		
		ResultInfo result = new ResultInfo();
		if(rewardId < 0){
			flash.error("奖品ID参数错误");
			
			toEditRewardsPre(rewardId);
		}
		
		t_mall_rewards rd = rewardsService.findByID(rewardId);
		
		if(rd == null){
			flash.error("没有找到该奖品");
			
			toEditRewardsPre(rewardId);
		}
		
		result = rewardsService.checkRewardsValue(rewards);
		
		if(result.code < 1){
			
			LoggerUtil.info(false,"校验奖品参数时：%s", result.msg);
			flash.error(result.msg);
			toEditRewardsPre(rewardId);
		}

		result = rewardsService.editRewards(rewards, rd);
		
		if(result.code < 1){
			
			LoggerUtil.info(true,"保存奖品参数时：%s", result.msg);
			flash.error(result.msg);
			toEditRewardsPre(rewardId);
		}
		
		String _value = "0";
		boolean display = settingService.updateSetting(SettingKey.ACTIVITY_IS_USE, _value);
		if (!display) {
			result.msg = "数据没有更新";
			
			LoggerUtil.info(true,"关闭抽奖时：%s", result.msg);
			flash.error(result.msg);
			toEditRewardsPre(rewardId);
		}
		
		/** 添加管理员事件 */
		Map<String, String>param = new HashMap<String, String>();
		param.put("supervisor", ""+getCurrentSupervisorId());
		param.put("rewardId", ""+rd.id);
		param.put("rewardName", rd.name);
		boolean flag = supervisorService.addSupervisorEvent(getCurrentSupervisorId(), t_event_supervisor.Event.REWARDS_EDIT, param);
		
		if(!flag){
			
			flash.error("保存管理员事件失败");
			LoggerUtil.error(true, "保存管理员事件失败");
			toEditRewardsPre(rewardId);
		}
		
		flash.success(result.msg);
		showRewardsPre(1,10);
	}
	
	/**
	 * 更新奖品状态
	 *
	 * @param rewardId
	 * @param rewardName 奖品名称，用于添加管理员事件时使用
	 * @param isUse true-当前状态为上架；false-当前状态为下架
	 *
	 * @author yaoyi
	 * @createDate 2015年12月16日
	 */
	public static void updateGoodsStatus(long rewardId, String rewardName, boolean isUse){
		ResultInfo res = new ResultInfo();
		
		if(rewardId < 1){
			res.code = -1;
			res.msg = "参数错误";
			
			renderJSON(res);
		}
		
		Event ev = isUse ? Event.REWARDS_DISABLED : Event.REWARDS_ENABLE;
		
		boolean upd= rewardsService.updateRewardsStatus(rewardId, !isUse);
		if(!upd){
			LoggerUtil.error(true, "更新奖品上下架状态失败!");
			
			res.code = -1;
			res.msg = "更新奖品上下架状态失败!";
			renderJSON(res);
		}
		
		/** 添加管理员事件 */
		Map<String, String>param = new HashMap<String, String>();
		param.put("supervisor", ""+getCurrentSupervisorId());
		param.put("rewardId", ""+rewardId);
		param.put("rewardName", rewardName);
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
	
	/**
	 * 奖品 删除
	 * @rightID 1105005
	 *
	 * @param sign 资讯加密id
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月29日
	 */
	public static void delRewards(String sign){
		ResultInfo result = Security.decodeSign(sign, Constants.MALL_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			renderJSON(result);
		}
		long rewardId = Long.parseLong((String) result.obj);
		t_mall_rewards rd = rewardsService.findByID(rewardId);
		
		if(rd == null){
			
			result.code=-1;
			result.msg="该奖品不存在";
			
			renderJSON(result);
		}
		
		boolean delFlag = rewardsService.delRewards(rewardId);
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
	
	/**
	 * 编辑抽奖规则页面
	 * 
	 * @rightID 1105006
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public static void toEditLotteryRulePre(){
		
		//基本信息
		int activity_is_use = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.ACTIVITY_IS_USE), 0);
		String activity_name = settingService.findSettingValueByKey(SettingKey.ACTIVITY_NAME);
		String activity_start_time = settingService.findSettingValueByKey(SettingKey.ACTIVITY_START_TIME);
		String activity_end_time = settingService.findSettingValueByKey(SettingKey.ACTIVITY_END_TIME);
		String expend_score = settingService.findSettingValueByKey(SettingKey.EXPEND_SCORE);
		String daily_lottery_number = settingService.findSettingValueByKey(SettingKey.DAILY_LOTTERY_NUMBER);
		String activity_rule = settingService.findSettingValueByKey(SettingKey.ACTIVITY_RULE);
		
		renderArgs.put("activity_is_use", activity_is_use);
		renderArgs.put("activity_name", activity_name);
		renderArgs.put("activity_start_time", activity_start_time);
		renderArgs.put("activity_end_time", activity_end_time);
		renderArgs.put("expend_score", expend_score);
		renderArgs.put("daily_lottery_number", daily_lottery_number);
		renderArgs.put("activity_rule", activity_rule);
		
		render();
	}
	
	/**
	 * 编辑抽奖规则页面
	 * 
	 * @rightID 1105006
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public static void editLotteryRule(){
		
		checkAuthenticity();
		
		String activity_name = params.get("activity_name");
		if (StringUtils.isBlank(activity_name)) {
			flash.error("请输入活动名称");

			toEditLotteryRulePre();
		}
		
		String expend_score = params.get("expend_score");
		if (StringUtils.isBlank(expend_score)) {
			flash.error("请输入抽奖积分");

			toEditLotteryRulePre();
		}
		
		if(Integer.valueOf(expend_score) <= 0){
			flash.error("抽奖积分应大于0");

			toEditLotteryRulePre();
		}
		
		String daily_lottery_number = params.get("daily_lottery_number");
		if (StringUtils.isBlank(daily_lottery_number)) {
			flash.error("请输入每日抽奖次数");

			toEditLotteryRulePre();
		}
		
		if(Integer.valueOf(daily_lottery_number) <= 0){
			flash.error("每日抽奖次数应大于0");

			toEditLotteryRulePre();
		}
		
		String activity_rule = params.get("activity_rule");
		if (StringUtils.isBlank(activity_rule)) {
			flash.error("请输入活动规则");

			toEditLotteryRulePre();
		}
		
		String activity_start_time = params.get("activity_start_time");
		String activity_end_time = params.get("activity_end_time");
		if (StringUtils.isBlank(activity_start_time) || StringUtils.isBlank(activity_end_time)) {
			flash.error("请输入活动时间");

			toEditLotteryRulePre();
		}
		
		Date startTime = DateUtil.strToDate(activity_start_time, Constants.DATE_PLUGIN);
		Date endTime = DateUtil.strToDate(activity_end_time, Constants.DATE_PLUGIN);
		
		if(!DateUtil.isDateAfter(endTime, startTime)){
			flash.error("活动开始时间小于结束时间");
			
			toEditLotteryRulePre();
		}
		
		Map<String, String> map = new HashMap<String, String>();
		map.put(SettingKey.ACTIVITY_NAME, params.get("activity_name"));
		map.put(SettingKey.ACTIVITY_START_TIME, params.get("activity_start_time"));
		map.put(SettingKey.ACTIVITY_END_TIME, params.get("activity_end_time"));
		map.put(SettingKey.EXPEND_SCORE, params.get("expend_score"));
		map.put(SettingKey.DAILY_LOTTERY_NUMBER, params.get("daily_lottery_number"));
		map.put(SettingKey.ACTIVITY_RULE, params.get("activity_rule"));
		boolean flag = settingService.updateSettings(map);
		if (!flag) {

			flash.error("抽奖规则更新失败，请稍后再试");
			toEditLotteryRulePre();
		}
		
		long supervisorId = getCurrentSupervisorId();
		supervisorService.addSupervisorEvent(supervisorId, Event.LOTTERY_RULE_EDIT, null);
		
		flash.success("抽奖规则保存成功");
		
		toEditLotteryRulePre();
	
	}
	
	
	/**
	 * 上传商品图片
	 * 
	 * @param imgFile
	 * @param fileName
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年4月5日
	 */
	@SuppressWarnings("unchecked")
	public static void uploadRewardsImage(File imgFile, String fileName){
		response.contentType="text/html";
		ResultInfo result = new ResultInfo();
		if (imgFile == null) {
			result.code = -1;
			result.msg = "请选择要上传的图片";
			
			renderJSON(result);
		}
		if(StringUtils.isBlank(fileName) || fileName.length() > 32){
			result.code = -1;
			result.msg = "图片名称长度应该位于1~32位之间";
			
			renderJSON(result);
		}
		
		result = FileUtil.uploadImgags(imgFile);
		if (result.code < 0) {

			renderJSON(result);
		}
		
		Map<String, Object> fileInfo = (Map<String, Object>) result.obj;
		fileInfo.put("imgName", fileName);
		
		renderJSON(result);
	}
	
	/**
	 * 更改"是否开启抽奖"系统参数
	 *
	 * @param flag ture显示(1),false不显示0
	 * @return
	 *
	 * @author jiayijian
	 * @createDate 2016年3月26日
	 */
	public static void updateIsLotteryShow(boolean flag) {
		ResultInfo result = new ResultInfo();
		
		String _value = "0";
		if (flag) {
			_value = "1";
		}
		
		//开启条件：概率总和等于100%
		if(flag){
			double totalProbability = rewardsService.queryTotalProbability();
			
			if(totalProbability != 100){
				
				result.msg = "概率总和等于100%才能开启";
				
				renderJSON(result);
			}
			
			//最新上架的8个奖品
			List<t_mall_rewards> rewardList =  rewardsService.queryFrontNewRewarsIsUse();
			
			if(rewardList == null || rewardList.size() != 8){
				
				result.msg = "需要8个奖品才能开启";
				
				renderJSON(result);
			}
		}
		
		boolean display = settingService.updateSetting(SettingKey.ACTIVITY_IS_USE, _value);
		if (!display) {
			result.msg = "数据没有更新";
			
			renderJSON(result);
		}
		
		result.code = 1;
		result.msg = "抽奖状态更改成功";
		
		long supervisor_id = getCurrentSupervisorId();
		supervisorService.addSupervisorEvent(supervisor_id, Event.LOTTERY_RULE_EDIT, null);
		
		renderJSON(result);
	}
}
