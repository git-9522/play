package jobs;

import play.Logger;
import play.jobs.Every;
import play.jobs.Job;
import services.common.NoticeService;
import common.constants.ConfConst;
import common.utils.Factory;

/**
 * 定时任务:发送短信(3分钟扫描一次)
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年2月23日
 */
@Every("3min")
public class SmsSend extends Job {

	public void doJob() {
		if(!ConfConst.IS_START_JOBS){
			return;
		}
		
		Logger.info("定时发送短信");
		NoticeService noticeService = Factory.getService(NoticeService.class);
		noticeService.sendSMSTask();
	}
	
}
