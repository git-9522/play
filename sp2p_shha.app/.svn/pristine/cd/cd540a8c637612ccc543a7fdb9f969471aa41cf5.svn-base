package controllers.app.wealth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.app.bean.DebtInvestBean;
import models.app.bean.InOutDebt;
import models.app.bean.MyInvestRecordBean;
import models.common.entity.t_pact;
import models.core.entity.t_bill;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

import org.apache.commons.lang.StringUtils;

import payment.impl.PaymentProxy;
import service.AccountAppService;
import service.DebtAppService;
import service.LoanAppService;
import services.common.PactService;
import services.core.BillService;

import com.shove.Convert;
import common.constants.ConfConst;
import common.constants.Constants;
import common.enums.DeviceType;
import common.enums.ServiceType;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.OrderNoUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.StrUtil;

/**
 * 账户中心-财富管理
 *
 * @description 包括我的投资，我的借款，我的受让/转让，转让申请等
 *
 * @author DaiZhengmiao
 * @createDate 2016年6月29日
 */
public class MyFundAction {
	
	private static AccountAppService accountAppService = Factory.getService(AccountAppService.class);
	private static LoanAppService loanAppService = Factory.getService(LoanAppService.class);
	private static BillService billService = Factory.getService(BillService.class);
	private static DebtAppService debtAppService = Factory.getService(DebtAppService.class);
	private static PactService pactService = Factory.getService(PactService.class);

	/***
	 * 
	 * 我的投资接口（OPT=231）
	 * @param parameters
	 * @return
	 * @throws Exception
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-5
	 */
	public static String pageOfMyInvest (Map<String, String> parameters) throws Exception{
		JSONObject json = new JSONObject();
		
		String signId = parameters.get("userId");
		String currentPageStr = parameters.get("currPage");
		String pageNumStr = parameters.get("pageSize");
		
		ResultInfo result = Security.decodeSign(signId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
			 return json.toString();
		}

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
		
		long userId = Long.parseLong(result.obj.toString());
		
		PageBean<MyInvestRecordBean> page = accountAppService.pageOfMyInvestRecord(currPage, pageSize, userId);
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setIgnoreDefaultExcludes(false);
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		jsonConfig.setExcludes(new String[] {// 只要设置这个数组，指定过滤哪些字段。
				"investOriId",
				"id"
		});
		  
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", 1);
		map.put("msg", "查询成功");
		map.put("records", page.page);
//		JSONObject.fromObject(object, jsonConfig)
		return JSONObject.fromObject(map,jsonConfig).toString();
	}
		
	
	/***
	 * 
	 * 投资账单接口（OPT=232）
	 * @param parameters
	 * @return
	 * @throws Exception
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-5
	 */
	public static String listOfInvestBill (Map<String, String> parameters) throws Exception{
		JSONObject json = new JSONObject();
		String signUsersId = parameters.get("userId");
		String signInvestId = parameters.get("investId");
		ResultInfo result = Security.decodeSign(signUsersId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
			 return json.toString();
		}

		long userId = Long.parseLong(result.obj.toString());
		
		result = Security.decodeSign(signInvestId, Constants.INVEST_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code",-1);
			 json.put("msg", result.msg);
			 
			 return json.toString();
		}
		long investId = Long.parseLong(result.obj.toString());

		return accountAppService.listOfInvestBillRecord(userId,investId);
	}
	
