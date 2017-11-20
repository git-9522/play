package controllers.back.spread;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.shove.Convert;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.ArrayUtil;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.StrUtil;
import common.utils.excel.ExcelUtils;
import common.utils.jsonAxml.JsonDateValueProcessor;
import common.utils.jsonAxml.JsonDoubleValueProcessor;
import controllers.common.BackBaseController;
import models.common.bean.ShowUserInfo;
import models.common.entity.t_event_supervisor.Event;
import models.common.entity.t_user;
import models.core.bean.RedpacketRecord;
import models.core.entity.t_red_packet;
import models.core.entity.t_red_packet.RedpacketType;
import models.core.entity.t_red_packet_task.TaskSendType;
import models.core.entity.t_red_packet_task.TaskStatus;
import models.core.entity.t_red_packet_user;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import services.common.UserService;
import services.core.RedpacketService;
import services.core.RedpacketUserService;

/**
 * 后台-推广-红包
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年2月15日
 */
public class RedpacketCtrl extends BackBaseController {

	protected static RedpacketService redpacketService = Factory.getService(RedpacketService.class);
	
	protected static UserService userService = Factory.getService(UserService.class);
	
	protected static RedpacketUserService redpacketUserService = Factory.getService(RedpacketUserService.class);
	
	/**
	 * 后台-推广-红包-红包规则
	 *
	 * @rightID 701001
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月15日
	 */
	public static void showRedpacketsPre() {
		List<t_red_packet> redPackets = redpacketService.findAllRedPacketRules(t_red_packet.RedpacketType.REGISTER.code, true);
		
		Map<String, Object> maps = new HashMap<String, Object>();
		if (redPackets == null || redPackets.size() <= 0) {
			render(maps, redPackets);
		}
		
		maps.put("letter", redpacketService.findByType(t_red_packet.RedpacketType.REGISTER.code).is_send_letter);
		maps.put("sms", redpacketService.findByType(t_red_packet.RedpacketType.REGISTER.code).is_send_sms);
		maps.put("email", redpacketService.findByType(t_red_packet.RedpacketType.REGISTER.code).is_send_email);
		
		render(maps, redPackets);
	}
	
	/**
	 * 后台-推广-红包-红包规则-设置红包规则
	 *
	 * @rightID 701002
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月15日
	 */
	public static void editRedpackets(boolean letter, boolean email, boolean sms) {
		checkAuthenticity();
		
		List<Object> idList = redpacketService.findAllRedPacketRuleId(t_red_packet.RedpacketType.REGISTER.code, true);
////		if (idList == null || idList.size() <= 0) {
////			error500();
////		}
//		
		long redPacketRuleId = 0L;
		String amountStr = "";
		String useRuleStr = "";
		String endTimeStr = "";
		String bidPeriodStr = "";
		double amount = 0.0;
		double useRule = 0.0;
		int endTime = 0;
		int bidPeriod = 0;
		
		ResultInfo result = new ResultInfo();
		
		for (Object object : idList) {
			redPacketRuleId = Convert.strToLong(object.toString(), 0L);
			amountStr = params.get("amount" + redPacketRuleId);
			useRuleStr = params.get("useRule" + redPacketRuleId);
			endTimeStr = params.get("endTime" + redPacketRuleId);
			bidPeriodStr = params.get("bidPeriod" + redPacketRuleId);
			
			if ((!StrUtil.isOneDouble(amountStr)) || StrUtil.isNumLess(amountStr, 0) || StrUtil.isNumMore(amountStr, 1000)) {
				flash.error("开户红包金额不正确");
				showRedpacketsPre();
			}
			
			if ((!StrUtil.isOneDouble(useRuleStr)) || StrUtil.isNumLess(useRuleStr, 0) || StrUtil.isNumMore(useRuleStr, 999999)) {
				flash.error("最低投资金额不正确");
				showRedpacketsPre();
			}
			
			if ((!StrUtil.isNumeric(bidPeriodStr)) || StrUtil.isNumLess(bidPeriodStr, 0) || StrUtil.isNumMore(bidPeriodStr, 12)) {
				flash.error("标的期限不正确");
				showRedpacketsPre();
			}
			
			if ((!StrUtil.isNumeric(endTimeStr)) || StrUtil.isNumLess(endTimeStr, 0) || StrUtil.isNumMore(endTimeStr, 365)) {
				flash.error("有效期限不正确");
				showRedpacketsPre();
			}
			
			amount = Convert.strToDouble(amountStr, 0.0);
			useRule = Convert.strToDouble(useRuleStr, 0.0);
			endTime = Convert.strToInt(endTimeStr, 0);
			bidPeriod = Convert.strToInt(bidPeriodStr, 0);
			result = redpacketService.updateRedPacketRule(redPacketRuleId, amount, useRule,bidPeriod, endTime, letter, email, sms);
			if (result.code < 1) {
				LoggerUtil.error(true, "更新开户红包规则出错,数据回滚");
				flash.error("更新开户红包规则失败");
				showRedpacketsPre();
			}
		}
		
		amountStr = params.get("amount");
		useRuleStr = params.get("useRule");
		endTimeStr = params.get("endTime");
		bidPeriodStr = params.get("bidPeriod");
		
		if (StringUtils.isNotBlank(amountStr) && StringUtils.isNotBlank(amountStr) && StringUtils.isNotBlank(amountStr)) {
			if ((!StrUtil.isOneDouble(amountStr)) || StrUtil.isNumLess(amountStr, 0) || StrUtil.isNumMore(amountStr, 1000)) {
				flash.error("开户红包金额不正确");
				showRedpacketsPre();
			}
			
			if ((!StrUtil.isOneDouble(useRuleStr)) || StrUtil.isNumLess(useRuleStr, 0) || StrUtil.isNumMore(useRuleStr, 999999)) {
				flash.error("最低投资金额不正确");
				showRedpacketsPre();
			}
			
			if ((!StrUtil.isNumeric(bidPeriodStr)) || StrUtil.isNumLess(bidPeriodStr, 0) || StrUtil.isNumMore(bidPeriodStr, 12)) {
				flash.error("标的期限不正确");
				showRedpacketsPre();
			}
			
			if ((!StrUtil.isNumeric(endTimeStr)) || StrUtil.isNumLess(endTimeStr, 0) || StrUtil.isNumMore(endTimeStr, 365)) {
				flash.error("有效期限不正确");
				showRedpacketsPre();
			}
			
			amount = Convert.strToDouble(amountStr, 0.0);
			useRule = Convert.strToDouble(useRuleStr, 0.0);
			endTime = Convert.strToInt(endTimeStr, 0);
			bidPeriod = Convert.strToInt(bidPeriodStr, 0);
					
			result = redpacketService.addRedPacketRule(t_red_packet.RedpacketType.REGISTER.code, amount, useRule,bidPeriod, endTime, letter, email, sms);
			if (result.code < 1) {
				LoggerUtil.error(true, "更新开户红包规则出错,数据回滚");
				flash.error("更新开户红包规则失败");
				showRedpacketsPre();
			}
		}
		
		Long supervisorId = getCurrentSupervisorId();
		supervisorService.addSupervisorEvent(supervisorId, Event.SPREAD_EDIT_REDPACKEDT, null);
		flash.success("更新开户红包规则成功");
		showRedpacketsPre();
	}
	
