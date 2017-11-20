package services.core;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.shove.Convert;

import common.constants.ConfConst;
import common.enums.Client;
import common.enums.NoticeScene;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.NoUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.number.Arith;
import daos.core.AutoBidDao;
import daos.core.AutoInvestSettingDao;
import daos.core.InvestDao;
import models.common.entity.t_deal_user;
import models.common.entity.t_group_menbers;
import models.common.entity.t_group_menbers_user;
import models.common.entity.t_user_fund;
import models.common.entity.t_user_info;
import models.core.bean.BidInvestRecord;
import models.core.bean.CpsUserInvest;
import models.core.bean.UserInvestRecord;
import models.core.entity.t_addrate_user;
import models.core.entity.t_auto_bid;
import models.core.entity.t_auto_invest_setting;
import models.core.entity.t_bid;
import models.core.entity.t_cash_user;
import models.core.entity.t_invest;
import models.core.entity.t_invest.InvestType;
import models.core.entity.t_invest.TransferStatus;
import models.core.entity.t_product;
import models.core.entity.t_red_packet_user;
import services.base.BaseService;
import services.common.DealUserService;
import services.common.GroupMenbersService;
import services.common.NoticeService;
import services.common.UserFundService;
import services.common.UserInfoService;
import services.common.UserService;

/**
 * 投资service
 *
 * @author liudong
 * @createDate 2015年12月15日
 */
public class InvestService extends BaseService<t_invest> {

	protected InvestDao investDao = null;

	protected AutoInvestSettingDao autoInvestSettingDao = Factory.getDao(AutoInvestSettingDao.class);

	protected AutoBidDao autoBidDao = Factory.getDao(AutoBidDao.class);

