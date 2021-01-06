package com.tenx.fraudamlmanager.payments.controller.v1;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenx.fraudamlmanager.domain.PaymentMetrics;
import com.tenx.fraudamlmanager.domain.PaymentMetricsType;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.payments.model.api.BalanceBefore;
import com.tenx.fraudamlmanager.payments.model.api.FraudCheckResponse;
import com.tenx.fraudamlmanager.payments.model.api.OnUsPayment;
import com.tenx.fraudamlmanager.payments.service.FinCrimeCheckService;
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
 * This test class is meant to test the api OnUsOutboundPayment.
 *
 * @author Massimo Della Rovere
 */

@ExtendWith(SpringExtension.class)
@WebMvcTest(OnUsPaymentsController.class)
public class OnUsPaymentsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    FinCrimeCheckService finCrimeCheckService;

    @MockBean
    private PaymentMetrics paymentMetrics;

    @Autowired
    ObjectMapper objMap;

    private final static String ON_US_ENDPOINT = "/v1/payments/onUs/finCrimeCheck";

    @Test
    public void onUsPaymentCheckClear() throws Exception {
        OnUsPayment onUsPayment = new OnUsPayment();
        onUsPayment.setDebtorName("test");
        onUsPayment.setTransactionId("test");
        onUsPayment.setDebtorName("test");
        onUsPayment.setBalanceBefore(new BalanceBefore("GBP", 500.00, "GBP", 500.00));
        onUsPayment.setDebtorAccountNumber("test");
        onUsPayment.setDebtorSortCode("test");
        onUsPayment.setCreditorName("test");
        onUsPayment.setCreditorAccountNumber("test");
        onUsPayment.setCreditorSortCode("test");
        onUsPayment.setTransactionId("test");
        onUsPayment.setInstructedAmount(0.00d);
        onUsPayment.setBaseCurrencyCode("test");
        onUsPayment.setDebtorPartyKey("test");
        onUsPayment.setTransactionDate(new Date());

        String jsonString = objMap.writeValueAsString(onUsPayment);
        given(finCrimeCheckService.checkFinCrime(any(OnUsPayment.class))).willReturn(new FraudCheckResponse(true, ""));
        mockMvc.perform(post(ON_US_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(jsonString))
            .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath(".clear", hasItem(true)));

        Mockito.verify(paymentMetrics, VerificationModeFactory.times(1))
                .incrementCounterTag(PaymentMetrics.PAYMENTS_RECEIVED, PaymentMetricsType.ON_US.toString());
    }

    @Test
    public void checkOnUs4xxError() throws Exception {
        OnUsPayment onUsPayment = new OnUsPayment();

        mockMvc.perform(post(ON_US_ENDPOINT)
            .content(objMap.writeValueAsString(onUsPayment))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is4xxClientError());
    }

    @Test
    public void onUs5xxError() throws Exception {
        OnUsPayment onUsPayment = new OnUsPayment(
            "test", "test", "test", "test", "test",
            "test", new BalanceBefore("EURO", 1234, "Euro", 123),
            1234.00, "test", 1234.00, "test", 1234.00, new Date(),
            "test", "test", "test", new Date(), "test", "test", "test",
            "test", "test", "test", new Date(), new Date(), "test", "test", "test",
            new Date()
        );

        doThrow(new TransactionMonitoringException(500, "mocked exception")).when(finCrimeCheckService)
            .checkFinCrime(onUsPayment);
        mockMvc.perform(post(ON_US_ENDPOINT)
            .content(objMap.writeValueAsString(onUsPayment))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is5xxServerError());
    }

}
