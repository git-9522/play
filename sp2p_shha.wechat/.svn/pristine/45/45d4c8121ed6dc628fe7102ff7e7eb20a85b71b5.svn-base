package controllers.wechat.front.account;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

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
import common.utils.number.Arith;
import controllers.common.SubmitRepeat;
import controllers.wechat.WechatBaseController;
import controllers.wechat.interceptor.AccountWxInterceptor;
import models.common.entity.t_pact;
import models.core.bean.DebtInvest;
import models.core.entity.t_bill;
import models.wechat.bean.InvestBillBean;
import models.wechat.bean.InvestBillDetailsBean;
import models.wechat.bean.LoanBillBean;
import models.wechat.bean.LoanBillDetailsBean;
import models.wechat.bean.UserDebtBasic;
import models.wechat.bean.UserDebtDetail;
import models.wechat.bean.UserInvestBasic;
import models.wechat.bean.UserInvestDetail;
import models.wechat.bean.UserLoanBasic;
import models.wechat.bean.UserLoanDetail;
import payment.impl.PaymentProxy;
import play.mvc.With;
import service.wechat.BidWechatService;
import service.wechat.BillWechatInvestService;
import service.wechat.BillWechatService;
import service.wechat.DebtWechatService;
import service.wechat.InvestWechatService;
import services.common.InformationService;
import services.common.PactService;

/**
 * 微信-账户中心-资产管理
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年5月4日
 */
@With({AccountWxInterceptor.class,SubmitRepeat.class})
public class MyFundCtrl extends WechatBaseController {
	
	protected static InvestWechatService investWechatService = Factory.getService(InvestWechatService.class);
	
	protected static BidWechatService bidWechatService = Factory.getService(BidWechatService.class);
	
	protected static BillWechatInvestService billWechatInvestService = Factory.getService(BillWechatInvestService.class);
	
	protected static PactService pactService = Factory.getService(PactService.class);
	
	protected static BillWechatService billWechatService = Factory.getService(BillWechatService.class);
	
	protected static DebtWechatService debtWechatService = Factory.getService(DebtWechatService.class);

	protected static InformationService informationService = Factory.getService(InformationService.class);
	
	/**
	 * 跳转资产管理页面
	 *
	 *
	 * @author Songjia
	 * @createDate 2016年5月11日
	 */
	public static void accountManagePre(){
		render();
	}
	
	
	/**
	 * 账户中心-我的理财
	 *
	 * @param currPage
	 *
	 * @author Songjia
	 * @createDate 2016年5月11日
	 */
	public static void accountInvestPre(int currPage){
		if (currPage < 1) {
			currPage = 1;
		}

		PageBean<UserInvestBasic> pageBean = investWechatService.pageOfUserInvest(currPage, 4, getCurrUser().id);
		
		render(pageBean);
	}

	/**
	 * 账户中心-我的理财-理财详情
	 *
	 * @param investId
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年6月12日
	 */
	public static void accountInvestDetailPre(long investId){
		UserInvestDetail userInvestDetail = investWechatService.findUserInvestDetail(investId);
		
		if (userInvestDetail == null) {
			toResultPage(WxPageType.PAGE_FAIL, "非常遗憾，服务器暂时无法响应");
		}
		
		ResultInfo result = debtWechatService.isInvestCanbeTransfered(investId);
		boolean canbeTransferd = false;
		if (result.code == 1) {
			canbeTransferd = true;
		}
		
		render(userInvestDetail,canbeTransferd);
	}
	
	/**
	 * 用户理财账单列表下拉
	 *
	 * @param investId 投资记录ID
	 *
	 * @author Songjia
	 * @createDate 2016年5月11日
	 */
	public static void pullDownMyBillInvestPre(long investId){
		List<InvestBillBean> billInvestList = billWechatInvestService.queryAccountInvestBillWx(investId);
		
		render(billInvestList);
	}
	
	/**
	 * 理财账单详情
	 *
	 * @param investBillId  理财账单ID
	 *
	 * @author Songjia
	 * @createDate 2016年5月11日
	 */
	public static void investBillDetailsPre(long investBillId) {
		InvestBillDetailsBean investBillDetails = billWechatInvestService.findInvestBillDetails(investBillId);
		
		if (investBillDetails == null) {
			toResultPage(WxPageType.PAGE_FAIL, "非常遗憾，服务器暂时无法响应");
		}
		render(investBillDetails);
	}
	
	
	/**
	 * 账户中心-我的借款
	 *
	 * @param currPage
	 * @param pageSize
	 *
	 * @author Songjia
	 * @createDate 2016年5月11日
	 */
	public static void accountLoanPre(int currPage){
		if (currPage < 1) {
			currPage = 1;
		}

		PageBean<UserLoanBasic> pageBean = bidWechatService.pageOfMyLoan(currPage, 5, getCurrUser().id);
		
		render(pageBean);
	}
	
