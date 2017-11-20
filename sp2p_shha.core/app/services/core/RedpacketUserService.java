package services.core;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import common.enums.NoticeScene;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import daos.core.RedpacketUserDao;
import models.common.entity.t_user;
import models.core.bean.RedpacketRecord;
import models.core.entity.t_red_packet;
import models.core.entity.t_red_packet_user;
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
public class RedpacketUserService extends BaseService<t_red_packet_user> {

	protected RedpacketUserDao redpacketUserDao = null;
	
	protected NoticeService noticeService = Factory.getService(NoticeService.class);
	
	protected UserService userService = Factory.getService(UserService.class);
	
	protected RedpacketUserService() {
		this.redpacketUserDao =  Factory.getDao(RedpacketUserDao.class);
		super.basedao = this.redpacketUserDao;
	}
	
	/**
	 * 查询某个用户的所有红包
	 *
	 * @param userid 用户的ID
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月15日
	 */
	public List<t_red_packet_user> queryRedpacketsByUserId(long userId) {
		List<t_red_packet_user> redpacketUsers = redpacketUserDao.findListByColumn(" user_id=? ", userId);
		
		return redpacketUsers;
	}
	
	/**
	 * 根据状态查询红包
	 * 
	 * @param userid
	 * @param status
	 * 
	 * @return
	 */
	public List<t_red_packet_user> queryRedpacketsByUserIdStatus(long userId, int status) {
		List<t_red_packet_user> redpacketUsers = redpacketUserDao.findListByColumn(" user_id=? and status = ? ", userId, status);
		
		return redpacketUsers;
	}
	
	/**
	 * 根据红包ID查询红包
	 * 
	 * @param redPacketId
	 * 
	 * @return
	 */
	public t_red_packet_user queryRedPacket(long redPacketId) {
		
		return redpacketUserDao.findByID(redPacketId);
	}
	
	public t_red_packet_user findUserRedById(long redId, long userId) {
		
		return redpacketUserDao.findByColumn(" id = ? and user_id = ? ", redId, userId);
	}
	
	/**
	 * 后台红包发放记录分页
	 * @param showType
	 * @param currPage
	 * @param pageSize
	 * @param exports
	 * @param userName
	 * @param orderType
	 * @param orderValue
	 * @return
	 */
	public PageBean<RedpacketRecord> queryUserRedpacketList(int showType, 
			int currPage, int pageSize, int exports, String userName, 
			int orderType, int orderValue) {
		
		return redpacketUserDao.pageOfUserRedpacket(showType, currPage, pageSize, exports, userName, orderType, orderValue);
	}
	
	/**
	 * 更新红包状态：未使用→使用中
	 * 
	 * @param red_id
	 * @param userId
	 * 
	 * @return
	 */
	public ResultInfo updateUserRedToUsing(long redId, long userId) {
		ResultInfo result = new ResultInfo();
		StringBuffer sql = new StringBuffer();
		sql.append(" UPDATE t_red_packet_user SET status = :status1 WHERE id = :redId AND user_id = :userId AND status = :status2 AND NOW() < end_time ");
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("status1", t_red_packet_user.RedpacketStatus.USING.code);
		args.put("redId", redId);
		args.put("userId", userId);
		args.put("status2", t_red_packet_user.RedpacketStatus.UNUSED.code);
		
		int row = redpacketUserDao.updateBySQL(sql.toString(), args);
		
		if (row <= 0) {
			result.code = -1;
			result.msg = "更新红包信息失败";
			
			return result;
		}
		
		result.code = 1 ;
		result.msg ="更新红包信息成功";
		
		return result;
	}
	
