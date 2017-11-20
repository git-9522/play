package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 会员分组关系信息表
 * 
 * @description 
 *
 * @author jiayijian
 * @createDate 2017年3月31日
 */
@Entity
public class t_group_menbers_user extends Model {

	/** 创建时间 */
	public Date time;

	/** 分组名称 */
	public String group_name;

	/**关联用户ID */
	public long user_id;

	/**会员分组ID */
	public long group_id;
}
