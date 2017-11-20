package controllers.back.finance;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.shove.Convert;

import common.annotation.SubmitCheck;
import common.annotation.SubmitOnly;
import common.enums.Client;
import common.enums.ServiceType;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.OrderNoUtil;
import common.utils.ResultInfo;
import controllers.common.BackBaseController;
import controllers.common.SubmitRepeat;
import models.common.entity.t_event_supervisor;
import payment.impl.PaymentProxy;
import play.mvc.With;
import services.common.SupervisorService;

/**
 * 后台-财务-商户号管理-控制器
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2015年12月19日
 */
@With(SubmitRepeat.class)
public class MerchantMngCtrl extends BackBaseController {
	
	protected static SupervisorService supervisorService = Factory.getService(SupervisorService.class);

	/**
	 * 后台-财务-商户号管理-进入商户号管理
	 * @rightID 509001
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月28日
	 */
	public static void toMerchantPre() {
		// 托管 查询商户可用余额
		ResultInfo result = PaymentProxy.getInstance().queryMerchantBalance(Client.PC.code);
		String showContent = "";
		
		if (result.code < 1) {
			showContent = "查询担保账户可用余额异常";
			
			if(result.code == -2){
				showContent = "暂不支持该功能";
			}
			render(showContent);
		}
		
		List<Map<String, Object>> merDetails = (List<Map<String, Object>>) result.obj;
		
		render(merDetails, showContent);
	}
	
	/**
	 * 后台-财务-商户号管理-充值页面
	 * @rightID 509002
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月20日
	 */
	@SubmitCheck
	public static void toMerchantRechargePre(String type) {
		render(type);
	}
	
	/**
	 * 商户充值
	 * @rightID 509002
	 *
	 * @param rechargeAmt 充值金额
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月27日
	 */
	@SubmitOnly
	public static void merchantRecharge (double rechargeAmt, String type, String bankId) {
		checkAuthenticity();
		
		if (rechargeAmt <= 0) {
			flash.error("充值金额必须大于0");
			
			toMerchantRechargePre(type);
		}
		
		if(StringUtils.isBlank(type)) {
			flash.error("子账户错误");
			toMerchantRechargePre(type);
		}
		
		boolean addEvent = supervisorService.addSupervisorEvent(getCurrentSupervisorId(),  t_event_supervisor.Event.GUARANTEE_RECHARGE, null);
		if (!addEvent) {
			flash.error("添加管理员事件失败");
			
			toMerchantRechargePre(type);
		}
		
		String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.MERCHANT_RECHARGE);
		
		ResultInfo result = PaymentProxy.getInstance().merchantRecharge(Client.PC.code, serviceOrderNo, rechargeAmt, type, bankId);
		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
			flash.error(result.msg);
			
			toMerchantRechargePre(type);
		}
		
		toMerchantRechargePre(type);
	}
	
	/**
	 * 后台-财务-商户号管理-商户提现页面
	 * @rightID 509003
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月20日
	 */
	@SubmitCheck
	public static void toMerchantWithdrawalPre(String type) {
		render(type);
	}
	
	/**
	 * 商户提现
	 * @rightID 509003
	 *
	 * @param withdrawalAmt 提现金额
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月27日
	 */
	@SubmitOnly
	public static void merchantWithdrawal (double withdrawalAmt, String type) {
		checkAuthenticity();
		
		if (withdrawalAmt <= 0) {
			flash.error("提现金额必须大于0");
			
			toMerchantRechargePre(type);
		}
		
		boolean addEvent = supervisorService.addSupervisorEvent(getCurrentSupervisorId(),  t_event_supervisor.Event.GUARANTEE_WITHDRAW, null);
		if (!addEvent) {
			flash.error("添加管理员事件失败");
			
			toMerchantRechargePre(type);
		}
		
		String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.MERCHANT_WITHDRAWAL);
		
		ResultInfo result = PaymentProxy.getInstance().merchantWithdrawal(Client.PC.code, serviceOrderNo, withdrawalAmt, type);
		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
			flash.error(result.msg);
			
			toMerchantRechargePre(type);
		}
		
		toMerchantRechargePre(type);
	}

	
}
