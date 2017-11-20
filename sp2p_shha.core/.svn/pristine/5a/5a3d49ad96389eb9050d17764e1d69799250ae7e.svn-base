package models.core.entity;

import java.util.Date;

import javax.persistence.Entity;

import common.enums.Client;
import play.db.jpa.Model;

/**
 * 投资记录表
 * 
 * @Title t_invest_log
 * @Description 投标成功后添加
 * @author hjs_djk
 * @createDate 2017年10月25日 下午1:56:26
 */
@Entity
public class t_invest_log extends Model {
	/** 是否为首投 */
	public boolean is_first_invest = false;
	/** 投资表ID **/
	public Long invest_id;
	/** 状态，投资奖励是否分发 **/
	public boolean status;

	/** 发放时间 **/
	public Date delivery_time;
	/** 备注 **/
	public String remark;

	public t_invest_log() {

	}

	public t_invest_log(t_invest invest, boolean isfirstInvest) {
		this.user_id = invest.user_id;
		this.time = invest.time;
		this.bid_id = invest.bid_id;
		this.amount = invest.amount;
		this.correct_amount = invest.correct_amount;
		this.correct_interest = invest.correct_interest;
		this.setTransfer_status(t_invest.TransferStatus.NORMAL);
		this.setClient(invest.getClient());
		this.service_order_no = invest.service_order_no;
		this.trust_order_no = invest.trust_order_no;
		this.invest_id = invest.id;
		this.is_first_invest = isfirstInvest;

	}

	/** 用户Id(投资人) */
	public Long user_id;

	/** 投资时间 */
	public Date time;

	/** 借款标ID */
	public long bid_id;

	/** 投资金额 */
	public double amount;

	/** 纠正本金 */
	public double correct_amount;

	/** 纠偏利息 */
	public double correct_interest;

	/** 分摊到每一笔投资的借款服务费 */
	public double loan_service_fee_divide;

	/** 投资入口 */
	private int client;

	/** 债权转让状态:0 正常(转让入的也是0) -1 已转让出 1 转让中 */
	private int transfer_status;

	/** 债权ID（申请完成之后的债权id） */
	public long debt_id;//

	/** 业务订单号,投标操作唯一标识 */
	public String service_order_no;

	/** 交易订单号。托管方产生 */
	public String trust_order_no;

	/**
	 * 红包投资金额
	 */
	public double red_packet_amount;

	/**
	 * 红包ID
	 */
	public long red_packet_id;

	/** 是否存在标投资奖励 */
	public boolean is_invest_reward = false;

	/** 奖励金额 **/
	public double reward_amount;

	/** 是否使用加息券 */
	public boolean is_use_rate = false;
	/** 加息券ID **/
	public long rate_id;
	/** 加息金额 **/
	public double rate_amount;

	/** 投标方式: 1pc 2自动 3android 4ios 5wechat */
	private int invest_type;

	public t_invest.InvestType getInvest_type() {
		t_invest.InvestType invest_type = t_invest.InvestType.getEnum(this.invest_type);
		return invest_type;
	}

	public void setInvest_type(t_invest.InvestType invest_type) {
		this.invest_type = invest_type.code;
	}

	public Client getClient() {
		Client client = Client.getEnum(this.client);

		return client;
	}

	public void setClient(Client client) {
		this.client = client.code;
	}

	public t_invest.TransferStatus getTransfer_status() {
		return t_invest.TransferStatus.getEnum(this.transfer_status);
	}

	public void setTransfer_status(t_invest.TransferStatus transfer_status) {
		this.transfer_status = transfer_status.code;
	}

	
	/** 现金券ID */
	public long cash_id;
	
	/** 现金券金额 */
	public double cash_amount;
	public enum SendStatus {
		
		/** 0:未发送 */
		UNSEND(0, "未发送"),
		
		/** 1:已发送 */
		HASSEND(2, "已发送");
		
		
		public int code;
		
		public String value;  
		
		private SendStatus(int code, String value) {
			this.code = code;
			this.value = value;
		}

		public static SendStatus getEnum(int code){
			SendStatus[] dts = SendStatus.values();
			for (SendStatus dt: dts) {
				if (dt.code == code) {
					return dt;
				}
			}
			
			return null;
		}
	}
}
