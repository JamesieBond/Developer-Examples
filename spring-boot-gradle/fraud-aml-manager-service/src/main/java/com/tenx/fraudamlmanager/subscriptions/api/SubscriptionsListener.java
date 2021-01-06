package com.tenx.fraudamlmanager.subscriptions.api;

import com.tenx.dub.subscription.event.v1.SubscriptionEvent;
import com.tenx.fraudamlmanager.subscriptions.domain.SubscriptionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@EnableKafka
@Slf4j
public class SubscriptionsListener {

  @Autowired
  private SubscriptionService subscriptionService;

  @KafkaListener(containerFactory = "deadLetterQueueKafkaListener",
      topics = "${spring.kafka.consumer.subscription-event-v3-topic}", groupId = "subscriptionEventGroupId")
  public void handleSubscriptionEvent(ConsumerRecord<String, SubscriptionEvent> subscriptionMessage,
      Acknowledgment acknowledgment) {
    if (subscriptionMessage.value() == null) {
      log.warn("subscriptionMessage should not be null!");
    } else {
      log.info("Subscription event received for id: {}", subscriptionMessage.value().getSubscriptionKey());
      Subscription subscription = SubscriptionEventToSubscriptionMapper.MAPPER
          .toSubscription(subscriptionMessage.value());
      subscriptionService.saveSubscriptionEvent(subscription);
    }
    acknowledgment.acknowledge();
  }

}
