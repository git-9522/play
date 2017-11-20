package services.core;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.number.Arith;
import daos.core.BillInvestDao;
import models.common.entity.t_deal_platform;
import models.common.entity.t_deal_user;
import models.common.entity.t_user_fund;
import models.core.bean.BillInvest;
import models.core.bean.InvestReceive;
import models.core.entity.t_bill_invest;
import models.core.entity.t_bill_invest.Status;
import models.core.entity.t_invest;
import services.base.BaseService;
import services.common.DealPlatformService;
import services.common.DealUserService;
import services.common.NoticeService;
import services.common.UserFundService;

/**
 * 理财账单 业务实现类
 *
 * @author liudong
 * @createDate 2015年12月16日
 */
public class BillInvestService extends BaseService<t_bill_invest> {

	protected static InvestService investService = Factory.getService(InvestService.class);
	protected static UserFundService userFundService = Factory.getService(UserFundService.class);
	protected static DealUserService dealUserService = Factory.getService(DealUserService.class);
	protected static DealPlatformService dealPlatformService = Factory.getService(DealPlatformService.class);
	protected static NoticeService noticeService = Factory.getService(NoticeService.class);
	
	protected BillInvestDao billInvestDao = Factory.getDao(BillInvestDao.class);
	
	protected BillInvestService() {
		super.basedao = billInvestDao;
	}

	
	
	/**
	 * 生成投资账单
	 *
	 * @param bid_id
	 * @param repayment_type
	 * @param res
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月24日
	 */
	public ResultInfo createBillInvest(long bid_id, int repayment_type, ResultInfo res) {
		
		return billInvestDao.saveBillInvest(bid_id, repayment_type, res);
	}

	public boolean copyToBillInvest(long originalInvestId,long newUserId,long newInvestId){
		return billInvestDao.copyToBillInvest(originalInvestId,newUserId,newInvestId);
	}
	
	/**
	 * 理财人收到回款，更新理财账单回款数据（状态和实际回款时间）
	 *
	 * @param billInvestId 理财账单id
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月25日
	 */
	public boolean updateReceiveData(long billInvestId) {
		int row = billInvestDao.updateStatusAndRealReceiveTime(billInvestId, t_bill_invest.Status.RECEIVED.code, new Date());
		if (row < 1) {
			LoggerUtil.error(true, "理财人收到回款，更新理财账单回款数据失败。[billInvestId:%s]", billInvestId);
			
			return false;
		}
		
		return true;
	}
	
	/**
	 * 债权转让完成，更新原来投资账单(未还的)的状态为'已转让'
	 *
	 * @param invested
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月29日
	 */
	public boolean updateBillToTransfered(long invested){
		
		return billInvestDao.updateBillToTransfered(invested);
	}
	
	/**
	 * 判断某个投资是否有逾期记录
	 *
	 * @param investId 投资id
	 * @return 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年4月5日
	 */
	public boolean isInvestOverdue(long investId) {
		t_bill_invest bill = billInvestDao.findByColumn(" invest_id=? AND is_overdue=? ", investId,true);
		return bill != null;
	}
	
	/**
	 * 查询投资总额  
	 *
	 * @param showType default:所有  1.待还  2.逾期待还  3.已还  4.已转让
	 * @param loanName 借款人昵称
	 * @param projectName 项目名称
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年1月19日
	 */
	public double findBillInvestTotalAmt(int showType, String loanName,String projectName) {
		return billInvestDao.findBillInvestTotalAmt(showType,loanName,projectName);
	}
	
	/**
	 * 查询所有的应收利息(也即给用户创造的收益，已还未还都算)
	 *
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月23日
	 */
	public double findAllInterest() {
		
		return billInvestDao.findAllInterest();
	}
	
	/**
	 * 查询某个标记已还的本金
	 *
	 * @param investId 标记id
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月29日
	 */
	public double findHasReceivePrincipal(long investId){
		
		return billInvestDao.findHasReceivePrincipal(investId);
	}
	
	/**
	 * 查询某个投资的待还账单
	 *
	 * @param investId
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年4月11日
	 */
	public int countPayingBill(Long investId){
		
		return billInvestDao.countByColumn(" invest_id=? AND status=? ", investId,models.core.entity.t_bill_invest.Status.NO_RECEIVE.code);
	}
	
	/**
	 * 查询某个投资的账单
	 *
	 * @param investId 
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年4月6日
	 */
	public List<InvestReceive> queryMyBillInvest(long investId) {
		
		return billInvestDao.queryMyBillInvestFront(investId);
	}
	
