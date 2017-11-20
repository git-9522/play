package controllers.app.wealth;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.shove.Convert;

import service.ext.mall.AddressService;
import service.ext.mall.ExchangeService;
import service.ext.mall.GoodsService;
import services.common.UserFundService;
import services.common.UserService;
import services.core.CashUserService;
import services.core.RateService;
import services.core.RedpacketUserService;
import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Factory;
import common.utils.JPAUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;
import models.common.bean.CurrUser;
import models.common.entity.t_user;
import models.core.entity.t_addrate_user;
import models.core.entity.t_cash_user;
import models.core.entity.t_red_packet_user;
import models.ext.mall.entiey.t_mall_address;
import models.ext.mall.entiey.t_mall_goods;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 积分商城
 *
 * @description 绑定收货地址、积分兑换、奖品展示、红包、现金券、加息券
 *
 *
 */
public class IntegralMallAction {
	
	protected static AddressService adressService =  Factory.getService(AddressService.class);
	
	protected static ExchangeService exchangeService =  Factory.getService(ExchangeService.class);
	
	protected static GoodsService goodsService =  Factory.getService(GoodsService.class);
	
	protected static RedpacketUserService redpacketUserService = Factory.getService(RedpacketUserService.class);

	protected static UserService userService = Factory.getService(UserService.class);

	protected static RateService rateService = Factory.getService(RateService.class);
	
	protected static CashUserService cashUserService = Factory.getService(CashUserService.class);

	protected static AddressService addressService = Factory.getService(AddressService.class);

	protected static UserFundService userFundService = Factory.getService(UserFundService.class);
	
	/**
	 * 展示积分商城
	 * 
	 * 
	 * @return
	 * @throws Exception
	 * 
	 */
	public static String showMall() throws Exception {
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		
		//虚拟物品
		List<t_mall_goods> virtualGoods = goodsService.findSameGoods(1);
		
		//实体物品
		List<t_mall_goods> entityGoods = goodsService.findSameGoods(0);
		
		result.put("virtualGoods", virtualGoods);
		result.put("entityGoods", entityGoods);
		result.put("code", 1);
		result.put("msg", "成功");

		return JSONObject.fromObject(result).toString();
	}
	
	
	/**
	 * 
	 * 展示红包（使用中，未使用，过期）
	 * 
	 * @return
	 * @throws Exception
	 * 
	 */
	public static String showMyRedPacket(Map<String, String> parameters) throws Exception{
		
		JSONObject json = new JSONObject();
		
		int currPage = Convert.strToInt(parameters.get("currPage"), 1);
		int pagesize = Convert.strToInt(parameters.get("pagesize"), 5);
		String signId = parameters.get("userId");
		
		ResultInfo result = Security.decodeSign(signId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 return json.toString();
			 
		}
		
		long userId = Long.parseLong(result.obj.toString());
		
		PageBean<t_red_packet_user> used = redpacketUserService.pageOfUserRed(userId, t_red_packet_user.RedpacketStatus.USED.code, currPage, pagesize);
		PageBean<t_red_packet_user> unused = redpacketUserService.pageOfUserRed(userId, t_red_packet_user.RedpacketStatus.UNUSED.code, currPage, pagesize);
		PageBean<t_red_packet_user> expired = redpacketUserService.pageOfUserRed(userId, t_red_packet_user.RedpacketStatus.EXPIRED.code, currPage, pagesize);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("used", used);
		map.put("unused", unused);
		map.put("expired", expired);
		map.put("code", 1);
		map.put("msg", "成功");
		
		return JSONObject.fromObject(map).toString();
	}
	
	
	/**
	 * 
	 * 展示加息券（使用中，未使用，过期）
	 * 
	 * @return
	 * @throws Exception
	 * 
	 */
	public static String showMyRates(Map<String, String> parameters) throws Exception{
		
		JSONObject json = new JSONObject();

		int currPage = Convert.strToInt(parameters.get("currPage"), 1);
		int pagesize = Convert.strToInt(parameters.get("pagesize"), 5);
		String signId = parameters.get("userId");
		
		ResultInfo result = Security.decodeSign(signId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 return json.toString();
			 
		}
		
		long userId = Long.parseLong(result.obj.toString());
		
		PageBean<t_addrate_user> used = rateService.pageOfUserRate(userId, t_addrate_user.RateStatus.USED.code, currPage, pagesize);
		PageBean<t_addrate_user> unused = rateService.pageOfUserRate(userId, t_addrate_user.RateStatus.UNUSED.code, currPage, pagesize);
		PageBean<t_addrate_user> expired = rateService.pageOfUserRate(userId, t_addrate_user.RateStatus.EXPIRED.code, currPage, pagesize);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("used", used);
		map.put("unused", unused);
		map.put("expired", expired);
		map.put("code", 1);
		map.put("msg", "成功");
		
		return JSONObject.fromObject(map).toString();
	}


