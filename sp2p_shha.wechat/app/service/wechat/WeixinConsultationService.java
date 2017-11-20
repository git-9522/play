package service.wechat;

import common.utils.Factory;
import common.utils.ResultInfo;
import models.common.entity.t_setting_platform;
import services.base.BaseService;
import services.common.SettingService;

/**
 * 查询微信咨询语
 * 
 * @author liudong
 * @createDate 2016年5月5日
 */
public class WeixinConsultationService extends BaseService<t_setting_platform> {

	protected static SettingService settingService = Factory.getService(SettingService.class);
	
	/**
	 * 编辑微信欢迎语或咨询语
	 * 
	 * @param
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年5月5日
	 */
	public ResultInfo updateWeixinConsultation(String _key, String _value){
		ResultInfo result = new ResultInfo();
		boolean flag = settingService.updateOrAddSetting(_key, _value);
		if(!flag){
			result.code=-1;
			result.msg="编辑失败";
			
			return result;
		}
		
		result.code=1;
		result.msg="编辑成功";
		
		return result;
	}
	
	/**
	 * 查询微信欢迎语或咨询语
	 * 
	 * @param
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年5月5日
	 */
	public String findWeixinConsultation(String _key){
		t_setting_platform options = settingService.findSettingByKey(_key);
		if(options == null){
			return null;
		}
		
		return options._value;
	}
	
	
}
