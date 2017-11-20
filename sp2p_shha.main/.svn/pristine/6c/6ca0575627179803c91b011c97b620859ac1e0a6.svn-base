package jobs;

import java.util.List;
import java.util.Map;

import com.shove.Convert;

import common.constants.ConfConst;
import common.enums.Client;
import common.enums.ServiceType;
import common.utils.Factory;
import common.utils.JPAUtil;
import common.utils.LoggerUtil;
import common.utils.OrderNoUtil;
import common.utils.ResultInfo;
import hf.HfConsts;
import models.core.entity.t_bill_invest;
import payment.impl.PaymentProxy;
import play.Logger;
import play.db.jpa.JPAPlugin;
import play.jobs.Every;
import play.jobs.Job;
import services.core.BillInvestService;

/**
 * 发放加息金额：标的奖励加息，使用加息卷
 * @author jiayijian
 */
@Every("3min")
public class SendRate extends Job {
	
	@Override
	public void doJob() throws Exception {
		BillInvestService billInvestService = Factory.getService(BillInvestService.class);
		
		List<t_bill_invest> list = billInvestService.queryNoRateInvestBills();
		
		if (list == null || list.size() <= 0) {
			
			return ;
		}
		
		Logger.info("-----------执行开始：发放加息金额----------");
		
		double totalAmt = 0;
		for (t_bill_invest invest : list) {
			totalAmt = totalAmt +invest.add_invest + invest.reward_invest;
		}
		
		if (ConfConst.IS_TRUST) {
			
			/*String serviceOrderNo =*/ OrderNoUtil.getNormalOrderNo(ServiceType.CONVERSION);
			
			//商户可用余额查询，
			ResultInfo result = PaymentProxy.getInstance().queryMerchantBalance(Client.PC.code);
			if (result.code < 1) {
				
				LoggerUtil.info(false, "查询商户可用余额异常");
				return ;
			}
			
			double merBalance = 0;
			
			List<Map<String, String>> acctDetails = (List<Map<String, String>>) result.obj;
			for (Map<String, String> acctDetail : acctDetails) {
				if (HfConsts.SERVFEEACCTID.equals(acctDetail.get("SubAcctId"))) {
					merBalance = Convert.strToDouble(acctDetail.get("AcctBal").replace(",", ""), 0);
				}
			}
			
			if (totalAmt > merBalance) {
				
				LoggerUtil.info(false, "执行：发放加息金额时，商户余额不足，请充值");
				return ;
			}
		}
		
		for (t_bill_invest invest : list) {
			try {
				/* 关闭自动事务 */
				JPAPlugin.closeTx(false);
				
				if (t_bill_invest.Status.RECEIVED.code != invest.getStatus().code ) {
					
					continue ;
				}
				
				if (t_bill_invest.Ratestatus.NO_SEND.code != invest.getRate_status().code) {
					
					continue ;
				}
				
				if((invest.add_invest + invest.reward_invest) <= 0){
					continue ;
				}
				
				if (0.00 != invest.real_add_invest) {
						
					continue ;
				}
				
				JPAPlugin.startTx(false);
				
				JPAUtil.transactionBegin();			
				/** 请求接口先更新加息券发放状态，防止重复发送 */
				int row = billInvestService.updateBillRateStatus(invest);
				
				if (row <= 0) {
					
					continue ;
				}
				
				JPAUtil.transactionCommit();
				String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.TRANSFER);
				PaymentProxy.getInstance().sendRate(Client.PC.code, serviceOrderNo, invest);
			} catch (Exception e) {
				LoggerUtil.error(true, "发放加息金额失败：" + e.getMessage());
				
				continue ;
			} finally {
				JPAPlugin.closeTx(false);
				LoggerUtil.error(false, "发放加息金额事务正常关闭，userId = %s, bidId = %s ", invest.user_id, invest.bid_id);
			}
			
			/* 开启自动事务 */
			JPAPlugin.startTx(false);
		}
		
		Logger.info("-----------执行结束：发放加息金额----------");
	}
	
}