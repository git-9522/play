package models.app.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import models.core.entity.t_bill_invest.Status;
import common.FeeCalculateUtil;
import common.utils.NoUtil;
import common.utils.number.Arith;

@Entity
public class BillInvestListBean implements Serializable{
    @Id
	public long billInvestId;
    
    public long bidId;

    public long billInvestNo;
	
    public Date time;
    
	/** 本金 */
	public double receiveCorpus;

	/** 利息 */
	public double receiveInterest;
	
	/** 是否开启投标奖励 */
	public boolean  is_invest_reward;
	
	/** 投标奖励年利率 */
	public double  reward_rate;
	
	/**附加利息*/
	public double addAmount;	
	
	/** 到期时间 */
	public Date receiveTime;
	public Long getReceiveTime() {
		if (receiveTime==null) {
			return null;
		}
		return this.receiveTime.getTime();
	}

	/** 账单编号 */
	public String getBillInvestNo() {
		return NoUtil.getBillInvestNo(this.billInvestNo, this.time);
	}
	
}
