package daos.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daos.base.BaseDao;
import models.common.entity.t_questionnaire_detail;

public class QuestionnaireDetailDao extends BaseDao<t_questionnaire_detail> {

	protected QuestionnaireDetailDao() {
		
	}

	public List<t_questionnaire_detail> findDetailsByQuestionnaireId(long id) {
		String sql = "SELECT * FROM t_questionnaire_detail WHERE questionnaire_id = :questionnaire_id ORDER BY sort";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("questionnaire_id", id);
		return this.findListBySQL(sql, condition);
	}
	
}