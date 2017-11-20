package models.core.entity;


import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import common.enums.IsOverdue;
import common.utils.NoUtil;
import common.utils.number.Arith;
import play.db.jpa.Model;

/**
 * 理财账单 
 *
 * @author liudong
 * @createDate 2015年12月16日
 */
@Entity
public class t_bill_invest extends Model {


	/** 账单生成时间 */
	public Date time;
	
	/** 理财账单编号 */
	@Transient
	public String bill_invest_no;
	public String getBill_invest_no () {
		return NoUtil.getBillInvestNo(this.id, this.time);
	}
	
	/** 投资人ID */
	public long user_id;
	
	/** 投资ID */
	public long invest_id;
	
	/** 借款标ID */
	public long bid_id;
	
	/** 期数 */
	public int period;
	
	/** 借款标标题  */
	public String title;
	
	/** 收款时间 */
	public Date receive_time;
	
	/** 本期应收款金额 */
	public double receive_corpus;
	
	/** 本期应收利息 */
	public double receive_interest;
	
	/** 是否逾期:false:未逾期；true:逾期 */
	private boolean is_overdue;
	
	/** 逾期的罚款 */
	public double overdue_fine;
	
	/** 逾期天数  */
	public int overdue_days;
	
	/** 收款状态:0-待还；1-已还；2-已转让 */
	private int status;
	
	/** 实际收款时间 */
	public Date real_receive_time;
	
	
	/**投资奖励金额*/
	public double reward_invest;
	
	/**加息金额*/
	public double add_invest;
	
	/**加息金额发放状态:0未发放1已发放2处理中*/
	public int rate_status;
	
	/**实际发放加息金额（reward_invest+add_invest）*/
	public double real_add_invest;
	
	/** 本息合计 */
	@Transient
	public double corpus_interest;
	public double getCorpus_interest() {
			return Arith.add(this.receive_corpus, this.receive_interest);
	}

	public Status getStatus() {
		Status stat = Status.getEnum(this.status);
		
		return stat;
	}

	public void setStatus(Status bookedStatus) {
		this.status = bookedStatus.code;
	}
	
	public IsOverdue getIs_overdue() {
		IsOverdue isOverdue = IsOverdue.getEnum(this.is_overdue);
		
		return isOverdue;
	}

	public void setIs_overdue(IsOverdue isOverdue) {
		this.is_overdue = isOverdue.code;
	}
	
	public void setRate_status(Ratestatus bookedStatus) {
		this.rate_status = bookedStatus.code;
	}
	
	public Ratestatus getRate_status() {
		Ratestatus stat = Ratestatus.getEnum(this.rate_status);
		
		return stat;
	}
	
	
	/**
	 *枚举： 理财账单--收款状态  :0-待还；1-已还；2、已转让
	 *
	 * @author liudong
	 * @createDate 2015年12月23日
	 */
	public enum Status {
		
		/** 0：待还 */
		NO_RECEIVE(0,"待还"),
		
		/** 1：已还 */
		RECEIVED(1,"已还"),
		
		/** 2：已转让 */
		TRANSFERED(2,"已转让");
		
		/** 状态 */
		public int code;
		
		/** 描述 */
		public String value;
		
		
		private Status(int code, String status) {
			this.code = code;
			this.value = status;
		}
		
		public static Status getEnum(int code) {
			Status[] _status = Status.values();
			for (Status stat : _status) {
				if (stat.code == code) {
	
					return stat;
				}
			}
			return null;
		}
	}
	
	/**
	 *枚举： 理财账单--加息金额发放状态  :0-未发放；1-处理中；2、已发放
	 *
	 * @author jiayijian
	 * @createDate 2017年4月5日
	 */
	public enum Ratestatus {
		
		/** 0：未发放 */
		NO_SEND(0,"未发放"),
		
		/** 1：处理中 */
		PROCESSING(1,"处理中"),
		
		/** 2：已发放 */
		SENDED(1,"已发放");
		
		/** 状态 */
		public int code;
		
		/** 描述 */
		public String value;
		
		
		private Ratestatus(int code, String status) {
			this.code = code;
			this.value = status;
		}
		
		public static Ratestatus getEnum(int code) {
			Ratestatus[] _status = Ratestatus.values();
			for (Ratestatus stat : _status) {
				if (stat.code == code) {
	
					return stat;
				}
			}
			return null;
		}
	}
	
	
}