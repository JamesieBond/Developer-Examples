package com.tenx.fraudamlmanager.paymentsv3.domestic.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
import com.tenx.fraudamlmanager.paymentsv3.domestic.domain.DomesticOutReturnPaymentV3;
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
public class DomesticOutReturnControllerTestV3 {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    DomesticFinCrimeCheckServiceV3 domesticFinCrimeCheckServiceV3;

    @MockBean
    PaymentMetrics paymentMetrics;

    @Autowired
    ObjectMapper objectMapString;

    @Captor
    ArgumentCaptor<DomesticOutReturnPaymentV3> argumentCaptor;

    private static final String FPSOUTBOUNDURL = "/v3/payments/domesticPaymentOutboundReturnFinCrimeCheck";

    /**
     * @throws Exception Verifying the outbound request with post response
     */
    @Test
    public void domesticOutReturnPaymentV3CheckClear() throws Exception {
        DomesticOutReturnPaymentRequestV3 domesticOutReturnPaymentRequestV3 = new DomesticOutReturnPaymentRequestV3();
        domesticOutReturnPaymentRequestV3.setCreditorAccountDetails(new AccountDetailsRequestV3("Test", "Test"));
        domesticOutReturnPaymentRequestV3.setCreditorName("Test");
        domesticOutReturnPaymentRequestV3.setDebtorAccountDetails(new AccountDetailsRequestV3("Test", "Test"));
        domesticOutReturnPaymentRequestV3.setDebtorName("test");
        domesticOutReturnPaymentRequestV3.setAmount(new PaymentAmountRequestV3("Test", 200, "Test", 200));
        domesticOutReturnPaymentRequestV3.setBalanceBefore(new BalanceBeforeRequestV3("GBP", 500.00, "GBP", 500.00));
        domesticOutReturnPaymentRequestV3.setTransactionId("Testing1234");
        domesticOutReturnPaymentRequestV3.setTransactionDate(new Date());
        domesticOutReturnPaymentRequestV3.setMessageDate(new Date());
        domesticOutReturnPaymentRequestV3.setTransactionStatus("test");
        domesticOutReturnPaymentRequestV3.setTransactionReference("test");
        domesticOutReturnPaymentRequestV3.setTransactionNotes("test");
        domesticOutReturnPaymentRequestV3.setTransactionTags(new ArrayList<>());
        domesticOutReturnPaymentRequestV3.setExistingPayee(Boolean.TRUE);
        domesticOutReturnPaymentRequestV3.setPartyKey("partyKey");

        given(domesticFinCrimeCheckServiceV3.checkFinCrimeV3(any(DomesticOutReturnPaymentV3.class)))
                .willReturn(new FraudCheckV3(FraudAMLSanctionsCheckResponseCodeV3.passed));
        mockMvc.perform(post(FPSOUTBOUNDURL)
                .content(objectMapString.writeValueAsString(domesticOutReturnPaymentRequestV3))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());

        Mockito.verify(paymentMetrics, VerificationModeFactory.times(1))
                .incrementCounterTag(PaymentMetrics.PAYMENTS_RETURNED, PaymentMetricsType.DOMESTIC_OUT.toString());
        Mockito.verify(domesticFinCrimeCheckServiceV3, times(1)).checkFinCrimeV3(argumentCaptor.capture());


        DomesticOutReturnPaymentV3 domesticOutReturnPaymentV3 = argumentCaptor.getValue();

        assertEquals("Test", domesticOutReturnPaymentV3.getCreditorAccountDetails().getAccountNumber());
        assertEquals("Test", domesticOutReturnPaymentV3.getCreditorAccountDetails().getBankId());
        assertEquals("Test", domesticOutReturnPaymentV3.getDebtorAccountDetails().getAccountNumber());
        assertEquals("Test", domesticOutReturnPaymentV3.getDebtorAccountDetails().getBankId());
        assertEquals("Test", domesticOutReturnPaymentV3.getCreditorName());
        assertEquals("test", domesticOutReturnPaymentV3.getDebtorName());
        assertEquals("partyKey", domesticOutReturnPaymentV3.getPartyKey());
        assertEquals("Testing1234", domesticOutReturnPaymentV3.getTransactionId());
        assertEquals("Test", domesticOutReturnPaymentV3.getAmount().getCurrency());
        assertEquals(200, domesticOutReturnPaymentV3.getAmount().getValue(), 0);
        assertEquals("GBP", domesticOutReturnPaymentV3.getBalanceBefore().getCurrency());
        assertEquals(500.0, domesticOutReturnPaymentV3.getBalanceBefore().getBaseValue(), 0);
        assertEquals("test", domesticOutReturnPaymentV3.getTransactionReference());
        assertTrue(domesticOutReturnPaymentV3.getExistingPayee());

    }

    @Test
    public void checkFpsOutbound4xxError() throws Exception {
        DomesticOutReturnPaymentRequestV3 domesticOutReturnPaymentRequestV3 = new DomesticOutReturnPaymentRequestV3();

        mockMvc.perform(post(FPSOUTBOUNDURL)
                .content(objectMapString.writeValueAsString(domesticOutReturnPaymentRequestV3))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

}
