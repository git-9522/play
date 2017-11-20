package fddcontract;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fadada.sdk.client.FddClientBase;

import common.constants.ConfConst;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.ResultInfo;
import controllers.common.BaseController;
import models.common.entity.t_fdd_user_ca;
import models.fddcontract.bean.Customer;

import net.sf.json.JSONObject;
import services.base.BaseService;
import services.common.UserService;
import services.fddcontract.FddContractService;
import services.fddcontract.FddUserCaService;

/**	
 * 法大大合同管理
 *
 * @description 
 *
 * @author yuechuanyang
 * @createDate 2017年10月27日
 */
public class FddContract{
	
	protected static FddUserCaService fddUserCaService = Factory.getService(FddUserCaService.class);
	
	protected static FddContractService fddContractService = Factory.getService(FddContractService.class);

	
	/**	
	 * 法大大合同管理主方法
	 *
	 * @description 
	 *
	 * @author yuechuanyang
	 * @createDate 2017年10月27日
	 */
	public Map<String,Object> electronicContract(long bidId,File file,List<Customer> custs,String contract_id,String doc_title,String doc_type){
		 Map<String,Object> resultMap = new  HashMap<String,Object>();
		
		String tranResult = documentTransmission(contract_id,doc_title,file,doc_type);
		JSONObject obj = JSONObject.fromObject(tranResult);
		if(FddConst.FDD_SUCCESS_RESULT.equals(obj.get("result").toString()) && FddConst.FDD_SUCCESS_CODE.equals(obj.get("code").toString())){
			//所有客户签署
			for(Customer cus : custs){
				t_fdd_user_ca tfuc = fddUserCaService.selectFddUserCa(cus.getUser_id(), cus.getCustomer_name(), cus.getEmail(), cus.getMobile(), cus.getId());
				if(tfuc != null){
					String customerId = tfuc.getCustomer_id();
					String sign_keyword = getSignKeyword(cus.getClient_role());
					documentSigning(bidId,cus.getUser_id(),customerId,cus.getClient_role(),contract_id,doc_title,sign_keyword,FddConst.KEY_WORLD_LAST);
				}else{
					String customerId = getCustomerId(cus);
					String sign_keyword = getSignKeyword(cus.getClient_role());
					documentSigning(bidId,cus.getUser_id(),customerId,cus.getClient_role(),contract_id,doc_title,sign_keyword,FddConst.KEY_WORLD_LAST);
				}
			}
			//平台签署
			String customerId = ConfConst.FDD_CUSTOMER_ID;
			String sign_keyword = getSignKeyword(FddConst.CLIENT_ROLE_HA);
			documentSigning(bidId,0,customerId,FddConst.CLIENT_ROLE_HA,contract_id,doc_title,sign_keyword,FddConst.KEY_WORLD_LAST);
			//合同归档
			String result = contractFiling(contract_id);
			if(FddConst.FDD_SUCCESS_RESULT.equals(JSONObject.fromObject(result).get("result").toString()) && FddConst.FDD_SUCCESS_CODE.equals(JSONObject.fromObject(result).get("code").toString())){
				resultMap.put("code", 1);
				resultMap.put("msg", "合同归档成功");
			}else{
				resultMap.put("code", -1);
				resultMap.put("msg", JSONObject.fromObject(result).get("msg").toString());
			}
		}else{
			resultMap.put("code", -1);
			resultMap.put("msg", obj.get("msg").toString());
		};
		return resultMap;
	}
	
	
	/**	
	 * 法大大合同管理-获取用户id
	 *
	 * @description 
	 *
	 * @author yuechuanyang
	 * @createDate 2017年10月27日
	 */
	public  String getCustomerId (Customer customer){
		FddClientBase clientbase = new FddClientBase(ConfConst.FDD_APP_ID, ConfConst.FDD_SECRET, ConfConst.FDD_VERSION, ConfConst.FDD_URL);
		String response = clientbase.invokeSyncPersonAuto(customer.getCustomer_name(), customer.getEmail(), customer.getId(),customer.getIdent_type(), customer.getMobile());
		LoggerUtil.info(false, "获取用户id:"+response);
		JSONObject obj = JSONObject.fromObject(response);
		if(FddConst.FDD_SUCCESS_CODE.equals(obj.get("code").toString())){
			fddUserCaService.saveFddUserCa(customer.getUser_id(), obj.get("customer_id").toString(), customer.getCustomer_name(), customer.getEmail(), customer.getMobile(), customer.getId());
		}
		return obj.get("customer_id").toString();
	}
	
	/**	
	 * 法大大合同管理-文档签署
	 *
	 * @description 
	 *
	 * @author yuechuanyang
	 * @createDate 2017年10月27日
	 */
	public  String documentSigning (long bid_id,long user_id,String customer_id,String client_role,String contract_id,String doc_title,String sign_keyword,String keyword_strategy){
		String transaction_id = UUID.randomUUID().toString();
		String notify_url = "";
		FddClientBase client = new FddClientBase(ConfConst.FDD_APP_ID, ConfConst.FDD_SECRET, ConfConst.FDD_VERSION, ConfConst.FDD_URL);
		String response =client.invokeExtSignAuto(transaction_id,customer_id,client_role,contract_id,doc_title,sign_keyword,keyword_strategy,notify_url);
		LoggerUtil.info(false, "文档签署:"+response);
		fddContractService.saveFddContract(user_id, transaction_id, contract_id, customer_id,bid_id,JSONObject.fromObject(response).getString("download_url"),JSONObject.fromObject(response).getString("viewpdf_url"));
		return response;
	}
	
	
	/**	
	 * 法大大合同管理-文档传输
	 *
	 * @description 
	 *
	 * @author yuechuanyang
	 * @createDate 2017年10月27日
	 */
	public  String documentTransmission(String contract_id,String doc_title,File file,String doc_type){
		FddClientBase clientbase = new FddClientBase(ConfConst.FDD_APP_ID, ConfConst.FDD_SECRET, ConfConst.FDD_VERSION, ConfConst.FDD_URL);
		String response = clientbase.invokeUploadDocs(contract_id, doc_title, file, null,doc_type);
		LoggerUtil.info(false, "文档传输:"+response);
		return response;
	}
	
	
	/**	
	 * 法大大合同管理-合同归档
	 *
	 * @description 
	 *
	 * @author yuechuanyang
	 * @createDate 2017年10月27日
	 */
	public  String contractFiling(String contract_id){
		FddClientBase clientbase = new FddClientBase(ConfConst.FDD_APP_ID, ConfConst.FDD_SECRET, ConfConst.FDD_VERSION, ConfConst.FDD_URL);
		String response = clientbase.invokeContractFilling(contract_id);
		LoggerUtil.info(false, "合同归档:"+response);
		return response;
	}
	
	
	
	public  String getSignKeyword(String clientRole){
		String sign_keyword = "";
		if(clientRole == FddConst.CLIENT_ROLE_HA){
			sign_keyword = "丙方";
		}
		if(clientRole == FddConst.CLIENT_ROLE_INV){
			sign_keyword = "甲方";
		}
		if(clientRole == FddConst.CLIENT_ROLE_BOW){
			sign_keyword = "乙方";
		}
		return sign_keyword;
	}
}
