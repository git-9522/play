package daos.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daos.base.BaseDao;
import models.common.entity.t_questionnaire_option;

public class QuestionnaireOptionDao extends BaseDao<t_questionnaire_option> {
	
	protected QuestionnaireOptionDao() {
		
	}

	public List<t_questionnaire_option> findOptionsByQuestionnaireDetailId(long id) {
		String sql = "SELECT * FROM t_questionnaire_option WHERE questionnaire_detail_id = :questionnaire_detail_id ORDER BY sort";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("questionnaire_detail_id", id);
		return this.findListBySQL(sql, condition);
	}

}