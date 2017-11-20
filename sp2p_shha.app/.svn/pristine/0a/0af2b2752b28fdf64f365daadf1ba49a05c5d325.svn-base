package controllers.app.Invest;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.shove.Convert;

import common.constants.ConfConst;
import common.constants.Constants;
import common.constants.PaymentConst;
import common.enums.Client;
import common.enums.DeviceType;
import common.enums.ServiceType;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.OrderNoUtil;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.StrUtil;
import common.utils.UserValidataUtil;
import models.common.entity.t_user;
import models.core.entity.t_addrate_user;
import models.core.entity.t_bid;
import models.core.entity.t_cash_user;
import models.core.entity.t_invest.InvestType;
import models.core.entity.t_product;
import models.core.entity.t_product.ProductType;
import models.core.entity.t_red_packet_user;
import net.sf.json.JSONObject;
import payment.impl.PaymentProxy;
import service.InvestAppService;
import services.common.UserFundService;
import services.common.UserService;
import services.core.BidService;
import services.core.CashUserService;
import services.core.InvestService;
import services.core.ProductService;
import services.core.RateService;
import services.core.RedpacketUserService;

/**
 * 散标投资
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年6月30日
 */
public class InvestAction {
	private static InvestAppService investAppService = Factory.getService(InvestAppService.class);
	private static UserService userService = Factory.getService(UserService.class);
	private static BidService bidService = Factory.getService(BidService.class);
	private static ProductService productService = Factory.getService(ProductService.class);
	private static InvestService investService = Factory.getService(InvestService.class);
	private static RedpacketUserService redpacketUserService = Factory.getService(RedpacketUserService.class);
	private static CashUserService cashUserService = Factory.getService(CashUserService.class);
	private static RateService rateService = Factory.getService(RateService.class);
	protected static UserFundService userFundService = Factory.getService(UserFundService.class);

	/***
	 * 理财产品列表接口（OPT=311）
	 *
	 * @param parameters
	 * @return
	 * @throws Exception
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-7
	 */
	public static String pageOfInvestBids(Map<String, String> parameters) throws Exception{
		JSONObject json = new JSONObject();
	
		String currentPageStr = parameters.get("currPage");
		String pageNumStr = parameters.get("pageSize");

		if(!StrUtil.isNumeric(currentPageStr)||!StrUtil.isNumeric(pageNumStr)){
			 json.put("code",-1);
			 json.put("msg", "分页参数不正确");
			 
			 return json.toString();
		}
		
		int currPage = Convert.strToInt(currentPageStr, 1);
		int pageSize = Convert.strToInt(pageNumStr, 10);
		
		if (currPage < 1) {
			currPage = 1;
		}
		if (pageSize < 1) {
			pageSize = 10;
		}
		
		return investAppService.pageOfInvestBids(currPage, pageSize);
	}
	
