package models.wechat.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import common.utils.DateUtil;

import models.common.entity.t_deal_user.DealType;

/**
 * 用户交易记录详情实体
 *
 * @description 
 *
 * @author Songjia
 * @createDate 2016年5月6日
 */
@Entity
public class DealUserDetailBean implements Serializable{
	@Id
	public Long id;
	
	/** 交易类型  */
    public int dealType;
    
    /** 订单订单号 */
    public String  orderNo;
    
    /** 成交金额  */
    public double amount;
    
    /** 交易时间  */
    public Date time;
    
    /** 交易明细 */
    public String summary;

	public String getDealType() {
		DealType dt = DealType.getEnum(this.dealType);
		
		return dt == null ? null : dt.value;
	}

	
	
	public String getTime() {
		if(time==null){
			return null;
		}
		
		return DateUtil.dateToString(this.time, "yyyy/MM/dd hh:mm:ss");
	}
}
