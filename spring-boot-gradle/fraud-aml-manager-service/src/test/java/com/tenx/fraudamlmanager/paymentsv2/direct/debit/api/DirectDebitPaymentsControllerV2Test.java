package com.tenx.fraudamlmanager.paymentsv2.direct.debit.api;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenx.fraudamlmanager.domain.PaymentMetrics;
import com.tenx.fraudamlmanager.domain.PaymentMetricsType;
import com.tenx.fraudamlmanager.paymentsv2.direct.debit.domain.DirectDebitBacsPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.direct.debit.domain.DirectDebitFinCrimeCheckServiceV2;
import com.tenx.fraudamlmanager.paymentsv2.direct.debit.domain.DirectDebitTransactionMonitoringExceptionV2;
import java.util.Date;
import org.assertj.core.api.Assertions;
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

@ExtendWith(SpringExtension.class)
@WebMvcTest(DirectDebitPaymentsControllerV2.class)
public class DirectDebitPaymentsControllerV2Test {

    private static final String DIRECT_DEBIT_ENDPOINT = "/v2/payments/directDebitFinCrimeCheck";
    @Autowired
    MockMvc mockMvc;
    @MockBean
    DirectDebitFinCrimeCheckServiceV2 directDebitFinCrimeCheckServiceV2;
    @Autowired
    ObjectMapper objectMapString;
    @MockBean
    private PaymentMetrics paymentMetrics;
    @Captor
    private ArgumentCaptor<DirectDebitBacsPaymentV2> directDebitBacsPaymentV2ArgumentCaptor;

    @Test
    public void directDebitCheckClear() throws Exception {
        DirectDebitBacsPaymentRequestV2 directDebitRequestV2 = createDirectDebitRequestV2();

        mockMvc.perform(post(DIRECT_DEBIT_ENDPOINT)
            .content(objectMapString.writeValueAsString(directDebitRequestV2))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is2xxSuccessful());

        Mockito.verify(paymentMetrics, VerificationModeFactory.times(1))
            .incrementCounterTag(PaymentMetrics.PAYMENTS_RECEIVED, PaymentMetricsType.DIRECT_DEBIT.toString());
        Mockito.verify(directDebitFinCrimeCheckServiceV2, times(1))
            .checkFinCrimeV2(directDebitBacsPaymentV2ArgumentCaptor.capture());

        DirectDebitBacsPaymentV2 capturedDetails = directDebitBacsPaymentV2ArgumentCaptor.getValue();
        Assertions.assertThat(capturedDetails.getPartyKey()).isEqualTo(directDebitRequestV2.getPartyKey());
        Assertions.assertThat(capturedDetails.getCreditorName()).isEqualTo(directDebitRequestV2.getCreditorName());
        Assertions.assertThat(capturedDetails.getDebtorName()).isEqualTo(directDebitRequestV2.getDebtorName());
        Assertions.assertThat(capturedDetails.getTransactionDate())
            .isEqualTo(directDebitRequestV2.getTransactionDate());
        Assertions.assertThat(capturedDetails.getMessageDate()).isEqualTo(directDebitRequestV2.getMessageDate());
        Assertions.assertThat(capturedDetails.getTransactionId()).isEqualTo(directDebitRequestV2.getTransactionId());
        Assertions.assertThat(capturedDetails.getTransactionReference()).isEqualTo(directDebitRequestV2
            .getTransactionReference());
        Assertions.assertThat(capturedDetails.getTransactionStatus()).isEqualTo(directDebitRequestV2
            .getTransactionStatus());

        Assertions.assertThat(capturedDetails.getCreditorAccountDetails().getAccountNumber())
            .isEqualTo(directDebitRequestV2.getCreditorAccountDetails().getAccountNumber());
        Assertions.assertThat(capturedDetails.getCreditorAccountDetails().getBankId())
            .isEqualTo(directDebitRequestV2.getCreditorAccountDetails().getBankId());

        Assertions.assertThat(capturedDetails.getDebtorAccountDetails().getAccountNumber())
            .isEqualTo(directDebitRequestV2.getDebtorAccountDetails().getAccountNumber());
        Assertions.assertThat(capturedDetails.getDebtorAccountDetails().getBankId())
            .isEqualTo(directDebitRequestV2.getDebtorAccountDetails().getBankId());
    }

    @Test
    public void checkDirectDebit4xxError() throws Exception {
        DirectDebitBacsPaymentV2 directDebitPayment = new DirectDebitBacsPaymentV2();

        mockMvc.perform(post(DIRECT_DEBIT_ENDPOINT)
            .content(objectMapString.writeValueAsString(directDebitPayment))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is4xxClientError());
    }

    @Test
    public void directDebit5xxError() throws Exception {
        DirectDebitBacsPaymentRequestV2 directDebitRequestV2 = createDirectDebitRequestV2();

        DirectDebitBacsPaymentV2 directDebitBacsPaymentV2 = ApiDomainDirectDebitPaymentMapperV2.MAPPER
            .toDirectDebitPayment(directDebitRequestV2);

        doThrow(new DirectDebitTransactionMonitoringExceptionV2(
            DirectDebitTransactionMonitoringExceptionV2.Error.GENERAL_SERVICE_ERROR, "mocked exception"))
            .when(directDebitFinCrimeCheckServiceV2)
            .checkFinCrimeV2(directDebitBacsPaymentV2);
        mockMvc.perform(post(DIRECT_DEBIT_ENDPOINT)
            .content(objectMapString.writeValueAsString(directDebitRequestV2))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is5xxServerError());

        Mockito.verify(paymentMetrics, VerificationModeFactory.times(1))
            .incrementCounterTag(PaymentMetrics.PAYMENTS_RECEIVED, PaymentMetricsType.DIRECT_DEBIT.toString());

    }

    private DirectDebitBacsPaymentRequestV2 createDirectDebitRequestV2() {
        return new DirectDebitBacsPaymentRequestV2(
            new AccountDetailsDirectDebitRequestV2("1234 ", "5678"),
            "CreditorName",
            new AccountDetailsDirectDebitRequestV2("1234", "5678"),
            "DebitorName",
            new PaymentAmountRequestDirectDebitV2("USD", 1.00, "Euro", 2.00),
            "PartyKey",
            "TransactionId",
            new Date(),
            new Date(),
            "TransactionStatus",
            "TransactionReference"
        );

    }

}
