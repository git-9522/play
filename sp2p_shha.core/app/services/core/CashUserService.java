package services.core;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.constants.Constants;
import common.enums.NoticeScene;
import common.interfaces.ICacheable;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import daos.core.CashUserDao;
import models.common.entity.t_user;
import models.core.bean.CashRecord;
import models.core.entity.t_cash;
import models.core.entity.t_cash_user;
import play.cache.Cache;
import services.base.BaseService;
import services.common.NoticeService;
import services.common.UserService;


/**
 * 用户现金券service
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年2月16日
 */
public class CashUserService extends BaseService<t_cash_user> implements ICacheable {

	protected CashUserDao cashUserDao = Factory.getDao(CashUserDao.class);
	
	protected NoticeService noticeService = Factory.getService(NoticeService.class);
	
	protected UserService userService = Factory.getService(UserService.class);
	
	protected CashUserService() {
		this.cashUserDao =  Factory.getDao(CashUserDao.class);
		super.basedao = this.cashUserDao;
	}
	
	/**
	 * 查询某个用户的所有的现金券
	 *
	 * @param userId 用户的ID
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月15日
	 */
	public List<t_cash_user> queryCashByUserId(long userId) {
		List<t_cash_user> cashUserList = cashUserDao.findListByColumn(" user_id=? ", userId);
		
		return cashUserList;
	}
	
	/**
	 * 根据状态查询现金券
	 * 
	 * @param userId
	 * @param status
	 * 
	 * @return
	 */
	public List<t_cash_user> queryCashByUserIdStatus(long userId, int status) {
		List<t_cash_user> cashUserList = cashUserDao.findListByColumn(" user_id=? and status = ? ", userId , status);
		
		return cashUserList;
	}
	
	@Override
	public void addAFlushCache() {
		List<t_cash_user> packets = cashUserDao.findAll();
		
		Cache.safeSet(this.getClass().getName(),packets,Constants.CACHE_TIME_HOURS_24);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<t_cash_user> getCache() {
		List<t_cash_user> packets = (List<t_cash_user>) Cache.get(this.getClass().getName());
		if (packets == null) {
			addAFlushCache();
			packets = (List<t_cash_user>) Cache.get(this.getClass().getName());
		}
		
		return packets;
	}

	@Override
	public void clearCache() {
		Cache.safeDelete(this.getClass().getName());
	}
	
	
	public t_cash_user findCashUserById(long id) {
		
		return cashUserDao.findByColumn(" id=?", id);
	}
	
	/**
	 * 后台现金券发放记录分页
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
	public PageBean<CashRecord> queryUserCashList(int showType, 
			int currPage, int pageSize, int exports, String userName, 
			int orderType, int orderValue) {
		
		return cashUserDao.pageOfUserCash(showType, currPage, pageSize, exports, userName, orderType, orderValue);
	}
	
	/**
	 * 更新现金券状态：未使用→使用中
	 * 
	 * @param cashId
	 * @param userId
	 * 
	 * @return
	 */
	public ResultInfo updateUserCashToUsing(long cashId, long userId) {
		ResultInfo result = new ResultInfo();
		StringBuffer sql = new StringBuffer();
		sql.append(" UPDATE t_cash_user SET status = :status1  WHERE id = :cashId AND user_id = :userId AND status = :status2 AND NOW() < end_time ");
		Map<String , Object> args = new HashMap<String, Object>();
		args.put("status1", t_cash_user.CashStatus.USING.code);
		args.put("cashId", cashId);
		args.put("userId", userId);
		args.put("status2", t_cash_user.CashStatus.UNUSED.code);
		
		int row = cashUserDao.updateBySQL(sql.toString(), args);
		
		if (row <= 0) {
			result.code = -1;
			result.msg = "更新现金券信息失败";
			
			return result;
		}
		
		result.code = 1 ;
		result.msg ="更新现金券信息成功";
		
		return result;
	}
	
	/**
	 * 更新现金券状态：使用中→已使用
	 * 
	 * @param cashId
	 * @param userId
	 * 
	 * @return
	 */
	public ResultInfo updateUserCashToUsed(long cashId, long userId) {
		ResultInfo result = new ResultInfo();
		
		StringBuffer sql = new StringBuffer();
		sql.append(" UPDATE t_cash_user SET status = :status1  WHERE id = :cashId AND user_id = :userId AND status = :status2 ");
		Map<String , Object> args = new HashMap<String, Object>();
		args.put("status1", t_cash_user.CashStatus.USED.code);
		args.put("cashId", cashId);
		args.put("userId", userId);
		args.put("status2", t_cash_user.CashStatus.USING.code);
		
		int row = cashUserDao.updateBySQL(sql.toString(), args);
		
		if (row <= 0) {
			result.code = -1;
			result.msg = "更新现金券信息失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "更新现金券信息成功";
		
		return result;
	}
	
	/**
	 * 更新现金券状态：使用中→未使用
	 * 
	 * @param cashId
	 * @param userId
	 * 
	 * @return
	 */
	public ResultInfo updateUserCashToUnused(long cashId, long userId) {
		ResultInfo result = new ResultInfo();
		
		StringBuffer sql = new StringBuffer();
		sql.append(" UPDATE t_cash_user SET status = :status1  WHERE id = :cashId AND user_id = :userId AND status = :status2 ");
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("status1", t_cash_user.CashStatus.UNUSED.code);
		args.put("cashId", cashId);
		args.put("userId", userId);
		args.put("status2", t_cash_user.CashStatus.USING.code);
		
		int row = cashUserDao.updateBySQL(sql.toString(), args);
		
		if (row <= 0) {
			result.code = -1;
			result.msg = "更新现金券信息失败";
			
			return result;
		}
		
		result.code = 1 ;
		result.msg ="更新现金券信息成功";
		
		return result;
	}
	
	/**
	 * 根据类型分页查询前台现金券
	 * 
	 * @param userId
	 * @param type
	 * @param currPage
	 * @param pageSize
	 * 
	 * @return
	 */
	public PageBean<t_cash_user> pageOfUserCash(long userId, int type, int currPage, int pageSize) {
		if (userId <= 0) {
			
			return new PageBean<t_cash_user>();
		}
		
		StringBuffer querySQL = new StringBuffer("SELECT * FROM t_cash_user WHERE user_id = :user_id AND status = :status2 ORDER BY time DESC ");
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(1) FROM t_cash_user WHERE user_id = :user_id AND status = :status2 ORDER BY time DESC ");
		
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("user_id", userId);
		maps.put("status2", type);
		
		PageBean<t_cash_user> pageBean = cashUserDao.pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), t_cash_user.class, maps);
		
		return pageBean;
	}
	
