package controllers.back.mall;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.shove.Convert;

import common.constants.Constants;
import common.constants.ext.SignInKey;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.StrUtil;
import common.utils.excel.ExcelUtils;
import common.utils.jsonAxml.JsonDateValueProcessor;
import common.utils.jsonAxml.JsonDoubleValueProcessor;
import controllers.common.BackBaseController;
import models.common.bean.ScoreRecord;
import models.common.entity.t_event_supervisor.Event;
import models.common.entity.t_score_user;
import models.common.entity.t_sign_in_rule;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import services.common.ScoreUserService;
import services.common.SignInRuleService;

public class ScoreCtrl extends BackBaseController {

	protected static SignInRuleService signInRuleService = Factory.getService(SignInRuleService.class);
	
	protected static ScoreUserService scoreUserService = Factory.getService(ScoreUserService.class);
	
	
	/**
	 * 后台-积分商城-积分规则
	 * 
	 * @rightID 1101001
	 * 
	 * @author YanPengFei
	 * @createDate 2017年2月24日
	 */
	public static void showSignInRulesPre() {
		List<t_sign_in_rule> ruleList = signInRuleService.findAll();
		
		if (ruleList == null || ruleList.size() <= 0) {
			error500();
		}
		
		Map<String, t_sign_in_rule> ruleMap = new HashMap<String, t_sign_in_rule>();
		
		for (t_sign_in_rule rule : ruleList) {
			ruleMap.put(rule._key, rule);
		}
		
		render(ruleList, ruleMap);
	}
	
	/**
	 * 后台-积分商城-编辑积分规则
	 * 
	 * @rightID 1101002
	 * 
	 * @author YanPengFei
	 * @createDate 2017年2月24日
	 */
	public static void editSignInRules() {
		checkAuthenticity();
		
		String score_one = params.get("score_" + SignInKey.ONE);
		String score_two = params.get("score_" + SignInKey.TWO);
		String score_three = params.get("score_" + SignInKey.THREE);
		String score_four = params.get("score_" + SignInKey.FOUR);
		String score_five = params.get("score_" + SignInKey.FIVE);
		String score_six = params.get("score_" + SignInKey.SIX);
		String score_seven = params.get("score_" + SignInKey.SEVEN);
		
		String extra_score_one = params.get("extra_score_" + SignInKey.ONE);
		String extra_score_two = params.get("extra_score_" + SignInKey.TWO);
		String extra_score_three = params.get("extra_score_" + SignInKey.THREE);
		String extra_score_four = params.get("extra_score_" + SignInKey.FOUR);
		String extra_score_five = params.get("extra_score_" + SignInKey.FIVE);
		String extra_score_six = params.get("extra_score_" + SignInKey.SIX);
		String extra_score_seven = params.get("extra_score_" + SignInKey.SEVEN);
		
		if ((!StrUtil.isOneDouble(score_one)) || StrUtil.isNumLess(score_one, 0) || StrUtil.isNumMore(score_one, 1000)) {
			flash.error("连续签到一天赠送积分不正确");
			showSignInRulesPre();
		}
		
		if ((!StrUtil.isOneDouble(extra_score_one)) || StrUtil.isNumLess(extra_score_one, 0) || StrUtil.isNumMore(extra_score_one, 1000)) {
			flash.error("连续签到一天额外加成积分不正确");
			showSignInRulesPre();
		}
		
		
		if ((!StrUtil.isOneDouble(score_two)) || StrUtil.isNumLess(score_two, 0) || StrUtil.isNumMore(score_two, 1000)) {
			flash.error("连续签到两天赠送积分不正确");
			showSignInRulesPre();
		}
		
		if ((!StrUtil.isOneDouble(extra_score_two)) || StrUtil.isNumLess(extra_score_two, 0) || StrUtil.isNumMore(extra_score_two, 1000)) {
			flash.error("连续签到两天额外加成积分不正确");
			showSignInRulesPre();
		}
		
		
		if ((!StrUtil.isOneDouble(score_three)) || StrUtil.isNumLess(score_three, 0) || StrUtil.isNumMore(score_three, 1000)) {
			flash.error("连续签到三天赠送积分不正确");
			showSignInRulesPre();
		}
		
		if ((!StrUtil.isOneDouble(extra_score_three)) || StrUtil.isNumLess(extra_score_three, 0) || StrUtil.isNumMore(extra_score_three, 1000)) {
			flash.error("连续签到三天额外加成积分不正确");
			showSignInRulesPre();
		}
		
		
		if ((!StrUtil.isOneDouble(score_four)) || StrUtil.isNumLess(score_four, 0) || StrUtil.isNumMore(score_four, 1000)) {
			flash.error("连续签到四天赠送积分不正确");
			showSignInRulesPre();
		}
		
		if ((!StrUtil.isOneDouble(extra_score_four)) || StrUtil.isNumLess(extra_score_four, 0) || StrUtil.isNumMore(extra_score_four, 1000)) {
			flash.error("连续签到四天额外加成积分不正确");
			showSignInRulesPre();
		}
		
		
		if ((!StrUtil.isOneDouble(score_five)) || StrUtil.isNumLess(score_five, 0) || StrUtil.isNumMore(score_five, 1000)) {
			flash.error("连续签到五天赠送积分不正确");
			showSignInRulesPre();
		}
		
		if ((!StrUtil.isOneDouble(extra_score_five)) || StrUtil.isNumLess(extra_score_five, 0) || StrUtil.isNumMore(extra_score_five, 1000)) {
			flash.error("连续签到五天额外加成积分不正确");
			showSignInRulesPre();
		}
		
		
		if ((!StrUtil.isOneDouble(score_six)) || StrUtil.isNumLess(score_six, 0) || StrUtil.isNumMore(score_six, 1000)) {
			flash.error("连续签到六天赠送积分不正确");
			showSignInRulesPre();
		}
		
		if ((!StrUtil.isOneDouble(extra_score_six)) || StrUtil.isNumLess(extra_score_six, 0) || StrUtil.isNumMore(extra_score_six, 1000)) {
			flash.error("连续签到六天额外加成积分不正确");
			showSignInRulesPre();
		}
		
		
		if ((!StrUtil.isOneDouble(score_seven)) || StrUtil.isNumLess(score_seven, 0) || StrUtil.isNumMore(score_seven, 1000)) {
			flash.error("连续签到七天赠送积分不正确");
			showSignInRulesPre();
		}
		
		if ((!StrUtil.isOneDouble(extra_score_seven)) || StrUtil.isNumLess(extra_score_seven, 0) || StrUtil.isNumMore(extra_score_seven, 1000)) {
			flash.error("连续签到七天额外加成积分不正确");
			showSignInRulesPre();
		}
		
		Map<String, Double> newRules = new HashMap<String, Double>();
		newRules.put(SignInKey.ONE, Double.valueOf(score_one));
		newRules.put(SignInKey.TWO, Double.valueOf(score_two));
		newRules.put(SignInKey.THREE, Double.valueOf(score_three));
		newRules.put(SignInKey.FOUR, Double.valueOf(score_four));
		newRules.put(SignInKey.FIVE, Double.valueOf(score_five));
		newRules.put(SignInKey.SIX, Double.valueOf(score_six));
		newRules.put(SignInKey.SEVEN, Double.valueOf(score_seven));

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("extra_score_" + SignInKey.ONE, extra_score_one);
		map.put("extra_score_" + SignInKey.TWO, extra_score_two);
		map.put("extra_score_" + SignInKey.THREE, extra_score_three);
		map.put("extra_score_" + SignInKey.FOUR, extra_score_four);
		map.put("extra_score_" + SignInKey.FIVE, extra_score_five);
		map.put("extra_score_" + SignInKey.SIX, extra_score_six);
		map.put("extra_score_" + SignInKey.SEVEN, extra_score_seven);

		ResultInfo result = signInRuleService.updateRules(newRules, map);
		
		if (result.code < 1) {
			LoggerUtil.info(true, "更新签到规则出错,数据回滚");
			flash.error("更新签到规则失败");
			showSignInRulesPre();
		} 

		Long supervisorId = getCurrentSupervisorId();
		supervisorService.addSupervisorEvent(supervisorId, Event.SIGN_IN_RULE_SET, null);

		flash.success("更新签到规则成功");
		showSignInRulesPre();
	}
	
