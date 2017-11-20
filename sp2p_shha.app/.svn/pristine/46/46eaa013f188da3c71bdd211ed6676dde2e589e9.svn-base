package service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.shove.Convert;

import models.app.bean.BidInformationBean;
import models.app.bean.BidInvestRecordBean;
import models.app.bean.BidReturnMoneyBean;
import models.app.bean.BidUserInfoBean;
import models.app.bean.InvestBidsBean;
import models.common.entity.t_information;
import models.core.entity.t_addrate_user;
import models.core.entity.t_agencies;
import models.core.entity.t_bid;
import models.core.entity.t_cash_user;
import models.core.entity.t_invest.InvestType;
import models.core.entity.t_product;
import models.core.entity.t_product.PeriodUnit;
import models.core.entity.t_red_packet_user;
import net.sf.json.JSONObject;
import payment.impl.PaymentProxy;
import services.common.InformationService;
import services.core.AgencyService;
import services.core.BidService;
import services.core.CashUserService;
import services.core.InvestService;
import services.core.ProductService;
import services.core.RateService;
import services.core.RedpacketService;
import services.core.RedpacketUserService;
import common.constants.ConfConst;
import common.constants.PaymentConst;
import common.enums.Client;
import common.enums.ServiceType;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.OrderNoUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.StrUtil;
import dao.InvestAppDao;

public class InvestAppService extends InvestService{
	
	private InvestAppDao investAppDao;
	
	private InvestAppService() {
		investAppDao = Factory.getDao(InvestAppDao.class);
	//	super.basedao = investDao;
	}
	
	protected static InvestService investService = Factory.getService(InvestService.class);

	protected static RedpacketUserService redpacketUserService = Factory.getService(RedpacketUserService.class);
	
	protected static InformationService informationService = Factory.getService(InformationService.class);
	
	protected static RateService rateService = Factory.getService(RateService.class);
	
	protected static BidService bidService = Factory.getService(BidService.class);
	
	protected static CashUserService cashUserService = Factory.getService(CashUserService.class);
	
	private static AboutUsService aboutAppService = Factory.getService(AboutUsService.class);

	private static ProductService productService = Factory.getService(ProductService.class);

	private static AgencyService agencyService = Factory.getService(AgencyService.class);
	
	
	/***
	 * 理财产品列表
	 *
	 * @param limit 最多条目数
	 * @return
	 * @throws Exception
	 *
	 * @author huangyunsong
	 * @createDate 2016年6月30日
	 */
	public List<InvestBidsBean> listOfInvestBids (int limit) throws Exception{
		
		PageBean<InvestBidsBean> investBidPage = investAppDao.pageOfInvestBids(1, limit);

		return investBidPage.page;
	}
	
	/***
	 * 
	 * 理财产品列表接口（OPT=311）
	 * @param currPage
	 * @param pageSize
	 * @return
	 * @throws Exception
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-7
	 */
	public String pageOfInvestBids (int currPage,int pageSize) throws Exception{
		
		PageBean<InvestBidsBean> investBidPage = investAppDao.pageOfInvestBids(currPage, pageSize);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", 1);
		map.put("msg", "查询成功!");
		map.put("records", investBidPage.page);
		
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 投标
	 *
	 * @param userId 用户id
	 * @param bidId 标的id
	 * @param investAmt 份数或者金额
	 * @param client 设备
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年4月7日
	 */
	public ResultInfo appInvest(long userId, long bidId, double investAmt, String redPacketIdStr, Client client) {
		ResultInfo resultInfo = new ResultInfo();
		
		t_bid bid = bidService.findByID(bidId);
		if (bid == null) {
			resultInfo.code = -1;
			resultInfo.msg = "糟糕，没有找到该标的";
			
			return resultInfo;
		}
			 
		InvestType investType = client.code == Client.ANDROID.code ? InvestType.ANDROID : InvestType.IOS;
		
		double amt = investAmt;
		//按份数购买时，投资金额=投资份数*每份的金额
		if (t_product.BuyType.PURCHASE_BY_COPY.equals(bid.getBuy_type())) {
			amt = new Double(investAmt).intValue() * bid.min_invest_amount;
		}
		
		long redPacketId = 0 ;
		double redPackeAmt = 0 ;
		if(StrUtil.isNumericPositiveInt(redPacketIdStr)){
			redPacketId = Convert.strToLong(redPacketIdStr, -1) ;
			
			if(redPacketId > 0){
				resultInfo = investService.investRedPacket(userId, redPacketId, investAmt,0,1) ;
				if(resultInfo.code <= 0 ){
					LoggerUtil.info(true, resultInfo.msg);
					
					resultInfo.code = -1;
					resultInfo.msg = resultInfo.msg;
					
					return resultInfo;
				}
				t_red_packet_user redPacketUser = (t_red_packet_user) resultInfo.obj ;
				 
				redPackeAmt =  redPacketUser.amount ;
			}
		}
		
		resultInfo = super.invest(userId, bid, amt, 0,null);
		if (resultInfo.code < 1) {
			resultInfo.code = -1;
			resultInfo.msg = resultInfo.msg;
			
			return resultInfo;
		}
			
		/*if(redPacketId > 0 ){
			int count = redpacketService.updateRedPacketLockTime(redPacketId, t_red_packet_user.RedpacketStatus.RECEIVED.code, t_red_packet_user.RedpacketStatus.IN_USED.code) ;
			if( count <= 0 ){
				LoggerUtil.info(true, "修改红包状态失败");
				
				resultInfo.code = -1;
				resultInfo.msg = "修改红包状态失败";
				
				return resultInfo;
			}
			
		}*/
		
		//业务流水号
		String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.INVEST);
		if (ConfConst.IS_TRUST) { 
			resultInfo = PaymentProxy.getInstance().invest(client.code, investType.code, serviceOrderNo, userId, bid, amt, redPackeAmt, redPacketId);
			if (resultInfo.code < 1) {
				resultInfo.code = -1;
				
				return resultInfo;
			}
			resultInfo.code = 1;
			
			if (ConfConst.CURRENT_TRUST_TYPE.equals(PaymentConst.TRUST_TYPE_FY)) {
				
				resultInfo.msg = "投标成功";
			}else{
				
				resultInfo.msg = "投标请求提交中";
			}
		
			return resultInfo;
		}
		
		if (!ConfConst.IS_TRUST){
			resultInfo = super.doInvest(userId, bidId, amt, serviceOrderNo, "", client.code,investType.code, redPacketId, redPackeAmt, 0L, 0.0, 0L, 0.0);
			if (resultInfo.code < 1) {
				resultInfo.code = -1;
				
				return resultInfo;
			}
		}
		
		resultInfo.code = 1;
		resultInfo.msg = "投标成功";
		
		return resultInfo;
	}
		
