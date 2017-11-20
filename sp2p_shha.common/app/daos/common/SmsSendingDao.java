package daos.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daos.base.BaseDao;
import models.common.entity.t_sms_sending;


/**
 * 系统发送SMS临时表的dao的具体实现
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月19日
 */
public class SmsSendingDao extends BaseDao<t_sms_sending> {

	private static final int TRY_TIMES_MAX = 4;
	
	protected SmsSendingDao() {
	}
	
	/**
	 * 查询没有最近没有发送的几条(尝试次数少于4次)
	 *
	 * @param num
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月1日
	 */
	public List<t_sms_sending> queryLastUnsended(int num){
		String querySQL = "select *  from t_sms_sending where is_send=:is_send and try_times < "+TRY_TIMES_MAX+" limit :num";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("is_send", false);
		condition.put("num", num);
		
		return findListBySQL(querySQL, condition);
	}
	
	/**
	 * 删除所有已经发送的记录
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月23日
	 */
	public int deleteSended(){
		String excuSQL = "delete from t_sms_sending where is_send=:is_send or try_times= "+TRY_TIMES_MAX;
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("is_send", true);
		
		return deleteBySQL(excuSQL, condition);
	}
}
