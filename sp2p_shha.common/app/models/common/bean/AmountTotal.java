package models.common.bean;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class AmountTotal {

	@Id
	@GeneratedValue
	public Long id;
	
	/**投资金额总计*/
	public double amount_sum;
	
	/**利息金额总计*/
	public double correct_interest_sum;
	
	/**红包金额总计 */
	public double red_packet_amount_sum;
}