	/**
	 * 我的借款（OPT=233）
	 *
	 * @param parameters
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年4月1日
	 */
	public static String pageOfMyLoan(Map<String, String> parameters){
		JSONObject json = new JSONObject();
		
		String signId = parameters.get("userId");
		String currentPageStr = parameters.get("currPage");
		String pageNumStr = parameters.get("pageSize");
		
		ResultInfo result = Security.decodeSign(signId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
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
		PageBean pageBean = accountAppService.pageOfMyLoan(Long.parseLong(result.obj.toString()), currPage, pageSize);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", 1);
		map.put("msg", "查询成功");
		map.put("records", pageBean.page);
		
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setIgnoreDefaultExcludes(false);
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		jsonConfig.setExcludes(new String[] {// 只要设置这个数组，指定过滤哪些字段。
				"id"
		});
		
		return JSONObject.fromObject(map, jsonConfig).toString();
	}
	
	
	/**
	 * opt=234 借款账单
	 *
	 * @param parameters
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年4月14日
	 */
	public static String listOfLoanBill(Map<String, String> parameters) {
		JSONObject json = new JSONObject();
		
		String signUserId = parameters.get("userId");
		String signBidId = parameters.get("bidId");
		if (StringUtils.isBlank(signUserId)) {
			json.put("code", -1);
			json.put("msg", "参数userId不能为空");
			
			return json.toString();
		}
		if (StringUtils.isBlank(signBidId)) {
			json.put("code", -1);
			json.put("msg", "参数bidId不能为空");
			
			return json.toString();
		}
		
		ResultInfo result = Security.decodeSign(signUserId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
			 return json.toString();
		}
		ResultInfo result2 = Security.decodeSign(signBidId, Constants.BID_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", -1);
			 json.put("msg", result.msg);
			 
			 return json.toString();
		}
		long userId = Long.parseLong(result.obj.toString());
		long bidId = Long.parseLong(result2.obj.toString());
		
		return loanAppService.listOfLoanBill(userId, bidId);
	}
	
	/****
	 * 还款请求（Opt=235）
	 *
	 * @param parameters
	 * @return
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-11
	 */
	public static String repayment (Map<String, String> parameters) {
		JSONObject json = new JSONObject();
		
		String billIdSign = parameters.get("billIdSign");
		String userIdSign = parameters.get("userId");
		String deviceType = parameters.get("deviceType");
		
		ResultInfo result = Security.decodeSign(billIdSign, Constants.BILL_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
			 return json.toString();
		}
		
		
		ResultInfo userResult = Security.decodeSign(userIdSign, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (userResult.code < 1) {
			 json.put("code", -1);
			 json.put("msg", result.msg);
			 
			 return json.toString();
		}
		
		
		if (DeviceType.getEnum(Convert.strToInt(deviceType, -99))==null) {
			json.put("code", -1);
			json.put("msg", "请使用移动客户端操作");
			
			return json.toString();
		}

		long billId = Long.parseLong(result.obj.toString());
		long userId = Long.parseLong(userResult.obj.toString());
		
		t_bill bill = billService.findByID(billId);
		if (bill == null) {
			 json.put("code", -1);
			 json.put("msg", "服务器繁忙");
			 
			 return json.toString();
		}
		
		/** 正常还款 或者部分还款*/
		if (t_bill.Status.NORMAL_NO_REPAYMENT.equals(bill.getStatus()) || t_bill.Status.PARTIAL_NORMAL_REPAYMENT.equals(bill.getStatus())) {
			result = billService.normalRepayment(userId, bill);
			if (result.code < 1) {
				 json.put("code", -1);
				 json.put("msg", result.msg);
				 
				 return json.toString();
			}
			
			List<Map<String, Double>> billInvestFeeList = (List<Map<String, Double>>) result.obj;
			
			String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.REPAYMENT);
			if (ConfConst.IS_TRUST) {
				result = PaymentProxy.getInstance().repayment(Convert.strToInt(deviceType, DeviceType.DEVICE_ANDROID.code), serviceOrderNo, bill, billInvestFeeList);

				if (result.code < 1 && result.code != ResultInfo.ALREADY_RUN) {
					 LoggerUtil.info(true, result.msg);
					 json.put("code", -1);
					 json.put("msg", result.msg);	 
				} else {
					 json.put("code", result.code);
					 json.put("msg", result.msg);
				}
				
				 return json.toString();
			}
			
			if (!ConfConst.IS_TRUST){
				result = billService.doRepayment(billId, billInvestFeeList, serviceOrderNo);
				if (result.code < 1) {
					 LoggerUtil.info(true, result.msg);
					 json.put("code", -1);
					 json.put("msg", result.msg);
					 
					 return json.toString();
				}
			}
			
			json.put("code", 1);
			json.put("msg", "还款成功,请核对账单");
			
			return json.toString();
		}
		
		/** 本息垫付后还款 */
		if (t_bill.Status.ADVANCE_PRINCIIPAL_NO_REPAYMENT.equals(bill.getStatus())){
			
			result = billService.advanceRepayment(userId, bill);
			if (result.code < 1) {
				 LoggerUtil.info(true, result.msg);
				 json.put("code", -1);
				 json.put("msg", result.msg);
				 
				 return json.toString();
			}
			
			String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.REPAYMENT_AFER_ADVANCE);
			if (ConfConst.IS_TRUST) {
				result = PaymentProxy.getInstance().advanceRepayment(Convert.strToInt(deviceType, DeviceType.DEVICE_ANDROID.code), serviceOrderNo, bill);
				if (result.code < 1 && result.code!=ResultInfo.ALREADY_RUN) {
					 LoggerUtil.info(true, result.msg);
					 json.put("code", -1);
					 json.put("msg", result.msg);
					 
					 return json.toString();
				}
				
				json.put("code", 1);
			    json.put("msg", "还款成功,请核对账单");		 	 
				
				return json.toString();
			}
			
			if (!ConfConst.IS_TRUST){
				result = billService.doAdvanceRepayment(serviceOrderNo, billId, bill.overdue_fine);
				if (result.code < 1) {
					 LoggerUtil.info(true, result.msg);
					 json.put("code", -1);
					 json.put("msg", result.msg);
					 
					 return json.toString();
				}
			}
			
			json.put("code", 1);
			json.put("msg", "还款成功,请核对账单");
			
			return json.toString();
		}
		
		 json.put("code", -1);
		 json.put("msg", "账单已还款");
		 
		 return json.toString();
	}

