package com.tenx.fraudamlmanager.paymentsv3.domestic.domain;

import static com.tenx.fraudamlmanager.domain.MetricsStaticStrings.DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraudamlmanager.deviceprofile.domain.PaymentsDeviceProfileService;
import com.tenx.fraudamlmanager.domain.PaymentMetrics;
import com.tenx.fraudamlmanager.domain.PaymentMetricsType;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.payments.service.impl.FraudCheckResponseMetrics;
import com.tenx.fraudamlmanager.paymentsv3.domain.AccountDetailsV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.BalanceBeforeV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudAMLSanctionsCheckResponseCodeV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudCheckV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.PaymentAmountV3;
import com.tenx.fraudamlmanager.paymentsv3.domestic.cases.domain.DomesticCaseServiceV3;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author Niall O'Connell
 */
//TODO: ARGUMENT CAPTURE!!!FAWEFAEWFEFEWAF bhhh
@ExtendWith(SpringExtension.class)
public class DomesticDomainServiceTestV3 {

  private DomesticFinCrimeCheckServiceV3 domesticFinCrimeCheckServiceV3;

  @MockBean
  PaymentMetrics paymentMetrics;

  @MockBean
  FraudCheckResponseMetrics fraudCheckMetric;

  @MockBean
  private DomesticCaseServiceV3 domesticCaseServiceV3;

  @MockBean
  private PaymentsDeviceProfileService paymentsDeviceProfileService;

  @Mock
  private domesticTransactionMonitoringConnector domesticTransactionMonitoringConnector;

  @Captor
  private ArgumentCaptor<DomesticInPaymentV3> domesticInPaymentV3ArgumentCaptor;

  @Captor
  private ArgumentCaptor<DomesticOutPaymentV3> domesticOutPaymentV3ArgumentCaptor;

  @Captor
  private ArgumentCaptor<DomesticOutReturnPaymentV3> domesticOutReturnPaymentTMARequestV3ArgumentCaptor;

  @BeforeEach
  public void beforeEach() {
    this.domesticFinCrimeCheckServiceV3 = new DomesticFinCrimeCheckServiceV3Impl(paymentMetrics, fraudCheckMetric, domesticCaseServiceV3,
        paymentsDeviceProfileService, domesticTransactionMonitoringConnector);
  }

  /**
   * @throws TransactionMonitoringException Generic exception
   */
  @Test
  public void checkDomesticOutPaymentPassedMethod() throws TransactionMonitoringException {
    DomesticOutPaymentV3 domesticOutPaymentV3 = new DomesticOutPaymentV3();
    domesticOutPaymentV3.setCreditorAccountDetails(new AccountDetailsV3("Test", "Test"));
    domesticOutPaymentV3.setCreditorName("Test");
    domesticOutPaymentV3.setDebtorAccountDetails(new AccountDetailsV3("Test", "Test"));
    domesticOutPaymentV3.setDebtorName("test");
    domesticOutPaymentV3.setDebtorPartyKey("debtorPartyKey");
    domesticOutPaymentV3.setAmount(new PaymentAmountV3("Test", 200, "Test", 200));
    domesticOutPaymentV3.setBalanceBefore(new BalanceBeforeV3("GBP", 500.00, "GBP", 500.00));
    domesticOutPaymentV3.setTransactionId("Testing1234");
    domesticOutPaymentV3.setTransactionDate(new DateTime(2020, 4, 11, 0, 0).toDate());
    domesticOutPaymentV3.setMessageDate(new DateTime(2020, 4, 10, 0, 0).toDate());
    domesticOutPaymentV3.setTransactionStatus("test");
    domesticOutPaymentV3.setTransactionReference("test");
    domesticOutPaymentV3.setTransactionNotes("test");
    domesticOutPaymentV3.setTransactionTags(new ArrayList<>());
    domesticOutPaymentV3.setExistingPayee(Boolean.TRUE);
    domesticOutPaymentV3.setThreatmetrixData(new HashMap<>());

    FraudCheckV3 fraudCheckV3 = new FraudCheckV3(
        FraudAMLSanctionsCheckResponseCodeV3.passed);

    given(domesticTransactionMonitoringConnector.sendFinCrimeCheck(any(DomesticOutPaymentV3.class)))
        .willReturn(fraudCheckV3);

    domesticFinCrimeCheckServiceV3
        .checkFinCrimeV3(domesticOutPaymentV3, "deviceKeyId2");
    // Passed status should neither save payment nor create case
    Mockito.verify(domesticCaseServiceV3, times(1)).processCaseForDomesticOut(eq(FraudAMLSanctionsCheckResponseCodeV3.passed), eq(domesticOutPaymentV3));

    Mockito.verify(domesticTransactionMonitoringConnector, times(1)).sendFinCrimeCheck(domesticOutPaymentV3ArgumentCaptor.capture());
    Mockito.verify(paymentMetrics, times(1)).incrementDownStreamSuccessPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.DOMESTIC_OUT);
    Mockito.verify(fraudCheckMetric, times(1)).incrementFraudCheck(
        fraudCheckMetric.OUTBOUND_FRAUDCHECK_TAG, "passed");

