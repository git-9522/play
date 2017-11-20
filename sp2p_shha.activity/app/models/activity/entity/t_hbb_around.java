package models.activity.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 虹宝宝加油费发放记录
 * 
 * @description 10月 虹宝宝全国游（记录）
 * @author djk
 * @createDate 2017年9月21日
 */
@Entity
public class t_hbb_around extends Model {

	/** 创建时间 */
	public Date time;
	
	/** 当前发放次数 */
	public int this_count;
	
	/** 截止当前发放次数 */
	public int count;
	
	/** 本次为虹宝宝添加汽油(公里汽油) */
	public int add_trip;
	
	/** 本次获得总加油费 */
	public double value;

	/** 用户id */
	public long user_id;
	
	/** 状态，是否分发 */
	public boolean status;
}
