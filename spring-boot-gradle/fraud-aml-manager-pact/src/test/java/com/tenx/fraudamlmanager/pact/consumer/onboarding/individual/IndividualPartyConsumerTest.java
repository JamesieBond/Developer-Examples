package com.tenx.fraudamlmanager.pact.consumer.onboarding.individual;

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
import com.tenx.fraudamlmanager.onboarding.individual.api.Address;
import com.tenx.fraudamlmanager.onboarding.individual.api.IndividualPartyDetails;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.collections4.MapUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(PactConsumerTestExt.class)
public class IndividualPartyConsumerTest extends SpringBootTestBase {

    private static final String STATE_INDIVIDUAL_EXISTS = "That FeatureSpace client exists for V1 Individual Party";
    private static final String STATE_INDIVIDUAL_400 = "A BAD REQUEST Error occurs for INDIVIDUAL party event";
    private static final String STATE_INDIVIDUAL_500 = "That Internal Server Error Occurs for Individual Party Events";
    private static final String ENDPOINT = "/v1/parties/individualParty/";
    private static final String INDIVIDUALPARTY_200 = "V1 IndividualParty: Status 200 request";
    private static final String INDIVIDUALPARTY_200_RF = "V1 IndividualParty: Status 200 request with Required Fields";
    private static final String INDIVIDUALPARTY_400 = "V1 IndividualParty: Status 400 request";
    private static final String INDIVIDUALPARTY_500 = "V1 IndividualParty: Status 500 request";
    private static IndividualPartyDetails individualPartyDetails = new IndividualPartyDetails();
    private static IndividualPartyDetails individualPartyDetailsBadRequest = new IndividualPartyDetails();
    private static IndividualPartyDetails individualPartyDetailsRequiredFields;
    private Map<String, String> headers = MapUtils
        .putAll(new HashMap<>(), new String[]{"Content-Type", "application/json"});

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TransactionMonitoringClient transactionMonitoringClient;

    @BeforeAll
    public static void init() {
        individualPartyDetails =
            new IndividualPartyDetails(
                UUID.randomUUID().toString(),
                "Provisioned",
                "123124123",
                "test@email.com",
                "Joe",
                "Baggott",
                "Shoehorn",
                19890201,
                LocalDate.now(),
                new ArrayList<>(),
                new Address());

        individualPartyDetailsBadRequest =
            new IndividualPartyDetails(
                null,
                "",
                "123124123",
                "test@email.com",
                "test",
                "",
                "",
                19890201,
                LocalDate.now(),
                new ArrayList<>(),
                new Address());

        individualPartyDetailsRequiredFields =
            new IndividualPartyDetails(
                UUID.randomUUID().toString(),
                "CustomerModified",
                null,
                null,
                null,
                null,
                null,
                0,
                null,
                new ArrayList<Address>(),
                new Address());
    }

    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseOk(PactDslWithProvider builder)
        throws JsonProcessingException {
        return builder
            .given(STATE_INDIVIDUAL_EXISTS)
            .uponReceiving(INDIVIDUALPARTY_200)
            .path(ENDPOINT)
            .method("POST")
            .body(objectMapper.writeValueAsString(individualPartyDetails))
            .willRespondWith()
            .status(200)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "ResponseOk", port = "1234")
    void testResponseOk(MockServer mockServer) throws Exception {
        transactionMonitoringClient.sendIndividualPartyEvent(individualPartyDetails);
    }

    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseOkRequiredFields(PactDslWithProvider builder)
        throws JsonProcessingException {
        return builder
            .given(STATE_INDIVIDUAL_EXISTS)
            .uponReceiving(INDIVIDUALPARTY_200_RF)
            .path(ENDPOINT)
            .method("POST")
            .body(objectMapper.writeValueAsString(individualPartyDetailsRequiredFields))
            .willRespondWith()
            .status(200)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "ResponseOkRequiredFields", port = "1234")
    void testResponseOkRequiredFields(MockServer mockServer) throws Exception {
        transactionMonitoringClient.sendIndividualPartyEvent(individualPartyDetailsRequiredFields);
    }

    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseBadRequest(PactDslWithProvider builder)
        throws JsonProcessingException {
        return builder
            .given(STATE_INDIVIDUAL_400)
            .uponReceiving(INDIVIDUALPARTY_400)
            .path(ENDPOINT)
            .method("POST")
            .body(objectMapper.writeValueAsString(individualPartyDetailsBadRequest))
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
                    transactionMonitoringClient.sendIndividualPartyEvent(
                        individualPartyDetailsBadRequest));
        assertEquals(transactionMonitoringException.getErrorDetails().getHttpStatusCode(), 400);
    }

    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseInternalServerError(PactDslWithProvider builder)
        throws JsonProcessingException {
        return builder
            .given(STATE_INDIVIDUAL_500)
            .uponReceiving(INDIVIDUALPARTY_500)
            .path(ENDPOINT)
            .method("POST")
            .body(objectMapper.writeValueAsString(individualPartyDetails))
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
                () -> transactionMonitoringClient.sendIndividualPartyEvent(individualPartyDetails));
        assertEquals(transactionMonitoringException.getErrorDetails().getHttpStatusCode(), 500);
    }
}
