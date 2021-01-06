package com.tenx.fraudamlmanager.paymentsv3.direct.debit.api;

import com.tenx.fraudamlmanager.domain.PaymentMetrics;
import com.tenx.fraudamlmanager.domain.PaymentMetricsType;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.paymentsv3.api.DomainApiFraudCheckResponseV3Mapper;
import com.tenx.fraudamlmanager.paymentsv3.api.FraudCheckResponseV3;
import com.tenx.fraudamlmanager.paymentsv3.direct.debit.domain.DirectDebitFinCrimeCheckServiceV3;
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
public class DirectDebitPaymentsControllerV3 {

    @Autowired
    private DirectDebitFinCrimeCheckServiceV3 directDebitFinCrimeCheckServiceV3;

    @Autowired
    private PaymentMetrics paymentMetrics;

    @ApiOperation(value = "Endpoint to consume Direct Debit for payments")
    @PostMapping("/directDebitFinCrimeCheck")
    public FraudCheckResponseV3 checkDirectDebitV3(
        @RequestBody @Valid DirectDebitPaymentRequestV3 directDebitPaymentV3)
        throws TransactionMonitoringException {
        log.info("checkDirectDebitV3 request received with ID: {}", directDebitPaymentV3.getTransactionId());

        paymentMetrics.incrementCounterTag(PaymentMetrics.PAYMENTS_RECEIVED, PaymentMetricsType.DIRECT_DEBIT.toString());
        return DomainApiFraudCheckResponseV3Mapper.MAPPER.toFraudCheckResponseV3(
                directDebitFinCrimeCheckServiceV3.checkFinCrimeV3(
                        ApiDomainDirectDebitPaymentMapper.MAPPER.toDirectDebitPayment(directDebitPaymentV3)));
    }

}
