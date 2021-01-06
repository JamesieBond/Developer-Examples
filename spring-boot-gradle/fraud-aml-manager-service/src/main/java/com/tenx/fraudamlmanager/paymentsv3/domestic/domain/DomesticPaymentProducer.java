package com.tenx.fraudamlmanager.paymentsv3.domestic.domain;

import com.tenx.fraudamlmanager.paymentsv3.domain.FraudCheckV3;

public interface DomesticPaymentProducer {

    void publishDomesticInResponseEvent(FraudCheckV3 fraudCheckV3, String transactionId);

    void publishDomesticOutResponseEvent(FraudCheckV3 fraudCheckV3, String transactionId);

    void publishDomesticOutReturnResponseEvent(FraudCheckV3 fraudCheckV3, String transactionId);
}
