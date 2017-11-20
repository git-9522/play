package daos.common;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import common.constants.Constants;
import common.utils.PageBean;
import daos.base.BaseDao;
import models.common.entity.t_reversal_record;

public class ReversalRecordDao extends BaseDao<t_reversal_record> {

	public PageBean<t_reversal_record> pageOfBackRecords(int showType, int currPage, int pageSize, String userName,
			String awardMoney, int exports) {
		
		StringBuffer querySQL = new StringBuffer("select * from t_reversal_record where 1=1 ");
		StringBuffer countSQL = new StringBuffer("select count(1) from t_reversal_record where 1=1 ");
		
		Map<String, Object> args = new HashMap<String, Object>();
		
		if(StringUtils.isNotBlank(userName)){
			querySQL.append(" AND user_name = :userName");
			countSQL.append(" AND user_name = :userName");
			args.put("userName", userName.trim());
		}
		
		if(StringUtils.isNotBlank(awardMoney)){
			querySQL.append(" AND award_money = :awardMoney");
			countSQL.append(" AND award_money = :awardMoney");
			args.put("awardMoney", awardMoney.trim());
		}
		
		switch (showType) {
		case 1:
			querySQL.append(" AND status = :status");
			countSQL.append(" AND status = :status");
			args.put("status", false);
			break;
		case 2:
			querySQL.append(" AND status = :status");
			countSQL.append(" AND status = :status");
			args.put("status", true);
			break;
		default:
			break;
		}
				
		querySQL.append(" order by award_money desc, id desc ");
		
		if(exports == Constants.EXPORT){
			PageBean<t_reversal_record> page = new PageBean<t_reversal_record>();
			page.page = findListBeanBySQL(querySQL.toString(), t_reversal_record.class, args);
			return page;
		}
		
		return this.pageOfBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), args);
	}

	public boolean updateStatus(t_reversal_record record) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", true);
		map.put("delivery_time", record.delivery_time);
		map.put("id", record.id);
		int row =  updateBySQL("UPDATE t_reversal_record SET status = :status, delivery_time = :delivery_time WHERE id = :id", map);
		if(row == 0) {
			return true;
		}
		return false;
	}

}