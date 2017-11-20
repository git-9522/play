package jobs;

import java.util.Date;
import java.util.List;

import common.constants.ConfConst;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.ResultInfo;
import models.core.entity.t_invest_log;
import play.Logger;
import play.jobs.Every;
import play.jobs.Job;
import services.activity.AwardNumberRecordService;
import services.activity.Invert11Activity5Service;
import services.core.InvestLogService;
@Every("5min")
public class ActivityTask extends Job{
	/** 活动添加（奖号） */
	private static AwardNumberRecordService awardNumberRecordService = Factory.getService(AwardNumberRecordService.class);
	private static InvestLogService investlogservice = Factory.getService(InvestLogService.class);
	private static Invert11Activity5Service invert11activity5service=Factory.getService(Invert11Activity5Service.class);
	public void doJob() {
		if (!ConfConst.IS_START_JOBS) {
			return;
		}
		Logger.info("************活动定时任务添加奖号开始************" );
		 List<t_invest_log> logs= investlogservice.queryUnSendInvestLog();
		for (t_invest_log log : logs) {
			//iPhonex 抽奖奖励发放
			StringBuffer sb=new StringBuffer();
			ResultInfo info=awardNumberRecordService.add(log);
			if(info.code==1){
				sb.append(DateUtil.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss")+"--已发放iPhone抽奖号码;");
			}else{
				
				sb.append(DateUtil.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss")+"--【红金所发放iPhone抽奖号码】"+info.msg);
			}
			
			//红金所脱单红包奖励返现发放
			info=invert11activity5service.sendPacketMoney(log);
			if(info.code==1){
				sb.append(DateUtil.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss")+"--已发放红金所脱单红包奖励;");
			}else{
				sb.append(DateUtil.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss")+"--【红金所脱单红包】"+info.msg);
			}
			t_invest_log updateLog=investlogservice.findByID(log.id);
			updateLog.remark=sb.toString();
			updateLog.delivery_time=new Date();
			updateLog.status=true;
			investlogservice.saveLog(updateLog);
		}
		Logger.info("************活动定时任务添加奖号结束***************");
	}

}
