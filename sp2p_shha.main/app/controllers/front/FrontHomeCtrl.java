package controllers.front;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import models.common.entity.t_advertisement;
import models.common.entity.t_advertisement.Location;
import models.common.entity.t_consultant;
import models.common.entity.t_friend_ship_link;
import models.common.entity.t_help_center;
import models.common.entity.t_help_center.Column;
import models.common.entity.t_information;
import models.common.entity.t_partner;
import models.core.bean.DebtTransfer;
import models.core.bean.FrontBid;
import models.core.bean.FrontProduct;
import models.core.entity.t_bid;
import models.core.entity.t_product.ProductWebType;
import models.ext.experience.entity.t_experience_bid;
import payment.impl.PaymentProxy;
import play.Play;
import play.libs.Codec;
import play.templates.GroovyTemplate;
import play.templates.types.SafeHTMLFormatter;
import service.ext.experiencebid.ExperienceBidService;
import services.common.AdvertisementService;
import services.common.ConsultantService;
import services.common.FriendShipLinkService;
import services.common.HelpCenterService;
import services.common.InformationService;
import services.common.PartnerService;
import services.core.BidService;
import services.core.BillInvestService;
import services.core.BillService;
import services.core.DebtService;
import services.core.InvestService;
import services.core.ProductService;

import com.shove.Convert;
import com.sun.org.apache.bcel.internal.generic.NEW;

import common.constants.Constants;
import common.constants.ModuleConst;
import common.constants.SettingKey;
import common.enums.Client;
import common.enums.InformationMenu;
import common.enums.ServiceType;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.OrderNoUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import controllers.common.FrontBaseController;

/**
 * 前台-首页控制器
 *
 * @description
 *
 * @author huangyunsong
 * @createDate 2015年12月17日
 */

public class FrontHomeCtrl extends FrontBaseController {

	/** 注入资讯管理services */
	protected static InformationService informationService = Factory.getService(InformationService.class);

	/** 注入理财顾问services */
	protected static ConsultantService consultantService = Factory.getService(ConsultantService.class);

	/** 注入合作伙伴services */
	protected static PartnerService partnerService = Factory.getService(PartnerService.class);

	/** 广告图片 */
	protected static AdvertisementService advertisementService = Factory.getService(AdvertisementService.class);

	/** 散标投资 */
	protected static BidService bidService = Factory.getService(BidService.class);

	/** 借款账单services */
	protected static BillService billService = Factory.getService(BillService.class);

	protected static BillInvestService billInvestService = Factory.getService(BillInvestService.class);

	/** 帮助中心 */
	protected static HelpCenterService helpCenterService = Factory.getService(HelpCenterService.class);

	protected static DebtService debtService = Factory.getService(DebtService.class);

	protected static InvestService investService = Factory.getService(InvestService.class);

	protected static ProductService productService = Factory.getService(ProductService.class);
	/** 友情链接 */
	protected static FriendShipLinkService friendShipLinkService = Factory.getService(FriendShipLinkService.class);

