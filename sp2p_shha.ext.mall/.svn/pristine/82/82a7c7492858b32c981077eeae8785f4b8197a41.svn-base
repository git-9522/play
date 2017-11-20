package daos.ext.mall;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import common.utils.PageBean;
import daos.base.BaseDao;
import models.ext.mall.bean.BackExchanges;
import models.ext.mall.bean.UserExchanges;
import models.ext.mall.entiey.t_mall_exchange;

/**
 * 积分商城-实物兑换Dao
 *
 * @description 
 *
 * @author jiayijian
 * @createDate 2017年3月18日
 */
public class ExchangeDao extends BaseDao<t_mall_exchange> {
	
	protected ExchangeDao(){};
	
	/**
	 * 查询兑换记录
	 * @param numNo 编号
	 * @param goodsName 商品名称
	 * @param currPage
	 * @param pageSize
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public PageBean<BackExchanges> pageOfBackExchanges(int showType,int currPage,int  pageSize,String numNo,String goodsName){
		
		StringBuffer querySQL = new StringBuffer("select e.id as id, u.`name` as user_name,e.`name` as goods_name,e.time as exchange_time,e.exchange_num as exchange_num, e.spend_scroe as spend_scroe,e.address as address ,e.`status` as status from t_mall_exchange e LEFT JOIN t_user u on e.user_id = u.id where 1=1  ");
		StringBuffer countSQL = new StringBuffer("select count(e.id) from t_mall_exchange e LEFT JOIN t_user u ON e.user_id = u.id where 1=1 ");
		
		Map<String, Object> args = new HashMap<String, Object>();
		
		querySQL.append(" AND e.type = :type ");
		countSQL.append(" AND e.type = :type ");
		args.put("type", showType);
		
		/** 按编号搜索  */
		if(StringUtils.isNotBlank(numNo)){
			querySQL.append(" AND e.id = :goodsId");
			countSQL.append(" AND e.id = :goodsId");
			args.put("goodsId", numNo.trim());
		}
		
		/** 按商品名称搜索  */
		if(StringUtils.isNotBlank(goodsName)){
			querySQL.append(" AND e.name LIKE :name");
			countSQL.append(" AND e.name LIKE :name");
			args.put("name", "%"+goodsName.trim()+"%");
		}
		
		querySQL.append(" order by e.id desc ");
		
		return this.pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), BackExchanges.class, args);
	}
	
	
	/**
	 * 查询用户兑换记录
	 * @param userid 
	 * @param currPage
	 * @param pageSize
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public PageBean<UserExchanges> pageOfExchangesByUser(int currPage,int  pageSize,long userId){
		
		StringBuffer querySQL = new StringBuffer("select e.id as id, e.`name` as goods_name,e.time as exchange_time,e.exchange_num as exchange_num, e.spend_scroe as spend_scroe,e.address as address ,e.`status` as status,e.image_url as image_url from t_mall_exchange e LEFT JOIN t_user u on e.user_id = u.id where 1=1  ");
		StringBuffer countSQL = new StringBuffer("select count(e.id) from t_mall_exchange e LEFT JOIN t_user u ON e.user_id = u.id where 1=1 ");
		
		Map<String, Object> args = new HashMap<String, Object>();
		
		querySQL.append(" AND e.user_id = :userId ");
		countSQL.append(" AND e.user_id = :userId ");
		args.put("userId", userId);
		
		querySQL.append(" AND e.type = :type ");
		countSQL.append(" AND e.type = :type ");
		args.put("type", t_mall_exchange.GoodsType.ENTITY.code);
		
		querySQL.append(" order by e.id desc ");
		
		return this.pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), UserExchanges.class, args);
	}
	
	/**
	 * 查询用户兑换记录
	 * @param userid 
	 * @param excId
	 * @param pageSize
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public t_mall_exchange findUserExchangeByid(long userid,long excId){
		
		return this.findByColumn(" id = ? and user_id = ? ", excId,userid);
	}
	
	/**
	 * 更新兑换记录状态
	 * @param id 记录id
	 * @param express_company 物流公司
	 * @param tracking_number 快递订单号
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public int updateExchangeStatus (long id,String express_company,String tracking_number){
		
		String sql = "update t_mall_exchange set express_company = :company,tracking_number = :number,delivery_time = now(),status = :status where id = :id and status = :oldStatus ";
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("company", express_company);
		args.put("number", tracking_number);
		args.put("status", t_mall_exchange.Status.DELIVERED.code);
		args.put("id", id);
		args.put("oldStatus", t_mall_exchange.Status.TOBEDELIVERED.code);
		
		return this.updateBySQL(sql, args);
	}

	/**
	 * 统计个人已兑换数量
	 * @param goodsId 记录id
	 * @param userId 物流公司
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	
	public int userHasExchange(long userId,long goodsId){
		
		return this.countByColumn(" user_id = ? and goods_id = ?", userId,goodsId);
	}
	
}