	/**
	 * 
	 * 展示现金券（使用中，未使用，过期）
	 * 
	 * @return
	 * @throws Exception
	 * 
	 */
	public static String showMyCash(Map<String, String> parameters) throws Exception{
		
		JSONObject json = new JSONObject();
		
		int currPage = Convert.strToInt(parameters.get("currPage"), 1);
		int pagesize = Convert.strToInt(parameters.get("pagesize"), 5);
		String signId = parameters.get("userId");
		
		ResultInfo result = Security.decodeSign(signId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 return json.toString();
			 
		}
		
		long userId = Long.parseLong(result.obj.toString());
		
		PageBean<t_cash_user> used = cashUserService.pageOfUserCash(userId, t_cash_user.CashStatus.USED.code, currPage, pagesize);
		PageBean<t_cash_user> unused = cashUserService.pageOfUserCash(userId, t_cash_user.CashStatus.UNUSED.code, currPage, pagesize);
		PageBean<t_cash_user> expired = cashUserService.pageOfUserCash(userId, t_cash_user.CashStatus.EXPIRED.code, currPage, pagesize);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("used", used);
		map.put("unused", unused);
		map.put("expired", expired);
		map.put("code", 1);
		map.put("msg", "成功");
		
		return JSONObject.fromObject(map).toString();
	}
	
	
	/**
	 * 添加收货地址(准备)
	 * 
	 * @return
	 * @throws Exception
	 * 
	 */
	public static String addAddressReady() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> list = JPAUtil.getList("select * from t_pay_pro_city");
		map.put("code", 1);
		map.put("msg", "成功");
		map.put("cityList", list);
		return JSONObject.fromObject(map).toString();
	}
	
	
	/**
	 * 添加收货地址
	 * 
	 * @return
	 * @throws Exception
	 * 
	 */
	public static String addAddress(Map<String, String> parameters) throws Exception {
		
		JSONObject json = new JSONObject();

		String signId = parameters.get("userId");
		String receiver = parameters.get("receiver");
		String tel = parameters.get("tel");
		String province = parameters.get("province");
		String city = parameters.get("city");
		String address = parameters.get("address");
		int status = Convert.strToInt(parameters.get("status"), 0);//初始为0，作为默认地址为1
		
		ResultInfo result = Security.decodeSign(signId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 return json.toString();
		}
		
		long userId = Long.parseLong(result.obj.toString());
		
		result = addressService.addAddress(userId, receiver, tel, province, city, address);
		if (result.code < 1) {
			 json.put("code", -1);
			 json.put("msg", "添加地址失败");
			 return json.toString();
		}
		
		if(status == 1){
			t_mall_address newAddress = (t_mall_address)result.obj;
			long addressId = newAddress.id;
			result = addressService.setDefaultAddress(userId, addressId);
			if (result.code < 1) {
				 json.put("code", -1);
				 json.put("msg", "默认地址设置失败");
				 return json.toString();
			}
		}
		
		json.put("code", 1);
		json.put("msg", "成功");
		return json.toString();
	}

	
	/**
	 * 
	 * 查看地址列表
	 * 
	 * @param parameters
	 * @return
	 * @throws Exception
	 * 
	 */
	public static String addressList(Map<String, String> parameters) throws Exception {
		
		JSONObject json = new JSONObject();
		
		String signId = parameters.get("userId");
		
		ResultInfo result = Security.decodeSign(signId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 return json.toString();
		}
		
		long userId = Long.parseLong(result.obj.toString());
		List<t_mall_address> addressList = addressService.findAddressByUserId(userId);
		
		json.put("code", 1);
		json.put("msg", "成功");
		json.put("addressList", addressList);
		
		return json.toString();
	}
	
	
	/**
	 * 
	 * 查看积分商品详情
	 * 
	 * @param parameters
	 * @return
	 * @throws Exception
	 * 
	 */
	public static String showMallGoods(Map<String, String> parameters) throws Exception {
		
		JSONObject json = new JSONObject();
		
		String signUserId = parameters.get("userId");
		String signGoodsId = parameters.get("goodsId");
		
		ResultInfo result = Security.decodeSign(signUserId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 return json.toString();
			 
		}

		long userId = Long.parseLong(result.obj.toString());
		t_user user = userService.findByID(userId);
		
		result = Security.decodeSign(signGoodsId, Constants.MALL_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			json.put("code", result.code);
			json.put("msg", result.msg);
			return json.toString();
		}
		
		long goodsId = Long.parseLong(result.obj.toString());
		t_mall_goods goods = goodsService.findByID(goodsId);
		if(goods == null){
			json.put("code", -1);
			json.put("msg", "商品不存在");
			return json.toString();
			
		}
		
		t_mall_address defaultAddress = null;
		List<t_mall_address> addressList = null;
		int scoreBalance = 0;//用户可用积分
		int hasExchangeNum = 0;//个人已兑换的数量
		if(user != null){
			hasExchangeNum = exchangeService.userHasExchange(user.id, goodsId);
			scoreBalance = (int)userFundService.findUserScoreBalance(user.id);
			defaultAddress = addressService.findDefaultAddress(user.id);
			addressList = addressService.findAddressByUserId(user.id);
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", 1);
		map.put("msg", "成功");
		map.put("goods", goods);
		map.put("hasExchangeNum", hasExchangeNum);
		map.put("scoreBalance", scoreBalance);
		map.put("defaultAddress", defaultAddress);
		map.put("addressList", addressList);
		return JSONObject.fromObject(map).toString();
	}
	
	
	/**
	 * 
	 * 查看积分商品兑换
	 * 
	 * @param parameters
	 * @return
	 * @throws Exception
	 * 
	 */
	public static String exchangeGoods(Map<String, String> parameters) throws Exception {
		
		JSONObject json = new JSONObject();
		
		String signUserId = parameters.get("userId");
		String signGoodsId = parameters.get("goodsId");
		String signAddressId = parameters.get("addressId");
		int exchangeNum = Integer.parseInt(parameters.get("exchangeNum"));
		
		ResultInfo result = Security.decodeSign(signGoodsId, Constants.MALL_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			json.put("code", -1);
			json.put("msg", "商品号错误");
			return json.toString();
		}
		
		long goodsId = Long.parseLong(result.obj.toString());
		t_mall_goods goods = goodsService.findByID(goodsId);
		if(goods == null){
			json.put("code", -2);
			json.put("msg", "商品不存在");
			return json.toString();
		}
		
		result = Security.decodeSign(signUserId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			json.put("code", -3);
			json.put("msg", "用户错误");
			return json.toString();
		}
		
		long userId = Long.parseLong(result.obj.toString());
		
		if(signAddressId == null){
			json.put("code", -4);
			json.put("msg", "地址不能为空");
			return json.toString();
		}
		
		long addressId = 0L;
		if(StringUtils.isNotBlank(signAddressId)){
			result = Security.decodeSign(signAddressId, Constants.MALL_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
			if (result.code < 1) {
				json.put("code", -5);
				json.put("msg", "地址错误");
				return json.toString();
			}
			
			addressId = Long.parseLong(result.obj.toString());
		}
		
		result = exchangeService.doExchange(userId, goodsId, addressId, exchangeNum);
		if(result.code < 1){
			json.put("code", result.code);
			json.put("msg", result.msg);
			return json.toString();
		}
		
		json.put("code", 1);
		json.put("msg", "成功");
		return json.toString();
	}
}
