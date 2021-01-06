package com.tenx.fraudamlmanager.pact.consumer.v3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.model.RequestResponsePact;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenx.fraudamlmanager.SpringBootTestBase;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.paymentsv3.direct.credit.infrastructure.tma.DirectCreditPaymentV3TMARequest;
import com.tenx.fraudamlmanager.paymentsv3.domain.AccountDetailsV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.BalanceBeforeV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.PaymentAmountV3;
import com.tenx.fraudamlmanager.paymentsv3.infrastructure.tma.TransactionMonitoringClientV3;
import java.io.IOException;
import java.util.Date;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(PactConsumerTestExt.class)
public class DirectCreditConsumerTestV3 extends SpringBootTestBase {

    private static final String ENDPOINT = "/v3/payments/directCreditFinCrimeCheck";

    private static final String STATE_V3_200 = "That FeatureSpace client exists for V3 Payments";
    private static final String STATE_V3_400 = "A BAD REQUEST Error occurs for a V3 payment";
    private static final String STATE_500 = "That Internal Server Error Occurs";
    private static final String DC_200 = "V3 DirectCredit: Status 200 request";
    private static final String DC_200_RF = "V3 DirectCredit: Status 200 request with Required Fields";
    private static final String DC_400 = "V3 DirectCredit: Status 400 request";
    private static final String DC_500 = "V3 DirectCredit: Status 500 request";
    private static DirectCreditPaymentV3TMARequest directCreditPaymentV3;
    private static DirectCreditPaymentV3TMARequest directCreditPaymentBadRequestV3;
    private static DirectCreditPaymentV3TMARequest directCreditPaymentRequiredFieldsV3;

    @Autowired
    private ObjectMapper objectMapString;

    @Autowired
    private TransactionMonitoringClientV3 transactionMonitoringClientV3;

    @BeforeAll
    public static void init() {

        directCreditPaymentV3 = new DirectCreditPaymentV3TMARequest(new AccountDetailsV3("1234", "5678"),
                "test",
                new AccountDetailsV3("1234", "5678"),
                "test",
                new PaymentAmountV3("test", 1.00, "test", 2.00),
                "testTesttest",
                "test",
                new Date(), new Date(),
                new BalanceBeforeV3("test", 200, "test", 200),
                "test",
                "test"
        );

        directCreditPaymentRequiredFieldsV3 = new DirectCreditPaymentV3TMARequest(new AccountDetailsV3("1234", "5678"),
                "test",
                new AccountDetailsV3("1234", "5678"),
                "test",
                new PaymentAmountV3("test ", 1.00, "test", 2.00),
                null,
                "test",
                new Date(), new Date(),
                new BalanceBeforeV3("test", 200, "test", 200),
                null,
                null);

        directCreditPaymentBadRequestV3 = new DirectCreditPaymentV3TMARequest(new AccountDetailsV3("1234", "5678"),
                "test",
                new AccountDetailsV3("1234", "5678"),
                "test",
                null,
                null,
                null,
                new Date(), new Date(),
                null,
                "test",
                "test");

    }

    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseOk(PactDslWithProvider builder) throws JsonProcessingException {

        return builder
            .given(STATE_V3_200)
            .uponReceiving(DC_200)
            .path(ENDPOINT)
            .method("POST")
            .body(objectMapString.writeValueAsString(directCreditPaymentV3))
            .willRespondWith()
            .status(200)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "ResponseOk", port = "1234")
    void testResponseOk(MockServer mockServer) throws TransactionMonitoringException {
        transactionMonitoringClientV3.checkFinCrimeV3(directCreditPaymentV3);
    }


    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseOkRequiredFields(PactDslWithProvider builder) throws JsonProcessingException {

        return builder
            .given(STATE_V3_200)
            .uponReceiving(DC_200_RF)
            .path(ENDPOINT)
            .method("POST")
            .body(objectMapString.writeValueAsString(directCreditPaymentRequiredFieldsV3))
            .willRespondWith()
            .status(200)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "ResponseOkRequiredFields", port = "1234")
    void testResponseOkRequiredFields(MockServer mockServer) throws TransactionMonitoringException {
        transactionMonitoringClientV3.checkFinCrimeV3(directCreditPaymentRequiredFieldsV3);
    }


    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseBadRequest(PactDslWithProvider builder) throws JsonProcessingException {
        return builder
            .given(STATE_V3_400)
            .uponReceiving(DC_400)
            .path(ENDPOINT)
            .method("POST")
            .body(objectMapString.writeValueAsString(directCreditPaymentBadRequestV3))
            .willRespondWith()
            .status(400)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "ResponseBadRequest", port = "1234")
    void testResponseBadRequest(MockServer mockServer) throws IOException {
        TransactionMonitoringException transactionMonitoringException = assertThrows(
            TransactionMonitoringException.class, () ->
                transactionMonitoringClientV3.checkFinCrimeV3(directCreditPaymentBadRequestV3));
        assertEquals(transactionMonitoringException.getErrorDetails().getHttpStatusCode(), 400);

    }

    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseInternalServerError(PactDslWithProvider builder) throws JsonProcessingException {
        return builder
            .given(STATE_500)
            .uponReceiving(DC_500)
            .path(ENDPOINT)
            .method("POST")
            .body(objectMapString.writeValueAsString(directCreditPaymentV3))
            .willRespondWith()
            .status(500)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "ResponseInternalServerError", port = "1234")
    void testResponseInternalServerError(MockServer mockServer) throws IOException {
        TransactionMonitoringException transactionMonitoringException = assertThrows(
            TransactionMonitoringException.class, () ->
                transactionMonitoringClientV3.checkFinCrimeV3(directCreditPaymentV3));
        assertEquals(transactionMonitoringException.getErrorDetails().getHttpStatusCode(), 500);

    }

}