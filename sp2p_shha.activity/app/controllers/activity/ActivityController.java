package controllers.activity;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.shove.Convert;

import common.constants.ActivityConstants;
import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Factory;
import common.utils.ResultInfo;
import common.utils.Security;
import controllers.activity.action.Invert11Actity6Action;
import controllers.common.FrontBaseController;
import models.common.bean.CurrUser;
import play.cache.Cache;
import services.activity.AwardNumberRecordService;
/**
 * 活动请求控制器
 * @Title ActivityController 
 * @Description TODO Description
 * @author hjs_djk
 * @createDate 2017年10月24日 上午11:58:50
 */
public class ActivityController extends FrontBaseController{
	protected static AwardNumberRecordService awardnumberrecordservice = Factory
			.getService(AwardNumberRecordService.class);
	/**
	 * 	活动请求入口
	 * @throws IOException
	 */
	public static void index() throws IOException{
		ResultInfo result = new ResultInfo();
		int type= Convert.strToInt(params.get("type"),1);
		if (type < 0 || type > 1) {
			result.code = -1;
			result.msg = "设备类型异常";
			renderJSON(result);
		}
		Long userId = 0L;

		if (type == 0) {
			// APP
			String userIdObj = params.get("userId");
			result = Security.decodeSign(userIdObj, Constants.USER_ID_SIGN, Constants.VALID_TIME,
					ConfConst.ENCRYPTION_APP_KEY_DES);
			if (result.code < 1) {
				result.code = -1;
				result.msg = "用户id异常";
				renderJSON(result);
				return;
			}
			userId = Long.parseLong(result.obj.toString());
			System.out.println(userId);
		} else {
			// WECHAT OR WEB
			CurrUser currUser = getCurrUser();

			if (currUser == null) {
				result.code = ResultInfo.NOT_LOGIN;
				result.msg = "未登录请登录";
				renderJSON(result);
			}

			userId = currUser.id;
		}
		int opt=Convert.strToInt(params.get("opt"),0);
		switch (opt) {
		case ActivityConstants.ACTIVITY_07_11_6_QUERYLIST:
			result=Invert11Actity6Action.queryList(userId);
			break;
		case ActivityConstants.ACTIVITY_07_11_6_RUN:
			Long inver_id = Long.parseLong(params.get("inver_id")!=null?params.get("inver_id"):"0");
			if(inver_id==0L){
				result.code = -1;
				result.msg = "参数异常";
				renderJSON(result);
			}
			String key=userId+"ACTIVITY_07_11_6_RUN_"+inver_id;
			Object cache = Cache.get(key);
	    	if(cache != null){
					result.code = -50;
					result.msg = "请求过于频繁稍后再试";
					renderJSON(result);
	    	}else{
	    		Cache.safeSet(key, inver_id,Constants.CACHE_TIME_SECOND_60);
	    		
	    	}
			result=Invert11Actity6Action.run(userId, params);
			/* 清除缓存中的验证码 */
	    	Cache.safeDelete(key);
			break;	
		default:
			break;
		}
		renderJSON(result);
    	
	}
	
	/**
	 * 领号记录列表
	 */
	public static void awardNumberListPre() {
		List<Map<String, Object>> awardNumberList = awardnumberrecordservice.getAwardNumberList();
		renderJSON(awardNumberList);
	}
	
	
}
