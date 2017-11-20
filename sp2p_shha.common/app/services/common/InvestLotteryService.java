package services.common;

import java.util.Date;
import java.util.List;

import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import daos.common.InvestLotteryDao;
import models.common.bean.InvestLottery;
import models.common.entity.t_invest_lottery;
import models.common.entity.t_invest_reward;
import services.base.BaseService;

public class InvestLotteryService extends BaseService<t_invest_lottery> {
	
	protected InvestLotteryDao investLotteryDao = Factory.getDao(InvestLotteryDao.class);
	
	protected InvestLotteryService() {
		basedao = investLotteryDao;
	}

	public PageBean<InvestLottery> pageOfBackRewards(int currPage, int pageSize, String id, String name,
			String mobile, int exports) {
		return investLotteryDao.pageOfBackRewards(currPage, pageSize, id, name, mobile, exports);
	}

	public int calulateInvestLotteryInDates(long userId, Date startDate, Date endDate) {
		return investLotteryDao.calulateInvestLotteryInDates(userId, startDate, endDate);
	}

	public List<InvestLottery> queryLatestLotteries(int size) {
		return investLotteryDao.queryLatestLotteries(size);
	}

	public ResultInfo insert(t_invest_reward reward, long userId) {
		ResultInfo result = new ResultInfo();
		t_invest_lottery lottery = new t_invest_lottery();
		lottery.time = new Date();
		lottery.user_id = userId;
		lottery.name = reward.name;
		lottery.reward_id = reward.id;
		lottery.value = reward.value;
		lottery.status = false;
		
		if(investLotteryDao.save(lottery)) {
			result.code = 1;
			result.msg = "添加投资抽奖记录成功";
			result.obj = lottery;
		} else {
			result.code = -1;
			result.msg = "添加投资抽奖记录失败";
		}
		
		return result;
	}

	public List<t_invest_lottery> queryAllUnDelivery() {
		return findListByColumn("status = ?", false);
	}

	public boolean update(t_invest_lottery lottery) {
		return investLotteryDao.updateStatus(lottery);
	}

}