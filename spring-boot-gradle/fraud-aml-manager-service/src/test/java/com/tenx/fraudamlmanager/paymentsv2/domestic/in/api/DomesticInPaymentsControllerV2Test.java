package com.tenx.fraudamlmanager.paymentsv2.domestic.in.api;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenx.fraudamlmanager.domain.PaymentMetrics;
import com.tenx.fraudamlmanager.domain.PaymentMetricsType;
import com.tenx.fraudamlmanager.paymentsv2.domestic.api.AccountDetailsRequestV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.api.BalanceBeforeRequestV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.api.PaymentAmountRequestV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.in.domain.DomesticInFinCrimeCheckServiceV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.in.domain.DomesticInPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.in.domain.DomesticInTransactionMonitoringExceptionV2;
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

@ExtendWith(SpringExtension.class)
@WebMvcTest(DomesticInPaymentsControllerV2.class)
public class DomesticInPaymentsControllerV2Test {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private DomesticInFinCrimeCheckServiceV2 domesticInFinCrimeCheckServiceV2;

  @MockBean
  private PaymentMetrics paymentMetrics;

  @Autowired
  private ObjectMapper objectMapString;

  private static final String FPSINBOUNDURL = "/v2/payments/domesticPaymentInboundFinCrimeCheck";

  @Test
  public void DomesticInboundCheckClear() throws Exception {
    DomesticInPaymentRequestV2 domesticInPaymentRequestV2 = createDomesticInPaymentRequestV2();

    mockMvc.perform(post(FPSINBOUNDURL)
        .content(objectMapString.writeValueAsString(domesticInPaymentRequestV2))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful());

    DomesticInPaymentV2 domesticInPaymentV2 = DomesticInPaymentRequestV2Mapper.MAPPER
        .domesticInPaymentRequestV2toDomesticInPaymentV2(domesticInPaymentRequestV2);
    Mockito.verify(paymentMetrics, VerificationModeFactory.times(1)).incrementCounterTag(PaymentMetrics.PAYMENTS_RECEIVED, PaymentMetricsType.DOMESTIC_IN .toString());
    Mockito.verify(domesticInFinCrimeCheckServiceV2, times(1))
        .checkFinCrimeV2(domesticInPaymentV2);
  }

  @Test
  public void domesticIn5xxError() throws Exception {
    DomesticInPaymentRequestV2 domesticInPaymentRequestV2 = createDomesticInPaymentRequestV2();
    DomesticInPaymentV2 domesticInPaymentV2 = DomesticInPaymentRequestV2Mapper.MAPPER
            .domesticInPaymentRequestV2toDomesticInPaymentV2(domesticInPaymentRequestV2);
    doThrow(new DomesticInTransactionMonitoringExceptionV2(DomesticInTransactionMonitoringExceptionV2.Error.GENERAL_SERVICE_ERROR,
        "mocked exception")).when(domesticInFinCrimeCheckServiceV2)
        .checkFinCrimeV2(domesticInPaymentV2);
    mockMvc.perform(post(FPSINBOUNDURL)
        .content(objectMapString.writeValueAsString(domesticInPaymentRequestV2))
        .contentType(MediaType.APPLICATION_JSON)
        .header("deviceKeyId", "deviceKeyId")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().is5xxServerError());
    Mockito.verify(paymentMetrics, VerificationModeFactory.times(1)).incrementCounterTag(PaymentMetrics.PAYMENTS_RECEIVED, PaymentMetricsType.DOMESTIC_IN.toString());
  }

  private DomesticInPaymentRequestV2 createDomesticInPaymentRequestV2() {
    return new DomesticInPaymentRequestV2(new AccountDetailsRequestV2("123", "abc"),
        "creditor name", new AccountDetailsRequestV2("098", "zyx"),
        "debitor name",
        new PaymentAmountRequestV2("EUR", 30, "EUR", 30),
        new BalanceBeforeRequestV2("GBP", 500.00, "GBP", 500.00),
        "123key",
        new Date(), new Date(),
        "Review", "Reference Test");
  }

}
