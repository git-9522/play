package daos.common;

import java.util.HashMap;
import java.util.Map;

import daos.base.BaseDao;
import models.common.entity.t_moon_cake_lottery_record;

public class MoonCakeLotteryRecordDao extends BaseDao<t_moon_cake_lottery_record> {

	public int calulateLotterySizeWithPeriod(long userId, int period) {
		String sql = "SELECT COUNT(1) from t_moon_cake_lottery_record WHERE period = :period AND user_id = :userId";
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("period", period);
		args.put("userId", userId);
		Object object = this.findSingleBySQL(sql, args);
		if(object == null){
			return 0;
		}
		return new Integer(object.toString());
	}

	public boolean updateStatus(t_moon_cake_lottery_record record) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", true);
		map.put("delivery_time", record.delivery_time);
		map.put("id", record.id);
		int row =  updateBySQL("UPDATE t_moon_cake_lottery_record SET status = :status, delivery_time = :delivery_time WHERE id = :id", map);
		if(row == 0) {
			return true;
		}
		return false;
	}

}