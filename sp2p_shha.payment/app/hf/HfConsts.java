package hf;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.shove.Convert;

import common.constants.Constants;
import common.utils.LoggerUtil;
import play.Logger;
import play.Play;

public class HfConsts {

	/** 汇付天下常见配置属性 */
	private static Properties hfConf = null;

	/** 请求必须参数、ChkValue、汇付响应ChkValue key集合 */
	private static Map<String, String[]> maps = null;

	/**
	 * 汇付主配置文件
	 */
	private static final String path = Play.getFile("conf" + File.separator + "hf" + File.separator + "chinapnr.properties").getAbsolutePath();

	/**
	 * 加载主配置文件，并初始化相关信息
	 */
	static {
		Logger.info("汇付主配置文件路径：%s", path);

		if (hfConf == null) {
			loadProperties();
		}

		if (maps == null) {
			initMaps();
		}
		
		initSupport();
		
	}

	/**
	 * 商户私钥文件地址
	 */
	public final static String MER_PRI_KEY_PATH = Play.getFile("conf" + File.separator + "hf" + File.separator + hfConf.getProperty("chinapnr_merKeyFile")).getAbsolutePath();

	/**
	 * 商户公钥文件地址
	 */
	public final static String MER_PUB_KEY_PATH = Play.getFile("conf" + File.separator + "hf" + File.separator + hfConf.getProperty("chinapnr_PgPubkFile")).getAbsolutePath();

	/**
	 * 汇付天下接口提交地址
	 */
	public final static String CHINAPNR_URL = hfConf.getProperty("chinapnr_url");

	/**
	 * 接口版本1.0
	 */
	public final static String VERSION1 = hfConf.getProperty("chinapnr_version");

	/**
	 * 接口版本2.0
	 */
	public final static String VERSION2 = hfConf.getProperty("chinapnr_version_2.0");
	
	/**
	 * 接口版本3.0
	 */
	public final static String VERSION3 = hfConf.getProperty("chinapnr_version_3.0");
	
	/**
	 * 接口版本4.0
	 */
	public final static String VERSION4 = hfConf.getProperty("chinapnr_version_4.0");

	/**
	 * 汇付商户号
	 */
	public final static String MERID = hfConf.getProperty("chinapnr_merId");

	/**
	 * 商户客户号
	 */
	public final static String MERCUSTID = hfConf.getProperty("chinapnr_merCustId");

	/**
	 * 商户转账-出账子账户
	 */
	public static final String TRANSFEROUTACCTID = hfConf.getProperty("chinapnr_cfb");

	/**
	 * 商户子账户-用来收取服务费的子账户
	 */
	public static final String SERVFEEACCTID = hfConf.getProperty("chinapnr_fee");

	/**
	 * 汇付取现手续费收取方式：true-向用户收取；false-向平台收取
	 */
	public final static boolean USER_PAY_WITHDRAW_FEE = Convert.strToBoolean(hfConf.getProperty("chinapnr_pay_withdraw_fee"), false);
	
	
	/**
	 * 汇付天下成功状态码
	 */
	public final static String SUCCESS_CODE = "000";
	
	/**
	 * 最大投资手续费率
	 */
	public final static String MAXTENDERRATE = hfConf.getProperty("maxTenderRate");

	/**
	 * 借款手续费率
	 */
	public final static String BORROWERRATE = hfConf.getProperty("BorrowerRate");
	
	/**
	 * 自动投标计划支持-需要商户与汇付天下签约
	 */
	public final static boolean TENDERPLANSUPPORT = "true".equalsIgnoreCase(hfConf.getProperty("tenderPlanSupport"))?true:false;
	
	/**
	 * 自动投标计划类型 P--部分授权  W--完全授权
	 */
	public final static String TENDERPLANTYPE = hfConf.getProperty("tenderPlanType");
	
	/**
	 * 银行卡列表
	 */
	public final static String BANK_LIST = getProperty(hfConf, "bank_list", System.getProperty("file.encoding"));

	/** 异步回调时，响应汇付的标志的前缀 */
	public static final String RESP_PREFIX = "RECV_ORD_ID_";

	public static final String TRXID = "TrxId";

	public static final String PROID = "ProId";

	public static final String ORDID = "OrdId";

	public static final String BATCHID = "BatchId";
	

	/**
	 * 用户注册
	 */
	public final static String CMD_USERREGISTER = "UserRegister";

	/**
	 * 用户开户后台
	 */
	public final static String CMD_BGRRGISTER = "BgRegister";

	/**
	 * 用户绑卡
	 */
	public final static String CMD_USERBINDCARD = "UserBindCard";

	/**
	 * 用户登录
	 */
	public final static String CMD_USERLOGIN = "UserLogin";

	/**
	 * 网银充值
	 */
	public final static String CMD_NETSAVE = "NetSave";

	/**
	 * 自动扣款（放款）
	 */
	public final static String CMD_LOANS = "Loans";

	/**
	 * 自动扣款（还款）
	 */
	public final static String CMD_REPAYMENT = "Repayment";

	/**
	 * 前台用户间转账接口
	 */
	public final static String CMD_USRTRANSFER = "UsrTransfer";

	/**
	 * 转账（商户用）
	 */
	public final static String CMD_TRANSFER = "Transfer";

	/**
	 * 取现
	 */
	public final static String CMD_CASH = "Cash";

	/**
	 * 余额查询(页面)
	 */
	public final static String CMD_QUERYBALANCE = "QueryBalance";

	/**
	 * 余额查询(后台)
	 */
	public final static String CMD_QUERYBALANCEBG = "QueryBalanceBg";

	/**
	 * 商户子账户信息查询
	 */
	public final static String CMD_QUERYACCTS = "QueryAccts";

	/**
	 * 交易状态查询
	 */
	public final static String CMD_QUERYTRANSSTAT = "QueryTransStat";

	/**
	 * 商户扣款对账
	 */
	public final static String CMD_TRFRECONCILIATION = "TrfReconciliation";

	/**
	 * 投标对账(放款和还款对账) 、放还款对账
	 */
	public final static String CMD_RECONCILIATION = "Reconciliation";

	/**
	 * 取现对账
	 */
	public final static String CMD_CASHRECONCILIATION = "CashReconciliation";

	/**
	 * 充值对账
	 */
	public final static String CMD_SAVERECONCILIATION = "SaveReconciliation";

	/**
	 * 资金（货款）冻结
	 */
	public final static String CMD_USRFREEZE = "UsrFreezeBg";

	/**
	 * 资金（货款）解冻
	 */
	public final static String CMD_USRUNFREEZE = "UsrUnFreeze";

	/**
	 * 标的信息录入接口
	 */
	public final static String CMD_ADDBIDINFO = "AddBidInfo";

	/**
	 * 债权转让接口
	 */
	public final static String CMD_CREDITASSIGN = "CreditAssign";

