package com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.api;

import com.tenx.fraudamlmanager.domain.PaymentMetrics;
import com.tenx.fraudamlmanager.domain.PaymentMetricsType;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain.DomesticOutReturnFinCrimeCheckServiceV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain.DomesticOutReturnPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain.DomesticOutReturnTransactionMonitoringExceptionV2;
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
public class DomesticOutReturnPaymentControllerV2 {

  @Autowired
  private DomesticOutReturnFinCrimeCheckServiceV2 domesticOutReturnFinCrimeCheckServiceV2;

  @Autowired
  private PaymentMetrics paymentMetrics;

  @ApiOperation(value = "Tracking returned FPS out bound payments")
  @PostMapping("/domesticPaymentOutboundReturnFinCrimeCheck")
  public void checkFpsOutboundPaymentReturn(@RequestBody @Valid DomesticOutReturnPaymentRequestV2
      domesticOutReturnPaymentRequestV2)
      throws DomesticOutReturnTransactionMonitoringExceptionV2 {
    log.info("domesticPaymentOutboundReturnV2 request received with ID: {}",
        domesticOutReturnPaymentRequestV2.getTransactionId());

    paymentMetrics.incrementCounterTag(PaymentMetrics.PAYMENTS_RETURNED, PaymentMetricsType.DOMESTIC_OUT.toString());

    DomesticOutReturnPaymentV2 domesticOutReturnPaymentV2 = DomesticOutReturnPaymentRequestV2Mapper.MAPPER
        .domesticOutReturnPaymentRequestV2toDomesticOutReturnPaymentV2(domesticOutReturnPaymentRequestV2);
    domesticOutReturnFinCrimeCheckServiceV2.checkFinCrimeV2(domesticOutReturnPaymentV2);
  }

}
