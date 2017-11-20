package services.core;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import common.utils.Factory;
import common.utils.ResultInfo;
import daos.core.RedPacketTransferDao;
import models.common.entity.t_deal_platform;
import models.common.entity.t_deal_user;
import models.common.entity.t_user_fund;
import models.core.entity.t_red_packet_transfer;
import services.base.BaseService;
import services.common.DealPlatformService;
import services.common.DealUserService;
import services.common.UserFundService;

public class RedPacketTransferService extends BaseService<t_red_packet_transfer> {

	protected RedPacketTransferDao redPacketTransferDao = null;
	
	protected static UserFundService userFundService = Factory.getService(UserFundService.class);
	
	protected static DealUserService dealUserService = Factory.getService(DealUserService.class);
	
	protected static DealPlatformService dealPlatformService = Factory.getService(DealPlatformService.class);
	
	protected RedPacketTransferService() {
		redPacketTransferDao = Factory.getDao(RedPacketTransferDao.class);
		super.basedao = redPacketTransferDao;
	}
	
	/**
	 * 添加红包转账记录
	 * 
	 * @param redPacketUserId
	 * @param redPacketAmount
	 * @param orderNo
	 * 
	 * @return
	 */
	public ResultInfo addRedPacketTransferRecord(long userId, long redPacketUserId, double redPacketAmount) {
		ResultInfo result = new ResultInfo();
		
		t_red_packet_transfer transfer = new t_red_packet_transfer();
		transfer.user_id = userId;
		transfer.red_packet_user_id = redPacketUserId;
		transfer.time = new Date();
		transfer.amount = redPacketAmount;
		transfer.setStatus(t_red_packet_transfer.Status.TRANSFERING);
		transfer.complete_time = null;
		transfer.order_no = "";
		transfer = redPacketTransferDao.saveT(transfer);
		
		if (transfer == null) {
			result.code = -1;
			result.msg = "添加转账记录失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "";
		result.obj = transfer;
		
		return result;
	}
	
	/**
	 * 更新转账记录信息
	 * 
	 * @param transferRecordId 
	 * @param newStatus 
	 * @param completeTime 
	 * @param oldStatus 
	 * @param orderNo
	 * 
	 * @return
	 */
	public int updateTransferRecord(long transferRecordId, int newStatus, Date completeTime, int oldStatus, String orderNo) {
		
		return redPacketTransferDao.updateTransferRecord(transferRecordId, newStatus, completeTime, oldStatus, orderNo);
	}
	
	/**
	 * 更新转账记录信息
	 * 
	 * @param transferRecordId 
	 * @param newStatus 
	 * @param completeTime 
	 * @param oldStatus 
	 * @param orderNo
	 * 
	 * @return
	 */
	public int updateTransferRecord(long transferRecordId, int newStatus,  int oldStatus) {
		
		return redPacketTransferDao.updateTransferRecord(transferRecordId, newStatus,  oldStatus);
	}
	
	/**
	 * 红包转账本地业务执行
	 * 
	 * @param transferRecordId
	 * @param userId
	 * @param orderNo
	 * @param tempState 定时任务，临时标记
	 * 
	 * @return
	 */
	public ResultInfo doTransfer(long transferRecordId, String orderNo,int tempState) {
		ResultInfo result = new ResultInfo();
		
		if (transferRecordId <= 0L) {
			result.code = -1;
			result.msg = "转账记录数据异常";
			
			return result;
		}
		
		if (StringUtils.isBlank(orderNo)) {
			result.code = -1;
			result.msg = "交易订单号数据异常";
			
			return result;
		}
		
		t_red_packet_transfer transfer = findTransferById(transferRecordId);
		
		if (transfer == null) {
			result.code = -1;
			result.msg = "转账记录不存在";
			
			return result;
		}
		
		int rows = 0;
		if(tempState == 6){
			
			rows = updateTransferRecord(transfer.id, t_red_packet_transfer.Status.TRANSFERED.code, new Date(), 
					tempState, orderNo);
			
		}else{
			
			rows = updateTransferRecord(transfer.id, t_red_packet_transfer.Status.TRANSFERED.code, new Date(), 
					t_red_packet_transfer.Status.TRANSFERING.code, orderNo);
		}
		
		
		if (rows <= 0) {
			result.code = -1;
			result.msg = "更新转账记录失败";
			
			return result;
		}
		
		boolean isSignSuccess = true; //用户签名是否通过
		
		result = userFundService.userFundSignCheck(transfer.user_id);
		if (result.code < 1) {
			isSignSuccess = false;
		}
		
		boolean addFund = userFundService.userFundAdd(transfer.user_id, transfer.amount);
		if (!addFund) {
			result.code = -1;
			result.msg = "增加用户可用余额失败";
			
			return result;
		}
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(transfer.user_id);
		if (userFund == null) {
			result.code = -1;
			result.msg = "获取用户资金信息失败";
			
			return result;
		}
		
		//如果用户资金没有遭到非法改动，那么就更新其防篡改sign，否则不更新
		if (isSignSuccess) {
			userFundService.userFundSignUpdate(transfer.user_id);
		}
		
		//添加用户入账记录
		boolean addDeal = dealUserService.addDealUserInfo(
				orderNo, 
				transfer.user_id, 
				transfer.amount, 
				userFund.balance, 
				userFund.freeze, 
				t_deal_user.OperationType.RED_PACKET_TRANSFER, 
				null);
		if (!addDeal) {
			result.code = -1;
			result.msg = "添加用户入账记录失败";
			
			return result;
		}
		
		//添加平台支出记录
		addDeal = dealPlatformService.addPlatformDeal(
				transfer.user_id, 
				transfer.amount, 
				t_deal_platform.DealRemark.RED_PACKET_TRANSFER, 
				null);
			if (!addDeal) {
			result.code = -1;
			result.msg = "添加平台支出记录失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "";
		
		return result;
	}
	
	/**
	 * 查询转账记录
	 * 
	 * @param id
	 * @return
	 *
	 * @author YanPengFei
	 * @createDate 2017年2月20日
	 */
	public t_red_packet_transfer findTransferById(long id) {
		
		return redPacketTransferDao.findTransferById(id);
	}
	
}
