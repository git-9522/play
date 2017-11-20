package jobs;

import play.Logger;
import play.jobs.Job;
import play.jobs.On;
import services.common.NoticeService;
import common.constants.ConfConst;
import common.utils.Factory;

/**
 * 定时任务:清理邮件临时发送表中已经发送的数据
 *
 * @description 每天凌晨4点中执行
 *
 * @author DaiZhengmiao
 * @createDate 2016年2月23日
 */
@On("0 0 4 * * ?")
public class EmailSendingClear extends Job {
	
	public void doJob() {
		if(!ConfConst.IS_START_JOBS){
			return;
		}
		
		Logger.info("定时清理邮件临时发送表");
		NoticeService noticeService = Factory.getService(NoticeService.class);
		noticeService.deleteEmailSending();
	}
}
