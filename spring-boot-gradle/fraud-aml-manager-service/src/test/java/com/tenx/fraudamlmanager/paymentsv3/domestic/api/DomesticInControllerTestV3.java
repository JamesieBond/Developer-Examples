package com.tenx.fraudamlmanager.paymentsv3.domestic.api;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenx.fraudamlmanager.domain.PaymentMetrics;
import com.tenx.fraudamlmanager.domain.PaymentMetricsType;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.paymentsv3.api.AccountDetailsRequestV3;
import com.tenx.fraudamlmanager.paymentsv3.api.BalanceBeforeRequestV3;
import com.tenx.fraudamlmanager.paymentsv3.api.PaymentAmountRequestV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudAMLSanctionsCheckResponseCodeV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudCheckV3;
import com.tenx.fraudamlmanager.paymentsv3.domestic.domain.DomesticFinCrimeCheckServiceV3;
import com.tenx.fraudamlmanager.paymentsv3.domestic.domain.DomesticInPaymentV3;
import java.util.Date;
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

/**
 * @author Niall O'Connell
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(DomesticPaymentsControllerV3.class)
public class DomesticInControllerTestV3 {

    private static final String DOMESTIC_IN_URL_V3 = "/v3/payments/domesticPaymentInboundFinCrimeCheck";
    @Autowired
    MockMvc mockMvc;
    @MockBean
    DomesticFinCrimeCheckServiceV3 domesticFinCrimeCheckServiceV3;
    @MockBean
    DomesticPaymentMapper apiDomainDomesticPaymentMapper;
    @MockBean
    private PaymentMetrics paymentMetrics;
    @Autowired
    ObjectMapper objectMapString;

    @Captor
    ArgumentCaptor<DomesticInPaymentV3> argumentCaptor;

    /**
     * @throws Exception Verifying the outbound request with post response
     */
    @Test
    public void domesticInPaymentV3CheckClear() throws Exception {
        DomesticInPaymentRequestV3 domesticInPaymentRequestV3 = new DomesticInPaymentRequestV3(
                new AccountDetailsRequestV3("123", "abc"), "creditor name",
                new AccountDetailsRequestV3("098", "zyx"), "debitor name",
                new PaymentAmountRequestV3("EUR", 10.0, "EUR", 10.0),
                new BalanceBeforeRequestV3("EUR", 100.00, "EUR", 100.00),
                "123key",
                new Date(), new Date(),
                "Review", "Test", "creditorPartyKey");

        given(domesticFinCrimeCheckServiceV3.checkFinCrimeV3(any(DomesticInPaymentV3.class)))
                .willReturn(new FraudCheckV3(FraudAMLSanctionsCheckResponseCodeV3.passed));
        mockMvc.perform(post(DOMESTIC_IN_URL_V3)
                .content(objectMapString.writeValueAsString(domesticInPaymentRequestV3))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());

        Mockito.verify(paymentMetrics, VerificationModeFactory.times(1))
                .incrementCounterTag(PaymentMetrics.PAYMENTS_RECEIVED, PaymentMetricsType.DOMESTIC_IN.toString());
        Mockito.verify(domesticFinCrimeCheckServiceV3, times(1)).checkFinCrimeV3(argumentCaptor.capture());

        DomesticInPaymentV3 domesticInPayment = argumentCaptor.getValue();

        assertEquals("123", domesticInPayment.getCreditorAccountDetails().getAccountNumber());
        assertEquals("abc", domesticInPayment.getCreditorAccountDetails().getBankId());
        assertEquals("098", domesticInPayment.getDebtorAccountDetails().getAccountNumber());
        assertEquals("zyx", domesticInPayment.getDebtorAccountDetails().getBankId());
        assertEquals("creditor name", domesticInPayment.getCreditorName());
        assertEquals("debitor name", domesticInPayment.getDebtorName());
        assertEquals("creditorPartyKey", domesticInPayment.getCreditorPartyKey());
        assertEquals("123key", domesticInPayment.getTransactionId());
        assertEquals("EUR", domesticInPayment.getAmount().getCurrency());
        assertEquals(10.0, domesticInPayment.getAmount().getValue(), 0);
        assertEquals("EUR", domesticInPayment.getBalanceBefore().getCurrency());
        assertEquals(100.00, domesticInPayment.getBalanceBefore().getBaseValue(), 0);
        assertEquals("Test", domesticInPayment.getTransactionReference());
    }

    @Test
    public void checkFpsInbound4xxError() throws Exception {
        DomesticInPaymentRequestV3 domesticInPaymentRequestV3 = new DomesticInPaymentRequestV3();

        mockMvc.perform(post(DOMESTIC_IN_URL_V3)
                .content(objectMapString.writeValueAsString(domesticInPaymentRequestV3))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is4xxClientError());
    }

    @Test
    public void domesticIn5xxError() throws Exception {
        DomesticInPaymentRequestV3 domesticInPaymentV3 = new DomesticInPaymentRequestV3(
                new AccountDetailsRequestV3("123", "abc"), "creditor name",
                new AccountDetailsRequestV3("098", "zyx"), "debitor name",
                new PaymentAmountRequestV3("EUR", 10.0, "EUR", 10.0),
                new BalanceBeforeRequestV3("EUR", 100.00, "EUR", 100.00),
                "123key",
                new Date(), new Date(),
                "Review", "Test", "creditorPartyKey");

        doThrow(new TransactionMonitoringException(500, "mocked exception")).when(domesticFinCrimeCheckServiceV3)
                .checkFinCrimeV3(any(DomesticInPaymentV3.class));
        mockMvc.perform(post(DOMESTIC_IN_URL_V3)
                .content(objectMapString.writeValueAsString(domesticInPaymentV3))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is5xxServerError());
    }
}
