package service.wechat;

import java.util.List;

import common.utils.Factory;
import common.utils.PageBean;
import dao.wechat.BillWechatInvestDao;
import models.wechat.bean.InvestBillBean;
import models.wechat.bean.InvestBillDetailsBean;
import models.wechat.bean.ReceiveBillDetailBean;
import models.wechat.bean.ReceiveBillPlanBean;
import services.core.BillInvestService;

/**
 * 微信理财账单service
 *
 * @description
 *
 * @author Songjia
 * @createDate 2016年5月11日
 */
public class BillWechatInvestService extends BillInvestService{
	protected BillWechatInvestDao billWechatInvestDao;
	
	protected BillWechatInvestService() {
		billWechatInvestDao = Factory.getDao(BillWechatInvestDao.class);
		super.basedao = billWechatInvestDao;
	}
	
	/**
	 * 查询理财账单详情
	 *
	 * @param investBillId  理财账单ID
	 * @return
	 *
	 * @author Songjia
	 * @createDate 2016年5月10日
	 */
	public InvestBillDetailsBean findInvestBillDetails(long investBillId) {
		return billWechatInvestDao.findInvestBillById(investBillId);
	}
	
	/***
	 * 账户中心 回款计划 的账单详情
	 * @param billId 理财账单ID
	 * @return
	 *
	 * @author luzhiwei
	 * @createDate 2016-05-10
	 */
	public ReceiveBillDetailBean  findInvestReceiveBillDeatil(long billId) {
		
		return billWechatInvestDao.findInvestReceiveBillDeatil(billId);
	}
	
	/**
	 * 不分页查询 ，用户该笔投资的理财账单
	 *
	 * @param investId  投资记录ID
	 * @return
	 *
	 * @author Songjia
	 * @createDate 2016年5月11日
	 */
	public List<InvestBillBean> queryAccountInvestBillWx(long investId) {
		return billWechatInvestDao.queryMyBillInvestFrontWx(investId);
	}
	
	/***
	 * 
	 * 账户中心 回款计划
	 * @param userId 用户ID
	 * @param currPage
	 * @param pageSize
	 * @return
	 *
	 * @author luzhiwei
	 * @createDate 2016-05-10
	 */
	public PageBean<ReceiveBillPlanBean>  pageOfInvestReceiveBill(long userId, int currPage, int pageSize) {
		
		return billWechatInvestDao.pageOfInvestReceiveBill(userId,  currPage,  pageSize);
	}
	
}
