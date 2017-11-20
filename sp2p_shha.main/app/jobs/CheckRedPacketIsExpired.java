package jobs;

import java.util.List;

import models.core.entity.t_red_packet_user;
import common.utils.Factory;
import play.Logger;
import play.jobs.Every;
import play.jobs.Job;
import services.core.RedpacketUserService;

/**
 * 检查红包是否过期，每隔20分钟执行一次
 * 
 * @author yanpengfei
 *
 */
@Every("20min")
public class CheckRedPacketIsExpired extends Job {
	
	protected RedpacketUserService redpacketUserService = Factory.getService(RedpacketUserService.class);
	
	public void doJob() throws Exception {
		Logger.info("--------------检查红包是否过期,开始---------------------");
		
		List<t_red_packet_user> redPacketUserList = redpacketUserService.findAllExpiredRedPacketByEndTime();
		
		if (redPacketUserList == null || redPacketUserList.size() <= 0) {
			Logger.info("--------------暂无过期红包---------------------");
			Logger.info("--------------检查红包是否过期,结束---------------------");
			
			return ;
		}
		
		int rows = redpacketUserService.updateAllExpiredRedPacketStatus();
		
		if (rows <= 0) {
			Logger.info("--------------改变红包状态为已过期失败---------------------");
		} else {
			Logger.info("--------------处理过期的红包数量为：" + redPacketUserList.size() + "---------------------");
		}
		
		Logger.info("--------------检查红包是否过期,结束---------------------");
	}
	
}
