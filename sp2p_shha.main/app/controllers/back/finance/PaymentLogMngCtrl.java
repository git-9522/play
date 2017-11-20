package controllers.back.finance;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import models.bean.PaymentRequestLogs;
import models.entity.t_payment_call_back;
import models.entity.t_payment_request;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.commons.lang.StringUtils;

import payment.impl.PaymentProxy;
import services.PaymentService;
import common.constants.ConfConst;
import common.constants.Constants;
import common.constants.PaymentConst;
import common.enums.Client;
import common.enums.ServiceType;
import common.utils.Factory;
import common.utils.OrderNoUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.excel.ExcelUtils;
import common.utils.jsonAxml.JsonDateValueProcessor;
import common.utils.jsonAxml.JsonDoubleValueProcessor;
import controllers.common.BackBaseController;
import fy.FyPayType;

/**
 * 后台-财务-托管日志-控制器
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2016年1月16日
 */
public class PaymentLogMngCtrl extends BackBaseController {
	
	protected static PaymentService paymentService = Factory.getSimpleService(PaymentService.class);

	/**
	 * 后台-财务-托管日志-资金托管接口请求记录列表
	 * @rightID 510001
	 *
	 * @param showType 筛选切换参数
	 * @param currPage 当前页
	 * @param pageSize 分页大小
	 * @param serviceType 按业务类型筛选
	 * @param userName 按用户名筛选
	 * @param serviceOrderNo 按业务订单号筛选
	 * @param orderNo 按交易订单号筛选
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月23日
	 */
	public static void showRequestLogsPre (int showType, int currPage, int pageSize, Integer serviceType,
			String userName, String serviceOrderNo, String orderNo) {
		if (showType < 0 || showType > 2) {
			showType = 0;  //参数错误，默认显示所有
		}
		
		PageBean<PaymentRequestLogs> page = paymentService.pageOfRequestRecord(showType, currPage, pageSize, serviceType, userName, serviceOrderNo, orderNo);
		
		render(page, showType, serviceType, userName, serviceOrderNo, orderNo);
	}

	/**
	 * 查看请求参数
	 * @rightID 510002
	 *
	 * @param requestId
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月18日
	 */
	public static void showRequestDataPre (String requestMark) {
		Map<String, String> requestPrams = paymentService.queryRequestParams(requestMark);
		if (requestPrams == null) {
			flash.error("请求参数为空");
			redirect("back.finance.PaymentLogMngCtrl.showRequestLogsPre");
		}
		
		render(requestPrams);
	}
	
	/**
	 * 查看回调参数列表
	 * @rightID 510003
	 *
	 * @param requestId
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月18日
	 */
	public static void showCBDatasPre (long requestId) {
		t_payment_request paymentRequest = paymentService.findPaymentRequestById(requestId);
		if (paymentRequest == null) {
			flash.error("请求记录不存在");
			redirect("back.finance.PaymentLogMngCtrl.showRequestLogsPre");
		}
		
		String serviceOrderNo = paymentRequest.service_order_no;
		
		String orderNo = paymentRequest.order_no;
		
		List<t_payment_call_back> CBList = paymentService.queryCallBackList(paymentRequest.mark);

		boolean isFYPayment = true;
		if (!ConfConst.CURRENT_TRUST_TYPE.equals(PaymentConst.TRUST_TYPE_FY)) {
			isFYPayment = false;
		}
		
		render(serviceOrderNo, CBList, orderNo, requestId, paymentRequest, isFYPayment);
	}
	
	/**
	 * 日志补单
	 * @rightID 510003
	 *
	 * @param callBackId 回调日志id
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月19日
	 */
	public static void repair(long callBackId){
		ResultInfo result = new ResultInfo();
		
		String callBackURL = paymentService.findCBURLByCBId(callBackId);
		if (StringUtils.isBlank(callBackURL)) {
			result.code = -1;
			result.msg = "查询异步回调地址失败";

			renderJSON(result);
		}
		
		Map<String, String> callBackParams = paymentService.queryCallBackParams(callBackId);
		if (callBackParams == null) {
			result.code = -1;
			result.msg = "查询回调参数失败";

			renderJSON(result);
		}
		
		callBackParams.put("url", callBackURL);
		
		result.code = 1;
		result.obj = callBackParams;
		
		renderJSON(result);
	}
	
	/**
	 * 交易查询
	 * @rightID 510003
	 * @param txn_ssn 查询的富友流水号
	 * @author jyq
	 * 
	 * @createDate 2016年9月23日
	 */
	public static void queryRepair(String txn_ssn, long requestId){
		ResultInfo result = new ResultInfo();
		
		t_payment_request pr = paymentService.queryRequest(txn_ssn);
		if (pr == null) {
			result.code = -1;
			result.msg = "查询托管请求记录失败";
			
			flash.error(result.msg);
			showCBDatasPre(requestId);
		}
		
		/*ServiceType serviceType =*/ pr.getService_type();
		
		//查询请求时入库的参数
		Map<String, String> remarkParams = paymentService.queryRequestParams(txn_ssn);
				
		if (remarkParams == null){
			result.code = -1;
			result.msg = "查询托管请求备注参数失败";
			
			flash.error(result.msg);
			showCBDatasPre(requestId);
		}
		
		FyPayType fyPayType = FyPayType.getEnum(pr.getPay_type_code());
		
		//业务流水号
		String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.QUERYTXN);
				
		if (ConfConst.IS_TRUST) { 
			
			//如果是充值提现查询
			if (FyPayType.NETSAVE.equals(fyPayType) || FyPayType.CASH.equals(fyPayType)) {
				
				result = PaymentProxy.getInstance().queryCztx(Client.PC.code, serviceOrderNo, pr);
				if (result.code < 1) {
					flash.error(result.msg);
					showCBDatasPre(requestId);
				}
			}else{
				//交易查询
				result = PaymentProxy.getInstance().queryTxn(Client.PC.code, serviceOrderNo, pr);
				if (result.code < 1) {
					flash.error(result.msg);
					showCBDatasPre(requestId);
				}
			}
			
			
		}
		
		result.code = 1;
		result.msg = "查询成功, 请核对结果!";
		result.obj = remarkParams;
		
		flash.success(result.msg);
		showCBDatasPre(requestId);
	}
}
