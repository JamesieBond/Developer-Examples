package com.tenx.fraudamlmanager.infrastructure;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.util.backoff.BackOff;

@Slf4j
public class KafkaErrorHandler extends SeekToCurrentErrorHandler {

  public KafkaErrorHandler(DeadLetterPublishingRecoverer dlqRecoverer, BackOff backOff) {
    super(dlqRecoverer, backOff);
  }

  @Override
  public void handle(Exception thrownException, List<ConsumerRecord<?, ?>> records, Consumer<?, ?> consumer,
      MessageListenerContainer container) {
    super.handle(thrownException, records, consumer, container);
    log.error("Error during processing, retrying 2 times and then send the message to dead letter queue: ",
        thrownException.getCause());

  }

}
