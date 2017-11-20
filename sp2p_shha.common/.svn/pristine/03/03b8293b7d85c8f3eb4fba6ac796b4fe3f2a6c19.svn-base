package models.common.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Transient;

import play.db.jpa.Model;

@Entity
public class t_questionnaire extends Model {

	public Date create_time;
	
	public Date modify_time;
	
	public String name;
	
	public boolean is_used;
	
	public String description;
	
	@Transient
	public List<t_questionnaire_detail> details;
	
}