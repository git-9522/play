package daos.common;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import common.utils.PageBean;
import daos.base.BaseDao;
import models.common.entity.t_reversal_rule;

public class ReversalRuleDao extends BaseDao<t_reversal_rule> {

	public PageBean<t_reversal_rule> pageOfBackRewards(int currPage, int pageSize, String id, String level) {
		StringBuffer querySQL = new StringBuffer("select * from t_reversal_rule where 1=1 ");
		StringBuffer countSQL = new StringBuffer("select count(1) from t_reversal_rule where 1=1 ");
		
		Map<String, Object> args = new HashMap<String, Object>();
		
		/** 按编号搜索  */
		if(StringUtils.isNotBlank(id)){
			querySQL.append(" AND id = :id");
			countSQL.append(" AND id = :id");
			args.put("id", id.trim());
		}
		
		/** 按奖品名称搜索  */
		if(StringUtils.isNotBlank(level)){
			querySQL.append(" AND level = :level");
			countSQL.append(" AND level = :level");
			args.put("level", level);
		}
		
		querySQL.append(" order by id desc ");
		
		return this.pageOfBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), args);
	}

}