	/**
	 * 主动投标
	 */
	public final static String CMD_INITIATIVETENDER = "InitiativeTender";

	/**
	 * 自动投标
	 */
	public final static String CMD_AUTOTENDER = "AutoTender";

	/**
	 * 自动投标计划
	 */
	public final static String CMD_AUTOTENDERPLAN = "AutoTenderPlan";

	/**
	 * 自动投标计划关闭
	 */
	public final static String CMD_AUTOTENDERPLANCLOSE = "AutoTenderPlanClose";

	/**
	 * 标的撤销
	 */
	public final static String CMD_TENDERCANCLE = "TenderCancle";

	/**
	 * 标的撤销
	 */
	public final static String CMD_QUERYTRANSTYPE = "TenderCancle";

	/**
		 * 
		 */
	public final static String CMD_QUERYCARDINFO = "QueryCardInfo";

	/**
	 * 用户账户支付
	 */
	public final static String CMD_USRACCPAY = "UsrAcctPay";

	/**
	 * 用户信息查询
	 */
	public final static String CMD_QUERYUSRINFO = "QueryUsrInfo";

	/**
	 * 交易明细查询
	 */
	public final static String CMD_QUERYTRANSDETAIL = "QueryTransDetail";

	/**
	 * 提现复核
	 */
	public final static String CMD_CASHAUDIT = "CashAudit";

	/**
	 * 批量还款
	 */
	public final static String CMD_BATCHREPAYMENT = "BatchRepayment";

	/**
	 * 债权查询
	 */
	public final static String CMD_CREDITASSIGNRECONCILIATION = "CreditAssignReconciliation";
	
	/**
	 * 企业开户
	 */
	public final static String CMD_CORPREGISTER = "CorpRegister";
	
	/**
	 * 企业开户状态查询
	 */
	public final static String CMD_CORPREGISTERQUERY = "CorpRegisterQuery";

	/**
	 * 重复还款标识
	 */
	public final static String REPAYMENT_ED = "351";
	
	/* ------------------- 上海银行托管 ------------------- */
	
	/**
	 * 用户换绑卡接口
	 */
	public final static String CMD_QUICKBINDING = "QuickBinding";
	
	/**
	 * 账户激活
	 */
	public final static String CMD_BOSACCTACTIVATE = "BosAcctActivate";
	
	/**
	 * 发送短信验证码
	 */
	public final static String CMD_SENDSMSCODE = "SendSmsCode";
	
	/**
	 * 充值
	 */
	public final static String CMD_DIRECTRECHARGE = "DirectRecharge";
	
	/**
	 * 代扣签约
	 */
	public final static String CMD_WITHHOLDINGSIGN = "WithHoldingSign";
	
	/**
	 * 代扣解约
	 */
	public final static String CMD_AWCANCEL = "AwCancel";
	
	/**
	 * 投标撤销
	 */
	public final static String CMD_AUTOTENDERCANCLE = "AutoTenderCancle";
	
	/**
	 * 代扣签约交易查询
	 */
	public final static String CMD_AWTRANSQUERY = "AwTransQuery";
	
	/**
	 * 代扣签约查询
	 */
	public final static String CMD_AWSIGNQUERY = "AwSignQuery";

	/**
	 * 加载汇付天下配置文件
	 */
	private static void loadProperties() {
		Logger.info("读取ChinaPnr配置文件...");
		hfConf = new Properties();
		try {
			hfConf.load(new FileInputStream(new File(path)));

		} catch (Exception e) {

			LoggerUtil.error(false, e, "读取汇付天下配置库异常");
		}
	}
	
