package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.enums.PactType;
import common.utils.Security;
import play.db.jpa.Model;

/**
 * 实体：合同模板
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年1月18日
 */
@Entity
public class t_template_pact extends Model {
	
	/** 创建时间 */
	public Date time;
	
	/** 类型 */
	private int type;
	
	/** 合同名称 */
	public String name;
	
	/** 合同模板内容 */
	public String content;
	
	/** 水印图片路径 */
	public String image_url;
	
	/** 水印图片的分辨率 */
	public String image_resolution;
	
	/** 水印图片大小 */
	public String image_size;
	
	/** 水印图片格式(后缀名) */
	public String image_format;

	public PactType getType() {
		return PactType.getEnum(type);
	}

	public void setType(PactType type) {
		this.type = type.code;
	}
	
	@Transient
	public String sign;

	public String getSign() {
		
		String signID = Security.addSign(id, Constants.MSG_PACTTEMP_SIGN, ConfConst.ENCRYPTION_KEY_DES);
		
		return signID;
		
	}
	
}
