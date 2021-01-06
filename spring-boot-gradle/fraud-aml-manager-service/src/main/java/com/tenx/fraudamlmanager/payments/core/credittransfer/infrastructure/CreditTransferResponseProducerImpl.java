package com.tenx.fraudamlmanager.payments.core.credittransfer.infrastructure;

import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.Pacs002;
import com.tenxbanking.iso.lib.IsoFraudCheckResponseV01;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CreditTransferResponseProducerImpl implements CreditTransferResponseProducer{

  @Value("${spring.kafka.producer.payments-core-fraud-check-response-topic}")
  private String paymentsCoreFraudCheckResponseTopic;

  private final KafkaTemplate<String, IsoFraudCheckResponseV01> kafkaProducerTemplate;


  public void publishFraudCheckResponse(Pacs002 pacs002) throws CreditTransferPublishException{
    IsoFraudCheckResponseV01 isoFraudCheckResponseV01 = IsoFraudCheckResponseV01Mapper.MAPPER.toIsoFraudCheckResponseV01(pacs002);
    try{
      kafkaProducerTemplate.send(paymentsCoreFraudCheckResponseTopic, isoFraudCheckResponseV01);
    } catch (KafkaException ex) {
      throw new CreditTransferPublishException(
          "Failed to produce event for message ID: " + isoFraudCheckResponseV01.getApplicationHeader().getMessageDefinitionIdentifier(), ex);
    }
  }
}
