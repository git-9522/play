package services.core;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.enums.NoticeScene;
import common.utils.ArrayUtil;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import daos.core.RateDao;
import daos.core.RateSendingDao;
import daos.core.RateTaskDao;
import daos.core.RateUserDao;
import models.common.entity.t_user;
import models.core.bean.RateRecord;
import models.core.entity.t_addrate;
import models.core.entity.t_addrate_sending;
import models.core.entity.t_addrate_sending.SendStatus;
import models.core.entity.t_addrate_task;
import models.core.entity.t_addrate_user;
import services.base.BaseService;
import services.common.NoticeService;
import services.common.UserFundService;
import services.common.UserService;

/**
 * 加息卷Service
 *
 * @author jiayijian
 * @createDate 2017年04月05日
 */
public class RateService extends BaseService<t_addrate_user>{
	
	protected UserFundService userFundService = Factory.getService(UserFundService.class);
	
	protected UserService userService = Factory.getService(UserService.class);
	
	protected NoticeService noticeService = Factory.getService(NoticeService.class);
	
	protected RateSendingDao rateSendingDao = Factory.getDao(RateSendingDao.class);

	protected RateTaskDao rateTaskDao = Factory.getDao(RateTaskDao.class);

	protected RateDao rateDao = Factory.getDao(RateDao.class);
	
	protected RateUserDao rateUserDao;
	
	protected RateService(){
		rateUserDao = Factory.getDao(RateUserDao.class);
		super.basedao = rateUserDao;
	}
	
	
	/**
	 * 后台加息券发放记录分页
	 * 
	 * @param showType
	 * @param currPage
	 * @param pageSize
	 * @param exports
	 * @param userName
	 * @param orderType
	 * @param orderValue
	 * 
	 * @return
	 */
	public PageBean<RateRecord> pageOfUserRate(int showType, 
			int currPage, int pageSize, int exports, String userName, 
			int orderType, int orderValue,String startTime,String endTime) {
		
		return rateUserDao.pageOfUserRate(showType, currPage, pageSize, exports, userName, orderType, orderValue, startTime, endTime);
	}
	
