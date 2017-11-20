package jobs;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import common.constants.ConfConst;
import common.enums.Client;
import common.enums.ServiceType;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.OrderNoUtil;
import common.utils.ResultInfo;
import models.activity.bean.InvestAnnual;
import models.common.entity.t_deal_platform.DealRemark;
import models.common.entity.t_deal_user.OperationType;
import models.common.entity.t_user_fund;
import payment.impl.PaymentProxy;
import play.Logger;
import play.jobs.Every;
import play.jobs.Job;
import services.activity.HbbAroundService;
import services.common.DealPlatformService;
import services.common.DealUserService;
import services.common.UserFundService;

/**
 * 
 * @Title THBB
 * @Description 在2017年11月1日 2点执行 发放虹宝宝 最终抵达城市所在省份投资者奖励
 * @author hjs_djk
 * @createDate 2017年9月22日 下午4:53:36
 */
// @On("0 0 2 1 11 ? 2017-2017")
@Every("10min")
public class THBB extends Job {
	private static boolean isRun = false;

	@Override
	public void doJob() throws Exception {
		if (!ConfConst.IS_START_JOBS) {
			return;
		}

		Date startDate = org.apache.commons.lang3.time.DateUtils
				.addMinutes(DateUtil.strToDate(HbbAroundService.end_time, "yyyy-MM-dd HH:mm:ss"), 10);
		Date endDteate = org.apache.commons.lang3.time.DateUtils
				.addMinutes(DateUtil.strToDate(HbbAroundService.end_time, "yyyy-MM-dd HH:mm:ss"), 35);
		long thistime = new Date().getTime();
		if ((startDate.getTime() > thistime) || (endDteate.getTime() < thistime)) {
			return;
		}
		if (isRun) {
			return;
		}
		Logger.info("-----------开始执行定时任务：发放虹宝宝 最终抵达城市所在省份投资者奖励---------");
		/** 注入系统设置service */
		HbbAroundService hbbaroundservice = Factory.getService(HbbAroundService.class);
		DealPlatformService dealPlatformService = Factory.getService(DealPlatformService.class);
		DealUserService dealUserService = Factory.getService(DealUserService.class);
		UserFundService userFundService = Factory.getService(UserFundService.class);
		// 获取活动时间内的所有投资记录
		List<InvestAnnual> listall_InvestAnnual = hbbaroundservice.queryUserInvertAnnuali();

		/** 虹宝宝总行程 */
		int trips = 0;
		// 活动时间内 总累计年化金额
		double allmoney = 0;
		for (InvestAnnual annual : listall_InvestAnnual) {
			allmoney += annual.annual_money;
		}
		int allcount = (int) (allmoney / 50);
		trips = hbbaroundservice.base_trip * allcount;
		HashMap<String, Object> map = hbbaroundservice.trips_adress(trips);
		List<InvestAnnual> list_InvestAnnual = hbbaroundservice.queryUserInvertAnnuali(map.get("code").toString());
		for (InvestAnnual annual : list_InvestAnnual) {
			allmoney += annual.annual_money;
			// 发放累计投资年化金额的%1
			// 转账 发放奖励
			String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.TRANSFER);
			ResultInfo result = PaymentProxy.getInstance().transfer(Client.PC.code, serviceOrderNo, annual.user_id,
					annual.annual_money * 0.01);
			if (result.code == 1) {
				// 处理平台记录
				dealPlatformService.addPlatformDeal(annual.user_id, annual.annual_money * 0.01,
						DealRemark.HBBAROUND_TRIPS_TRANSFER, null);
				// 处理个人记录
				t_user_fund userFund = userFundService.queryUserFundByUserId(annual.user_id);
				userFund.balance = userFund.balance + annual.annual_money * 0.01;
				userFundService.update(userFund);
				// 更新用户签名失败
				userFundService.userFundSignUpdate(annual.user_id);
				dealUserService.addDealUserInfo(serviceOrderNo, annual.user_id, annual.annual_money * 0.01,
						userFund.balance, userFund.freeze, OperationType.HBBAROUND_TRIPS_TRANSFER, null);

			}
		}
		Logger.info("-----------结束执行定时任务：发放虹宝宝 最终抵达城市所在省份投资者奖励----------");
		isRun = true;
	}

}
