package models.wechat.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;






import common.utils.NoUtil;
import jobs.IndexStatisticsJob;
import models.core.entity.t_bid.Status;
import models.core.entity.t_product.PeriodUnit;
import models.core.entity.t_product.ProductType;
import models.core.entity.t_product.RepaymentType;

/***
 * 
 * 理财标列表实体
 * @description 
 *
 * @author luzhiwei
 * @createDate 2016-5-5
 */
@Entity
public class InvestBidBean implements Serializable {
	
    /** t_bid  id */
	@Id
    public  long  id;
   
	/** 标题  */
    public String title;
    
	/** 年利率  */
    public double apr;
    
	/** 所属借款产品ID */
	public long product_id;
	
	/** 产品类型
	 * 1-普通
	 * 2-信用
	 * 3-净值 */
	private int type;
	
	public void setType(ProductType producttype){
		this.type = producttype.code;
	}
	
	public ProductType getType(){
		return ProductType.getEnum(this.type);
	}
	
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
    
    /** 预发售判断 */
    public Date preRelease;

	/** 还款方式
	 * 1-先息后本
	 * 2-等本等息
	 * 3-一次性还款 */
	private int repayment_type;
    
	public RepaymentType getRepayment_type() {
		RepaymentType repType = RepaymentType.getEnum(this.repayment_type);
		
		return repType;
	}
	
	public void setRepayment_type(RepaymentType repaymentType) {
		this.repayment_type = repaymentType.code;
	}
    
    /** 标编号 */
	@Transient
	public String bidNo;
	public String getBidNo(){
		return NoUtil.getBidNo(this.id);
	}
	
	/** 年率整数部分 */
	@Transient
	public String bigNum;
	public String getBigNum(){
		String num1 = String.valueOf(this.apr);
		String[] split = num1.split("\\.");
		return split[0];
	}
	
	/** 年率小数部分 */
	@Transient
	public String smallNum;
	public String getSmallNum(){
		String num1 = String.valueOf(this.apr);
		String[] split = num1.split("\\.");
		return split[1];
	}


	public String  getPeriodUnit(){
		PeriodUnit  period = PeriodUnit.getEnum(this.periodUnit);

		return period == null ? null : period.getShowValue() ;
	}
	
	public Status  getStatus(){
		
		return Status.getEnum(this.status);
	}

	//TODO  改版
	/**标的属于产品的类型*/
	public String getProductName(){
		return  IndexStatisticsJob.webProdMap.get(product_id+"");
	}
	/**奖励利率*/
	public double rewardRate;
}
