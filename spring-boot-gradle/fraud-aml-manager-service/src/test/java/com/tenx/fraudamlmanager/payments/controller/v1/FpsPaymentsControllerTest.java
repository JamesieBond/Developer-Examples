package com.tenx.fraudamlmanager.payments.controller.v1;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenx.fraudamlmanager.domain.PaymentMetrics;
import com.tenx.fraudamlmanager.domain.PaymentMetricsType;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.payments.model.api.BalanceBefore;
import com.tenx.fraudamlmanager.payments.model.api.FpsInboundPayment;
import com.tenx.fraudamlmanager.payments.model.api.FpsOutboundPayment;
import com.tenx.fraudamlmanager.payments.model.api.FraudCheckResponse;
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
 * @author Anas Ibrahim
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(FpsPaymentsController.class)
public class FpsPaymentsControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    FinCrimeCheckService finCrimeCheckService;

    @MockBean
    private PaymentMetrics paymentMetrics;

    @Autowired
    ObjectMapper objectMapString;

    private static final String FPSINBOUNDURL = "/v1/payments/fpsInbound/finCrimeCheck";
    private static final String FPSOUTBOUNDURL = "/v1/payments/fpsOutbound/finCrimeCheck";

    /**
     * @throws Exception Verifying the inbound request with post response
     */
    @Test
    public void fpsOutboundCheckClear() throws Exception {
        FpsOutboundPayment fpsOutboundPayment = new FpsOutboundPayment();
        fpsOutboundPayment.setDebtorName("test");
        fpsOutboundPayment.setBalanceBefore(new BalanceBefore("GBP", 500.00, "GBP", 500.00));
        fpsOutboundPayment.setDebtorAccountNumber("test");
        fpsOutboundPayment.setDebtorSortCode("test");
        fpsOutboundPayment.setCreditorName("test");
        fpsOutboundPayment.setCreditorAccountNumber("test");
        fpsOutboundPayment.setCreditorSortCode("test");
        fpsOutboundPayment.setTransactionId("test");
        fpsOutboundPayment.setInstructedAmount(0.00d);
        fpsOutboundPayment.setBaseCurrencyCode("test");
        fpsOutboundPayment.setTransactionDate(new Date());
        fpsOutboundPayment.setTransactionReference("test");

        given(finCrimeCheckService.checkFinCrime(any(FpsOutboundPayment.class))).willReturn(new FraudCheckResponse(true, ""));
        mockMvc.perform(post(FPSOUTBOUNDURL)
                .content(objectMapString.writeValueAsString(fpsOutboundPayment))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath(".clear", hasItem(true)));

        Mockito.verify(paymentMetrics, VerificationModeFactory.times(1))
                .incrementCounterTag(PaymentMetrics.PAYMENTS_RECEIVED, PaymentMetricsType.DOMESTIC_OUT.toString());
        Mockito.verify(finCrimeCheckService, times(1)).checkFinCrime(any(FpsOutboundPayment.class));

    }

    @Test
    public void checkFpsOutbound4xxError() throws Exception {
        FpsOutboundPayment fpsOutboundPayment = new FpsOutboundPayment();

        mockMvc.perform(post(FPSOUTBOUNDURL)
            .content(objectMapString.writeValueAsString(fpsOutboundPayment))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is4xxClientError());
    }

    @Test
    public void fpsOutbound5xxError() throws Exception {
        FpsOutboundPayment fpsOutboundPayment = new FpsOutboundPayment(
            "test", "test", "test",
            "test", "test", "test",
            "test", "test", "test",
            new BalanceBefore("EURO", 123.00, "EURO", 123.00),
            123.00, "test", "test", "test", "test",
            new Date(), "test", new Date()
        );

        doThrow(new TransactionMonitoringException(500, "mocked exception")).when(finCrimeCheckService)
            .checkFinCrime(fpsOutboundPayment);
        mockMvc.perform(post(FPSOUTBOUNDURL)
            .content(objectMapString.writeValueAsString(fpsOutboundPayment))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is5xxServerError());
    }

    @Test
    public void fpsInboundCheckClear() throws Exception {
        FpsInboundPayment fpsInboundPayment = new FpsInboundPayment();
        fpsInboundPayment.setDebtorName("test");
        fpsInboundPayment.setBalanceBefore(new BalanceBefore("GBP", 500.00, "GBP", 500.00));
        fpsInboundPayment.setDebtorAccountNumber("test");
        fpsInboundPayment.setDebtorSortCode("test");
        fpsInboundPayment.setCreditorAccountName("test");
        fpsInboundPayment.setCreditorAccountNumber("test");
        fpsInboundPayment.setCreditorSortCode("test");
        fpsInboundPayment.setTransactionId("test");
        fpsInboundPayment.setInstructedAmount(0.00d);
        fpsInboundPayment.setTransactionDate(new Date());
        fpsInboundPayment.setInstructedAmountCurrency("test");

        given(finCrimeCheckService.checkFinCrime(any(FpsInboundPayment.class))).willReturn(new FraudCheckResponse(true, ""));
        mockMvc.perform(post(FPSINBOUNDURL)
                .content(objectMapString.writeValueAsString(fpsInboundPayment))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath(".clear", hasItem(true)));

        Mockito.verify(paymentMetrics, VerificationModeFactory.times(1))
                .incrementCounterTag(PaymentMetrics.PAYMENTS_RECEIVED, PaymentMetricsType.DOMESTIC_IN.toString());
        Mockito.verify(finCrimeCheckService, times(1)).checkFinCrime(any(FpsInboundPayment.class));
    }

    @Test
    public void checkFpsInbound4xxError() throws Exception {
        FpsInboundPayment fpsInboundPayment = new FpsInboundPayment();

        mockMvc.perform(post(FPSINBOUNDURL)
            .content(objectMapString.writeValueAsString(fpsInboundPayment))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is4xxClientError());
    }

    @Test
    public void fpsInbound5xxError() throws Exception {
        FpsInboundPayment fpsInboundPayment = new FpsInboundPayment(
            "test", "test", "test",
            new BalanceBefore("EURO", 123, "EURO", 123),
            new Date(),
            "test", "test", "test", "test",
            123.00, 123.00,
            "test", "test", "test",
            "test", "test"
        );

        doThrow(new TransactionMonitoringException(500, "mocked exception")).when(finCrimeCheckService)
            .checkFinCrime(fpsInboundPayment);
        mockMvc.perform(post(FPSINBOUNDURL)
            .content(objectMapString.writeValueAsString(fpsInboundPayment))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is5xxServerError());
    }
}
