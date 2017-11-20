package services.common;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import daos.common.ReversalRecordDao;
import models.common.entity.t_reversal_record;
import services.base.BaseService;

public class ReversalRecordService extends BaseService<t_reversal_record> {

	protected static ReversalRecordDao reversalRecordDao = Factory.getDao(ReversalRecordDao.class);
	
	protected ReversalRecordService(){
		super.basedao = reversalRecordDao;
	}

	public PageBean<t_reversal_record> pageOfBackRecords(int showType, int currPage, int pageSize, String userName,
			String awardMoney, int exports) {
		return reversalRecordDao.pageOfBackRecords(showType, currPage, pageSize, userName, awardMoney, exports);
	}

	public List<Map<String, Object>> findInvestInfo(long userId, Date startDate, Date endDate, double reversalInvest) {
		String sql = "SELECT i.id AS investId, DATE_FORMAT(i.time,'%Y-%m-%d %T') AS time, i.amount AS investAmount, b.period AS period, IF(rr.id IS NULL, TRUE, FALSE) AS reversalStatus FROM t_invest i LEFT JOIN t_bid b ON i.bid_id = b.id LEFT JOIN t_reversal_record rr ON i.id = rr.invest_id WHERE i.user_id = :userId AND i.time >=  :startDate AND i.time < :endDate AND i.amount > :reversalInvest AND i.time > (SELECT IF(MIN(TIME) IS NULL, NOW(), MIN(TIME)) FROM t_invest WHERE user_id = " + userId + ") ORDER BY reversalStatus DESC, time;";
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("userId", userId);
		args.put("startDate", startDate);
		args.put("endDate", endDate);
		args.put("reversalInvest", reversalInvest);
		return reversalRecordDao.findListMapBySQL(sql, args);
	}

	public t_reversal_record findByInvestId(long investId) {
		return reversalRecordDao.findByColumn(" invest_id = ? ", investId);
	}

	public ResultInfo insert(t_reversal_record record) {
		ResultInfo result = new ResultInfo();
		boolean flag = reversalRecordDao.save(record);
		if(flag) {
			result.code = 1;
		} else {
			result.code = -1;
		}
		return result;
	}

	public List<t_reversal_record> queryAllUnDelivery() {
		return findListByColumn("status = ?", false);
	}

	public boolean update(t_reversal_record record) {
		return reversalRecordDao.updateStatus(record);
	}
	
}