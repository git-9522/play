package models.core.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import models.core.entity.t_red_packet_user.RedpacketStatus;

@Entity
public class RedpacketRecord implements Serializable {
	
	/** t_red_packet_user表ID */
	@Id
	public long id;
	
	/** 用户名 */
	public String name;
	
	/** 使用规则 */
	public double employ_rule;
	
	/** 发放时间 */
	public Date time;
	
	/** 红包状态：0-未使用、1-使用中、2-已使用、3-已过期 */
	public int status;
	
	/** 到期时间 */
	public Date end_time;
	
	/** 投资金额 */
	public double invest_amount;
	
	/** 红包金额 */
	public double amount;
	
	public RedpacketStatus getStatus() {
		RedpacketStatus status = RedpacketStatus.getEnum(this.status);
		
		return status;
	}
	
	public void setStatus(RedpacketStatus status) {
		this.status = status.code;
	}
	
	/** 红包ID */
	public long red_packet_id;
	
	/** 红包类型 */
	public int type;
	
	/** 红包类型名称 */
	@Transient
	public String type_name;
	
	public String getType_name() {
		String typeName = "";
		
		switch (this.type) {
			case 1:
				typeName = "开户赠送";
				break;
			case 2:
				typeName = "批量发放";
				break;
			case 3:
				typeName = "积分兑换";
				break;
			default:
				break;
		}
		
		return typeName;
	}
	
}
