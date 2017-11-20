package controllers.wechat.front;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.shove.Convert;

import common.annotation.LoginCheck;
import common.annotation.PaymentAccountCheck;
import common.annotation.SubmitCheck;
import common.annotation.SubmitOnly;
import common.constants.ConfConst;
import common.constants.Constants;
import common.constants.WxPageType;
import common.enums.Client;
import common.enums.ServiceType;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.OrderNoUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;
import controllers.common.SubmitRepeat;
import controllers.wechat.WechatBaseController;
import controllers.wechat.interceptor.UserStatusWxInterceptor;
import jobs.IndexStatisticsJob;
import models.common.bean.CurrUser;
import models.common.entity.t_information;
import models.common.entity.t_user_info;
import models.core.bean.BidItemOfSupervisorSubject;
import models.core.entity.t_addrate_user;
import models.core.entity.t_agencies;
import models.core.entity.t_bid;
import models.core.entity.t_cash_user;
import models.core.entity.t_invest.InvestType;
import models.core.entity.t_product;
import models.core.entity.t_product.ProductType;
import models.core.entity.t_red_packet_user;
import models.wechat.bean.BidLoanUserBean;
import models.wechat.bean.InvestBidBean;
import models.wechat.bean.InvestRecordBean;
import models.wechat.bean.RepaymentRecordBean;
import payment.impl.PaymentProxy;
import play.mvc.With;
import service.wechat.InvestWechatService;
import services.common.InformationService;
import services.common.UserFundService;
import services.common.UserInfoService;
import services.core.AgencyService;
import services.core.AuditSubjectBidService;
import services.core.BidService;
import services.core.CashUserService;
import services.core.InvestService;
import services.core.ProductService;
import services.core.RateService;
import services.core.RedpacketUserService;


/**
 * 微信-投资管理控制器
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年5月6日
 */
@With({SubmitRepeat.class,UserStatusWxInterceptor.class})
public class InvestCtrl extends WechatBaseController {

	protected static InvestService investService = Factory.getService(InvestService.class);
	
	protected static BidService bidService = Factory.getService(BidService.class);
	
	protected static InvestWechatService investWechatService = Factory.getService(InvestWechatService.class);
	
	protected static AuditSubjectBidService auditSujbectBidService = Factory.getService(AuditSubjectBidService.class);
	
	protected static UserFundService userFundService = Factory.getService(UserFundService.class);
	
	protected static RedpacketUserService redpacketUserService = Factory.getService(RedpacketUserService.class);

	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	
	protected static CashUserService cashUserService = Factory.getService(CashUserService.class);
	
	protected static InformationService informationService = Factory.getService(InformationService.class);
	
	protected static AgencyService agencyService = Factory.getService(AgencyService.class);

	protected static RateService rateService = Factory.getService(RateService.class);

	protected static ProductService productService = Factory.getService(ProductService.class);
	
	
	/***
	 * 理财首页
	 * @author luzhiwei
	 * @createDate 2016-5-5
	 */
	public static void toWechatInvestPre(){
		
		PageBean<InvestBidBean> pageBean = investWechatService.pageOfInvestBids(1, 100);
		Date now = new Date();
		
		render(pageBean,now);
	}
	
	/***
	 * 理财首页-散标投资
	 * @param currPage
	 * @param pageSize
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-5-5
	 */
	public static void showWechatBidsPre(int currPage, int pageSize){
		if (currPage < 1) {
			currPage = 1;
		}

		if (pageSize < 5) {
			pageSize = 5;
		}
		
		PageBean<InvestBidBean> pageBean = investWechatService.pageOfInvestBids(currPage, pageSize);
		
		Date now = new Date();
		
		render(pageBean,now);
	}
	
