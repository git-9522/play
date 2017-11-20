package models.common.bean;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * @author panshaobin
 * 2016年10月18日
 */
@Entity
public class RedPacketRecord {

	@Id
	public long id;
	
	/**关联用户ID*/
	public long user_id;
	
	/**会员名*/
	public String name;
	
	/**红包类型*/
	public String red_packet_name;
	
	/**金额*/
	public double amount;
	
	/**时间*/
	public Date time;
	
	/**状态*/
	public int status;
	
	@Transient
	public String statusName;

	public String getStatusName() {
		switch (status) {
		case 0:
			statusName = "未激活";
			break;
		case 1:
			statusName = "未领取";
			break;
		case 2:
			statusName = "已领取";
			break;
		case 3:
			statusName = "已使用";
			break;
		case -1:
			statusName = "已过期";
			break;
		default:
			statusName = "使用中";
			break;
		}
		
		return statusName;
	}
	
	
	
}