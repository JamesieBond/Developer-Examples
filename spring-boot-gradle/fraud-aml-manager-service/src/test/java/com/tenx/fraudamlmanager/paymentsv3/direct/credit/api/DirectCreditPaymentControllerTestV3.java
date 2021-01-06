package com.tenx.fraudamlmanager.paymentsv3.direct.credit.api;

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
import com.tenx.fraudamlmanager.paymentsv3.direct.credit.domain.DirectCreditFinCrimeCheckServiceV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudAMLSanctionsCheckResponseCodeV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudCheckV3;
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
@WebMvcTest(DirectCreditPaymentsControllerV3.class)
public class DirectCreditPaymentControllerTestV3 {


    private static final String DIRECT_CREDIT_URL_V3 = "/v3/payments/directCreditFinCrimeCheck";
    @Autowired
    MockMvc mockMvc;

    @MockBean
    DirectCreditFinCrimeCheckServiceV3 finCrimeCheckServiceV3;

    @MockBean
    private PaymentMetrics paymentMetrics;

    @Autowired
    ObjectMapper objectMapString;

    /**
     * @throws Exception Verifying the outbound request with post response
     */
    @Test
    public void directCreditCheckClear() throws Exception {
        DirectCreditPaymentRequestV3 directCreditPaymentV3 = new DirectCreditPaymentRequestV3(
                new AccountDetailsRequestV3("1234", "5678"),
                "test",
                new AccountDetailsRequestV3("1234", "5678"),
                "test",
                new PaymentAmountRequestV3("test", 1.00, "test", 2.00),
                "test",
                "test",
                new Date(), new Date(),
                new BalanceBeforeRequestV3("test", 200, "test", 200),
                "test",
                "test"
        );

        given(finCrimeCheckServiceV3.checkFinCrimeV3(any()))
                .willReturn(new FraudCheckV3(FraudAMLSanctionsCheckResponseCodeV3.passed));
        mockMvc.perform(post(DIRECT_CREDIT_URL_V3)
                .content(objectMapString.writeValueAsString(directCreditPaymentV3))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
        Mockito.verify(paymentMetrics, VerificationModeFactory.times(1))
                .incrementCounterTag(PaymentMetrics.PAYMENTS_RECEIVED, PaymentMetricsType.DIRECT_CREDIT.toString());
        Mockito.verify(finCrimeCheckServiceV3, times(1)).checkFinCrimeV3(any());

    }

    @Test
    public void directCredit4xxError() throws Exception {
        DirectCreditPaymentRequestV3 directCreditPaymentV3 = new DirectCreditPaymentRequestV3(
                new AccountDetailsRequestV3("1234", "5678"),
                "test",
                new AccountDetailsRequestV3("1234", "5678"),
                "test",
                new PaymentAmountRequestV3("test", 1.00, "test", 2.00),
                null,
                null,
                null, null,
                new BalanceBeforeRequestV3("test", 200, "test", 200),
                "test",
                "test"
        );

        mockMvc.perform(post(DIRECT_CREDIT_URL_V3)
            .content(objectMapString.writeValueAsString(directCreditPaymentV3))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is4xxClientError());
    }

    @Test
    public void directCredit5xxError() throws Exception {
        DirectCreditPaymentRequestV3 directCreditPaymentV3 = new DirectCreditPaymentRequestV3(
                new AccountDetailsRequestV3("1234", "5678"),
                "test",
                new AccountDetailsRequestV3("1234", "5678"),
                "test",
                new PaymentAmountRequestV3("test", 1.00, "test", 2.00),
                "test",
                "test",
                new Date(), new Date(),
                new BalanceBeforeRequestV3("test", 200, "test", 200),
                "test",
                "test"
        );

        doThrow(new TransactionMonitoringException(500, "mocked exception")).when(finCrimeCheckServiceV3)
                .checkFinCrimeV3(any());
        mockMvc.perform(post(DIRECT_CREDIT_URL_V3)
                .content(objectMapString.writeValueAsString(directCreditPaymentV3))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }
}
