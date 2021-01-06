package com.tenx.fraudamlmanager.paymentsv3.direct.credit.domain;

import static com.tenx.fraudamlmanager.domain.MetricsStaticStrings.DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraudamlmanager.domain.PaymentMetrics;
import com.tenx.fraudamlmanager.domain.PaymentMetricsType;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.payments.service.impl.FraudCheckResponseMetrics;
import com.tenx.fraudamlmanager.paymentsv3.direct.credit.cases.domain.DirectCreditCaseServiceV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.AccountDetailsV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.BalanceBeforeV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudAMLSanctionsCheckResponseCodeV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudCheckV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.PaymentAmountV3;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author Niall O'Connell
 */
@ExtendWith(SpringExtension.class)
public class DirectCreditFinCrimeCheckServiceTestV3 {

  private DirectCreditFinCrimeCheckServiceV3 directCreditDomainServiceV3;

  @MockBean
  DirectCreditTransactionMonitoringConnectorV3 directCreditTransactionMonitoringConnectorV3;

  @MockBean
  PaymentMetrics paymentMetrics;

  @MockBean
  FraudCheckResponseMetrics fraudCheckMetric;

  @MockBean
  DirectCreditCaseServiceV3 caseDomainServiceV3;

  @BeforeEach
  public void beforeEach() {
    this.directCreditDomainServiceV3 = new DirectCreditFinCrimeCheckServiceV3Impl(
        directCreditTransactionMonitoringConnectorV3, paymentMetrics, fraudCheckMetric, caseDomainServiceV3);
  }

  /**
   * @throws TransactionMonitoringException Generic exception
   */
  @Test
  public void checkDirectCreditPaymentPassedMethod() throws TransactionMonitoringException {
    DirectCreditPaymentV3 directCreditPaymentV3 = new DirectCreditPaymentV3();
    directCreditPaymentV3.setCreditorAccountDetails(new AccountDetailsV3("Test", "Test"));
    directCreditPaymentV3.setCreditorName("Test");
    directCreditPaymentV3.setPartyKey("Test");
    directCreditPaymentV3.setDebtorAccountDetails(new AccountDetailsV3("Test", "Test"));
    directCreditPaymentV3.setDebtorName("test");
    directCreditPaymentV3.setAmount(new PaymentAmountV3("Test", 200, "Test", 200));
    directCreditPaymentV3.setBalanceBefore(new BalanceBeforeV3("GBP", 500.00, "GBP", 500.00));
    directCreditPaymentV3.setTransactionId("Testing1234");
    directCreditPaymentV3.setTransactionDate(new Date());
    directCreditPaymentV3.setMessageDate(new Date());
    directCreditPaymentV3.setTransactionStatus("test");
    directCreditPaymentV3.setTransactionReference("test");

    FraudCheckV3 fraudCheckV3 = new FraudCheckV3(
        FraudAMLSanctionsCheckResponseCodeV3.passed);

    given(directCreditTransactionMonitoringConnectorV3.sendFinCrimeCheck(directCreditPaymentV3))
        .willReturn(fraudCheckV3);
    directCreditDomainServiceV3.checkFinCrimeV3(directCreditPaymentV3);
    Mockito.verify(caseDomainServiceV3, times(1)).processPaymentForCase(eq(FraudAMLSanctionsCheckResponseCodeV3.passed), eq(directCreditPaymentV3));
    Mockito.verify(directCreditTransactionMonitoringConnectorV3, times(1)).sendFinCrimeCheck(directCreditPaymentV3);
    Mockito.verify(paymentMetrics, times(1)).incrementDownStreamSuccessPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.DIRECT_CREDIT);
    Mockito.verify(fraudCheckMetric, times(1)).incrementFraudCheck(
        fraudCheckMetric.DIRECT_CREDIT_FRAUDCHECK_TAG, "passed");
  }
}
