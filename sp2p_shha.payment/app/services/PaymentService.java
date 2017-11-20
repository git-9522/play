package services;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import models.bean.PaymentRequestLogs;
import models.bean.UserFundCheck;
import models.entity.t_payment_call_back;
import models.entity.t_payment_request;

import org.apache.commons.lang.StringUtils;

import payment.impl.PaymentProxy;
import play.mvc.Scope.Params;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import common.constants.RemarkPramKey;
import common.enums.CashType;
import common.enums.Client;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;

import daos.PaymentCallBackDao;
import daos.PaymentRequstDao;
import hf.HfConsts;

public class PaymentService {
	
	protected static PaymentRequstDao paymentRequstDao = Factory.getDao(PaymentRequstDao.class);
	
	protected static PaymentCallBackDao paymentCallBackDao = Factory.getDao(PaymentCallBackDao.class);
	
	/**
	 * 根据请求id查询请求实体
	 *
	 * @param requestId
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月18日
	 */
	public t_payment_request findPaymentRequestById (long requestId) {
		t_payment_request pr = paymentRequstDao.findByID(requestId);
		if (pr == null) {
			LoggerUtil.info(false, "查询托管请求参数时，查询请求记录失败");
			
			return null;
		}
		
		return pr;
	}
	
	/**
	 * 根据请求id查询请求实体
	 *
	 * @param callBackId
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月18日
	 */
	public t_payment_call_back findPaymentCallBackById (long callBackId) {
		t_payment_call_back pcb = paymentCallBackDao.findByID(callBackId);
		if (pcb == null) {
			LoggerUtil.info(false, "托管回调记录不存在");
			
			return null;
		}
		
		return pcb;
	}
	
	/**
	 * 查询托管请求/备注参数
	 *
	 * @param requestMark
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月8日
	 */
	public Map<String, String> queryRequestParams(String requestMark) {
		
		if (StringUtils.isBlank(requestMark)) {
			LoggerUtil.info(false, "查询托管请求备注参数时，requestMark为空");
			
			return null;
		}
		
		t_payment_request pr = paymentRequstDao.findByColumn("mark = ?", requestMark);
		if (pr == null) {
			LoggerUtil.info(false, "查询托管请求备注参数时，查询请求记录失败");
			
			return null;
		}
		
		if (StringUtils.isBlank(pr.req_params)) {
			LoggerUtil.info(false, "查询托管请求备注参数时，备注参数为空");
			
			return null;
		}
		
		Map<String, String> requestPrams = new Gson().fromJson(pr.req_params,
				new TypeToken<LinkedHashMap<String, String>>(){}.getType());
		
		requestPrams.put(RemarkPramKey.SERVICE_ORDER_NO, pr.service_order_no);  //业务订单号，备用
	
		return requestPrams;
	}

	/**
	 * 查询托管回调参数列表
	 *
	 * @param requestId
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月8日
	 */
	public List<t_payment_call_back> queryCallBackList(String requestMark) {
		List<t_payment_call_back> CBList = paymentCallBackDao.findListByColumn("request_mark = ?", requestMark);
		if (CBList == null || CBList.size() <= 0) {

			return null;
		}

		return CBList;
	}
	
	/**
	 * 查询托管回调参数
	 *
	 * @param callBackId
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月8日
	 */
	public Map<String, String> queryCallBackParams(long callBackId) {
		
		t_payment_call_back pcb = paymentCallBackDao.findByID(callBackId);
		if (pcb == null) {
			LoggerUtil.info(false, "查询托管回调记录");
			
			return null;
		}
		
		if (StringUtils.isBlank(pcb.cb_params)) {
			LoggerUtil.info(false, "查询托管回调参数时，回调参数为空");
			
			return null;
		}
		
		Map<String, String> remarkParams = new Gson().fromJson(pcb.cb_params,
				new TypeToken<LinkedHashMap<String, String>>(){}.getType());

		return remarkParams;
	}
	
