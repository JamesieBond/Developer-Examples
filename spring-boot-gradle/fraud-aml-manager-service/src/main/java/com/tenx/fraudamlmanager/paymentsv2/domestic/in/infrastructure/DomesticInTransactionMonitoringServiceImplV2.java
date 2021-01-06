package com.tenx.fraudamlmanager.paymentsv2.domestic.in.infrastructure;

import static com.tenx.fraudamlmanager.domain.MetricsStaticStrings.DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME;

import com.tenx.fraudamlmanager.domain.PaymentMetrics;
import com.tenx.fraudamlmanager.domain.PaymentMetricsType;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.paymentsv2.domestic.in.domain.DomesticInPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.in.domain.DomesticInTransactionMonitoringExceptionV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.in.domain.DomesticInTransactionMonitoringServiceV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DomesticInTransactionMonitoringServiceImplV2
  implements DomesticInTransactionMonitoringServiceV2 {

  private static final String TRANSACTION_MONITORING_FAILED_MESSAGE =
    "Failed to call Transaction Monitoring Adapter for %s with ID: %s";
  private static final String TRANSACTION_MONITORING_CALLED_LOG =
    "Calling Transaction Monitoring Adapter for {} with ID: {}";

  private final PaymentMetrics paymentMetrics;
  private final DomesticInTransactionMonitoringClientV2 transactionMonitoringClient;

  @Override
  public void checkDomesticInPaymentV2(DomesticInPaymentV2 domesticInPaymentV2)
    throws DomesticInTransactionMonitoringExceptionV2 {

    try {
      log.info(
        TRANSACTION_MONITORING_CALLED_LOG,
        DomesticInPaymentV2.class.getSimpleName(),
        domesticInPaymentV2.getTransactionId());
      transactionMonitoringClient.checkFinCrimeV2(domesticInPaymentV2);
      paymentMetrics.incrementDownStreamSuccessPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.DOMESTIC_IN);
    } catch (TransactionMonitoringException e) {
      paymentMetrics.incrementDownStreamFailPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.DOMESTIC_IN);
      log.error(
        TRANSACTION_MONITORING_FAILED_MESSAGE,
        DomesticInPaymentV2.class.getSimpleName(),
        domesticInPaymentV2.getTransactionId(), e);
      throw new DomesticInTransactionMonitoringExceptionV2(
        HttpStatus.BAD_REQUEST.value() == e.getErrorDetails().getHttpStatusCode()
          ? DomesticInTransactionMonitoringExceptionV2.Error.MISSING_REQUIRED_FIELDS
          : DomesticInTransactionMonitoringExceptionV2.Error.GENERAL_SERVICE_ERROR,
        e.getMessage());
    }
  }
}
