package common.tags;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import controllers.common.BackBaseController;
import groovy.lang.Closure;
import models.common.bean.CurrSupervisor;
import models.common.entity.t_questionnaire_option;
import models.common.entity.t_questionnaire_record;
import play.templates.FastTags;
import play.templates.GroovyTemplate.ExecutableTemplate;
import play.templates.JavaExtensions;

public class CommonTags extends FastTags {
	
	/**
	 * 权限管理标签
	 * <br>demo:
	 * <br>#{rightMng rightId:100}<i data-title="编辑" class="iconfont edit-btn">&#xe602;</i>#{/rightMng}
	 *
	 * @author huangyunsong
	 * @createDate 2016年3月9日
	 */
	public static void _rightMng(Map<String, Object> params, Closure body, PrintWriter out, 
			ExecutableTemplate template, int fromLine) {
		if (params.get("rightId") == null) {
			
			return;
		}
		
		long rightId = Long.parseLong(params.get("rightId").toString());
		
		List<Long> rights = null;
		CurrSupervisor currSupervisor = BackBaseController.getCurrSupervisor();
		if(currSupervisor != null){
			rights = currSupervisor.rights;
		}
		
		
		boolean hasRight = false;
		if (rights != null && rights.size() > 0) {
			for(Long right : rights){
				if(right.longValue() == rightId){
					hasRight = true;
					break;
				}
			}
		}
		
		//权限判断，有权限，则输出标签体内容
		if (hasRight) {
			out.println(JavaExtensions.toString(body));
		} 
	} 
	
	@SuppressWarnings("unchecked")
	public static void _addMyClass(Map<String, Object> params, Closure body, PrintWriter out, 
			ExecutableTemplate template, int fromLine) {
		if(params.get("option") == null || params.get("records") == null || params.get("className") == null) {
			return;
		}
		try {
			t_questionnaire_option option = (t_questionnaire_option) params.get("option");
			List<t_questionnaire_record> records = (List<t_questionnaire_record>) params.get("records");
			String className = (String) params.get("className");
			for(t_questionnaire_record record : records) {
				if(record.questionnaire_option_id == option.id) {
					out.println("class=\"" + className + "\"");
					return;
				}
			}
		} catch (Exception e) {
			return;
		}
	}

}
