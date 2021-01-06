package com.tenx.fraudamlmanager.paymentsv3.domestic.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenx.fraudamlmanager.domain.PaymentMetrics;
import com.tenx.fraudamlmanager.domain.PaymentMetricsType;
import com.tenx.fraudamlmanager.paymentsv3.api.AccountDetailsRequestV3;
import com.tenx.fraudamlmanager.paymentsv3.api.BalanceBeforeRequestV3;
import com.tenx.fraudamlmanager.paymentsv3.api.PaymentAmountRequestV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudAMLSanctionsCheckResponseCodeV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudCheckV3;
import com.tenx.fraudamlmanager.paymentsv3.domestic.domain.DomesticFinCrimeCheckServiceV3;
import com.tenx.fraudamlmanager.paymentsv3.domestic.domain.DomesticOutPaymentV3;
import java.util.ArrayList;
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
public class DomesticOutControllerTestV3 {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    DomesticFinCrimeCheckServiceV3 domesticFinCrimeCheckServiceV3;

    @MockBean
    PaymentMetrics paymentMetrics;

    @Autowired
    ObjectMapper objectMapString;

    @Captor
    ArgumentCaptor<DomesticOutPaymentV3> argumentCaptor;

    private static final String FPSOUTBOUNDURL = "/v3/payments/domesticPaymentOutboundFinCrimeCheck";

    /**
     * @throws Exception Verifying the outbound request with post response
     */
    @Test
    public void domesticOutPaymentV3CheckClear() throws Exception {
        DomesticOutPaymentRequestV3 domesticOutPaymentRequestV3 = new DomesticOutPaymentRequestV3();
        domesticOutPaymentRequestV3.setCreditorAccountDetails(new AccountDetailsRequestV3("Test", "Test"));
        domesticOutPaymentRequestV3.setCreditorName("Test");
        domesticOutPaymentRequestV3.setDebtorAccountDetails(new AccountDetailsRequestV3("Test", "Test"));
        domesticOutPaymentRequestV3.setDebtorName("test");
        domesticOutPaymentRequestV3.setDebtorPartyKey("debtorPartyKey");
        domesticOutPaymentRequestV3.setAmount(new PaymentAmountRequestV3("Test", 200, "Test", 200));
        domesticOutPaymentRequestV3.setBalanceBefore(new BalanceBeforeRequestV3("GBP", 500.00, "GBP", 500.00));
        domesticOutPaymentRequestV3.setTransactionId("Testing1234");
        domesticOutPaymentRequestV3.setTransactionDate(new Date());
        domesticOutPaymentRequestV3.setMessageDate(new Date());
        domesticOutPaymentRequestV3.setTransactionStatus("test");
        domesticOutPaymentRequestV3.setTransactionReference("test");
        domesticOutPaymentRequestV3.setTransactionNotes("test");
        domesticOutPaymentRequestV3.setTransactionTags(new ArrayList<>());
        domesticOutPaymentRequestV3.setExistingPayee(Boolean.TRUE);

        given(domesticFinCrimeCheckServiceV3.checkFinCrimeV3(any(DomesticOutPaymentV3.class), eq("deviceKeyId1")))
                .willReturn(new FraudCheckV3(FraudAMLSanctionsCheckResponseCodeV3.passed));
        mockMvc.perform(post(FPSOUTBOUNDURL)
                .content(objectMapString.writeValueAsString(domesticOutPaymentRequestV3))
                .contentType(MediaType.APPLICATION_JSON)
                .header("deviceKeyId", "deviceKeyId1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());

        Mockito.verify(paymentMetrics, VerificationModeFactory.times(1))
                .incrementCounterTag(PaymentMetrics.PAYMENTS_RECEIVED, PaymentMetricsType.DOMESTIC_OUT.toString());
        Mockito.verify(domesticFinCrimeCheckServiceV3, times(1)).checkFinCrimeV3(argumentCaptor.capture(), eq("deviceKeyId1"));

        DomesticOutPaymentV3 domesticOutPaymentV3 = argumentCaptor.getValue();

        assertEquals("Test", domesticOutPaymentV3.getCreditorAccountDetails().getAccountNumber());
        assertEquals("Test", domesticOutPaymentV3.getCreditorAccountDetails().getBankId());
        assertEquals("Test", domesticOutPaymentV3.getDebtorAccountDetails().getAccountNumber());
        assertEquals("Test", domesticOutPaymentV3.getDebtorAccountDetails().getBankId());
        assertEquals("Test", domesticOutPaymentV3.getCreditorName());
        assertEquals("test", domesticOutPaymentV3.getDebtorName());
        assertEquals("debtorPartyKey", domesticOutPaymentV3.getDebtorPartyKey());
        assertEquals("Testing1234", domesticOutPaymentV3.getTransactionId());
        assertEquals("Test", domesticOutPaymentV3.getAmount().getCurrency());
        assertEquals(200, domesticOutPaymentV3.getAmount().getValue(), 0);
        assertEquals("GBP", domesticOutPaymentV3.getBalanceBefore().getCurrency());
        assertEquals(500.0, domesticOutPaymentV3.getBalanceBefore().getBaseValue(), 0);
        assertEquals("test", domesticOutPaymentV3.getTransactionReference());
        assertTrue(domesticOutPaymentV3.getExistingPayee());

    }

    @Test
    public void checkFpsOutbound4xxError() throws Exception {
        DomesticOutPaymentRequestV3 domesticOutPaymentRequestV3 = new DomesticOutPaymentRequestV3();

        mockMvc.perform(post(FPSOUTBOUNDURL)
            .content(objectMapString.writeValueAsString(domesticOutPaymentRequestV3))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is4xxClientError());
    }

}