	/***
	 * 
	 * 查看合同（OPT=236）
	 * @return
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-12
	 */
	public static String showBidPact(Map<String, String> parameters){
		JSONObject json = new JSONObject();
		String bidIdSign = parameters.get("bidId");
		
		ResultInfo result = Security.decodeSign(bidIdSign, Constants.BID_ID_SIGN, Constants.VALID_TIME,ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code",-1);
			 json.put("msg", result.msg);
			 
			 return json.toString();
		}
		
		long bidId = Long.parseLong((String)result.obj);
		
	   return accountAppService.findBidPact(bidId);
	}
	
	/***
	 * 理财账单详情 (OPT=237)
	 *
	 * @param parameters
	 * @return
	 * @throws Exception
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-14
	 */
	public static String investBillInfo(Map<String, String> parameters) throws Exception{
		JSONObject json = new JSONObject();
		
		String billInvestId = parameters.get("billInvestId");
		String bidId = parameters.get("bidId");
		if(!StrUtil.isNumeric(billInvestId)){
			 json.put("code", -1);
			 json.put("msg", "账单id非法");
			 
			 return json.toString();
		}
		if(!StrUtil.isNumeric(bidId)){
			 json.put("code", -1);
			 json.put("msg", "标id非法");
			 
			 return json.toString();
		}
		
		return accountAppService.findInvestBillRecord(Convert.strToLong(billInvestId,0),Convert.strToLong( bidId,0));
	}
	
	/**
	 * opt=238 借款账单详情
	 *
	 * @param parameters
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年4月14日
	 */
	public static String findLoanBill(Map<String, String> parameters) {
		JSONObject json = new JSONObject();
		
		String signUserId = parameters.get("userId");
		String billIdSign = parameters.get("billIdSign");
		String bidIdSign = parameters.get("bidId");
		if (StringUtils.isBlank(signUserId)) {
			json.put("code", -1);
			json.put("msg", "参数userId不能为空");
			
			return json.toString();
		}
		if (StringUtils.isBlank(billIdSign)) {
			json.put("code", -1);
			json.put("msg", "参数billIdSign不能为空");
			
			return json.toString();
		}
		if (StringUtils.isBlank(bidIdSign)) {
			json.put("code", -1);
			json.put("msg", "参数bidId不能为空");
			
			return json.toString();
		}
		
		ResultInfo result = Security.decodeSign(signUserId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			json.put("code", ResultInfo.LOGIN_TIME_OUT);
			json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			
			return json.toString();
		}
		ResultInfo result2 = Security.decodeSign(billIdSign, Constants.BILL_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			json.put("code", -1);
			json.put("msg", result.msg);
			
			return json.toString();
		}
		ResultInfo result3 = Security.decodeSign(bidIdSign, Constants.BID_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			json.put("code", -1);
			json.put("msg", result.msg);
			
			return json.toString();
		}
		long userId = Long.parseLong(result.obj.toString());
		long billId = Long.parseLong(result2.obj.toString());
		long bidId = Long.parseLong(result3.obj.toString());
		
		return loanAppService.findLoanBill(userId, billId, bidId);
	}
	
	/**
	 * 我的受让/我的转让(OPT=239)
	 *
	 * @param parameters
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年6月30日
	 */
	public static String pageOfDebt(Map<String, String> parameters){
		JSONObject json = new JSONObject();
		String signId = parameters.get("userId");
		String debtOfStr = parameters.get("debtOf");
		String currentPageStr = parameters.get("currPage");
		String pageNumStr = parameters.get("pageSize");
		
		if (!StrUtil.isNumeric(currentPageStr) || !StrUtil.isNumeric(pageNumStr)) {
			json.put("code", -1);
			json.put("msg", "分页参数不正确");

			return json.toString();
		}
		int currPage = Convert.strToInt(currentPageStr, 1);
		int pageSize = Convert.strToInt(pageNumStr, 5);
		
		int debtOf = -1;
		if (!StrUtil.isNumeric(debtOfStr)) {
			json.put("code", -1);
			json.put("msg", "债权类型有误");

			return json.toString();
		} else {
			debtOf = Convert.strToInt(debtOfStr, -1);
			if (debtOf != 1 && debtOf != 0) {
				json.put("code", -1);
				json.put("msg", "债权类型有误");

				return json.toString();
			}
		}
		
		ResultInfo result = Security.decodeSign(signId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 return json.toString();
		}
		long userId = Long.valueOf(result.obj+"");
		
		PageBean<InOutDebt> page = null;
		if(debtOf == 0){
			page = debtAppService.pageOfAppDebtByUser(currPage, pageSize, null, userId);//0受让，1转让
		} else {
			page = debtAppService.pageOfAppDebtByUser(currPage, pageSize, userId, null);//0受让，1转让
		}
		
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setIgnoreDefaultExcludes(false);
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		jsonConfig.setExcludes(new String[] {// 只要设置这个数组，指定过滤哪些字段。
				"id"
		});
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", 1);
		map.put("msg", "查询成功");
		map.put("records", page.page);
		return JSONObject.fromObject(map,jsonConfig).toString();
	}


