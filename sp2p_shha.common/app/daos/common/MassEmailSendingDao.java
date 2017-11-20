package daos.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.constants.Constants;
import common.enums.NoticeScene;
import daos.base.BaseDao;
import models.common.entity.t_mass_email_sending;

/**
 * 群发邮件的临时表dao
 * 
 * @author liudong
 * @createDate 2016年4月8日
 */
public class MassEmailSendingDao extends BaseDao<t_mass_email_sending> {

	protected MassEmailSendingDao(){
		
	}
	
	/**
	 * 往邮件群发临时表中批量插入数据
	 *
	 * @param member_type  会员类型  -1-全部会员  0-新会员   1-理财会员    2-借款会员
	 * @param title 邮件标题
	 * @param content 邮件内容
	 * @param scene 场景
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年7月18日
	 */
	public int sendMassEamil(int member_type,String title,String content,NoticeScene scene) {
		/*
		
		1.场景不可配置，全部用户(26)
		insert into t_mass_email_sending(time,email,title,content,is_send,try_times)
		select now(),ui.email,"测试的内容",0,0 from t_user_info ui 
		where ui.email is not null;
		
		2.场景不可配置，不为全部用户(20)
		insert into t_mass_email_sending(time,email,title,content,is_send,try_times)
		select now(),ui.email,"测试的内容",0,0 from t_user_info ui 
		where ui.email is not null and ui.member_type=3;
		
		3.场景可配置，全部用(24)
		insert into t_mass_email_sending(time,email,title,content,is_send,try_times)
		select now(),ui.email,"测试的内容",0,0 from t_user_info ui 
		where ui.email is not null 
		and not EXISTS (select id from t_notice_setting_user nsu where  nsu.scene=68 and nsu.email=0 and nsu.user_id=ui.user_id)
		
		
		4.场景可配置，不为全部用(19)
		insert into t_mass_email_sending(time,email,title,content,is_send,try_times)
		select now(),ui.email,"测试的内容",0,0 from t_user_info ui 
		where ui.email is not null 
		and ui.member_type=3
		and not EXISTS (select id from t_notice_setting_user nsu where  nsu.scene=68 and nsu.email=0 and nsu.user_id=ui.user_id)

		 */
		
		Map<String, Object> condition = new HashMap<String, Object>();
		
		int a = 0;
		if (!scene.maskable) {
			if (member_type == -1) {
				//1.场景不可配置，全部用户(26)
				
				String sql = "INSERT INTO t_mass_email_sending(time,email,title,content,is_send,try_times) SELECT now(),ui.email,:title, :content,0,0 FROM t_user_info ui  WHERE ui.email IS NOT NULL ";
				
				condition.put("content", content);
				condition.put("title", title);
				a = updateBySQL(sql, condition);
			} else {
				//2.场景不可配置，不为全部用户(20)
				String sql = " INSERT INTO t_mass_email_sending(time,email,title,content,is_send,try_times) SELECT now(),ui.email,:title,:content,0,0 FROM t_user_info ui  WHERE ui.email IS NOT NULL AND ui.member_type= :memberType ";
				
				condition.put("content", content);
				condition.put("title", title);
				condition.put("memberType", member_type);
				a = updateBySQL(sql, condition);
			}
		} else {
			if (member_type == -1) {
				//3.场景可配置，全部用户(24)
				String sql = " INSERT INTO t_mass_email_sending(time,email,title,content,is_send,try_times) SELECT now(),ui.email,:title,:content,0,0 FROM t_user_info ui  WHERE ui.email IS NOT NULL AND NOT EXISTS (SELECT id FROM t_notice_setting_user nsu WHERE  nsu.scene=:scene AND nsu.email=0 AND nsu.user_id=ui.user_id) ";
				
				condition.put("content", content);
				condition.put("title", title);
				condition.put("scene", scene.code);
				a = updateBySQL(sql, condition);
				
			} else {
				//4.场景可配置，不为全部用户(19)
				String sql = " INSERT INTO t_mass_email_sending(time,email,title,content,is_send,try_times) SELECT now(),ui.email,:title,:content,0,0 FROM t_user_info ui  WHERE ui.email IS NOT NULL  AND ui.member_type=:memberType AND NOT EXISTS (SELECT id FROM t_notice_setting_user nsu WHERE  nsu.scene= :scene AND nsu.email=0 AND nsu.user_id=ui.user_id) ";
				
				condition.put("content", content);
				condition.put("title", title);
				condition.put("memberType", member_type);
				condition.put("scene", scene.code);
				a = updateBySQL(sql, condition);
			}
		}
	
		return a;
	}
	
	/**
	 * 删除群发邮件表中 已经发送的记录
	 * 
	 * @author liudong
	 * @createDate 2016年4月5日
	 *
	 */
	public int deleteMassSended(){
		String excuSQL = "DELETE FROM t_mass_email_sending WHERE is_send=:is_send OR try_times= :try_times ";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("is_send", true);
		condition.put("try_times", Constants.TRY_EMAIL_TIMES);
		
		return deleteBySQL(excuSQL, condition);
	}
	
	/**
	 * 
	 * 群发邮件、选择没有发送成功的邮件
	 * 
	 * @param
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年4月9日
	 *
	 */
	public List<t_mass_email_sending> queryLastUnsendedMassEmail(int num){
		String querySQL = "SELECT * FROM t_mass_email_sending WHERE is_send=:is_send AND try_times <= :try_times LIMIT :num ";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("is_send", false);
		condition.put("try_times", Constants.TRY_EMAIL_TIMES);
		condition.put("num", num);
		
		return findListBySQL(querySQL, condition);
	}
}
