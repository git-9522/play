package daos.common;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daos.base.BaseDao;
import models.common.entity.t_experience_gold;

public class ExperienceGoldDao extends BaseDao<t_experience_gold>{
	
	protected ExperienceGoldDao(){};
	
	/**
	 * 查询所有体验金（可用）
	 *
	 * @return
	 *
	 * @author yuechuanyang
	 * @createDate 2017年10月17日
	 */
	public List<t_experience_gold> queryExperienceGold() {
		String sql="SELECT * FROM t_experience_gold WHERE is_use = 0";
		return this.findListBySQL(sql,new HashMap<String, Object>());
	}
	
	
	/**
	 * 查询所有体验金
	 *
	 * @return
	 *
	 * @author yuechuanyang
	 * @createDate 2017年10月17日
	 */
	public List<t_experience_gold> queryAllExperienceGold() {
		String sql="SELECT * FROM t_experience_gold";
		return this.findListBySQL(sql,new HashMap<String, Object>());
	}
	
	/**
	 * 增加体验金
	 *
	 * @return
	 *
	 * @author yuechuanyang
	 * @createDate 2017年10月17日
	 */
	public int saveExperienceGold(Date time,double amount,String remark,int is_use){
		String sql = "INSERT INTO t_experience_gold (time,amount,remark,is_use) VALUES (:time,:amount,:remark,:is_use)";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("time", time);
		condition.put("amount", amount);
		condition.put("remark", remark);
		condition.put("is_use", is_use);
		return this.updateBySQL(sql, condition);
	}
	
	/**
	 * 修改体验金
	 *
	 * @return
	 *
	 * @author yuechuanyang
	 * @createDate 2017年10月17日
	 */
	public int updateExperienceGold(long id,int is_use){
		String sql = "UPDATE t_experience_gold SET is_use = :is_use WHERE id =:id";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("id", id);
		condition.put("is_use", is_use);
		return this.updateBySQL(sql, condition);
	}
	
	
	/**
	 * 删除体验金
	 *
	 * @return
	 *
	 * @author yuechuanyang
	 * @createDate 2017年10月17日
	 */
	public int delExperienceGold(long id){
		String sql = "DELETE FROM t_experience_gold WHERE id = :id";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("id", id);
		return this.updateBySQL(sql, condition);
	}
}
