package controllers.payment.hf;

import controllers.payment.PaymentBaseCtrl;

/**
 * 请求汇付托管控制器
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2016年1月7日
 */
public class HfPaymentRequestCtrl extends PaymentBaseCtrl  {

	private static HfPaymentRequestCtrl instance = null;
	
	private HfPaymentRequestCtrl(){
		
	}
	
	public static HfPaymentRequestCtrl getInstance(){
		if(instance == null){
			synchronized (HfPaymentRequestCtrl.class) {
				if(instance == null){
					instance = new HfPaymentRequestCtrl();
				}
			}
		}
		
		return instance;
	}
	
}
