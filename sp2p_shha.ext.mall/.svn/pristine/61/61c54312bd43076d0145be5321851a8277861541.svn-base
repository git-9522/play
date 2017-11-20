package controllers.back.mall;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;
import controllers.common.BackBaseController;
import models.ext.mall.bean.BackExchanges;
import models.ext.mall.entiey.t_mall_exchange;
import service.ext.mall.ExchangeService;
/**
 * 积分商城-实物商品兑换发货
 *
 * @description 
 *
 * @author jiayijian
 * @createDate 2017年3月16日
 */
public class ExchangeCtrl extends BackBaseController{
	
	protected static ExchangeService exchangeService = Factory.getService(ExchangeService.class);
	
	/**
	 * 积分商城-实物兑换记录
	 * 
	 * @rightID 801001
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public static void showExchangesPre(int currPage, int pageSize) {
		
		int showType = 0;
		
		if (showType < 0 || showType > 2) {
			showType = 0;
		}
		String numNo = params.get("numNo");
		String goodsName = params.get("goodsName");
	
		PageBean<BackExchanges> pageBean = exchangeService.pageOfBackExchanges(showType,currPage, pageSize,numNo,goodsName);
		
		render(pageBean,numNo,goodsName);
	}
	
	/**
	 * 积分商城-派送页面
	 * 
	 * @rightID 801001
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public static void toDeliveryPre(String sign){
		
		ResultInfo result = Security.decodeSign(sign, Constants.MALL_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			flash.error(result.msg);
			showExchangesPre(1,10);
		}
		
		long goodsId = Long.parseLong((String) result.obj);
		
		t_mall_exchange exhange = exchangeService.findByID(goodsId);
		
		if(exhange == null){
			
			flash.error("该记录不存在");
			showExchangesPre(1,10);
		}
		
		render(exhange);
	}
	
	/**
	 * 积分商城-执行派送
	 * 
	 * @rightID 801001
	 *
	 * @param sign id 
	 * @param express_company 物流公司 
	 * @param tracking_number 快递单号 
	 * 
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public static void doDelivery(String sign,String express_company,String tracking_number){
		
		checkAuthenticity();
		
		flash.put("express_company", express_company);
		flash.put("tracking_number", tracking_number);
		
		ResultInfo result = Security.decodeSign(sign, Constants.MALL_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			flash.error("该记录不存在");
			toDeliveryPre(sign);
		}
		
		long goodsId = Long.parseLong((String) result.obj);
		
		result = exchangeService.doDelivery(goodsId,express_company,tracking_number);
		
		if (result.code < 1) {
			LoggerUtil.info(true,"执行派送商品时：%s", result.msg);
			flash.error(result.msg);
			toDeliveryPre(sign);
		}
		
		flash.success(result.msg);
		showExchangesPre(1,10);
	}
	
	/**
	 * 积分商城-派送详情
	 * 
	 * @rightID 801001
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public static void toDeliveryDetailPre(String sign){
		
		ResultInfo result = Security.decodeSign(sign, Constants.MALL_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			flash.error(result.msg);
			showExchangesPre(1,10);
		}
		
		long goodsId = Long.parseLong((String) result.obj);
		
		t_mall_exchange exhange = exchangeService.findByID(goodsId);
		
		if(exhange == null){
			
			flash.error("该记录不存在");
			showExchangesPre(1,10);
		}
		
		render(exhange);
	}
	
}
