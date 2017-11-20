package daos.ext.mall;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import common.constants.Constants;
import common.utils.PageBean;
import daos.base.BaseDao;
import models.ext.mall.entiey.t_mall_rewards;

/**
 * 积分商城-奖品Dao
 *
 * @description 
 *
 * @author jiayijian
 * @createDate 2017年3月18日
 */
public class RewardsDao extends BaseDao<t_mall_rewards>{
	
	protected RewardsDao() {}
	
	/**
	 * 查询奖品
	 * @param numNo 编号
	 * @param rewardName 奖品名称
	 * @param currPage
	 * @param pageSize
	 * @param exports 1:导出  default：不导出
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public PageBean<t_mall_rewards> pageOfBackRewards(int currPage,int  pageSize,String numNo,String rewardName,int  exports){
		
		StringBuffer querySQL = new StringBuffer("select * from t_mall_rewards where 1=1 ");
		StringBuffer countSQL = new StringBuffer("select count(id) from t_mall_rewards where 1=1 ");
		
		Map<String, Object> args = new HashMap<String, Object>();
		
		/** 按编号搜索  */
		if(StringUtils.isNotBlank(numNo)){
			querySQL.append(" AND id = :rewardsId");
			countSQL.append(" AND id = :rewardsId");
			args.put("rewardsId", numNo.trim());
		}
		
		/** 按奖品名称搜索  */
		if(StringUtils.isNotBlank(rewardName)){
			querySQL.append(" AND name LIKE :name");
			countSQL.append(" AND name LIKE :name");
			args.put("name", "%"+rewardName.trim()+"%");
		}
		
		querySQL.append(" order by id desc ");
		
		if(exports == Constants.EXPORT){
			PageBean<t_mall_rewards> pageBean = new PageBean<t_mall_rewards>();
			pageBean.page = this.findListBySQL(querySQL.toString(), args);
		    
		    return pageBean;
		}
		
		return this.pageOfBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), args);
	}
	
	/**
	 * 更新奖品状态
	 *
	 * @param id 奖品id
	 * @param isUse 上下架
	 * @return
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public int updateRewardsStatus(long id, boolean isUse) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("is_use", isUse);
		map.put("id", id);
		
		return this.updateBySQL("UPDATE t_mall_rewards SET is_use=:is_use WHERE id=:id", map);
	}
	
	/**
	 *查询所有上架的奖品
	 * 
	 * @return
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public List<t_mall_rewards> queryALlRewarsIsUse(){
		
		return this.findListByColumn(" is_use = 1");
	}
	
	/**
	 *查询最新上架的8个奖品
	 * 
	 * @return
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public List<t_mall_rewards> queryFrontNewRewarsIsUse(){
		
		String sql = "select * from t_mall_rewards where is_use= :is_use order by id desc limit :limit";
		
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("is_use",true);
		args.put("limit",8);
		
		return this.findListBySQL(sql, args);
	}
	
	/**
	 *查询所有上架的奖品总概率
	 * 
	 * @return
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public double queryTotalProbability(){
		
		String sql = "select IFNULL(SUM(probability),0) from t_mall_rewards where is_use= :is_use ";
		
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("is_use",true);
		
		return this.findSingleDoubleBySQL(sql, 0, args);
	}
	
 
}
