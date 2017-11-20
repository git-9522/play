package models.wechat.bean;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.NoUtil;
import common.utils.Security;

/**
 * 用户借款基本信息实体
 *
 * @description 微信-账户中心-资产管理-我的借款
 *
 * @author DaiZhengmiao
 * @createDate 2016年6月13日
 */
@Entity
public class UserLoanBasic implements Serializable {

	@Id
	public long id;
	
	/**用户 id*/
	public long user_id;
	
	@Transient
	public String sign;
	
	public String getSign() {
		String signID = Security.addSign(id, Constants.BID_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
		
		return signID;
	}
	
	/** 借款标题 */
	public String title;
	
	/** 标的已还账单数 */
	public int has_repayment_bill;
	
	/** 标的总的账单数 */
	public int total_repayment_bill;
	
	@Transient
	public String bid_no;
	
	public String getBid_no(){
		return NoUtil.getBidNo(id);
	}
}
