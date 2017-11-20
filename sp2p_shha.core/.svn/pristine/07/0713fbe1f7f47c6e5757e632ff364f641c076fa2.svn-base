package services.core;

import java.util.Date;
import java.util.List;

import common.constants.Constants;
import common.interfaces.ICacheable;
import common.utils.ArrayUtil;
import common.utils.Factory;
import common.utils.ResultInfo;
import daos.core.CashDao;
import daos.core.CashSendingDao;
import daos.core.CashTaskDao;
import models.common.entity.t_user;
import models.core.entity.t_cash;
import models.core.entity.t_cash_sending;
import models.core.entity.t_cash_task;
import play.cache.Cache;
import services.base.BaseService;
import services.common.UserFundService;
import services.common.UserService;

/**
 * 现金券service
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年2月16日
 */
public class CashService extends BaseService<t_cash> implements ICacheable {

	protected CashDao cashDao = null;
	
	protected UserFundService userFundService = Factory.getService(UserFundService.class);

	protected CashSendingDao cashSendingDao = Factory.getDao(CashSendingDao.class);
	
	protected UserService userService = Factory.getService(UserService.class);

	protected CashTaskDao cashTaskDao = Factory.getDao(CashTaskDao.class);
	
	protected CashService() {
		this.cashDao =  Factory.getDao(CashDao.class);
		super.basedao = this.cashDao;
	}
	
	public t_cash findCashById(long cashId) {
		
		return cashDao.findByID(cashId);
	}
	
	/**
	 * 根据现金券类型查询某个现金券
	 *
	 */
	public t_cash findByType(int type) {
		t_cash cash = cashDao.findByColumn(" type = ? ", type);
		
		return cash;
	}
	
	/**
	 * 根据现金券类型查询某个现金券
	 *
	 * @return
	 */
	public t_cash findByTypeForSend(int type) {
		t_cash cash = cashDao.findByColumn(" type = ? and amount > 0 ", type);
		
		return cash;
	}
	
	/**
	 * 添加用于所有用户的现金券发放定时任务
	 * 
	 * @param userList
	 * @param amount
	 * @param useRule
	 * @param endTime
	 * @param letter
	 * @param email
	 * @param sms
	 * 
	 * @return
	 */
	public ResultInfo addSendTaskUseOfAllUser(List<t_user> userList, double amount, double useRule, int endTime, boolean letter, 
			boolean email, boolean sms) {
		ResultInfo result = new ResultInfo();
		
		if (amount <= 0.00 || amount > 1000) {
			result.code = -1;
			result.msg = "现金券金额不正确";
			
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
			t_cash_sending send = new t_cash_sending();
			send.user_id = user.id;
			send.time = new Date();
			send.amount = amount;
			send.use_rule = useRule;
			send.end_time = endTime;
			send.is_send_letter = letter;
			send.is_send_email = email;
			send.is_send_sms = sms;
			send.is_send = false;
			send.send_time = null;
			
			if (!cashSendingDao.save(send)) {
				result.code = -1;
				result.msg = "添加批量发放现金券定时任务失败";
				
				return result;
			}
		}
		
		result.code = 1;
		result.msg = "";
		
		return result;
	}
	
	/**
	 * 添加用于选中用户的现金券发放定时任务
	 * 
	 * @param users
	 * @param amount
	 * @param useRule
	 * @param endTime
	 * @param letter
	 * @param email
	 * @param sms
	 * 
	 * @return
	 */
	public ResultInfo addSendTaskUseOfSelectUser(String users, double amount, double useRule, int endTime, boolean letter, 
			boolean email, boolean sms) {
		ResultInfo result = new ResultInfo();
		
		if (amount <= 0.00 || amount > 1000) {
			result.code = -1;
			result.msg = "现金券金额不正确";
			
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
			t_cash_sending send = new t_cash_sending();
			send.user_id = userInfo.id;
			send.time = new Date();
			send.amount = amount;
			send.use_rule = useRule;
			send.end_time = endTime;
			send.is_send_letter = letter;
			send.is_send_email = email;
			send.is_send_sms = sms;
			send.is_send = false;
			send.send_time = null;
			
			if (!cashSendingDao.save(send)) {
				result.code = -1;
				result.msg = "添加批量发放现金券定时任务失败";
				
				return result;
			}
		}
		
		result.code = 1;
		result.msg = "";
		
		return result;
	}
	
	/**
	 * 查询未发放的现金券
	 * 
	 * @return
	 */
	public List<t_cash_sending> queryUnSendCash() {
		
		return cashSendingDao.queryUnSendCash();
	}
	
	/**
	 * 更新群发现金券的状态
	 * 
	 * @param sendId 群发现金券id
	 */
	public int updateCashSendStatus(long sendId) {
		
		return cashSendingDao.updateCashSendStatus(sendId);
	}
	
