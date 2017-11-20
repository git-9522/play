package common.enums;



/**
 * 枚举类型:客户端类型
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月8日
 */
public enum Client {
	
	/** PC:1 */
	PC(1,"PC","Web端", "/public/common/investtype-pc.png"),

	/** 安卓:2 */
	ANDROID(2,"Android","安卓", "/public/common/investtype-android.png"),
	
	/** IOS:3 */
	IOS(3,"IOS","IOS客户端", "/public/common/investtype-ios.png"),


	/** 微信:4 */
	WECHAT(4,"Wechat","微信端", "/public/common/investtype-wechat.png");


	public int code;
	public String value;
	public String description;
	public String icon;
	
	private Client(int code, String value, String description, String icon) {
		this.code = code;
		this.value = value;
		this.description = description;
		this.icon = icon;
	}
	
	/**
	 * 根据code取得客户端类型,没有找到返回null
	 *
	 * @param code
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月8日
	 */
	public static Client getEnum(int code){
		Client[] clients = Client.values();
		for (Client cli : clients) {
			if (cli.code == code) {

				return cli;
			}
		}
		
		return PC;
	}
	
	/***
	 * 是否为移动设备
	 * @param code
	 * @return
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-1
	 */
	public static boolean  isAppEnum(int code){
		
		if(code == ANDROID.code || code == IOS.code){
			return true;
		}	
		
		return false;
	}
}
