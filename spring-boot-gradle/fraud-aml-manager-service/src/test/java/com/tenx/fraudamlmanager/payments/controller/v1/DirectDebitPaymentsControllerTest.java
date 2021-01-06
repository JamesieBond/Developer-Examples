package com.tenx.fraudamlmanager.payments.controller.v1;

import static org.hamcrest.Matchers.hasItem;
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
import com.tenx.fraudamlmanager.payments.model.api.DirectDebitPayment;
import com.tenx.fraudamlmanager.payments.model.api.FraudCheckResponse;
import com.tenx.fraudamlmanager.payments.model.api.PayAccount;
import com.tenx.fraudamlmanager.payments.model.api.PaymentAmount;
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
 * @author Kamil Cierpisz
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(DirectDebitPaymentsController.class)
public class DirectDebitPaymentsControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    FinCrimeCheckService finCrimeCheckService;

    @MockBean
    private PaymentMetrics paymentMetrics;

    @Autowired
    ObjectMapper objectMapString;

    private final static String DIRECT_DEBIT_ENDPOINT = "/v1/payments/directDebit/finCrimeCheck";

    @Test
    public void directDebitCheckClear() throws Exception {
        DirectDebitPayment directDebitPayment = new DirectDebitPayment();
        directDebitPayment.setId("test");
        directDebitPayment.setPaymentReference("test");
        directDebitPayment.setPaymentStatusReason("test");
        directDebitPayment.setSubscriptionKey("test");
        directDebitPayment.setPartyKey("test");
        directDebitPayment.setProcessingDate(new Date());
        directDebitPayment.setPaymentAmount(new PaymentAmount("test", 10.0));
        directDebitPayment.setPayee(new PayAccount("test", "test", "test"));
        directDebitPayment.setPayer(new PayAccount("test", "test", "test"));


        given(finCrimeCheckService.checkFinCrime(directDebitPayment)).willReturn(new FraudCheckResponse(true, ""));
        mockMvc.perform(post(DIRECT_DEBIT_ENDPOINT)
                .content(objectMapString.writeValueAsString(directDebitPayment))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath(".clear", hasItem(true)));

        Mockito.verify(paymentMetrics, VerificationModeFactory.times(1))
                .incrementCounterTag(PaymentMetrics.PAYMENTS_RECEIVED, PaymentMetricsType.DIRECT_DEBIT.toString());
        Mockito.verify(finCrimeCheckService, times(1)).checkFinCrime(directDebitPayment);
    }

    @Test
    public void checkDirectDebit4xxError() throws Exception {
        DirectDebitPayment directDebitPayment = new DirectDebitPayment();
        mockMvc.perform(post(DIRECT_DEBIT_ENDPOINT)
            .content(objectMapString.writeValueAsString(directDebitPayment))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is4xxClientError());
    }

    @Test
    public void directDebit5xxError() throws Exception {
        DirectDebitPayment directDebitPayment = new DirectDebitPayment(
            "test",
            new PaymentAmount("test", 12342),
            new PayAccount("test", "test", "bankid"),
            new PayAccount("payer", "accnumber", "bankid"),
            "paymentref",
            "PaymentStatusReason",
            "SubKey",
            "partyKey",
            new Date()
        );

        doThrow(new TransactionMonitoringException(500, "mocked exception")).when(finCrimeCheckService)
            .checkFinCrime(directDebitPayment);
        mockMvc.perform(post(DIRECT_DEBIT_ENDPOINT)
            .content(objectMapString.writeValueAsString(directDebitPayment))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is5xxServerError());
    }
}