	/**
	 * 查询托管回调参数，通过请求标识
	 *
	 * @param requestMark
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月8日
	 */
	public Map<String, String> queryFirstCBParamsByMark(String requestMark) {

		t_payment_call_back pcb = paymentCallBackDao.findByColumn("request_mark = ?", requestMark);
		if (pcb == null) {
			LoggerUtil.info(false, "查询托管回调记录");
			
			return null;
		}
		
		if (StringUtils.isBlank(pcb.cb_params)) {
			LoggerUtil.info(false, "查询托管回调参数时，回调参数为空");
			
			return null;
		}
		
		Map<String, String> remarkParams = new Gson().fromJson(pcb.cb_params,
				new TypeToken<LinkedHashMap<String, String>>(){}.getType());
		
		return remarkParams;
	}
	
	/**
	 * 获取资金托管接口参数
	 *
	 * @param params
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月7日
	 */
	public Map<String,String> getRespParams(Params params){
		Map<String,String> paramMap = new HashMap<String, String>();
		String reqparams = null;
		try {
			//如果直接使用params.allSimple()则会出现乱码
			reqparams = URLDecoder.decode(URLDecoder.decode(params.urlEncode(),"UTF-8"),"UTF-8");
			
		} catch (Exception e) {
			
			LoggerUtil.error(false, e, "获取资金托管接口参数异常");
		}
		
		if (reqparams == null) {
			
			return paramMap;
		}
		
		String param[] = reqparams.split("&");
		for (int i = 0; i < param.length; i++) {
			String content = param[i];
			String key = content.substring(0, content.indexOf("="));
			String value = content.substring(content.indexOf("=") + 1, content.length());
			try {
				paramMap.put(key, URLDecoder.decode(value,"UTF-8"));
			} catch (Exception e) {
				
				LoggerUtil.error(false, e, "汇付天下回调构造参数异常");
			}
		}
		
		return paramMap;
	}

