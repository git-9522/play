package controllers.wechat.front;

import java.util.ArrayList;
import java.util.List;

import common.constants.ColumnKey;
import common.utils.Factory;
import common.utils.PageBean;
import controllers.wechat.WechatBaseController;
import models.common.entity.t_advertisement;
import models.common.entity.t_help_center;
import models.common.entity.t_information;
import models.common.entity.t_advertisement.Location;
import services.common.AdvertisementService;
import services.common.HelpCenterService;
import services.common.InformationService;

/**
 * 微信-发现控制器
 *
 * @description 
 *
 * @author yuechuanyang
 * @createDate 2017年11月2日
 */
public class DiscoverCtrl extends  WechatBaseController{
	
	protected static AdvertisementService advertisementService = Factory.getService(AdvertisementService.class);
	
	protected static InformationService informationService = Factory.getService(InformationService.class);

	/** 帮助中心 */
	protected static HelpCenterService helpCenterService = Factory.getService(HelpCenterService.class);
	

	/**
	 * 分页查询活动中心
	 *  
	 * @param isUse是否可用
	 * @param currPage 当前页 
	 * @param pageSize 每页条数
	 * @return
	 *
	 * @author yuechuanyang
	 * @createDate 2017年11月2日
	 */
	public static void goDiscoverPre(int currPage, int pageSize,String isUse){
		if (pageSize < 1) {
			pageSize = 6;
		}
		if (currPage < 1) {
			currPage = 1;
		}
		PageBean<t_advertisement> page = advertisementService.pageOfAdvertisementActiveForWechat(currPage, pageSize,
				"true".equals(isUse));
		render(page);
	}	
	
	/**
	 * 分页查询媒体报道
	 *  
	 * @param list 多个栏目key票拼接的list 
	 * @param currPage 当前页 
	 * @param pageSize 每页条数
	 * @return
	 *
	 * @author yuechuanyang
	 * @createDate 2017年11月2日
	 */
	public static void goInfoReportPre(int currPage,int pageSize){
		List<String> list = new ArrayList<String>();
		list.add(ColumnKey.INFO_REPORT);
		PageBean<t_information> page = informationService.pageOfInformationFront(list, currPage, pageSize, "");
		render(page);
	}
	
	
	/**
	 * 分页查询金融课堂
	 *  
	 * @param list 多个栏目key票拼接的list 
	 * @param currPage 当前页 
	 * @param pageSize 每页条数
	 * @return
	 *
	 * @author yuechuanyang
	 * @createDate 2017年11月2日
	 */
	public static void goFinancialClassroomPre(int currPage,int pageSize){
		List<String> list = new ArrayList<String>();
		list.add(ColumnKey.INFO_STORY);
		PageBean<t_information> page = informationService.pageOfInformationFront(list, currPage, pageSize, "");
		render(page);
	}
	
	
	
	/**
	 * 分页查询公告
	 *  
	 * @param list 多个栏目key票拼接的list 
	 * @param currPage 当前页 
	 * @param pageSize 每页条数
	 * @return
	 *
	 * @author yuechuanyang
	 * @createDate 2017年11月2日
	 */
	public static void getNoticePre(int currPage,int pageSize){
		List<String> list = new ArrayList<String>();
		list.add(ColumnKey.INFO_BULLETIN);
		PageBean<t_information> page = informationService.pageOfInformationFront(list, currPage, pageSize, "");
		render(page);
	}
	
	/**
	 * 查询帮助中心并分类
	 *  
	 * @return
	 *
	 * @author yuechuanyang
	 * @createDate 2017年11月3日
	 */
	public static void getAllHelpCenterPre(){
		List<t_help_center> list = helpCenterService.getAllHelpCenter();
		List<t_help_center> loginList = new ArrayList<t_help_center>();
		List<t_help_center> productList = new ArrayList<t_help_center>();
		List<t_help_center> rechargeList = new ArrayList<t_help_center>();
		List<t_help_center> otherList = new ArrayList<t_help_center>();	
		for(t_help_center thc : list){
			if(thc.getColumn().code == 1){
				loginList.add(thc);
			}else if(thc.getColumn().code == 2){
				productList.add(thc);
			}else if(thc.getColumn().code == 3){
				rechargeList.add(thc);
			}else{
				otherList.add(thc);
			}
		}
		render(loginList,productList,rechargeList,otherList);
	}
	
	/**
	 * 查询信息详情
	 *  
	 * @return
	 *
	 * @author yuechuanyang
	 * @createDate 2017年11月3日
	 */
	public static void getInfoDetailPre(long id){
		t_information tf = informationService.findInformationByIdFront(id);
		informationService.addInformationReadCount(id);
		render(tf);
	}
	
	
	
	/**
	 * 查询帮助中心详情
	 *  
	 * @return
	 *
	 * @author yuechuanyang
	 * @createDate 2017年11月3日
	 */
	public static void getHelpCenterInfoPre(long id){
		t_help_center thc = helpCenterService.getHelpCenterInfo(id);
		render(thc);
	}
	
	/**
	 * 进入运营数据页面
	 *  
	 * @return
	 *
	 * @author yuechuanyang
	 * @createDate 2017年11月3日
	 */
	public static void getOperationDataPre(){
		List<t_advertisement> adverList = 	advertisementService.queryAdvertisementFront(Location.WX_OPERATION_REPORT,6);
		render(adverList);
	}
}
