package controllers.payment.fy;

import controllers.payment.PaymentBaseCtrl;

public class FyPaymentRequestCtrl extends PaymentBaseCtrl{

	private static FyPaymentRequestCtrl instance = null;
	
	private FyPaymentRequestCtrl(){
		
	}
	
	public static FyPaymentRequestCtrl getInstance(){
		if (instance == null){
			synchronized (FyPaymentRequestCtrl.class){
				if (instance == null){
					instance = new FyPaymentRequestCtrl();
				}
			}
		}
		
		return instance;
	}
	
}
