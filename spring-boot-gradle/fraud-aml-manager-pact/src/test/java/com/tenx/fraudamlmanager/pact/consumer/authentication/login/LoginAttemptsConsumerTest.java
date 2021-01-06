package com.tenx.fraudamlmanager.pact.consumer.authentication.login;

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
import com.tenx.fraudamlmanager.authentication.loginattempts.infrastructure.AuthMethod;
import com.tenx.fraudamlmanager.authentication.loginattempts.infrastructure.AuthOutcome;
import com.tenx.fraudamlmanager.authentication.loginattempts.infrastructure.LoginAttempts;
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
public class LoginAttemptsConsumerTest extends SpringBootTestBase {

    private static final String STATE_LOGIN_EXISTS = "That FeatureSpace client exists for Login Attempts";
    private static final String STATE_LOGIN_400 = "A BAD REQUEST Error occurs for Login Attempts";
    private static final String STATE_LOGIN_500 = "That Internal Server Error Occurs for Login Attempts";
    private static final String ENDPOINT = "/v1/authentication/login";
    private static final String LOGINATTEMPTS_200 = "Login Attempts: Status 200 request";
    private static final String LOGINATTEMPTS_200_RF = "Login Attempts: Status 200 request with Required Fields";
    private static final String LOGINATTEMPTS_400 = "Login Attempts: Status 400 request";
    private static final String LOGINATTEMPTS_500 = "Login Attempts: Status 500 request";
    private static LoginAttempts loginAttempts = new LoginAttempts();
    private static LoginAttempts loginAttemptsBadRequest = new LoginAttempts();
    private static LoginAttempts loginAttemptsRequiredFields;
    private Map<String, String> headers = MapUtils
        .putAll(new HashMap<>(), new String[]{"Content-Type", "application/json"});

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TransactionMonitoringClient transactionMonitoringClient;

    @BeforeAll
    public static void init() {
        loginAttempts =
            new LoginAttempts(
                "partyKey", AuthOutcome.SUCCESS,
                AuthMethod.PASSCODE, "failureReason");

        loginAttemptsBadRequest =
            loginAttempts =
                new LoginAttempts(
                    "", AuthOutcome.SUCCESS,
                    AuthMethod.PASSCODE, "failureReason");

        loginAttemptsRequiredFields =
            loginAttempts =
                new LoginAttempts(
                    "PartyKey", AuthOutcome.SUCCESS,
                    AuthMethod.PASSCODE, null);
    }

    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseOk(PactDslWithProvider builder)
        throws JsonProcessingException {
        return builder
            .given(STATE_LOGIN_EXISTS)
            .uponReceiving(LOGINATTEMPTS_200)
            .path(ENDPOINT)
            .method("POST")
            .body(objectMapper.writeValueAsString(loginAttempts))
            .willRespondWith()
            .status(200)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "ResponseOk", port = "1234")
    void testResponseOk(MockServer mockServer) throws IOException, TransactionMonitoringException {
        transactionMonitoringClient.sendLoginAttemptsEvent(loginAttempts);
    }

    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseOkRequiredFields(PactDslWithProvider builder)
        throws JsonProcessingException {
        return builder
            .given(STATE_LOGIN_EXISTS)
            .uponReceiving(LOGINATTEMPTS_200_RF)
            .path(ENDPOINT)
            .method("POST")
            .body(objectMapper.writeValueAsString(loginAttemptsRequiredFields))
            .willRespondWith()
            .status(200)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "ResponseOkRequiredFields", port = "1234")
    void testResponseOkRequiredFields(MockServer mockServer) throws IOException, TransactionMonitoringException {
        transactionMonitoringClient.sendLoginAttemptsEvent(loginAttemptsRequiredFields);
    }

    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseBadRequest(PactDslWithProvider builder)
        throws JsonProcessingException {
        return builder
            .given(STATE_LOGIN_400)
            .uponReceiving(LOGINATTEMPTS_400)
            .path(ENDPOINT)
            .method("POST")
            .body(objectMapper.writeValueAsString(loginAttemptsBadRequest))
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
                    transactionMonitoringClient.sendLoginAttemptsEvent(loginAttemptsBadRequest));
        assertEquals(400, 400);
    }

    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseInternalServerError(PactDslWithProvider builder)
        throws JsonProcessingException {
        return builder
            .given(STATE_LOGIN_500)
            .uponReceiving(LOGINATTEMPTS_500)
            .path(ENDPOINT)
            .method("POST")
            .body(objectMapper.writeValueAsString(loginAttempts))
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
                () -> transactionMonitoringClient.sendLoginAttemptsEvent(loginAttempts));
    }
}
