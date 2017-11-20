package controllers.back.spread;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.shove.Convert;

import common.constants.Constants;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.StrUtil;
import common.utils.excel.ExcelUtils;
import common.utils.jsonAxml.JsonDateValueProcessor;
import common.utils.jsonAxml.JsonDoubleValueProcessor;
import controllers.common.BackBaseController;
import models.common.entity.t_event_supervisor.Event;
import models.ext.cps.bean.CpsSpreadRecord;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import services.common.UserService;
import services.ext.cps.CpsService;

/**
 * 后台-推广-CPS-CPS记录
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年3月15日
 */
public class CpsRecordCtrl extends BackBaseController {

	protected static CpsService cpsService = Factory.getService(CpsService.class);

	protected static UserService userService = Factory.getService(UserService.class);

	/**
	 * 推广记录查询
	 * 
	 * @rightID 705001
	 *
	 * @param currPage 当前页 
	 * @param pageSize 每页条数
	 * @param userName 会员名(被推广人)
	 * @param exports 1：导出  default：不导出
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年3月16日
	 */
	@SuppressWarnings("unchecked")
	public static void showAllCpsRecordsPre(int currPage, int pageSize) {
		if (currPage < 1) {
			currPage = 1;
		}
		if (pageSize < 1) {
			pageSize = 10;
		}
		//会员
		String userName = params.get("userName");
		
		//渠道
		Integer is_spread = params.get("is_spread", Integer.class);
		
		if(is_spread == null) {
			is_spread = 0;
		}
		
		//导出
		int exports = Convert.strToInt(params.get("exports"), 0);
		
		//查询记录
		PageBean<CpsSpreadRecord> page = cpsService.pageOfCpsSpreadRecord(currPage, pageSize, userName, is_spread, exports);
		
		//导出
		if(exports == Constants.EXPORT){
			List<CpsSpreadRecord> list = page.page;
					 
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(Constants.DATE_TIME_FORMATE));
			jsonConfig.registerJsonValueProcessor(Double.class, new JsonDoubleValueProcessor(Constants.FINANCE_FORMATE_NORMAL));
			JSONArray arrList = JSONArray.fromObject(list, jsonConfig);		
			
			File file = ExcelUtils.export("CPS记录",arrList,
				new String[] {"编号", "会员", "累计理财", "推广人", "推广人电话", "是否为渠道", "累计返佣", "推广时间"},
				new String[] {"user_id","user_name", "total_invest", "spreader_name", "spreader_mobile", "is_spread", "total_rebate", "time"});
					   
			renderBinary(file, "CPS推广记录.xls");
		}
		
		//累计投资总额，累计返佣总额
		Map<String, Object> totalAmt = cpsService.findTotalCpsAmount(userName);
		
		render(page,userName,totalAmt,is_spread);
	}
	
	public static void manageCpsUsersPre() {
		render();
	}
	
	public static void doSpreadUser() {
		
		ResultInfo result = new ResultInfo();
		
		String mobiles = params.get("inputText");
		
		if(!StrUtil.isVaildMobiles(mobiles)) {
			result.code = -1;
			result.msg = "渠道号码不能为空或者格式不准确";
			renderJSON(result);
		}
 		
		Integer flag = params.get("flag", Integer.class);
		
		if(flag == null || flag > 1 || flag < 0) {
			flag = 0;
		}
		
		if(mobiles.charAt(mobiles.length() - 1) == ',') {
			mobiles.substring(0, mobiles.length() - 1);
		}
		
		result = userService.setSpreadUser(mobiles, flag);
		
		if(result.code == -1) {
			result.msg = "添加渠道失败";
			renderJSON(result);
		}
		
		long supervisor_id = getCurrentSupervisorId();
		supervisorService.addSupervisorEvent(supervisor_id, Event.SPREAD_EDIT_CPS, null);
		
		renderJSON(result);
		
	}
	
}
