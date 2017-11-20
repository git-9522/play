package controllers.back.finance;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import models.core.bean.Bill;
import models.core.entity.t_bill;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.commons.lang.StringUtils;

import payment.impl.PaymentProxy;
import services.core.BillService;

import com.shove.Convert;
import common.constants.ConfConst;
import common.constants.Constants;
import common.enums.Client;
import common.enums.ServiceType;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.OrderNoUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.excel.ExcelUtils;
import common.utils.jsonAxml.JsonDateValueProcessor;
import common.utils.jsonAxml.JsonDoubleValueProcessor;

import controllers.common.BackBaseController;

/**
 * 后台-财务-借款账单-控制器
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2015年12月19日
 */
public class BillMngCtrl extends BackBaseController{
	
	protected static BillService billService = Factory.getService(BillService.class);

	/**
	 * 后台-财务-借款账单-借款账单列表
	 * @rightID 502001
	 *
	 * @param currPage 当前页
	 * @param pageSize 每页条数
	 * @param showType 查询状态  default:所有    1:待还(正常待还+逾期待还+本息垫付待还) 2:逾期待还(待还+逾期) 3:已还(正常还款、本息垫付还款 、线下收款、本息垫付后线下收款 )
	 * @param exports 1：导出  default：不导出
	 * @param loanName 借款人昵称
	 * @param orderType 排序栏目  0：编号   3：账单金额    5：逾期时长   6：到期时间    7：还款时间
	 * @param orderValue 排序规则 0,降序，1,升序
	 * @param projectName 项目
	 *
	 * @author liudong
	 * @createDate 2015年12月18日
	 */
	public static void showLoanBillsPre(int showType, int currPage,int pageSize){
		int exports = Convert.strToInt(params.get("exports"), 0);
		String loanName = params.get("loanName");
		String projectName = params.get("projectName");
		
		//排序栏目
		String orderTypeStr = params.get("orderType");
		int orderType = Convert.strToInt(orderTypeStr, 0);// 0：编号   3：账单金额    5：逾期时长   6：到期时间    7：还款时间
		if(orderType != 0 && orderType  != 3 && orderType  != 5 && orderType  != 6 && orderType  != 7){
			orderType = 0;
		}
		renderArgs.put("orderType", orderType);
				
		//排序规则
		String orderValueStr = params.get("orderValue");
		int orderValue = Convert.strToInt(orderValueStr, 0);//0,降序，1,升序
		if(orderValue<0 || orderValue>1){
			orderValue = 0;
		}
		renderArgs.put("orderValue", orderValue);
		
		/** 查询账单 */
		PageBean<Bill> page = billService.pageOfBillBack(showType,currPage, pageSize, exports, loanName, orderType, orderValue, projectName);
		
		//导出
		if(exports == Constants.EXPORT){
			List<Bill> list = page.page;
			 
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(Constants.DATE_FORMATE));
			jsonConfig.registerJsonValueProcessor(Double.class, new JsonDoubleValueProcessor(Constants.FINANCE_FORMATE_NORMAL));
			JSONArray arrList = JSONArray.fromObject(list, jsonConfig);
			
			for (Object obj : arrList) {
				JSONObject bill = (JSONObject)obj;
				
				bill.put("period_no", bill.get("period")+"|"+bill.get("totalPeriod"));
				
				if(StringUtils.isNotBlank(bill.getString("status"))){
					bill.put("status", t_bill.Status.valueOf(bill.get("status").toString()).value);
				}
			}
			
			String fileName="借款账单列表";
			switch (showType) {
				case 1:
					fileName = "待还借款账单列表";
					break;
				case 2:
					fileName = "逾期待还借款账单列表";
					break;
				case 3:
					fileName = "已还借款账单列表";
					break;
				default:
					fileName = "借款账单列表";
					break;
			}
			
			File file = ExcelUtils.export(fileName,
			arrList,
			new String[] {
			"编号", "项目", "借款人", "账单金额", "期数", "逾期时长", "到期时间", "还款时间", "状态"},
			new String[] {
			"bill_no","title", "name", "corpus_interest", "period_no", "overdue_days", "repayment_time", "real_repayment_time", "status"});
			   
			renderBinary(file, fileName + ".xls");
		}
		
		/** 查询账单总额 */
		double totalAmt = billService.findTotalBillAmount(showType,loanName,projectName);
		
