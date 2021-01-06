package com.tenx.fraudamlmanager.paymentsv2.domestic.outreturned.api;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenx.fraudamlmanager.domain.PaymentMetrics;
import com.tenx.fraudamlmanager.domain.PaymentMetricsType;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.api.AccountDetailsDomesticOutReturnRequestV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.api.BalanceBeforeDomesticOutReturnRequestV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.api.DomesticOutReturnPaymentControllerV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.api.DomesticOutReturnPaymentRequestV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.api.DomesticOutReturnPaymentRequestV2Mapper;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.api.PaymentAmountDomesticOutReturnRequestV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain.DomesticOutReturnFinCrimeCheckServiceV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain.DomesticOutReturnPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain.DomesticOutReturnTransactionMonitoringExceptionV2;
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
@WebMvcTest(DomesticOutReturnPaymentControllerV2.class)
public class DomesticOutReturnPaymentControllerV2Test {

  private static final String FPSOUTBOUNDRETURNURL = "/v2/payments/domesticPaymentOutboundReturnFinCrimeCheck";
  @Autowired
  MockMvc mockMvc;
  @MockBean
  DomesticOutReturnFinCrimeCheckServiceV2 domesticOutReturnFinCrimeCheckServiceV2;
  @Autowired
  ObjectMapper objectMapString;
  @MockBean
  private PaymentMetrics paymentMetrics;
  @Captor
  private ArgumentCaptor<DomesticOutReturnPaymentV2> domesticOutReturnPaymentV2ArgumentCaptor;


  @Test
  public void DomesticOutboundReturnCheckClear() throws Exception {
    DomesticOutReturnPaymentRequestV2 domesticOutReturnPaymentRequestV2 = createDomesticOutReturnPaymentRequestV2();

    mockMvc.perform(post(FPSOUTBOUNDRETURNURL)
        .content(objectMapString.writeValueAsString(domesticOutReturnPaymentRequestV2))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful());

    Mockito.verify(paymentMetrics, VerificationModeFactory.times(1))
        .incrementCounterTag(PaymentMetrics.PAYMENTS_RETURNED, PaymentMetricsType.DOMESTIC_OUT.toString());
    Mockito.verify(domesticOutReturnFinCrimeCheckServiceV2, times(1))
        .checkFinCrimeV2(domesticOutReturnPaymentV2ArgumentCaptor.capture());

    DomesticOutReturnPaymentV2 capturedDetails = domesticOutReturnPaymentV2ArgumentCaptor.getValue();

    Assertions.assertThat(capturedDetails.getPartyKey()).isEqualTo(domesticOutReturnPaymentRequestV2.getPartyKey());
    Assertions.assertThat(capturedDetails.getCreditorName())
        .isEqualTo(domesticOutReturnPaymentRequestV2.getCreditorName());
    Assertions.assertThat(capturedDetails.getDebtorName()).isEqualTo(domesticOutReturnPaymentRequestV2.getDebtorName());
    Assertions.assertThat(capturedDetails.getTransactionDate())
        .isEqualTo(domesticOutReturnPaymentRequestV2.getTransactionDate());
    Assertions.assertThat(capturedDetails.getMessageDate())
        .isEqualTo(domesticOutReturnPaymentRequestV2.getMessageDate());
    Assertions.assertThat(capturedDetails.getTransactionId())
        .isEqualTo(domesticOutReturnPaymentRequestV2.getTransactionId());
    Assertions.assertThat(capturedDetails.getTransactionReference()).isEqualTo(domesticOutReturnPaymentRequestV2
        .getTransactionReference());
    Assertions.assertThat(capturedDetails.getTransactionStatus()).isEqualTo(domesticOutReturnPaymentRequestV2
        .getTransactionStatus());
    Assertions.assertThat(capturedDetails.getExistingPayee()).isEqualTo(domesticOutReturnPaymentRequestV2
        .getExistingPayee());
    Assertions.assertThat(capturedDetails.getTransactionNotes()).isEqualTo(domesticOutReturnPaymentRequestV2
        .getTransactionNotes());

    Assertions.assertThat(capturedDetails.getCreditorAccountDetails().getAccountNumber())
        .isEqualTo(domesticOutReturnPaymentRequestV2.getCreditorAccountDetails().getAccountNumber());
    Assertions.assertThat(capturedDetails.getCreditorAccountDetails().getBankId())
        .isEqualTo(domesticOutReturnPaymentRequestV2.getCreditorAccountDetails().getBankId());

    Assertions.assertThat(capturedDetails.getDebtorAccountDetails().getAccountNumber())
        .isEqualTo(domesticOutReturnPaymentRequestV2.getDebtorAccountDetails().getAccountNumber());
    Assertions.assertThat(capturedDetails.getDebtorAccountDetails().getBankId())
        .isEqualTo(domesticOutReturnPaymentRequestV2.getDebtorAccountDetails().getBankId());

    Assertions.assertThat(capturedDetails.getAmount().getBaseCurrency())
        .isEqualTo(domesticOutReturnPaymentRequestV2.getAmount().getBaseCurrency());
    Assertions.assertThat(capturedDetails.getAmount().getBaseValue())
        .isEqualTo(domesticOutReturnPaymentRequestV2.getAmount().getBaseValue());
    Assertions.assertThat(capturedDetails.getAmount().getValue())
        .isEqualTo(domesticOutReturnPaymentRequestV2.getAmount().getValue());
    Assertions.assertThat(capturedDetails.getAmount().getCurrency())
        .isEqualTo(domesticOutReturnPaymentRequestV2.getAmount().getCurrency());

    Assertions.assertThat(capturedDetails.getBalanceBefore().getBaseCurrency())
        .isEqualTo(domesticOutReturnPaymentRequestV2.getBalanceBefore().getBaseCurrency());
    Assertions.assertThat(capturedDetails.getBalanceBefore().getCurrency())
        .isEqualTo(domesticOutReturnPaymentRequestV2.getBalanceBefore().getCurrency());
    Assertions.assertThat(capturedDetails.getBalanceBefore().getBaseValue())
        .isEqualTo(domesticOutReturnPaymentRequestV2.getBalanceBefore().getBaseValue());
    Assertions.assertThat(capturedDetails.getBalanceBefore().getValue())
        .isEqualTo(domesticOutReturnPaymentRequestV2.getBalanceBefore().getValue());


  }

