package com.tenx.fraudamlmanager.paymentsv2.cases.domain;

import com.tenx.fraudamlmanager.cases.v2.domain.PaymentCaseDeletion;
import com.tenx.fraudamlmanager.cases.v2.domain.TransactionCaseCreation;
import com.tenx.fraudamlmanager.paymentsv2.domestic.in.domain.DomesticInPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.out.domain.DomesticOutPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain.DomesticOutReturnPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.onus.domain.OnUsPaymentV2;

public interface PaymentCaseServiceV2 extends PaymentCaseDeletion, TransactionCaseCreation {

	void createSavePaymentCase(OnUsPaymentV2 onUsPaymentV2);

	void createSavePaymentCase(DomesticOutPaymentV2 domesticOutPaymentV2);

	void createSavePaymentCase(DomesticOutReturnPaymentV2 domesticOutReturnPaymentV2);

	void createSavePaymentCase(DomesticInPaymentV2 domesticInPaymentV2);

}