	/**
	 * 分页查询 我的理财账单 
	 * 
	 * @param currPage 当前页
	 * @param pageSize 每页条数 
	 * @param userId 用户userId
	 * @return
	 *
	 * @author liudong
	 * @createDate 2015年12月16日
	 */
	public PageBean<t_bill_invest> pageOfMyBillInvest(int currPage,int pageSize, long userId) {
		
		return billInvestDao.pageOfMyBillInvest(currPage, pageSize, userId);
	}

	/**
	 * 分页查询 ,理财账单列表
	 *
	 * @param showType default:所有  1.待还  2.逾期待还  3.已还   4.已转让
	 * @param currPage
	 * @param pageSize
	 * @param orderType 参与排序的栏目 0,按编号;3,账单金额;5,逾期时长;6,到期时间;7,回款时间
	 * @param orderValue 排序的类型:0,降序;1,升序
	 * @param exports 1:导出  default：不导出
	 * @param loanName 借款人昵称
	 * @param projectName 项目
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月23日
	 */
	public PageBean<BillInvest> pageOfBillInvestBack(int showType, int currPage, int pageSize,int orderType,int orderValue, int exports, String loanName, String projectName) {

		return billInvestDao.pageOfBillInvestBack(showType,currPage, pageSize,orderType,orderValue, exports, loanName, projectName);
	}
	
	/**
	 * 分页查询 回款计划
	 * @param currPage
	 * @param pageSize
	 * @param userId
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月18日
	 *
	 */
	public PageBean<InvestReceive> pageOfInvestReceive(int currPage, int pageSize, long userId) {

		return billInvestDao.pageOfInvestReceive(currPage, pageSize, userId);
	}
	
	/**
	 * 分页查询理财账单
	 *
	 * @param currPage
	 * @param pageSize
	 * @param investId 
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年4月11日
	 */
	public PageBean<InvestReceive> pageOfRepaymentBill(int currPage,
			int pageSize, long investId) {
		/*
		 SELECT
				tbi.id AS id,
				tbi.time AS time,
				tbi.user_id as user_id,
				tbi.receive_corpus AS receive_corpus,
				tbi.receive_interest AS receive_interest,
				(tbi.receive_corpus + tbi.receive_interest) AS corpus_interest,
				tbi.invest_id AS invest_id,
				tbi.period AS period,
				tbi.receive_time AS receive_time,
				tbi.real_receive_time AS real_receive_time,
				tbi.status AS status,
				tb.service_fee_rule AS service_fee_rule,
				(SELECT COUNT(id)FROM t_bill WHERE bid_id = tbi.bid_id) AS totalPeriod
			FROM
				t_bill_invest tbi,
				t_bid tb
			WHERE
				tbi.bid_id = tb.id
			AND invest_id =: investId
		 */
		String querySQL = "SELECT tbi.id AS id,(tbi.reward_invest + tbi.add_invest) as add_amount, tbi.time AS time,tbi.user_id as user_id, tbi.receive_corpus AS receive_corpus, tbi.receive_interest AS receive_interest, (tbi.receive_corpus + tbi.receive_interest) AS corpus_interest,tbi.invest_id AS invest_id, tbi.period AS period, tbi.receive_time AS receive_time, tbi.real_receive_time AS real_receive_time, tbi.status AS status,tb.service_fee_rule AS service_fee_rule, (SELECT COUNT(id)FROM t_bill WHERE bid_id = tbi.bid_id) AS totalPeriod FROM t_bill_invest tbi, t_bid tb WHERE invest_id =:investId AND tbi.bid_id = tb.id ";
		String countSQL = "SELECT COUNT(tbi.id) FROM t_bill_invest tbi, t_bid tb WHERE invest_id =:investId AND tbi.bid_id = tb.id  ";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("investId", investId);
		
		return billInvestDao.pageOfBeanBySQL(currPage, pageSize, countSQL, querySQL, InvestReceive.class, condition);
	}
	
	/**
	 * 不分页查询 ，用户该笔投资的理财账单
	 *
	 * @param investId 投资Id
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年1月20日
	 */
	public List<InvestReceive> queryMyBillInvestFront(long investId) {
		return billInvestDao.queryMyBillInvestFront(investId);
	}

	/**
	 * 查询待还的理财账单。根据借款标和借款账单期数
	 *
	 * @param bidId  标的id
	 * @param period  借款账单期数
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月25日
	 */
	public  List<t_bill_invest> queryNOReceiveInvestBills(long bidId, int period) {
		
		return billInvestDao.findListByColumn("bid_id=? AND period=? AND status=?", bidId, period, t_bill_invest.Status.NO_RECEIVE.code);
	}
	
