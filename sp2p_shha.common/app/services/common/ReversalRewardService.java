package services.common;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import daos.common.ReversalRewardDao;
import models.common.entity.t_reversal_reward;
import services.base.BaseService;

public class ReversalRewardService extends BaseService<t_reversal_reward> {

	protected static ReversalRewardDao reversalRewardDao = Factory.getDao(ReversalRewardDao.class);
	
	protected ReversalRewardService(){
		super.basedao = reversalRewardDao;
	}

	public List<t_reversal_reward> queryByLevel(int level) {
		return reversalRewardDao.findListByColumn("level = ?", level);
	}

	public PageBean<t_reversal_reward> pageOfBackRewards(int currPage, int pageSize, String id, String level,
			String period) {
		return reversalRewardDao.pageOfBackRewards(currPage, pageSize, id, level, period);
	}

	public ResultInfo insert(t_reversal_reward reward) {
		ResultInfo result = new ResultInfo();
		reward.time = new Date();
		boolean flag = reversalRewardDao.save(reward);
		if(flag) {
			result.code = 1;
			result.msg = "保存翻牌奖励成功";
		} else {
			result.code = -1;
			result.msg = "保存翻牌奖励失败";
		}
		return result;
	}

	public ResultInfo update(t_reversal_reward reward) {
		ResultInfo result = new ResultInfo();
		boolean flag = reversalRewardDao.save(reward);
		if(flag) {
			result.code = 1;
			result.msg = "修改翻牌奖励成功";
		} else {
			result.code = -1;
			result.msg = "修改翻牌奖励失败";
		}
		return result;
	}

	public boolean del(long id) {
		return reversalRewardDao.delete(id) != -99;
	}

	public t_reversal_reward findByLevelAndPeriod(int level, int period) {
		String sql = "SELECT * FROM t_reversal_reward WHERE level = :level AND period = :period";
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("level", level);
		args.put("period", period);
		return reversalRewardDao.findBySQL(sql, args);
	}
	
}