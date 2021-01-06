package com.tenx.fraudamlmanager.paymentsv3.direct.debit.domain;

import static com.tenx.fraudamlmanager.domain.MetricsStaticStrings.DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME;

import com.tenx.fraudamlmanager.domain.PaymentMetrics;
import com.tenx.fraudamlmanager.domain.PaymentMetricsType;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.payments.service.impl.FraudCheckResponseMetrics;
import com.tenx.fraudamlmanager.paymentsv3.direct.debit.cases.domain.DirectDebitCaseServiceV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudCheckV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirectDebitFinCrimeCheckServiceV3Impl implements DirectDebitFinCrimeCheckServiceV3 {

  private final DirectDebitTransactionMonitoringConnectorV3 directDebitTransactionMonitoringConnectorV3;
  private final PaymentMetrics paymentMetrics;
  private final FraudCheckResponseMetrics responseStatusMetrics;
  private final DirectDebitCaseServiceV3 directDebitCaseServiceV3;


  private static final String TRANSACTION_MONITORING_FAILED_CODE = "Failure due to Transaction Monitoring Adapter";
  private static final String FEIGN_CALLED_LOG = "Calling Transaction Monitoring Adapter for {} with ID: {}";
  private static final String TMA_RESPONSE_RECEIVED = "FraudResponse: {} returned from Transaction Monitoring Adapter for {} with ID: {}";

  @Override
  public FraudCheckV3 checkFinCrimeV3(DirectDebitPaymentV3 directDebitPaymentV3)
      throws TransactionMonitoringException {

    try {

      log.info(FEIGN_CALLED_LOG, DirectDebitPaymentV3.class.getSimpleName(),
          directDebitPaymentV3.getTransactionId());
      FraudCheckV3 tmFraudResponse = directDebitTransactionMonitoringConnectorV3.sendFinCrimeCheck(directDebitPaymentV3);
      paymentMetrics.incrementDownStreamSuccessPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.DIRECT_DEBIT);
      directDebitCaseServiceV3.processCaseForDirectDebit(tmFraudResponse.getStatus(), directDebitPaymentV3);
      responseStatusMetrics.incrementFraudCheck(responseStatusMetrics.DIRECT_DEBIT_FRAUDCHECK_TAG,
          tmFraudResponse.getStatus().name().toLowerCase());
      log.info(TMA_RESPONSE_RECEIVED, tmFraudResponse.getStatus(), DirectDebitPaymentV3.class.getSimpleName(),
          directDebitPaymentV3.getTransactionId());
      return tmFraudResponse;
    } catch (TransactionMonitoringException e) {
      log.info(TRANSACTION_MONITORING_FAILED_CODE);
      paymentMetrics.incrementDownStreamFailPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.DIRECT_DEBIT);
      throw e;
    }

  }
}