	/***
	 * 借款标详情（opt=312）
	 * @param bidId 标id
	 * @return
	 * @throws Exception
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-7
	 */
	public String findInvestBidInformation(long userId, long bidId) throws Exception{
		
		Date now = new Date();
		
		t_bid bid = bidService.findByID(bidId);
		
		t_product product = productService.queryProduct(bid.product_id);
		
		double balance = userFundService.findUserBalance(userId);
		
		BidInformationBean bidInfo = investAppDao.findInvestBidInformation(bidId);
		
		t_agencies agencies = null;
		
		if(bid.is_agency){
			agencies = agencyService.findByID(bid.agency_id);
		}
		
		List<t_red_packet_user> redPacketList = redpacketUserService.queryRedpacketsByUserIdStatus(userId, t_red_packet_user.RedpacketStatus.UNUSED.code);
		
		List<t_addrate_user> ratesList = rateService.queryRateByUserIdStatus(userId, t_addrate_user.RateStatus.UNUSED.code);
		
		List<t_cash_user> cashList = cashUserService.queryCashByUserIdStatus(userId, t_cash_user.CashStatus.UNUSED.code);
		
		t_information investDeal = informationService.findInvestDeal();
		
		String contactUs = aboutAppService.findContactUs();
		JSONObject obj = JSONObject.fromObject(contactUs);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", 1);
		map.put("msg", "查询成功!");
		map.put("is_invest_password", bid.is_invest_password);
		map.put("pro", product.getType().code == t_product.ProductType.NEWBIE.code?true:false);//标类型
		map.put("proName", product.name);//标类型名称
		map.put("bidInfo", JSONObject.fromObject(bidInfo));//标详情
		map.put("agencies", agencies==null?"":agencies);//发布机构
		map.put("balance", balance);//可用余额
		map.put("redPacketList", redPacketList);//红包
		map.put("ratesList", ratesList);//加息券
		map.put("cashList", cashList);//现金券
		map.put("tel", obj.get("companyTel"));//客服电话
		map.put("deal", investDeal);//合同
		map.put("now", now.getTime());//时间
		return JSONObject.fromObject(map).toString();
	}
		
	/***
	 * 借款人详情接口（OPT=322）
	 * @param bidId 标id
	 * @return
	 * @throws Exception
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-7
	 */
	public String findInvestBidDeatils(long bidId) throws Exception{
		BidUserInfoBean bidInfo = investAppDao.findInvestBidsUserInfo(bidId);
		List<Map<String, Object>> listMaps = investAppDao.listOfInvestBidItems(bidId);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", 1);
		map.put("msg", "查询成功!");
		map.put("bidInfo", JSONObject.fromObject(bidInfo));
		map.put("bidItemSupervisor", listMaps);
		
		return JSONObject.fromObject(map).toString();
	}
		
	/***
	 * 投标记录接口（OPT=324）
	 * @param bidId 标id
	 * @return
	 * @throws Exception
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-7
	 */
	public String pageOfInvestBidsRecord(int currPage,int pageSize,long bidId) throws Exception{
		PageBean<BidInvestRecordBean> bidRecord = investAppDao.pageOfInvestBidsRecord(currPage,pageSize,bidId);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", 1);
		map.put("msg", "查询成功!");
		map.put("records", bidRecord.page);
		
		return JSONObject.fromObject(map).toString();
	}
		
	/***
	 * 回款计划接口（OPT=323）
	 * @param bidId 标id
	 * @return
	 * @throws Exception
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-7
	 */
	public String listOfRepaymentBill(long bidId) throws Exception{
		List<BidReturnMoneyBean> bidRecord = investAppDao.listOfRepaymentBill(bidId);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", 1);
		map.put("msg", "查询成功!");
		map.put("records", bidRecord);
		
		
		return JSONObject.fromObject(map).toString();
	}
	
}
