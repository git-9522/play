package daos.common;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import common.constants.ModuleConst;
import common.utils.number.Arith;
import daos.base.BaseDao;
import models.common.entity.t_user_fund;

/**
 * 用户资金dao实现
 * 
 * @description 
 *
 * @author ChenZhipeng
 * @createDate 2015年12月21日
 */
public class UserFundDao extends BaseDao<t_user_fund> {

	protected UserFundDao() {}
	
	/**
	 * 计算平台浮动金
	 * 
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年3月4日
	 *
	 */
	public double findPlatformFloatAmount() {
		String sql = "SELECT SUM(tuf.balance + tuf.freeze) FROM t_user_fund tuf";
		
		return findSingleDoubleBySQL(sql, 0.00, null);
	}
	
	/**
	 * 获取用户可用余额
	 *
	 * @param userId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月14日
	 */
	public double findUserBalance(long userId) {
		
		String sql = "select balance from t_user_fund where user_id=:user_id";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("user_id", userId);
		
		return super.findSingleDoubleBySQL(sql, 0.00, params);
	}

	/**
	 * 根据userId查询用户资金
	 *  
	 * @param userId 用户id
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月22日
	 *
	 */
	public t_user_fund findUserFundByUserId(long userId) {
		String sql="SELECT * FROM t_user_fund WHERE user_id =:user_id";
		Map<String,Object> condition = new HashMap<String, Object>();
		condition.put("user_id", userId);
		
		return findBySQL(sql, condition);
	}
	
	/**
	 * 查询用户的虚拟资产
	 *
	 * @param userId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月17日
	 */
	public double findUserVisualBalance(long userId) {
		
		String sql = "select visual_balance from t_user_fund where user_id=:user_id";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("user_id", userId);
		
		return super.findSingleDoubleBySQL(sql, 0.00, params);
	}
	
	/**
	 * 保存第三方托管账户
	 *
	 * @param userId
	 * @param paymentAccount 第三方托管账户
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月8日
	 */
	public int updatePaymentAccount(long userId, String paymentAccount) {
		
		String sql = "UPDATE t_user_fund SET payment_account = :paymentAccount, is_actived = 1 WHERE user_id = :userId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("paymentAccount", paymentAccount);
		condition.put("userId", userId);
		
		return updateBySQL(sql, condition);
	}
	
	public int updateCorpPaymentAccount(long userId, String paymentAccount) {
		
		String sql = "UPDATE t_user_fund SET payment_account = :paymentAccount, is_actived = 1, is_corp = 1 WHERE user_id = :userId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("paymentAccount", paymentAccount);
		condition.put("userId", userId);
		
		return updateBySQL(sql, condition);
	}
	
	/**
	 * 增加用户可用余额
	 * 
	 * @param userId 用户Id
	 * @param amoutn 变动金额
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月18日
	 *
	 */
	public int updateUserFundAdd(long userId, double amount) {
		String sql = "UPDATE t_user_fund SET  balance = balance + :amount WHERE user_id =:userId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("amount", amount);
		condition.put("userId", userId);
		return updateBySQL(sql,condition);
	}
	
	/**
	 * 冻结用户资金
	 * 
	 * @param userId 用户Id
	 * @param amount 变动金额
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月18日
	 *
	 */
	public int updateUserFundFreeze(long userId, double amount) {
		String sql = "UPDATE t_user_fund SET  balance = balance - :amount, freeze = freeze + :amount WHERE user_id =:userId AND balance >= :amount";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("amount", amount);
		condition.put("userId", userId);

		return updateBySQL(sql,condition);

	}

	/**
	 * 扣除用户可用余额
	 * 
	 * @param userId 用户Id
	 * @param amount 变动金额
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月18日
	 *
	 */
	public int updateUserFundMinus(long userId, double amount) {
		String sql = "UPDATE t_user_fund SET  balance = balance - :amount WHERE user_id =:userId AND balance >= :amount";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("amount", amount);
		condition.put("userId", userId);
		
		return updateBySQL(sql,condition);

	}
	
	/**
	 * 扣除用户冻结金额
	 * 
	 * @param userId 用户Id
	 * @param amount 变动金额
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月18日
	 *
	 */
	public int updateUserFundMinusFreezeAmt(long userId, double amount) {
		String sql = "UPDATE t_user_fund SET  freeze = freeze - :amount WHERE user_id =:userId AND freeze >= :amount";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("amount", amount);
		condition.put("userId", userId);
		
		return updateBySQL(sql,condition);
		
	}


	/**
	 * 更新用户资产签名字段 
	 *
	 * @param userId
	 * @param userFundsign 资产签名
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月23日
	 */
	public int updateUserFundSign(long userId, String userFundsign) {
		String sql = "UPDATE t_user_fund SET fund_sign = :sign WHERE user_id = :userId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("sign", userFundsign);
		condition.put("userId", userId);
		
		return updateBySQL(sql, condition);
	}

