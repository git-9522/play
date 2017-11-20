package service.wechat;


import java.io.IOException;
import java.util.Date;

import models.wechat.bean.WXUserFundInfo;
import play.Logger;

import com.shove.gateway.weixin.gongzhong.ReceiveMessageInterface;
import com.shove.gateway.weixin.gongzhong.vo.message.Message;
import com.shove.gateway.weixin.gongzhong.vo.message.receive.ReceiveClickMessage;
import com.shove.gateway.weixin.gongzhong.vo.message.receive.ReceiveGroupMessageNotice;
import com.shove.gateway.weixin.gongzhong.vo.message.receive.ReceiveImageMessage;
import com.shove.gateway.weixin.gongzhong.vo.message.receive.ReceiveLinkMessage;
import com.shove.gateway.weixin.gongzhong.vo.message.receive.ReceiveLocationMessage;
import com.shove.gateway.weixin.gongzhong.vo.message.receive.ReceiveSubscribeMessage;
import com.shove.gateway.weixin.gongzhong.vo.message.receive.ReceiveTemplateMessage;
import com.shove.gateway.weixin.gongzhong.vo.message.receive.ReceiveTextMessage;
import com.shove.gateway.weixin.gongzhong.vo.message.receive.ReceiveVideoMessage;
import com.shove.gateway.weixin.gongzhong.vo.message.receive.ReceiveVoiceMessage;
import com.shove.gateway.weixin.gongzhong.vo.message.reply.ReplyTextMessage;

import common.WeChatUtil;
import common.constants.WXConstants;
import common.utils.Factory;
import common.utils.ResultInfo;
import common.utils.number.NumberFormat;
/**
 * 实现接收信息接口类。（接口是shove中封装的，这里我们实现它）
 * 
 * @author liudong
 * @createDate 2016年5月6日
 */
public class WeChatReceiveMessageService implements ReceiveMessageInterface {
	
	/** 微信欢迎咨询语service  */
	protected static WeixinConsultationService weixinConsultationService = Factory.getService(WeixinConsultationService.class);

	/** 微信绑定解绑状态  */
	protected static WeChatBindService weChatBindService = Factory.getService(WeChatBindService.class);
	
	/** 微信UserWechatService  */
	protected static UserWechatService userWechatService = Factory.getService(UserWechatService.class);
	
