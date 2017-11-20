package jobs;

import java.util.List;

import common.constants.ConfConst;
import common.constants.ModuleConst;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.ResultInfo;
import models.ext.experience.entity.t_experience_bid;
import play.Logger;
import play.db.jpa.JPAPlugin;
import play.jobs.Job;
import play.jobs.On;
import service.ext.experiencebid.ExperienceBidService;

/**
 * 每天检查一次体验标还款，执行时间22：00
 *
 * @description 
 *
 * @author yaoyi
 * @createDate 2016年2月19日
 */
@On("0 0 22 * * ?")
public class ExperienceBidAutoRepayment extends Job{
	
	@Override
	public void doJob() throws Exception {
		if(!ConfConst.IS_START_JOBS){
			return;
		}
		
		if(ModuleConst.EXT_EXPERIENCEBID){
			Logger.info("--------------------体验标自动还款,开始--------------------");
			
			ResultInfo result = new ResultInfo();
			
			ExperienceBidService experienceBidService = Factory.getService(ExperienceBidService.class);
			
			//检查还款时间是今天的
			List<t_experience_bid> experienceBids = experienceBidService.queryExperienceBidForRepayment();
			if(experienceBids == null || experienceBids.size()==0){
				Logger.info("今天没有到期还款的体验标");
				Logger.info("--------------------体验标自动还款,结束--------------------");
				return;
			}
			
			JPAPlugin.closeTx(false);
			for(t_experience_bid bid : experienceBids){
				
				try{
					
					JPAPlugin.startTx(false);
					result = experienceBidService.experienceBidAutoRepayment(bid);
					if(result.code < 1){
						LoggerUtil.info(true, "体验标id：%s,自动还款失败,原因：", bid.id, result.msg);
					}else{
						LoggerUtil.info(false, "体验标id：%s,自动还款成功", bid.id);
					}
					
				}catch(Exception e){
					LoggerUtil.info(true, "体验标id：%s,自动还款失败,原因：", bid.id, e.getMessage());
				}finally{
					JPAPlugin.closeTx(false);
				}
				
			}
			
			JPAPlugin.startTx(false);
			
			Logger.info("--------------------体验标自动还款,结束--------------------");
		}
	}
	
}
