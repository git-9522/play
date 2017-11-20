package models.core.entity;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Transient;

import common.FeeCalculateUtil;
import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.NoUtil;
import common.utils.Security;
import models.core.entity.t_product.BuyType;
import models.core.entity.t_product.PeriodUnit;
import models.core.entity.t_product.RepaymentType;
import play.db.jpa.Model;

/**
 * 标的
 *
 * @author yaoyi
 * @createDate 2015年12月17日
 */
@Entity
public class t_bid extends Model{

	/**保障方式id*/
	public Long guarantee_mode_id;

	/**担保措施*/
	public String guarantee_measures;
	/**还款来源*/
	public String repayment_source;

	/** 发布时间 */
	public Date time = new Date();
	
	/** 标的编号:前缀（J）+id */
	@Transient
	public String bid_no;
	public String getBid_no(){
		return NoUtil.getBidNo(this.id);
	}
	
	/** 借款人ID */
	public long user_id;
	
	/** 所属借款产品ID */
	public long product_id;
	
	/** 借款标题 */
	public String title;
	
	/** 借款金额 */
	public double amount;
	
	/** 年利率 */
	public double apr;
	
	/** 借款期限单位
	 *1-天
	 *2-月*/
	private int period_unit;
	
	/** 借款期限 */
	public int period;
	
	/** 借款服务费 */
	public double loan_fee;
	
	/** 借款服务费、理财服务费、逾期罚息规则（以JSON串的形式存储）(数据来自借款产品) */
	public String service_fee_rule;
	
	/** 还款方式
	 * 1-先息后本
	 * 2-等本等息
	 * 3-一次性还款 */
	private int repayment_type;
	
	/** 购买方式  */
	private int buy_type;
	
	/** 借款描述 */
	public String description;
	
	/** 标的状态 */
	public int status;
	
	/** 登记标的入口：1 pc 2 app 3 wechat */
	public int client;
	
	/** 投标期限。单位固定：天 */
	public int invest_period;
	
	/** 
	 * 按份数购买时：每份金额（也是起购金额）
	 * 按金额购买：起购金额 
	 */
	public double min_invest_amount;
	
	/** 拆分份数 (数据来自借款产品）*/
	public int invest_copies;
	
	/** 保证金 */
	public double bail;
	
	/** 发售时间 */
	public Date pre_release_time;
	
	/** 满标时间 */
	public Date invest_expire_time;
	
	/** 实际满标时间 */
	public Date real_invest_expire_time;
	
	/** 借款进度比例 */
	public double loan_schedule;
	
	/** 已投总额(冗余) */
	public double has_invested_amount;
	
	/** 加入人次 */
	public int invest_count;
	
	/** 初审审核人 */
	public long preauditor_supervisor_id;
	
	/** 初审时间 */
	public Date preaudit_time;
	
	/** 审核意见 */
	public String preaudit_suggest;
	
	/** 复审审核人ID */
	public long auditor_supervisor_id;
	
	/** 复审时间 */
	public Date audit_time;
	
	/** 复审意见 */
	public String audit_suggest;
	
	/** 放款人ID */
	public long release_supervisor_id;
	
	/** 放款时间 */
	public Date release_time;
	
	/** 最后还款时间 */
	public Date last_repay_time;
	
	/** 判定是否是机构标 */
	public boolean is_agency;
	
	/** 机构ID */
	public long agency_id;
	
	/** 是否开启投标密码 */
	public boolean  is_invest_password;
	
	/** 投标密码 */
	public String  invest_password;
	
	/** 会员分组ID */
	public long  group_id;
	
	/** 是否开启投标奖励 */
	public boolean  is_invest_reward;
	
	/** 投标奖励年利率 */
	public double  reward_rate;
	
