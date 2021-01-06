package com.tenx.fraudamlmanager.pact.consumer.authentication.stepup;

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
import com.tenx.fraudamlmanager.authentication.stepup.infrastructure.StepUpAuthMethod;
import com.tenx.fraudamlmanager.authentication.stepup.infrastructure.StepUpAuthOutcome;
import com.tenx.fraudamlmanager.authentication.stepup.infrastructure.StepUpPayload;
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
public class StepUpConsumerTest extends SpringBootTestBase {

    private static final String STATE_STEPUP_EXISTS = "That FeatureSpace client exists for StepUp";
    private static final String STATE_STEPUP_400 = "A BAD REQUEST Error occurs for StepUp";
    private static final String STATE_STEPUP_500 = "That Internal Server Error Occurs for StepUp";
    private static final String ENDPOINT = "/v1/authentication/stepUp";
    private static final String STEPUP_200 = "StepUp: Status 200 request";
    private static final String STEPUP_200_RF = "StepUp: Status 200 request with Required Fields";
    private static final String STEPUP_400 = "StepUp: Status 400 request";
    private static final String STEPUP_500 = "StepUp: Status 500 request";
    private static StepUpPayload stepUpDetails = new StepUpPayload();
    private static StepUpPayload stepUpDetailsBadRequest = new StepUpPayload();
    private static StepUpPayload stepUpDetailsRequiredFields;
    private Map<String, String> headers = MapUtils
        .putAll(new HashMap<>(), new String[]{"Content-Type", "application/json"});

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TransactionMonitoringClient transactionMonitoringClient;

    @BeforeAll
    public static void init() {
        stepUpDetails =
            new StepUpPayload(
                "partyKey", StepUpAuthOutcome.STEPUP_SUCCESS,
                StepUpAuthMethod.PASSCODE, "failureReason");

        stepUpDetailsBadRequest =
            stepUpDetails =
                new StepUpPayload(
                    "", StepUpAuthOutcome.STEPUP_SUCCESS,
                    StepUpAuthMethod.PASSCODE, "failureReason");

        stepUpDetailsRequiredFields =
            stepUpDetails =
                new StepUpPayload(
                    "partyKey", StepUpAuthOutcome.STEPUP_SUCCESS,
                    StepUpAuthMethod.PASSCODE, null);
    }

    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseOk(PactDslWithProvider builder)
        throws JsonProcessingException {
        return builder
            .given(STATE_STEPUP_EXISTS)
            .uponReceiving(STEPUP_200)
            .path(ENDPOINT)
            .method("POST")
            .body(objectMapper.writeValueAsString(stepUpDetails))
            .willRespondWith()
            .status(200)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "ResponseOk", port = "1234")
    void testResponseOk(MockServer mockServer) throws IOException, TransactionMonitoringException {
        transactionMonitoringClient.sendStepUpEvent(stepUpDetails);
    }

    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseOkRequiredFields(PactDslWithProvider builder)
        throws JsonProcessingException {
        return builder
            .given(STATE_STEPUP_EXISTS)
            .uponReceiving(STEPUP_200_RF)
            .path(ENDPOINT)
            .method("POST")
            .body(objectMapper.writeValueAsString(stepUpDetailsRequiredFields))
            .willRespondWith()
            .status(200)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "ResponseOkRequiredFields", port = "1234")
    void testResponseOkRequiredFields(MockServer mockServer) throws IOException, TransactionMonitoringException {
        transactionMonitoringClient.sendStepUpEvent(stepUpDetailsRequiredFields);
    }

    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseBadRequest(PactDslWithProvider builder)
        throws JsonProcessingException {
        return builder
            .given(STATE_STEPUP_400)
            .uponReceiving(STEPUP_400)
            .path(ENDPOINT)
            .method("POST")
            .body(objectMapper.writeValueAsString(stepUpDetailsBadRequest))
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
                    transactionMonitoringClient.sendStepUpEvent(stepUpDetailsBadRequest));
        assertEquals(400, 400);
    }

    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseInternalServerError(PactDslWithProvider builder)
        throws JsonProcessingException {
        return builder
            .given(STATE_STEPUP_500)
            .uponReceiving(STEPUP_500)
            .path(ENDPOINT)
            .method("POST")
            .body(objectMapper.writeValueAsString(stepUpDetails))
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
                () -> transactionMonitoringClient.sendStepUpEvent(stepUpDetails));
    }
}
