package models.wechat.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import common.utils.NoUtil;

/**
 * 用户理财账单实体
 *
 * @description 
 *
 * @author Songjia
 * @createDate 2016年5月10日
 */
@Entity
public class InvestBillBean implements Serializable{
	@Id
	public Long id;
	
	/** 时间 */
	public Date time;
	
	/** 应收本息 */
	public double corpus_interest;
	
	/** 到期时间 */
	public Date receive_time;
	
	/** 账单编号 */
	@Transient
	public String bill_invest_no;
	public String getBill_invest_no () {
		return NoUtil.getBillInvestNo(this.id, this.time);
	}
	
}
