package common.enums;

/**
 * 后台-推广-推广下拉菜单
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年2月16日
 */
public enum SpreadMenu {

	/** 1:红包 */
	REDPACKET(1, "红包", "/supervisor/spread/redpacket/showRedpackets.html"),
	
	/** 2.现金券 */
	CASH(2, "现金券", "/supervisor/spread/cash/batchSendCash.html"),
	
	/** 3.加息券 */
	RATE(3, "加息券", "/supervisor/spread/rate/batchSendRate.html"),
	
	/** 4:CPS推广 */
	CPS(4, "CPS推广", "/supervisor/spread/cpsSetting/toCpsSetting.html"),
	
	/** 5:财富圈 */
	WEALTHCIRCLE(5, "财富圈", "/supervisor/spread/wealthCircleSetting/toWealthCircleSetting.html"),
		
	/** 6:体验标 */
	EXPERIENCEBID(6, "体验标", "/supervisor/spread/experiencebid/showExperienceBid.html"),
	
	/** 7:红包领取活动 */
	ACTIVITY(7, "红包领取活动", "/supervisor/spread/activity/showActivity.html"),
	
	/** 8:投标抽奖 */
	LOTTERY(8, "投标大转盘", "/supervisor/spread/activity/showLotteryActivity.html"),
	
	/** 9:翻牌活动 */
	REVERSAL(9, "翻牌活动", "/supervisor/spread/activity/showReversalActivity.html");
	
	public int code;
	public String value;
	public String url;
	
	private SpreadMenu(int code, String value,String url) {
		this.code = code;
		this.value = value;
		this.url = url;
	}  
	
	public Integer getEnumValue() {
		
		return this.code;
	}

	public String getEnumName() {
		
		return this.value;
	}

	public String getUrl() {
		return url;
	}
	
}
