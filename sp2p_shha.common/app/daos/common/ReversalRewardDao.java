package daos.common;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import common.utils.PageBean;
import daos.base.BaseDao;
import models.common.entity.t_reversal_reward;

public class ReversalRewardDao extends BaseDao<t_reversal_reward> {

	public PageBean<t_reversal_reward> pageOfBackRewards(int currPage, int pageSize, String id, String level,
			String period) {
		StringBuffer querySQL = new StringBuffer("select * from t_reversal_reward where 1=1 ");
		StringBuffer countSQL = new StringBuffer("select count(1) from t_reversal_reward where 1=1 ");
		
		Map<String, Object> args = new HashMap<String, Object>();
		
		if(StringUtils.isNotBlank(id)){
			querySQL.append(" AND id = :id");
			countSQL.append(" AND id = :id");
			args.put("id", id.trim());
		}
		
		if(StringUtils.isNotBlank(level)){
			querySQL.append(" AND level = :level");
			countSQL.append(" AND level = :level");
			args.put("level", level);
		}

		if(StringUtils.isNotBlank(period)){
			querySQL.append(" AND period = :period");
			countSQL.append(" AND period = :period");
			args.put("period", period);
		}
		
		querySQL.append(" order by id desc ");
		
		return this.pageOfBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), args);
	}

}