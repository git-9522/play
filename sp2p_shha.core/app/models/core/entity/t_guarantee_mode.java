package models.core.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;
@Entity
public class t_guarantee_mode extends Model{
	
	public String name;
	
	public Date time;
}
