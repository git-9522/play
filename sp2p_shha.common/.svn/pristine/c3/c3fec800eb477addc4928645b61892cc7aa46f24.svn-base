package services.common;

import java.util.List;

import common.utils.Factory;
import common.utils.ResultInfo;
import daos.common.MoonCakeLotteryRecordDao;
import models.common.entity.t_moon_cake_lottery_record;
import services.base.BaseService;

public class MoonCakeLotteryRecordService extends BaseService<t_moon_cake_lottery_record> {
	
	protected static MoonCakeLotteryRecordDao moonCakeLotteryRecordDao = Factory.getDao(MoonCakeLotteryRecordDao.class);
	
	protected MoonCakeLotteryRecordService() {
		super.basedao = moonCakeLotteryRecordDao;
	}

	public int calulateLotterySizeWithPeriod(long userId, int period) {
		return moonCakeLotteryRecordDao.calulateLotterySizeWithPeriod(userId, period);
	}

	public ResultInfo insert(t_moon_cake_lottery_record record) {
		ResultInfo result = new ResultInfo();
		if(moonCakeLotteryRecordDao.save(record)) {
			result.code = 1;
			result.msg = "添加抽奖记录成功";
		} else {
			result.code = -1;
			result.msg = "添加抽奖记录失败";
		}
		return result;
	}

	public List<t_moon_cake_lottery_record> queryAllUnDelivery() {
		return findListByColumn("status = ?", false);
	}

	public boolean update(t_moon_cake_lottery_record record) {
		return moonCakeLotteryRecordDao.updateStatus(record);
	}
	
}