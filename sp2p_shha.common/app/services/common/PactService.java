package services.common;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import common.enums.PactType;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.ResultInfo;
import common.utils.StrUtil;
import common.utils.pdf.DefaultPdfWatermark;
import common.utils.pdf.PDFUtil;
import daos.common.PactDao;
import daos.common.TemplatePactDao;
import models.common.entity.t_pact;
import models.common.entity.t_template_pact;
import play.Play;
import services.base.BaseService;

/**
 * 合同service的具体实现
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年1月18日
 */
public class PactService extends BaseService<t_pact> {

	protected PactDao pactDao = null;
	
	protected TemplatePactDao templatePactDao = Factory.getDao(TemplatePactDao.class);
	
	protected PactService (){
		this.pactDao = Factory.getDao(PactDao.class);
		super.basedao = pactDao;
	}	
	
	/**
	 * 插入一条合同记录到数据库
	 *
	 * @param pact
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月19日
	 */
	public ResultInfo createPact(t_pact pact) {
		ResultInfo result = new ResultInfo();
		boolean flag = pactDao.save(pact);
		if(!flag){
			result.code = -1;
			result.msg = "合同没有添加到数据库";
			
			return result;
		} 
		result.code = 1;
		result.msg = "合同添加成功";
		result.obj = pact;
	
		return result;
	}
	
	/**
	 * 更新一个合同模板的名称/内容、水印
	 *
	 * @param id 待更新模板的id
	 * @param name 更新后的名称
	 * @param content 合同模板的内容
	 * @param imageUrl 水印图片的路径
	 * @param imageResolution 水印图片吗的分辨率
	 * @param imageSize 水印图片的大小
	 * @param imageFormat 水印图片的后缀名
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月18日
	 */
	public boolean updatePactTemp(long id,String name,String content,String imageUrl,String imageResolution,String imageSize,String imageFormat) {
		t_template_pact pact = templatePactDao.findByID(id);
		pact.name = name;
		pact.content = content;
		pact.image_url = imageUrl;
		pact.image_resolution = imageResolution;
		pact.image_size = imageSize;
		pact.image_format = imageFormat;
		
		return templatePactDao.save(pact);
	}

	/**
	 * 根据合同类型查找合同模板
	 *
	 * @param pactType
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月19日
	 */
	public t_template_pact findByType(PactType pactType) {
		t_template_pact temp = templatePactDao.findByColumn(" type=? ", pactType.code);
		
		return temp;
	}
	
	/**
	 * 查找bid对应的合同(一个bid只有一份合同)
	 *
	 * @param bidId
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月19日
	 */
	public t_pact findByBidid(long bidId) {
		t_pact pact = pactDao.findByColumn(" type=? and pid=?", PactType.BID.code,bidId);
		
		return  pact;
	}
	
	/**
	 * 查找债权对应的合同(一个债权只有一份合同)
	 *
	 * @param debtId
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月19日
	 */
	public t_pact findByDebtId(long debtId) {
		t_pact pact = pactDao.findByColumn(" type=? and pid=?", PactType.DEBT.code,debtId);
		
		return  pact;
	}
	
	/**
	 * 查询所有的合同模板
	 *
	 * @param currPage
	 * @param pageSize
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月18日
	 */
	public List<t_template_pact> queryAllTemps() {
		
		return templatePactDao.findAll();
	}

	/**
	 * 根据合同模板id查找合同
	 *
	 * @param tempId 合同id
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月18日
	 */
	public t_template_pact findByTempId(long tempId) {
		
		return templatePactDao.findByID(tempId);
	}

