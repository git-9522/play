package models.app.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.cfg.annotations.Nullability;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.DateUtil;
import common.utils.NoUtil;
import common.utils.Security;
import jobs.IndexStatisticsJob;
import models.common.entity.t_deal_user.DealType;
import models.core.entity.t_bid.Status;
import models.core.entity.t_product.PeriodUnit;
import models.core.entity.t_product.RepaymentType;
/***
 * 
 * 理财标列表实体
 * @description 
 *
 * @author luzhiwei
 * @createDate 2016-4-7
 */
@Entity
public class InvestBidsBean implements Serializable {
	
    /** t_bid  id */
	@Id
    public  long  id;
   

	
	/** 标题  */
    public String title;
    
	/** 年利率  */
    public double apr;
    
	/** 期限  */
    public int period;
    
	/** 期限单位 */
    public int periodUnit;
    
    /** 借款金额  */
    public double amount;
    
    /** 已投金额*/
    public double hasInvestedAmount;
    
    /** 状态 */
    public int status;
    
    /**投标进度*/
    public double loanSchedule;
    
	/** 是否开启投标奖励 */
	public boolean  is_invest_reward;
	
	/** 投标奖励年利率 */
	public double  reward_rate;
    
    /** 预发售判断 */
    public Date preRelease;
    
    public long productId;
    
	/**标的属于的产品类型*/
	@Transient
	public String bidType;
	public String getBidType(){
	return 	IndexStatisticsJob.webProdMap.get(productId+"");
	}
   /* public boolean getPreRelease() {
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
    
    /** 标编号 */
	@Transient
	public String bidNo;
	public String getBidNo(){
		return NoUtil.getBidNo(this.id);
	}
	
	
    /** 标id加密 */
	@Transient
	public String bidIdSign;
	public String getBidIdSign () {
		
		return Security.addSign(this.id, Constants.BID_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);
	}
	

	public String  getPeriodUnit(){
		PeriodUnit  period = PeriodUnit.getEnum(this.periodUnit);

		return period == null ? null : period.getShowValue() ;
	}
    
}