	/***
	 * 项目详情(投标页面)
	 * @param bidId
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-5-5
	 */
	@SubmitCheck
	public static void investWechatPre(long bidId) {
		t_bid bid = bidService.findByID(bidId);
		if (bid == null) {

			error404();
		}

		t_user_info loanUserInfo = userInfoService.findUserInfoByUserId(bid.user_id);

		CurrUser currUser = getCurrUser();
		if (currUser != null) {
			double balance = userFundService.findUserBalance(currUser.id);
			int countRedPacket = redpacketUserService.countUserRedPacket(currUser.id, t_red_packet_user.RedpacketStatus.UNUSED.code);
			int countCash = cashUserService.countUserCash(currUser.id, t_cash_user.CashStatus.UNUSED.code);
			double totalRedPacket = redpacketUserService.totalUserRedPacket(currUser.id, t_red_packet_user.RedpacketStatus.UNUSED.code);
		
			//TODO 改版
			//会员可用红包列表
			List<t_red_packet_user> redPacketList = redpacketUserService.findCanUseRedPacket(currUser.id, 99999, bid.period);
			//会员可用加息券列表
			List<t_addrate_user> rateList = rateService.findCanUseRate(currUser.id, 99999, bid.period);
			renderArgs.put("redPacketList", redPacketList);
			renderArgs.put("rateList", rateList);
			
			//
			renderArgs.put("balance", balance);
			renderArgs.put("countRedPacket", countRedPacket);
			renderArgs.put("countCash", countCash);
			renderArgs.put("totalRedPacket", totalRedPacket);
			renderArgs.put("rewardRedPacket", Security.addSign(Constants.REWARD_TYPE_RED_PACKET, Constants.REWARD_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES));
			renderArgs.put("rewardCash", Security.addSign(Constants.REWARD_TYPE_CASH, Constants.REWARD_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES));
		}

		List<BidItemOfSupervisorSubject> bidItemOfSupervisorSubjects = auditSujbectBidService.queryBidRefSubjectSupervisor(bidId);
		
		Date nowTime = new Date();
		if (bid.pre_release_time!=null && nowTime.before(bid.pre_release_time)) {//是否预发售状态
			renderArgs.put("preRelease", true);
		}
		renderArgs.put("sysNowTime", nowTime.getTime());//服务器时间传到客户端
		
		t_information investDeal = informationService.findInvestDeal();
		//是否是机构标 
		t_agencies agencies = null;
		if(bid.is_agency){
			agencies = agencyService.findByID(bid.agency_id);
		}
		//TODO 改版 添加标的产品名称
		String productName=IndexStatisticsJob.webProdMap.get(bid.product_id+"");
		
		render(bid,agencies, loanUserInfo, bidItemOfSupervisorSubjects,investDeal,productName);	
	}

