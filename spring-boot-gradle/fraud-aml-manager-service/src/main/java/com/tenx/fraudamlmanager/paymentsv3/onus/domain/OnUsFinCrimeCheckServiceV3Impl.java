package com.tenx.fraudamlmanager.paymentsv3.onus.domain;

import static com.tenx.fraudamlmanager.domain.MetricsStaticStrings.DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME;

import com.tenx.fraudamlmanager.deviceprofile.domain.PaymentsDeviceProfileService;
import com.tenx.fraudamlmanager.domain.PaymentMetrics;
import com.tenx.fraudamlmanager.domain.PaymentMetricsType;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.payments.service.impl.FraudCheckResponseMetrics;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudCheckV3;
import com.tenx.fraudamlmanager.paymentsv3.onus.api.OnUsPaymentRequestV3;
import com.tenx.fraudamlmanager.paymentsv3.onus.cases.domain.OnUsCaseServiceV3;
import com.tenx.fraudamlmanager.paymentsv3.onus.infrastructure.OnUsPaymentV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Niall O'Connell
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OnUsFinCrimeCheckServiceV3Impl implements OnUsFinCrimeCheckServiceV3 {

  private static final String TRANSACTION_MONITORING_FAILED_CODE = "Failure due to Transaction Monitoring Adapter";
  private static final String FEIGN_CALLED_LOG = "Calling Transaction Monitoring Adapter for {} with ID: {}";
  private static final String TMA_RESPONSE_RECEIVED = "FraudResponse: {} returned from Transaction Monitoring Adapter for {} with ID: {}";
  private final PaymentMetrics paymentMetrics;
  private final FraudCheckResponseMetrics responseStatusMetrics;
  private final OnUsCaseServiceV3 paymentCaseService;
  private final PaymentsDeviceProfileService paymentsDeviceProfileService;
  private final OnUsTransactionMonitoringConnector onUsTransactionMonitoringConnector;

  public FraudCheckV3 checkFinCrimeV3(OnUsPaymentV3 onUsPaymentV3, String deviceKeyId)
      throws TransactionMonitoringException {
    try {

      onUsPaymentV3.setThreatmetrixData(
          paymentsDeviceProfileService
              .getThreatmetrixDataForPayment(onUsPaymentV3.getDebtorPartyKey(), deviceKeyId));
      log.info(FEIGN_CALLED_LOG, OnUsPaymentRequestV3.class.getSimpleName(),
          onUsPaymentV3.getTransactionId());
      FraudCheckV3 tmFraudResponse = onUsTransactionMonitoringConnector.checkFinCrimeV3(onUsPaymentV3);
      paymentMetrics.incrementDownStreamSuccessPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.ON_US);
      paymentCaseService.processCaseForOnUs(tmFraudResponse.getStatus(), onUsPaymentV3);
      responseStatusMetrics.incrementFraudCheck(responseStatusMetrics.ONUS_FRAUDCHECK_TAG,
          tmFraudResponse.getStatus().name().toLowerCase());
      log.info(TMA_RESPONSE_RECEIVED, tmFraudResponse.getStatus(), OnUsPaymentRequestV3.class.getSimpleName(),
          onUsPaymentV3.getTransactionId());
      return tmFraudResponse;
    } catch (TransactionMonitoringException e) {
      log.info(TRANSACTION_MONITORING_FAILED_CODE);
      paymentMetrics.incrementDownStreamFailPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.ON_US);
      throw e;
    }
  }
}
