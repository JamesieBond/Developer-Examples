package com.tenx.fraudamlmanager.pact.consumer.v3;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.model.RequestResponsePact;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenx.fraudamlmanager.SpringBootTestBase;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.paymentsv3.direct.debit.infrastructure.tma.DirectDebitPaymentV3TMARequest;
import com.tenx.fraudamlmanager.paymentsv3.domain.AccountDetailsV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.BalanceBeforeV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudAMLSanctionsCheckResponseCodeV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.PaymentAmountV3;
import com.tenx.fraudamlmanager.paymentsv3.infrastructure.tma.FraudAMLSanctionsCheckResponseV3;
import com.tenx.fraudamlmanager.paymentsv3.infrastructure.tma.TransactionMonitoringClientV3;
import java.io.IOException;
import java.util.Date;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(PactConsumerTestExt.class)
public class DirectDebitV3ConsumerTest extends SpringBootTestBase {

    private static final String ENDPOINT = "/v3/payments/directDebitFinCrimeCheck";
    private static final String GIVEN_FS_CLIENT = "That FeatureSpace client exists for V3 Payments";
    private static final String GIVEN_INTERNAL_SERVER_ERROR = "That Internal Server Error Occurs";
    private static final String DirectDebit_200 = "V3 DirectDebit: Status 200 request";
    private static final String DirectDebit_200_RF = "V3 DirectDebit: Status 200 request with Required Fields";
    private static final String DirectDebit_400 = "V3 DirectDebit: Status 400 request";
    private static final String DirectDebit_500 = "V3 DirectDebit: Status 500 request";
    private static DirectDebitPaymentV3TMARequest directDebitPaymentV3;
    private static DirectDebitPaymentV3TMARequest directDebitPaymentBadRequestV3;
    private static DirectDebitPaymentV3TMARequest directDebitPaymentRequiredFieldsV3;

    @Autowired
    private ObjectMapper objectMapString;
    @Autowired
    private TransactionMonitoringClientV3 transactionMonitoringClientV3;

    @BeforeAll
    public static void init() {

        directDebitPaymentV3 =
                new DirectDebitPaymentV3TMARequest(
                        new AccountDetailsV3("1234 ", "5678"),
                        "test",
                        new AccountDetailsV3("1234", "5678"),
                        "test",
                        new PaymentAmountV3("test", 1.00, "test", 2.00),
                        "test",
                        "test",
                        new Date(),
                        new Date(),
                        new BalanceBeforeV3("Test", 500, "Test", 500),
                        "test",
                        "test");

        directDebitPaymentRequiredFieldsV3 =
                new DirectDebitPaymentV3TMARequest(
                        new AccountDetailsV3("1234 ", "5678"),
                        "test",
                        new AccountDetailsV3("1234", "5678"),
                        "test",
                        new PaymentAmountV3("test", 1.00, "test", 2.00),
                        null,
                        "test",
                        new Date(),
                        new Date(),
                        new BalanceBeforeV3("Test", 500, "Test", 500),
                        null,
                        null);

        directDebitPaymentBadRequestV3 =
                new DirectDebitPaymentV3TMARequest(
                        new AccountDetailsV3("1234 ", "5678"),
                        null,
                        new AccountDetailsV3("1234", "5678"),
                        "test",
                        new PaymentAmountV3("test", 1.00, "test", 2.00),
                        "test",
                        "test",
                        new Date(),
                        new Date(),
                        new BalanceBeforeV3("Test", 500, "Test", 500),
                        "test",
                        "test");
    }

    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseOk(PactDslWithProvider builder) throws JsonProcessingException {
        return builder
                .given(GIVEN_FS_CLIENT)
                .uponReceiving(DirectDebit_200)
                .path(ENDPOINT)
                .method("POST")
                .body(objectMapString.writeValueAsString(directDebitPaymentV3))
                .willRespondWith()
                .status(200)
                .body(new PactDslJsonBody()
                        .stringType("status", "passed")
                        .eachLike("externalCases"))
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "ResponseOk", port = "1234")
    void testResponseOk(MockServer mockServer) throws TransactionMonitoringException {
        FraudAMLSanctionsCheckResponseV3 fraudAMLSanctionsCheckResponseV3 = transactionMonitoringClientV3
                .checkFinCrimeV3(directDebitPaymentV3);
        assertThat(fraudAMLSanctionsCheckResponseV3.getStatus(), equalTo(FraudAMLSanctionsCheckResponseCodeV3.passed));
        assertThat(fraudAMLSanctionsCheckResponseV3.getExternalCases(), is(not(empty())));
    }

    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseOkRequiredFields(PactDslWithProvider builder) throws JsonProcessingException {
        return builder
                .given(GIVEN_FS_CLIENT)
                .uponReceiving(DirectDebit_200_RF)
                .path(ENDPOINT)
                .method("POST")
                .body(objectMapString.writeValueAsString(directDebitPaymentRequiredFieldsV3))
                .willRespondWith()
                .status(200)
                .body(new PactDslJsonBody()
                        .stringType("status", "passed")
                        .eachLike("externalCases"))
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "ResponseOkRequiredFields", port = "1234")
    void testResponseOkRequiredFields(MockServer mockServer) throws TransactionMonitoringException {
        FraudAMLSanctionsCheckResponseV3 fraudAMLSanctionsCheckResponseV3 = transactionMonitoringClientV3
                .checkFinCrimeV3(directDebitPaymentRequiredFieldsV3);
        assertThat(fraudAMLSanctionsCheckResponseV3.getStatus(), equalTo(FraudAMLSanctionsCheckResponseCodeV3.passed));
        assertThat(fraudAMLSanctionsCheckResponseV3.getExternalCases(), is(not(empty())));
    }

    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseBadRequest(PactDslWithProvider builder) throws JsonProcessingException {
        return builder
                .uponReceiving(DirectDebit_400)
                .path(ENDPOINT)
                .method("POST")
                .body(objectMapString.writeValueAsString(directDebitPaymentBadRequestV3))
                .willRespondWith()
                .status(400)
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "ResponseBadRequest", port = "1234")
    void testResponseBadRequest(MockServer mockServer) throws IOException {
        TransactionMonitoringException transactionMonitoringException = assertThrows(
                TransactionMonitoringException.class, () ->
                        transactionMonitoringClientV3.checkFinCrimeV3(directDebitPaymentBadRequestV3));
        assertEquals(transactionMonitoringException.getErrorDetails().getHttpStatusCode(), 400);

    }

    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseInternalServerError(PactDslWithProvider builder) throws JsonProcessingException {
        return builder
                .given(GIVEN_INTERNAL_SERVER_ERROR)
                .uponReceiving(DirectDebit_500)
                .path(ENDPOINT)
                .method("POST")
                .body(objectMapString.writeValueAsString(directDebitPaymentV3))
                .willRespondWith()
                .status(500)
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "ResponseInternalServerError", port = "1234")
    void testResponseInternalServerError(MockServer mockServer) throws IOException {
        TransactionMonitoringException transactionMonitoringException = assertThrows(
                TransactionMonitoringException.class, () ->
                        transactionMonitoringClientV3.checkFinCrimeV3(directDebitPaymentV3));
        assertEquals(transactionMonitoringException.getErrorDetails().getHttpStatusCode(), 500);

    }

}
