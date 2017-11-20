package daos.common;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import common.constants.Constants;
import common.utils.PageBean;
import daos.base.BaseDao;
import models.common.bean.UserStatistics;
import models.common.entity.t_user_info;

public class UsersStatisticsDao extends BaseDao<t_user_info> {
	
	protected UsersStatisticsDao(){}
	
	public PageBean<UserStatistics> findUserStatistics(int showType, int currPage, int pageSize, int orderType, int orderValue, String userName, int exports){
		StringBuffer countSQL=null;
		StringBuffer querySQL=null;
		if(showType==1){
			 countSQL = new StringBuffer("SELECT count(r.user_id) from (SELECT w.*,w1.spreader_id from (SELECT t.user_id,u.name,u.reality_name,u.id_number,t.amount,t.correct_interest,t.red_packet_amount,t.time,u.mobile from t_invest t,t_user_info u where u.user_id  not in (SELECT user_id from t_bid) and t.user_id=u.user_id and t.time <= (select case when (select tt.time from t_invest tt where tt.user_id=t.user_id   ORDER BY tt.time asc limit 1) is Null then CURRENT_TIMESTAMP() else  (select tt.time from t_invest tt where tt.user_id=t.user_id   ORDER BY tt.time asc limit 1) end as add_time)  ORDER BY t.time desc ) as w LEFT JOIN t_cps_user w1 on w.user_id= w1.user_id ) as r LEFT JOIN t_user_info r1 on r.spreader_id=r1.user_id  where 1=1");
			 querySQL = new StringBuffer("SELECT r.user_id as id,r.name as name,r.reality_name as reality_name,r.id_number as id_number,r.amount as amount,r.correct_interest as correct_interest,r.red_packet_amount as red_packet_amount,r.time as time,r.mobile as mobile,r1.mobile as mobile2 from (SELECT w.*,w1.spreader_id from (SELECT t.user_id,u.name,u.reality_name,u.id_number,t.amount,t.correct_interest,t.red_packet_amount,t.time,u.mobile from t_invest t,t_user_info u where u.user_id  not in (SELECT user_id from t_bid) and t.user_id=u.user_id and t.time <= (select case when (select tt.time from t_invest tt where tt.user_id=t.user_id   ORDER BY tt.time asc limit 1) is Null then CURRENT_TIMESTAMP() else  (select tt.time from t_invest tt where tt.user_id=t.user_id   ORDER BY tt.time asc limit 1) end as add_time)  ORDER BY t.time desc ) as w LEFT JOIN t_cps_user w1 on w.user_id= w1.user_id ) as r LEFT JOIN t_user_info r1 on r.spreader_id=r1.user_id where 1=1");
		}else if(showType==2){
			 countSQL = new StringBuffer("SELECT COUNT(*) from (SELECT w.*,w1.spreader_id from (SELECT t.user_id,u.name,u.reality_name,u.id_number,t.amount,t.correct_interest,t.red_packet_amount,t.time,u.mobile from t_invest t,t_user_info u where u.user_id  not in (SELECT user_id from t_bid) and t.user_id=u.user_id and t.time > (select case when (select tt.time from t_invest tt where tt.user_id=t.user_id   ORDER BY tt.time asc limit 1) is Null then CURRENT_TIMESTAMP() else  (select tt.time from t_invest tt where tt.user_id=t.user_id   ORDER BY tt.time asc limit 1) end as add_time)  ORDER BY t.time desc ) as w LEFT JOIN t_cps_user w1 on w.user_id= w1.user_id ) as r LEFT JOIN t_user_info r1 on r.spreader_id=r1.user_id where 1=1");
			 querySQL = new StringBuffer("SELECT r.user_id as id,r.name as name ,r.reality_name as reality_name,r.id_number as id_number ,r.amount as amount,r.correct_interest as correct_interest,r.red_packet_amount as red_packet_amount,r.time as time,r.mobile as mobile ,r1.mobile as mobile2 from (SELECT w.*,w1.spreader_id from (SELECT t.user_id,u.name,u.reality_name,u.id_number,t.amount,t.correct_interest,t.red_packet_amount,t.time,u.mobile from t_invest t,t_user_info u where u.user_id  not in (SELECT user_id from t_bid) and t.user_id=u.user_id and t.time > (select case when (select tt.time from t_invest tt where tt.user_id=t.user_id   ORDER BY tt.time asc limit 1) is Null then CURRENT_TIMESTAMP() else  (select tt.time from t_invest tt where tt.user_id=t.user_id   ORDER BY tt.time asc limit 1) end as add_time)  ORDER BY t.time desc ) as w LEFT JOIN t_cps_user w1 on w.user_id= w1.user_id ) as r LEFT JOIN t_user_info r1 on r.spreader_id=r1.user_id where 1=1");
		
		}else{
			 countSQL = new StringBuffer("SELECT count(r.user_id) from (SELECT w.*,w1.spreader_id from (SELECT t.user_id,u.name,u.reality_name,u.id_number,t.amount,t.correct_interest,t.red_packet_amount,t.time,u.mobile from t_invest t,t_user_info u where u.user_id  not in (SELECT user_id from t_bid) and t.user_id=u.user_id  ORDER BY t.time desc ) as w LEFT JOIN t_cps_user w1 on w.user_id= w1.user_id ) as r LEFT JOIN t_user_info r1 on r.spreader_id=r1.user_id  where 1=1");
			 querySQL = new StringBuffer("SELECT r.user_id as id,r.name as name,r.reality_name as reality_name,r.id_number as id_number,r.amount as amount,r.correct_interest as correct_interest,r.red_packet_amount as red_packet_amount,r.time as time,r.mobile as mobile,r1.mobile as mobile2 from (SELECT w.*,w1.spreader_id from (SELECT t.user_id,u.name,u.reality_name,u.id_number,t.amount,t.correct_interest,t.red_packet_amount,t.time,u.mobile from t_invest t,t_user_info u where u.user_id  not in (SELECT user_id from t_bid) and t.user_id=u.user_id   ORDER BY t.time desc ) as w LEFT JOIN t_cps_user w1 on w.user_id= w1.user_id ) as r LEFT JOIN t_user_info r1 on r.spreader_id=r1.user_id where 1=1");
		
		}
		
		Map<String,Object> map=new HashMap<String,Object>();
		if(StringUtils.isNotBlank(userName)){
			countSQL.append(" AND r.name like :userName ");
			querySQL.append(" AND r.name like :userName ");
			map.put("userName", "%"+userName+"%");
		}
		
		switch (orderType) {
		case 1:
			querySQL.append(" order by id ");
			if(orderValue==0){
				querySQL.append(" DESC ");	
			}
			break;
		case 2:
			querySQL.append(" order by amount ");
			if(orderValue==0){
				querySQL.append(" DESC ");	
			}
			break;
		case 3:
			querySQL.append(" order by correct_interest ");
			if(orderValue==0){
				querySQL.append(" DESC ");	
			}
			break;
		case 4:
			querySQL.append(" order by red_packet_amount ");
			if(orderValue==0){
				querySQL.append(" DESC ");	
			}
			break;
		case 5:
			querySQL.append(" order by time ");
			if(orderValue==0){
				querySQL.append(" DESC ");	
			}
			break;
		default:
			querySQL.append(" order by time desc ");
			break;
		}
		if(exports == Constants.EXPORT){
			PageBean<UserStatistics> page = new PageBean<UserStatistics>();
			page.page = findListBeanBySQL(querySQL.toString(), UserStatistics.class, map);
			return page;
		}
		
		return this.pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), UserStatistics.class, map);
	}
	

}
