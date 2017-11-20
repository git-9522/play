package jobs;

import common.constants.ConfConst;
import common.utils.Factory;
import common.utils.ResultInfo;
import play.Logger;
import play.jobs.Job;
import play.jobs.On;
import play.jobs.OnApplicationStart;
import services.core.BillService;

/**
 * 未还账单提醒(到期前一个星期提醒一次，到期当天提醒一次。  未还账单只提醒这2次)
 *
 * @description 
 *
 * @author yaoyi
 * @createDate 2016年3月1日
 */
@On("0 0 9 * * ?")
public class BillRemind extends Job{
	
	@Override
	public void doJob(){
		if(!ConfConst.IS_START_JOBS){
			return;
		}
		
		Logger.info("-----------账单到期提醒，开始----------");
		
		BillService billService = Factory.getService(BillService.class);
		
		ResultInfo result = billService.billRemind();
		Logger.info(result.msg);
		
		Logger.info("-----------=账单到期提醒，结束----------");
	}
	
}
