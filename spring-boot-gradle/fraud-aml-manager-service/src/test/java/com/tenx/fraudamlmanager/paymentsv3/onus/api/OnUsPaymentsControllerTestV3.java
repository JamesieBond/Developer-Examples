package com.tenx.fraudamlmanager.paymentsv3.onus.api;

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
import com.tenx.fraudamlmanager.paymentsv3.onus.domain.OnUsFinCrimeCheckServiceV3;
import com.tenx.fraudamlmanager.paymentsv3.onus.infrastructure.OnUsPaymentV3;
import java.util.ArrayList;
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
@WebMvcTest(OnUsPaymentsControllerV3.class)
public class OnUsPaymentsControllerTestV3 {

    private final static String ON_US_ENDPOINT_V3 = "/v3/payments/onUsFinCrimeCheck";
    @Autowired
    MockMvc mockMvc;
    @MockBean
    OnUsFinCrimeCheckServiceV3 onUsFinCrimeCheckServiceV3;
    @MockBean
    OnUsPaymentMapper onUsPaymentsMapper;
    @MockBean
    private PaymentMetrics paymentMetrics;
    @Autowired
    ObjectMapper objMap;

    @Test
    public void onUsPaymentCheckClearV3() throws Exception {
        OnUsPaymentRequestV3 onUsPaymentRequestV3 = new OnUsPaymentRequestV3(
                new AccountDetailsRequestV3("Test", "Test"),
                "Test",
                new AccountDetailsRequestV3("Test", "Test"),
                "Test",
                new PaymentAmountRequestV3("Test", 2, "Test", 2),
                new BalanceBeforeRequestV3("GBP", 500.00, "GBP", 500.00),
                "Test",
                "Test",
                "Test",
                new Date(), new Date(),
                "Test",
                "Test",
                "Test",
                new ArrayList<>(),
                true
        );
        OnUsPaymentV3 onUsPaymentV3 = new OnUsPaymentV3();

        given(onUsPaymentsMapper.toOnUsPayment(onUsPaymentRequestV3)).willReturn(onUsPaymentV3);
        given(onUsFinCrimeCheckServiceV3.checkFinCrimeV3(onUsPaymentV3, "deviceKeyId1"))
                .willReturn(new FraudCheckV3(FraudAMLSanctionsCheckResponseCodeV3.passed));
        mockMvc.perform(post(ON_US_ENDPOINT_V3)
                .content(objMap.writeValueAsString(onUsPaymentRequestV3))
                .contentType(MediaType.APPLICATION_JSON)
                .header("deviceKeyId", "deviceKeyId1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());

        Mockito.verify(paymentMetrics, VerificationModeFactory.times(1))
                .incrementCounterTag(PaymentMetrics.PAYMENTS_RECEIVED, PaymentMetricsType.ON_US.toString());
        Mockito.verify(onUsFinCrimeCheckServiceV3, times(1)).checkFinCrimeV3(onUsPaymentV3, "deviceKeyId1");
        Mockito.verify(onUsPaymentsMapper, times(1)).toOnUsPayment(onUsPaymentRequestV3);

    }

    @Test
    public void checkOnUs4xxErrorV3() throws Exception {
        OnUsPaymentRequestV3 onUsPaymentRequestV3 = new OnUsPaymentRequestV3();

        mockMvc.perform(post(ON_US_ENDPOINT_V3)
            .content(objMap.writeValueAsString(onUsPaymentRequestV3))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is4xxClientError());
    }

    @Test
    public void onUs5xxErrorV3() throws Exception {
        OnUsPaymentRequestV3 onUsPaymentRequestV3 = new OnUsPaymentRequestV3(
                new AccountDetailsRequestV3("Test", "Test"),
                "Test",
                new AccountDetailsRequestV3("Test", "Test"),
                "Test",
                new PaymentAmountRequestV3("Test", 2, "Test", 2),
                new BalanceBeforeRequestV3("GBP", 500.00, "GBP", 500.00),
                "Test",
                "Test",
                "Test",
                new Date(), new Date(),
                "Test",
                "Test",
                "Test",
                new ArrayList<>(),
                true
        );
        OnUsPaymentV3 onUsPaymentV3 = new OnUsPaymentV3();

        given(onUsPaymentsMapper.toOnUsPayment(onUsPaymentRequestV3)).willReturn(onUsPaymentV3);
        doThrow(new TransactionMonitoringException(500, "mocked exception")).when(onUsFinCrimeCheckServiceV3)
            .checkFinCrimeV3(onUsPaymentV3, "deviceKeyId1");
        mockMvc.perform(post(ON_US_ENDPOINT_V3)
            .content(objMap.writeValueAsString(onUsPaymentRequestV3))
            .contentType(MediaType.APPLICATION_JSON)
            .header("deviceKeyId", "deviceKeyId1")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is5xxServerError());

        Mockito.verify(onUsFinCrimeCheckServiceV3, times(1)).checkFinCrimeV3(onUsPaymentV3, "deviceKeyId1");
        Mockito.verify(onUsPaymentsMapper, times(1)).toOnUsPayment(onUsPaymentRequestV3);

    }
}
