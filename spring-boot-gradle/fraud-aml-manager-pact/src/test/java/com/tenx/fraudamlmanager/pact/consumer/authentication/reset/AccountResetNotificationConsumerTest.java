package com.tenx.fraudamlmanager.pact.consumer.authentication.reset;

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
import com.tenx.fraudamlmanager.authentication.reset.infrastructure.AuthReset;
import com.tenx.fraudamlmanager.authentication.reset.infrastructure.IdentityAccountReset;
import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.TransactionMonitoringClient;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections4.MapUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Niall O'Connell
 */
@ExtendWith(PactConsumerTestExt.class)
public class AccountResetNotificationConsumerTest extends SpringBootTestBase {


    private static final String STATE_RESTRICTION_EXISTS = "FeatureSpace client exists for RESTRICTION event";
    private static final String STATE_RESTRICTION_400 = "BAD REQUEST Error occurs for RESTRICTION event";
    private static final String STATE_RESTRICTION_500 = "Internal Server Error occurs for RESTRICTION event";
    private static final String ENDPOINT = "/v1/authentication/reset";
    private static final String RESTRICTION_200 = "V1 RESTRICTION: Status 200 request";
    private static final String RESTRICTION_200_RF = "V1 RESTRICTION: Status 200 request with Required Fields";
    private static final String RESTRICTION_400 = "V1 RESTRICTION: Status 400 request";
    private static final String RESTRICTION_500 = "V1 RESTRICTION: Status 500 request";
    private static AuthReset authReset = new AuthReset();
    private static AuthReset authResetBadRequest = new AuthReset();
    private static AuthReset authResetRequiredFields;

    private Map<String, String> headers = MapUtils
        .putAll(new HashMap<>(), new String[]{"Content-Type", "application/json"});

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TransactionMonitoringClient transactionMonitoringClient;

    @BeforeAll
    public static void init() {
        authReset =
            new AuthReset(
                "partyKey",
                IdentityAccountReset.PASSED);

        authResetBadRequest =
            new AuthReset(null,
                IdentityAccountReset.PASSED);

        authResetRequiredFields =
            new AuthReset(
                "partyKey", IdentityAccountReset.PASSED);
    }


    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseOk(PactDslWithProvider builder)
        throws JsonProcessingException {
        return builder
            .given(STATE_RESTRICTION_EXISTS)
            .uponReceiving(RESTRICTION_200)
            .path(ENDPOINT)
            .method("POST")
            .body(objectMapper.writeValueAsString(authReset))
            .willRespondWith()
            .status(200)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "ResponseOk", port = "1234")
    void testResponseOk(MockServer mockServer) throws Exception {
        transactionMonitoringClient.sendIdentityAccountResetEvent(authReset);
    }

    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseOkRequiredFields(PactDslWithProvider builder)
        throws JsonProcessingException {
        return builder
            .given(STATE_RESTRICTION_EXISTS)
            .uponReceiving(RESTRICTION_200_RF)
            .path(ENDPOINT)
            .method("POST")
            .body(objectMapper.writeValueAsString(authResetRequiredFields))
            .willRespondWith()
            .status(200)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "ResponseOkRequiredFields", port = "1234")
    void testResponseOkRequiredFields(MockServer mockServer) throws Exception {
        transactionMonitoringClient.sendIdentityAccountResetEvent(authResetRequiredFields);
    }

    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseBadRequest(PactDslWithProvider builder)
        throws JsonProcessingException {
        return builder
            .given(STATE_RESTRICTION_400)
            .uponReceiving(RESTRICTION_400)
            .path(ENDPOINT)
            .method("POST")
            .body(objectMapper.writeValueAsString(authResetBadRequest))
            .willRespondWith() /**/
            .status(400)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "ResponseBadRequest", port = "1234")
    void testResponseBadRequest(MockServer mockServer) throws IOException {
        TransactionMonitoringException transactionMonitoringException =
            assertThrows(
                TransactionMonitoringException.class,
                () ->
                    transactionMonitoringClient.sendIdentityAccountResetEvent(
                        authResetBadRequest));
        assertEquals(transactionMonitoringException.getErrorDetails().getHttpStatusCode(), 400);
    }

    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseInternalServerError(PactDslWithProvider builder)
        throws JsonProcessingException {
        return builder
            .given(STATE_RESTRICTION_500)
            .uponReceiving(RESTRICTION_500)
            .path(ENDPOINT)
            .method("POST")
            .body(objectMapper.writeValueAsString(authReset))
            .willRespondWith() /**/
            .status(500)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "ResponseInternalServerError", port = "1234")
    void testResponseInternalServerError(MockServer mockServer) throws IOException {
        TransactionMonitoringException transactionMonitoringException =
            assertThrows(
                TransactionMonitoringException.class,
                () -> transactionMonitoringClient.sendIdentityAccountResetEvent(authReset));
        assertEquals(transactionMonitoringException.getErrorDetails().getHttpStatusCode(), 500);
    }

}
