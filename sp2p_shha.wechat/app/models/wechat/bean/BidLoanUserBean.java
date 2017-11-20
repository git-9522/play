package models.wechat.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import common.enums.AnnualIncome;
import common.enums.Car;
import common.enums.Education;
import common.enums.Gender;
import common.enums.House;
import common.enums.Marital;
import common.enums.NetAssets;
import common.enums.Province;
import common.enums.WorkExperience;
import common.utils.DateUtil;
import common.utils.StrUtil;
import models.core.entity.t_product.RepaymentType;
/***
 * 借款人信息和标信息实体
 *
 * @description 
 *
 * @author luzhiwei
 * @createDate 2016-5-5
 */
@Entity
public class BidLoanUserBean implements Serializable {

	public long bid_id;

	/** 真实姓名 */
	public String realityName;
	public String getRealityName() {
		if(null==realityName||"".equals(realityName)){
			return  "";
		}
		return  StrUtil.asterisk(realityName==null?"":realityName, 1, 0, 3);
	}
    @Id
	/** 身份证号码 */
	public String idNumber;
	

	/** 信用等级 */
	public String creditLevelId;


	
	public  int sex;
	/** 性别(枚举)： 1 男 2 女 3 保密 */
	public String getSex() {
		Gender gender = Gender.getEnum(sex);
		return gender == null ? null : gender.name;
	}



	
	public int education;
	/** 教育情况 (枚举)*/
	public String getEducation() {
		Education education = Education.getEnum(this.education);
		return education==null ? null : education.value;
	}

	public int marital;
	/** 婚姻状况  (枚举)*/
	public String getMarital() {
		Marital marital = Marital.getEnum(this.marital);
		return marital==null ? null : marital.value;
	}

	
	public int annualIncome;
	/** 年收入 (枚举) */
	public String getAnnualIncome() {
		AnnualIncome annualIncom = AnnualIncome.getEnum(this.annualIncome);
		return annualIncom == null ? null : annualIncom.value;
	}


	public int netAsset;
	/** 资产估值 (枚举) */
	public String getNetAsset() {
		NetAssets assets = NetAssets.getEnum(this.netAsset);
		return assets == null ? null : assets.value;
	}

	public int workExperience;
	/** 工作经验  (枚举)*/
	public String getWorkExperience() {
		WorkExperience workExperience = WorkExperience.getEnum(this.workExperience);
		return workExperience == null ? null : workExperience.value;
	}

	
	public int house;
	/** 房贷情况 (枚举) */
	public String getHouse() {
		House house = House.getEnum(this.house);
		return house == null ? null : house.value;
	}

	public int car;
	/** 车贷情况  (枚举)*/
	public String getCar() {
		Car car = Car.getEnum(this.car);
		return car == null ? null : car.value;
	}

	
	/** 借款描述  */
	public String description;
   
	
	/** 风控描述  */
	public String preauditSuggest;

	@Transient
	public int age;
	
	/** 年龄 */
	public int getAge(){
		
		if (StringUtils.isBlank(this.idNumber)) {
			
			return 0;
		}
		Date birth = DateUtil.strToDate(this.idNumber.substring(6, 14), "yyyyMMdd");
		int age = DateUtil.getAge(birth);
		return age ;
	}
	/** 还款方式
	 * 1-先息后本
	 * 2-等本等息
	 * 3-一次性还款 */
	public int repaymentType;
	public String getRepaymentType() {
		RepaymentType repType = RepaymentType.getEnum(this.repaymentType);
		
		return repType == null ? null : repType.value;
	}
	 
	/**借款期限*/
	public int period;
	/**购买期限*/
	public int invest_period;
	
	   /**户籍编号*/
	   public String prov_id; 
	   /**户籍*/
	   @Transient
	   public String province;
	   public String getProvince(){
		   Province province = Province.getProvByCode(prov_id);
		 return  province==null?"":province.name;
       }
	  public double apr;
	   /** 还款来源*/
	   public  String repayment_source;
	   /** 担保措施*/
	   public String guarantee_measures;
	    /**保障措施id*/
		public Long guarantee_mode_id;
		public String  guarantee_mode;
		/**工作单位*/
		public String work_unit;

		public String getWork_unit() {
			if(null==work_unit||"".equals(work_unit)){
				return "";
			}
			return StrUtil.asterisk(work_unit, 2, 4, 2);
		}

		/**成立时间*/
		public Date start_time;
		/**注册资金*/
		public double registered_fund;
		/**所属借款产品ID*/
		public long product_id;
		/**标题*/
		public String title;
    
    
}
