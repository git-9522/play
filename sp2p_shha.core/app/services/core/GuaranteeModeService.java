package services.core;

import java.util.List;

import common.utils.Factory;
import daos.core.GuaranteeModeDao;
import models.core.entity.t_guarantee_mode;
import services.base.BaseService;

public class GuaranteeModeService extends BaseService<t_guarantee_mode> {

	protected static GuaranteeModeDao guaranteeModeDao=Factory.getDao(GuaranteeModeDao.class);
	
	protected GuaranteeModeService(){
		super.basedao=guaranteeModeDao;
		
	}
	/**获取所有保障方式条目*/
	public List<t_guarantee_mode> getAll(){
		List<t_guarantee_mode> findAll = guaranteeModeDao.getAll();
		return findAll;
		
	}
	/**获取保障方式*/
	public String getGuaranteeMode(Long id){
		t_guarantee_mode guaranteeMode = guaranteeModeDao.findByID(id);
		return guaranteeMode==null?null:guaranteeMode.name;
	}
}