	/**
	 * 导出合同成PDF文档
	 *
	 * @param pactId 合同id
	 * @param withWater 是否添加水印
	 * @return 如果成功则obj中为导出后的file
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月19日
	 */
	public ResultInfo exportPact(long pactId, boolean withWater) {
		ResultInfo result = new ResultInfo();
				
		t_pact pact = pactDao.findByID(pactId);
		if (pact == null) {
			result.code = -1;
			result.msg = "合同不存在";

			return result; 
		}
		
		File expFile = null;
		try {
			String image_url = pact.image_url;
			if(withWater && StringUtils.isNotBlank(image_url)){
				
				if (image_url.startsWith("/")) {
					image_url = image_url.substring(1);
				}
				File file = Play.getFile(image_url);
				String imgString = "";
				if(file.exists()){
					imgString = file.getAbsolutePath();
				} else {
					imgString = PDFUtil.WATERMARKIMAGEPATH;
				}
				
				expFile = PDFUtil.exportHTMLPdfWithWatermark(pact.content, null, new DefaultPdfWatermark(imgString));
			} else {
				expFile = PDFUtil.exportHTMLPdf(pact.content, null);
			}
			result.code = 1;
			result.msg = "导出成功";
			result.obj = expFile;
			
		} catch (Exception e) {
			e.printStackTrace();
			LoggerUtil.info(false, e, "合同导出失败【合同id:%s】", pactId+"");
			result.code = -1;
			result.msg = "合同导出失败!";
		}
		
		return result;
	}
	public String showBidPactModelPre(){
		
		DecimalFormat myformat = new DecimalFormat();
		myformat.applyPattern("0.00");
		
		Map<String, String> contentParam = new HashMap<String, String>();
		String now = DateUtil.dateToString(new Date(), "yyyy年MM月dd日");
		t_template_pact temp = this.findByType(PactType.BID);
	
		
		//合同名称:pact_name
		contentParam.put("pact_name", "");
		
		//合同编号:pact_no
		contentParam.put("pact_no", "");
		
		//借款人真实姓名loan_real_name
		contentParam.put("loan_real_name","");

		//借款人平台用户名:loan_name
		contentParam.put("loan_name", "");
		
		//借款人平台用户名:id_number
		contentParam.put("id_number", "");
		
		//资金借出方:invest_list(table,资金借出方(网站用户名),借出金额)
		contentParam.put("invest_list", "");
		
		//合同签订时间
		contentParam.put("date_sign", "");
		
		//借款用途:purpose_name
		contentParam.put("purpose_name", "");
				
		//借款开始日期:release_date
		contentParam.put("release_date","");
					
		//借款到期日:last_repay_time
		contentParam.put("last_repay_time", "");
			
		//放款方式:repayment_type
		contentParam.put("repayment_type","");
		
		//借款金额:bid_amount
		contentParam.put("bid_amount", "");
		
		//借款金额大写:bid_camount
		contentParam.put("bid_camount", "");
		
		//借款期限:period_num
		contentParam.put("period_num", "");
			
		//借款期限单位:period_unit
		contentParam.put("period_unit", "");
					
		//借款年利率:apr
		contentParam.put("apr", "");
		
		//借款服务费的key：借款金额百分比loan_amount_rate
		contentParam.put("loan_amount_rate", "");
		
		//借款服务费的key：减去的借款期数sub_period
		contentParam.put("sub_period", "");
				
		// 借款服务费的key：减去借款期数后，借款金额的百分比sub_loan_amount_rate
		contentParam.put("sub_loanAmount_rate", "");
		
		//理财服务费的key：百分比invest_amount_rate
		contentParam.put("invest_amount_rate", "");
		
		//逾期罚息的key：百分比overdue_amount_rate
		contentParam.put("overdue_amount_rate", "");
		
		//转让服务费率:transfer_fee_rate
		contentParam.put("transfer_fee_rate",  "");
		
		//借款人签署时间
		contentParam.put("date_real_name", "");		
		
		
		//出借人信息列表
		contentParam.put("invest_name_list", "");
		//借款人签署时间
		contentParam.put("date_invest", now);
		
		//平台名称:plateform_name
		contentParam.put("plateform_name", "");
		
		//平台签署时间:date_plateform
		contentParam.put("date_plateform", now);
		
		//还款计划表:repay_list(table)

		contentParam.put("repay_list", "");
		return StrUtil.replaceByMap(temp.content, contentParam);
		
	}
	

}
