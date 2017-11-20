package services.common;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.shove.Convert;

import common.constants.SettingKey;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import daos.common.InvestRewardDao;
import models.common.entity.t_invest_reward;
import services.base.BaseService;

public class InvestRewardService extends BaseService<t_invest_reward> {

	protected InvestRewardDao investRewardDao = Factory.getDao(InvestRewardDao.class);
	
	protected static SettingService settingService = Factory.getService(SettingService.class);
	
	protected static InvestLotteryService investLotteryService = Factory.getService(InvestLotteryService.class);
	
	protected InvestRewardService() {
		basedao = investRewardDao;
	}
	
	public int queryRewardCount(boolean isUse) {
		return investRewardDao.queryRewardCount(isUse);
	}
	
	public double queryRewardProbability(boolean isUse) {
		return investRewardDao.queryRewardProbability(isUse);
	}

	public PageBean<t_invest_reward> pageOfBackRewards(int currPage, int pageSize, String numNo, String rewardName,
			int exports) {
		return investRewardDao.pageOfBackRewards(currPage, pageSize, numNo, rewardName, exports);
	}

	public ResultInfo insert(t_invest_reward rewards) {
		ResultInfo result = new ResultInfo();
		rewards.time = new Date();
		rewards.is_use = false;
		if(investRewardDao.save(rewards)) {
			result.code = 1;
			result.msg = "保存奖品成功";
		} else {
			result.code = -1;
			result.msg = "保存奖品失败";
		}
		return result;
	}

	public ResultInfo update(t_invest_reward rewards, t_invest_reward rd) {
		ResultInfo result = new ResultInfo();
		rd.name = rewards.name;
		rd.value = rewards.value;
		rd.probability = rewards.probability;
		rd.is_use = false;
		rd.last_edit_time = new Date();
		if(investRewardDao.save(rewards)) {
			result.code = 1;
			result.msg = "更新奖品成功";
		} else {
			result.code = -1;
			result.msg = "更新奖品失败";
		}
		return result;
	}

	public boolean updateRewardsStatus(long id, boolean isUse) {
		int i = investRewardDao.updateRewardsStatus(id, isUse);
		if (i != 1) {
			return false;
		}
		return true;
	}

	public boolean del(long id) {
		int row = investRewardDao.delete(id);
		if(row<=0){
			return false;
		}
		return true;
	}

	public ResultInfo doLottery(long userId) {
		ResultInfo result = new ResultInfo();
		
		//抽奖活动是否开启
		int lottery_is_use = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.LOTTERY_IS_USE), 0);
		if(lottery_is_use != 1){
			result.code = -1;
			result.msg ="抽奖未开启";
			return result;
		}
			
		//最新上架的8个奖品
		List<t_invest_reward> rewards =  findListByColumn("is_use = ?", true);
			
		if(rewards == null || rewards.size() != 8){
			result.code = -1;
			result.msg = "抽奖未开启";
			return result;
		}
			
		String activity_start_time = settingService.findSettingValueByKey(SettingKey.LOTTERY_START_TIME);
		String activity_end_time = settingService.findSettingValueByKey(SettingKey.LOTTERY_END_TIME);
			
		Date startTime = DateUtil.strToDate(activity_start_time, "MM/dd/yyyy");
		Date endTime = DateUtil.strToDate(activity_end_time, "MM/dd/yyyy");
		Date nowTime = new Date();
			
		//活动时间未到
		if(nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()){
			result.code = -1;
			result.msg = "抽奖未开启";
			return result;
		}
			
		//返回奖品记录id、奖品名称
		Object res[] = lottery(rewards);
			
		if(res == null || res.length == 0){
			result.code = -1;
			result.msg = "抽奖执行失败";
			return result;
		}
		
		//prizeId 下标，itemId 奖品id,msg 奖品名称
		int prizeId = (Integer) res[0];
		long itemId = (Long) res[1];
		String msg =  (String) res[2];
			
		result =  investLotteryService.insert(rewards.get(prizeId), userId);
			
		if(result.code < 1){
			LoggerUtil.info(true,"生成抽奖记录时:"+result.msg );
			result.code = -1;
			//result.msg = "生成抽奖记录失败";
			return result;
		}
			
		result.code = 1;
		result.msg = "抽奖成功";
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("prizeId", prizeId);
		map.put("itemId", itemId);
		map.put("msg", msg);
		
		result.obj = map;
		
		LoggerUtil.info(false,"抽奖成功 = 下标："+map.get("prizeId")+"\t奖项ID:"+map.get("itemId")+"\t提示信息:"+map.get("msg"));
		return result;
	}
	
	public Object[] lottery(List<t_invest_reward> rewards){
		
		int prizeId = -1;//商品id下标
		long itemId = -1;//商品id
		String msg = "";//商品名称
		try {
			//index,id,prize【奖项】,v【中奖率】
			Object[][] prizeArr = new  Object[rewards.size()][4];
			for(int i = 0; i < rewards.size(); i++){
				//中奖率大于O才纳入抽奖
				if(rewards.get(i).probability > 0){
					prizeArr[i][0] = i;
					prizeArr[i][1] = rewards.get(i).id;
					prizeArr[i][2] = rewards.get(i).value;
					prizeArr[i][3] = rewards.get(i).probability;
				}
			}
			
			//概率数组
			Double[] obj = new Double[prizeArr.length];
			for(int i = 0; i < prizeArr.length; i++){
				obj[i] = (Double) prizeArr[i][3];
			}
			
			//LoggerUtil.info(false,"各项原概率："+Arrays.toString(obj));
			
			int  sum = 0;//概率数组的总概率精度 
			for(int i = 0; i < obj.length; i++){
				obj[i] = obj[i]* 100;
				sum += obj[i];
			}
			
			int randomNum = new Random().nextInt(sum);//随机生成0到sum的整数
			
			//LoggerUtil.info(false,"各项新概率："+Arrays.toString(obj) +" 概率总和："+sum +" 随机数："+randomNum );
			
			//sumOne区间开始
			double sumOne = 0;//概率转换为概率总和上的比例区间，随机数在区间内为中奖，
			for(int i = 0; i < obj.length; i++){//概率数组循环 
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
			msg = "" + prizeArr[prizeId][2];
		} catch (Exception e) {
			
			e.printStackTrace();
			
			return null;
		}
		
		return new Object[]{prizeId, itemId, msg};
	}
	
}