	/**
	 * 后台-推广-红包-发放记录
	 *
	 * @rightID 701003
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月15日
	 */
	@SuppressWarnings("unchecked")
	public static void showRedpacketsRecordPre(int showType, int currPage, int pageSize) {
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
		
		PageBean<RedpacketRecord> pageBean = redpacketUserService.queryUserRedpacketList(showType, currPage, pageSize, exports, userName, orderType, orderValue);
		
		//导出
		if (exports == Constants.EXPORT) {
			List<RedpacketRecord> list = pageBean.page;
			 
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(Constants.DATE_PLUGIN));
			jsonConfig.registerJsonValueProcessor(Double.class, new JsonDoubleValueProcessor(Constants.FINANCE_FORMATE_NORMAL));
			JSONArray arrList = JSONArray.fromObject(list, jsonConfig);
			
			for (Object obj : arrList) {
				JSONObject rate = (JSONObject)obj;
				rate.put("status", t_red_packet_user.RedpacketStatus.valueOf(rate.get("status").toString()).value);
			}
			String fileName="红包发放记录";
			File file = ExcelUtils.export(fileName,
					arrList,
					new String[] {
					"序号", "用户名", "发放渠道", "发放时间", "到期时间", "红包面值(元)", "使用条件(投资大于)", "创造投资", "状态"},
					new String[] {
					"id", "name", "type_name", "time", "end_time", "amount", "employ_rule", "invest_amount", "status"});
					   
			renderBinary(file, fileName + ".xls");
		}
		
