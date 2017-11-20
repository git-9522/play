package daos.core;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import daos.base.BaseDao;
import models.core.entity.t_red_packet_transfer;

/**
 * 红包转账dao
 *
 * @description 
 *
 * @author YanPengFei
 * @createDate 2017年2月20日
 */
public class RedPacketTransferDao extends BaseDao<t_red_packet_transfer> {

	protected RedPacketTransferDao() {}
	
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
		String updateSQL = "update t_red_packet_transfer set status = :newStatus, complete_time = :completeTime, order_no = :orderNo where id = :transferRecordId and status = :oldStatus";
		
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("newStatus", newStatus);
		args.put("completeTime", completeTime);
		args.put("orderNo", orderNo);
		args.put("transferRecordId", transferRecordId);
		args.put("oldStatus", oldStatus);
		
		return super.updateBySQL(updateSQL, args);
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
	public int updateTransferRecord(long transferRecordId, int newStatus, int oldStatus) {
		String updateSQL = "update t_red_packet_transfer set status = :newStatus where id = :transferRecordId and status = :oldStatus";
		
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("newStatus", newStatus);
		args.put("transferRecordId", transferRecordId);
		args.put("oldStatus", oldStatus);
		
		return super.updateBySQL(updateSQL, args);
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
		String sql = "SELECT * FROM t_red_packet_transfer WHERE id = :id";
		
		Map<String,Object> condition = new HashMap<String, Object>();
		condition.put("id", id);
		
		return findBySQL(sql, condition);
	}
	
}
