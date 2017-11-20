package controllers.activity.action;

import com.shove.Convert;

import common.utils.Factory;
import common.utils.ResultInfo;
import play.mvc.Scope;
import services.activity.Invert11Activity6Service;

public class Invert11Actity6Action {
	private static Invert11Activity6Service invert11actity6service = Factory.getService(Invert11Activity6Service.class);

	public static ResultInfo queryList(Long userID) {
		return invert11actity6service.queryList(userID);
	}

	public static ResultInfo run(Long userID,  Scope.Params parameters) {
		int cid = Convert.strToInt(parameters.get("cid"), 0);
		Long inver_id = Long.parseLong(parameters.get("inver_id")!=null?parameters.get("inver_id"):"0");
		return invert11actity6service.run(userID, cid, inver_id);
	}
}
