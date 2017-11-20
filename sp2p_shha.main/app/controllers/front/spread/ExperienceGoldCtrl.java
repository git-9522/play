package controllers.front.spread;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import common.enums.Client;
import common.enums.ServiceType;
import common.utils.Factory;
import common.utils.OrderNoUtil;
import common.utils.ResultInfo;
import controllers.common.FrontBaseController;
import hf.HfPaymentCallBackService;
import hf.HfPaymentService;
import hf.HfUtils;
import models.common.entity.t_exp_gold_account_user;
import models.common.entity.t_experience_gold;
import models.common.entity.t_user;
import models.common.entity.t_user_fund;
import models.core.entity.t_invest;
import net.sf.json.JSONObject;
import payment.impl.PaymentProxy;
import services.common.ExpGoldAccountUserService;
import services.common.ExpGoldShowRecordService;
import services.common.ExpGoldUserRecordService;
import services.common.ExperienceGoldService;
import services.common.NoticeService;
import services.common.UserFundService;
import services.common.UserService;
import services.core.InvestService;

/**
 * 前台-新手礼包-体验金
 *
 * @description 
 *
 * @author yuechuanyang
 * @createDate 2017年10月17日
 */
public class ExperienceGoldCtrl extends FrontBaseController{
	
	protected static UserService userService = Factory.getService(UserService.class);
	
	protected static ExperienceGoldService experienceGoldService = Factory.getService(ExperienceGoldService.class);
	
	protected static ExpGoldAccountUserService expGoldAccountUserService = Factory.getService(ExpGoldAccountUserService.class);
	
	protected static ExpGoldShowRecordService expGoldShowRecordService = Factory.getService(ExpGoldShowRecordService.class);
	
	protected static ExpGoldUserRecordService expGoldUserRecordService = Factory.getService(ExpGoldUserRecordService.class);

	protected static InvestService investService = Factory.getService(InvestService.class);

	protected static UserFundService userFundService = Factory.getService(UserFundService.class);
	
	private static HfPaymentCallBackService hfPaymentCallBackService = Factory.getSimpleService(HfPaymentCallBackService.class);
	
	/**
	 * 领取体验金
	 * @description 
	 *
	 * @author yuechuanyang
	 * @createDate 2017年10月17日
	 */
	public static void receiveExpGoldPre(int client){
		t_experience_gold exp = experienceGoldService.queryExperienceGold();
		double amount = exp.getAmount();
		long exp_gold_id = exp.getId();
		double invest_amount = exp.getInvest_amount();
		double seven_day_rate = exp.getSeven_day_rate();
		ResultInfo result = new ResultInfo();
		if(getCurrUser() == null){
			result.code = -1;
			result.msg = "用户未登录，请先登录！";
			renderJSON(result);
		}
		long userId = getCurrUser().id;
		//根据id查询用户信息
		t_user user = userService.findUserById(userId);
		if(user == null){
			result.code = -2;
			result.msg = "不存在该用户！";
			renderJSON(result);
		}
		//判断是否开户7天内
		long time = new Date().getTime() - user.time.getTime();
		if(time > 7*24*60*60*1000){
			result.code = -3;
			result.msg = "您的账户开户超过7天，不属于新手账户！";
			renderJSON(result);
		}
		//查询领取次数
		long receiveTimes = expGoldUserRecordService.getCountOfExpGoldUserRecord(userId);
		if(receiveTimes > 0){
			result.code = -4;
			result.msg = "您已经领取过体验金了！";
			renderJSON(result);
		}
		//判断用户是否投资过2次(查询投资次数)
		int investCount = investService.countInvestOfUser(userId,true);
		Date create_time = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(create_time);
		cal.add(Calendar.DATE, 15);
		Date end_time = cal.getTime();
		if(investCount >= 2){//查过两次投资直接按照第二次投资时的金额的1/1000 + 体验金转入账户
			//查询第二次投资金额
			List<t_invest> list = investService.queryInvestRecordByUserId(userId);
			double secInvestAmount = list.get(1).amount;
			//转账
			result=hfPaymentCallBackService.ExpGoldSend(userId, secInvestAmount,client);
		}else{//没有2次投资，存入体验金账户，如有复投，投资时的金额的1/1000 + 体验金转入账户
			//插入领取记录
			expGoldUserRecordService.saveExpGoldUserRecord(userId, exp_gold_id, create_time, end_time, "", amount);
			//插入体验金账户
			expGoldAccountUserService.saveExpGoldAccountUser(userId, amount, create_time, end_time, 0, "",invest_amount,seven_day_rate);
			result.code = 1;
			result.msg = "体验金领取成功，并存入体验金账户，复投时可转入账户！";
		}
		renderJSON(result);
	}
	
	
	/**
	 * 查询体验金
	 * @description 
	 *
	 * @author yuechuanyang
	 * @createDate 2017年10月17日
	 */
	public static void getExpGold(){
		t_experience_gold exp = experienceGoldService.queryExperienceGold();
		ResultInfo result = new ResultInfo();
		result.code = 1;
		result.msg = "查询成功！";
		result.obj = exp;
		renderJSON(result);
	}
	
	
	/**
	 * 查询体验金账户信息
	 * @description 
	 *
	 * @author yuechuanyang
	 * @createDate 2017年10月17日
	 */
	public static void queryExpGoldAccountUserByUserId(){
		long userId = getCurrUser().id;
		t_exp_gold_account_user exp = expGoldAccountUserService.queryAllExpGoldAccountUserByUserId(userId);
		render(exp);
	}
}
