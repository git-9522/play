package models.common.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Transient;

import play.db.jpa.Model;

@Entity
public class t_questionnaire_detail extends Model {

	public long questionnaire_id;
	
	public String content;
	
	public String description;
	
	public int sort;
	
	public boolean is_multiple;
	
	public int size;
	
	public Date create_time;
	
	public Date modify_time;
	
	@Transient
	public List<t_questionnaire_option> options;
	
	@Transient
	public List<t_questionnaire_record> records;
	
}