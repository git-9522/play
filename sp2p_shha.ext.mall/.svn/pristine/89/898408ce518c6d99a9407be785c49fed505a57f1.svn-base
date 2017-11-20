package controllers.back.mall;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.shove.Convert;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.excel.ExcelUtils;
import common.utils.file.FileUtil;
import common.utils.jsonAxml.JsonDateValueProcessor;
import controllers.common.BackBaseController;
import models.common.entity.t_event_supervisor;
import models.common.entity.t_event_supervisor.Event;
import models.ext.mall.entiey.t_mall_goods;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import service.ext.mall.GoodsService;
/**
 * 积分商城-积分商品
 *
 * @description 
 *
 * @author jiayijian
 * @createDate 2017年3月16日
 */
public class GoodsCtrl extends BackBaseController{
	
	protected static GoodsService goodsService = Factory.getService(GoodsService.class);
	
	
	/**
	 * 积分商城-积分商品
	 * 
	 * @rightID 1103001
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	@SuppressWarnings("unchecked")
	public static void showGoodsPre(int showType,int currPage, int pageSize) {
		
		int exports = Convert.strToInt(params.get("exports"), 0);
		
		String numNo = params.get("numNo");
		String goodsName = params.get("goodsName");
		
		if (showType < 0 || showType > 2) {
			showType = 0;
		}
		
		PageBean<t_mall_goods> pageBean = goodsService.pageOfBackgoods(showType,currPage, pageSize,numNo,goodsName,exports);
		
		//导出
		if(exports == Constants.EXPORT){
			List<t_mall_goods> list = pageBean.page;
			 
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(Constants.DATE_TIME_FORMATE));
			JSONArray arrList = JSONArray.fromObject(list, jsonConfig);
			
			for (Object obj : arrList) {
				JSONObject goods = (JSONObject)obj;	
				
				if(StringUtils.isNotBlank(goods.getString("type"))){
					goods.put("type", t_mall_goods.GoodsType.getEnum(Integer.valueOf(goods.getString("type"))).value);
				}
				
				if(StringUtils.isNotBlank(goods.getString("is_use"))){
					goods.put("is_use", Convert.strToBoolean(goods.getString("is_use"), false)? "上架":"下架");
				}
				
				if(StringUtils.isNotBlank(goods.getString("inventory")) && StringUtils.isNotBlank(goods.getString("is_unlimited_inven"))){
					goods.put("inventory", Convert.strToBoolean(goods.getString("is_unlimited_inven"), false)? "无限":Convert.strToInt(goods.getString("inventory"), 0));
				}
				
				if(StringUtils.isNotBlank(goods.getString("exchange_maximum")) && StringUtils.isNotBlank(goods.getString("is_unlimited_exc_max"))){
					goods.put("exchange_maximum", Convert.strToBoolean(goods.getString("is_unlimited_exc_max"), false)? "无限":Convert.strToInt(goods.getString("exchange_maximum"), 0));
				}
			}
			
			String fileName="理财项目列表";
			
			File file = ExcelUtils.export(fileName,
			arrList,
			new String[] {
			"编号", "实物名称", "类型", "实物总数", "兑换上限", "已兑换数量", "虹银价格", "修改时间", "状态"},
			new String[] {
			"id","name", "type", "inventory", "exchange_maximum", "exchanged_num", "spend_scroe", "last_edit_time","is_use"});
			   
			renderBinary(file, fileName + ".xls");
		}
		
		
		render(pageBean,showType,numNo,goodsName);
	}
	
	/**
	 * 添加积分商品页面
	 * 
	 * @rightID 1103002
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public static void toAddGoodsPre(){
		
		render();
	}
	
	/**
	 * 添加积分商品
	 * 
	 * @rightID 1103002
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public static void addGoods(t_mall_goods goods){
		checkAuthenticity();
		flash.put("type", goods.type);
		flash.put("name", goods.name);
		flash.put("image_url", goods.image_url);
		flash.put("is_unlimited_inven", goods.is_unlimited_inven);
		flash.put("inventory", goods.inventory);
		
		flash.put("is_unlimited_exc_max", goods.is_unlimited_exc_max);
		flash.put("exchange_maximum", goods.exchange_maximum);
		flash.put("attribute", goods.attribute);
		flash.put("attribute_value", goods.attribute_value);
		flash.put("min_invest_amount", goods.min_invest_amount);
		
		flash.put("limit_day", goods.limit_day);
		flash.put("spend_scroe", goods.spend_scroe);
		flash.put("description", goods.description);
	
		ResultInfo result = new ResultInfo();
		
		result = goodsService.checkGoodsValue(goods);
		
		if(result.code < 1){
			
			LoggerUtil.info(false,"校验积分商品参数时：%s", result.msg);
			flash.error(result.msg);
			toAddGoodsPre();
		}
		
		result = goodsService.addGoods(goods);
		
		if(result.code < 1){
			
			LoggerUtil.info(true,"保存积分商品参数时：%s", result.msg);
			flash.error(result.msg);
			toAddGoodsPre();
		}
		flash.success(result.msg);
		showGoodsPre(goods.type,1,10);
	}
	
	/**
	 * 编辑积分商品页面
	 * 
	 * @rightID 1103003
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public static void toEditGoodsPre(long goodsId){
		
		if(goodsId < 0){
			flash.error("商品ID参数错误");
			
			showGoodsPre(0,1,10);
		}
		
		t_mall_goods goods = goodsService.findByID(goodsId);
		
		if(goods == null){
			flash.error("没有找到该商品");
			
			showGoodsPre(0, 1, 10);
		}
		
		render(goods);
	}
	
	
	/**
	 * 编辑积分商品
	 * 
	 * @rightID 1103003
	 *
	 * @author jiayijian
	 * @createDate 2017年3月18日
	 */
	public static void editGoods(t_mall_goods goods,long goodsId){
		checkAuthenticity();
		
		ResultInfo result = new ResultInfo();
		if(goodsId < 0){
			flash.error("商品ID参数错误");
			
			showGoodsPre(0,1,10);
		}
		
		t_mall_goods gd = goodsService.findByID(goodsId);
		
		if(gd == null){
			flash.error("没有找到该商品");
			
			toEditGoodsPre(goodsId);
		}
		
		goods.type = gd.type;
		
		result = goodsService.checkGoodsValue(goods);
		
		if(result.code < 1){
			
			LoggerUtil.info(false,"校验积分商品参数时：%s", result.msg);
			flash.error(result.msg);
			toEditGoodsPre(goodsId);
		}

		result = goodsService.editGoods(goods,gd);
		
		if(result.code < 1){
			
			LoggerUtil.info(true,"保存积分商品参数时：%s", result.msg);
			flash.error(result.msg);
			toEditGoodsPre(goodsId);
		}
		
		/** 添加管理员事件 */
		Map<String, String>param = new HashMap<String, String>();
		param.put("supervisor", ""+getCurrentSupervisorId());
		param.put("goodsId", ""+goodsId);
		param.put("goodsName", gd.name);
		boolean flag = supervisorService.addSupervisorEvent(getCurrentSupervisorId(), t_event_supervisor.Event.GOODS_EDIT, param);
		
		if(!flag){
			
			flash.error("保存管理员事件失败");
			LoggerUtil.error(true, "保存管理员事件失败");
			toEditGoodsPre(goodsId);
		}
		
		flash.success(result.msg);
		showGoodsPre(gd.type,1,10);
	}
	
