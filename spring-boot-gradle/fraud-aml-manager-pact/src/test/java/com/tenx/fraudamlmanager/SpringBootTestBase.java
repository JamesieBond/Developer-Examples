package com.tenx.fraudamlmanager;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties =
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration")
public class SpringBootTestBase {

  @MockBean
  private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

}
