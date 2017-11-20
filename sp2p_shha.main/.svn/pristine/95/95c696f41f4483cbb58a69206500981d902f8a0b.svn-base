package controllers.back;

import java.io.File;
import java.util.Date;
import java.util.List;

import com.shove.Convert;

import common.constants.Constants;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.excel.ExcelUtils;
import common.utils.jsonAxml.JsonDateValueProcessor;
import common.utils.jsonAxml.JsonDoubleValueProcessor;
import controllers.common.BackBaseController;
import models.common.bean.AmountTotal;
import models.common.bean.ShowUserInfo;
import models.common.bean.UserStatistics;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import services.common.AmountTotalService;
import services.common.UsersStatisticsService;

public class BackStatisticsCtrl extends BackBaseController {
	protected static UsersStatisticsService usersStatisticsService = Factory.getService(UsersStatisticsService.class);
	protected static AmountTotalService amountTotalService = Factory.getService(AmountTotalService.class);

	public static void usersStatisticsPre(int showType, int currPage, int pageSize) {
		/* 搜索条件 */
		String userName = params.get("userName");
		int exports = Convert.strToInt(params.get("exports"), 0);

		if (showType < 0 || showType > 2) {
			showType = 0;
		}
		renderArgs.put("showType", showType);
		/* 排序栏目 */
		String orderTypeStr = params.get("orderType");
		int orderType = Convert.strToInt(orderTypeStr, 0);
		if (orderType < 0 || orderType > 5) {
			orderType = 0;
		}
		renderArgs.put("orderType", orderType);
		/* 排序方式 */
		String orderValueStr = params.get("orderValue");
		int orderValue = Convert.strToInt(orderValueStr, 0);// 0,降序，1,升序
		if (orderValue < 0 || orderValue > 1) {
			orderValue = 0;
		}
		renderArgs.put("orderValue", orderValue);
		PageBean<UserStatistics> pageBean = usersStatisticsService.findUserStatistics(showType, currPage, pageSize,
				orderType, orderValue, userName, exports);
		// 导出
		if (exports == Constants.EXPORT) {
			List<UserStatistics> list = pageBean.page;

			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(Constants.DATE_TIME_FORMATE));
			jsonConfig.registerJsonValueProcessor(Double.class,
					new JsonDoubleValueProcessor(Constants.FINANCE_FORMATE_NORMAL));
			JSONArray arrList = JSONArray.fromObject(list, jsonConfig);

			String fileName = "会员投资统计表";
			switch (showType) {
			case 1:
				fileName = "会员首投统计表";
				break;
			case 2:
				fileName = "会员复投统计表";
				break;
			default:
				fileName = "会员投资统计表";
				break;
			}

			File file = ExcelUtils.export(fileName, arrList,
					new String[] { "编号", "会员", "身份证号", "投资金额", "利息", "红包金额", "时间", "手机号" }, new String[] { "id", "name",
							"id_number", "amount", "correct_interest", "red_packet_amount", "time", "mobile" });

			renderBinary(file, fileName + ".xls");
		}
		AmountTotal amountTotal = amountTotalService.amountTotal(showType,userName);
		render(pageBean,amountTotal,userName);
	}

}
