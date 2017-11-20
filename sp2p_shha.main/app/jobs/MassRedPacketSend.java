package jobs;

import java.util.List;

import models.core.entity.t_red_packet_sending;
import common.constants.ext.RedpacketKey;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.ResultInfo;
import play.Logger;
import play.db.jpa.JPAPlugin;
import play.jobs.Every;
import play.jobs.Job;
import services.core.RedpacketService;
import services.core.RedpacketUserService;

/**
 * 群发红包定时任务，每隔10分钟执行
 * 
 * @author yanpengfei
 * @createDate 2016年12月21日
 */
@Every("10min")
public class MassRedPacketSend extends Job {

	protected RedpacketService redpacketService = Factory.getService(RedpacketService.class);
	
	protected RedpacketUserService redpacketUserService = Factory.getService(RedpacketUserService.class);
	
	@Override
	public void doJob() throws Exception {
		Logger.info("-----------开始执行定时任务：群发红包-----------");
		
		//查询群发红包表中未发放的红包
		List<t_red_packet_sending> sendList = redpacketService.queryUnSendRedPacket();
		
		if (null == sendList || sendList.size() <= 0) {
			Logger.info("-----------暂无红包需要发送-----------");
			Logger.info("-----------结束执行定时任务：群发红包-----------");
			
			return;
		}
		
		ResultInfo result = new ResultInfo();
		
		int rows = 0;
		
		/* 关闭自动事务 */
		JPAPlugin.closeTx(false);
		
		for (t_red_packet_sending send : sendList) {
			try {
				/* 开启自动事务 */
				JPAPlugin.startTx(false);
				
				//添加用户红包记录
				result = redpacketUserService.jobSendRedPacketToUser(send.name, send.user_id, send.id, send.amount, send.use_rule, send.bid_period,
						send.end_time, send.is_send_letter, send.is_send_email, send.is_send_sms);
				
				if (result.code < 1) {
					continue;
				}
				
				//发放成功修改群发红包表中的状态
				rows = redpacketService.updateRedPacketSendStatus(send.id);
				
				if (rows <= 0) {
					continue;
				}
			} catch (Exception e) {
				LoggerUtil.error(true, "群发红包失败：" + e.getMessage());
				
				continue ;
			} finally {
				/* 关闭自动事务 */
				JPAPlugin.closeTx(false);
				LoggerUtil.info(false, "群发红包事务正常关闭，sendId = %s", send.id);
			}
		}
			
		/* 开启自动事务 */
		JPAPlugin.startTx(false);
		
		Logger.info("-----------结束执行定时任务：群发红包-----------");
	}
	
}
