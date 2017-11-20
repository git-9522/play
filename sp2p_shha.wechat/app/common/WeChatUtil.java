package common;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;

import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import play.Logger;
import play.cache.Cache;
import play.libs.Encrypt;
import play.mvc.Http;
import play.mvc.Scope.Session;

import com.shove.gateway.weixin.gongzhong.vo.message.Message;
import com.shove.gateway.weixin.gongzhong.vo.message.reply.ReplyImageMessage;
import com.shove.gateway.weixin.gongzhong.vo.message.reply.ReplyTextMessage;
import common.constants.ConfConst;
import common.constants.WXConstants;


/**
 * 微信工具类
 * 
 * @author liudong
 * @createDate 2016年5月5日
 */
public class WeChatUtil {
	
	/**
	 * POST请求 
	 * 
	 * @param
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年5月9日
	 */
	public static JSONObject doPostStr(String url,String outStr) {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost httpost = new HttpPost(url);
		JSONObject jsonObject = null;
		String result = null;
		try {
			httpost.setEntity(new StringEntity(outStr,"UTF-8"));
			HttpResponse response = client.execute(httpost);
			result = EntityUtils.toString(response.getEntity(),"UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		jsonObject = JSONObject.fromObject(result);
		Logger.info("微信请求响应结果:"+jsonObject);
		
		//参数转换
		JSONObject json = new JSONObject();
		json.put("code", jsonObject.get("errcode"));
		json.put("msg", jsonObject.get("errmsg"));
		return json;
	}
	
	/**
	 * get请求
	 *
	 * @param url 请求url
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年5月12日
	 */
	public static JSONObject doGetStr(String url){
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		JSONObject jsonObject = null;
		HttpResponse httpResponse;
		try {
			httpResponse = client.execute(httpGet);
			HttpEntity entity = httpResponse.getEntity();
			if(entity != null){
				String result = EntityUtils.toString(entity,"UTF-8");
				jsonObject = JSONObject.fromObject(result);
				Logger.info("微信请求响应结果:"+jsonObject);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return jsonObject;
	}
	
	/**
	 * 编码方式设置
	 * 
	 * @param
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年5月6日
	 */
	public static String urlEncodeUTF8(String source) {
		String result = source;
		try {
			result = URLEncoder.encode(source,"utf-8");
		} catch (UnsupportedEncodingException e) {
			Logger.error(e.getMessage());
		}
		
		return result;
	}
	
	/**
	 * 对openId进行加密
	 * @param openId
	 * @return
	 */
	public static String encryptOpenId(String openId) {
		long time = System.currentTimeMillis();
		//openId有效期为1个小时
		Cache.set("wechat_time" + Session.current().getId(), time, "60mn");
		String openId3DES = WXConstants.TOKEN + "," + time + "," + openId;
		openId3DES = Encrypt.encrypt3DES(openId3DES, ConfConst.ENCRYPTION_KEY_DES);
		return openId3DES;
	}
	
	/**
	 * 对openId进行解密
	 */
	public static String decrypt3DESOpenId(String open3DESId) {
		Object time = Cache.get("wechat_time" + Session.current().getId());
		if(null == time) {
			return null;
			
		}
		
		String[] strs = Encrypt.decrypt3DES(open3DESId, ConfConst.ENCRYPTION_KEY_DES).split(",");
		String openId = strs[2];
		
		return openId;
	}
	
	/**
	 * 编码字节
	 *
	 * @param content
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年5月12日
	 */
	public static int getByteSize(String content) {  
	    int size = 0;  
	    if (null != content) {  
	        try {  
	            // 汉字采用utf-8编码时占3个字节  
	            size = content.getBytes("utf-8").length;  
	        } catch (UnsupportedEncodingException e) {  
	            Logger.error(e.getMessage());
	        }  
	    }  
	    return size;  
	}  
	
	/**
	 * 获取当前时间
	 * 
	 * @param
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年5月10日
	 */
	public static long getCurrentTime() {
		
		return System.currentTimeMillis();
	}
	
	/**
	 * 回复文本消息
	 *
	 * @param ReplyTextMessage
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年5月12日
	 */
	public static void replyTextMessage(ReplyTextMessage message) throws IOException {
		/**
		 * 	
		 	<xml>
			<ToUserName><![CDATA[{0}]]></ToUserName>
			<FromUserName><![CDATA[{1}]]></FromUserName>
			<CreateTime>{2,number,#}</CreateTime>
			<MsgType><![CDATA[{3}]]></MsgType>
			<Content><![CDATA[{4}]]></Content>
			</xml>
		 */
		String xml = "<xml><ToUserName><![CDATA[{0}]]></ToUserName><FromUserName><![CDATA[{1}]]></FromUserName><CreateTime>{2,number,#}</CreateTime><MsgType><![CDATA[{3}]]></MsgType><Content><![CDATA[{4}]]></Content></xml>";

		xml = MessageFormat.format(
				xml,
				new Object[] {
					message.getToUserName(),
					message.getFromUserName(),
					Long.valueOf(message.getCreateTime()), 
					Message.TYPE_TEXT,
					message.getContent()
				}
			);
		Http.Response.current().print(xml);
	}

	/**
	 * 回复图片消息
	 *
	 * @param ReplyImageMessage
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年5月12日
	 */
	public static void replyImageMessage(ReplyImageMessage message) throws IOException {
		/**
		 XML:
		 	<xml>
			<ToUserName><![CDATA[{0}]]></ToUserName>
			<FromUserName><![CDATA[{1}]]></FromUserName>
			<CreateTime>{2,number,#}</CreateTime>
			<MsgType><![CDATA[{3}]]></MsgType>
			<Image>
				<MediaId><![CDATA[{4}]]></MediaId>
			</Image>
			</xml>
		 */
		String xml = "<xml><ToUserName><![CDATA[{0}]]></ToUserName><FromUserName><![CDATA[{1}]]></FromUserName><CreateTime>{2,number,#}</CreateTime><MsgType><![CDATA[{3}]]></MsgType><Image><MediaId><![CDATA[{4}]]></MediaId></Image></xml>";

		xml = MessageFormat.format(
				xml,
				new Object[] {
						message.getToUserName(),
						message.getFromUserName(),
						Long.valueOf(message.getCreateTime()),
						Message.TYPE_IMAGE,
						message.getMediaId()
					}
			);

		Http.Response.current().print(xml);
	}
}
