package com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.infrastructure;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.tenx.fraud.payments.fps.FPSFraudCheckResponse;
import com.tenx.fraudamlmanager.cases.domain.PaymentCaseException;
import com.tenx.fraudamlmanager.cases.v2.domain.PaymentCaseDataStore;
import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.exceptions.TransactionManagerException;
import com.tenx.fraudamlmanager.payments.service.impl.FraudCheckResponseMetrics;
import com.tenx.fraudamlmanager.paymentsv2.domestic.out.domain.DomesticOutPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.domain.DomesticFinCrimeCheckResultNotificationService;
import com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.domain.FinCrimeCheckResultNotificationException;
import com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.domain.FinCrimeCheckResultResponseCodeV2;
import com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.domain.FinCrimeCheckResultV2;
import com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.infrastructure.transactionmanager.TransactionManagerConnectorImplV2;
import com.tenx.fraudamlmanager.paymentsv2.onus.domain.OnUsPaymentV2;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class DomesticFinCrimeCheckResultNotificationServiceImplTest {

  private DomesticFinCrimeCheckResultNotificationService domesticFinCrimeCheckResultNotificationService;

  @MockBean
  private DomesticFinCrimeCheckResultProducerV2 domesticFinCrimeCheckResultProducerV2;

  @MockBean
  private PaymentCaseDataStore paymentCaseDataStore;

  @MockBean
  private TransactionManagerConnectorImplV2 transactionManagerConnectorImplV2;

  @MockBean
  private FraudCheckResponseMetrics fraudCheckMetric;

  @BeforeEach
  private void initTest() {
    domesticFinCrimeCheckResultNotificationService = new DomesticFinCrimeCheckResultNotificationServiceImpl(
        domesticFinCrimeCheckResultProducerV2, paymentCaseDataStore, transactionManagerConnectorImplV2, fraudCheckMetric);
  }

  @Test
  public void testNotifyDomesticFinCrimeCheckResult()
      throws FinCrimeCheckResultNotificationException, PaymentCaseException, TransactionManagerException {
    FinCrimeCheckResultV2 finCrimeCheckResultV2 = new FinCrimeCheckResultV2(
        "txnId", FinCrimeCheckResultResponseCodeV2.PASSED
    );

    FPSFraudCheckResponse fpsFraudCheckResponse = FPSFraudCheckResponse
        .newBuilder().setPaymentType(DomesticOutPaymentV2.class.getSimpleName())
        .setStatus(finCrimeCheckResultV2.getStatus().name())
        .setTransactionId(finCrimeCheckResultV2.getTransactionId()).build();

    given(
        paymentCaseDataStore
            .findPaymentTypeByTransactionId(finCrimeCheckResultV2.getTransactionId()))
        .willReturn(Optional.of(DomesticOutPaymentV2.class.getSimpleName()));
    domesticFinCrimeCheckResultNotificationService
        .notifyDomesticFinCrimeCheckResult(finCrimeCheckResultV2);
    verify(domesticFinCrimeCheckResultProducerV2, times(1))
        .publishDomesticFinCrimeCheckResult(fpsFraudCheckResponse);
    verify(transactionManagerConnectorImplV2, times(0))
        .notifyTransactionManager(any());
    Mockito.verify(fraudCheckMetric, VerificationModeFactory.times(1)).incrementFraudCheck(
        fraudCheckMetric.mapPaymentType(DomesticOutPaymentV2.class.getSimpleName()), "passed");

  }

  @Test
  public void testNotifyDomesticFinCrimeCheckResultWithOnUsPaymentV2()
      throws FinCrimeCheckResultNotificationException, PaymentCaseException, TransactionManagerException {
    FinCrimeCheckResultV2 finCrimeCheckResultV2 = new FinCrimeCheckResultV2(
        "txnId", FinCrimeCheckResultResponseCodeV2.PASSED
    );

    FPSFraudCheckResponse fpsFraudCheckResponse = FPSFraudCheckResponse
        .newBuilder().setPaymentType(OnUsPaymentV2.class.getSimpleName())
        .setStatus(finCrimeCheckResultV2.getStatus().name())
        .setTransactionId(finCrimeCheckResultV2.getTransactionId()).build();

    given(
        paymentCaseDataStore
            .findPaymentTypeByTransactionId(finCrimeCheckResultV2.getTransactionId()))
        .willReturn(Optional.of(OnUsPaymentV2.class.getSimpleName()));
    domesticFinCrimeCheckResultNotificationService
        .notifyDomesticFinCrimeCheckResult(finCrimeCheckResultV2);
    verify(domesticFinCrimeCheckResultProducerV2, times(1))
        .publishDomesticFinCrimeCheckResult(fpsFraudCheckResponse);
    verify(transactionManagerConnectorImplV2, times(0))
        .notifyTransactionManager(any());

  }

  @Test
  public void testNotifyDomesticFinCrimeCheckResultGivenPaymentCaseIsNotFound()
      throws PaymentCaseException {
    FinCrimeCheckResultV2 finCrimeCheckResultV2 = new FinCrimeCheckResultV2(
        "txnId", FinCrimeCheckResultResponseCodeV2.PASSED
    );

    given(
        paymentCaseDataStore
            .findPaymentTypeByTransactionId(finCrimeCheckResultV2.getTransactionId()))
        .willReturn(Optional.ofNullable(null));
    assertThrows(
        FinCrimeCheckResultNotificationException.class,
        () ->
            domesticFinCrimeCheckResultNotificationService
                .notifyDomesticFinCrimeCheckResult(finCrimeCheckResultV2));
    verify(domesticFinCrimeCheckResultProducerV2, times(0))
        .publishDomesticFinCrimeCheckResult(any());

  }

  @Test
  public void testNotifyDomesticFinCrimeCheckResultGivenPaymentTypeIsNotDomestic()
      throws PaymentCaseException, FinCrimeCheckResultNotificationException, TransactionManagerException {
    FinCrimeCheckResultV2 finCrimeCheckResultV2 = new FinCrimeCheckResultV2(
        "txnId", FinCrimeCheckResultResponseCodeV2.PASSED
    );

    given(
        paymentCaseDataStore
            .findPaymentTypeByTransactionId(finCrimeCheckResultV2.getTransactionId()))
        .willReturn(Optional.of("RandomPaymentType"));
    domesticFinCrimeCheckResultNotificationService.notifyDomesticFinCrimeCheckResult(finCrimeCheckResultV2);
    verify(domesticFinCrimeCheckResultProducerV2, times(0))
        .publishDomesticFinCrimeCheckResult(any());
    verify(transactionManagerConnectorImplV2, times(1))
        .notifyTransactionManager(any());

  }
}