	/**
	 * 记录群发现金券任务
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
	 * @param cash_id 关联现金券id
	 */
	public ResultInfo addCashTask(String name, long identification, long total_number, long is_send_number, long last_user_id, long current_user_id, int status, int send_type, String user_id_str, long cash_id){
		ResultInfo result = new ResultInfo();
		t_cash_task task = new t_cash_task();

		task.name = name;
		task.identification = identification;
		task.total_number = total_number;
		task.is_send_number = is_send_number;
		task.last_user_id = last_user_id;
		task.current_user_id = current_user_id;
		task.status = status;
		task.send_type = send_type;
		task.user_id_str = user_id_str;
		task.cash_id = cash_id;
		
		if(!cashTaskDao.save(task)){
			result.code = -1;
			result.msg = "添加批量发放现金券定时任务失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "";
		
		return result;
	}
	
	@Override
	public List<t_cash> findAll() {
		
		return getCache();
	}
	
	@Override
	public void addAFlushCache() {
		List<t_cash> packets = cashDao.findAll();
		Cache.safeSet(this.getClass().getName(),packets,Constants.CACHE_TIME_HOURS_24);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<t_cash> getCache() {
		List<t_cash> packets = (List<t_cash>) Cache.get(this.getClass().getName());
		
		if (packets == null) {
			addAFlushCache();
			packets = (List<t_cash>) Cache.get(this.getClass().getName());
		}
		
		return packets;
	}

	@Override
	public void clearCache() {
		Cache.safeDelete(this.getClass().getName());
	}

	/**
	 * 添加现金券实体类
	 * 
	 * @param type
	 * @param amount
	 * @param useRule
	 * @param endTime
	 * @param letter
	 * @param email
	 * @param sms
	 * @return
	 */
	
	public ResultInfo saveCash(int type, double amount, double useRule, int bidPeriod, int endTime, boolean letter, boolean email, boolean sms) {

		ResultInfo result = new ResultInfo();
		
		t_cash cash = new t_cash();
		cash.time = new Date();
		cash.type = type;
		cash.amount = amount;
		cash.use_rule = useRule;
		cash.bid_period = bidPeriod;
		cash.end_time = endTime;
		cash.is_send_letter = letter;
		cash.is_send_email = email;
		cash.is_send_sms = sms;
		
		t_cash cashT = cashDao.saveT(cash);
		if(cashT == null){
			result.code = -1;
			result.msg = "添加现金券规则失败";
			
			return result;
			
		}
		
		result.obj = cashT.id;
		result.code = 1;
		result.msg = "";
		return result;
	}

	
	/**
	 * 查询未发放的任务
	 * 
	 * @return
	 */
	public t_cash_task queryUnSendTask() {
		return cashTaskDao.queryUnSendTask();
	}

	
	/**
	 * 更新群发现金券的数量
	 * 
	 * @param taskId 群发现金券任务id
	 */
	public ResultInfo updateCashTaskCount(long taskId) {
		ResultInfo result = new ResultInfo();
		
		int rows = cashTaskDao.updateCashTaskCount(taskId);
		
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
	 * 添加用于所有用户的现金券发放定时任务
	 * 
	 * @param user 发送的用户
	 * @param cashId 发送的现金券
	 * @param sign 关联任务表唯一索引
	 * 
	 * @return
	 */
	public ResultInfo saveSending(t_user user, long cashId, String sign) {
		ResultInfo result = new ResultInfo();
		
		if (user == null) {
			result.code = -1;
			result.msg = "用户不存在";
			
			return result;
		}
		
		t_cash cash = this.findByID(cashId);
		
		if (cash == null) {
			result.code = -1;
			result.msg = "现金券不存在";
			
			return result;
		}
		
		t_cash_sending send = new t_cash_sending();
		send.user_id = user.id;
		send.time = new Date();
		send.amount = cash.amount;
		send.use_rule = cash.use_rule;
		send.bid_period = cash.bid_period;
		send.end_time = cash.end_time;
		send.is_send_letter = cash.is_send_letter;
		send.is_send_email = cash.is_send_email;
		send.is_send_sms = cash.is_send_sms;
		send.is_send = false;
		send.send_time = null;
		send.task_sign = sign;
		
		if (!cashSendingDao.save(send)) {
			result.code = -1;
			result.msg = "添加批量发放现金券定时任务失败";
			
			return result;
			
		}
		
		result.code = 1;
		result.msg = "";
		
		return result;
	}

	/**
	 * 更新现金券任务
	 *
	 * @param userId 当前发放任务中最后的Id
	 * @param num 已发放的人数
	 * @param taskId 修改任务Id
	 * @return
	 *
	 */
	public ResultInfo updateCashTaskUser(long userId, long taskId) {
		ResultInfo result = new ResultInfo();
		
		int rows = cashTaskDao.updateCashTaskUser(userId, taskId);
		
		if (rows < 1) {
			result.code = -1;
			result.msg = "更新失败";
		}
		
		result.code = 1;
		result.msg = "更新成功";
		
		return result;
	}

	/**
	 * 查询现金券任务
	 * @param id 任务Id
	 * 
	 */
	public t_cash_task queryTaskById(Long id) {
		return cashTaskDao.findByID(id);
	}

	/**
	 * 更新现金券任务状态
	 * @param id 任务Id
	 * 
	 */
	public ResultInfo updateCashTaskStatus(Long taskId) {
		ResultInfo result = new ResultInfo();
		int rows = cashTaskDao.updateCashTaskStatus(taskId);
		
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
		
		return cashTaskDao.findUserList(currtId, taskId);
	}
}
