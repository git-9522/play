package services.common;

import common.utils.Factory;
import common.utils.ResultInfo;
import daos.common.CorpInfoDao;
import models.common.entity.t_corp_info;
import services.base.BaseService;

public class CorpInfoService extends BaseService<t_corp_info> {

	protected CorpInfoDao corpInfoDao = Factory.getDao(CorpInfoDao.class);

	protected CorpInfoService() {
		super.basedao = this.corpInfoDao;
	}

	public t_corp_info queryByUserId(long userId) {
		return corpInfoDao.findByColumn("user_id = ?", userId);
	}

	public ResultInfo saveOrUpdate(t_corp_info corpInfo) {
		ResultInfo result = new ResultInfo();
		t_corp_info cInfo = queryByUserId(corpInfo.user_id);
		if(cInfo != null) {
			cInfo.copyProperties(corpInfo);
		} else {
			cInfo = corpInfo;
		}
		if(corpInfoDao.save(cInfo)) {
			result.code = 1;
			result.msg = "保存企业信息成功";
		} else {
			result.code = -1;
			result.msg = "保存企业信息失败";
		}
		return result;
	}
	
}