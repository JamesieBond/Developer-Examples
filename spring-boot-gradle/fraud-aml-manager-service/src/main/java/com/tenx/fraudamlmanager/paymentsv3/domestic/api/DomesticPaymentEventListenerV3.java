package com.tenx.fraudamlmanager.paymentsv3.domestic.api;

import com.tenx.fraud.payments.fpsin.FPSInTransaction;
import com.tenx.fraud.payments.fpsin.FPSInboundPaymentFraudCheck;
import com.tenx.fraud.payments.fpsout.FPSOutTransaction;
import com.tenx.fraud.payments.fpsout.FPSOutboundPaymentFraudCheck;
import com.tenx.fraud.payments.onus.FPSOutReturnTransaction;
import com.tenx.fraud.payments.onus.FPSOutboundReturnPaymentFraudCheck;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.paymentsv3.domestic.domain.DomesticEventService;
import java.text.ParseException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
//@KafkaListener(id = "domesticPaymentEventV3", topics = "${spring.kafka.consumer.fps-fraud-check-request-event-topic}", idIsGroup = false)
//This Kafka listener was disabled during the implementation of DEV-129522
public class DomesticPaymentEventListenerV3 {

    @Autowired
    private DomesticEventService domesticEventService;

    @KafkaHandler
    public void handleInboundPaymentEvent(FPSInboundPaymentFraudCheck fpsInboundPaymentFraudCheck,
        Acknowledgment acknowledgment) throws TransactionMonitoringException, ParseException {

        FPSInTransaction fpsInTransaction = fpsInboundPaymentFraudCheck.getTransaction();
        log.info("Fraud inbound payment kafka event received, transactionId: {}",
            fpsInTransaction == null ? "No transaction present" : fpsInTransaction.getId());
        domesticEventService.produceEventForFinCrime(
            EventDomainDomesticPaymentMapper.MAPPER.toDomesticInPaymentV3(fpsInboundPaymentFraudCheck));

        acknowledgment.acknowledge();
    }

    @KafkaHandler
    public void handleOutboundPaymentEvent(FPSOutboundPaymentFraudCheck fpsOutboundPaymentFraudCheck, Acknowledgment acknowledgment) throws TransactionMonitoringException, ParseException {

        FPSOutTransaction fpsOutTransaction = fpsOutboundPaymentFraudCheck.getTransaction();
        log.info("Fraud outbound payment kafka event received, transactionId: {}",
            fpsOutTransaction == null ? "No transaction present" : fpsOutTransaction.getId());
        domesticEventService.produceEventForFinCrime(
            EventDomainDomesticPaymentMapper.MAPPER.toDomesticOutPaymentV3(fpsOutboundPaymentFraudCheck), "");

        acknowledgment.acknowledge();
    }

    @KafkaHandler
    public void handleOutboundReturnPaymentEvent(FPSOutboundReturnPaymentFraudCheck outboundReturnPaymentFraudCheck, Acknowledgment acknowledgment) throws TransactionMonitoringException, ParseException {

        FPSOutReturnTransaction fpsOutReturnTransaction = outboundReturnPaymentFraudCheck.getTransaction();
        log.info("Fraud outbound return payment kafka event received, transactionId: {}",
            fpsOutReturnTransaction == null ? "No transaction present" : fpsOutReturnTransaction.getId());
        domesticEventService.produceEventForFinCrime(
            EventDomainDomesticPaymentMapper.MAPPER.toDomesticOutReturnPaymentV3(outboundReturnPaymentFraudCheck));
        acknowledgment.acknowledge();
    }

}
