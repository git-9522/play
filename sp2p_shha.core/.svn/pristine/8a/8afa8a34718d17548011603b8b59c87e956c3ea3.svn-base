package daos.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import common.constants.Constants;
import common.utils.PageBean;
import daos.base.BaseDao;
import models.core.bean.BackRiskAgency;
import models.core.entity.t_agencies;

/**
 * 合作机构表DAO
 *
 * @description 
 *
 * @author jiayijian
 * @createDate 2017年3月30日
 */
public class AgencyDao extends BaseDao<t_agencies>{
	
	protected AgencyDao(){}
	
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
		
		/**
		SELECT 
			a.id, 
			a.name, 
			a.business_license_img, 
			a.organization_code, 
			( SELECT COUNT(*) FROM t_bid WHERE agency_id = a.id ) AS bid_count, 
			( SELECT COUNT(*) FROM t_bid WHERE agency_id = a.id AND `status` IN (4, 5)) AS success_bid_count, 
			a.time,
			a.is_use 
		FROM t_agencies a 
		WHERE 
			1 = 1
		 */
		StringBuffer querySQL = new StringBuffer("SELECT a.id, a.name, a.business_license_img, a.organization_code, ( SELECT COUNT(*) FROM t_bid WHERE agency_id = a.id ) AS bid_count, ( SELECT COUNT(*) FROM t_bid WHERE agency_id = a.id AND `status` IN (4, 5)) AS success_bid_count, a.time, a.is_use FROM t_agencies a WHERE 1 = 1 ");
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(*) FROM t_agencies a WHERE 1 = 1 ");
		
		Map<String, Object> conditionArgs = new HashMap<String, Object>();
		
		/** 按编号搜索  */
		if(StringUtils.isNotBlank(numNo)){
			querySQL.append(" AND a.id LIKE :bidId");
			countSQL.append(" AND a.id LIKE :bidId");
			conditionArgs.put("bidId", "%"+numNo+"%");
		}
		
		/** 按机构名称搜索  */
		if(StringUtils.isNotBlank(userName)){
			querySQL.append(" AND a.name LIKE :userName");
			countSQL.append(" AND a.name LIKE :userName");
			conditionArgs.put("userName", "%"+userName+"%");
		}
		
		querySQL.append(" order by id desc ");
		
		if(exports == Constants.EXPORT){
			PageBean<BackRiskAgency> page = new PageBean<BackRiskAgency>();
			page.page = this.findListBeanBySQL(querySQL.toString(), BackRiskAgency.class, conditionArgs);
			return page;
		}
		
		return pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), BackRiskAgency.class, conditionArgs);
	}
	
	/**
	 * 更新机构状态
	 *
	 * @param id 机构id
	 * @param isUse 上下架
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月16日
	 */
	public int updateAgencyStatus(long id, boolean isUse) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("is_use", isUse);
		map.put("id", id);
		
		return this.updateBySQL("UPDATE t_agencies SET is_use=:is_use WHERE id=:id", map);
	}
	
	/**
	 * 查询所有启用的合作机构
	 * @param isUse
	 * @return
	 * @author jiayijian
	 * @createDate 2017年3月28日
	 */
	public List<t_agencies> queryAgencyIsUse(boolean isUse){
		
		return this.findListByColumn(" is_use = ? order by id desc ", isUse);
	}

}
