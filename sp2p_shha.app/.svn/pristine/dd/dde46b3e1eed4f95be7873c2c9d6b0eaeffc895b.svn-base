package controllers.app.notice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.shove.Convert;

import common.constants.SettingKey;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import models.common.entity.t_advertisement;
import models.common.entity.t_advertisement.Location;
import net.sf.json.JSONObject;
import services.common.AdvertisementService;
import services.common.SettingService;

/**
 * 活动中心(显示4个活动)
 */
public class ActivityCenterAction  {
	private static SettingService settingService = Factory.getService(SettingService.class);
	public static AdvertisementService advertisementService=Factory.getService(AdvertisementService.class);
	/**
	 * 活动列表  OPT=900
	 * @return
	 */
	public static String activityList(){
		Map<String, Object> result=new HashMap<String, Object>();
		List<t_advertisement> banners = advertisementService.queryAdvertisementFront(Location.ACTIVITY_CENTER, 4);
		List<Map<String,Object>> bannerList = new ArrayList<Map<String,Object>>();
		
		if(banners==null){
		 result.put("banners", null);
		}else{
			for (t_advertisement banner : banners) {
				Map<String, Object> bannerMap=new HashMap<String, Object>();
				bannerMap.put("image_url", banner.image_url);
				bannerMap.put("url", banner.url);
				bannerList.add(bannerMap);
			}
			
		}
		result.put("code",1);
		result.put("msg", "查询活动列表成功");
		result.put("banners", bannerList);
	
		return JSONObject.fromObject(result).toString();
		
	}
	/**
	 * 运营报告列表 OPT=1000
    */
	public static String  operationReportList(Map<String, String> parameters){
		Map<String, Object> result=new HashMap<String, Object>();
		int pageSize=Convert.strToInt(parameters.get("pageSize"), 5);
		int currPage=Convert.strToInt(parameters.get("currPage"), 1);
		PageBean<t_advertisement> advertisements = advertisementService.pageOfAdvertisementBack(currPage,pageSize,Location.OPERATION_REPORT);
		List<Map<String, Object>> operationReport=new ArrayList<Map<String, Object>>();
	for(t_advertisement advertisement:advertisements.page){
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("url",advertisement.url);
		map.put("name",advertisement.name);
		map.put("img_url",advertisement.image_url);
		operationReport.add(map);
		
	}
	Map<String, String> settings = settingService.queryAllSettings();
	result.put("code", 1);
	result.put("msg", "查询运营列表成功");
	result.put("operationReport", operationReport);
	result.put("companyTel",settings.get(SettingKey.COMPANY_TEL));
	return JSONObject.fromObject(result).toString();
	}
}
