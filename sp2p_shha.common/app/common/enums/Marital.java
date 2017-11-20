package common.enums;


/**
 * 枚举:婚姻状况
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月9日
 */
public enum Marital {
	
	/** 已婚 */
	MARRIED(1,"已婚"),
	
	/** 未婚*/
	UNMARRRIED(2,"未婚");
	
	public int code;
	public String value;  
	
	private Marital(int code, String value) {
		this.code = code;
		this.value = value;
	}

	public static Marital getEnum(int code){
		Marital[] maritals = Marital.values();
		for (Marital marital : maritals) {
			if (marital.code == code) {

				return marital;
			}
		}
		
		return null;
	}
	
}
