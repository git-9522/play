package models.core.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/** 
 * 自动投标设置
 * 
 * @description 
 * 
 * @author ZhouYuanZeng 
 * @createDate 2016年3月29日 上午9:26:50  
 */
@Entity
public class t_auto_invest_setting extends Model {
	
	/** 添加时间 */
	public Date time;
	
	/** 用户Id(投资人) */
	public long user_id;
	
	/** 状态 0 关闭 1 开启*/
	public boolean status;
	
	/** 最低年利率*/
	public double min_apr;
	
	/** 最长投资期限(月) */
	public int max_period;
	
	/** 每笔次投资金额  */
	public double amount;
	
	/** 排队等待时间 */
	public Date wait_time;

}
