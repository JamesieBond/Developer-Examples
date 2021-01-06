package com.tenx.fraudamlmanager.cases.update.api;

import com.tenx.dub.casegovernor.event.v1.CaseEventV2;
import com.tenx.fraudamlmanager.cases.update.domain.CaseUpdateService;
import com.tenx.fraudamlmanager.cases.update.domain.PaymentCaseUpdate;
import com.tenx.fraudamlmanager.payments.fincrimecheckresult.domain.FinCrimeCheckResultException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CaseUpdateListener {

  private final CaseUpdateService caseUpdateServiceV2;

  @KafkaListener(topics = "${spring.kafka.consumer.casegovernor-event-v1-topic}", containerFactory = "deadLetterQueueKafkaListener",
      groupId = "fraud-group-case-v2")
  public void handleCaseUpdate(
      ConsumerRecord<String, CaseEventV2> message, Acknowledgment acknowledgment) {
    log.info("Case event received for v2/payments.");
    CaseEventV2 caseEventV2 = message.value();
    log.info("Case event case id: {}", caseEventV2.getTenxCaseId());
    PaymentCaseUpdate caseEventData = CaseUpdateMapper.MAPPER.toCaseEventData(caseEventV2);
    try {
      caseUpdateServiceV2.checkForUpdateFinCrimeCheck(caseEventData);
      acknowledgment.acknowledge();
    } catch (FinCrimeCheckResultException e) {
      log.error("Exception thrown while processing the event {} ", FinCrimeCheckResultException.class);
    }
  }

}

