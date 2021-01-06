package com.tenx.fraudamlmanager.paymentsv3.onus.api;

import com.tenx.fraudamlmanager.domain.PaymentMetrics;
import com.tenx.fraudamlmanager.domain.PaymentMetricsType;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.paymentsv3.api.DomainApiFraudCheckResponseV3Mapper;
import com.tenx.fraudamlmanager.paymentsv3.api.FraudCheckResponseV3;
import com.tenx.fraudamlmanager.paymentsv3.onus.domain.OnUsFinCrimeCheckServiceV3;
import com.tenx.fraudamlmanager.paymentsv3.onus.infrastructure.OnUsPaymentV3;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v3/payments")
@Slf4j
@Api(tags = "payments")
public class OnUsPaymentsControllerV3 {

    @Autowired
    private OnUsFinCrimeCheckServiceV3 onUsFinCrimeCheckServiceV3;

    @Autowired
    private OnUsPaymentMapper onUsMapper;

    @Autowired
    private PaymentMetrics paymentMetrics;

    @ApiOperation(value = "Financial Crime check for On Us payments")
    @PostMapping("/onUsFinCrimeCheck")
    public FraudCheckResponseV3 checkOnUsPayment(@RequestBody @Valid OnUsPaymentRequestV3 onUsPaymentRequestV3,
        @RequestHeader(value = "deviceKeyId", required = false) String deviceKeyId)
        throws TransactionMonitoringException {
        log.info("checkFraudOnUsPayment request received with ID: {}",
            onUsPaymentRequestV3.getTransactionId());

        paymentMetrics.incrementCounterTag(PaymentMetrics.PAYMENTS_RECEIVED, PaymentMetricsType.ON_US.toString());
        OnUsPaymentV3 onUsPaymentV3 = onUsMapper.toOnUsPayment(onUsPaymentRequestV3);
        return DomainApiFraudCheckResponseV3Mapper.MAPPER.toFraudCheckResponseV3(onUsFinCrimeCheckServiceV3.checkFinCrimeV3(onUsPaymentV3, deviceKeyId));
    }

}
