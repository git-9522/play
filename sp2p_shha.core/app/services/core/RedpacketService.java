package services.core;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import common.enums.NoticeScene;
import common.utils.ArrayUtil;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import daos.core.RedPacketSendingDao;
import daos.core.RedPacketTaskDao;
import daos.core.RedpacketDao;
import daos.core.RedpacketUserDao;
import models.common.entity.t_user;
import models.core.entity.t_red_packet;
import models.core.entity.t_red_packet_sending;
import models.core.entity.t_red_packet_task;
import models.core.entity.t_red_packet_user;
import play.Logger;
import services.base.BaseService;
import services.common.NoticeService;
import services.common.UserService;

/**
 * 红包service
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年2月16日
 */
public class RedpacketService extends BaseService<t_red_packet> {

	protected RedpacketDao redPacketDao	 = null;
	
	protected RedpacketUserDao redpacketUserDao = Factory.getDao(RedpacketUserDao.class);
	
	protected NoticeService noticeService = Factory.getService(NoticeService.class);
	
	protected UserService userService = Factory.getService(UserService.class);
	
	protected RedPacketSendingDao redPacketSendingDao = Factory.getDao(RedPacketSendingDao.class);
	
	protected RedPacketTaskDao redPacketTaskDao = Factory.getDao(RedPacketTaskDao.class);
	
	protected RedpacketService(){
		this.redPacketDao =  Factory.getDao(RedpacketDao.class);
		super.basedao = this.redPacketDao;
	}
	
	/**
	 * 更新红包规则
	 *
	 * @param redPacketRuleId 红包规则id
	 * @param amount 红包金额
	 * @param useRule 使用规则
	 * @param bidPeriod 使用规则：标的期限
	 * @param endTime 有效期限
	 * @param letter 是否启用站内信通知
	 * @param email 是否启用邮件通知
	 * @param sms 是否启用手机通知
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月15日
	 */
	public ResultInfo updateRedPacketRule(long redPacketRuleId, double amount, double useRule,int bidPeriod, int endTime, 
			boolean letter, boolean email, boolean sms) {
		ResultInfo result = new ResultInfo();
		
		int rows = redPacketDao.updateRedPacketRule(redPacketRuleId, amount, useRule,bidPeriod, endTime, letter, email, sms);
		
		if (rows <= 0) {
			result.code = -1;
			result.msg = "更新失败";
		}
		
		result.code = 1;
		result.msg = "更新成功";
		
		return result;
	}

	/**
	 * 更新红包任务
	 *
	 * @param userId 当前发放任务中最后的Id
	 * @param taskId 修改任务Id
	 * @return
	 *
	 */
	public ResultInfo updateRedPacketTaskUser(long userId, long taskId) {
		ResultInfo result = new ResultInfo();
		
		int rows = redPacketTaskDao.updateRedPacketTaskUser(userId, taskId);
		
		if (rows < 1) {
			result.code = -1;
			result.msg = "更新失败";
		}
		
		result.code = 1;
		result.msg = "更新成功";
		
		return result;
	}
	
	/**
	 * 根据红包类型查询某个红包
	 *
	 * @param type 红包类型
	 */
	public t_red_packet findByType(int type) {
		t_red_packet red = redPacketDao.findByColumn(" type = ? and is_use = ?", type, true);
		
		return red;
	}
	
	/**
	 * 根据红包类型查询某个红包(用于发送红包)
	 *
	 * @return
	 */
	public t_red_packet findByTypeForSend(int type) {
		t_red_packet redPacket = redPacketDao.findByColumn(" type = ? and amount > 0 ", type);
		
		return redPacket;
	}
	
