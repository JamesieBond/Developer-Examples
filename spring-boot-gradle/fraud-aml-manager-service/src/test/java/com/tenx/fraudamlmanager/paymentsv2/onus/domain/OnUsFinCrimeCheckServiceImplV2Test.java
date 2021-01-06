package com.tenx.fraudamlmanager.paymentsv2.onus.domain;

import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraudamlmanager.paymentsv2.cases.domain.PaymentCaseServiceV2;
import com.tenx.fraudamlmanager.paymentsv2.domain.PaymentDeviceProfileServiceV2;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class OnUsFinCrimeCheckServiceImplV2Test {

  private OnUsFinCrimeCheckServiceV2 onUsFinCrimeCheckServiceV2;

  @MockBean
  private OnUsTransactionMonitoringServiceV2 onUsTransactionMonitoringServiceV2;

  @MockBean
  private PaymentDeviceProfileServiceV2 paymentDeviceProfileServiceV2;

  @MockBean
  private PaymentCaseServiceV2 paymentCaseServiceV2;

  @BeforeEach
  public void beforeEach() {
    this.onUsFinCrimeCheckServiceV2 = new OnUsFinCrimeCheckServiceV2Impl(
      onUsTransactionMonitoringServiceV2, paymentCaseServiceV2);
  }

  @Test
  public void checkOnUsPayment() throws OnUsTransactionMonitoringExceptionV2 {
    OnUsPaymentV2 onUsPaymentV2 = new OnUsPaymentV2(
      new AccountDetailsOnUsV2("Test", "098"),
      "creditor name",
      new AccountDetailsOnUsV2("Test", "098"),
      "debtor name",
      new PaymentAmountOnUsV2("EUR", 10, "EUR", 10),
      new BalanceBeforeOnUsV2("GBP", 500.00, "GBP", 500.00),
      "1234",
      "5678",
      "123key",
      new Date(), new Date(),
      "Review", "Test", "Test",
      new ArrayList<String>(), true, new HashMap<>());

    onUsFinCrimeCheckServiceV2.checkFinCrimeV2(onUsPaymentV2);

    Mockito.verify(onUsTransactionMonitoringServiceV2, times(1))
      .checkOnUsPaymentV2(onUsPaymentV2);
  }

}
