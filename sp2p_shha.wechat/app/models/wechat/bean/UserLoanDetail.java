package models.wechat.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import models.core.entity.t_bid.Status;
import models.core.entity.t_product.PeriodUnit;
import models.core.entity.t_product.RepaymentType;
import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.NoUtil;
import common.utils.Security;

/**
 * 用户借款基本信息实体
 *
 * @description 微信-账户中心-资产管理-我的借款
 *
 * @author DaiZhengmiao
 * @createDate 2016年6月13日
 */
@Entity
public class UserLoanDetail implements Serializable {

	@Id
	public long id;
	
	/**用户 id*/
	public long user_id;
	
	/** 借款金额 */
	public double amount;
	
	/** 借款标题 */
	public String title;
	
	/** 年利率 */
	public double apr;
	
	/** 借款期限单位
	 *1-天
	 *2-月*/
	public int period_unit;
	
	public PeriodUnit getPeriod_unit(){
		return PeriodUnit.getEnum(this.period_unit);
	}
	
	/** 借款期限 */
	public int period;
	
	/** 标的状态 */
	public int status;
	
	public Status getStatus(){
		return Status.getEnum(this.status);
	}
	
	/** 放款时间 */
	public Date release_time;
	
	/** 还款方式 */
	public int repayment_type;
	
	public RepaymentType getRepayment_type(){
		return RepaymentType.getEnum(this.repayment_type);
	}
	
	/** 标的已还账单数 */
	public int has_repayment_bill;
	
	/** 标的总的账单数 */
	public int total_repayment_bill;
	
	@Transient
	public String bid_no;
	
	@Transient
	public String sign;
	
	public String getSign() {
		String signID = Security.addSign(id, Constants.BID_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
		
		return signID;
	}
	public String getBid_no(){
		return NoUtil.getBidNo(id);
	}
}