	/**
	 * 可以使用的奖励记录
	 * 
	 * @param bidPeriod 红包使用规则:借款期限(月)，0代表无限制 
	 * @param investAmount
	 */
	public static void rewardRecord(double investAmount, String rewardTypeSign,int bidPeriod) {
		CurrUser currUser = getCurrUser();
		
		Map<String, Object> rewardMap = null;
		
		//可使用的奖励类型：1.可使用红包、2.可使用现金券
		ResultInfo result = Security.decodeSign(rewardTypeSign, Constants.REWARD_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		
		if (result.code < 1) {
			result.code = -1;
			result.msg = "参数错误";
			
			renderJSON(result);
		}
		
		int rewardType = Convert.strToInt(result.obj.toString(), 0);
		
		if (rewardType == 0) {
			result.code = -1;
			result.msg = "参数错误";
			
			renderJSON(result);
		}
		
		if(bidPeriod < 0){
			result.code = -1;
			result.msg = "参数错误";
			
			renderJSON(result);
		}
		
		rewardMap =	new HashMap<String, Object>();
		
		if (rewardType == Constants.REWARD_TYPE_RED_PACKET) {
			List<t_red_packet_user> redPacketList = redpacketUserService.findCanUseRedPacket(currUser.id, investAmount,bidPeriod);
			rewardMap.put("code", 1);
			rewardMap.put("rewardList", dealRedPacket(redPacketList));
		} else if (rewardType == Constants.REWARD_TYPE_CASH) {
			List<t_cash_user> cashList = cashUserService.findCanUseCash(currUser.id, investAmount, bidPeriod);
			rewardMap.put("code", 1);
			rewardMap.put("rewardList", dealCash(cashList));
		}
		
		result.code = 1;
		result.msg = "";
		result.obj = rewardMap;
		
		renderJSON(result);
	}
	
	private static List<t_red_packet_user> dealRedPacket(List<t_red_packet_user> redPacketList) {
		List<t_red_packet_user> redPacketListNew = new ArrayList<t_red_packet_user>();
		
		for (t_red_packet_user redPacket : redPacketList) {
			redPacket.sign = redPacket.getSign();
			redPacketListNew.add(redPacket);
		}
		
		return redPacketListNew;
	}
	
	private static List<t_cash_user> dealCash(List<t_cash_user> cashList) {
		List<t_cash_user> cashListNew = new ArrayList<t_cash_user>();
		
		for (t_cash_user cash : cashList) {
			cash.sign = cash.getSign();
			cashListNew.add(cash);
		}
		
		return cashListNew;
	}
	
	/**
	 * 可以使用的加息券记录
	 * 
	 * @param bidPeriod 红包使用规则:借款期限(月)，0代表无限制 
	 * @param investAmount
	 */
	public static void rateRecord(double investAmount,int bidPeriod) {
		CurrUser currUser = getCurrUser();
		ResultInfo result = new ResultInfo();
		Map<String, Object> rewardMap =	new HashMap<String, Object>();
		
		if(bidPeriod < 0){
			result.code = -1;
			result.msg = "参数错误";
			
			renderJSON(result);
		}
		
		List<t_addrate_user> rateList = rateService.findCanUseRate(currUser.id, investAmount,bidPeriod);
		rewardMap.put("code", 1);
		rewardMap.put("rateList", dealRate(rateList));
		
		result.code = 1;
		result.msg = "";
		result.obj = rewardMap;
		
		renderJSON(result);
	}
	
	private static List<t_addrate_user> dealRate(List<t_addrate_user> rateList) {
		List<t_addrate_user> rateListNew = new ArrayList<t_addrate_user>();
		
		if(rateList == null){
			return rateListNew;
		}
		
		for (t_addrate_user rate : rateList) {
			rate.sign = rate.getSign();
			rateListNew.add(rate);
		}
		
		return rateListNew;
	}
	
	
	
	
	/***
	 * 借款详情
	 * @param bidId
	 *
	 * @author luzhiwei
	 * @createDate 2016-5-5
	 */
	public static void bidLoanDetailsPre(long bidId) {
		
		BidLoanUserBean loanUser = investWechatService.findInvestBidsUserInfo(bidId);
		
		/*List<BidItemOfSupervisorSubject> bidItemOfSupervisorSubjects = auditSujbectBidService.queryBidRefSubjectSupervisor(bidId);*/
		
		t_bid bid = bidService.findByID(bidId);
		
		t_product pro = productService.findByID(bid.product_id);
		
		render(loanUser , pro);
	}
		
	/***
	 * 投标记录
	 * @param bidId
	 * @return
	 *
	 * @author luzhiwei
	 * @createDate 2016-5-5
	 */
	public static void investRecordPre(long bidId,String title,String product, int currPage,int pageSize) {
		
		if (currPage < 1) {
			currPage = 1;
		}

		if (pageSize < 5) {
			pageSize = 100;
		}

		PageBean<InvestRecordBean> pageBean = investWechatService.pageOfInvestRecord(currPage,pageSize,bidId);
		
		render(pageBean, bidId,title,product);
	}
		
	/***
	 * 回款计划
	 * @param currPage
	 * @param currPage
	 * @param bidId
	 * @author luzhiwei
	 * @createDate 2016-5-5
	 */
	public static void repaymentRecordPre(long bidId,String title,String product, int currPage, int pageSize){
		if (currPage < 1) {
			currPage = 1;
		}

		if (pageSize < 5) {
			pageSize = 100;
		}
		
		PageBean<RepaymentRecordBean> pageBean = investWechatService.pageOfRepaymentRecord(currPage,pageSize,bidId);
		
		render(pageBean, bidId,title,product);
	}
		
	/**
	 * 投标操作
	 *
	 * @param bidSign 标的签名
	 * @param investAmt 投标金额（按金额购买）
	 *
	 * @author lzw
	 * @createDate 2016-5-5
	 */
	@LoginCheck
	@PaymentAccountCheck
	@SubmitOnly
	public static void invest(String bidSign, double investAmt) {
		ResultInfo result = Security.decodeSign(bidSign, Constants.BID_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			error500();
		}
		
		long bidId = Long.parseLong(result.obj.toString());
		t_bid bid = bidService.findByID(bidId);
		if (bid == null) {
			error500();
		}
		
		if (investAmt % 1 != 0) {
			flash.error("请输入正整数的投标金额");
			investWechatPre(bidId);
		}
		
		//按份数购买时，投资金额=投资份数*每份的金额
		if (t_product.BuyType.PURCHASE_BY_COPY.equals(bid.getBuy_type())) {
			investAmt = new Double(Double.toString(investAmt)).intValue() * bid.min_invest_amount;
		}
		
		t_product tProduct = productService.queryProduct(bid.product_id);
		if (tProduct == null) {
			flash.error("借款标关联的产品数据异常");
			investWechatPre(bidId);
		}
		
		CurrUser currUser = getCurrUser();
		if (currUser == null) {
			flash.error("请先登录");
			investWechatPre(bidId);
		}
		
		long userId = currUser.id;
		
		long investId = investService.queryIsHaveInvestRecord(userId);
		
		/* 用于限制新手单笔投资不得大于100000 */
		if(!currUser.is_old && investId <= 0 && investAmt > 100000) {
			flash.error("用户首次投资限额不得超过10万元");
			investWechatPre(bidId);
		}
		
		//若用户选择投资的是新手标则要进行控制
		if (tProduct.getType().code == ProductType.NEWBIE.code) {
			if (currUser.is_old) {
				flash.error("新手标仅限新注册的用户");
				investWechatPre(bidId);
			}
			
			if (investId > 0L) {
				flash.error("新手标仅限未成功投资过的用户");
				investWechatPre(bidId);
			}
		}
		
		if(tProduct.getType().code == ProductType.OLD_CUSTOMER.code) {
			
			if (investId <= 0L) {
				flash.error("老用户专享仅限已成功投资过的用户");
				investWechatPre(bidId);
			}
		}

		if(tProduct.id == 6) {
			if(investAmt % 10000 != 0) {
				flash.error("预约表投资金额只能为10000的整数倍");
				investWechatPre(bidId);
			}
		}
		
		long redPacketId = 0L; //红包ID
		double redPacketAmt = 0.0; //红包金额
		String redPacketIdStr = params.get("redPacketSign"); //红包ID加密串
		
		if (StringUtils.isNotBlank(redPacketIdStr)) {
			result = Security.decodeSign(redPacketIdStr, Constants.RED_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
			if (result.code >= 1) {
				redPacketId = Long.parseLong(result.obj.toString());
			}
		}
		
		long cashId = 0L; //现金券ID
		double cashAmt = 0.0; //现金券金额
		String cashIdStr = params.get("cashSign"); //现金券ID加密串
		
		if (StringUtils.isNotBlank(cashIdStr)) {
			result = Security.decodeSign(cashIdStr, Constants.CASH_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
			if (result.code >= 1) {
				cashId = Long.parseLong(result.obj.toString());
			}
		}
		
		if (redPacketId > 0L && cashId > 0L) {
			flash.error("红包及现金券不能同时使用");
			investWechatPre(bidId);
		}
		
		if (redPacketId > 0L) {
			result = investService.investRedPacket(userId, redPacketId, investAmt,bid.period,bid.getPeriod_unit().code);
			if (result.code < 1) {
				LoggerUtil.info(true, result.msg);
				flash.error(result.msg);
				investWechatPre(bidId);
			}
			
			t_red_packet_user redPacketUser = (t_red_packet_user) result.obj;
			redPacketAmt =  redPacketUser.amount;
		}
		
		if (cashId > 0L) {
			//投标使用现金券校验
			result = investService.investCash(userId, cashId, investAmt);
			
			if (result.code < 1) {
				LoggerUtil.info(true, result.msg);
				flash.error(result.msg);
				investWechatPre(bidId);
			}
			
			t_cash_user cashUser = (t_cash_user) result.obj;
			cashAmt = cashUser.amount;
		}
		
		long rateId = 0L; //加息券ID
		double addRate = 0.0; //加息券利率
		String rateIdStr = params.get("rateSign"); //加息券ID加密串
		
		if (StringUtils.isNotBlank(rateIdStr)) {
			result = Security.decodeSign(rateIdStr, Constants.RATE_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
			if (result.code >= 1) {
				rateId = Long.parseLong(result.obj.toString());
			}
		}
		
		if (rateId > 0L) {
			//投标使用加息券校验
			result = investService.investRate(userId, rateId,investAmt,bid.period,bid.getPeriod_unit().code);
			
			if (result.code < 1) {
				LoggerUtil.info(true, result.msg);
				flash.error(result.msg);
				investWechatPre(bidId);
			}
			
			t_addrate_user rateUser = (t_addrate_user) result.obj;
			addRate = rateUser.rate;
		}
		
		String investPassword = params.get("investPassword"); //投资密码
		
		//投标准备
		result = investService.invest(userId, bid, investAmt, cashAmt,investPassword);
		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
			flash.error(result.msg);
			investWechatPre(bidId);
		}
		
		int rows = 0;
		
		//仅使用红包时才修改红包状态
		if (redPacketId > 0L && cashId <= 0L) {
			rows = redpacketUserService.updateRedPacketLockTime(redPacketId, t_red_packet_user.RedpacketStatus.UNUSED.code, t_red_packet_user.RedpacketStatus.USING.code);
			if (rows <= 0) {
				LoggerUtil.info(true, "修改红包状态失败");
				flash.error("修改红包状态失败");
				investWechatPre(bidId);
			}
		} else if (redPacketId <= 0L && cashId > 0L) { //仅使用现金券时才修改现金券状态
			rows = cashUserService.updateUserCashLockTime(cashId, t_cash_user.CashStatus.UNUSED.code, t_cash_user.CashStatus.USING.code);
			if (rows <= 0) {
				LoggerUtil.info(true, "修改现金券状态失败");
				flash.error("修改现金券状态失败");
				investWechatPre(bidId);
			}
		}
		
		if (rateId > 0L){//仅使用加息券时才修改加息券状态
			
			rows = rateService.updateUserRateLockTime(rateId, t_addrate_user.RateStatus.UNUSED.code, t_addrate_user.RateStatus.USING.code);
			if (rows <= 0) {
				LoggerUtil.info(true, "修改加息券状态失败");
				flash.error("修改加息券状态失败");
				investWechatPre(bidId);
			}
		}
		
		//业务流水号
		String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.INVEST);
		
		if (ConfConst.IS_TRUST) { 
			result = PaymentProxy.getInstance().invest(Client.WECHAT.code, InvestType.WECHAT.code , serviceOrderNo, userId, bid, investAmt, 
					redPacketAmt, redPacketId, cashAmt, cashId,rateId,addRate);
			if (result.code < 1) {
				LoggerUtil.info(true, result.msg);
				flash.error(result.msg);
				investWechatPre(bidId);
			}
		}

		if (!ConfConst.IS_TRUST) {
			result = investService.doInvest(userId, bidId, investAmt, serviceOrderNo, "", Client.WECHAT.code, InvestType.WECHAT.code, 
					redPacketId, redPacketAmt, cashId, cashAmt,rateId,addRate);
			if (result.code < 1) {
				LoggerUtil.info(true, result.msg);
				flash.error(result.msg);
				investWechatPre(bidId);
			}
		}
		
		toResultPage(WxPageType.PAGE_COMMUNAL_SUCC, "投标成功");
	}
	
	/**
	 * 查看借款标的合同
	 *
	 * @param bidSign 标的ID加密串
	 *
	 * @author Songjia
	 * @createDate 2016年5月11日
	 */
	public static void showBidPactPre(){
		
		t_information pact = informationService.findInvestDeal();
		
		render(pact);
	}
}
