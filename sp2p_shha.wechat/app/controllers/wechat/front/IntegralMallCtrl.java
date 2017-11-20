package controllers.wechat.front;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import common.annotation.LoginCheck;
import common.annotation.SimulatedCheck;
import common.annotation.SubmitCheck;
import common.annotation.SubmitOnly;
import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Factory;
import common.utils.JPAUtil;
import common.utils.LoggerUtil;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.StrUtil;
import controllers.common.SubmitRepeat;
import controllers.wechat.WechatBaseController;
import controllers.wechat.interceptor.UserStatusWxInterceptor;
import models.common.bean.CurrUser;
import models.common.entity.t_pay_pro_city;
import models.ext.mall.entiey.t_mall_address;
import models.ext.mall.entiey.t_mall_goods;
import models.ext.mall.entiey.t_mall_goods.GoodsType;
import play.mvc.With;
import service.ext.mall.AddressService;
import service.ext.mall.ExchangeService;
import service.ext.mall.GoodsService;
import services.common.UserFundService;
import services.common.UserService;
import services.core.InvestService;

/**
 * 积分商城
 *
 * @description 绑定收货地址、积分兑换、奖品展示、红包、现金券、加息券
 *
 *
 */
@With({ SubmitRepeat.class, UserStatusWxInterceptor.class })
public class IntegralMallCtrl extends WechatBaseController {

	protected static AddressService adressService = Factory.getService(AddressService.class);

	protected static ExchangeService exchangeService = Factory.getService(ExchangeService.class);

	protected static GoodsService goodsService = Factory.getService(GoodsService.class);

	protected static UserService userService = Factory.getService(UserService.class);

	protected static AddressService addressService = Factory.getService(AddressService.class);

	protected static UserFundService userFundService = Factory.getService(UserFundService.class);
	
	protected static InvestService investService = Factory.getService(InvestService.class);
	
