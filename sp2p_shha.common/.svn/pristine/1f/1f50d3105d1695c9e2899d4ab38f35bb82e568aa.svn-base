package models.common.bean;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Security;

/**
 * 用户信息显示bean
 * 用于显示用户基本信息、资金相关信息、理财信息等
 * @description 
 *
 * @author ChenZhipeng
 * @createDate 2015年12月31日
 */
@Entity
public class ShowGroupMenberInfo {

	@Id
	/** 成员记录id */
	public long id;
	
	/** 操作时间 */
	public Date time; 
	
	/** 用户名 */
	public String name;
	
	/** 手机号码 */
	public String mobile;
	
	/** 邮箱 */
	public String email;
	
	/** 可用余额 */
	public double balance;
	
	/** 冻结资金 */
	public double freeze;
	
	/** 登录次数 */
	public int login_count;
	
	/** 最后登录时间 */
	public Date last_login_time;
	
	/** 是否允许登录 */
	public boolean is_allow_login;
	
	/** 待还 */
	public double no_repayment;
	
	/** 待收 */
	public double no_receive;
	
	/** 累计借款 */
	public double borrow_total;
	
	/** 累计投资 */
	public double invest_total;
	
	public ShowGroupMenberInfo(){}

	@Transient
	public String sign;//加密ID

	public String getSign() {
		return Security.addSign(this.id, Constants.USER_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
	}
	
	
}
