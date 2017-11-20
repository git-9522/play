package service.ext.mall;

import java.security.InvalidParameterException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import com.shove.Convert;

import common.constants.Constants;
import common.constants.SettingKey;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import daos.ext.mall.GoodsDao;
import daos.ext.mall.LotteryDao;
import daos.ext.mall.RewardsDao;
import models.common.entity.t_score_user;
import models.common.entity.t_user_info;
import models.core.entity.t_addrate_user;
import models.core.entity.t_cash;
import models.core.entity.t_red_packet;
import models.ext.mall.bean.FrontLottery;
import models.ext.mall.entiey.t_mall_goods;
import models.ext.mall.entiey.t_mall_lottery;
import models.ext.mall.entiey.t_mall_rewards;
import play.db.jpa.JPA;
import services.base.BaseService;
import services.common.ScoreUserService;
import services.common.SettingService;
import services.common.UserFundService;
import services.common.UserInfoService;
import services.core.CashUserService;
import services.core.RateService;
import services.core.RedpacketUserService;

/**
 * 积分商城-奖品Service
 *
 * @author jiayijian
 * @createDate 2017年3月18日
 */
public class RewardsService extends BaseService<t_mall_rewards>{
	
	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	
	protected static SettingService settingService = Factory.getService(SettingService.class);
	
	protected RewardsDao rewardsDao = Factory.getDao(RewardsDao.class);
	
	protected LotteryDao lotteryDao = Factory.getDao(LotteryDao.class);
	
	protected GoodsDao goodsDao = Factory.getDao(GoodsDao.class);
	
	protected static UserFundService userFundService = Factory.getService(UserFundService.class);
	
	protected static ScoreUserService scoreUserService = Factory.getService(ScoreUserService.class);
	
	protected static RedpacketUserService redpacketUserService = Factory.getService(RedpacketUserService.class);
	
	protected static CashUserService cashUserService = Factory.getService(CashUserService.class);
	
	protected static RateService rateService = Factory.getService(RateService.class);
	
	protected static ExchangeService exchangeService = Factory.getService(ExchangeService.class);
	
	protected RewardsService() {
		super.basedao = rewardsDao;
	}
	
