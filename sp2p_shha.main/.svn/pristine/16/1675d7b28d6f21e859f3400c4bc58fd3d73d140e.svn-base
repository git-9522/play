package controllers.back.finance;

import hf.HfConsts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import models.bean.PaymentRequestLogs;
import models.bean.UserFundCheck;
import models.core.entity.t_invest;
import models.entity.t_payment_call_back;
import models.entity.t_payment_request;
import payment.impl.PaymentProxy;
import services.PaymentService;
import services.core.InvestService;
import common.enums.Client;
import common.enums.ServiceType;
import common.utils.Factory;
import common.utils.HttpUtil;
import common.utils.LoggerUtil;
import common.utils.OrderNoUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import controllers.common.BackBaseController;

/**
 * 后台-财务-资金校对-控制器
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2016年1月16日
 */
public class FundCheckCtrl extends BackBaseController {
	
	protected static PaymentService paymentService = Factory.getSimpleService(PaymentService.class);
	
	protected static InvestService investService = Factory.getService(InvestService.class);

	/**
	 * 用户资金信息列表
	 * @rightID 511001
	 *
	 * @param userName 
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年3月10日
	 */
	public static void showFundInfoPre(String userName, int currPage, int pageSize){
		if (currPage < 1) {
			currPage = 1;
		}

		if (pageSize < 1) {
			pageSize = 5;
		}
		
		PageBean<UserFundCheck> page = paymentService.pageOfUserFundCheck(userName, currPage, pageSize);
		
		render(page,userName);	
	}
	
	/**
	 * 后台投标撤回 
	 */
	public static void autoTenderCancelPre() {
		render();
	}
	
	public static void autoTenderCancel(long userId, double transAmt, long investId, String ordId) {
		
		ResultInfo result;
		
		if(userId <= 0) {
			flash.error("用户id错误");
			autoTenderCancelPre();
		}
		
		if(transAmt <= 0) {
			flash.error("金额输出错误");
			autoTenderCancelPre();
		}
		
		Map<String, String> unFreezeParam = new HashMap<String, String>();
		
		String serviceOrderNo = "";
		
		if(investId != 0) {
			t_invest invest = investService.findByID(investId);
			if(invest == null) {
				flash.error("投标信息找不到");
				autoTenderCancelPre();
			} else {
				unFreezeParam.put("UnFreezeOrdId", OrderNoUtil.getNormalOrderNo(ServiceType.USER_UNFREEZE));
				unFreezeParam.put("FreezeTrxId", invest.trust_order_no);
				serviceOrderNo = invest.service_order_no;
			}
		} else {
			if(StringUtils.isBlank(ordId)) {
				flash.error("投标订单不能为空");
				autoTenderCancelPre();
			}
			serviceOrderNo = ordId;
		}
		
		result = PaymentProxy.getInstance().autoTenderCancle(Client.PC.code, serviceOrderNo, userId, transAmt, unFreezeParam, investId);
		
		if(result.code == 1) {
			flash.success(result.msg);
			autoTenderCancelPre();
		} else {
			flash.error(result.msg);
			autoTenderCancelPre();
		}
		
	}
	
	/**
	 * 后台解冻接口 
	 */
	public static void usrUnFreezePre() {
		render();
	}

	public static void doUsrUnFreeze(long userId, String freezeTrxId, String ordNo, long investId) {
		ResultInfo result;
		
		if(userId <= 0) {
			flash.error("用户id错误");
			usrUnFreezePre();
		}
		
		if(investId <= 0) {
			if(StringUtils.isBlank(freezeTrxId)) {
				flash.error("业务订单号不能为空");
				usrUnFreezePre();
			}
			if(StringUtils.isBlank(ordNo)) {
				flash.error("第三方返回交易订单号");
				usrUnFreezePre();
			}
		} else {
			t_invest invest = investService.findByID(investId);
			if(invest == null) {
				flash.error("投标信息找不到");
				usrUnFreezePre();
			}
			ordNo = invest.service_order_no;
			freezeTrxId = invest.trust_order_no;
		}
		
		result = PaymentProxy.getInstance().usrUnFreeze(Client.PC.code, userId, ordNo, freezeTrxId);
		
		if(result.code == 1) {
			flash.success(result.msg);
			autoTenderCancelPre();
		} else {
			flash.error(result.msg);
			autoTenderCancelPre();
		}
	}
	
}