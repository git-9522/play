package controllers.back.webOperation;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import common.annotation.SubmitCheck;
import common.annotation.SubmitOnly;
import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;
import controllers.common.BackBaseController;
import controllers.common.SubmitRepeat;
import models.common.entity.t_event_supervisor;
import models.common.entity.t_friend_ship_link;
import play.mvc.With;
import services.common.FriendShipLinkService;

/**
 * 后台-运维-友情链接
 * 
 * @author hjs
 * @createDate 2017年7月17日
 */
@With(SubmitRepeat.class)
public class FriendShipLinkMngCtrl extends BackBaseController {
	/** 注入友情链接services */
	protected static FriendShipLinkService friendShipLinkService = Factory.getService(FriendShipLinkService.class);

	/**
	 * 查询友情链接
	 *
	 * @param currPage
	 *            当前页
	 * @param pageSize
	 *            每页条数
	 * @return
	 * 
	 * @author hjs
	 * @createDate 2017年7月17日
	 */
	public static void showFriendShipLinkPre(int currPage, int pageSize) {
		if(currPage==0)currPage=1;
		if(pageSize==0)pageSize=10;
		PageBean<t_friend_ship_link> page = friendShipLinkService.pageOfFriendShipLinkBack(currPage, pageSize);

		render(page);
	}

	/**
	 * 删除友情链接
	 *
	 * @param sign
	 *            友情链接加密id
	 * @return
	 * 
	 * @author hjs
	 * @createDate 2017年7月17日
	 */
	public static void delFriendShipLink(String sign) {
		ResultInfo result = Security.decodeSign(sign, Constants.FRIENDSHIPLINK_ID_SIGN, Constants.VALID_TIME,
				ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {

			renderJSON(result);
		}
		long id = Long.parseLong((String) result.obj);
		t_friend_ship_link friendShipLink = friendShipLinkService.findByID(id);

		boolean delFlag = friendShipLinkService.delFriendShipLink(id);
		if (!delFlag) {
			result.code = -1;
			result.msg = "删除失败";

			renderJSON(result);
		}

		// 添加管理员事件
		long supervisorId = getCurrentSupervisorId();
		Map<String, String> supervisorEventParam = new HashMap<String, String>();
		supervisorEventParam.put("friendshiplink_id", friendShipLink.id.toString());
		supervisorEventParam.put("friendshiplink_name", friendShipLink.name);
		supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.FRIENDSHIPLINK_REMOVE,
				supervisorEventParam);

		result.code = 1;
		result.msg = "删除成功";

		renderJSON(result);
	}

	/**
	 * 进入 添加友情链接页面
	 * 
	 *
	 * @return
	 * 
	 * @author hjs
	 * @createDate 2017年7月17日
	 */
	@SubmitCheck
	public static void toAddFriendShipLinkPre() {

		render();
	}

	/**
	 * 添加友情链接确认页面
	 * 
	 * @rightID 205002
	 *
	 * @param friendShipLink
	 *            友情链接
	 * 
	 * @return
	 * @author hjs
	 * @createDate 2017年7月17日
	 */
	@SubmitOnly
	public static void addFriendShipLink(t_friend_ship_link friendShipLink) {
		checkAuthenticity();

		/* 回显数据 */
		flash.put("name", friendShipLink.name);
		flash.put("url", friendShipLink.url);
		flash.put("sort", friendShipLink.sort);
		flash.put("description", friendShipLink.description);

		if (StringUtils.isBlank(friendShipLink.name)) {
			flash.error("名称不能为空");
			toAddFriendShipLinkPre();
		}
		if (StringUtils.isNotBlank(friendShipLink.url)) {
			if (friendShipLink.url.length() < 1 || friendShipLink.url.length() > 100) {
				flash.error("链接长度为0到100个字符");
				toAddFriendShipLinkPre();
			}
		}

		boolean addFlag = friendShipLinkService.addFriendShipLink(friendShipLink);
		if (!addFlag) {
			flash.error("添加失败");
			toAddFriendShipLinkPre();
		}

		// 添加管理员事件
		long supervisorId = getCurrentSupervisorId();
		Map<String, String> supervisorEventParam = new HashMap<String, String>();
		supervisorEventParam.put("friendshiplink_id", friendShipLink.id.toString());
		supervisorEventParam.put("friendshiplink_name", friendShipLink.name);
		supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.FRIENDSHIPLINK_ADD,
				supervisorEventParam);

		flash.success("添加友情链接成功！");
		showFriendShipLinkPre(1, 10);
	}

	/**
	 * 后台-运维-友情链接-编辑友情链接
	 * 
	 * 进入 编辑页面
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月28日
	 */
	public static void toEditFriendShipLinkPre(long id) {
		t_friend_ship_link friendShipLink = friendShipLinkService.findByID(id);
		if (friendShipLink == null) {
			error404();
		}
		render(friendShipLink);
	}

	/**
	 * 编辑确认页面
	 *
	 * @param sign
	 *            友情链接加密id
	 * @param name
	 *            名称
	 * @param order
	 *            排序
	 * @param url
	 *            友情链接
	 * @return
	 * 
	 * @author hjs
	 * @createDate 2017年7月17日
	 */
	public static void editFriendShipLink(String sign, String name, int sort, String url, String description) {
		ResultInfo result = Security.decodeSign(sign, Constants.FRIENDSHIPLINK_ID_SIGN, Constants.VALID_TIME,
				ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {

			error404();
		}
		long id = Long.parseLong((String) result.obj);
		t_friend_ship_link friendShipLink = friendShipLinkService.findByID(id);

		boolean editFlag = friendShipLinkService.upadteFriendShipLink(id, name,sort, url, description);
		if (!editFlag) {
			flash.error("编辑失败");
			toEditFriendShipLinkPre(id);
		}

		// 添加管理员事件
		long supervisorId = getCurrentSupervisorId();
		Map<String, String> supervisorEventParam = new HashMap<String, String>();
		supervisorEventParam.put("friendshiplink_id", friendShipLink.id.toString());
		supervisorEventParam.put("friendshiplink_name", friendShipLink.name);
		supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.FRIENDSHIPLINK_EDIT, supervisorEventParam);

		flash.success("编辑友情链接成功！");
		showFriendShipLinkPre(1, 10);
	}

}
