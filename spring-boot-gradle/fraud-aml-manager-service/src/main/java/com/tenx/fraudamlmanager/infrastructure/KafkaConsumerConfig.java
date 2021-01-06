package com.tenx.fraudamlmanager.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
@Configuration
@EnableKafka
public class KafkaConsumerConfig {

  public static final String DEAD_LETTER_QUEUE = "-dead-letter-queue";

  @Bean("deadLetterQueueKafkaListener")
  public ConcurrentKafkaListenerContainerFactory<Object, Object> kafkaListenerContainerFactory(
      ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
      ConsumerFactory<Object, Object> kafkaConsumerFactory,
      KafkaTemplate<Object, Object> template) {
    ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
    configurer.configure(factory, kafkaConsumerFactory);
    factory.setErrorHandler(new KafkaErrorHandler(dlqRecoverer(template), new FixedBackOff(1000, 2L)));
    return factory;
  }

  public DeadLetterPublishingRecoverer dlqRecoverer(KafkaTemplate<Object, Object> template) {
    return new DeadLetterPublishingRecoverer(template,
        (consumerRecord, exception) -> new TopicPartition(consumerRecord.topic() + DEAD_LETTER_QUEUE, 0)) {
      @Override
      protected void publish(ProducerRecord<Object, Object> outRecord, KafkaOperations<Object, Object> kafkaTemplate) {
        log.info("Publishing to {}", outRecord.topic());
        super.publish(outRecord, kafkaTemplate);
      }
    };
  }
}
