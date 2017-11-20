package jobs;

import java.util.List;
import java.util.Map;

import com.shove.Convert;

import models.core.entity.t_red_packet_transfer;
import models.core.entity.t_red_packet_user;
import common.enums.Client;
import common.utils.Factory;
import common.utils.JPAUtil;
import common.utils.LoggerUtil;
import common.utils.ResultInfo;
import payment.impl.PaymentProxy;
import play.Logger;
import play.db.jpa.JPA;
import play.db.jpa.JPAPlugin;
import play.jobs.Every;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import services.core.RedPacketTransferService;
import services.core.RedpacketUserService;

/**
 * 检查红包是否转账失败，如果是失败，则将状态改为5，然后执行该定时任务，
 * 
 * @author yanpengfei
 *
 */
//@Every("5min")
//@OnApplicationStart
public class CheckRedPacketTransferFail extends Job {
	
	protected RedpacketUserService redpacketUserService = Factory.getService(RedpacketUserService.class);
	protected static RedPacketTransferService redPacketTransferService = Factory.getService(RedPacketTransferService.class);
	
	public void doJob() throws Exception {
		
		ResultInfo result = new ResultInfo();
		
		Logger.info("--------------检查红包是否转账失败,开始---------------------");
		
		List<t_red_packet_transfer> transferList = redPacketTransferService.findListByColumn(" status = 5 ");//5为临时标记状态
		
		if (transferList == null || transferList.size() <= 0) {
			Logger.info("--------------暂无转账失败红包---------------------");
			Logger.info("--------------检查红包是否转账失败,结束---------------------");
			
			return ;
		}
		
		double totalAmt = 0;
		for(t_red_packet_transfer transA : transferList){
			totalAmt += transA.amount;
		}
		
		// 托管 查询商户可用余额
		result = PaymentProxy.getInstance().queryMerchantBalance(Client.PC.code);
		// String showContent = "";
		
		if (result.code < 1) {
			
			Logger.info("--------------查询担保账户可用余额异常---------------------");
			return;
		}
		
		if(result.obj != null){
			
			Map<String, Object> merDetail = (Map<String, Object>) result.obj;
			double merBalance = Convert.strToDouble(merDetail.get("balance").toString(), 0);
			
			if(totalAmt > merBalance){
				Logger.info("--------------出账用户余额不足---------------------");
				return;
			}
		}
		int num  = 0;
		for(t_red_packet_transfer trans : transferList){
			
			/* 开启自动事务 */
			JPAUtil.transactionBegin();
			//5-为临时标记状态，6-处理中
			int row = redPacketTransferService.updateTransferRecord(trans.id, 6, 5);
			
			JPAUtil.transactionCommit();
			
			if (row <= 0) {
				continue;
			}
			
			result = PaymentProxy.getInstance().merchantTransfer(Client.PC.code, "", trans.user_id, trans,6);
			
			if (result.code < 1 && result.code != ResultInfo.ALREADY_RUN) {
				continue;
			}
			
			num += 1;
		}
		
		if (num <= 0) {
			Logger.info("--------------重新发放红包失败---------------------");
		} else {
			Logger.info("--------------处理转账失败的红包数量为：" + transferList.size() + "---处理成功个数："+ num +"------------------");
		}
		
		Logger.info("--------------检查红包是否转账失败,结束---------------------");
	}
	
}
