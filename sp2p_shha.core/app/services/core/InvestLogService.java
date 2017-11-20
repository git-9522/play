package services.core;

import java.util.List;

import common.utils.Factory;
import common.utils.ResultInfo;
import daos.core.InvestLogDao;
import models.core.entity.t_invest_log;
import services.base.BaseService;

/**
 * 投资记录查询
 * 
 * @Title InvestLogService
 * @Description （流标 撤标 转让不记录）
 * @author hjs_djk
 * @createDate 2017年10月25日 下午2:40:34
 */
public class InvestLogService extends BaseService<t_invest_log> {
	protected InvestLogDao investLogDao = null;

	protected InvestLogService() {
		investLogDao = Factory.getDao(InvestLogDao.class);
		super.basedao = this.investLogDao;
	}

	@SuppressWarnings("unchecked")
	public ResultInfo saveLog(t_invest_log log) {
		ResultInfo result = new ResultInfo();
		boolean isSave = basedao.save(log);
		result.code = isSave ? 1 : -1;
		result.msg = isSave ? "保存记录成功！" : "保存记录失败!";
		result.obj = isSave ? log : null;
		return result;
	}
	
	/**
	 * 查询未发放投资记录
	 * 
	 * @return
	 */
	public List<t_invest_log> queryUnSendInvestLog() {
		return investLogDao.queryUnSendInvestLog();
	}
	
	/**
	 * 查询未发放投资记录(指定时间段)
	 * 
	 * @return
	 */
	public List<t_invest_log> queryUnSendInvestLog(String start_time,String end_time) {
		return investLogDao.queryUnSendInvestLog(start_time,end_time);
	}
}