	/**
	 * 初始化汇付天下必须字段、ChkValue字段、响应ChkValue字段 key集合
	 */
	private static void initMaps() {
		maps = new HashMap<String, String[]>();
		// 开户
		/*String[] userRegister_must = { "Version", "CmdId", "MerCustId",
				"BgRetUrl", "ChkValue" };
		String[] userRegister_chkValue = { "Version", "CmdId", "MerCustId",
				"BgRetUrl", "RetUrl", "UsrId", "UsrName", "IdType", "IdNo",
				"UsrMp", "UsrEmail", "MerPriv" };
		String[] userRegister_resp = { "CmdId", "RespCode", "MerCustId",
				"UsrId", "UsrCustId", "BgRetUrl", "TrxId", "RetUrl", "MerPriv" };
		maps.put("UserRegister_must", userRegister_must);
		maps.put("UserRegister_chkValue", userRegister_chkValue);
		maps.put("UserRegister_resp", userRegister_resp);*/

		// 登录
		String[] userLogin_must = { "Version", "CmdId", "MerCustId",
				"UsrCustId" };
		String[] userLogin_chkValue = {};
		maps.put("UserLogin_must", userLogin_must);
		maps.put("UserLogin_chkValue", userLogin_chkValue);

		// 余额查询(后台)
		String[] queryBalanceBg_must = { "Version", "CmdId", "MerCustId",
				"UsrCustId", "ChkValue" };
		String[] queryBalanceBg_chkValue = { "Version", "CmdId", "MerCustId",
				"UsrCustId" };
		String[] queryBalanceBg_resp = { "CmdId", "RespCode", "MerCustId",
				"UsrCustId", "AvlBal", "AcctBal", "FrzBal" };
		maps.put("QueryBalanceBg_must", queryBalanceBg_must);
		maps.put("QueryBalanceBg_chkValue", queryBalanceBg_chkValue);
		maps.put("QueryBalanceBg_resp", queryBalanceBg_resp);

		// 余额查询(页面)
		String[] queryBalance_must = { "Version", "CmdId", "MerCustId",
				"UsrCustId", "ChkValue" };
		String[] queryBalance_chkValue = { "Version", "CmdId", "MerCustId",
				"UsrCustId" };
		maps.put("QueryBalance_must", queryBalance_must);
		maps.put("QueryBalance_chkValue", queryBalance_chkValue);

		// 网银充值
		String[] netSave_must = { "Version", "CmdId", "MerCustId", "UsrCustId",
				"OrdId", "OrdDate", "TransAmt", "BgRetUrl", "ChkValue" };
		String[] netSave_chkValue = { "Version", "CmdId", "MerCustId",
				"UsrCustId", "OrdId", "OrdDate", "GateBusiId", "OpenBankId",
				"DcFlag", "TransAmt", "RetUrl", "BgRetUrl", "MerPriv" };
		String[] netSave_resp = { "CmdId", "RespCode", "MerCustId",
				"UsrCustId", "OrdId", "OrdDate", "TransAmt", "TrxId", "RetUrl",
				"BgRetUrl", "MerPriv" };
		maps.put("NetSave_must", netSave_must);
		maps.put("NetSave_chkValue", netSave_chkValue);
		maps.put("NetSave_resp", netSave_resp);

		// 资金（货款）冻结
		String[] usrFreeze_must = { "Version", "CmdId", "MerCustId",
				"UsrCustId", "OrdId", "OrdDate", "TransAmt", "BgRetUrl",
				"ChkValue" };
		String[] usrFreeze_chkValue = { "Version", "CmdId", "MerCustId",
				"UsrCustId", "SubAcctType", "SubAcctId", "OrdId", "OrdDate",
				"TransAmt", "RetUrl", "BgRetUrl", "MerPriv" };
		String[] usrFreeze_resp = { "CmdId", "RespCode", "MerCustId",
				"UsrCustId", "SubAcctType", "SubAcctId", "OrdId", "OrdDate",
				"TransAmt", "RetUrl", "BgRetUrl", "TrxId", "MerPriv" };
		maps.put("UsrFreezeBg_must", usrFreeze_must);
		maps.put("UsrFreezeBg_chkValue", usrFreeze_chkValue);
		maps.put("UsrFreezeBg_resp", usrFreeze_resp);

		// 资金（货款）解冻
		String[] usrUnFreeze_must = { "Version", "CmdId", "MerCustId", "OrdId",
				"OrdDate", "TrxId", "BgRetUrl", "ChkValue" };
		String[] usrUnFreeze_chkValue = { "Version", "CmdId", "MerCustId",
				"OrdId", "OrdDate", "TrxId", "RetUrl", "BgRetUrl", "MerPriv" };
		String[] usrUnFreeze_resp = { "CmdId", "RespCode", "MerCustId",
				"OrdId", "OrdDate", "TrxId", "RetUrl", "BgRetUrl", "MerPriv" };
		maps.put("UsrUnFreeze_must", usrUnFreeze_must);
		maps.put("UsrUnFreeze_chkValue", usrUnFreeze_chkValue);
		maps.put("UsrUnFreeze_resp", usrUnFreeze_resp);

		// 自动扣款(还款)2.0
		/*String[] repayment_must = { "Version", "CmdId", "MerCustId", "OrdId",
				"OrdDate", "OutCustId", "SubOrdId", "SubOrdDate", "TransAmt",
				"Fee", "InCustId", "BgRetUrl", "ChkValue" };
		String[] repayment_chkValue = { "Version", "CmdId", "MerCustId",
				"OrdId", "OrdDate", "OutCustId", "SubOrdId", "SubOrdDate",
				"OutAcctId", "TransAmt", "Fee", "InCustId", "InAcctId",
				"DivDetails", "FeeObjFlag", "BgRetUrl", "MerPriv", "ReqExt" };
		String[] repayment_resp = { "CmdId", "RespCode", "MerCustId", "OrdId",
				"OrdDate", "OutCustId", "SubOrdId", "SubOrdDate", "OutAcctId",
				"TransAmt", "Fee", "InCustId", "InAcctId", "FeeObjFlag",
				"BgRetUrl", "MerPriv", "RespExt" };
		maps.put("Repayment_must", repayment_must);
		maps.put("Repayment_chkValue", repayment_chkValue);
		maps.put("Repayment_resp", repayment_resp);*/
		
		// 自动扣款(还款)3.0
		String[] repayment_must = { "Version", "CmdId", "MerCustId", "OrdId",
				"OrdDate", "OutCustId", "SubOrdId", "SubOrdDate", "OutAcctId",
				"PrincipalAmt","InterestAmt", "Fee", "InCustId", "InAcctId", "BgRetUrl",
				"ChkValue" };
		String[] repayment_chkValue = { "Version", "CmdId", "MerCustId", "ProId",
				"OrdId", "OrdDate", "OutCustId", "SubOrdId", "SubOrdDate",
				"OutAcctId", "PrincipalAmt", "InterestAmt", "Fee", "InCustId", "InAcctId",
				"DivDetails", "FeeObjFlag", "DzObject","BgRetUrl", "MerPriv", "ReqExt" };
		String[] repayment_resp = { "CmdId", "RespCode", "MerCustId", "ProId" ,"OrdId",
				"OrdDate", "OutCustId", "SubOrdId", "SubOrdDate", "OutAcctId",
				"PrincipalAmt","InterestAmt" , "Fee", "InCustId", "InAcctId", "FeeObjFlag",
				"DzObject","BgRetUrl", "MerPriv", "RespExt" };
		maps.put("Repayment_must", repayment_must);
		maps.put("Repayment_chkValue", repayment_chkValue);
		maps.put("Repayment_resp", repayment_resp);

		// 债权转让接口
		String[] creditAssign_must = { "Version", "CmdId", "MerCustId",
				"SellCustId", "CreditAmt", "CreditDealAmt", "BidDetails",
				"Fee", "DivAcctId", "DivAmt", "BuyCustId", "OrdId", "OrdDate",
				"BgRetUrl", "ChkValue" };
		String[] creditAssign_chkValue = { "Version", "CmdId", "MerCustId",
				"SellCustId", "CreditAmt", "CreditDealAmt", "BidDetails",
				"Fee", "DivDetails", "BuyCustId", "OrdId", "OrdDate", "RetUrl",
				"BgRetUrl", "MerPriv", "ReqExt","PageType","LcId","TotalLcAmt" };
		String[] creditAssign_resp = { "CmdId", "RespCode", "MerCustId",
				"SellCustId", "CreditAmt", "CreditDealAmt", "Fee", "BuyCustId",
				"OrdId", "OrdDate", "RetUrl", "BgRetUrl", "MerPriv", "RespExt","LcId","TotalLcAmt" };
		maps.put("CreditAssign_must", creditAssign_must);
		maps.put("CreditAssign_chkValue", creditAssign_chkValue);
		maps.put("CreditAssign_resp", creditAssign_resp);

		// 用户绑卡
		String[] userBindCard_must = { "Version", "CmdId", "MerCustId",
				"UsrCustId", "BgRetUrl", "ChkValue" };
		String[] userBindCard_chkValue = { "Version", "CmdId", "MerCustId",
				"UsrCustId", "BgRetUrl", "MerPriv" };
		String[] userBindCard_resp = { "CmdId", "RespCode", "MerCustId",
				"OpenAcctId", "OpenBankId", "UsrCustId", "TrxId", "BgRetUrl",
				"MerPriv" };
		maps.put("UserBindCard_must", userBindCard_must);
		maps.put("UserBindCard_chkValue", userBindCard_chkValue);
		maps.put("UserBindCard_resp", userBindCard_resp);

		// 2.0主动投标
		String[] initiativeTender_must = { "Version", "CmdId", "MerCustId",
				"OrdId", "OrdDate", "TransAmt", "UsrCustId", "MaxTenderRate",
				"BorrowerDetails", "IsFreeze", "BgRetUrl", "ChkValue" };
		String[] initiativeTender_chkValue = { "Version", "CmdId", "MerCustId",
				"OrdId", "OrdDate", "TransAmt", "UsrCustId", "MaxTenderRate",
				"BorrowerDetails", "IsFreeze", "FreezeOrdId", "RetUrl",
				"BgRetUrl", "MerPriv","ReqExt" };
		String[] initiativeTender_resp = { "CmdId", "RespCode", "MerCustId",
				"OrdId", "OrdDate", "TransAmt", "UsrCustId", "TrxId",
				"IsFreeze", "FreezeOrdId", "FreezeTrxId", "RetUrl", "BgRetUrl",
				"MerPriv", "RespExt" };
		maps.put("InitiativeTender_must", initiativeTender_must);
		maps.put("InitiativeTender_chkValue", initiativeTender_chkValue);
		maps.put("InitiativeTender_resp", initiativeTender_resp);

		// 自动投标计划
		String[] autoTenderPlan_must = { "Version", "CmdId", "MerCustId",
				"UsrCustId", "TenderPlanType", "ChkValue" };
		String[] autoTenderPlan_chkValue = { "Version", "CmdId", "MerCustId",
				"UsrCustId", "TenderPlanType", "TransAmt", "RetUrl", "MerPriv" };
		String[] autoTenderPlan_resp = { "CmdId", "RespCode", "MerCustId",
				"UsrCustId", "TenderPlanType", "TransAmt", "RetUrl", "MerPriv" };
		maps.put("AutoTenderPlan_must", autoTenderPlan_must);
		maps.put("AutoTenderPlan_chkValue", autoTenderPlan_chkValue);
		maps.put("AutoTenderPlan_resp", autoTenderPlan_resp);
		// 自动投标计划关闭
		String[] autoTenderPlanClose_must = { "Version", "CmdId", "MerCustId",
				"UsrCustId", "ChkValue" };
		String[] autoTenderPlanClose_chkValue = { "Version", "CmdId",
				"MerCustId", "UsrCustId", "RetUrl", "MerPriv" };
		String[] autoTenderPlanClose_resp = { "CmdId", "RespCode", "MerCustId",
				"UsrCustId", "RetUrl", "MerPriv" };
		maps.put("AutoTenderPlanClose_must", autoTenderPlanClose_must);
		maps.put("AutoTenderPlanClose_chkValue", autoTenderPlanClose_chkValue);
		maps.put("AutoTenderPlanClose_resp", autoTenderPlanClose_resp);

		// 自动投标2.0
		String[] autoTender_must = { "Version", "CmdId", "MerCustId", "OrdId",
				"OrdDate", "TransAmt", "UsrCustId", "MaxTenderRate",
				"BorrowerDetails", "IsFreeze", "BgRetUrl", "ChkValue" };
		String[] autoTender_chkValue = { "Version", "CmdId", "MerCustId",
				"OrdId", "OrdDate", "TransAmt", "UsrCustId", "MaxTenderRate",
				"BorrowerDetails", "IsFreeze", "FreezeOrdId", "RetUrl",
				"BgRetUrl", "MerPriv", "ReqExt" };
		String[] autoTender_resp = { "CmdId", "RespCode", "MerCustId", "OrdId",
				"OrdDate", "TransAmt", "UsrCustId", "TrxId", "IsFreeze",
				"FreezeOrdId", "FreezeTrxId", "RetUrl", "BgRetUrl", "MerPriv",
				"RespExt" };
		maps.put("AutoTender_must", autoTender_must);
		maps.put("AutoTender_chkValue", autoTender_chkValue);
		maps.put("AutoTender_resp", autoTender_resp);

		// 标的信息录入
		String[] addBidInfo_must = { "Version", "CmdId", "MerCustId", "ProId",
				"BorrCustId", "BorrTotAmt", "YearRate", "RetType",
				"BidStartDate", "BidEndDate", "RetAmt", "RetDate", "ProArea",
				"BgRetUrl", "ChkValue" };
		String[] addBidInfo_chkValue = { "Version", "CmdId", "MerCustId",
				"ProId", "BorrCustId", "BorrTotAmt", "YearRate", "RetType",
				"BidStartDate", "BidEndDate", "RetAmt", "RetDate",
				"GuarCompId", "GuarAmt", "ProArea", "BgRetUrl", "MerPriv",
				"ReqExt" };
		String[] addBidInfo_resp = { "CmdId", "RespCode", "MerCustId", "ProId",
				"BorrCustId", "BorrTotAmt", "GuarCompId", "GuarAmt", "ProArea",
				"BgRetUrl", "MerPriv", "RespExt" };
		maps.put("AddBidInfo_must", addBidInfo_must);
		maps.put("AddBidInfo_chkValue", addBidInfo_chkValue);
		maps.put("AddBidInfo_resp", addBidInfo_resp);

		// 取现
		String[] cash_must = { "Version", "CmdId", "MerCustId", "OrdId",
				"UsrCustId", "TransAmt", "BgRetUrl", "ChkValue" };
		String[] cash_chkValue = { "Version", "CmdId", "MerCustId", "OrdId",
				"UsrCustId", "TransAmt", "ServFee", "ServFeeAcctId",
				"OpenAcctId", "RetUrl", "BgRetUrl", "Remark", "MerPriv",
				"ReqExt", "PageType" };
		String[] cash_resp = { "CmdId", "RespCode", "MerCustId", "OrdId",
				"UsrCustId", "TransAmt", "OpenAcctId", "OpenBankId", "FeeAmt",
				"FeeCustId", "FeeAcctId", "ServFee", "ServFeeAcctId", "RetUrl",
				"BgRetUrl", "MerPriv", "RespExt" };
		maps.put("Cash_must", cash_must);
		maps.put("Cash_chkValue", cash_chkValue);
		maps.put("Cash_resp", cash_resp);

		// 投标撤销2.0
		String[] tenderCancle_must = { "Version", "CmdId", "MerCustId",
				"OrdId", "OrdDate", "TransAmt", "UsrCustId", "IsUnFreeze",
				"BgRetUrl", "ChkValue" };
		String[] tenderCancle_chkValue = { "Version", "CmdId", "MerCustId",
				"OrdId", "OrdDate", "TransAmt", "UsrCustId", "IsUnFreeze",
				"UnFreezeOrdId", "FreezeTrxId", "RetUrl", "BgRetUrl",
				"MerPriv", "ReqExt" };
		String[] tenderCancle_resp = { "CmdId", "RespCode", "MerCustId",
				"OrdId", "OrdDate", "TransAmt", "UsrCustId", "IsUnFreeze",
				"UnFreezeOrdId", "FreezeTrxId", "RetUrl", "BgRetUrl",
				"MerPriv", "RespExt" };
		maps.put("TenderCancle_must", tenderCancle_must);
		maps.put("TenderCancle_chkValue", tenderCancle_chkValue);
		maps.put("TenderCancle_resp", tenderCancle_resp);

		// 交易状态查询
		String[] queryTransStat_must = { "Version", "CmdId", "MerCustId",
				"OrdId", "OrdDate", "QueryTransType", "ChkValue" };
		String[] queryTransStat_chkValue = { "Version", "CmdId", "MerCustId",
				"OrdId", "OrdDate", "QueryTransType" };
		String[] queryTransStat_resp = { "CmdId", "RespCode", "MerCustId",
				"OrdId", "OrdDate", "QueryTransType", "TransStat" };
		// 冻结解冻交易查询返回验签key
		String[] queryTransStat_Freeze_resp = { "CmdId", "RespCode",
				"MerCustId", "OrdId", "OrdDate", "QueryTransType", "TransStat",
				"TransAmt", "TrxId" };
		maps.put("QueryTransStat_must", queryTransStat_must);
		maps.put("QueryTransStat_chkValue", queryTransStat_chkValue);
		maps.put("QueryTransStat_resp", queryTransStat_resp);
		maps.put("QueryTransStat_Freeze_resp", queryTransStat_Freeze_resp);

		// 自动扣款（放款）
		String[] loans_must = { "Version", "CmdId", "MerCustId", "OrdId",
				"OrdDate", "OutCustId", "TransAmt", "Fee", "SubOrdId",
				"SubOrdDate", "InCustId", "IsDefault", "IsUnFreeze",
				"BgRetUrl", "ChkValue" };
		String[] loans_chkValue = { "Version", "CmdId", "MerCustId", "OrdId",
				"OrdDate", "OutCustId", "TransAmt", "Fee", "SubOrdId",
				"SubOrdDate", "InCustId", "DivDetails", "FeeObjFlag",
				"IsDefault", "IsUnFreeze", "UnFreezeOrdId", "FreezeTrxId",
				"BgRetUrl", "MerPriv", "ReqExt" };
		String[] loans_resp = { "CmdId", "RespCode", "MerCustId", "OrdId",
				"OrdDate", "OutCustId", "OutAcctId", "TransAmt", "Fee",
				"InCustId", "InAcctId", "SubOrdId", "SubOrdDate", "FeeObjFlag",
				"IsDefault", "IsUnFreeze", "UnFreezeOrdId", "FreezeTrxId",
				"BgRetUrl", "MerPriv", "RespExt" };
		maps.put("Loans_must", loans_must);
		maps.put("Loans_chkValue", loans_chkValue);
		maps.put("Loans_resp", loans_resp);

		// 转账（商户用）
		String[] transfer_must = { "Version", "CmdId", "OrdId", "OutCustId",
				"OutAcctId", "TransAmt", "InCustId", "BgRetUrl", "ChkValue" };
		String[] transfer_chkValue = { "Version", "CmdId", "OrdId",
				"OutCustId", "OutAcctId", "TransAmt", "InCustId", "InAcctId",
				"RetUrl", "BgRetUrl", "MerPriv" };
		String[] transfer_resp = { "CmdId", "RespCode", "OrdId", "OutCustId",
				"OutAcctId", "TransAmt", "InCustId", "InAcctId", "RetUrl",
				"BgRetUrl", "MerPriv" };
		maps.put("Transfer_must", transfer_must);
		maps.put("Transfer_chkValue", transfer_chkValue);
		maps.put("Transfer_resp", transfer_resp);

		// 商户子账户信息查询
		String[] queryAccts_must = { "Version", "CmdId", "MerCustId",
				"ChkValue" };
		String[] queryAccts_chkValue = { "Version", "CmdId", "MerCustId" };
		String[] queryAccts_resp = { "CmdId", "RespCode", "MerCustId" };
		maps.put("QueryAccts_must", queryAccts_must);
		maps.put("QueryAccts_chkValue", queryAccts_chkValue);
		maps.put("QueryAccts_resp", queryAccts_resp);

		// 商户扣款对账
		String[] trfReconciliation_must = { "Version", "CmdId", "MerCustId",
				"BeginDate", "EndDate", "PageNum", "PageSize", "ChkValue" };
		String[] trfReconciliation_chkValue = { "Version", "CmdId",
				"MerCustId", "BeginDate", "EndDate", "PageNum", "PageSize" };
		String[] trfReconciliation_resp = { "CmdId", "RespCode", "MerCustId",
				"BeginDate", "EndDate", "PageNum", "PageSize", "TotalItems" };
		maps.put("TrfReconciliation_must", trfReconciliation_must);
		maps.put("TrfReconciliation_chkValue", trfReconciliation_chkValue);
		maps.put("TrfReconciliation_resp", trfReconciliation_resp);

		// 投标对账(放款和还款对账)
		String[] reconciliation_must = { "Version", "CmdId", "MerCustId",
				"BeginDate", "EndDate", "PageNum", "PageSize",
				"QueryTransType", "ChkValue" };
		String[] reconciliation_chkValue = { "Version", "CmdId", "MerCustId",
				"BeginDate", "EndDate", "PageNum", "PageSize", "QueryTransType" };
		String[] reconciliation_resp = { "CmdId", "RespCode", "MerCustId",
				"BeginDate", "EndDate", "PageNum", "PageSize", "TotalItems",
				"QueryTransType" };
		maps.put("Reconciliation_must", reconciliation_must);
		maps.put("Reconciliation_chkValue", reconciliation_chkValue);
		maps.put("Reconciliation_resp", reconciliation_resp);

		// 取现对账
		String[] cashReconciliation_must = { "Version", "CmdId", "MerCustId",
				"BeginDate", "EndDate", "PageNum", "PageSize", "ChkValue" };
		String[] cashReconciliation_chkValue = { "Version", "CmdId",
				"MerCustId", "BeginDate", "EndDate", "PageNum", "PageSize" };
		String[] cashReconciliation_resp = { "CmdId", "RespCode", "MerCustId",
				"BeginDate", "EndDate", "PageNum", "PageSize", "TotalItems" };
		maps.put("CashReconciliation_must", cashReconciliation_must);
		maps.put("CashReconciliation_chkValue", cashReconciliation_chkValue);
		maps.put("CashReconciliation_resp", cashReconciliation_resp);

		// 充值对账
		String[] saveReconciliation_must = { "Version", "CmdId", "MerCustId",
				"BeginDate", "EndDate", "PageNum", "PageSize", "ChkValue" };
		String[] saveReconciliation_chkValue = { "Version", "CmdId",
				"MerCustId", "BeginDate", "EndDate", "PageNum", "PageSize" };
		String[] saveReconciliation_resp = { "CmdId", "RespCode", "MerCustId",
				"BeginDate", "EndDate", "PageNum", "PageSize", "TotalItems" };
		maps.put("SaveReconciliation_must", saveReconciliation_must);
		maps.put("SaveReconciliation_chkValue", saveReconciliation_chkValue);
		maps.put("SaveReconciliation_resp", saveReconciliation_resp);

		// 充值对账
		String[] queryCardInfo_must = { "Version", "CmdId", "MerCustId",
				"UsrCustId", "ChkValue" };
		String[] queryCardInfo_chkValue = { "Version", "CmdId", "MerCustId",
				"UsrCustId", "CardId" };
		String[] queryCardInfo_resp = { "CmdId", "RespCode", "MerCustId",
				"UsrCustId", "CardId" };
		maps.put("QueryCardInfo_must", queryCardInfo_must);
		maps.put("QueryCardInfo_chkValue", queryCardInfo_chkValue);
		maps.put("QueryCardInfo_resp", queryCardInfo_resp);

		// 前台用户间转账接口
		String[] usrTransfer_must = { "Version", "CmdId", "MerCustId", "OrdId",
				"RetUrl", "BgRetUrl", "UsrCustId", "InUsrCustId", "TransAmt",
				"MerPriv", "ChkValue" };
		String[] usrTransfer_chkValue = { "Version", "CmdId", "OrdId",
				"UsrCustId", "MerCustId", "TransAmt", "InUsrCustId", "RetUrl",
				"BgRetUrl", "MerPriv", "ReqExt" };
		String[] usrTransfer_resp = { "CmdId", "RespCode", "OrdId",
				"MerCustId", "UsrCustId", "TransAmt", "InUsrCustId", "RetUrl",
				"BgRetUrl", "MerPriv", "RespExt" };
		maps.put("UsrTransfer_must", usrTransfer_must);
		maps.put("UsrTransfer_chkValue", usrTransfer_chkValue);
		maps.put("UsrTransfer_resp", usrTransfer_resp);

		// 前台用户间转账接口
		String[] usrAcctPay_must = { "Version", "CmdId", "OrdId", "UsrCustId",
				"MerCustId", "TransAmt", "InAcctId", "InAcctType", "BgRetUrl",
				"MerPriv", "ChkValue" };
		String[] usrAcctPay_chkValue = { "Version", "CmdId", "OrdId",
				"UsrCustId", "MerCustId", "TransAmt", "InAcctId", "InAcctType",
				"RetUrl", "BgRetUrl", "MerPriv", "ReqExt" };
		String[] usrAcctPay_resp = { "CmdId", "RespCode", "OrdId", "UsrCustId",
				"MerCustId", "TransAmt", "InAcctId", "InAcctType", "RetUrl",
				"BgRetUrl", "MerPriv" };
		maps.put("UsrAcctPay_must", usrAcctPay_must);
		maps.put("UsrAcctPay_chkValue", usrAcctPay_chkValue);
		maps.put("UsrAcctPay_resp", usrAcctPay_resp);

		// 用户信息查询接口
		String[] queryUsrInfo_must = { "Version", "CmdId", "MerCustId",
				"CertId", "ChkValue" };
		String[] queryUsrInfo_chkValue = { "Version", "CmdId", "MerCustId",
				"CertId", "ReqExt" };
		String[] queryUsrInfo_resp = { "CmdId", "RespCode", "MerCustId",
				"UsrCustId", "UsrId", "CertId", "UsrStat", "RespExt" };
		maps.put("QueryUsrInfo_must", queryUsrInfo_must);
		maps.put("QueryUsrInfo_chkValue", queryUsrInfo_chkValue);
		maps.put("QueryUsrInfo_resp", queryUsrInfo_resp);

		// 交易明细
		String[] queryTransDetail_must = { "Version", "CmdId", "MerCustId",
				"OrdId", "QueryTransType", "ChkValue" };
		String[] queryTransDetail_chkValue = { "Version", "CmdId", "MerCustId",
				"OrdId", "QueryTransType", "ReqExt" };
		String[] queryTransDetail_resp = { "CmdId", "RespCode", "MerCustId",
				"UsrCustId", "OrdId", "OrdDate", "QueryTransType", "TransAmt",
				"TransStat", "FeeAmt", "FeeCustId", "FeeAcctId", "GateBusiId",
				"RespExt" };
		maps.put("QueryTransDetail_must", queryTransDetail_must);
		maps.put("QueryTransDetail_chkValue", queryTransDetail_chkValue);
		maps.put("QueryTransDetail_resp", queryTransDetail_resp);

		// 取现复核
		String[] cashAudit_must = { "Version", "CmdId", "MerCustId", "OrdId",
				"UsrCustId", "TransAmt", "AuditFlag", "BgRetUrl", "ChkValue" };
		String[] cashAudit_chkValue = { "Version", "CmdId", "MerCustId",
				"OrdId", "UsrCustId", "TransAmt", "AuditFlag", "RetUrl",
				"BgRetUrl", "MerPriv" };
		String[] cashAudit_resp = { "CmdId", "RespCode", "MerCustId", "OrdId",
				"UsrCustId", "TransAmt", "OpenAcctId", "OpenBankId",
				"AuditFlag", "RetUrl", "BgRetUrl", "MerPriv" };
		maps.put("CashAudit_must", cashAudit_must);
		maps.put("CashAudit_chkValue", cashAudit_chkValue);
		maps.put("CashAudit_resp", cashAudit_resp);

		// 批量还款
		String[] batchRepayment_must = { "Version", "CmdId", "MerCustId",
				"OutCustId", "BatchId", "MerOrdDate", "BgRetUrl", "ProId",
				"InDetails", "ChkValue" };
		String[] batchRepayment_chkValue = { "Version", "CmdId", "MerCustId",
				"OutCustId", "OutAcctId", "BatchId", "MerOrdDate", "InDetails",
				"BgRetUrl", "MerPriv", "ReqExt", "ProId" };
		String[] batchRepayment_resp = { "CmdId", "RespCode", "MerCustId",
				"OutCustId", "OutAcctId", "BatchId", "MerOrdDate", "BgRetUrl",
				"MerPriv", "SucNum", "FailNum", "ErrMsg", "ProId", "ReqExt" };
		maps.put("BatchRepayment_must", batchRepayment_must);
		maps.put("BatchRepayment_chkValue", batchRepayment_chkValue);
		maps.put("BatchRepayment_resp", batchRepayment_resp);

		// 债权转让查询接口
		String[] creditAssignReconciliation_must = { "Version", "CmdId",
				"MerCustId", "BeginDate", "EndDate", "PageNum", "PageSize",
				"ChkValue" };
		String[] creditAssignReconciliation_chkValue = { "Version", "CmdId",
				"MerCustId", "OrdId", "BeginDate", "EndDate", "SellCustId",
				"BuyCustId", "PageNum", "PageSize", "ReqExt" };
		String[] creditAssignReconciliation_resp = { "CmdId", "RespCode",
				"MerCustId", "OrdId", "BeginDate", "EndDate", "SellCustId",
				"BuyCustId", "PageNum", "PageSize", "TotalItems", "RespExt" };
		maps.put("CreditAssignReconciliation_must",
				creditAssignReconciliation_must);
		maps.put("CreditAssignReconciliation_chkValue",
				creditAssignReconciliation_chkValue);
		maps.put("CreditAssignReconciliation_resp",
				creditAssignReconciliation_resp);
		
		/* ------------ 上海银行托管 ------------*/
		
		// 上海银行托管发送短信
		String[] sendSmsCode_must = { "Version", "CmdId", "MerCustId",
				"BusiType", "UsrMp", "ChkValue" };
		String[] sendSmsCode_chkValue = { "Version", "CmdId",
				"MerCustId", "UsrCustId", "BusiType", "OpenAcctId", "UsrMp", "SmsTempType" };
		String[] sendSmsCode_resp = { "CmdId", "RespCode", "MerCustId",
				"UsrCustId", "BusiType", "SmsSeq", "DepoBankSeq" };
		maps.put("SendSmsCode_must", sendSmsCode_must);
		maps.put("SendSmsCode_chkValue", sendSmsCode_chkValue);
		maps.put("SendSmsCode_resp", sendSmsCode_resp);

		// 上海银行托管开户
		String[] userRegister_must = { "Version", "CmdId", "MerCustId",	"BgRetUrl",
				"UsrId", "UsrName", "IdType", "IdNo", "UsrMp", "CardId", "BankId", 
				"ProvId", "AreaId", "SmsCode", "SmsSeq", "ChkValue" };
		String[] userRegister_chkValue = { "Version", "CmdId", "MerCustId",
				"BgRetUrl", "RetUrl", "UsrId", "IdType", "IdNo",
				"UsrMp", "UsrEmail", "MerPriv", "PageType", "CardId", "BankId",
				"ProvId", "AreaId" };
		String[] userRegister_resp = { "CmdId", "RespCode", "MerCustId",
				"UsrId", "UsrCustId", "BgRetUrl", "TrxId", "RetUrl", "MerPriv",
				"RegStat", "BankAcctId" };
		maps.put("UserRegister_must", userRegister_must);
		maps.put("UserRegister_chkValue", userRegister_chkValue);
		maps.put("UserRegister_resp", userRegister_resp);

		// 上海银行托管账户激活
		// 存量用户迁移到上海银行后，需进行激活操作，方可进行后续资金操作
		String[] bosAcctActivate_must = { "Version", "CmdId", "MerCustId",
				"UsrCustId", "OrdId", "OrdDate", "RetUrl", "BgRetUrl" };
		String[] bosAcctActivate_chkValue = { "Version", "CmdId", "MerCustId",
				"UsrCustId", "OrdId", "OrdDate", "PageType", "RetUrl", "BgRetUrl" };
		String[] bosAcctActivate_resp = { "CmdId", "RespCode",	"MerCustId",
				"UsrCustId", "OrdId", "CardId", "BankId", "UsrMp", "ProvId", "AreaId",
				"DepoBankSeq", "PageType", "RetUrl", "BgRetUrl" };
		maps.put("BosAcctActivate_must", bosAcctActivate_must);
		maps.put("BosAcctActivate_chkValue", bosAcctActivate_chkValue);
		maps.put("BosAcctActivate_resp", bosAcctActivate_resp);
		
		// 上海银行托管用户换绑卡
		String[] quickBinding_must = { "Version", "CmdId", "MerCustId",	"UsrCustId",
				"OrdId", "TradeType", "BankId", "OpenAcctId", "UsrMp", "ProvId",
				"AreaId", "SmsCode", "SmsSeq", "OrgSmsExt", "BgRetUrl"};
		String[] quickBinding_chkValue = { "Version", "CmdId",	"MerCustId", "UsrCustId",
				"OrdId", "TradeType", "BankId", "OpenAcctId", "UsrMp", "ProvId",
				"AreaId", "SmsCode", "SmsSeq", "OrgSmsExt", "BgRetUrl", "MerPriv" };
		String[] quickBinding_resp = { "CmdId", "RespCode", "MerCustId", "UsrCustId",
				"OrdId", "TradeType", "OpenAcctId", "TrxId", "BindDepoBankSeq", "BgRetUrl",
				"MerPriv" };
		maps.put("QuickBinding_must", quickBinding_must);
		maps.put("QuickBinding_chkValue", quickBinding_chkValue);
		maps.put("QuickBinding_resp", quickBinding_resp);
		
		// 上海银行托管充值
		String[] directRecharge_must = { "Version", "CmdId", "MerCustId",
				"UsrCustId", "OrdId", "OrdDate", "GateBusiId", "TransAmt", "BgRetUrl" };
		String[] directRecharge_chkValue = { "Version", "CmdId",
				"MerCustId", "UsrCustId", "OrdId", "OrdDate", "GateBusiId",
				"BankId", "TransAmt", "SubAcctId", "SmsSeq", "SmsCode", "SignId", "RetUrl", "BgRetUrl" };
		String[] directRecharge_resp = { "CmdId", "RespCode", "MerCustId",
				"UsrCustId", "OrdId", "OrdDate", "TrxId", "DepoBankSeq", "GateBusiId",
				"BankId", "TransAmt", "RetUrl", "BgRetUrl"};
		maps.put("DirectRecharge_must", directRecharge_must);
		maps.put("DirectRecharge_chkValue", directRecharge_chkValue);
		maps.put("DirectRecharge_resp", directRecharge_resp);

		// 上海银行托管代扣签约
		String[] withHoldingSign_must = { "Version", "CmdId", "MerCustId",
				"UsrCustId", "ProId", "WhSignAmt", "Nper", "OrdId", "OrdDate",
				"EffectiveDate", "BgRetUrl" };
		// md5 -> RSA
		String[] withHoldingSign_chkValue = { "Version", "CmdId", "MerCustId",
				"UsrCustId", "ProId", "WhSignAmt", "Nper", "OrdId", "OrdDate",
				"EffectiveDate", "DeductionsVerify", "OpenBankId", "OpenAcctId",
				"BgRetUrl", "RetUrl", "PageType", "MerPriv", "ReqExt" };
		// md5 -> RSA
		String[] withHoldingSign_resp = { "CmdId", "RespCode", "SecRespCode",
				"MerCustId", "UsrCustId", "ProId", "WhSignAmt", "Nper", "OrdId",
				"OrdDate", "EffectiveDate", "DeductionsVerify", "DeductionsVerifyAmt",
				"OpenBankId", "OpenAcctId", "BgRetUrl", "RetUrl", "MerPriv"};
		maps.put("WithHoldingSign_must", withHoldingSign_must);
		maps.put("WithHoldingSign_chkValue", withHoldingSign_chkValue);
		maps.put("WithHoldingSign_resp", withHoldingSign_resp);

		// 上海银行托管代扣解约
		String[] awCancel_must = { "Version", "CmdId", "MerCustId",
				"OrdId", "BgRetUrl" };
		// md5 -> RSA
		String[] awCancel_chkValue = { "Version", "CmdId", "MerCustId",
				"OrdId", "RetUrl", "BgRetUrl", "MerPriv" };
		// md5 -> RSA
		String[] awCancel_resp = { "CmdId", "RespCode", "MerCustId",
				"OrdId", "RetUrl", "BgRetUrl", "MerPriv" };
		maps.put("AwCancel_must", awCancel_must);
		maps.put("AwCancel_chkValue", awCancel_chkValue);
		maps.put("AwCancel_resp", awCancel_resp);

		// 上海银行托管投标撤销
		String[] autoTenderCancle_must = { "Version", "CmdId", "MerCustId",
				"OrdId", "OrdDate", "TransAmt", "UsrCustId", "IsUnFreeze", "BgRetUrl" };
		// md5 -> RSA
		String[] autoTenderCancle_chkValue = { "Version", "CmdId", "MerCustId",
				"OrdId", "OrdDate", "TransAmt", "UsrCustId", "IsUnFreeze", "UnFreezeOrdId",
				"FreezeTrxId", "BgRetUrl", "MerPriv", "ReqExt" };
		// md5 -> RSA
		String[] autoTenderCancle_resp = { "CmdId", "RespCode", "MerCustId", "OrdId",
				"OrdDate", "TransAmt", "UsrCustId", "IsUnFreeze", "UnFreezeOrdId",
				"FreezeTrxId", "BgRetUrl", "MerPriv", "ReqExt" };
		maps.put("AutoTenderCancle_must", autoTenderCancle_must);
		maps.put("AutoTenderCancle_chkValue", autoTenderCancle_chkValue);
		maps.put("AutoTenderCancle_resp", autoTenderCancle_resp);

		// 上海银行托管代扣签约交易查询
		String[] awTransQuery_must = { "Version", "CmdId", "MerCustId", "OrdId",
				"OrdDate", "SignOrdId" };
		// md5 -> RSA
		String[] awTransQuery_chkValue = { "Version", "CmdId", "MerCustId", "OrdId",
				"OrdDate", "SignOrdId", "MerPriv" };
		// md5 -> RSA
		String[] awTransQuery_resp = { "CmdId", "RespCode", "MerCustId", "OrdId",
				"OrdDate", "SignOrdId", "Stat", "TransAmt", "FeeAmt", "MerPriv",
				"FeeCustId", "FeeAcctId", "MerPriv" };
		maps.put("AwTransQuery_must", awTransQuery_must);
		maps.put("AwTransQuery_chkValue", awTransQuery_chkValue);
		maps.put("AwTransQuery_resp", awTransQuery_resp);

		// 上海银行托管代扣签约查询
		String[] awSignQuery_must = { "Version", "CmdId", "MerCustId", "SignOrdId" };
		// md5 -> RSA
		String[] awSignQuery_chkValue = { "Version", "CmdId", "MerCustId",
				"SignOrdId", "MerPriv" };
		// md5 -> RSA
		String[] awSignQuery_resp = { "CmdId", "RespCode", "MerCustId",
				"SignOrdId", "Stat", "MerPriv" };
		maps.put("AwSignQuery_must", awSignQuery_must);
		maps.put("AwSignQuery_chkValue", awSignQuery_chkValue);
		maps.put("AwSignQuery_resp", awSignQuery_resp);
		
		// 企业开户
		String[]  corpRegister_must = { "Version", "CmdId", "MerCustId", "UsrId", "BusiCode",
				"BgRetUrl" };
		String[] corpRegister_chkValue = { "Version", "CmdId", "MerCustId", "UsrId", "UsrName", 
				"InstuCode", "BusiCode", "TaxCode", "MerPriv", "GuarType", "BgRetUrl", "ReqExt" };
		String[] corpRegister_resp = { "CmdId", "RespCode", "MerCustId", "UsrId", "UsrName", "UsrCustId",
				"AuditStat", "TrxId", "OpenBankId", "CardId", "BgRetUrl", "RespExt" };
		maps.put("CorpRegister_must", corpRegister_must);
		maps.put("CorpRegister_chkValue", corpRegister_chkValue);
		maps.put("CorpRegister_resp", corpRegister_resp);

		// 企业开户状态查询
		String[]  corpRegisterQuery_must = { "Version", "CmdId", "MerCustId", "BusiCode" };
		String[] corpRegisterQuery_chkValue = { "Version", "CmdId", "MerCustId", "BusiCode", "ReqExt" };
		String[] corpRegisterQuery_resp = { "CmdId", "RespCode", "MerCustId", "UsrCustId", "UsrId",
				"AuditStat", "BusiCode", "RespExt" };
		maps.put("CorpRegisterQuery_must", corpRegisterQuery_must);
		maps.put("CorpRegisterQuery_chkValue", corpRegisterQuery_chkValue);
		maps.put("CorpRegisterQuery_resp", corpRegisterQuery_resp);
	}

