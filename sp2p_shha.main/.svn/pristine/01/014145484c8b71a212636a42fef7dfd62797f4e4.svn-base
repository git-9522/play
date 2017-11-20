package jobs;

import java.util.List;

import common.constants.ConfConst;
import common.enums.Client;
import common.enums.ServiceType;
import common.utils.Factory;
import common.utils.OrderNoUtil;
import models.common.entity.t_user_info;
import payment.impl.PaymentProxy;
import play.Logger;
import play.jobs.Every;
import play.jobs.Job;
import play.jobs.On;
import services.common.UserInfoService;

@On("0 30 2 * * ?")
public class UserInfoTask extends Job {

	@Override
	public void doJob() throws Exception {
		if(!ConfConst.IS_START_JOBS){
			return;
		}
		
		Logger.info("-----------开始执行定时任务：更新用户信息----------");
		UserInfoService userInfoService = Factory.getService(UserInfoService.class);

		List<t_user_info> infos = userInfoService.queryIdNumbers();
		
		for(t_user_info info : infos) {
			String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.QUERY_PERSION_INFORMATION);
			PaymentProxy.getInstance().queryPersionInformation(Client.PC.code, serviceOrderNo, info.user_id, null, info.id_number, null);
		}
		
		Logger.info("-----------执行结束：更新用户信息-----------");
	}
	
}