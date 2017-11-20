package jobs;

import models.core.entity.t_addrate_user;
import models.core.entity.t_cash_user;
import models.core.entity.t_red_packet_user;
import common.constants.ConfConst;
import common.utils.Factory;
import play.jobs.Every;
import play.jobs.Job;
import services.core.CashUserService;
import services.core.RateService;
import services.core.RedpacketUserService;
/**
 * 解锁锁定的红包、现金卷、加息卷
 * @author ZERO
 */
@Every("5min")
public class UnlockedRedPacket extends Job{

	protected static RedpacketUserService redpacketUserService = Factory.getService(RedpacketUserService.class);
	
	protected static CashUserService cashUserService = Factory.getService(CashUserService.class);
	
	protected static RateService rateService = Factory.getService(RateService.class);
	
	public void doJob() throws Exception {
		if(!ConfConst.IS_TRUST){
			return;
		}
	
		//解锁锁定的红包
		redpacketUserService.updateRePacketLockStatus(t_red_packet_user.RedpacketStatus.USING.code, t_red_packet_user.RedpacketStatus.UNUSED.code) ;
		//解锁锁定的现金卷
		cashUserService.updateUserCashLockStatus(t_cash_user.CashStatus.USING.code, t_cash_user.CashStatus.UNUSED.code);
		//解锁锁定的加息卷
		rateService.updateUserRateLockStatus(t_addrate_user.RateStatus.USING.code, t_addrate_user.RateStatus.UNUSED.code);
	}
}
