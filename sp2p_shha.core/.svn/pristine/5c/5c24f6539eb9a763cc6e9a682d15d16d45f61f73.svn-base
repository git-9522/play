package models.core.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Security;

/**
 * 后台-风控-合作机构列表的bean
 *
 * @description 
 *
 * @author jiayijian
 * @createDate 2017年3月28日
 */
@Entity
public class BackRiskAgency implements Serializable{

	@Id
	public long id;
	
	/** 机构名称 */
	public String name;
	
	/** 营业执照图片 */
	public String business_license_img;
	
	/** 组织机构代码 */
	public String organization_code;
	
	/** 累计发布标的 */
	public int bid_count;
	
	/** 成功的标的 */
	public int success_bid_count;
	
	/** 合作时间 */
	public Date time;
	
	/** 是否使用 */
	public int is_use;
	
	@Transient
	public String sign;
	public String getSign() {
		String signID = Security.addSign(id, Constants.BID_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
		
		return signID;
	}
	
	public BackRiskAgency(){}
	
}
