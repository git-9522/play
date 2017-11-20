package services.activity;

import java.util.Date;
import java.util.List;

import common.enums.Client;
import common.enums.ServiceType;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.JPAUtil;
import common.utils.OrderNoUtil;
import common.utils.ResultInfo;
import daos.activity.Invert11Actity6Dao;
import hf.HfUtils;
import models.activity.entity.t_invert_11_activity6_log;
import models.common.entity.t_deal_platform.DealRemark;
import models.common.entity.t_deal_user.OperationType;
import models.common.entity.t_user_fund;
import models.core.entity.t_bid;
import models.core.entity.t_invest;
import payment.impl.PaymentProxy;
import services.base.BaseService;
import services.common.DealPlatformService;
import services.common.DealUserService;
import services.common.UserFundService;
import services.core.BidService;
import services.core.InvestService;

/**
 * 11 月 活动6 业务处理（复投参与）
 * 
 * @Title Invert11Actity6
 * @Description 千里姻缘一线牵、俊男美女任您选（抽奖记录）
 * @author hjs_djk
 * @createDate 2017年10月23日 下午6:07:43
 */
public class Invert11Activity6Service extends BaseService<t_invert_11_activity6_log> {
	/** 返现比例 **/
	public static double rate = 0.01;
	/** 开始时间 **/
	public static final String start_time = new String("2017-11-01 00:00:00");
	/** 结束时间 **/
	public static final String end_time = new String("2017-11-30 23:59:59");
	public static InvestService investservice = Factory.getService(InvestService.class);
	public static BidService bidservice = Factory.getService(BidService.class);
	public static DealPlatformService dealPlatformService = Factory.getService(DealPlatformService.class);
	public static DealUserService dealUserService = Factory.getService(DealUserService.class);
	public static UserFundService userFundService = Factory.getService(UserFundService.class);
	protected Invert11Actity6Dao invert11actity6dao;

	protected Invert11Activity6Service() {
		invert11actity6dao = Factory.getDao(Invert11Actity6Dao.class);
		super.basedao = invert11actity6dao;
	}

	public ResultInfo run(Long userID, int cid, Long inver_id) {
		ResultInfo result = new ResultInfo();
		Date startDate = DateUtil.strToDate(start_time, "yyyy-MM-dd HH:mm:ss");
		Date endDteate = DateUtil.strToDate(end_time, "yyyy-MM-dd HH:mm:ss");
		long thistime = new Date().getTime();
		if ((startDate.getTime() > thistime) || (endDteate.getTime() < thistime)) {
			result.code = -1;
			result.msg = "活动未开启";
			return result;
		}
		List<t_invest> list = invert11actity6dao.queryList(start_time, end_time, userID);
		if (list == null || list.size() < 1) {
			result.code = -1;
			result.msg = "抽奖次数不足";
			return result;
		}
		t_invest inverInvest = investservice.findByID(inver_id);
		if (inverInvest == null) {
			result.code = -2;
			result.msg = "参数异常";
			return result;
		}
		if(!inverInvest.user_id.equals(userID)){
			result.code = -3;
			result.msg = "参数异常";
			return result;
			
		}

		if (invert11actity6dao.checkCount(start_time, end_time, userID, inver_id)) {
			result.code = -2;
			result.msg = "抽奖次数不足";
			return result;
		}

		if (inverInvest != null) {
			t_user_fund userFund = userFundService.queryUserFundByUserId(userID);
			t_bid bid = bidservice.findByID(inverInvest.bid_id);
			if (bid == null) {
				result.code = -3;
				result.msg = "抽奖次数不足";
				return result;
			}
			t_invert_11_activity6_log log = new t_invert_11_activity6_log();
			log.invest_id = inverInvest.id;
			log.cid = cid;
			log.amount = inverInvest.amount;
			log.user_id = userID;
			log.user_name = userFund.name;
			// 约会金
			log.value = Double.parseDouble(HfUtils.formatAmount((inverInvest.amount / 12 * bid.period) * rate));;
			log.time = new Date();
			String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.TRANSFER);
			 JPAUtil.transactionBegin();
			result = PaymentProxy.getInstance().transfer(Client.PC.code, serviceOrderNo, userID, log.value);
			if (result.code == 1) {
			
				log.status = true;
				invert11actity6dao.save(log);
				// 处理平台记录
				dealPlatformService.addPlatformDeal(userID, log.value, DealRemark.REVERSAL_TRANFER, null);
				// 处理个人记录
				 JPAUtil.transactionCommit();
				 JPAUtil.transactionBegin();
				userFund = userFundService.queryUserFundByUserId(userID);
				userFund.balance = userFund.balance + log.value;
				userFundService.update(userFund);
				// 更新用户签名
				userFundService.userFundSignUpdate(userID);
				JPAUtil.transactionCommit();
				 JPAUtil.transactionBegin();
				dealUserService.addDealUserInfo(serviceOrderNo, userID, log.value, userFund.balance, userFund.freeze,
						OperationType.REVERSAL_TRANFER, null);
				JPAUtil.transactionCommit();	
				result.msg = "抽奖成功！";
				result.obj=log.value;
			} else {
				result.code = -4;
				result.msg = "抽奖失败";
			}
		}
		return result;
	}

	/**
	 * 查询投资记录
	 * 
	 * @param user_id
	 * @return
	 */
	public ResultInfo queryList(Long user_id) {
		ResultInfo result = new ResultInfo();
		Date startDate = DateUtil.strToDate(start_time, "yyyy-MM-dd HH:mm:ss");
		Date endDteate = DateUtil.strToDate(end_time, "yyyy-MM-dd HH:mm:ss");
		long thistime = new Date().getTime();
		if ((startDate.getTime() > thistime) || (endDteate.getTime() < thistime)) {
			result.code = -1;
			result.msg = "活动未开启";
			return result;
		}
		result.code = 1;
		result.msg = "查询成功！";
		result.obj = invert11actity6dao.findAllInverstList(start_time,end_time,user_id );
		return result;
	}
}
