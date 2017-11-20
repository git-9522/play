package controllers.app.notice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.shove.Convert;

import common.constants.ConfConst;
import common.constants.Constants;
import common.enums.InformationMenu;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;
import models.common.entity.t_information;
import models.common.entity.t_user_info;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;
import service.AccountAppService;
import services.common.InformationService;

/**
 * 首页公告 
 * @author admin
 *
 */
public class NoticeAction {
	/**
	 * 公告key
	 */
	protected static InformationMenu informationMenu=InformationMenu.INFO_BULLETIN;//首页公告类别：官方公告 
	
	private static InformationService informationService = Factory.getService(InformationService.class);
	/**
	 * 首页公告导航  OPT=800
	 * 显示最新的一条  
	 * @return
	 */
	public static String getInformation(){
		JSONObject json=new JSONObject();
		t_information information=null;
		List<t_information> informations = informationService.queryInformationFront(informationMenu, 1);
		if(informations.size()<0||null==informations.get(0)){
		 json.put("title", "");	
		 json.put("informationId", "");
		}else{
			information=informations.get(0);
			 json.put("title",information.title);	
			 json.put("informationId",information.sign );
		}
		json.put("code", 1);
		json.put("msg", "成功");
		return json.toString();
	}
	/**
	 * 通过id查询公告详情  OPT=801
	 * @param parameters
	 * @return
	 */
	public static String getInformationById(Map<String, String> parameters){
		JSONObject json=new JSONObject();
		String informationIdSign= parameters.get("informationId");
		ResultInfo informationIdSignDecode = Security.decodeSign(informationIdSign, Constants.INFORMATION_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (informationIdSignDecode.code < 1) { 
			json.put("code", informationIdSignDecode.code);
			json.put("msg",informationIdSignDecode.msg);
			return json.toString();
		}
			long informationId = Long.parseLong(informationIdSignDecode.obj.toString());
			t_information information = informationService.findInformationByIdFront(informationId);
			if(information==null){
				json.put("code", -1);
				json.put("msg","查询资讯为空");
				return json.toString();
			}
			json.put("title", information.title);
			json.put("show_time", information.show_time);
			StringBuffer sb=new StringBuffer();
			sb.append("<html lang=\"en\">\n <head>\n  <meta charset=\"UTF-8\">\n  <meta name=\"Generator\" content=\"EditPlus\u00AE\">\n  <meta name=\"Author\" content=\"\">\n  <meta name=\"Keywords\" content=\"\">\n  <meta name=\"Description\" content=\"\">\n <meta name='viewport' content = 'user-scalable=yes, width=device-width,initial-scale=1'/><style type=text/css>img{max-width: 100%;}</style> <title>官方公告</title>\n");
			sb.append("<body><h3 style=\"font-weight:normal;\">");
			sb.append(information.title);
			sb.append("</h3>\n<p style=\"text-align:right;color:#999\"> 发布时间 : ");
			sb.append(DateUtil.dateToString(information.show_time, Constants.DATE_PLUGIN5));
			sb.append("</p>");
			sb.append(information.content);
			sb.append(" </body></html>");
			json.put("content", sb.toString());
			json.put("code", 1);
			json.put("msg","查询资讯成功");
		
		return json.toString();
	}
	/**
	 * 查询资讯列表（分页） OPT=802
	 *  
	 * @param parameters
	 *         informationMenu：如果是1返回官方公告；2返回媒体报告列表;3返回金融课堂 
	 * @return
	 * */
	public static String getInformationList(Map<String, String> parameters){
		int currPage=Convert.strToInt(parameters.get("currPage"),1);
		int pageSize= Convert.strToInt(parameters.get("pageSize"), 15);
		List<String> informationMenus=new ArrayList<String>();
		int informationMenu=Convert.strToInt(parameters.get("informationMenu"), 0);//如果是1返回官方公告；2返回媒体报告列表;3返回金融课堂
		Map<String, Object> map=new HashMap<String, Object>();
		if(informationMenu==1){
			informationMenus.add(InformationMenu.INFO_BULLETIN.code);
		}else if(informationMenu==2){
			informationMenus.add(InformationMenu.INFO_REPORT.code);
		}else if(informationMenu==3){
			/*informationMenus.add(InformationMenu.HOME_HELPCENTER_CLASS.code);*/
			informationMenus.add(InformationMenu.INFO_STORY.code);
		}else{
			map.put("code", -1);
			map.put("msg", "资讯类别有误");
		}
		PageBean<t_information> informations = informationService.pageOfInformationFront(informationMenus, currPage, pageSize,null);
		
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setIgnoreDefaultExcludes(false);
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		jsonConfig.setExcludes(// 只要设置这个数组，指定过滤哪些字段。
				new String[] {"id","order_time","image_format","column_name","image_format","image_resolution","image_size","keywords","persistent","is_use","support_count","source_from","read_count","content"}
						
		);
		map.put("code", 1);
		map.put("msg", "查询列表成功");
		map.put("informations",informations);
		return JSONObject.fromObject(map,jsonConfig).toString();
	}
}
