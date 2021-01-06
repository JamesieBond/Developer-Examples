package com.tenx.fraudamlmanager.payments.service.impl;

import static com.tenx.fraudamlmanager.domain.MetricsStaticStrings.DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME;

import com.tenx.fraudamlmanager.domain.PaymentMetrics;
import com.tenx.fraudamlmanager.domain.PaymentMetricsType;
import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.TransactionMonitoringClient;
import com.tenx.fraudamlmanager.payments.cases.PaymentCaseServiceV1;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.payments.model.api.DirectCreditPayment;
import com.tenx.fraudamlmanager.payments.model.api.DirectDebitPayment;
import com.tenx.fraudamlmanager.payments.model.api.FpsInboundPayment;
import com.tenx.fraudamlmanager.payments.model.api.FpsOutboundPayment;
import com.tenx.fraudamlmanager.payments.model.api.FraudCheckResponse;
import com.tenx.fraudamlmanager.payments.model.api.OnUsPayment;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.DirectCreditBacsPayment;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.DirectDebitBacsPayment;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.DomesticInPayment;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.DomesticOutPayment;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.FraudAMLSanctionsCheckResponse;
import com.tenx.fraudamlmanager.payments.service.FinCrimeCheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Massimo Della Rovere
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FinCrimeCheckServiceImpl implements FinCrimeCheckService {

  private final String BLOCKED = "Blocked";
  private final String PASSED = "Passed";
  private final String REFERRED = "Referred";

  private final PaymentMetrics paymentMetrics;
  private final FraudCheckResponseMetrics responseStatusMetrics;
  private final TransactionMonitoringClient transactionMonitoringClient;
  private final PaymentMappingService paymentMappingService;
  private final PaymentCaseServiceV1 paymentCaseServiceV1;

  private static final String SUCCESSFUL_PUSH_TO_FRAUDDECISION_TOPIC = "The tmFraudResponse has been successfully published to the FraudDecision Topic";
  private static final String TRANSACTIONMONITORING_CALLED_LOG = "Calling Transaction Monitoring Adapter for {} with ID: {}";
  private static final String TRANSACTIONMONITORING_RESPONSE_RECEIVED = "FraudResponse: {} returned from Transaction Monitoring Adapter for {} with ID: {}";
  private static final String TRANSACTION_MONITORING_FAILED_MESSAGE = "Failure due to Transaction Monitoring Adapter";

  public FraudCheckResponse checkFinCrime(OnUsPayment onUsPaymentApi) throws TransactionMonitoringException {
    try {

      com.tenx.fraudamlmanager.payments.model.transactionmonitoring.OnUsPayment onUsPayment = paymentMappingService
          .mapOnUsPayment(onUsPaymentApi);
      FraudAMLSanctionsCheckResponse tmFraudResponse = transactionMonitoringClient.checkFinCrime(onUsPayment);
      responseStatusMetrics.incrementFraudCheck(responseStatusMetrics.ONUS_FRAUDCHECK_TAG, tmFraudResponse.getStatus().toLowerCase());
      log.info(TRANSACTIONMONITORING_CALLED_LOG,
          com.tenx.fraudamlmanager.payments.model.transactionmonitoring.OnUsPayment.class.getSimpleName(),
          onUsPayment.getTransactionId());
      log.info(TRANSACTIONMONITORING_RESPONSE_RECEIVED, tmFraudResponse.getStatus(),
          com.tenx.fraudamlmanager.payments.model.transactionmonitoring.OnUsPayment.class.getSimpleName(),
          onUsPaymentApi.getTransactionId());
      log.info(SUCCESSFUL_PUSH_TO_FRAUDDECISION_TOPIC);
      paymentMetrics.incrementDownStreamSuccessPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.ON_US);
      if (tmFraudResponse.getStatus().equals(REFERRED)) {
        paymentCaseServiceV1.createBlockedPaymentCase(onUsPayment);
      }
      FraudCheckResponse fraudCheckResponse = paymentMappingService.mapToOldFraudCheckResponse(tmFraudResponse);
      fraudCheckResponse.setClear(tmFraudResponse.getStatus().equals(PASSED));
      return fraudCheckResponse;
    } catch (TransactionMonitoringException e) {
      log.info(TRANSACTION_MONITORING_FAILED_MESSAGE);
      paymentMetrics.incrementDownStreamFailPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.ON_US);
      throw e;
    }
  }

  public FraudCheckResponse checkFinCrime(FpsOutboundPayment fpsOutboundPayment) throws TransactionMonitoringException {
    try {

      DomesticOutPayment domesticOutPayment = paymentMappingService.mapToFpsOutboundPayment(fpsOutboundPayment);
      FraudAMLSanctionsCheckResponse tmFraudResponse = transactionMonitoringClient.checkFinCrime(domesticOutPayment);
      responseStatusMetrics.incrementFraudCheck(responseStatusMetrics.OUTBOUND_FRAUDCHECK_TAG, tmFraudResponse.getStatus().toLowerCase());
      log.info(TRANSACTIONMONITORING_CALLED_LOG, DomesticOutPayment.class.getSimpleName(),
          domesticOutPayment.getTransactionId());
      log.info(TRANSACTIONMONITORING_RESPONSE_RECEIVED, tmFraudResponse.getStatus(),
          FpsOutboundPayment.class.getSimpleName(),
          fpsOutboundPayment.getTransactionId());
      paymentMetrics.incrementDownStreamSuccessPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.DOMESTIC_OUT);
      log.info(SUCCESSFUL_PUSH_TO_FRAUDDECISION_TOPIC);
      if (tmFraudResponse.getStatus().equals(REFERRED)) {
        paymentCaseServiceV1.createBlockedPaymentCase(domesticOutPayment);
      }

      FraudCheckResponse fraudCheckResponse = paymentMappingService.mapToOldFraudCheckResponse(tmFraudResponse);
      fraudCheckResponse.setClear(tmFraudResponse.getStatus().equals(PASSED));
      return fraudCheckResponse;
    } catch (TransactionMonitoringException e) {
      log.info(TRANSACTION_MONITORING_FAILED_MESSAGE);
      paymentMetrics.incrementDownStreamFailPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.DOMESTIC_OUT);
      throw e;
    }
  }

  public FraudCheckResponse checkFinCrime(FpsInboundPayment fpsInboundPayment) throws TransactionMonitoringException {
    try {

      DomesticInPayment domesticInPayment = paymentMappingService.mapToFpsInboundPayment(fpsInboundPayment);
      FraudAMLSanctionsCheckResponse tmFraudResponse = transactionMonitoringClient.checkFinCrime(domesticInPayment);
      responseStatusMetrics.incrementFraudCheck(responseStatusMetrics.INBOUND_FRAUDCHECK_TAG, tmFraudResponse.getStatus().toLowerCase());
      log.info(TRANSACTIONMONITORING_CALLED_LOG, FpsInboundPayment.class.getSimpleName(),
          fpsInboundPayment.getTransactionId());
      log.info(TRANSACTIONMONITORING_RESPONSE_RECEIVED, tmFraudResponse.getStatus(),
          FpsInboundPayment.class.getSimpleName(),
          fpsInboundPayment.getTransactionId());
      log.info(SUCCESSFUL_PUSH_TO_FRAUDDECISION_TOPIC);
      paymentMetrics.incrementDownStreamSuccessPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.DOMESTIC_IN);
      FraudCheckResponse fraudCheckResponse = paymentMappingService.mapToOldFraudCheckResponse(tmFraudResponse);
      fraudCheckResponse.setClear(tmFraudResponse.getStatus().equals(PASSED));

      return fraudCheckResponse;
    } catch (TransactionMonitoringException e) {
      log.info(TRANSACTION_MONITORING_FAILED_MESSAGE);
      paymentMetrics.incrementDownStreamFailPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.DOMESTIC_IN);
      throw e;
    }
  }

  public FraudCheckResponse checkFinCrime(DirectDebitPayment directDebitPayment) throws TransactionMonitoringException {
    try {

      DirectDebitBacsPayment directDebitBacsPayment = paymentMappingService.mapToDiectDebitPayment(directDebitPayment);
      FraudAMLSanctionsCheckResponse tmFraudResponse = transactionMonitoringClient.checkFinCrimeDirectDebit(directDebitBacsPayment);
      responseStatusMetrics.incrementFraudCheck(responseStatusMetrics.DIRECT_DEBIT_FRAUDCHECK_TAG,
          tmFraudResponse.getStatus().toLowerCase());
      log.info(TRANSACTIONMONITORING_CALLED_LOG, DirectDebitPayment.class.getSimpleName(),
          directDebitPayment.getId());
      log.info(TRANSACTIONMONITORING_RESPONSE_RECEIVED, tmFraudResponse.getStatus(),
          DirectDebitPayment.class.getSimpleName(),
          directDebitPayment.getId());
      log.info(SUCCESSFUL_PUSH_TO_FRAUDDECISION_TOPIC);
      paymentMetrics.incrementDownStreamSuccessPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.DIRECT_DEBIT);
      FraudCheckResponse fraudCheckResponse = paymentMappingService.mapToOldFraudCheckResponse(tmFraudResponse);
      fraudCheckResponse.setClear(tmFraudResponse.getStatus().equals(PASSED));
      return fraudCheckResponse;
    } catch (TransactionMonitoringException e) {
      log.info(TRANSACTION_MONITORING_FAILED_MESSAGE);
      paymentMetrics.incrementDownStreamFailPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.DIRECT_DEBIT);
      throw e;
    }
  }

  public FraudCheckResponse checkFinCrime(DirectCreditPayment directCreditPayment) throws TransactionMonitoringException {
    try {
      DirectCreditBacsPayment directCreditBacsPayment = paymentMappingService.mapToDirectCreditPayment(directCreditPayment);
      FraudAMLSanctionsCheckResponse tmFraudResponse = transactionMonitoringClient.checkFinCrimeDirectCredit(directCreditBacsPayment);
      responseStatusMetrics.incrementFraudCheck(responseStatusMetrics.DIRECT_CREDIT_FRAUDCHECK_TAG,
          tmFraudResponse.getStatus().toLowerCase());
      log.info(TRANSACTIONMONITORING_CALLED_LOG, DirectCreditPayment.class.getSimpleName(),
          directCreditPayment.getId());
      log.info(TRANSACTIONMONITORING_RESPONSE_RECEIVED, tmFraudResponse.getStatus(),
          DirectCreditPayment.class.getSimpleName(),
          directCreditPayment.getId());
      log.info(SUCCESSFUL_PUSH_TO_FRAUDDECISION_TOPIC);
      paymentMetrics.incrementDownStreamSuccessPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.DIRECT_CREDIT);
      FraudCheckResponse fraudCheckResponse = paymentMappingService.mapToOldFraudCheckResponse(tmFraudResponse);
      fraudCheckResponse.setClear(tmFraudResponse.getStatus().equals(PASSED));
      return fraudCheckResponse;
    } catch (TransactionMonitoringException e) {
      log.info(TRANSACTION_MONITORING_FAILED_MESSAGE);
      paymentMetrics.incrementDownStreamFailPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.DIRECT_CREDIT);
      throw e;
    }
  }
}
