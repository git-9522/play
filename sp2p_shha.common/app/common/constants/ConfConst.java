package common.constants;

import com.shove.Convert;

import play.Play;

/**
 * 本系统主配置文件配置参数对应的常量
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月7日
 */
public final class ConfConst {
	
	private ConfConst() {
	}
	
	/** 系统版本号 */
	public static final String SP2P_VERSION = Play.configuration.getProperty("sp2p.version");
	
	/* 托管配置 */
	/** 是否是本地测试环境 */
	public static final boolean IS_LOCALHOST = Convert.strToBoolean(Play.configuration.getProperty("is.localhost"), false);
	
	/** 是否开启资金托管模式 */
	public static final boolean IS_TRUST = Convert.strToBoolean(Play.configuration.getProperty("pay.is.trust"), false);
	
	/** 资金托管类型(汇付:HF) */
	public static final String CURRENT_TRUST_TYPE = Play.configuration.getProperty("pay.trust.type");
	
	
	
	/** 自定义项目路径配置 */
	public static final String HTTP_PATH = Play.configuration.getProperty("http.path");
	
	/** 项目访问根路径*/ 
	public static final String APPLICATION_BASEURL = Play.configuration.getProperty("application.baseUrl") + Play.configuration.getProperty("http.path") + "/";

	
	
	/** 系统加密串，每个项目请独立生成。必须16位字母或数字 */
	public static final String ENCRYPTION_KEY_MD5 = Play.configuration.getProperty("fixed.secret.md5");
	
	public static final String ENCRYPTION_KEY_DES = Play.configuration.getProperty("fixed.secret.des");
	
	/** APP连接PC的加密串 */
	public static final String ENCRYPTION_APP_KEY_MD5 = Play.configuration.getProperty("app.fixed.secret.md5");
	

	public static final String ENCRYPTION_APP_KEY_DES = Play.configuration.getProperty("app.fixed.secret.des");
	

	
	/** 初始信用积分 */
	public static final String CREDIT_SCORE = Play.configuration.getProperty("credit.score");
	
	/** 初始信用额度 */
	public static final String CREDIT_LINE = Play.configuration.getProperty("credit.line");
	
	/** 每个手机号码每天限发短信条数 */
	public static final int SEND_SMS_MAX_MOBILE = Convert.strToInt(Play.configuration.getProperty("send_sms_max_mobile"), 20);

	/** 每个IP地址每天限发短信条数 */
	public static final int SEND_SMS_MAX_IP = Convert.strToInt(Play.configuration.getProperty("send_sms_max_ip"), 1000);
	
	
	
	/** 系统定时器是否发送短信给用户 */
	public static final boolean IS_SMS_REALSEND = Convert.strToBoolean(Play.configuration.getProperty("is.sms_realsend"), true);

	/** 是否需要发送并校验短信验证码 */
	public static final boolean IS_CHECK_MSG_CODE = Convert.strToBoolean(Play.configuration.getProperty("is.check_msg_code"), true);

	/** 是否需要校验图形验证码 */
	public static final boolean IS_CHECK_PIC_CODE = Convert.strToBoolean(Play.configuration.getProperty("is.check_pic_code"), true);
	
	/** 系统定时任务是否执行 */
	public static final boolean IS_START_JOBS = Convert.strToBoolean(Play.configuration.getProperty("is.start_jobs"), true);
	
	/** 是否需要安全云盾登录 */
	public static final boolean IS_CHECK_UKEY = Convert.strToBoolean(Play.configuration.getProperty("is.check_ukey"), true);
	
	/** 科目库最多保存数量 */
	public static final int SUBJECT_LIBRARY_MAX_SIZE = Convert.strToInt(Play.configuration.getProperty("subject_library_max_size"), 100);

	
	
	/** 安卓下载地址  */
	public static final String ANDROID_DOWNLOAD_URL =  Play.configuration.getProperty("apk_path");
	
	/** IOS 下载地址  */
	public static final String IOS_DOWNLOAD_URL =  Play.configuration.getProperty("ios_path");
	
	
	/** 每次兑换上限金额 */
	public static final int MAX_CONVERSION = Convert.strToInt(Play.configuration.getProperty("max_conversion"), 200);

	/** 支付类型-汇付 */
	public static final String TRUST_TYPE_FY = "FY";
	
	/** 法大大虹金所客户id */
	public static final String FDD_CUSTOMER_ID = Play.configuration.getProperty("fdd_customer_id");
	
	/** 法大大AppID */
	public static final String FDD_APP_ID = Play.configuration.getProperty("fdd_app_id");
	
	/** 法大大版本号 */
	public static final String FDD_VERSION = Play.configuration.getProperty("fdd_version");
	
	/** 法大大秘钥 */
	public static final String FDD_SECRET = Play.configuration.getProperty("fdd_secret");
	
	/** 法大大服务器请求地址 */
	public static final String FDD_URL = Play.configuration.getProperty("fdd_url");
	
}
