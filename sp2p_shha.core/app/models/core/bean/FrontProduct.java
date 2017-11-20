package models.core.bean;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import common.utils.Factory;
import services.core.ProductService;

/**
 * 前台借款产品
 * @author YanPengFei
 */
@Entity
public class FrontProduct {
	
	/** 产品ID */
	@Id
	public long id;
	
	/** 产品名称 */
	public String name;
	
	/** 产品图片 */
	public String image_url;
	
	/** 借款额度上限 */
	public double max_amount;
	
	/** 关联的借款标数量 */
	@Transient
	public int total_bid;
	
	public int getTotal_bid() {
		
		return Factory.getService(ProductService.class).countRelevanceBid(this.id);
	}
	
}
