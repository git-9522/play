package models.common.bean;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import models.common.entity.t_score_user.DealType;

/**
 * 积分记录
 *
 * @author YanPengFei
 * @createDate 2017年2月24日
 */
@Entity
public class UserScoreRecord {

	/** 用户积分记录ID */
	@Id
	@GeneratedValue
	public Long id;
	
	/** 用户名 */
	public String user_name;
	
	/** 场景类型：1.获得、2.兑换 */
	private int deal_type;
	
	/** 添加时间 */
	public Date time;
	
	/** 积分 */
	public double score;
	
	/** 备注 */
	public String summary;
	
	public DealType getDeal_type() {
		DealType dealType = DealType.getEnum(this.deal_type);
		
		return dealType;
	}
	
	public void setDeal_type(DealType dealType) {
		
		this.deal_type = dealType.code;
	}
	
}
