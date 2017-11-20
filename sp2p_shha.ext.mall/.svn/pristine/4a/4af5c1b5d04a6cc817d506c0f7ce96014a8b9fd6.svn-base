package controllers.front.mallFront;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.shove.Convert;

import common.annotation.LoginCheck;
import common.annotation.SimulatedCheck;
import common.annotation.SubmitCheck;
import common.annotation.SubmitOnly;
import common.constants.ConfConst;
import common.constants.Constants;
import common.constants.SettingKey;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.JPAUtil;
import common.utils.LoggerUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.StrUtil;
import controllers.common.FrontBaseController;
import controllers.common.SubmitRepeat;
import controllers.common.interceptor.SimulatedInterceptor;
import controllers.common.interceptor.UserStatusInterceptor;
import models.common.bean.CurrUser;
import models.common.entity.t_advertisement;
import models.common.entity.t_user_info;
import models.common.entity.t_advertisement.Location;
import models.ext.mall.bean.FrontLottery;
import models.ext.mall.entiey.t_mall_address;
import models.ext.mall.entiey.t_mall_goods;
import models.ext.mall.entiey.t_mall_rewards;
import play.mvc.With;
import service.ext.mall.AddressService;
import service.ext.mall.ExchangeService;
import service.ext.mall.GoodsService;
import service.ext.mall.RewardsService;
import services.common.AdvertisementService;
import services.common.UserFundService;
import services.common.UserInfoService;
import services.core.InvestService;

/**
 * 前台-积分商城
 *
 * @description 
 *
 * @author jiayijian
 * @createDate 2017年3月16日
 */
@With({UserStatusInterceptor.class, SubmitRepeat.class,SimulatedInterceptor.class})
public class MallFrontCtrl extends FrontBaseController{
	
	protected static GoodsService goodsService = Factory.getService(GoodsService.class);
	
	protected static AddressService addressService = Factory.getService(AddressService.class);
	
	protected static UserFundService userFundService = Factory.getService(UserFundService.class);
	
	protected static ExchangeService exchangeService = Factory.getService(ExchangeService.class);
	
	protected static RewardsService rewardsService = Factory.getService(RewardsService.class);
	
	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	
	protected static InvestService investService = Factory.getService(InvestService.class);
	
	/** 广告图片 */
	protected static AdvertisementService advertisementService = Factory.getService(AdvertisementService.class);
	
	/**
	 * 前台-积分商城首页
	 * 
	 * @author jiayijian
	 * @createDate 2017年3月16日
	 */
	public static void mallFrontPre(){
		//查询banner 图(左)
		List<t_advertisement> bannerlist = advertisementService.queryAdvertisementFront(Location.PC_MALL_BANNER1,4);
		List<t_advertisement> bannerlist2 = advertisementService.queryAdvertisementFront(Location.PC_MALL_BANNER2,2);
		render(bannerlist,bannerlist2);
	}
	
	/**
	 * 前台-积分商城首页-实物商品
	 * @param currPage
	 * @param pageSize
	 *
	 * @author jiayijian
	 * @createDate 2017年3月16日
	 *
	 */
	public static void listOfEntityGoodsPre(int currPage,int pageSize){
		
		if (pageSize < 1) {
			pageSize = 5;
		}
		PageBean<t_mall_goods> pageBean = goodsService.pageOfFrontGoodsPre(0,currPage, pageSize);
		
		render(pageBean);
	}
	
	/**
	 * 前台-积分商城首页-虚拟商品
	 * @param currPage
	 * @param pageSize
	 *
	 * @author jiayijian
	 * @createDate 2017年3月16日
	 *
	 */
	public static void listOfVirtualGoodsPre(int currPage,int pageSize){
		
		if (pageSize < 1) {
			pageSize = 5;
		}
		PageBean<t_mall_goods> pageBean = goodsService.pageOfFrontGoodsPre(1,currPage, pageSize);
		
		render(pageBean);
	}
	
	/**
	 * 前台-积分商城首页-商品详情
	 *
	 * @author jiayijian
	 * @createDate 2017年3月16日
	 *
	 */
	@SubmitCheck
	@LoginCheck
	@SimulatedCheck
	public static void showGoodsDetailPre(String sign){
		
		ResultInfo result = Security.decodeSign(sign, Constants.MALL_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			flash.error(result.msg);
			mallFrontPre();
		}
		long goodsId = Long.parseLong((String) result.obj);
		
		t_mall_goods goods = goodsService.findByID(goodsId);
		
		if(goods == null){
			
			flash.error("该商品不存在");
			mallFrontPre();
		}
		
		CurrUser user = getCurrUser();
		
		t_mall_address defaultAddress = null;
		List<t_mall_address> addressList = null;
		int scoreBalance = 0;
		int hasExchangeNum = 0;//个人已兑换的数量
		// 兑换限制need
		int count = 0;
		if(user != null){
			
			hasExchangeNum = exchangeService.userHasExchange(user.id, goodsId);
			scoreBalance = (int)userFundService.findUserScoreBalance(user.id);
			defaultAddress = addressService.findDefaultAddress(user.id);
			addressList = addressService.findAddressByUserId(user.id);
			count = investService.countInvestOfUser(user.id);
		}
		
		List<Map<String, Object>> list = JPAUtil.getList("select * from t_pay_pro_city group by prov_num");
		
		render(goods,defaultAddress,addressList,list,scoreBalance,hasExchangeNum,count);
	}
	