	/***
	 * 借款标详情（opt=312）
	 *
	 * @param parameters
	 * @return
	 * @throws Exception
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-13
	 */
	public static String investBidInformation(Map<String, String> parameters) throws Exception{
		JSONObject json = new JSONObject();
		
		String userIdSign = parameters.get("userId");
		String bidIdSign = parameters.get("bidIdSign");
		long userId = -1;
		
		//登录才验证userId
		if(!"-1".equals(userIdSign)){
			ResultInfo userIdSignDecode = Security.decodeSign(userIdSign, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
			if (userIdSignDecode.code < 1) {
				json.put("code", ResultInfo.LOGIN_TIME_OUT);
				json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
				
				return json.toString();
			}
			
			userId = Long.parseLong(userIdSignDecode.obj.toString());
		}
		
		ResultInfo result = Security.decodeSign(bidIdSign, Constants.BID_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code",-1);
			 json.put("msg", result.msg);
			 return json.toString();
		}
    	
		
		long bidId = Long.parseLong(result.obj.toString());
		
		return investAppService.findInvestBidInformation(userId, bidId);
	}
	
	/***
	 * 借款人详情接口（OPT=322）
	 * @param parameters
	 * @return
	 * @throws Exception
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-7
	 */
	public static String investBidDeatils(Map<String, String> parameters) throws Exception{
		JSONObject json = new JSONObject();
		
		String bidIdSign = parameters.get("bidIdSign");
		
		ResultInfo result = Security.decodeSign(bidIdSign, Constants.BID_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code",-1);
			 json.put("msg", result.msg);
			 return json.toString();
		}
    	
		long bidId = Long.parseLong(result.obj.toString());
		
		return investAppService.findInvestBidDeatils(bidId);
	}
		
	/***
	 * 回款计划接口（OPT=323）
	 * @param parameters
	 * @return
	 * @throws Exception
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-7
	 */
	public static String listOfRepaymentBill(Map<String, String> parameters) throws Exception{
		JSONObject json = new JSONObject();
		
		String bidIdSign = parameters.get("bidIdSign");
		
		ResultInfo result = Security.decodeSign(bidIdSign, Constants.BID_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code",-1);
			 json.put("msg", result.msg);
			 return json.toString();
		}
    	
		long bidId = Long.parseLong(result.obj.toString());
		
		return investAppService.listOfRepaymentBill(bidId);
	}
	
	/***
	 * 投标记录接口（OPT=324）
	 * @param parameters
	 * @return
	 * @throws Exception
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-7
	 */
	public static String investBidsRecord(Map<String, String> parameters) throws Exception{
		
		JSONObject json = new JSONObject();
		
		String currentPageStr = parameters.get("currPage");
		String pageNumStr = parameters.get("pageSize");
		String bidIdSign = parameters.get("bidIdSign");
		
		ResultInfo result = Security.decodeSign(bidIdSign, Constants.BID_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code",-1);
			 json.put("msg", result.msg);
			 return json.toString();
		}
    	
		long bidId = Long.parseLong(result.obj.toString());
		
		if(!StrUtil.isNumeric(currentPageStr)||!StrUtil.isNumeric(pageNumStr)){
			 json.put("code",-1);
			 json.put("msg", "分页参数不正确");
			 
			 return json.toString();
		}
		
		int currPage = Convert.strToInt(currentPageStr, 1);
		int pageSize = Convert.strToInt(pageNumStr, 10);
		
		if (currPage < 1) {
			currPage = 1;
		}
		if (pageSize < 1) {
			pageSize = 10;
		}
			
		return investAppService.pageOfInvestBidsRecord(currPage, pageSize,bidId);
	}
	
	/**
	 * 投标
	 *
	 * @param parameters
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年4月7日
	 */
	public static String invest(Map<String, String> parameters) {
		JSONObject json = new JSONObject();
		ResultInfo result = new ResultInfo();
		
		String userIdSign = parameters.get("userId");
		String investAmtStr = parameters.get("investAmt");//投标金额或者投标份数
		String bidIdSign = parameters.get("bidIdSign");
		String deviceType = parameters.get("deviceType");//设备类型
		String redPacketStr = parameters.get("bribeId");//红包ID
		String cashIdStr = parameters.get("cashId");//现金券ID
		String rateIdStr = parameters.get("rateId");//加息券ID
		String investPassword = parameters.get("investPassword"); //投资密码
		
		//用户验证
		ResultInfo userIdSignDecode = Security.decodeSign(userIdSign, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (userIdSignDecode.code < 1) {
			json.put("code", ResultInfo.LOGIN_TIME_OUT);
			json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			
			return json.toString();
		}
		
		long userId = Long.parseLong(userIdSignDecode.obj.toString());
		t_user user = userService.findByID(userId);
		if (user == null) {
			json.put("code", -1);
			json.put("msg", "请先登录");
			
			return json.toString();
		}
		
		//富有必须绑定邮箱
		if (ConfConst.CURRENT_TRUST_TYPE.equals(PaymentConst.TRUST_TYPE_FY)) {
			if (!UserValidataUtil.checkBindEmail(userId)) {
				 json.put("code", ResultInfo.NOT_BIND_EMAIL);
				 json.put("msg", "请绑定邮箱");
				 
				 return json.toString();
			}
		}
		
		if (!UserValidataUtil.checkRealName(userId)) {
			 json.put("code",ResultInfo.NOT_REAL_NAME);
			 json.put("msg", "请实名认证");
			 return json.toString();
		}
		
		if (!UserValidataUtil.checkActivited(userId)) {
			 json.put("code",ResultInfo.NO_ACTIVITED);
			 json.put("msg", "请激活");
			 return json.toString();
		}
		
		//标例验证
		ResultInfo bidIdSignDecode = Security.decodeSign(bidIdSign, Constants.BID_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (bidIdSignDecode.code < 1) {
			json.put("code", -1);
			json.put("msg", bidIdSignDecode.msg);
			
			return json.toString();
		}
		
		long bidId = Long.parseLong(bidIdSignDecode.obj.toString());
		t_bid bid = bidService.findByID(bidId);
		
		if (DeviceType.getEnum(Convert.strToInt(deviceType, -99))==null) {
			json.put("code", -1);
			json.put("msg", "请使用移动客户端操作");
			
			return json.toString();
		}
		
		//投标金额校验
		double investAmt = Convert.strToDouble(investAmtStr, 0);
		if (investAmt % 1 != 0) {
			json.put("code", -1);
			json.put("msg", "请输入正确的投标金额!");
			return json.toString();
		}
		
		//按份数购买时，投资金额=投资份数*每份的金额
		if (t_product.BuyType.PURCHASE_BY_COPY.equals(bid.getBuy_type())) {
			investAmt = new Double(Double.toString(investAmt)).intValue() * bid.min_invest_amount;
		}
		
		if (!UserValidataUtil.checkPaymentAccount(userId)) {
			 json.put("code",ResultInfo.NOT_PAYMENT_ACCOUNT);
			 json.put("msg", "请先开通资金存管");
			 return json.toString();
		}
		
		t_product tProduct = productService.queryProduct(bid.product_id);
		if (tProduct == null) {
			json.put("code", -1);
			json.put("msg", "借款标关联的产品数据异常");
			return json.toString();
		}
		
		long investId = investService.queryIsHaveInvestRecord(userId);
		
		/* 用于限制新手单笔投资不得大于100000 */
		if(!user.is_old && investId <= 0 && investAmt > 100000) {
			json.put("code", -1);
			json.put("msg", "用户首次投资限额不得超过10万元");
			return json.toString();
		}
		
		//若用户选择投资的是新手标则要进行控制
		if (tProduct.getType().code == ProductType.NEWBIE.code) {
			if (user.is_old) {
				json.put("code", -1);
				json.put("msg", "新手标仅限新注册的用户");
				return json.toString();
			}
			
			if (investId > 0L) {
				json.put("code", -1);
				json.put("msg", "新手标仅限未成功投资过的用户");
				return json.toString();
			}
		}
		
		if(tProduct.getType().code == ProductType.OLD_CUSTOMER.code) {
			
			if (investId <= 0L) {
				json.put("code", -1);
				json.put("msg", "老用户专享仅限已成功投资过的用户");
				return json.toString();
			}
		}

		if(tProduct.id == 6) {
			if(investAmt % 10000 != 0) {
				json.put("code", -1);
				json.put("msg", "预约标投资金额只能为10000的整数倍");
				return json.toString();
			}
		}
		
		//红包校验区
		long redPacketId = 0L;
		double redPacketAmt = 0.0; //红包金额
		if (StringUtils.isNotBlank(redPacketStr)) {
			result = Security.decodeSign(redPacketStr, Constants.RED_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
			if (result.code >= 1) {
				redPacketId = Long.parseLong(result.obj.toString());
			}
		}
		
		if (redPacketId > 0L) {
			result = investService.investRedPacket(userId, redPacketId, investAmt,bid.period,bid.getPeriod_unit().code);
			if (result.code < 1) {
				json.put("code",-2);
				json.put("msg", "红包校验失败");
				return json.toString();
			}
			
			t_red_packet_user redPacketUser = (t_red_packet_user) result.obj;
			redPacketAmt =  redPacketUser.amount;
		}
		//红包END
		
		//现金券校验区
		long cashId = 0L; //现金券ID
		double cashAmt = 0.0; //现金券金额
		if (StringUtils.isNotBlank(cashIdStr)) {
			result = Security.decodeSign(cashIdStr, Constants.CASH_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
			if (result.code >= 1) {
				cashId = Long.parseLong(result.obj.toString());
			}
		}
		
		if (cashId > 0L) {
			//投标使用现金券校验
			result = investService.investCash(userId, cashId, investAmt);
			
			if (result.code < 1) {
				json.put("code",-2);
				json.put("msg", "现金券校验失败");
				return json.toString();
			}
			
			t_cash_user cashUser = (t_cash_user) result.obj;
			cashAmt = cashUser.amount;
		}
		//现金券END
		
		if (redPacketId > 0L && cashId > 0L) {
			json.put("code",-2);
			json.put("msg", "红包与现金券不能共用");
			return json.toString();
		}
		
		
		int rows = 0;
		//加息券校验区
		long rateId = 0L; //加息券ID
		double addRate = 0.0; //加息券利率
		
		if (StringUtils.isNotBlank(rateIdStr)) {
			result = Security.decodeSign(rateIdStr, Constants.RATE_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
			if (result.code >= 1) {
				rateId = Long.parseLong(result.obj.toString());
			}
		}

		if (rateId > 0L) {
			//投标使用加息券校验
			result = investService.investRate(userId, rateId,investAmt,bid.period,bid.getPeriod_unit().code);
			
			if (result.code < 1) {
				json.put("code",-2);
				json.put("msg", "加息券校验失败");
				return json.toString();
			}
			
			t_addrate_user rateUser = (t_addrate_user) result.obj;
			addRate = rateUser.rate;
			
			rows = rateService.updateUserRateLockTime(rateId, t_addrate_user.RateStatus.UNUSED.code, t_addrate_user.RateStatus.USING.code);
			if (rows <= 0) {
				json.put("code",-2);
				json.put("msg", "修改加息券状态失败");
				return json.toString();
			}
		}
		//加息券END
		
		//投标准备
		result = investService.invest(userId, bid, investAmt, cashAmt,investPassword);
		if (result.code < 1) {
			json.put("code",result.code);
			json.put("msg", result.msg);
			return json.toString();
		}
		
		//仅使用红包时才修改红包状态
		if (redPacketId > 0L && cashId <= 0L) {
			rows = redpacketUserService.updateRedPacketLockTime(redPacketId, t_red_packet_user.RedpacketStatus.UNUSED.code, t_red_packet_user.RedpacketStatus.USING.code);
			if (rows <= 0) {
				json.put("code",-3);
				json.put("msg", "红包投标异常");
				return json.toString();
			}
		} else if (redPacketId <= 0L && cashId > 0L) { //仅使用现金券时才修改现金券状态
			rows = cashUserService.updateUserCashLockTime(cashId, t_cash_user.CashStatus.UNUSED.code, t_cash_user.CashStatus.USING.code);
			if (rows <= 0) {
				json.put("code",-3);
				json.put("msg", "现金券投标异常");
				return json.toString();
			}
		}
		
		//业务流水号
		Client portCode = Client.getEnum(Integer.parseInt(deviceType));
		InvestType investType = portCode.code == Client.ANDROID.code ? InvestType.ANDROID : InvestType.IOS;
		
		String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.INVEST);
		if (ConfConst.IS_TRUST) { 
			result = PaymentProxy.getInstance().invest(portCode.code, investType.code, serviceOrderNo, userId, bid, investAmt, 
					redPacketAmt, redPacketId, cashAmt, cashId,rateId,addRate);
			if (result.code < 1) {
				LoggerUtil.info(true, result.msg);
				json.put("code",-4);
				json.put("msg", "存管投标失败");
				return json.toString();
			}
			
			json.put("html", result.obj.toString());
		}

		if (!ConfConst.IS_TRUST) {
			result = investService.doInvest(userId, bidId, investAmt, serviceOrderNo, "", portCode.code, investType.code, 
					redPacketId, redPacketAmt, cashId, cashAmt,rateId,addRate);
			if (result.code < 1) {
				LoggerUtil.info(true, result.msg);
				json.put("code",-4);
				json.put("msg", "非存管投标失败");
				return json.toString();
			}
		}

		json.put("code", 1);
		json.put("msg", "成功");
		return json.toString();
	}
	
}
