package com.tenx.fraudamlmanager.paymentsv3.infrastructure.tma;

import com.tenx.fraudamlmanager.paymentsv3.direct.credit.infrastructure.tma.DirectCreditPaymentV3TMARequest;
import com.tenx.fraudamlmanager.paymentsv3.direct.debit.infrastructure.tma.DirectDebitPaymentV3TMARequest;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudAMLSanctionsCheckResponseCodeV3;
import com.tenx.fraudamlmanager.paymentsv3.domestic.infrastructure.tma.DomesticInPaymentTMARequestV3;
import com.tenx.fraudamlmanager.paymentsv3.domestic.infrastructure.tma.DomesticOutPaymentTMARequestV3;
import com.tenx.fraudamlmanager.paymentsv3.domestic.infrastructure.tma.DomesticOutReturnPaymentTMARequestV3;
import com.tenx.fraudamlmanager.paymentsv3.onus.infrastructure.OnUsPaymentV3;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author James Spencer
 */
@Service
@ConditionalOnProperty(value = "transactionmonitoring.enableMock",
    havingValue = "true")
public class TransactionMonitoringClientMockV3 implements TransactionMonitoringClientV3 {


    /**
     * @param domesticOutPaymentV3 the payload to pass
     */
    @PostMapping(value = "/v3/payments/domesticPaymentOutboundFinCrimeCheck", consumes = "application/json")
    public FraudAMLSanctionsCheckResponseV3 checkFinCrimeV3(
            @RequestBody DomesticOutPaymentTMARequestV3 domesticOutPaymentV3) {
        return new FraudAMLSanctionsCheckResponseV3(FraudAMLSanctionsCheckResponseCodeV3.passed);
    }

    /**
     * @param domesticOutReturnPaymentV3 the payload to pass
     */
    @PostMapping(value = "/v3/payments/domesticPaymentOutboundReturnFinCrimeCheck", consumes = "application/json")
    public FraudAMLSanctionsCheckResponseV3 checkFinCrimeV3(
            @RequestBody DomesticOutReturnPaymentTMARequestV3 domesticOutReturnPaymentV3) {
        return new FraudAMLSanctionsCheckResponseV3(FraudAMLSanctionsCheckResponseCodeV3.passed);
    }

    /**
     * @param domesticInPaymentV3 the payload to pass
     */
    @PostMapping(value = "/v3/payments/domesticPaymentInboundFinCrimeCheck", consumes = "application/json")
    public FraudAMLSanctionsCheckResponseV3 checkFinCrimeV3(@RequestBody DomesticInPaymentTMARequestV3 domesticInPaymentV3) {
        return new FraudAMLSanctionsCheckResponseV3(FraudAMLSanctionsCheckResponseCodeV3.passed);
    }

    /**
     * @param onUsPaymentV3 the payload to pass
     */
    @PostMapping(value = "/v3/payments/onUsFinCrimeCheck", consumes = "application/json")
    public FraudAMLSanctionsCheckResponseV3 checkFinCrimeV3(@RequestBody OnUsPaymentV3 onUsPaymentV3) {
        return new FraudAMLSanctionsCheckResponseV3(FraudAMLSanctionsCheckResponseCodeV3.passed);
    }

    /**
     * @param directDebitPaymentV3 payload to pass
     */
    @PostMapping(value = "/v3/payments/directDebitFinCrimeCheck", consumes = "application/json")
    public FraudAMLSanctionsCheckResponseV3 checkFinCrimeV3(@RequestBody DirectDebitPaymentV3TMARequest directDebitPaymentV3) {
        return new FraudAMLSanctionsCheckResponseV3(FraudAMLSanctionsCheckResponseCodeV3.passed);
    }

    /**
     * @param directCreditPaymentV3 the payload to pass
     */
    @PostMapping(value = "/v3/payments/directCreditFinCrimeCheck", consumes = "application/json")
    public FraudAMLSanctionsCheckResponseV3 checkFinCrimeV3(@RequestBody DirectCreditPaymentV3TMARequest directCreditPaymentV3) {
        return new FraudAMLSanctionsCheckResponseV3(FraudAMLSanctionsCheckResponseCodeV3.passed);
    }


}