package com.tenx.fraudamlmanager.paymentsv3.infrastructure.tma;

import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.TransactionMonitoringConfigV3;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.paymentsv3.direct.credit.infrastructure.tma.DirectCreditPaymentV3TMARequest;
import com.tenx.fraudamlmanager.paymentsv3.direct.debit.infrastructure.tma.DirectDebitPaymentV3TMARequest;
import com.tenx.fraudamlmanager.paymentsv3.domestic.infrastructure.tma.DomesticInPaymentTMARequestV3;
import com.tenx.fraudamlmanager.paymentsv3.domestic.infrastructure.tma.DomesticOutPaymentTMARequestV3;
import com.tenx.fraudamlmanager.paymentsv3.domestic.infrastructure.tma.DomesticOutReturnPaymentTMARequestV3;
import com.tenx.fraudamlmanager.paymentsv3.onus.infrastructure.OnUsPaymentV3;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author Massimo Della Rovere
 */
@FeignClient(name = "transaction-monitoring-adapter-v3", url = "${transactionmonitoring.url}", configuration = TransactionMonitoringConfigV3.class)
@ConditionalOnProperty(value = "transactionmonitoring.enableMock", havingValue = "false", matchIfMissing = true)
public interface TransactionMonitoringClientV3 {

    /**
     * @param domesticOutPaymentTMARequestV3 the payload to pass
     */
    @PostMapping(value = "/v3/payments/domesticPaymentOutboundFinCrimeCheck", consumes = "application/json")
    FraudAMLSanctionsCheckResponseV3 checkFinCrimeV3(@RequestBody DomesticOutPaymentTMARequestV3 domesticOutPaymentTMARequestV3)
            throws TransactionMonitoringException;

    /**
     * @param domesticOutReturnPaymentTMARequestV3 the payload to pass
     */
    @PostMapping(value = "/v3/payments/domesticPaymentOutboundReturnFinCrimeCheck", consumes = "application/json")
    FraudAMLSanctionsCheckResponseV3 checkFinCrimeV3(@RequestBody DomesticOutReturnPaymentTMARequestV3 domesticOutReturnPaymentTMARequestV3)
            throws TransactionMonitoringException;

    /**
     * @param domesticInPaymentTMARequestV3 the payload to pass
     */
    @PostMapping(value = "/v3/payments/domesticPaymentInboundFinCrimeCheck", consumes = "application/json")
    FraudAMLSanctionsCheckResponseV3 checkFinCrimeV3(@RequestBody DomesticInPaymentTMARequestV3 domesticInPaymentTMARequestV3)
            throws TransactionMonitoringException;

    /**
     * @param onUsPaymentV3 the payload to pass
     */
    @PostMapping(value = "/v3/payments/onUsFinCrimeCheck", consumes = "application/json")
    FraudAMLSanctionsCheckResponseV3 checkFinCrimeV3(@RequestBody OnUsPaymentV3 onUsPaymentV3)
        throws TransactionMonitoringException;

    /**
     * @param directDebitPaymentV3 payload to pass
     */
    @PostMapping(value = "/v3/payments/directDebitFinCrimeCheck", consumes = "application/json")
    FraudAMLSanctionsCheckResponseV3 checkFinCrimeV3(@RequestBody DirectDebitPaymentV3TMARequest directDebitPaymentV3)
        throws TransactionMonitoringException;

    /**
     * @param directCreditPaymentV3 the payload to pass
     */
    @PostMapping(value = "/v3/payments/directCreditFinCrimeCheck", consumes = "application/json")
    FraudAMLSanctionsCheckResponseV3 checkFinCrimeV3(@RequestBody DirectCreditPaymentV3TMARequest directCreditPaymentV3)
        throws TransactionMonitoringException;
}