package com.tenx.fraudamlmanager.payments.core.credittransfer.api;

import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.CreditTransferTransactionException;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.CreditTransferTransactionService;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.Pacs008;
import com.tenx.fraudamlmanager.payments.core.credittransfer.infrastructure.CreditTransferPublishException;
import com.tenxbanking.iso.lib.IsoCreditTransferFraudCheckRequestV01;
import java.util.Collection;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentRequestFraudCheckListener {

  private final CreditTransferTransactionService creditTransferTransactionService;

  @KafkaListener(id = "PaymentRequestFraudCheckListener", containerFactory = "deadLetterQueueKafkaListener",
      topics = "${spring.kafka.consumer.payments-core-credit-transfer-fraud-check-request-topic}", idIsGroup = false)
  public void processPaymentRequestEvent(
      ConsumerRecord<String, IsoCreditTransferFraudCheckRequestV01> paymentRequestFraudCheckEventV1CR,
      Acknowledgment acknowledgment) throws CreditTransferTransactionException, CreditTransferPublishException {

    IsoCreditTransferFraudCheckRequestV01 isoCreditTransferFraudCheckRequestV01 = paymentRequestFraudCheckEventV1CR
        .value();
    validatePartyKeyPresent(isoCreditTransferFraudCheckRequestV01);
    log.info("Payment request fraud check event received with key: {}", getTransactionTraceIdentification(isoCreditTransferFraudCheckRequestV01));

    Pacs008 pacs008 = Pacs008Mapper.MAPPER.toPacs008(isoCreditTransferFraudCheckRequestV01);

    creditTransferTransactionService.creditTransferFinCrimeCheck(pacs008);

    acknowledgment.acknowledge();
  }

  private String getTransactionTraceIdentification(IsoCreditTransferFraudCheckRequestV01 isoCreditTransferFraudCheckRequestV01)
      throws CreditTransferTransactionException {
    return isoCreditTransferFraudCheckRequestV01.getCreditTransferFraudCheckRequest()
        .getCreditTransferTransactionInformation().stream()
        .map(com.tenxbanking.iso.lib.CreditTransferTransaction39::getSupplementaryData)
        .filter(Objects::nonNull)
        .flatMap(Collection::stream)
        .map(com.tenxbanking.iso.lib.SupplementaryData::getEnvelope)
        .filter(Objects::nonNull)
        .map(com.tenxbanking.iso.lib.SupplementaryDataEnvelope::getTransactionTraceIdentification)
        .findFirst().orElseThrow(
        () -> new CreditTransferTransactionException(CreditTransferTransactionException.Error.MISSING_REQUIRED_FIELDS,
            "Missing transactionTraceIdentification field."));
  }

  private void validatePartyKeyPresent(IsoCreditTransferFraudCheckRequestV01 isoCreditTransferFraudCheckRequestV01)
      throws CreditTransferTransactionException {
    isoCreditTransferFraudCheckRequestV01.getCreditTransferFraudCheckRequest()
        .getCreditTransferTransactionInformation().stream()
        .map(com.tenxbanking.iso.lib.CreditTransferTransaction39::getSupplementaryData)
        .filter(Objects::nonNull)
        .flatMap(Collection::stream)
        .map(com.tenxbanking.iso.lib.SupplementaryData::getEnvelope)
        .filter(Objects::nonNull)
        .map(com.tenxbanking.iso.lib.SupplementaryDataEnvelope::getPartyKey)
        .findFirst().orElseThrow(
        () -> new CreditTransferTransactionException(CreditTransferTransactionException.Error.MISSING_REQUIRED_FIELDS,
            "Missing partyKey field."));
  }
}
