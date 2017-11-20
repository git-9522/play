package jobs;

import java.util.List;

import models.core.entity.t_cash_user;
import common.utils.Factory;
import play.Logger;
import play.jobs.Every;
import play.jobs.Job;
import services.core.CashUserService;
import services.core.RateService;

/**
 * 检查加息券是否过期，每隔20分钟执行一次
 * 
 * @author jiayijian
 *
 */
@Every("20min")
public class CheckRateIsExpired extends Job {

	protected static RateService rateService = Factory.getService(RateService.class);
	
	public void doJob() throws Exception {
		Logger.info("--------------检查加息券是否过期,开始---------------------");
		
		/*int rows =*/ rateService.updateAllExpiredRateStatus();
		
		Logger.info("--------------检查加息券是否过期,结束---------------------");
	}
	
}
