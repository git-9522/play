package models.entity;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import play.db.jpa.Model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 资金托管回调日志
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2016年1月6日
 */
@Entity
public class t_payment_call_back extends Model {
	
	/** 回调时间 */
	public Date time;
	
	/** 托管请求唯一标识 */
	public String request_mark;
	
	/** 回调参数 */
	public String cb_params;
	
	/** 回调类型 */
	private int data_type;
	
	
	public void setData_type(DataType dataType){
		this.data_type = dataType.code;
	}
	
	public DataType getData_type(){
		return DataType.getEnum(this.data_type);
	}
	
	@Transient
	public Map<String, String> cb_params_map;
	
	public Map<String, String> getCb_params_map () {
		if (StringUtils.isBlank(this.cb_params)) {
			
			return new LinkedHashMap<String, String>();
		} 
		
		Map<String, String> CBParams = new Gson().fromJson(this.cb_params,
				new TypeToken<LinkedHashMap<String, String>>(){}.getType());

		return CBParams;
	}
 
	/**
	 * 日志类型
	 *
	 * @description 
	 *
	 * @author jyq
	 * @createDate 2016年09月23日
	 */
	public enum DataType {
		
		/** 回调日志 */
		CALLBACK(0, "回调日志"),
		
		/** 查询日志 */
		QUERY(1, "查询日志");
		
		
		/** 类型 */
		public int code;
		
		/** 名称 */
		public String value;
		
		private DataType(int code, String value){
			this.code = code;
			this.value = value;
		}
		
		public static DataType getEnum(int code){
			DataType[] incomes = DataType.values();
			for (DataType income : incomes) {
				if (income.code == code) {

					return income;
				}
			}
			
			return null;
		}
	}
}