		render(page,totalAmt,showType,loanName, projectName);
	}
	
	
	/**
	 * 本息垫付
	 * @rightID 502003
	 *
	 * @param billSign 借款账单签名
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月30日
	 */
	public static void principalAdvance(String billSign) {
		ResultInfo result = Security.decodeSign(billSign, Constants.BILL_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			LoggerUtil.info(false, "签名检验失败");
			
			error404();
		}
		
		long billId = Long.parseLong(result.obj.toString());
		t_bill bill = billService.findByID(billId);
		if (bill == null) {
			
			error404();
		}
		
		//商户可用余额查询，
		result = PaymentProxy.getInstance().queryMerchantBalance(Client.PC.code);
		if (result.code < 1) {
			flash.error("查询商户可用余额异常");
			showLoanBillsPre(0,1,10);
		}
		
		Map<String, Object> merDetail = (Map<String, Object>) result.obj;
		double merBalance = Convert.strToDouble(merDetail.get("balance").toString(), 0);
		
		//垫付总额
		double advanceAmt = bill.repayment_corpus + bill.repayment_interest + bill.overdue_fine;
		
		if (advanceAmt > merBalance) {
			flash.error("商户余额不足，请充值");
			
			showLoanBillsPre(0,1,10);
		}
		
		long supervisorId = getCurrentSupervisorId();
		
		result = billService.principalAdvance(bill, supervisorId);
		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
			flash.error(result.msg);
			
			showLoanBillsPre(0,1,10);
		}
		
		List<Map<String, Double>> billInvestFeeList = (List<Map<String, Double>>) result.obj;
	
		String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.ADVANCE);
		if (ConfConst.IS_TRUST) {
			result = PaymentProxy.getInstance().advance(Client.PC.code, serviceOrderNo, bill, billInvestFeeList);
			if (result.code < 1 && result.code != ResultInfo.ALREADY_RUN) {
				LoggerUtil.info(true, result.msg);
				
				flash.error(result.msg);
				showLoanBillsPre(0,1,10);
			}
			
			flash.success("本息垫付成功，请核对账单！");
			showLoanBillsPre(0,1,10);
		}
		
		if (!ConfConst.IS_TRUST){
			
			result = billService.doPrincipalAdvance(billId, serviceOrderNo, billInvestFeeList);
			if (result.code < 1) {
				LoggerUtil.info(true, result.msg);
				
				flash.error(result.msg);
				showLoanBillsPre(0,1,10);
			}
			
		}
		
		flash.success(result.msg);
		showLoanBillsPre(0,1,10);
		
	}
	
	/**
	 * 线下收款
	 * @rightID 502002
	 *
	 * @param billSign 借款账单
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月22日
	 */
	public static void offlineReceive (String billSign) {
		ResultInfo result = Security.decodeSign(billSign, Constants.BILL_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			LoggerUtil.info(false, "签名检验失败");
			
			error404();
		}
		
		long billId = Long.parseLong(result.obj.toString());
		
		t_bill bill = billService.findByID(billId);
		if (bill == null) {
			
			error404();
		}
		
		long supervisorId = getCurrentSupervisorId();
		
		// 托管 查询商户可用余额
		result = PaymentProxy.getInstance().queryMerchantBalance(Client.PC.code);
		if (result.code < 1) {
			flash.error("查询商户可用余额异常");
			showLoanBillsPre(0,1,10);
		}
		
		Map<String, Object> merDetail = (Map<String, Object>) result.obj;
		double merBalance = Convert.strToDouble(merDetail.get("balance").toString(), 0);
		
		//垫付总额
		double advanceAmt = bill.repayment_corpus + bill.repayment_interest + bill.overdue_fine;
		
		if (advanceAmt > merBalance) {
			flash.error("商户余额不足，请充值");
			showLoanBillsPre(0,1,10);
		}
		
		/** 线下收款 */
		if (t_bill.Status.NORMAL_NO_REPAYMENT.equals(bill.getStatus())) {
			result = billService.offlineReceive(supervisorId, bill);
			if (result.code < 1) {
				LoggerUtil.info(true, result.msg);
				
				return;
			}
			
			List<Map<String, Double>> billInvestFeeList = (List<Map<String, Double>>) result.obj;
			
			String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.OFFLINE_RECEIVE);
			if (ConfConst.IS_TRUST) {
				result = PaymentProxy.getInstance().offlineReceive(Client.PC.code, serviceOrderNo, bill, billInvestFeeList);
				if (result.code < 1 && result.code != ResultInfo.ALREADY_RUN) {
					LoggerUtil.info(true, result.msg);
					
					flash.error(result.msg);
					showLoanBillsPre(0,1,10);
				}
				
				flash.success("线下收款成功，请核对账单！");
				showLoanBillsPre(0,1,10);
				
				return;
			}
			
			result = billService.doOfflineReceive(bill.id, billInvestFeeList, serviceOrderNo);
			if (result.code < 1) {
				LoggerUtil.info(true, result.msg);
				
				return;
			}
			
			flash.success(result.msg);
			showLoanBillsPre(0,1,10);

			return ;
		}
		
		if (!ConfConst.IS_TRUST){
			/** 本息垫付后线下收款 */
			if (t_bill.Status.ADVANCE_PRINCIIPAL_NO_REPAYMENT.equals(bill.getStatus())){
				
				result = billService.doOfflineReceiveAfterAdvance(supervisorId, billId);
				if (result.code < 1) {
					LoggerUtil.info(true, result.msg);
					
					return;
				}
				
				flash.success(result.msg);
				showLoanBillsPre(0,1,10);
				
				return ;
			}
		}
		
		flash.error("状态非法，不能执行线下收款");
		showLoanBillsPre(0,1,10);
	}

}