	/**
	 * 查询用户可用现金券
	 * @param userId
	 * @return
	 */
	public List<t_cash_user> queryUserCashList(long userId) {
		
		return cashUserDao.findListByColumn(" user_id = ? AND status = ? AND NOW() < end_time ", userId,t_cash_user.CashStatus.UNUSED.code);
	}


	/**
	 * 查找当前投资金额下的可用现金券
	 * @param userId
	 * @param investAmt
	 * @return
	 */
	public List<t_cash_user> queryUserCashListByAmount(long userId, double investAmt) {
		
		return cashUserDao.findListByColumn(" user_id = ? AND status = ? AND NOW() < end_time AND use_rule <= ? ", userId,t_cash_user.CashStatus.UNUSED.code,investAmt);
	}
	
	/**
	 * 定时任务给用户发送现金券
	 * 
	 * @param userId 用户id
	 * @param sendId t_cash_sending表的ID
	 * @param amount 现金券金额
	 * @param useRule 使用规则
	 * @param endTime 有效天数
	 * @param letter 是否发送站内信
	 * @param email 是否发送邮件
	 * @param sms 是否发送短信
	 */
	public ResultInfo jobSendCashToUser(long userId, long sendId, double amount, double useRule, int bidPeriod,
			int endTime, boolean letter, boolean email, boolean sms) {
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
		
		t_user user = userService.findUserById(userId);
		
		if (user == null) {
			result.code = -1;
			result.msg = "用户信息不存在";
			
			return result;
		}
		
		t_cash_user cashUser = new t_cash_user();
		cashUser.time = new Date();
		cashUser.user_id = userId;
		cashUser.amount = amount;
		cashUser.type = t_cash.CashType.BATCH.code;
		cashUser.setStatus(t_cash_user.CashStatus.UNUSED);
		cashUser.use_rule = useRule;
		cashUser.bid_period = bidPeriod;
		cashUser.end_time = DateUtil.addDay(new Date(), endTime);
		cashUser.cash_id = sendId;
		cashUser.mark = userId + "" + sendId + "B" + t_cash.CashType.BATCH.code + DateUtil.dateToString(new Date(), "yyyyMMddhhmmssss");
		
		cashUser = cashUserDao.saveT(cashUser);
		
		if (cashUser == null) {
			result.code = -1;
			result.msg = "发送现金券失败";
			
			return result;
		}
		
		/** 发送通知  */
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("user_name", user.name);
		param.put("amount", amount);
		
		boolean flag = noticeService.sendSysNoticeForRed(userId, NoticeScene.CASH_BATCH, param, email, sms, letter);
		
		if (!flag) {
			result.code = -1;
			result.msg = "发送通知失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "发送现金券成功";
		
		return result;
	}
	
	/**
	 * 积分商城兑换/抽奖现金券
	 * 
	 * @param userId 用户id
	 * @param goodsId 商品id
	 * @param amount 现金券金额
	 * @param useRule 使用规则
	 * @param endTime 有效天数
	 * @param endTime 有效天数
	 * @param i 第几个
	 * @param type 现金券类型 
	 */
	public ResultInfo exchangeCash(long userId, long goodsId, double amount, double useRule, int endTime,int i,int type) {
		ResultInfo result = new ResultInfo();
		
		t_cash_user cashUser = new t_cash_user();
		cashUser.time = new Date();
		cashUser.user_id = userId;
		cashUser.amount = amount;
		cashUser.type = type;
		cashUser.setStatus(t_cash_user.CashStatus.UNUSED);
		cashUser.use_rule = useRule;
		cashUser.end_time = DateUtil.addDay(new Date(), endTime);
		cashUser.cash_id = goodsId;
		cashUser.mark = userId + "U" + goodsId +"N"+ i +"M" + type + DateUtil.dateToString(new Date(), "yyyyMMddhhmmssss");
		
		cashUser = cashUserDao.saveT(cashUser);
		
		if (cashUser == null) {
			result.code = -1;
			result.msg = "发送现金券失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "发送现金券成功";
		
		return result;
	}
	
	/**
	 * @author menghuijia
	 * 
	 * 积分商城兑换/抽奖现金券 方法重写添加bidPeriod参数
	 * 
	 * @param userId 用户id
	 * @param goodsId 商品id
	 * @param amount 现金券金额
	 * @param useRule 使用规则
	 * @param bidPeriod 借款期限(月)，0代表无限制 
	 * @param endTime 有效天数
	 * @param i 第几个
	 * @param type 现金券类型 
	 */
	public ResultInfo exchangeCash(long userId, long goodsId, double amount, double useRule, int bidPeriod, int endTime,int i,int type) {
		ResultInfo result = new ResultInfo();
		
		t_cash_user cashUser = new t_cash_user();
		cashUser.time = new Date();
		cashUser.user_id = userId;
		cashUser.amount = amount;
		cashUser.type = type;
		cashUser.setStatus(t_cash_user.CashStatus.UNUSED);
		cashUser.use_rule = useRule;
		cashUser.end_time = DateUtil.addDay(new Date(), endTime);
		cashUser.bid_period = bidPeriod;
		cashUser.cash_id = goodsId;
		cashUser.mark = userId + "U" + goodsId +"N"+ i +"M" + type + DateUtil.dateToString(new Date(), "yyyyMMddhhmmssss");
		
		cashUser = cashUserDao.saveT(cashUser);
		
		if (cashUser == null) {
			result.code = -1;
			result.msg = "发送现金券失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "发送现金券成功";
		
		return result;
	}
	
	/**
	 * 通过到期时间查询所有满足过期条件的现金券
	 * 
	 * @return
	 */
	public List<t_cash_user> findAllExpiredCashByEndTime() {
		
		return cashUserDao.findAllExpiredCashByEndTime();
	}
	
	/**
	 * 将所有满足过期条件的现金券状态更新
	 * 
	 * @return
	 */
	public int updateAllExpiredCashStatus() {
		
		return cashUserDao.updateAllExpiredCashStatus();
	}
	
	/**
	 * 修改用户现金券状态
	 * 
	 * @param cashId 
	 * @param oldStatus 
	 * @param newStatus 
	 */
	public int updateUserCashStatus(long cashId, int oldStatus, int newStatus) {
		
		return cashUserDao.updateUserCashStatus(cashId, oldStatus, newStatus);
	}
	
	/**
	 * 锁定现金券
	 * @param redPacketId
	 * @param status
	 * @param endStatus
	 * @return
	 */
	public int updateUserCashLockTime(long cashId , int status , int newStatus ){
		return cashUserDao.updateUserCashLockTime(cashId, status, newStatus) ;
	}
	
	/**
	 * 修改现金券锁定状态
	 * @param status
	 * @param endStatus
	 * @return
	 */
	public int updateUserCashLockStatus(int status , int newStatus){
		return cashUserDao.updateUserCashLockStatus( status, newStatus) ;
	}
	
	
	/**
	 * 查询用户可以使用的现金券
	 * 
	 * @param userId
	 * @param amount
	 * @return
	 */
	public List<t_cash_user> findCanUseCash(long userId, double amount, int bidPeriod) {
		
		return cashUserDao.findCanUseCash(userId, amount, bidPeriod);
	}
	
	/**
	 * 统计用户现金券数量
	 * 
	 * @param userId
	 * @param status
	 * @return
	 */
	public int countUserCash(long userId, int status) {
		
		return cashUserDao.countUserCash(userId, status);
	}
	/**
	 * 统计会员现金券总金额 ：筛选（未用、未过期）
	 * @param userId
	 * @return
	 */
	public double UserCashSum(long userId ){
		
		String sql="select ifnull(SUM(amount),0.00) from t_cash_user where user_id = :userId and status =0 AND  NOW()< end_time ";
		Map<String, Object> args=new HashMap<String, Object>();
		args.put("userId", userId);
		return cashUserDao.findSingleDoubleBySQL(sql, 0.00, args);
	}
}
