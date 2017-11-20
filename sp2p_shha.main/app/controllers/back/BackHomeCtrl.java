package controllers.back;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.shove.Convert;

import common.constants.SettingKey;
import common.utils.ECharts;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import controllers.common.BackBaseController;
import models.common.bean.SupervisorEventLog;
import models.common.entity.t_event_supervisor.Event;
import services.common.ConversionService;
import services.common.UserFundService;
import services.common.UserService;
import services.core.BidService;
import services.core.BillInvestService;
import services.core.BillService;
import services.core.DebtService;
import services.core.StatisticIndexEchartDataService;

/**
 * 后台-首页
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2015年12月17日
 */
public class BackHomeCtrl extends BackBaseController {
		
	protected static UserService userService = Factory.getService(UserService.class);
	
	protected static UserFundService userFundService = Factory.getService(UserFundService.class);
	
	protected static BidService bidService = Factory.getService(BidService.class);
	
	protected static BillInvestService billInvestService = Factory.getService(BillInvestService.class);
	
	protected static StatisticIndexEchartDataService echartDataService = Factory.getService(StatisticIndexEchartDataService.class);
	
	protected static BillService billService = Factory.getService(BillService.class);
	
	protected static ConversionService conversionService = Factory.getService(ConversionService.class);
	
	protected static DebtService debtService = Factory.getService(DebtService.class);

	/**
	 * 后台-首页<br>
	 * 
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月18日
	 */
	public static void backHomePre() {
		int is_statistics_show = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.IS_STATISTICS_SHOW), 0);//前台是否显示统计数据
		renderArgs.put("is_statistics_show", is_statistics_show);
		
		String project_releases_trailer = settingService.findSettingValueByKey(SettingKey.PROJECT_RELEASES_TRAILER);//平台短讯
		renderArgs.put("project_releases_trailer", project_releases_trailer);
		
		PageBean<SupervisorEventLog> pageBean = supervisorService.pageOfAllEventLogs(1, 7, 0, 0, null, null, null);
		renderArgs.put("eventLogs", pageBean.page);
		
		Map countBidInfo = bidService.backCountBidInfo();
		Map countBillInfo = billService.backCountBillInfo();
		countBidInfo.putAll(countBillInfo);
		
		int applyingConv = conversionService.countConversions();//待兑换奖励记录
		
		int applyingDebt = debtService.backCountDebtInfo();//待审转让项目
		
		render(countBidInfo,applyingConv,applyingDebt);
	}

	/**
	 * 后台-首页-ECharts数据加载
	 * 
	 * @param type
	 * @param position
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月25日
	 *
	 */
	public static void showEchartsDataPre(int type,String position){
		//根据type获取不同Echarts数据
		ECharts chartBean = new ECharts();
		if ("left".equals(position)) {
			chartBean = echartDataService.findMembersCount(type);
		}else if("right".equals(position)){
			chartBean = echartDataService.findMoneyNumber(type);
		}
		renderJSON(chartBean);
	}
	
	/**
	 * 首页-首页-前台数据显示<br>
	 * 
	 * @rightID 10100101
	 *
	 * @param flag true前台显示统计数据，false不显示统计数据
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月22日
	 */
	public static void updateIsStatisticsShow(boolean flag) {
		ResultInfo result = new ResultInfo();
		
		if (!settingService.updateIsStatisticsShow(flag)) {
			result.msg = "数据没有更新";
			
			renderJSON(result);
		}
		
		result.code = 1;
		result.msg = "更新成功";
		
		long supervisor_id = getCurrentSupervisorId();
		supervisorService.addSupervisorEvent(supervisor_id, Event.HOME_STATISTICS_SHOW, null);
		
		renderJSON(result);
	}
	
	/**
	 * 后台-首页-项目发布预告
	 * 
	 * @rightID 101002
	 *
	 * @param trailer 新的项目预告
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月22日
	 */
	public static void updateProjectReleasesTrailer(String trailer) {
		ResultInfo result = new ResultInfo();
	
		if (StringUtils.isBlank(trailer) || trailer.length() > 20) {
			result.code = -2;
			result.msg = "你输入的数据有误，请检查后重新输入!";

			renderJSON(result);
		}
		
		boolean flag = settingService.updateProjectReleasesTrailer(trailer);
		
		if (!flag) {
			result.msg = "没有更新成功，请稍后重试!";
			
			renderJSON(result);
		}
		
		result.code = 1;
		result.msg = "更新成功";
		
		long supervisor_id = getCurrentSupervisorId();
		supervisorService.addSupervisorEvent(supervisor_id, Event.HOME_EDIT_TRAILER, null);
	
		renderJSON(result);
	}
}
