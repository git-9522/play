package service.ext.mall;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.number.Arith;
import daos.ext.mall.ExchangeDao;
import models.common.entity.t_score_user;
import models.common.entity.t_user;
import models.core.entity.t_addrate_user;
import models.core.entity.t_cash;
import models.core.entity.t_red_packet;
import models.ext.mall.bean.BackExchanges;
import models.ext.mall.bean.UserExchanges;
import models.ext.mall.entiey.t_mall_address;
import models.ext.mall.entiey.t_mall_exchange;
import models.ext.mall.entiey.t_mall_goods;
import play.db.jpa.JPA;
import services.base.BaseService;
import services.common.ScoreUserService;
import services.common.UserFundService;
import services.common.UserService;
import services.core.CashUserService;
import services.core.InvestService;
import services.core.RateService;
import services.core.RedpacketUserService;

/**
 * 积分商城-实物兑换Service
 *
 * @author jiayijian
 * @createDate 2017年3月18日
 */
public class ExchangeService extends BaseService<t_mall_exchange> {

	protected ExchangeDao exchangeDao = Factory.getDao(ExchangeDao.class);
	
	protected static GoodsService goodsService = Factory.getService(GoodsService.class);
	
	protected static AddressService addressService = Factory.getService(AddressService.class);
	
	protected static UserFundService userFundService = Factory.getService(UserFundService.class);
	
	protected static ExchangeService exchangeService = Factory.getService(ExchangeService.class);
	
	protected RedpacketUserService redpacketUserService = Factory.getService(RedpacketUserService.class);
	
	protected CashUserService cashUserService = Factory.getService(CashUserService.class);
	
	protected static ScoreUserService scoreUserService = Factory.getService(ScoreUserService.class);
	
	protected static RateService rateService = Factory.getService(RateService.class);
	
	protected static UserService userService = Factory.getService(UserService.class);
	
	protected static InvestService investService = Factory.getService(InvestService.class);
	
	protected ExchangeService() {
		super.basedao = exchangeDao;
	}
	
	/**
	 * 查询兑换记录
	 * @param numNo 编号
	 * @param goodsName 商品名称
	 * @param currPage
	 * @param pageSize
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public PageBean<BackExchanges> pageOfBackExchanges(int showType,int currPage,int  pageSize,String numNo,String goodsName){
		
		return exchangeDao.pageOfBackExchanges(showType,currPage, pageSize,numNo,goodsName);
	}
	
	/**
	 * 查询用户兑换记录
	 * @param userid 
	 * @param currPage
	 * @param pageSize
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public PageBean<UserExchanges> pageOfExchangesByUser(int currPage,int  pageSize,long userId){
		
		return exchangeDao.pageOfExchangesByUser(currPage, pageSize,userId);
	}
	
	/**
	 * 查询用户兑换记录
	 * @param userid 
	 * @param excId
	 * @param pageSize
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public t_mall_exchange findUserExchangeByid(long userid,long excId){
		
		return exchangeDao.findUserExchangeByid(userid,excId);
	}
	
	/**
	 * 积分商城-派送
	 * 
	 *
	 * @param userId  用户id
	 * @param goodsId 商品id
	 * @param addressId 收货地址
	 * @param exchangeNum 兑换数量
	 * 
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public ResultInfo doExchange(long userId,long goodsId,Long addressId,int exchangeNum){
		
		ResultInfo result = new ResultInfo();
		
		t_mall_goods goods = goodsService.findByID(goodsId);
		
		if(goods == null){
			
			result.code = -1;
			result.msg ="该商品不存在";
			
			return result;
		}

		int count = investService.countInvestOfUser(userId);
		
		if(count < goods.min_invest_count) {
			result.code = ResultInfo.LESS_INVEST_COUNT;
			result.msg = "兑换该商品还需" + (goods.min_invest_count - count) + "次投标!";
			return result;
		}
		
		t_mall_address adderss = null;
		//创建实物兑换记录
		if(goods.type == t_mall_goods.GoodsType.ENTITY.code){
			
			adderss = addressService.findByID(addressId);
			
			if(adderss == null){
				
				result.code = -1;
				result.msg ="该地址不存在";
				
				return result;
			}
			
			if(adderss.user_id != userId){
				
				result.code = -1;
				result.msg ="该地址id错误";
				
				return result;
			}
		}
		
		if(exchangeNum < 1){
			
			result.code = -1;
			result.msg ="兑换数量错误";
			
			return result;
		}
		
		//总消耗积分
		int totalScore = (int) Arith.mul(goods.spend_scroe, exchangeNum);
		
		int scoreBalance = (int) userFundService.findUserScoreBalance(userId);
		
		if(totalScore > scoreBalance){
			
			result.code = -1;
			result.msg ="用户积分不足";
			
			return result;
		}
		
		/** 添加用户积分记录 */
		Map<String, String> summaryPrams = new HashMap<String, String>();
		summaryPrams.put("goodsName", goods.name);
		summaryPrams.put("score", (int) totalScore + "");
		result = scoreUserService.addScoreRecord(2, userId, (int) totalScore,t_score_user.OperationType.MALL_EXCHANGE,summaryPrams);
		
