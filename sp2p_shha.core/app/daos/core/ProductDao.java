package daos.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.utils.PageBean;
import daos.base.BaseDao;
import models.core.bean.FrontProduct;
import models.core.entity.t_bid.Status;
import models.core.entity.t_product;
import models.core.entity.t_product.ProductType;

/**
 * 借款产品DAO
 *
 * @description 
 *
 * @author yaoyi
 * @createDate 2016年3月7日
 */
public class ProductDao extends BaseDao<t_product> {

	protected ProductDao() {}

	/**
	 * 更新产品状态
	 *
	 * @param id 产品id
	 * @param isUse 上下架
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月16日
	 */
	public int updateProductStatus(long id, boolean isUse) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("is_use", isUse);
		map.put("id", id);
		
		return this.updateBySQL("UPDATE t_product SET is_use=:is_use WHERE id=:id", map);
	}

	/**
	 * 分页查询借款产品列
	 *
	 * @param pageSize 页大小
	 * @param currPage 当前页
	 * @param status 借款产品状态
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月17日
	 */
	public PageBean<t_product> pageOfProduct(int pageSize, int currPage, Boolean status) {
		
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(id) FROM t_product ");
		StringBuffer querySQL = new StringBuffer("SELECT * FROM t_product ");
		
		Map<String, Object> conditionArgs = new HashMap<String, Object>();
		if(status != null){
			countSQL.append(" WHERE is_use=:status");
			querySQL.append(" WHERE is_use=:status");
			conditionArgs.put("status", status);
		}
		querySQL.append(" ORDER BY order_time DESC");
				
		return super.pageOfBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), conditionArgs);
	}

	/**
	 * 根据产品名称获取产品
	 *
	 * @param name 借款产品名称
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月28日
	 */
	public List<t_product> queryProductByName(String name) {
		
		String sql = "SELECT * FROM t_product WHERE name=:name";
		Map<String, Object>params = new HashMap<String, Object>();
		params.put("name", name);
		
		return findListBySQL(sql, params);
	}

	/**
	 * 前台-借款-返回上架状态产品列表，只取前10个
	 * 
	 * @param isNeedNewbieProduct 是否需要展示新手借款产品
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月7日
	 */
	public List<FrontProduct> queryProductIsUse(boolean isNeedNewbieProduct) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("is_use", true);
		
		String sql = null;
		
		if (isNeedNewbieProduct) {
			sql = "SELECT id, name, image_url, max_amount FROM t_product WHERE is_use = :is_use ORDER BY order_time DESC LIMIT 10";
		} else {
			sql = "SELECT id, name, image_url, max_amount FROM t_product WHERE is_use = :is_use and type <> :productType ORDER BY order_time DESC LIMIT 10";
			args.put("productType", ProductType.NEWBIE.code);
		}
		
		return super.findListBeanBySQL(sql, FrontProduct.class, args);
	}
	
	/**
	 * 通过产品类型查询产品
	 * @param type
	 * @return
	 */
	public t_product queryProductByType(int type) {
		
		return findByColumn(" type = ? ", type);
	}
	
	/**
	 * 查询借款产品关联的借款标数量
	 * @param productId
	 * @return
	 */
	public int countRelevanceBid(long productId) {
		String sql = "select count(tb.id) from t_bid tb inner join t_product tp on tb.product_id = tp.id where tp.id = :productId and tb.status IN (:statusList)";
		
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("productId", productId);
		args.put("statusList", Status.PROCESS);
		
		return super.countBySQL(sql, args);
	}
}
