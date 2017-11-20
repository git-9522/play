package service.wechat;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.shove.Xml;
import com.shove.gateway.weixin.gongzhong.GongZhongObject;
import com.shove.gateway.weixin.gongzhong.ReceiveMessageInterface;
import com.shove.gateway.weixin.gongzhong.utils.GongZhongUtils;
import com.shove.gateway.weixin.gongzhong.vo.message.receive.ReceiveClickMessage;
import com.shove.gateway.weixin.gongzhong.vo.message.receive.ReceiveGroupMessageNotice;
import com.shove.gateway.weixin.gongzhong.vo.message.receive.ReceiveImageMessage;
import com.shove.gateway.weixin.gongzhong.vo.message.receive.ReceiveLinkMessage;
import com.shove.gateway.weixin.gongzhong.vo.message.receive.ReceiveLocationMessage;
import com.shove.gateway.weixin.gongzhong.vo.message.receive.ReceiveSubscribeMessage;
import com.shove.gateway.weixin.gongzhong.vo.message.receive.ReceiveTextMessage;
import com.shove.gateway.weixin.gongzhong.vo.message.receive.ReceiveVideoMessage;
import com.shove.gateway.weixin.gongzhong.vo.message.receive.ReceiveVoiceMessage;

import common.WeChatUtil;
import common.constants.WXConstants;
import common.utils.ResultInfo;
import net.sf.json.JSONObject;
import play.Logger;
import play.cache.Cache;
import play.mvc.Http;


/**
 * 
 * shove组件中有这个封装类，这里单独抽离出来
 * 
 * @author fefrg
 *
 */
public class WeChatGongZhongService extends GongZhongObject {
	public static final String APIURL = "https://api.weixin.qq.com/cgi-bin/";
	public static final String MEDIAURL = "http://file.api.weixin.qq.com/cgi-bin/media/";
			
	public static String execute(ReceiveMessageInterface receiveMessageInterface)throws Exception {
		ResultInfo result = new ResultInfo();
		
		Http.Response response = Http.Response.current();
		Http.Request request = Http.Request.current();

		response.encoding = "utf-8";
		request.encoding = "utf-8";

		String method = request.method;// 得到是GET或者是POST
		if ("GET".equals(method)) {
			result = verifyDeveloper();

			if (result.code == 1) {

				System.out.println("接入成功");
				response.print(result.obj);
			}

			return null;
		}

		requestMessage(receiveMessageInterface);

		return null;
	}

	/**
	 * 微信接入
	 * 
	 * @param
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年5月6日
	 */
	public static ResultInfo verifyDeveloper() throws NoSuchAlgorithmException,IOException {
		ResultInfo result = new ResultInfo();
			
		Http.Response response = Http.Response.current();
		Http.Request request = Http.Request.current();
		response.encoding = "utf-8";
		request.encoding = "utf-8";
		
		String signature = request.params.get("signature");
		String echostr = request.params.get("echostr");
		String timestamp = request.params.get("timestamp");
		String nonce = request.params.get("nonce");

		if (StringUtils.isBlank(timestamp)) {
			result.code=-1;
			result.msg="微信请求的timestamp为空，接入失败";
			
			return result;
		}
		
		if (StringUtils.isBlank(nonce)) {
			result.code=-1;
			result.msg="微信请求的nonce为空，接入失败";
			
			return result;
		}

		String[] str = { WXConstants.TOKEN, timestamp, nonce };

		Arrays.sort(str);
		String total = "";
		for (String string : str) {
			total = total + string;
		}

		MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
		sha1.update(total.getBytes());
		byte[] codedBytes = sha1.digest();
		String codedString = new BigInteger(1, codedBytes).toString(16);

		if (!codedString.equals(signature)) {
			result.code=-1;
			result.msg="微信接入验证失败";
			
			return result;
		}

		result.code=1;
		result.obj=echostr;
		
		return result;
	}

	/**
	 * 获取accesstoken
	 * 
	 * @param
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年5月6日
	 */
	public static String getAccessToken() {
		JSONObject jsonObject = null;
		String result = (String) Cache.get("WeiXinUtils.getAccessToken",String.class);

		if (result == null) {
			String parameter = "grant_type=client_credential&appid=" + WXConstants.APPID + "&secret=" + WXConstants.APPSECRET;
			jsonObject = WeChatUtil.doGetStr("https://api.weixin.qq.com/cgi-bin/token?" + parameter);

			result = jsonObject.getString("access_token");
			Cache.set("WeiXinUtils.getAccessToken", result, "120mn");
		}

		return result;
	}

