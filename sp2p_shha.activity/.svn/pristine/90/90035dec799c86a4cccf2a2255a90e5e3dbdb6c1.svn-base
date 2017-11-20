package jobs;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;

import common.constants.ConfConst;
import common.enums.Client;
import common.enums.ServiceType;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.OrderNoUtil;
import common.utils.ResultInfo;
import models.common.entity.t_invest_lottery;
import models.common.entity.t_moon_cake_lottery_record;
import models.common.entity.t_reversal_record;
import models.common.entity.t_user_fund;
import models.activity.bean.InvestAnnual;
import models.activity.bean.InvestHbbSumAnnual;
import models.activity.entity.t_hbb_around;
import models.common.entity.t_deal_platform.DealRemark;
import models.common.entity.t_deal_user.OperationType;
import payment.impl.PaymentProxy;
import play.Logger;
import play.jobs.Every;
import play.jobs.Job;
import services.activity.HbbAroundService;
import services.common.DealPlatformService;
import services.common.DealUserService;
import services.common.InvestLotteryService;
import services.common.MoonCakeLotteryRecordService;
import services.common.ReversalRecordService;
import services.common.UserFundService;

/**
 * 
 * @Title ThbbAroundTask
 * @Description 10月活动虹宝宝奖励发放 5分钟执行一次
 * @author hjs_djk
 * @createDate 2017年9月21日 下午3:12:45
 */
@Every("2min")
public class ThbbAroundTask extends Job {
	/** 虹宝宝全国之旅 圈数 */
	public static int pen = 0;
	/** 虹宝宝 当前圈行驶的总公里数 **/
	public static int trips = 0;
	/** 当前城市 **/
	public static String city = "上海";
	/** 下个城市 **/
	public static String nextCity = "南京";
	/** 累计参与用户 **/
	public static int userCount = 0;
	/** 当前站距离下一站距离 **/
	public static int nextTrip = 0;
	/** 当前省份编码 **/
	public static String code = "31";

	@Override
	public void doJob() throws Exception {

		if (!ConfConst.IS_START_JOBS) {
			return;
		}

		Date startDate = DateUtil.strToDate(HbbAroundService.start_time, "yyyy-MM-dd HH:mm:ss");
		Date endDteate = DateUtil.strToDate(HbbAroundService.end_time, "yyyy-MM-dd HH:mm:ss");
		long thistime = new Date().getTime();
		if ((startDate.getTime() > thistime) || (endDteate.getTime() < thistime)) {
			return;
		}
		Logger.info("-----------开始执行定时任务：发放虹宝宝全国之旅 投资返现----------");
		/** 注入系统设置service */
		HbbAroundService hbbaroundservice = Factory.getService(HbbAroundService.class);
		DealPlatformService dealPlatformService = Factory.getService(DealPlatformService.class);
		DealUserService dealUserService = Factory.getService(DealUserService.class);
		UserFundService userFundService = Factory.getService(UserFundService.class);
		List<InvestAnnual> list_InvestAnnual = hbbaroundservice.queryUserInvertAnnuali();
		/** 虹宝宝当前行驶的路程 */
		double allAnnualMoney = 0;
		// 获取截止当前 活动时间内用户已经发放的所有奖励和加油费
		List<InvestHbbSumAnnual> list_InvestHbbSumAnnual = hbbaroundservice.queryUserHbbSumMoney();
		HashMap<String, InvestHbbSumAnnual> map = new HashMap<String, InvestHbbSumAnnual>();
		for (InvestHbbSumAnnual hbbsumannual : list_InvestHbbSumAnnual) {
			map.put(hbbsumannual.user_id + "", hbbsumannual);
		
		}
		for (InvestAnnual annual : list_InvestAnnual) {
			allAnnualMoney+=annual.annual_money;
			// 本次更新发放奖励
			t_hbb_around tha = new t_hbb_around();
			InvestHbbSumAnnual temp_hbbsumannual = map.get(annual.user_id + "");
			if (temp_hbbsumannual == null) {
				// 第一次发放奖励
				// 本次发放奖励次数 取整
				int thiscount = (int) (annual.annual_money / hbbaroundservice.annul_money);
				if (thiscount > 0) {
					tha.add_trip = hbbaroundservice.base_trip * thiscount;
					tha.count = thiscount;
					tha.this_count = thiscount;
					tha.user_id = annual.user_id;
					tha.value = hbbaroundservice.base_money * thiscount;
					tha.time = new Date();
				} else {
					tha = null;
				}

			} else {
				int thiscount = (int) (annual.annual_money / hbbaroundservice.annul_money) - temp_hbbsumannual.counts;
				if (thiscount > 0) {
					tha.add_trip = hbbaroundservice.base_trip * thiscount;
					tha.count = temp_hbbsumannual.counts + thiscount;
					tha.this_count = thiscount;
					tha.user_id = annual.user_id;
					tha.value = hbbaroundservice.base_money * thiscount;
					tha.time = new Date();
				} else {
					tha = null;
				}

			}
			// 转账 发放奖励
			if (tha != null) {
				if (tha.value > 0) {
					String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.TRANSFER);
					ResultInfo result = PaymentProxy.getInstance().transfer(Client.PC.code, serviceOrderNo, tha.user_id,
							tha.value);
					if (result.code == 1) {
						tha.status = true;
						hbbaroundservice.save(tha);
						// 处理平台记录
						dealPlatformService.addPlatformDeal(tha.user_id, tha.value, DealRemark.HBBAROUND_TRANSFER,
								null);
						// 处理个人记录
						t_user_fund userFund = userFundService.queryUserFundByUserId(tha.user_id);
						userFund.balance = userFund.balance + tha.value;
						userFundService.update(userFund);
						// 更新用户签名失败
						userFundService.userFundSignUpdate(tha.user_id);
						dealUserService.addDealUserInfo(serviceOrderNo, tha.user_id, tha.value, userFund.balance,
								userFund.freeze, OperationType.HBBAROUND_TRANSFER, null);

					}
				}
			}
		}
		// 更新虹宝宝当前行程和所在城市信息
		int allcount = (int) (allAnnualMoney / hbbaroundservice.annul_money);
		this.trips = hbbaroundservice.base_trip * allcount;
		HashMap<String, Object> trip_map = hbbaroundservice.trips_adress(trips);
		this.pen = (int) trips / 45000 + 1;
		this.city = trip_map.get("city").toString();
		this.nextCity = trip_map.get("nextCity").toString();
		this.nextTrip = Integer.parseInt(trip_map.get("nextTrip").toString());
		this.code = trip_map.get("code").toString();
		this.userCount=list_InvestAnnual.size();
		Logger.info("----------发放虹宝宝全国之旅 投资返现 完毕----------");
	}
}
