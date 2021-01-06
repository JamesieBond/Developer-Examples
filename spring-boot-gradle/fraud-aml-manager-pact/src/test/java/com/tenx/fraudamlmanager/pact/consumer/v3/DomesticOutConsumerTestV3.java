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
import com.tenx.fraudamlmanager.paymentsv3.domestic.infrastructure.tma.DomesticOutPaymentTMARequestV3;
import com.tenx.fraudamlmanager.paymentsv3.infrastructure.tma.AccountDetailsTMARequestV3;
import com.tenx.fraudamlmanager.paymentsv3.infrastructure.tma.BalanceBeforeTMARequestV3;
import com.tenx.fraudamlmanager.paymentsv3.infrastructure.tma.PaymentAmountTMARequestV3;
import com.tenx.fraudamlmanager.paymentsv3.infrastructure.tma.TransactionMonitoringClientV3;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(PactConsumerTestExt.class)
public class DomesticOutConsumerTestV3 extends SpringBootTestBase {

    private static final String ENDPOINT = "/v3/payments/domesticPaymentOutboundFinCrimeCheck";
    private static final String GIVEN_FS_CLIENT = "That FeatureSpace client exists for V3 Payments";
    private static final String GIVEN_INTERNALSERVERERROR = "That Internal Server Error Occurs";
    private static final String DomesticOut_200 = "V3 DomesticOut: Status 200 request";
    private static final String DomesticOut_200_RF = "V3 DomesticOut: Status 200 request with Required Fields";
    private static final String DomesticOut_400 = "V3 DomesticOut: Status 400 request";
    private static final String DomesticOut_500 = "V3 DomesticOut: Status 500 request";
    private static DomesticOutPaymentTMARequestV3 DomesticOutPaymentTMARequestV3;
    private static DomesticOutPaymentTMARequestV3 domesticOutPaymentBadTMARequestV3;
    private static DomesticOutPaymentTMARequestV3 domesticOutPaymentRequiredFieldsV3;

    @Autowired
    private ObjectMapper objectMapString;
    @Autowired
    private TransactionMonitoringClientV3 transactionMonitoringClient;

    @BeforeAll
    public static void init() {

        DomesticOutPaymentTMARequestV3 = new DomesticOutPaymentTMARequestV3(new AccountDetailsTMARequestV3("AccountNumber", "BankID"),
                "CreditorFirst CreditorSecond", new AccountDetailsTMARequestV3("AccountNumber", "BankID"),
                "DebtorFirst DebtorSecond", "debtorPartyKey",
                new PaymentAmountTMARequestV3("GBP", 30, "GBP", 30),
                "TranID",
                new Date(),
                new BalanceBeforeTMARequestV3("GBP", 500.00, "GBP", 500.00),
                new Date(),
                "TransactionStatus", "TranRef", "TranNotes",
                new ArrayList<String>(), true, new HashMap<>());

        domesticOutPaymentRequiredFieldsV3 = new DomesticOutPaymentTMARequestV3(
                new AccountDetailsTMARequestV3("Required", "Required"),
                "Required", new AccountDetailsTMARequestV3("Required", "Required"),
                "Required", "Required",
                new PaymentAmountTMARequestV3("Required", 30, "Required", 30),
                "Required",
                new Date(),
                new BalanceBeforeTMARequestV3("Required", 500.00, "Required", 500.00),
                new Date(),
                "TransactionStatus", "TranRef", "TranNotes",
                new ArrayList<String>(), true, new HashMap<>());

        domesticOutPaymentBadTMARequestV3 = new DomesticOutPaymentTMARequestV3(
                new AccountDetailsTMARequestV3("AccountNumber", "BankID"),
                null, new AccountDetailsTMARequestV3("AccountNumber", "BankID"),
                "DebtorFirst DebtorSecond", "debtorPartyKey",
                new PaymentAmountTMARequestV3("GBP", 30, "GBP", 30),
                "TranID",
                new Date(),
                new BalanceBeforeTMARequestV3("GBP", 500.00, "GBP", 500.00),
                new Date(),
                "TransactionStatus", "TranRef", "TranNotes",
                new ArrayList<String>(), true, new HashMap<>());
    }

    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseOk(PactDslWithProvider builder) throws JsonProcessingException {
        return builder
                .given(GIVEN_FS_CLIENT)
                .uponReceiving(DomesticOut_200)
                .path(ENDPOINT)
                .method("POST")
                .body(objectMapString.writeValueAsString(DomesticOutPaymentTMARequestV3))
            .willRespondWith()
            .status(200)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "ResponseOk", port = "1234")
    void testResponseOk(MockServer mockServer) throws TransactionMonitoringException {
        transactionMonitoringClient.checkFinCrimeV3(DomesticOutPaymentTMARequestV3);
    }

    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseOkRequiredFields(PactDslWithProvider builder) throws JsonProcessingException {
        return builder
            .given(GIVEN_FS_CLIENT)
            .uponReceiving(DomesticOut_200_RF)
            .path(ENDPOINT)
            .method("POST")
            .body(objectMapString.writeValueAsString(domesticOutPaymentRequiredFieldsV3))
            .willRespondWith()
            .status(200)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "ResponseOkRequiredFields", port = "1234")
    void testResponseOkRequiredFields(MockServer mockServer) throws TransactionMonitoringException {
        transactionMonitoringClient.checkFinCrimeV3(domesticOutPaymentRequiredFieldsV3);
    }

    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseBadRequest(PactDslWithProvider builder) throws JsonProcessingException {
        return builder
                .uponReceiving(DomesticOut_400)
                .path(ENDPOINT)
                .method("POST")
                .body(objectMapString.writeValueAsString(domesticOutPaymentBadTMARequestV3))
            .willRespondWith()
            .status(400)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "ResponseBadRequest", port = "1234")
    void testResponseBadRequest(MockServer mockServer) throws IOException {
        TransactionMonitoringException transactionMonitoringException = assertThrows(
                TransactionMonitoringException.class, () ->
                        transactionMonitoringClient.checkFinCrimeV3(domesticOutPaymentBadTMARequestV3));
        assertEquals(transactionMonitoringException.getErrorDetails().getHttpStatusCode(), 400);

    }

    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseInternalServerError(PactDslWithProvider builder) throws JsonProcessingException {
        return builder
                .given(GIVEN_INTERNALSERVERERROR)
                .uponReceiving(DomesticOut_500)
                .path(ENDPOINT)
                .method("POST")
                .body(objectMapString.writeValueAsString(DomesticOutPaymentTMARequestV3))
            .willRespondWith()
            .status(500)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "ResponseInternalServerError", port = "1234")
    void testResponseInternalServerError(MockServer mockServer) throws IOException {
        TransactionMonitoringException transactionMonitoringException = assertThrows(
                TransactionMonitoringException.class, () ->
                        transactionMonitoringClient.checkFinCrimeV3(DomesticOutPaymentTMARequestV3));
        assertEquals(transactionMonitoringException.getErrorDetails().getHttpStatusCode(), 500);

    }

}
