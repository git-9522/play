package service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.app.bean.BillInfo;
import models.app.bean.BillListBean;
import net.sf.json.JSONObject;
import services.core.BidService;
import services.core.BillService;
import common.utils.Factory;
import dao.LoanAppDao;

public class LoanAppService extends BidService{
	
	private static BillService billService = Factory.getService(BillService.class);
	
	private LoanAppDao loanDao;
	
	private LoanAppService() {
		loanDao = Factory.getDao(LoanAppDao.class);
		super.basedao = loanDao;
	}
	
	/***
	 * 
	 * 借款账单
	 * @param userId 用户id
	 * @param bidId  标id
	 * @return
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-5-11
	 */
	public String listOfLoanBill(long userId, long bidId) {
		
        List<BillListBean> list = loanDao.listOfLoanBill(userId, bidId);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", 1);
        map.put("msg", "查询成功");
        map.put("records", list);
        
        return JSONObject.fromObject(map).toString();
	}
	
	/***
	 * 
	 * 借款账单详情
	 * @param userId 用户id
	 * @param billId 借款账单id
	 * @param bidId  标id
	 * @return
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-5-11
	 */
	public String findLoanBill(long userId, long billId, long bidId) {
		
		BillInfo billInfo = loanDao.findLoanBill(userId, billId);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", 1);
		map.put("msg", "查询成功");
		map.put("billInfo", billInfo);
		int totalPeriod = billService.findBidTotalBillCount(bidId);
		map.put("totalPeriod", totalPeriod);
		
		return JSONObject.fromObject(map).toString();
	}

}
