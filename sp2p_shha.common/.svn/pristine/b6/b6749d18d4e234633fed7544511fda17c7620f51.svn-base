package daos.common;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import common.constants.Constants;
import common.utils.DateUtil;
import common.utils.PageBean;
import daos.base.BaseDao;
import models.common.bean.InvestLottery;
import models.common.entity.t_invest_lottery;

public class InvestLotteryDao extends BaseDao<t_invest_lottery> {

	public PageBean<InvestLottery> pageOfBackRewards(int currPage, int pageSize, String id, String name,
			String mobile, int exports) {
		StringBuffer querySQL = new StringBuffer("SELECT l.id, l.time, l.user_id, l.reward_id, u.mobile, l.name, l.value, l.status, l.delivery_time  FROM t_invest_lottery l LEFT JOIN t_user u ON l.user_id = u.id WHERE 1=1 ");
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(1) from t_invest_lottery l LEFT JOIN t_user u ON l.user_id = u.id WHERE 1=1 ");
		
		Map<String, Object> args = new HashMap<String, Object>();
		
		/** 按编号搜索  */
		if(StringUtils.isNotBlank(id)){
			querySQL.append(" AND l.id = :id");
			countSQL.append(" AND l.id = :id");
			args.put("id", id.trim());
		}
		
		/** 按奖品名称搜索  */
		if(StringUtils.isNotBlank(name)){
			querySQL.append(" AND l.name LIKE :name");
			countSQL.append(" AND l.name LIKE :name");
			args.put("name", "%" + name.trim() + "%");
		}
		
		/** 按手机名称搜索  */
		if(StringUtils.isNotBlank(mobile)){
			querySQL.append(" AND u.mobile = :mobile");
			countSQL.append(" AND u.mobile = :mobile");
			args.put("mobile", mobile);
		}
		
		querySQL.append(" order by id desc ");
		
		if(exports == Constants.EXPORT){
			PageBean<InvestLottery> pageBean = new PageBean<InvestLottery>();
			pageBean.page = this.findListBeanBySQL(querySQL.toString(), InvestLottery.class, args);
		    
		    return pageBean;
		}
		return this.pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), InvestLottery.class, args);
	}

	public int calulateInvestLotteryInDates(long userId, Date startDate, Date endDate) {
		String start = DateUtil.dateToString(startDate, "yyyy-MM-dd");
		String end = DateUtil.dateToString(endDate, "yyyy-MM-dd");
		String sql = "SELECT COUNT(1) from t_invest_lottery WHERE user_id = :userId AND DATE_FORMAT(time, '%Y-%m-%d') >= :start AND DATE_FORMAT(time, '%Y-%m-%d') <= :end";
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("userId", userId);
		args.put("start", start);
		args.put("end", end);
		Object object = this.findSingleBySQL(sql, args);
		if(object == null){
			return 0;
		}
		return new Integer(object.toString());
	}

	public List<InvestLottery> queryLatestLotteries(int size) {
		String sql = "SELECT l.id, l.time, l.user_id, l.reward_id, u.mobile, l.name, l.value, l.status, l.delivery_time  FROM t_invest_lottery l LEFT JOIN t_user u ON l.user_id = u.id ORDER BY l.id DESC LIMIT 0, " + size;
		return findListBeanBySQL(sql, InvestLottery.class, null);
	}

	public boolean updateStatus(t_invest_lottery lottery) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", true);
		map.put("delivery_time", lottery.delivery_time);
		map.put("id", lottery.id);
		int row =  updateBySQL("UPDATE t_invest_lottery SET status = :status, delivery_time = :delivery_time WHERE id = :id", map);
		if(row == 0) {
			return true;
		}
		return false;
 	}
	
}