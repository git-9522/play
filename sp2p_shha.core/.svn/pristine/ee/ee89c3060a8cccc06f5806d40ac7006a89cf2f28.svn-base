package services.core;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.shove.Convert;

import common.FeeCalculateUtil;
import common.enums.NoticeScene;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.NoUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.number.Arith;
import common.utils.number.NumberFormat;
import daos.core.BillDao;
import models.common.entity.t_deal_platform;
import models.common.entity.t_deal_user;
import models.common.entity.t_event_supervisor;
import models.common.entity.t_user_fund;
import models.core.bean.Bill;
import models.core.entity.t_bid;
import models.core.entity.t_bill;
import models.core.entity.t_bill_invest;
import services.base.BaseService;
import services.common.DealPlatformService;
import services.common.DealUserService;
import services.common.NoticeService;
import services.common.SupervisorService;
import services.common.UserFundService;
import services.common.UserService;

/**
 * 借款账单
 *
 * @author liudong
 * @createDate 2015年12月18日
 */
public class BillService extends BaseService<t_bill> {

	protected BillDao billDao = Factory.getDao(BillDao.class);
	
	protected static UserService userService = Factory.getService(UserService.class);
	
	protected static UserFundService userFundService = Factory.getService(UserFundService.class);
	
	protected static DealUserService dealUserService = Factory.getService(DealUserService.class);
	
	protected static DealPlatformService dealPlatformService = Factory.getService(DealPlatformService.class);
	
	protected static NoticeService noticeService = Factory.getService(NoticeService.class);

	protected static BidService bidService = Factory.getService(BidService.class);
	
	protected static BillInvestService billInvestService = Factory.getService(BillInvestService.class);
	
	protected static SupervisorService supervisorService = Factory.getService(SupervisorService.class);
	
	protected static DebtService debtService = Factory.getService(DebtService.class);
	
	protected BillService() {
		super.basedao = billDao;
	}
	
	/**
	 * 保存账单
	 *
	 * @param tbill
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月24日
	 */
	public boolean createBill(t_bill tbill){
		
		return billDao.save(tbill);
	}
	
	/**
	 * 回款计划
	 *
	 * @param bidId 标的id
	 *
	 * @author liudong
	 * @createDate 2015年12月18日
	 */
	public List<t_bill> findBillByBidId(long bidId) {
		List<t_bill> bills = billDao.findBillByBidId(bidId);
		
		return bills;
	}
	
	/**
	 * 标的的总账单数
	 *
	 * @param bidId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月20日
	 */
	public int findBidTotalBillCount(long bidId){
		return Convert.strToInt(String.valueOf(billDao.queryBillCount(bidId, 0, false).get("period")), 0);
	}

	/**
	 * 统计借款账单 应还本金利息合计
	 *
	 * @param showType  default:所有        1:待还(正常待还+逾期待还+本息垫付待还) 2:逾期待还(待还+逾期) 3:已还(正常还款、本息垫付还款 、线下收款、本息垫付后线下收款 )
	 * @param loanName 借款人昵称
	 * @param projectName 项目
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年1月19日
	 */
	public double findTotalBillAmount(int showType, String loanName, String projectName) {
		
		return billDao.findTotalBillAmount(showType,loanName,projectName);
	}

	
	/**
	 * 分页查询  借款账单
	 *
	 * @param showType default:所有    1:待还(正常待还+本息垫付待还) 2:逾期待还(待还+逾期) 3:已还(正常还款、本息垫付还款 、线下收款、本息垫付后线下收款 )
	 * @param exports 1：导出  default：不导出
	 * @param loanName 借款人昵称
	 * @param orderType 排序栏目  0：编号   3：账单金额    5：逾期时长   6：到期时间    7：还款时间
	 * @param orderValue 排序规则 0,降序，1,升序
	 * @param projectName 项目
	 *
	 * @author liudong
	 * @createDate 2015年12月18日
	 */
	public PageBean<Bill> pageOfBillBack(int showType,int currPage, int pageSize, int exports, String loanName, int orderType, int orderValue, String projectName) {
		
		return billDao.pageOfBillBack(showType,currPage, pageSize, exports, loanName, orderType, orderValue, projectName);
	}
	
	/**
	 * 标的还款计划
	 *
	 * @param currPage 当前页  
	 * @param pageSize 每页的条数 
	 * @param bidId 标的id
	 * @return 
	 * 
	 * @author liudong
	 * @createDate 2016年12月30日
	 */
	public PageBean<Map<String, Object>> pageOfRepaymentBill(int currPage,int pageSize, long bidId) {
		
		return billDao.pageOfRepaymentBill(currPage, pageSize, bidId);
	}
	
	/**
	 * 根据标的ID查询理财账单
	 *
	 * @param bidId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月22日
	 */
	public List<t_bill> queryBidBill(long bidId) {
		
		Map<String, Object>params = new HashMap<String, Object>();
		params.put("bid_id", bidId);
		
		return billDao.findListByColumn("bid_id=:bid_id", params);
	}
	
	/**
	 * 查询待还总额
	 * @return
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年4月26日
	 *
	 */
	public double queryTotalNoRepaymentAmount() {

		return billDao.findTotalNoRepaymentAmount();
	}
	
	/**
	 * 到期还款率  (到期还款率=（账单数-逾期待还账单数）/待还账单数) 保留三位小数
	 *
	 * @author liudong
	 * @createDate 2015年12月23日
	 */
	public double queryExpireRepaymentRate() {
		double repayrate = billDao.queryExpireRepaymentRate();
		return Double.parseDouble(NumberFormat.round(repayrate, 3));
	}

	/**
	 * 本息垫付（准备）
	 *
	 * @param bill
	 * @param supervisorId 管理员ID
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月25日
	 */
	public ResultInfo principalAdvance(t_bill bill, long supervisorId) {
		ResultInfo result = new ResultInfo();
		
		if (bill == null) {
			throw new InvalidParameterException("bill is null");
		}
		
		/** 校验账单状态  */
		if (!t_bill.Status.NORMAL_NO_REPAYMENT.equals(bill.getStatus())) {
			result.code = -1;
			result.msg = "本期账单不在本息垫付范围内";
			
			return result;
		}
		
		List<t_bill_invest> billInvestList = billInvestService.queryNOReceiveInvestBills(bill.bid_id, bill.period);
		if (billInvestList == null || billInvestList.size() <= 0) {
			result.code = -1;
			result.msg = "查询待还的理财账单失败";
			
			return result;
		}
		
		t_bid bid = bidService.findByID(bill.bid_id);
		if (bid == null) {
			result.code = -1;
			result.msg = "获取借款标信息失败";
			
			return result;
		}
		
		/** 结算理财服务费和逾期罚息费  */
		List<Map<String, Double>> billInvestFeeList = new ArrayList<Map<String,Double>>();
		Map<String, Double> billInvestFee = null;
		for (t_bill_invest billInvest : billInvestList) {
			double investServiceFee = FeeCalculateUtil.getInvestManagerFee(billInvest.receive_interest, bid.service_fee_rule,billInvest.user_id);
			billInvestFee = new HashMap<String, Double>();
			billInvestFee.put("investServiceFee", investServiceFee);
			billInvestFee.put("overdueFine", billInvest.overdue_fine);
			
			billInvestFeeList.add(billInvestFee);
		}
		
		/** 添加管理员事件　*/
		Map<String, String> supervisorEventParam = new HashMap<String, String>();
		supervisorEventParam.put("bill_no ", bill.bill_no);
		supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.BILL_PRINCIPAL, supervisorEventParam);
		
