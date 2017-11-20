package common.utils;

import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;

import play.db.jpa.JPA;


public class JPAUtil {
	/**
	 * 开启事务
	 * @return
	 */
	public static void transactionBegin(){
		boolean flag = JPA.em().getTransaction().isActive();
		if(!flag){
			JPA.em().getTransaction().begin();
		}		
	}
	
	/**
	 * 事务提交
	 */
	public static void transactionCommit(){
		boolean isMarkedRollBack = JPA.em().getTransaction().getRollbackOnly();
		
		if (!isMarkedRollBack) {
			JPA.em().getTransaction().commit();
		}
		
		boolean isActive = JPA.em().getTransaction().isActive();
		
		if(!isActive){
			JPA.em().getTransaction().begin();
		}
	}
	
	public static Query createNativeQuery(String sql, Object... params) {
		Query query = JPA.em().createNativeQuery(sql);
		int index = 0;
		
		for (Object param : params) {
			query.setParameter(++index, param);
		}
		
		return query;
	}
	
	/**
	 * 执行查询语句，返回一个map集合
	 * @param sql
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> getList(String sql, Object... params) {
		Query query = createNativeQuery(sql, params);
		query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		
		try {
			return query.getResultList();
		} catch (Exception e) {
			LoggerUtil.error(false, e, "数据库异常：%s", e.getMessage());
			
			return null;
		}
	}
}