	/**
	 * 查看合同（OPT=2312）
	 *
	 * @param parameters
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年7月6日
	 */
	public static String showDebtPact(Map<String, String> parameters){
		JSONObject json = new JSONObject();
		String debtIdSign = parameters.get("debtId");
		
		ResultInfo result = Security.decodeSign(debtIdSign, Constants.DEBT_TRANSFER_ID_SIGN, Constants.VALID_TIME,ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);

			return json.toString();
		}
		
		long debtId = Long.parseLong((String)result.obj);
		
		t_pact pact = pactService.findByDebtId(debtId);
		if (pact == null) {
			json.put("code", -1);
			json.put("msg", "协议不存在");

			return json.toString();
		}
		json.put("html", pact.content);
		json.put("code", 1);
		json.put("msg", "加载成功");
		
		return json.toString();
	}
	
	/**
	 * 我的财富-资产管理-我的理财-进入债权申请页面(OPT=2313)
	 *
	 * @param parameters
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年6月30日
	 */
	public static String applyDebtPre(Map<String, String> parameters){
		
		JSONObject json = new JSONObject();
		String signId = parameters.get("investId");
		
		ResultInfo result = Security.decodeSign(signId, Constants.INVEST_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 return json.toString();
		}
		
		long investId = Long.parseLong((String)result.obj);
		result = debtAppService.isInvestCanbeTransfered(investId);
		if (result.code < 1) {
			
			json.put("code", -1);
			json.put("msg", result.code);
			return json.toString();
		}
		
		DebtInvestBean debtInvest = debtAppService.findDebtInvestByInvestId(investId);
		if (debtInvest == null) {

			json.put("code", -1);
			json.put("msg", "债权不存在");
			return json.toString();
		}
		
		json.put("code", 1);
		json.put("msg", "投资剩余债权信息查询成功");
		json.put("investId", debtInvest.getSign());//投资id加密串
		json.put("debtAmount", debtInvest.debt_amount);//债权总额
		json.put("debtPrincipal", debtInvest.debt_principal);//待收本金
		json.put("debtInterest", debtInvest.debt_interest);//待收利息
		json.put("period", debtInvest.period);//待转让期数
		
		
		return json.toString();
	}
	
	/**
	 * 我的财富-资产管理-我的理财-债权申请(OPT=2314)
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月25日
	 */
	public static String applyDebtTransfer(Map<String, String> parameters) {
		JSONObject json = new JSONObject();
		
		String title = parameters.get("title");
		String periodStr = parameters.get("period");//转让时间
		int period = 0;
		String transferPriceStr = parameters.get("transferPrice");
		int transferPrice = 0;
		String investIdSign = parameters.get("investId");
		long investId = 0L;
		
		
		if (StringUtils.isBlank(title) || title.length() > 20 || title.length() < 2) {
			json.put("code", -2);
			json.put("msg", "转让标题长度为2~20位字符");
			return json.toString();
		}
		if (StringUtils.isBlank(periodStr)) {
			json.put("code", -2);
			json.put("msg", "转让有效期为1~7天");
			return json.toString();

		} else {
			period = Integer.parseInt(periodStr);
			if (period < 1 || period > 7) {
				json.put("code", -2);
				json.put("msg", "转让有效期为1~7天");
				return json.toString();
			}
		}
		
		if (StringUtils.isBlank(transferPriceStr)) {
			json.put("code", -2);
			json.put("msg", "转让价格不能为空");
			return json.toString();
		}
		transferPrice = Integer.parseInt(transferPriceStr);
		
		ResultInfo result = Security.decodeSign(investIdSign, Constants.INVEST_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			json.put("code", ResultInfo.LOGIN_TIME_OUT);
			json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			return json.toString();
		}
		investId = Long.parseLong((String)result.obj);
		
		result = debtAppService.applyDebtTransfer(investId, title, period, transferPrice);
		if (result.code < 1) {
			json.put("code", -1);
			json.put("msg", result.msg);
			return json.toString();
		}
		
		json.put("code", 1);
		json.put("msg", "债权转让申请成功，请等待审核结果");
		
		return json.toString();
	}
	
}
