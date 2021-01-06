package com.tenx.fraudamlmanager.paymentsv2.domestic.outreturned.infrastructure;

import static com.tenx.fraudamlmanager.domain.MetricsStaticStrings.DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraudamlmanager.domain.PaymentMetrics;
import com.tenx.fraudamlmanager.domain.PaymentMetricsType;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain.AccountDetailsDomesticOutReturnV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain.BalanceBeforeDomesticOutReturnV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain.DomesticOutReturnPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain.DomesticOutReturnTransactionMonitoringExceptionV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain.DomesticOutReturnTransactionMonitoringServiceV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain.PaymentAmountDomesticOutReturnV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.infrastructure.DomesticOutReturnTransactionMonitoringClientV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.infrastructure.DomesticOutReturnTransactionMonitoringServiceImplV2;
import java.util.ArrayList;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class DomesticOutReturnTransactionMonitoringServiceImplV2Test {

  private DomesticOutReturnTransactionMonitoringServiceV2 domesticOutReturnTransactionMonitoringServiceV2;

  @MockBean
  private PaymentMetrics paymentMetrics;
  @MockBean
  private DomesticOutReturnTransactionMonitoringClientV2 domesticOutReturnTransactionMonitoringClientV2;

  @BeforeEach
  public void beforeEach() {
    this.domesticOutReturnTransactionMonitoringServiceV2 = new DomesticOutReturnTransactionMonitoringServiceImplV2(
        paymentMetrics, domesticOutReturnTransactionMonitoringClientV2);
  }

  @Test
  public void testCheckDomesticOutReturnPaymentV2()
      throws TransactionMonitoringException, DomesticOutReturnTransactionMonitoringExceptionV2 {
    DomesticOutReturnPaymentV2 domesticOutReturnPaymentV2 = createDomesticOutReturnPaymentV2();
    domesticOutReturnTransactionMonitoringServiceV2.checkDomesticOutReturnPaymentV2(domesticOutReturnPaymentV2);
    Mockito.verify(domesticOutReturnTransactionMonitoringClientV2, times(1))
        .postReturnPayment(domesticOutReturnPaymentV2);
    Mockito.verify(paymentMetrics, times(1)).incrementDownStreamSuccessPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.DOMESTIC_OUT);

  }

  @Test
  public void testCheckDomesticOutReturnPaymentV2GivenTMClientBadRequestError()
      throws TransactionMonitoringException {
    DomesticOutReturnPaymentV2 domesticOutReturnPaymentV2 = createDomesticOutReturnPaymentV2();
    doThrow(new TransactionMonitoringException(HttpStatus.BAD_REQUEST.value(), "test"))
        .when(domesticOutReturnTransactionMonitoringClientV2).postReturnPayment(domesticOutReturnPaymentV2);
    DomesticOutReturnTransactionMonitoringExceptionV2 exceptionV2 = assertThrows(
        DomesticOutReturnTransactionMonitoringExceptionV2.class,
        () -> domesticOutReturnTransactionMonitoringServiceV2
            .checkDomesticOutReturnPaymentV2(domesticOutReturnPaymentV2));
    assertThat(exceptionV2.getError(),
        is(DomesticOutReturnTransactionMonitoringExceptionV2.Error.MISSING_REQUIRED_FIELDS));
    Mockito.verify(domesticOutReturnTransactionMonitoringClientV2, times(1))
        .postReturnPayment(domesticOutReturnPaymentV2);
    Mockito.verify(paymentMetrics, times(1)).incrementDownStreamFailPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.DOMESTIC_OUT);

  }

  @Test
  public void testCheckDomesticOutReturnPaymentV2GivenTMClientServerError()
      throws TransactionMonitoringException {
    DomesticOutReturnPaymentV2 domesticOutReturnPaymentV2 = createDomesticOutReturnPaymentV2();
    doThrow(new TransactionMonitoringException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "test"))
        .when(domesticOutReturnTransactionMonitoringClientV2).postReturnPayment(domesticOutReturnPaymentV2);
    DomesticOutReturnTransactionMonitoringExceptionV2 exceptionV2 = assertThrows(
        DomesticOutReturnTransactionMonitoringExceptionV2.class,
        () -> domesticOutReturnTransactionMonitoringServiceV2
            .checkDomesticOutReturnPaymentV2(domesticOutReturnPaymentV2));
    assertThat(exceptionV2.getError(),
        is(DomesticOutReturnTransactionMonitoringExceptionV2.Error.GENERAL_SERVICE_ERROR));
    Mockito.verify(domesticOutReturnTransactionMonitoringClientV2, times(1))
        .postReturnPayment(domesticOutReturnPaymentV2);
    Mockito.verify(paymentMetrics, times(1)).incrementDownStreamFailPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.DOMESTIC_OUT);

  }

  private DomesticOutReturnPaymentV2 createDomesticOutReturnPaymentV2() {
    return new DomesticOutReturnPaymentV2(new AccountDetailsDomesticOutReturnV2("AccountNumber",
        "BankID"), "CreditorFirst CreditorSecond",
        new AccountDetailsDomesticOutReturnV2("AccountNumber", "BankID"),
        "DebtorFirst DebtorSecond",
        new PaymentAmountDomesticOutReturnV2("GBP", 30, "GBP", 30),
        "TranID", new Date(), new Date(), "TransactionStatus",
        "TranRef",
        "TranNotes", new ArrayList<String>(), true,
        new BalanceBeforeDomesticOutReturnV2("GBP", 500.00, "GBP", 500.00),
        "PartyKey"
    );
  }

}
