package controllers.back.spread;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import services.common.UserService;
import services.core.CashService;
import services.core.CashUserService;
import models.common.bean.ShowUserInfo;
import models.common.entity.t_user;
import models.common.entity.t_event_supervisor.Event;
import models.core.bean.CashRecord;
import models.core.entity.t_cash.CashType;
import models.core.entity.t_cash_task.TaskStatus;
import models.core.entity.t_red_packet_task.TaskSendType;
import models.core.entity.t_cash_user;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import com.shove.Convert;

import common.constants.Constants;
import common.utils.ArrayUtil;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.StrUtil;
import common.utils.excel.ExcelUtils;
import common.utils.jsonAxml.JsonDateValueProcessor;
import common.utils.jsonAxml.JsonDoubleValueProcessor;
import controllers.common.BackBaseController;

/**
 * 后台-推广-现金券
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年2月15日
 */
public class CashCtrl extends BackBaseController {
	
	protected static CashService cashService = Factory.getService(CashService.class);
	
	protected static UserService userService = Factory.getService(UserService.class);
	
	protected static CashUserService cashUserService = Factory.getService(CashUserService.class);
	
	/**
	 * 后台-推广-现金券-发放记录
	 *
	 * @rightID 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月15日
	 */
	public static void showCashRecordPre(int showType, int currPage, int pageSize) {
		int exports = Convert.strToInt(params.get("exports"), 0);
		String userName = params.get("userName");
		
		if (showType < 0 || showType > 3) {
			showType = 0;
		}
		
		//排序栏目
		String orderTypeStr = params.get("orderType");
		int orderType = Convert.strToInt(orderTypeStr, 0);
		if (orderType != 0 && orderType  != 2 && orderType  != 7) {
			orderType = 0;
		}
		renderArgs.put("orderType", orderType);
		
		//排序规则
		String orderValueStr = params.get("orderValue");
		int orderValue = Convert.strToInt(orderValueStr, 0); //0,降序，1,升序
		if (orderValue<0 || orderValue>1) {
			orderValue = 0;
		}
		renderArgs.put("orderValue", orderValue);
		
		PageBean<CashRecord> pageBean = cashUserService.queryUserCashList(showType, currPage, pageSize, exports, userName, orderType, orderValue);
		
		//导出
		if (exports == Constants.EXPORT) {
			List<CashRecord> list = pageBean.page;
			 
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(Constants.DATE_PLUGIN));
			jsonConfig.registerJsonValueProcessor(Double.class, new JsonDoubleValueProcessor(Constants.FINANCE_FORMATE_NORMAL));
			JSONArray arrList = JSONArray.fromObject(list, jsonConfig);
			
			for (Object obj : arrList) {
				JSONObject rate = (JSONObject)obj;	
				rate.put("status", t_cash_user.CashStatus.valueOf(rate.get("status").toString()).value);
			}
			String fileName="现金券发放记录";
			File file = ExcelUtils.export(fileName,
					arrList,
					new String[] {
					"序号", "用户名", "发放渠道", "发放时间", "到期时间", "现金券面值(元)", "使用条件(投资大于)", "创造投资", "状态"},
					new String[] {
					"id", "name", "type_name", "time", "end_time", "amount", "employ_rule", "invest_amount", "status"});
					   
			renderBinary(file, fileName + ".xls");
			
		}
		