	/**
	 * 查询奖品
	 * @param numNo 编号
	 * @param rewardName 奖品名称
	 * @param currPage
	 * @param pageSize
	 * @param exports 1:导出  default：不导出
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public PageBean<t_mall_rewards> pageOfBackRewards(int currPage,int  pageSize,String numNo,String rewardName,int exports){
		
		return rewardsDao.pageOfBackRewards(currPage, pageSize,numNo,rewardName, exports);
	}
	
	/**
	 * 校验奖品参数
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public ResultInfo checkRewardsValue(t_mall_rewards reward){
		ResultInfo result = new ResultInfo();
		
		if(StringUtils.isBlank(reward.name) || reward.name.length() > 10){
			
			result.code = -1;
			result.msg = "奖品名称为空或名称过长";
			
			return result;
		}
		
		if(reward.type != 5 && StringUtils.isBlank(reward.image_url)){
			
			result.code = -1;
			result.msg = "奖品图片为空";
			
			return result;
		}
		
		if(reward.type > 5 || reward.type < 0){
			
			result.code = -1;
			result.msg = "积分模式错误";
			
			return result;
		}
		
		if(reward.type == t_mall_rewards.RewardType.MULTIPLESCORE.code && reward.type_value <= 0){
			
			result.code = -1;
			result.msg = "积分倍数应大于等于1";
			
			return result;
		}
		
		if(reward.type == t_mall_rewards.RewardType.REDPACKET.code && (reward.type_value <= 0 || reward.type_value > 1000)){
			
			result.code = -1;
			result.msg = "红包金额应在1~1000之间";
			
			return result;
		}
		
		if(reward.type == t_mall_rewards.RewardType.CASH.code && (reward.type_value <= 0 || reward.type_value > 1000)){
			
			result.code = -1;
			result.msg = "现金卷应在1~1000之间";
			
			return result;
		}
		
		if(reward.type == t_mall_rewards.RewardType.CASH.RATE.code && (reward.type_value <= 0 || reward.type_value > 100)){
			
			result.code = -1;
			result.msg = "加息卷应在0.1~100之间";
			
			return result;
		}
		
		if(reward.type != t_mall_rewards.RewardType.ENTITY.code && reward.type != t_mall_rewards.RewardType.MULTIPLESCORE.code  && reward.type != t_mall_rewards.RewardType.SPECIFYSCORE.code){
			
			if(reward.min_invest_amount <= 0){
				
				result.code = -1;
				result.msg = "最低投资应大于0";
				
				return result;
			}
			
			if(reward.limit_day <= 0){
				
				result.code = -1;
				result.msg = "有效期限应大于0";
				
				return result;
			}
		}
		
		if(reward.type_value < 0){
			
			result.code = -1;
			result.msg = "奖品类型参数值错误";
			
			return result;
		}
		
		if(reward.probability <= 0 || reward.probability >= 100 ){
			
			result.code = -1;
			result.msg = "中奖概率应该大于0且小于100";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "奖品参数正常";
		return result;
	}
	
	/**
	 * 添加奖品
	 * 
	 * @param goods 
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public ResultInfo addReward(t_mall_rewards reward){
		
		ResultInfo result = new ResultInfo();
		
		t_mall_rewards rd = new t_mall_rewards();
		rd.time = new Date();
		rd.name = reward.name;
		rd.type = reward.type;
		rd.type_value = reward.type_value;
		rd.image_url = reward.image_url;
		
		if(reward.type == t_mall_rewards.RewardType.ENTITY.code) {
			rd.image_url = goodsDao.findByID((long) rd.type_value).image_url;
		} else if(reward.type != t_mall_rewards.RewardType.MULTIPLESCORE.code  && reward.type != t_mall_rewards.RewardType.SPECIFYSCORE.code){
			rd.min_invest_amount = reward.min_invest_amount;
			rd.limit_day = reward.limit_day;
			rd.bid_period = reward.bid_period;
		} else {
			rd.min_invest_amount = 0;
			rd.limit_day = 0;
		}
		rd.probability = reward.probability;
		rd.is_use = false;//false-下架
		
		if(!rewardsDao.save(rd)){
			
			result.code = -1;
			result.msg = "保存奖品失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "保存奖品成功";
		result.obj = rd;
		
		return result;
	}
	
	
	/**
	 * 编辑奖品
	 * 
	 * @param reward 编辑参数
	 * @param rd 旧参数
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public ResultInfo editRewards(t_mall_rewards reward,t_mall_rewards rd){
		
		ResultInfo result = new ResultInfo();
		
		rd.last_edit_time = new Date();
		rd.name = reward.name;
		rd.type = reward.type;
		rd.type_value = reward.type_value;
		rd.image_url = reward.image_url;
		
		if(reward.type == t_mall_rewards.RewardType.ENTITY.code) {
			t_mall_goods good = goodsDao.findByID((long) rd.type_value);
			rd.image_url = good.image_url;
		} else if(reward.type != t_mall_rewards.RewardType.MULTIPLESCORE.code  && reward.type != t_mall_rewards.RewardType.SPECIFYSCORE.code){
			rd.min_invest_amount = reward.min_invest_amount;
			rd.limit_day = reward.limit_day;
			rd.bid_period = reward.bid_period;
		} else {
			rd.min_invest_amount = 0;
			rd.limit_day = 0;
		}
		
		rd.probability = reward.probability;
		
		if(!rewardsDao.save(rd)){
			
			result.code = -1;
			result.msg = "编辑奖品失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "编辑奖品成功";
				
		return result;
	}
	

	/**
	 * 通过id更新奖品的上下架状态
	 *
	 * @param rewardId
	 * @param isUse 1-上架产品 ；0-下架产品
	 * @return
	 *
	 * @author jiayijian
	 * @createDate 2017年3月21日
	 */
	public boolean updateRewardsStatus(long rewardId, boolean isUse){
		
		int i = rewardsDao.updateRewardsStatus(rewardId, isUse);
		if (i!=1) {
			
			return false;
		}
		
		return true;
	}
	
	/**
	 * 奖品 奖品删除
	 *
	 * @param id 奖品id
	 * @return
	 * 
	 * @author jiayijian
	 * @createDate 2017年3月21日
	 */
	public boolean delRewards(long id) {
		int row = rewardsDao.delete(id);
		if(row<=0){
			
			return false;
		}
		
		return true;
	}
	
