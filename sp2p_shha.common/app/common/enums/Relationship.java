package common.enums;


/**
 * 枚举类型:用户紧急联系人
 * 
 * @description 
 *
 * @author ChenZhipeng
 * @createDate 2015年12月17日
 */
public enum Relationship {
	
	PARENTS(1,"父母"),
	
	SPOUSE(2,"配偶"),
	
	CHILDREN(3,"子女"),
	
	RELATIVE(4,"亲戚"),
	
	FRIEND(5,"朋友"),
	
	COLLEAGUE(6,"同事");
	

	public int code;
	public String value;  
	
	private Relationship(int code, String value) {
		this.code = code;
		this.value = value;
	}

	public static Relationship getEnum(int code){
		Relationship[] rs = Relationship.values();
		for (Relationship r : rs) {
			if (r.code == code) {

				return r;
			}
		}
		
		return null;
	}
}
