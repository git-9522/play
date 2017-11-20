package daos.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.utils.PageBean;
import daos.base.BaseDao;
import models.common.entity.t_friend_ship_link;

/**
 * 友情链接dao实现
 * 
 * @description
 *
 * @author hjs
 * @createDate 2017年7月17日
 */
public class FriendShipLinkDao extends BaseDao<t_friend_ship_link> {

	protected FriendShipLinkDao() {
	}

	/**
	 * 友情链接 编辑
	 *
	 * @param name
	 *            名称
	 * @param order
	 *            排序
	 * @param url
	 *            友情链接链接
	 * @param description
	 *            简介
	 * @return
	 * 
	 * @author hjs
	 * @createDate 2017年07月17日
	 */
	public int upadteFriendShipLink(long id, String name, int sort, String url, String description) {
		String sql = "UPDATE t_friend_ship_link SET name=:name,sort=:sort,url=:url,description=:description WHERE id=:id";

		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("name", name);
		condition.put("sort", sort);
		condition.put("url", url);
		condition.put("description", description);
		condition.put("id", id);

		return this.updateBySQL(sql, condition);
	}

	/**
	 * 获取友情链接 后台 分页
	 *
	 * @param currPage
	 *            当前页面
	 * @param pageSize
	 *            显示条数
	 * @return
	 * 
	 * @author hjs
	 * @createDate 2017年7月17日
	 */
	public PageBean<t_friend_ship_link> pageOfFriendShipLinkBack(int currPage, int pageSize) {
		String sql = "SELECT * FROM t_friend_ship_link ORDER BY sort";
		String sqlCount = "SELECT COUNT(1) FROM t_friend_ship_link";

		return this.pageOfBySQL(currPage, pageSize, sqlCount, sql, null);
	}

	/**
	 * 获取友情链接 前几条 不分页
	 *
	 * @param limit
	 *            查询条数
	 * @return
	 * 
	 * @author hjs
	 * @createDate 2017年7月17日
	 */
	public List<t_friend_ship_link> queryFriendShipLinkFront(int limit) {
		String sql = "SELECT * FROM t_friend_ship_link ORDER BY sort";
		Map<String, Object> condition = new HashMap<String, Object>();
		if(limit > 0) {
			condition.put("limit", limit);
			sql.concat(" LIMIT :limit");
		}
		return this.findListBySQL(sql, condition);
	}

}
