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
import com.tenx.fraudamlmanager.payments.model.api.DirectCreditPayment;
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
@WebMvcTest(DirectCreditPaymentsController.class)
public class DirectCreditPaymentsControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    FinCrimeCheckService finCrimeCheckService;

    @MockBean
    private PaymentMetrics paymentMetrics;

    @Autowired
    ObjectMapper objectMapString;

    private static final String DIRECT_CREDIT_ENDPOINT = "/v1/payments/directCredit/finCrimeCheck";

    @Test
    public void directCreditCheckClear() throws Exception {
        DirectCreditPayment directCreditPayment = new DirectCreditPayment();
        directCreditPayment.setId("test");
        directCreditPayment.setPaymentReference("test");
        directCreditPayment.setPaymentStatusReason("test");
        directCreditPayment.setSubscriptionKey("test");
        directCreditPayment.setPartyKey("test");
        directCreditPayment.setProcessingDate(new Date());
        directCreditPayment.setPaymentAmount(new PaymentAmount("test", 10.0));
        directCreditPayment.setPayee(new PayAccount("test", "test", "test"));
        directCreditPayment.setPayer(new PayAccount("test", "test", "test"));


        given(finCrimeCheckService.checkFinCrime(directCreditPayment)).willReturn(new FraudCheckResponse(true, ""));
        mockMvc.perform(post(DIRECT_CREDIT_ENDPOINT)
                .content(objectMapString.writeValueAsString(directCreditPayment))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath(".clear", hasItem(true)));

        Mockito.verify(paymentMetrics, VerificationModeFactory.times(1))
                .incrementCounterTag(PaymentMetrics.PAYMENTS_RECEIVED, PaymentMetricsType.DIRECT_CREDIT.toString());
        Mockito.verify(finCrimeCheckService, times(1)).checkFinCrime(directCreditPayment);
    }

    @Test
    public void checkDirectCredit4xxError() throws Exception {
        DirectCreditPayment directCreditPayment = new DirectCreditPayment();

        mockMvc.perform(post(DIRECT_CREDIT_ENDPOINT)
            .content(objectMapString.writeValueAsString(directCreditPayment))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is4xxClientError());
    }

    @Test
    public void directCredit5xxError() throws Exception {
        DirectCreditPayment directCreditPayment = new DirectCreditPayment(
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
            .checkFinCrime(directCreditPayment);
        mockMvc.perform(post(DIRECT_CREDIT_ENDPOINT)
            .content(objectMapString.writeValueAsString(directCreditPayment))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is5xxServerError());
    }
}
