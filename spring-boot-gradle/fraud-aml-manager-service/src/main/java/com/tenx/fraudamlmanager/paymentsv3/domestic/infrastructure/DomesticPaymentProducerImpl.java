package com.tenx.fraudamlmanager.paymentsv3.domestic.infrastructure;

import com.tenx.fraud.payments.fps.FPSFraudCheckResponse;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudCheckV3;
import com.tenx.fraudamlmanager.paymentsv3.domestic.domain.DomesticPaymentProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DomesticPaymentProducerImpl implements DomesticPaymentProducer {

    private final KafkaTemplate<String, FPSFraudCheckResponse> kafkaProducerTemplate;

    @Value("${spring.kafka.producer.fps-fraud-check-response-event-topic}")
    private String fpsResponseTopic;

    @Override
    public void publishDomesticInResponseEvent(FraudCheckV3 fraudCheckV3, String transactionId) {
        log.info("send Domestic inbound message, transaction id: {}, status: {}", transactionId, fraudCheckV3.getStatus());
        kafkaProducerTemplate.send(fpsResponseTopic, transactionId,
                DomesticEventMapper.MAPPER.toFPSInboundResponse(fraudCheckV3, transactionId));
    }

    @Override
    public void publishDomesticOutResponseEvent(FraudCheckV3 fraudCheckV3, String transactionId) {
        log.info("send Domestic outbound message, transaction id: {}, status: {}", transactionId, fraudCheckV3.getStatus());
        kafkaProducerTemplate.send(fpsResponseTopic, transactionId,
                DomesticEventMapper.MAPPER.toFPSOutboundResponse(fraudCheckV3, transactionId));
    }

    @Override
    public void publishDomesticOutReturnResponseEvent(FraudCheckV3 fraudCheckV3, String transactionId) {
        log.info("send Domestic Outbound return message. transaction id: {}, status: {}", transactionId, fraudCheckV3.getStatus());
        kafkaProducerTemplate.send(fpsResponseTopic, transactionId,
                DomesticEventMapper.MAPPER.toFPSOutboundReturnResponse(fraudCheckV3, transactionId));
    }
}
