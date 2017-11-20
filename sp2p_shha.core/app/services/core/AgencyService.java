package services.core;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import daos.core.AgencyDao;
import models.core.bean.BackRiskAgency;
import models.core.entity.t_agencies;
import services.base.BaseService;

/**
 * 合作机构表Service
 *
 * @author jiayijian
 * @createDate 2017年3月30日
 */
public class AgencyService extends BaseService<t_agencies>{
	
	protected AgencyDao agencyDao;
	
	protected AgencyService() {
		agencyDao = Factory.getDao(AgencyDao.class);
		super.basedao = agencyDao;
	}
	
	/**
	 * 后台-风控-合作机构
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
	 */
	public PageBean<BackRiskAgency> pageOfAgencyRisk( int currPage, int pageSize, int exports,String userName,String numNo) {
		
		return agencyDao.pageOfAgencyRisk(currPage, pageSize, exports, userName, numNo);
	}
	
	
	/**
	 * 合作机构 校验参数
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
	public ResultInfo checkAgencyValue(String name,String business_license_img,String organization_code,String introduction){
		
		ResultInfo result = new ResultInfo();
		if(StringUtils.isBlank(name)){
			
			result.code = -1;
			result.msg = "请填写机构名称";
			return result;
		}
		
		if(name.length() > 20){
			
			result.code = -1;
			result.msg = "机构名称应小于20个字符";
			return result;
		}
		
//		if(StringUtils.isBlank(business_license_img)){
//			
//			result.code = -1;
//			result.msg = "请上传营业执照图片";
//			return result;
//		}
//		
//		if(StringUtils.isBlank(organization_code)){
//			
//			result.code = -1;
//			result.msg = "请填写组织机构代码 ";
//			return result;
//		}	
		
		if(StringUtils.isNotBlank(introduction)){
			
			if(introduction.length() > 500){
				
				result.code = -1;
				result.msg = "机构介绍应小于500个字符";
				return result;
			}
		}
		
		result.code = 1;
		return result;
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
	public ResultInfo addAgency(String name,String business_license_img,String organization_code,String introduction){
		ResultInfo result = new ResultInfo();
		
		t_agencies agency = new t_agencies();
		
		agency.time = new Date();
		agency.name = name;
		agency.introduction = introduction;
		agency.organization_code = organization_code;
		agency.business_license_img = business_license_img;
		agency.is_use = false;//不使用
		
		if(!agencyDao.save(agency)){
			
			result.code = -1;
			result.msg = "保存机构失败";
			return result;
		}
		result.obj = agency;
		result.code = 1;
		result.msg = "保存机构成功";
		return result;
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
	public ResultInfo editAgency(long agencyId,String name,String business_license_img,String organization_code,String introduction){
		ResultInfo result = new ResultInfo();
		
		t_agencies agency = agencyDao.findByID(agencyId);
		if(agency == null){
			
			result.code = -1;
			result.msg = "没有找到该合作机构";
			return result;
		}
		
		agency.name = name;
		agency.introduction = introduction;
		agency.organization_code = organization_code;
		agency.business_license_img = business_license_img;
		
		if(!agencyDao.save(agency)){
			
			result.code = -1;
			result.msg = "保存机构失败";
			return result;
		}
		
		result.code = 1;
		result.msg = "保存机构成功";
		return result;
	}

	/**
	 * 通过id更新作机构的上下架状态
	 *
	 * @param agencyId
	 * @param isUse 1-上架产品 ；0-下架产品
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月9日
	 */
	public boolean updateAgencyStatus(long agencyId, boolean isUse){
		
		int i = agencyDao.updateAgencyStatus(agencyId, isUse);
		if (i!=1) {
			
			return false;
		}
		
		return true;
	}
	
	/**
	 * 查询所有启用的合作机构
	 * @param isUse
	 * @return
	 * @author jiayijian
	 * @createDate 2017年3月28日
	 */
	public List<t_agencies> queryAgencyIsUse(boolean isUse){
		
		return agencyDao.queryAgencyIsUse(isUse);
	}
} 
