package jobs;

import java.util.List;

import models.core.entity.t_cash_sending;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.ResultInfo;
import play.Logger;
import play.db.jpa.JPAPlugin;
import play.jobs.Every;
import play.jobs.Job;
import services.core.CashService;
import services.core.CashUserService;

/**
 * 群发现金券定时任务，每隔10分钟执行
 * 
 * @author yanpengfei
 * @createDate 2016年12月21日
 */
@Every("10min")
public class MassCashSend extends Job {

	protected CashService cashService = Factory.getService(CashService.class);
	
	protected CashUserService cashUserService = Factory.getService(CashUserService.class);
	
	@Override
	public void doJob() throws Exception {
		Logger.info("-----------开始执行定时任务：群发现金券-----------");
		
		//查询群发现金券表中未发放的现金券
		List<t_cash_sending> sendList = cashService.queryUnSendCash();
		
		if (null == sendList || sendList.size() <= 0) {
			Logger.info("-----------暂无现金券需要发送-----------");
			Logger.info("-----------结束执行定时任务：群发现金券-----------");
			
			return;
		}
		
		ResultInfo result = new ResultInfo();
		
		int rows = 0;
		
		/* 关闭自动事务 */
		JPAPlugin.closeTx(false);
		
		for (t_cash_sending send : sendList) {
			try {
				/* 开启自动事务 */
				JPAPlugin.startTx(false);
				
				//添加用户现金券记录
				result = cashUserService.jobSendCashToUser(send.user_id, send.id, send.amount, send.use_rule, send.bid_period,
						send.end_time, send.is_send_letter, send.is_send_email, send.is_send_sms);
				
				if (result.code < 1) {
					continue;
				}
				
				//发放成功修改群发现金券表中的状态
				rows = cashService.updateCashSendStatus(send.id);
				
				if (rows <= 0) {
					continue;
				}
			} catch (Exception e) {
				LoggerUtil.error(true, "群发现金券失败：" + e.getMessage());
				
				continue ;
			} finally {
				/* 关闭自动事务 */
				JPAPlugin.closeTx(false);
				LoggerUtil.info(false, "群发现金券事务正常关闭，sendId = %s", send.id);
			}
		}
			
		/* 开启自动事务 */
		JPAPlugin.startTx(false);
		
		Logger.info("-----------结束执行定时任务：群发现金券-----------");
	}
	
}
