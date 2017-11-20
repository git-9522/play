package daos.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import common.utils.PageBean;
import daos.base.BaseDao;
import models.core.entity.t_red_packet;

/**
 * 红包dao
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年2月14日
 */
public class RedpacketDao extends BaseDao<t_red_packet> {

	protected RedpacketDao() {
	}
	
	/**
	 * 查询所有红包规则
	 * 
	 * @param type 红包类型
	 * @param isUse 是否启用
	 * @return
	 */
	public List<t_red_packet> findAllRedPacketRules(int type, boolean isUse) {
		
		return super.findListByColumn(" type = ? and is_use = ?", type, isUse);
	}

	public int findAllRedPacketRuleCount(int type, boolean isUse) {
		return super.countByColumn(" type = ? and is_use = ?", type, isUse);
	}
	
	/**
	 * 更新启用状态
	 * 
	 * @param id 红包规则id
	 * @param isUse 是否启用
	 * @return
	 */
	public int changeIsUseStatus(long id, boolean isUse) {
		String updateSql = " t_red_packet set is_use = :isUse where id = :id";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("id", id);
		condition.put("isUse", isUse);
		
		return this.updateBySQL(updateSql, condition);
	}
	
	/**
	 * 查询所有红包规则id
	 * 
	 * @param type 红包类型
	 * @param isUse 是否启用
	 * @return
	 */
	public List<Object> findAllRedPacketRuleId(int type, boolean isUse) {
		String sql = "select id from t_red_packet where type = :type and is_use = :isUse";
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("type", type);
		args.put("isUse", isUse);
		
		return super.findListSingleBySQL(sql, args);
	}
	
	/**
	 * 更新红包规则
	 * 
	 * @param redPacketRuleId 红包规则id
	 * @param amount 红包金额
	 * @param useRule 使用规则
	 * @param bidPeriod 使用规则：标的期限
	 * @param endTime 有效期限
	 * @param letter 是否启用站内信通知
	 * @param email 是否启用邮件通知
	 * @param sms 是否启用手机通知
	 * @return
	 */
	public int updateRedPacketRule(long redPacketRuleId, double amount, double useRule,int bidPeriod, int endTime, boolean letter, boolean email, boolean sms) {
		String sql = "update t_red_packet set amount = :amount, use_rule = :useRule,bid_period = :bidPeriod, end_time = :endTime, "
				+ "is_send_letter = :letter, is_send_email = :email, is_send_sms = :sms where id = :id";
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("amount", amount);
		args.put("useRule", useRule);
		args.put("bidPeriod", bidPeriod);
		args.put("endTime", endTime);
		args.put("letter", letter);
		args.put("email", email);
		args.put("sms", sms);
		args.put("id", redPacketRuleId);
		
		return super.updateBySQL(sql, args);
	}

	public PageBean<t_red_packet> pageOfRedPacket(int currPage, int pageSize, int type, Boolean isUse, String numNo) {
		
		StringBuffer resultSQL = new StringBuffer("SELECT * FROM t_red_packet WHERE 1=1 ");
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(1) FROM t_red_packet WHERE 1=1 ");
		
		Map<String, Object> args = new HashMap<String, Object>();
		
		/** 按编号搜索  */
		if(StringUtils.isNotBlank(numNo)){
			resultSQL.append(" AND id = :packetId");
			countSQL.append(" AND id = :packetId");
			args.put("packetId", numNo.trim());
		}
		
		if(type != 0) {
			resultSQL.append(" AND type = :type");
			countSQL.append(" AND type = :type");
			args.put("type", type);
		}
		
		if(isUse != null) {
			resultSQL.append(" AND is_use = :is_use");
			countSQL.append(" AND is_use = :is_use");
			args.put("is_use", isUse);
		}
		
		resultSQL.append(" order by id desc ");
		
		return this.pageOfBySQL(currPage, pageSize, countSQL.toString(), resultSQL.toString(), args);
	}

	public int updateStatus(long id, boolean isUse) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("is_use", isUse);
		map.put("id", id);
		
		return this.updateBySQL("UPDATE t_red_packet SET is_use=:is_use WHERE id=:id", map);
	}

}
