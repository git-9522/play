package jobs;

import java.util.List;

import models.common.entity.t_user;
import models.core.entity.t_addrate_sending;
import models.core.entity.t_cash_sending;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.ResultInfo;
import play.Logger;
import play.db.jpa.JPAPlugin;
import play.jobs.Every;
import play.jobs.Job;
import services.common.UserService;
import services.core.CashService;
import services.core.CashUserService;
import services.core.RateService;

/**
 * 群发加息券定时任务，每隔10分钟执行
 * 
 * @author jiayijian
 * @createDate 2016年4月5日
 */
@Every("10min")
public class MassRateSend extends Job {

	protected RateService rateService = Factory.getService(RateService.class);
	protected static UserService userService = Factory.getService(UserService.class);
	
	@Override
	public void doJob() throws Exception {
		Logger.info("-----------开始执行定时任务：群发加息券-----------");
		
		//查询群发加息券表中未发放的加息券
		List<t_addrate_sending> sendList = rateService.queryUnSendRate();
		
		if (null == sendList || sendList.size() <= 0) {
			Logger.info("-----------暂无加息券需要发送-----------");
			Logger.info("-----------结束执行定时任务：群发加息券-----------");
			
			return;
		}
		
		ResultInfo result = new ResultInfo();
		
		/* 关闭自动事务 */
		JPAPlugin.closeTx(false);
		
		for (t_addrate_sending send : sendList) {
			
			try {
				/* 开启自动事务 */
				JPAPlugin.startTx(false);
					
				//添加用户加息券记录
				result = rateService.jobSendRateToUser(send.user_id, send.id, send.rate, send.use_rule, send.bid_period,
						send.end_time, send.is_send_letter, send.is_send_email, send.is_send_sms);
				
				if (result.code < 1) {
					continue;
				}
				
				//发放成功修改群发加息券表中的状态
				int rows = rateService.updateRateSendStatus(send.id);
				
				if (rows <= 0) {
					
					LoggerUtil.error(true, "群发加息券更新状态失败,id=：" + send.id);
					continue;
				}
					
				
			} catch (Exception e) {
				LoggerUtil.error(true, "群发加息券失败：" + e.getMessage());
				
				continue ;
			} finally {
				/* 关闭自动事务 */
				JPAPlugin.closeTx(false);
				LoggerUtil.info(false, "群发加息券事务正常关闭");
			}
		}
			
		/* 开启自动事务 */
		JPAPlugin.startTx(false);
		
		Logger.info("-----------结束执行定时任务：群发加息券-----------");
	}
	
}
