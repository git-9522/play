package services.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import common.constants.Constants;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.ResultInfo;
import common.utils.StrUtil;
import daos.activity.AwardNumberRecordDao;
import models.activity.entity.t_award_number_record;
import models.common.entity.t_user_info;
import models.core.entity.t_invest_log;
import play.Logger;
import services.base.BaseService;
import services.common.UserInfoService;
/**
 *  投资送IPHONE 
 * @Title AwardNumberRecordService 
 * @Description 投资送IPHONE 活动业务处理
 * @author hjs_djk
 * @createDate 2017年10月23日 上午10:37:34
 */
public class AwardNumberRecordService extends BaseService<t_award_number_record> {
	/** 开始时间 **/
	public static final String start_time = new String("2017-11-01 00:00:00");
	/** 结束时间 **/
	public static final String end_time = new String("2017-11-30 23:59:59");
	/** 年化金额基数 **/
	public  double annul_money = 100.00;
	protected static AwardNumberRecordDao awardNumberRecordDao = Factory.getDao(AwardNumberRecordDao.class);
	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	public AwardNumberRecordService() {

		super.basedao = awardNumberRecordDao;

	}

	/**
	 * 添加投资IPHONE  抽奖号码
	 * 
	 * @param invest
	 * @return
	 */
	public ResultInfo add(t_invest_log invest) {
		Date startTime = DateUtil.strToDate(start_time, "yyyy-MM-dd HH:mm:ss");
		Date endTime = DateUtil.strToDate(end_time, "yyyy-MM-dd HH:mm:ss");
		Date currTime = new Date();
		ResultInfo result = new ResultInfo();
		result.code = 1;
		// 不在活动时间以内不执行以下方法
		if (currTime.getTime() < startTime.getTime() || currTime.getTime() > endTime.getTime()) {

			result.code = -10;
			result.msg = "不在活动时间内";
			return result;
		}

		t_user_info userInfo = userInfoService.findUserInfoByUserId(invest.user_id);
		if (userInfo == null) {

			result.code = -10;
			result.msg = "领号记录保存==》userinfo对象获取异常";

			return result;
		}
		try {

			// 获取累计年化金额
			double annualizedAmount = getAnnualMoney(start_time,
					DateUtil.dateToString(invest.time, "yyyy-MM-dd HH:mm:ss"), invest.user_id);
			Logger.info("定时任务添加奖号,投资记录ID=" + invest.id + " start_time=" + end_time + "  end_time="
					+ DateUtil.dateToString(invest.time, "yyyy-MM-dd HH:mm:ss") + "  年化金额=" + annualizedAmount);
			// 计算奖号个数总数(取整)
			int AwardNumberAmount = (int) (annualizedAmount / annul_money);

			// 获取用户已生成的中奖号数量
			int amount = getAwardNumberaAmount(invest.user_id);
			// 应生成中奖号数量
			int AwardNumber = AwardNumberAmount - (amount == -1 ? 0 : amount);
			if (AwardNumber <= 0) {
				result.code = -10;
				result.msg = "可生成抽奖号码次数不足";
				return result;
			}

			// 获取最大期数
			int PeriodMax = getPeriodsMax();
			PeriodMax = PeriodMax == -1 ? 1 : PeriodMax;
			// 最大奖号
			String numberMaxStr = getAwardNumberMax(PeriodMax);
			int numberMax = new Integer(numberMaxStr);
			// 奖号起始
			int currAwardNumber = numberMax + 1;
			if (9999 <= currAwardNumber + AwardNumber) {
				while (AwardNumber > 0) {
					String numberStr = cretenumStr(currAwardNumber);
					int count = (AwardNumber + currAwardNumber) > 9999 ? (10000 - currAwardNumber) : AwardNumber;
					t_award_number_record awardNumberRecord = new t_award_number_record();
					awardNumberRecord.user_id = invest.user_id;
					awardNumberRecord.user_name = userInfo.name;
					awardNumberRecord.amount = invest.amount;
					awardNumberRecord.min_award_number = numberStr;
					awardNumberRecord.max_award_number = cretenumStr(currAwardNumber + (count - 1));
					awardNumberRecord.periods = PeriodMax;
					awardNumberRecord.time = new Date();
					awardNumberRecord.count = count;
					awardNumberRecordDao.save(awardNumberRecord);
					AwardNumber -= count;
					PeriodMax++;
					currAwardNumber = 0;
				}

			} else {
				String numberStr = cretenumStr(currAwardNumber);
				t_award_number_record awardNumberRecord = new t_award_number_record();
				awardNumberRecord.user_id = invest.user_id;
				awardNumberRecord.user_name = userInfo.name;
				awardNumberRecord.amount = invest.amount;
				awardNumberRecord.min_award_number = numberStr;
				awardNumberRecord.max_award_number = cretenumStr(currAwardNumber + (AwardNumber - 1));
				awardNumberRecord.periods = PeriodMax;
				awardNumberRecord.count = AwardNumber;
				awardNumberRecord.time = new Date();
				awardNumberRecordDao.save(awardNumberRecord);
			}

		} catch (Exception e) {
			result.code = -10;
			result.msg = "领号记录保存失败";
		}

		result.msg = "领号记录保存成功";
		return result;
	}

	public static String cretenumStr(int num) {
		String numberStr = "000" + num;
		return numberStr.substring(numberStr.length() - 4);

	}

	public double getAnnualMoney(String startTime, String endTime, long userId) {
		return awardNumberRecordDao.getAnnualMoney(startTime, endTime, userId);
	}

	/**
	 * 获取最大奖号
	 * 
	 * @param period
	 *            摇奖期数
	 * 
	 */
	public String getAwardNumberMax(int period) {

		return awardNumberRecordDao.getAwardNumberMax(period);
	}

	/**
	 * 获取最大摇奖期数
	 * 
	 */
	public int getPeriodsMax() {

		return awardNumberRecordDao.getPeriodsMax();
	}

	/** 查询用户奖号总数 */
	public int getAwardNumberaAmount(long userId) {

		return awardNumberRecordDao.getAwardNumberaAmount(userId);
	}

	/**
	 * 获取领号记录列表
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getAwardNumberList() {
		List<Map<String, Object>> oldAwardNumberList = awardNumberRecordDao.getAwardNumberList();
		List<Map<String, Object>> awardNumberList = new ArrayList<Map<String, Object>>();
		for (Map m : oldAwardNumberList) {
			if (m == null) {
				continue;
			}
			String uname = (String) m.get("user_name");
			Date oldTime = (Date) m.get("time");
			String time = DateUtil.dateToString(oldTime, Constants.DATE_PLUGIN);
			if (uname == null) {
				continue;
			}
			m.put("user_name", StrUtil.asterisk(uname, 3, 2, 6));
			m.put("time", time);
			awardNumberList.add(m);
		}
		return awardNumberList;
	}
}
