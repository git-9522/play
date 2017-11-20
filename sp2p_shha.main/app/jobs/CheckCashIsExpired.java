package jobs;

import java.util.List;

import models.core.entity.t_cash_user;
import common.utils.Factory;
import play.Logger;
import play.jobs.Every;
import play.jobs.Job;
import services.core.CashUserService;

/**
 * 检查现金券是否过期，每隔20分钟执行一次
 * 
 * @author yanpengfei
 *
 */
@Every("20min")
public class CheckCashIsExpired extends Job {

	protected CashUserService cashUserService = Factory.getService(CashUserService.class);
	
	public void doJob() throws Exception {
		Logger.info("--------------检查现金券是否过期,开始---------------------");
		
		List<t_cash_user> cashList = cashUserService.findAllExpiredCashByEndTime();
		
		if (cashList == null || cashList.size() <= 0) {
			Logger.info("--------------暂无过期现金券---------------------");
			Logger.info("--------------检查现金券是否过期,结束---------------------");
			
			return ;
		}
		
		int rows = cashUserService.updateAllExpiredCashStatus();
		
		if (rows <= 0) {
			Logger.info("--------------改变现金券状态为已过期失败---------------------");
		} else {
			Logger.info("--------------处理过期的现金券数量为：" + cashList.size() + "---------------------");
		}
		
		Logger.info("--------------检查现金券是否过期,结束---------------------");
	}
	
}
