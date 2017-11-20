package hf;


public enum HfPayType {

	USERREGISTER(1, "用户开户", true),
	
	USERBINDCARD(2, "用户绑卡", true),
	
	NETSAVE(3, "网银充值", true),
	
	CASH(31, "取现", true),
	
	ADDBIDINFO(4, "标的信息录入", true),
	
	INITIATIVETENDER(5, "主动投标", true),
	
	AUTOTENDER(6, "自动投标", true),
	
	AUTOTENDERPLAN(7, "自动投标计划", true),
	
	TENDERCANCLE(8, "投标撤销", true),
	
	LOANS(9, "放款", true),
	
	USRFREEZEBG(10, "资金冻结", true),
	
	USRUNFREEZE(11, "资金解冻", true),
	
	REPAYMENT(12, "还款", true),
	
	TRANSFER(13, "商户转账", true),
	
	USRACCTPAY(14, "用户支付", true),
	
	BATCHREPAYMENT(15, "批量还款", true),
	
	QUERYCARDINFO(16, "银行卡查询", false),
	
	QUERYACCTS(17, "商户余额查询", false),
	
	QUERYBALANCEBG(18, "用户余额查询", false),
	
	CREDITASSIGN(19,"债权转让",true),
	
	BIDATTACHINFO(20, "标的补录", true),
	
	BATCHREPAYMENT_(21, "单笔还款", true),
	
	SENDSMSCODE(22, "发送短信验证码", false),
	
	BOSACCTACTIVATE(23, "账户激活", true),
	
	QUICKBINDING(24, "换绑卡", true),
	
	DIRECTRECHARGE(25, "充值", true),
	
	QUERYUSRINFO(26, "查询用户信息", false), 
	
	AUTOTENDERCANCLE(27, "后台投标撤销", true), 
	
	CORPREGISTER(28, "企业开户", true), 
	
	CORPREGISTERQUERY(29, "企业开户查询", false);
	
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
	 * 是否添加回调记录（注意:当isAddRequestRecord为true，isAddCallBackRecord配置才生效，即不添加请求记录，则不添加回调记录）
	 */
	public boolean isAddCallBackRecord = true;
	
	private HfPayType(int code, String value, boolean isAddRequestRecord) {
		this.code = code;
		this.value = value;
		this.isAddRequestRecord = isAddRequestRecord;
	}
	
	private HfPayType(int code, String value, boolean isAddRequestRecord, boolean isAddCallBackRecord) {
		this.code = code;
		this.value = value;
		this.isAddRequestRecord = isAddRequestRecord;
		this.isAddCallBackRecord = isAddCallBackRecord;
	}
	
	public static HfPayType getEnum(int code) {
		HfPayType[] status = HfPayType.values();
		for (HfPayType statu : status) {
			if (statu.code == code) {
				
				return statu;
			}
		}
		
		return null;
	}
	
}
