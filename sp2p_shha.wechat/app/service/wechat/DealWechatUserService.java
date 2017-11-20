package service.wechat;

import common.utils.Factory;
import common.utils.PageBean;
import dao.wechat.DealWechatUserDao;
import models.wechat.bean.DealUserBean;
import models.wechat.bean.DealUserDetailBean;
import services.common.DealUserService;

/**
 * 微信交易记录Service
 *
 * @description 
 *
 * @author Songjia
 * @createDate 2016年5月5日
 */
public class DealWechatUserService extends DealUserService{
	
	protected DealWechatUserDao dealWechatUserDao = Factory.getDao(DealWechatUserDao.class);
	
	protected DealWechatUserService() {
		super.basedao = this.dealWechatUserDao;
	}
	
	
	/**
	 * 微信端交易记录详情查询
	 *
	 * @param dealRecordId 交易记录ID
	 * @return
	 *
	 * @author Songjia
	 * @createDate 2016年5月6日
	 */
	public DealUserDetailBean findDealRecorddetail(long dealRecordId) {
		return dealWechatUserDao.dealRecordDetail(dealRecordId);
	}
	
	/**
	 * 微信端交易记录分页查询
	 *
	 * @param currPage
	 * @param pageSize
	 * @param userId
	 * @return
	 *
	 * @author Songjia
	 * @createDate 2016年5月6日
	 */
	public PageBean<DealUserBean> pageOfWechatDealUser(int currPage, int pageSize, long userId) {
		return dealWechatUserDao.pageOfWechatDealUser(currPage, pageSize, userId);
	}
	

}
