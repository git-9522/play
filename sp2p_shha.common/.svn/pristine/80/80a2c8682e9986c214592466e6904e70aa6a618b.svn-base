package services.common;

import java.util.Date;
import java.util.List;

import common.utils.Factory;
import daos.common.ExperienceGoldDao;
import models.common.entity.t_experience_gold;
import services.base.BaseService;

public class ExperienceGoldService extends BaseService<t_experience_gold>{
	
    protected static ExperienceGoldDao experienceGoldDao = Factory.getDao(ExperienceGoldDao.class); 
	
	protected ExperienceGoldService() {
		super.basedao = this.experienceGoldDao;
	}
	
	
	/**
	 * 前台-查询体验金（目前只取第一个可用的体验金）
	 * @return
	 * 
	 * @author yuechuanyang
	 * @createDate 2017年10月17日
	 */
	public t_experience_gold queryExperienceGold(){
		List<t_experience_gold>  list = experienceGoldDao.queryExperienceGold();
		if(list != null && list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	
	/**
	 * 后台-查询体验金全部
	 * @return
	 * 
	 * @author yuechuanyang
	 * @createDate 2017年10月19日
	 */
	public List<t_experience_gold> queryAllExperienceGold(){
		List<t_experience_gold>  list = experienceGoldDao.queryAllExperienceGold();
		return list;
	}
	
	/**
	 * 后台-新增体验金
	 * @return
	 * 
	 * @author yuechuanyang
	 * @createDate 2017年10月19日
	 */
	public int saveExperienceGold(Date time,double amount,String remark,int is_use){
		return this.saveExperienceGold( time, amount, remark, is_use);
	}
	
	/**
	 * 后台-修改体验金
	 * @return
	 * 
	 * @author yuechuanyang
	 * @createDate 2017年10月19日
	 */
	public int updateExperienceGold(long id,int is_use){
		return this.updateExperienceGold(id,is_use);
	}
	
	/**
	 * 后台-删除体验金
	 * @return
	 * 
	 * @author yuechuanyang
	 * @createDate 2017年10月19日
	 */
	public int delExperienceGold(long id){
		return this.delExperienceGold(id);
	}
}
