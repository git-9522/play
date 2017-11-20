package daos.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.utils.number.Arith;
import daos.base.BaseDao;
import models.common.entity.t_questionnaire_record;

public class QuestionnaireRecordDao extends BaseDao<t_questionnaire_record> {

	protected QuestionnaireRecordDao() {
		
	}

	/**
	 * 
	 * @param questionnaireId t_questionnaire.id
	 * @param questionnaireDetailId t_questionnaire_detail.id
	 * @param userId t_user.id
	 * @return
	 */
	public List<t_questionnaire_record> findRecordsByQuestionnaireDetailAndUser(long questionnaireId, long questionnaireDetailId, long userId) {
		String sql = "SELECT * FROM t_questionnaire_record WHERE user_id = :user_id AND questionnaire_id = :questionnaire_id AND questionnaire_detail_id = :questionnaire_detail_id";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("user_id", userId);
		condition.put("questionnaire_id", questionnaireId);
		condition.put("questionnaire_detail_id", questionnaireDetailId);
		return this.findListBySQL(sql, condition);
	}

	/**
	 * 
	 * @param questionnaireId
	 * @param questionnaireDetailId
	 * @param questionnaireOptionId
	 * @return
	 */
	public double queryScale(long questionnaireId, long questionnaireDetailId, long questionnaireOptionId) {
		int sum = this.querySumOption(questionnaireId, questionnaireDetailId);
		int count = this.queryOptionCount(questionnaireId, questionnaireDetailId, questionnaireOptionId);
		if(sum == 0) {
			return 0;
		} else {
			return Arith.div(100.0 * count, sum, 2);
		}
	}
	
	/**
	 * @param questionnaireId
	 * @param questionnaireDetailId
	 * @return
	 */
	public int querySumOption(long questionnaireId, long questionnaireDetailId) {
		String sql = "SELECT COUNT(1) FROM t_questionnaire_record WHERE questionnaire_id = :questionnaire_id AND questionnaire_detail_id = :questionnaire_detail_id";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("questionnaire_id", questionnaireId);
		condition.put("questionnaire_detail_id", questionnaireDetailId);
		return this.countBySQL(sql, condition);
	}
	
	/**
	 * @param questionnaireId
	 * @param questionnaireDetailId
	 * @return
	 */
	public int queryOptionCount(long questionnaireId, long questionnaireDetailId, long questionnaireOptionId) {
		String sql = "SELECT COUNT(1) FROM t_questionnaire_record WHERE questionnaire_id = :questionnaire_id AND questionnaire_detail_id = :questionnaire_detail_id AND questionnaire_option_id = :questionnaire_option_id";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("questionnaire_id", questionnaireId);
		condition.put("questionnaire_detail_id", questionnaireDetailId);
		condition.put("questionnaire_option_id", questionnaireOptionId);
		return this.countBySQL(sql, condition);
	}

	public int getCount(long questionnaire_id, long user_id) {
		String sql = "SELECT COUNT(1) FROM t_questionnaire_record WHERE questionnaire_id = :questionnaire_id AND user_id = :user_id";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("questionnaire_id", questionnaire_id);
		condition.put("user_id", user_id);
		return this.countBySQL(sql, condition);
	}
	
}