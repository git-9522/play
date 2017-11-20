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
 * 用户的债权基本信息
 *
 * @description 微信-账户中心-资产管理-我的受让/我的转让
 *
 * @author DaiZhengmiao
 * @createDate 2016年3月30日
 */
@Entity
public class UserDebtBasic implements Serializable {

	@Id
	public Long id;
	
	/** 债权转让项目的编号:前缀（Q）+id */
	@Transient
	public String debt_transfer_no;
	public String getDebt_transfer_no() {
		return NoUtil.getDebtTransferNo(this.id);
	}

	/** 对应的投资id */
	public Long invest_id;
	
	/** 转让人 user_id */
	public long user_id;
	
	/** 受让人 user_id */
	public Long transaction_user_id;
	
	/** 转让标题 */
	public String title;
	
	@Transient
	public String sign;
	public String getSign () {
		
		return Security.addSign(this.id, Constants.DEBT_TRANSFER_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
	}
	
}
