package models.core.bean;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.NoUtil;
import common.utils.Security;
import models.core.entity.t_bid.Status;
import models.core.entity.t_product.PeriodUnit;
import models.core.entity.t_product.RepaymentType;

/**
 * 用户的投资特权记录
 *
 * @author jiayijan
 * @createDate 2017年4月1日
 */
@Entity
public class MyPrerogativeRecord {
	
	@Id
	/** 标的Id */
	public long bid_id;
	
	/** 标的编号:前缀（J）+id */
	@Transient
	public String bid_no;
	public String getBid_no(){
		return NoUtil.getBidNo(bid_id);
	}
	
	/** 标的标题 */
	public String title;
	
	/** 标的金额 */
	public double amount;
	
	/** 投资期限 */
	public int period;
	
	/** 借款期限单位 1-天 2-月*/
	private int period_unit;
	
	/** 标的利率*/
	public double apr;
	
	/** 投标奖励年利率*/
	public double reward_rate;
	
	/** 还款方式  1-先息后本 2-等本等息  3-一次性还款 */
	private int repayment_type;
	
	/** 发布时间 */
	public Date time;
	
	/** 已投金额 */
	public double hasInvestedAmount;
	
	/** 标的状态 */
	private int status;
	
	/** 投资密码 */
	public String invest_password;
	
	/** 标的Id加密 */
	@Transient
	public String bidIdSign;
	public String getBidIdSign() {
		return Security.addSign(this.bid_id, Constants.BID_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
	}

	public RepaymentType getRepayment_type() {
		RepaymentType repType = RepaymentType.getEnum(this.repayment_type);
		
		return repType;
	}
	
	public PeriodUnit getPeriod_unit(){
		return PeriodUnit.getEnum(this.period_unit);
	}
	
	public Status getStatus() {
		Status status = Status.getEnum(this.status);
		return status;
	}
	
}
