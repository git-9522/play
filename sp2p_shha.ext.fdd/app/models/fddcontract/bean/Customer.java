package models.fddcontract.bean;

public class Customer {
	
	//用户姓名
	private String customer_name;
	//邮箱
	private String email; 
	//证件类型
	private String ident_type;
	//证件id
	private String id;
	//电话
	private String mobile;

	//用户类型
	private String client_role;
	
	//平台id
	private long user_id;
	
	
	
	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public String getClient_role() {
		return client_role;
	}

	public void setClient_role(String client_role) {
		this.client_role = client_role;
	}

	public String getCustomer_name() {
		return customer_name;
	}

	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getIdent_type() {
		return ident_type;
	}

	public void setIdent_type(String ident_type) {
		this.ident_type = ident_type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	
}
