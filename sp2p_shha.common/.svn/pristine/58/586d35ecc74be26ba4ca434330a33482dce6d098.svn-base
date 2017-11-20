package common.enums;


/**
 * 枚举类型:教育情况
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月8日
 */
public enum Education {
	
	/** 高中以下学历:1 */
	JUNIOR_ORBELOW(1,"高中以下"),
	
	/** 高中学历:2 */
	SENIOR(2,"高中"),
	
	/** 专科学历:2 */
	COLLEGE(3,"专科"),
	
	/** 本科学历:4 */
	Undergraduate(4,"本科"),
	
	/** 本科以上学历:5 */
	DEGREE_ORABOVE(5,"本科以上");

	public int code;
	public String value;  
	
	private Education(int code, String value) {
		this.code = code;
		this.value = value;
	}

	/**
	 * 根据编码获取学历,没有返回null
	 *
	 * @param code
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月8日
	 */
	public static Education getEnum(int code){
		Education[] educations = Education.values();
		for (Education education : educations) {
			if (education.code == code) {

				return education;
			}
		}
		
		return null;
	}
}
