package models.wechat.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import common.FeeCalculateUtil;
import common.utils.NoUtil;
import common.utils.number.Arith;
import models.core.entity.t_bill_invest.Status;

/****
 * 
 * 回款计划 详情
 * @description 
 *
 * @author luzhiwei
 * @createDate 2016-05-10
 */
@Entity
public class ReceiveBillDetailBean implements Serializable {
   
	/** billId */
	@Id
	public long billInvestNo;
	
	/** 用户Id */
	public long userId;
	
	/** 到期时间 */
	public Date receiveTime;  

	
	/** 期数 */
	public int period;
	
	/** 总期数 */
	public int totalPeriod;
	
	
	/** 本金 */
	public double receiveCorpus; 
	
	/** 利息 */
	public double receiveInterest;
	
	/** 时间 */
	public Date time;
	
	/** 服务费规则*/
	public String serviceFeeRule;
	
	/** 状态 */
	public int status;
	/** 状态描述 */
	@Transient
	public String statusStr;
	public String getStatusStr() {
		Status stat = Status.getEnum(this.status);
		
		return stat.value;
	}
	
	/** 账单编号 */
	public String getBillInvestNo () {
		return NoUtil.getBillInvestNo(this.billInvestNo, this.time);
	}	
	@Transient
	public double loanFee;
	/** 服务费 */
	public double getLoanFee() {
		
		return FeeCalculateUtil.getInvestManagerFee(this.receiveInterest, this.serviceFeeRule,this.userId);
	}

	/** 预计到账 */
	@Transient
	public double principalInterest;
	public double getPrincipalInterest() {
		
		return Arith.sub(Arith.add(this.receiveInterest, this.receiveCorpus) , this.getLoanFee());
	}


}
