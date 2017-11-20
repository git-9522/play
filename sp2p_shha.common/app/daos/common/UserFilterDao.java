package daos.common;

import java.util.HashMap;
import java.util.Map;

import daos.base.BaseDao;
import models.common.entity.t_offline_user_filter;

public class UserFilterDao extends BaseDao<t_offline_user_filter> {

	public UserFilterDao(){	}
	
	/**
	 * 添加推广人
	 * */
	public boolean addOfflineUserFilter(String spreaderMobile ,int period1,int period3,int period6){
		t_offline_user_filter offline_user_filter=new t_offline_user_filter();
		offline_user_filter.period1=period1;
		offline_user_filter.period3=period3;
		offline_user_filter.period6=period6;
		offline_user_filter.spreader_mobile=spreaderMobile;
		return save(offline_user_filter);
	}
	/**
	 * 通过id修改推广人
	 * @param spreaderMobile
	 * @param id
	 * @return
	 */
	public int updateOfflineUserFilter(String spreaderMobile ,int period1,int period3,int period6,Long id){
	
		String sql=" UPDATE t_offline_user_filter tof SET tof.spreader_mobile=:spreaderMobile ,tof.period1=:period1,tof.period3=:period3,tof.period6=:period6 where  id=:id ";
		Map<String, Object> filter=new HashMap<String, Object>();
		filter.put("spreaderMobile", spreaderMobile);
		filter.put("period1", period1);
		filter.put("period3", period3);
		filter.put("period6", period6);
		filter.put("id", id);
		return this.updateBySQL(sql, filter);
	}
	/**通过id删除推广人*/
	public int delOfflineUserFilter(Long id){
		return delete(id);
	}
	/**通过id查询推广人*/
	public t_offline_user_filter findById(Long id){
		String querySQL=" SELECT * FROM t_offline_user_filter WHERE  id=:id ";
		Map<String, Object> filter= new HashMap<String,Object>();
		filter.put("id", id);
		return findBySQL(querySQL, filter);
	}
	/**
	 * 是否是投资指定标的线下用户（不享受指定线上活动）
	 * @param userId
	 * @return true：是   false：否
	 *         
	 */
	public boolean isOfflineUser(Long userId ,int period ){
		String querySQL=" SELECT COUNT(1) FROM t_user u WHERE id= (select tc.spreader_id FROM t_cps_user tc WHERE  tc.user_id = :user_id ) AND  u.mobile in(SELECT spreader_mobile FROM t_offline_user_filter WHERE period1 = :period OR period3=:period OR period6=:period) ";
		Map<String , Object> filter= new HashMap<String,Object>();
		filter.put("user_id", userId);
		filter.put("period", period);
		Object obj=findSingleBySQL(querySQL, filter);
		return new Integer(obj.toString())>0;
		
	}
	/**通过手机号判断是不是推广人*/
	public boolean isSpreader(String spreaderMobile){
		String sql="SELECT COUNT(id) FROM t_user t WHERE t.mobile=:spreaderMobile AND is_spread=1;";
		Map<String , Object> filter= new HashMap<String,Object>();
		filter.put("spreaderMobile", spreaderMobile);
		return findSingleIntBySQL(sql,0,filter)>0;
	
	}
	/**通过手机号判断推广人是否存在*/
	public boolean isAlreadyExist(String spreaderMobile){
		String querySQL=" SELECT count(1) FROM t_offline_user_filter WHERE  spreader_mobile=:spreaderMobile ";
		Map<String, Object> filter= new HashMap<String,Object>();
		filter.put("spreaderMobile", spreaderMobile);
		int findSingleIntBySQL = findSingleIntBySQL(querySQL,0,filter);
		return findSingleIntBySQL>0;
	
	}
}
