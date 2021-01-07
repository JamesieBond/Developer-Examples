package com.tenxbanking.messageoutbox.publisher;


import static com.tenxbanking.messageoutbox.entity.EventType.AVRO;
import static com.tenxbanking.messageoutbox.entity.MessageOutboxStatus.PENDING;
import static com.tenxbanking.messageoutbox.entity.MessageOutboxStatus.SENT;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenxbanking.messageoutbox.avro.AvroSerializer;
import com.tenxbanking.messageoutbox.entity.MessageOutboxEntity;
import com.tenxbanking.messageoutbox.entity.MessageOutboxStatus;
import com.tenxbanking.messageoutbox.repository.MessageOutboxJpaRepository;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessageOutboxPublisher {

  private final MessageOutboxProperties outboxProperties;
  private final MessageOutboxJpaRepository outboxCockroachRepository;
  private final KafkaTemplate<Object, Object> template;
  private final KafkaTemplate<Object, Object> jsonTemplate;
  private final AvroSerializer avroSerializer;
  private final ObjectMapper mapper;
  private final ExecutorService executorService;

  @Autowired
  public MessageOutboxPublisher(
      MessageOutboxProperties outboxProperties,
      MessageOutboxJpaRepository outboxCockroachRepository,
      KafkaTemplate<Object, Object> outboxAvroTemplate,
      KafkaTemplate<Object, Object> outboxJsonTemplate,
      AvroSerializer avroSerializer,
      ObjectMapper mapper) {
    this.outboxProperties = outboxProperties;
    this.outboxCockroachRepository = outboxCockroachRepository;
    this.template = outboxAvroTemplate;
    this.jsonTemplate = outboxJsonTemplate;
    this.avroSerializer = avroSerializer;
    this.mapper = mapper;
    this.executorService = Executors.newFixedThreadPool(outboxProperties.getPublishingThreads());
  }

  public void publishAll() {

    log.debug("Running outbox publishing schedule");

    List<MessageOutboxEntity> allMessages = outboxCockroachRepository.findPendingMessages(outboxProperties.getBatchSize());

    if (allMessages.size() > 0) {
      log.info("Found {} outbox messages to publish ", allMessages.size());
    }

    Collection<? extends Callable<Void>> runnables = allMessages
        .stream()
        .collect(Collectors.groupingBy(MessageOutboxEntity::getTopic))
        .entrySet()
        .stream()
        .map(messagesByTopic ->
            messagesByTopic
                .getValue()
                .stream()
                .collect(Collectors.groupingBy(MessageOutboxEntity::getMessageKey))
                .entrySet()
                .stream()
                .map(topicMessagesByKey -> tryPublishMessages(messagesByTopic.getKey(), topicMessagesByKey.getKey(), topicMessagesByKey.getValue()))
                .collect(Collectors.toList()))
        .flatMap(Collection::stream)
        .collect(Collectors.toList());

    try {
      executorService.invokeAll(runnables);
    } catch (InterruptedException e) {
      log.error("Interrupted Exception while waiting for unfinished message outbox publishing threads");
      throw new RuntimeException(e);
    }

  }

  private Callable<Void> tryPublishMessages(String topic, UUID key, List<MessageOutboxEntity> topicMessagesForKey) {
    return () -> {

      log.info("Publishing {} messages with key {} on topic {}", topicMessagesForKey.size(), key, topic);

      AtomicBoolean failedDeliveryForMessageKey = new AtomicBoolean(false);

      topicMessagesForKey.forEach(message -> {

        if (!failedDeliveryForMessageKey.get()) {
          try {
            publish(message);
            updateEntity(message, SENT);
          } catch (Exception e) {
            log.error("Exception publishing message with key {} to topic {}. Publishing will be halted for messages with this key on this topic", message.getMessageKey(), message.getTopic(), e);
            failedDeliveryForMessageKey.set(true);
          }
        }
      });
      return null;
    };
  }

  private void publish(MessageOutboxEntity message) throws ExecutionException, InterruptedException, JsonProcessingException, ClassNotFoundException {
    SendResult<Object, Object> result;
    if (message.getEventType() == AVRO) {
      result = template.send(message.getTopic(), message.getMessageKey().toString(), avroSerializer.fromJson(message.getMessage(), message.getClassName())).get();
    } else {
      result = jsonTemplate.send(message.getTopic(), message.getMessageKey().toString(), mapper.readValue(message.getMessage(), Class.forName(message.getClassName()))).get();
    }
    log.info("Published message with key {} to topic {}. With {}", message.getMessageKey(), message.getTopic(), result.getRecordMetadata());
  }

  private void updateEntity(MessageOutboxEntity message, MessageOutboxStatus status) {
    MessageOutboxEntity updatedEntity = message
        .toBuilder()
        .status(status)
        .updatedTimestamp(Instant.now())
        .build();

    log.info("Saving updated message outbox entity with id {}, key {} and status {}", updatedEntity.getId(), updatedEntity.getMessageKey(), updatedEntity.getStatus());
    outboxCockroachRepository.save(updatedEntity);
  }

}
