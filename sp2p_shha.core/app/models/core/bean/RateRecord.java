package models.core.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import models.core.entity.t_addrate_user.RateStatus;
import models.core.entity.t_addrate_user.RateType;
/**
 * 后台-推广-加息卷发放记录的bean
 *
 * @description 
 *
 * @author jiayijian
 * @createDate 2017年4月5日
 */
@Entity
public class RateRecord implements Serializable {
	
	/** t_addrate_user表ID */
	@Id
	public long id;
	
	/** 用户名 */
	public String name;
	
	/** 最低投资（投资金额必须大于或等于该值） **/
	public double use_rule;
	
	/** 发放时间 */
	public Date time;
	
	/** 加息券状态：\r\n0.未使用\r\n、1.使用中\r\n、2.已使用\r\n、3.已过期 **/
	public int status;
	
	/** 到期时间 */
	public Date end_time;
	
	/** 投资金额 */
	public double invest_amount;
	
	/** 加息利率 **/
	public double rate;
	
	/** 加息卷来原id:0-批量直接发放，其它来自批量定时任务或积分兑换 **/
	public long send_id;
	
	/** 加息券类型：1.批量、2.兑换 **/
	public int type;
	
	public RateStatus getRateStatus() {
		RateStatus status = RateStatus.getEnum(this.status);
		
		return status;
	}
	
	public RateType getRateType() {
		RateType status = RateType.getEnum(this.type);
		
		return status;
	}
}
