package com.tenx.fraudamlmanager.cards.api;

import com.tenx.fraudamlmanager.cards.domain.IndividualPartyInfo;
import com.tenx.fraudamlmanager.cards.domain.IndividualPartyInfoService;
import com.tenxbanking.individual.event.IndividualEventV1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class IndividualInfoListener {

  private final IndividualPartyInfoService individualPartyInfoService;

  @KafkaListener(id = "IndividualInfoListener", groupId = "${spring.kafka.consumer.group-id}",
      containerFactory = "deadLetterQueueKafkaListener", topics = "${spring.kafka.consumer.individual-party-v2-topic}", idIsGroup = false)
  public void processIndividualPartyEvent(
      ConsumerRecord<String, IndividualEventV1> individualEventV1ConsumerRecord,
      Acknowledgment acknowledgment) {
    IndividualEventV1 individualEventV1 = individualEventV1ConsumerRecord.value();
    log.info("Individual party event received. partyKey: {}", individualEventV1.getPartyKey());
    IndividualPartyInfo individualPartyInfo = IndividualEventToIndividualPartyInfoMapper.MAPPER
        .toIndividualPartyInfo(individualEventV1);
    individualPartyInfoService.storePartyInfo(individualPartyInfo);
    acknowledgment.acknowledge();
  }

}
