package daos.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daos.base.BaseDao;
import models.core.entity.t_statistic_index_echart_data;

public class StatisticIndexEchartDataDao extends BaseDao<t_statistic_index_echart_data>{

	public StatisticIndexEchartDataDao() {
	}
	
	/** 
	 * 查询新增理财会员
	 * @param type
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月22日
	 *
	 */
	public List<Object> findFinanciaCount(int type) {
		String sql = "SELECT new_financial_members_count FROM t_statistic_index_echart_data WHERE type =:type ORDER BY id";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("type", type);
		
		return findListSingleBySQL(sql, condition);
	}

	/**
	 * 查询新增注册会员
	 * @param type
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月22日
	 *
	 */
	public List<Object> findRegisterCount(int type) {
		String sql = "SELECT new_register_members_count FROM t_statistic_index_echart_data WHERE type =:type ORDER BY id";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("type", type);
		
		return findListSingleBySQL(sql, condition);
	}

	/**
	 * 查询投资金额
	 * @param type
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月22日
	 *
	 */
	public List findInvestMoney(int type) {
		String sql = "SELECT invest_money FROM t_statistic_index_echart_data WHERE type =:type ORDER BY id";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("type", type);
		
		return findListSingleBySQL(sql, condition);
	}

	/**
	 * 查询充值金额
	 * @param type
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月22日
	 *
	 */
	public List findRechargeMoney(int type) {
		String sql = "SELECT recharge_money FROM t_statistic_index_echart_data WHERE type =:type ORDER BY id";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("type", type);
		
		return findListSingleBySQL(sql, condition);
	}

	
}
