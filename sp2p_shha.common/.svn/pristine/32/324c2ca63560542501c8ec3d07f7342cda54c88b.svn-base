package services.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.utils.Factory;
import common.utils.ResultInfo;
import daos.common.SignInRuleDao;
import models.common.entity.t_sign_in_rule;
import services.base.BaseService;

/**
 * 签到规则service
 *
 * @author YanPengFei
 * @createDate 2017年2月24日
 */
public class SignInRuleService extends BaseService<t_sign_in_rule> {

	protected SignInRuleDao signInRuleDao = null;
	
	protected SignInRuleService() {
		signInRuleDao = Factory.getDao(SignInRuleDao.class);
		super.basedao = signInRuleDao;
	}
	
	/**
	 * 更新签到规则
	 *
	 * @param newRules map的key为签到的唯一标识，value为更新后的值
	 * @return
	 *
	 * @author YanPengFei
	 * @createDate 2017年2月24日
	 */
	public ResultInfo updateRules(Map<String, Double> newRules, Map<String, Object> map) {
		ResultInfo result = new ResultInfo();
		
		for (String key : newRules.keySet()) {
			t_sign_in_rule rule = signInRuleDao.findByColumn(" _key=? ", key);
			
			if (rule == null) {
				result.code = -2;
				result.msg = "没有找到对应的签到规则!";
			
				return result;
			}
			
			rule.score = newRules.get(key);
			rule.extra_score = Double.parseDouble(map.get("extra_score_" + key).toString());
			
			if (!signInRuleDao.save(rule)) {
				result.code = -3;
				result.msg = "更新失败!";
			
				return result;
			}
		}

		result.code = 1;
		result.msg = "更新成功!";
		
		return result;
	}
	
	/**
	 * 查询所有积分规则(map形式)
	 * 
	 * @return
	 */
	public Map<String, t_sign_in_rule> findAllRulesMap() {
		List<t_sign_in_rule> rulesList = signInRuleDao.findAll();
		
		if (rulesList == null || rulesList.size() <= 0) {
			
			return null;
		}
		
		Map<String, t_sign_in_rule> rulesMap = new HashMap<String, t_sign_in_rule>();
		
		for (t_sign_in_rule rule : rulesList) {
			rulesMap.put(rule._key, rule);
		}
		
		return rulesMap;
	}
	
}
