package models.app.bean;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import models.core.entity.t_bill_invest.Status;

import common.FeeCalculateUtil;
import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.NoUtil;
import common.utils.Security;
import common.utils.number.Arith;

/****
 * 
 * 回款计划
 * @description 
 *
 * @author luzhiwei
 * @createDate 2016-4-1
 */
@Entity
public class ReturnMoneyPlanBean implements Serializable {
   
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

	/**附加利息（投资奖励金额+加息金额）*/
	public double addAmount;
	
	/** 预计到账 */
	@Transient
	public double principalInterest;
	public double getPrincipalInterest() {
		
		return Arith.sub(Arith.add(Arith.add(this.receiveInterest, this.receiveCorpus),addAmount) , this.getLoanFee());
	}

	
	public Long getReceiveTime() {
		if (receiveTime == null) {
			return null;
		}
		return receiveTime.getTime();
	}

}
