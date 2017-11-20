package service.wechat;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.shove.Convert;

import common.constants.CacheKey;
import common.constants.Constants;
import common.constants.ModuleConst;
import common.constants.SettingKey;
import common.utils.Factory;
import common.utils.ResultInfo;
import dao.wechat.WeChatBindDao;
import models.common.entity.t_score_user;
import models.wechat.entity.t_wechat_bind;
import play.Logger;
import play.cache.Cache;
import play.db.jpa.JPA;
import play.mvc.Scope.Session;
import services.base.BaseService;
import services.common.ScoreUserService;
import services.common.SettingService;
import services.common.UserFundService;
import services.common.UserService;

/**
 * 微信公众号绑定servcie
 * 
 * @author liudong
 * @createDate 2016年5月7日
 */
public class WeChatBindService extends BaseService<t_wechat_bind> {
	
	protected static WeChatBindDao weChatBindDao = Factory.getDao(WeChatBindDao.class);
	
	protected static UserService userService = Factory.getService(UserService.class);
	
	protected static SettingService settingService = Factory.getService(SettingService.class);
	
	protected static ScoreUserService scoreUserService = Factory.getService(ScoreUserService.class);
	
	protected static UserFundService userFundService = Factory.getService(UserFundService.class);
	
	protected WeChatBindService(){
		super.basedao = weChatBindDao;
	}

	/**
	 * 绑定微信公众号
	 * 
	 * @param openId 微信openId
	 * @param mobile 手机号码
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年5月7日
	 */
	public ResultInfo weChatbind(String openId,String mobile){
		ResultInfo result = new ResultInfo();
		
		long user_id = userService.queryIdByMobile(mobile);
		
		//判断用户是否存在
		/*if(user_id == null){
			result.code = -1;
			result.msg = "绑定失败";
			
			Logger.info("....用户不存在,user_id为空....");
			
			return result;
		}*/
		
		//判断用户是否已经绑定别的微信号
		t_wechat_bind wechat_bind = weChatBindDao.findByColumn(" open_id=? OR user_id=? ", openId, user_id);
		if(wechat_bind != null){
			result.code = -1;
			result.msg = "您的微信号或账号已被绑定";
			
			Logger.info("...您的微信号或账号已被绑定....");
			
			return result;
		}
		
		t_wechat_bind weChatBind = new t_wechat_bind();
		weChatBind.time = new Date();
		weChatBind.open_id = openId;
		weChatBind.user_id = user_id;
		
		boolean flag = weChatBindDao.save(weChatBind);
		if(!flag){
			result.code=-1;
			result.msg="绑定失败";
			
			return result;
		}
		
		/** 绑定成功，设置用户为登陆状态 */
		if (Session.current() != null) {
			String sessionId = Session.current().getId();
			/* 设置用户凭证 */
			Cache.set(CacheKey.FRONT_ + sessionId, weChatBind.user_id, Constants.CACHE_TIME_MINUS_30);
			userService.flushUserCache(weChatBind.user_id);
		}
		
		// 积分商城
		if(ModuleConst.EXT_MALL){
			
			double score = Convert.strToDouble(settingService.findSettingValueByKey(SettingKey.BIND_WECHAT_SCORE), 0);
			
			if(score > 0){
				
				//增加积分
				int rows = userFundService.updateUserScoreBalanceAdd(user_id, score);
				if (rows <= 0) {
					JPA.setRollbackOnly();
					result.code=-1;
					result.msg="绑定微信获得积分失败";
					
					return result;
				}
				
				//及时查询用户的可用积分
				double scoreBalance = userFundService.findUserScoreBalance(user_id);
				
				/** 添加用户积分记录 */
				Map<String, String> summaryPrams = new HashMap<String, String>();
				summaryPrams.put("score", (int) score + "");
				boolean addDeal = scoreUserService.addScoreUserInfo(
						user_id, 
						score, 
						scoreBalance, 
						t_score_user.OperationType.BIND_WECHAT_SCORE, 
						summaryPrams);
				
				if (!addDeal) {
					JPA.setRollbackOnly();
					
					result.code=-1;
					result.msg="添加积分记录失败";
					
					return result;
				}
				
				userService.flushUserCache(user_id);
			}
		}
		
		result.code=1;
		result.msg="绑定成功";
		
		return result;
	}
	
	/**
	 * 微信公众号解绑
	 * 
	 * @param  openId 微信openId
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年5月7日
	 */
	public ResultInfo wechatUnbind(String openId){
		ResultInfo result = new ResultInfo();
		
		t_wechat_bind weChatBind = weChatBindDao.findByColumn(" open_id=? ", openId);
		if(weChatBind == null){
			result.code=-1;
			result.msg="微信号尚未成功绑定用户";
			
			Logger.info(".....微信号尚未成功绑定用户.....");
			
			return result;
		}
		
		//删除绑定关系
		int row = weChatBindDao.delete(weChatBind.id);
		if(row < 1){
			result.code=-1;
			result.msg="解除绑定失败";
			
			Logger.info(".....解除绑定失败.....");
			
			return result;
		}
		
		result.code=1;
		result.msg="解除绑定成功";
		
		Logger.info(".....解除绑定成功.....");
		
		return result;
	}
	
	/**
	 * 判断微信是否已经绑定
	 * 
	 * @param openId 微信openId
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年5月10日
	 */
	public boolean queryWeChatBind(String openId){
		t_wechat_bind weChatBind = weChatBindDao.findByColumn(" open_id=? ", openId);
		if(weChatBind == null){
			return false;
		}
		return true;
	}
}
