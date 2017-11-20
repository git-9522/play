package dao.wechat;

import java.util.HashMap;
import java.util.Map;

import models.wechat.entity.t_wechat_menu;
import daos.base.BaseDao;

/**
 * 微信菜单dao
 * 
 * @author liudong
 * @createDate 2016年5月5日
 */
public class WeChatMenuDao extends BaseDao<t_wechat_menu> {

	protected WeChatMenuDao() {
	}
	
	/**
	 * 更新微信菜单名称
	 *
	 * @param scale
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年5月11日
	 */
	public int updateWechatMenu(long id,String name){
		String sql = "UPDATE t_wechat_menu SET name =:name WHERE id=:id ";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("name", name);
		condition.put("id", id);
		
		return super.updateBySQL(sql, condition);
	}
}