	/**
	 *查询所有上架的奖品
	 * 
	 * @return
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public List<t_mall_rewards> queryALlRewarsIsUse(){
		
		return rewardsDao.queryALlRewarsIsUse();
	}
	
	/**
	 *查询最新上架的8个奖品
	 * 
	 * @return
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public List<t_mall_rewards> queryFrontNewRewarsIsUse(){
		
		return rewardsDao.queryFrontNewRewarsIsUse();
	}
	
	/**
	 *查询所有上架的奖品总概率
	 * 
	 * @return
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public double queryTotalProbability(){
		
		return rewardsDao.queryTotalProbability();
	}
	
	/**
	 *查询最新的7个中奖记录
	 * 
	 * @return
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public List<FrontLottery> queryFrontLottery(){
		
		return lotteryDao.queryFrontLottery();
	}
	
	/**
	 * 创建中奖记录
	 * @param userId
	 * @param spend_scroe
	 * @param reward
	 * @return
	 */
	public ResultInfo createLotteryRecord(long userId,int spend_scroe,t_mall_rewards reward){
		ResultInfo result = new ResultInfo();
		
		t_mall_lottery lot = new t_mall_lottery();
		
		lot.time = new Date();
		lot.reward_id = reward.id;
		lot.user_id = userId;
		lot.name = reward.name;
		lot.type = reward.type;
		lot.type_value = reward.type_value;
		lot.min_invest_amount = reward.min_invest_amount;
		lot.limit_day = reward.limit_day;
		lot.image_url = reward.image_url;
		lot.spend_scroe = spend_scroe;
		
		if(!lotteryDao.save(lot)){
			
			result.code = -1;
			result.msg ="保存中奖记录失败";
			LoggerUtil.info(true, result.msg);
			return result;
		}
		
		result.code = 1;
		result.msg ="保存中奖记录成功";
		
		return result;
	}
	