	/**
	 * 获取汇付天下请求必须字段集合
	 * 
	 * @param key
	 * @return
	 */
	public static String[] getMustKeys(String key) {
		return maps.get(key + "_must");
	}

	/**
	 * 获取汇付天下ChkValue组成字段集合
	 * 
	 * @param key
	 * @return
	 */
	public static String[] getChkValueKeys(String key) {
		return maps.get(key + "_chkValue");
	}

	/**
	 * 获取汇付天下响应ChkValue响应key集合
	 * 
	 * @param key
	 * @return
	 */
	public static String[] getRespChkValueKeys(String key) {
		return maps.get(key + "_resp");
	}

	/**
	 * 不同支付平台差异性融合
	 * 
	 * @return
	 */
	public static void initSupport() {
		Constants.LOAN_SERVICE_FEE_MAXRATE = Convert.strToDouble(hfConf.getProperty("borrowManageMaxrate"), 50.0); 
		Constants.DEBT_TRANSFER_MAXRATE = Convert.strToDouble(hfConf.getProperty("debtTransferMaxrate"), 10.0);
		Constants.WITHDRAW_MAXRATE = Convert.strToDouble(hfConf.getProperty("maxWithdrawRate"), 1.0);
	}

	/**
	 * 资源文件中文乱码解决
	 *
	 * @param properties
	 * @param key
	 * @param encoding
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年2月15日
	 */
	public static String getProperty(Properties properties, String key, String encoding){
		String value = properties.getProperty(key);
		
		if (value == null) {
			
			return "";
		}

		try {
			value = new String(value.getBytes("ISO8859-1"), encoding);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

		return value;
	}
	
}
