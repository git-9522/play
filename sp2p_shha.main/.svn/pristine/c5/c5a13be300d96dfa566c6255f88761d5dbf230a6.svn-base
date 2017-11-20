package jobs;

import common.constants.ConfConst;
import common.utils.Factory;
import play.Logger;
import play.jobs.Every;
import play.jobs.Job;
import services.common.NoticeService;

/**
 * 定时任务:群发短信(10分钟扫描一次)
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年2月23日
 */
@Every("10min")
public class MassSmsSend extends Job {

	public void doJob() {
		if(!ConfConst.IS_START_JOBS){
			return;
		}
		
		Logger.info("--------------群发短信,开始---------------------");
		NoticeService noticeService = Factory.getService(NoticeService.class);
		noticeService.sendMassSMSTask();
		Logger.info("--------------群发短信,结束---------------------");
	}
}
