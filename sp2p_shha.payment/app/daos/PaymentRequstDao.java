package daos;

import java.util.HashMap;
import java.util.Map;

import models.entity.t_payment_request;
import daos.base.BaseDao;

public class PaymentRequstDao extends BaseDao<t_payment_request> {

	/**
	 * 请求处理成功，更新请求状态，并防止重复回调
	 *
	 * @param requestMark
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月8日
	 */
	public int updateReqStatusToSuccess(String requestMark) {
		String sql = "UPDATE t_payment_request SET status = :status WHERE mark = :requestMark AND status <> :status";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", t_payment_request.Status.SUCCESS.code);
		params.put("requestMark", requestMark);
		
		return updateBySQL(sql, params);
		
	}
	
	public int updateReqStatusToSuccessByOrdId(String ordId) {
		String sql = "UPDATE t_payment_request SET status = :status WHERE order_no = :ordId AND status <> :status";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", t_payment_request.Status.SUCCESS.code);
		params.put("ordId", ordId);
		
		return updateBySQL(sql, params);
		
	}
	
}