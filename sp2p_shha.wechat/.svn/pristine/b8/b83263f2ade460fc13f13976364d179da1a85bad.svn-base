package service.wechat;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import common.utils.Factory;
import common.utils.PageBean;
import dao.wechat.DebtTransferWechatDao;
import models.core.bean.DebtTransfer;
import models.core.bean.DebtTransferDetail;
import models.core.entity.t_debt_transfer;
import models.wechat.bean.UserDebtBasic;
import models.wechat.bean.UserDebtDetail;
import models.wechat.bean.WechatDebtTransfer;
import models.wechat.bean.WechatDebtTransferDetail;
import services.core.DebtService;

/**
 * 债权转让service-微信端
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年6月13日
 */
public class DebtWechatService extends DebtService {

	protected DebtTransferWechatDao debtTransferWechatDao;
	
	protected DebtWechatService() {
		debtTransferWechatDao = Factory.getDao(DebtTransferWechatDao.class);
	}

	/**
	 * 根据债权id查询债权的详细信息
	 *
	 * @param debtId 债权Id
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年6月13日
	 */
	public UserDebtDetail findUserDebtDetail(long debtId) {
		return debtTransferWechatDao.findUserDebtDetail(debtId);
	}
	
	/**
	 * 分页查询某个用户的债权
	 *
	 * @param currPage 当前第几页
	 * @param pageSize 每页显示的记录数
	 * @param userId 转让人id(和transactionUserId二选一)
	 * @param transactionUserId 受让人id(和userId二选一)
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年6月13日
	 */
	public PageBean<UserDebtBasic> pageOfUserDebt(int currPage,int pageSize,Long userId,Long transactionUserId ) {
		StringBuffer querySQL = new StringBuffer(" SELECT dt.id AS id, dt.time AS time, dt.invest_id AS invest_id, dt.user_id AS user_id,dt.transaction_user_id as transaction_user_id, dt.title AS title FROM t_debt_transfer dt  ");
		StringBuffer countSQL = new StringBuffer(" SELECT count(1) FROM t_debt_transfer dt ")	;
		
		Map<String, Object> condition = null;
		
		if(userId != null || transactionUserId != null){
			querySQL.append(" WHERE  ");
			countSQL.append(" WHERE ");
			
			condition = new HashMap<String, Object>();
			 
			if(userId != null){
				querySQL.append("  dt.user_id= :userId ");
				countSQL.append("  dt.user_id= :userId ");
				condition.put("userId", userId);
			}
			
			if(transactionUserId != null){
				querySQL.append("  dt.transaction_user_id= :transactionUserId ");
				countSQL.append("  dt.transaction_user_id= :transactionUserId ");
				condition.put("transactionUserId", transactionUserId);
			}
		}
		querySQL.append(" ORDER BY dt.id DESC ");
		
		return debtTransferDao.pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), UserDebtBasic.class, condition);

	}
	/**
	 * 分页查询处于竞拍和成功状态的债权
	 *
	 * @param currPage
	 * @param pageSize
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月28日
	 */
	public PageBean<WechatDebtTransfer> DebtTransfers(int currPage,int pageSize,int status){
		
		StringBuffer countSQL = new StringBuffer(" SELECT count(id) FROM t_debt_transfer dt WHERE dt.status in (:status) ");
		StringBuffer querySQL = new StringBuffer(" SELECT tb.apr AS apr ,tb.reward_rate AS reward_rate, dt.id AS id, dt.end_time as end_time, dt.time AS time, dt.invest_id AS invest_id, dt.user_id AS user_id, dt.title AS title, dt.debt_amount  debt_amount, dt.debt_principal AS debt_principal, dt.transfer_price AS transfer_price, dt.transfer_period AS period, dt.status AS status FROM t_debt_transfer dt ,t_invest ti ,t_bid tb WHERE dt.invest_id = ti.id AND ti.bid_id = tb.id AND dt.status in (:status) ORDER BY dt.status, dt.id DESC");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		List<Integer> statusList = new LinkedList<Integer>();
		statusList.add(t_debt_transfer.Status.AUCTING.code);
		statusList.add(t_debt_transfer.Status.SUCC.code);
		if(status == 99){
			condition.put("status", statusList);
		}else if (status == 1){
			condition.put("status", t_debt_transfer.Status.AUCTING.code);
		}else{
			condition.put("status", t_debt_transfer.Status.SUCC.code);
		}
		
		
		return debtTransferDao.pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), WechatDebtTransfer.class, condition);
	}
	/**
	 * 根据债权id查询债权的详细信息
	 *
	 * @param debtId 债权Id
	 * @return 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月25日
	 */
	public WechatDebtTransferDetail wechatFindDebtDetailById(long debtId){
		
		return debtTransferWechatDao.wechatFindDebtDetailById(debtId);
	}
}
