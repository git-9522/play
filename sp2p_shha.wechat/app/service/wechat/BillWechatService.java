package service.wechat;

import java.util.List;

import common.utils.Factory;
import dao.wechat.BillWechatDao;
import models.wechat.bean.LoanBillBean;
import models.wechat.bean.LoanBillDetailsBean;
import services.core.BillService;

public class BillWechatService extends BillService{
	protected BillWechatDao billWechatDao;
	
	protected BillWechatService() {
		billWechatDao = Factory.getDao(BillWechatDao.class);
		super.basedao = billWechatDao;
	}
	
	/**
	 * 微信借款账单详情查询
	 *
	 * @param billId  借款账单ID
	 * @return
	 *
	 * @author Songjia
	 * @createDate 2016年5月10日
	 */
	public LoanBillDetailsBean findLoanBillDetails(long billId) {
		return billWechatDao.findLoanBillById(billId);
	}
	
	/**
	 * 微信借款账单列表查询
	 *
	 * @param bidId
	 * @return
	 *
	 * @author Songjia
	 * @createDate 2016年5月9日
	 */
	public List<LoanBillBean> queryAccountLoanBillWx(long bidId) {
		return billWechatDao.queryMyBillInvestFrontWx(bidId);
	}
}
