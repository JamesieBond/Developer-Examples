package com.tenx.fraudamlmanager.payments.cases;

import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.DomesticOutPayment;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.OnUsPayment;

public interface PaymentCaseServiceV1 {

	void createBlockedPaymentCase(DomesticOutPayment domesticOutPayment);

	void createBlockedPaymentCase(OnUsPayment onUsPayment);

}
