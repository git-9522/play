package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Security;
import play.db.jpa.Model;

/**
 * 实体:友情链接
 *
 * @description
 *
 * @author hjs
 * @createDate 2017年7月17日
 */
@Entity
public class t_friend_ship_link extends Model {
	/** 添加时间 */
	public Date time;

	/** 名称 */
	public String name;

	/** 友情链接 */
	public String url;

	/** 排序 */
	public int sort;

	/** 简介 */
	public String description;

	@Transient
	public String sign;

	public String getSign() {
		String signID = Security.addSign(id, Constants.FRIENDSHIPLINK_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
		return signID;
	}

	@Override
	public String toString() {
		return "t_friend_ship_link [time=" + time + ", name=" + name + ", url=" + url + ", sort=" + sort
				+  ", description=" + description + "]";
	}

}
