package jobs;

import java.util.List;
import java.util.Map;

import models.common.entity.t_user_fund;
import models.core.entity.t_auto_invest_setting;
import models.core.entity.t_bid;
import models.core.entity.t_invest.InvestType;

import com.shove.Convert;
import com.shove.security.Encrypt;

import common.constants.ConfConst;
import common.constants.SettingKey;
import common.enums.Client;
import common.enums.ServiceType;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.OrderNoUtil;
import common.utils.ResultInfo;
import daos.common.UserFundDao;
import payment.impl.PaymentProxy;
import play.Logger;
import play.db.jpa.JPAPlugin;
import play.jobs.Every;
import play.jobs.Job;
import services.common.SettingService;
import services.common.UserFundService;
import services.core.BidService;
import services.core.InvestService;

/** 
 * 临时批量更新签名
 * 
 */
//@Every("5min")
public class userFundSignUpdata extends Job {
	
	@Override
	public void doJob() throws Exception{
		/** 注入系统设置service */
		/*UserFundService userFundService = Factory.getService(UserFundService.class);*/
		UserFundDao userFundDao = Factory.getDao(UserFundDao.class);
		ResultInfo result = new ResultInfo();
		Logger.info("-----------批量更新签名 start----------");
		
		List<t_user_fund> users = userFundDao.findAll();
		
		if(users == null || users.size() == 0){
			return;
		}
		long starTime=System.currentTimeMillis();
		for(t_user_fund userFund: users){
			
			String userFundsign = Encrypt.MD5(userFund.id + userFund.balance + userFund.freeze + userFund.visual_balance + ConfConst.ENCRYPTION_KEY_MD5);
			
			int row = userFundDao.updateUserFundSign(userFund.user_id, userFundsign);
			
			if (row <= 0) {
				result.code = -2;
				LoggerUtil.info(false, "更新用户资产签名字段失败");
				
			}
		}
		long endTime=System.currentTimeMillis();
		long Time=endTime-starTime;
		Logger.info("-----------批量更新签名 end--(执行时间："+Time+"ms)--------");
	}
}
