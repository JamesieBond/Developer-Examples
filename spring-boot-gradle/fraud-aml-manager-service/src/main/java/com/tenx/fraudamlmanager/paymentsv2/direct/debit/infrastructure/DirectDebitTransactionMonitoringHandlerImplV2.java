package com.tenx.fraudamlmanager.paymentsv2.direct.debit.infrastructure;

import static com.tenx.fraudamlmanager.domain.MetricsStaticStrings.DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME;

import com.tenx.fraudamlmanager.domain.PaymentMetrics;
import com.tenx.fraudamlmanager.domain.PaymentMetricsType;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.paymentsv2.direct.debit.domain.DirectDebitBacsPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.direct.debit.domain.DirectDebitTransactionMonitoringExceptionV2;
import com.tenx.fraudamlmanager.paymentsv2.direct.debit.domain.DirectDebitTransactionMonitoringHandlerV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DirectDebitTransactionMonitoringHandlerImplV2 implements
    DirectDebitTransactionMonitoringHandlerV2 {

  private static final String TRANSACTION_MONITORING_FAILED_MESSAGE =
      "Failed to call Transaction Monitoring Adapter for {} with ID: {}";
  private static final String TRANSACTION_MONITORING_CALLED_LOG =
      "Calling Transaction Monitoring Adapter for {} with ID: {}";

  private final PaymentMetrics paymentMetrics;
  private final DirectDebitTransactionMonitoringClientV2 directDebitTransactionMonitoringClientV2;

  @Override
  public void checkDirectDebitV2(DirectDebitBacsPaymentV2 directCreditBacsPaymentV2)
      throws DirectDebitTransactionMonitoringExceptionV2 {

    try {
      log.info(TRANSACTION_MONITORING_CALLED_LOG,
          DirectDebitBacsPaymentV2.class.getSimpleName(),
          directCreditBacsPaymentV2.getTransactionId());
      directDebitTransactionMonitoringClientV2.checkFinCrimeV2DirectDebit(directCreditBacsPaymentV2);
      paymentMetrics.incrementDownStreamSuccessPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.DIRECT_DEBIT);
    } catch (TransactionMonitoringException e) {
      paymentMetrics.incrementDownStreamFailPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.DIRECT_DEBIT);
      log.error(
          TRANSACTION_MONITORING_FAILED_MESSAGE,
          DirectDebitBacsPaymentV2.class.getSimpleName(),
          directCreditBacsPaymentV2.getTransactionId());
      throw new DirectDebitTransactionMonitoringExceptionV2(
          HttpStatus.BAD_REQUEST.value() == e.getErrorDetails().getHttpStatusCode()
              ? DirectDebitTransactionMonitoringExceptionV2.Error.MISSING_REQUIRED_FIELDS
              : DirectDebitTransactionMonitoringExceptionV2.Error.GENERAL_SERVICE_ERROR,
          e.getMessage(), e);
    }
  }
}
