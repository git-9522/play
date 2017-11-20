package models.wechat.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import models.core.entity.t_bill.Status;

/****
 * 
 * 回款计划
 * @description 
 *
 * @author luzhiwei
 * @createDate 2016-4-7
 */
@Entity
public class RepaymentRecordBean implements Serializable {
   
	/** billId */
	@Id
	public long id;
	

	/** 回款时间 */
	public Date repaymentTime;  
	
	
	/** 本息 */
	public double principalInterest; 
	
   /** 状态  */
    public  int status;
    
    /**期数 */
    public int period;
    
    /** 总期数 */
    public int totalPeriod;
    
	public String getStatus() {
		Status status = Status.getEnum(this.status);
		
		return status == null ? null : status.value;
	}
	

}
