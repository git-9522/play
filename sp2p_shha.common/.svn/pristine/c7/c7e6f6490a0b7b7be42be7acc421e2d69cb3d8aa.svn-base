package daos.common;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import common.utils.PageBean;
import daos.base.BaseDao;
import models.common.bean.ShowGroupMenberInfo;
import models.common.entity.t_group_menbers_user;

/**
 * 会员分组关系信息DAO接口
 *
 * @description 
 *
 * @author jiayijian
 * @createDate 2017年3月31日
 */
public class GroupMenbersUserDao extends BaseDao<t_group_menbers_user>{
	
	protected GroupMenbersUserDao(){}
	
	/**
	 *  统计分组成员个数
	 * @param gId
	 * @author jiayijan
	 * @createDate 2017年03月31日
	 *
	 */
	public int countMenbersUser(long gId){
		return this.countByColumn( " group_id = ?", gId);
	}
	
	/**
	 *  删除分组成员
	 * @param gId
	 * @author jiayijan
	 * @createDate 2017年03月31日
	 *
	 */
	public int delMenbersUser(long gId){
		
		String sql = "delete from t_group_menbers_user where group_id = :gId ";
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("gId", gId);
		
		return this.deleteBySQL(sql, args);
	}

	
	/**
	 * 后台-会员管理-会员分组成员列表
	 * @param currPage
	 * @param pageSize
	 *
	 * @author jiayijain
	 * @createDate 2017年4月1日
	 */
	public PageBean<ShowGroupMenberInfo> pageOfGroupMenbersUse(int gId, int currPage, int pageSize,String userName) {
		
		/**
		  SQL:
		 	SELECT
				gmu.id AS id,
				t.time AS time,
				t.name AS name,
				tui.mobile AS mobile,
				tui.email AS email,
				tuf.balance AS balance,
				tuf.freeze AS freeze,
				t.login_count AS login_count,
				t.last_login_time AS last_login_time,
				t.is_allow_login AS is_allow_login,
				(SELECT IFNULL(SUM(tb.repayment_corpus + tb.repayment_interest),0) FROM t_bill tb WHERE t.id = tb.user_id AND tb.status IN (0, 1)) AS no_repayment,
				(SELECT IFNULL(SUM(tbi.receive_corpus + tbi.receive_interest),0) FROM t_bill_invest tbi WHERE t.id = tbi.user_id AND tbi.status IN (0)) AS no_receive,
				(SELECT IFNULL(SUM(tb.amount), 0) FROM t_bid tb WHERE t.id = tb.user_id AND tb.status IN (4, 5)) AS borrow_total,
				(SELECT IFNULL(SUM(ti.amount), 0) FROM t_invest ti,t_bid tb WHERE ti.debt_id=0 AND t.id = ti.user_id AND ti.bid_id = tb.id AND tb.status IN (4, 5)) AS invest_total
			FROM
				t_user t,
				t_user_info tui,
				t_user_fund tuf,
				t_group_menbers gm,
				t_group_menbers_user gmu
			WHERE
				t.id = tuf.user_id
			AND t.id = tui.user_id 
			AND gmu.user_id = t.id
			AND gm.id = gmu.group_id
		 */
		
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(1) FROM t_user t, t_user_info tui, t_user_fund tuf,t_group_menbers gm,t_group_menbers_user gmu WHERE t.id = tuf.user_id AND t.id = tui.user_id AND gmu.user_id = t.id AND gm.id = gmu.group_id ");
		StringBuffer querySQL = new StringBuffer("SELECT gmu.id AS id,t.time AS time,t.name AS name,tui.mobile AS mobile,tui.email AS email,tuf.balance AS balance,tuf.freeze AS freeze,t.login_count AS login_count,t.last_login_time AS last_login_time,t.is_allow_login AS is_allow_login,(SELECT IFNULL(SUM(tb.repayment_corpus + tb.repayment_interest),0) FROM  t_bill tb WHERE t.id = tb.user_id AND tb.status IN (0,1)) AS no_repayment,(SELECT IFNULL(SUM(tbi.receive_corpus + tbi.receive_interest),0) FROM t_bill_invest tbi WHERE t.id = tbi.user_id AND tbi.status = 0) AS no_receive,(SELECT IFNULL(SUM(tb.amount),0) FROM t_bid tb WHERE t.id = tb.user_id AND tb.status IN (4, 5)) AS borrow_total, (SELECT IFNULL(SUM(ti.amount), 0) FROM t_invest ti,t_bid tb WHERE t.id = ti.user_id AND ti.bid_id=tb.id AND tb.status in (4,5) AND ti.debt_id=0 ) AS invest_total FROM t_user t, t_user_info tui, t_user_fund tuf,t_group_menbers gm,t_group_menbers_user gmu WHERE t.id = tuf.user_id AND t.id = tui.user_id AND gmu.user_id = t.id AND gm.id = gmu.group_id ");
		
		Map<String, Object> args = new HashMap<String, Object>();
		
		countSQL.append(" AND gm.id = :gId");
		querySQL.append(" AND gm.id = :gId");
		args.put("gId", gId);
		
		if (StringUtils.isNotBlank(userName)) {
			/* 按用户名搜索 */
			countSQL.append(" AND t.name like :name");
			querySQL.append(" AND t.name like :name");
			args.put("name", "%"+userName+"%");
		}
		
		return super.pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), ShowGroupMenberInfo.class, args);
	}
	
	/**
	 * 投资标准备查询分组关系
	 * 
	 * @param gId 分组记录id
	 * @param userId 
	 * 
	 * @author jiayijain
	 * @createDate 2017年4月1日
	 * @return
	 */
	public t_group_menbers_user findMenberUserInfo(long gId,long userId){
		
		return this.findByColumn(" group_id = ? and user_id = ? ", gId,userId);
	}
}
