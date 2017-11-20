package services.activity;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.time.DateUtils;

import common.utils.DateUtil;
import common.utils.Factory;
import daos.activity.HbbAroundDao;
import models.activity.bean.InvestAnnual;
import models.activity.bean.InvestHbbSumAnnual;
import models.activity.entity.t_hbb_around;
import services.base.BaseService;
import services.common.DealPlatformService;
import services.common.DealUserService;
import services.common.InvestLotteryService;
import services.common.MoonCakeLotteryRecordService;
import services.common.ReversalRecordService;
import services.common.UserFundService;

/**
 * 10月 虹宝宝全国游活动业务处理
 * 
 * @author djk
 * @createDate 2017年9月21日
 *
 */
public class HbbAroundService extends BaseService<t_hbb_around> {
	/** 路线 */
	public static final Object[][] lines = {
			{ 0, 3000, 6500, 10000, 13000, 16000, 18000, 18500, 18700, 20200, 21200, 22200, 22300, 23300, 24300, 24400,
					24900, 25000, 25100, 25200, 27200, 27700, 30200, 32700, 32900, 33400, 34900, 35000, 37500, 39500,
					41500, 45000 },
			{ 3000, 3500, 3500, 3000, 3000, 2000, 500, 200, 1500, 1000, 1000, 100, 1000, 1000, 100, 500, 100, 100, 100,
					2000, 500, 2500, 2500, 200, 500, 1500, 100, 2500, 2000, 2000, 3500, 0 },
			{ "上海", "南京", "合肥", "郑州", "济南", "石家庄", "天津", "北京", "沈阳", "长春", "哈尔滨", "呼和浩特", "太原", "西安", "银川", "兰州", "西宁",
					"乌鲁木齐", "拉萨", "成都", "重庆", "武汉", "长沙", "贵阳", "昆明", "南宁", "海口", "广州", "福州", "南昌", "杭州", "上海" },
			{ "上海", "江苏", "安徽", "河南", "山东", "河北", "天津", "北京", "辽宁", "吉林", "黑龙江", "内蒙古", "山西", "陕西", "宁夏", "甘肃", "青海",
					"新疆", "西藏", "四川", "重庆", "湖北", "湖南", "贵州", "云南", "广西", "海南", "广东", "福建", "江西", "浙江", "上海" },
			{ 31, 32, 34, 41, 37, 13, 12, 11, 21, 22, 23, 15, 14, 61, 64, 62, 63, 65, 54, 51, 50, 42, 43, 52, 53, 45,
					46, 44, 35, 36, 33, 31 } };

	/** 开始时间 **/
	public static final String start_time = new String("2017-11-01 00:00:00");
	/** 结束时间 **/
	public static final String end_time = new String("2017-11-30 23:59:59");
	/** 年化金额基数 **/
	public static final double annul_money = 100.00;
	/** 虹宝宝行程基数 **/
	public static final int base_trip = 10;
	/** 用户获得加油费基数 **/
	public static final double base_money = 2.0;

	protected HbbAroundDao hbbarounddao;
	InvestLotteryService investLotteryService = Factory.getService(InvestLotteryService.class);
	DealPlatformService dealPlatformService = Factory.getService(DealPlatformService.class);
	DealUserService dealUserService = Factory.getService(DealUserService.class);
	UserFundService userFundService = Factory.getService(UserFundService.class);
	ReversalRecordService reversalRecordService = Factory.getService(ReversalRecordService.class);
	MoonCakeLotteryRecordService moonCakeLotteryRecordService = Factory.getService(MoonCakeLotteryRecordService.class);

	protected HbbAroundService() {
		hbbarounddao = Factory.getDao(HbbAroundDao.class);
		super.basedao = hbbarounddao;
	}

	public static void main(String[] args) {
		// for (int i = 0; i < lines[0].length; i++) {
		// System.out.println(lines[3][i] + "\t" + lines[2][i] + "\t" +
		// lines[0][i] + "\t" + lines[1][i]);
		//
		// }

		Date startDate = DateUtil.strToDate(HbbAroundService.start_time, "yyyy-MM-dd HH:mm:ss");
		Date endDteate = DateUtil.strToDate(HbbAroundService.end_time, "yyyy-MM-dd HH:mm:ss");
		
		long thistime = new Date().getTime();
//		if (!(startDate.getTime() > thistime) || !(endDteate.getTime() < thistime)) {
//			System.out.println(1);
//			return;
//		}
//		System.out.println(2);
		endDteate=DateUtils.addMinutes(endDteate, 3000);
		if (!(startDate.getTime() > thistime) || !(endDteate.getTime() < thistime)) {
			System.out.println(1);
		}
		System.out.println(3);
	}

	/**
	 * 获取截止当前 活动时间内用户投资的年化金额
	 * 
	 * @return
	 */
	public List<InvestAnnual> queryUserInvertAnnuali() {
		return hbbarounddao.queryUserInvertAnnuali(HbbAroundService.start_time,HbbAroundService.end_time);
	}

	/**
	 * 获取截止当前 指定省的 活动时间内用户投资的年化金额
	 * 
	 * @return
	 */
	public List<InvestAnnual> queryUserInvertAnnuali(String str) {
		return hbbarounddao.queryUserInvertAnnuali(str,HbbAroundService.start_time,HbbAroundService.end_time);
	}

	/**
	 * 获取截止当前 活动时间内用户已经发放的所有奖励和加油费
	 * 
	 * @return
	 */
	public List<InvestHbbSumAnnual> queryUserHbbSumMoney() {
		return hbbarounddao.queryUserHbbSumMoney(HbbAroundService.start_time,HbbAroundService.end_time);
	}

	/**
	 * 计算当前到达的
	 * 
	 * @return
	 */
	public HashMap<String, Object> trips_adress(int trips) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		int last_trips = trips % 45000;
		if (trips == 0 || trips % 45000 == 0||last_trips<Integer.parseInt(lines[0][1].toString())) {
			result.put("province", lines[2][0]);
			result.put("city", lines[3][0]);
			result.put("nextCity", lines[3][1]);
			result.put("nextTrip", lines[1][0]);
			result.put("code", "31");
		} else {
			for (int i = 1; i < lines[0].length - 1; i++) {
				if (Integer.parseInt(lines[0][i].toString()) <= last_trips
						&& Integer.parseInt(lines[0][i + 1].toString()) > last_trips) {
					result.put("province", lines[3][i]);
					result.put("city", lines[2][i]);
					result.put("nextCity", lines[2][i + 1]);
					result.put("nextTrip", lines[1][i]);
					result.put("code", lines[4][i]);
				}
			}

		}
		return result;
	}

	public boolean save(t_hbb_around t) {
		return hbbarounddao.save(t);
	}

}
