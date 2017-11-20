package models.ext.cps.bean;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * cps推广记录
 *
 * @author liudong
 * @createDate 2016年3月16日
 */
@Entity
public class CpsSpreadRecord {
	@Id
	public Long id;
	
	/** 创建时间 */
	public Date time;
	
	/** 会员编号 */
	public long user_id;
	
	/** 会员昵称(被推广人昵称) */
	public String user_name;
	
	/** 推广人昵称 */
	public String spreader_name;
	
	/** 推广人手机 */
	public String spreader_mobile;
	
	/** 是否为渠道 */
	public boolean is_spread;
	
	/** 累计理财 */
	public double total_invest;
	
	/** 累计返佣 */
	public double total_rebate;

}
