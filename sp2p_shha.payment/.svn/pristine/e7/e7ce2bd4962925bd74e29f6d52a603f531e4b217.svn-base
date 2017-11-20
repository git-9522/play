package fy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.json.XML;

import com.google.gson.Gson;

import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.ResultInfo;
import daos.PaymentRequstDao;
import models.entity.t_payment_request;

public class FyUtils{
	
	public static final SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
	public static final Gson gson = new Gson();
	
	public static Log log = LogFactory.getLog(FyUtils.class);
	
	protected static PaymentRequstDao paymentRequstDao = Factory.getDao(PaymentRequstDao.class);
	
	/**
	 * 将xml转化成map
	 * @param data
	 * @return
	 */
	public static Map<String, String> parseXmlToMap(String data){		
		if (StringUtils.isBlank(data)){
			LoggerUtil.error(false, "连接超时");
			
			return null;
		}
		
		Map<String, String> dataMap = new HashMap<String, String>();
		
		try {
			JSONObject jsonObj = XML.toJSONObject(data);
			JSONObject ap = jsonObj.getJSONObject("ap");
			JSONObject plain = ap.getJSONObject("plain");
			@SuppressWarnings("unchecked")
			java.util.Iterator<String> iterator = plain.keys();
		
			while (iterator.hasNext()){
				String key = iterator.next();
				if (plain.get(key).getClass().isAssignableFrom(String.class)){
					dataMap.put(key, plain.get(key).toString());
				} else{
					dataMap.put(key, "");
				}
			}
		} catch (Exception e){
			LoggerUtil.error(false, e, "将xml转化成map异常");
			
			return null;
		}
		
		String str = data.substring(data.indexOf("<plain>"), data.indexOf("</plain>") + 8);
		String signature = data.substring(data.indexOf("<signature>") + 11, data.indexOf("</signature>"));
		dataMap.put("signature", signature.toString());
		dataMap.put("plain", str.toString());
		
		return dataMap;
	}
	
