package com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.infrastructure;

import static com.tenx.fraudamlmanager.domain.MetricsStaticStrings.DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME;

import com.tenx.fraudamlmanager.domain.PaymentMetrics;
import com.tenx.fraudamlmanager.domain.PaymentMetricsType;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain.DomesticOutReturnPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain.DomesticOutReturnTransactionMonitoringExceptionV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain.DomesticOutReturnTransactionMonitoringServiceV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DomesticOutReturnTransactionMonitoringServiceImplV2
    implements DomesticOutReturnTransactionMonitoringServiceV2 {

  private static final String TRANSACTION_MONITORING_FAILED_MESSAGE =
      "Failed to call Transaction Monitoring Adapter for %s with ID: %s";
  private static final String TRANSACTION_MONITORING_CALLED_LOG =
      "Calling Transaction Monitoring Adapter for {} with ID: {}";

  private final PaymentMetrics paymentMetrics;
  private final DomesticOutReturnTransactionMonitoringClientV2 domesticOutReturnTransactionMonitoringClientV2;

  @Override
  public void checkDomesticOutReturnPaymentV2(DomesticOutReturnPaymentV2 domesticOutReturnPaymentV2)
      throws DomesticOutReturnTransactionMonitoringExceptionV2 {

    try {
      log.info(
          TRANSACTION_MONITORING_CALLED_LOG,
          DomesticOutReturnPaymentV2.class.getSimpleName(),
          domesticOutReturnPaymentV2.getTransactionId());
      domesticOutReturnTransactionMonitoringClientV2.postReturnPayment(domesticOutReturnPaymentV2);
      paymentMetrics.incrementDownStreamSuccessPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.DOMESTIC_OUT);
    } catch (TransactionMonitoringException e) {
      paymentMetrics.incrementDownStreamFailPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.DOMESTIC_OUT);
      log.error(
          TRANSACTION_MONITORING_FAILED_MESSAGE,
          DomesticOutReturnPaymentV2.class.getSimpleName(),
          domesticOutReturnPaymentV2.getTransactionId(), e);
      throw new DomesticOutReturnTransactionMonitoringExceptionV2(
          HttpStatus.BAD_REQUEST.value() == e.getErrorDetails().getHttpStatusCode()
              ? DomesticOutReturnTransactionMonitoringExceptionV2.Error.MISSING_REQUIRED_FIELDS
              : DomesticOutReturnTransactionMonitoringExceptionV2.Error.GENERAL_SERVICE_ERROR,
          e.getMessage());
    }
  }
}
