package com.tenx.fraudamlmanager.deviceprofile.api;

import com.tenx.fraud.DeviceProfilingEvent;
import com.tenx.fraudamlmanager.deviceprofile.domain.DeviceProfilingService;
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
public class DeviceProfilingEventListener {

  @Autowired
  private DeviceProfilingService deviceProfilingService;

  @KafkaListener(topics = "${spring.kafka.consumer.device-profiling-event-topic}", containerFactory = "deadLetterQueueKafkaListener")
  public void handleDeviceProfilingEvent(
      ConsumerRecord<String, DeviceProfilingEvent> deviceProfilingMessage,
      Acknowledgment acknowledgment) {
    log.info(
        "Device Profiling event received for partyKey: {}",
        deviceProfilingMessage.value().getPartyKey());
    deviceProfilingService.retrieveAndSaveDeviceProfilingEventData(deviceProfilingMessage.value());
    acknowledgment.acknowledge();
  }
}
