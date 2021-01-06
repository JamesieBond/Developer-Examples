package com.tenx.fraudamlmanager.paymentsv2.domestic.in.api;

import com.tenx.fraudamlmanager.domain.PaymentMetrics;
import com.tenx.fraudamlmanager.domain.PaymentMetricsType;
import com.tenx.fraudamlmanager.paymentsv2.domestic.in.domain.DomesticInFinCrimeCheckServiceV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.in.domain.DomesticInPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.in.domain.DomesticInTransactionMonitoringExceptionV2;
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
public class DomesticInPaymentsControllerV2 {

  @Autowired
  private DomesticInFinCrimeCheckServiceV2 domesticInFinCrimeCheckServiceV2;

  @Autowired
  private PaymentMetrics paymentMetrics;

  @ApiOperation(value = "Financial Crime check for FPS inbound payments")
  @PostMapping("/domesticPaymentInboundFinCrimeCheck")
  public void checkDomesticInPaymentV2(
    @RequestBody @Valid DomesticInPaymentRequestV2 domesticInPaymentRequestV2)
    throws DomesticInTransactionMonitoringExceptionV2 {
    log.info("checkFraudFpsInboundPaymentV2 request received with ID: {}",
      domesticInPaymentRequestV2.getTransactionId());

    paymentMetrics.incrementCounterTag(PaymentMetrics.PAYMENTS_RECEIVED, PaymentMetricsType.DOMESTIC_IN.toString());
    DomesticInPaymentV2 domesticInPaymentV2 = DomesticInPaymentRequestV2Mapper.MAPPER
      .domesticInPaymentRequestV2toDomesticInPaymentV2(domesticInPaymentRequestV2);
    domesticInFinCrimeCheckServiceV2.checkFinCrimeV2(domesticInPaymentV2);

  }

}
