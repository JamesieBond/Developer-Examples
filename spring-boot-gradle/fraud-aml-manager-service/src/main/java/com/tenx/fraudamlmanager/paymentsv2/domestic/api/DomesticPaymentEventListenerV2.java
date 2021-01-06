package com.tenx.fraudamlmanager.paymentsv2.domestic.api;

import com.tenx.fraud.payments.fpsin.FPSInTransaction;
import com.tenx.fraud.payments.fpsin.FPSInboundPaymentFraudCheck;
import com.tenx.fraud.payments.fpsout.FPSOutTransaction;
import com.tenx.fraud.payments.fpsout.FPSOutboundPaymentFraudCheck;
import com.tenx.fraud.payments.onus.FPSOutReturnTransaction;
import com.tenx.fraud.payments.onus.FPSOutboundReturnPaymentFraudCheck;
import com.tenx.fraud.payments.onus.ONUSPaymentFraudCheck;
import com.tenx.fraud.payments.onus.ONUSTransaction;
import com.tenx.fraudamlmanager.paymentsv2.domestic.in.domain.DomesticInFinCrimeCheckServiceV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.in.domain.DomesticInTransactionMonitoringExceptionV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.in.domain.EventDomesticInPaymentMapperV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.out.api.EventDomesticOutPaymentMapperV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.out.domain.DomesticOutFinCrimeCheckServiceV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.out.domain.DomesticOutTransactionMonitoringExceptionV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.api.EventDomesticOutReturnPaymentMapperV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain.DomesticOutReturnFinCrimeCheckServiceV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain.DomesticOutReturnTransactionMonitoringExceptionV2;
import com.tenx.fraudamlmanager.paymentsv2.onus.api.ONUSPaymentFraudCheckMapper;
import com.tenx.fraudamlmanager.paymentsv2.onus.domain.OnUsFinCrimeCheckServiceV2;
import com.tenx.fraudamlmanager.paymentsv2.onus.domain.OnUsPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.onus.domain.OnUsTransactionMonitoringExceptionV2;
import java.text.ParseException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
@KafkaListener(id = "domesticPaymentEventV2", containerFactory = "deadLetterQueueKafkaListener",
    topics = "${spring.kafka.consumer.fps-fraud-check-request-event-topic}", idIsGroup = false)
public class DomesticPaymentEventListenerV2 {

  private static final String NO_TRANSACTION_PRESENT = "No transaction present";

  private final DomesticOutReturnFinCrimeCheckServiceV2 domesticOutReturnFinCrimeCheckServiceV2;

  private final DomesticOutFinCrimeCheckServiceV2 domesticOutFinCrimeCheckServiceV2;

  private final DomesticInFinCrimeCheckServiceV2 domesticInFinCrimeCheckServiceV2;

  private final OnUsFinCrimeCheckServiceV2 onUsFinCrimeCheckServiceV2;

  @KafkaHandler
  public void handleInboundPaymentEvent(FPSInboundPaymentFraudCheck fpsInboundPaymentFraudCheck,
      Acknowledgment acknowledgment) throws DomesticInTransactionMonitoringExceptionV2, ParseException {

    FPSInTransaction fpsInTransaction = fpsInboundPaymentFraudCheck.getTransaction();
    log.info("Fraud inbound payment kafka event received, transactionId: {}",
        fpsInTransaction == null ? NO_TRANSACTION_PRESENT : fpsInTransaction.getId());
    domesticInFinCrimeCheckServiceV2
        .checkFinCrimeV2(EventDomesticInPaymentMapperV2.MAPPER.toDomesticInPaymentV2(fpsInboundPaymentFraudCheck));

    acknowledgment.acknowledge();
  }

  @KafkaHandler
  public void handleOutboundPaymentEvent(FPSOutboundPaymentFraudCheck fpsOutboundPaymentFraudCheck,
      Acknowledgment acknowledgment) throws DomesticOutTransactionMonitoringExceptionV2, ParseException {

    FPSOutTransaction fpsOutTransaction = fpsOutboundPaymentFraudCheck.getTransaction();
    log.info("Fraud outbound payment kafka event received, transactionId: {}",
        fpsOutTransaction == null ? NO_TRANSACTION_PRESENT : fpsOutTransaction.getId());
    domesticOutFinCrimeCheckServiceV2
        .checkFinCrimeV2(EventDomesticOutPaymentMapperV2.MAPPER.toDomesticOutPaymentV2(fpsOutboundPaymentFraudCheck),
            "");

    acknowledgment.acknowledge();
  }

  @KafkaHandler
  public void handleOutboundReturnPaymentEvent(FPSOutboundReturnPaymentFraudCheck outboundReturnPaymentFraudCheck,
      Acknowledgment acknowledgment)
      throws DomesticOutReturnTransactionMonitoringExceptionV2, ParseException {

    FPSOutReturnTransaction fpsOutReturnTransaction = outboundReturnPaymentFraudCheck.getTransaction();
    log.info("Fraud outbound return payment kafka event received, transactionId: {}",
        fpsOutReturnTransaction == null ? NO_TRANSACTION_PRESENT : fpsOutReturnTransaction.getId());
    domesticOutReturnFinCrimeCheckServiceV2.checkFinCrimeV2(
        EventDomesticOutReturnPaymentMapperV2.MAPPER.toDomesticOutReturnPaymentV2(outboundReturnPaymentFraudCheck));
    acknowledgment.acknowledge();
  }

  @KafkaHandler
  public void handleOnUsPayments(ONUSPaymentFraudCheck onusPaymentFraudCheck, Acknowledgment acknowledgment)
      throws OnUsTransactionMonitoringExceptionV2, ParseException {
    ONUSTransaction onusTransaction = onusPaymentFraudCheck.getTransaction();
    log.info("Fraud OnUs payment kafka event received, DebtorTransactionId:{}",
        onusTransaction == null ? NO_TRANSACTION_PRESENT : onusTransaction.getDebtorTransactionId());

    OnUsPaymentV2 onUsPaymentV2 = ONUSPaymentFraudCheckMapper.MAPPER.toOnUsPayment(onusPaymentFraudCheck);
    onUsFinCrimeCheckServiceV2.checkFinCrimeV2(onUsPaymentV2);
    acknowledgment.acknowledge();
  }

}