	/**
	 * 前台-积分商城首页-商品详情-兑换
	 *
	 * @author jiayijian
	 * @createDate 2017年3月16日
	 *
	 */
	@SubmitOnly
	@LoginCheck
	@SimulatedCheck
	public static void exchangeGoods(String goodsSign, int exchangeNum){
		
		ResultInfo result = Security.decodeSign(goodsSign, Constants.MALL_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			error500();
		}
		
		long goodsId = Long.parseLong(result.obj.toString());
		
		t_mall_goods goods = goodsService.findByID(goodsId);
		
		if(goods == null){
			
			error500();
		}
		
		long userId = getCurrUser().id;
		
		long addressId = 0L;
		String addressIdStr = params.get("addressId");
		
		if (StringUtils.isNotBlank(addressIdStr)) {
			result = Security.decodeSign(addressIdStr, Constants.MALL_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
			if (result.code >= 1) {
				addressId = Long.parseLong(result.obj.toString());
			}
		}
		
		result = exchangeService.doExchange(userId, goodsId, addressId, exchangeNum);
		
		if(result.code < 1){
			LoggerUtil.info(true, "兑换商品时：%s", result.msg);
			flash.error(result.msg);
			showGoodsDetailPre(goods.sign);
		}
		
		flash.success("兑换成功");
		showGoodsDetailPre(goods.sign);
	}
	
	/**
	 * 前台-积分商城首页-商品详情-添加地址
	 *
	 * @param receiver 收货人姓名
	 * @param tel 电话号码
	 * @param province 省
	 * @param city 市
	 * @param address 详细地址
	 * @author jiayijian
	 * @createDate 2017年3月16日
	 *
	 */
	@LoginCheck
	@SimulatedCheck
	public static void addAddress(String receiver,String tel,String province,String city,String address){
		ResultInfo result = new ResultInfo();
		
		if(StringUtils.isBlank(receiver)){
			
			result.code = -1;
			result.msg ="请填写收件人";
			renderJSON(result);
		}
		
		if(StringUtils.isBlank(tel)){
			
			result.code = -1;
			result.msg ="请填写联系电话";
			renderJSON(result);
		}
		
		/* 验证手机号是否符合规范 */
		if (!StrUtil.isMobileNum(tel)) {
			
			result.code = -1;
			result.msg ="手机号不符合规范";
			renderJSON(result);
		}
		
		if(StringUtils.isBlank(province)){
			
			result.code = -1;
			result.msg ="请选择省市";
			renderJSON(result);
		}
		
		if(StringUtils.isBlank(city)){
			
			result.code = -1;
			result.msg ="请选择省市";
			renderJSON(result);
		}
		
		if(StringUtils.isBlank(address)){
			
			result.code = -1;
			result.msg ="请填写详细地址";
			renderJSON(result);
		}
		
		long userId = getCurrUser().id;
		
		result = addressService.addAddress(userId, receiver, tel, province, city, address);
		
		if(result.code < 1){
			result.code = -1;
			result.msg ="添加地址失败";
			renderJSON(result);
		}
		
		t_mall_address newAddress = (t_mall_address)result.obj;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("addressId",newAddress.sign );
		map.put("getAllAddress",newAddress.getAllAddress() );
		map.put("code", 1);
		map.put("msg", "添加地址成功");

		renderJSON(map);
	}
	
	/**
	 * 前台-积分商城首页-商品详情-设置默认地址
	 *
	 * @param sign 地址id
	 * @author jiayijian
	 * @createDate 2017年3月16日
	 *
	 */
	@LoginCheck
	@SimulatedCheck
	public static void setDefaultAddress(String sign){
		
		ResultInfo result = Security.decodeSign(sign, Constants.MALL_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			renderJSON(result);
		}
		long addressId = Long.parseLong((String) result.obj);
		
		
		long userId = getCurrUser().id;
		
		result = addressService.setDefaultAddress(userId, addressId);
		
		if(result.code < 1){
			LoggerUtil.info(true, "设置默认地址时：%s", result.msg);
			renderJSON(result);
		}
		
		renderJSON(result);
	}
	
	/**
	 * 前台-积分商城首页-商品详情-设置默认地址
	 *
	 * @param sign 地址id
	 * @author jiayijian
	 * @createDate 2017年3月16日
	 *
	 */
	@LoginCheck
	@SimulatedCheck
	public static void listOfAddressPre(){
		
		long userId = getCurrUser().id;
			
		List<t_mall_address> addressList = addressService.findAddressByUserId(userId);
		
		render(addressList);
	}
	
	/**
	 * 前台-积分商城首页-商品详情-设置默认地址
	 *
	 * @param sign 地址id
	 * @author jiayijian
	 * @createDate 2017年3月16日
	 *
	 */
	@LoginCheck
	@SimulatedCheck
	public static void delAddress(String sign){
		
		ResultInfo result = Security.decodeSign(sign, Constants.MALL_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			renderJSON(result);
		}
		long addressId = Long.parseLong((String) result.obj);
		
		
		long userId = getCurrUser().id;
		
		result = addressService.delAddress(userId, addressId);
		
		if(result.code < 1){
			LoggerUtil.info(true, "删除认地址时：%s", result.msg);
			renderJSON(result);
		}
		
		renderJSON(result);
	}
	
	
	/**
	 * 前台-积分商城首页-商品详情-选择地址
	 *
	 * @param sign id
	 * @author jiayijian
	 * @createDate 2017年3月16日
	 *
	 */
	@LoginCheck
	@SimulatedCheck
	public static void findAddress(String sign){
		
		ResultInfo result = Security.decodeSign(sign, Constants.MALL_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			renderJSON(result);
		}
		long addressId = Long.parseLong((String) result.obj);
		
		
		t_mall_address newAddress = addressService.findByID(addressId);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("addressId",newAddress.sign );
		map.put("getAllAddress",newAddress.getAllAddress() );
		map.put("code", 1);
		map.put("msg", "查询地址成功");

		renderJSON(map);
	}
	
	/**
	 * 前台-积分商城-积分抽奖页面
	 *
	 * @author jiayijian
	 * @createDate 2017年3月16日
	 *
	 */
	@LoginCheck
	public static void mallLotteryPre(){
		
		//最新的7个中奖记录
		List<FrontLottery> lotterylist = rewardsService.queryFrontLottery();
		//最新上架的8个奖品
		List<t_mall_rewards> rewardList =  rewardsService.queryFrontNewRewarsIsUse();
		//每日抽奖次数
		int dailyLotteryNumber = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.DAILY_LOTTERY_NUMBER), 0);
		//抽奖活动是否开启
		int activity_is_use = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.ACTIVITY_IS_USE), 0);
		//抽奖消耗的积分
		int expend_score = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.EXPEND_SCORE), 0);
		
		String activity_start_time = settingService.findSettingValueByKey(SettingKey.ACTIVITY_START_TIME);
		String activity_end_time = settingService.findSettingValueByKey(SettingKey.ACTIVITY_END_TIME);
		String activity_rule = settingService.findSettingValueByKey(SettingKey.ACTIVITY_RULE);
		
		Date startTime = DateUtil.strToDate(activity_start_time, Constants.DATE_PLUGIN);
		Date endTime = DateUtil.strToDate(activity_end_time, Constants.DATE_PLUGIN);
		Date nowTime = new Date();
		
		//活动时间未到
		if(nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()){
			
			activity_is_use = 0;
		}
		
		CurrUser user = getCurrUser();
		int scoreBalance = 0;//用户可用积分
		if(user != null){
			
			scoreBalance = (int)userFundService.findUserScoreBalance(user.id);
			
			t_user_info userInfo = userInfoService.findUserInfoByUserId(user.id);
			
			if(userInfo.lottery_day != null && !DateUtil.isDateEqual(userInfo.lottery_day, nowTime)){
				
				//更新控制（非当天抽奖重置抽奖次数）
				ResultInfo result = userInfoService.updateControlLotteryTimes(2, userInfo.user_id, nowTime);
				
				if(result.code < 1){
					
					result.msg = "更新抽奖次数控制失败";
					flash.error(result.msg);
					mallFrontPre();
				}
			}else{
				// 消耗积分最多能抽奖的次数
				int temp = (int) (1.0 * scoreBalance / expend_score);
				if(temp > dailyLotteryNumber) {
					// 当理论次数大于设置次数时
					// temp = 100 daily = 10
					dailyLotteryNumber = userInfo.lottery_times >  dailyLotteryNumber ? 0: dailyLotteryNumber - userInfo.lottery_times;
				} else {
					// 当理论次数小于设置次数时
					// temp = 100 daily = 10
					dailyLotteryNumber = temp;
				}
			}
		}
		
		render(lotterylist,activity_rule,scoreBalance,rewardList,dailyLotteryNumber,activity_is_use,expend_score);
	}
	/**
	 * 前台-积分商城-积分抽奖页面-执行抽奖
	 *
	 * @author jiayijian
	 * @createDate 2017年3月16日
	 *
	 */
	@SuppressWarnings("unchecked")
	@LoginCheck
	@SimulatedCheck
	public static void doLottery(){
		ResultInfo result = new ResultInfo();
		// unused
		// CurrUser user = getCurrUser();
		
		long userId = getCurrUser().id;
		//奖励商品记录
		result = rewardsService.doLottery(userId);
		
		if(result.code < 1){
			LoggerUtil.info(true,"执行抽奖时:"+result.msg );
			renderJSON(result);
		}
		//prizeId 下标，itemId 奖品id,msg 奖品名称
		Map<String,Object> map = (Map<String, Object>) result.obj;
		renderJSON(map);
	}
}


