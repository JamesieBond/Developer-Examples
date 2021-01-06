package com.tenx.fraudamlmanager.paymentsv3.direct.credit.domain;

import static com.tenx.fraudamlmanager.domain.MetricsStaticStrings.DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME;

import com.tenx.fraudamlmanager.domain.PaymentMetrics;
import com.tenx.fraudamlmanager.domain.PaymentMetricsType;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.payments.service.impl.FraudCheckResponseMetrics;
import com.tenx.fraudamlmanager.paymentsv3.direct.credit.cases.domain.DirectCreditCaseServiceV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudCheckV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Niall O'Connell
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DirectCreditFinCrimeCheckServiceV3Impl implements DirectCreditFinCrimeCheckServiceV3 {

  private static final String TRANSACTION_MONITORING_FAILED_CODE = "Failure due to Transaction Monitoring Adapter";
  private static final String FEIGN_CALLED_LOG = "Calling Transaction Monitoring Adapter for {} with ID: {}";
  private static final String TMA_RESPONSE_RECEIVED = "FraudResponse: {} returned from Transaction Monitoring Adapter for {} with ID: {}";
  private final DirectCreditTransactionMonitoringConnectorV3 directCreditTransactionMonitoringConnectorV3;
  private final PaymentMetrics paymentMetrics;
  private final FraudCheckResponseMetrics responseStatusMetrics;
  private final DirectCreditCaseServiceV3 directCreditCaseServiceV3;

  public FraudCheckV3 checkFinCrimeV3(DirectCreditPaymentV3 directCreditPaymentV3)
      throws TransactionMonitoringException {
    try {

      log.info(FEIGN_CALLED_LOG, DirectCreditPaymentV3.class.getSimpleName(),
          directCreditPaymentV3.getTransactionId());
      FraudCheckV3 tmFraudResponse = directCreditTransactionMonitoringConnectorV3.sendFinCrimeCheck(directCreditPaymentV3);
      paymentMetrics.incrementDownStreamSuccessPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.DIRECT_CREDIT);
      directCreditCaseServiceV3.processPaymentForCase(tmFraudResponse.getStatus(), directCreditPaymentV3);
      responseStatusMetrics.incrementFraudCheck(responseStatusMetrics.DIRECT_CREDIT_FRAUDCHECK_TAG,
          tmFraudResponse.getStatus().name().toLowerCase());
      log.info(TMA_RESPONSE_RECEIVED, tmFraudResponse.getStatus(), DirectCreditPaymentV3.class.getSimpleName(),
          directCreditPaymentV3.getTransactionId());
      return tmFraudResponse;
    } catch (TransactionMonitoringException e) {
      log.info(TRANSACTION_MONITORING_FAILED_CODE);
      paymentMetrics.incrementDownStreamFailPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.DIRECT_CREDIT);
      throw e;
    }
  }
}
