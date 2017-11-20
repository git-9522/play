package services.activity;

import common.enums.Client;
import common.enums.ServiceType;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.OrderNoUtil;
import common.utils.ResultInfo;
import hf.HfUtils;
import models.common.entity.t_deal_platform.DealRemark;
import models.common.entity.t_deal_user.OperationType;
import models.common.entity.t_user_fund;
import models.core.entity.t_bid;
import models.core.entity.t_invest_log;
import payment.impl.PaymentProxy;
import services.base.BaseService;
import services.common.DealPlatformService;
import services.common.DealUserService;
import services.common.UserFundService;
import services.core.BidService;

/**
 * 11 月 活动5 业务处理（复投参与）
 * 
 * @Title Invert11Actity6
 * @Description 虹金所脱单红包
 * @author hjs_djk
 * @createDate 2017年10月23日 下午6:07:43
 */
public class Invert11Activity5Service extends BaseService {
	protected static  DealPlatformService dealPlatformService = Factory.getService(DealPlatformService.class);
	protected static  DealUserService dealUserService = Factory.getService(DealUserService.class);
	protected static  UserFundService userFundService = Factory.getService(UserFundService.class);
	protected static BidService bidService = Factory.getService(BidService.class);
	/** 开始时间 **/
	public static final String start_time = new String("2017-11-01 00:00:00");
	/** 结束时间 **/
	public static final String end_time = new String("2017-11-30 23:59:59");
	public ResultInfo sendPacketMoney(t_invest_log investlog) {
		ResultInfo result = new ResultInfo();
		if (DateUtil.isBetweenDate(DateUtil.strToDate(start_time, "yyyy-MM-dd HH:mm:ss"),
				DateUtil.strToDate(end_time, "yyyy-MM-dd HH:mm:ss"))) {
			if (!investlog.is_first_invest) { // 首次投资者不参与
				t_bid bid = bidService.findByID(investlog.bid_id);
				double transAmt = 0.0;
				// 仲夏夜之梦之第二章:仲夏红包
				if (bid.period == 1) {// 筛选线下用户并某周期标的不享受返现活动
					// 一月标 
					transAmt = investlog.amount * 0.30/100 ;
				} else if (bid.period == 2) {
					// 二月标
					transAmt = investlog.amount * 0.63/100;
				} else if (bid.period == 3) {
					// 三月标
					transAmt = investlog.amount * 1.00/100;
				} else if (bid.period == 6) {
					// 六月标
					transAmt = investlog.amount * 2.00/100;
				} else if (bid.period ==12) {
					// 12月标
					transAmt = investlog.amount * 1.00/100;
				}
				if (transAmt > 0) {
					// 转账
					transAmt=Double.parseDouble(HfUtils.formatAmount(transAmt));;
					String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.TRANSFER);
					result = PaymentProxy.getInstance().transfer(Client.PC.code, serviceOrderNo, investlog.user_id, transAmt);
					if (result.code < 1) {
						return result;
					}
					// 处理平台记录
					dealPlatformService.addPlatformDeal(investlog.user_id, transAmt, DealRemark.RED_PACKET_TRANSFER, null);
					// 处理个人记录
					boolean isSignSuccess = true; // 用户签名是否通过

					result = userFundService.userFundSignCheck(investlog.user_id);
					if (result.code < 1) {
						isSignSuccess = false;
					}

					boolean addFund = userFundService.userFundAdd(investlog.user_id, transAmt);
					if (!addFund) {
						result.code = -1;
						result.msg = "增加用户可用余额失败";
						return result;
					}

					t_user_fund userFund = userFundService.queryUserFundByUserId(investlog.user_id);

					if (isSignSuccess) {
						userFundService.userFundSignUpdate(investlog.user_id);
					}

					dealUserService.addDealUserInfo(serviceOrderNo, investlog.user_id, transAmt, userFund.balance, userFund.freeze,
							OperationType.RED_PACKET_TRANSFER, null);
					result.code = 1;
					result.msg = "本次活动【虹金所脱单红包】投资记录【"+investlog.invest_id+"】奖励发放完毕";
				}else{
					result.code = -1;
					result.msg = "投资奖励<=0，不发放本次活动【虹金所脱单红包】奖励";
				}
			}else{
				
				result.code = -1;
				result.msg = "首次投资不参与本活动，不发放本次活动【虹金所脱单红包】奖励";
			}
		}else{
			result.code = -1;
			result.msg = "不在活动时间内，不发放本次活动【虹金所脱单红包】奖励";
			
		}
		return result;
	}

}
