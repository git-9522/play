package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 签到规则表
 *
 * @author YanPengFei
 * @createDate 2017年2月24日
 */
@Entity
public class t_sign_in_rule extends Model {

	/** 添加时间 */
	public Date time;
	
	/** 名称 */
	public String name;
	
	/** 连续天数唯一标识 */
	public String _key;
	
	/** 赠送积分 */
	public double score;
	
	/** 额外加成积分 */
	public double extra_score;
	
}
