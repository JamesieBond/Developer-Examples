package com.tenx.fraudamlmanager.payments.integrationtest;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.tenx.fraudamlmanager.SpringBootTestBase;
import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.TransactionMonitoringClient;
import com.tenx.fraudamlmanager.payments.model.api.BalanceBefore;
import com.tenx.fraudamlmanager.payments.model.api.DirectCreditPayment;
import com.tenx.fraudamlmanager.payments.model.api.DirectDebitPayment;
import com.tenx.fraudamlmanager.payments.model.api.FpsInboundPayment;
import com.tenx.fraudamlmanager.payments.model.api.FpsOutboundPayment;
import com.tenx.fraudamlmanager.payments.model.api.FraudCheckResponse;
import com.tenx.fraudamlmanager.payments.model.api.OnUsPayment;
import com.tenx.fraudamlmanager.payments.model.api.PayAccount;
import com.tenx.fraudamlmanager.payments.model.api.PaymentAmount;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.ExternalCaseDetails;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.FraudAMLSanctionsCheckResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
public class PaymentsIntegrationTest extends SpringBootTestBase {
    private static Stream<Arguments> stubbedURLs() {
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

        FraudAMLSanctionsCheckResponse fraudAMLSanctionsCheckResponse = new FraudAMLSanctionsCheckResponse("testId", "Passed", new ArrayList<ExternalCaseDetails>());

        DirectDebitPayment directDebitPayment = new DirectDebitPayment();
        directDebitPayment.setId("Sample");
        directDebitPayment.setPartyKey("Sample");
        directDebitPayment.setPayee(new PayAccount("Sample", "Sample", "Sample"));
        directDebitPayment.setPayer(new PayAccount("Sample", "Sample", "Sample"));
        directDebitPayment.setPaymentAmount(new PaymentAmount("GBP", 1000.00));
        directDebitPayment.setPaymentReference("Sample");
        directDebitPayment.setPaymentStatusReason("Sample");
        directDebitPayment.setProcessingDate(new Date());
        directDebitPayment.setSubscriptionKey("Sample");

        DirectCreditPayment directCreditPayment = new DirectCreditPayment();
        directCreditPayment.setId("Sample");
        directCreditPayment.setPartyKey("Sample");
        directCreditPayment.setPayee(new PayAccount("Sample", "Sample", "Sample"));
        directCreditPayment.setPayer(new PayAccount("Sample", "Sample", "Sample"));
        directCreditPayment.setPaymentAmount(new PaymentAmount("GBP", 1000.00));
        directCreditPayment.setPaymentReference("Sample");
        directCreditPayment.setPaymentStatusReason("Sample");
        directCreditPayment.setProcessingDate(new Date());
        directCreditPayment.setSubscriptionKey("Sample");

        FraudCheckResponse fraudCheckResponse = new FraudCheckResponse(true, "Sample");

        return Stream.of(
                Arguments.of("/v1/payments/domesticPaymentOutboundFinCrimeCheck", "/v1/payments/fpsOutbound/finCrimeCheck", fpsOutboundPayment, fraudAMLSanctionsCheckResponse),
                Arguments.of("/v1/payments/domesticPaymentInboundFinCrimeCheck", "/v1/payments/fpsInbound/finCrimeCheck", fpsInboundPayment, fraudAMLSanctionsCheckResponse),
                Arguments.of("/v1/payments/onUsFinCrimeCheck", "/v1/payments/onUs/finCrimeCheck", onUsPayment, fraudAMLSanctionsCheckResponse),
                Arguments.of("/v1/payments/directDebitFinCrimeCheck", "/v1/payments/directDebit/finCrimeCheck", directDebitPayment, fraudAMLSanctionsCheckResponse),
                Arguments.of("/v1/payments/directCreditFinCrimeCheck", "/v1/payments/directCredit/finCrimeCheck", directCreditPayment, fraudAMLSanctionsCheckResponse)
        );
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private TransactionMonitoringClient transactionMonitoringClient;

    private static WireMockServer wireMockServer;

    @BeforeAll
    public static void beforeEach() {
        setupWiremock();
    }

    @AfterAll
    public static void teardown() {
        wireMockServer.stop();
    }

    private static void setupWiremock() {
        wireMockServer = new WireMockServer(wireMockConfig().port(1234));
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
    }

    private void stubTMAService(String stubUrl, Object fraudAMLSanctionsCheckResponse) throws JsonProcessingException {
        stubFor(post(urlEqualTo(stubUrl))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(fraudAMLSanctionsCheckResponse))));
    }

    @ParameterizedTest
    @MethodSource("stubbedURLs")
    public void FPSpaymentsE2EIntegrationTest(String newStubbedURL, String stubbedURL, Object payload, Object fraudAMLSanctionsCheckResponse) throws Exception {
        stubTMAService(newStubbedURL, fraudAMLSanctionsCheckResponse);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(stubbedURL)
                .content(objectMapper.writeValueAsString(payload))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath(".clear", hasItem(true)));
    }

}