	protected UserService userService = Factory.getService(UserService.class);

	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);

	protected static UserFundService userFundService = Factory.getService(UserFundService.class);

	protected static BidService bidService = Factory.getService(BidService.class);

	protected static DealUserService dealUserService = Factory.getService(DealUserService.class);

	protected static NoticeService noticeService = Factory.getService(NoticeService.class);

	protected static RedpacketUserService redpacketUserService = Factory.getService(RedpacketUserService.class);

	protected static CashUserService cashUserService = Factory.getService(CashUserService.class);

	protected static GroupMenbersService groupMenbersService = Factory.getService(GroupMenbersService.class);

	protected static RateService rateService = Factory.getService(RateService.class);

	protected InvestService() {
		investDao = Factory.getDao(InvestDao.class);
		super.basedao = this.investDao;
	}

	/**
	 * 债权转让从原来的标记复制一份新的投资
	 *
	 * @param original
	 * @param newUserid
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月29日
	 */
	public ResultInfo copyToInvest(long original, long debtId, long newUserid, String service_order_no) {
		ResultInfo result = new ResultInfo();
		t_invest originalInvest = investDao.findByID(original);

		t_invest tInvest = new t_invest();// 债权竞拍成功在投资表里面插入新纪录
		tInvest.user_id = newUserid;
		tInvest.time = new Date();
		tInvest.bid_id = originalInvest.bid_id;
		tInvest.amount = originalInvest.amount;
		tInvest.correct_amount = originalInvest.correct_amount;
		tInvest.correct_interest = originalInvest.correct_interest;
		tInvest.setTransfer_status(models.core.entity.t_invest.TransferStatus.NORMAL);
		tInvest.debt_id = debtId;
		tInvest.setClient(originalInvest.getClient()); // 债权转让后，重新生成一条新的投资记录，需要把该条投资记录的来源也加进来（用于后台理财情况数据统计分析）
		tInvest.service_order_no = service_order_no;
		tInvest.trust_order_no = originalInvest.trust_order_no;

		if (!investDao.save(tInvest)) {
			result.code = -1;
			result.msg = "添加投资记录失败";

			return result;
		}

		result.code = 1;
		result.msg = "添加投资记录成功";
		result.obj = tInvest;

		return result;
	}

	/**
	 * 投标(准备)
	 *
	 * @param userId
	 *            用户id
	 * @param bid
	 *            标的对象
	 * @param investAmt
	 *            投标金额
	 * @param cashAmt
	 *            现金券金额
	 * @param investPassword
	 *            投资密码
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月28日
	 */
	public ResultInfo invest(long userId, t_bid bid, double investAmt, double cashAmt, String investPassword) {
		ResultInfo result = new ResultInfo();

		if (bid == null) {
			throw new InvalidParameterException("bid is null");
		}

		if (bid.pre_release_time.after(new Date())) {
			result.code = -1;
			result.msg = "该标的还未发售";

			return result;
		}

		/** 不能投资自己发布的借款标 */
		if (bid.user_id == userId) {
			result.code = -1;
			result.msg = "不能投资自己发布的借款标";

			return result;
		}

		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		if (userFund == null) {
			result.code = -1;
			result.msg = "获取用户资金信息失败";

			return result;
		}

		/** 用户资金安全校验 */
		result = userFundService.userFundSignCheck(userId);
		if (result.code < 1) {

			return result;
		}

		/** 可用余额是否充足，例如用户投资1000元，使用10元现金券，则用户必须得有990元可用余额，否则不能执行投标操作 */
		if ((userFund.balance + cashAmt) < investAmt) {
			result.code = -1;
			result.msg = "余额不足";

			return result;
		}

		/** 标的是否可投 */
		if (!t_bid.Status.FUNDRAISING.equals(bid.getStatus())
				|| DateUtil.isDatetimeAfter(new Date(), bid.invest_expire_time)) {
			result.code = -1;
			result.msg = "此借款标已经不处于招标状态，请投资其他借款标！";

			return result;
		}

		/** 投资金额校验 */
		double leftAmt = bid.amount - bid.has_invested_amount; // 可投金额
		// 可投金额大于或等于最低投标金额时，投资金额必须大于或等于最低投标金额
		if (leftAmt >= bid.min_invest_amount && investAmt < bid.min_invest_amount) {
			result.code = -1;
			result.msg = "投资金额必须大于或等于最低投标金额！";

			return result;
		}

		// 投资金额 不能大于可投金额
		if (investAmt > leftAmt) {
			result.code = -1;
			result.msg = "投资金额必须小于或等于可投金额";

			return result;
		}

		// 可投金额小于最低投标金额时，投资金额必须等于可投金额
		if (leftAmt < bid.min_invest_amount && investAmt != leftAmt) {
			result.code = -1;
			result.msg = "当前可投金额小于起购金额，投资金额必须等于可投金额！";

			return result;
		}

		// 是否开启投标密码
		if (bid.is_invest_password) {

			if (StringUtils.isBlank(investPassword)) {

				result.code = -1;
				result.msg = "请填写投资密码！";

				return result;
			}

			t_group_menbers group = groupMenbersService.findByID(bid.group_id);

			if (group == null) {

				result.code = -1;
				result.msg = "查询分组信息失败！";

				return result;
			}

			t_group_menbers_user menUser = groupMenbersService.findMenberUserInfo(bid.group_id, userId);

			if (menUser == null) {

				result.code = -1;
				result.msg = "该标的只向" + group.name + "开放！";

				return result;
			}

			if (!bid.invest_password.equals(investPassword)) {

				result.code = -1;
				result.msg = "投资密码错误！";

				return result;
			}

		}

		result.code = 1;
		result.msg = "投标准备完毕";

		return result;
	}

	/**
	 * 投标(执行)
	 *
	 * @param userId
	 *            用户ID
	 * @param bidId
	 *            标的ID
	 * @param investAmt
	 *            投标金额
	 * @param loanServiceFeeDivide
	 *            分摊的借款服务费
	 * @param serviceOrderNo
	 *            业务订单号
	 * @param trustOrderNo
	 *            第三方返回交易订单号
	 * @param client
	 *            客户端
	 * @param investType
	 *            投标方式
	 * @param redPacketId
	 *            红包ID
	 * @param redPacketAmt
	 *            红包金额
	 * @param cashId
	 *            现金券ID
	 * @param cashAmt
	 *            现金券金额
	 * @param rateId
	 *            加息券ID
	 * @param addRate
	 *            加息券年利率
	 * 
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月28日
	 */
	public ResultInfo doInvest(long userId, long bidId, double investAmt, String serviceOrderNo, String trustOrderNo,
			int client, int investType, long redPacketId, double redPacketAmt, long cashId, double cashAmt, long rateId,
			double addRate) {
		ResultInfo result = new ResultInfo();

		t_bid bid = bidService.findByID(bidId);
		if (bid == null) {
			result.code = -1;
			result.msg = "该借款标不存在,[bidId:" + bidId + "]";

			return result;
		}

		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		if (userFund == null) {
			result.code = -1;
			result.msg = "获取用户资金信息失败,[userId:" + userId + "]";

			return result;
		}

		/** 计算投标进度，用于超额投标控制 */
		double schedule = Arith.divDown(Arith.mul(Arith.add(bid.has_invested_amount, investAmt), 100.0), bid.amount, 2);
		if (schedule > 100.0) {
			result.code = ResultInfo.OVER_BID_AMOUNT;
			result.msg = "超额投标";

			return result;
		}

		if (redPacketId > 0L && cashId > 0L) {
			result.code = ResultInfo.OVER_BID_AMOUNT;
			result.msg = "红包及现金券不能同时使用";

			return result;
		}

		int rows = 0;

		if (redPacketId > 0L) {
			// 如果红包状态已经被修改，那么此次投资就当超标处理
			rows = redpacketUserService.updateUserRedPacketStatus(redPacketId,
					t_red_packet_user.RedpacketStatus.USING.code, t_red_packet_user.RedpacketStatus.USED.code);
			if (rows <= 0) {
				result.code = -1316;
				result.msg = "修改红包状态失败";

				return result;
			}
		}

		if (cashId > 0L) {
			// 如果现金券状态已经被修改，那么此次投资就当超标处理
			rows = cashUserService.updateUserCashStatus(cashId, t_cash_user.CashStatus.USING.code,
					t_cash_user.CashStatus.USED.code);
			if (rows <= 0) {
				result.code = -1316;
				result.msg = "修改现金券状态失败";

				return result;
			}
		}

		if (rateId > 0L) {
			// 如果加息券状态已经被修改，那么此次投资就当超标处理
			rows = rateService.updateUserRateStatus(rateId, t_addrate_user.RateStatus.USING.code,
					t_addrate_user.RateStatus.USED.code);
			if (rows <= 0) {
				result.code = -1316;
				result.msg = "修改加息券状态失败";

				return result;
			}
		}

		// 更新投标进度和已投金额，并防止并发情况
		result = bidService.updateBidScheduleAndInvestAmt(bid.id, investAmt, schedule);
		if (result.code < 1) {

			return result;
		}

		/** 满标: 更新满标时间, 更新标的状态为（复审中） */
		if (bid.amount == (bid.has_invested_amount + investAmt)) {
			result = bidService.bidExpire(bid.id);
			if (result.code < 1) {

				return result;
			}
		}

		/** 冻结投资金额，需要扣除掉使用的现金券金额 */
		boolean freezeAmt = userFundService.userFundFreeze(userId, investAmt - cashAmt);
		if (!freezeAmt) {
			result.code = -1;
			result.msg = "冻结用户资金失败";

			return result;
		}

		/** 用户资产签名更新 */
		result = userFundService.userFundSignUpdate(userId);
		if (result.code < 1) {
			result.code = -1;
			result.msg = "用户资产签名更新失败，请联系管理员";

			return result;
		}

		/** 添加用户交易记录 */
		userFund = userFundService.queryUserFundByUserId(userId); // 刷新用户资金
		Map<String, String> summaryPrams = new HashMap<String, String>();
		summaryPrams.put("bidNo", bid.bid_no);
		boolean addDeal = dealUserService.addDealUserInfo(serviceOrderNo, // 订单号
				userId, investAmt - cashAmt, // 实际投资金额需要扣除掉使用的现金券金额
				userFund.balance, userFund.freeze, t_deal_user.OperationType.INVEST_FREEZE, summaryPrams);

		if (!addDeal) {
			result.code = -1;
			result.msg = "添加交易记录失败！";

			return result;
		}

		/** 更新会员性质 */
		boolean memberType = userInfoService.updateUserMemberType(userId, t_user_info.MemberType.FINANCIAL_MEMBER);
		if (!memberType) {
			result.code = -1;
			result.msg = "更新会员状态时失败";

			return result;
		}

		/** 计算分摊的借款服务费 */
		double loanServiceFeeDivide = Arith.div(Arith.mul(investAmt, bid.loan_fee), bid.amount, 2);

		t_invest invest = new t_invest();
		invest.user_id = userId;
		invest.time = new Date();
		invest.bid_id = bid.id;
		invest.amount = investAmt;
		invest.loan_service_fee_divide = loanServiceFeeDivide;
		invest.setClient(Client.getEnum(client));
		invest.service_order_no = serviceOrderNo;
		invest.trust_order_no = trustOrderNo;
		invest.setInvest_type(InvestType.getEnum(investType));
		invest.red_packet_id = redPacketId;
		invest.red_packet_amount = redPacketAmt;
		invest.cash_id = cashId;
		invest.cash_amount = cashAmt;

		// 投标奖励
		if (bid.is_invest_reward) {

			invest.is_invest_reward = true;
			invest.reward_amount = calculateInvestReward(investAmt, bid.reward_rate, bid.period,
					bid.getPeriod_unit().code, bid.getRepayment_type().code);
		}
		// 加息卷

		if (rateId > 0L) {
			invest.rate_id = rateId;
			invest.rate_amount = calculateInvestAddRate(investAmt, addRate, bid.period, bid.getPeriod_unit().code,
					bid.getRepayment_type().code);
		}

		invest = investDao.saveT(invest);
		if (invest == null) {
			result.code = -1;
			result.msg = "保存投资信息失败";

			return result;
		}

		/** 发送通知 */
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("user_name", userFund.name);
		param.put("bid_no", bid.bid_no);
		param.put("bid_name", bid.title);
		param.put("amount", investAmt);
		param.put("period_num", bid.period);
		param.put("period_unit", bid.getPeriod_unit().getShowValue());
		param.put("apr", bid.apr);
		param.put("repayment_type", bid.getRepayment_type().value);
		noticeService.sendSysNotice(userId, NoticeScene.INVEST_SUCC, param);

		result.code = 1;
		result.msg = "投标成功";
		result.obj=invest;
		return result;
	}

	/**
	 * 投标使用红包校验
	 * 
	 * @param bid_period
	 * @param period_unit
	 * @param userId
	 * @param redPacketId
	 * @param investAmt
	 * 
	 * @return
	 */
	public ResultInfo investRedPacket(long userId, long redPacketId, double investAmt, int bid_period,
			int period_unit) {
		ResultInfo result = new ResultInfo();
		t_red_packet_user redPacketUser = redpacketUserService.queryRedPacket(redPacketId);

		if (redPacketUser.user_id != userId) {
			result.code = -3;
			result.msg = "您所使用的红包不存在";

			return result;
		}

		if (!(investAmt >= redPacketUser.use_rule)) {
			result.code = -3;
			result.msg = "投资金额必须大于等于红包最低投资金额";

			return result;
		}

		if (DateUtil.isDateAfter(new Date(), redPacketUser.end_time)) {
			result.code = -3;
			result.msg = "您所使用的红包已过期";

			int rows = redpacketUserService.updateUserRedPacketStatus(redPacketId,
					t_red_packet_user.RedpacketStatus.UNUSED.code, t_red_packet_user.RedpacketStatus.EXPIRED.code);

			if (rows <= 0) {
				result.code = -3;
				result.msg = "修改红包状态失败";
			}

			return result;
		}

		if (redPacketUser.getStatus().code != t_red_packet_user.RedpacketStatus.UNUSED.code) {
			result.code = -3;
			result.msg = "红包状态错误";

			return result;
		}

		if (redPacketUser.bid_period > 0) {
			// 月标
			if (t_product.PeriodUnit.MONTH.code == period_unit) {
				if (redPacketUser.bid_period < bid_period) {

					result.code = -3;
					result.msg = "投资标的期限必须大于等于红包标的期限";

					return result;
				}
			}
		}

		result.code = 1;
		result.msg = "";
		result.obj = redPacketUser;

		return result;
	}

	/**
	 * 更新某个投资的转让状态
	 *
	 * @param investId
	 *            投资id
	 * @param status
	 *            更新后的状态
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月25日
	 */
	public boolean updateTransferStatus(Long investId, TransferStatus status) {
		t_invest invest = investDao.findByID(investId);
		invest.setTransfer_status(status);

		return investDao.save(invest);
	}

	/**
	 * 根据主键查找实体
	 */
	public t_invest findByID(long investId) {

		return investDao.findByID(investId);
	}

	/**
	 * 获取某个用户的总投资金额(由债权转让过来的不算)
	 *
	 * @param userId
	 *            用户id
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年4月7日
	 */
	public double findTotalInvest(Long userId) {

		return investDao.findTotalInvest(userId);
	}

	/**
	 * 获取投资金额
	 * 
	 * @param startTime
	 * @param endTime
	 * @param type
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月25日
	 *
	 */
	public double findTotalInvestByDate(String startTime, String endTime, int type) {

		return investDao.findTotalInvestByDate(startTime, endTime, type);
	}

	/**
	 * 查询一个借款标的所有投资记录
	 *
	 * @param bidId
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月20日
	 */
	public List<BidInvestRecord> listOfBidInvestRecords(long bidId) {

		return investDao.listOfBidInvestRecords(bidId);
	}

	/**
	 * 查询一个借款标的所有用户信息(借款合同)
	 *
	 * @param bidId
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月20日
	 */
	public List<t_user_info> listOfBidInvestUsers(long bidId) {
		return investDao.listOfBidInvestUsers(bidId);
	}

	/**
	 * 用于标的详情的投资列表
	 *
	 * @param currPage
	 * @param pageSize
	 * @param bidId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月27日
	 */
	public PageBean<BidInvestRecord> pageOfBidInvestDetail(int currPage, int pageSize, long bidId) {
		return investDao.pageOfBidInvestDetail(currPage, pageSize, bidId);
	}

	/**
	 * 分页查询 某个用户的投资记录
	 * 
	 * @param currPage
	 *            当前页
	 * @param pageSize
	 *            每页条数
	 * @param userId
	 *            用户userId
	 * @return
	 *
	 * @author liudong
	 * @createDate 2015年12月17日
	 */
	public PageBean<UserInvestRecord> pageOfUserInvestRecord(int currPage, int pageSize, long userId) {
		return investDao.pageOfUserInvestRecord(currPage, pageSize, userId);
	}

	/**
	 * 分页查询 某个用户的投资记录 （CPS）
	 * 
	 * @param currPage
	 *            当前页
	 * @param pageSize
	 *            每页条数
	 * @param userId
	 *            用户userId
	 * @return
	 *
	 * @author liudong
	 * @createDate 2015年12月17日
	 */
	public PageBean<CpsUserInvest> pageOfCpsUserInvestRecord(int currPage, int pageSize, long userId) {
		return investDao.pageOfCpsUserInvestRecord(currPage, pageSize, userId);
	}

	/**
	 * 获取标的的投资记录
	 *
	 * @param bidId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月22日
	 */
	public List<t_invest> queryBidInvest(long bidId) {

		List<t_invest> list = new ArrayList<t_invest>();

		list = investDao.findListByColumn("bid_id = ? ", bidId);
		return list;
	}

	/**
	 * 查询最新理财信息
	 * 
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月29日
	 *
	 */
	public List<Map<String, Object>> queryNewInvestList() {
		List<Map<String, Object>> newInvests = investDao.queryNewInvestList();
		List<Map<String, Object>> investsList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> map : newInvests) {
			Map<String, Object> investsMap = new HashMap<String, Object>();
			Date time = (Date) map.get("time");
			investsMap.put("time", DateUtil.getTimeLine(time));
			investsMap.put("photo", map.get("photo"));
			investsMap.put("amount", map.get("amount"));
			investsMap.put("bid_no", NoUtil.getBidNo(Long.parseLong(map.get("id").toString())));
			investsList.add(investsMap);
		}

		return investsList;
	}

	/**
	 * 查询每周用户投资金额排行
	 * 
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月29日
	 *
	 */
	public List<Map<String, Object>> queryWeekInvestList(int count) {

		return investDao.queryWeekInvestList(count);
	}

	/**
	 * 根据user_id查询t_auto_invest_setting
	 * 
	 * @param userId
	 * @return
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年03月24日
	 */
	public t_auto_invest_setting findAutoInvestByUserId(long userId) {

		return autoInvestSettingDao.findAutoInvestOptionByUserId(userId);
	}

	/**
	 * 保存自动投标设置信息
	 * 
	 * @param userId
	 * @param minApr
	 * @param maxPeriod
	 * @param investAmt
	 * @return
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年03月24日
	 */
	public ResultInfo saveAutoInvest(long userId, double minApr, int maxPeriod, double investAmt) {
		ResultInfo result = new ResultInfo();

		t_auto_invest_setting autoInvestOption = autoInvestSettingDao.findByColumn("user_id = ?", userId);

		if (autoInvestOption == null) {
			autoInvestOption = new t_auto_invest_setting();
		}
		autoInvestOption.time = new Date();
		autoInvestOption.user_id = userId;
		autoInvestOption.min_apr = minApr;
		autoInvestOption.max_period = maxPeriod;
		autoInvestOption.amount = investAmt;
		autoInvestOption.status = ConfConst.IS_TRUST ? false : true;

		boolean isSuccess = autoInvestSettingDao.save(autoInvestOption);
		if (!isSuccess) {
			result.code = -1;
			result.msg = "保存自动投标信息失败";

			return result;
		}

		result.code = 1;
		result.msg = "开启自动投标成功";

		return result;
	}

	/**
	 * 更新自动投标状态
	 * 
	 * @param userId
	 * @return
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年03月24日
	 */
	public ResultInfo updateAutoInvestStatus(long userId) {
		ResultInfo result = new ResultInfo();

		int i = autoInvestSettingDao.updateAutoInvestStatus(userId, true);
		if (i != 1) {
			result.code = -1;
			result.msg = "开启自动投标失败";

			return result;
		}

		result.code = 1;
		result.msg = "开启自动投标成功";

		return result;
	}

	/**
	 * 关闭自动投标
	 * 
	 * @param userId
	 * @return
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年03月25日
	 */
	public ResultInfo closeAutoInvest(long userId) {
		ResultInfo result = new ResultInfo();

		t_auto_invest_setting autoInvestOption = autoInvestSettingDao.findByColumn("user_id = ?", userId);
		if (autoInvestOption == null) {
			result.code = 1;
			result.msg = "关闭自动投标成功";
			return result;
		}

		int i = autoInvestSettingDao.updateAutoInvestStatus(userId, false);
		if (i != 1) {
			result.code = -1;
			result.msg = "关闭自动投标失败";

			return result;
		}

		result.code = 1;
		result.msg = "关闭自动投标成功";

		return result;
	}

	/**
	 * 查出所有开启自动投标的用户
	 * 
	 * @return
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年03月25日
	 */
	public List<Object> queryAllAutoUser() {

		return autoInvestSettingDao.queryAllAutoUser();
	}

	/**
	 * 判断该借款标是否超过95%
	 * 
	 * @param bidId
	 * @return
	 * 
	 * @author ZhouYuanZeng
	 * @createDate 2016年03月25日
	 */
	public boolean judgeSchedule(long bidId) {
		Double schedule = bidService.findBidSchedule(bidId);

		/*
		 * if(schedule == null ){ return false; }
		 */

		if (schedule == null || schedule >= 95) {
			return false;
		}

		return true;
	}

	/**
	 * 将用户排到自动投标队尾
	 * 
	 * @param userId
	 * @return
	 * 
	 * @author ZhouYuanZeng
	 * @createDate 2016年03月25日
	 */
	public void updateUserAutoBidTime(long userId) {
		autoInvestSettingDao.updateUserAutoBidTime(userId);
	}

	/**
	 * 判断用户是否已经自动投过当前标
	 * 
	 * @param userId
	 * @param bidId
	 * @return
	 * 
	 * @author ZhouYuanZeng
	 * @createDate 2016年03月25日
	 */
	public boolean hasAutoInvestTheBid(long userId, long bidId) {

		boolean flag = false;
		t_auto_bid bid = autoBidDao.findAutoBidByUserIdAndBidId(userId, bidId);

		if (bid != null) {
			flag = true;
		}

		return flag;
	}

	/**
	 * 标的是否符合用户设置的条件
	 * 
	 * @param autoOptions
	 * @param unit
	 * @param bidId
	 * @return
	 * 
	 * @author ZhouYuanZeng
	 * @createDate 2016年03月25日
	 */
	public boolean judgeBidByParam(t_auto_invest_setting autoOptions, int unit, long bidId) {
		Long bid = autoInvestSettingDao.findBidByParam(autoOptions, unit, bidId);

		/*
		 * if(null == bid){ return false; }
		 */

		if (null == bid || bid < 1) {
			return false;
		}

		return true;
	}

	/**
	 * 计算最后投标金额
	 * 
	 * @param investAmt
	 * @param schedule
	 * @param amount
	 * @param hasInvestedAmt
	 * @return
	 * 
	 * @author ZhouYuanZeng
	 * @createDate 2016年03月25日
	 */
	public int calculateBidAmount(double investAmt, double schedule, double amount, double hasInvestedAmt) {

		int maxBidAmt = (int) (amount * 0.2);

		if (schedule < 95) {
			while (investAmt > maxBidAmt) {
				investAmt = investAmt - 50;
			}

			do {
				schedule = Arith.divDown(Arith.mul(Arith.add(hasInvestedAmt, investAmt), 100.0), amount, 2);
				if (schedule > 95) {
					investAmt = investAmt - 50;
				}
			} while (schedule > 95);
		}

		return (int) investAmt;
	}

	/**
	 * 计算自动投标份数
	 * 
	 * @param amount
	 * @param averageAmt
	 * @return
	 * 
	 * @author ZhouYuanZeng
	 * @createDate 2016年03月25日
	 */
	public int calculateFinalInvestAmount(double amount, double averageAmt) {
		int temp = 0;
		temp = (int) (amount / averageAmt);
		return temp;
	}

	/**
	 * 增加用户自动投标记录
	 * 
	 * @param userId
	 * @param bidId
	 * 
	 * @author ZhouYuanZeng
	 * @createDate 2016年03月25日
	 */
	public void addAutoBidRecord(long userId, long bidId) {

		t_auto_bid bid = new t_auto_bid();

		bid.bid_id = bidId;
		bid.time = new Date();
		bid.user_id = userId;

		autoBidDao.save(bid);
	}

	/**
	 * 根据标的id查询，未完成的债权转让投资用户
	 * 
	 * @param bidId
	 *            标的id
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年3月25日
	 *
	 */
	public List<t_invest> queryInvestTransfer(long bidId) {

		return investDao.findListByColumn("transfer_status=? AND bid_id=?", t_invest.TransferStatus.TRANSFERING.code,
				bidId);
	}

	/**
	 * 投标使用现金券校验
	 * 
	 * @param userId
	 * @param cashId
	 * @param investAmt
	 * 
	 * @return
	 */
	public ResultInfo investCash(long userId, long cashId, double investAmt) {
		ResultInfo result = new ResultInfo();
		t_cash_user cashUser = cashUserService.findCashUserById(cashId);

		if (cashUser.user_id != userId) {
			result.code = -3;
			result.msg = "您所使用的现金券不存在";

			return result;
		}

		if (!(investAmt >= cashUser.use_rule)) {
			result.code = -3;
			result.msg = "投资金额必须大于等于现金券最低投资金额";

			return result;
		}

		if (DateUtil.isDateAfter(new Date(), cashUser.end_time)) {
			result.code = -3;
			result.msg = "您所使用的现金券已过期";

			int rows = cashUserService.updateUserCashStatus(cashId, t_cash_user.CashStatus.UNUSED.code,
					t_cash_user.CashStatus.EXPIRED.code);

			if (rows <= 0) {
				result.code = -3;
				result.msg = "修改现金券状态失败";
			}

			return result;
		}

		if (cashUser.getStatus().code != t_cash_user.CashStatus.UNUSED.code) {
			result.code = -3;
			result.msg = "现金券状态错误";

			return result;
		}

		result.code = 1;
		result.msg = "";
		result.obj = cashUser;

		return result;
	}

	/**
	 * 投标使用加息券校验
	 * 
	 * @param userId
	 * @param rateId
	 * @param investAmt
	 * 
	 * @return
	 */
	public ResultInfo investRate(long userId, long rateId, double investAmt, int bid_period, int period_unit) {
		ResultInfo result = new ResultInfo();
		t_addrate_user rateUser = rateService.findByID(rateId);

		if (rateUser.user_id != userId) {
			result.code = -3;
			result.msg = "您所使用的加息券不存在";

			return result;
		}

		if (!(investAmt >= rateUser.use_rule)) {
			result.code = -3;
			result.msg = "投资金额必须大于等于加息券最低投资金额";

			return result;
		}

		if (DateUtil.isDateAfter(new Date(), rateUser.end_time)) {
			result.code = -3;
			result.msg = "您所使用的加息券已过期";

			int rows = rateService.updateUserRateStatus(rateId, t_addrate_user.RateStatus.UNUSED.code,
					t_addrate_user.RateStatus.EXPIRED.code);

			if (rows <= 0) {
				result.code = -3;
				result.msg = "修改加息券状态失败";
			}

			return result;
		}

		if (rateUser.getRateStatus().code != t_addrate_user.RateStatus.UNUSED.code) {
			result.code = -3;
			result.msg = "加息券状态错误";

			return result;
		}

		if (rateUser.bid_period > 0) {
			// 月标
			if (t_product.PeriodUnit.MONTH.code == period_unit) {
				if (rateUser.bid_period < bid_period) {

					result.code = -3;
					result.msg = "投资标的期限必须大于等于加息卷标的期限";

					return result;
				}
			}
		}

		result.code = 1;
		result.msg = "";
		result.obj = rateUser;

		return result;
	}

	/**
	 * 月排行榜
	 *
	 * @return
	 *
	 * @author YanPengFei
	 * @createDate 2017年2月28日
	 */
	public List<Map<String, Object>> queryMonthInvestList(int count) {

		return investDao.queryMonthInvestList(count);
	}

	/**
	 * 总排行榜
	 *
	 * @return
	 *
	 * @author YanPengFei
	 * @createDate 2017年2月28日
	 */
	public List<Map<String, Object>> queryTotalInvestList(int count) {

		return investDao.queryTotalInvestList(count);
	}

	/**
	 * 查询用户是否有投资记录
	 * 
	 * @param userId
	 * @return
	 */
	public long queryIsHaveInvestRecord(long userId) {

		return investDao.queryIsHaveInvestRecord(userId);
	}

	/**
	 * 统计用户投资次数
	 *
	 * @param userid
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月17日
	 */
	public int countInvestOfUser(long userId) {

		return investDao.countInvestOfUser(userId);
	}
	
	/**
	 * 统计用户投资次数
	 *
	 * @param userid
	 * @param isFilteDebt 是否过滤债券转让
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月17日
	 */
	public int countInvestOfUser(long userId,boolean isFilteDebt) {
		return investDao.countInvestOfUser(userId,isFilteDebt);
	}

	/**
	 * 统计用户投资记录
	 *
	 * @param userid
	 * @return
	 *
	 * @author yuechuanyang
	 * @createDate 2017年10月17日
	 */
	public List<t_invest> queryInvestRecordByUserId(long userId){
		return investDao.queryInvestRecordByUserId(userId);
	}
	
	/**
	 * 计算投标奖励的具体金额
	 * 
	 * @param investAmount
	 *            投资金额
	 * @param rate
	 *            投标奖励年利率
	 * @param period
	 *            投资期限
	 * @param unit
	 *            标的期限类型
	 * @param repayment_type
	 *            还款方式
	 * @return
	 * @author jiayijian
	 * @createDate 2017年4月5日
	 */
	public double calculateInvestReward(double investAmount, double rate, int period, int unit, int repayment_type) {

		double rate_amount = Arith.divDown(Arith.mul(investAmount, rate), 100, 24);
		if (1 == unit) {// 天标
			rate_amount = Arith.divDown(rate_amount, 360, 24);
			rate_amount = Arith.mul(rate_amount, period);
		} else {// 月标

			if (2 == repayment_type) {// 等额本息
				rate_amount = Arith.divDown(
						Arith.mul(investAmount * rate / 12 / 100, Math.pow(1 + rate / 12 / 100, period)),
						Math.pow(1 + rate / 12 / 100, period) - 1, 2);
				rate_amount = Arith.mul(rate_amount, period) - investAmount;
			} else {// 一次性还款,先息后本
				rate_amount = Arith.divDown(rate_amount, 12, 24);
				rate_amount = Arith.mul(rate_amount, period);
			}
		}

		return Arith.round(rate_amount, 2);
	}

	/**
	 * 计算投标使用加息卷的具体金额
	 * 
	 * @param investAmount
	 *            投资金额
	 * @param rate
	 *            加息卷年利率
	 * @param period
	 *            投资期限
	 * @param unit
	 *            标的期限类型
	 * @param repayment_type
	 *            还款方式
	 * @return
	 * @author jiayijian
	 * @createDate 2017年4月5日
	 */
	public double calculateInvestAddRate(double investAmount, double rate, int period, int unit, int repayment_type) {

		double rate_amount = Arith.divDown(Arith.mul(investAmount, rate), 100, 24);
		if (1 == unit) {// 天标
			rate_amount = Arith.divDown(rate_amount, 360, 24);
			rate_amount = Arith.mul(rate_amount, period);
		} else {// 月标

			if (2 == repayment_type) {// 等额本息
				rate_amount = Arith.divDown(
						Arith.mul(investAmount * rate / 12 / 100, Math.pow(1 + rate / 12 / 100, period)),
						Math.pow(1 + rate / 12 / 100, period) - 1, 2);
				rate_amount = Arith.mul(rate_amount, period) - investAmount;
			} else {// 一次性还款,先息后本
				rate_amount = Arith.divDown(rate_amount, 12, 24);
				rate_amount = Arith.mul(rate_amount, period);
			}
		}

		return Arith.round(rate_amount, 2);
	}

	/**
	 * 获取标的的使用加息卷的投资记录
	 *
	 * @param bidId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月22日
	 */
	public List<t_invest> queryBidInvestUsedRate(long bidId) {

		List<t_invest> list = new ArrayList<t_invest>();

		list = investDao.findListByColumn("bid_id = ? and rate_id > 0 and rate_amount > 0 ", bidId);
		return list;
	}

	/**
	 * 
	 * 计算总利息（用于统计到总收益上）
	 * 
	 * @param userId
	 */
	public double calculateTotalInterest(long userId) {
		return investDao.calculateTotalInterest(userId);

	}

	public double calulateInvestMoneyInDates(long userId, Date startDate, Date endDate) {
		return investDao.calulateInvestMoneyInDates(userId, startDate, endDate);
	}

	public long calulateInvestMoneyInDates(String start, String end) {
		return investDao.calulateInvestMoneyInDates(start, end);
	}

	public int delInvest(long investId) {
		return investDao.delete(investId);
	}

	public double queryBidInvest(String date) {
		String sql = "SELECT SUM(amount) FROM t_invest WHERE time >= :time";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("time", date);
		Object obj = investDao.findSingleBySQL(sql, map);
		return obj == null ? 0 : Convert.strToDouble(obj.toString(), 0);
	}

	public List<Map<String, Object>> queryMoneyAndUser(String startDate, String endDate) {
		String sql = "SELECT user_id AS userId, TRUNCATE(amount / 100, 0) * 100 AS amount FROM t_invest WHERE time >= :start AND time < :end";
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("start", startDate);
		args.put("end", endDate);
		return investDao.findListMapBySQL(sql, args);
	}

	public long calulateInvestMoneyInDatesWithPeriod(long userId, String start, String end, int period) {
		return investDao.calulateInvestMoneyInDatesWithPeriod(userId, start, end, period);
	}
}