	/**
	 * 查询已还的理财账单。根据借款标和借款账单期数
	 *
	 * @param bidId  标的id
	 * @param period  借款账单期数
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月25日
	 */
	public  List<t_bill_invest> queryReceiveInvestBills(long bidId, int period) {
		
		return billInvestDao.findListByColumn("bid_id=? AND period=? AND status=?", bidId, period, t_bill_invest.Status.RECEIVED.code);
	}
	
	/**
	 * 查询用户待收金额
	 *
	 * @param userId
	 * @param res
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月28日
	 */
	public double getUserReceive(long userId) {
		
		return billInvestDao.findUserReceive(userId, Status.NO_RECEIVE.code);
	}
	
	/**
	 * 投资奖励金额，分配到每一期账单
	 *
	 * @param bidId
	 * @return
	 *
	 * @author jiayijian
	 * @createDate 2017年04月05日
	 */
	public ResultInfo addRewardAmtToBillInvest(long bidId) {
		ResultInfo result = new ResultInfo();
		//获取所有的投资记录
		List<t_invest> investList = investService.queryBidInvest(bidId);
		
		if(investList == null || investList.size() <= 0){
			
			result.code = -1;
			result.msg = "查询投资记录失败";
			return result;
		}
		
		for(t_invest invest : investList){
			
			List<t_bill_invest> billList = this.findListByColumn(" invest_id = ? and status = ? order by period asc ", invest.id,t_bill_invest.Status.NO_RECEIVE.code);
			if(billList == null || billList.size() <= 0){
				result.code = -1;
				result.msg = "查询投资账单记录失败";
				return result;
			}
			
			double averageRate = Arith.divDown(invest.reward_amount, billList.size(), 2);
			double rewardAmount = invest.reward_amount;
			int i = 0;
			for(t_bill_invest bill : billList){
				
				if(rewardAmount <= 0){
					continue;
				}
				
				if(i < (billList.size() - 1)){
					
					rewardAmount = rewardAmount - averageRate;
					bill.reward_invest = averageRate;
				}else{
					bill.reward_invest = rewardAmount;
				}
				
				if(!billInvestDao.save(bill)){
					
					result.code = -1;
					result.msg = "更新投资账单记录失败";
					return result;
				}
				
				i++;
			}
		}
		
		result.code = 1;
		result.msg = "分配投资奖励成功";
		return result;
	}
	
	
	/**
	 * 投资使用加息卷的收益金额，分配到每一期账单
	 *
	 * @param bidId
	 * @return
	 *
	 * @author jiayijian
	 * @createDate 2017年04月05日
	 */
	public ResultInfo addRateAmtToBillInvest(long bidId) {
		ResultInfo result = new ResultInfo();
		//获取所有的投资记录
		List<t_invest> investList = investService.queryBidInvestUsedRate(bidId);
		
		if(investList == null || investList.size() <= 0){
			
			result.code = 1;
			result.msg = "不存在使用加息卷的投资记录";
			return result;
		}
		
		for(t_invest invest : investList){
			
			List<t_bill_invest> billList = this.findListByColumn(" invest_id = ? and status = ? order by period asc ", invest.id,t_bill_invest.Status.NO_RECEIVE.code);
			if(billList == null || billList.size() <= 0){
				result.code = -1;
				result.msg = "查询投资账单记录失败";
				return result;
			}
			
			double averageRate = Arith.divDown(invest.rate_amount, billList.size(), 2);
			double rateAmount = invest.rate_amount;
			int i = 0;
			for(t_bill_invest bill : billList){
				
				if(rateAmount <= 0){
					continue;
				}
				
				if(i < (billList.size() - 1)){
					
					rateAmount = rateAmount - averageRate;
					bill.add_invest = averageRate;
				}else{
					bill.add_invest = rateAmount;
				}
				
				if(!billInvestDao.save(bill)){
					
					result.code = -1;
					result.msg = "更新投资账单记录失败";
					return result;
				}
				
				i++;
			}
		}
		
		result.code = 1;
		result.msg = "分配投资奖励成功";
		return result;
	}

	
	/**
	 * 查询待还的理财账单。根据借款标和借款账单期数
	 *
	 * @param bidId  标的id
	 * @param period  借款账单期数
	 * @return
	 *
	 * @author jiayijian
	 * @createDate 2017年4月5日
	 */
	public  List<t_bill_invest> queryNoRateInvestBills() {
		
		return super.findListByColumn(" rate_status= ? AND status = ? AND (add_invest <> 0.00 OR reward_invest <> 0.00 ) AND real_add_invest = 0.00 ",
						t_bill_invest.Ratestatus.NO_SEND.code,t_bill_invest.Status.RECEIVED.code);
	}
	
