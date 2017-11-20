package daos.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daos.base.BaseDao;
import models.core.entity.t_invest_log;
/**
 *  投资记录
 * @Title investLogDao 
 * @Description  记录投资记录和发放奖励状态
 * @author hjs_djk
 * @createDate 2017年10月25日 下午2:34:52
 */
public class InvestLogDao extends BaseDao<t_invest_log>{
	protected InvestLogDao() {
	}
	
	
	/**
	 * 查询未发放投资记录
	 * 
	 * @return
	 */
	public List<t_invest_log> queryUnSendInvestLog() {
		String querySQL = "select * from t_invest_log where status = :status and delivery_time is null limit 1000";
		Map<String,Object> args = new HashMap<String,Object>();
		args.put("status", t_invest_log.SendStatus.UNSEND.code);
		return findListBySQL(querySQL, args);
	}
	
	/**
	 * 查询未发放投资记录  (指定时间段的)
	 * 
	 * @return
	 */
	public List<t_invest_log> queryUnSendInvestLog(String start_time,String end_time) {
		String querySQL = "select * from t_invest_log where status = :status and delivery_time is null and time>=:start_time and time<=:end_time limit 1000";
		Map<String,Object> args = new HashMap<String,Object>();
		args.put("status", t_invest_log.SendStatus.UNSEND.code);
		args.put("start_time", start_time);
		args.put("end_time", end_time);
		return findListBySQL(querySQL, args);
	}

}
