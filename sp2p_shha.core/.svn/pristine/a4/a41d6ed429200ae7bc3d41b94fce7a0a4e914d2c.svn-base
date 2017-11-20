package models.core.bean;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Security;
import models.core.entity.t_bid.Status;
import models.core.entity.t_product.PeriodUnit;

/**
 * 前台-借款标列表Bean
 *
 * @description 
 *
 * @author yaoyi
 * @createDate 2016年1月25日
 */
@Entity
public class FrontBid {

	/** 借款标ID */
	@Id
	public long id;
	
	/** 借款标题 */
	public String title;
	
	/** 年利率 */
	public double apr;
	
	/** 借款期限单位：1.天、2.月 */
	public int period_unit;
	
	/** 借款期限 */
	public int period;
	
	public PeriodUnit getPeriod_unit() {
	
		return PeriodUnit.getEnum(this.period_unit);
	}
	
	/** 借款金额 */
	public double amount;
	
	/** 已投金额*/
    public double has_invested_amount;
	
	/** 借款进度比例 */
	public double loan_schedule;
	
	/** 
	 * 按份数购买时：每份金额（也是起购金额）
	 * 按金额购买：起购金额 
	 */
	public double min_invest_amount;
	
	/** 标的状态 */
	public int status;
	
	public Status getStatus() {
		
		return Status.getEnum(this.status);
	}
	
	/** 是否开启投标奖励 */
	public boolean  is_invest_reward;
	
	/** 投标奖励年利率 */
	public double  reward_rate;
	
	/** 发售时间 */
	public Date pre_release_time;
	
	public long product_id;
	
	public String guarantee_mode_id;
	
	@Transient
	public long productId;
	
	public long getProductId() {
		return product_id;
	}
	
	@Transient
	public String sign;
	
	public String getSign() {
		String signID = Security.addSign(id, Constants.BID_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
		
		return signID;
	}
	
	 /** app标id加密 */
	@Transient
	public String appSign;
	public String getAppSign () {
		
		return Security.addSign(id, Constants.BID_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);
	}

	@Transient
	public String perUnit;
	public String getPerUnit(){
		return this.getPeriod_unit().getShowValue();
	}
	
}
