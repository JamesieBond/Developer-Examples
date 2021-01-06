package com.tenx.fraudamlmanager.paymentsv3.direct.credit.api;

import com.tenx.fraudamlmanager.domain.PaymentMetrics;
import com.tenx.fraudamlmanager.domain.PaymentMetricsType;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.paymentsv3.api.DomainApiFraudCheckResponseV3Mapper;
import com.tenx.fraudamlmanager.paymentsv3.api.FraudCheckResponseV3;
import com.tenx.fraudamlmanager.paymentsv3.direct.credit.domain.DirectCreditFinCrimeCheckServiceV3;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v3/payments")
@Slf4j
@Api(tags = "payments")
public class DirectCreditPaymentsControllerV3 {

    @Autowired
    private DirectCreditFinCrimeCheckServiceV3 finCrimeCheckServiceV3;

    @Autowired
    private PaymentMetrics paymentMetrics;

    @ApiOperation(value = "Endpoint to consume Direct Credit for payments")
    @PostMapping("/directCreditFinCrimeCheck")
    public FraudCheckResponseV3 checkDirectCredit(
        @RequestBody @Valid DirectCreditPaymentRequestV3 directCreditPaymentV3)
        throws TransactionMonitoringException {
        log.info("checkDirectCreditV3 request received with ID: {}", directCreditPaymentV3.getTransactionId());
        paymentMetrics.incrementCounterTag(PaymentMetrics.PAYMENTS_RECEIVED, PaymentMetricsType.DIRECT_CREDIT.toString());
        return DomainApiFraudCheckResponseV3Mapper.MAPPER.toFraudCheckResponseV3
            (finCrimeCheckServiceV3.checkFinCrimeV3(
                ApiDomainDirectCreditPaymentMapper.MAPPER.toDirectCreditPayment(directCreditPaymentV3)));
    }
}
