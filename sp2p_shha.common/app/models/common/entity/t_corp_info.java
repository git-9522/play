package models.common.entity;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class t_corp_info extends Model  {
	
	public t_corp_info() {
	}
	
	public t_corp_info(long user_id, String instu_code, String busi_code, String tax_code,
			boolean guar_type, double guar_corp_earnest_amt, int status) {
		this.user_id = user_id;
		this.instu_code = instu_code;
		this.busi_code = busi_code;
		this.tax_code = tax_code;
		this.guar_type = guar_type;
		this.guar_corp_earnest_amt = guar_corp_earnest_amt;
		this.status = status;
	}
	
	public long user_id;
	
	/**
	 * 组织机构代码
	 */
	public String instu_code;
	
	/**
	 * 营业执照编号
	 */
	public String busi_code;
	
	/**
	 * 税务登记号
	 */
	public String tax_code;
	
	/**
	 * 是否为担保类型
	 */
	public boolean guar_type;
	
	/**
	 * 企业用户备案金
	 */
	public double guar_corp_earnest_amt;
	
	/**
	 * 状态
	 */
	private int status;

	public void setStatus(Status status) {
		this.status = status.code;
	}
	
	public Status getStatus() {
		Status status = Status.getEnumByCode(this.status);
		return status;
	}
	
	public void copyProperties(t_corp_info corpInfo) {
		this.user_id = corpInfo.user_id;
		this.instu_code = corpInfo.instu_code;
		this.busi_code = corpInfo.busi_code;
		this.tax_code = corpInfo.tax_code;
		this.guar_type = corpInfo.guar_type;
		this.guar_corp_earnest_amt = corpInfo.guar_corp_earnest_amt;
		this.status = corpInfo.getStatus().code;
	}
	
	public enum Status {
		
		/** 初始 */
		INIT(0, "I", "初始"),
		
		/** 提交 */
		COMMIT(1, "T", "提交"),
		
		/** 审核中 */
		AUDITING(2, "P", "审核中"),
		
		/** 审核拒绝 */
		REJECT(3, "R", "审核拒绝"),
		
		/** 开户失败 */
		FAIL(4, "F", "开户失败"),
		
		/** 开户中 */
		RPOCESS(5, "K", "开户中"),
		
		/** 开户成功 */
		SUCCESS(6, "Y", "开户成功");
		
		
		public int code;
		
		public String value;
		
		public String name;
		//ycy
		public static Status[] processes = { COMMIT, AUDITING, RPOCESS, SUCCESS };
		
		public static Status[] fails = { REJECT, FAIL };
		
		private Status(int code, String value, String name) {
			this.code = code;
			this.value = value;
			this.name = name;
		}
		
		public static Status getEnumByCode(int code){
			Status[] statuses = Status.values();
			for(Status status : statuses){
				if(status.code == code){
					return status;
				}
			}
			return null;
		}
		
		public static Status getEnumByValue(String value) {
			Status[] statuses = Status.values();
			for(Status status : statuses){
				if(value.equals(status.value)) {
					return status;
				}
			}
			return null;
		}
		
		public static boolean inProcesses(int code) {
			for(Status status : processes) {
				if(code == status.code) {
					return true;
				}
			}
			return false;
		}
		
	}
	
}