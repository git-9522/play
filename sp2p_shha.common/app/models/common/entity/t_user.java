package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.enums.Client;
import common.utils.Security;
import common.utils.StrUtil;
import play.db.jpa.Model;

/**
 * 用户表(注册、登录信息)
 *  
 * @description 
 *
 * @author ChenZhipeng
 * @createDate 2015年12月15日
 */

@Entity
public class t_user extends Model {
	
	public t_user(){
	}
	
	public t_user(Date time, String name, String mobile, String password) {
		super();
		this.time = time;
		this.name = name;
		this.mobile = mobile;
		this.password = password;
	}

	/** 创建时间 */
	public Date time;

	/** 用户名 */
	public String name;

	/** 手机号码 */
	public String mobile;

	/** 登录密码 */
	public String password;

	/** 连续登录失败次数 */
	public int password_continue_fails;

	/** 密码连续错误被锁定 */
	public boolean is_password_locked;

	/** 密码连续错误被锁定时间 */
	public Date password_locked_time;

	/** 是否允许登录(锁定) */
	public boolean is_allow_login;

	/** 后台管理员执行锁定的时间 */
	public Date lock_time;

	/** 登录次数 */
	public int login_count;
	
	/**用户密码加密无key */
	public boolean is_no_key;

	/** 上次登录时间 */
	public Date last_login_time;

	/** 上次登录ip */
	public String last_login_ip;

	/** 上次登录入口(枚举)：1 pc 2 app 3 wechat */
	private int last_login_client;
	
	/** 是否是推广人员 */
	public boolean is_spread;
	
	public Client getLast_login_client() {
		Client client = Client.getEnum(last_login_client);
		return client;
	}

	public void setLast_login_client(Client last_login_client) {
		this.last_login_client = last_login_client.code;
	}
	
	/**加密ID*/
	@Transient
	public String sign;

	public String getSign() {
		
		return Security.addSign(this.id, Constants.USER_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
	}
	
	/**app加密ID*/
	@Transient
	public String appSign;
	
	public String getAppSign() {
		
		return Security.addSign(this.id, Constants.USER_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);
	}
	
	/** 是否老用户 */
	public boolean is_old;
	
	public String getTelName() {
		String tName = this.mobile;
		
		return StrUtil.asterisk(tName, 3, 2, 6);
	}
}