	/**
	 * 前台-积分商城-积分抽奖页面-执行抽奖
	 *
	 * @author jiayijian
	 * @createDate 2017年3月16日
	 */
	public  ResultInfo doLottery(long userId){
		
		ResultInfo result = new ResultInfo();
		
		//抽奖活动是否开启
		int activity_is_use = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.ACTIVITY_IS_USE), 0);
		if(activity_is_use != 1){
			
			result.code = -1;
			result.msg ="抽奖未开启";
			return result;
		}
		
		//最新上架的8个奖品
		List<t_mall_rewards> rewardList =  rewardsDao.queryFrontNewRewarsIsUse();
		
		if(rewardList == null || rewardList.size() != 8){
			
			result.code = -1;
			result.msg = "抽奖未开启";
			
			return result;
		}
		
		String activity_start_time = settingService.findSettingValueByKey(SettingKey.ACTIVITY_START_TIME);
		String activity_end_time = settingService.findSettingValueByKey(SettingKey.ACTIVITY_END_TIME);
		
		Date startTime = DateUtil.strToDate(activity_start_time, Constants.DATE_PLUGIN);
		Date endTime = DateUtil.strToDate(activity_end_time, Constants.DATE_PLUGIN);
		Date nowTime = new Date();
		
		//活动时间未到
		if(nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()){
			
			result.code = -1;
			result.msg = "抽奖未开启";
			
			return result;
		}
		
		//返回奖品记录id、奖品名称
		Object res[] = doAward(rewardList);
		
		if(res == null || res.length == 0){
			result.code = -1;
			result.msg = "抽奖执行失败";
			
			return result;
		}
		//prizeId 下标，itemId 奖品id,msg 奖品名称
		int prizeId = (Integer) res[0];
		long itemId = (Long) res[1];
		String msg =  (String) res[2];
		
		result =  LotteryRecord(itemId,userId);
		
		if(result.code < 1){
			
			LoggerUtil.info(true,"生成抽奖记录时:"+result.msg );
			
			result.code = -1;
			//result.msg = "生成抽奖记录失败";
			
			return result;
		}
		
		//是否中奖标记：0未中奖，1中奖
		int isPrize = 0;
		if(result.obj != null){
			isPrize = (Integer) result.obj;
		}
		
		result.code = 1;
		result.msg = "抽奖成功";
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("prizeId", prizeId);
		map.put("itemId", itemId);
		map.put("msg", msg);
		map.put("isPrize", isPrize);
		
		result.obj = map;
		
		LoggerUtil.info(false,"抽奖成功 = 下标："+map.get("prizeId")+"\t奖项ID:"+map.get("itemId")+"\t提示信息:"+map.get("msg"));
		return result;
	}
	
	/**
	 * 抽奖记录
	 *
	 * @param rewardId 商品Id
	 * @param userId 用户id
	 * 
	 * @return
	 *
	 * @author jiayijan
	 * @createDate 2017年1月25日
	 */
	public ResultInfo LotteryRecord(long rewardId,long userId){
		ResultInfo result = new ResultInfo();
		
		if (rewardId <= 0) {
			throw new InvalidParameterException("rewardId <= 0");
		}
		
		t_mall_rewards reward = rewardsDao.findByID(rewardId);
		
		if(reward == null){
			
			result.code = -1;
			result.msg = "查询奖励商品失败";
		   
			return result;
		}
		
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		
		if(userInfo == null){
			
			result.code = -1;
			result.msg = "查询用户信息失败";
		   
			return result;
		}
		
		//用户可用积分
		int scoreBalance = (int)userFundService.findUserScoreBalance(userId);
		//所需积分
		int expend_score = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.EXPEND_SCORE), 0);
		
		if(scoreBalance < expend_score){
			
			result.code = -1;
			result.msg = "可用积分不足,所需积分为"+expend_score;
		   
			return result;
		}
		
		/*减少积分*/
		/** 添加用户积分记录 */
		Map<String, String> summaryPrams = new HashMap<String, String>();
		summaryPrams.put("score", (int) expend_score + "");
		result = scoreUserService.addScoreRecord(2, userId, (int) expend_score,t_score_user.OperationType.MALL_LOTTERY_MINUS,summaryPrams);
		
		if (result.code < 1) {
			
			JPA.setRollbackOnly();
			result.code = -1;
			result.msg = "添加积分记录失败";
			
			return result;
		}
		
		/*每天抽奖次数控制*/
		result = controlLotteryTimes(userInfo);
		
		if(result.code < 1){
			
			if(result.code != -2){
				
				result.msg = "更新抽奖次数控制失败";
			}
			
			result.code = -1;
			
			return result;
		}
		
		// 积分奖励处理
		if(reward.type == t_mall_rewards.RewardType.MULTIPLESCORE.code || reward.type == t_mall_rewards.RewardType.SPECIFYSCORE.code){
			//奖励积分
			int addScore = (int) (reward.type_value * expend_score);//倍数积分 
			
			if( reward.type == t_mall_rewards.RewardType.SPECIFYSCORE.code ){
				
				if(reward.type_value == 0){//指定积分为0时，是不中奖。
					
					result.code = 1;
					result.msg = "谢谢参与";//不中奖,到止结束。
					return result;
				}
				
				addScore = (int) reward.type_value;//指定积分 
			}
			
			/*增加用户积分*/
			/** 添加用户积分记录 */
			Map<String, String> summaryPramsAdd = new HashMap<String, String>();
			summaryPramsAdd.put("score", (int) addScore + "");
			result = scoreUserService.addScoreRecord(1, userId, (int) addScore,t_score_user.OperationType.MALL_LOTTERY_ADD,summaryPramsAdd);
			
			if (result.code < 1) {
				
				JPA.setRollbackOnly();
				result.code = -1;
				result.msg = "添加积分记录失败";
				
				return result;
			}
			
		}else if(reward.type == t_mall_rewards.RewardType.REDPACKET.code){
			
			//红包直接发放
			result = redpacketUserService.exchangeRedPacket(reward.name,userId,reward.id, reward.type_value, reward.min_invest_amount,reward.bid_period,reward.limit_day,1,t_red_packet.RedpacketType.LOTTERY.code);
			
			if(result.code < 1){
				
				result.code = -1;
				result.msg ="红包发放失败 ";
				LoggerUtil.info(true, result.msg);
				return result;
			}
			
		}else if(reward.type == t_mall_rewards.RewardType.CASH.code){
			
			//直接发放现金券
			result =cashUserService.exchangeCash(userId,reward.id, reward.type_value, reward.min_invest_amount,reward.bid_period, reward.limit_day,1,t_cash.CashType.LOTTERY.code);
			
			if(result.code < 1){
				
				result.code = -1;
				result.msg ="现金券发放失败 ";
				LoggerUtil.info(true, result.msg);
				return result;
			}
			
		}else if(reward.type == t_mall_rewards.RewardType.RATE.code){
			
			//直接发放加息卷
			result =rateService.exchangeRate(userId,reward.id, reward.type_value, reward.min_invest_amount,reward.bid_period, reward.limit_day,1,t_addrate_user.RateType.LOTTERY.code);
			
			if(result.code < 1){
				
				result.code = -1;
				result.msg ="加息券发放失败 ";
				LoggerUtil.info(true, result.msg);
				return result;
			}
		} else if (reward.type == t_mall_rewards.RewardType.ENTITY.code) {
			// 创建 t_mall_exchange
			t_mall_goods good = goodsDao.findByID((long) reward.type_value);
			
			result = exchangeService.doEntityExchange(userId, good);
			
			if(result.code < 1){
				
				result.code = -1;
				result.msg ="实物发放失败 ";
				LoggerUtil.info(true, result.msg);
				return result;
			}
		}
		
		/*生成奖励记录*/
		result = createLotteryRecord(userId, expend_score, reward);
		
		if(result.code < 1){
			
			result.code = -1;
			result.msg = "生成中奖记录失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "生成抽奖记录成功";
		result.obj = 1;//标记为已中奖
		
		return result;
	}
	
	/**
	 * 每天抽奖次数控制
	 *
	 * @param itemId 商品Id
	 * @param userId 用户id
	 * 
	 * @return
	 *
	 * @author jiayijan
	 * @createDate 2017年1月25日
	 */
	public ResultInfo  controlLotteryTimes(t_user_info userInfo){
		ResultInfo result = new ResultInfo();
		
		Date date = new Date();
		
		/*每天抽奖次数控制*/
		int dailyLotteryNumber = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.DAILY_LOTTERY_NUMBER), 0);
		
		if(userInfo.lottery_day == null || DateUtil.isDateEqual(userInfo.lottery_day, date)){
			
			if(userInfo.lottery_times >= dailyLotteryNumber){
				
				result.code = -2;
				result.msg = "每天只能抽奖"+dailyLotteryNumber+"次";
				
				return result;
			}
			
			//更新控制（当天抽奖增加抽奖数次）
			result = userInfoService.updateControlLotteryTimes(1, userInfo.user_id, date);
			
			if(result.code < 1){
				
				result.code = -1;
				result.msg = "更新抽奖次数控制失败";
				
				return result;
			}
		}else{
			
			//更新控制（非当天抽奖重置抽奖次数）
			result = userInfoService.updateControlLotteryTimes(0, userInfo.user_id, date);
			
			if(result.code < 1){
				
				result.code = -1;
				result.msg = "更新抽奖次数控制失败";
				
				return result;
			}
		}
		
		result.code = 1;
		result.msg = "更新抽奖次数控制成功";
		
		return result;
	}
	
	/**
	 * 积分商城-抽奖执行方法
	 * @return 返回奖品记录id、奖品名称
	 * @author jiayijian
	 * @createDate 2017年1月23日
	 */
	public Object[] doAward(List<t_mall_rewards> items){
		
		int prizeId = -1;//商品id下标
		long itemId = -1;//商品id
		String msg = "";//商品名称
		try {
			
			//index,id,prize【奖项】,v【中奖率】
			Object[][] prizeArr = new  Object[items.size()][4];
			
			for(int i=0;i<items.size();i++){
				
				//中奖率大于O才纳入抽奖
				if(items.get(i).probability > 0){
					
					prizeArr[i][0] = i;
					prizeArr[i][1] = items.get(i).id;
					prizeArr[i][2] = items.get(i).name;
					prizeArr[i][3] = items.get(i).probability;
				}
				
			}
			
			//概率数组
			Double obj[] = new Double[prizeArr.length];
			for(int i=0;i<prizeArr.length;i++){
				obj[i] = (Double) prizeArr[i][3];
			}
			
			//LoggerUtil.info(false,"各项原概率："+Arrays.toString(obj));
			
			int  sum = 0;//概率数组的总概率精度 
			for(int i=0;i<obj.length;i++){
				
				obj[i] = obj[i]* 100;
				sum+=obj[i];
			}
			
			int randomNum = new Random().nextInt(sum);//随机生成0到sum的整数
			
			//LoggerUtil.info(false,"各项新概率："+Arrays.toString(obj) +" 概率总和："+sum +" 随机数："+randomNum );
			
			//sumOne区间开始
			double sumOne = 0;//概率转换为概率总和上的比例区间，随机数在区间内为中奖，
			for(int i=0;i<obj.length;i++){//概率数组循环 
				//sumTwo区间结束
				double sumTwo = sumOne + obj[i];
				
				if(sumOne < randomNum && randomNum <= sumTwo){//中奖
					
					prizeId = i;
					break;
				}else{
					
					sumOne = sumTwo;
				}
			}
			itemId = (Long) prizeArr[prizeId][1];
			//商品名称
			msg = (String) prizeArr[prizeId][2];
		} catch (Exception e) {
			
			e.printStackTrace();
			
			return null;
		}
		
		return new Object[]{prizeId,itemId,msg};
	}
}