		if (result.code < 1) {
			
			JPA.setRollbackOnly();
			result.code = -1;
			result.msg = "添加积分记录失败";
			
			return result;
		}
		
		//校验库存
		if(!goods.is_unlimited_inven){
			
			if(exchangeNum > goods.inventory){
				
				result.code = -1;
				result.msg ="库存不足";
				LoggerUtil.info(true, result.msg);
				return result;
			}
		}
		
		int row2 = goodsService.updateGoodsInventory(goodsId, exchangeNum, goods.is_unlimited_inven);
		
		if(row2 < 1){
			
			result.code = -1;
			result.msg ="更新库数量失败";
			LoggerUtil.info(true, result.msg);
			return result;
		}
		
		//校验个人兑换限制
		if(!goods.is_unlimited_exc_max){
			
			//个人已兑换的数量
			int hasExchangeNum = exchangeDao.userHasExchange(userId, goodsId);
			
			if((exchangeNum+hasExchangeNum) >= goods.exchange_maximum){
				
				result.code = -1;
				result.msg ="已超过个人兑换数量上限 ";
				
				return result;
			}
		}
		
		//创建兑换记录
		
		result = createExchangeRecord(goods, userId, totalScore, exchangeNum, adderss);
		
		if(result.code < 1){
			
			return result;
		}
		
		//实物兑换至此结束
		if(goods.type == t_mall_goods.GoodsType.ENTITY.code){
			
			result.code = 1;
			result.msg ="兑换成功";
			
			return result;
			
		}
		
		//派发虚拟商品（红包、现金卷、加息卷）
		if(goods.attribute == t_mall_goods.GoodsAttr.REDPACKET.code){
			
			for(int i = 0 ; i < exchangeNum ;i++){
				
				//红包直接发放
				result = redpacketUserService.exchangeRedPacket(goods.name,userId,goodsId, goods.attribute_value, goods.min_invest_amount,goods.limit_day,i,t_red_packet.RedpacketType.EXCHANGE.code);
				
				if(result.code < 1){
					
					result.code = -1;
					result.msg ="红包发放失败 ";
					LoggerUtil.info(true, result.msg);
					return result;
				}
			}
			
		}else if(goods.attribute == t_mall_goods.GoodsAttr.CASH.code){
			
			for(int i = 0 ; i < exchangeNum ;i++){
				//直接发放现金券
				result =cashUserService.exchangeCash(userId,goodsId, goods.attribute_value, goods.min_invest_amount, goods.limit_day,i,t_cash.CashType.EXCHANGE.code);
				
				if(result.code < 1){
					
					result.code = -1;
					result.msg ="现金券发放失败 ";
					LoggerUtil.info(true, result.msg);
					return result;
				}
			}
			
		}else if(goods.attribute == t_mall_goods.GoodsAttr.VOLUME.code){
			
			for(int i = 0 ; i < exchangeNum ;i++){
				
				//直接发放加息卷
				result =rateService.exchangeRate(userId,goodsId, goods.attribute_value, goods.min_invest_amount, goods.limit_day,i,t_addrate_user.RateType.EXCHANGE.code);
				
				if(result.code < 1){
					
					result.code = -1;
					result.msg ="加息券发放失败 ";
					LoggerUtil.info(true, result.msg);
					return result;
				}
			}
		}
		
		
		result.code = 1;
		result.msg ="兑换成功";
		
