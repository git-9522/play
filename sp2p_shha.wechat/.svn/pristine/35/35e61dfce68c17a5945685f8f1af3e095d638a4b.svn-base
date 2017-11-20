package models.wechat.bean;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * 微信端资产状况 ，点击click返回资产状况
 * 
 * @author liudong
 * @createDate 2016年5月9日
 */
@Entity
public class WXUserFundInfo {

	@Id
	@GeneratedValue
	public long id;
	
	/** 用户昵称 */
	public String name;

	/** 可用余额 */
	public double balance;

	/** 总收益 */
	public double total_income;

	/** 待回款资金笔数 */
	public int no_payment_count;

	/** 成功投资笔数 */
	public int invest_count;

	/** 总资产 */
	@Transient
	public double total_assets;

	/** 我的奖励 */
	public double reward;

	/** 冻结资金 */
	public double freeze;

	/** 待还 */
	public double no_repayment;

	/** 待收 */
	public double no_receive;

}
