package models.common.bean;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import common.utils.DateUtil;

/**
 * 用户签到记录(用于前台展示)
 *
 * @author YanPengFei
 * @createDate 2017年2月27日
 */
@Entity
public class SignInUserRecord {
	
	/** 用户签到记录ID */
	@Id
	@GeneratedValue
	public Long id;
	
	/** 添加时间 */
	public Date time;
	
	/** 用户ID */
	public long user_id;
	
	/** 赠送积分 */
	public double score;
	
	/** 额外加成积分 */
	public double extra_score;
	
	/** 签到时的天数 */
	@Transient
	public int day;
	
	public int getDay() {
		
		return DateUtil.getDay(this.time);
	}

}
