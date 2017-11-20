package controllers.app;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import play.db.jpa.JPA;
import play.db.jpa.JPAPlugin;
import models.app.bean.DebtTransferBean;
import models.app.bean.InvestBidsBean;
import models.common.bean.SignInUserRecord;
import models.common.entity.t_advertisement;
import models.common.entity.t_score_user;
import models.common.entity.t_sign_in_record;
import models.common.entity.t_sign_in_rule;
import models.common.entity.t_advertisement.Location;
import models.common.entity.t_bank_card_user;
import models.common.entity.t_user;
import models.core.bean.FrontBid;
import models.core.entity.t_product.PeriodUnit;
import models.ext.experience.entity.t_experience_bid;
import net.sf.json.JSONObject;
import service.DebtAppService;
import service.InvestAppService;
import service.ext.experiencebid.ExperienceBidService;
import services.common.AdvertisementService;
import services.common.BankCardUserService;
import services.common.InformationService;
import services.common.ScoreUserService;
import services.common.SignInRecordService;
import services.common.SignInRuleService;
import services.common.UserFundService;
import services.common.UserService;
import services.core.BidService;
import services.core.InvestService;
import common.constants.ConfConst;
import common.constants.Constants;
import common.constants.ext.SignInKey;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;
import jobs.IndexStatisticsJob;

/**
 * app首页
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2016年6月30日
 */
public class HomeAction {
	
	protected static AdvertisementService advertisementService =  Factory.getService(AdvertisementService.class);
	
	protected static InformationService informationService =  Factory.getService(InformationService.class);
	
	protected static InvestAppService investAppService = Factory.getService(InvestAppService.class);
	
	protected static ExperienceBidService experienceBidService = Factory.getService(ExperienceBidService.class);
	
//	private static DebtAppService debtAppService = Factory.getService(DebtAppService.class);

	private static UserService userService = Factory.getService(UserService.class);

	private static BidService bidService = Factory.getService(BidService.class);

	private static SignInRecordService signInRecordService = Factory.getService(SignInRecordService.class);
	
	private static SignInRuleService signInRuleService = Factory.getService(SignInRuleService.class);
	
	private static UserFundService userFundService = Factory.getService(UserFundService.class);
	
	private static ScoreUserService scoreUserService = Factory.getService(ScoreUserService.class);
	
	private static InvestService investService = Factory.getService(InvestService.class);
	
	public static BankCardUserService bankCardUserService=Factory.getService(BankCardUserService.class);
	
