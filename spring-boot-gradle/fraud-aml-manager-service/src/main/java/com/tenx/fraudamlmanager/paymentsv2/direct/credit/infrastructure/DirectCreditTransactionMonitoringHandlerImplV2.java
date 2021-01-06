package com.tenx.fraudamlmanager.paymentsv2.direct.credit.infrastructure;

import static com.tenx.fraudamlmanager.domain.MetricsStaticStrings.DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME;

import com.tenx.fraudamlmanager.domain.PaymentMetrics;
import com.tenx.fraudamlmanager.domain.PaymentMetricsType;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.paymentsv2.direct.credit.domain.DirectCreditBacsPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.direct.credit.domain.DirectCreditTransactionMonitoringExceptionV2;
import com.tenx.fraudamlmanager.paymentsv2.direct.credit.domain.DirectCreditTransactionMonitoringHandlerV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DirectCreditTransactionMonitoringHandlerImplV2 implements
    DirectCreditTransactionMonitoringHandlerV2 {

  private static final String TRANSACTION_MONITORING_FAILED_MESSAGE =
      "Failed to call Transaction Monitoring Adapter for {} with ID: {}";
  private static final String TRANSACTION_MONITORING_CALLED_LOG =
      "Calling Transaction Monitoring Adapter for {} with ID: {}";

  private final PaymentMetrics paymentMetrics;
  private final DirectCreditTransactionMonitoringClientV2 directCreditTransactionMonitoringClientV2;

  @Override
  public void checkDirectCreditV2(DirectCreditBacsPaymentV2 directCreditBacsPaymentV2)
      throws DirectCreditTransactionMonitoringExceptionV2 {

    try {
      log.info(TRANSACTION_MONITORING_CALLED_LOG,
          DirectCreditBacsPaymentV2.class.getSimpleName(),
          directCreditBacsPaymentV2.getTransactionId());
      directCreditTransactionMonitoringClientV2.checkFinCrimeV2DirectCredit(directCreditBacsPaymentV2);
      paymentMetrics.incrementDownStreamSuccessPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.DIRECT_CREDIT);
    } catch (TransactionMonitoringException e) {
      paymentMetrics.incrementDownStreamFailPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.DIRECT_CREDIT);
      log.error(
          TRANSACTION_MONITORING_FAILED_MESSAGE,
          DirectCreditBacsPaymentV2.class.getSimpleName(),
          directCreditBacsPaymentV2.getTransactionId());
      throw new DirectCreditTransactionMonitoringExceptionV2(
          HttpStatus.BAD_REQUEST.value() == e.getErrorDetails().getHttpStatusCode()
              ? DirectCreditTransactionMonitoringExceptionV2.Error.MISSING_REQUIRED_FIELDS
              : DirectCreditTransactionMonitoringExceptionV2.Error.GENERAL_SERVICE_ERROR,
          e.getMessage(), e);
    }
  }
}
