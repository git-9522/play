package models.activity.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 *  11月活动6 （复投参与）
 * @Title t_invert_activity6_log 
 * @Description 千里姻缘一线牵、俊男美女任您选（抽奖记录）
 * @author hjs_djk
 * @createDate 2017年10月23日 下午5:49:45
 */
@Entity
public class t_invert_11_activity6_log  extends Model{
	/**
	 * 创建时间
	 */
	public Date time;
	
	/**
	 * 用户id
	 */
	public Long user_id;
	
	
	/**
	 * 用户名
	 */
	public String user_name;
	
	
	/**
	 * 投资记录ID
	 */
	public Long invest_id;

	
	/**
	 * 投资金额
	 */
	public double amount;
	
	/**
	 * 投资奖励
	 */
	public double value;
	
	
	/**
	 * 历史人物id
	 */
	public int cid;
	/** 
	 * 状态，是否分发
	 */
	public boolean status;

}