		result.code = 1;
		result.msg = "本息垫付准备完毕";
		result.obj = billInvestFeeList;
		
		return result;
	}

	/**
	 * 正常还款（准备）
	 *
	 * @param userId 用户ID
	 * @param bill 借款账单
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月28日
	 */
	public ResultInfo normalRepayment(long userId, t_bill bill) {
		ResultInfo result = new ResultInfo();
		
		if (bill == null) {
			throw new InvalidParameterException("bill is null");
		}
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		
		if (userFund == null) {
			result.code = -1;
			result.msg = "获取用户资金信息失败";
			
			return result;
		}
		
		/** 用户资金是否出现异常变动 */
		result = userFundService.userFundSignCheck(userId);
		if (result.code < 1) {
			
			return result;
		}
		
		List<t_bill_invest> billInvestList = billInvestService.queryNOReceiveInvestBills(bill.bid_id, bill.period);
		if (billInvestList == null || billInvestList.size() <= 0) {
			result.code = -1;
			result.msg = "查询待还的理财账单失败";
			
			return result;
		}
		
		double payAmt = 0;
		
		for(t_bill_invest billInvest : billInvestList) {
			payAmt = Arith.add(Arith.add(Arith.add(billInvest.receive_corpus, billInvest.receive_interest), billInvest.overdue_fine), payAmt);
		}
		
		/** 借款人资金是否充足 */
		// double payAmt =  Arith.add(Arith.add(bill.repayment_corpus, bill.repayment_interest), bill.overdue_fine);
		if (userFund.balance < payAmt) {
			result.code = -1;
			result.msg = "余额不足";
			
			return result;
		}
		
		t_bid bid = bidService.findByID(bill.bid_id);
		if (bid == null) {
			result.code = -1;
			result.msg = "获取借款标信息失败";
			
			return result;
		}
		
		/** 结算理财服务费和逾期罚息费  */
		List<Map<String, Double>> billInvestFeeList = new ArrayList<Map<String,Double>>();
		Map<String, Double> billInvestFee = null;
		for (t_bill_invest billInvest : billInvestList) {
			double investServiceFee = FeeCalculateUtil.getInvestManagerFee(billInvest.receive_interest, bid.service_fee_rule, billInvest.user_id);
			billInvestFee = new HashMap<String, Double>();
			billInvestFee.put("investServiceFee", investServiceFee);
			billInvestFee.put("overdueFine", billInvest.overdue_fine);
			
			billInvestFeeList.add(billInvestFee);
		}
		
		result.code = 1;
		result.msg = "还款准备完毕";
		result.obj = billInvestFeeList;
		
		return result;
	}
	
	
	/**
	 * 线下收款（准备）
	 *
	 * @param userId 用户ID
	 * @param bill 借款账单
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月28日
	 */
	public ResultInfo offlineReceive(long supervisorId, t_bill bill) {
		ResultInfo result = new ResultInfo();
		
		if (bill == null) {
			throw new InvalidParameterException("bill is null");
		}
		
		List<t_bill_invest> billInvestList = billInvestService.queryNOReceiveInvestBills(bill.bid_id, bill.period);
		if (billInvestList == null || billInvestList.size() <= 0) {
			result.code = -1;
			result.msg = "查询待还的理财账单失败";
			
			return result;
		}
		
		t_bid bid = bidService.findByID(bill.bid_id);
		if (bid == null) {
			result.code = -1;
			result.msg = "获取借款标信息失败";
			
			return result;
		}
		
		/** 结算理财服务费和逾期罚息费  */
		List<Map<String, Double>> billInvestFeeList = new ArrayList<Map<String,Double>>();
		Map<String, Double> billInvestFee = null;
		for (t_bill_invest billInvest : billInvestList) {
			double investServiceFee = FeeCalculateUtil.getInvestManagerFee(billInvest.receive_interest, bid.service_fee_rule,billInvest.user_id);
			billInvestFee = new HashMap<String, Double>();
			billInvestFee.put("investServiceFee", investServiceFee);
			billInvestFee.put("overdueFine", billInvest.overdue_fine);
			
			billInvestFeeList.add(billInvestFee);
		}
		
		/** 添加管理员事件 */
		Map<String, String> supervisorEventParam = new HashMap<String, String>();
		supervisorEventParam.put("bill_no ", bill.bill_no);
		supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.BILL_OFFLINE, supervisorEventParam);
		
		result.code = 1;
		result.msg = "线下收款准备完毕";
		result.obj = billInvestFeeList;
		
		return result;
	}
	
	/**
	 * 本息垫付还款（准备）
	 *
	 * @param userId 用户ID
	 * @param bill 借款账单
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月28日
	 */
	public ResultInfo advanceRepayment(long userId, t_bill bill) {
		ResultInfo result = new ResultInfo();
		
		if (bill == null) {
			throw new InvalidParameterException("bill is null");
		}
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		
		if (userFund == null) {
			result.code = -1;
			result.msg = "获取用户资金信息失败";
			
			return result;
		}
		
		/** 用户资金是否出现异常变动 */
		result = userFundService.userFundSignCheck(userId);
		if (result.code < 1) {
			
			return result;
		}
		
		/** 借款人资金是否充足 */
		double payAmt =  Arith.add(Arith.add(bill.repayment_corpus, bill.repayment_interest), bill.overdue_fine);
		if (userFund.balance < payAmt) {
			result.code = -1;
			result.msg = "余额不足";
			
			return result;
		}

		result.code = 1;
		result.msg = "还款准备完毕";
		
		return result;
	}
	
	/**
	 * 本息垫付（执行）
	 *
	 * @param billId
	 * @param serviceOrderNo
	 * @param billInvestFeeList
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月25日
	 */
	public ResultInfo doPrincipalAdvance(long billId, String serviceOrderNo, List<Map<String, Double>> billInvestFeeList) {
		ResultInfo result = new ResultInfo();
		
		t_bill bill = findByID(billId);
		if (bill == null) {
			result.code = -1;
			result.msg = "该借款账单不存在";
			
			return result;
		}
		
		//判断还款的借款标有没有债权在转让，如果有，将其状态回归原态,并解冻竞拍者资金
		result = debtService.queryDebtTransferNoComplete(bill.bid_id);
		if(result.code < 1){
			
			return result;
		}
		

		List<t_bill_invest> billInvestList = billInvestService.queryNOReceiveInvestBills(bill.bid_id, bill.period);
		if (billInvestList == null || billInvestList.size() <= 0) {
			result.code = -1;
			result.msg = "查询待还的理财账单失败";
			
			return result;
		}
		
		/** 垫付罚息 */
		double advanceOverdueFine = 0;

		/** 理财人收款 */
		for (int i = 0; i < billInvestList.size(); i++) {
			t_bill_invest billInvest = billInvestList.get(i);

			// 更新理财账单收款情况
			boolean receive = billInvestService.updateReceiveData(billInvest.id);
			if (!receive) {
				result.code = -1;
				result.msg = "理财人收到回款，更新理财账单回款数据失败";
				
				return result;
			}
			
			boolean isSignSuccess = true;
			result = userFundService.userFundSignCheck(billInvest.user_id);
			if (result.code < 1) {
				isSignSuccess = false;
			}
			
			//理财服务费
			double investServiceFee = billInvestFeeList.get(i).get("investServiceFee");  
			//逾期罚息
			double overdueFine = billInvestFeeList.get(i).get("overdueFine"); 
		    //理财人应收金额
//			double receiveAmt = billInvest.receive_corpus + billInvest.receive_interest + overdueFine - investServiceFee; 
			double receiveAmt =Arith.sub(Arith.add(Arith.add(billInvest.receive_corpus ,billInvest.receive_interest) ,overdueFine), investServiceFee);
			
			boolean addFund = userFundService.userFundAdd(billInvest.user_id, receiveAmt);
			if (!addFund) {
				result.code = -1;
				result.msg = "增加理财人可用余额失败";
				
				return result;
			}
			
			//刷新理财人资金信息
			t_user_fund investUserFund = userFundService.queryUserFundByUserId(billInvest.user_id);
			if (investUserFund == null) {
				result.code = -1;
				result.msg = "获取理财资金信息失败";
				
				return result;
			}
			
			//如果投资用户账户资金没有遭到非法改动，那么就更新其篡改标志，否则不更新
			if(isSignSuccess){
				userFundService.userFundSignUpdate(billInvest.user_id);
			}

			//添加理财人收款记录
			Map<String, String> summaryParam = new HashMap<String, String>();
			summaryParam.put("billInvestNo", billInvest.bill_invest_no);
			boolean addDeal = dealUserService.addDealUserInfo(
					serviceOrderNo,
					billInvest.user_id, 
					billInvest.receive_corpus + billInvest.receive_interest, 
					investUserFund.balance + investServiceFee - overdueFine,   //此时不计“理财服务费”和“逾期罚息”
					investUserFund.freeze, 
					t_deal_user.OperationType.RECEIVE,
					summaryParam);
			if (!addDeal) {
				result.code = -1;
				result.msg = "添加理财人收款记录失败";
				
				return result;
			}
			
			//添加逾期罚息记录
			if (overdueFine > 0) {
				addDeal = dealUserService.addDealUserInfo(
						serviceOrderNo,
						billInvest.user_id, 
						overdueFine, 
						investUserFund.balance + investServiceFee,   //此时不计“理财服务费”
						investUserFund.freeze, 
						t_deal_user.OperationType.RECEIVE_OVERDUE_FINE,
						summaryParam);
				if (!addDeal) {
					result.code = -1;
					result.msg = "添加逾期罚息记录失败";
					
					return result;
				}
			}
			
			//添加扣除理财服务费记录
			addDeal = dealUserService.addDealUserInfo(
					serviceOrderNo,
					billInvest.user_id, 
					investServiceFee, 
					investUserFund.balance, 
					investUserFund.freeze, 
					t_deal_user.OperationType.INVEST_SERVICE_FEE,
					summaryParam);
			if (!addDeal) {
				result.code = -1;
				result.msg = "添加扣除理财服务费记录失败";
				
				return result;
			}
			
			//添加平台收支记录
			Map<String, Object> dealRemarkParam = new HashMap<String, Object>();
			dealRemarkParam.put("bill_invest_no", billInvest.bill_invest_no);
			addDeal = dealPlatformService.addPlatformDeal(
					billInvest.user_id,
					investServiceFee, 
					t_deal_platform.DealRemark.INVEST_FEE,
					dealRemarkParam);
 			if (!addDeal) {
				result.code = -1;
				result.msg = "添加平台收支记录失败";
				
				return result;
			}
 			
 			//通知理财人
 			Map<String, Object> sceneParame = new HashMap<String, Object>();
 			sceneParame.put("user_name", investUserFund.name);
 			sceneParame.put("bill_no", billInvest.bill_invest_no);
 			sceneParame.put("amount", billInvest.receive_corpus + billInvest.receive_interest + overdueFine);
 			sceneParame.put("principal", billInvest.receive_corpus);
 			sceneParame.put("interest", billInvest.receive_interest);
 			sceneParame.put("fee", investServiceFee);
 			sceneParame.put("balance", receiveAmt);
 			noticeService.sendSysNotice(billInvest.user_id, NoticeScene.INVEST_SECTION, sceneParame);

 			advanceOverdueFine += overdueFine;
		}
		
		// 更新借款账单还款情况
		boolean repayment = updateAdvanceData(bill.id);
		if (!repayment) {
			result.code = -1;
			result.msg = "本息垫付成功，更新借款账单还款数据失败";
			LoggerUtil.error(true, "本息垫付成功，更新借款账单还款数据失败。");
			
			return result;
		}
		
		//本息垫付，添加平台收支记录
		Map<String, Object> dealRemarkParam = new HashMap<String, Object>();
		dealRemarkParam.put("bill_no", bill.bill_no);
		boolean addDeal = dealPlatformService.addPlatformDeal(
				bill.user_id,
				bill.repayment_corpus + bill.repayment_interest, 
				t_deal_platform.DealRemark.ADVANCE_PRIN_INTER,
				dealRemarkParam);
		if (!addDeal) {
			result.code = -1;
			result.msg = "添加平台本息垫付收支记录失败";
			
			return result;
		}
		
		//罚息垫付，添加平台收支记录
		if (advanceOverdueFine > 0) {
			addDeal = dealPlatformService.addPlatformDeal(
					bill.user_id,
					advanceOverdueFine, 
					t_deal_platform.DealRemark.OVERDUE_INTEREST,
					dealRemarkParam);
			if (!addDeal) {
				result.code = -1;
				result.msg = "添加平台罚息垫付收支记录失败";
				
				return result;
			}
		}
		
		result.code = 1;
		result.msg = "垫付成功";
		
		return result;
	}

	/**
	 * 正常还款（执行）
	 * 
	 * @param billId 借款账单ID
	 * @param overdueFeeList 逾期罚息列表（理财账单）
	 * @param serviceOrderNo 业务订单号
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月28日
	 */
	public ResultInfo doRepayment(long billId, List<Map<String, Double>> billInvestFeeList, String serviceOrderNo) {
		ResultInfo result = new ResultInfo();
		
		t_bill bill = findByID(billId);
		if (bill == null) {
			result.code = -1;
			result.msg = "该借款账单不存在";
			
			return result;
		}
		
		//判断还款的借款标是否有未完成的债权转让，如果有，将其状态回归原态,并解冻竞拍者资金
		result = debtService.queryDebtTransferNoComplete(bill.bid_id);
		if(result.code < 1){
			
			return result;
		}

		List<t_bill_invest> billInvestList = billInvestService.queryNOReceiveInvestBills(bill.bid_id, bill.period);
		if (billInvestList == null || billInvestList.size() <= 0) {
			result.code = -1;
			result.msg = "查询待还的理财账单失败";
			
			return result;
		}
		
		/** 借款人扣除的逾期罚息 */
		double loanOverdueFine = 0;  //借款人扣除的逾期罚息 = 理财人收取的逾期罚息之和 
		
		/** 理财人收款 */
		for (int i = 0; i < billInvestList.size(); i++) {
			t_bill_invest billInvest = billInvestList.get(i);

			// 更新理财账单收款情况
			boolean receive = billInvestService.updateReceiveData(billInvest.id);
			if (!receive) {
				result.code = -1;
				result.msg = "理财人收到回款，更新理财账单回款数据失败";
				
				return result;
			}
			
			//理财服务费
			double investServiceFee = billInvestFeeList.get(i).get("investServiceFee");  
			//逾期罚息
			double overdueFine = billInvestFeeList.get(i).get("overdueFine"); 
		    //理财人应收金额
//			double receiveAmt = billInvest.receive_corpus + billInvest.receive_interest + overdueFine - investServiceFee; 
			double receiveAmt =Arith.sub(Arith.add(Arith.add(billInvest.receive_corpus ,billInvest.receive_interest) ,overdueFine), investServiceFee);
			
			boolean isSignSuccess = true;  //用户签名是否通过
			result = userFundService.userFundSignCheck(billInvest.user_id);
			if (result.code < 1) {
				isSignSuccess = false;
			}
			
			boolean addFund = userFundService.userFundAdd(billInvest.user_id, receiveAmt);
			if (!addFund) {
				result.code = -1;
				result.msg = "增加理财人可用余额失败";
				
				return result;
			}
			
			//刷新理财人资金信息
			t_user_fund investUserFund = userFundService.queryUserFundByUserId(billInvest.user_id);
			if (investUserFund == null) {
				result.code = -1;
				result.msg = "获取理财资金信息失败";
				
				return result;
			}
			
			//如果投资用户账户资金没有遭到非法改动，那么就更新其篡改标志，否则不更新
			if(isSignSuccess){
				userFundService.userFundSignUpdate(billInvest.user_id);
			}

			//添加理财人收款记录
			Map<String, String> summaryParam = new HashMap<String, String>();
			summaryParam.put("billInvestNo", billInvest.bill_invest_no);
			boolean addDeal = dealUserService.addDealUserInfo(
					serviceOrderNo,
					billInvest.user_id, 
					billInvest.receive_corpus + billInvest.receive_interest, 
					investUserFund.balance + investServiceFee - overdueFine,   //此时不计“理财服务费”和“逾期罚息”
					investUserFund.freeze, 
					t_deal_user.OperationType.RECEIVE,
					summaryParam);
			if (!addDeal) {
				result.code = -1;
				result.msg = "添加理财人收款记录失败";
				
				return result;
			}
			
			//添加逾期罚息记录
			if (overdueFine > 0) {
				addDeal = dealUserService.addDealUserInfo(
						serviceOrderNo,
						billInvest.user_id, 
						overdueFine, 
						investUserFund.balance + investServiceFee,   //此时不计“理财服务费”
						investUserFund.freeze, 
						t_deal_user.OperationType.RECEIVE_OVERDUE_FINE,
						summaryParam);
				if (!addDeal) {
					result.code = -1;
					result.msg = "添加逾期罚息记录失败";
					
					return result;
				}
			}
			
			//添加扣除理财服务费记录
			addDeal = dealUserService.addDealUserInfo(
					serviceOrderNo,
					billInvest.user_id, 
					investServiceFee, 
					investUserFund.balance, 
					investUserFund.freeze, 
					t_deal_user.OperationType.INVEST_SERVICE_FEE,
					summaryParam);
			if (!addDeal) {
				result.code = -1;
				result.msg = "添加扣除理财服务费记录失败";
				
				return result;
			}
			
			//添加平台收支记录
			Map<String, Object> dealRemarkParam = new HashMap<String, Object>();
			dealRemarkParam.put("bill_invest_no", billInvest.bill_invest_no);
			addDeal = dealPlatformService.addPlatformDeal(
					billInvest.user_id,
					investServiceFee, 
					t_deal_platform.DealRemark.INVEST_FEE,
					dealRemarkParam);
 			if (!addDeal) {
				result.code = -1;
				result.msg = "添加平台收支记录失败";
				
				return result;
			}
 			
 			//通知理财人
 			Map<String, Object> sceneParame = new HashMap<String, Object>();
 			sceneParame.put("user_name", investUserFund.name);
 			sceneParame.put("bill_no", billInvest.bill_invest_no);
 			sceneParame.put("amount", billInvest.receive_corpus + billInvest.receive_interest + overdueFine);
 			sceneParame.put("principal", billInvest.receive_corpus);
 			sceneParame.put("interest", billInvest.receive_interest);
 			sceneParame.put("fee", investServiceFee);
 			sceneParame.put("balance", receiveAmt);
 			noticeService.sendSysNotice(billInvest.user_id, NoticeScene.INVEST_SECTION, sceneParame);
 			
 			//计算借款人扣除的逾期罚息
 			loanOverdueFine += overdueFine;
		}
		
		// 更新借款账单还款情况
		boolean repayment = updateRepaymentData(bill.id, t_bill.Status.NORMAL_REPAYMENT);
		if (!repayment) {
			result.code = -1;
			result.msg = "借款人还款成功，更新借款账单还款数据失败";
			
			return result;
		}
		
		/** 扣除借款人还款金额  */
		double repayAmt = bill.repayment_corpus + bill.repayment_interest + loanOverdueFine;
		boolean minusFund = userFundService.userFundMinus(bill.user_id, repayAmt);
		if (!minusFund) {
			result.code = -1;
			result.msg = "扣除借款人可用余额失败";
			
			return result;
		}
		
		//更新借款人资产签名更新
		userFundService.userFundSignUpdate(bill.user_id);
		
		//刷新借款人资金信息
		t_user_fund loanUserFund = userFundService.queryUserFundByUserId(bill.user_id);
		if (loanUserFund == null) {
			result.code = -1;
			result.msg = "获取借款人资金信息失败";
			
			return result;
		}
		
		//添加借款人还款记录
		Map<String, String> summaryParam = new HashMap<String, String>();
		summaryParam.put("billNo", bill.bill_no);
		boolean addDeal = dealUserService.addDealUserInfo(
				serviceOrderNo,
				bill.user_id, 
				bill.repayment_corpus + bill.repayment_interest, 
				loanUserFund.balance + loanOverdueFine,   //此时不计“逾期罚息”
				loanUserFund.freeze, 
				t_deal_user.OperationType.REPAYMENT,
				summaryParam);
		if (!addDeal) {
			result.code = -1;
			result.msg = "添加借款人还款记录失败";
			
			return result;
		}
		
		//添加借款人逾期罚息记录
		if (loanOverdueFine > 0) {
			addDeal = dealUserService.addDealUserInfo(
					serviceOrderNo,
					bill.user_id, 
					loanOverdueFine, 
					loanUserFund.balance,
					loanUserFund.freeze,
					t_deal_user.OperationType.REPAYMENT_OVERDUE_FINE,
					summaryParam);
			if (!addDeal) {
				result.code = -1;
				result.msg = "添加逾期罚息记录失败";
				
				return result;
			}
		}
		
		if (isEndPayment(bill.bid_id)) {
			bidService.bidEnd(bill.bid_id);
		}
		
		//通知借款人
		Map<String, Object> sceneParame = new HashMap<String, Object>();
		sceneParame.put("user_name", loanUserFund.name);
		sceneParame.put("bill_no", bill.bill_no);
		sceneParame.put("amount", bill.repayment_corpus + bill.repayment_interest);
		noticeService.sendSysNotice(bill.user_id, NoticeScene.REPAYMENT_SUCC, sceneParame);
		
		result.code = 1;
		result.msg = "还款成功";
		
		
		return result;
	}
	
	public ResultInfo doRepayment(long billId, long billInvestId, Map<String, Double> billInvestFee, String serviceOrderNo) {
		ResultInfo result = new ResultInfo();
		
		t_bill bill = findByID(billId);
		if (bill == null) {
			result.code = -1;
			result.msg = "借款账单不存在";
			
			return result;
		}
		
		//判断还款的借款标是否有未完成的债权转让，如果有，将其状态回归原态,并解冻竞拍者资金
		result = debtService.queryDebtTransferNoComplete(bill.bid_id);
		if(result.code < 1){
			
			return result;
		}
		
		List<t_bill_invest> billInvestList = billInvestService.queryNOReceiveInvestBills(bill.bid_id, bill.period);
		if (billInvestList == null || billInvestList.size() <= 0) {
			result.code = -1;
			result.msg = "查询待还的理财账单失败";
			
			return result;
		}

		t_bill_invest billInvest = billInvestService.findByID(billInvestId);
		if (billInvest == null) {
			result.code = -1;
			result.msg = "查询待还的理财账单失败";
			
			return result;
		}
		
		if(billInvest.getStatus().code != t_bill_invest.Status.NO_RECEIVE.code) {
			result.code = -1;
			result.msg = "待还的理财账单状态错误";
			return result;
		}
		
		/** 借款人扣除的逾期罚息 */
		double loanOverdueFine = 0;  //借款人扣除的逾期罚息 = 理财人收取的逾期罚息之和 
		
		/** 理财人收款 */

		// 更新理财账单收款情况
		boolean receive = billInvestService.updateReceiveData(billInvest.id);
		if (!receive) {
			result.code = -1;
			result.msg = "理财人收到回款，更新理财账单回款数据失败";
			
			return result;
		}
		//理财服务费
		double investServiceFee = billInvestFee.get("investServiceFee");  
		//逾期罚息
		double overdueFine = billInvestFee.get("overdueFine"); 
	    //理财人应收金额
		//double receiveAmt = billInvest.receive_corpus + billInvest.receive_interest + overdueFine - investServiceFee; 
		double receiveAmt =Arith.sub(Arith.add(Arith.add(billInvest.receive_corpus ,billInvest.receive_interest) ,overdueFine), investServiceFee);
			
		boolean isSignSuccess = true;  //用户签名是否通过
		result = userFundService.userFundSignCheck(billInvest.user_id);
		if (result.code < 1) {
			isSignSuccess = false;
		}
			
		boolean addFund = userFundService.userFundAdd(billInvest.user_id, receiveAmt);
		if (!addFund) {
			result.code = -1;
			result.msg = "增加理财人可用余额失败";
			
			return result;
		}
			
		//刷新理财人资金信息
		t_user_fund investUserFund = userFundService.queryUserFundByUserId(billInvest.user_id);
		if (investUserFund == null) {
			result.code = -1;
			result.msg = "获取理财资金信息失败";
			
			return result;
		}
			
		//如果投资用户账户资金没有遭到非法改动，那么就更新其篡改标志，否则不更新
		if(isSignSuccess){
			userFundService.userFundSignUpdate(billInvest.user_id);
		}

		//添加理财人收款记录
		Map<String, String> summaryParam = new HashMap<String, String>();
		summaryParam.put("billInvestNo", billInvest.bill_invest_no);
		boolean addDeal = dealUserService.addDealUserInfo(
				serviceOrderNo,
				billInvest.user_id, 
				billInvest.receive_corpus + billInvest.receive_interest, 
				investUserFund.balance + investServiceFee - overdueFine,   //此时不计“理财服务费”和“逾期罚息”
				investUserFund.freeze, 
				t_deal_user.OperationType.RECEIVE,
				summaryParam);
		if (!addDeal) {
			result.code = -1;
			result.msg = "添加理财人收款记录失败";
			
			return result;
		}
			
		//添加逾期罚息记录
		if (overdueFine > 0) {
			addDeal = dealUserService.addDealUserInfo(
					serviceOrderNo,
					billInvest.user_id, 
					overdueFine, 
					investUserFund.balance + investServiceFee,   //此时不计“理财服务费”
					investUserFund.freeze, 
					t_deal_user.OperationType.RECEIVE_OVERDUE_FINE,
					summaryParam);
			if (!addDeal) {
				result.code = -1;
				result.msg = "添加逾期罚息记录失败";
				
				return result;
			}
		}
			
		//添加扣除理财服务费记录
		addDeal = dealUserService.addDealUserInfo(
				serviceOrderNo,
				billInvest.user_id, 
				investServiceFee, 
				investUserFund.balance, 
				investUserFund.freeze, 
				t_deal_user.OperationType.INVEST_SERVICE_FEE,
				summaryParam);
		if (!addDeal) {
			result.code = -1;
			result.msg = "添加扣除理财服务费记录失败";
			
			return result;
		}
		
		//添加平台收支记录
		Map<String, Object> dealRemarkParam = new HashMap<String, Object>();
		dealRemarkParam.put("bill_invest_no", billInvest.bill_invest_no);
		addDeal = dealPlatformService.addPlatformDeal(
				billInvest.user_id,
				investServiceFee, 
				t_deal_platform.DealRemark.INVEST_FEE,
				dealRemarkParam);
		if (!addDeal) {
			result.code = -1;
			result.msg = "添加平台收支记录失败";
			
			return result;
		}
 			
		//通知理财人
		Map<String, Object> sceneParame = new HashMap<String, Object>();
		sceneParame.put("user_name", investUserFund.name);
		sceneParame.put("bill_no", billInvest.bill_invest_no);
		sceneParame.put("amount", billInvest.receive_corpus + billInvest.receive_interest + overdueFine);
		sceneParame.put("principal", billInvest.receive_corpus);
		sceneParame.put("interest", billInvest.receive_interest);
		sceneParame.put("fee", investServiceFee);
		sceneParame.put("balance", receiveAmt);
		noticeService.sendSysNotice(billInvest.user_id, NoticeScene.INVEST_SECTION, sceneParame);
		
		//计算借款人扣除的逾期罚息
		loanOverdueFine += overdueFine;
		
		// 更新借款账单还款情况
		if(billInvestList.size() == 1) {
			// 全部完成
			boolean repayment = updateRepaymentData(bill.id, t_bill.Status.NORMAL_REPAYMENT);
			if (!repayment) {
				result.code = -1;
				result.msg = "借款人还款成功，更新借款账单还款数据失败";
				
				return result;
			}
		} else {
			// 部分完成
			boolean repayment = updateRepaymentData(bill.id, t_bill.Status.PARTIAL_NORMAL_REPAYMENT);
			if (!repayment) {
				result.code = -1;
				result.msg = "借款人部分还款成功，更新借款账单还款数据失败";
				return result;
			}
		}
		
		
		/** 扣除借款人还款金额  */
		double repayAmt = billInvest.receive_corpus + billInvest.receive_interest + loanOverdueFine;
		boolean minusFund = userFundService.userFundMinus(bill.user_id, repayAmt);
		if (!minusFund) {
			result.code = -1;
			result.msg = "扣除借款人可用余额失败";
			
			return result;
		}
		
		//更新借款人资产签名更新
		userFundService.userFundSignUpdate(bill.user_id);
		
		//刷新借款人资金信息
		t_user_fund loanUserFund = userFundService.queryUserFundByUserId(bill.user_id);
		if (loanUserFund == null) {
			result.code = -1;
			result.msg = "获取借款人资金信息失败";
			
			return result;
		}
		
		//添加借款人还款记录
		Map<String, String> theSummaryParam = new HashMap<String, String>();
		theSummaryParam.put("billNo", bill.bill_no);
		boolean theAddDeal = dealUserService.addDealUserInfo(
				serviceOrderNo,
				bill.user_id, 
				billInvest.receive_corpus + billInvest.receive_interest, 
				loanUserFund.balance + loanOverdueFine,   //此时不计“逾期罚息”
				loanUserFund.freeze, 
				t_deal_user.OperationType.REPAYMENT,
				theSummaryParam);
		if (!theAddDeal) {
			result.code = -1;
			result.msg = "添加借款人还款记录失败";
			
			return result;
		}
		
		//添加借款人逾期罚息记录
		if (loanOverdueFine > 0) {
			theAddDeal = dealUserService.addDealUserInfo(
					serviceOrderNo,
					bill.user_id, 
					loanOverdueFine, 
					loanUserFund.balance,
					loanUserFund.freeze,
					t_deal_user.OperationType.REPAYMENT_OVERDUE_FINE,
					theSummaryParam);
			if (!theAddDeal) {
				result.code = -1;
				result.msg = "添加逾期罚息记录失败";
				
				return result;
			}
		}
		
		if (isEndPayment(bill.bid_id)) {
			bidService.bidEnd(bill.bid_id);
		}
		
		//通知借款人
		Map<String, Object> theSceneParame = new HashMap<String, Object>();
		theSceneParame.put("user_name", loanUserFund.name);
		theSceneParame.put("bill_no", bill.bill_no);
		theSceneParame.put("amount", billInvest.receive_corpus + billInvest.receive_corpus);
		noticeService.sendSysNotice(bill.user_id, NoticeScene.REPAYMENT_SUCC, sceneParame);
		
		result.code = 1;
		result.msg = "还款成功";
		
		return result;
	}

	/**
	 * 垫付后线下收款（执行）
	 *
	 * @param supervisorId 管理员ID
	 * @param billId 借款账单ID
	 * @param loanOverdueFine 逾期罚息
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月28日
	 */
	public ResultInfo doOfflineReceiveAfterAdvance(long supervisorId, long billId) {
		ResultInfo result = new ResultInfo();
		
		t_bill bill = findByID(billId);
		if (bill == null) {
			result.code = -1;
			result.msg = "该借款账单不存在";
			
			return result;
		}
		
		// 更新借款账单还款情况
		boolean repayment = updateRepaymentData(bill.id, t_bill.Status.OUT_LINE_PRINCIIPAL_RECEIVE);
		if (!repayment) {
			result.code = -1;
			result.msg = "借款人本息垫付还款成功，更新借款账单还款数据失败";
			
			return result;
		}
		
		if (isEndPayment(bill.bid_id)) {
			bidService.bidEnd(bill.bid_id);
		}
		
		t_user_fund loanUser = userFundService.queryUserFundByUserId(bill.user_id);
		
		//通知借款人
		Map<String, Object> sceneParame = new HashMap<String, Object>();
		sceneParame.put("user_name", loanUser.name);
		sceneParame.put("bill_no", bill.bill_no);
		sceneParame.put("amount", bill.repayment_corpus + bill.repayment_interest);
		noticeService.sendSysNotice(bill.user_id, NoticeScene.REPAYMENT_SUCC, sceneParame);
		
		/** 添加管理员事件 */
		Map<String, String> supervisorEventParam = new HashMap<String, String>();
		supervisorEventParam.put("bill_no ", bill.bill_no);
		supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.BILL_OFFLINE, supervisorEventParam);
		
		result.code = 1;
		result.msg = "本息垫付后线下收款成功";
		
		return result;
	}
	
	/**
	 * 线下收款（执行）
	 * 
	 * @param bill 借款账单ID
	 * @param overdueFeeList 逾期罚息列表（理财账单）
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月28日
	 */
	public ResultInfo doOfflineReceive(long billId, List<Map<String, Double>> billInvestFeeList, String serviceOrderNo) {
		ResultInfo result = new ResultInfo();
		
		t_bill bill = findByID(billId);
		if (bill == null) {
			result.code = -1;
			result.msg = "该借款账单不存在";
			
			return result;
		}
		
		//判断还款的借款标有没有债权在转让，如果有，将其状态回归原态,并解冻竞拍者资金
		result = debtService.queryDebtTransferNoComplete(bill.bid_id);
		if(result.code < 1){
			
			return result;
		}

		List<t_bill_invest> billInvestList = billInvestService.queryNOReceiveInvestBills(bill.bid_id, bill.period);
		if (billInvestList == null || billInvestList.size() <= 0) {
			result.code = -1;
			result.msg = "查询待还的理财账单失败";
			
			return result;
		}
		
		/** 逾期罚息费 */
		double loanOverdueFine = 0; 
		
		/** 理财人收款 */
		for (int i = 0; i < billInvestList.size(); i++) {
			t_bill_invest billInvest = billInvestList.get(i);

			// 更新理财账单收款情况
			boolean receive = billInvestService.updateReceiveData(billInvest.id);
			if (!receive) {
				result.code = -1;
				result.msg = "理财人收到回款，更新理财账单回款数据失败";
				
				return result;
			}
			
			boolean isSignSuccessed = true;
			result = userFundService.userFundSignCheck(billInvest.user_id);
			if (result.code < 1) {
				isSignSuccessed = false;
			}
			
			//理财服务费
			double investServiceFee = billInvestFeeList.get(i).get("investServiceFee");  
			//逾期罚息
			double overdueFine = billInvestFeeList.get(i).get("overdueFine"); 
		    //理财人应收金额
//			double receiveAmt = billInvest.receive_corpus + billInvest.receive_interest + overdueFine - investServiceFee; 
			double receiveAmt =Arith.sub(Arith.add(Arith.add(billInvest.receive_corpus ,billInvest.receive_interest) ,overdueFine), investServiceFee);
			
			boolean addFund = userFundService.userFundAdd(billInvest.user_id, receiveAmt);
			if (!addFund) {
				result.code = -1;
				result.msg = "增加理财人可用余额失败";
				
				return result;
			}
			
			//刷新理财人资金信息
			t_user_fund investUserFund = userFundService.queryUserFundByUserId(billInvest.user_id);
			if (investUserFund == null) {
				result.code = -1;
				result.msg = "获取理财资金信息失败";
				
				return result;
			}
			
			//如果投资用户账户资金没有遭到非法改动，那么就更新其篡改标志，否则不更新
			if(isSignSuccessed){
				userFundService.userFundSignUpdate(billInvest.user_id);
			}

			//添加理财人收款记录
			Map<String, String> summaryParam = new HashMap<String, String>();
			summaryParam.put("billInvestNo", billInvest.bill_invest_no);
			boolean addDeal = dealUserService.addDealUserInfo(
					serviceOrderNo,
					billInvest.user_id, 
					billInvest.receive_corpus + billInvest.receive_interest, 
					investUserFund.balance + investServiceFee - overdueFine,   //此时不计“理财服务费”和“逾期罚息”
					investUserFund.freeze, 
					t_deal_user.OperationType.RECEIVE,
					summaryParam);
			if (!addDeal) {
				result.code = -1;
				result.msg = "添加理财人收款记录失败";
				
				return result;
			}
			
			//添加逾期罚息记录
			if (overdueFine > 0) {
				addDeal = dealUserService.addDealUserInfo(
						serviceOrderNo,
						billInvest.user_id, 
						overdueFine, 
						investUserFund.balance + investServiceFee,   //此时不计“理财服务费”
						investUserFund.freeze, 
						t_deal_user.OperationType.RECEIVE_OVERDUE_FINE,
						summaryParam);
				if (!addDeal) {
					result.code = -1;
					result.msg = "添加逾期罚息记录失败";
					
					return result;
				}
			}
			
			//添加扣除理财服务费记录
			addDeal = dealUserService.addDealUserInfo(
					serviceOrderNo,
					billInvest.user_id, 
					investServiceFee, 
					investUserFund.balance, 
					investUserFund.freeze, 
					t_deal_user.OperationType.INVEST_SERVICE_FEE,
					summaryParam);
			if (!addDeal) {
				result.code = -1;
				result.msg = "添加扣除理财服务费记录失败";
				
				return result;
			}
			
			//添加平台收支记录
			Map<String, Object> dealRemarkParam = new HashMap<String, Object>();
			dealRemarkParam.put("bill_invest_no", billInvest.bill_invest_no);
			addDeal = dealPlatformService.addPlatformDeal(
					billInvest.user_id,
					investServiceFee, 
					t_deal_platform.DealRemark.INVEST_FEE,
					dealRemarkParam);
 			if (!addDeal) {
				result.code = -1;
				result.msg = "添加平台收支记录失败";
				
				return result;
			}
 			
 			//通知理财人
 			Map<String, Object> sceneParame = new HashMap<String, Object>();
 			sceneParame.put("user_name", investUserFund.name);
 			sceneParame.put("bill_no", billInvest.bill_invest_no);
 			sceneParame.put("amount", billInvest.receive_corpus + billInvest.receive_interest + overdueFine);
 			sceneParame.put("principal", billInvest.receive_corpus);
 			sceneParame.put("interest", billInvest.receive_interest);
 			sceneParame.put("fee", investServiceFee);
 			sceneParame.put("balance", receiveAmt);
 			noticeService.sendSysNotice(billInvest.user_id, NoticeScene.INVEST_SECTION, sceneParame);

 			loanOverdueFine += overdueFine;
 			
		}
		
		// 更新借款账单还款情况
		boolean repayment = updateRepaymentData(bill.id, t_bill.Status.OUT_LINE_RECEIVE);
		if (!repayment) {
			result.code = -1;
			result.msg = "借款人还款成功，更新借款账单还款数据失败";
			
			return result;
		}

		if (isEndPayment(bill.bid_id)) {
			bidService.bidEnd(bill.bid_id);
		}
		
		//线下收款，添加平台收支记录
		Map<String, Object> dealRemarkParam = new HashMap<String, Object>();
		dealRemarkParam.put("bill_no", bill.bill_no);
		boolean addDeal = dealPlatformService.addPlatformDeal(
				bill.user_id,
				bill.repayment_corpus + bill.repayment_interest + loanOverdueFine, 
				t_deal_platform.DealRemark.OFFLINE_SECTION,
				dealRemarkParam);
		if (!addDeal) {
			result.code = -1;
			result.msg = "添加平台收支记录失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "线下收款成功";
		
		
		return result;
	}
	
	/**
	 * 本息垫付还款（执行）
	 *
	 * @param userId 用户ID
	 * @param billId 借款账单ID
	 * @param loanOverdueFine 逾期罚息
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月28日
	 */
	public ResultInfo doAdvanceRepayment(String serviceOrderNo, long billId, double loanOverdueFine) {
		ResultInfo result = new ResultInfo();
		
		t_bill bill = findByID(billId);
		if (bill == null) {
			result.code = -1;
			result.msg = "该借款账单不存在";
			
			return result;
		}
		
		// 更新借款账单还款情况
		boolean repayment = updateRepaymentData(bill.id, t_bill.Status.ADVANCE_PRINCIIPAL_REPAYMENT);
		if (!repayment) {
			result.code = -1;
			result.msg = "借款人本息垫付还款成功，更新借款账单还款数据失败";
			
			return result;
		}
		
		/** 扣除借款人还款金额  */
		double repayAmt = bill.repayment_corpus + bill.repayment_interest + loanOverdueFine;
		boolean minusFund = userFundService.userFundMinus(bill.user_id, repayAmt);
		if (!minusFund) {
			result.code = -1;
			result.msg = "扣除借款人可用余额失败";
			
			return result;
		}
		
		//更新借款人资产签名更新
		userFundService.userFundSignUpdate(bill.user_id);
		
		//刷新借款人资金信息
		t_user_fund loanUserFund = userFundService.queryUserFundByUserId(bill.user_id);
		if (loanUserFund == null) {
			result.code = -1;
			result.msg = "获取借款人资金信息失败";
			
			return result;
		}
		
		//添加借款人还款记录
		Map<String, String> summaryParam = new HashMap<String, String>();
		summaryParam.put("billNo", bill.bill_no);
		boolean addDeal = dealUserService.addDealUserInfo(
				serviceOrderNo, 
				bill.user_id, 
				bill.repayment_corpus + bill.repayment_interest, 
				loanUserFund.balance + loanOverdueFine,   //此时不计“逾期罚息”
				loanUserFund.freeze, 
				t_deal_user.OperationType.REPAYMENT,
				summaryParam);
		if (!addDeal) {
			result.code = -1;
			result.msg = "添加借款人还款记录失败";
			
			return result;
		}
		
		//添加借款人逾期罚息记录
		if (loanOverdueFine > 0) {
			addDeal = dealUserService.addDealUserInfo(
					serviceOrderNo,
					bill.user_id, 
					loanOverdueFine, 
					loanUserFund.balance,
					loanUserFund.freeze, 
					t_deal_user.OperationType.REPAYMENT_OVERDUE_FINE,
					summaryParam);
			if (!addDeal) {
				result.code = -1;
				result.msg = "添加逾期罚息记录失败";
				
				return result;
			}
		}
		
		if (isEndPayment(bill.bid_id)) {
			bidService.bidEnd(bill.bid_id);
		}
		
		//本息垫付还款，添加平台收支记录
		Map<String, Object> dealRemarkParam = new HashMap<String, Object>();
		dealRemarkParam.put("bill_no", bill.bill_no);
		addDeal = dealPlatformService.addPlatformDeal(
				bill.user_id,
				bill.repayment_corpus + bill.repayment_interest, 
				t_deal_platform.DealRemark.ADVANCE_PRIN_INTER_INCOME,
				dealRemarkParam);
		if (!addDeal) {
			result.code = -1;
			result.msg = "添加平台本息垫付还款收支记录失败";
			
			return result;
		}
		
		//罚息垫付还款，添加平台收支记录
		if (loanOverdueFine > 0) {
			addDeal = dealPlatformService.addPlatformDeal(
					bill.user_id,
					loanOverdueFine, 
					t_deal_platform.DealRemark.OVERFUE_INTEREST_INCOME,
					dealRemarkParam);
			if (!addDeal) {
				result.code = -1;
				result.msg = "添加平台罚息垫付还款收支记录失败";
				
				return result;
			}
		}
		
		//通知借款人
		Map<String, Object> sceneParame = new HashMap<String, Object>();
		sceneParame.put("user_name", loanUserFund.name);
		sceneParame.put("bill_no", bill.bill_no);
		sceneParame.put("amount", bill.repayment_corpus + bill.repayment_interest);
		noticeService.sendSysNotice(bill.user_id, NoticeScene.REPAYMENT_SUCC, sceneParame);
		
		result.code = 1;
		result.msg = "本息垫付还款成功";
		
		return result;
	}

	/**
	 * 借款人还款成功
	 * <br>更新借款账单状态:正常还款或本息垫付还款
	 * <br>更新实际还款时间
	 *
	 * @param billId 借款账单ID
 	 * @param status 还款状态枚举
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月29日
	 */
	public boolean updateRepaymentData(long billId, t_bill.Status status) {
		int row = billDao.updateStatus(billId, status.code, new Date());
		if (row < 1) {
			LoggerUtil.error(true, "借款人还款成功，更新借款账单还款数据失败。【billId：%s】", billId);
			
			return false;
		}
		
		return true;
	}
	
	/**
	 * 借款人部分还款成功
	 * <br>更新借款账单状态:正常还款或本息垫付还款
	 * <br>更新实际还款时间
	 *
	 * @param billId 借款账单ID
 	 * @param status 还款状态枚举
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月29日
	 */
	public boolean updatePARTIALRepaymentData(long billId, t_bill.Status status) {
		int row = billDao.updateStatus(billId, status.code, new Date());
		if (row < 1) {
			LoggerUtil.error(true, "借款人部分还款成功，更新借款账单还款数据失败。【billId：%s】", billId);
			
			return false;
		}
		
		return true;
	}

	/**
	 * 本息垫付成功
	 * <br>更新借款账单状态为：本息垫付待还
	 *
	 * @param billId 借款账单ID
	 * @param status 还款状态枚举
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月29日
	 */
	public boolean updateAdvanceData(long billId) {

		int row = billDao.updateStatus(billId, t_bill.Status.ADVANCE_PRINCIIPAL_NO_REPAYMENT.code);
		
		if (row < 1) {
			
			return false;
		}
		
		return true;
	}

	/**
	 * 查询用户待还
	 *
	 * @param userId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月28日
	 */
	public double getUserPay(long userId) {
		
		List<Integer> statusList = new ArrayList<Integer>();
		statusList.add(t_bill.Status.NORMAL_NO_REPAYMENT.code);
		statusList.add(t_bill.Status.ADVANCE_PRINCIIPAL_NO_REPAYMENT.code);
		
		return billDao.findUserPay(userId, statusList);
	}
	/**
	 * 后台-首页-账单信息的统计字段
	 *
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月15日
	 */
	public Map<String, Object> backCountBillInfo(){
		
		return billDao.backCountBillInfo();
	}

	/**
	 * 判断该借款标是否已经还完。
	 *
	 * @param bidId
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月29日
	 */
	public boolean isEndPayment(long bidId) {
		
		List<t_bill> list = billDao.queryNoRepaymentBillList(bidId);
		
		if (list == null || list.size() == 0) {
			
			return true;
		}
		
		return false;
	}
	/**
	 * 定时任务-系统标记逾期或者重新计算逾期费用
	 *
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月27日
	 */
	public ResultInfo systemMarkOverdue() {
		
		return billDao.autoMarkOverdue();
	}

	/**
	 * 定时任务-账单将要到期或者已经到期的提醒
	 *
	 *
	 * @author yaoyi
	 * @createDate 2016年3月1日
	 */
	public ResultInfo billRemind() {
		ResultInfo result = new ResultInfo();
		
		//查询今天或者一个星期后到期的“正常待还”和“本息垫付待还”账单
		List<Map<String, Object>> listOfWillExpireBill = billDao.queryListOfWillExpireBill();
		
		if (listOfWillExpireBill==null || listOfWillExpireBill.size()==0) {
			result.code = 1;
			result.msg = "当前没有需要提醒的账单!";
			
			return result;
		}
		
		int success = 0;
		
		for(Map<String, Object> mapBill : listOfWillExpireBill){
			
			Map<String, Object> sceneParame = new HashMap<String, Object>();
			sceneParame.put("user_name", mapBill.get("name"));
			sceneParame.put("bill_no", NoUtil.getBillNo(Long.parseLong(mapBill.get("billId").toString()), (Date)mapBill.get("time")));
			sceneParame.put("repayment_time", DateUtil.dateToString((Date)mapBill.get("repayment_time"), "yyyy-MM-dd"));
			sceneParame.put("amount", mapBill.get("repayment_amount"));
			boolean send = noticeService.sendSysNotice(Long.parseLong(mapBill.get("userId").toString()), NoticeScene.BILL_EXPIRES, sceneParame);
			if (send) {
				success++;
			}
		}
		
		result.code = 1;
		result.msg = "本次账单提醒总共通知"+listOfWillExpireBill.size()+"人次，成功通知"+success+"人次!";
		
		return result;
	}
	
}
