package com.tenx.fraudamlmanager.paymentsv2.onus.infrastructure;

import static com.tenx.fraudamlmanager.domain.MetricsStaticStrings.DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraudamlmanager.domain.PaymentMetrics;
import com.tenx.fraudamlmanager.domain.PaymentMetricsType;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.paymentsv2.onus.domain.AccountDetailsOnUsV2;
import com.tenx.fraudamlmanager.paymentsv2.onus.domain.BalanceBeforeOnUsV2;
import com.tenx.fraudamlmanager.paymentsv2.onus.domain.OnUsPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.onus.domain.OnUsTransactionMonitoringExceptionV2;
import com.tenx.fraudamlmanager.paymentsv2.onus.domain.OnUsTransactionMonitoringServiceV2;
import com.tenx.fraudamlmanager.paymentsv2.onus.domain.PaymentAmountOnUsV2;
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
public class OnUsTransactionMonitoringServiceImplV2Test {


  private OnUsTransactionMonitoringServiceV2 onUsTransactionMonitoringServiceV2;

  @MockBean
  private PaymentMetrics paymentMetrics;

  @MockBean
  private OnUsTransactionMonitoringClientV2 onUsTransactionMonitoringClientV2;

  @BeforeEach
  public void beforeEach() {
    this.onUsTransactionMonitoringServiceV2 = new OnUsTransactionMonitoringServiceImplV2(
      paymentMetrics, onUsTransactionMonitoringClientV2);
  }

  @Test
  public void testCheckOnUsPaymentV2()
    throws TransactionMonitoringException, OnUsTransactionMonitoringExceptionV2 {
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

    onUsTransactionMonitoringServiceV2.checkOnUsPaymentV2(onUsPaymentV2);
    Mockito.verify(onUsTransactionMonitoringClientV2, times(1))
      .checkFinCrimeV2(onUsPaymentV2);
    Mockito.verify(paymentMetrics, times(1)).incrementDownStreamSuccessPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.ON_US);

  }
}
