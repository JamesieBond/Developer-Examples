package com.tenx.fraudamlmanager.pact.consumer.registration;

import static org.junit.Assert.assertEquals;
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
import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.TransactionMonitoringClient;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.registration.infrastructure.RegistrationDetails;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections4.MapUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(PactConsumerTestExt.class)
public class CustomerRegistrationConsumerTest extends SpringBootTestBase {

    private static final String STATE_CUSTOMER_REGISTRATION_EXISTS = "That FeatureSpace client exists for V1 Customer Registration";
    private static final String STATE_CUSTOMER_REGISTRATION_400 = "A BAD REQUEST Error occurs for CUSTOMER party event";
    private static final String STATE_CUSTOMER_REGISTRATION_500 = "That Internal Server Error Occurs for CustomerRegistration Events";
    private static final String ENDPOINT = "/v1/registration/";
    private static final String CUSTOMERREGISTRATION_200 = "V1 CustomerRegistration: Status 200 request";
    private static final String CUSTOMERREGISTRATION_200_RF = "V1 CustomerRegistration: Status 200 request with Required Fields";
    private static final String CUSTOMERREGISTRATION_400 = "V1 CustomerRegistration: Status 400 request";
    private static final String CUSTOMERREGISTRATION_500 = "V1 CustomerRegistration: Status 500 request";
    private static RegistrationDetails registrationDetails = new RegistrationDetails();
    private static RegistrationDetails registrationDetailsBadRequest = new RegistrationDetails();
    private static RegistrationDetails registrationDetailsRequiredFields;
    private Map<String, String> headers = MapUtils
        .putAll(new HashMap<>(), new String[]{"Content-Type", "application/json"});

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TransactionMonitoringClient transactionMonitoringClient;

    @BeforeAll
    public static void init() {
        registrationDetails =
            new RegistrationDetails(
                "partyKey",
                true,
                true);

        registrationDetailsBadRequest =
            new RegistrationDetails(
                null,
                true,
                true);

        registrationDetailsRequiredFields =
            new RegistrationDetails(
                "PartyKey",
                true,
                true
            );
    }

    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseOk(PactDslWithProvider builder)
        throws JsonProcessingException {
        return builder
            .given(STATE_CUSTOMER_REGISTRATION_EXISTS)
            .uponReceiving(CUSTOMERREGISTRATION_200)
            .path(ENDPOINT)
            .method("POST")
            .body(objectMapper.writeValueAsString(registrationDetails))
            .willRespondWith()
            .status(200)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "ResponseOk", port = "1234")
    void testResponseOk(MockServer mockServer) throws Exception {
        transactionMonitoringClient.sendCustomerRegistrationEvent(registrationDetails);
    }

    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseOkRequiredFields(PactDslWithProvider builder)
        throws JsonProcessingException {
        return builder
            .given(STATE_CUSTOMER_REGISTRATION_EXISTS)
            .uponReceiving(CUSTOMERREGISTRATION_200_RF)
            .path(ENDPOINT)
            .method("POST")
            .body(objectMapper.writeValueAsString(registrationDetailsRequiredFields))
            .willRespondWith()
            .status(200)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "ResponseOkRequiredFields", port = "1234")
    void testResponseOkRequiredFields(MockServer mockServer) throws Exception {
        transactionMonitoringClient.sendCustomerRegistrationEvent(registrationDetailsRequiredFields);
    }

    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseBadRequest(PactDslWithProvider builder)
        throws JsonProcessingException {
        return builder
            .given(STATE_CUSTOMER_REGISTRATION_400)
            .uponReceiving(CUSTOMERREGISTRATION_400)
            .path(ENDPOINT)
            .method("POST")
            .body(objectMapper.writeValueAsString(registrationDetailsBadRequest))
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
                    transactionMonitoringClient.sendCustomerRegistrationEvent(
                        registrationDetailsBadRequest));
        assertEquals(transactionMonitoringException.getErrorDetails().getHttpStatusCode(), 400);
    }

    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseInternalServerError(PactDslWithProvider builder)
        throws JsonProcessingException {
        return builder
            .given(STATE_CUSTOMER_REGISTRATION_500)
            .uponReceiving(CUSTOMERREGISTRATION_500)
            .path(ENDPOINT)
            .method("POST")
            .body(objectMapper.writeValueAsString(registrationDetails))
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
                () -> transactionMonitoringClient.sendCustomerRegistrationEvent(registrationDetails));
        assertEquals(transactionMonitoringException.getErrorDetails().getHttpStatusCode(), 500);
    }


}
