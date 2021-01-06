package com.tenx.fraudamlmanager.pact.consumer.onboarding.payee;

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
import com.tenx.fraudamlmanager.onboarding.payee.api.ChangeType;
import com.tenx.fraudamlmanager.onboarding.payee.api.PayeeAccount;
import com.tenx.fraudamlmanager.onboarding.payee.api.PayeeBeneficiary;
import com.tenx.fraudamlmanager.onboarding.payee.api.PayeeData;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.collections4.MapUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(PactConsumerTestExt.class)
public class CreatePayeeConsumerTest extends SpringBootTestBase {

    private static final String STATE_PAYEE_EXISTS = "That FeatureSpace client exists for V1 New Payee";
    private static final String STATE_PAYEE_400 = "A BAD REQUEST Error occurs for PAYEE party event";
    private static final String STATE_PAYEE_500 = "That Internal Server Error Occurs for New Payee Events";
    private static final String ENDPOINT = "/v1/parties/payee/";
    private static final String NEWPAYEE_200 = "V1 NewPayee: Status 200 request";
    private static final String NEWPAYEE_200_RF = "V1 NewPayee: Status 200 request with Required Fields";
    private static final String NEWPAYEE_400 = "V1 NewPayee: Status 400 request";
    private static final String NEWPAYEE_500 = "V1 NewPayee: Status 500 request";
    private static PayeeData payeeData = new PayeeData();
    private static PayeeData newPayeeDetailsBadRequest = new PayeeData();
    private static PayeeData newPayeeRequiredFields;
    private Map<String, String> headers = MapUtils
        .putAll(new HashMap<>(), new String[]{"Content-Type", "application/json"});

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TransactionMonitoringClient transactionMonitoringClient;

    @BeforeAll
    public static void init() {
        List<PayeeAccount> payeeAccountList = new ArrayList<PayeeAccount>();
        payeeAccountList.add(new PayeeAccount("id", "identification", "identificationName", "name"));

        payeeData =
            new PayeeData(
                UUID.randomUUID().toString(),
                "payeeId",
                "accountId",
                "none",
                "payeeName",
                ChangeType.CREATE,
                new PayeeBeneficiary("id", "firstName", "lastName", "reference"),
                payeeAccountList);

        newPayeeDetailsBadRequest =
            new PayeeData(
                null,
                "",
                "abcgdf",
                "test@email.com",
                "payeeName",
                ChangeType.CREATE,
                new PayeeBeneficiary(),
                payeeAccountList);

        newPayeeRequiredFields =
            new PayeeData(
                null,
                "partyKey",
                "payeeId",
                "accountId",
                "payeeName",
                ChangeType.CREATE,
                new PayeeBeneficiary("id", "firstName", "lastName", "reference"),
                payeeAccountList);
    }

    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseOk(PactDslWithProvider builder)
        throws JsonProcessingException {
        return builder
            .given(STATE_PAYEE_EXISTS)
            .uponReceiving(NEWPAYEE_200)
            .path(ENDPOINT)
            .method("POST")
            .body(objectMapper.writeValueAsString(payeeData))
            .willRespondWith()
            .status(200)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "ResponseOk", port = "1234")
    void testResponseOk(MockServer mockServer) throws TransactionMonitoringException {
        transactionMonitoringClient.sendPayeeEvent(payeeData);
    }

    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseOkRequiredFields(PactDslWithProvider builder)
        throws JsonProcessingException {
        return builder
            .given(STATE_PAYEE_EXISTS)
            .uponReceiving(NEWPAYEE_200_RF)
            .path(ENDPOINT)
            .method("POST")
            .body(objectMapper.writeValueAsString(newPayeeRequiredFields))
            .willRespondWith()
            .status(200)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "ResponseOkRequiredFields", port = "1234")
    void testResponseOkRequiredFields(MockServer mockServer) throws TransactionMonitoringException {
        transactionMonitoringClient.sendPayeeEvent(newPayeeRequiredFields);
    }

    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseBadRequest(PactDslWithProvider builder)
        throws JsonProcessingException {
        return builder
            .given(STATE_PAYEE_400)
            .uponReceiving(NEWPAYEE_400)
            .path(ENDPOINT)
            .method("POST")
            .body(objectMapper.writeValueAsString(newPayeeDetailsBadRequest))
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
                    transactionMonitoringClient.sendPayeeEvent(
                        newPayeeDetailsBadRequest));
        assertEquals(400, 400);
    }

    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseInternalServerError(PactDslWithProvider builder)
        throws JsonProcessingException {
        return builder
            .given(STATE_PAYEE_500)
            .uponReceiving(NEWPAYEE_500)
            .path(ENDPOINT)
            .method("POST")
            .body(objectMapper.writeValueAsString(payeeData))
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
                () -> transactionMonitoringClient.sendPayeeEvent(payeeData));
        assertEquals(transactionMonitoringException.getErrorDetails().getHttpStatusCode(), 500);
    }

}
