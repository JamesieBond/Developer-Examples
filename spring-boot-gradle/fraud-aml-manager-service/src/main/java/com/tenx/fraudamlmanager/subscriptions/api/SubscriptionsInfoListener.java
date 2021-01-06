package com.tenx.fraudamlmanager.subscriptions.api;

import com.tenx.dub.subscription.event.v1.SubscriptionEvent;
import com.tenx.fraudamlmanager.subscriptions.domain.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionsInfoListener {

  private final SubscriptionService subscriptionService;

  @KafkaListener(containerFactory = "deadLetterQueueKafkaListener",
      topics = "${spring.kafka.consumer.subscription-event-v3-topic}", groupId = "subscription-store-info")
  public void handleSubscriptionEvent(ConsumerRecord<String, SubscriptionEvent> subscriptionMessage,
      Acknowledgment acknowledgment) {
    if(subscriptionMessage.value() == null)
    {
      log.warn("subscriptionMessage should not be null!");
    }
    else {
      try {
        log.info("Subscription info event received for id: {}", subscriptionMessage.value().getSubscriptionKey());
        Subscription subscription = SubscriptionEventToSubscriptionMapper.MAPPER
            .toSubscription(subscriptionMessage.value());
        subscriptionService.saveSubscriptionInfo(subscription);
      } catch (SubscriptionException e) {
        log.error("Exception thrown while processing the event {} ", e.getMessage());
      }
    }
    acknowledgment.acknowledge();

  }
}