	/**
	 * 先更新加息券发放状态，防止重复发送
	 * @param invest
	 * @return
	 */
	public int updateBillRateStatus(t_bill_invest invest) {
		
		if(null == invest){
			return -1;
		}
		String sql = "update t_bill_invest set rate_status = :rate_status where status = :status AND rate_status = :old_rate_status AND id=:id AND  real_add_invest = 0.00 ";
		
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("rate_status", t_bill_invest.Ratestatus.PROCESSING.code);
		maps.put("status", t_bill_invest.Status.RECEIVED.code);
		maps.put("old_rate_status",t_bill_invest.Ratestatus.NO_SEND.code);
		maps.put("id", invest.id);
		
		return billInvestDao.updateBySQL(sql, maps);
	}
	
	/**
	 *  商户转账(投标奖励、加息卷)
	 * @param serviceOrderNo
	 * @param investId
	 * @return
	 */
	public ResultInfo doSendRate(String serviceOrderNo, long billInvestId) {
		ResultInfo result = new ResultInfo();
		t_bill_invest billinvest = billInvestDao.findByID(billInvestId);
		if(billinvest == null){
			result.code = -1;
			result.msg = "理财账单信息错误";
			return result;
		}
		
		double realAddAmount = Arith.add(billinvest.add_invest, billinvest.reward_invest);
		
		//更新加息券实际发放金额
		boolean flag = updateBillRealAddInvest(billinvest, realAddAmount);
		if(!flag){
			result.code = -1;
			result.msg = "更新加息券实际金额失败";
			
			return result;
		}
		
		boolean fundAdd = userFundService.userFundAdd(billinvest.user_id, realAddAmount);
		if (!fundAdd) {
			result.code = -1;
			result.msg = "增加用户可用余额失败";
			
			return result;
		}
		
		//签名更新
		result = userFundService.userFundSignUpdate(billinvest.user_id);
		if (result.code < 1) {
			
			return result;
		}
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(billinvest.user_id);
		Map<String, String> summaryParam = new HashMap<String, String>();
		summaryParam.put("rateAmt", realAddAmount+"");
		summaryParam.put("billInvestNo", billinvest.bill_invest_no);
		boolean addDeal = dealUserService.addDealUserInfo(serviceOrderNo,
				billinvest.user_id,
				realAddAmount, 
				userFund.balance, 
				userFund.freeze, 
				t_deal_user.OperationType.SENDRATE, summaryParam);
		if (!addDeal) {
			result.code = -1;
			result.msg = "添加交易记录失败";
			
			return result;
		}
		
		if(billinvest.add_invest > 0){
			
			Map<String, Object> plantParam = new HashMap<String, Object>();
			plantParam.put("rateAmt", billinvest.add_invest);
			plantParam.put("billInvestNo", billinvest.bill_invest_no);
			addDeal = dealPlatformService.addPlatformDeal(billinvest.user_id, billinvest.add_invest,t_deal_platform.DealRemark.ADD_RATE, plantParam);
			
			if (!addDeal) {
				result.code = -1;
				result.msg = "添加平台收支记录失败";
				
				return result;
			}
		}
		
		if(billinvest.reward_invest > 0){
			
			Map<String, Object> plantParam = new HashMap<String, Object>();
			plantParam.put("rateAmt", billinvest.reward_invest);
			plantParam.put("billInvestNo", billinvest.bill_invest_no);
			addDeal = dealPlatformService.addPlatformDeal(billinvest.user_id, billinvest.add_invest,t_deal_platform.DealRemark.REWARD_INVEST, plantParam);
			
			if (!addDeal) {
				result.code = -1;
				result.msg = "添加平台收支记录失败";
				
				return result;
			}
		}
		
		result.code = 1;
		result.msg = "加息金额发放成功";
		
		return result;
	}
	
	/**
	 * 更新加息金额发放状态
	 *  
	 * @param invest
	 * @param real_add_invest
	 * @return
	 */
	public boolean updateBillRealAddInvest(t_bill_invest invest,double real_add_invest){
		boolean flag = false;
		if(null == invest){
			return flag;
		}
		
		String sql = "update t_bill_invest set real_add_invest = :real_add_invest where id=:id AND status = :status AND rate_status = :old_rate_status " ;
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("real_add_invest", real_add_invest);
		maps.put("id", invest.id);
		maps.put("status", t_bill_invest.Status.RECEIVED.code);
		maps.put("old_rate_status",t_bill_invest.Ratestatus.PROCESSING.code);
		
		int row = billInvestDao.updateBySQL(sql, maps);
		
		if(row>0){
			flag = true;
		}
		
		return flag;
	}
}
