package service.wechat;

import common.utils.Factory;
import common.utils.ResultInfo;
import dao.wechat.UserWechatDao;
import models.common.entity.t_user;
import models.wechat.bean.WXUserFundInfo;
import services.common.UserService;

/**
 * 微信UserService拓展
 * 
 * @author liudong
 * @createDate 2016年5月9日
 */
public class UserWechatService extends UserService {

	private UserWechatDao userWechatDao = null;
	
	protected UserWechatService() {
		userWechatDao = Factory.getDao(UserWechatDao.class);
	}
	
	/**
	 * 微信 - 资产状况 (根据openId查询)
	 * 
	 * @param
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年5月9日
	 */
	public WXUserFundInfo findUserFundInfo(String openId) {
		WXUserFundInfo wxUserFundInfo = userWechatDao.findUserFundInfo(openId);
		
		/* 计算总资产 */
		wxUserFundInfo.total_assets = wxUserFundInfo.balance + wxUserFundInfo.freeze - wxUserFundInfo.no_repayment + wxUserFundInfo.no_receive + wxUserFundInfo.reward;
		
		return wxUserFundInfo;
	}
	
	/**
	 * 微信 - 查询用户是否存在(根据手机号和密码查询)
	 * 
	 * @param mobile 注册手机号
	 * @param password 登录密码
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年5月9日
	 */
	public ResultInfo queryUserByMobileAndPwd(String mobile,String password) {
		ResultInfo result = new ResultInfo();
		
		t_user user = userWechatDao.findByColumn("mobile=? AND password=?", mobile,password);
		if (user == null) {
			result.code = -1;
			result.msg = "账号或密码错误";

			return result;
		}
		
		result.code = 1;
		result.msg="交易成功";
		
		return result;
	}
}
