package models.app.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.cfg.annotations.Nullability;

import common.constants.ConfConst;
import common.constants.Constants;
import common.enums.AnnualIncome;
import common.enums.Car;
import common.enums.Client;
import common.enums.Education;
import common.enums.Gender;
import common.enums.House;
import common.enums.Marital;
import common.enums.NetAssets;
import common.enums.Relationship;
import common.enums.WorkExperience;
import common.utils.DateUtil;
import common.utils.NoUtil;
import common.utils.Security;
import jobs.IndexStatisticsJob;
import models.common.entity.t_credit_level;
import models.common.entity.t_deal_user.DealType;
import models.common.entity.t_user_info.MemberType;
import models.core.entity.t_bid.Status;
import models.core.entity.t_product.BuyType;
import models.core.entity.t_product.PeriodUnit;
import models.core.entity.t_product.RepaymentType;
/***
 * 标信息实体
 *
 * @description 
 *
 * @author luzhiwei
 * @createDate 2016-4-13
 */
@Entity
public class BidInformationBean implements Serializable {

  @Id
  public long id;
  
	/** 标的编号:前缀（J）+id */
	@Transient
	public String bidNo;
	public String getBidNo(){
		return NoUtil.getBidNo(this.id);
	}
	
	/** 借款标题 */
	public String title;
	
	/** 年利率 */
	public double apr;
	
	/** 是否开启投标奖励 */
	public boolean  is_invest_reward;
	
	/** 投标奖励年利率 */
	public double  reward_rate;
	
	/** 借款期限单位
	 *1-天
	 *2-月*/
	public  int periodUnit;
	
	/** 借款期限 */
	public int period;
	
	/** 借款金额 */
	public double amount;
	
	/** 已投总额(冗余) */
	public double hasInvestedAmount;
	
	/** 
	 * 按份数购买时：每份金额（也是起购金额）
	 * 按金额购买：起购金额 
	 */
	public double minInvestAmount;
    
	
	/** 借款进度比例 */
	public double loanSchedule;
	
	/** 满标时间 */
	public Date invest_expire_time;
	
	/** 购买方式  */
	public int buyType;
	
	
	/** 还款方式
	 * 1-先息后本
	 * 2-等本等息
	 * 3-一次性还款 */
	public int repaymentType;
	
	
	/** 标的状态 */
	public int status;
	
	/** 预发售时间 */
	public Date preRelease;
	
	public long productId;
	
	/*public boolean getPreRelease() {
		if (preRelease==null) {
    		return false;
    	}
    	if (new Date().compareTo(preRelease)==-1) {
    		return true;
    	}
    	return false;
	}*/
	
    public long getPreRelease() {
    	return preRelease.getTime();
    }
	
	public String getRepaymentType() {
		RepaymentType repType = RepaymentType.getEnum(this.repaymentType);
		
		return repType == null ? null : repType.value;
	}

	public String getPeriodUnit(){
		PeriodUnit peroidUnitEnum = PeriodUnit.getEnum((this.periodUnit));
		return peroidUnitEnum == null ? null : peroidUnitEnum.getShowValue();
	}
	
	
	@Transient
	public String bidIdSign;
	public String getBidIdSign () {
		
		return Security.addSign(this.id, Constants.BID_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);
	}
	/**引用保障方式表*/
	public Long guarantee_mode_id;
	/**保障方式*/
	@Transient
	public String guarantee_mode;
	/**标的属于的产品类型*/
	@Transient
	public String bidType;
	public String getBidType(){
		return IndexStatisticsJob.webProdMap.get(productId+"");
	}
	/**投资期限*/
	public int invest_period;
}
