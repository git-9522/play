package controllers.back.setting;

import common.annotation.SubmitCheck;
import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;
import controllers.common.BackBaseController;
import controllers.common.SubmitRepeat;
import models.common.entity.t_offline_user_filter;
import play.mvc.With;
import services.common.UserFilterService;

@With(SubmitRepeat.class)
public class BackActivityFilterUser extends BackBaseController {

	protected static UserFilterService userFilterService = Factory.getService(UserFilterService.class);

	/** 线下用户过滤添加推广 */
	@SubmitCheck
	public static void addUserFilter(String mobile, int period1, int period3, int period6) {
		ResultInfo result = new ResultInfo();
		boolean isSpreader = userFilterService.isSpreader(mobile);
		if (!isSpreader) {
			String info = "此号不是推广人手机号!";

			toAddUserFilterPre(info);
		}
		boolean isExist = userFilterService.isAlreadyExist(mobile);
		if (isExist) {
			String info = "推广人手机号已经存在!";

			toAddUserFilterPre(info);
		}
		boolean rt = userFilterService.addOfflineUserFilter(mobile, period1, period3, period6);
		if (!rt) {
			result.code = -1;
			String info = "添加数据有误!";

			toAddUserFilterPre(info);
		}
		String info = "添加成功!";
		renderArgs.put("info", info);
		toAddUserFilterPre(info);

	}

	public static void delUserFilter(String sign) {
		ResultInfo result = new ResultInfo();
		result = Security.decodeSign(sign, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {

			renderJSON(result);
		}
		Long id = Long.parseLong((String) result.obj);
		int rt = userFilterService.delOfflineUserFilter(id);
		if (rt < 0) {
			result.code = -1;
			result.msg = "删除失败!";
			renderJSON(result);
		}
		renderJSON(result);

	}

	public static void updateUserFilter(String sign, int period1, int period3, int period6, String spreaderMobile) {
		ResultInfo result = new ResultInfo();
		result = Security.decodeSign(sign, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {

			renderJSON(result);
		}
		Long id = Long.parseLong((String) result.obj);
		userFilterService.updateOfflineUserFilter(spreaderMobile, period1, period3, period6, id);
		showUserFilterPre(1, 10);
	}

	public static void showUserFilterPre(int currPage, int pageSize) {
		PageBean<t_offline_user_filter> page = userFilterService.getAll(currPage, pageSize);
		render(page);
	}

	@SubmitCheck
	public static void toAddUserFilterPre(String info) {

		render(info);

	}

	public static void toUpdateUserFilterPre(String sign) {
		ResultInfo result = new ResultInfo();
		result = Security.decodeSign(sign, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {

			renderJSON(result);
		}
		Long id = Long.parseLong((String) result.obj);
		t_offline_user_filter userFilter = userFilterService.findById(id);
		render(userFilter);
	}
}