	/**
	 * 借款标详情详细信息
	 *
	 * @param bidId
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年6月13日
	 */
	public static void accountLoanDetailPre(long bidId){
		UserLoanDetail userLoanDetail = bidWechatService.findUserLoanDetail(bidId);
		
		if (userLoanDetail == null) {
			toResultPage(WxPageType.PAGE_FAIL, "非常遗憾，服务器暂时无法响应");
		}
		
		render(userLoanDetail);
	}
	
	/**
	 * 用户借款账单下拉
	 *
	 * @param bidId 借款标的ID
	 *
	 * @author Songjia
	 * @createDate 2016年5月11日
	 */
	public static void pullDownMyBillPre(long bidId){
		List<LoanBillBean> loanBillList = billWechatService.queryAccountLoanBillWx(bidId);
		int totalPeriod = billWechatService.findBidTotalBillCount(bidId);//账单总期数
		
		render(loanBillList, totalPeriod);
	}
	
	
	/**
	 * 借款账单详情
	 *
	 * @param billId 借款账单ID
	 * @param totalPeriod 账单总期数
	 * 
	 * @author Songjia
	 * @createDate 2016年5月11日
	 */
	public static void loanBillDetailsPre(long billId, int totalPeriod) {
		 LoanBillDetailsBean loanBillDetails = billWechatService.findLoanBillDetails(billId);
		 
		 render(loanBillDetails, totalPeriod);
	}
	
	/**
	 * 查看借款标的合同
	 *
	 * @param bidSign 标的ID加密串
	 *
	 * @author Songjia
	 * @createDate 2016年5月11日
	 */
	public static void showBidPactPre(String bidSign){
		ResultInfo result = Security.decodeSign(bidSign, Constants.BID_ID_SIGN, Constants.VALID_TIME,ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			toResultPage(WxPageType.PAGE_FAIL, result.msg);
		}
		long bidId = Long.parseLong((String)result.obj);
	
		t_pact pact = pactService.findByBidid(bidId);
		if (pact == null) {
			toResultPage(WxPageType.PAGE_FAIL, "非常遗憾，服务器暂时无法响应");
		}
		render(pact);
	}
	
	/**
	 * 微信还款
	 *
	 * @param billSign 账单ID解密串
	 *
	 * @author Songjia
	 * @createDate 2016年5月11日
	 */
	@SuppressWarnings("unchecked")
	public static void repayment(String billSign) {
		ResultInfo result = Security.decodeSign(billSign, Constants.BILL_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			LoggerUtil.info(false, "签名校验失败");
			toResultPage(WxPageType.PAGE_COMMUNAL_FAIL, "签名校验失败");
		}

		long billId = Long.parseLong(result.obj.toString());
		
		t_bill bill = billWechatService.findByID(billId);
		if (bill == null) {
			toResultPage(WxPageType.PAGE_COMMUNAL_FAIL, "非常遗憾，您访问的页面穿越了，我们正在努力找回");
		}

		long userId = getCurrUser().id;
		
		/** 正常还款 */
		if (t_bill.Status.NORMAL_NO_REPAYMENT.equals(bill.getStatus())) {
			result = billWechatService.normalRepayment(userId, bill);
			if (result.code < 1) {
				LoggerUtil.info(true, result.msg);
				toResultPage(WxPageType.PAGE_COMMUNAL_FAIL, result.msg);
			}
			
			List<Map<String, Double>> billInvestFeeList = (List<Map<String, Double>>) result.obj;
			
			String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.REPAYMENT);
			if (ConfConst.IS_TRUST) {
				result = PaymentProxy.getInstance().repayment(Client.WECHAT.code, serviceOrderNo, bill, billInvestFeeList);
				if (result.code < 1 && result.code != ResultInfo.ALREADY_RUN) {
					LoggerUtil.info(true, result.msg);
					toResultPage(WxPageType.PAGE_COMMUNAL_FAIL, result.msg);
				}
				
				toResultPage(WxPageType.PAGE_COMMUNAL_SUCC, "还款成功,请核对账单");
			}
			
			result = billWechatService.doRepayment(billId, billInvestFeeList, serviceOrderNo);
			if (result.code < 1) {
				LoggerUtil.info(true, result.msg);
				toResultPage(WxPageType.PAGE_COMMUNAL_FAIL, result.msg);
			}
			toResultPage(WxPageType.PAGE_COMMUNAL_SUCC, "还款成功,请核对账单");
			return ;
		}
		
		/** 本息垫付后还款 */
		if (t_bill.Status.ADVANCE_PRINCIIPAL_NO_REPAYMENT.equals(bill.getStatus())){
			
			result = billWechatService.advanceRepayment(userId, bill);
			if (result.code < 1) {
				LoggerUtil.info(true, result.msg);
				
				toResultPage(WxPageType.PAGE_COMMUNAL_FAIL, result.msg);
			}
			
			String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.REPAYMENT_AFER_ADVANCE);
			if (ConfConst.IS_TRUST) {
				result = PaymentProxy.getInstance().advanceRepayment(Client.WECHAT.code, serviceOrderNo, bill);
				if (result.code < 1 && result.code!=ResultInfo.ALREADY_RUN) {
					LoggerUtil.info(true, result.msg);
					toResultPage(WxPageType.PAGE_COMMUNAL_FAIL, result.msg);
				}
				
				toResultPage(WxPageType.PAGE_COMMUNAL_SUCC, "还款成功,请核对账单");

				return;
			}
			
