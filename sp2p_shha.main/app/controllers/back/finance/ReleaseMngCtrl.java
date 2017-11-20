package controllers.back.finance;

import java.io.File;
import java.util.Date;
import java.util.List;

import models.core.bean.BackFinanceBid;
import models.core.entity.t_bid;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.commons.lang.StringUtils;

import payment.impl.PaymentProxy;
import services.core.BidService;

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
 * 后台-财务-财务放款-控制器
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2015年12月19日
 */
public class ReleaseMngCtrl extends BackBaseController {
	
	protected static BidService bidservice = Factory.getService(BidService.class);
	
	/**
	 * 后台-财务-财务放款-财务放款列表
	 * @rightID 501001
	 *
	 * @param showType 筛选类型  0-所有;1-待放款;2-已放款(还款中,已还款)
	 * @param currPage 当前页
	 * @param pageSize 每页条数
	 * @param exports 1：导出  default:不导出
	 * @param loanName 借款人昵称
	 * @param orderType 排序栏目  0：编号   2：借款金额  7：放款时间
	 * @param orderValue 排序规则 0,降序，1,升序
	 *
	 * @author yaoyi
	 * @createDate 2015年12月23日
	 */
	public static void showReleaseListPre(int showType, int currPage, int pageSize){
		int exports = Convert.strToInt(params.get("exports"), 0);
		String loanName = params.get("loanName");
		
		if (showType < 0 || showType > 2) {
			showType = 0;
		}
		
		//排序栏目
		String orderTypeStr = params.get("orderType");
		int orderType = Convert.strToInt(orderTypeStr, 0);// 0：编号   2：借款金额  7：放款时间
		if(orderType != 0 && orderType  != 2 && orderType  != 7){
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
		
		PageBean<BackFinanceBid> pageBean= bidservice.pageOfBidFinance(showType, currPage, pageSize,exports,loanName,orderType,orderValue);
		
		//导出
		if(exports == Constants.EXPORT){
			List<BackFinanceBid> list = pageBean.page;
			 
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(Constants.DATE_FORMATE));
			jsonConfig.registerJsonValueProcessor(Double.class, new JsonDoubleValueProcessor(Constants.FINANCE_FORMATE_NORMAL));
			JSONArray arrList = JSONArray.fromObject(list, jsonConfig);
			
			for (Object obj : arrList) {
				JSONObject bid = (JSONObject)obj;	
				
				if(StringUtils.isNotBlank(bid.getString("status"))){
					bid.put("status", t_bid.Status.valueOf(bid.get("status").toString()).value);
				}
			}
			
			String fileName=null;
			switch (showType) {
				case 1:
					fileName = "待放款财务放款列表";
					break;
				case 2:
					fileName = "已放款财务放款列表";
					break;
				default:
					fileName = "财务放款列表";
					break;
			}
						
			File file = ExcelUtils.export(fileName,
			arrList,
			new String[] {
			"编号", "项目", "项目金额", "借款人", "初审管理员", "满标审管理员", "放款", "状态"},
			new String[] {
			"bid_no","title", "amount", "name", "pre_audit_supervisor", 
			"audit_supervisor", "release_supervisor","status"});
			   
			renderBinary(file, fileName + ".xls");
		}
		
		//统计总额 
		double totalAmt = bidservice.findTotalBidAmountFinance(showType,loanName);	

		render(pageBean, totalAmt, showType, loanName);
	}
	
	/**
	 * 财务放款
	 * @rightID 501002
	 *
	 * @param bidSign
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月21日
	 */
	public static void release(String bidSign){
		ResultInfo result = Security.decodeSign(bidSign, Constants.BID_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			error404();
		}
		
		long bidId = Long.parseLong(result.obj.toString());
		
		t_bid bid = bidservice.findByID(bidId);
		if (bid == null) {
			flash.error("借款标不存在");
			
			showReleaseListPre(0, 1, 10);
		}
		
		long supervisorId = getCurrentSupervisorId();
		
		result = bidservice.release(bid, supervisorId);
		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
			flash.error(result.msg);
			
			showReleaseListPre(0, 1, 10);
		}
		
		String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.BID_AUDIT_SUCC);
		
		if (ConfConst.IS_TRUST) {
			result = PaymentProxy.getInstance().bidSuditSucc(Client.PC.code, serviceOrderNo, supervisorId, bid);
			if (result.code < 1) {
				LoggerUtil.info(true, result.msg);
				flash.error(result.msg);
			} else {
				flash.success("放款成功,已将项目置为[还款中]!");
			}
			
			showReleaseListPre(0, 1, 10);
		}
		
		if (!ConfConst.IS_TRUST){
			
			result = bidservice.doRelease(bidId, supervisorId, serviceOrderNo);
			if (result.code < 1) {
				LoggerUtil.info(true, result.msg);
				flash.error(result.msg);
			} else {
				flash.success("放款成功,已将项目置为[还款中]!");
			}
		}
		
		showReleaseListPre(0, 1, 10);
	}
	
}
