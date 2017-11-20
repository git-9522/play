package service.wechat;

import com.shove.JSONUtils;
import com.shove.gateway.weixin.gongzhong.GongZhongObject;
import com.shove.gateway.weixin.gongzhong.utils.GongZhongUtils;
import com.shove.gateway.weixin.gongzhong.vo.weboauth.OauthAccessToken;
import com.shove.gateway.weixin.gongzhong.vo.weboauth.UserInfo;

import common.WeChatUtil;
import common.constants.WXConstants;
import net.sf.json.JSONObject;

/**
 * 网页授权回调
 * 
 * @author liudong
 * @createDate 2016年5月10日
 */
public class WeChatWebOAuthManageService extends GongZhongObject {

	public static String getBaseOauth2Url(String redirectUri, String state) {
		String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="
				+ WXConstants.APPID
				+ "&redirect_uri="
				+ redirectUri
				+ "&response_type=code&scope=snsapi_base&state="
				+ state
				+ "#wechat_redirect";

		return url;
	}

	public static String getUserinfoOauth2Url(String redirectUri, String state) {
		String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="
				+ WXConstants.APPID
				+ "&redirect_uri="
				+ redirectUri
				+ "&response_type=code&scope=snsapi_userinfo&state="
				+ state
				+ "#wechat_redirect";

		return url;
	}

	/**
	 * 网页授权获取AccessToken
	 * 
	 * @param
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年5月10日
	 */
	public static OauthAccessToken getAccessToken(String code) {

		String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
				+ WXConstants.APPID + "&secret="
				+ WXConstants.APPSECRET + "&code=" + code
				+ "&grant_type=authorization_code";
		JSONObject result = WeChatUtil.doGetStr(url);

		return (OauthAccessToken) JSONUtils.toBean(result,OauthAccessToken.class);
	}

	/**
	 * 网页AccessToken刷新
	 * 
	 * @param
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年5月10日
	 */
	public static OauthAccessToken refreshAccessToken(String refreshToken) {
		String result = GongZhongUtils.sendPost(
				"https://api.weixin.qq.com/sns/oauth2/refresh_token?appid="
						+ WXConstants.APPID
						+ "&grant_type=refresh_token&refresh_token="
						+ refreshToken, "");

		return (OauthAccessToken) JSONUtils.toBean(
				JSONObject.fromObject(result), OauthAccessToken.class);
	}

	/**
	 * 网页授权后，获取公众号的基本信息
	 * 
	 * @param
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年5月10日
	 */
	public static UserInfo getUserInfo(String accessToken, String openId) {
		String url = "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken + "&openid=" + openId;
		JSONObject result = WeChatUtil.doGetStr(url);

		return (UserInfo) JSONUtils.toBean(result, UserInfo.class);
	}
}