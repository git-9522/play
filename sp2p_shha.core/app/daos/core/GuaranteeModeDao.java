package daos.core;

import java.util.List;

import daos.base.BaseDao;
import models.core.entity.t_guarantee_mode;

public class GuaranteeModeDao extends BaseDao<t_guarantee_mode> {

	protected GuaranteeModeDao(){}
	
	public List<t_guarantee_mode> getAll(){
		String sql="select * from t_guarantee_mode";
		
		return this.findListBySQL(sql,null);
		
	}
	public t_guarantee_mode getGuaranteeMode(Long id){
		return this.findByID(id);
	}
}
