package common.enums;

/**
 * sp2p托管业务类型枚举
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2016年1月7日
 */
public enum ServiceType {

	/** 开户 */
	REGIST(10, "开户"),
	
	/** 用户绑卡 */
	BIND_CARD(11, "用户绑卡"),
	
	/** 自动投标签约 */
	AUTO_INVEST_SIGN(12, "自动投标签约"),
	
	/** 自动还款签约 */
	AUTO_REPAYMENT_SIGN(13, "自动还款签约"),

	/** 充值 */
	RECHARGE(30, "充值"),
	
	/** 提现 */
	WITHDRAW(31, "提现"),
	
	/** 标的发布 */
	BID_CREATE(32, "标的发布"),

	/** 手动投标  */
	INVEST(33, "手动投标"),
	
	/** 自动投标 */
	AUTO_INVEST(34, "自动投标"),
	
	/** 标的审核通过（放款） */
	BID_AUDIT_SUCC(35, "放款"),
	
	/** 初审不通过 */
	BID_PRE_AUDIT_FAIL(36, "初审不通过"),
	
	/** 复审不通过 */
	BID_AUDIT_FAIL(37, "复审不通过"),

	/** 流标 */
	BID_FLOW(38, "流标"),
	
	/** 还款 */
	REPAYMENT(39, "还款"),
	
	/** 垫付 */
	ADVANCE(40, "垫付"),
	
	/** 线下收款 */
	OFFLINE_RECEIVE(41, "线下收款"),
	
	/** 垫付后还款 */
	REPAYMENT_AFER_ADVANCE(43, "垫付后还款"),
	
	/** 奖励兑换 */
	CONVERSION(44, "奖励兑换"),
	
	/** 商户充值 */
	MERCHANT_RECHARGE(45, "商户充值"),
	
	/** 商户提现 */
	MERCHANT_WITHDRAWAL(46, "商户提现"),
	
	/** 债权转让 */
	DEBT_TRANSFER(47, "债权转让"),
	
	/** 查询余额 */
	QUERY_BALANCE_BG(48, "余额查询(后台)"),
	
	/** 商户与个人间转账 */
	TRANSFER(49, "转账（商户用）"),
	
	/** 个人与个人之间转账 */
	USER_TRANSFER(50, "转账（用户间）"),
	
	/** 资金解冻 */
	USER_UNFREEZE(51, "资金解冻"),
	
	/** 查询用户信息 */
	QUERY_PERSION_INFORMATION(52, "查询用户信息"),
	
	/** 修改用户手机号 */
	CHANGE_USER_MOBILE(54, "修改用户手机号"),
	
	/** 提现退票 */
	WITHDRAW_REFUND(55, "提现退票"),
	
	/** 用户修改信息 */
	USER_MODIFY_INFO(56, "用户修改信息"),
	
	/** 用户注销 */
	USER_LOGOUT(57, "用户注销"),
	
	/** 交易查询 */
	QUERYTXN(61, "交易查询"),
	
	/** 标的撤销 */
	TENDER_CANCLE(62, "标的撤销"),
	
	/* ------------- 上海银行托管 ------------- */
	
	/** 发送短信验证码 */
	SENDSMSCODE(63, "发送短信验证码"),
	
	/** 账户激活 */
	BOSACCTACTIVATE(64, "账户激活"),
	
	/** 换绑卡 */
	QUICKBINDING(65, "换绑卡"), 
	
	/** 后台投标撤销 */ 
	AUTOTENDERCANCLE(66, "后台投标撤销"),
	
	/** 企业开户 */ 
	CORPREGISTER(67, "企业开户");
	
	
	/**
	 * 操作类型代号
	 */
	public int code;
	
	/**
	 * 描述
	 */
	public String value;
	
	private ServiceType(int code, String value) {
		this.code = code;
		this.value = value;
	}
	
	public static ServiceType getEnum(int code) {
		ServiceType[] pts = ServiceType.values();
		for (ServiceType pt : pts) {
			if (pt.code == code) {
				
				return pt;
			}
		}
		
		return null;
	}
}