		return result;
		
	}
	
	/**
	 * 创建兑换记录
	 * @param goods 兑换商品
	 * @param userId 用户id
	 * @param totalScore 总消耗积分
	 * @param exchangeNum 兑换数量
	 * @param adderss 收货地址
	 * @return
	 */
	public ResultInfo createExchangeRecord(t_mall_goods goods,long userId,int totalScore,int exchangeNum,t_mall_address adderss){
		ResultInfo result = new ResultInfo();
		
		t_mall_exchange exc = new t_mall_exchange();
		exc.time = new Date();
		exc.user_id = userId;
		exc.goods_id = goods.id;
		exc.name = goods.name;
		exc.type = goods.type;
		
		if(goods.type == t_mall_goods.GoodsType.VIRTUAL.code){
			
			exc.attribute = goods.attribute;
			exc.attribute_value = goods.attribute_value;
			exc.min_invest_amount = goods.min_invest_amount;
			exc.limit_day = goods.limit_day;
		}
		
		exc.image_url = goods.image_url;
		exc.description = goods.description;
		exc.spend_scroe = totalScore;
		exc.exchange_num = exchangeNum;
		
		if(goods.type == t_mall_goods.GoodsType.ENTITY.code){
			
			exc.status = t_mall_exchange.Status.TOBEDELIVERED.code;
			exc.receiver_name = adderss.receiver;
			exc.mobile = adderss.tel;
			exc.address = adderss.area+adderss.address;
		}else{
			
			exc.status = t_mall_exchange.Status.DELIVERED.code;
		}
		
		exc._key =  userId + "G" + goods.id + "E" + DateUtil.dateToString(new Date(), "yyyyMMddhhmmssss");
		
		if(!exchangeDao.save(exc)){
			
			result.code = -1;
			result.msg ="保存兑换记录失败";
			LoggerUtil.info(true, result.msg);
			return result;
		}
		
		result.code = 1;
		result.msg ="兑换成功";
		
		return result;
		
	}
	
	/**
	 * 统计个人已兑换数量
	 * @param goodsId 记录id
	 * @param userId 物流公司
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	
	public int userHasExchange(long userId,long goodsId){
		
		return exchangeDao.userHasExchange(userId, goodsId);
	}
	
	/**
	 * 积分商城-派送
	 * 
	 *
	 * @param goodsId 
	 * @param express_company 物流公司 
	 * @param tracking_number 快递单号 
	 * 
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public ResultInfo doDelivery(long goodsId,String express_company,String tracking_number){
		
		ResultInfo result = new ResultInfo();
		
		t_mall_exchange exhange = exchangeDao.findByID(goodsId);
		
		if(exhange == null){
			
			result.code = -1;
			result.msg ="该商品不存在";
			
			return result;
		}
		
		if(exhange.status != t_mall_exchange.Status.TOBEDELIVERED.code){
			
			result.code = -1;
			result.msg ="该商品已派送";
			
			return result;
		}
		
		int row = exchangeDao.updateExchangeStatus( goodsId, express_company, tracking_number);
		
		if(row < 1){
			
			result.code = -1;
			result.msg ="更新兑换记录状态失败";
			JPA.setRollbackOnly();
			return result;
		}
		
		result.code = 1;
		result.msg ="派送成功";
		return result;
	}

	public ResultInfo doEntityExchange(long userId, t_mall_goods good) {
		
		ResultInfo result = new ResultInfo();
		
		t_user user = userService.findByID(userId);
		
		if(user == null) {
			result.code = -1;
			result.msg ="用户并未收到";
			LoggerUtil.info(true, result.msg);
			return result;
		}
		
		t_mall_exchange exc = new t_mall_exchange();
		exc.time = new Date();
		exc.user_id = userId;
		exc.goods_id = good.id;
		exc.name = good.name;
		exc.type = t_mall_exchange.GoodsType.ENTITY.code;
		exc.image_url = good.image_url;
		exc.description = good.description;
		exc.spend_scroe = 0;
		exc.exchange_num = 1;
		exc.status = t_mall_exchange.Status.TOBEDELIVERED.code;
		exc.receiver_name = user.name;
		exc.mobile = user.mobile;
		exc._key =  userId + "G" + good.id + "E" + DateUtil.dateToString(new Date(), "yyyyMMddhhmmssss");
		
		if(!exchangeDao.save(exc)){
			result.code = -1;
			result.msg ="保存兑换记录失败";
			LoggerUtil.info(true, result.msg);
			return result;
		}
		
		result.code = 1;
		result.msg ="兑换成功";
		
		return result;
	}
}
