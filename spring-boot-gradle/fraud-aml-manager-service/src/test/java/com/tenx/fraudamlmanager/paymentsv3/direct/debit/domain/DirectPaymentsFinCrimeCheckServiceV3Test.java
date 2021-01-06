package com.tenx.fraudamlmanager.paymentsv3.direct.debit.domain;

import static com.tenx.fraudamlmanager.domain.MetricsStaticStrings.DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraudamlmanager.domain.PaymentMetrics;
import com.tenx.fraudamlmanager.domain.PaymentMetricsType;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.payments.service.impl.FraudCheckResponseMetrics;
import com.tenx.fraudamlmanager.paymentsv3.direct.debit.cases.domain.DirectDebitCaseServiceV3;
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
public class DirectPaymentsFinCrimeCheckServiceV3Test {

  private DirectDebitFinCrimeCheckServiceV3 finCrimeCheckServiceV3;

  @MockBean
  DirectDebitTransactionMonitoringConnectorV3 infrastructureServiceV3;

  @MockBean
  private PaymentMetrics paymentMetrics;

  @MockBean
  FraudCheckResponseMetrics fraudCheckMetric;

  @MockBean
  private DirectDebitCaseServiceV3 directDebitCaseServiceV3;


  @BeforeEach
  public void beforeEach() {
    this.finCrimeCheckServiceV3 = new DirectDebitFinCrimeCheckServiceV3Impl(
        infrastructureServiceV3, paymentMetrics, fraudCheckMetric, directDebitCaseServiceV3);
  }

  /**
   * @throws TransactionMonitoringException Generic exception
   */
  @Test
  public void testDirectDebitPaymentV3() throws TransactionMonitoringException {
    DirectDebitPaymentV3 directDebitPayment = new DirectDebitPaymentV3(
        new AccountDetailsV3("1234 ", "5678"),
        "test",
        new AccountDetailsV3("1234", "5678"),
        "test",
        new PaymentAmountV3("test", 1.00, "test", 2.00),
        "test",
        "test",
        new Date(),
        new Date(),
        new BalanceBeforeV3("Test", 500, "Test", 500),
        "test",
        "test"
    );

    FraudCheckV3 fraudCheckV3 = new FraudCheckV3(
        FraudAMLSanctionsCheckResponseCodeV3.passed);

    given(infrastructureServiceV3.sendFinCrimeCheck(directDebitPayment))
        .willReturn(fraudCheckV3);

    finCrimeCheckServiceV3.checkFinCrimeV3(directDebitPayment);
    Mockito.verify(directDebitCaseServiceV3, times(1)).processCaseForDirectDebit(eq(FraudAMLSanctionsCheckResponseCodeV3.passed), eq(directDebitPayment));

    Mockito.verify(infrastructureServiceV3, times(1)).sendFinCrimeCheck(eq(directDebitPayment));
    Mockito.verify(paymentMetrics, times(1)).incrementDownStreamSuccessPayment(DOWNSTREAM_SERVICE_TRANSACTIONMONITORING_NAME, PaymentMetricsType.DIRECT_DEBIT);
    Mockito.verify(fraudCheckMetric, times(1)).incrementFraudCheck(
        fraudCheckMetric.DIRECT_DEBIT_FRAUDCHECK_TAG, "passed");
  }

}

