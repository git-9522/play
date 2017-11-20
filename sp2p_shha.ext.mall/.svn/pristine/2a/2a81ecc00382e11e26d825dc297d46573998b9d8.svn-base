package daos.ext.mall;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daos.base.BaseDao;
import models.ext.mall.bean.FrontLottery;
import models.ext.mall.entiey.t_mall_lottery;

/**
 * 积分商城-中奖记录Dao
 *
 * @description 
 *
 * @author jiayijian
 * @createDate 2017年3月18日
 */
public class LotteryDao extends BaseDao<t_mall_lottery>{
	
	protected LotteryDao() {}
	
	/**
	 *查询最新的7个中奖记录
	 * 
	 * @return
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public List<FrontLottery> queryFrontLottery(){
		
		String sql = "select m.id as id,m.time as time,m.`name` as reward_name,u.`name` as user_name from t_mall_lottery m LEFT JOIN t_user u on u.id = m.user_id where 1=1 order by m.time desc  ";
		
		Map<String, Object> args = new HashMap<String, Object>();
		
		return this.findListBeanBySQL(sql, FrontLottery.class, args);
	}
	
	
 
}
