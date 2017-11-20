package daos.ext.mall;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import common.constants.Constants;
import common.utils.PageBean;
import daos.base.BaseDao;
import models.ext.mall.entiey.t_mall_goods;

/**
 * 积分商城-积分商品Dao
 *
 * @description 
 *
 * @author jiayijian
 * @createDate 2017年3月18日
 */
public class GoodsDao extends BaseDao<t_mall_goods>{
	
	protected GoodsDao() {}
	
	/**
	 * 查询积分商品
	 * @param showType 商品类型 0-实物；1-虚拟
	 * @param numNo 编号
	 * @param goodsName 商品名称
	 * @param currPage
	 * @param pageSize
	 * @param exports 1:导出  default：不导出
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public PageBean<t_mall_goods> pageOfBackgoods(int showType,int currPage,int  pageSize,String numNo,String goodsName,int  exports){
		
		StringBuffer querySQL = new StringBuffer("select * from t_mall_goods where 1=1 ");
		StringBuffer countSQL = new StringBuffer("select count(id) from t_mall_goods where 1=1 ");
		
		Map<String, Object> args = new HashMap<String, Object>();
		
		if(showType == 1){
			querySQL.append(" and type = :type ");
			countSQL.append(" and type = :type ");
			args.put("type", t_mall_goods.GoodsType.VIRTUAL.code);
		}else if(showType == 0){
			querySQL.append(" and type = :type ");
			countSQL.append(" and type = :type ");
			args.put("type", t_mall_goods.GoodsType.ENTITY.code);
		} else {
			querySQL.append(" and type = :type ");
			countSQL.append(" and type = :type ");
			args.put("type", t_mall_goods.GoodsType.DRAW.code);
		}
		 
		/** 按编号搜索  */
		if(StringUtils.isNotBlank(numNo)){
			querySQL.append(" AND id = :goodsId");
			countSQL.append(" AND id = :goodsId");
			args.put("goodsId", numNo.trim());
		}
		
		/** 按商品名称搜索  */
		if(StringUtils.isNotBlank(goodsName)){
			querySQL.append(" AND name LIKE :name");
			countSQL.append(" AND name LIKE :name");
			args.put("name", "%"+goodsName.trim()+"%");
		}
		
		querySQL.append(" order by id desc ");
		
		if(exports == Constants.EXPORT){
			PageBean<t_mall_goods> pageBean = new PageBean<t_mall_goods>();
			pageBean.page = this.findListBySQL(querySQL.toString(), args);
		    
		    return pageBean;
		}
		
		return this.pageOfBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), args);
	}
	
	/**
	 * 前台-积分商城首页-查询商品
	 * @param showType 类型 0-实物；1-虚拟
	 * @param currPage
	 * @param pageSize
	 *
	 * @author jiayijian
	 * @createDate 2017年3月16日
	 *
	 */
	public PageBean<t_mall_goods> pageOfFrontGoodsPre(int showType,int currPage,int  pageSize){
		
		StringBuffer querySQL = new StringBuffer("select * from t_mall_goods where 1=1  ");
		StringBuffer countSQL = new StringBuffer("select count(id) from t_mall_goods where 1=1 ");
		
		Map<String, Object> args = new HashMap<String, Object>();
		
		querySQL.append(" and is_use = :isUse ");
		countSQL.append(" and is_use = :isUse ");
		args.put("isUse", true);
		
		if(showType == 1){
			querySQL.append(" and type = :type ");
			countSQL.append(" and type = :type ");
			
			args.put("type", t_mall_goods.GoodsType.VIRTUAL.code);
		}else{
			
			querySQL.append(" and type = :type ");
			countSQL.append(" and type = :type ");
			
			args.put("type", t_mall_goods.GoodsType.ENTITY.code);
		}
		
		querySQL.append(" order by id desc ");
		
		
		return this.pageOfBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), args);
	}
	
	/**
	 * 更新商品状态
	 *
	 * @param id 商品id
	 * @param isUse 上下架
	 * @return
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public int updateGoodsStatus(long id, boolean isUse) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("is_use", isUse);
		map.put("id", id);
		
		return this.updateBySQL("UPDATE t_mall_goods SET is_use=:is_use WHERE id=:id", map);
	}

	/**
	 *  更新库数量
	 * @param goodsId 商品id
	 * @param exchangeNum  兑换数量
	 * @param isUnlimited 库存数量是否无限制
	 * @return
	 */
	public int updateGoodsInventory(long goodsId,int exchangeNum,boolean isUnlimited){
		
		String sql = "";
		Map<String, Object> map = new HashMap<String, Object>();
		if(isUnlimited){
			
			sql = "UPDATE t_mall_goods SET exchanged_num=exchanged_num +:exchangeNum WHERE id=:id";
			
			map.put("exchangeNum", exchangeNum);
			map.put("id", goodsId);
		}else{
			
			sql = "UPDATE t_mall_goods SET exchanged_num=exchanged_num +:exchangeNum,inventory = inventory - :exchangeNum  WHERE id=:id and inventory >=  :exchangeNum ";

			map.put("exchangeNum", exchangeNum);
			map.put("id", goodsId);
			
		}
		
		return this.updateBySQL(sql, map);
	}
	
	
	/**
	 * APP商城首页展示虚拟产品分类
	 * @param showType 类型 0-实物；1-虚拟
	 * @param currPage
	 * @param pageSize
	 * @param attribute 物品属性0-默认值，1-红包（元），2-现金卷（元），3-加息卷（%）
	 *
	 *
	 */
	public PageBean<t_mall_goods> pageOfFrontSameGoods(int currPage,int  pageSize, int attribute){
		
		StringBuffer querySQL = new StringBuffer("select * from t_mall_goods where 1=1  ");
		StringBuffer countSQL = new StringBuffer("select count(id) from t_mall_goods where 1=1 ");
		
		Map<String, Object> args = new HashMap<String, Object>();
		
		querySQL.append(" and is_use = :isUse ");
		countSQL.append(" and is_use = :isUse ");
		args.put("isUse", true);
		
		querySQL.append(" and type = :type ");
		countSQL.append(" and type = :type ");
		args.put("type", t_mall_goods.GoodsType.VIRTUAL.code);
		
		
		if(attribute == 1){
			querySQL.append(" and attribute = :attribute ");
			countSQL.append(" and attribute = :attribute ");
			args.put("type", t_mall_goods.GoodsAttr.REDPACKET.code);
			
		}else if(attribute == 2){
			querySQL.append(" and attribute = :attribute ");
			countSQL.append(" and attribute = :attribute ");
			args.put("type", t_mall_goods.GoodsAttr.CASH.code);
			
		}else if(attribute == 3){
			querySQL.append(" and attribute = :attribute ");
			countSQL.append(" and attribute = :attribute ");
			args.put("type", t_mall_goods.GoodsAttr.VOLUME.code);
			
		}
		
		querySQL.append(" order by id desc ");
		
		
		return this.pageOfBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), args);
	}
}
