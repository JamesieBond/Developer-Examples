package com.tenx.fraudamlmanager.paymentsv2.domestic.in.infrastructure;

import static com.tenx.fraudamlmanager.domain.MetricsStaticStrings.DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraudamlmanager.domain.PaymentMetrics;
import com.tenx.fraudamlmanager.domain.PaymentMetricsType;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.paymentsv2.domestic.domain.AccountDetailsV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.domain.BalanceBeforeV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.domain.PaymentAmountV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.in.domain.DomesticInPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.in.domain.DomesticInTransactionMonitoringExceptionV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.in.domain.DomesticInTransactionMonitoringServiceV2;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class DomesticInTransactionMonitoringServiceImplV2Test {

  private DomesticInTransactionMonitoringServiceV2 domesticInTransactionMonitoringServiceV2;

  @MockBean
  private PaymentMetrics paymentMetrics;
  @MockBean
  private DomesticInTransactionMonitoringClientV2 transactionMonitoringClient;

  @BeforeEach
  public void beforeEach() {
    this.domesticInTransactionMonitoringServiceV2 = new DomesticInTransactionMonitoringServiceImplV2(
        paymentMetrics, transactionMonitoringClient);
  }

  @Test
  public void testCheckDomesticInPaymentV2()
      throws TransactionMonitoringException, DomesticInTransactionMonitoringExceptionV2 {
    DomesticInPaymentV2 domesticInPaymentV2 = createDomesticInPaymentV2();
    domesticInTransactionMonitoringServiceV2.checkDomesticInPaymentV2(domesticInPaymentV2);
    Mockito.verify(transactionMonitoringClient, times(1)).checkFinCrimeV2(domesticInPaymentV2);
    Mockito.verify(paymentMetrics, times(1)).incrementDownStreamSuccessPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.DOMESTIC_IN);

  }

  @Test
  public void testCheckDomesticInPaymentV2GivenTMClientBadRequestError()
      throws TransactionMonitoringException {
    DomesticInPaymentV2 domesticInPaymentV2 = createDomesticInPaymentV2();
    doThrow(new TransactionMonitoringException(HttpStatus.BAD_REQUEST.value(), "test"))
        .when(transactionMonitoringClient).checkFinCrimeV2(domesticInPaymentV2);
    DomesticInTransactionMonitoringExceptionV2 exceptionV2 = assertThrows(
            DomesticInTransactionMonitoringExceptionV2.class,
        () -> domesticInTransactionMonitoringServiceV2
            .checkDomesticInPaymentV2(domesticInPaymentV2));
    assertThat(exceptionV2.getError(), is(DomesticInTransactionMonitoringExceptionV2.Error.MISSING_REQUIRED_FIELDS));
    Mockito.verify(transactionMonitoringClient, times(1)).checkFinCrimeV2(domesticInPaymentV2);
    Mockito.verify(paymentMetrics, times(1)).incrementDownStreamFailPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.DOMESTIC_IN);

  }

  @Test
  public void testCheckDomesticInPaymentV2GivenTMClientServerError()
      throws TransactionMonitoringException {
    DomesticInPaymentV2 domesticInPaymentV2 = createDomesticInPaymentV2();
    doThrow(new TransactionMonitoringException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "test"))
        .when(transactionMonitoringClient).checkFinCrimeV2(domesticInPaymentV2);
    DomesticInTransactionMonitoringExceptionV2 exceptionV2 = assertThrows(
            DomesticInTransactionMonitoringExceptionV2.class,
        () -> domesticInTransactionMonitoringServiceV2
            .checkDomesticInPaymentV2(domesticInPaymentV2));
    assertThat(exceptionV2.getError(), is(DomesticInTransactionMonitoringExceptionV2.Error.GENERAL_SERVICE_ERROR));
    Mockito.verify(transactionMonitoringClient, times(1)).checkFinCrimeV2(domesticInPaymentV2);
    Mockito.verify(paymentMetrics, times(1)).incrementDownStreamFailPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.DOMESTIC_IN);

  }

  private DomesticInPaymentV2 createDomesticInPaymentV2() {
    return new DomesticInPaymentV2(new AccountDetailsV2("Test", "123", "Test", "abc", "Test", "IbanTest"),
            "creditor name",
            "test",
      new AccountDetailsV2("Test", "123", "Test", "abc", "Test", "IbanTest"),
            "debitor name",
            "test",
            new PaymentAmountV2("", 30, "", 30),
            "123key",
            new Date(),
            new BalanceBeforeV2("GBP", 500.00, "GBP", 500.00),
            new Date(),
            "Review",
            "Reference Test",
            "test domestic notes",
            "456key",
            "paymentTypeTest");
  }

}
