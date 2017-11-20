package common.utils;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import services.common.BankCardUserService;
import services.common.UserFundService;
import services.common.UserInfoService;
import models.common.entity.t_bank_card_user;
import models.common.entity.t_user_fund;
import models.common.entity.t_user_info;
import common.utils.Factory;
import common.utils.ResultInfo;

public class UserValidataUtil {
    
	private static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	private static UserFundService userFundService = Factory.getService(UserFundService.class);
	private static BankCardUserService bankCardUserService = Factory.getService(BankCardUserService.class);
	

	/**
	 *  是否绑定邮箱(true 已绑定，false未绑定)
	 * @param userId
	 * @return
	 */
	public static boolean checkBindEmail(long userId){
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		/* 判断该用户是否绑定邮箱 */
		if (StringUtils.isBlank(userInfo.email)) {
			return false;
		}
		
		return true;
	}
	
	/***
	 * 是否实名认证(true 已实名认证，false未实名认证)
	 *
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-1
	 */
	public static boolean checkRealName(long userId){
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		/* 判断该用户是否实名认证 */
		if (StringUtils.isBlank(userInfo.reality_name) || StringUtils.isBlank(userInfo.id_number) ) {
			return false;
		}
		
		return true;
	}
	
	
	
	/***
	 * 是否绑定银行卡(true 已绑卡，false未绑卡)
	 *
	 * @param userId
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-1
	 */
	public static boolean checkBankCard(long userId){
		List<t_bank_card_user> cardList = bankCardUserService.queryCardByUserId(userId);
		if (cardList == null || cardList.size() < 1) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * 资金存管开户（true 已开户，false 未开户）
	 *
	 * @param userId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年4月7日
	 */
	public static boolean checkPaymentAccount(long userId) {
		t_user_fund userInfo = userFundService.queryUserFundByUserId(userId);
		
		return StringUtils.isBlank(userInfo.payment_account)?false:true;
	}
	
	/**
	 * 上海银行存管是否激活
	 * 
	 * @param userId
	 * @return
	 */
	public static boolean checkActivited(long userId) {
		t_user_fund userInfo = userFundService.queryUserFundByUserId(userId);
		
		return userInfo.is_actived;
	}
}