	/**
	 * 积分商品 删除
	 * @rightID 1103005
	 *
	 * @param sign 资讯加密id
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月29日
	 */
	public static void delGoods(String sign){
		ResultInfo result = Security.decodeSign(sign, Constants.MALL_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			renderJSON(result);
		}
		long goodsId = Long.parseLong((String) result.obj);
		t_mall_goods gd = goodsService.findByID(goodsId);
		
		if(gd == null){
			
			result.code=-1;
			result.msg="该商品不存在";
			
			renderJSON(result);
		}
		
		boolean delFlag = goodsService.delGoods(goodsId);
		if(!delFlag){
			result.code=-1;
			result.msg="删除失败";
			
			renderJSON(result);
		}else{
			//管理员事件
			long supervisorId = getCurrentSupervisorId();
			Map<String, String> supervisorEventParam = new HashMap<String, String>(); 
			supervisorEventParam.put("goodsId", gd.id+"");  
			supervisorEventParam.put("goodsName", gd.name);
			supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.GOODS_DELETE, supervisorEventParam);
		}
		
		result.code=1;
		result.msg="删除成功";
		
		renderJSON(result);
	}
	
	/**
	 * 上传商品图片
	 * 
	 * @param imgFile
	 * @param fileName
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年4月5日
	 */
	@SuppressWarnings("unchecked")
	public static void uploadGoodsImage(File imgFile, String fileName){
		response.contentType="text/html";
		ResultInfo result = new ResultInfo();
		if (imgFile == null) {
			result.code = -1;
			result.msg = "请选择要上传的图片";
			
			renderJSON(result);
		}
		if(StringUtils.isBlank(fileName) || fileName.length() > 32){
			result.code = -1;
			result.msg = "图片名称长度应该位于1~32位之间";
			
			renderJSON(result);
		}
		
		result = FileUtil.uploadImgags(imgFile);
		if (result.code < 0) {

			renderJSON(result);
		}
		
		Map<String, Object> fileInfo = (Map<String, Object>) result.obj;
		fileInfo.put("imgName", fileName);
		
		renderJSON(result);
	}
	
	/**
	 * 更新积分商品状态
	 *
	 * @param goodsId
	 * @param goodsName 商品名称，用于添加管理员事件时使用
	 * @param isUse true-当前状态为上架；false-当前状态为下架
	 *
	 * @author yaoyi
	 * @createDate 2015年12月16日
	 */
	public static void updateGoodsStatus(long goodsId, String goodsName, boolean isUse){
		ResultInfo res = new ResultInfo();
		
		if(goodsId < 1){
			res.code = -1;
			res.msg = "参数错误";
			
			renderJSON(res);
		}
		
		Event ev = isUse ? Event.GOODS_DISABLED : Event.GOODS_ENABLE;
		
		boolean upd= goodsService.updateGoodsStatus(goodsId, !isUse);
		if(!upd){
			LoggerUtil.error(true, "更新积分商品上下架状态失败!");
			
			res.code = -1;
			res.msg = "更新积分商品上下架状态失败!";
			renderJSON(res);
		}
		
		/** 添加管理员事件 */
		Map<String, String>param = new HashMap<String, String>();
		param.put("supervisor", ""+getCurrentSupervisorId());
		param.put("goodsId", ""+goodsId);
		param.put("goodsName", goodsName);
		boolean flag = supervisorService.addSupervisorEvent(getCurrentSupervisorId(), ev, param);
		
		if(!flag){
			LoggerUtil.error(true, "保存管理员事件失败!");
			res.code = -1;
			res.msg = "保存管理员事件失败";
			
			renderJSON(res);
		}
		
		res.code = 1;
		res.msg = "";
		res.obj = !isUse;
		
		renderJSON(res);
	}

	public static void getAllDrawGoods(int type, boolean isUsed) {
		List<t_mall_goods> goods = goodsService.findListByType(type, isUsed);
		ResultInfo result = new ResultInfo();
		result.code = 1;
		result.msg = "success";
		result.obj = goods;
		renderJSON(result);
	}
	
}
