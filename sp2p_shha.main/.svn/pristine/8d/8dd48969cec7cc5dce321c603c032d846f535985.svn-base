package controllers.back.risk;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.commons.lang.StringUtils;

import models.common.entity.t_event_supervisor.Event;
import models.core.bean.BackRiskAgency;
import models.core.bean.BackRiskBid;
import models.core.entity.t_agencies;
import models.core.entity.t_bid;
import models.core.entity.t_product;

import com.shove.Convert;

import services.core.AgencyService;

import common.constants.Constants;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.excel.ExcelUtils;
import common.utils.file.FileUtil;
import common.utils.jsonAxml.JsonDateValueProcessor;
import common.utils.jsonAxml.JsonDoubleValueProcessor;

import controllers.common.BackBaseController;

/**
 * 后台-风控-合作机构管理控制器
 *
 * @description 
 *
 * @author jiayijian
 * @createDate 2017年3月30日
 */
public class AgencyMngCtrl extends BackBaseController {
	
	protected static AgencyService agencyService = Factory.getService(AgencyService.class);
	
	/**
	 * 后台-风控-合作机构
	 * 
	 * @rightID 407001
	 * 
	 * @param currPage 当前页
	 * @param pageSize 每页条数
	 * @param userName 机构名称
	 * @param exports 1：导出   default：不导出
	 * @param numNo 编号
	 * @return
	 *
	 * @author jiayijian
	 * @createDate 2017年3月28日
	 *
	 */
	public static void showAgencyPre(int currPage,int pageSize){
		
		String numNo = params.get("numNo");
		String userName = params.get("userName");
		
		int exports = Convert.strToInt(params.get("exports"), 0);
		
		PageBean<BackRiskAgency> pageBean = agencyService.pageOfAgencyRisk(currPage, pageSize, exports, userName, numNo);
		//导出
		if(exports == Constants.EXPORT){
			List<BackRiskAgency> list = pageBean.page;
			 
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(Constants.DATE_FORMATE));
			jsonConfig.registerJsonValueProcessor(Double.class, new JsonDoubleValueProcessor(Constants.FINANCE_FORMATE_NORMAL));
			JSONArray arrList = JSONArray.fromObject(list, jsonConfig);
			
			for (Object obj : arrList) {
				JSONObject agency = (JSONObject)obj;	
				
				if(StringUtils.isNotBlank(agency.getString("is_use"))){
					agency.put("is_use", agency.getString("is_use").equals("1")?"启用":"停止");
				}
			}
			
			String fileName="合作机构列表";
			
			File file = ExcelUtils.export(fileName,
			arrList,
			new String[] {
			"编号", "机构名称", "营业执照", "组织机构代码", "累计发布标的", "成功的标的", "合作时间", "状态"},
			new String[] {
			"id","name", "business_license_img", "organization_code", "bid_count", "success_bid_count", "time", "is_use"});
			   
			renderBinary(file, fileName + ".xls");
		}
		
