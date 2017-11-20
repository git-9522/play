package models.wechat.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import common.enums.Client;
import common.utils.StrUtil;

/***
 * 投标记录
 *
 * @description 
 *
 * @author luzhiwei
 * @createDate 2016-5-5
 */

@Entity
public class InvestRecordBean implements Serializable{
	
	@Id
	public long id;
	
	
	/** 用户名 */
	public String name;
	
	/** 投标金额 */
	public double amount;
	
	/** 投标端 */
	public int client;
	
	/** 投标时间 */
	public Date time;

/*	public String getClient() {
		Client client = Client.getEnum(this.client);
		
		return client == null ? null : client.value;
	}*/
	
	/*public String getTime() {
		return DateUtil.getTimeLine(this.time);
	}*/

	@Transient
	public String telName;
	
	public String getTelName(){
		String str = this.name.substring(2);
		return StrUtil.asterisk(str, 3, 2, 6);
	}
	
}
