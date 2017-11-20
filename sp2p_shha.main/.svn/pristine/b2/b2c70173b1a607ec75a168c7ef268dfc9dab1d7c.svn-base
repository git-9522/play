package jobs;

import java.util.List;
import java.util.Map;

import models.core.bean.BidInvestRecord;
import models.core.entity.t_bid;
import models.core.entity.t_invest;
import common.constants.ConfConst;
import common.utils.Factory;
import common.utils.JPAUtil;
import common.utils.ResultInfo;
import play.db.jpa.JPA;
import play.jobs.Every;
import play.jobs.Job;
import service.ext.wealthcircle.WealthCircleService;
import services.core.BidService;
import services.core.InvestService;
import services.ext.cps.CpsService;

@Every("5min")
public class RewardGrant extends Job{

	private static BidService bidService = Factory.getService(BidService.class) ;
	private static InvestService investService = Factory.getService(InvestService.class) ;
	private static CpsService cpsService = Factory.getService(CpsService.class) ;
	private static WealthCircleService wealthCircleService = Factory.getService(WealthCircleService.class) ;
	
	@Override
	public void doJob() throws Exception {
		if(!ConfConst.IS_START_JOBS){
			return;
		}
		
		cpsRewardGrant() ;
		wealthcircleRewardGrant() ;
	}
	
	/**
	 * 添加cps 奖励
	 */
	@SuppressWarnings("deprecation")
	public void cpsRewardGrant(){
		
		List<Map<String, Object>> list = bidService.selectRewardBidId(t_bid.RewardGrantType.NOT_REWARD.code) ;
		if(list==null || list.size() == 0){
			return ;
		}
		
		long bidId= 0 ;
		List<t_invest> investList ;
		ResultInfo result = new ResultInfo() ;
		int countBid = 0 ;
		for (Map<String, Object> map : list) {
			bidId = Long.parseLong( map.get("bid_id").toString() ) ;
			investList = investService.queryBidInvest(bidId) ;
			if(investList == null || investList.size() ==0){
				continue ;
			}
			
			for (t_invest invest : investList) {
				result = cpsService.investGiveCpsCommission(invest.user_id, invest.amount);
				
				if(result.code < 0){
					JPA.setRollbackOnly();
					continue ;
				}
			}
			
			countBid = bidService.updateBidRewardGrantType(bidId, t_bid.RewardGrantType.NOT_REWARD.code, t_bid.RewardGrantType.CPS_REWARD.code) ;
			
			if( countBid > 0 ){
				JPAUtil.transactionCommit();
			}else{
				JPA.setRollbackOnly();
			}
		}
	}
	
	/**
	 * 财富圈奖励发放
	 */
	@SuppressWarnings("deprecation")
	public void wealthcircleRewardGrant(){
		
		List<Map<String, Object>> list = bidService.selectRewardBidId(t_bid.RewardGrantType.CPS_REWARD.code) ;
		if(list==null || list.size() == 0){
			return ;
		}
		
		long bidId= 0 ;
		List<t_invest> investList ;
		ResultInfo result = new ResultInfo() ;
		int countBid = 0 ;
		for (Map<String, Object> map : list) {
			bidId = Long.parseLong( map.get("bid_id").toString() ) ;
			investList = investService.queryBidInvest(bidId) ;
			if(investList == null || investList.size() ==0){
				continue ;
			}
			
			for (t_invest invest : investList) {
				result = wealthCircleService.investGiveWcCommission(invest.user_id, invest.amount);
				
				if(result.code < 0){
					JPA.setRollbackOnly();
					continue ;
				}
			}
			
			countBid = bidService.updateBidRewardGrantType(bidId, t_bid.RewardGrantType.CPS_REWARD.code, t_bid.RewardGrantType.WEALTHCIRCLE_REWARD.code) ;
			
			if( countBid > 0 ){
				JPAUtil.transactionCommit();
			}else{
				JPA.setRollbackOnly();
			}
		}
	}
}
