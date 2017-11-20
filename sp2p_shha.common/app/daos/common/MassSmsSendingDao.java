package daos.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.constants.Constants;
import common.enums.NoticeScene;
import daos.base.BaseDao;
import models.common.entity.t_mass_sms_sending;

/**
 * 群发短信临时表dao
 * 
 * @author liudong
 * @createDate 2016年4月8日
 */
public class MassSmsSendingDao extends BaseDao<t_mass_sms_sending> {
	
	protected MassSmsSendingDao() {
		
	}
	
	/**
	 * 往短信群发临时表中批量插入数据
	 *
	 * @param member_type  会员类型  -1-全部会员  0-新会员   1-理财会员    2-借款会员
	 * @param content 短信内容
	 * @param scene 场景
	 * @param type 短信类型
	 * @return 插入的数量
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年7月18日
	 */
	public int sendMassSMS(int member_type,String content,NoticeScene scene, int type) {
		/*
		1.场景不可配置，全部用户(173)
		insert into t_mass_sms_sending(time,mobile,content,is_send,try_times)
		select now(),ui.user_id,"测试的内容",0,0 from t_user_info ui;


		2.场景不可配置，不为全部用户
		insert into t_mass_sms_sending(time,mobile,content,is_send,try_times)
		select now(),ui.user_id,"测试的内容",0,0 from t_user_info ui where  ui.member_type=3;

		3.场景可配置，全部用
		insert into t_mass_sms_sending(time,mobile,content,is_send,try_times)
		select now(),ui.user_id,"测试的内容",0,0 from t_user_info ui
		where 
		not EXISTS (select id from t_notice_setting_user nsu where  nsu.scene=68 and nsu.sms=0 and nsu.user_id=ui.user_id)

		4.场景可配置，不为全部用
		insert into t_mass_sms_sending(time,mobile,content,is_send,try_times)
		select now(),ui.user_id,"测试的内容",0,0 from t_user_info ui
		where ui.member_type=3
		and not EXISTS (select id from t_notice_setting_user nsu where  nsu.scene=68 and nsu.sms=0 and nsu.user_id=ui.user_id)
		*/
		Map<String, Object> condition = new HashMap<String, Object>();
		
		int a = 0;
		
		if (!scene.maskable) {
			if (member_type == -1) {
				//1.场景不可配置，全部用户
				String sql = "INSERT INTO t_mass_sms_sending(time,mobile,content,is_send,try_times,type) SELECT now(),ui.mobile,:content,0,0,:type FROM t_user_info ui ";
				condition.put("content", content);
				condition.put("type", type);
				a = updateBySQL(sql, condition);
				
			} else {
				//2.场景不可配置，不为全部用户
				String sql = "INSERT INTO t_mass_sms_sending(time,mobile,content,is_send,try_times,type) SELECT now(),ui.mobile,:content,0,0,:type FROM t_user_info ui WHERE  ui.member_type= :memberType ";
				
				condition.put("content", content);
				condition.put("memberType", member_type);
				condition.put("type", type);
				a = updateBySQL(sql, condition);
			}
		} else {
			if (member_type == -1) {
				//3.场景可配置，全部用户
				
				String sql = "INSERT INTO t_mass_sms_sending(time,mobile,content,is_send,try_times,type) SELECT now(),ui.mobile, :content,0,0,:type FROM t_user_info ui WHERE NOT EXISTS (SELECT id FROM t_notice_setting_user nsu WHERE  nsu.scene= :scene AND nsu.sms=0 and nsu.user_id=ui.user_id) ";
				condition.put("content", content);
				condition.put("scene", scene.code);
				condition.put("type", type);
				a = updateBySQL(sql, condition);
				
			} else {
				//4.场景可配置，不为全部用户
				String sql = "INSERT INTO t_mass_sms_sending(time,mobile,content,is_send,try_times,type) SELECT now(),ui.mobile,:content,0,0,:type FROM t_user_info ui WHERE ui.member_type= :memberType AND NOT EXISTS (SELECT id FROM t_notice_setting_user nsu WHERE  nsu.scene= :scene AND nsu.sms=0 AND nsu.user_id=ui.user_id) ";
				condition.put("content", content);
				condition.put("memberType", member_type);
				condition.put("scene", scene.code);
				condition.put("type", type);
				a = updateBySQL(sql, condition);
			}
		}
		
		return a;
	}
	
	/**
	 * 查询没有最近没有发送成功的几条
	 * 
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年4月5日
	 *
	 */
	public List<t_mass_sms_sending> queryLastUnsendedMassSms(int num){
		String querySQL = "SELECT *  FROM t_mass_sms_sending WHERE is_send=:is_send AND try_times < :try_times limit :num";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("is_send", false);
		condition.put("try_times", Constants.TRY_SMS_TIMES);
		condition.put("num", num);
		
		return findListBySQL(querySQL, condition);
	}
	
	/**
	 * 删除群发短信中已经发送或者发送失败次数达到上限的短信
	 * 
	 *
	 * @author liudong
	 * @createDate 2016年4月5日
	 *
	 */
	public int deleteMassSended(){
		String excuSQL = "DELETE FROM t_mass_sms_sending WHERE is_send=:is_send OR try_times= :try_times ";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("is_send", true);
		condition.put("try_times", Constants.TRY_SMS_TIMES);
		
		return deleteBySQL(excuSQL, condition);
	}
}
