package daos.common;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import daos.base.BaseDao;
import models.common.bean.AmountTotal;
import models.common.entity.t_user_info;

public class AmountTotalDao extends BaseDao<t_user_info>{

	public AmountTotalDao(){}
	
	public AmountTotal amountTotal(int showType,String userName){
		StringBuffer querySQL=null;
		if(showType==1){
			querySQL=new StringBuffer("SELECT r.user_id as id,sum(r.amount) as amount_sum,sum(r.correct_interest) as correct_interest_sum,sum(r.red_packet_amount) as red_packet_amount_sum from (SELECT w.*,w1.spreader_id from (SELECT t.user_id,u.name,u.reality_name,u.id_number,t.amount,t.correct_interest,t.red_packet_amount,t.time,u.mobile from t_invest t,t_user_info u where u.user_id  not in (SELECT user_id from t_bid) and t.user_id=u.user_id and t.time <= (select case when (select tt.time from t_invest tt where tt.user_id=t.user_id   ORDER BY tt.time asc limit 1) is Null then CURRENT_TIMESTAMP() else  (select tt.time from t_invest tt where tt.user_id=t.user_id   ORDER BY tt.time asc limit 1) end as add_time)  ORDER BY t.time desc ) as w LEFT JOIN t_cps_user w1 on w.user_id= w1.user_id ) as r LEFT JOIN t_user_info r1 on r.spreader_id=r1.user_id where 1=1 ");
		}else if(showType==2){
			querySQL=new StringBuffer("SELECT r.user_id as id ,sum(r.amount) as amount_sum,sum(r.correct_interest) as correct_interest_sum,sum(r.red_packet_amount) as red_packet_amount_sum from (SELECT w.*,w1.spreader_id from (SELECT t.user_id,u.name,u.reality_name,u.id_number,t.amount,t.correct_interest,t.red_packet_amount,t.time,u.mobile from t_invest t,t_user_info u where u.user_id  not in (SELECT user_id from t_bid) and t.user_id=u.user_id and t.time > (select case when (select tt.time from t_invest tt where tt.user_id=t.user_id   ORDER BY tt.time asc limit 1) is Null then CURRENT_TIMESTAMP() else  (select tt.time from t_invest tt where tt.user_id=t.user_id   ORDER BY tt.time asc limit 1) end as add_time)  ORDER BY t.time desc ) as w LEFT JOIN t_cps_user w1 on w.user_id= w1.user_id ) as r LEFT JOIN t_user_info r1 on r.spreader_id=r1.user_id where 1=1 ");
		}else{
			 querySQL = new StringBuffer("SELECT r.user_id as id ,sum(r.amount) as amount_sum,sum(r.correct_interest) as correct_interest_sum,sum(r.red_packet_amount) as red_packet_amount_sum from (SELECT w.*,w1.spreader_id from (SELECT t.user_id,u.name,u.reality_name,u.id_number,t.amount,t.correct_interest,t.red_packet_amount,t.time,u.mobile from t_invest t,t_user_info u where u.user_id  not in (SELECT user_id from t_bid) and t.user_id=u.user_id   ORDER BY t.time desc ) as w LEFT JOIN t_cps_user w1 on w.user_id= w1.user_id ) as r LEFT JOIN t_user_info r1 on r.spreader_id=r1.user_id where 1=1 ");
		}
			Map<String,Object> filter=new HashMap<String,Object>();
			if(StringUtils.isNotBlank(userName)){
				querySQL.append(" AND r.name like :userName ");
				filter.put("userName","%"+userName+"%");
			}
		return this.findBeanBySQL(querySQL.toString(), AmountTotal.class, filter);
	}
}