	/**
	 * 将加息券的规则添加到定时任务，发放到用户由定时任务执行
	 * 
	 * @param rate
	 * @param useRule
	 * @param bidPeriod 借款期限(月)，0代表无限制
	 * @param endTime
	 * @param letter
	 * @param email
	 * @param sms
	 * 
	 * @return
	 */
	public ResultInfo addSendTaskUseOfAllUser(double rate, double useRule,int bidPeriod, int endTime, boolean letter, 
			boolean email, boolean sms) {
		ResultInfo result = new ResultInfo();
		
		t_addrate_sending send = new t_addrate_sending();
		send.time = new Date();
		send.rate = rate;
		send.use_rule = useRule;
		send.bid_period = bidPeriod;
		send.end_time = endTime;
		send.is_send_letter = letter;
		send.is_send_email = email;
		send.is_send_sms = sms;
		send.status = 0;
		send.send_time = null;
		
		if (!rateSendingDao.save(send)) {
			result.code = -1;
			result.msg = "添加批量发放加息券定时任务失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "";
		
		return result;
	}
	
	
	/**
	 * 选中的用户直接发放加息券
	 * 
	 * @param users
	 * @param rate
	 * @param useRule
	 * @param bidPeriod 借款期限(月)，0代表无限制
	 * @param endTime
	 * @param letter
	 * @param email
	 * @param sms
	 * 
	 * @return
	 */
	public ResultInfo addSendUseOfSelectUser(String users, double rate, double useRule,int bidPeriod, int endTime, boolean letter, 
			boolean email, boolean sms) {
		ResultInfo result = new ResultInfo();
		
		users = users.replaceAll("\\s", "");
		String[] user = users.split(",");
		user = ArrayUtil.arrayUnique(user);
		
		if(user.length > 20){
			
			result.code = -1;
			result.msg = "批量直接发放加息券一次最多支持20个用户";
			
			return result;
		}
		
		for (String userName : user) {
			if (!userService.isNameExists(userName)) {
				continue;
			}
			
			result = userService.findUserInfoByName(userName);
			if (result.code < 1) {
				continue;
			}
			
			Date date  = new Date();
			t_user userInfo = (t_user) result.obj;
			t_addrate_user addrate = new t_addrate_user();
			addrate.time = date;
			addrate.send_id = 0;
			addrate.user_id = userInfo.id;
			addrate.rate = rate;
			addrate.type = t_addrate_user.RateType.BATCH.code;
			addrate.status = t_addrate_user.RateStatus.UNUSED.code;
			addrate.use_rule = useRule;
			addrate.bid_period = bidPeriod;
			addrate.end_time = DateUtil.addDay(date, endTime);
			addrate.mark = userInfo.id + "U" + 0 + "B" + t_addrate_user.RateType.BATCH.code + DateUtil.dateToString(date, "yyyyMMddhhmmssss");;
			
			if (!rateUserDao.save(addrate)) {
				result.code = -1;
				result.msg = "发放加息券失败";
				
				return result;
			}
			
			/** 发送通知  */
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("user_name", userInfo.name);
			param.put("rate", rate);
			noticeService.sendSysNoticeForRed(userInfo.id, NoticeScene.RATE_BATCH, param, email, sms, letter);
			
		}
		
		result.code = 1;
		result.msg = "";
		
		return result;
	}
	
	
	/**
	 * 查询未发放的加息券
	 * 
	 * @return
	 */
	public List<t_addrate_sending> queryUnSendRate() {
		
		return rateSendingDao.queryUnSendRate();
	}
	
	/**
	 * 更新群发加息券的状态
	 * 
	 * @param sendId 群发加息券id
	 */
	public int updateRateSendStatus(long sendId) {
		
		return rateSendingDao.updateRateSendStatus(sendId);
	}
	
	/**
	 * 定时任务给用户发送加息券
	 * 
	 * @param userId 用户id
	 * @param sendId t_cash_sending表的ID
	 * @param rate 加息券 年利率
	 * @param useRule 使用规则
	 * @param bidPeriod  借款期限(月)，0代表无限制
	 * @param endTime 有效天数
	 * @param letter 是否发送站内信
	 * @param email 是否发送邮件
	 * @param sms 是否发送短信
	 */
	public ResultInfo jobSendRateToUser(long userId, long sendId, double rate, double useRule, int bidPeriod,
			int endTime, boolean letter, boolean email, boolean sms) {
		ResultInfo result = new ResultInfo();
		
		t_user user = userService.findUserById(userId);
		
		if (user == null) {
			result.code = -1;
			result.msg = "用户信息不存在";
			
			return result;
		}
		
		Date date  = new Date();
		t_addrate_user addrate = new t_addrate_user();
		addrate.time = date;
		addrate.send_id = sendId;
		addrate.user_id = user.id;
		addrate.rate = rate;
		addrate.type = t_addrate_user.RateType.BATCH.code;
		addrate.status = t_addrate_user.RateStatus.UNUSED.code;
		addrate.use_rule = useRule;
		addrate.bid_period = bidPeriod;
		addrate.end_time = DateUtil.addDay(date, endTime);
		addrate.mark = user.id + "U" + sendId + "B" + t_addrate_user.RateType.BATCH.code + DateUtil.dateToString(date, "yyyyMMddhhmmssss");;
		
		if (!rateUserDao.save(addrate)) {
			result.code = -1;
			result.msg = "发放加息券失败";
			
			return result;
		}
		
		/** 发送通知  */
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("user_name", user.name);
		param.put("rate", rate);
		noticeService.sendSysNoticeForRed(user.id, NoticeScene.RATE_BATCH, param, email, sms, letter);
		
		result.code = 1;
		result.msg = "发送加息券成功";
		
		return result;
	}
	
	
	/**
	 * 积分商城兑换/抽奖加息券
	 * 
	 * @param userId 用户id
	 * @param goodsId 商品id
	 * @param rate 加息利率
	 * @param useRule 使用规则
	 * @param endTime 有效天数
	 * @param i 第几个
	 * @param type 加息卷类型  
	 */
	public ResultInfo exchangeRate(long userId, long goodsId, double rate, double useRule,int endTime, int i,int type) {
		ResultInfo result = new ResultInfo();
		
		Date date  = new Date();
		t_addrate_user addrate = new t_addrate_user();
		addrate.time = date;
		addrate.send_id = goodsId;
		addrate.user_id = userId;
		addrate.rate = rate;
		addrate.type = type;
		addrate.status = t_addrate_user.RateStatus.UNUSED.code;
		addrate.use_rule = useRule;
		addrate.bid_period = 0;
		addrate.end_time = DateUtil.addDay(date, endTime);
		addrate.mark = userId + "U" + goodsId + "N"+ i + "M" + type + DateUtil.dateToString(date, "yyyyMMddhhmmssss");
		
		if (!rateUserDao.save(addrate)) {
			result.code = -1;
			result.msg = "发放加息券失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "发送加息券成功";
		
		return result;
	}
	
	/**
	 * 
	 * @author menghuijia
	 * 
	 * 积分商城兑换/抽奖加息券 重写方法，添加bidPeriod参数
	 * 
	 * @param userId 用户id
	 * @param goodsId 商品id
	 * @param rate 加息利率
	 * @param useRule 使用规则
	 * @param bidPeriod 借款期限(月)，0代表无限制 
	 * @param endTime 有效天数
	 * @param i 第几个
	 * @param type 加息卷类型  
	 */
	public ResultInfo exchangeRate(long userId, long goodsId, double rate, double useRule, int bidPeriod, int endTime, int i,int type) {
		ResultInfo result = new ResultInfo();
		
		Date date  = new Date();
		t_addrate_user addrate = new t_addrate_user();
		addrate.time = date;
		addrate.send_id = goodsId;
		addrate.user_id = userId;
		addrate.rate = rate;
		addrate.type = type;
		addrate.status = t_addrate_user.RateStatus.UNUSED.code;
		addrate.use_rule = useRule;
		addrate.bid_period = bidPeriod;
		addrate.end_time = DateUtil.addDay(date, endTime);
		addrate.mark = userId + "U" + goodsId + "N"+ i + "M" + type + DateUtil.dateToString(date, "yyyyMMddhhmmssss");
		
		if (!rateUserDao.save(addrate)) {
			result.code = -1;
			result.msg = "发放加息券失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "发送加息券成功";
		
		return result;
	}

	
	/**
	 * 根据类型分页查询前台加息券
	 * 
	 * @param userId
	 * @param type
	 * @param currPage
	 * @param pageSize
	 * 
	 * @return
	 */
	public PageBean<t_addrate_user> pageOfUserRate(long userId, int type, int currPage, int pageSize) {
		if (userId <= 0) {
			
			return new PageBean<t_addrate_user>();
		}
		
		StringBuffer querySQL = new StringBuffer("SELECT * FROM t_addrate_user WHERE user_id = :user_id AND status = :status2 ORDER BY time DESC ");
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(1) FROM t_addrate_user WHERE user_id = :user_id AND status = :status2 ORDER BY time DESC ");
		
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("user_id", userId);
		maps.put("status2", type);
		
		PageBean<t_addrate_user> pageBean = rateUserDao.pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), t_addrate_user.class, maps);
		
		return pageBean;
	}
	
	/**
	 * 将所有满足过期条件的加息券状态更新
	 * 
	 * @return
	 */
	public int updateAllExpiredRateStatus() {
		
		return rateUserDao.updateAllExpiredRateStatus();
	}
	
	/**
	 * 查询用户可以使用的加息券
	 * 
	 * @param bidPeriod 红包使用规则:借款期限(月)，0代表无限制 
	 * @param userId
	 * @param amount
	 * @return
	 */
	public List<t_addrate_user> findCanUseRate(long userId, double amount,int bidPeriod) {
		
		return rateUserDao.findCanUseRate(userId, amount,bidPeriod);
	}
	
	/**
	 * 查询用户可以使用的加息券集合（APP端）
	 * 
	 * @param userId
	 * @return
	 */
	public List<t_addrate_user> findRateByUserId(long userId) {
		
		return rateUserDao.findListByColumn("user_id =?", userId);
	}
	
	
	/**
	 * 修改用户加息券状态
	 * 
	 * @param rateId 
	 * @param oldStatus 
	 * @param newStatus 
	 */
	public int updateUserRateStatus(long rateId, int oldStatus, int newStatus) {
		
		return rateUserDao.updateUserRateStatus(rateId, oldStatus, newStatus);
	}
	
	/**
	 * 锁定加息券
	 * @param rateId
	 * @param status
	 * @param endStatus
	 * @return
	 */
	public int updateUserRateLockTime(long rateId , int status , int newStatus ){
		return rateUserDao.updateUserRateLockTime(rateId, status, newStatus) ;
	}
	
	/**
	 * 修改加息券锁定状态
	 * @param status
	 * @param endStatus
	 * @return
	 */
	public int updateUserRateLockStatus(int status , int newStatus){
		return rateUserDao.updateUserRateLockStatus( status, newStatus) ;
	}
	
	/**
	 * 根据状态查询加息券
	 * 
	 * @param userid
	 * @param status
	 * 
	 * @return
	 */
	public List<t_addrate_user> queryRateByUserIdStatus(long userId, int status) {
		List<t_addrate_user> rateUsers = rateUserDao.findListByColumn(" user_id=? and status = ? ", userId, status);
		return rateUsers;
	}
	
	/**
	 * 添加用于选中用户的红包发放定时任务
	 * 
	 * @param users
	 * @param name
	 * @param rate
	 * @param useRule
	 * @param bidPeriod 使用规则：标的期限
	 * @param endTime
	 * @param letter
	 * @param email
	 * @param sms
	 * 
	 * @return
	 */
	public ResultInfo saveAddRate(int type, double rate, double useRule,int bidPeriod, int endTime, 
			boolean letter, boolean email, boolean sms) {
		ResultInfo result = new ResultInfo();
		
		t_addrate addrate = new t_addrate();
		addrate.time = new Date();
		addrate.type = type;
		addrate.rate = rate;
		addrate.use_rule = useRule;
		addrate.bid_period = bidPeriod;
		addrate.end_time = endTime;
		addrate.is_send_letter = letter;
		addrate.is_send_email = email;
		addrate.is_send_sms = sms;
		addrate.is_use  = true;
		
		t_addrate quan = rateDao.saveT(addrate);
		if (quan == null) {
			result.code = -1;
			result.msg = "添加加息券规则失败";
			
			return result;
		}
		
		result.obj = quan.id;
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
	 * @param addrate_id 关联任务id
	 */
	public ResultInfo addRateTask(String name, long identification, long total_number, long is_send_number, long last_user_id, long current_user_id, int status, int send_type, String user_id_str, long addrate_id){
		ResultInfo result = new ResultInfo();
		
		t_addrate_task task = new t_addrate_task();
		
		task.name = name;
		task.identification = identification;
		task.total_number = total_number;
		task.is_send_number = is_send_number;
		task.last_user_id = last_user_id;
		task.current_user_id = current_user_id;
		task.status = status;
		task.send_type = send_type;
		task.user_id_str = user_id_str;
		task.addrate_id = addrate_id;
		
		if(!rateTaskDao.save(task)){
			result.code = -1;
			result.msg = "添加批量发放加息券定时任务失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "";
		
		return result;
	}
	
	/**
	 * 查询未发放的加息券任务
	 * 
	 * @return
	 */
	public t_addrate_task queryUnSendTask() {
		
		return rateTaskDao.queryUnSendTask();
	}
	
	/**
	 * 查询分批次发送用户列表
	 * 
	 * @return
	 */
	public List<t_user> findUserList(long currtId , long taskId) {
		
		return rateTaskDao.findUserList(currtId, taskId);
	}

	/**
	 * 更新群发红包的数量
	 * 
	 * @param taskId 群发加息券任务id
	 */
	public ResultInfo updateRateTaskCount(long taskId) {
		ResultInfo result = new ResultInfo();
		
		int rows = rateTaskDao.updateRateTaskCount(taskId);
		
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
	 * 添加用于所有用户的红包发放定时任务
	 * 
	 * @param user 发送的用户
	 * @param addrate_id 发送的红包
	 * @param sign 关联任务表唯一索引
	 * 
	 * @return
	 */
	public ResultInfo saveSending(t_user user, long addrate_id, String sign) {
		ResultInfo result = new ResultInfo();
		
		if (user == null) {
			result.code = -1;
			result.msg = "用户不存在";
			
			return result;
		}
		
		t_addrate rate = rateDao.findByID(addrate_id);
		
		if (rate == null) {
			result.code = -1;
			result.msg = "红包不存在";
			
			return result;
		}
		
		t_addrate_sending send = new t_addrate_sending();
		
		send.user_id = user.id;
		send.time = new Date();
		send.rate = rate.rate;
		send.use_rule = rate.use_rule;
		send.bid_period = rate.bid_period;
		send.end_time = rate.end_time;
		send.is_send_letter = rate.is_send_letter;
		send.is_send_email = rate.is_send_email;
		send.is_send_sms = rate.is_send_sms;
		send.status = SendStatus.UNSEND.code;
		send.send_time = null;
		send.task_sign = sign;
		
		if(!rateSendingDao.save(send)){
			result.code = -1;
			result.msg = "添加批量发放加息券定时任务失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "";
		
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
	public ResultInfo updateRateTaskUser(long userId, long taskId) {
		ResultInfo result = new ResultInfo();
		
		int rows = rateTaskDao.updateRateTaskUser(userId, taskId);
		
		if (rows < 1) {
			result.code = -1;
			result.msg = "更新失败";
		}
		
		result.code = 1;
		result.msg = "更新成功";
		
		return result;
	}

	/**
	 * 查询加息券任务
	 * @param id 任务Id
	 * 
	 */
	public t_addrate_task queryTaskById(Long id) {
		return rateTaskDao.findByID(id);
	}


	/**
	 * 更新加息券任务状态
	 * @param id 任务Id
	 * 
	 */
	public ResultInfo updateRateTaskStatus(Long taskId) {
		ResultInfo result = new ResultInfo();
		int rows = rateTaskDao.updateRateTaskStatus(taskId);
		
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
	 * 根据useriId查询会员累计未用加息卷
	 * @param userId
	 * @return
	 */
	public double userRateSum(long userId) {
		if (userId <= 0) {
			
			return 0;
		}
		StringBuffer querySQL = new StringBuffer("SELECT ifnull(SUM(rate),0.00) FROM t_addrate_user WHERE user_id = :user_id  AND status = 0 and now() < end_time");
		
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("user_id", userId);
		return rateUserDao.findSingleDoubleBySQL(querySQL.toString(),0.00, maps);
	}
	
	
}