	/**
	 * 接受CLICK事件, 目前只有咨询是click事件，key为1
	 * 
	 * @param
	 * @return 
	 *
	 * @author liudong
	 * @createDate 2016年5月6日
	 */
	public String eventClickMessage(ReceiveClickMessage receiveClickMessage) {
		
		//点击事件消息回复拼接
		ReplyTextMessage replyTextMessage = new ReplyTextMessage();
		replyTextMessage.setFromUserName(receiveClickMessage.getToUserName());
		replyTextMessage.setToUserName(receiveClickMessage.getFromUserName());
		replyTextMessage.setMsgType(Message.TYPE_TEXT);
		replyTextMessage.setCreateTime(new Date().getTime());
		
		String eventKey = receiveClickMessage.getEventKey();
		
		//判断是否绑定
		boolean flag = weChatBindService.queryWeChatBind(receiveClickMessage.getFromUserName());
		
		if(!flag){
			
			//绑定url
			String url1 = "<a href=\"https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=snsapi_base&state=${STATE}#wechat_redirect\">${BIND}</a>";
			url1 = url1.replace("APPID", WXConstants.APPID);
			url1 = url1.replace("REDIRECT_URI",WeChatUtil.urlEncodeUTF8(WXConstants.REDIRECT_URI));
			url1 = url1.replace("${STATE}", "31").replace("${BIND}", "点击这里绑定");
			
			//注册url
		    String url2 = "<a href=\"wechat_register_uri\">点击这里注册</a>";
			url2 = url2.replace("wechat_register_uri", WXConstants.WECHAT_REGISTER_URI);
			
			String content = weixinConsultationService.findWeixinConsultation("weixin_bind");
	
			content = content.replace("点击这里绑定", url1).replace("点击这里注册", url2);
			
			Logger.info("........"+content.toString());
			replyTextMessage.setContent(content.toString());
			// 向用户发送关注语
			try {
				WeChatUtil.replyTextMessage(replyTextMessage);
				return null;
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		//点击事件1 资产状况
		if("1".equals(eventKey) && flag){
			WXUserFundInfo wxUserFundInfo = userWechatService.findUserFundInfo(receiveClickMessage.getFromUserName());
			if(wxUserFundInfo != null){
				StringBuffer sb = new StringBuffer();
				sb.append("亲爱的"+ wxUserFundInfo.name +",您目前：").append("\n");
				sb.append("总资产："+ NumberFormat.finance(wxUserFundInfo.total_assets) +"元").append("\n");
				sb.append("可用余额："+ NumberFormat.finance(wxUserFundInfo.balance) +"元").append("\n");
				sb.append("待收："+ NumberFormat.finance(wxUserFundInfo.no_receive) +"元").append("\n");
				sb.append("总收益："+ NumberFormat.finance(wxUserFundInfo.total_income) +"元").append("\n");
				sb.append("我的奖励："+ NumberFormat.finance(wxUserFundInfo.reward) +"元").append("\n");
				sb.append("冻结："+ NumberFormat.finance(wxUserFundInfo.freeze) +"元").append("\n");
				sb.append("待还："+ NumberFormat.finance(wxUserFundInfo.no_repayment) +"元").append("\n\n");
				sb.append("回复【解绑】可解除微信绑定");
				
				replyTextMessage.setContent(sb.toString());
				try {
					WeChatUtil.replyTextMessage(replyTextMessage);
				} catch (Exception e) {
					Logger.info("点击查看资产状况时%s", e.getMessage());
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}
	
	/**
	 * 接收用户发送过来的消息
	 * 
	 * @param
	 * @return 
	 *
	 * @author liudong
	 * @createDate 2016年5月6日
	 */
	public String receiveTextMessage(ReceiveTextMessage receiveTextMessage) {

		ReplyTextMessage replyTextMessage = new ReplyTextMessage();

		replyTextMessage.setFromUserName(receiveTextMessage.getToUserName());
		replyTextMessage.setToUserName(receiveTextMessage.getFromUserName());
		replyTextMessage.setMsgType(Message.TYPE_TEXT);
		replyTextMessage.setCreateTime(new Date().getTime());

		/** 接收消息  */
		String fromContent = receiveTextMessage.getContent();
		
		/** 解绑关键词 */
		String unbind = weixinConsultationService.findWeixinConsultation("weixin_unbound");
		
		if (fromContent.equals(unbind)) {
			String toContent = null;

			ResultInfo result = weChatBindService.wechatUnbind(receiveTextMessage.getFromUserName());
			toContent = result.msg;
			
			replyTextMessage.setContent(toContent);
			
			try {
				WeChatUtil.replyTextMessage(replyTextMessage);
			} catch (Exception e) {
				Logger.error("解除绑定时%s", e.getMessage());
			}
			
		}else{
			String toContent = "立即理财,请<a href=\"WECHAT_INVEST_URI\">点击这里</a>";
			toContent = toContent.replace("WECHAT_INVEST_URI", WXConstants.WECHAT_INVEST_URI);
			replyTextMessage.setContent(toContent);
			try {
				WeChatUtil.replyTextMessage(replyTextMessage);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}

	/**
	 * 用户关注公众号
	 * 
	 * @param
	 * @return 
	 *
	 * @author liudong
	 * @createDate 2016年5月6日
	 */
	public String eventSubscribeMessage(ReceiveSubscribeMessage receiveSubscribeMessage) {
		try {
			// 向用户发送关注语，并且提示用户进行绑定
			ReplyTextMessage replyTextMessage = new ReplyTextMessage();
			replyTextMessage.setFromUserName(receiveSubscribeMessage.getToUserName());
			replyTextMessage.setToUserName(receiveSubscribeMessage.getFromUserName());
			replyTextMessage.setCreateTime(new Date().getTime());
			replyTextMessage.setMsgType(Message.TYPE_TEXT);
			replyTextMessage.setMsgId(receiveSubscribeMessage.getMsgId());
			
			String url = "<a href=\"WECHAT_INVEST_URI\">点击这里</a>";
			url = url.replace("WECHAT_INVEST_URI", WXConstants.WECHAT_INVEST_URI);

			String content = weixinConsultationService.findWeixinConsultation("weixin_welcome");	
			content = content.replace("点击这里", url);
					
			replyTextMessage.setContent(content);
			
			// 向用户发送关注语
			WeChatUtil.replyTextMessage(replyTextMessage);
		} catch (Exception e) {

			Logger.error("向用户发送关注语时%s", e.getMessage());
		}

		return null;
	}

	/**
	 * 用户取消关注公众号
	 */
	public String eventUnSubscribeMessage(ReceiveSubscribeMessage receiveSubscribeMessage) {
		String openId = receiveSubscribeMessage.getFromUserName();
		Logger.info("openId为"+openId+"取消关注");
		
		return null;
	}

	/**
	 * 接受图片消息
	 */
	public String receiveImageMessage(ReceiveImageMessage receivegroupmessagenotice) {

		return null;
	}

	/**
	 * 接受超链接
	 */
	public String receiveLinkMessage(ReceiveLinkMessage receivegroupmessagenotice) {

		return null;
	}

	/**
	 * 接受位置信息
	 */
	public String receiveLocationMessage(ReceiveLocationMessage receivegroupmessagenotice) {

		return null;
	}

	/**
	 * 接受视频消息
	 */
	public String receiveVideoMessage(ReceiveVideoMessage receivegroupmessagenotice) {

		return null;
	}

	/**
	 * 接受音频消息
	 */
	public String receiveVoiceMessage(ReceiveVoiceMessage receivegroupmessagenotice) {

		return null;
	}

	public String receiveTemplateSendJobFinishMessag(ReceiveTemplateMessage receivegroupmessagenotice) {

		return null;
	}

	public String eventGroupMessageNotice(ReceiveGroupMessageNotice receivegroupmessagenotice) {
		
		return null;
	}

}
