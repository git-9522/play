package controllers.back.mall;

import java.util.HashMap;
import java.util.Map;

import common.constants.SettingKey;
import common.utils.StrUtil;
import controllers.common.BackBaseController;
import models.common.entity.t_event_supervisor.Event;

/**
 * 后台-积分商城-规则设置
 *
 * @description 
 *
 * @author jiayijian
 * @createDate 2017年3月16日
 */
public class MallSettingCtrl extends BackBaseController{
	
	
	
	/**
	 * 积分商城-获取积分规则
	 * 
	 * @rightID 1101001
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public static void showGainScorePre() {
		
		String register_score = settingService.findSettingValueByKey(SettingKey.REGISTER_SCORE);
		String bind_card_score = settingService.findSettingValueByKey(SettingKey.BIND_CARD_SCORE);
		String first_recharge = settingService.findSettingValueByKey(SettingKey.FIRST_RECHARGE);
		String first_invest = settingService.findSettingValueByKey(SettingKey.FIRST_INVEST);
		String bind_mailbox_score = settingService.findSettingValueByKey(SettingKey.BIND_MAILBOX_SCORE);
		String bind_wechat_score = settingService.findSettingValueByKey(SettingKey.BIND_WECHAT_SCORE);
		
		renderArgs.put("register_score", register_score);
		renderArgs.put("bind_card_score", bind_card_score);
		renderArgs.put("first_recharge", first_recharge);
		renderArgs.put("first_invest", first_invest);
		renderArgs.put("bind_mailbox_score", bind_mailbox_score);
		renderArgs.put("bind_wechat_score", bind_wechat_score);
		
		render();
	}
	
	/**
	 * 编辑获取积分规则
	 * 
	 * @rightID 1101003
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public static void editGainScore(){
		checkAuthenticity();
		
		String register_score = params.get("register_score");
		String bind_card_score = params.get("bind_card_score");
		String first_recharge = params.get("first_recharge");
		String first_invest = params.get("first_invest");
		String bind_mailbox_score = params.get("bind_mailbox_score");
		String bind_wechat_score = params.get("bind_wechat_score");
		
		if ((!StrUtil.isNumericPositiveInt(register_score)) || StrUtil.isNumLess(register_score, 0) || StrUtil.isNumMore(register_score, 10000)) {
			flash.error("注册成功 获得积分不正确");
			showGainScorePre();
		}
		
		if ((!StrUtil.isNumericPositiveInt(bind_card_score)) || StrUtil.isNumLess(bind_card_score, 0) || StrUtil.isNumMore(bind_card_score, 10000)) {
			flash.error("绑卡成功 获得积分不正确");
			showGainScorePre();
		}

		if ((!StrUtil.isNumericPositiveInt(first_recharge)) || StrUtil.isNumLess(first_recharge, 0) || StrUtil.isNumMore(first_recharge, 10000)) {
			flash.error("首次充值 获得积分不正确");
			showGainScorePre();
		}
		
		if ((!StrUtil.isNumericPositiveInt(first_invest)) || StrUtil.isNumLess(first_invest, 0) || StrUtil.isNumMore(first_invest, 10000)) {
			flash.error("首次投资 获得积分不正确");
			showGainScorePre();
		}
		
		if ((!StrUtil.isNumericPositiveInt(bind_mailbox_score)) || StrUtil.isNumLess(bind_mailbox_score, 0) || StrUtil.isNumMore(bind_mailbox_score, 10000)) {
			flash.error("绑定邮箱 获得积分不正确");
			showGainScorePre();
		}
		
		if ((!StrUtil.isNumericPositiveInt(bind_wechat_score)) || StrUtil.isNumLess(bind_wechat_score, 0) || StrUtil.isNumMore(bind_wechat_score, 10000)) {
			flash.error("绑定微信 获得积分不正确");
			showGainScorePre();
		}
		
		Map<String, String> infos = new HashMap<String, String>();
		infos.put(SettingKey.REGISTER_SCORE, register_score);
		infos.put(SettingKey.BIND_CARD_SCORE, bind_card_score);
		infos.put(SettingKey.FIRST_RECHARGE, first_recharge);
		infos.put(SettingKey.FIRST_INVEST, first_invest);
		infos.put(SettingKey.BIND_MAILBOX_SCORE, bind_mailbox_score);
		infos.put(SettingKey.BIND_WECHAT_SCORE, bind_wechat_score);
		
		boolean flag = settingService.updateSettings(infos);
		if (!flag) {

			flash.error("获取积分规则更新失败，请稍后再试");
			showGainScorePre();
		}
		
		long supervisorId = getCurrentSupervisorId();
		supervisorService.addSupervisorEvent(supervisorId, Event.GAIN_SCORE_SET, null);
		
		flash.success("获取积分规则保存成功");
		showGainScorePre();
	}
	
	/**
	 * 积分商城-投资赚积分规则
	 * 
	 * @rightID 1101001
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public static void showInvestGainScorePre() {
		
		String invest_score = settingService.findSettingValueByKey(SettingKey.INVEST_SCORE);
		renderArgs.put("invest_score", invest_score);
		
		render();
	}
	
	
	
	/**
	 * 编辑投资赚积分规则
	 * 
	 * @rightID 1101004
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public static void editInvestGainScore(){
		checkAuthenticity();
		
		String invest_score = params.get("invest_score");
		if ((!StrUtil.isNumericPositiveInt(invest_score)) || StrUtil.isNumLess(invest_score, 0) || StrUtil.isNumMore(invest_score, 10000)) {
			flash.error("投资赚积分不正确");
			showInvestGainScorePre();
		}
		
		Map<String, String> infos = new HashMap<String, String>();
		infos.put(SettingKey.INVEST_SCORE, params.get("invest_score"));
		
		boolean flag = settingService.updateSettings(infos);
		if (!flag) {

			flash.error("投资赚积分规更新失败，请稍后再试");
			showInvestGainScorePre();
		}
		
		long supervisorId = getCurrentSupervisorId();
		supervisorService.addSupervisorEvent(supervisorId, Event.INVEST_GAIN_SCORE_SET, null);
		
		flash.success("投资赚积分规保存成功");
		showInvestGainScorePre();
		
	}

}
