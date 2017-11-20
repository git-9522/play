package jobs;

import java.util.List;

import models.core.entity.t_bid;
import payment.impl.PaymentProxy;
import play.Logger;
import play.db.jpa.JPAPlugin;
import play.jobs.Every;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import services.core.BidService;
import common.constants.ConfConst;
import common.enums.Client;
import common.enums.ServiceType;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.OrderNoUtil;
import common.utils.ResultInfo;

/**
 * 每29分钟检查一次是否流标
 *
 * @description 
 *
 * @author yaoyi
 * @createDate 2016年1月23日
 */
@Every("29min")
public class CheckBidIsFlow extends Job {

	@Override
	public void doJob() throws Exception {
		if(!ConfConst.IS_START_JOBS){
			return;
		}
		
	    BidService bidService = Factory.getService(BidService.class);
		
		/** 不支持自动流标的接口，请注释掉这个定时任务 */
		
		Logger.info("--------------检查是否流标,开始---------------------");
		
		List<t_bid> bids = bidService.queryBidIsFlow();
		if(bids == null || bids.size() == 0){
			
			return;
		}
		JPAPlugin.closeTx(false);
		
		for (t_bid bid : bids) {
			try{
				JPAPlugin.startTx(false);

				ResultInfo result = new ResultInfo();

				//业务订单号
				String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.BID_FLOW);
				//托管，流标
				if(ConfConst.IS_TRUST){
					result = PaymentProxy.getInstance().bidFailed(Client.PC.code, serviceOrderNo, bid, ServiceType.BID_FLOW);
					if(result.code < 1 && result.code!=ResultInfo.ALREADY_RUN){
						
						LoggerUtil.error(true, "自动流标失败编号(借款中->流标)：%s，原因：%s", bid.id, result.msg);
						return;
					}
					
					continue;
				}
				
				if (!ConfConst.IS_TRUST){
					
					result = bidService.doBidFlow(serviceOrderNo, bid);
					
					if(result.code < 1){
						
						LoggerUtil.error(true, "自动流标失败编号(借款中->流标)：%s，原因：%s", bid.id, result.msg);
					}
				}
				
			}catch(Exception e){
				
				LoggerUtil.error(true, "自动流标失败编号(借款中->流标)：%s，原因：%s", bid.id, e.getMessage());
			}finally{
				
				JPAPlugin.closeTx(false);
				LoggerUtil.error(false, "自动流标事务正常关闭，id = %s ", bid.id);
			}
		}
		
		JPAPlugin.startTx(false);
		
		Logger.info("--------------检查是否流标,结束---------------------");
	}
	
}