		render(pageBean, userName);
	}
	
	/**
	 * 后台-推广-现金券-批量发放
	 *
	 * @rightID 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月15日
	 */
	public static void batchSendCashPre() {
		render();
	}
	
	/**
	 * 后台-推广-现金券-批量发放执行
	 *
	 * @rightID 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月15日
	 */
	public static void batchSendCash(boolean letter, boolean email, boolean sms) {
		String amountStr = params.get("amount");
		String useRuleStr = params.get("useRule");
		String bidPeriodStr = params.get("bidPeriod");//使用规则：标的期限
		String endTimeStr = params.get("endTime");
		String sendTypeStr = params.get("sendType"); //发送对象类型
		String users = params.get("users"); //具体发送对象
		
		if ((!StrUtil.isOneDouble(amountStr)) || StrUtil.isNumLess(amountStr, 0) || StrUtil.isNumMore(amountStr, 1000)) {
			flash.error("现金券金额不正确");
			batchSendCashPre();
		}
		
		if ((!StrUtil.isOneDouble(useRuleStr)) || StrUtil.isNumLess(useRuleStr, 0) || StrUtil.isNumMore(useRuleStr, 999999)) {
			flash.error("最低投资金额不正确");
			batchSendCashPre();
		}
		
		if ((!StrUtil.isNumeric(bidPeriodStr)) || StrUtil.isNumLess(bidPeriodStr, 0) || StrUtil.isNumMore(bidPeriodStr, 12)) {
			flash.error("标的期限不正确");
			batchSendCashPre();
		}
		
		if ((!StrUtil.isNumeric(endTimeStr)) || StrUtil.isNumLess(endTimeStr, 0) || StrUtil.isNumMore(endTimeStr, 365)) {
			flash.error("有效期限不正确");
			batchSendCashPre();
		}
		
		if (StrUtil.isNumLess(sendTypeStr, 0) || StrUtil.isNumMore(sendTypeStr, 1)) {
			flash.error("发送对象不正确");
			batchSendCashPre();
		}
		
		double amount = Convert.strToDouble(amountStr, 0.00);
		double useRule = Convert.strToDouble(useRuleStr, 0.00);
		int bidPeriod = Convert.strToInt(bidPeriodStr, -1);
		int endTime = Convert.strToInt(endTimeStr, -1);
		int sendType = Convert.strToInt(sendTypeStr, -1);
		
		//任务名组装
		StringBuffer name = new StringBuffer();
		Date time = new Date();
		String timeStr = DateUtil.dateToString(time, "yyyyMMddHHmmss");
		name.append(timeStr);
		name.append("Cash");
		
		/*添加现金券对象*/
		ResultInfo result = cashService.saveCash(CashType.BATCH.code, amount, useRule, bidPeriod, endTime, letter, email, sms);
		if (result.code < 1) {
			flash.error(result.msg);
			batchSendCashPre();
		}
		long cashId = (Long)result.obj;
		
		if (sendType == -1) {
			flash.error("发送对象不正确");
			batchSendCashPre();
		}
		
		ResultInfo rsult = new ResultInfo();
		
		if (sendType == 1) {
			if (StringUtils.isBlank(users)) {
				flash.error("请选择至少一个发送对象");
				batchSendCashPre();
			} else {
				//选中用户组装字符串
				users = users.replaceAll("\\s", "");
				String[] user = users.split(",");
				user = ArrayUtil.arrayUnique(user);
				String userIds = "";
				for (String userName : user) {
					if (!userService.isNameExists(userName)) {
						continue;
					}
					result = userService.findUserInfoByName(userName);
					if (result.code < 1) {
						continue;
					}
					t_user userInfo = (t_user) result.obj;
					userIds += userInfo.id + ",";
				}
				
				result = userService.findUserInfoByName(user[user.length-1]);
				t_user lastUser = (t_user) result.obj;
				
				//添加任务表
				rsult = cashService.addCashTask(name.toString(),  Long.parseLong(timeStr), user.length, 0, lastUser.id, 0, TaskStatus.EXECUTE.code, TaskSendType.CHOOSE.code, userIds, cashId);
				
				if (rsult.code < 1) {
					flash.error(rsult.msg);
					batchSendCashPre();
				}
				
				flash.success("添加批量发放现金券定时任务成功");
				batchSendCashPre();
			}
		} else if (sendType == 0) {
			List<t_user> userList = userService.findAllUser();
			
			if (userList == null || userList.size() <= 0) {
				flash.error("发送对象不正确");
				batchSendCashPre();
			}
			
			//添加用于所有用户的现金券发放定时任务
			t_user lastUser = userList.get(userList.size()-1);
			
			rsult = cashService.addCashTask(name.toString(), Long.parseLong(timeStr), userList.size(), 0, lastUser.id, 0, TaskStatus.EXECUTE.code, TaskSendType.MASS.code, null, cashId);
			
			if (rsult.code < 1) {
				flash.error(rsult.msg);
				batchSendCashPre();
			}
			
			flash.success("添加批量发放现金券定时任务成功");
			batchSendCashPre();
		}
		
		flash.error("添加批量发放现金券定时任务失败");
		batchSendCashPre();
	}
	
	/**
	 * 查询用户列表
	 * @param currPage
	 * @param pageSize
	 * @param key
	 */
	public static void showUsersPre(int currPage, int pageSize, String key) {
		if (currPage < 1) {
			currPage = 1;
		}
		
		if (pageSize < 1) {
			pageSize = 10;
		}
		
		flash.put("searchKey", key);
		PageBean<ShowUserInfo> pageBean = userService.pageOfUserInfo(0, currPage, pageSize, 0, 0, key, 0);
		render(pageBean);
	}
	
}
