package com.tenx.fraudamlmanager.paymentsv2.onus.infrastructure;

import static com.tenx.fraudamlmanager.domain.MetricsStaticStrings.DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME;

import com.tenx.fraudamlmanager.domain.PaymentMetrics;
import com.tenx.fraudamlmanager.domain.PaymentMetricsType;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.paymentsv2.onus.domain.OnUsPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.onus.domain.OnUsTransactionMonitoringExceptionV2;
import com.tenx.fraudamlmanager.paymentsv2.onus.domain.OnUsTransactionMonitoringServiceV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OnUsTransactionMonitoringServiceImplV2
  implements OnUsTransactionMonitoringServiceV2 {

  private static final String TRANSACTION_MONITORING_FAILED_MESSAGE =
    "Failed to call Transaction Monitoring Adapter for %s with ID: %s";
  private static final String TRANSACTION_MONITORING_CALLED_LOG =
    "Calling Transaction Monitoring Adapter for {} with ID: {}";

  private final PaymentMetrics paymentMetrics;
  private final OnUsTransactionMonitoringClientV2 transactionMonitoringClient;

  @Override
  public void checkOnUsPaymentV2(OnUsPaymentV2 onUsPaymentV2)
    throws OnUsTransactionMonitoringExceptionV2 {

    try {
      log.info(
        TRANSACTION_MONITORING_CALLED_LOG,
        OnUsPaymentV2.class.getSimpleName(),
        onUsPaymentV2.getTransactionId());
      transactionMonitoringClient.checkFinCrimeV2(onUsPaymentV2);
      paymentMetrics.incrementDownStreamSuccessPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.ON_US);
    } catch (TransactionMonitoringException e) {
      paymentMetrics.incrementDownStreamFailPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.ON_US);
      log.error(
        TRANSACTION_MONITORING_FAILED_MESSAGE,
        OnUsPaymentV2.class.getSimpleName(),
        onUsPaymentV2.getTransactionId(), e);
      throw new OnUsTransactionMonitoringExceptionV2(
        HttpStatus.BAD_REQUEST.value() == e.getErrorDetails().getHttpStatusCode()
          ? OnUsTransactionMonitoringExceptionV2.Error.MISSING_REQUIRED_FIELDS
          : OnUsTransactionMonitoringExceptionV2.Error.GENERAL_SERVICE_ERROR,
        e.getMessage());
    }
  }
}
