package services.core;

import java.util.HashMap;
import java.util.List;

import common.utils.Factory;
import common.utils.PageBean;
import daos.core.ProductDao;
import models.core.bean.FrontProduct;
import models.core.entity.t_product;
import models.core.entity.t_product.ProductWebType;
import services.base.BaseService;

public class ProductService extends BaseService<t_product> {

	private ProductDao productDao;

	protected ProductService() {

		productDao = Factory.getDao(ProductDao.class);
		super.basedao = productDao;

	}

	/**
	 * 添加借款产品
	 *
	 * @param tp
	 *            产品对象
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月26日
	 */
	public boolean createProduct(t_product tp) {

		return productDao.save(tp);
	}

	/**
	 * 通过id更新产品的上下架状态
	 *
	 * @param productId
	 * @param isUse
	 *            1-上架产品 ；0-下架产品
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月9日
	 */
	public boolean updateProductStatus(long productId, boolean isUse) {

		int i = productDao.updateProductStatus(productId, isUse);
		if (i != 1) {

			return false;
		}

		return true;
	}

	/**
	 * Ajax异步校验产品名称已经存在
	 *
	 * @param name
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月26日
	 */
	public boolean checkHasProduct(String name) {

		List<t_product> list = productDao.queryProductByName(name);
		if (list == null || list.size() == 0) {

			return false;
		}

		return true;
	}

	/**
	 * 根据id获取产品信息
	 *
	 * @param id
	 *            产品id
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月26日
	 */
	public t_product queryProduct(long id) {

		return super.findByID(id);
	}

	/**
	 * 查询借款产品列表
	 *
	 * @param pageSize
	 *            页大小
	 * @param currPage
	 *            当前页
	 * @param status
	 *            上下架状态
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月26日
	 */
	public PageBean<t_product> pageOfProduct(int pageSize, int currPage, Boolean status) {

		return productDao.pageOfProduct(pageSize, currPage, status);
	}

	/**
	 * 前台-借款页面-返回上架状态产品列表（限10条）
	 * 
	 * @param isNeedNewbieProduct
	 *            是否需要展示新手借款产品
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月7日
	 */
	public List<FrontProduct> queryProductIsUse(boolean isNeedNewbieProduct) {

		return productDao.queryProductIsUse(isNeedNewbieProduct);
	}

	/**
	 * 通过产品类型查询产品
	 * 
	 * @param type
	 * @return
	 */
	public t_product queryProductByType(int type) {

		return productDao.queryProductByType(type);
	}

	/**
	 * 查询借款产品关联的借款标数量
	 * 
	 * @param productId
	 * @return
	 */
	public int countRelevanceBid(long productId) {

		return productDao.countRelevanceBid(productId);
	}

	public HashMap<String,String> queryWebProduct() {
		HashMap<String, String> map =new HashMap<String, String>();
		PageBean<t_product> lists = productDao.pageOfProduct(100,1,true);
		for (t_product t : lists.page) {
			for (ProductWebType p : ProductWebType.values()) {
				List<Integer> s=ProductWebType.getProductWebTypeList(p.code);
				if(s.contains(t.getType().code)){
					map.put(t.id+"", p.value);
				}
				
			}
		}
		return map;
	}

}
