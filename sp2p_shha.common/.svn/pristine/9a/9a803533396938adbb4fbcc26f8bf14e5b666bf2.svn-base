package services.common;

import java.util.List;

import common.utils.Factory;
import daos.common.QuestionnaireDao;
import daos.common.QuestionnaireDetailDao;
import daos.common.QuestionnaireOptionDao;
import daos.common.QuestionnaireRecordDao;
import models.common.entity.t_questionnaire;
import models.common.entity.t_questionnaire_detail;
import models.common.entity.t_questionnaire_option;
import models.common.entity.t_questionnaire_record;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import play.Logger;
import services.base.BaseService;

public class QuestionnaireService extends BaseService<t_questionnaire> {
	
	protected QuestionnaireDao questionnaireDao = Factory.getDao(QuestionnaireDao.class);
	
	protected QuestionnaireDetailDao questionnaireDetailDao = Factory.getDao(QuestionnaireDetailDao.class);
	
	protected QuestionnaireOptionDao questionnaireOptionDao = Factory.getDao(QuestionnaireOptionDao.class);
	
	protected QuestionnaireRecordDao questionnaireRecordDao = Factory.getDao(QuestionnaireRecordDao.class);
	
	// 获取问卷调查
	/**
	 * 
	 * @param id 问卷调查id
	 * @return
	 */
	public t_questionnaire queryById(long questionnaireId, long user_id) {
		t_questionnaire  questionnaire = this.questionnaireDao.findByID(questionnaireId);
		if(!questionnaire.is_used) {
			// 未使用直接return null
			return null;
		} else {
			// 获取问卷题目
			List<t_questionnaire_detail> details = this.questionnaireDetailDao.findDetailsByQuestionnaireId(questionnaireId);
			if(details == null || details.isEmpty()) {
				return questionnaire;
			} else {
				questionnaire.details = details;
				for(t_questionnaire_detail detail : details) {
					// 组装选项
					List<t_questionnaire_option> options = this.questionnaireOptionDao.findOptionsByQuestionnaireDetailId(detail.id);
					if(options != null && !options.isEmpty()) {
						detail.options = options;
						for(t_questionnaire_option option : options) {
							double scale = this.questionnaireRecordDao.queryScale(questionnaireId, detail.id, option.id);
							option.scale = scale;
						}
					}
					// 组装用户记录
					List<t_questionnaire_record> records = this.questionnaireRecordDao.findRecordsByQuestionnaireDetailAndUser(questionnaireId, detail.id, user_id);
					if(records != null && !records.isEmpty()) {
						detail.records = records;
					}
				}
			}
		}
		return questionnaire;
	}

	public void commitQuestionnaire(String params, long user_id) {
		JSONArray array = JSONArray.fromObject(params);
		for(int i = 0; i < array.size(); i++) {
			JSONObject object = array.getJSONObject(i);
			Logger.debug(object.toString());
			t_questionnaire_record record = (t_questionnaire_record) JSONObject.toBean(object, t_questionnaire_record.class);
			Logger.debug("detail_id : %s, option_id : %s, time : %s", record.questionnaire_detail_id, record.questionnaire_option_id, record.create_date);
			record.user_id = user_id;
			this.insertQuestionnaireRecord(record);
		}
	}
	
	public void insertQuestionnaireRecord(t_questionnaire_record record) {
		this.questionnaireRecordDao.save(record);
	}
	
	public boolean hasCommit(long questionnaire_id, long user_id) {
		int count = this.questionnaireRecordDao.getCount(questionnaire_id, user_id);
		if(count != 0) {
			return false;
		}
		return true;
	}
}