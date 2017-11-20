package jobs;

import java.util.Date;
import java.util.List;

import common.constants.ConfConst;
import common.enums.Client;
import common.enums.ServiceType;
import common.utils.Factory;
import common.utils.OrderNoUtil;
import common.utils.ResultInfo;
import models.common.entity.t_deal_platform.DealRemark;
import models.common.entity.t_deal_user.OperationType;
import models.common.entity.t_invest_lottery;
import models.common.entity.t_moon_cake_lottery_record;
import models.common.entity.t_reversal_record;
import models.common.entity.t_user_fund;
import payment.impl.PaymentProxy;
import play.Logger;
import play.jobs.Every;
import play.jobs.Job;
import services.common.DealPlatformService;
import services.common.DealUserService;
import services.common.InvestLotteryService;
import services.common.MoonCakeLotteryRecordService;
import services.common.ReversalRecordService;
import services.common.UserFundService;

@Every("5min")
public class DeliveryTask extends Job {
	
	@Override
	public void doJob() throws Exception {
		if(!ConfConst.IS_START_JOBS){
			return;
		}
		Logger.info("-----------开始执行定时任务：发放投资抽奖----------");
		InvestLotteryService investLotteryService = Factory.getService(InvestLotteryService.class);
		DealPlatformService dealPlatformService = Factory.getService(DealPlatformService.class);
		DealUserService dealUserService = Factory.getService(DealUserService.class);
		UserFundService userFundService = Factory.getService(UserFundService.class);
		ReversalRecordService reversalRecordService = Factory.getService(ReversalRecordService.class);
		MoonCakeLotteryRecordService moonCakeLotteryRecordService = Factory.getService(MoonCakeLotteryRecordService.class);
		
		List<t_invest_lottery> lotteries = investLotteryService.queryAllUnDelivery();
		
		for(t_invest_lottery lottery : lotteries) {
 			String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.TRANSFER); 
			ResultInfo result = PaymentProxy.getInstance().transfer(Client.PC.code, serviceOrderNo, lottery.user_id, lottery.value);
			if(result.code == 1) {
				// 处理平台记录
				dealPlatformService.addPlatformDeal(lottery.user_id, lottery.value, DealRemark.INVEST_LOTTERY_TRANFER, null);
				// 处理个人记录
				t_user_fund userFund = userFundService.queryUserFundByUserId(lottery.user_id);
				userFund.balance = userFund.balance + lottery.value;
				userFundService.update(userFund);
				// 更新用户签名失败
				userFundService.userFundSignUpdate(lottery.user_id);
				dealUserService.addDealUserInfo(serviceOrderNo, lottery.user_id, lottery.value, userFund.balance, userFund.freeze, OperationType.INVEST_LOTTERY_TRANSFER, null);
				// 更新状态
				lottery.status = true;
				lottery.delivery_time = new Date();
				investLotteryService.update(lottery);
			}
		}

		Logger.info("-----------结束执行定时任务：发放投资抽奖----------");
		
		Logger.info("-----------开始执行定时任务：发放翻牌奖励----------");
		List<t_reversal_record> records = reversalRecordService.queryAllUnDelivery();
		for(t_reversal_record record : records) {
 			String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.TRANSFER); 
			ResultInfo result = PaymentProxy.getInstance().transfer(Client.PC.code, serviceOrderNo, record.user_id, record.award_money);
			if(result.code == 1) {
				// 处理平台记录
				dealPlatformService.addPlatformDeal(record.user_id, record.award_money, DealRemark.REVERSAL_TRANFER, null);
				// 处理个人记录
				t_user_fund userFund = userFundService.queryUserFundByUserId(record.user_id);
				userFund.balance = userFund.balance + record.award_money;
				userFundService.update(userFund);
				// 更新用户签名失败
				userFundService.userFundSignUpdate(record.user_id);
				dealUserService.addDealUserInfo(serviceOrderNo, record.user_id, record.award_money, userFund.balance, userFund.freeze, OperationType.REVERSAL_TRANFER, null);
				// 更新状态
				record.status = true;
				record.delivery_time = new Date();
				reversalRecordService.update(record);
			}
		}
		Logger.info("-----------结束执行定时任务：发放翻牌奖励----------");
		
		Logger.info("-----------开始执行定时任务：发放Moon翻牌奖励----------");
		List<t_moon_cake_lottery_record> moonRecords = moonCakeLotteryRecordService.queryAllUnDelivery();
		for(t_moon_cake_lottery_record record : moonRecords) {
 			String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.TRANSFER); 
			ResultInfo result = PaymentProxy.getInstance().transfer(Client.PC.code, serviceOrderNo, record.user_id, record.award_money);
			if(result.code == 1) {
				// 处理平台记录
				dealPlatformService.addPlatformDeal(record.user_id, record.award_money, DealRemark.INVEST_LOTTERY_TRANFER, null);
				// 处理个人记录
				t_user_fund userFund = userFundService.queryUserFundByUserId(record.user_id);
				userFund.balance = userFund.balance + record.award_money;
				userFundService.update(userFund);
				// 更新用户签名失败
				userFundService.userFundSignUpdate(record.user_id);
				dealUserService.addDealUserInfo(serviceOrderNo, record.user_id, record.award_money, userFund.balance, userFund.freeze, OperationType.INVEST_LOTTERY_TRANSFER, null);
				// 更新状态
				record.status = true;
				record.delivery_time = new Date();
				moonCakeLotteryRecordService.update(record);
			}
		}
		Logger.info("-----------结束执行定时任务：发放Moon翻牌奖励----------");
	}

}