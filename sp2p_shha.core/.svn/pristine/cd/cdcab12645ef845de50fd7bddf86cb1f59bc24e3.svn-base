package models.core.entity;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 加息券发送任务表
 * @author pc
 *
 */
@Entity
public class t_addrate_task extends Model{
	
	
	/** 任务名（年+月+日+时+分+秒+发放类型） */
	public String name;
	
	/** 任务标识 */
	public long identification;
	
	/** 发放总人数 */
	public long total_number;
	
	/** 已发放的人数 */
	public long is_send_number;
	
	/** 该批次发放最后的用户id */
	public long last_user_id;
	
	/** 最近一次发送任务中最后的用户id */
	public long current_user_id;
	
	/** 任务执行状态(0、执行中 ， 1、已完成) */
	public int status;
	
	/**发送任务类型(0、群发  ,  1、用户选择) */
	public int send_type;
	
	/** 若send_type=1时，使用用户id来拼接字符串，中间用","间隔 */
	public String user_id_str;
	
	/**关联加息劵id*/
	public long addrate_id;
	
	/**
	 * 枚举:任务状态
	 *
	 * @description 
	 *
	 */
	public enum TaskStatus {
		/** 0:执行中 */
		EXECUTE(0, "执行中"),
		
		/** 1:已完成 */
		COMPLETE(1, "已完成");
		
		public int code;
		
		public String value;
		
		private TaskStatus(int code, String value) {
			this.code = code;
			this.value = value;
		}
		
		public static TaskStatus getEnum(int code){
			TaskStatus[] ts = TaskStatus.values();
			for (TaskStatus t : ts) {
				if (t.code == code) {
					return t;
				}
			}
			return null;
		}
	}
	
	/**
	 * 枚举:任务发送类型
	 *
	 * @description 
	 *
	 */
	public enum TaskSendType {
		/** 0:群发 */
		MASS(0, "群发"),
		
		/** 1:用户选择 */
		CHOOSE(1, "用户选择");
		
		public int code;
		
		public String value;
		
		private TaskSendType(int code, String value) {
			this.code = code;
			this.value = value;
		}
		
		public static TaskSendType getEnum(int code){
			TaskSendType[] tst = TaskSendType.values();
			for (TaskSendType t : tst) {
				if (t.code == code) {
					return t;
				}
			}
			return null;
		}
	}
	
	
	
}