	/**
	 * 更新红包状态：使用中→已使用
	 * 
	 * @param red_id
	 * @param userId
	 * 
	 * @return
	 */
	public ResultInfo updateUserRedToUsed(long redId, long userId) {
		ResultInfo result = new ResultInfo();
		
		StringBuffer sql = new StringBuffer();
		sql.append(" UPDATE t_red_packet_user SET status = :status1  WHERE id = :redId AND user_id = :userId AND status = :status2 ");
		Map<String , Object> args = new HashMap<String, Object>();
		args.put("status1", t_red_packet_user.RedpacketStatus.USED.code);
		args.put("redId", redId);
		args.put("userId", userId);
		args.put("status2", t_red_packet_user.RedpacketStatus.USING.code);
		
		int row = redpacketUserDao.updateBySQL(sql.toString(), args);
		
		if (row <= 0) {
			result.code = -1;
			result.msg = "更新红包信息失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "更新红包信息成功";
		
		return result;
	}
	
	/**
	 * 更新红包状态：使用中→未使用
	 * 
	 * @param red_id
	 * @param userId
	 * 
	 * @return
	 */
	public ResultInfo updateUserRedToUnused(long redId, long userId) {
		ResultInfo result = new ResultInfo();
		
		StringBuffer sql = new StringBuffer();
		sql.append(" UPDATE t_red_packet_user SET status = :status1  WHERE id = :redId AND user_id = :userId AND status = :status2 ");
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("status1", t_red_packet_user.RedpacketStatus.UNUSED.code);
		args.put("redId", redId);
		args.put("userId", userId);
		args.put("status2", t_red_packet_user.RedpacketStatus.USING.code);
		
		int row = redpacketUserDao.updateBySQL(sql.toString(), args);
		
		if (row <= 0) {
			result.code = -1;
			result.msg = "更新红包信息失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg ="更新红包信息成功";
		
		return result;
	}
	
	/**
	 * 根据类型分页查询前台红包
	 * 
	 * @param userId
	 * @param type
	 * @param currPage
	 * @param pageSize
	 * 
	 * @return
	 */
	public PageBean<t_red_packet_user> pageOfUserRed(long userId, int type, int currPage, int pageSize) {
		if (userId <= 0) {
			
			return new PageBean<t_red_packet_user>();
		}
		
		StringBuffer querySQL = new StringBuffer("SELECT * FROM t_red_packet_user WHERE user_id = :user_id AND status = :status2 ORDER BY time DESC ");
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(1) FROM t_red_packet_user WHERE user_id = :user_id AND status = :status2 ");
		
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("user_id", userId);
		maps.put("status2", type);
		
		PageBean<t_red_packet_user> pageBean = redpacketUserDao.pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), t_red_packet_user.class, maps);
		
		return pageBean;
	}
	
	/**
	 * 查询用户可用红包
	 * 
	 * @param userId
	 * 
	 * @return
	 */
	public List<t_red_packet_user> queryUserRedList(long userId) {
		
		return redpacketUserDao.findListByColumn(" user_id = ? AND status = ? AND NOW() < end_time ", userId, t_red_packet_user.RedpacketStatus.UNUSED.code);
	}


	/**
	 * 查找当前投资金额下的可用红包
	 * 
	 * @param userId
	 * @param investAmt
	 * 
	 * @return
	 */
	public List<t_red_packet_user> queryUserRedListByAmount(long userId, double investAmt) {
		
		return redpacketUserDao.findListByColumn(" user_id = ? AND status = ? AND NOW() < end_time AND use_rule <= ? ", userId, t_red_packet_user.RedpacketStatus.UNUSED.code, investAmt);
	}
	
