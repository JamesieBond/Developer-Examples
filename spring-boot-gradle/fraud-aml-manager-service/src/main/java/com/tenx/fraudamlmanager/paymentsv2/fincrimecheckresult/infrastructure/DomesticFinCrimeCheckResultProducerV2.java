package com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.infrastructure;

import com.tenx.fraud.payments.fps.FPSFraudCheckResponse;
import com.tenx.fraudamlmanager.domain.PaymentMetrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DomesticFinCrimeCheckResultProducerV2 {

  private final KafkaTemplate<String, FPSFraudCheckResponse> kafkaProducerTemplate;

  private final PaymentMetrics paymentMetrics;

  @Value("${spring.kafka.producer.fps-fraud-check-response-event-topic}")
  private String fpsResponseTopic;

  public void publishDomesticFinCrimeCheckResult(FPSFraudCheckResponse fpsFraudCheckResponse) {
    kafkaProducerTemplate.send(fpsResponseTopic, fpsFraudCheckResponse);
    paymentMetrics.incrementCounter(PaymentMetrics.PAYMENTS_FPS_FRAUDCHECK_PUBLISHED);
    log.info("Published DomesticFinCrimeCheckResult event. transaction id: {}, status: {}",
        fpsFraudCheckResponse.getTransactionId(), fpsFraudCheckResponse.getStatus());
  }

}
