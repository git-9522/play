package services.common;

import java.util.Date;
import java.util.List;

import common.utils.Factory;
import common.utils.PageBean;
import daos.common.FriendShipLinkDao;
import models.common.entity.t_friend_ship_link;
import services.base.BaseService;

/**
 * 友情链接service实现
 * 
 * @description
 *
 * @author hjs
 * @createDate 2017年7月17日
 */
public class FriendShipLinkService extends BaseService<t_friend_ship_link> {
	protected FriendShipLinkDao friendShipLinkDao = Factory.getDao(FriendShipLinkDao.class);;

	protected FriendShipLinkService() {
		super.basedao = this.friendShipLinkDao;
	}

	/**
	 * 添加 友情链接
	 *
	 * @param friendShipLink
	 *            友情链接
	 * @return
	 * 
	 * @author hjs
	 * @createDate 2017年7月17日
	 */
	public boolean addFriendShipLink(t_friend_ship_link friendShipLink) {
		friendShipLink.time = new Date();
		System.out.println(friendShipLink.toString());
		return friendShipLinkDao.save(friendShipLink);
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
	public boolean upadteFriendShipLink(long id, String name, int order, String url, String description) {
		int row = friendShipLinkDao.upadteFriendShipLink(id, name, order, url, description);
		if (row <= 0) {
			return false;
		}
		return true;
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
		return friendShipLinkDao.pageOfFriendShipLinkBack(currPage, pageSize);
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

		return friendShipLinkDao.queryFriendShipLinkFront(limit);
	}

	/**
	 * 友情链接 删除
	 *
	 * @param id
	 *            友情链接id
	 * @return
	 * 
	 * @author hjs
	 * @createDate 2017年07月17日
	 */
	public boolean delFriendShipLink(long id) {
		int row = friendShipLinkDao.delete(id);
		if (row <= 0) {
			return false;
		}
		return true;
	}

}
