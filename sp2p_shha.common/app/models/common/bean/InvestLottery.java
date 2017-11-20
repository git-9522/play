package models.common.bean;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class InvestLottery {

	@Id
	@GeneratedValue
	public long id;
	
	public Date time;
	
	public long user_id;
	
	public long reward_id;
	
	public String mobile;
	
	public String name;
	
	public double value;
	
	public boolean status;
	
	public Date delivery_time;
	
}