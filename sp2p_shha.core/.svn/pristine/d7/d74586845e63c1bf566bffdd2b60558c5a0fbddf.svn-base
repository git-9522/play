package jobs;

import java.util.Date;
import java.util.HashMap;

import common.constants.ConfConst;
import common.utils.Factory;
import play.jobs.Job;
import play.jobs.On;
import play.jobs.OnApplicationStart;
import services.common.UserFundService;
import services.common.UserService;
import services.core.BidService;
import services.core.BillInvestService;
import services.core.BillService;
import services.core.ProductService;
import services.core.StatisticIndexEchartDataService;

/**
 * 前台、后台首页统计数据展示,执行时间：02：00
 * 
 * @description
 *
 * @author ChenZhipeng
 * @createDate 2016年1月25日
 */
@On("0 0 2 * * ?")
@OnApplicationStart
public class IndexStatisticsJob extends Job {

	/** 注册用户数 */
	public static long userCount;

	/** 放款项目数量 */
	public static int bidCount;

	/** 放款项目总额(累计成交) */
	public static double totalBorrowAmount;

	/** 待还理财账单的账单总额 */
	public static double totalBillAmount;

	/** 平台浮动金 */
	public static double platformFloatAmount;

	/** 数据更新时间 */
	public static Date date;

	/** 给用户来带的收益 */
	public static double income;

	/** 到期还款率 */
	public static double expireRepaymentRate;
	/** 标对应前台产品类型**/
	public static HashMap<String, String> webProdMap;

	public void doJob() {
		if (!ConfConst.IS_START_JOBS) {
			return;
		}

		statistics();
	}

	private static void statistics() {
		StatisticIndexEchartDataService echartDataService = Factory.getService(StatisticIndexEchartDataService.class);
		UserService userService = Factory.getService(UserService.class);

		UserFundService userFundService = Factory.getService(UserFundService.class);

		BidService bidService = Factory.getService(BidService.class);

		BillInvestService billInvestService = Factory.getService(BillInvestService.class);

		BillService billService = Factory.getService(BillService.class);

		ProductService productservice = Factory.getService(ProductService.class);
		/* 注册用户数 */
		userCount = userService.queryTotalRegisterUserCount();

		/* 放款项目数量 */
		bidCount = bidService.queryReleasedBidsNum();
		// 老数据
		double investTotalOld = 35447540.00;

		/* 放款项目总额 */
		totalBorrowAmount = bidService.queryTotalBorrowAmount() + investTotalOld;

		/* 待还总额 */
		totalBillAmount = billService.queryTotalNoRepaymentAmount();

		/* 平台浮动金 */
		platformFloatAmount = userFundService.queryPlatformFloatAmount();
		// 老数据
		double incomeOld = 276938.04;

		/* 给用户来带的收益 */
		income = billInvestService.findAllInterest() + incomeOld;

		double repaymentRate = billService.queryExpireRepaymentRate() * 100;

		/* 到期还款率 */
		expireRepaymentRate = repaymentRate > 0 ? repaymentRate : 100;

		/* 更新时间 */
		date = new Date();

		/* 更新Echart报表数据 */
		echartDataService.saveOrUpdataEchartsData();

		/** 更新 **/
		webProdMap = productservice.queryWebProduct();
	}

}
