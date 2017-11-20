package controllers.activity;


import java.util.Date;
import java.util.HashMap;
import java.util.List;

import common.constants.SettingKey;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.ResultInfo;
import controllers.common.FrontBaseController;
import jobs.ThbbAroundTask;
import models.activity.bean.InvestAnnual;
import play.Logger;
import services.activity.HbbAroundService;

/**
 * 10月活动 虹宝宝全国游
 * 
 * @description
 *
 * @author djk
 * @createDate 2017年9月21日
 */
public class HbbAroundActivityCtrl extends FrontBaseController {

	protected static HbbAroundService hbbaroundservice = Factory.getService(HbbAroundService.class);

	/**
	 * 虹宝宝全国游
	 */
	public static void indexPre() {
		ResultInfo result = new ResultInfo();
		Date startDate = DateUtil.strToDate(HbbAroundService.start_time, "yyyy-MM-dd HH:mm:ss");
		Date endDteate = DateUtil.strToDate(HbbAroundService.end_time, "yyyy-MM-dd HH:mm:ss");
		long thistime = new Date().getTime();
		if ((startDate.getTime() > thistime) || (endDteate.getTime() < thistime)) {
			result.code = -1;
			result.msg = "活动未开启";
			renderJSON(result);
		}
		int trips = 0;
		double allAnnualMoney = 0;
		List<InvestAnnual> list_InvestAnnual = hbbaroundservice.queryUserInvertAnnuali();
		for (InvestAnnual investannual : list_InvestAnnual) {
			allAnnualMoney += investannual.annual_money;
		}
		if (ThbbAroundTask.trips == 0) {
			Logger.info("年化总额 allAnnualMoney ===============================" + allAnnualMoney);
			int thiscount = (int) (allAnnualMoney / hbbaroundservice.annul_money);
			Logger.info("发放奖励总次数 thiscount ===============================" + thiscount);
			trips = hbbaroundservice.base_trip * thiscount;
			HashMap<String, Object> trip_map = hbbaroundservice.trips_adress(trips);
			ThbbAroundTask.userCount = list_InvestAnnual.size();
			ThbbAroundTask.pen = (int) trips / 45000 + 1;
			ThbbAroundTask.trips = trips;
			ThbbAroundTask.city = trip_map.get("city").toString();
			ThbbAroundTask.nextCity = trip_map.get("nextCity").toString();
			ThbbAroundTask.nextTrip = Integer.parseInt(trip_map.get("nextTrip").toString());
			ThbbAroundTask.code = trip_map.get("code").toString();
		}
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("userCount", ThbbAroundTask.userCount);
		map.put("pen", ThbbAroundTask.pen);
		map.put("trips", ThbbAroundTask.trips);
		map.put("city", ThbbAroundTask.city);
		map.put("nextCity", ThbbAroundTask.nextCity);
		map.put("nextTrip", ThbbAroundTask.nextTrip);
		map.put("code", ThbbAroundTask.code);
		result.code = 0;
		result.msg = "success";
		result.obj = map;
		renderJSON(result);

	}

}
