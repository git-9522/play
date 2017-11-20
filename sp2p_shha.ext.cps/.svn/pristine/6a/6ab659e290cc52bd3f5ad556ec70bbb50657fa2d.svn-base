package controllers.front.account;

import com.shove.security.Encrypt;

import common.annotation.LoginCheck;
import common.annotation.PaymentAccountCheck;
import common.annotation.SimulatedCheck;
import common.constants.ConfConst;
import common.constants.Constants;
import common.constants.ext.CpsSettingKey;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;
import controllers.common.FrontBaseController;
import controllers.common.interceptor.AccountInterceptor;
import controllers.common.interceptor.SimulatedInterceptor;
import daos.ext.cps.CpsUserDao;
import models.common.bean.CurrUser;
import models.core.bean.CpsUserInvest;
import models.ext.cps.bean.CpsUser;
import models.ext.cps.entity.t_cps_account;
import models.ext.cps.entity.t_cps_user;
import play.mvc.With;
import services.common.UserService;
import services.core.InvestService;
import services.ext.cps.CpsService;

/**
 * 前台-我的奖励-CPS控制器
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年3月15日
 */
@With({AccountInterceptor.class,SimulatedInterceptor.class})
public class MyCpsCtrl extends FrontBaseController {

	protected static CpsService cpsService = Factory.getService(CpsService.class);
	
	protected static UserService userService = Factory.getService(UserService.class);
	
	protected static InvestService investService = Factory.getService(InvestService.class);
	
	protected static CpsUserDao cpsUserDao = Factory.getDao(CpsUserDao.class);
	
	/**
	 * 前台-我的奖励-CPS-推广记录
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月15日
	 */
	public static void showMyCpsPre(int currPage) {
		String rebate_reg = cpsService.findSettingValueByKey(CpsSettingKey.REBATE_REG);
		String rebate_invest = cpsService.findSettingValueByKey(CpsSettingKey.REBATE_INVEST);
		String discount_invest = cpsService.findSettingValueByKey(CpsSettingKey.DISCOUNT_INVEST);
		
		renderArgs.put("rebate_reg", rebate_reg);
		renderArgs.put("rebate_invest", rebate_invest);
		renderArgs.put("discount_invest", discount_invest);
		
		if (currPage < 1) {
			currPage = 1;
		}
		CurrUser currUser = getCurrUser();
		t_cps_account cpsAccount= cpsService.findCpsAccountByUserId(currUser.id);
		PageBean<CpsUser> page = cpsService.pageOfCpsusersBySpreaderId(currPage, 5, currUser.id);
		
		String mobile = userService.findByID(currUser.id).mobile;
		
		String mobileSign = Encrypt.encrypt3DES(mobile, ConfConst.ENCRYPTION_KEY_DES);
		
		render(cpsAccount,page,mobile,mobileSign);
	}
	
	/**
	 * 前台-我的奖励-CPS-被推广会员投资记录
	 *
	 *@param cpsUserId 被推广关系id
	 * @author DaiZhengmiao
	 * @createDate 2016年3月15日
	 */
	public static void showCpsInvestPre(int currPage,String cpsSign) {
		
		ResultInfo result = Security.decodeSign(cpsSign, Constants.CPS_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			result.code = -1;
			
			renderJSON(result);
		}
		long cpsUserId = Long.parseLong((String) result.obj);
		CurrUser currUser = getCurrUser();
		
		t_cps_user cpsUser = cpsUserDao.findByID(cpsUserId);
		if (cpsUser == null || cpsUser.spreader_id != currUser.id) {
			result.code = 0;
			result.msg = "推广记录不存在或者不是该用户的推广会员";
			
			renderJSON(result);
		}
		
		cpsSign = Security.addSign(cpsUser.id, Constants.CPS_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
		
		PageBean<CpsUserInvest> page = investService.pageOfCpsUserInvestRecord(currPage, 5, cpsUser.user_id);
		render(page,cpsSign);
	}
	
	/**
	 * 前台-我的奖励-CPS-推广记录-领取返佣
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月15日
	 */
	@LoginCheck
	@SimulatedCheck
	public static void reciveCps(String cpsSign){
		ResultInfo result = Security.decodeSign(cpsSign, Constants.CPS_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			result.code = -1;
			
			renderJSON(result);
		}
		CurrUser currUser = getCurrUser();
		long cpsUserId = Long.parseLong((String) result.obj);
		
		result = cpsService.reciveCps(cpsUserId,currUser.id);
		if (result.code < 1) {
			LoggerUtil.error(true, "返佣领取失败:【%s】", result.msg);
			
			result.msg = "返佣领取失败";
		
			renderJSON(result);
		}
		
		result.code = 1;
		result.msg = "返佣领取成功";
		renderJSON(result);
	}
	
	/**
	 * 前台-我的奖励-CPS-推广记录-CPS账户兑换
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月16日
	 */
	@LoginCheck
	@SimulatedCheck
	@PaymentAccountCheck
	public static void cpsExchange() {	
		ResultInfo result = cpsService.applayConversion(getCurrUser().id);
		if (result.code < 1) {
			
			LoggerUtil.error(true, "CPS申请兑换时出错,数据回滚:【%s】",result.msg);
			
			result.msg = "CPS返佣申请兑换失败";
			renderJSON(result);
		}
		
		result.code = 1;
		result.msg = "CPS返佣申请兑换成功";
		renderJSON(result);
		
	}
}
