package jobs;

import common.constants.ConfConst;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.ResultInfo;
import play.Logger;
import play.jobs.Job;
import play.jobs.On;
import play.jobs.OnApplicationStart;
import services.core.BillService;

/**
 * 每天执行系统标记逾期，执行时间：00：30
 *
 * @description 
 *
 * @author yaoyi
 * @createDate 2016年2月27日
 */
@On("0 30 0 * * ?")
public class MarkOverdue extends Job{
	
	@Override
	public void doJob() throws Exception{
		if(!ConfConst.IS_START_JOBS){
			return;
		}
		
		Logger.info("-----------开始执行定时任务：标记逾期----------");
		BillService billService = Factory.getService(BillService.class);
		
		ResultInfo result = billService.systemMarkOverdue();
		if(result.code < 1){
			LoggerUtil.error(true, "标记逾期定时任务执行失败! %s", result.msg);
		}
		
		Logger.info("-----------执行结束：标记逾期----------");
	}
	
}
