package com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.infrastructure;

import com.tenx.fraud.payments.fps.FPSFraudCheckResponse;
import com.tenx.fraudamlmanager.cases.domain.PaymentCaseException;
import com.tenx.fraudamlmanager.cases.v2.domain.PaymentCaseDataStore;
import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.exceptions.TransactionManagerException;
import com.tenx.fraudamlmanager.payments.service.impl.FraudCheckResponseMetrics;
import com.tenx.fraudamlmanager.paymentsv2.domestic.in.domain.DomesticInPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.out.domain.DomesticOutPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain.DomesticOutReturnPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.domain.DomesticFinCrimeCheckResultNotificationService;
import com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.domain.FinCrimeCheckResultNotificationException;
import com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.domain.FinCrimeCheckResultV2;
import com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.infrastructure.transactionmanager.TransactionManagerConnectorImplV2;
import com.tenx.fraudamlmanager.paymentsv2.onus.domain.OnUsPaymentV2;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DomesticFinCrimeCheckResultNotificationServiceImpl
    implements DomesticFinCrimeCheckResultNotificationService {

  private final DomesticFinCrimeCheckResultProducerV2 domesticFinCrimeCheckResultProducerV2;
  private final PaymentCaseDataStore paymentCaseDataStore;
  private final TransactionManagerConnectorImplV2 transactionManagerConnectorImplV2;
  private final FraudCheckResponseMetrics responseStatusMetrics;

  public void notifyDomesticFinCrimeCheckResult(FinCrimeCheckResultV2 finCrimeCheckResult)
      throws FinCrimeCheckResultNotificationException, PaymentCaseException, TransactionManagerException {
    log.info(
        "Publish DomesticFinCrimeCheckResult event. transaction id: {}, status: {}",
        finCrimeCheckResult.getTransactionId(),
        finCrimeCheckResult.getStatus());
    Optional<String> paymentType =
        paymentCaseDataStore.findPaymentTypeByTransactionId(finCrimeCheckResult.getTransactionId());
    if (paymentType.isPresent()) {
      publishDomesticFinCrimeCheckResponse(
          finCrimeCheckResult, paymentType.get());
      responseStatusMetrics.incrementFraudCheck(responseStatusMetrics.mapPaymentType(paymentType.get()),
          finCrimeCheckResult.getStatus().name().toLowerCase());
    } else {
      handlePaymentTypeNotFound(finCrimeCheckResult);
    }
  }

  private void publishDomesticFinCrimeCheckResponse(FinCrimeCheckResultV2 finCrimeCheckResultV2, String paymentType)
      throws FinCrimeCheckResultNotificationException, TransactionManagerException {
    if (isDomesticPaymentType(paymentType)) {
      FPSFraudCheckResponse fpsFraudCheckResponse =
          buildFPSFraudCheckResponse(finCrimeCheckResultV2, paymentType);
      domesticFinCrimeCheckResultProducerV2.publishDomesticFinCrimeCheckResult(
          fpsFraudCheckResponse);
    } else {
      transactionManagerConnectorImplV2.notifyTransactionManager(finCrimeCheckResultV2);
    }
  }

  private boolean isDomesticPaymentType(String paymentType) {
    return DomesticInPaymentV2.class.getSimpleName().equals(paymentType)
        || DomesticOutPaymentV2.class.getSimpleName().equals(paymentType)
        || DomesticOutReturnPaymentV2.class.getSimpleName().equals(paymentType)
        || OnUsPaymentV2.class.getSimpleName().equals(paymentType);
  }

  private FPSFraudCheckResponse buildFPSFraudCheckResponse(
      FinCrimeCheckResultV2 finCrimeCheckResultV2, String paymentType) {
    return FPSFraudCheckResponse.newBuilder()
        .setPaymentType(paymentType)
        .setStatus(finCrimeCheckResultV2.getStatus().name())
        .setTransactionId(finCrimeCheckResultV2.getTransactionId())
        .build();
  }

  private void handlePaymentTypeNotFound(FinCrimeCheckResultV2 finCrimeCheckResultV2)
      throws FinCrimeCheckResultNotificationException {
    throw new FinCrimeCheckResultNotificationException(
        "Failed publishing DomesticFinCrimeCheckResult because payment type is not found. transaction id: "
            + finCrimeCheckResultV2.getTransactionId());
  }
}
