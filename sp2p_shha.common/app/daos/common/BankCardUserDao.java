package daos.common;

import java.util.HashMap;
import java.util.Map;

import common.utils.LoggerUtil;
import common.utils.PageBean;
import daos.base.BaseDao;
import models.common.entity.t_bank_card_user;

/**
 * 用户银行账户信息dao接口
 * 
 * @description 
 *
 * @author ChenZhipeng
 * @createDate 2015年12月23日
 */
public class BankCardUserDao extends BaseDao<t_bank_card_user> {

	protected BankCardUserDao() {}
	
	/**
	 * 查询用户银行卡列表
	 * 
	 * @param pageSize
	 * @param currPage
	 * @param userId
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月23日
	 *
	 */
	public PageBean<t_bank_card_user> pageOfUserCard(int currPage, int pageSize, long userId) {

		return super.pageOfByColumn(currPage, pageSize, "user_id=? ORDER BY status", userId);
	}

	/**
	 * 修改用户银行卡状态
	 * 
	 * @param id 银行卡id
	 * @param userId 用户id
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月23日
	 *
	 */
	public int updateCardStatus(long id, long userId) {
		
		/*通过userid修改数据*/
		String byStatusSql = "UPDATE t_bank_card_user SET status = 2 WHERE status = 1 AND user_id =:userId";
		Map<String,Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);
		int isByStatus = updateBySQL(byStatusSql, condition);
		if (isByStatus < 0) {
			
			return -1;
		}
		
		/*通过id修改数据*/
		String byIdSql = "UPDATE t_bank_card_user SET status = 1 WHERE id =:id";
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("id", id);
		int isByIdSql = updateBySQL(byIdSql, map);
		if (isByIdSql < 0) {
			LoggerUtil.error(true, "数据更新失败");
			
			return -1;
		}
		
		return isByIdSql;
	}

	/**
	 * 根据银行卡号查询用户银行账户信息
	 * 
	 * @param bankAccount 银行卡号
	 * @return
	 *
	 * @author YanPengFei
	 * @createDate 2016年08月01日
	 *
	 */
	public t_bank_card_user findBankCardUserByBankAccount(String bankAccount) {
		String sql = "SELECT * FROM t_bank_card_user WHERE account =:bankAccount";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("bankAccount", bankAccount);

		return findBySQL(sql, condition);
	}
}