	/**
	 * 签名校验、状态码判断、防止重单
	 * @param paramMap 返回参数map
	 * @param desc 描述
	 * @param type 接口类型
	 * @param payType 是否需要防重单
	 * @return
	 */
	public static ResultInfo checkSign(Map<String, String> paramMap, String desc, String type, FyPayType payType){
		ResultInfo result = new ResultInfo();
		
		//第一步:判断连接是否超时
		if (paramMap == null){
			LoggerUtil.info(false, "连接超时");
			
			result.code = -1;
			result.msg = "连接超时";
			
			return result;
		}
		
		//是否校验签名
		boolean check = true;
		
		if (paramMap.containsKey("data_type") && "1".equals(paramMap.get("data_type"))){ 
			check = false;
		}
		
		if (check) {
			//第二步:校验签名
			boolean flag = FyUtils.verifySign(paramMap.get("plain"), paramMap.get("signature"));
			
			if (flag == false){
				LoggerUtil.info(false, "%s签名失败", desc);
				
				result.code = -1;
				result.msg = "返回数据签名校验失败";
				
				return result;
			}
		}
		//第三步:状态码判断；根据不同接口，不同处理
		//满标放款
		if (type.equals(FyPayType.LOANS.value)){
			if (!paramMap.get("resp_code").equals("0000") && !paramMap.get("resp_code").equals("3122")){ //3122状态码:原授权交易已全部完成
				LoggerUtil.info(false, FyUtils.getErrorMsg(paramMap.get("resp_code")));
				
				result.code = -1;
				result.msg = "满标放款失败";
				
				return result;
			}
		}
		
		//商户与用户间转账
		if (type.equals(FyPayType.MERCHANTANDPERSIONTRANSFER.value)){
			if (!paramMap.get("resp_code").equals("0000") && !paramMap.get("resp_code").equals("5345")){ //5345状态码:流水号重复，未处理成功时，第三方不会记录该流水号已使用
				LoggerUtil.info(false, FyUtils.getErrorMsg(paramMap.get("resp_code")));
				
				result.code = -1;
				result.msg = "商户与用户间转账失败";
				
				return result;
			}
		}
		
		//债权转让
		if (type.equals(FyPayType.CREDITASSIGN.value)){	
			if (!paramMap.get("resp_code").equals("0000") && !paramMap.get("resp_code").equals("5345")){ //5345状态码:流水号重复，未处理成功时，第三方不会记录该流水号已使用
				LoggerUtil.info(false, FyUtils.getErrorMsg(paramMap.get("resp_code")));
				
				result.code = -1;
				result.msg = "债权转让失败";
				
				return result;
			}
		}
		
		//解冻保证金
		if (type.equals(FyPayType.USRUNFREEZE.value)) {
			if (!paramMap.get("resp_code").equals("0000") && !paramMap.get("resp_code").equals("3124")
					 && !paramMap.get("resp_code").equals("5345")) {
				
				LoggerUtil.info(false, FyUtils.getErrorMsg(paramMap.get("resp_code")));
				
				result.code = -1;
				result.msg = "解冻保证金失败";
				
				return result;
			}
		}
		
		//还款
		if (type.equals(FyPayType.REPAYMENT.value)){
			if (!paramMap.get("resp_code").equals("0000") && !paramMap.get("resp_code").equals("5345")){ //5345状态码:流水号重复，未处理成功时，第三方不会记录该流水号已使用
				LoggerUtil.info(false, FyUtils.getErrorMsg(paramMap.get("resp_code")));
				
				result.code = -1;
				result.msg = "还款失败";
				
				return result;
			}
		}
		
		//标的撤销
		if (type.equals(FyPayType.TENDERCANCLE.value)) {
			if (!paramMap.get("resp_code").equals("0000") && !paramMap.get("resp_code").equals("3122")) { //3122状态码:原授权交易已全部完成
				LoggerUtil.info(false, FyUtils.getErrorMsg(paramMap.get("resp_code")));
				
				result.code = -1;
				result.msg = "标的撤销失败";
				
				return result;
			}
		}
		
		//其它
		if (!type.equals(FyPayType.LOANS.value) && !type.equals(FyPayType.MERCHANTANDPERSIONTRANSFER.value) 
				&& !type.equals(FyPayType.REPAYMENT.value) && !type.equals(FyPayType.CREDITASSIGN.value)
				&& !type.equals(FyPayType.USRUNFREEZE.value) && !type.equals(FyPayType.TENDERCANCLE.value)){
			if (!paramMap.get("resp_code").equals("0000")){
				LoggerUtil.info(false, FyUtils.getErrorMsg(paramMap.get("resp_code")));
				
				result.code = -1;
				result.msg = FyUtils.getErrorMsg(paramMap.get("resp_code"));
				result.obj = paramMap.get("resp_code");
				
				return result;
			}
		}
		
		//第四步，防止重单
		if (payType.isAddRequestRecord){
			String requestMark = paramMap.get("mchnt_txn_ssn").toString();
			
			t_payment_request pr = paymentRequstDao.findByColumn("mark = ?", requestMark);
			
			if (pr == null){
				result.code = -1;
				result.msg = payType.value + "查询托管请求记录失败";
				
				LoggerUtil.info(false, payType.value + "查询托管请求记录失败");
				
				return result;
			}
			
			int row = paymentRequstDao.updateReqStatusToSuccess(requestMark);
			
			if (row > 0){
				result.code = 1;
				result.msg = payType.value + "更新请求状态成功";
				
				return result;
			}
			
			if (row == 0){
				result.code = ResultInfo.ALREADY_RUN;
				result.msg = payType.value + "已成功执行";
				
				return result;
			}
			
			if (row < 0){
				result.code = ResultInfo.ERROR_SQL;
				result.msg = payType.value + "更新请求状态时，数据库异常";
				
				LoggerUtil.info(false, payType.value + "更新请求状态时，数据库异常");
				
				return result;
			}
		}
		
		result.code = 1;
		result.msg = payType.value + "更新请求状态成功";
		
		return result;
	}
	
