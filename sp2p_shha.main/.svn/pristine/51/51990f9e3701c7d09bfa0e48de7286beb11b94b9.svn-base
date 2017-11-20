package plugin;

import play.PlayPlugin;
import play.mvc.Http;

public class JsonPlugin extends PlayPlugin {

	public boolean rawInvocation(Http.Request request, Http.Response response) throws Exception {
		
		// 获取请求头中的请求类型
		if (request.headers.containsKey("access-control-request-method")) {
			// 注入请求类型到requst的method中// 在跨域请求中play框架不会自转换请求类型所以需要手动注入
			request.method = request.headers.get("access-control-request-method").value();
		}

		Http.Response.current().setHeader("Access-Control-Allow-Origin", "*");

		if (request.headers.containsKey("access-control-request-headers")) {
			Http.Response.current().setHeader("Access-Control-Allow-Headers",
					request.headers.get("access-control-request-headers").value());
		}
		
		return super.rawInvocation(request, response);
	}

}