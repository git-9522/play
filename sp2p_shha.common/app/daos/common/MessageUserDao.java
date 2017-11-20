package daos.common;

import java.util.HashMap;
import java.util.Map;

import common.enums.NoticeScene;
import daos.base.BaseDao;
import models.common.entity.t_message_user;

/**
 *  用户接收系统消息表Dao的具体实现
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月19日
 */
public class MessageUserDao extends BaseDao<t_message_user> {

	protected MessageUserDao() {
	}
	
	/**
	 * 群发站内信(批量插入数据)
	 *
	 * @param member_type 会员类型  -1-全部会员  0-新会员   1-理财会员    2-借款会员
	 * @param msgId 消息id	
	 * @param scene 站内信场景
	 * @return 插入的数量
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年7月18日
	 */
	public int sendMassMsg(int member_type,long msgId,NoticeScene scene) {
		/*
		 1.场景不可配置，全部用户(173)
		insert into t_message_user (time,user_id,message_id,is_read)
		select now(),ui.user_id,100,0 from t_user_info ui
		
		2.场景不可配置，不为全部用户(59)
		insert into t_message_user (time,user_id,message_id,is_read)
		select now(),ui.user_id,100,0 from t_user_info ui where ui.member_type=3
		
		3.场景可配置，全部用户(172)
		insert into t_message_user (time,user_id,message_id,is_read)
		select now(),ui.user_id,100,0 from t_user_info ui 
		where not EXISTS (select id from t_notice_setting_user nsu where  nsu.scene=68 and nsu.msg=0 and nsu.user_id=ui.user_id)
		
		
		4.场景可配置，不为全部用户(58)
		insert into t_message_user (time,user_id,message_id,is_read)
		select now(),ui.user_id,100,0 from t_user_info ui 
		where ui.member_type=3
		and not EXISTS (select id from t_notice_setting_user nsu where  nsu.scene=68 and nsu.msg=0 and nsu.user_id=ui.user_id)

		 */
		
		Map<String, Object> condition = new HashMap<String, Object>();
		
		int a = 0;
		if (!scene.maskable) {
			if (member_type == -1) {
				// 1.场景不可配置，全部用户(173)
				String sql = "INSERT INTO t_message_user (time,user_id,message_id,is_read) SELECT now(),ui.user_id,:msgId,0 FROM t_user_info ui ";
				
				condition.put("msgId", msgId);
				
				a = updateBySQL(sql, condition);
			} else {
				//2.场景不可配置，不为全部用户(59)
				String sql = "INSERT INTO t_message_user (time,user_id,message_id,is_read) SELECT now(),ui.user_id,:msgId,0 FROM t_user_info ui WHERE ui.member_type=:memberType ";
				
				condition.put("msgId", msgId);
				condition.put("memberType", member_type);
				
				a = updateBySQL(sql, condition);
			}
		} else {
			if (member_type == -1) {
				//3.场景可配置，全部用户(172)
				String sql = "INSERT INTO t_message_user (time,user_id,message_id,is_read) SELECT now(),ui.user_id,:msgId,0 FROM t_user_info ui WHERE NOT EXISTS (SELECT id FROM t_notice_setting_user nsu WHERE  nsu.scene= :scene AND nsu.msg=0 AND nsu.user_id=ui.user_id) ";
				
				condition.put("msgId", msgId);
				condition.put("scene", scene.code);
				
				a = updateBySQL(sql, condition);
				
			} else {
				//4.场景可配置，不为全部用户(58)
				String sql = "INSERT INTO t_message_user (time,user_id,message_id,is_read) SELECT now(),ui.user_id,:msgId,0 FROM t_user_info ui WHERE ui.member_type= :memberType AND NOT EXISTS (SELECT id FROM t_notice_setting_user nsu WHERE  nsu.scene= :scene AND nsu.msg=0 AND nsu.user_id=ui.user_id) ";
				
				condition.put("msgId", msgId);
				condition.put("memberType", member_type);
				condition.put("scene", scene.code);
				
				a = updateBySQL(sql, condition);
			}
		}
		
		return a;
	}

	/**
	 * 删除某个用户的某条站内信
	 *
	 * @param userId 用户的id
	 * @param msgId 站内信(msg)的id【不是t_message_user的id，而是message_id】
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月25日
	 */
	public boolean deleteUserMsg(long userId, long msgId) {
		String sql = "DELETE FROM t_message_user WHERE user_id=:user_id AND message_id=:message_id";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("user_id", userId);
		condition.put("message_id", msgId);
		
		int a = super.deleteBySQL(sql, condition);
		
		return a >= 1;
	}
	
}
