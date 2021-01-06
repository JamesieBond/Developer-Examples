package com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.infrastructure.transactionmanager;


import static com.tenx.fraudamlmanager.domain.MetricsStaticStrings.DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME;

import com.tenx.fraudamlmanager.domain.PaymentMetrics;
import com.tenx.fraudamlmanager.infrastructure.transactionmanager.TransactionManagerClient;
import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.exceptions.TransactionManagerException;
import com.tenx.fraudamlmanager.payments.service.impl.FraudCheckResponseMetrics;
import com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.domain.FinCrimeCheckResultV2;
import com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.domain.TransactionManagerConnector;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionManagerConnectorImplV2 implements TransactionManagerConnector {

  private static final String TRANSACTION_MANAGER_CLIENT_ERROR = "Transaction Manager client error";

  private static final String TRANSACTION_MANAGER_CLIENT_ERROR_WITH_TRANSACTION_ID = "Transaction Manager client error, transactionId: {}";

  private static final String FIN_CRIME_CHECK_RESULT = "FinCrime Check Result transaction ID: {}, for Status: {}";

  private static final String FIN_CRIME_CHECK_SUCCESS = "FinCrime Check Result successfully processed for Id: {}";

  private final TransactionManagerClient transactionManagerClient;

  private final PaymentMetrics paymentMetrics;
  private final FraudCheckResponseMetrics responseStatusMetrics;

  @Override
  public void notifyTransactionManager(FinCrimeCheckResultV2 finCrimeCheckResultV2)
      throws TransactionManagerException {
    try {
      log.info(
          FIN_CRIME_CHECK_RESULT,
          finCrimeCheckResultV2.getTransactionId(),
          finCrimeCheckResultV2.getStatus());
      transactionManagerClient.postFraudAMLSanctionsNotification(
          FinCrimeCheckResultToFinCrimeCheckMapperV2.MAPPER.toFinCrimeCheckTM(
              finCrimeCheckResultV2));
      log.info(FIN_CRIME_CHECK_SUCCESS, finCrimeCheckResultV2.getTransactionId());
      responseStatusMetrics.incrementFraudCheck(responseStatusMetrics.TM_PAYMENT_TAG,
          finCrimeCheckResultV2.getStatus().name().toLowerCase());
      paymentMetrics
          .incrementDownStreamSuccessFincrimeCheckResult(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME);
    } catch (TransactionManagerException e) {
      paymentMetrics.incrementDownStreamSuccessFincrimeCheckResult(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME);
      if (e.getErrorDetails().getHttpStatusCode() != 406) {
        throw new TransactionManagerException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
            TRANSACTION_MANAGER_CLIENT_ERROR, e);
      }
    } catch (FeignException e) {
      paymentMetrics.incrementDownStreamFailFincrimeCheckResult(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME);
      log.info(TRANSACTION_MANAGER_CLIENT_ERROR_WITH_TRANSACTION_ID, finCrimeCheckResultV2.getTransactionId());
      throw new TransactionManagerException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
          TRANSACTION_MANAGER_CLIENT_ERROR, e);
    }
  }
}
