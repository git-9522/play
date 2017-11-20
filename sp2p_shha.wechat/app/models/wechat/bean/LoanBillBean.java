package models.wechat.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import common.utils.NoUtil;
/**
 * 用户借款账单实体
 *
 * @description 
 *
 * @author Songjia
 * @createDate 2016年5月10日
 */
@Entity
public class LoanBillBean implements Serializable{
	@Id
	public Long id;
	
	/** 时间 */
	public Date time;
	
	/** 本息合计 */
	public double corpus_interest;
	
	/** 还款日 */
	public Date repayment_time;
	
	@Transient
	public String bill_no;
	public String getBill_no(){
		return NoUtil.getBillNo(this.id, this.time);
	}
}
