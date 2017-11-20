package models.common.entity;

import java.util.Date;
import java.util.Map;

import javax.persistence.Entity;

import common.utils.StrUtil;
import play.db.jpa.Model;

/**
 * 用户积分记录表
 *
 * @author YanPengFei
 * @createDate 2017年2月24日
 */
@Entity
public class t_score_user extends Model {

	/** 添加时间 */
	public Date time;
	
	/** 用户ID */
	public long user_id;
	
	/** 场景类型：1.获得、2.兑换 */
	private int deal_type;
	
	/** 操作类型：1.签到、2.兑换红包 */
	private int operation_type;
	
	/** 积分 */
	public double score;
	
	/** 可用积分 */
	public double balance;
	
	/** 备注 */
	public String summary;
	
	public DealType getDeal_type() {
		DealType dealType = DealType.getEnum(this.deal_type);
		
		return dealType;
	}
	
	public void setDeal_type(DealType dealType) {
		
		this.deal_type = dealType.code;
	}
	
	public OperationType getOperation_type() {
		OperationType operationType = OperationType.getEnum(this.operation_type);
		
		return operationType;
	}

	public void setOperation_type(OperationType operationType, Map<String, String> summaryParam) {
		this.deal_type = operationType.dealType.code;
		this.operation_type = operationType.code;
		this.summary = StrUtil.replaceByMap(operationType.value, summaryParam);
	}
	
	/**
	 * 场景类型枚举
	 *
	 * @description 
	 *
	 * @author YanPengFei
	 * @createDate 2017年2月24日
	 */
	public enum DealType {
		/** 获得 */
		INCOME(1, "获得"),
		
		/** 消耗 */
		PAY(2, "消耗");
		
		public int code;
		
		public String value;  
		
		private DealType(int code, String value) {
			this.code = code;
			this.value = value;
		}

		public static DealType getEnum(int code) {
			DealType[] dealTypeArray = DealType.values();
			
			for (DealType dealType: dealTypeArray) {
				if (dealType.code == code) {

					return dealType;
				}
			}
			
			return null;
		}
	}
	
	/**
	 * 操作类型枚举
	 * 
	 * @description 
	 *
	 * @author YanPengFei
	 * @createDate 2017年2月24日
	 */
	public enum OperationType {
		/** 
		 * 操作类型:签到[100]
		 * <br>交易类型：收入[1]
		 * <br>备注模板：连续签到day天，获得score积分，额外加成extra_score积分
		 */
		SIGNIN(100, "签到", DealType.INCOME, "连续签到day天，获得score积分，额外加成extra_score积分"),
		
		/** 
		 * 操作类型:兑换红包[201]
		 * <br>交易类型：支出[2]
		 * <br>备注模板：兑换红包支出score积分
		 */
		REDPACKET(200, DealType.PAY, "兑换红包支出score积分"),
		
		/** 
		 * 操作类型:注册成功[300]
		 * <br>交易类型：收入[1]
		 * <br>备注模板：注册成功获得score积分 
		 */
		REGISTER_SCORE(300, DealType.INCOME, "注册成功获得score积分"),
		
		/** 
		 * 操作类型:绑卡成功[301]
		 * <br>交易类型：收入[1]
		 * <br>备注模板：绑卡成功获得score积分
		 */
		BIND_CARD_SCORE(301, DealType.INCOME, "绑卡成功获得score积分"),
		
		/** 
		 * 操作类型:绑定邮箱[302]
		 * <br>交易类型：收入[1]
		 * <br>备注模板：绑定邮箱获得score积分
		 */
		BIND_MAILBOX_SCORE(302, DealType.INCOME, "绑定邮箱获得score积分"),
		
		/** 
		 * 操作类型:绑定微信[303]
		 * <br>交易类型：收入[1]
		 * <br>备注模板：绑定微信获得score积分
		 */
		BIND_WECHAT_SCORE(303, DealType.INCOME, "绑定微信获得score积分"),
		
		/** 
		 * 操作类型:首次充值[304]
		 * <br>交易类型：收入[1]
		 * <br>备注模板：首次充值获得score积分
		 */
		FIRST_RECHARGE(304, DealType.INCOME, "首次充值获得score积分"),
		
		/** 
		 * 操作类型:首次投资[305]
		 * <br>交易类型：收入[1]
		 * <br>备注模板：首次投资获得score积分
		 */
		FIRST_INVEST(305, DealType.INCOME, "首次投资获得score积分"),
		
		/** 
		 * 操作类型:cps推广开户[306]
		 * <br>交易类型：收入[1]
		 * <br>备注模板：cps推广开户获得score积分
		 */
		CPS_OPEN_ACCOUNT(306, DealType.INCOME, "cps推广开户获得score积分"),
		
		/** 
		 * 操作类型:cps推广首次投资[307]
		 * <br>交易类型：收入[1]
		 * <br>备注模板：cps推广首次投资获得score积分
		 */
		CPS_FIRST_INVEST(307, DealType.INCOME, "cps推广首次投资获得score积分"),
		
		/** 
		 * 操作类型:投资[308]
		 * <br>交易类型：收入[1]
		 * <br>备注模板：投资获得score积分
		 */
		DO_INVEST(308, DealType.INCOME, "投资获得score积分"),
		
		/** 
		 * 操作类型:兑换商品[309]
		 * <br>交易类型：消耗[2]
		 * <br>备注模板：兑换商品 goodsName 消耗score积分
		 */
		MALL_EXCHANGE(309, DealType.PAY, "兑换商品 goodsName 消耗score积分"),
		
		/** 
		 * 操作类型:抽奖[310]
		 * <br>交易类型：消耗[2]
		 * <br>备注模板：抽奖消耗score积分
		 */
		MALL_LOTTERY_MINUS(310, DealType.PAY, "抽奖消耗score积分"),
		
		/** 
		 * 操作类型:抽奖奖励[311]
		 * <br>交易类型：获得[1]
		 * <br>备注模板：抽奖获得score积分
		 */
		MALL_LOTTERY_ADD(311, DealType.INCOME, "抽奖获得score积分"),
		
		/**
		 * 操作类型:问卷调查奖励[400]
		 * <br>交易类型：获得[1]
		 * <br>备注模板：问卷调查score积分
		 */
		QUESTIONNAIRE_COMMIT(400, DealType.INCOME, "问卷调查获得score积分")
		;
		
		public int code;
		
		/** 操作描述  */
		public String description;
		
		/** 交易类型code  */
		public DealType dealType;
		
		/** 备注模板 */
		public String value;
		
		private OperationType(int code, DealType dealType, String value) {
			this.code = code;
			this.dealType = dealType;
			this.value = value;
		}
		
		private OperationType(int code, String description, DealType dealType, String value) {
			this.code = code;
			this.description = description;
			this.dealType = dealType;
			this.value = value;
		}
		
		public static OperationType getEnum(int code) {
			OperationType[] operationTypeArray = OperationType.values();
			
			for (OperationType operationType: operationTypeArray) {
				if (operationType.code == code) {

					return operationType;
				}
			}
			
			return null;
		}
	}
	
}