  @Test
  public void checkDomesticOutboundReturn4xxError() throws Exception {
    DomesticOutReturnPaymentRequestV2 domesticOutReturnPaymentRequestV2 = new DomesticOutReturnPaymentRequestV2();

    mockMvc.perform(post(FPSOUTBOUNDRETURNURL)
        .content(objectMapString.writeValueAsString(domesticOutReturnPaymentRequestV2))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().is4xxClientError());
  }

  @Test
  public void checkDomesticOutboundReturn5xxError() throws Exception {
    DomesticOutReturnPaymentRequestV2 domesticOutReturnPaymentRequestV2 = createDomesticOutReturnPaymentRequestV2();

    DomesticOutReturnPaymentV2 domesticOutReturnPaymentV2 = DomesticOutReturnPaymentRequestV2Mapper.MAPPER
        .domesticOutReturnPaymentRequestV2toDomesticOutReturnPaymentV2(domesticOutReturnPaymentRequestV2);

    doThrow(new DomesticOutReturnTransactionMonitoringExceptionV2(
        DomesticOutReturnTransactionMonitoringExceptionV2.Error.GENERAL_SERVICE_ERROR,
        "mocked exception")).when(domesticOutReturnFinCrimeCheckServiceV2)
        .checkFinCrimeV2(domesticOutReturnPaymentV2);
    mockMvc.perform(post(FPSOUTBOUNDRETURNURL)
        .content(objectMapString.writeValueAsString(domesticOutReturnPaymentRequestV2))
        .contentType(MediaType.APPLICATION_JSON)
        .header("deviceKeyId", "deviceKeyId")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().is5xxServerError());
  }

  private DomesticOutReturnPaymentRequestV2 createDomesticOutReturnPaymentRequestV2() {
    return new DomesticOutReturnPaymentRequestV2(new AccountDetailsDomesticOutReturnRequestV2("AccountNumber",
        "BankID"), "CreditorFirst CreditorSecond",
        new AccountDetailsDomesticOutReturnRequestV2("AccountNumber", "BankID"),
        "DebtorFirst DebtorSecond",
        new PaymentAmountDomesticOutReturnRequestV2("GBP", 30, "GBP", 30),
        "TranID", new Date(), new Date(), "TransactionStatus",
        "TranRef",
        "TranNotes", new ArrayList<String>(), true,
        new BalanceBeforeDomesticOutReturnRequestV2("GBP", 500.00, "GBP", 500.00),
        "PartyKey"
    );
  }
}