package com.tenx.fraudamlmanager.paymentsv2.onus.api;

import com.tenx.fraudamlmanager.domain.PaymentMetrics;
import com.tenx.fraudamlmanager.domain.PaymentMetricsType;
import com.tenx.fraudamlmanager.paymentsv2.onus.domain.OnUsFinCrimeCheckServiceV2;
import com.tenx.fraudamlmanager.paymentsv2.onus.domain.OnUsPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.onus.domain.OnUsTransactionMonitoringExceptionV2;
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
public class OnUsPaymentsControllerV2 {

  @Autowired
  private OnUsFinCrimeCheckServiceV2 onUsFinCrimeCheckServiceV2;

  @Autowired
  private PaymentMetrics paymentMetrics;

  @ApiOperation(value = "Financial Crime check for On Us payments")
  @PostMapping("/onUsFinCrimeCheck")
  public void checkOnUsPayment(@RequestBody @Valid OnUsPaymentRequestV2 onUsPaymentRequestV2, String deviceKeyId)
    throws OnUsTransactionMonitoringExceptionV2 {
    log.info("onUsV2 request received with ID: {}", onUsPaymentRequestV2.getTransactionId());

    paymentMetrics.incrementCounterTag(PaymentMetrics.PAYMENTS_RECEIVED, PaymentMetricsType.ON_US.toString());

    OnUsPaymentV2 onUsPaymentV2 = OnUsPaymentMapperV2.MAPPER
      .toOnUsPayment(onUsPaymentRequestV2);

    onUsFinCrimeCheckServiceV2.checkFinCrimeV2(onUsPaymentV2);
  }

}
