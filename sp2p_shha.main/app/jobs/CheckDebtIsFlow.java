package jobs;

import common.constants.ConfConst;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.ResultInfo;
import play.Logger;
import play.jobs.Every;
import play.jobs.Job;
import services.core.DebtService;

@Every("15min")
public class CheckDebtIsFlow extends Job {

	public void doJob() {
		if(!ConfConst.IS_START_JOBS){
			return;
		}
		
		DebtService debtService = Factory.getService(DebtService.class);
		
		Logger.info("--------------定时判断债权过期,开始---------------------");
		ResultInfo result = debtService.judgeDebtFlow();
		if(result.code < 1){
			
			LoggerUtil.error(true, "定时债权过期业务失败! %s", result.msg);
		}
		Logger.info("--------------定时判断债权过期,结束---------------------");
	  
	}
}
