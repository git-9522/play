package controllers.app.notice;

import java.util.Map;

import com.shove.Convert;

import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import models.common.entity.t_help_center;
import models.common.entity.t_help_center.Column;
import net.sf.json.JSONObject;
import services.common.HelpCenterService;

/**
 * 发现问题-常见问题  OPT=901
 * @author admin
 *
 */
public class FrequentlyAskedQuestionsAction {
	
	public static HelpCenterService helpCenterService =Factory.getService(HelpCenterService.class);

	/**
	 * 常见问题列表
	 * @param parameters
	 * @return
	 */
	public static String FrequentlyAskedQuestionsList(Map<String, String> parameters){
		JSONObject json=new JSONObject();
		int pageSize=Convert.strToInt(parameters.get("pageSize"),8);
		int currPage=Convert.strToInt(parameters.get("currPage"), 1);
		int column=Convert.strToInt(parameters.get("column_no"), 0);
		
		PageBean<t_help_center> frequentlyAskedQuestions = helpCenterService.pageOfHelpCenterFront(currPage, pageSize,Column.getEnum(column) );
		json.put("code", 1);
		json.put("msg", "查询常见问题成功");
		json.put("questionsList", frequentlyAskedQuestions);
		
		return json.toString();
	}
	
}