		render(pageBean,numNo,userName);
	}
	
	/**
	 * 后台-风控-添加合作机构页面
	 *
	 * @author jiayijian
	 * @createDate 2017年3月28日
	 *
	 */
	public static void toAddAgencyPre(){
		render();
	}
	
	/**
	 * 后台-风控-添加合作机构
	 * 
	 * @param name 机构名称
	 * @param business_license_img  营业执照图片
	 * @param organization_code 组织机构代码 	 	 	
	 * @param introduction 机构介绍
	 *
	 * @author jiayijian
	 * @createDate 2017年3月28日
	 *
	 */
	public static void addAgency(String name,String business_license_img,String organization_code,String introduction){
		
		
		flash.put("name", name);
		flash.put("business_license_img", business_license_img);
		flash.put("organization_code", organization_code);
		flash.put("introduction", introduction);
		
		ResultInfo result = new ResultInfo();
		
		result = agencyService.checkAgencyValue(name, business_license_img, organization_code, introduction);
		
		if(result.code < 1){
			
			LoggerUtil.info(true, "添加机构时：%s", result.msg);
			flash.error(result.msg);
			toAddAgencyPre();
		}
		
		result = agencyService.addAgency(name, business_license_img, organization_code, introduction);
		
		if(result.code < 1){
			
			LoggerUtil.info(true, "添加机构时：%s", result.msg);
			flash.error(result.msg);
			toAddAgencyPre();
		}
		
		t_agencies agency = (t_agencies) result.obj;
		
		/** 添加管理员事件 */
		Map<String, String>param = new HashMap<String, String>();
		param.put("supervisor", ""+getCurrentSupervisorId());
		param.put("agencyId", ""+agency.id);
		param.put("agencyName", agency.name);
		boolean flag = supervisorService.addSupervisorEvent(getCurrentSupervisorId(), Event.AGENCY_ADD, param);
		
		if(!flag){
			LoggerUtil.error(true, "保存管理员事件失败!");
			
			flash.error("保存管理员事件失败");
			toAddAgencyPre();
		}
		
		flash.success(result.msg);
		showAgencyPre(1,10);
	}
	
	
	/**
	 * 后台-风控-编辑合作机构页面
	 *
	 * @param agencyId 机构id
	 * @author jiayijian
	 * @createDate 2017年3月28日
	 *
	 */
	public static void toEditAgencyPre(long agencyId){
		
		/*ResultInfo result = new ResultInfo();*/
		
		if(agencyId < 0){
			flash.error("机构ID参数错误");
			
			showAgencyPre(1, 10);
		}
		t_agencies agency = agencyService.findByID(agencyId);
		if(agency == null){
			flash.error("没有找到该合作机构");
			
			showAgencyPre(1, 10);
		}
		
		render(agency);
	}
	
	
	/**
	 * 后台-风控-编辑合作机构
	 * 
	 * @param agencyId 机构id
	 * @param name 机构名称
	 * @param business_license_img  营业执照图片
	 * @param organization_code 组织机构代码 	 	 	
	 * @param introduction 机构介绍
	 *
	 * @author jiayijian
	 * @createDate 2017年3月28日
	 *
	 */
	public static void editAgency(long agencyId,String name,String business_license_img,String organization_code,String introduction){
		
		ResultInfo result = new ResultInfo();
		
		t_agencies agency = agencyService.findByID(agencyId);
		if(agency == null){
			
			flash.error("没有找到该合作机构");
			showAgencyPre(1,10);
		}
		
		result = agencyService.checkAgencyValue(name, business_license_img, organization_code, introduction);
		
		if(result.code < 1){
			
			LoggerUtil.info(true, "编辑机构时：%s", result.msg);
			flash.error(result.msg);
			toEditAgencyPre(agencyId);
		}
		
		result = agencyService.editAgency(agencyId,name, business_license_img, organization_code, introduction);
		
		if(result.code < 1){
			
			LoggerUtil.info(true, "编辑机构时：%s", result.msg);
			flash.error(result.msg);
			toEditAgencyPre(agencyId);
		}
		
		/** 添加管理员事件 */
		Map<String, String>param = new HashMap<String, String>();
		param.put("supervisor", ""+getCurrentSupervisorId());
		param.put("agencyId", ""+agencyId);
		param.put("agencyName", agency.name);
		boolean flag = supervisorService.addSupervisorEvent(getCurrentSupervisorId(), Event.AGENCY_EDIT, param);
		
		if(!flag){
			LoggerUtil.error(true, "保存管理员事件失败!");
			
			flash.error("保存管理员事件失败");
			toEditAgencyPre(agencyId);
		}
		
		flash.success(result.msg);
		showAgencyPre(1,10);
	}
	
	
	/**
	 * 更新合作机构状态
	 *
	 * @param agencyId
	 * @param agencyName 机构状态名称，用于添加管理员事件时使用
	 * @param isUse true-当前状态为上架；false-当前状态为下架
	 *
	 * @author yaoyi
	 * @createDate 2015年12月16日
	 */
	public static void updateAgencyStatus(long agencyId, String agencyName, boolean isUse){
		ResultInfo res = new ResultInfo();
		
		if(agencyId < 1){
			res.code = -1;
			res.msg = "参数错误";
			
			renderJSON(res);
		}
		
		Event ev = isUse ? Event.AGENCY_DISABLED : Event.AGENCY_ENABLE;
		
		boolean upd= agencyService.updateAgencyStatus(agencyId, !isUse);
		if(!upd){
			LoggerUtil.error(true, "更新机构状态失败!");
			
			res.code = -1;
			res.msg = "更新机构状态失败!";
			renderJSON(res);
		}
		
		/** 添加管理员事件 */
		Map<String, String>param = new HashMap<String, String>();
		param.put("supervisor", ""+getCurrentSupervisorId());
		param.put("agencyId", ""+agencyId);
		param.put("agencyName", agencyName);
		boolean flag = supervisorService.addSupervisorEvent(getCurrentSupervisorId(), ev, param);
		
		if(!flag){
			LoggerUtil.error(true, "保存管理员事件失败!");
			res.code = -1;
			res.msg = "保存管理员事件失败";
			
			renderJSON(res);
		}
		
		res.code = 1;
		res.msg = "";
		res.obj = !isUse;
		
		renderJSON(res);
	}
	
	/**
	 * 上传图片
	 * 
	 * @param imgFile
	 * @param fileName
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年4月5日
	 */
	public static void uploadImage(File imgFile, String fileName){
		response.contentType="text/html";
		ResultInfo result = new ResultInfo();
		if (imgFile == null) {
			result.code = -1;
			result.msg = "请选择要上传的图片";
			
			renderJSON(result);
		}
		if(StringUtils.isBlank(fileName) || fileName.length() > 32){
			result.code = -1;
			result.msg = "图片名称长度应该位于1~32位之间";
			
			renderJSON(result);
		}
		
		result = FileUtil.uploadImgags(imgFile);
		if (result.code < 0) {

			renderJSON(result);
		}
		
		Map<String, Object> fileInfo = (Map<String, Object>) result.obj;
		fileInfo.put("imgName", fileName);
		
		renderJSON(result);
	}

}
