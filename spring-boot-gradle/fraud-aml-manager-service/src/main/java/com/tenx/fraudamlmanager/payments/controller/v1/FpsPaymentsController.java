package com.tenx.fraudamlmanager.payments.controller.v1;

import com.tenx.fraudamlmanager.domain.PaymentMetrics;
import com.tenx.fraudamlmanager.domain.PaymentMetricsType;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.payments.model.api.FpsInboundPayment;
import com.tenx.fraudamlmanager.payments.model.api.FpsOutboundPayment;
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
public class FpsPaymentsController {
    @Autowired
    private FinCrimeCheckService finCrimeCheckService;

    @Autowired
    private PaymentMetrics paymentMetrics;

    @ApiOperation(value = "Financial Crime check for FPS outbound payments")
    @PostMapping("/fpsOutbound/finCrimeCheck")
    public FraudCheckResponse checkFpsOutboundPayment(@RequestBody @Valid FpsOutboundPayment fpsOutboundPayment) throws TransactionMonitoringException {
        log.info("checkFraudFpsOutboundPaymentV1 request received with ID: {}", fpsOutboundPayment.getTransactionId());
        paymentMetrics.incrementCounterTag(PaymentMetrics.PAYMENTS_RECEIVED, PaymentMetricsType.DOMESTIC_OUT.toString());
        return finCrimeCheckService.checkFinCrime(fpsOutboundPayment);
    }
    @ApiOperation(value = "Financial Crime check for FPS inbound payments")
    @PostMapping("/fpsInbound/finCrimeCheck")
    public FraudCheckResponse checkFpsInboundPayment(@RequestBody @Valid FpsInboundPayment fpsInboundPayment) throws TransactionMonitoringException {
        log.info("checkFraudFpsInboundPaymentV1 request received with ID: {}", fpsInboundPayment.getTransactionId());
        paymentMetrics.incrementCounterTag(PaymentMetrics.PAYMENTS_RECEIVED, PaymentMetricsType.DOMESTIC_IN.toString());;
        return finCrimeCheckService.checkFinCrime(fpsInboundPayment);
    }

}
