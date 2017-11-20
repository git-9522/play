package controllers.back.finance;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import models.common.bean.RechargeRecord;
import models.common.bean.RedPacketRecord;
import models.common.bean.WithdrawalRecord;
import models.core.entity.t_red_packet_user;
import com.shove.Convert;
import services.core.RedpacketUserService;
import common.constants.Constants;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.excel.ExcelUtils;
import common.utils.jsonAxml.JsonDateValueProcessor;
import common.utils.jsonAxml.JsonDoubleValueProcessor;
import controllers.common.BackBaseController;
import daos.core.RedpacketUserDao;

/**
 * @author panshaobin
 * 2016年10月18日
 */
public class RedPacketRecordCtrl extends BackBaseController {
	
	protected static RedpacketUserService redpacketUserService = Factory.getService(RedpacketUserService.class);
	
	//protected static RedpacketService redpacketService = Factory.getService(RedpacketService.class);
	
	/**
	 * 后台-财务-红包记录-红包记录列表
	 *
	 * @param currPage
	 * @param pageSize
	 * @param exports 1：导出  default:不导出
	 * @param name 会员昵称
	 *
	 * @author panshaobin
	 * @createDate 2016年10月18日
	 */
	public static void showRedPacketRecordPre(int currPage, int pageSize){
		/*int exports = Convert.strToInt(params.get("exports"), 0);
		String name = params.get("name");
		
		*//** 红包记录 *//*
		PageBean<RedPacketRecord> page = redpacketUserService.pageOfRedPacket(currPage, pageSize, exports, name);
		
		//导出
		if(exports == Constants.EXPORT){
			List<RedPacketRecord> list = page.page;
			 
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(Constants.DATE_TIME_FORMATE));
			jsonConfig.registerJsonValueProcessor(Double.class, new JsonDoubleValueProcessor(Constants.FINANCE_FORMATE_NORMAL));
			JSONArray arrList = JSONArray.fromObject(list, jsonConfig);
						
			File file = ExcelUtils.export("红包使用记录表",
			arrList,
			new String[] {
			"编号","红包类型", "会员", "红包金额", "时间", "状态"},
			new String[] {
			"id","red_packet_name" , "name", "amount", "time", "statusName"});
			   
			renderBinary(file, "红包使用记录表.xls");
		}
		render(page);*/
	}

}