package com.tenx.fraudamlmanager.payments.controller.v1;

import com.tenx.fraudamlmanager.domain.PaymentMetrics;
import com.tenx.fraudamlmanager.domain.PaymentMetricsType;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.payments.model.api.DirectDebitPayment;
import com.tenx.fraudamlmanager.payments.model.api.FraudCheckResponse;
import com.tenx.fraudamlmanager.payments.service.FinCrimeCheckService;
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
@RequestMapping("/v1/payments")
@Slf4j
@Api(tags = "payments")
public class DirectDebitPaymentsController {
    @Autowired
    private FinCrimeCheckService finCrimeCheckService;

    @Autowired
    private PaymentMetrics paymentMetrics;

    @ApiOperation(value = "Endpoint to consume Direct Debit for payments")
    @PostMapping("/directDebit/finCrimeCheck")
    public FraudCheckResponse checkDirectDebit(@RequestBody @Valid DirectDebitPayment directDebitPayment) throws TransactionMonitoringException {
        log.info("checkDirectDebitV1 request received with ID: {}", directDebitPayment.getId());
        paymentMetrics.incrementCounterTag(PaymentMetrics.PAYMENTS_RECEIVED, PaymentMetricsType.DIRECT_DEBIT.toString());
        return finCrimeCheckService.checkFinCrime(directDebitPayment);
    }

}
