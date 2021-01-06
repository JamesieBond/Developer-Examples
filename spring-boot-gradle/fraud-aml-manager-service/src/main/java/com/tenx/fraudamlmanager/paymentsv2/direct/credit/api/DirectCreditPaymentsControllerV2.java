package com.tenx.fraudamlmanager.paymentsv2.direct.credit.api;

import com.tenx.fraudamlmanager.domain.PaymentMetrics;
import com.tenx.fraudamlmanager.domain.PaymentMetricsType;
import com.tenx.fraudamlmanager.paymentsv2.direct.credit.domain.DirectCreditBacsPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.direct.credit.domain.DirectCreditFinCrimeCheckServiceV2;
import com.tenx.fraudamlmanager.paymentsv2.direct.credit.domain.DirectCreditTransactionMonitoringExceptionV2;
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
public class DirectCreditPaymentsControllerV2 {

  @Autowired
  private DirectCreditFinCrimeCheckServiceV2 directCreditFinCrimeCheckServiceV2;

  @Autowired
  private PaymentMetrics paymentMetrics;

  @ApiOperation(value = "Financial Crime check for DirectCredit payments")
  @PostMapping("/directCreditFinCrimeCheck")
  public void checkDirectCredit(@RequestBody @Valid DirectCreditBacsPaymentRequestV2 directCreditBacsPaymentRequestV2)
      throws DirectCreditTransactionMonitoringExceptionV2 {
    log.info("checkDirectCreditV2 request received with ID: {}", directCreditBacsPaymentRequestV2.getTransactionId());

    paymentMetrics.incrementCounterTag(PaymentMetrics.PAYMENTS_RECEIVED, PaymentMetricsType.DIRECT_CREDIT.toString());

    DirectCreditBacsPaymentV2 directCreditBacsPaymentV2 = ApiDomainDirectCreditPaymentMapperV2.MAPPER
        .toDirectCreditPayment(directCreditBacsPaymentRequestV2);

    directCreditFinCrimeCheckServiceV2.checkFinCrimeDirectCreditV2(directCreditBacsPaymentV2);
  }

}
