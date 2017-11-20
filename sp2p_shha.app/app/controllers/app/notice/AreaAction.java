package controllers.app.notice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.enums.Area;
import common.enums.Province;
import common.utils.EnumUtil;
import net.sf.json.JSONObject;

/**
  * 地区
  * @author admin
  *
  */
public class AreaAction {
	/**
	 * 获取所有省级  OPT=256
	 * @return
	 * @throws Exception 
	 */
	public static String getAllProvince(){
		JSONObject json=new JSONObject();
		
		List<Map<String, String>> Provinces =new ArrayList<Map<String,String>>();
	
		for (Province province : Province.values()) {
			Map<String, String> map=new HashMap<String, String>();
			map.put("value", province.name);
			map.put("code", province.code);
			Provinces.add(map);
			
		}
		json.put("code", 1);
		json.put("msg","查询成功");
		json.put("Provinces", Provinces);
		return json.toString();
	}
/**
 * 根据省级编号获取市级 OPT=257
 * @param code
 * @return
 */
	public static String getAreaByProvinceId(Map<String, String> parameters){
		String code=parameters.get("code");
		
		JSONObject json=new JSONObject();
		Area[] areases= Province.getAreasByCode(code);
	List<Map<String, String>> citys=new ArrayList<Map<String,String>>(); 
		
		for (Area area : areases) {
			Map<String, String> city=new HashMap<String, String>();
			city.put("code", area.code);
			city.put("value", area.name);
			citys.add(city);
		}
		json.put("code", 1);
		json.put("msg","查询成功");
		json.put("city", citys);
		return json.toString();
	}

	
	
}
