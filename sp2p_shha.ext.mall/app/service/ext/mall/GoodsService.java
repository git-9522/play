package service.ext.mall;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import daos.ext.mall.GoodsDao;
import models.ext.mall.entiey.t_mall_goods;
import models.ext.mall.entiey.t_mall_goods.GoodsType;
import services.base.BaseService;

/**
 * 积分商城-积分商品Service
 *
 * @author jiayijian
 * @createDate 2017年3月18日
 */
public class GoodsService extends BaseService<t_mall_goods>{
	
	protected GoodsDao goodsDao = Factory.getDao(GoodsDao.class);
	
	protected GoodsService() {
		super.basedao = goodsDao;
	}
	
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
	public PageBean<t_mall_goods> pageOfBackgoods(int showType,int currPage,int  pageSize,String numNo,String goodsName,int exports){
		
		return goodsDao.pageOfBackgoods(showType,currPage, pageSize,numNo,goodsName, exports);
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
		
		return goodsDao.pageOfFrontGoodsPre(showType,currPage, pageSize);
	}
	
	
	/**
	 * 校验商品参数
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public ResultInfo checkGoodsValue(t_mall_goods goods){
		ResultInfo result = new ResultInfo();
		
		if(goods.type > 2 || goods.type < 0){
			
			result.code = -1;
			result.msg = "商品类型有错误";
			
			return result;
		}
		
		if(StringUtils.isBlank(goods.name) || goods.name.length() > 10){
			
			result.code = -1;
			result.msg = "商品名称为空或名称过长";
			
			return result;
		}
		
		if(StringUtils.isBlank(goods.image_url)){
			
			result.code = -1;
			result.msg = "商品图片为空";
			
			return result;
		}
		
		if(goods.type != 2 && !goods.is_unlimited_inven && goods.inventory <= 0){
			
			result.code = -1;
			result.msg = "请设置库存数量上限";
			
			return result;
		}
		
		if(goods.type != 2 && goods.inventory < 0){
			result.code = -1;
			result.msg = "库存数量错误";
			
			return result;
		}
		
		if(goods.type != 2 && !goods.is_unlimited_exc_max && goods.exchange_maximum <= 0){
			
			result.code = -1;
			result.msg = "请设置兑换数量上限";
			
			return result;
		}
		
		if(goods.type != 2 && goods.exchange_maximum < 0){
			result.code = -1;
			result.msg = "兑换数量上限错误";
			
			return result;
		}
		
		//虚拟商品特有参数
		if(goods.type == 1){
			
			if(goods.attribute < 1 || goods.attribute > 3){
				
				result.code = -1;
				result.msg = "商品属性错误";
				
				return result;
			}
			
			if(goods.attribute == 3 && (goods.attribute_value < 0.1 || goods.attribute_value > 100)){
				
				result.code = -1;
				result.msg = "商品属性加息卷范围是0.1~100";
				
				return result;
			}
			
			if(goods.attribute_value  <= 0){
				
				result.code = -1;
				result.msg = "商品属性参数设置错误";
				
				return result;
			}
			
			if(goods.min_invest_amount  <= 0){
				
				result.code = -1;
				result.msg = "最低投资限制错误";
				
				return result;
			}
			
			if(goods.limit_day  <= 0){
				
				result.code = -1;
				result.msg = "有效期限错误";
				
				return result;
			}
			
		}
		
		if(goods.type != 2 && goods.spend_scroe <= 0){
			result.code = -1;
			result.msg = "兑换消耗积分错误";
			
			return result;
		}
		
		if(StringUtils.isBlank(goods.description)){
			
			result.code = -1;
			result.msg = "商品介绍为空";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "商品参数正常";
		return result;
	}
	
	/**
	 * 添加积分商品
	 * 
	 * @param goods 
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public ResultInfo addGoods(t_mall_goods goods){
		
		ResultInfo result = new ResultInfo();
		
		t_mall_goods gd = new t_mall_goods();
		gd.time = new Date();
		gd.name = goods.name;
		gd.type = goods.type;
		gd.spend_scroe = goods.spend_scroe;
		
		gd.is_unlimited_inven = goods.is_unlimited_inven;
		gd.inventory = goods.is_unlimited_inven ? 0:goods.inventory;
		gd.is_unlimited_exc_max = goods.is_unlimited_exc_max;
		gd.exchange_maximum =goods.is_unlimited_exc_max? 0:goods.exchange_maximum;
		
		gd.image_url = goods.image_url;
		gd.description = goods.description;
		gd.min_invest_count = goods.min_invest_count;
		gd.is_use = false;//0-下架
		//虚拟商品特有参数
		if(goods.type == t_mall_goods.GoodsType.VIRTUAL.code){
			
			gd.attribute = goods.attribute;
			gd.attribute_value = goods.attribute_value;
			gd.min_invest_amount = goods.min_invest_amount;
			gd.limit_day = goods.limit_day;
		}
		
		if(!goodsDao.save(gd)){
			
			result.code = -1;
			result.msg = "保存商品失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "保存商品成功";
				
		return result;
	}
	
	
	/**
	 * 编辑积分商品
	 * 
	 * @param goods 编辑参数
	 * @param gd 旧参数
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public ResultInfo editGoods(t_mall_goods goods,t_mall_goods gd){
		
		ResultInfo result = new ResultInfo();
		
		gd.last_edit_time = new Date();
		gd.name = goods.name;
		gd.type = goods.type;
		gd.spend_scroe = goods.spend_scroe;
		
		gd.is_unlimited_inven = goods.is_unlimited_inven;
		gd.inventory = goods.is_unlimited_inven ? 0:goods.inventory;
		gd.is_unlimited_exc_max = goods.is_unlimited_exc_max;
		gd.exchange_maximum =goods.is_unlimited_exc_max? 0:goods.exchange_maximum;
		
		gd.image_url = goods.image_url;
		gd.description = goods.description;
		gd.min_invest_count = goods.min_invest_count;
		//虚拟商品特有参数
		if(goods.type == t_mall_goods.GoodsType.VIRTUAL.code){
			
			gd.attribute = goods.attribute;
			gd.attribute_value = goods.attribute_value;
			gd.min_invest_amount = goods.min_invest_amount;
			gd.limit_day = goods.limit_day;
		}
		
		if(!goodsDao.save(gd)){
			
			result.code = -1;
			result.msg = "编辑商品失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "编辑商品成功";
				
		return result;
	}
	

	/**
	 * 通过id更新产品的上下架状态
	 *
	 * @param productId
	 * @param isUse 1-上架产品 ；0-下架产品
	 * @return
	 *
	 * @author jiayijian
	 * @createDate 2017年3月21日
	 */
	public boolean updateGoodsStatus(long productId, boolean isUse){
		
		int i = goodsDao.updateGoodsStatus(productId, isUse);
		if (i!=1) {
			
			return false;
		}
		
		return true;
	}
	
