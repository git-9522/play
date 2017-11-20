package common.enums;

import java.util.Arrays;
import java.util.List;

/**
 *  枚举： 资讯管理 左侧菜单
 *
 * @author liudong
 * @createDate 2016年1月5日
 */

public enum InformationMenu {

	/** 公司介绍  */
	HOME_PROFILE("home_profile","公司介绍"),
	
	/** 加入我们  */
	HOME_JOINUS("home_joinus","加入我们"),
	
	/** 官方公告  */
	INFO_BULLETIN("info_bulletin","官方公告"),
	
	/** 媒体报道 */
	INFO_REPORT("info_report","媒体报道"),
	
	/**金融课堂*/
/*	HOME_HELPCENTER_CLASS("home_helpcenter_class","金融课堂"),*/
	
	/** 金融课堂  */
	INFO_STORY("info_story","金融课堂"),
	
	/** 理财故事  */
/*	INFO_STORY("info_story","理财故事"),*/
	
	/** 平台注册协议  */
	PLATFORM_REGISTER("platform_register","平台注册协议"),
	
	/** 投资协议模板  */
	INVEST_AGREEMENT_TEMPLATE("investment_agreement_template","投资协议"),
	
	/** 借款协议 */
	LOAN_AGREEMENT("loan_agreement","借款协议");
	
	public String code;
	public String value;
	
	
	/** 资讯 （前台） 官方公告，理财故事，媒体报道  */
	public static final List<String> INFORMATION_FRONT = Arrays.asList( 
			INFO_BULLETIN.code,
			INFO_REPORT.code,
			INFO_STORY.code);
	
	
	private InformationMenu(String code, String value) {
		this.code = code;
		this.value = value;
	}

	public static InformationMenu getEnum(String code){
		InformationMenu[] menus = InformationMenu.values();
		for (InformationMenu menu : menus) {
			if (menu.code.equals(code)) {

				return menu;
			}
		}
		
		return null;
	}
	
}
