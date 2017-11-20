package services.common;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import daos.common.ReversalRuleDao;
import models.common.entity.t_reversal_rule;
import services.base.BaseService;

public class ReversalRuleService extends BaseService<t_reversal_rule> {

	protected static ReversalRuleDao reversalRuleDao = Factory.getDao(ReversalRuleDao.class);
	
	protected ReversalRuleService(){
		super.basedao = reversalRuleDao;
	}

	public PageBean<t_reversal_rule> pageOfBackRules(int currPage, int pageSize, String id, String level) {
		return reversalRuleDao.pageOfBackRewards(currPage, pageSize, id, level);
	}

	public ResultInfo insert(t_reversal_rule rule) {
		ResultInfo result = new ResultInfo();
		rule.time = new Date();
		if(reversalRuleDao.save(rule)) {
			result.code = 1;
		} else {
			result.code = -1;
			result.msg = "保存翻牌规则失败";
		}
		return result;
	}

	public boolean del(long id) {
		return reversalRuleDao.delete(id) != -99;
	}

	public ResultInfo check(t_reversal_rule rule) {
		ResultInfo result = new ResultInfo();
		
		if(rule.id != null) {
			t_reversal_rule r = findByID(rule.id);
			if(r == null) {
				result.code = -100;
				result.msg = "该翻盘概率不存在";
				return result;
			}
			if(r.level != rule.level) {
				result.code = -1;
				result.msg = "不能修改规则水平";
				return result;
			}
			rule.time = r.time;
		}
		
		if(rule.probability < 0 || rule.probability > 100) {
			result.code = -1;
			result.msg = "中奖概率应该在 0 - 100之间";
			return result;
		}
		
		if(rule.min != -1 && rule.max != -1) {
			if(rule.min > rule.max) {
				result.code = -1;
				result.msg = "最小投资金额必须大于最大投资金额";
				return result;
			}
		}
		
		result.code = 1;
		return result;
	}

	public ResultInfo update(t_reversal_rule rule) {
		ResultInfo result = new ResultInfo();
		if(reversalRuleDao.save(rule)) {
			result.code = 1;
		} else {
			result.code = -1;
			result.msg = "更新翻牌规则失败";
		}
		return result;
	}

	public List<Map<String, Object>> findAllProbability() {
		String sql = "SELECT SUM(probability) AS probability, min FROM t_reversal_rule GROUP BY (min)";
		return reversalRuleDao.findListMapBySQL(sql, null);
	}

	public List<t_reversal_rule> findByAmout(double amount) {
		String sql = "SELECT * FROM t_reversal_rule WHERE min <= :min AND (max >= :max OR max = -1)";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("min", amount);
		map.put("max", amount);
		return reversalRuleDao.findListBySQL(sql, map);
	}
	
}