	@Transient
	public String sign;
	public String getSign () {
		
		return Security.addSign(this.id, Constants.BID_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
	}
	
	/** 标的编号：资金托管用，借款标唯一标识 */
	public String mer_bid_no;
	
	/** 业务订单号：标的登记操作唯一标识 */
	public String service_order_no;
	
	/** 托管方返回的交易订单号 */
	public String trust_order_no;

	public void setPeriod_unit(PeriodUnit productPeriodUnit){
		this.period_unit = productPeriodUnit.code;
	}
	
	public PeriodUnit getPeriod_unit(){
		return PeriodUnit.getEnum((this.period_unit));
	}
	
	public double getInvestServiceFee(){
		return FeeCalculateUtil.getSumBidInvestServiceFee(getLoanSumInterest() , service_fee_rule);
	}
	
	public double getLoanSumInterest(){
		return FeeCalculateUtil.getLoanSumInterest(amount, apr, period, getPeriod_unit(), getRepayment_type());
	}
	
	public BuyType getBuy_type() {
		BuyType buyType = BuyType.getEnum(this.buy_type);
		
		return buyType;
	}

	public void setBuy_type(BuyType buyType) {
		this.buy_type = buyType.code;
	}
	
	public Status getStatus() {
		Status status = Status.getEnum(this.status);
		
		return status;
	}
	
	public RepaymentType getRepayment_type() {
		RepaymentType repType = RepaymentType.getEnum(this.repayment_type);
		
		return repType;
	}
	
	public void setRepayment_type(RepaymentType repaymentType) {
		this.repayment_type = repaymentType.code;
	}

	public void setStatus(Status status) {
		this.status = status.code;
	}
	
	public t_bid() {}
	
	/**
	 * 奖励发放状态 0 未发放 1 cps已发放 2 财富圈已发放
	 */
	public int reward_grant_type ;

	public enum RewardGrantType {
		
		NOT_REWARD(0,"奖励未发放"),
		CPS_REWARD(1,"cps奖励已发放"),
		WEALTHCIRCLE_REWARD(2,"财富圈奖励发放") ;
		
		/** 状态 */
		public int code;
		
		/** 描述 */
		public String value;
		
		private RewardGrantType(int code, String value){
			this.code = code;
			this.value = value;
		}
		
		public static RewardGrantType getEnum(int code){
			RewardGrantType[] rewardGrantType = RewardGrantType.values();
			for(RewardGrantType income:rewardGrantType){
				if(income.code == code){
					
					return income;
				}
			}
			
			return null;
		}
		
	}
	
	/**
	 * 标的状态
	 *
	 * @description 
	 *
	 * @author yaoyi
	 * @createDate 2015年12月26日
	 */
	public enum Status {
		
		/** 初审中 */
		PREAUDITING(0, "初审中"),
		
		/** 借款中(初审通过) */
		FUNDRAISING(1, "借款中"),
		
		/** 复审，满标审核(已满标) */
		AUDITING(2, "满标审核"), 
		
		/** 待放款(复审通过) */
		WAIT_RELEASING(3, "待放款"),	
		
		/** 还款中(财务已放款) */
		REPAYING(4, "还款中"),
		
		/** 已还款 */
		REPAYED(5, "已还款"),
		
		/** 部分还款 */
		PARTIAL_NORMAL_REPAYMENT(6,"部分正常还款"),
		
		/** 初审不通过 */
		PREAUDIT_NOT_THROUGH(-1, "初审不通过"),
		
		/** 复审不通过 */
		AUDIT_NOT_THROUGH(-2, "复审不通过"),
		
		/** 流标(固定期限内没有满标，系统执行流标) */
		FLOW(-3, "流标");
		
		/** 状态 */
		public int code;
		
		/** 描述 */
		public String value;
		
		/** 放款状态(包括 还款中（财务已放款）和已还款 ) */
		public static final List<Integer> LOAN = Arrays.asList(
				REPAYING.code, REPAYED.code); 
		
		/** 前台-理财页面，显示的正在进行中的标的(1.借款中、2.复审待审、3.待放款、4.还款中)*/
		public static final List<Integer> PROCESS = Arrays.asList(
				FUNDRAISING.code, AUDITING.code, WAIT_RELEASING.code, REPAYING.code);
		
		/** 失败的标的（初审不通过，复审不通过，流标） */
		public static final List<Integer> FAIL = Arrays.asList(
				Status.PREAUDIT_NOT_THROUGH.code, Status.AUDIT_NOT_THROUGH.code, Status.FLOW.code);
		
		private Status(int code, String value){
			this.code = code;
			this.value = value;
		}
		
		public static Status getEnum(int code){
			Status[] bidstatus = Status.values();
			for(Status income:bidstatus){
				if(income.code == code){
					
					return income;
				}
			}
			
			return null;
		}
	}
}
