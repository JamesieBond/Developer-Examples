package com.tenx.fraudamlmanager.registration.api;

import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.registration.infrastructure.CustomerRegistrationEventService;
import com.tenx.security.forgerockfacade.resource.CustomerRegistration;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CustomerRegistrationEventListener {

    @Autowired
    private CustomerRegistrationEventService customerRegistrationEventSevice;

    @KafkaListener(id = "CustomerRegistrationEventListener", containerFactory = "deadLetterQueueKafkaListener",
        topics = "${spring.kafka.consumer.kafka-identity-customer-registration-v1-topic}", idIsGroup = false)
    public void handleCustomerRegistrationEvent(
            ConsumerRecord<String, CustomerRegistration> customerRegistrationEventCR, Acknowledgment acknowledgment) throws TransactionMonitoringException {
        log.info("CustomerRegistration event received.");
        customerRegistrationEventSevice.processCustomerRegistrationEvent(customerRegistrationEventCR.value());
        acknowledgment.acknowledge();
    }
}
