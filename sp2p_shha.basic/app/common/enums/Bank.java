package common.enums;

public enum Bank {

	ICBC("ICBC", "工商银行", true),
	ABC("ABC", "农业银行", true),
	CMB("CMB", "招商银行", true),
	CCB("CCB", "建设银行", true),
	BCCB("BCCB", "北京银行", false),
	BJRCB("BJRCB", "北京农村商业银行", false),
	BOC("BOC", "中国银行", true),
	BOCOM("BOCOM", "交通银行", true),
	CMBC("CMBC", "民生银行", true),
	BOS("BOS", "上海银行", true),
	CBHB("CBHB", "渤海银行", true),
	CEB("CEB", "光大银行", true),
	CIB("CIB", "兴业银行", true),
	CITIC("CITIC", "中信银行", true),
	CZB("CZB", "浙商银行", false),
	GDB("GDB", "广发银行", true),
	HKBEA("HKBEA", "东亚银行", false),
	HXB("HXB", "华夏银行", true),
	HZCB("HZCB", "杭州银行", false),
	NJCB("NJCB", "南京银行", false),
	PINGAN("PINGAN", "平安银行", true),
	PSBC("PSBC", "邮储银行", true),
	SDB("SDB", "深发银行", false),
	SPDB("SPDB", "浦发", true),
	SRCB("SRCB", "上海农村商业银行", false);
	
	private Bank(String code, String name, boolean flag) {
		this.code = code;
		this.name = name;
		this.flag = flag;
	}
	
	public String code;
	
	public String name;
	
	/**
	 * 是否支持快捷充值
	 */
	public boolean flag;
	
	public static Bank getBank(String code) {
		for(Bank bank : Bank.values()) {
			if(bank.code.equals(code)) {
				return bank;
			}
		}
		return null;
	}
	
}