package jobs;

import java.util.List;
import java.util.Map;

import models.core.entity.t_auto_invest_setting;
import models.core.entity.t_bid;
import models.core.entity.t_invest.InvestType;

import com.shove.Convert;

import common.constants.ConfConst;
import common.constants.SettingKey;
import common.enums.Client;
import common.enums.ServiceType;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.OrderNoUtil;
import common.utils.ResultInfo;
import payment.impl.PaymentProxy;
import play.Logger;
import play.db.jpa.JPAPlugin;
import play.jobs.Every;
import play.jobs.Job;
import services.common.SettingService;
import services.core.BidService;
import services.core.InvestService;

/** 
 * 自动投标，每15分钟执行一次
 * 
 * @description 
 * 
 * @author ZhouYuanZeng 
 * @createDate 2016年3月25日 上午9:52:33  
 */
@Every("15min")
public class AutoInvest extends Job {
	
	@Override
	public void doJob() throws Exception{
		
		if(!ConfConst.IS_START_JOBS){
			return;
		}
		
		/** 注入系统设置service */
		SettingService settingService = Factory.getService(SettingService.class);
		
		/* 平台是否开启自动投标 */
		int isAutoInvestShow = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.IS_AUTO_INVEST_SHOW), 0);
		if(isAutoInvestShow == 0)
		{
			return;
		}
		
		Logger.info("-----------开始执行定时任务：自动投标----------");
		
		InvestService investService = Factory.getService(InvestService.class);
		
		BidService bidService = Factory.getService(BidService.class);
		
		/* 查出所有符合自动投标条件的标的 */
		List<t_bid> bidList =  bidService.queryAllBidList();
		if(null == bidList || bidList.size() < 1) {
			return;
		}
		
		/* 查出所有开启自动投标的用户 */
		List<Object> userIds = investService.queryAllAutoUser();
		if (null == userIds || userIds.size() < 1) {
			return;
		}
		
		ResultInfo result = new ResultInfo();
		
		int unit = 0;//标产品期限单位    1：天，2：月
		long bidUserId = -1;
		long bidId= -1;
		boolean over = false;
		long investUserId = -1;
		t_auto_invest_setting userParam = null;
		
		/* 遍历所有的符合条件进度低于95%的招标中的借款标 */
		for(t_bid bid : bidList) {
			bidId = bid.id;
			bidUserId = bid.user_id;
					
			if(bidId < 1 || bidUserId < 1) {
				continue ;
			}
			
			/* 进度是否低于95% */
			over = investService.judgeSchedule(bidId);
			if(!over){
				continue ;
			}
			
			/* 关闭自动事务 */
			JPAPlugin.closeTx(false);
			/* 遍历所有设置了投标机器人用户ID */
			for(Object userId : userIds) {
				try {
					/* 开启自动投标事务 */
					JPAPlugin.startTx(false);
					investUserId = Convert.strToLong(userId + "",0);
					
					/* 如果该借款是发布者的标,则发布者不能投标,用户自动排队到后面  */
					if(bidUserId == investUserId) {
						/* 将该用户排到队尾 */
						investService.updateUserAutoBidTime(investUserId);
						
						continue ;
					}
					
					/* 获取用户设置的投标机器人参数 */
					userParam = investService.findAutoInvestByUserId(investUserId);
					if(null == userParam) {
						continue ;
					}
					
					/* 该用户已经投过该标的 */
					over = investService.hasAutoInvestTheBid(investUserId, bidId);
					if(over) {
						/* 将该用户排到队尾 */
						investService.updateUserAutoBidTime(investUserId);
						
						continue ;
					}
					
					unit = bid.getPeriod_unit().code;
					/* 标的是否符合用户设置的条件 */
					over = investService.judgeBidByParam(userParam, unit, bidId);
					if(!over) {
						/* 将该用户排到队尾 */
						investService.updateUserAutoBidTime(investUserId);
						
						continue ;
					}		
					
					/* 每次计算投标金额之前重新查询标的信息 */
					t_bid currBid = bidService.findByID(bidId);
					if (currBid == null) {
						continue;
					}
					
					double amount = currBid.amount;  //标的借款总额
					double hasInvestedAmt = currBid.has_invested_amount;  //标的已投金额
					double loanSchedule = currBid.loan_schedule;  //标的进度
					double setAmt = userParam.amount;  //用户设置的每次投标金额
					/* 进度是否低于95% */
					if(loanSchedule >= 95){
						continue;
					}
					int investAmt = investService.calculateBidAmount(setAmt, loanSchedule, amount, hasInvestedAmt);  //计算出投标金额
					
					int buyType = bid.getBuy_type().code;  //购买方式1-按金额,2-按份数
					double minInvestAmt = bid.min_invest_amount;  //按份数购买：每份的金额；按金额购买：起购金额
					if(buyType == 2){
						int investCopies = investService.calculateFinalInvestAmount(investAmt, minInvestAmt);
						investAmt = (int) minInvestAmt * investCopies;
					}
					
					/* 投标准备 */
					result = investService.invest(investUserId, currBid, investAmt, 0,null);
					if (result.code < 1) {
						LoggerUtil.info(true, result.msg);
						continue;
					}

					String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.AUTO_INVEST);//业务订单号
					
					if (ConfConst.IS_TRUST) { 
						result = PaymentProxy.getInstance().autoInvest(Client.PC.code, InvestType.AUTO.code, serviceOrderNo, 
								investUserId, currBid, investAmt);
						if (result.code < 1 && result.code != ResultInfo.ALREADY_RUN) {
							LoggerUtil.info(true, result.msg);
							continue;
						}
					}else{
						result = investService.doInvest(investUserId, bidId, investAmt, serviceOrderNo, "", Client.PC.code, 
								InvestType.AUTO.code, 0L, 0.0, 0L, 0.0, 0L, 0.0);
						if (result.code < 1 && result.code != ResultInfo.ALREADY_RUN) {
							LoggerUtil.info(true, result.msg);
							continue;
						}
					}

					/* 添加自动投标记录 */
					investService.addAutoBidRecord(investUserId, bidId);
					/* 将该用户排到队尾 */
					investService.updateUserAutoBidTime(investUserId);
					
				} catch (Exception e) {
					LoggerUtil.error(true,"自动投标失败：" + e.getMessage());
					continue ;
				} finally {
					/* 关闭自动投标事务 */
					JPAPlugin.closeTx(false);
					LoggerUtil.error(false,"自动投标事务正常关闭，userId = %s, bidId = %s ", userId, bidId);
				}
			}
			/* 开启自动投标事务 */
			JPAPlugin.startTx(false);
		}
		
		Logger.info("-----------执行结束：自动投标----------");
	}
}