	/**
	 * 积分商品 商品 删除
	 *
	 * @param id 商品id
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月28日
	 */
	public boolean delGoods(long id) {
		int row = goodsDao.delete(id);
		if(row<=0){
			
			return false;
		}
		
		return true;
	}
	
	/**
	 *  更新库数量
	 * @param goodsId 商品id
	 * @param exchangeNum  兑换数量
	 * @param isUnlimited 库存数量是否无限制
	 * @return
	 */
	public int updateGoodsInventory(long goodsId,int exchangeNum,boolean isUnlimited){
		return goodsDao.updateGoodsInventory(goodsId,exchangeNum,isUnlimited);
	}
	
	
	/**
	 * APP商城首页展示产品分类
	 * @param showType 商品类型 0-实物；1-虚拟
	 * @param attribute 物品属性0-默认值，1-红包（元），2-现金卷（元），3-加息卷（%）
	 *
	 *
	 */
	public List<t_mall_goods> findSameGoods(int showType){
		
		return goodsDao.findListByColumn("type = ? and is_use = ?", showType, true);
	}

	/**
	 * 微信商城首页展示虚拟产品分类
	 * @param attribute 物品属性0-默认值，1-红包（元），2-现金卷（元），3-加息卷（%）
	 *
	 *
	 */
	public List<t_mall_goods> findVirtualGoods(int attribute){
		
		return goodsDao.findListByColumn("type = ? and attribute = ? and is_use = ?", GoodsType.VIRTUAL.code, attribute, true);
	}

	public List<t_mall_goods> findListByType(int type, boolean isUsed) {
		return goodsDao.findListByColumn("type = ? and is_use = ?", type, isUsed);
	}
	
}
