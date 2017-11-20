package models.bean;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * 托管对账bean
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2016年2月15日
 */
@Entity
public class UserFundCheck {
	@Id
	public long id;
	public String userName;
	public String account;
	public double systemBlance;
	public double systemFreeze;
	@Transient
	public double pBlance;
	@Transient
	public double pFreeze;
	//状态，0正常，1异常
	@Transient
	public int status;
	
	public int getStatus(){
		if (compareBlance() && compareFreeze()){
			this.status = 0;
			
		}
		else{
			this.status = 1;
		}
		return this.status;
	}
	
	public boolean compareBlance(){
		if (Double.compare(this.systemBlance, this.pBlance) == 0){
			return true;
		}
		else{
			return false;
		}
	}
	
	public boolean compareFreeze(){
		if (Double.compare(this.systemFreeze, this.pFreeze) == 0){
			return true;
		}
		else{
			return false;
		}
	}
}