	/**
	 * 资金托管请求记录
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
	 * @createDate 2016年1月16日
	 */
	public PageBean<PaymentRequestLogs> pageOfRequestRecord(int showType, int currPage, int pageSize, Integer serviceType, 
			String userName, String serviceOrderNo, String orderNo) {
		/*
		 *  SELECT
				pr.id AS id,
				pr.mark AS mark,
				ui.name AS user_name,
				pr.service_order_no AS service_order_no,
				pr.order_no AS order_no,
				pr.service_type AS service_type,
				pr.pay_type AS pay_type,
				pr.time AS time,
				pr.status AS status
			FROM
				t_payment_request pr
			LEFT JOIN t_user_info ui ON ui.id = pr.user_id
			WHERE 1=1
		 */
		StringBuffer querySQL = new StringBuffer("SELECT pr.id AS id, pr.mark AS mark, ui.name AS user_name, pr.service_order_no AS service_order_no, pr.order_no AS order_no, pr.service_type AS service_type, pr.pay_type AS pay_type, pr.time AS time, pr.status AS status FROM t_payment_request pr LEFT JOIN t_user_info ui ON ui.user_id = pr.user_id WHERE 1=1");
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(1) FROM t_payment_request pr LEFT JOIN t_user_info ui ON ui.user_id = pr.user_id WHERE 1=1");
		
		Map<String, Object> conditionArgs = new HashMap<String, Object>();
		
		if (showType == 1) {
			querySQL.append(" AND pr.status = ").append(t_payment_request.Status.ERROR.code);
			countSQL.append(" AND pr.status = ").append(t_payment_request.Status.ERROR.code);
		}
		if (showType == 2) {
			querySQL.append(" AND pr.status = ").append(t_payment_request.Status.FAILED.code);
			countSQL.append(" AND pr.status = ").append(t_payment_request.Status.FAILED.code);
		}
		
		if (serviceType != null) {
			querySQL.append(" AND pr.service_type = :serviceType");
			countSQL.append(" AND pr.service_type = :serviceType");
			conditionArgs.put("serviceType", serviceType);
		}
		
		if (StringUtils.isNotBlank(userName)) {
			querySQL.append(" AND ui.name like :userName");
			countSQL.append(" AND ui.name like :userName");
			conditionArgs.put("userName", "%"+userName+"%");
		}
		
		if (StringUtils.isNotBlank(serviceOrderNo)) {
			querySQL.append(" AND pr.service_order_no = :serviceOrderNo");
			countSQL.append(" AND pr.service_order_no = :serviceOrderNo");
			conditionArgs.put("serviceOrderNo", serviceOrderNo);
		}
		
		if (StringUtils.isNotBlank(orderNo)) {
			querySQL.append(" AND pr.order_no = :orderNo");
			countSQL.append(" AND pr.order_no = :orderNo");
			conditionArgs.put("orderNo", orderNo);
		}
		 
		querySQL.append(" ORDER BY time DESC"); //按时间降序

		return paymentRequstDao.pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), PaymentRequestLogs.class, conditionArgs);
	}

	/**
	 * 根据回调记录Id查询异步回调地址
	 *
	 * @param callBackId
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月19日
	 */
	public String findCBURLByCBId(long callBackId) {
		String sql = "SELECT pr.ayns_url FROM t_payment_request pr JOIN t_payment_call_back pcb ON pcb.request_mark=pr.mark WHERE pcb.id = :callBackId";
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("callBackId", callBackId);
		
		return paymentCallBackDao.findSingleStringBySQL(sql, "", args);
	}

	/**
	 * 托管资金校对列表
	 *
	 * @param name
	 * @param currPage
	 * @param pageSize
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年2月15日
	 */
	@SuppressWarnings("unchecked")
	public PageBean<UserFundCheck> pageOfUserFundCheck(String userName, int currPage, int pageSize) {
		
		StringBuffer querySQL = new StringBuffer("SELECT user_id AS id, name AS userName, payment_account AS account, balance AS systemBlance, freeze AS systemFreeze FROM t_user_fund WHERE payment_account IS NOT NULL AND payment_account<>''");
	
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(1) FROM t_user_fund WHERE payment_account IS NOT NULL AND payment_account<>''");
		
		Map<String, Object> conditionArgs = new HashMap<String, Object>();
		
		if (StringUtils.isNotBlank(userName)) {
			querySQL.append(" AND name like :userName");
			countSQL.append(" AND name like :userName");
			conditionArgs.put("userName", "%"+userName+"%");
		}
		
		querySQL.append(" ORDER BY id DESC");

		PageBean<UserFundCheck> pageBean = paymentRequstDao.pageOfBeanBySQL(currPage, pageSize, countSQL.toString(),
				querySQL.toString(), UserFundCheck.class, conditionArgs);
		
		List<UserFundCheck> pageList = new ArrayList<UserFundCheck>();
		
		if (pageBean.page != null && pageBean.page.size() != 0){
			for (UserFundCheck ufc : pageBean.page){
				//捕获异常后将余额设为空
				ResultInfo result = PaymentProxy.getInstance().queryFundInfo(Client.PC.code, ufc.account);
				if (result.code < 1) {
					LoggerUtil.error(false, "查询用户资金异常。【userId】", ufc.id);
				}
				Map<String, Object> fundInfo = (Map<String, Object>) result.obj;
				
				if (fundInfo != null){
					ufc.pBlance = Double.valueOf(fundInfo.get("pBlance").toString()); 
					ufc.pFreeze = Double.valueOf(fundInfo.get("pFreeze").toString()); 
				}
				
				pageList.add(ufc);
			}
		}
		
		pageBean.page = pageList;

		return pageBean;
	}
	
	/**
	 * 查询托管请求记录
	 *
	 * @param requestMark
	 * @return
	 *
	 * @author jyq
	 * @createDate 2016年9月23日
	 */
	public t_payment_request queryRequest(String requestMark) {
		
		if (StringUtils.isBlank(requestMark)) {
			LoggerUtil.info(false, "查询托管请求备注参数时，requestMark为空");
			
			return null;
		}
		
		t_payment_request pr = paymentRequstDao.findByColumn("mark = ?", requestMark);
		if (pr == null) {
			LoggerUtil.info(false, "查询托管请求备注参数时，查询请求记录失败");
			
			return null;
		}
		return pr;
	}
	
	/**
	 * 查询托管请求记录
	 *
	 * @param requestMark
	 * @return
	 *
	 * @author jyq
	 * @createDate 2016年9月23日
	 */
		
	public static t_payment_request queryRequestByOrderNo(String orderNo) {
		if (StringUtils.isBlank(orderNo)) {
			LoggerUtil.info(false, "查询托管请求备注参数时，orderNo为空");
			
			return null;
		}
		
		t_payment_request pr = paymentRequstDao.findByColumn("order_no = ?",orderNo);
		if (pr == null) {
			LoggerUtil.info(false, "查询托管请求备注参数时，查询请求记录失败");
			
			return null;
		}
		return pr;
	}
	
	public double queryServFee(double withdrawalAmt, String cashType) {
		double fixedAmount = 2.5;
		double withdrawalFee = 0;
		Date specDate = DateUtil.strToDate("20170929", "yyyyMMdd");
		Date now = new Date();
		if(!HfConsts.USER_PAY_WITHDRAW_FEE) {
			// 商户收取服务费
			// withdrawalFee = FeeCalculateUtil.getWithdrawalFee(withdrawalAmt);
			if(cashType.equals(CashType.FAST.code)) {
				int currentHour = DateUtil.getCurrentHour();
				if(currentHour > 14) {
					// 超过14点
					cashType = CashType.GENERAL.code;
				} else if(currentHour == 14 && DateUtil.getCurrentMinute() >= 30) {
					// 14点，超过30分
					cashType = CashType.GENERAL.code;
				} else {
					// 不到14：30
					try {
						int days = DateUtil.getDaysDiffFloor(specDate, now);
						if(days <= 1) {
							cashType = CashType.IMMEDIATE.code;
						} else if(days > 9) {
							// 10.8之后
							int currentDay = DateUtil.getCurrentDayOfWeek();
							if(currentDay > 5) {
								// 非工作日
								cashType = CashType.GENERAL.code;
							} else {
								// 工作日
								cashType = CashType.IMMEDIATE.code;
							}
						} else {
							cashType = CashType.GENERAL.code;
						}
					} catch (Exception e) {
						// 9.29之前
						int currentDay = DateUtil.getCurrentDayOfWeek();
						if(currentDay > 5) {
							// 非工作日
							cashType = CashType.GENERAL.code;
						} else {
							// 工作日
							cashType = CashType.IMMEDIATE.code;
						}
					}
				}
			}  
			if(cashType.equals(CashType.GENERAL.code)) {
				// 固定值
				withdrawalFee = fixedAmount; 
			} 
			if(cashType.equals(CashType.IMMEDIATE.code)) {
				boolean flag = false;
				try {
					int days = DateUtil.getDaysDiffFloor(specDate, now);
					if(days > 9) {
						flag = true;
					} else if(days == 0) {
						withdrawalFee = fixedAmount + withdrawalAmt * 0.0005;
					} else {
						withdrawalFee = fixedAmount + withdrawalAmt * 0.0005 * (9 - days + 1);
					}
				} catch (Exception e) {
					// 出现异常表明，在正常逻辑范围内
					flag = true;
				}
				if(flag) {
					int currentDay = DateUtil.getCurrentDayOfWeek();
					if(currentDay > 4) {
						withdrawalFee = fixedAmount + withdrawalAmt * 0.0005 * (DateUtil.DAYS_IN_A_WEEK - currentDay + 1);
					} else {
						withdrawalFee = fixedAmount + withdrawalAmt * 0.0005;
					}
				}
			}
		}
		return withdrawalFee;
	}
}
