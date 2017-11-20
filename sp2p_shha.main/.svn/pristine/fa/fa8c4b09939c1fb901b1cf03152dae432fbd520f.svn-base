package jobs;

import common.constants.ConfConst;
import common.utils.Factory;
import play.Logger;
import play.jobs.Every;
import play.jobs.Job;
import services.common.NoticeService;

/**
 * 定时任务:群发邮件 
 * 
 *
 * @author liudong
 * @createDate 2016年4月5日
 *
 */
@Every("10min")
public class MassEmailSend extends Job {
	
	public void doJob() {
		if(!ConfConst.IS_START_JOBS){
			return;
		}
		
		Logger.info("--------------群发邮件,开始---------------------");
		NoticeService noticeService = Factory.getService(NoticeService.class);
		noticeService.sendMassEmailTask();
		Logger.info("--------------群发邮件,结束---------------------");
	}
}
