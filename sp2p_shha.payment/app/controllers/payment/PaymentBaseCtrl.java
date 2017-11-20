package controllers.payment;

import common.utils.ResultInfo;
import play.mvc.Controller;

/**
 * 托管控制器-基类
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2016年1月7日
 */
public class PaymentBaseCtrl extends Controller {
	
	/**
	 * 表单自动提交，请求第三方
	 *
	 * @param html
	 * @param client
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月7日
	 */
	public void submitForm(String html, int client){
		renderHtml(html);
	}
    
	/****
	 * 返回App处理
	 * 如果有需要可渲染成html
	 * @param result 结果集关键字为result，给APP解析使用的
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-6
	 */
	public static void redirectApp(ResultInfo result){
		String html = "<title> 虹金所 </title> <meta name='keywords' content='虹金所' /> <meta name='description' content='虹金所' /> <meta content='text/html; charset=utf-8' http-equiv='Content-Type' /> <style type='text/css'> .emil-box{ width: 100%; font-family: 'Microsoft YaHei'; font-size: 4em; color: #333; background: #E7E8EB; word-break: break-all; } .emil-main{ background: #fff; height:25%; width:100%; text-align:center; }  .emil-notice{  height:100%; }  </style> <div class='emil-box'> <img src='http://www.niumail.com.cn/data/attachments/bd420f40-ab9e-4ea3-a611-6710a961a0ac' /> <div class='emil-main'> <p> <span style='font-size:1.2em;'>"+result.msg+"</span> </p> </div> <div class='emil-notice'>  </div> </div>";
		
		renderHtml(html);
	}
	
	/**
	 * 跳转到公共的500页面
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月14日
	 */
	protected static void error500(){

		renderTemplate("common/errors/http-500.html");	
	}
}