	/**
	 * 前台-首页
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月18日
	 */
	public static void frontHomePre() {
		// 前台是否显示统计数据
		int is_statistics_show = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.IS_STATISTICS_SHOW),
				0);
		renderArgs.put("is_statistics_show", is_statistics_show);

		// 项目发布预告
		String project_releases_trailer = settingService.findSettingValueByKey(SettingKey.PROJECT_RELEASES_TRAILER);
		renderArgs.put("project_releases_trailer", project_releases_trailer);

		// 首页banner
		List<t_advertisement> banners = advertisementService.queryAdvertisementFront(Location.HOME_TURN_ADS, 10);
		renderArgs.put("banners", banners);

		List<t_advertisement> ads = advertisementService.queryAdvertisementFront(Location.HOME_AD, 2);
		renderArgs.put("ads", ads);

		// 散标投资
		/*
		 * PageBean<FrontBid> pageOfBis = bidService.pageOfBidInvest(1, 5); if
		 * (pageOfBis.page != null) { renderArgs.put("bids", pageOfBis.page); }
		 */

		// 新手投资
		PageBean<FrontBid> pageOfNewbieBis = bidService.pageOfNewbieBidInvest(1, 2);
		if (pageOfNewbieBis.page != null) {
			renderArgs.put("newbieBids", pageOfNewbieBis.page);
		}

		// 媒体报道
		List<t_information> inforeports = informationService.queryInformationFront(InformationMenu.INFO_REPORT, 5);
		renderArgs.put("inforeports", inforeports);

		// 统计资讯(官方公告，媒体报道，理财故事 )前5篇
		List<t_information> informations = informationService.queryInformationFront(InformationMenu.INFO_BULLETIN,
				Constants.INFORMATION_ADS_NUM);
		renderArgs.put("informations", informations);

		// 合作伙伴
		List<t_partner> partners = partnerService.queryPartnersFront(Constants.PARTNER_NUM);
		renderArgs.put("partners", partners);

		/* 验证码 */
		String randomId = Codec.UUID();
		renderArgs.put("randomId", randomId);

		// 周排行榜
		List<Map<String, Object>> weekInvest = investService.queryWeekInvestList(5);
		if (weekInvest != null && weekInvest.size() > 0) {
			renderArgs.put("weekInvest", weekInvest);
		}

		// 月排行榜
		List<Map<String, Object>> monthInvest = investService.queryMonthInvestList(5);
		if (monthInvest != null && monthInvest.size() > 0) {
			renderArgs.put("monthInvest", monthInvest);
		}

		// 总排行榜
		List<Map<String, Object>> totalInvest = investService.queryTotalInvestList(5);
		if (totalInvest != null && totalInvest.size() > 0) {
			renderArgs.put("totalInvest", totalInvest);
		}

		// List<FrontProduct> productList =
		// productService.queryProductIsUse(true);
		// renderArgs.put("productList", productList);

		// int frontBidsNum = bidService.findFrontBidsNum();
		// renderArgs.put("frontBidsNum", frontBidsNum);
		for (ProductWebType productwebtype : ProductWebType.values()) {
			PageBean<FrontBid> pageBean = bidService.pageOfBidWebInvest(1, 3, productwebtype.code, 0, 0, 0, 0, 0, 0);

			renderArgs.put("pageBean" + productwebtype.code, pageBean.page);
		}
		renderArgs.put("sysNowTime", new Date());

		render();
	}

	/**
	 * 首页-公司介绍
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月7日
	 */
	public static void aboutUsPre() {

		t_information profile = informationService.findLastProfile();

		if (profile == null) {

			error404();
		}

		render(profile);
	}

	/**
	 * 首页-平台优势
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月7日
	 */
	public static void safePre() {
		render();
	}

	/**
	 * 首页-加入我们
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月7日
	 */
	public static void joinusPre() {
		t_information joinus = informationService.findLastJoinus();

		if (joinus == null) {

			error404();
		}

		render(joinus);
	}
	/**
	 * 合作伙伴
	 *
	 * @author djk
	 * @createDate 2017年10月19日
	 */
	public static void showPartnersPre(int currPage, int pageSize) {
		if (pageSize < 1) {
			pageSize = 3;
		}
		if (currPage < 1) {
			currPage = 1;
		}
		PageBean<t_partner> page = partnerService.pageOfPartnerBack(currPage, pageSize);
		render(page);
	}

	/**
	 * 首页-联系我们
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月7日
	 */
	public static void contactusPre() {
		render();
	}

	/**
	 * 首页-新手好礼
	 *
	 *
	 * @author djk
	 * @createDate 2017年6月28日
	 */
	public static void activePre() {
		render();
	}

	/**
	 * 首页-邀请好友
	 *
	 *
	 * @author djk
	 * @createDate 2017年6月28日
	 */
	public static void invitePre() {
		render();
	}

	/**
	 * 平台活动页面
	 * 
	 * @param type
	 *            活动类型：1.38女王节、2.邀请好友、3.投资返现、4.新手专享
	 */
	public static void toActivityPre(int type) {
		render(type);
	}

	/**
	 * 平台活动中心
	 * 
	 * @param type
	 *            活动类型：1.38女王节、2.邀请好友、3.投资返现、4.新手专享
	 */
	public static void toActivityCenterPre() {
		render();
	}

	/**
	 * 首页-安全保障
	 * 
	 * 
	 */
	public static void toSafetyPre() {
		render();
	}

	/**
	 * 首页-APP下载
	 * 
	 * 
	 */
	public static void appDownloadPre() {
		render();
	}

	/**
	 * 首页-联系我们-百度地图
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月7日
	 */
	public static void baidumapPre() {
		render();
	}

	/**
	 * 首页-银行托管
	 *
	 *
	 * @author djk
	 * @createDate 2017年06月30日
	 */
	public static void bankDepositPre() {
		render();
	}

	/**
	 * 首页-帮助中心
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月7日
	 */
	public static void helpPre(int columnNo, String currPage) {
		int current = Convert.strToInt(currPage, -1);

		if (current < 0) {
			error404();
		}

		Column column = Column.getEnum(columnNo);

		if (column == null) {
			error404();
		}

		PageBean<t_help_center> page = helpCenterService.pageOfHelpCenterFront(current, 8, column);

		render(page, column);

	}

	/**
	 * 禁止收录
	 */
	public static void robots() {
		boolean is_robot = Convert.strToBoolean(Play.configuration.getProperty("is.robots"), true);
		String path = Play.configuration.getProperty("trust.funds.path") + "/robots.txt";
		InputStream is = null;
		try {
			is = new FileInputStream(new File(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if (!is_robot) {
			renderBinary(is);
		} else {
			renderText("百度收录已开启");
		}
	}

	/**
	 * 首页-散标投资
	 * 
	 * @param productId
	 */
	public static void showBidsPre(long productId) {
		PageBean<FrontBid> pageBean = bidService.pageOfBidInvest(1, 5, productId, 0, 0, 0);

		renderArgs.put("pageBean", pageBean);
		renderArgs.put("sysNowTime", new Date());

		render();
	}

	/**
	 * 微信授权回调
	 */
	public static void MP_verify_bC3f2JnFLGs4t6SW() {
		renderText("bC3f2JnFLGs4t6SW");
	}

	/**
	 * 运营报告 5月
	 */
	public static void operationReportPre() {
		render();
	}

	/**
	 * 运营报告 6月
	 */
	public static void operationReport6Pre() {
		render();
	}

	/**
	 * APP Banner图
	 */
	public static void activeDetailPre(int type) {
		render(type);
	}

	/**
	 * 友情链接
	 */
	public static void friendShipLinkPre() {
		List<t_friend_ship_link> friendShipLinks = friendShipLinkService.queryFriendShipLinkFront(0);
		render(friendShipLinks);
	}
	/**
	 *  金融资讯
	 */
	public static void financeInfoPre() {
		render();
	}

	public static void querySumInvest(String date) {
		double sum = investService.queryBidInvest(date);
		ResultInfo result = new ResultInfo();
		result.code = 1;
		result.msg = "";
		result.obj = sum;
		renderJSON(result);
	}
	

	public static void altMsgPre() {
		// 获取弹框
		List<t_advertisement> advertisements = advertisementService.queryAdvertisementFront(Location.PC_AlERT, 1);
		renderJSON(advertisements);
	}
	
	public static void altMsg() {
		altMsgPre();
	}

}
