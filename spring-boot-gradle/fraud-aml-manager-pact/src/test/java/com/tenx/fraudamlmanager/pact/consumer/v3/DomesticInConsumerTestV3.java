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
import com.tenx.fraudamlmanager.paymentsv3.domestic.infrastructure.tma.DomesticInPaymentTMARequestV3;
import com.tenx.fraudamlmanager.paymentsv3.infrastructure.tma.AccountDetailsTMARequestV3;
import com.tenx.fraudamlmanager.paymentsv3.infrastructure.tma.BalanceBeforeTMARequestV3;
import com.tenx.fraudamlmanager.paymentsv3.infrastructure.tma.PaymentAmountTMARequestV3;
import com.tenx.fraudamlmanager.paymentsv3.infrastructure.tma.TransactionMonitoringClientV3;
import java.io.IOException;
import java.util.Date;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(PactConsumerTestExt.class)
public class DomesticInConsumerTestV3 extends SpringBootTestBase {

    private static final String ENDPOINT = "/v3/payments/domesticPaymentInboundFinCrimeCheck";
    private static final String GIVEN_FS_CLIENT = "That FeatureSpace client exists for V3 Payments";
    private static final String GIVEN_INTERNALSERVERERROR = "That Internal Server Error Occurs";
    private static final String DomesticIn_200 = "V3 DomesticIn: Status 200 request";
    private static final String DomestinIn_200_RF = "V3 DomesticIn: Status 200 request with Required Fields";
    private static final String DomesticIn_400 = "V3 DomesticIn: Status 400 request";
    private static final String DomesticIn_500 = "V3 DomesticIn: Status 500 request";
    private static DomesticInPaymentTMARequestV3 DomesticInPaymentTMARequestV3;
    private static DomesticInPaymentTMARequestV3 domesticInPaymentBadTMARequestV3;
    private static DomesticInPaymentTMARequestV3 domesticInPaymentRequiredFieldsV3;

    @Autowired
    private ObjectMapper objectMapString;
    @Autowired
    private TransactionMonitoringClientV3 transactionMonitoringClientV3;

    @BeforeAll
    public static void init() {

        DomesticInPaymentTMARequestV3 = new DomesticInPaymentTMARequestV3(new AccountDetailsTMARequestV3("AccountNumber", "BankID"),
                "CreditorFirst CreditorSecond", new AccountDetailsTMARequestV3("AccountNumber", "BankID"),
                "DebtorFirst DebtorSecond",
                new PaymentAmountTMARequestV3(
                        "EUR", 10, "EUR", 10),
                new BalanceBeforeTMARequestV3("GBP", 500.00, "GBP", 500.00),
                "TranID",
                new Date(), new Date(),
                "TranStatus", "TranRef", "creditorDomesticInPaymentTMARequestV3tyKey");

        domesticInPaymentRequiredFieldsV3 = new DomesticInPaymentTMARequestV3(new AccountDetailsTMARequestV3("Required", "Required"),
                "Required", new AccountDetailsTMARequestV3("Required", "Required"),
                "Required",
                new PaymentAmountTMARequestV3("Required", 10, "Required", 10),
                new BalanceBeforeTMARequestV3("GBP", 500.00, "GBP", 500.00),
                "Required",
                new Date(), null,
                null, null, null);

        domesticInPaymentBadTMARequestV3 = new DomesticInPaymentTMARequestV3(new AccountDetailsTMARequestV3("1", "1"),
                null, new AccountDetailsTMARequestV3("AccountNum", "BankID"),
                "DebtorFirst DebtorSecond",
                new PaymentAmountTMARequestV3("EUR", 10, "EUR", 10),
                new BalanceBeforeTMARequestV3("GBP", 500.00, "GBP", 500.00),
                "TranID",
                new Date(), new Date(),
                "TranStatus", "TranRef", "creditorPartyKey");
    }

    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseOk(PactDslWithProvider builder) throws JsonProcessingException {

        return builder
                .given(GIVEN_FS_CLIENT)
                .uponReceiving(DomesticIn_200)
                .path(ENDPOINT)
                .method("POST")
                .body(objectMapString.writeValueAsString(DomesticInPaymentTMARequestV3))
            .willRespondWith()
            .status(200)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "ResponseOk", port = "1234")
    void testResponseOk(MockServer mockServer) throws TransactionMonitoringException {
        transactionMonitoringClientV3.checkFinCrimeV3(DomesticInPaymentTMARequestV3);
    }


    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseOkRequiredFields(PactDslWithProvider builder) throws JsonProcessingException {

        return builder
            .given(GIVEN_FS_CLIENT)
            .uponReceiving(DomestinIn_200_RF)
            .path(ENDPOINT)
            .method("POST")
            .body(objectMapString.writeValueAsString(domesticInPaymentRequiredFieldsV3
            ))
            .willRespondWith()
            .status(200)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "ResponseOkRequiredFields", port = "1234")
    void testResponseOkRequiredFields(MockServer mockServer) throws TransactionMonitoringException {
        transactionMonitoringClientV3.checkFinCrimeV3(domesticInPaymentRequiredFieldsV3);
    }


    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseBadRequest(PactDslWithProvider builder) throws JsonProcessingException {
        return builder
                .uponReceiving(DomesticIn_400)
                .path(ENDPOINT)
                .method("POST")
                .body(objectMapString.writeValueAsString(domesticInPaymentBadTMARequestV3))
            .willRespondWith()
            .status(400)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "ResponseBadRequest", port = "1234")
    void testResponseBadRequest(MockServer mockServer) throws IOException {
        TransactionMonitoringException transactionMonitoringException = assertThrows(
                TransactionMonitoringException.class, () ->
                        transactionMonitoringClientV3.checkFinCrimeV3(domesticInPaymentBadTMARequestV3));
        assertEquals(transactionMonitoringException.getErrorDetails().getHttpStatusCode(), 400);

    }

    @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseInternalServerError(PactDslWithProvider builder) throws JsonProcessingException {
        return builder
                .given(GIVEN_INTERNALSERVERERROR)
                .uponReceiving(DomesticIn_500)
                .path(ENDPOINT)
                .method("POST")
                .body(objectMapString.writeValueAsString(DomesticInPaymentTMARequestV3))
            .willRespondWith()
            .status(500)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "ResponseInternalServerError", port = "1234")
    void testResponseInternalServerError(MockServer mockServer) throws IOException {
        TransactionMonitoringException transactionMonitoringException = assertThrows(
                TransactionMonitoringException.class, () ->
                        transactionMonitoringClientV3.checkFinCrimeV3(DomesticInPaymentTMARequestV3));
        assertEquals(transactionMonitoringException.getErrorDetails().getHttpStatusCode(), 500);

    }

}
