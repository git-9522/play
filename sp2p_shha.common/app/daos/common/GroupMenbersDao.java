package daos.common;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import common.utils.PageBean;
import daos.base.BaseDao;
import models.common.entity.t_group_menbers;

/**
 * 会员分组信息表DAO接口
 *
 * @description 
 *
 * @author jiayijian
 * @createDate 2017年3月31日
 */
public class GroupMenbersDao extends BaseDao<t_group_menbers>{
	
	protected GroupMenbersDao(){}
	
	/**
	 * 后台-会员管理-会员分组列表<br>
	 * 
	 * @param currPage
	 * @param pageSize
	 *
	 * @author jiayijan
	 * @createDate 2017年03月31日
	 *
	 */
	public PageBean<t_group_menbers> pageOfGroupMenbers( int currPage, int pageSize, String userName) {
		
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(1) FROM t_group_menbers g where 1=1  ");
		StringBuffer querySQL = new StringBuffer("SELECT * FROM t_group_menbers g where 1=1 ");
		Map<String, Object> args = new HashMap<String, Object>();
		
		if (StringUtils.isNotBlank(userName)) {
			/* 按用户名搜索 */
			countSQL.append(" AND g.name like :name");
			querySQL.append(" AND g.name like :name");
			args.put("name", "%"+userName+"%");
		}
		
		return this.pageOfBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), args);
	}
	
	/**
	 * 会员分组-更新会员人数<br>
	 *
	 * @author jiayijan
	 * @createDate 2017年03月31日
	 *
	 */
	public int updateMenberCountAdd(long gId,int num){
		
		String sql = "update t_group_menbers set menber_count = menber_count + :num where id = :gId ";
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("gId", gId);
		args.put("num", num);
		
		return this.updateBySQL(sql, args);
	}
	
	/**
	 * 会员分组-更新会员人数<br>
	 *
	 * @author jiayijan
	 * @createDate 2017年03月31日
	 *
	 */
	public int updateMenberCountMinus(long gId,int num){
		
		String sql = "update t_group_menbers set menber_count = menber_count - :num where id = :gId and  menber_count >= :num";
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("gId", gId);
		args.put("num", num);
		
		return this.updateBySQL(sql, args);
	}

}