	/**
	 * 消息接收
	 * 
	 * @param ReceiveMessageInterface
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年5月6日
	 */
	private static void requestMessage(ReceiveMessageInterface receiveMessageInterface) throws Exception {
		Http.Response response = Http.Response.current();
		Http.Request request = Http.Request.current();

		response.encoding = "utf-8";
		request.encoding = "utf-8";
		
		String receiveMsg = request.params.allSimple().get("body");
		Map<String, Object> xmlMap = Xml.extractSimpleXMLResultMap(receiveMsg);
		String msgType = (String) xmlMap.get("MsgType");
	
		if (WXConstants.MESSAGE_TEXT.equals(msgType)) {
			ReceiveTextMessage message = (ReceiveTextMessage) GongZhongUtils.map2Bean(xmlMap, ReceiveTextMessage.class);
			receiveMessageInterface.receiveTextMessage(message);

			return;
		}
		
		if (WXConstants.MESSAGE_IMAGE.equals(msgType)) {
			ReceiveImageMessage message = (ReceiveImageMessage) GongZhongUtils.map2Bean(xmlMap, ReceiveImageMessage.class);
			receiveMessageInterface.receiveImageMessage(message);

			return;
		}

		if (WXConstants.MESSAGE_VIDEO.equals(msgType)) {
			ReceiveVideoMessage message = (ReceiveVideoMessage) GongZhongUtils.map2Bean(xmlMap, ReceiveVideoMessage.class);
			receiveMessageInterface.receiveVideoMessage(message);

			return;
		}

		if (WXConstants.MESSAGE_VOICE.equals(msgType)) {
			ReceiveVoiceMessage message = (ReceiveVoiceMessage) GongZhongUtils.map2Bean(xmlMap, ReceiveVoiceMessage.class);
			receiveMessageInterface.receiveVoiceMessage(message);

			return;
		}

		if (WXConstants.MESSAGE_LOCATION.equals(msgType)) {
			ReceiveLocationMessage message = (ReceiveLocationMessage) GongZhongUtils.map2Bean(xmlMap, ReceiveLocationMessage.class);
			receiveMessageInterface.receiveLocationMessage(message);

			return;
		}

		if (WXConstants.MESSAGE_LINK.equals(msgType)) {
			ReceiveLinkMessage message = (ReceiveLinkMessage) GongZhongUtils.map2Bean(xmlMap, ReceiveLinkMessage.class);
			receiveMessageInterface.receiveLinkMessage(message);

			return;
		}

		if (WXConstants.MESSAGE_EVNET.equals(msgType)) {
			String event = (String) xmlMap.get("Event");
			Logger.info(".....事件....."+event);
			
			if (WXConstants.EVENT_SUBSCRIBE.equals(event)) {
				ReceiveSubscribeMessage message = (ReceiveSubscribeMessage) GongZhongUtils.map2Bean(xmlMap, ReceiveSubscribeMessage.class);
				receiveMessageInterface.eventSubscribeMessage(message);
				return;
			}

			if (WXConstants.EVENT_UNSUBSCRIBE.equals(event)) {
				ReceiveSubscribeMessage message = (ReceiveSubscribeMessage) GongZhongUtils.map2Bean(xmlMap, ReceiveSubscribeMessage.class);
				receiveMessageInterface.eventUnSubscribeMessage(message);
				return;
			}

			if ((WXConstants.EVENT_CLICK.equals(event)) || (WXConstants.EVENT_VIEW.equals(event))) {
				ReceiveClickMessage message = (ReceiveClickMessage) GongZhongUtils.map2Bean(xmlMap, ReceiveClickMessage.class);
				System.out.println("...."+message.getEventKey());
				receiveMessageInterface.eventClickMessage(message);
				return;
			}

			if (WXConstants.EVENT_MASSSENDJOBFINISH.equals(event)) {
				ReceiveGroupMessageNotice message = (ReceiveGroupMessageNotice) GongZhongUtils.map2Bean(xmlMap, ReceiveGroupMessageNotice.class);
				receiveMessageInterface.eventGroupMessageNotice(message);
			}

			return;
		}
	}

}