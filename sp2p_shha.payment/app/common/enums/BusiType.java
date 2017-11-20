package common.enums;

public enum BusiType {
	
	/**
	 * 开户
	 */
	REGISTER(0, "user_register"),
	
	/**
	 * 充值
	 */
	CHARGE(1, "recharge"),
	
	/**
	 * 换绑卡旧卡
	 */
	ORGIN_BIND(2, "rebind"),

	/**
	 * 换绑卡新卡
	 */
	NEW_BIND(3, "rebind");
	
	private BusiType(int code, String value) {
		this.code = code;
		this.value = value;
	}
	
	public int code;
	
	public String value;
	
	public static BusiType getTypeByCode(int code) {
		for(BusiType busiType : BusiType.values()) {
			if(busiType.code == code) {
				return busiType;
			}
		}
		return null;
	}
	
}