	/**
	 * 后台-积分商城-积分记录
	 *
	 * @rightID 1102001
	 *
	 * @author YanPengFei
	 * @createDate 2017年02月24日
	 */
	@SuppressWarnings("unchecked")
	public static void showScoreRecordPre(int showType, int currPage, int pageSize) {
		int exports = Convert.strToInt(params.get("exports"), 0);
		String userName = params.get("userName");
		
		if (showType < 0 || showType > 2) {
			showType = 0;
		}
		
		//排序栏目
		String orderTypeStr = params.get("orderType");
		int orderType = Convert.strToInt(orderTypeStr, 0);
		
		if (orderType != 0) {
			orderType = 0;
		}
		
		renderArgs.put("orderType", orderType);
		
		//排序规则
		String orderValueStr = params.get("orderValue");
		int orderValue = Convert.strToInt(orderValueStr, 0); //0.降序、1.升序
		
		if (orderValue < 0 || orderValue > 1) {
			orderValue = 0;
		}
		
		renderArgs.put("orderValue", orderValue);
		
		PageBean<ScoreRecord> pageBean = scoreUserService.queryScoreRecordList(showType, currPage, pageSize, exports, userName, orderType, orderValue);
		
		//导出
		if (exports == Constants.EXPORT) {
			List<ScoreRecord> list = pageBean.page;
			 
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(Constants.DATE_PLUGIN));
			jsonConfig.registerJsonValueProcessor(Double.class, new JsonDoubleValueProcessor(Constants.NUMBER_FORMATE_NO_DECIMAL_POINT));
			JSONArray arrList = JSONArray.fromObject(list, jsonConfig);
			
			for (Object obj : arrList) {
				JSONObject jsonObject = (JSONObject) obj;
				jsonObject.put("deal_type", t_score_user.DealType.valueOf(jsonObject.get("deal_type").toString()).value);
			}
			
			String fileName = "积分记录";
			File file = ExcelUtils.export(fileName, 
					arrList, 
					new String[] {
					"序号", "用户名", "积分场景", "发生时间", "科目明细", "积分"},
					new String[] {
					"id", "user_name", "deal_type", "time", "summary", "score"});
					
			renderBinary(file, fileName + ".xls");
		}
		
		render(pageBean, userName);
	}
	
}
