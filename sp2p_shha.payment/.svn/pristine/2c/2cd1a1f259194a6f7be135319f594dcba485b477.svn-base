package fy;


public enum FyPayType {
	
	USERREGISTER(1, "用户开户", true, ""),
	
	//USERBINDCARD(2, "用户绑卡", true),
	
	NETSAVE(3, "网银充值", true, "PW11"),
	
	//ADDBIDINFO(4, "标的信息录入", true),
	
	INITIATIVETENDER(5, "主动投标", true, "PW13"),
	
	AUTOTENDER(6, "自动投标", true, "PW13"),
	
	AUTOTENDERPLAN(7, "自动投标计划", true, ""),
	
	TENDERCANCLE(8, "投标撤销", true, "PWCF"),
	
	LOANS(9, "放款", true, "PW03"),
	
	USRFREEZEBG(10, "资金冻结", true, "PWDJ"),
	
	USRUNFREEZE(11, "资金解冻", true, "PWJD"),
	
	REPAYMENT(12, "还款", true, "PW03"),
	
	MERCHANTANDPERSIONTRANSFER(13, "商户与个人之间转账", true, "PWPC"),
	
	PERSIONTRANSFER(14, "个人与个人之间转账", true, "PW03"),
	
	//BATCHREPAYMENT(15, "批量还款", true),
	
	QUERYCARDINFO(16, "银行卡查询", false, ""),
	
	QUERYACCTS(17, "商户余额查询", false, ""),
	
	QUERYBALANCEBG(18, "用户余额查询", false, ""),
	
	CREDITASSIGN(19,"债权转让",true, "PW03"),
	
	CASH(31, "取现", true, "PWTX"),
	
	FASTSAVE(32, "快速充值", true, ""),
	
	QUERYPERSIONINFORMATION(33, "查询用户信息", true, ""),
	
	CHANGEUSERMOBILE(34, "修改用户手机号", true, ""),
	
	/** 提现退票 */
	WITHDRAW_REFUND(35, "提现退票", true, ""),
	
	/** 用户修改信息 */
	USER_MODIFY_INFO(36, "用户修改信息", true, ""),
	
	/** 用户注销 */
	USER_LOGOUT(37, "用户注销", true, ""),

	QUERYCZTX(61, "充值提现查询", true, ""),
	
	QUERYTXN(62, "交易查询", true, ""),
	
	;
	
	/**
	 * 接口类型代号
	 */
	public int code;
	
	/**
	 * 描述
	 */
	public String value;

	/**
	 * 是否添加请求记录
	 */
	public boolean isAddRequestRecord;
	
	/**
	 * 交易类型描述 用于交易查询
	 */
	public String busiTp;
	
	/**
	 * 是否添加回调记录（注意:当isAddRequestRecord为true，isAddCallBackRecord配置才生效，即不添加请求记录，则不添加回调记录）
	 */
	public boolean isAddCallBackRecord = true;
	
	private FyPayType(int code, String value, boolean isAddRequestRecord, String busiTp){
		this.code = code;
		this.value = value;
		this.isAddRequestRecord = isAddRequestRecord;
		this.busiTp = busiTp;
	}
	
	private FyPayType(int code, String value, boolean isAddRequestRecord, boolean isAddCallBackRecord, String busiTp){
		this.code = code;
		this.value = value;
		this.isAddRequestRecord = isAddRequestRecord;
		this.isAddCallBackRecord = isAddCallBackRecord;
		this.busiTp = busiTp;
	}
	
	public static FyPayType getEnum(int code){
		FyPayType[] status = FyPayType.values();
		for (FyPayType statu : status){
			if (statu.code == code){
				
				return statu;
			}
		}
		
		return null;
	}
	
}
