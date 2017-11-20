package models.common.bean;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
@Entity
public class UserStatistics{

	@Id
	@GeneratedValue
	public long id;
	/**用户id*/
	/*public Long user_id;*/
	/**用户昵称*/
	public String name;
	/**真实姓名*/
	public String reality_name;
	/**身份证号码*/
	public String id_number;
	/**投资金额*/
	public double amount;
	/**纠偏利息*/
	public double correct_interest;
	/**投资使用的红包金额*/
	public double red_packet_amount;
	
	public Date time;
	
	public String mobile;
	
	public String mobile2;
	
}