	/**
	 * 添加用于所有用户的红包发放定时任务
	 * 
	 * @param userList
	 * @param name
	 * @param amount
	 * @param useRule
	 * @param bidPeriod 使用规则：标的期限
	 * @param endTime
	 * @param letter
	 * @param email
	 * @param sms
	 * 
	 * @return
	 */
	public ResultInfo addSendTaskUseOfAllUser(List<t_user> userList, String name, double amount, double useRule,int bidPeriod, int endTime, boolean letter, 
			boolean email, boolean sms) {
		ResultInfo result = new ResultInfo();
		
		if (StringUtils.isBlank(name)) {
			result.code = -1;
			result.msg = "红包名称缺失";
			
			return result;
		}
		
		if (amount <= 0.00 || amount > 1000) {
			result.code = -1;
			result.msg = "红包金额不正确";
			
			return result;
		}
		
		if (useRule <= 0.00 || useRule > 999999) {
			result.code = -1;
			result.msg = "最低投资金额不正确";
			
			return result;
		}
		
		if (endTime <= 0 || endTime > 365) {
			result.code = -1;
			result.msg = "有效期限不正确";
			
			return result;
		}
		
		for (t_user user : userList) {
			t_red_packet_sending send = new t_red_packet_sending();
			send.user_id = user.id;
			send.time = new Date();
			send.name = name;
			send.amount = amount;
			send.use_rule = useRule;
			send.bid_period = bidPeriod;
			send.end_time = endTime;
			send.is_send_letter = letter;
			send.is_send_email = email;
			send.is_send_sms = sms;
			send.is_send = false;
			send.send_time = null;
			
			if (!redPacketSendingDao.save(send)) {
				result.code = -1;
				result.msg = "添加批量发放红包定时任务失败";
				
				return result;
			}
		}
		
		result.code = 1;
		result.msg = "";
		
		return result;
	}
	
	/**
	 * 添加用于所有用户的红包发放定时任务
	 * 
	 * @param user 发送的用户
	 * @param redPacketId 发送的红包
	 * @param sign 关联任务表唯一索引
	 * 
	 * @return
	 */
	public ResultInfo saveSending(t_user user, long redPacketId, String sign) {
		ResultInfo result = new ResultInfo();
		
		if (user == null) {
			result.code = -1;
			result.msg = "用户不存在";
			
			return result;
		}
		
		t_red_packet redPacket = this.findByID(redPacketId);
		
		if (redPacket == null) {
			result.code = -1;
			result.msg = "红包不存在";
			
			return result;
		}
		
		t_red_packet_sending send = new t_red_packet_sending();
		send.user_id = user.id;
		send.time = new Date();
		send.name = "活动发放";
		send.amount = redPacket.amount;
		send.use_rule = redPacket.use_rule;
		send.bid_period = redPacket.bid_period;
		send.end_time = redPacket.end_time;
		send.is_send_letter = redPacket.is_send_letter;
		send.is_send_email = redPacket.is_send_email;
		send.is_send_sms = redPacket.is_send_sms;
		send.is_send = false;
		send.send_time = null;
		send.task_sign = sign;
		
		if (!redPacketSendingDao.save(send)) {
			result.code = -1;
			result.msg = "添加批量发放红包定时任务失败";
			
			return result;
			
		}
		
		result.code = 1;
		result.msg = "";
		
		return result;
	}
	
	/**
	 * 添加用于选中用户的红包发放定时任务
	 * 
	 * @param users
	 * @param name
	 * @param amount
	 * @param useRule
	 * @param bidPeriod 使用规则：标的期限
	 * @param endTime
	 * @param letter
	 * @param email
	 * @param sms
	 * 
	 * @return
	 */
	public ResultInfo addSendTaskUseOfSelectUser(String users, String name, double amount, double useRule,int bidPeriod, int endTime, boolean letter, 
			boolean email, boolean sms) {
		ResultInfo result = new ResultInfo();
		
		if (StringUtils.isBlank(name)) {
			result.code = -1;
			result.msg = "红包名称缺失";
			
			return result;
		}
		
		if (amount <= 0.00 || amount > 1000) {
			result.code = -1;
			result.msg = "红包金额不正确";
			
			return result;
		}
		
		if (useRule <= 0.00 || useRule > 999999) {
			result.code = -1;
			result.msg = "最低投资金额不正确";
			
			return result;
		}
		
		if (endTime <= 0 || endTime > 365) {
			result.code = -1;
			result.msg = "有效期限不正确";
			
			return result;
		}
		
		users = users.replaceAll("\\s", "");
		String[] user = users.split(",");
		user = ArrayUtil.arrayUnique(user);
		
		for (String userName : user) {
			if (!userService.isNameExists(userName)) {
				continue;
			}
			
			result = userService.findUserInfoByName(userName);
			if (result.code < 1) {
				continue;
			}
			
			t_user userInfo = (t_user) result.obj;
			t_red_packet_sending send = new t_red_packet_sending();
			send.user_id = userInfo.id;
			send.time = new Date();
			send.name = name;
			send.amount = amount;
			send.use_rule = useRule;
			send.bid_period = bidPeriod;
			send.end_time = endTime;
			send.is_send_letter = letter;
			send.is_send_email = email;
			send.is_send_sms = sms;
			send.is_send = false;
			send.send_time = null;
			
			if (!redPacketSendingDao.save(send)) {
				result.code = -1;
				result.msg = "添加批量发放红包定时任务失败";
				
				return result;
			}
		}
		
		result.code = 1;
		result.msg = "";
		
		return result;
	}
	
