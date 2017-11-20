package controllers.wechat.front;

import java.util.Date;

import models.common.bean.CurrUser;
import models.core.bean.DebtTransfer;
import models.core.bean.DebtTransferDetail;
import models.core.bean.InvestReceive;
import models.wechat.bean.WechatDebtTransfer;
import models.wechat.bean.WechatDebtTransferDetail;
import payment.impl.PaymentProxy;
import play.mvc.With;
import service.wechat.BillWechatInvestService;
import service.wechat.DebtWechatService;
import services.common.UserFundService;

import common.annotation.LoginCheck;
import common.annotation.SubmitCheck;
import common.annotation.SubmitOnly;
import common.constants.ConfConst;
import common.constants.Constants;
import common.constants.WxPageType;
import common.enums.Client;
import common.enums.ServiceType;
import common.utils.Factory;
import common.utils.OrderNoUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;

import controllers.common.SubmitRepeat;
import controllers.wechat.WechatBaseController;
import controllers.wechat.interceptor.UserStatusWxInterceptor;

/**
 * 债权转让
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年6月22日
 */
@With({SubmitRepeat.class,UserStatusWxInterceptor.class})
public class DebtCtrl extends WechatBaseController {
	
	protected static DebtWechatService debtWechatService = Factory.getService(DebtWechatService.class);
	
	protected static UserFundService userFundService = Factory.getService(UserFundService.class);
	
	protected static BillWechatInvestService billWechatInvestService = Factory.getService(BillWechatInvestService.class);
	
	/**
	 * 进入债权列表页面
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年6月15日
	 */
	public static void toWechatDebtPre(int currPage) {
		if (currPage < 1) {
			currPage = 1;
		}
		//债权
		PageBean<WechatDebtTransfer> pageBean = debtWechatService.DebtTransfers(currPage, 100, 99);
		Date sysNowTime = new Date();
		
		render(pageBean,sysNowTime);
	}
	//TODO 改版之前
	/*	public static void toWechatDebtPre() {

	render();
}*/
	/**
	 * 记载债权列表页面
	 *
	 * @param currPage
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年6月20日
	 */
	public static void showWechatDebtsPre(int currPage) {
		if (currPage < 1) {
			currPage = 1;
		}
		//债权
		PageBean<DebtTransfer> pageBean = debtWechatService.pageOfDebtTransfer(currPage, 100, 99);
		Date sysNowTime = new Date();
		
		render(pageBean,sysNowTime);
	}
	
	/**
	 * 债权详情(债权购买页面)
	 *
	 * @param debtId 债权id
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年6月15日
	 */
	@SubmitCheck
	public static void debtWechatPre(long debtId) {
		if (debtId <= 0) {
			toResultPage(WxPageType.PAGE_FAIL, "债权不存在");
		}
		
	WechatDebtTransferDetail detail = debtWechatService.wechatFindDebtDetailById(debtId);
		if (detail == null) {

			toResultPage(WxPageType.PAGE_FAIL, "债权不存在");
		}
		
		CurrUser currUser = getCurrUser();
		if (currUser != null) {
			double balance = userFundService.findUserBalance(currUser.id);
			renderArgs.put("balance", balance);
		}
		
		renderArgs.put("sysNowTime", new Date());//服务器时间传到客户端
		//回款计划
		PageBean<InvestReceive> bill=null;
		if( detail.invest_id!=null){
		bill = billWechatInvestService.pageOfRepaymentBill(1, 24, detail.invest_id);
		}
		render(detail,bill);
	}
	
	/**
	 * 债权的回款计划
	 *
	 * @param currPage
	 * @param pageSize
	 * @param investId 投资id
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年6月15日
	 */
	public static void repaymentDebtRecordPre(int currPage,long investId){
		if (currPage < 1) {
			currPage = 1;
		}

		PageBean<InvestReceive> pageBean = billWechatInvestService.pageOfRepaymentBill(currPage, 5, investId); 
		
		render(pageBean);
	}

	
	/**
	 * 购买债权
	 *
	 * @param debtSign
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年6月15日
	 */
	@SubmitOnly
	@LoginCheck
	public static void buyDebt(String debtSign){
		checkAuthenticity();
		ResultInfo result = Security.decodeSign(debtSign, Constants.DEBT_TRANSFER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			flash.error(result.msg);
			//TODO 改版  toWechatDebtPre();
			toWechatDebtPre(1);
		}
		long debtId = Long.parseLong((String)result.obj);
		CurrUser currUser = getCurrUser();

		//准备
		result = debtWechatService.debtTransfer(debtId, currUser.id);
		if(result.code < 1){
			toResultPage(WxPageType.PAGE_COMMUNAL_FAIL, result.msg);
		}
			
		//t_debt_tansfer的订单号
		String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.DEBT_TRANSFER);
			
		if (ConfConst.IS_TRUST) {
			//资金托管
			PaymentProxy.getInstance().debtTransfer(Client.WECHAT.code, serviceOrderNo, debtId, currUser.id);
			
			debtWechatPre(debtId);
		}
		
		//进入页面
		debtWechatPre(debtId);
	}
	
}
