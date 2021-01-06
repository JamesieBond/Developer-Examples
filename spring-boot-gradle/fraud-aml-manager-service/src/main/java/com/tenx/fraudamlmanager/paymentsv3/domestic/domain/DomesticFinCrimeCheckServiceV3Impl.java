package com.tenx.fraudamlmanager.paymentsv3.domestic.domain;

import static com.tenx.fraudamlmanager.domain.MetricsStaticStrings.DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME;

import com.tenx.fraudamlmanager.deviceprofile.domain.PaymentsDeviceProfileService;
import com.tenx.fraudamlmanager.domain.PaymentMetrics;
import com.tenx.fraudamlmanager.domain.PaymentMetricsType;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.payments.service.impl.FraudCheckResponseMetrics;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudCheckV3;
import com.tenx.fraudamlmanager.paymentsv3.domestic.cases.domain.DomesticCaseServiceV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Niall O'Connell
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DomesticFinCrimeCheckServiceV3Impl implements DomesticFinCrimeCheckServiceV3 {

  private final PaymentMetrics paymentMetrics;
  private final FraudCheckResponseMetrics responseStatusMetrics;
  private final DomesticCaseServiceV3 domesticCaseServiceV3;
  private final PaymentsDeviceProfileService paymentsDeviceProfileService;
  private final domesticTransactionMonitoringConnector domesticTransactionMonitoringConnector;

  private static final String TRANSACTION_MONITORING_FAILED_CODE = "Failure due to Transaction Monitoring Adapter";
  private static final String FEIGN_CALLED_LOG = "Calling Transaction Monitoring Adapter for {} with ID: {}";
  private static final String TMA_RESPONSE_RECEIVED = "FraudResponse: {} returned from Transaction Monitoring Adapter for {} with ID: {}";

  public FraudCheckV3 checkFinCrimeV3(DomesticOutPaymentV3 domesticOutPaymentV3, String deviceKeyId)
      throws TransactionMonitoringException {
    try {

      domesticOutPaymentV3.setThreatmetrixData(
          paymentsDeviceProfileService
              .getThreatmetrixDataForPayment(domesticOutPaymentV3.getDebtorPartyKey(), deviceKeyId));
      log.info(FEIGN_CALLED_LOG, DomesticOutPaymentV3.class.getSimpleName(),
          domesticOutPaymentV3.getTransactionId());
      FraudCheckV3 tmFraudResponse = domesticTransactionMonitoringConnector.sendFinCrimeCheck(domesticOutPaymentV3);
      paymentMetrics.incrementDownStreamSuccessPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.DOMESTIC_OUT);
      domesticCaseServiceV3.processCaseForDomesticOut(tmFraudResponse.getStatus(), domesticOutPaymentV3);
      responseStatusMetrics.incrementFraudCheck(responseStatusMetrics.OUTBOUND_FRAUDCHECK_TAG,
          tmFraudResponse.getStatus().name().toLowerCase());
      log.info(TMA_RESPONSE_RECEIVED, tmFraudResponse.getStatus(), DomesticOutPaymentV3.class.getSimpleName(),
          domesticOutPaymentV3.getTransactionId());
      return tmFraudResponse;
    } catch (TransactionMonitoringException e) {
      log.info(TRANSACTION_MONITORING_FAILED_CODE);
      paymentMetrics.incrementDownStreamFailPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.DOMESTIC_OUT);
      throw e;
    }
  }


  public FraudCheckV3 checkFinCrimeV3(DomesticOutReturnPaymentV3 domesticOutReturnPaymentV3)
      throws TransactionMonitoringException {
    try {

      log.info(FEIGN_CALLED_LOG, DomesticOutReturnPaymentV3.class.getSimpleName(),
          domesticOutReturnPaymentV3.getTransactionId());
      FraudCheckV3 tmFraudResponse = domesticTransactionMonitoringConnector.sendFinCrimeCheck(domesticOutReturnPaymentV3);
      paymentMetrics.incrementDownStreamSuccessPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.DOMESTIC_OUT);
      domesticCaseServiceV3.processCaseForDomesticOutReturn(tmFraudResponse.getStatus(), domesticOutReturnPaymentV3);
      responseStatusMetrics.incrementFraudCheck(responseStatusMetrics.OUTBOUND_RETURN_FRAUDCHECK_TAG,
          tmFraudResponse.getStatus().name().toLowerCase());
      log.info(TMA_RESPONSE_RECEIVED, tmFraudResponse.getStatus(),
          DomesticOutReturnPaymentV3.class.getSimpleName(),
          domesticOutReturnPaymentV3.getTransactionId());
      return tmFraudResponse;
    } catch (TransactionMonitoringException e) {
      log.info(TRANSACTION_MONITORING_FAILED_CODE);
      paymentMetrics.incrementDownStreamFailPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.DOMESTIC_OUT);
      throw e;
    }
  }

  public FraudCheckV3 checkFinCrimeV3(DomesticInPaymentV3 domesticInPaymentV3)
      throws TransactionMonitoringException {
    try {

      log.info(FEIGN_CALLED_LOG, DomesticInPaymentV3.class.getSimpleName(),
          domesticInPaymentV3.getTransactionId());
      FraudCheckV3 tmFraudResponse = domesticTransactionMonitoringConnector.sendFinCrimeCheck(domesticInPaymentV3);
      paymentMetrics.incrementDownStreamSuccessPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.DOMESTIC_IN);
      domesticCaseServiceV3.processCaseForDomesticIn(tmFraudResponse.getStatus(), domesticInPaymentV3);
      responseStatusMetrics.incrementFraudCheck(responseStatusMetrics.INBOUND_FRAUDCHECK_TAG,
          tmFraudResponse.getStatus().name().toLowerCase());
      log.info(TMA_RESPONSE_RECEIVED, tmFraudResponse.getStatus(), DomesticInPaymentV3.class.getSimpleName(),
          domesticInPaymentV3.getTransactionId());
      return tmFraudResponse;
    } catch (TransactionMonitoringException e) {
      log.info(TRANSACTION_MONITORING_FAILED_CODE);
      paymentMetrics.incrementDownStreamFailPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.DOMESTIC_IN);
      throw e;
    }
  }
}
