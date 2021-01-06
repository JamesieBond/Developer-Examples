package com.tenx.fraudamlmanager;


import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(controlledShutdown = true, partitions = 1)
public abstract class KafkaTestBase {

  protected final String CONSUMER_GROUP_ID = "consumer-group-test";
  protected final String CLIENT_PREFIX = "test";
  
  @Autowired
  protected KafkaProperties kafkaProperties;

  @Autowired
  protected EmbeddedKafkaBroker embeddedKafkaBroker;

  @Autowired
  protected KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

  protected Map<String, Object> producerProps;

  protected Map<String, Object> consumerProps;

  static {
    System.setProperty(EmbeddedKafkaBroker.BROKER_LIST_PROPERTY,
        "spring.kafka.bootstrap-servers");
  }

  @BeforeEach
  public void initKafkaTest() {

    producerProps = kafkaProperties.buildProducerProperties();

    //consumers used in test code needs to be created like this in code because otherwise it won't work
    consumerProps = new HashMap<>(KafkaTestUtils.consumerProps("in-test-consumer", "false", embeddedKafkaBroker));
    consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
        org.apache.kafka.common.serialization.StringDeserializer.class);
    consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, CustomKafkaAvroDeserializer.class);
    consumerProps.put("specific.avro.reader", true);
    consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
    consumerProps.put("schema.registry.url", "mock://test-url");

    kafkaProperties.buildConsumerProperties();

  }

  public abstract void initTest();

  public abstract void resetTest();

  public static String eventId;

}