		render(pageBean, userName);
	}
	
	/**
	 * 后台-推广-红包-批量发放
	 *
	 * @rightID 701004
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月15日
	 */
	public static void batchSendRedPacketPre() {
		render();
	}
	
	/**
	 * 后台-推广-红包-批量发放执行
	 *
	 * @rightID 701005
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月15日
	 */
	public static void batchSendRedPacket(boolean letter, boolean email, boolean sms) {
		String amountStr = params.get("amount");
		String useRuleStr = params.get("useRule");
		String bidPeriodStr = params.get("bidPeriod");//使用规则：标的期限
		String endTimeStr = params.get("endTime");
		String sendTypeStr = params.get("sendType"); //发送对象类型
		String users = params.get("users"); //具体发送对象
		
		if ((!StrUtil.isOneDouble(amountStr)) || StrUtil.isNumLess(amountStr, 0) || StrUtil.isNumMore(amountStr, 1000)) {
			flash.error("红包金额不正确");
			batchSendRedPacketPre();
		}
		
		if ((!StrUtil.isOneDouble(useRuleStr)) || StrUtil.isNumLess(useRuleStr, 0) || StrUtil.isNumMore(useRuleStr, 999999)) {
			flash.error("最低投资金额不正确");
			batchSendRedPacketPre();
		}
		
		if ((!StrUtil.isNumeric(bidPeriodStr)) || StrUtil.isNumLess(bidPeriodStr, 0) || StrUtil.isNumMore(bidPeriodStr, 12)) {
			flash.error("标的期限不正确");
			showRedpacketsPre();
		}
		
		if ((!StrUtil.isNumeric(endTimeStr)) || StrUtil.isNumLess(endTimeStr, 0) || StrUtil.isNumMore(endTimeStr, 365)) {
			flash.error("有效期限不正确");
			batchSendRedPacketPre();
		}
		
		if (StrUtil.isNumLess(sendTypeStr, 0) || StrUtil.isNumMore(sendTypeStr, 1)) {
			flash.error("发送对象不正确");
			batchSendRedPacketPre();
		}
		
		double amount = Convert.strToDouble(amountStr, 0.00);
		double useRule = Convert.strToDouble(useRuleStr, 0.00);
		int endTime = Convert.strToInt(endTimeStr, -1);
		int sendType = Convert.strToInt(sendTypeStr, -1);
		int bidPeriod = Convert.strToInt(bidPeriodStr, -1);
		
		//任务名组装
		StringBuffer name = new StringBuffer();
		Date time = new Date();
		String timeStr = DateUtil.dateToString(time, "yyyyMMddHHmmss");
		name.append(timeStr);
		name.append("RedPacket");
		
		//添加红包对象
		ResultInfo result = redpacketService.saveRedpacket(RedpacketType.BATCH.code, amount, useRule, bidPeriod, endTime, letter, email, sms);
		if (result.code < 1) {
			flash.error(result.msg);
			batchSendRedPacketPre();
		}
		long redPacketId = (Long)result.obj;
		
		if (sendType == -1) {
			flash.error("发送对象不正确");
			batchSendRedPacketPre();
		}
		
		ResultInfo rsult = new ResultInfo();
		
		if (sendType == 1) {
			if (StringUtils.isBlank(users)) {
				flash.error("请选择至少一个发送对象");
				batchSendRedPacketPre();
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
				rsult = redpacketService.addRedPacketTask(name.toString(), Long.parseLong(timeStr), user.length, 0, lastUser.id, 0, TaskStatus.EXECUTE.code, TaskSendType.CHOOSE.code, userIds, redPacketId);
				
				if (rsult.code < 1) {
					flash.error(rsult.msg);
					batchSendRedPacketPre();
				}
				
				flash.success("添加批量发放红包定时任务成功");
				batchSendRedPacketPre();
			}
		} else if (sendType == 0) {
			
			List<t_user> userList = userService.findAllUser();
			
			if (userList == null || userList.size() <= 0) {
				flash.error("发送对象不正确");
				batchSendRedPacketPre();
			}
			
			//添加群发任务
			t_user lastUser = userList.get(userList.size()-1);
			
			rsult = redpacketService.addRedPacketTask(name.toString(), Long.parseLong(timeStr), userList.size(), 0, lastUser.id, 0, TaskStatus.EXECUTE.code, TaskSendType.MASS.code, null, redPacketId);
			if (rsult.code < 1) {
				flash.error(rsult.msg);
				batchSendRedPacketPre();
			}
			
			flash.success("添加批量发放红包定时任务成功");
			batchSendRedPacketPre();
		}
		
		flash.error("添加批量发放红包定时任务失败");
		batchSendRedPacketPre();
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
	
	/**
	 * 后台-推广-红包-开户红包-更新启用状态
	 *
	 * @rightID 701006
	 * 
	 * @param sign 红包规则id加密串
	 * @param isUse 是否启用
	 *
	 * @author yanpengfei
	 * @createDate 2016年12月14日
	 */
	public static void changeIsUseStatus(String sign, boolean isUse) {
		ResultInfo result = new ResultInfo();
		
		List<t_red_packet> redPackets = redpacketService.findAllRedPacketRules(t_red_packet.RedpacketType.REGISTER.code, true);
		if (redPackets != null && redPackets.size() <= 1) {
			result.code = -1;
			result.msg = "至少需要保留一个开户红包";
			renderJSON(result);
		}
		
		result = Security.decodeSign(sign, Constants.RED_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			renderJSON(result);
		}
		
		long id = Convert.strToLong((String) result.obj, 0L);
		if (id <= 0L) {
			result.code = -1;
			result.msg = "数据异常";
			renderJSON(result);
		}
		
		int rows = redpacketService.changeIsUseStatus(id, isUse);
		if (rows <= 0) {
			result.code = -1;
			result.msg = "删除失败";
			renderJSON(result);
		}
		
		result.code = 1;
		result.msg = "";
		
		renderJSON(result);
	}
	
}
