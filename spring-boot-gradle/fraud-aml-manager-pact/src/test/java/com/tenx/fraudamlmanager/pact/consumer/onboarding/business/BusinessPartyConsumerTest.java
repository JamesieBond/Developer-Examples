package com.tenx.fraudamlmanager.pact.consumer.onboarding.business;

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
import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.TransactionMonitoringClient;
import com.tenx.fraudamlmanager.onboarding.business.domain.Address;
import com.tenx.fraudamlmanager.onboarding.business.domain.BusinessPartyDetails;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenxbanking.party.event.business.PartyStatus;
import java.io.IOException;
import java.util.ArrayList;
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
public class BusinessPartyConsumerTest extends SpringBootTestBase {

    private static final String STATE_BUSINESS_EXISTS = "That FeatureSpace client exists for V1 Business Party";
    private static final String STATE_BUSINESS_400 = "A BAD REQUEST Error occurs for BUSINESS party event";
    private static final String STATE_BUSINESS_500 = "That Internal Server Error Occurs for Business Party Events";
    private static final String ENDPOINT = "/v1/parties/businessParty";
    private static final String BUSINESSPARTY_200 = "V1 BusinessParty: Status 200 request";
    private static final String BUSINESSPARTY_200_RF = "V1 BusinessParty: Status 200 request with Required Fields";
    private static final String BUSINESSPARTY_400 = "V1 BusinessParty: Status 400 request";
    private static final String BUSINESSPARTY_500 = "V1 BusinessParty: Status 500 request";
    private static BusinessPartyDetails businessPartyDetails = new BusinessPartyDetails();
    private static BusinessPartyDetails businessPartyDetailsBadRequest = new BusinessPartyDetails();
    private static BusinessPartyDetails businessPartyDetailsRequiredFields;
    private Map<String, String> headers = MapUtils
        .putAll(new HashMap<>(), new String[]{"Content-Type", "application/json"});

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TransactionMonitoringClient transactionMonitoringClient;

    @BeforeAll
    public static void init() {
        businessPartyDetails =
            new BusinessPartyDetails(
                "partyKey",
                "provisioned",
                new ArrayList<>(),
                new Address(),
                "companyName",
                "tradingName",
                "registrationNumber",
                PartyStatus.PROVISIONED.name());

        businessPartyDetailsBadRequest =
            new BusinessPartyDetails(
                "",
                "",
                new ArrayList<Address>(),
                new Address(),
                "companyName",
                "tradingName",
                "registrationNumber",
                PartyStatus.PROVISIONED.name());

        businessPartyDetailsRequiredFields =
            new BusinessPartyDetails(
                "partyKey",
                "CustomerModified",
                new ArrayList<Address>(),
                new Address(),
                "companyName",
                "tradingName",
                "registrationNumber",
                PartyStatus.PROVISIONED.name());
    }

    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseOk(PactDslWithProvider builder)
        throws JsonProcessingException {
        return builder
            .given(STATE_BUSINESS_EXISTS)
            .uponReceiving(BUSINESSPARTY_200)
            .path(ENDPOINT)
            .method("POST")
            .body(objectMapper.writeValueAsString(businessPartyDetails))
            .willRespondWith()
            .status(200)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "ResponseOk", port = "1234")
    void testResponseOk(MockServer mockServer) throws IOException, TransactionMonitoringException {
        transactionMonitoringClient.sendBusinessPartyEvent(businessPartyDetails);
    }

    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseOkRequiredFields(PactDslWithProvider builder)
        throws JsonProcessingException {
        return builder
            .given(STATE_BUSINESS_EXISTS)
            .uponReceiving(BUSINESSPARTY_200_RF)
            .path(ENDPOINT)
            .method("POST")
            .body(objectMapper.writeValueAsString(businessPartyDetailsRequiredFields))
            .willRespondWith()
            .status(200)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "ResponseOkRequiredFields", port = "1234")
    void testResponseOkRequiredFields(MockServer mockServer) throws IOException, TransactionMonitoringException {
        transactionMonitoringClient.sendBusinessPartyEvent(businessPartyDetailsRequiredFields);
    }

    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseBadRequest(PactDslWithProvider builder)
        throws JsonProcessingException {
        return builder
            .given(STATE_BUSINESS_400)
            .uponReceiving(BUSINESSPARTY_400)
            .path(ENDPOINT)
            .method("POST")
            .body(objectMapper.writeValueAsString(businessPartyDetailsBadRequest))
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
                    transactionMonitoringClient.sendBusinessPartyEvent(businessPartyDetailsBadRequest));
        assertEquals(400, 400);
    }

    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseInternalServerError(PactDslWithProvider builder)
        throws JsonProcessingException {
        return builder
            .given(STATE_BUSINESS_500)
            .uponReceiving(BUSINESSPARTY_500)
            .path(ENDPOINT)
            .method("POST")
            .body(objectMapper.writeValueAsString(businessPartyDetails))
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
                () -> transactionMonitoringClient.sendBusinessPartyEvent(businessPartyDetails));
    }
}
