package jobs;

import java.util.Date;
import java.util.List;
import java.util.Map;

import common.constants.ConfConst;
import common.enums.Client;
import common.enums.ServiceType;
import common.utils.Factory;
import common.utils.OrderNoUtil;
import common.utils.ResultInfo;
import models.common.entity.t_deal_platform.DealRemark;
import models.common.entity.t_deal_user.OperationType;
import models.common.entity.t_invest_lottery;
import models.common.entity.t_reversal_record;
import models.common.entity.t_user_fund;
import payment.impl.PaymentProxy;
import play.Logger;
import play.jobs.Every;
import play.jobs.Job;
import play.jobs.On;
import services.common.DealPlatformService;
import services.common.DealUserService;
import services.common.UserFundService;
import services.core.InvestService;

//@On("0 0 2 1 9 ? 2017")
//@Every("1min")
public class QQH extends Job {

	@Override
	public void doJob() throws Exception {
		if(!ConfConst.IS_START_JOBS){
			return;
		}
		Logger.info("-----------开始执行定时任务：鹊桥会----------");
		InvestService investService = Factory.getService(InvestService.class);
		DealPlatformService dealPlatformService = Factory.getService(DealPlatformService.class);
		UserFundService userFundService = Factory.getService(UserFundService.class);
		DealUserService dealUserService = Factory.getService(DealUserService.class);
		
		List<Map<String, Object>> maps = investService.queryMoneyAndUser("2017-08-25", "2017-09-01");
		
		long sum = investService.calulateInvestMoneyInDates("2017-08-25", "2017-09-01");
		
		if(sum < 99900) {
			Logger.info("-----------结束执行定时任务：鹊桥会----------");
			return;
		}
		
		long temp = sum % 99900;
		
		long calcTemp = temp;
		
		if(sum > 0 && sum % 99900 > 0) {
			//处理金额
			boolean flag = true;
			while(flag) {
				Map<String, Object> map = maps.get(maps.size() - 1);
				double amount = Double.parseDouble(map.get("amount").toString());
				if(amount < calcTemp) {
					maps.remove(map);
					calcTemp -= amount;
				} else if(amount == calcTemp) {
					maps.remove(map);
					flag = false;
				} else {
					flag = false;
					map.put("amount", amount - calcTemp);
				}
			}
		}
		
		for(Map<String, Object> map : maps) {
			double amount = Double.parseDouble(map.get("amount").toString()) / 1000;
			long userId = Long.parseLong(map.get("userId").toString());
 			String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.TRANSFER); 
			ResultInfo result = PaymentProxy.getInstance().transfer(Client.PC.code, serviceOrderNo, userId, amount);
			if(result.code == 1) {
				// 处理平台记录
				dealPlatformService.addPlatformDeal(userId, amount, DealRemark.QQH_TRANSFER, null);
				// 处理个人记录
				t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
				userFund.balance = userFund.balance + amount;
				userFundService.update(userFund);
				// 更新用户签名失败
				userFundService.userFundSignUpdate(userId);
				dealUserService.addDealUserInfo(serviceOrderNo, userId, amount, userFund.balance, userFund.freeze, OperationType.QQH_TRANSFER, null);
			}
		}

		Logger.info("-----------结束执行定时任务：鹊桥会----------");
		
	}
	
}