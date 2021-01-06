package com.tenx.fraudamlmanager.paymentsv2.domestic.in.domain;

import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraudamlmanager.paymentsv2.cases.domain.PaymentCaseServiceV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.domain.AccountDetailsV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.domain.BalanceBeforeV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.domain.PaymentAmountV2;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class DomesticInFinCrimeCheckServiceImplV2Test {

  private DomesticInFinCrimeCheckServiceV2 domesticInFinCrimeCheckServiceV2;

  @MockBean
  private DomesticInTransactionMonitoringServiceV2 domesticInTransactionMonitoringServiceV2;

  @MockBean
  private PaymentCaseServiceV2 paymentCaseServiceV2;

  @BeforeEach
  public void beforeEach() {
    this.domesticInFinCrimeCheckServiceV2 = new DomesticInFinCrimeCheckServiceV2Impl(domesticInTransactionMonitoringServiceV2, paymentCaseServiceV2);
  }

  @Test
  public void checkDomestiInPayment() throws DomesticInTransactionMonitoringExceptionV2 {
    DomesticInPaymentV2 domesticInPaymentV2 = new DomesticInPaymentV2(
            new AccountDetailsV2("Test", "098", "Test", "zyx", "Test", "IbanTest"),
            "creditor name",
            "test",
      new AccountDetailsV2("Test", "098", "Test", "zyx", "Test", "IbanTest"),
            "debitor name",
            "test",
            new PaymentAmountV2("EUR", 10, "EUR", 10),
            "123key",
            new Date(),
            new BalanceBeforeV2("GBP", 500.00, "GBP", 500.00),
            new Date(),
            "Review",
            "Test",
            "Test",
            "Test",
            "partyKey");

    domesticInFinCrimeCheckServiceV2.checkFinCrimeV2(domesticInPaymentV2);

    Mockito.verify(domesticInTransactionMonitoringServiceV2, times(1))
            .checkDomesticInPaymentV2(domesticInPaymentV2);

    Mockito.verify(paymentCaseServiceV2, times(1))
            .createSavePaymentCase(domesticInPaymentV2);
  }

}
