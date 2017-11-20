package models.wechat.bean;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.NoUtil;
import common.utils.Security;

/**
 * 借款账单详情实体
 *
 * @description 
 *
 * @author Songjia
 * @createDate 2016年5月10日
 */
@Entity
public class LoanBillDetailsBean implements Serializable{
	@Id
	public Long id;
	
	/** 生成时间 */
	public Date time;
	
	/** 账单期数 */
	public int period;
	
	/** 应还本金 */
	public double repayment_corpus;
	
	/** 应还利息 */
	public double repayment_interest;
	
	/** 本息合计 */
	public double corpus_interest;
	
	/** 还款日 */
	public Date repayment_time;
	
	/** 实际还款时间 */
	public Date real_repayment_time;
	
	/** 还款状态:0-正常待还； 1-本息垫付待还  2-正常还款  3-本息垫付还款  4-线下收款  5-本息垫付后线下收款；*/
	private int status;
	
	/** 借款账单编号 */
	@Transient
	public String bill_no;
	public String getBill_no(){
		return NoUtil.getBillNo(this.id, this.time);
	}
	
	/**加密ID*/
	@Transient
	public String sign;
	public String getSign() {
		return Security.addSign(this.id, Constants.BILL_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
	}
	
	
	public Status getStatus() {
		Status status = Status.getEnum(this.status);
		
		return status;
	}

	public void setStatus(Status status) {
		this.status = status.code;
	}
	
	
	/**
	 * 枚举：借款账单 ：  还款状态: 0:正常待还；1:本息垫付待还；2:正常还款；3:本息垫付还款；4:线下收款；5:本息垫付后线下收款；
	 *
	 * @author liudong
	 * @createDate 2015年12月21日
	 */
	public enum Status {
		
		/** 0  正常待还  */
		NORMAL_NO_REPAYMENT(0,"正常待还"),
		
		/** 1  本息垫付待还 */
		ADVANCE_PRINCIIPAL_NO_REPAYMENT(1,"本息垫付待还 "),
		
		/** 2  正常还款 */
		NORMAL_REPAYMENT(2,"正常还款"),
		
		/** 3  本息垫付后还款 */
		ADVANCE_PRINCIIPAL_REPAYMENT(3,"本息垫付还款"),
		
		/** 4  线下收款 */
		OUT_LINE_RECEIVE(4,"线下收款"),
		
		/** 5 本息垫付后线下收款 */
		OUT_LINE_PRINCIIPAL_RECEIVE(5,"本息垫付后线下收款");
		
		/** 状态 */
		public int code;
		
		/** 描述 */
		public String value;
		
		/** 未还(包括 正常待还+本息垫付待还 ) */
		public static final List<Integer> NO_REPAYMENT = Arrays.asList(
				NORMAL_NO_REPAYMENT.code,
				ADVANCE_PRINCIIPAL_NO_REPAYMENT.code); 

		/** 已还(正常还款、本息垫付还款 、线下收款、本息垫付后线下收款 ) */
		public static final List<Integer> REPAYED = Arrays.asList( 
				NORMAL_REPAYMENT.code,
				ADVANCE_PRINCIIPAL_REPAYMENT.code,
				OUT_LINE_RECEIVE.code,
				OUT_LINE_PRINCIIPAL_RECEIVE.code);
		
		private Status(int code, String value){
			this.code = code;
			this.value = value;
		}
		
		public static Status getEnum(int code){
			Status[] status = Status.values();
			for(Status s: status){
				if(s.code == code){
					
					return s ;
				}
			}
			
			return null;
		}
	}
}