	/**
	 * 定时任务给用户发送红包
	 * 
	 * @param redPacketName 红包名称
	 * @param sendId t_red_packet_sending表的ID
	 * @param userId 用户id
	 * @param amount 红包金额
	 * @param useRule 使用规则
	 * @param bidPeriod 使用规则：标的期限
	 * @param endTime 有效天数
	 * @param letter 是否发送站内信
	 * @param email 是否发送邮件
	 * @param sms 是否发送短信
	 */
	public ResultInfo jobSendRedPacketToUser(String redPacketName, long userId, long sendId, double amount, double useRule, int bidPeriod,
			int endTime, boolean letter, boolean email, boolean sms) {
		ResultInfo result = new ResultInfo();
		
		if (StringUtils.isBlank(redPacketName)) {
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
		
		t_user user = userService.findUserById(userId);
		
		if (user == null) {
			result.code = -1;
			result.msg = "用户信息不存在";
			
			return result;
		}
		
		t_red_packet_user redPacketUser = new t_red_packet_user();
		redPacketUser.time = new Date();
		redPacketUser.user_id = userId;
		redPacketUser.name = redPacketName;
		redPacketUser.type = t_red_packet.RedpacketType.BATCH.code;
		redPacketUser.amount = amount;
		redPacketUser.setStatus(t_red_packet_user.RedpacketStatus.UNUSED);
		redPacketUser.use_rule = useRule;
		redPacketUser.bid_period = bidPeriod;
		redPacketUser.end_time = DateUtil.addDay(new Date(), endTime);
		redPacketUser.red_packet_id = sendId;
		redPacketUser.mark = userId + "" + sendId + "B" + t_red_packet.RedpacketType.BATCH.code + DateUtil.dateToString(new Date(), "yyyyMMddhhmmssss");
		
		redPacketUser = redpacketUserDao.saveT(redPacketUser);
		
		if (redPacketUser == null) {
			result.code = -1;
			result.msg = "发送红包失败";
			
			return result;
		}
		
		/** 发送通知  */
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("user_name", user.name);
		param.put("amount", amount);
		
		boolean flag = noticeService.sendSysNoticeForRed(userId, NoticeScene.REDPACKET_BATCH, param, email, sms, letter);
		
		if (!flag) {
			result.code = -1;
			result.msg = "发送通知失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "发送红包成功";
		
		return result;
	}
	
	/**
	 * 积分商城兑换/抽奖红包
	 * 
	 * @param redPacketName 红包名称
	 * @param userId 用户id
	 * @param goodsId 商品id
	 * @param amount 红包金额
	 * @param useRule 使用规则
	 * @param endTime 有效天数
	 * @param i 第几个
	 * @param type 红包类型 
	 */
	public ResultInfo exchangeRedPacket(String redPacketName, long userId,long goodsId, double amount, double useRule, int endTime,int i,int type){
		
		ResultInfo result = new ResultInfo();
		
		t_red_packet_user redPacketUser = new t_red_packet_user();
		redPacketUser.time = new Date();
		redPacketUser.user_id = userId;
		redPacketUser.name = redPacketName;
		redPacketUser.type = type;
		redPacketUser.amount = amount;
		redPacketUser.setStatus(t_red_packet_user.RedpacketStatus.UNUSED);
		redPacketUser.use_rule = useRule;
		redPacketUser.end_time = DateUtil.addDay(new Date(), endTime);
		redPacketUser.red_packet_id = goodsId;
		redPacketUser.mark = userId + "U" + goodsId +"N"+ i + "M" + type + DateUtil.dateToString(new Date(), "yyyyMMddhhmmssss");
		
		redPacketUser = redpacketUserDao.saveT(redPacketUser);
		
		if (redPacketUser == null) {
			result.code = -1;
			result.msg = "发送红包失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "发送红包成功";
		
		return result;
	}
	
	/**
	 * @author menghuijia
	 * 
	 * 积分商城兑换/抽奖红包 重写方法 添加bidPeriod参数
	 * 
	 * @param redPacketName 红包名称
	 * @param userId 用户id
	 * @param goodsId 商品id
	 * @param amount 红包金额
	 * @param useRule 使用规则
	 * @param bidperiod 借款期限(月)，0代表无限制  
	 * @param endTime 有效天数
	 * @param i 第几个
	 * @param type 红包类型 
	 */
	public ResultInfo exchangeRedPacket(String redPacketName, long userId,long goodsId, double amount, double useRule, int bidPeriod, int endTime, int i, int type){
		
		ResultInfo result = new ResultInfo();
		
		t_red_packet_user redPacketUser = new t_red_packet_user();
		redPacketUser.time = new Date();
		redPacketUser.user_id = userId;
		redPacketUser.name = redPacketName;
		redPacketUser.type = type;
		redPacketUser.amount = amount;
		redPacketUser.setStatus(t_red_packet_user.RedpacketStatus.UNUSED);
		redPacketUser.use_rule = useRule;
		redPacketUser.bid_period = bidPeriod;
		redPacketUser.end_time = DateUtil.addDay(new Date(), endTime);
		redPacketUser.red_packet_id = goodsId;
		redPacketUser.mark = userId + "U" + goodsId +"N"+ i + "M" + type + DateUtil.dateToString(new Date(), "yyyyMMddhhmmssss");
		
		redPacketUser = redpacketUserDao.saveT(redPacketUser);
		
		if (redPacketUser == null) {
			result.code = -1;
			result.msg = "发送红包失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "发送红包成功";
		
		return result;
	}
	
	
	/**
	 * 通过到期时间查询所有满足过期条件的红包
	 * 
	 * @return
	 */
	public List<t_red_packet_user> findAllExpiredRedPacketByEndTime() {
		
		return redpacketUserDao.findAllExpiredRedPacketByEndTime();
	}
	
	/**
	 * 将所有满足过期条件的红包状态更新
	 * 
	 * @return
	 */
	public int updateAllExpiredRedPacketStatus() {
		
		return redpacketUserDao.updateAllExpiredRedPacketStatus();
	}
	
	/**
	 * 修改用户红包状态
	 * 
	 * @param redPacketId 用户红包ID
	 * @param oldStatus 红包旧状态
	 * @param newStatus 红包新状态
	 */
	public int updateUserRedPacketStatus(long redPacketId, int oldStatus, int newStatus) {
		
		return redpacketUserDao.updateUserRedPacketStatus(redPacketId, oldStatus, newStatus);
	}
	
	/**
	 * 锁定红包
	 * @param redPacketId
	 * @param status
	 * @param endStatus
	 * @return
	 */
	public int updateRedPacketLockTime(long redPacketId , int status , int endStatus ){
		return redpacketUserDao.updateRedPacketLockTime(redPacketId, status, endStatus) ;
	}
	
	/**
	 * 修改红包锁定状态
	 * @param status
	 * @param endStatus
	 * @return
	 */
	public int updateRePacketLockStatus(int status , int endStatus){
		return redpacketUserDao.updateRePacketLockStatus( status, endStatus) ;
	}
	
	/**
	 * 统计用户红包数量
	 * 
	 * @param userId
	 * @param status
	 * @return
	 */
	public int countUserRedPacket(long userId, int status) {
		
		return redpacketUserDao.countUserRedPacket(userId, status);
	}
	
	/**
	 * 查询用户可以使用的红包
	 * 
	 * @param bidPeriod 红包使用规则:借款期限(月)，0代表无限制 
	 * @param userId
	 * @param amount
	 * @return
	 */
	public List<t_red_packet_user> findCanUseRedPacket(long userId, double amount,int bidPeriod) {
		
		return redpacketUserDao.findCanUseRedPacket(userId, amount,bidPeriod);
	}
	
	/**
	 * 统计用户红包金额
	 * 
	 * @param userId
	 * @param status
	 * @return
	 */
	public double totalUserRedPacket(long userId, int status) {
		
		return redpacketUserDao.totalUserRedPacket(userId, status);
	}

	public int queryDailyGainRedPacketCount(long user_id, int code) {
		return this.redpacketUserDao.queryDailyGainRedPacketCount(user_id, code);
	}

	public ResultInfo insertBypacket(t_red_packet packet, long user_id, String name) {
		
		ResultInfo result = new ResultInfo();
		
		if (packet.amount <= 0.00 || packet.amount > 1000) {
			result.code = -1;
			result.msg = "红包金额不正确";
			
			return result;
		}
		
		if (packet.use_rule <= 0.00 || packet.use_rule > 999999) {
			result.code = -1;
			result.msg = "最低投资金额不正确";
			
			return result;
		}
		
		if (packet.end_time <= 0 || packet.end_time > 365) {
			result.code = -1;
			result.msg = "有效期限不正确";
			
			return result;
		}
		
		t_red_packet_user redPacketUser = new t_red_packet_user(packet);
		redPacketUser.user_id = user_id;
		redPacketUser.name = "每日活动红包";
		redPacketUser.type = t_red_packet.RedpacketType.ACTIVITY.code;
		redPacketUser.mark = user_id + "" + packet.id + "B" + t_red_packet.RedpacketType.ACTIVITY.code + DateUtil.dateToString(new Date(), "yyyyMMddhhmmssss");
		
		redPacketUser = redpacketUserDao.saveT(redPacketUser);
		
		if (redPacketUser == null) {
			result.code = -1;
			result.msg = "发送红包失败";
			
			return result;
		}
		
		/** 发送通知  */
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("user_name", name);
		param.put("amount", packet.amount);
		
		boolean flag = noticeService.sendSysNoticeForRed(user_id, NoticeScene.REDPACKET_ACTIVITY, param, packet.is_send_email, packet.is_send_sms, packet.is_send_letter);
		
		if (!flag) {
			result.code = -1;
			result.msg = "发送通知失败";
			
			return result;
		}
		
		result.code = 1;
		
		return result;
	}

	public ResultInfo createRedPacketSelf(long userId, int period, double amount) {
		ResultInfo result = new ResultInfo();
		
		t_red_packet_user redPacketUser = new t_red_packet_user();

		Date now = new Date();
		
		// 创建时间
		redPacketUser.time = new Date();
		// 最低投资金额
		redPacketUser.use_rule = amount;
		// 标的期限
		redPacketUser.bid_period = period;
		// 红包过期时间
		redPacketUser.end_time = DateUtil.addDay(now, 1);
		//
		redPacketUser.red_packet_id = -1L;
		
		double money = 0;
		
		if(period == 1) {
			money = 0.0025 * amount;
		} else if (period == 3) {
			money = 0.0125 * amount;
		} else {
			money = 0.03 * amount;
		}
		
		money = Double.parseDouble(String.format("%.2f", money));
		
		// 红包金额
		redPacketUser.amount = money;
		redPacketUser.user_id = userId;
		redPacketUser.name = "国庆红包";
		redPacketUser.type = t_red_packet.RedpacketType.ACTIVITY.code;
		redPacketUser.mark = userId + "" + redPacketUser.red_packet_id + "B" + t_red_packet.RedpacketType.ACTIVITY.code + DateUtil.dateToString(new Date(), "yyyyMMddhhmmssss");
		
		redPacketUser = redpacketUserDao.saveT(redPacketUser);
		
		if (redPacketUser == null) {
			result.code = -1;
			result.msg = "发送红包失败";
			
			return result;
		}
		
		t_user user = userService.findByID(userId);
		
		/** 发送通知  */
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("user_name", user.name);
		param.put("amount", money);
		
		boolean flag = noticeService.sendSysNoticeForRed(userId, NoticeScene.REDPACKET_ACTIVITY, param, false, false, false);
		
		if (!flag) {
			result.code = -1;
			result.msg = "发送通知失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "领取成功";
		result.obj = redPacketUser.amount;
		return result;
	}

	public int findSelfRedPacket(long userId, int period) {
		String sql = "SELECT COUNT(1) FROM t_red_packet_user WHERE user_id = :userId AND bid_period = :period AND DATE_FORMAT(time,'%Y-%m-%d') = :time AND red_packet_id = -1";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", userId);
		map.put("period", period);
		map.put("time", DateUtil.dateToString(new Date(), "yyyy-MM-dd"));
		return redpacketUserDao.findSingleIntBySQL(sql, 0, map);
	}

	public ResultInfo getCountRedPacketSelf() {
		ResultInfo result = new ResultInfo();
		String sql = "SELECT COUNT(1) FROM (SELECT user_id FROM t_red_packet_user WHERE red_packet_id = -1 GROUP BY user_id) a";
		int size = redpacketUserDao.findSingleIntBySQL(sql, 0, null);
		sql = "SELECT SUM(amount) FROM t_red_packet_user WHERE red_packet_id = -1";
		double sum = redpacketUserDao.findSingleDoubleBySQL(sql, 0, null);
		result.code = 1;
		result.msg = "success";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("size", size);
		map.put("sum", sum);
		result.obj = map;
		return result;
	}
	
}