	/**
	 * 查询未发放的红包
	 * 
	 * @return
	 */
	public List<t_red_packet_sending> queryUnSendRedPacket() {
		
		return redPacketSendingDao.queryUnSendRedPacket();
	}

	/**
	 * 查询未发放的红包任务
	 * 
	 * @return
	 */
	public t_red_packet_task queryUnSendTask() {
		
		return redPacketTaskDao.queryUnSendTask();
	}
	
	/**
	 * 更新群发红包的状态
	 * 
	 * @param sendId 群发红包id
	 */
	public int updateRedPacketSendStatus(long sendId) {
		
		return redPacketSendingDao.updateRedPacketSendStatus(sendId);
	}

	/**
	 * 更新群发红包的数量
	 * 
	 * @param taskId 群发红包任务id
	 */
	public ResultInfo updateRedPacketTaskCount(long taskId) {
		ResultInfo result = new ResultInfo();
		
		int rows = redPacketTaskDao.updateRedPacketTaskCount(taskId);
		
		if (rows < 1) {
			result.code = -1;
			result.msg = "更新失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "更新成功";
		
		return result;
	}
	
	/**
	 * 通过红包ID查询红包信息
	 * 
	 * @param redPacketId 红包id
	 * 
	 * @return
	 */
	public t_red_packet findRedPacketById(long redPacketId) {
		
		return redPacketDao.findByID(redPacketId);
	}
	
	/**
	 * 发送红包(因需求变更该方法不再使用)
	 * 
	 * @param userId 用户id
	 * @param type 红包类型
	 * @param name 红包名称
	 */
	@Deprecated
	public void sendRedToUser(long userId, int type, String name) {
		UserService userService = Factory.getService(UserService.class);
		t_user user = userService.findByID(userId);
		if (user == null) {
			LoggerUtil.info(true, "用户信息错误");
			
			return;
		}
		
		t_red_packet redPacket = findByTypeForSend(type);
		if (redPacket == null) {
			LoggerUtil.info(true, "无对应红包规则");
			
			return;
		}
		
		t_red_packet_user send = new t_red_packet_user();
		send.time = new Date();
		send.user_id = userId;
		send.name = name;
		send.type = type;
		send.amount = redPacket.amount;
		send.setStatus(t_red_packet_user.RedpacketStatus.UNUSED);
		send.use_rule = redPacket.use_rule;
		send.end_time = DateUtil.addDay(new Date(), redPacket.end_time);
		send.red_packet_id = redPacket.id;
		send.mark = user.id + "" + redPacket.id + "R" + type + DateUtil.dateToString(new Date(), "yyyyMMddhhmmssss");
		
		if (!redpacketUserDao.save(send)) {
			LoggerUtil.info(true, "发送红包失败");
			
			return;
		}
		
		/** 发送通知  */
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("user_name", user.name);
		param.put("amount", send.amount);
		
		noticeService.sendSysNoticeForRed(user.id, NoticeScene.REDPACKET_IPS, param, redPacket.is_send_email, redPacket.is_send_sms, redPacket.is_send_letter);
	}
	
	/**
	 * 查询所有红包规则
	 * 
	 * @param type 红包类型
	 * @param isUse 是否启用
	 * @return
	 */
	public List<t_red_packet> findAllRedPacketRules(int type, boolean isUse) {
		
		return redPacketDao.findAllRedPacketRules(type, isUse);
	}
	
	/**
	 * 查询所有红包规则数量
	 * 
	 * @param type 红包类型
	 * @param isUse 是否启用
	 * @return
	 */
	public int findAllRedPacketRuleCount(int type, boolean isUse) {
		
		return redPacketDao.findAllRedPacketRuleCount(type, isUse);
	}
	
	/**
	 * 更新启用状态
	 * 
	 * @param id 红包规则id
	 * @param isUse 是否启用
	 * @return
	 */
	public int changeIsUseStatus(long id, boolean isUse) {
		
		return redPacketDao.changeIsUseStatus(id, isUse);
	}
	
	/**
	 * 查询所有红包规则id
	 * 
	 * @param type 红包类型
	 * @param isUse 是否启用
	 * @return
	 */
	public List<Object> findAllRedPacketRuleId(int type, boolean isUse) {
		
		return redPacketDao.findAllRedPacketRuleId(type, isUse);
	}
	
	/**
	 * 添加红包规则
	 * 
	 * @param type 红包类型
	 * @param amount 红包金额
	 * @param useRule 使用规则
	 * @param bidPeriod 使用规则：标的期限
	 * @param endTime 有效期限
	 * @param letter 是否启用站内信通知
	 * @param email 是否启用邮件通知
	 * @param sms 是否启用手机通知
	 */
	public ResultInfo addRedPacketRule(int type, double amount, double useRule,int bidPeriod, int endTime, 
			boolean letter, boolean email, boolean sms) {
		ResultInfo result = new ResultInfo();
		
		t_red_packet redPacket = new t_red_packet();
		redPacket.time = new Date();
		redPacket.type = type;
		redPacket.amount = amount;
		redPacket.use_rule = useRule;
		redPacket.bid_period = bidPeriod;
		redPacket.end_time = endTime;
		redPacket.is_send_letter = letter;
		redPacket.is_send_email = email;
		redPacket.is_send_sms = sms;
		redPacket.is_use = true;
		
		if (!redPacketDao.save(redPacket)) {
			result.code = -1;
			result.msg = "添加红包规则失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "";
		
		return result;
	}
	
	/**
	 * 给用户发送开户红包
	 * 
	 * @param userId 用户id
	 * @param type 红包类型
	 * @param name 红包名称
	 */
	public void sendRegisterRedPacketToUser(long userId, int type, String name) {
		UserService userService = Factory.getService(UserService.class);
		
		t_user user = userService.findByID(userId);
		if (user == null) {
			Logger.info("发放开户红包时，查询用户信息错误");
			
			return;
		}
		
		if (user.is_old) {
			Logger.info("老平台用户不发放开户红包");
			
			return;
		}
		
		List<t_red_packet> redPacketList = findAllRedPacketRules(type, true);
		if (redPacketList == null || redPacketList.size() <= 0) {
			Logger.info("没有处于启用状态的开户红包，不进行发放");
			
			return;
		}
		
		t_red_packet_user send = null;
		Map<String, Object> param = null;
		
		for (t_red_packet redPacket : redPacketList) {
			send = new t_red_packet_user();
			send.time = new Date();
			send.user_id = userId;
			send.name = name;
			send.type = type;
			send.amount = redPacket.amount;
			send.setStatus(t_red_packet_user.RedpacketStatus.UNUSED);
			send.use_rule = redPacket.use_rule;
			send.bid_period = redPacket.bid_period;
			send.end_time = DateUtil.addDay(new Date(), redPacket.end_time);
			send.red_packet_id = redPacket.id;
			send.mark = user.id + "" + redPacket.id + "R" + type + DateUtil.dateToString(new Date(), "yyyyMMddhhmmssss");
			
			if (!redpacketUserDao.save(send)) {
				LoggerUtil.info(true, "发放开户红包失败，数据回滚");
				
				return;
			}
			
			param = new HashMap<String, Object>();
			param.put("user_name", user.name);
			param.put("amount", send.amount);
			
			noticeService.sendSysNoticeForRed(user.id, NoticeScene.REDPACKET_IPS, param, redPacket.is_send_email, 
					redPacket.is_send_sms, redPacket.is_send_letter);
		}
	}

	/**
	 * 添加用于选中用户的红包发放定时任务
	 * 
	 * @param users
	 * @param name
	 * @param amount
	 * @param useRule
	 * @param bidPeriod 使用规则：标的期限
	 * @param endTime
	 * @param letter
	 * @param email
	 * @param sms
	 * 
	 * @return
	 */
	public ResultInfo saveRedpacket(int type, double amount, double useRule,int bidPeriod, int endTime, 
			boolean letter, boolean email, boolean sms) {
		
		ResultInfo result = new ResultInfo();
		
		t_red_packet redPacket = new t_red_packet();
		redPacket.time = new Date();
		redPacket.type = type;
		redPacket.amount = amount;
		redPacket.use_rule = useRule;
		redPacket.bid_period = bidPeriod;
		redPacket.end_time = endTime;
		redPacket.is_send_letter = letter;
		redPacket.is_send_email = email;
		redPacket.is_send_sms = sms;
		redPacket.is_use = true;
		
		t_red_packet red_packet = redPacketDao.saveT(redPacket);
		if (red_packet == null) {
			result.code = -1;
			result.msg = "添加红包规则失败";
			
			return result;
		}
		
		result.obj = red_packet.id;
		result.code = 1;
		result.msg = "";
		return result;
	}
	
	
	/**
	 * 记录群发红包任务
	 * 
	 * @param name 任务名
	 * @param identification 任务标识
	 * @param total_number 发放总人数
	 * @param is_send_number 已发放的人数
	 * @param last_user_id 该批次发放最后的用户id
	 * @param current_user_id 最近一次发送任务中最后的用户id
	 * @param status 任务执行状态(0、执行中 ， 1、已完成)
	 * @param send_type 发送任务类型(0、群发  ,  1、用户选择)
	 * @param user_id_str 用户id来拼接字符串，中间用","间隔
	 * @param red_packet_id 关联红包id
	 */
	public ResultInfo addRedPacketTask(String name, long identification, long total_number, long is_send_number, long last_user_id, long current_user_id, int status, int send_type, String user_id_str, long red_packet_id){
		ResultInfo result = new ResultInfo();
		t_red_packet_task task = new t_red_packet_task();
		
		task.name = name;
		task.identification = identification;
		task.total_number = total_number;
		task.is_send_number = is_send_number;
		task.last_user_id = last_user_id;
		task.current_user_id = current_user_id;
		task.status = status;
		task.send_type = send_type;
		task.user_id_str = user_id_str;
		task.red_packet_id = red_packet_id;
		
		if(!redPacketTaskDao.save(task)){
			result.code = -1;
			result.msg = "添加批量发放红包定时任务失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "";
		
		return result;
	}

	/**
	 * 查询红包任务
	 * @param id 任务Id
	 * 
	 */
	public t_red_packet_task queryTaskById(Long id) {
		return redPacketTaskDao.findByID(id);
	}

	/**
	 * 更新红包任务状态
	 * @param id 任务Id
	 * 
	 */
	public ResultInfo updateRedPacketTaskStatus(Long taskId) {
		ResultInfo result = new ResultInfo();
		int rows = redPacketTaskDao.updateRedPacketTaskStatus(taskId);
		
		if (rows < 1) {
			result.code = -1;
			result.msg = "更新失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "更新成功";
		
		return result;
	}
	
	
	/**
	 * 查询分批次发送用户列表
	 * 
	 * @return
	 */
	public List<t_user> findUserList(long currtId , long taskId) {
		
		return redPacketTaskDao.findUserList(currtId, taskId);
	}

	public PageBean<t_red_packet> pageOfRedPacket(int currPage, int pageSize, int type, Boolean isUse, String numNo) {
		return redPacketDao.pageOfRedPacket(currPage, pageSize, type, isUse, numNo);
	}

	public t_red_packet insert(t_red_packet packet) {
		return this.redPacketDao.saveT(packet);
	}

	public boolean updateStatus(long id, boolean isUse) {
		int index = redPacketDao.updateStatus(id, isUse);
		if (index != 1) {
			return false;
		}
		return true;
	}

	public boolean delete(long id) {
		int index = this.redPacketDao.delete(id);
		if(index <= 0) {
			return false;
		}
		return true;
	}
	
}