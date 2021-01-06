package com.tenx.fraudamlmanager.paymentsv3.direct.debit.api;

import static org.mockito.BDDMockito.given;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenx.fraudamlmanager.domain.PaymentMetrics;
import com.tenx.fraudamlmanager.domain.PaymentMetricsType;
import com.tenx.fraudamlmanager.paymentsv3.direct.debit.domain.DirectDebitFinCrimeCheckServiceV3;
import com.tenx.fraudamlmanager.paymentsv3.direct.debit.domain.DirectDebitPaymentV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.AccountDetailsV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.BalanceBeforeV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudAMLSanctionsCheckResponseCodeV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudCheckV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.PaymentAmountV3;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

/**
 * @author Niall O'Connell
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(DirectDebitPaymentsControllerV3.class)
public class DirectDebitPaymentsControllerV3Test {

  @Autowired
  MockMvc mockMvc;

  @MockBean
  DirectDebitFinCrimeCheckServiceV3 finCrimeCheckServiceV3;

  @MockBean
  private PaymentMetrics paymentMetrics;

  @Autowired
  ObjectMapper objectMapString;

  private static final String ENDPOINT = "/v3/payments/directDebitFinCrimeCheck";

  /**
   * @throws Exception Verifying the outbound request with post response
   */
  @Test
  public void domesticOutReturnPaymentV3CheckClear() throws Exception {
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

    given(finCrimeCheckServiceV3.checkFinCrimeV3(directDebitPayment))
        .willReturn(new FraudCheckV3(FraudAMLSanctionsCheckResponseCodeV3.passed));
    mockMvc.perform(post(ENDPOINT)
        .content(objectMapString.writeValueAsString(directDebitPayment))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful());

    Mockito.verify(paymentMetrics, VerificationModeFactory.times(1))
        .incrementCounterTag(PaymentMetrics.PAYMENTS_RECEIVED, PaymentMetricsType.DIRECT_DEBIT.toString());
    Mockito.verify(finCrimeCheckServiceV3, times(1)).checkFinCrimeV3(directDebitPayment);

  }

  @Test
  public void checkFpsOutbound4xxError() throws Exception {
    DirectDebitPaymentV3 directDebitPayment = new DirectDebitPaymentV3();

    mockMvc.perform(post(ENDPOINT)
        .content(objectMapString.writeValueAsString(directDebitPayment))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().is4xxClientError());
  }

}
