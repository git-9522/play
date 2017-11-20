package controllers.common;

import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import common.annotation.SubmitCheck;
import common.annotation.SubmitOnly;
import play.Logger;
import play.cache.Cache;
import play.mvc.Before;
import play.mvc.Controller;

/**
 * 防止重复提交
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年3月7日
 */
public class SubmitRepeat extends Controller {

	@Before
	static void checkAccess() {
		
		SubmitOnly check = getActionAnnotation(SubmitOnly.class);
		SubmitCheck addCheck = getActionAnnotation(SubmitCheck.class);
		
		if(addCheck != null) {
			String uuid = UUID.randomUUID().toString();
			Cache.set(uuid, uuid, "30min");

			 Logger.info("重复提交生成的校验码:"+uuid);
			 flash.put("submitUuid",uuid);
		}
		
		if(check != null) {
			String uuid = params.get("uuidRepeat");
			Logger.info("重复提交校验:"+uuid);
			if(StringUtils.isBlank(uuid) || Cache.get(uuid) == null) {
				String url = request.headers.get("referer").value();
				flash.error("请勿重复提交");
				redirect(url);
			}
			
			Cache.delete(uuid);
	    }
	}
	
}
