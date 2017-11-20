package fy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shove.Convert;

import common.constants.Constants;
import common.utils.LoggerUtil;
import play.Play;

public class FyConsts {

	/** 富友常见配置属性 */
	private static Properties fyConf = null;
	
	/** 富友主配置文件*/
	private static final String path = Play.getFile("conf" + File.separator + "fy" + File.separator + "fy.properties").getAbsolutePath();
	
	/**商户代码 **/
	public static  String mchnt_cd = "";
	
	/**商户登录账户 **/
	public static  String mchnt_name = "";
	
	/**请求接口路径**/
	public static String  post_url = "";
	
	/**用户注册提交地址**/
	public final static String register = "reg.action";
	
	/**用户信息修改提交地址**/
	public final static String modifyUserInf = "modifyUserInf.action";
	
	/**预授权接口**/
	public final static String preAuth = "preAuth.action";
	
	/**冻结**/
	public final static String freeze = "freeze.action";
	
	/**解冻**/
	public final static String unFreeze = "unFreeze.action";
	
	/**预授权撤销接口**/
	public final static String preAuthCancel = "preAuthCancel.action";
	
	/**转账（商户与个人之间）**/
	public final static String transferBmu = "transferBmu.action";
	
	/**转账（个人与个人之间）**/
	public final static String transferBu = "transferBu.action";
	
	/**余额查询**/
	public final static String balanceAction = "BalanceAction.action";
	
	/**充值**/
	public final static String recharge = "500002.action";
	
	/**提现**/
	public final static String withdraw = "500003.action";
	
	/**app快速充值 */
	public final static String appFastRecharge = "app/500001.action";
	
	/**app提现*/
	public final static String appWithdraw = "app/500003.action";
	
	/**app快捷充值*/
	public final static String appRecharge = "app/500002.action";
	
	/**用户信息查询*/
	public final static String queryPersionInformation = "queryUserInfs.action";
	
	/**快速充值**/
	public final static String fastRecharge = "500001.action";
	
	/**更换用户手机号码*/
	public final static String changeUserMobile = "400101.action";
	
	/**充值提现查询*/
	public final static String querycztxAction = "querycztx.action";
	
	/**交易查询*/
	public final static String queryTxnAction = "queryTxn.action";
	
	/**用于后台补单的回调地址**/
	/**标的信息录入回调地址（补单）**/
	public final static String bidCreateRepair = "payment/fy/returnBidCreate";
	
	/**充值回调地址（补单）**/
	public final static String rechargeRepair = "payment/fy/returnRecharge";
	
	/**提现回调地址（补单）**/
	public final static String withdrawRepair = "payment/fy/returnWithdraw";
	
	/**用户开户回调地址（补单）**/
	public final static String userRegisterRepair = "payment/fy/returnUserRegister";
	
	/**放款回调地址（补单）**/
	public final static String bidSuditSuccRepair = "payment/fy/returnBidSuditSucc";
	
	/**标的审核不通过回调地址（补单）**/
	public final static String bidFailedRepair = "payment/fy/returnBidFailed";
	
	/**手动投标回调地址（补单）**/
	public final static String investRepair = "payment/fy/returnInvest";
	
	/**还款回调地址（补单）**/
	public final static String repaymentRepair = "payment/fy/returnRepayment";
	
	/**本息垫付后还款回调地址（补单）**/
	public final static String advanceRepaymentRepair = "payment/fy/returnAdvanceRepayment";
	
	/**本息垫付回调地址（补单）**/
	public final static String advanceRepair = "payment/fy/returnAdvance";
	
	/**债权转让回调地址（补单）**/
	public final static String debtTransferRepair = "payment/fy/returnDebtTransfer";
	
	/**自动投标回调地址（补单）**/
	public final static String autoInvestRepair = "payment/fy/returnAutoInvest";
	
	/**线下收款 **/
	public final static String offlineReceiveRepair = "payment/fy/returnOfflineReceive";
	
	/**快速充值回调地址（补单）**/
	public final static String fastRechargeRepair = "payment/fy/returnFastRecharge";
	
	/**充值通知接口 **/
	public final static String rechargeNotifyRepair = "payment/fy/thirdPartyRechargeNotify";
	
	/**提现通知接口 **/
	public final static String withdrawNotifyRepair = "payment/fy/thirdPartyWithdrawNotify";
	
	/**提现退票通知接口 **/
	public final static String withdrawRefundNotifyRepair = "payment/fy/thirdPartyWithdrawRefundNotify";
	
	/**用户修改信息通知通知接口 **/
	public final static String userModifyInfoNotifyRepair = "payment/fy/thirdPartyUserModifyInfoNotify";
	
	/**用户注销通知接口 **/
	public final static String userLogoutNotifyRepair = "payment/fy/thirdPartyUserLogoutNotify";
	
	/**
	 * 私钥 ,富友分配给商户的
	 */
	public static PrivateKey privateKey;
	
	/**
	 * 公钥，富友的公钥
	 */
	public static PublicKey publicKey;
	
	/**
	 * 私钥文件路径 如：
	 */
	public static String privateKeyPath = "";
	
	/**错误信息**/
	public static Map<String, String> error = new HashMap<String, String>();
	
	/**
	 * 公钥文件路径 如：
	 */
	public static String publicKeyPath = "";
	
	/**
	 * 加载富友配置文件
	 */
	private static void loadProperties(){
		LoggerUtil.info(false, "读取富友配置文件...");
		fyConf = new Properties();
		
		try {
			fyConf.load(new FileInputStream(new File(path))); 
			
		} catch (FileNotFoundException e){
			LoggerUtil.error(false, e, "读取富友配置库时 :%s", e.getMessage());
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	/**
	 * 不同支付平台差异性融合
	 */
	public static void initSupport(){
		Constants.LOAN_SERVICE_FEE_MAXRATE = Convert.strToDouble(fyConf.getProperty("borrowManageMaxrate"), 50.0);
		Constants.DEBT_TRANSFER_MAXRATE = Convert.strToDouble(fyConf.getProperty("debtTransferMaxrate"), 100.0);
		Constants.WITHDRAW_MAXRATE = Convert.strToDouble(fyConf.getProperty("maxWithdrawRate"), 1.0);
	}
	
	static {
		if (fyConf == null){
			loadProperties();
		}
		
		java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		mchnt_cd = fyConf.getProperty("mchnt_cd");
		post_url = fyConf.getProperty("post_url");
		mchnt_name = fyConf.getProperty("mchnt_name");
		
		privateKeyPath = Play.getFile("conf" + File.separator + "fy" + File.separator + fyConf.getProperty("privateKeyPath")).getAbsolutePath();
		publicKeyPath = Play.getFile("conf" + File.separator + "fy" + File.separator + fyConf.getProperty("publicKeyPath")).getAbsolutePath();
		
		Gson gson = new Gson();
		error = gson.fromJson(fyConf.getProperty("error"), new TypeToken<Map<String, String>>(){}.getType());
		initSupport();
	}
	
}
