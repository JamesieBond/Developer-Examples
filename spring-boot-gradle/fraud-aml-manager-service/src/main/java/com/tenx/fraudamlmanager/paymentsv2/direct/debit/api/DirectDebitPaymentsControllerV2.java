package com.tenx.fraudamlmanager.paymentsv2.direct.debit.api;

import com.tenx.fraudamlmanager.domain.PaymentMetrics;
import com.tenx.fraudamlmanager.domain.PaymentMetricsType;
import com.tenx.fraudamlmanager.paymentsv2.direct.debit.domain.DirectDebitBacsPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.direct.debit.domain.DirectDebitFinCrimeCheckServiceV2;
import com.tenx.fraudamlmanager.paymentsv2.direct.debit.domain.DirectDebitTransactionMonitoringExceptionV2;
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
@RequestMapping("/v2/payments")
@Slf4j
@Api(tags = "payments")
public class DirectDebitPaymentsControllerV2 {

    @Autowired
    private DirectDebitFinCrimeCheckServiceV2 directDebitFinCrimeCheckServiceV2;

    @Autowired
    private PaymentMetrics paymentMetrics;

    @ApiOperation(value = "Financial Crime check for DirectDebit payments")
    @PostMapping("/directDebitFinCrimeCheck")
    public void checkDirectDebit(@RequestBody @Valid DirectDebitBacsPaymentRequestV2 directDebitBacsPaymentRequestV2)
        throws DirectDebitTransactionMonitoringExceptionV2 {
        log.info("checkDirectDebitV2 request received with ID: {}", directDebitBacsPaymentRequestV2.getTransactionId());

        paymentMetrics.incrementCounterTag(PaymentMetrics.PAYMENTS_RECEIVED, PaymentMetricsType.DIRECT_DEBIT.toString());

        DirectDebitBacsPaymentV2 directDebitBacsPaymentV2 = ApiDomainDirectDebitPaymentMapperV2.MAPPER
            .toDirectDebitPayment(directDebitBacsPaymentRequestV2);
        directDebitFinCrimeCheckServiceV2.checkFinCrimeV2(directDebitBacsPaymentV2);
    }

}
