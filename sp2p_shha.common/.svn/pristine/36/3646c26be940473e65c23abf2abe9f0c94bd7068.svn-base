package daos.common;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;

import com.shove.Convert;

import common.constants.Constants;
import common.utils.PageBean;
import daos.base.BaseDao;
import models.common.entity.t_invest_reward;
import play.db.jpa.JPA;

public class InvestRewardDao extends BaseDao<t_invest_reward> {

	protected InvestRewardDao() {
		
	}
	
	public int queryRewardCount(boolean isUse) {
		return super.countByColumn(" is_use = ?", isUse);
	}
	
	public double queryRewardProbability(boolean isUse) {
		String sql = "SELECT SUM(probability) FROM t_invest_reward WHERE is_use = ?";
		Query query = JPA.em().createNativeQuery(sql);
		query.setParameter(1, isUse);
		Object obj = query.getSingleResult();
		if(obj == null) {
			return 0;
		} else {
			return Convert.strToDouble(obj.toString(), 0);
		}
	}

	public PageBean<t_invest_reward> pageOfBackRewards(int currPage, int pageSize, String numNo, String rewardName,
			int exports) {
		StringBuffer querySQL = new StringBuffer("select * from t_invest_reward where 1=1 ");
		StringBuffer countSQL = new StringBuffer("select count(id) from t_invest_reward where 1=1 ");
		
		Map<String, Object> args = new HashMap<String, Object>();
		
		/** 按编号搜索  */
		if(StringUtils.isNotBlank(numNo)){
			querySQL.append(" AND id = :rewardsId");
			countSQL.append(" AND id = :rewardsId");
			args.put("rewardsId", numNo.trim());
		}
		
		/** 按奖品名称搜索  */
		if(StringUtils.isNotBlank(rewardName)){
			querySQL.append(" AND name LIKE :name");
			countSQL.append(" AND name LIKE :name");
			args.put("name", "%"+rewardName.trim()+"%");
		}
		
		querySQL.append(" order by id desc ");
		
		if(exports == Constants.EXPORT){
			PageBean<t_invest_reward> pageBean = new PageBean<t_invest_reward>();
			pageBean.page = this.findListBySQL(querySQL.toString(), args);
		    
		    return pageBean;
		}
		
		return this.pageOfBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), args);
	}

	public int updateRewardsStatus(long id, boolean isUse) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("is_use", isUse);
		map.put("id", id);
		return this.updateBySQL("UPDATE t_invest_reward SET is_use = :is_use WHERE id = :id", map);
	}
	
}