			result = billWechatService.doAdvanceRepayment(serviceOrderNo, billId, bill.overdue_fine);
			if (result.code < 1) {
				LoggerUtil.info(true, result.msg);
				toResultPage(WxPageType.PAGE_COMMUNAL_FAIL, result.msg);
			}
			
			toResultPage(WxPageType.PAGE_COMMUNAL_SUCC, "还款成功,请核对账单");
			
			return ;
		}
		
		toResultPage(WxPageType.PAGE_COMMUNAL_FAIL, "账单状态非法");
	}
	
	/**
	 * 用户受让债权
	 *
	 * @param currPage
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年6月13日
	 */
	public static void accountInDebtPre(int currPage){
		if (currPage < 1) {
			currPage = 1;
		}

		PageBean<UserDebtBasic> pageBean = debtWechatService.pageOfUserDebt(currPage, 4, null,getCurrUser().id);
		
		render(pageBean);
	}
	
	/**
	 * 用户转让债权
	 *
	 * @param currPage
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年6月13日
	 */
	public static void accountOutDebtPre(int currPage){
		if (currPage < 1) {
			currPage = 1;
		}

		PageBean<UserDebtBasic> pageBean = debtWechatService.pageOfUserDebt(currPage, 4,getCurrUser().id, null);
		
		render(pageBean);
	}
	
	/**
	 * 账户中心-我的理财-理财详情
	 *
	 * @param debtId
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年6月12日
	 */
	public static void accountDebtDetailPre(long debtId){
		UserDebtDetail userDebtDetail = debtWechatService.findUserDebtDetail(debtId);
		
		if (userDebtDetail == null) {
			toResultPage(WxPageType.PAGE_FAIL, "非常遗憾，服务器暂时无法响应");
		}
		
		render(userDebtDetail);
	}
	

	/**
	 * 查看债权转让的协议
	 *
	 * @param debtSign 债权加密串
	 *
	 * @author Songjia
	 * @createDate 2016年5月11日
	 */
	public static void showDebtPactPre(String debtSign){
		ResultInfo result = Security.decodeSign(debtSign, Constants.DEBT_TRANSFER_ID_SIGN, Constants.VALID_TIME,ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			toResultPage(WxPageType.PAGE_FAIL, result.msg);
		}
		long debtId= Long.parseLong((String)result.obj);
	
		t_pact pact = pactService.findByDebtId(debtId);
		if (pact == null) {
			toResultPage(WxPageType.PAGE_FAIL, "非常遗憾，服务器暂时无法响应");
		}
		render(pact);
	}
	
	/**
	 * 我的财富-资产管理-我的理财-进入债权申请页面
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月25日
	 */
	@SubmitCheck
	public static void applyDebtPre(String sign){
		ResultInfo result = Security.decodeSign(sign, Constants.INVEST_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			LoggerUtil.info(false, "签名校验失败");
			
			toResultPage(WxPageType.PAGE_COMMUNAL_FAIL, "非常遗憾，服务器暂时无法响应");
		}
		
		long investId = Long.parseLong((String)result.obj);
		result = debtWechatService.isInvestCanbeTransfered(investId);
		if (result.code < 1) {
			
			toResultPage(WxPageType.PAGE_COMMUNAL_FAIL, result.msg);
		}
		
		DebtInvest debtInvest = debtWechatService.findDebtByInvestid(investId);
		if(debtInvest == null){
			
			toResultPage(WxPageType.PAGE_COMMUNAL_FAIL, "没有查到投资的相关信息");
		}
		double half_principal = Arith.div(debtInvest.debt_principal*8, 10, 0);//本金的80%~100%
		
		render(debtInvest,half_principal);
	}
	
	/**
	 * 我的财富-资产管理-我的理财-债权申请
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月25日
	 */
	@SubmitOnly
	public static void applyDebtTransfer(String sign,String title,int period,int transfer_price) {
		checkAuthenticity();
		ResultInfo result = Security.decodeSign(sign, Constants.INVEST_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			toResultPage(WxPageType.PAGE_COMMUNAL_FAIL, result.msg);
		}
		
		long investId = Long.parseLong((String)result.obj);
		if (StringUtils.isBlank(title) || title.length() > 20) {

			flash.error("转让标题长度有误");

			applyDebtPre(sign);
		}
		if (period <= 0 || period > 7) {
			flash.error("转让期限输入有误");
			applyDebtPre(sign);
		}
		if (transfer_price <= 0) {

			flash.error("转让底价输入有误");
			applyDebtPre(sign);
		}
		
		result = debtWechatService.applyDebtTransfer(investId, title, period, transfer_price);
		if (result.code < 1) {
			LoggerUtil.error(true, "债权转让申请失败:【%s】", result.msg);

			toResultPage(WxPageType.PAGE_COMMUNAL_FAIL, result.msg);
		}
		
		toResultPage(WxPageType.PAGE_COMMUNAL_SUCC, "债权转让申请成功");
	}
	
	
}
