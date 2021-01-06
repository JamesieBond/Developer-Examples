package com.tenx.fraudamlmanager.paymentsv2.onus.api;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenx.fraudamlmanager.domain.PaymentMetrics;
import com.tenx.fraudamlmanager.domain.PaymentMetricsType;
import com.tenx.fraudamlmanager.paymentsv2.onus.domain.OnUsFinCrimeCheckServiceV2;
import com.tenx.fraudamlmanager.paymentsv2.onus.domain.OnUsPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.onus.domain.OnUsTransactionMonitoringExceptionV2;
import java.util.ArrayList;
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
@WebMvcTest(OnUsPaymentsControllerV2.class)
public class OnUsPaymentsControllerV2Test {

  private static final String ON_US_ENDPOINT = "/v2/payments/onUsFinCrimeCheck";
  @Autowired
  MockMvc mockMvc;
  @MockBean
  OnUsFinCrimeCheckServiceV2 onUsFinCrimeCheckServiceV2;
  @Autowired
  ObjectMapper objectMapString;
  @MockBean
  private PaymentMetrics paymentMetrics;
  @Captor
  private ArgumentCaptor<OnUsPaymentV2> onUsPaymentV2ArgumentCaptor;

  @Test
  public void onUsCheckClear() throws Exception {
    OnUsPaymentRequestV2 onUsPaymentRequestV2 = createOnUsRequestV2();

    mockMvc.perform(post(ON_US_ENDPOINT)
      .content(objectMapString.writeValueAsString(onUsPaymentRequestV2))
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().is2xxSuccessful());

    Mockito.verify(paymentMetrics, VerificationModeFactory.times(1))
      .incrementCounterTag(PaymentMetrics.PAYMENTS_RECEIVED, PaymentMetricsType.ON_US.toString());
    Mockito.verify(onUsFinCrimeCheckServiceV2, times(1))
      .checkFinCrimeV2(onUsPaymentV2ArgumentCaptor.capture());

    OnUsPaymentV2 capturedDetails = onUsPaymentV2ArgumentCaptor.getValue();
    Assertions.assertThat(capturedDetails.getCreditorPartyKey()).isEqualTo(onUsPaymentRequestV2.getCreditorPartyKey());
    Assertions.assertThat(capturedDetails.getDebtorPartyKey()).isEqualTo(onUsPaymentRequestV2.getDebtorPartyKey());
    Assertions.assertThat(capturedDetails.getCreditorName()).isEqualTo(onUsPaymentRequestV2.getCreditorName());
    Assertions.assertThat(capturedDetails.getDebtorName()).isEqualTo(onUsPaymentRequestV2.getDebtorName());
    Assertions.assertThat(capturedDetails.getTransactionDate()).isEqualTo(onUsPaymentRequestV2.getTransactionDate());
    Assertions.assertThat(capturedDetails.getMessageDate()).isEqualTo(onUsPaymentRequestV2.getMessageDate());
    Assertions.assertThat(capturedDetails.getTransactionId()).isEqualTo(onUsPaymentRequestV2.getTransactionId());
    Assertions.assertThat(capturedDetails.getTransactionReference()).isEqualTo(onUsPaymentRequestV2
      .getTransactionReference());
    Assertions.assertThat(capturedDetails.getTransactionStatus()).isEqualTo(onUsPaymentRequestV2
      .getTransactionStatus());

    Assertions.assertThat(capturedDetails.getCreditorAccountDetails().getAccountNumber())
      .isEqualTo(onUsPaymentRequestV2.getCreditorAccountDetails().getAccountNumber());
    Assertions.assertThat(capturedDetails.getCreditorAccountDetails().getBankId())
      .isEqualTo(onUsPaymentRequestV2.getCreditorAccountDetails().getBankId());

    Assertions.assertThat(capturedDetails.getDebtorAccountDetails().getAccountNumber())
      .isEqualTo(onUsPaymentRequestV2.getDebtorAccountDetails().getAccountNumber());
    Assertions.assertThat(capturedDetails.getDebtorAccountDetails().getBankId())
      .isEqualTo(onUsPaymentRequestV2.getDebtorAccountDetails().getBankId());
  }

  @Test
  public void checkOnUs4xxError() throws Exception {
    OnUsPaymentRequestV2 onUsPaymentRequestV2 = new OnUsPaymentRequestV2();

    mockMvc.perform(post(ON_US_ENDPOINT)
      .content(objectMapString.writeValueAsString(onUsPaymentRequestV2))
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().is4xxClientError());
  }

  @Test
  public void onUs5xxError() throws Exception {
    OnUsPaymentRequestV2 onUsPaymentRequestV2 = createOnUsRequestV2();

    OnUsPaymentV2 onUsPaymentV2 = OnUsPaymentMapperV2.MAPPER
      .toOnUsPayment(onUsPaymentRequestV2);

    doThrow(new OnUsTransactionMonitoringExceptionV2(
      OnUsTransactionMonitoringExceptionV2.Error.GENERAL_SERVICE_ERROR, "mocked exception"))
      .when(onUsFinCrimeCheckServiceV2)
      .checkFinCrimeV2(onUsPaymentV2);
    mockMvc.perform(post(ON_US_ENDPOINT)
      .content(objectMapString.writeValueAsString(onUsPaymentRequestV2))
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().is5xxServerError());

    Mockito.verify(paymentMetrics, VerificationModeFactory.times(1))
      .incrementCounterTag(PaymentMetrics.PAYMENTS_RECEIVED, PaymentMetricsType.ON_US.toString());

  }

  private OnUsPaymentRequestV2 createOnUsRequestV2() {
    return new OnUsPaymentRequestV2(
      new AccountDetailsRequestOnUsV2("1234 ", "5678"),
      "CreditorName",
      new AccountDetailsRequestOnUsV2("1234", "5678"),
      "DebitorName",
      new PaymentAmountRequestOnUsV2("USD", 1.00, "Euro", 2.00),
      new BalanceBeforeRequestOnUsV2("GBP", 500.00, "GBP", 500.00),
      "CreditorPartyKey", "debtorPartyKey", "transactionId",
      new Date(),
      new Date(),
      "TransactionStatus", "TransactionReference", "TransactionNotes",
      new ArrayList<String>(), true);
  }
}
