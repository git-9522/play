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
 * 用户理财基本信息实体
 *
 * @description 微信-账户中心-资产管理-我的理财
 *
 * @author DaiZhengmiao
 * @createDate 2016年6月12日
 */
@Entity
public class UserInvestBasic implements Serializable {

	/** 投资Id */
	@Id
	public long id;
	
	@Transient
	public String sign;
	
	public String getSign() {
		String signID = Security.addSign(id, Constants.INVEST_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
		
		return signID;
	}
	
	/**用户 id*/
	public long user_id;
	
	/** 标的Id */
	public long bid_id;
	
	/** 已结束账单 */
	public int alreadRepay;
	
	/** 全部账单 */
	public int totalPay;
	
	/** 标的编号:前缀（J）+id */
	@Transient
	public String bid_no;
	public String getBid_no(){
		return NoUtil.getBidNo(bid_id);
	}
	
	/** 标的标题 */
	public String title;
}