    DomesticOutPaymentV3 domesticOutPaymentV3CapturedValue = domesticOutPaymentV3ArgumentCaptor.getValue();

    Assert.assertEquals("Test", domesticOutPaymentV3CapturedValue.getCreditorAccountDetails().getAccountNumber());
    Assert.assertEquals("Test", domesticOutPaymentV3CapturedValue.getCreditorName());
    Assert.assertEquals("Test", domesticOutPaymentV3CapturedValue.getCreditorAccountDetails().getBankId());
    Assert.assertEquals("Test", domesticOutPaymentV3CapturedValue.getDebtorAccountDetails().getAccountNumber());
    Assert.assertEquals("Test", domesticOutPaymentV3CapturedValue.getDebtorAccountDetails().getBankId());
    Assert.assertEquals(new DateTime(2020, 4, 10, 0, 0).toDate(), domesticOutPaymentV3CapturedValue.getMessageDate());
    Assert.assertEquals(new DateTime(2020, 4, 11, 0, 0).toDate(), domesticOutPaymentV3CapturedValue.getTransactionDate());
    Assert.assertEquals("Testing1234", domesticOutPaymentV3CapturedValue.getTransactionId());
    Assert.assertEquals("test", domesticOutPaymentV3CapturedValue.getTransactionNotes());
    Assert.assertEquals("GBP", domesticOutPaymentV3CapturedValue.getBalanceBefore().getBaseCurrency());
    Assert.assertEquals("GBP", domesticOutPaymentV3CapturedValue.getBalanceBefore().getCurrency());
    Assert.assertEquals("Test", domesticOutPaymentV3CapturedValue.getAmount().getBaseCurrency());
    Assert.assertEquals("Test", domesticOutPaymentV3CapturedValue.getAmount().getCurrency());
    Assert.assertEquals(500.00, domesticOutPaymentV3CapturedValue.getBalanceBefore().getValue(), 0);
    Assert.assertEquals(500.00, domesticOutPaymentV3CapturedValue.getBalanceBefore().getValue(), 0);
    Assert.assertEquals(200.00, domesticOutPaymentV3CapturedValue.getAmount().getValue(), 0);
    Assert.assertEquals(200.00, domesticOutPaymentV3CapturedValue.getAmount().getBaseValue(), 0);
    Assert.assertEquals("debtorPartyKey", domesticOutPaymentV3CapturedValue.getDebtorPartyKey());
    Assert.assertTrue(domesticOutPaymentV3CapturedValue.getExistingPayee());
  }

  @Test
  public void checkDomesticOutReturnPaymentPassedMethod() throws TransactionMonitoringException {
    DomesticOutReturnPaymentV3 domesticOutReturnPaymentV3 = new DomesticOutReturnPaymentV3();
    domesticOutReturnPaymentV3.setCreditorAccountDetails(new AccountDetailsV3("Test", "Test"));
    domesticOutReturnPaymentV3.setCreditorName("Test");
    domesticOutReturnPaymentV3.setDebtorAccountDetails(new AccountDetailsV3("Test", "Test"));
    domesticOutReturnPaymentV3.setDebtorName("test");
    domesticOutReturnPaymentV3.setAmount(new PaymentAmountV3("Test", 200, "Test", 200));
    domesticOutReturnPaymentV3.setBalanceBefore(new BalanceBeforeV3("GBP", 500.00, "GBP", 500.00));
    domesticOutReturnPaymentV3.setTransactionId("Testing1234");
    domesticOutReturnPaymentV3.setTransactionDate(new DateTime(2020, 4, 10, 0, 0).toDate());
    domesticOutReturnPaymentV3.setMessageDate(new DateTime(2020, 4, 11, 0, 0).toDate());
    domesticOutReturnPaymentV3.setTransactionStatus("test");
    domesticOutReturnPaymentV3.setTransactionReference("test");
    domesticOutReturnPaymentV3.setTransactionNotes("test");
    domesticOutReturnPaymentV3.setTransactionTags(new ArrayList<>());
    domesticOutReturnPaymentV3.setExistingPayee(Boolean.TRUE);
    domesticOutReturnPaymentV3.setPartyKey("test");

    FraudCheckV3 fraudCheckV3 = new FraudCheckV3(
        FraudAMLSanctionsCheckResponseCodeV3.passed);

    given(domesticTransactionMonitoringConnector.sendFinCrimeCheck(any(DomesticOutReturnPaymentV3.class)))
        .willReturn(fraudCheckV3);

    domesticFinCrimeCheckServiceV3.checkFinCrimeV3(domesticOutReturnPaymentV3);

    Mockito.verify(domesticTransactionMonitoringConnector, times(1)).sendFinCrimeCheck(domesticOutReturnPaymentTMARequestV3ArgumentCaptor.capture());
    Mockito.verify(paymentMetrics, times(1)).incrementDownStreamSuccessPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.DOMESTIC_OUT);
    Mockito.verify(domesticCaseServiceV3, times(1)).processCaseForDomesticOutReturn(eq(FraudAMLSanctionsCheckResponseCodeV3.passed), eq(domesticOutReturnPaymentV3));
    Mockito.verify(fraudCheckMetric, times(1)).incrementFraudCheck(
        fraudCheckMetric.OUTBOUND_RETURN_FRAUDCHECK_TAG, "passed");

    DomesticOutReturnPaymentV3 domesticOutReturnPaymentTMARequestV3ArgumentCaptorValue = domesticOutReturnPaymentTMARequestV3ArgumentCaptor.getValue();

    Assert.assertEquals("Test", domesticOutReturnPaymentTMARequestV3ArgumentCaptorValue.getCreditorAccountDetails().getAccountNumber());
    Assert.assertEquals("Test", domesticOutReturnPaymentTMARequestV3ArgumentCaptorValue.getCreditorName());
    Assert.assertEquals("Test", domesticOutReturnPaymentTMARequestV3ArgumentCaptorValue.getCreditorAccountDetails().getBankId());
    Assert.assertEquals("Test", domesticOutReturnPaymentTMARequestV3ArgumentCaptorValue.getDebtorAccountDetails().getAccountNumber());
    Assert.assertEquals("Test", domesticOutReturnPaymentTMARequestV3ArgumentCaptorValue.getDebtorAccountDetails().getBankId());
    Assert.assertEquals(new DateTime(2020, 4, 11, 0, 0).toDate(), domesticOutReturnPaymentTMARequestV3ArgumentCaptorValue.getMessageDate());
    Assert.assertEquals(new DateTime(2020, 4, 10, 0, 0).toDate(), domesticOutReturnPaymentTMARequestV3ArgumentCaptorValue.getTransactionDate());
    Assert.assertEquals("Testing1234", domesticOutReturnPaymentTMARequestV3ArgumentCaptorValue.getTransactionId());
    Assert.assertEquals("test", domesticOutReturnPaymentTMARequestV3ArgumentCaptorValue.getTransactionNotes());
    Assert.assertEquals("GBP", domesticOutReturnPaymentTMARequestV3ArgumentCaptorValue.getBalanceBefore().getBaseCurrency());
    Assert.assertEquals("GBP", domesticOutReturnPaymentTMARequestV3ArgumentCaptorValue.getBalanceBefore().getCurrency());
    Assert.assertEquals("Test", domesticOutReturnPaymentTMARequestV3ArgumentCaptorValue.getAmount().getBaseCurrency());
    Assert.assertEquals("Test", domesticOutReturnPaymentTMARequestV3ArgumentCaptorValue.getAmount().getCurrency());
    Assert.assertEquals(500.00, domesticOutReturnPaymentTMARequestV3ArgumentCaptorValue.getBalanceBefore().getValue(), 0);
    Assert.assertEquals(500.00, domesticOutReturnPaymentTMARequestV3ArgumentCaptorValue.getBalanceBefore().getValue(), 0);
    Assert.assertEquals(200.00, domesticOutReturnPaymentTMARequestV3ArgumentCaptorValue.getAmount().getValue(), 0);
    Assert.assertEquals(200.00, domesticOutReturnPaymentTMARequestV3ArgumentCaptorValue.getAmount().getBaseValue(), 0);
    Assert.assertEquals("test", domesticOutReturnPaymentTMARequestV3ArgumentCaptorValue.getPartyKey());
    Assert.assertTrue(domesticOutReturnPaymentTMARequestV3ArgumentCaptorValue.getExistingPayee());
  }

  /**
   * @throws TransactionMonitoringException Generic exception
   */
  @Test
  public void checkDomesticInPaymentPassedMethod() throws TransactionMonitoringException {
    DomesticInPaymentV3 domesticInPaymentV3 = new DomesticInPaymentV3(
        new AccountDetailsV3("123", "abc"), "creditor name",
        new AccountDetailsV3("098", "zyx"), "debitor name",
        new PaymentAmountV3("EUR", 10.0, "EUR", 10.0),
        new BalanceBeforeV3("EUR", 100.00, "EUR", 100.00),
        "123key",
        new Date(), new Date(),
        "Review", "Test", "creditorPartyKey");

    FraudCheckV3 fraudCheckV3 = new FraudCheckV3(
        FraudAMLSanctionsCheckResponseCodeV3.passed);

    given(domesticTransactionMonitoringConnector.sendFinCrimeCheck(any(DomesticInPaymentV3.class)))
        .willReturn(fraudCheckV3);


    domesticFinCrimeCheckServiceV3.checkFinCrimeV3(domesticInPaymentV3);
    // Passed status should neither save payment nor create case
    Mockito.verify(domesticCaseServiceV3, times(1)).processCaseForDomesticIn(eq(FraudAMLSanctionsCheckResponseCodeV3.passed), eq(domesticInPaymentV3));

    Mockito.verify(domesticTransactionMonitoringConnector, times(1)).sendFinCrimeCheck(domesticInPaymentV3ArgumentCaptor.capture());
    Mockito.verify(paymentMetrics, times(1)).incrementDownStreamSuccessPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.DOMESTIC_IN);
    Mockito.verify(fraudCheckMetric, times(1)).incrementFraudCheck(
        fraudCheckMetric.INBOUND_FRAUDCHECK_TAG, "passed");

    DomesticInPaymentV3 domesticInPaymentV3ArgumentCaptorValue = domesticInPaymentV3ArgumentCaptor.getValue();

    Assert.assertEquals("123", domesticInPaymentV3ArgumentCaptorValue.getCreditorAccountDetails().getAccountNumber());
    Assert.assertEquals("creditor name", domesticInPaymentV3ArgumentCaptorValue.getCreditorName());
    Assert.assertEquals("abc", domesticInPaymentV3ArgumentCaptorValue.getCreditorAccountDetails().getBankId());
    Assert.assertEquals("098", domesticInPaymentV3ArgumentCaptorValue.getDebtorAccountDetails().getAccountNumber());
    Assert.assertEquals("zyx", domesticInPaymentV3ArgumentCaptorValue.getDebtorAccountDetails().getBankId());
    Assert.assertEquals(domesticInPaymentV3.getMessageDate(), domesticInPaymentV3ArgumentCaptorValue.getMessageDate());
    Assert.assertEquals(domesticInPaymentV3.getTransactionDate(), domesticInPaymentV3ArgumentCaptorValue.getTransactionDate());
    Assert.assertEquals("123key", domesticInPaymentV3ArgumentCaptorValue.getTransactionId());
    Assert.assertEquals("Review", domesticInPaymentV3ArgumentCaptorValue.getTransactionStatus());
    Assert.assertEquals("EUR", domesticInPaymentV3ArgumentCaptorValue.getBalanceBefore().getBaseCurrency());
    Assert.assertEquals("EUR", domesticInPaymentV3ArgumentCaptorValue.getBalanceBefore().getCurrency());
    Assert.assertEquals("EUR", domesticInPaymentV3ArgumentCaptorValue.getAmount().getBaseCurrency());
    Assert.assertEquals("EUR", domesticInPaymentV3ArgumentCaptorValue.getAmount().getCurrency());
    Assert.assertEquals(100.00, domesticInPaymentV3ArgumentCaptorValue.getBalanceBefore().getValue(), 0);
    Assert.assertEquals(100.00, domesticInPaymentV3ArgumentCaptorValue.getBalanceBefore().getValue(), 0);
    Assert.assertEquals(10.0, domesticInPaymentV3ArgumentCaptorValue.getAmount().getValue(), 0);
    Assert.assertEquals(10.0, domesticInPaymentV3ArgumentCaptorValue.getAmount().getBaseValue(), 0);
    Assert.assertEquals("creditorPartyKey", domesticInPaymentV3ArgumentCaptorValue.getCreditorPartyKey());
  }
}

