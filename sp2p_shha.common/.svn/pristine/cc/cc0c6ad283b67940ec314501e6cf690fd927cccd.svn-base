package models.common.entity;


import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import common.constants.ConfConst;
import common.constants.Constants;
import common.enums.AnnualIncome;
import common.enums.Car;
import common.enums.Client;
import common.enums.Education;
import common.enums.Gender;
import common.enums.House;
import common.enums.Marital;
import common.enums.NetAssets;
import common.enums.Relationship;
import common.enums.WorkExperience;
import common.utils.DateUtil;
import common.utils.Security;
import play.db.jpa.Model;

/**
 * 用户表(基本信息)
 *  
 * @description 
 *
 * @author ChenZhipeng
 * @createDate 2015年12月15日
 */

@Entity
public class t_user_info extends Model {
	
	/** 省份代号 */
	public String prov_id = "";
	/** 城市代号 */
	public String area_id = "";
	/**工作单位*/
	public String work_unit;
	/**注册资金*/
	public double registered_fund;
	/**成立/入职时间*/
	public Date start_time;

	/** 用户ID(取t_users表的ID) */
	public long user_id;
	
	/** 用户昵称(冗余自t_users表) */
	public String name;
	
	/** 注册入口：1 pc 2 app 3 wechat */
	private int client;
	public Client getClient() {
		Client client = Client.getEnum(this.client);
		return client;
	}

	public void setClient(Client client) {
		this.client = client.code;
	}
	
	/** 手机号码 */
	public String mobile;
	
	/** 用户头像 */
	public String photo;
	
	/** 真实姓名 */
	public String reality_name;

	/** 身份证号码 */
	public String id_number;
	
	/** 邮箱 */
	public String email;

	/** 箱邮是否已经验证通过 */
	public boolean is_email_verified;
	
	/** 信用积分 */
	public int credit_score;

	/** 信用等级 */
	public long credit_level_id;
	@Transient
	public t_credit_level creditLevel;
	public t_credit_level getCreditLevel () {
		if (this.creditLevel == null) {
			this.creditLevel = t_credit_level.findById(credit_level_id);
		}
		return this.creditLevel;
	}

	/** 信用额度 */
	public double credit_line;
	
	/** 抽奖时间标记  */
	public Date lottery_day;
	
	/** 每天(抽奖时间)内的抽奖次数  */
	public int lottery_times;
	
	private int sex;
	/** 性别(枚举)： 1 男 2 女 3 保密 */
	public Gender getSex() {
		Gender gender = Gender.getEnum(sex);
		return gender;
	}

	public void setSex(Gender gender) {
		this.sex = gender.code;
	}

	/** 生日 */
	public Date birthday;
	
	private int education;
	/** 教育情况 (枚举)*/
	public Education getEducation() {
		Education education = Education.getEnum(this.education);
		return education;
	}

	public void setEducation(Education education) {
		this.education = education.code;
	}

	private int marital;
	/** 婚姻状况  (枚举)*/
	public Marital getMarital() {
		Marital marital = Marital.getEnum(this.marital);
		return marital;
	}

	public void setMarital(Marital marital) {
		this.marital = marital.code;
	}
	
	private int annual_income;
	/** 年收入 (枚举) */
	public AnnualIncome getAnnual_income() {
		AnnualIncome annualIncome = AnnualIncome.getEnum(this.annual_income);
		return annualIncome;
	}

	public void setAnnual_income(AnnualIncome annualIncome) {
		this.annual_income = annualIncome.code;
	}

	private int net_asset;
	/** 资产估值 (枚举) */
	public NetAssets getNet_asset() {
		NetAssets assets = NetAssets.getEnum(this.net_asset);
		return assets;
	}

	public void setNet_asset(NetAssets netAssets) {
		this.net_asset = netAssets.code;
	}

	private int work_experience;
	/** 工作经验  (枚举)*/
	public WorkExperience getWork_experience() {
		WorkExperience workExperience = WorkExperience.getEnum(this.work_experience);
		return workExperience;
	}

	public void setWork_experience(WorkExperience workExperience) {
		this.work_experience = workExperience.code;
	}
	
	private int house;
	/** 房贷情况 (枚举) */
	public House getHouse() {
		House house = House.getEnum(this.house);
		return house;
	}

	public void setHouse(House house) {
		this.house = house.code;
	}
	
	private int car;
	/** 车贷情况  (枚举)*/
	public Car getCar() {
		Car car = Car.getEnum(this.car);
		return car;
	}

	public void setCar(Car car) {
		this.car = car.code;
	}
	
	private int emergency_contact_type;
	/** 紧急联系人类型 (枚举)：0、未选择；1、父母；2、配偶；3、子女；4、亲戚；5、朋友；6、同事； */
	public Relationship getEmergency_contact_type() {
		Relationship relationship = Relationship.getEnum(this.emergency_contact_type);
		return relationship;
	}

	public void setEmergency_contact_type(Relationship relationship) {
		this.emergency_contact_type = relationship.code;
	}
	
	/** 紧急联系人姓名 */
	public String emergency_contact_name;

	/** 紧急联系人手机号码 */
	public String emergency_contact_mobile;
	
	private int member_type;
	/**会员类型 (枚举)：-1-全部会员  0-新会员；1-理财会员；2-借款会员；3-复合会员（理财&借款）*/
	public MemberType getMember_type() {
		MemberType memberType = MemberType.getEnum(this.member_type);
		return memberType;
	}

	public void setMember_type(MemberType memberType) {
		this.member_type = memberType.code;
	}

	/** 成为借款会员时间 */
	public Date loan_member_time;

	/** 成为理财会员时间 */
	public Date invest_member_time;
	
	/** 用户基本信息编辑进度 */
	public int add_base_info_schedule;

	public enum MemberType{
		
		/** 全部会员:-1 */
		ALL_MEMBER(-1,"全部会员"),

		/** 新会员:0 */
		NEW_MEMBER(0,"新会员"),
		
		/** 理财会员:1 */
		FINANCIAL_MEMBER(1,"理财会员"),
		
		/** 借款会员:2 */
		BORROW_MEMBER(2,"借款会员"),
		
		/** 复合会员:3 */
		COMPOSITE_MEMBERS(3,"复合会员");
		
		public int code;
		public String value;
		
		private MemberType(int code, String value) {
			this.code = code;
			this.value = value;
		}
		
		/**
		 * 根据code取得会员类型,没有找到返回null
		 * @description 
		 *
		 * @author Chenzhipeng
		 * @createDate 2015年12月17日
		 * @param code
		 * @return
		 */
		public static MemberType getEnum(int code){
			MemberType[] memberTypes = MemberType.values();
			for (MemberType memberType : memberTypes) {
				if (memberType.code == code) {

					return memberType;
				}
			}
			
			return NEW_MEMBER;
		}
	}

	/** 年龄 */
	@Transient
	public int age;
	
	public int getAge(){
		
		if (StringUtils.isBlank(this.id_number)) {
			
			return 0;
		}
		Date birth = DateUtil.strToDate(this.id_number.substring(6, 14), "yyyyMMdd");
		int age = DateUtil.getAge(birth);
		return age ;
	}
	
	/**加密ID*/
	@Transient
	public String sign;

	public String getSign() {
		return Security.addSign(this.user_id, Constants.USER_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
	}
	
	public String hf_name="";
	
	
}
