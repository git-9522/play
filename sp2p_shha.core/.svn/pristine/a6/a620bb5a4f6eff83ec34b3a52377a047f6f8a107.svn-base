package daos.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daos.base.BaseDao;
import models.core.entity.t_bid_item_supervisor;

/**
 * 管理员上传审核科目资料DAO
 *
 * @description 
 *
 * @author yaoyi
 * @createDate 2016年3月7日
 */
public class BidItemSupervisorDao extends BaseDao<t_bid_item_supervisor> {
	
	protected BidItemSupervisorDao(){}
	
	/**
	 * 管理员删除审核科目资料
	 *
	 * @param bidItemSupervisorId 管理员上传审核科目表id
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月19日
	 */
	public int delBidItemSupervisor(long bidItemSupervisorId) {
		
		return super.delete(bidItemSupervisorId);
	}
	
	/**
	 * 查询这个标的这个科目下传的审核资料
	 *
	 * @param bid_id 标的id
	 * @param bid_audit_subject_id 审核科目资料id
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月19日
	 */
	public List<t_bid_item_supervisor> queryBidItemSupervisor(long bid_id, long bid_audit_subject_id){
		
		String sql = "SELECT * FROM t_bid_item_supervisor WHERE bid_id=:bid_id AND bid_audit_subject_id=:bid_audit_subject_id";
		Map<String, Object>params = new HashMap<String, Object>();
		params.put("bid_id", bid_id);
		params.put("bid_audit_subject_id", bid_audit_subject_id);
		
		return super.findListBySQL(sql, params);
	}
	
}