	/**
	 * 表单返回通知校验
	 * @param paramMap 返回参数map
	 * @param desc 描述
	 * @param payType 是否需要防重单
	 * @param keys 待签名key值，按顺序来
	 * @return
	 */
	public static ResultInfo checkFormSign(Map<String, String> paramMap, String desc, FyPayType payType, String... keys) {
		ResultInfo result = new ResultInfo();
		
		if (paramMap == null) {
			result.code = -1;
			result.msg = payType.value + "连接超时";
			
			LoggerUtil.info(false, "连接超时");
			
			return result;
		}
		
		//是否校验签名
		boolean check = true;
		
		if (paramMap.containsKey("data_type") && "1".equals(paramMap.get("data_type"))) 
			check = false;
		
		if (check) {
			String sign = "";
			
			for (int i = 0; i < keys.length; i++) {
				sign = sign + paramMap.get(keys[i]) + "|";
			}
			
			sign = sign.substring(0, sign.length() - 1);
			boolean flag = FyUtils.verifySign(sign, paramMap.get("signature"));
			
			if (flag == false) {
				result.code = -1;
				result.msg = payType.value + "返回数据校验签名失败";
				
				LoggerUtil.info(false, "%s签名失败", desc);
				
				return result;
			}
		}
		
		if (!paramMap.get("resp_code").equals("0000")) {
			result.code = -1;
			result.msg = payType.value + "第三方未处理成功";
			
			LoggerUtil.info(false, FyUtils.getErrorMsg(paramMap.get("resp_code")));
			
			return result;
		}
		
		if (payType.isAddRequestRecord) {
			String requestMark = paramMap.get("mchnt_txn_ssn").toString();
			
			t_payment_request pr = paymentRequstDao.findByColumn("mark = ?", requestMark);
			
			if (pr == null) {
				result.code = -1;
				result.msg = payType.value + "查询托管请求记录失败";
				
				LoggerUtil.info(false, payType.value + "查询托管请求记录失败");
				
				return result;
			}
			
			int row = paymentRequstDao.updateReqStatusToSuccess(requestMark);
			
			if (row > 0) {
				result.code = 1;
				result.msg = payType.value + "更新请求状态成功";
				
				return result;
			}
			
			if (row == 0) {
				result.code = ResultInfo.ALREADY_RUN;
				result.msg = payType.value + "已成功执行";
				
				return result;
			}
			
			if (row < 0) {
				result.code = ResultInfo.ERROR_SQL;
				result.msg = payType.value + "更新请求状态时，数据库异常";
				
				LoggerUtil.info(false, payType.value + "更新请求状态时，数据库异常");
				
				return result;
			}
		}
		
		result.code = 1;
		result.msg = payType.value + "表单返回通知校验成功";
		
		return result;
	}
	
	/**
	 * 生成签名
	 * @param map
	 * @return
	 */
	public static String createSign(Map<String, String> map){
		Map<String, String> dataMap = new HashMap<String, String>();
		dataMap.putAll(map);
		dataMap.remove("signature");
		dataMap.remove("body");
		String[] dataArray = new String[dataMap.size()];
		int i = 0;
		String keys = "";
		
		for (Map.Entry<String, String> entry : dataMap.entrySet()){
			dataArray[i] = entry.getKey();
			i++;
			keys = keys +entry.getKey() + "|";
		}
		
		Arrays.sort(dataArray, String.CASE_INSENSITIVE_ORDER);

		StringBuffer sign = new StringBuffer("");
		
		for (int j = 0; j < dataArray.length; j++) {
			sign = sign.append(dataMap.get(dataArray[j]) + "|");
		}
		
		String signatureStr = FyUtils.sign(sign.substring(0, sign.length() - 1));
		
		return signatureStr;
	}

	/**
	 * init:初始化私钥
	 */
	public static void initPrivateKey() {
		try {
			if (FyConsts.privateKey == null) {
				FyConsts.privateKey = getPrivateKey(FyConsts.privateKeyPath);
			}
		} catch (Exception e) {
			LoggerUtil.info(false, "SecurityUtils初始化失败：%s", e.getMessage());
			e.printStackTrace();
			LoggerUtil.info(false, "密钥初始化失败");
		}
	}

	/**
	 * 初始化公钥
	 */
	public static void initPublicKey() {
		try {
			if (FyConsts.publicKey == null){
				FyConsts.publicKey = getPublicKey(FyConsts.publicKeyPath);
			}
		} catch (Exception e){
			LoggerUtil.info(false, "SecurityUtils初始化失败：%s", e.getMessage());
			e.printStackTrace();
			LoggerUtil.info(false, "密钥初始化失败");
		}
	}
	
	/**
	 * 对传入字符串进行签名
	 * 
	 * @param inputStr
	 * @return @
	 */
	public static String sign(String inputStr){
		String result = null;
		
		try {
			if (FyConsts.privateKey == null){
				// 初始化
				initPrivateKey();
			}
			
			byte[] tByte;
			Signature signature = Signature.getInstance("SHA1withRSA", "BC");
			signature.initSign(FyConsts.privateKey);
			signature.update(inputStr.getBytes("UTF-8"));
			tByte = signature.sign();
			result = Base64.encode(tByte);
		} catch (Exception e){
			e.printStackTrace();
			LoggerUtil.info(false, "密钥初始化失败");
		}
		
		return result;
	}
	
