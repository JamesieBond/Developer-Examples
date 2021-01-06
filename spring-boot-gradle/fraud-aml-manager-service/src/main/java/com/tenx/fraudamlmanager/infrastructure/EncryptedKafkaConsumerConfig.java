package com.tenx.fraudamlmanager.infrastructure;

import static io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG;
import static io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig.VALUE_SUBJECT_NAME_STRATEGY;
import static io.confluent.kafka.serializers.KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG;
import static org.apache.kafka.clients.CommonClientConfigs.RETRY_BACKOFF_MS_CONFIG;
import static org.apache.kafka.clients.CommonClientConfigs.SECURITY_PROTOCOL_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG;
import static org.apache.kafka.common.config.SaslConfigs.SASL_JAAS_CONFIG;
import static org.apache.kafka.common.config.SaslConfigs.SASL_MECHANISM;
import static org.apache.kafka.common.config.SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG;

import com.google.common.collect.ImmutableMap;
import com.tenx.kafka.EnableKafkaSecurity;
import com.tenx.kafka.config.TenxKafkaSecurityProperties;
import com.tenx.kafka.serialization.SecuredKafkaSerDesFactory;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.subject.TopicRecordNameStrategy;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
@Configuration
@EnableKafka
@EnableKafkaSecurity
public class EncryptedKafkaConsumerConfig {

  public static final String DEAD_LETTER_QUEUE = "-dead-letter-queue";

  private static final String SASL_JAAS_CONFIG_TEMPLATE = "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s\" password=\"%s\";";

  private final Map<String, Object> kafkaProperties;

  @Value("${spring.kafka.consumer.group-id}")
  private String groupId;

  @Autowired
  private TenxKafkaSecurityProperties kafkaSecurityProperties;

  @Autowired
  private SecuredKafkaSerDesFactory kafkaSerDesFactory;

  public EncryptedKafkaConsumerConfig(
      @Value("${spring.kafka.ssl.endpoint.identification.algorithm}") String sslAlgorithm,
      @Value("${spring.kafka.sasl.mechanism}") String saslMechanism,
      @Value("${spring.kafka.sasl.api.key}") String saslApiKey,
      @Value("${spring.kafka.sasl.api.secret}") String saslApiSecret,
      @Value("${spring.kafka.request.timeout.ms}") String requestTimeoutMs,
      @Value("${spring.kafka.retry.backoff.ms}") String retryBackoffMs,
      @Value("${spring.kafka.security.protocol}") String securityProtocol,
      @Value("${spring.kafka.properties.schema.registry.url}") String schemaRegistryUrl,
      @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers,
      @Value("${spring.kafka.consumer.group-id}") String groupId) {

    kafkaProperties = new ImmutableMap.Builder<String, Object>()
        .put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
        .put(REQUEST_TIMEOUT_MS_CONFIG, requestTimeoutMs)
        .put(SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, sslAlgorithm)
        .put(SASL_MECHANISM, saslMechanism)
        .put(SASL_JAAS_CONFIG, String.format(SASL_JAAS_CONFIG_TEMPLATE, saslApiKey, saslApiSecret))
        .put(SECURITY_PROTOCOL_CONFIG, securityProtocol)
        .put(RETRY_BACKOFF_MS_CONFIG, retryBackoffMs)
        .put(SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl)
        .put(VALUE_SUBJECT_NAME_STRATEGY, TopicRecordNameStrategy.class.getName())
        .build();
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

  @Bean("encryptedContainerFactory")
  public ConcurrentKafkaListenerContainerFactory<Object, Object> kafkaListenerContainerFactoryEncrypted(
      ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
      KafkaTemplate<Object, Object> template) {
    ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
    configurer.configure(factory, new DefaultKafkaConsumerFactory<>(decryptingConsumerProps(),
        new KafkaAvroDeserializer(),
        kafkaSerDesFactory.createSpecificRecordSecuredKafkaAvroDeserializer(decryptingConsumerProps())));
    factory.setErrorHandler(new KafkaErrorHandler(dlqRecoverer(template), new FixedBackOff(1000, 2L)));
    return factory;
  }

  private Map<String, Object> consumerProperties() {
    return new ImmutableMap.Builder<String, Object>()
        .put(SPECIFIC_AVRO_READER_CONFIG, true)
        .put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class)
        .put(VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class)
        .putAll(kafkaProperties)
        .build();
  }

  private Map<String, Object> decryptingConsumerProps() {
    return new ImmutableMap.Builder<String, Object>()
        .putAll(consumerProperties())
        .putAll(kafkaSecurityProperties.awsKmsEncryptionProperties())
        .build();
  }

}
