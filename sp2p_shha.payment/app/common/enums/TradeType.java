package common.enums;

public enum TradeType {

	/** 个人网银 */
	B2C(0, "B2C"),
	
	/** 企业网银 */
	B2B(1, "B2B"),
	
	/** 快捷充值 */
	QP(2, "QP"),
	
	/** 代扣签约充值 */
	AWS(3, "AWS");
	
	private TradeType(int code, String value) {
		this.code = code;
		this.value = value;
	}
	
	public int code;
	
	public String value;
	
	public static TradeType getTradeTypeByCode(int code) {
		for(TradeType tradeType : TradeType.values()) {
			if(tradeType.code == code) {
				return tradeType;
			}
		}
		return null;
	}
	
}