	public static String index(Map<String, String> parameters) throws Exception {
		
		
		JSONObject json = new JSONObject();
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		
		String signUserId = parameters.get("userId");
		
		ResultInfo info = Security.decodeSign(signUserId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		
		result.put("code", 1);
		result.put("msg", "成功");
		
		if (info.code < 1) {
			result.put("name", "");
			result.put("isAccount",false);
		}else{
			long userId = Long.parseLong(info.obj.toString());
			t_user user = userService.findByID(userId);
			
			result.put("name", user.getTelName());
			
			//新版加(是否已开户)
			List<t_bank_card_user>  bank= bankCardUserService.queryCardByUserId(userId);
			result.put("isAccount", bank.size()>0&&bank.get(0)!=null?true:false);
			if(user.is_old) {
				result.put("is_newbie", false);
			} else {
				long investId = investService.queryIsHaveInvestRecord(userId);
				if (investId > 0L) {
					result.put("is_newbie", false);
				} else {
					result.put("is_newbie", true);
				}
			}
		}

		
		double totalAmount = bidService.queryTotalBorrowAmount();
		result.put("totalAmount", totalAmount);
		
		//广告图片
		List<t_advertisement> banners = advertisementService.queryAdvertisementFront(Location.APP_HOME_TURN_ADS, 8);
		if (banners == null) {
			result.put("banners", null);
		} else {
			List<Map<String, Object>> bannerList = new ArrayList<Map<String,Object>>();
			for (t_advertisement banner : banners) {
				Map<String, Object> bannerMap = new HashMap<String, Object>();
				bannerMap.put("image_url", banner.image_url);
				bannerMap.put("url", banner.url);
				
				bannerList.add(bannerMap);
			}
			
			result.put("banners", bannerList);
		}
		
		
		// 新手标
		PageBean<FrontBid> expBid = bidService.pageOfNewbieBidInvest(1, 2);
		
		if (expBid.page == null) {
			result.put("expBid", null);
		} else {
			FrontBid frontBid = expBid.page.get(0);
			PeriodUnit unit = frontBid.getPeriod_unit();
			json.put("appSign", frontBid.appSign);
			json.put("amount", frontBid.amount);//借款金额
			json.put("apr", frontBid.apr);//年利率
			json.put("period", frontBid.period);//借款期限 
			json.put("period_unit", unit==null?null:unit.getShowValue());//借款期限单位
			json.put("is_invest_reward", frontBid.is_invest_reward);//是否开启投标奖励
			json.put("reward_rate", frontBid.reward_rate);//投标奖励年利率
			json.put("status", frontBid.getStatus());//标的状态
			json.put("min_invest_amount", frontBid.min_invest_amount);//最少投资金额
			json.put("preRelease", frontBid.pre_release_time);//发售时间
			json.put("tite",frontBid.title);//标的标题
			result.put("expBid", json);
		}

		
		//推荐标-散标投资
		List<InvestBidsBean> invests = investAppService.listOfInvestBids(1);
		result.put("invests", invests);
		
		//推荐标-债权
		/*PageBean<DebtTransferBean> debts = debtAppService.pageOfDebts(1, 2);
		result.put("debts", debts.page);*/
		
		Date sysNowTime = new Date();
		result.put("sysNowTime", sysNowTime);
		
		// 累计注册人数
		result.put("userCount", IndexStatisticsJob.userCount);
		
		// 累计成交额 
		result.put("totalBorrowAmount", IndexStatisticsJob.totalBorrowAmount);
		
		// 给用户来带的收益
		result.put("income", IndexStatisticsJob.income);
		
	
		
		
		return JSONObject.fromObject(result).toString();
	}

	
	/**
	 * 
	 * 签到
	 * 
	 * @param parameters
	 * @return
	 * @throws Exception
	 * 
	 * 
	 */
	public static String signIn(Map<String, String> parameters) throws Exception {
		
		ResultInfo result = new ResultInfo();
		JSONObject json = new JSONObject();
		
		String signId = parameters.get("userId");
		String currentDate = DateUtil.dateToString(new Date(), Constants.DATE_PLUGIN6);
		
		result = Security.decodeSign(signId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 return json.toString();
		}
		long userId = Long.parseLong(result.obj.toString());
		
		//及时查询用户的可用积分
		double scoreBalance = userFundService.findUserScoreBalance(userId);
		json.put("score", scoreBalance);
		
		List<SignInUserRecord> signInUserRecord = signInRecordService.listOfSignInUserRecord(userId, currentDate);
		//先查询是否已有当天的签到记录，若已经签到则不能再进行签到
		t_sign_in_record record = signInRecordService.querySignInToday(userId);
		if (record != null) {
			json.put("num", record.number);
			json.put("signIn", signInUserRecord);
			json.put("code", 2);
			json.put("msg", "今天已经签到了！");
			return json.toString();
		}
		
		Date signInDate = DateUtil.dateToDate(new Date(), Constants.DATE_PLUGIN5);
		int number = 1;
		String key = null;
		t_sign_in_rule rule = null;
		
		//获取所有的签到规则
		Map<String, t_sign_in_rule> rulesMap = signInRuleService.findAllRulesMap();
		if (rulesMap == null || rulesMap.size() <= 0) {
			JPA.setRollbackOnly();
			json.put("code", -1);
			json.put("msg", "查询签到规则失败");
			json.put("signIn", signInUserRecord);
			return json.toString();
		}
		
		//在没有当天签到记录的前提下，继续查询是否有昨天的签到记录
		record = signInRecordService.querySignInYesterday(userId);
		
		//若没有昨天的签到记录，则本次签到为连续第1天签到，否则就是连续第number+1(number+1<=7)天签到
		if (record == null) {
			//根据签到次数匹配签到规则的key
			key = matchSignInKey(number);
			
			if (rulesMap.containsKey(key)) {
				rule = rulesMap.get(key);
			} else {
				JPA.setRollbackOnly();
				json.put("code", -2);
				json.put("msg", "规则不完善");
				json.put("signIn", signInUserRecord);
				return json.toString();
			}
			
			if (rule == null) {
				JPA.setRollbackOnly();
				json.put("code", -2);
				json.put("msg", "没有签到规则");
				json.put("signIn", signInUserRecord);
				return json.toString();
			}
		
			//添加签到记录
			result = signInRecordService.addSignInRecord(userId, signInDate, number, rule.score, rule.extra_score);
			if (result.code < 1) {
				JPA.setRollbackOnly();
				json.put("code", result.code);
				json.put("msg", result.msg);
				json.put("signIn", signInUserRecord);
				return json.toString();
			}
		} else{
			number = record.number;
			if (number < 1) {
				json.put("code", -3);
				json.put("msg", "请刷新页面试试");
				json.put("signIn", signInUserRecord);
				return json.toString();
			}
			
			//在昨天连续签到的基础上+1
			number += 1;
			//若算上本次连续签到超过了7天，则重置为连续签到一天
			if (number > 7) {
				number = 1;
			}
			
			//根据签到次数匹配签到规则的key
			key = matchSignInKey(number);
			
			if (rulesMap.containsKey(key)) {
				rule = rulesMap.get(key);
			} else {
				JPA.setRollbackOnly();
				json.put("code", -2);
				json.put("msg", "规则不完善");
				json.put("signIn", signInUserRecord);
				return json.toString();
			}
			
			if (rule == null) {
				JPA.setRollbackOnly();
				json.put("code", -2);
				json.put("msg", "没有签到规则");
				json.put("signIn", signInUserRecord);
				return json.toString();
			}
			
			//添加签到记录
			result = signInRecordService.addSignInRecord(userId, signInDate, number, rule.score, rule.extra_score);
			if (result.code < 1) {
				JPA.setRollbackOnly();
				json.put("code", result.code);
				json.put("msg", result.msg);
				json.put("signIn", signInUserRecord);
				return json.toString();
			}
		}
		
		//签到赠送的积分为：正常赠送积分+额外加成积分
		double score = rule.score + rule.extra_score;
		
		//添加签到记录成功后，进行积分的奖励
		int rows = userFundService.updateUserScoreBalanceAdd(userId, score);
		if (rows <= 0) {
			JPA.setRollbackOnly();
			json.put("code", -3);
			json.put("msg", "请刷新页面试试");
			json.put("signIn", signInUserRecord);
			return json.toString();
		}
		
		/** 添加用户积分记录 */
		Map<String, String> summaryPrams = new HashMap<String, String>();
		summaryPrams.put("day", number + "");
		summaryPrams.put("score", (int) rule.score + "");
		summaryPrams.put("extra_score", (int) rule.extra_score + "");
		boolean addDeal = scoreUserService.addScoreUserInfo(userId,	score, scoreBalance, t_score_user.OperationType.SIGNIN,	summaryPrams);
		if (!addDeal) {
			JPA.setRollbackOnly();
			json.put("code", -4);
			json.put("msg", "添加积分记录失败");
			json.put("signIn", signInUserRecord);
			return json.toString();
		}
		
		//将上述签到的操作提交
		JPAPlugin.closeTx(false);
		JPAPlugin.startTx(false);
		
		//查询最新的签到记录
		signInUserRecord = signInRecordService.listOfSignInUserRecord(userId, currentDate);
		//及时查询用户的可用积分
		scoreBalance = userFundService.findUserScoreBalance(userId);
		
		json.put("num", number);//签到天数
		json.put("score", scoreBalance);//积分
		json.put("signIn", signInUserRecord);//已签到集合
		json.put("code", 1);
		json.put("msg", "成功");
		return json.toString();
		
	}
		
		
	/**
	 * 根据签到次数匹配签到规则的key
	 * 
	 * @param number
	 * @return
	 */
	private static String matchSignInKey(int number) {
		if (number < 1 || number > 7) {
			
			return null;
		}
		
		String key = null;
		
		switch (number) {
			case 1 :
				key = SignInKey.ONE;
				break;
			case 2 :
				key = SignInKey.TWO;
				break;
			case 3 :
				key = SignInKey.THREE;
				break;
			case 4 :
				key = SignInKey.FOUR;
				break;
			case 5 :
				key = SignInKey.FIVE;
				break;
			case 6 :
				key = SignInKey.SIX;
				break;
			case 7 :
				key = SignInKey.SEVEN;
				break;
			default :
				break;
		}
		
		return key;
	}
}