	/**
	 * 对富友返回的数据进行验签
	 * @param src 返回数据明文
	 * @param signValue 返回数据签名
	 * @return
	 */
	public static boolean verifySign(String src, String signValue){
		boolean bool = false;
		
		try {
			if (FyConsts.publicKey == null){
				initPublicKey();
			}
			
			Signature signature = Signature.getInstance("SHA1withRSA", "BC");
			signature.initVerify(FyConsts.publicKey);
			signature.update(src.getBytes("UTF-8"));
			bool = signature.verify(Base64.decode(signValue));
		} catch (Exception e){
			e.printStackTrace();
			LoggerUtil.info(false, "密钥初始化失败");
		}
		
		return bool;
	}
	
	private static PrivateKey getPrivateKey(String filePath){
		String base64edKey = readFile(filePath);
		KeyFactory kf;
		PrivateKey privateKey = null;
		
		try{
			kf = KeyFactory.getInstance("RSA", "BC");
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64
					.decode(base64edKey));
			privateKey = kf.generatePrivate(keySpec);
		} catch (Exception e){
			e.printStackTrace();
			LoggerUtil.info(false, "密钥初始化失败");
		}
		
		return privateKey;
	}
	
	private static PublicKey getPublicKey(String filePath){
		String base64edKey = readFile(filePath);
		KeyFactory kf;
		PublicKey publickey = null;
		
		try {
			kf = KeyFactory.getInstance("RSA", "BC");
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64
					.decode(base64edKey));
			publickey = kf.generatePublic(keySpec);
		} catch (Exception e) {
			e.printStackTrace();
			LoggerUtil.info(false, "密钥初始化失败");
		}
		
		return publickey;
	}

	private static String readFile(String fileName){
		try {
			File f = new File(fileName);
			FileInputStream in = new FileInputStream(f);
			int len = (int) f.length();

			byte[] data = new byte[len];
			int read = 0;
			
			while (read < len) {
				read += in.read(data, read, len - read);
			}
			
			in.close();
			
			return new String(data);
		} catch (IOException e) {
			
			return null;
		}
	}
	
	/**
	 * 带前缀的流水号
	 * @param pre (满标审核通过:BS; 满标审核不通过:BF; 标的借款管理费:BM; 标的投标奖励:BA; 标的解冻保证金:BUF; 债权转让:D; 本金垫付:A; 还款:R; 投资利息管理费:RM; 债权转让手续费:DM; 垫付还款:RA)备注:定义前缀的目的是，防止重复还款，重复转账
	 * @param orderNum
	 * @return
	 */
	public static String createBillNo(String pre, String orderNum){
		
		return pre + orderNum;
	}
	
	/**
	 * 获取错误信息
	 * @param code
	 * @return
	 */
	public static String getErrorMsg(String code){
		String msg = FyConsts.error.get(code);
		
		return msg == null ? "未知错误:" + code : msg;
	}
	
	/**
	 * 格式化金额,保留2位小数(将元转化为分)
	 * @param money 金额(单位元)
	 * @return 单位分
	 */
	public static String formatAmountToFen(double money){
		
		return String.format("%.0f", money * 100);
	}
	
	/**
	 * 格式化金额,保留2位小数(将分转化为元)
	 * @param money 金额(单位元)
	 * @return 单位分
	 */
	public static String formatAmountToYuan(double money){
		
		return String.format("%.2f", money / 100);
	}
	
	/**
	 * 构造表单信息
	 * @param maps 提交参数
	 * @param action 请求地址
	 * @return
	 */
	public static String createHtml(Map<String,String> maps, String action){
		StringBuffer buffer = new StringBuffer();
		buffer.append("<!DOCTYPE html>")
		.append("<html>")
		.append("<head>")
		.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />")
		.append("<title>Servlet AccountServlet</title>")
		.append("</head>")
		.append("<body>")
		.append("<h4>处理中...</h4>")
		.append("<form action="+action+" id=\"frm1\" method=\"post\">");
		
		for (Entry<String, String> entry : maps.entrySet()){
			buffer.append("<input type=\"hidden\" name=" + entry.getKey() + " value="+entry.getValue() + " />");
		}
		
		buffer.append("</form>")
		.append("<script language=\"javascript\">")
		.append("document.getElementById(\"frm1\").submit();")
		.append("</script>")
		.append("</body>")
		.append("</html>");
		
		return buffer.toString();
	}
	
	/**
	 * 将xml转换为map(用于plain节点)
	 * @param xmlStr
	 * @return
	 */
	public static Map<String, String> parseXmlToMapUsedToPlain(String xmlStr) {
		Map<String, String> dataMap = new HashMap<String, String>();
		
		try {
			JSONObject jsonObj = XML.toJSONObject(xmlStr);
			JSONObject plain = jsonObj.getJSONObject("plain");
			JSONObject results = plain.getJSONObject("results");
			JSONObject result = results.getJSONObject("result");
			@SuppressWarnings("unchecked")
			java.util.Iterator<String> iterator = result.keys();
			
			while (iterator.hasNext()){
				String key = iterator.next();
				if (result.get(key).getClass().isAssignableFrom(String.class)){
					dataMap.put(key, result.get(key).toString());
				} else{
					dataMap.put(key, "");
				}
			}
		} catch (Exception e){
			LoggerUtil.error(false, e, "将xml转化成map异常");
			
			return null;
		}
		
		return dataMap;
	}
	
	/**
	 * 响应第三方平台成功接收请求
	 * @param resp_code 响应码
	 * @param mchnt_cd 商户代码
	 * @param mchnt_txn_ssn 流水号
	 * @param signature 签名数据
	 * @return
	 */
	public static String notifyThirdParty(String resp_code, String mchnt_cd, String mchnt_txn_ssn, String signature) {
		StringBuilder rd = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		rd.append("<ap><plain><resp_code>").append(resp_code).append("</resp_code><mchnt_cd>").append(mchnt_cd);
		rd.append("</mchnt_cd><mchnt_txn_ssn>").append(mchnt_txn_ssn).append("</mchnt_txn_ssn></plain><signature>");
		rd.append(signature).append("</signature></ap>");
		
		return rd.toString();
	}
	
	/**
	 * 校验第三方通知表单签名
	 * @param paramMap 返回参数map
	 * @param desc 描述
	 * @param payType 是否需要防重单
	 * @param keys 待签名key值，按顺序来
	 * @return
	 */
	public static ResultInfo checkThirdPartyNotifyFormSign(Map<String, String> paramMap, String desc, FyPayType payType, String... keys) {
		ResultInfo result = new ResultInfo();
		
		if (paramMap == null) {
			result.code = -1;
			result.msg = payType.value + "连接超时";
			
			LoggerUtil.info(false, "连接超时");
			
			return result;
		}
		
		String sign = "";
		
		for (int i = 0; i < keys.length; i++) {
			sign = sign + paramMap.get(keys[i]) + "|";
		}
		
		sign = sign.substring(0, sign.length() - 1);
		boolean flag = FyUtils.verifySign(sign, paramMap.get("signature"));
		
		if (flag == false) {
			result.code = -1;
			result.msg = payType.value + "返回数据校验签名失败";
			
			LoggerUtil.info(false, "%s签名失败", desc);
			
			return result;
		}
		
		if (payType.isAddRequestRecord) {
			String requestMark = paramMap.get("mchnt_txn_ssn").toString();
			
			t_payment_request pr = paymentRequstDao.findByColumn("mark = ?", requestMark);
			
			if (pr == null) {
				result.code = -1;
				result.msg = payType.value + "查询托管请求记录失败";
				
				LoggerUtil.info(false, payType.value + "查询托管请求记录失败");
				
				return result;
			}
			
			int row = paymentRequstDao.updateReqStatusToSuccess(requestMark);
			
			if (row > 0) {
				result.code = 1;
				result.msg = payType.value + "更新请求状态成功";
				
				return result;
			}
			
			if (row == 0) {
				result.code = ResultInfo.ALREADY_RUN;
				result.msg = payType.value + "已成功执行";
				
				return result;
			}
			
			if (row < 0) {
				result.code = ResultInfo.ERROR_SQL;
				result.msg = payType.value + "更新请求状态时，数据库异常";
				
				LoggerUtil.info(false, payType.value + "更新请求状态时，数据库异常");
				
				return result;
			}
		}
		
		result.code = 1;
		result.msg = payType.value + "校验第三方通知表单成功";
		
		return result;
	}
}
