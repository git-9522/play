package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class t_questionnaire_record extends Model {

	public long user_id;
	
	public long questionnaire_id;
	
	public long questionnaire_detail_id;
	
	public long questionnaire_option_id;
	
	public Date create_date;
	
	public t_questionnaire_record() {
		this.create_date = new Date();
	}

	public t_questionnaire_record(long user_id, long questionnaire_id, long questionnaire_detail_id,
			long questionnaire_option_id) {
		this.user_id = user_id;
		this.questionnaire_id = questionnaire_id;
		this.questionnaire_detail_id = questionnaire_detail_id;
		this.questionnaire_option_id = questionnaire_option_id;
		this.create_date = new Date();
	}

}