	/**
	 * 展示积分商城
	 * 
	 * 
	 * @return
	 * @throws Exception
	 * 
	 */
	public static void showMallPre() throws Exception {
		
		// 实体物品
		List<t_mall_goods> virtualGoods = goodsService.findSameGoods(GoodsType.VIRTUAL.code);
		
		// 实体物品
		List<t_mall_goods> entityGoods = goodsService.findSameGoods(GoodsType.ENTITY.code);

		renderArgs.put("virtualGoods", virtualGoods);
		renderArgs.put("entityGoods", entityGoods);

		Date sysNowTime = new Date();

		render(sysNowTime);
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
	@SubmitCheck
	public static void showMallGoodsPre(String signGoodsId) throws Exception {

		ResultInfo result = Security.decodeSign(signGoodsId, Constants.MALL_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			flash.error(result.msg);
			showMallPre();
		}
		long goodsId = Long.parseLong((String) result.obj);

		t_mall_goods goods = goodsService.findByID(goodsId);

		if (goods == null) {

			flash.error("该商品不存在");
			showMallPre();
		}

		CurrUser user = getCurrUser();

		t_mall_address defaultAddress = null;
		int scoreBalance = 0;
		int hasExchangeNum = 0;// 个人已兑换的数量
		int count = 0;
		if (user != null) {
			hasExchangeNum = exchangeService.userHasExchange(user.id, goodsId);
			scoreBalance = (int) userFundService.findUserScoreBalance(user.id);
			defaultAddress = addressService.findDefaultAddress(user.id);
			count = investService.countInvestOfUser(user.id);
		}

		render(goods, defaultAddress, scoreBalance, hasExchangeNum, count);
	}

	/**
	 * 
	 * 查看商品详情页
	 * 
	 * @param parameters
	 * @return
	 * @throws Exception
	 * 
	 */
	public static void goodsDescriptionPre(String signGoodsId) throws Exception {

		ResultInfo result = Security.decodeSign(signGoodsId, Constants.MALL_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {

			flash.error(result.msg);
			showMallPre();
		}
		long goodsId = Long.parseLong((String) result.obj);

		t_mall_goods goods = goodsService.findByID(goodsId);

		if (goods == null) {

			flash.error("该商品不存在");
			showMallPre();
		}

		render(goods);
	}

	/**
	 * 
	 * 积分商品兑换
	 * 
	 * @param parameters
	 * @return
	 * @throws Exception
	 * 
	 */
	@SubmitOnly
	@LoginCheck
	@SimulatedCheck
	public static void exchangeGoods(String goodsSign, int exchangeNum) throws Exception {

		ResultInfo result = Security.decodeSign(goodsSign, Constants.MALL_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			error500();
		}

		long goodsId = Long.parseLong(result.obj.toString());

		t_mall_goods goods = goodsService.findByID(goodsId);

		if (goods == null) {

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

		result = exchangeService.doExchange(userId, goodsId, addressId,	exchangeNum);

		if (result.code < 1) {
			LoggerUtil.info(true, "兑换商品时：%s", result.msg);
			flash.error(result.msg);
			showMallGoodsPre(goods.sign);
		}

		flash.success("兑换成功");
		showMallGoodsPre(goods.sign);
	}

	/**
	 * 添加收货地址(准备)
	 * 
	 * @return
	 * @throws Exception
	 * 
	 */
	@LoginCheck
	@SimulatedCheck
	public static void addAddressReadyPre(String goodsSign) throws Exception {
		List<Map<String, Object>> list = JPAUtil.getList("select * from t_pay_pro_city group by prov_num");
		render(list,goodsSign);
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
	@SimulatedCheck
	public static void addressListPre() {
		CurrUser currUser = getCurrUser();
		if(currUser == null){
			return;
		}
		
		long userId = getCurrUser().id;

		List<t_mall_address> addressList = addressService.findAddressByUserId(userId);

		render(addressList);

	}

	/**
	 * 通过省编号查找属于该省的所有市
	 * @param prov_num
	 */
	public static void findByCity(String prov_num) {
		List<Map<String, Object>> list = t_pay_pro_city.find("prov_num = ? ", prov_num).fetch();
		renderJSON(list);
	}
	
	/**
	 * 添加收货地址
	 * 
	 * @return
	 * @throws Exception
	 * 
	 */
	@LoginCheck
	@SimulatedCheck
	public static void addAddress(String receiver, String tel, String province, String city, String address, int status) throws Exception {
		ResultInfo result = new ResultInfo();
		
		String goodsSign = params.get("goodsSign");
		
		if (StringUtils.isBlank(receiver)) {

			flash.error("请填写收件人");
			addAddressReadyPre(goodsSign);
		}

		if (StringUtils.isBlank(tel)) {

			flash.error("请填写联系电话");
			addAddressReadyPre(goodsSign);
		}

		/* 验证手机号是否符合规范 */
		if (!StrUtil.isMobileNum(tel)) {

			flash.error("手机号不符合规范");
			addAddressReadyPre(goodsSign);
		}

		if (StringUtils.isBlank(province)) {

			flash.error("请选择省市");
			addAddressReadyPre(goodsSign);
		}

		if (StringUtils.isBlank(city)) {

			flash.error("请选择省市");
			addAddressReadyPre(goodsSign);
		}

		if (StringUtils.isBlank(address)) {

			flash.error("请填写详细地址");
			addAddressReadyPre(goodsSign);
		}

		long userId = getCurrUser().id;

		result = addressService.addAddress(userId, receiver, tel, province,
				city, address);

		if (result.code < 1) {
			flash.error("添加地址失败");
			addAddressReadyPre(goodsSign);
		}

		t_mall_address newAddress = (t_mall_address) result.obj;
		
		if(status == 1){
			long addressId = newAddress.id;
			result = addressService.setDefaultAddress(userId, addressId);
			if (result.code < 1) {
				flash.error("默认地址设置失败");
				addAddressReadyPre(goodsSign);
			}
		}
		
		showMallGoodsPre(goodsSign);
	}
	
	/**
	 * 前台-积分商城首页-商品详情-选择地址
	 *
	 * @param sign id
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
		map.put("newAddress", newAddress);
		map.put("detailAddress", newAddress.getDetailAddress());
		map.put("code", 1);
		map.put("msg", "查询地址成功");

		renderJSON(map);
	}
}
