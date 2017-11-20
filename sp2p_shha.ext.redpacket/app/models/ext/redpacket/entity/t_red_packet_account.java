package models.ext.redpacket.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 用户的红包账号
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年2月14日
 */
@Entity
public class t_red_packet_account extends Model {

	/** 创建时间 */
	public Date time;
	
	/** 用户ID */
	public Long user_id;
	
	/** 红包金额 */
	public double balance;
	
}
