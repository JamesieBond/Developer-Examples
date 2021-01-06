package com.tenx.fraudamlmanager.pact.consumer.beneficiaries.mandates;

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
import com.tenx.fraudamlmanager.beneficiaries.mandates.infrastructure.BeneficiaryAction;
import com.tenx.fraudamlmanager.beneficiaries.mandates.infrastructure.SetupMandates;
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
public class PaymentsNotificationsConsumerTest extends SpringBootTestBase {

    private static final String STATE_MANDATES_EXISTS = "That FeatureSpace client exists for Mandates Payment Notification";
    private static final String STATE_MANDATES_400 = "A BAD REQUEST Error occurs for Mandates Payment Notification";
    private static final String STATE_MANDATES_500 = "That Internal Server Error Occurs for Mandates Payment Notification";
    private static final String ENDPOINT = "/v1/beneficiaries/mandates";
    private static final String MANDATES_200 = "Mandates Payment Notification: Status 200 request";
    private static final String MANDATES_200_RF = "Mandates Payment Notification: Status 200 request with Required Fields";
    private static final String MANDATES_400 = "Mandates Payment Notification: Status 400 request";
    private static final String MANDATES_500 = "Mandates Payment Notification: Status 500 request";
    private static SetupMandates mandatesBeneficiary = new SetupMandates();
    private static SetupMandates mandatesBeneficiaryBadRequest = new SetupMandates();
    private static SetupMandates mandatesBeneficiaryRequiredFields;
    private Map<String, String> headers = MapUtils
        .putAll(new HashMap<>(), new String[]{"Content-Type", "application/json"});

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TransactionMonitoringClient transactionMonitoringClient;

    @BeforeAll
    public static void init() {
        mandatesBeneficiary =
            new SetupMandates(
                "partyKeyTest",
                "bacsDDMandateRef",
                "creditorAccountName",
                "directDebitKey",
                BeneficiaryAction.SETUP
            );

        mandatesBeneficiaryBadRequest =
            new SetupMandates(
                null,
                null,
                null,
                null,
                BeneficiaryAction.SETUP);

        mandatesBeneficiaryRequiredFields =
            new SetupMandates(
                "partyKeyTest",
                "bacsDDMandateRef",
                "creditorAccountName",
                "directDebitKey",
                BeneficiaryAction.SETUP);
    }

    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseOk(PactDslWithProvider builder)
        throws JsonProcessingException {
        return builder
            .given(STATE_MANDATES_EXISTS)
            .uponReceiving(MANDATES_200)
            .path(ENDPOINT)
            .method("POST")
            .body(objectMapper.writeValueAsString(mandatesBeneficiary))
            .willRespondWith()
            .status(200)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "ResponseOk", port = "1234")
    void testResponseOk(MockServer mockServer) throws IOException, TransactionMonitoringException {
        transactionMonitoringClient.sendMandatesEvent(mandatesBeneficiary);
    }

    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseOkRequiredFields(PactDslWithProvider builder)
        throws JsonProcessingException {
        return builder
            .given(STATE_MANDATES_EXISTS)
            .uponReceiving(MANDATES_200_RF)
            .path(ENDPOINT)
            .method("POST")
            .body(objectMapper.writeValueAsString(mandatesBeneficiaryRequiredFields))
            .willRespondWith()
            .status(200)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "ResponseOkRequiredFields", port = "1234")
    void testResponseOkRequiredFields(MockServer mockServer) throws IOException, TransactionMonitoringException {
        transactionMonitoringClient.sendMandatesEvent(mandatesBeneficiaryRequiredFields);
    }

    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseBadRequest(PactDslWithProvider builder)
        throws JsonProcessingException {
        return builder
            .given(STATE_MANDATES_400)
            .uponReceiving(MANDATES_400)
            .path(ENDPOINT)
            .method("POST")
            .body(objectMapper.writeValueAsString(mandatesBeneficiaryBadRequest))
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
                    transactionMonitoringClient.sendMandatesEvent(mandatesBeneficiaryBadRequest));
        assertEquals(400, 400);
    }

    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseInternalServerError(PactDslWithProvider builder)
        throws JsonProcessingException {
        return builder
            .given(STATE_MANDATES_500)
            .uponReceiving(MANDATES_500)
            .path(ENDPOINT)
            .method("POST")
            .body(objectMapper.writeValueAsString(mandatesBeneficiary))
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
                () -> transactionMonitoringClient.sendMandatesEvent(mandatesBeneficiary));
    }
}