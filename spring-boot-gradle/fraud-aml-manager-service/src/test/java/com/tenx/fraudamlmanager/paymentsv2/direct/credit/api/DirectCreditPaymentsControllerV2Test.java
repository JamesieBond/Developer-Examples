package com.tenx.fraudamlmanager.paymentsv2.direct.credit.api;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenx.fraudamlmanager.domain.PaymentMetrics;
import com.tenx.fraudamlmanager.domain.PaymentMetricsType;
import com.tenx.fraudamlmanager.paymentsv2.direct.credit.domain.DirectCreditBacsPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.direct.credit.domain.DirectCreditFinCrimeCheckServiceV2;
import com.tenx.fraudamlmanager.paymentsv2.direct.credit.domain.DirectCreditTransactionMonitoringExceptionV2;
import java.util.Date;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest(DirectCreditPaymentsControllerV2.class)
public class DirectCreditPaymentsControllerV2Test {

  private static final String DIRECT_CREDIT_ENDPOINT = "/v2/payments/directCreditFinCrimeCheck";
  @Autowired
  MockMvc mockMvc;
  @MockBean
  DirectCreditFinCrimeCheckServiceV2 directCreditFinCrimeCheckServiceV2;
  @Autowired
  ObjectMapper objectMapString;
  @MockBean
  private PaymentMetrics paymentMetrics;
  @Captor
  private ArgumentCaptor<DirectCreditBacsPaymentV2> directCreditBacsPaymentV2ArgumentCaptor;

  @Test
  public void directDebitCheckClear() throws Exception {
    DirectCreditBacsPaymentRequestV2 directCreditRequestV2 = createDirectCreditRequestV2();

    mockMvc.perform(post(DIRECT_CREDIT_ENDPOINT)
        .content(objectMapString.writeValueAsString(directCreditRequestV2))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful());

    Mockito.verify(paymentMetrics, VerificationModeFactory.times(1))
        .incrementCounterTag(PaymentMetrics.PAYMENTS_RECEIVED, PaymentMetricsType.DIRECT_CREDIT.toString());
    Mockito.verify(directCreditFinCrimeCheckServiceV2, times(1))
        .checkFinCrimeDirectCreditV2(directCreditBacsPaymentV2ArgumentCaptor.capture());

    DirectCreditBacsPaymentV2 capturedDetails = directCreditBacsPaymentV2ArgumentCaptor.getValue();
    Assertions.assertThat(capturedDetails.getPartyKey()).isEqualTo(directCreditRequestV2.getPartyKey());
    Assertions.assertThat(capturedDetails.getCreditorName()).isEqualTo(directCreditRequestV2.getCreditorName());
    Assertions.assertThat(capturedDetails.getDebtorName()).isEqualTo(directCreditRequestV2.getDebtorName());
    Assertions.assertThat(capturedDetails.getTransactionDate())
        .isEqualTo(directCreditRequestV2.getTransactionDate());
    Assertions.assertThat(capturedDetails.getMessageDate()).isEqualTo(directCreditRequestV2.getMessageDate());
    Assertions.assertThat(capturedDetails.getTransactionId()).isEqualTo(directCreditRequestV2.getTransactionId());
    Assertions.assertThat(capturedDetails.getTransactionReference()).isEqualTo(directCreditRequestV2
        .getTransactionReference());
    Assertions.assertThat(capturedDetails.getTransactionStatus()).isEqualTo(directCreditRequestV2
        .getTransactionStatus());

    Assertions.assertThat(capturedDetails.getCreditorAccountDetails().getAccountNumber())
        .isEqualTo(directCreditRequestV2.getCreditorAccountDetails().getAccountNumber());
    Assertions.assertThat(capturedDetails.getCreditorAccountDetails().getBankId())
        .isEqualTo(directCreditRequestV2.getCreditorAccountDetails().getBankId());

    Assertions.assertThat(capturedDetails.getDebtorAccountDetails().getAccountNumber())
        .isEqualTo(directCreditRequestV2.getDebtorAccountDetails().getAccountNumber());
    Assertions.assertThat(capturedDetails.getDebtorAccountDetails().getBankId())
        .isEqualTo(directCreditRequestV2.getDebtorAccountDetails().getBankId());
  }

  @Test
  public void checkDirectCredit4xxError() throws Exception {
    DirectCreditBacsPaymentRequestV2 directCreditPayment = new DirectCreditBacsPaymentRequestV2();

    mockMvc.perform(post(DIRECT_CREDIT_ENDPOINT)
        .content(objectMapString.writeValueAsString(directCreditPayment))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().is4xxClientError());
  }

  @Test
  public void directCredit5xxError() throws Exception {
    DirectCreditBacsPaymentRequestV2 directCreditRequestV2 = createDirectCreditRequestV2();

    DirectCreditBacsPaymentV2 directCreditBacsPaymentV2 = ApiDomainDirectCreditPaymentMapperV2.MAPPER
        .toDirectCreditPayment(directCreditRequestV2);

    doThrow(new DirectCreditTransactionMonitoringExceptionV2(
        DirectCreditTransactionMonitoringExceptionV2.Error.GENERAL_SERVICE_ERROR, "mocked exception"))
        .when(directCreditFinCrimeCheckServiceV2)
        .checkFinCrimeDirectCreditV2(directCreditBacsPaymentV2);
    mockMvc.perform(post(DIRECT_CREDIT_ENDPOINT)
        .content(objectMapString.writeValueAsString(directCreditRequestV2))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().is5xxServerError());

    Mockito.verify(paymentMetrics, VerificationModeFactory.times(1))
        .incrementCounterTag(PaymentMetrics.PAYMENTS_RECEIVED, PaymentMetricsType.DIRECT_CREDIT.toString());

  }

  private DirectCreditBacsPaymentRequestV2 createDirectCreditRequestV2() {
    return new DirectCreditBacsPaymentRequestV2(
        new AccountDetailsDirectCreditRequestV2("1234 ", "5678"),
        "CreditorName",
        new AccountDetailsDirectCreditRequestV2("1234", "5678"),
        "DebitorName",
        new PaymentAmountDirectCreditRequestV2("USD", 1.00, "Euro", 2.00),
        "PartyKey",
        "TransactionId",
        new Date(),
        new Date(),
        "TransactionStatus",
        "TransactionReference"
    );

  }


}