	/**
	 * 解冻用户资金 
	 * 
	 * @param userId
	 * @param amount
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月18日
	 *
	 */
	public int updateUserFundUnFreeze(long userId, double amount) {
		String sql = "UPDATE t_user_fund SET  balance = balance + :amount, freeze = freeze - :amount WHERE user_id =:userId AND freeze >= :amount";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("amount", amount);
		condition.put("userId", userId);
		
		return updateBySQL(sql,condition);
	}
	
	/**
	 * 增加用户虚拟余额
	 * 
	 * @param userId 用户Id
	 * @param amoutn 变动金额
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月18日
	 *
	 */
	public int updateUserVisualFundAdd(long userId, double amount) {
		String sql = "UPDATE t_user_fund SET  visual_balance = visual_balance + :amount WHERE user_id =:userId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("amount", amount);
		condition.put("userId", userId);
		return updateBySQL(sql,condition);
	}
	
	/**
	 * 扣除用户的虚拟资产
	 *
	 * @param userId
	 * @param amount
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月16日
	 */
	public int updateUserVisualFundMinus(long userId, double amount) {
		String sql = "UPDATE t_user_fund SET  visual_balance = visual_balance - :amount WHERE user_id =:userId AND visual_balance >= :amount";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("amount", amount);
		condition.put("userId", userId);
		
		return updateBySQL(sql,condition);

	}

	/**
	 * 查询某个用户的折扣
	 * 
	 * @description 代码有耦合，如果后面添加其他的优惠活动，继续往后面添加代码
	 *
	 * @param userId 用户id
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年4月8日
	 */
	public double findUserDiscount(Long userId) {
		double discount = 10.0;
		if (ModuleConst.EXT_CPS) {
			String querySQL = " SELECT id FROM t_cps_user WHERE user_id= :userId ";
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("userId", userId);
			long id = findSingleLongBySQL(querySQL, -1, condition);
			if (id > 0) {
				querySQL = " SELECT cs._value FROM t_cps_setting cs WHERE _key='discount_invest'";
				String cps_discount = findSingleStringBySQL(querySQL, "", null);
				if(StringUtils.isNotBlank(cps_discount)){
					double cpsDiscount = Double.valueOf(cps_discount);
					discount = Arith.div(discount*cpsDiscount, 10, 2);
				}
			}
		}
		if (ModuleConst.EXT_WEALTHCIRCLE) {
			String querySQL = " SELECT id FROM t_wealthcircle_user WHERE user_id = :userId ";
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("userId", userId);
			long id = findSingleLongBySQL(querySQL, -1, condition);
			if (id > 0) {
				querySQL = " SELECT cs._value FROM t_wealthcircle_setting cs WHERE _key='discount_invest'";
				String wc_discount = findSingleStringBySQL(querySQL, "", null);
				if(StringUtils.isNotBlank(wc_discount)){
					double wcDiscount = Double.valueOf(wc_discount);
					discount = Arith.div(discount*wcDiscount, 10, 2);
				}
			}
		}
		
		return discount;
	}

	/**
	 * 通过用户的资管账号查询用户ID
	 * @param paymentAccount 用户资管账号
	 * @return
	 */
	public long findUserIdByPaymentAccount(String paymentAccount) {
		String sql = "select user_id from t_user_fund where payment_account=:paymentAccount";
		
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("paymentAccount", paymentAccount);
		
		return super.findSingleLongBySQL(sql, 0L, args);
	}
	
	/**
	 * 查询用户可用积分
	 * 
	 * @param userId
	 * @return
	 */
	public double findUserScoreBalance(long userId) {
		String sql = "select score_balance from t_user_fund where user_id = :userId";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		
		return super.findSingleDoubleBySQL(sql, 0.00, params);
	}
	
	/**
	 * 增加用户可用积分
	 * 
	 * @param userId
	 * @param score
	 * @return
	 */
	public int updateUserScoreBalanceAdd(long userId, double score) {
		String sql = "UPDATE t_user_fund SET score_balance = score_balance + :score WHERE user_id = :userId";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("score", score);
		condition.put("userId", userId);
		
		return updateBySQL(sql,condition);
	}
	
	/**
	 * 减少用户可用积分
	 * 
	 * @param userId
	 * @param score
	 * @return
	 */
	public int updateUserScoreBalanceMinus(long userId, double score) {
		String sql = "UPDATE t_user_fund SET score_balance = score_balance - :score WHERE user_id = :userId AND score_balance >= :score";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("score", score);
		condition.put("userId", userId);
		
		return updateBySQL(sql, condition);
	}

	public int doAcctived(long userId) {
		String sql = "UPDATE t_user_fund SET is_actived = 1 WHERE user_id = :userId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);
		return updateBySQL(sql, condition);
	}
	
}
