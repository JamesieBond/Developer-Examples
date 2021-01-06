package com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.infrastructure;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.tenx.fraud.payments.fps.FPSFraudCheckResponse;
import com.tenx.fraudamlmanager.domain.PaymentMetrics;
import com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.domain.FinCrimeCheckResultResponseCodeV2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(SpringExtension.class)
public class DomesticFinCrimeCheckResultProducerV2Test {

  private DomesticFinCrimeCheckResultProducerV2 domesticFinCrimeCheckResultProducerV2;

  @MockBean
  private KafkaTemplate<String, FPSFraudCheckResponse> kafkaProducerTemplate;

  @MockBean
  private PaymentMetrics paymentMetrics;

  @BeforeEach
  private void initTest() {
    domesticFinCrimeCheckResultProducerV2 = new DomesticFinCrimeCheckResultProducerV2(
        kafkaProducerTemplate, paymentMetrics);
  }

  @Test
  public void testPublishDomesticFinCrimeCheckResult() {
    FPSFraudCheckResponse fpsFraudCheckResponse =
        FPSFraudCheckResponse.newBuilder()
            .setPaymentType("paymentType")
            .setStatus(FinCrimeCheckResultResponseCodeV2.PASSED.name())
            .setTransactionId("txnId")
            .build();

    ReflectionTestUtils
        .setField(domesticFinCrimeCheckResultProducerV2, "fpsResponseTopic", "randomTopic");
    domesticFinCrimeCheckResultProducerV2.publishDomesticFinCrimeCheckResult(fpsFraudCheckResponse);
    verify(kafkaProducerTemplate, times(1)).send("randomTopic", fpsFraudCheckResponse);
    verify(paymentMetrics, times(1)).incrementCounter(PaymentMetrics.PAYMENTS_FPS_FRAUDCHECK_PUBLISHED);
  }

}
