package models.wechat.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import common.utils.NoUtil;

/****
 * 
 * 回款计划
 * @description 
 *
 * @author luzhiwei
 * @createDate 2016-4-1
 */
@Entity
public class ReceiveBillPlanBean implements Serializable {
   
	/** billId */
	@Id
	public long id;

	/** 到期时间 */
	public Date receiveTime;  


	/** 本金 */
	public double receiveCorpus; 
	
	/** 利息 */
	public double receiveInterest;
	
	/** 时间 */
	public Date time;
	
	/** 账单编号 */
    @Transient
	public String billInvestNo;
    

	public String getBillInvestNo () {
		return NoUtil.getBillInvestNo(this.id, this.time);
	}	
	


	


}
