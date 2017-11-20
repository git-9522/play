package models.core.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 实体:合作机构
 * 
 * @author jiayijian
 * @createDate 2017年3月30日
 */
@Entity
public class t_agencies extends Model {
	
	/** 创建时间 */
	public Date time;
	
	/** 合作机构名称 */
	public String name;
	
	/** 机构简介 */
	public String introduction;
	
	/** 组织机构代码 */
	public String organization_code;
	
	/** 营业执照图片 */
	public String business_license_img;
	
	/** 累计发布标的 */
	public int bid_count;
	
	/** 成功的标的 */
	public int success_bid_count;
	
	/** 是否使用 */
	public boolean is_use;

	public t_agencies() {

	}
}
