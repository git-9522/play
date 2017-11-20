package hf;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import chinapnr.SecureLink;
import common.utils.HttpUtil;
import common.utils.LoggerUtil;
import net.sf.json.JSONObject;

public class HfUtils {
	
    /** MD5签名类型 **/
    public static final String SIGN_TYPE_MD5 = "M";

    /** RSA签名类型 **/
    public static final String SIGN_TYPE_RSA = "R";

    /** RSA验证签名成功结果 **/
    public static final int RAS_VERIFY_SIGN_SUCCESS = 0;
    
    /** 商户客户号 **/
    public static final String RECV_MER_ID = HfConsts.MERID;

    /** 商户公钥文件地址 **/
    public static final String MER_PUB_KEY_PATH = HfConsts.MER_PUB_KEY_PATH;

    /** 商户私钥文件地址 **/
    public static final String MER_PRI_KEY_PATH = HfConsts.MER_PRI_KEY_PATH;
    
    /**
     * RSA方式加签
     *
     * @param forEncryptionStr
     * @return
     * @throws Exception
     *
     * @author huangyunsong
     * @createDate 2016年1月7日
     */
    public static String encryptByRSA(String forEncryptionStr) {
        SecureLink sl = new SecureLink();
        int result = sl.SignMsg(RECV_MER_ID, MER_PRI_KEY_PATH, forEncryptionStr);
        if (result < 0) {
        	
           return null;
        }
        
        return sl.getChkValue();
    }

    /**
     * RSA方式验签
     *
     * @param forEncryptionStr
     * @param chkValue
     * @return
     * @throws Exception
     *
     * @author huangyunsong
     * @createDate 2016年1月7日
     */
    public static boolean verifyByRSA(String forEncryptionStr, String chkValue) {
        try {
            int verifySignResult = new SecureLink().VeriSignMsg(MER_PUB_KEY_PATH, forEncryptionStr, chkValue);
            return verifySignResult == RAS_VERIFY_SIGN_SUCCESS;
        } catch (Exception e) {

        	LoggerUtil.error(false, e, "RSA方式验签异常");
        	return false;
        }
    }
	
	/**
	 * 生成html表单
	 *
	 * @param maps
	 * @param action
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月7日
	 */
	public static String createFormHtml(Map<String,String> maps,String action){
		StringBuffer buffer = new StringBuffer();
		buffer.append("<!DOCTYPE html>")
			  .append("<html>")
			  .append("<head>")
			  .append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />")
			  .append("<title>Servlet AccountServlet</title>")
			  .append("</head>")
			  .append("<body>")
			  .append("<form action="+action+" id=\"frm1\" method=\"post\">");		
		for(Entry<String, String> entry : maps.entrySet()){
			buffer.append("<input type=\"hidden\" name="+entry.getKey()+" value="+entry.getValue()+" />");
		}
		
		buffer.append("</form>")
			  .append("<script language=\"javascript\">")
			  .append("document.getElementById(\"frm1\").submit();")
			  .append("</script>")
			  .append("</body>")
			  .append("</html>");
		return buffer.toString();
	}
	
	public static Map<String, String> httpPost(Map<String, String> reqParams){
		String respStr  = HttpUtil.postMethod(HfConsts.CHINAPNR_URL, reqParams, "UTF-8");
		
		System.out.println(respStr);
		LoggerUtil.info(false, "HTTP请求参数:【%s】", respStr);
		
		return jsonToMap(respStr);
	}
	
	/**
	 * json To map
	 * @param json
	 * @return
	 */
	public static Map<String,String> jsonToMap(String json){
		JsonObject jsonObj = new JsonParser().parse(json).getAsJsonObject();
		return jsonToMap(jsonObj);
	}
	
	/**
	 * json To map
	 * @param json
	 * @return
	 */
	private static Map<String,String> jsonToMap(JsonObject json){
		Set<Entry<String, JsonElement>> set =json.entrySet();
		Map<String,String> maps = new HashMap<String, String>();
		for(Entry<String, JsonElement> entry : set){
			maps.put(entry.getKey(), (entry.getValue() instanceof JsonNull | entry.getValue() == null)? "":(entry.getValue() instanceof JsonArray)?entry.getValue().getAsJsonArray().toString():entry.getValue().getAsString());
		}
		return maps;
	}


	/**
	 * 格式化金额,保留2位小数
	 * @param money 金额
	 * @return
	 */
	public static String formatAmount(double money){
		
		return String.format("%.2f", money);
		
	}
	
	/**
	 * 获取格式日期(当前时间)
	 * @param format
	 */
	public static String getFormatDate(String format){	
		
		return new SimpleDateFormat(format).format(new Date());
	}
	
	/**
	 * 获取格式日期
	 * @param format
	 */
	public static final String getFormatDate(String format, Date date){	
		return new SimpleDateFormat(format).format(date);
	}
	
	/**
	 * 获取银行卡
	 *
	 * @param key
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月9日
	 */
	public static String getBankName(String code) {
		JSONObject banks = JSONObject.fromObject(HfConsts.BANK_LIST);
		
		return banks.containsKey(code)?banks.getString(code):code;
	}
	
}
