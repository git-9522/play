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
 * 用户的投标记录
 *
 * @author liudong
 * @createDate 2016年1月20日
 */
@Entity
public class UserInvestRecord {
	
	/** 投资Id */
	@Id
	public long id;
	
	@Transient
	public String sign;
	
	public String getSign() {
		String signID = Security.addSign(id, Constants.INVEST_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
		
		return signID;
	}
	
	/**用户 id*/
	public long user_id;
	
	/** 标的Id */
	public long bid_id;
	
	/** 标的编号:前缀（J）+id */
	@Transient
	public String bid_no;
	public String getBid_no(){
		return NoUtil.getBidNo(bid_id);
	}
	
	/** 投资金额 */
	public double amount;
	
	/** 标的标题 */
	public String title;
	
	/** 标的利率*/
	public double apr;
	
	/** 投资期限 */
	public int period;
	
	/** 借款期限单位 1-天 2-月*/
	private int period_unit;
	
	/** 还款方式  1-先息后本 2-等本等息  3-一次性还款 */
	private int repayment_type;
	
	/** 附加利息 （标的奖励+加息卷）*/
	public double add_amount;
	
	/** 放款时间 */
	public Date release_time;
	
	/** 标的状态 */
	private int status;
	
	/** 已结束账单数 */
	public int alreadRepay;
	
	/** 总共账单数 */
	public int totalPay;
	
	/** 账单加密 */
	@Transient
	public String billSign;
	
	/** 合同查看地址 */
	@Transient
	public String pactViewUrl;
	/** 合同下载地址 */
	@Transient
	public String pactDownloadUrl;
	
	
	public String getBillSign() {
		String signID = Security.addSign(id, Constants.BILL_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
		
		return signID;
	}
	
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
