package com.tenx.fraudamlmanager.pact.consumer.v2;

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
import com.tenx.fraudamlmanager.paymentsv2.onus.domain.AccountDetailsOnUsV2;
import com.tenx.fraudamlmanager.paymentsv2.onus.domain.BalanceBeforeOnUsV2;
import com.tenx.fraudamlmanager.paymentsv2.onus.domain.OnUsPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.onus.domain.PaymentAmountOnUsV2;
import com.tenx.fraudamlmanager.paymentsv2.onus.infrastructure.OnUsTransactionMonitoringClientV2;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(PactConsumerTestExt.class)
public class OnUsConsumerTest extends SpringBootTestBase {

  private static final String STATE_V2_EXISTS = "That FeatureSpace client exists for V2 Payments";
  private static final String STATE_V2_400 = "A BAD REQUEST Error occurs for a V2 payment";
  private static final String STATE_500 = "That Internal Server Error Occurs";
  private static final String ENDPOINT = "/v2/payments/onUsFinCrimeCheck";
  private static final String ONUS_200 = "V2 OnUs: Status 200 request";
  private static final String ONUS_200_RF = "V2 OnUs: Status 200 request with Required Fields";
  private static final String ONUS_400 = "V2 OnUs: Status 400 request";
  private static final String ONUS_500 = "V2 OnUs: Status 500 request";
  private static OnUsPaymentV2 onUsPaymentV2;
  private static OnUsPaymentV2 onUsPaymentBadRequestV2;
  private static OnUsPaymentV2 onUsPaymentRequiredFieldsV2;

  @Autowired
  private ObjectMapper objectMapString;
  @Autowired
  private OnUsTransactionMonitoringClientV2 transactionMonitoringClient;

  @BeforeAll
  public static void init() {
    onUsPaymentV2 = new OnUsPaymentV2(new AccountDetailsOnUsV2("AccountNumber", "BankID"),
      "CreditorFirstName CreditorSecondName",
      new AccountDetailsOnUsV2("AccountNumber", "BankID"), "debitor name",
      new PaymentAmountOnUsV2("EUR", 10, "EUR", 10),
      new BalanceBeforeOnUsV2("GBP", 500.00, "GBP", 500.00),
      "CredPartyKey", "DebtorPartyKey", "TranID", new Date(),
      new Date(), "TransactionStatus", "TranRef", "testNotes",
      new ArrayList<String>(), true, new HashMap<>());

    onUsPaymentBadRequestV2 = new OnUsPaymentV2(
      new AccountDetailsOnUsV2("AccountNumber", "BankID"),
      null,
      new AccountDetailsOnUsV2("AccountNumber", "BankID"), "debitor name",
      new PaymentAmountOnUsV2("EUR", 10, "EUR", 10),
      new BalanceBeforeOnUsV2("GBP", 500.00, "GBP", 500.00),
      "CredPartyKey", null, "TranID", new Date(),
      new Date(), "TransactionStatus", "TranRef", "testNotes",
      new ArrayList<String>(), true, new HashMap<>());

    onUsPaymentRequiredFieldsV2 = new OnUsPaymentV2(
      new AccountDetailsOnUsV2("Required", "Required"),
      "Required",
      new AccountDetailsOnUsV2("Required", "Required"),
      "Required",
      new PaymentAmountOnUsV2("Required", 10, "Required", 10),
      new BalanceBeforeOnUsV2("Required", 500.00, "Required", 500.00),
      null, "Required", "Required", new Date(),
      null, null, null, null,
      null, null, null);

  }

  @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
  public RequestResponsePact ResponseOk(PactDslWithProvider builder) throws JsonProcessingException {
    return builder
      .given(STATE_V2_EXISTS)
      .uponReceiving(ONUS_200)
      .path(ENDPOINT)
      .method("POST")
      .body(objectMapString.writeValueAsString(onUsPaymentV2))
      .willRespondWith()
      .status(200)
      .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "ResponseOk", port = "1234")
  void testResponseOk(MockServer mockServer) throws TransactionMonitoringException {
    transactionMonitoringClient.checkFinCrimeV2(onUsPaymentV2);
  }

  @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
  public RequestResponsePact ResponseOkRequiredFields(PactDslWithProvider builder) throws JsonProcessingException {
    return builder
      .given(STATE_V2_EXISTS)
      .uponReceiving(ONUS_200_RF)
      .path(ENDPOINT)
      .method("POST")
      .body(objectMapString.writeValueAsString(onUsPaymentRequiredFieldsV2))
      .willRespondWith()
      .status(200)
      .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "ResponseOkRequiredFields", port = "1234")
  void testResponseOkRequiredFields(MockServer mockServer) throws TransactionMonitoringException {
    transactionMonitoringClient.checkFinCrimeV2(onUsPaymentRequiredFieldsV2);
  }

  @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
  public RequestResponsePact ResponseBadRequest(PactDslWithProvider builder) throws JsonProcessingException {
    return builder
      .given(STATE_V2_400)
      .uponReceiving(ONUS_400)
      .path(ENDPOINT)
      .method("POST")
      .body(objectMapString.writeValueAsString(onUsPaymentBadRequestV2))
      .willRespondWith()/**/
      .status(400)
      .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "ResponseBadRequest", port = "1234")
  void testResponseBadRequest(MockServer mockServer) throws IOException {
    TransactionMonitoringException transactionMonitoringException = assertThrows(
      TransactionMonitoringException.class, () ->
        transactionMonitoringClient.checkFinCrimeV2(onUsPaymentBadRequestV2));
    assertEquals(transactionMonitoringException.getErrorDetails().getHttpStatusCode(), 400);
  }


  @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
  public RequestResponsePact ResponseInternalServerError(PactDslWithProvider builder) throws JsonProcessingException {
    return builder
      .given(STATE_500)
      .uponReceiving(ONUS_500)
      .path(ENDPOINT)
      .method("POST")
      .body(objectMapString.writeValueAsString(onUsPaymentV2))
      .willRespondWith()/**/
      .status(500)
      .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "ResponseInternalServerError", port = "1234")
  void testResponseInternalServerError(MockServer mockServer) throws IOException {
    TransactionMonitoringException transactionMonitoringException = assertThrows(
      TransactionMonitoringException.class, () -> transactionMonitoringClient.checkFinCrimeV2(onUsPaymentV2));
    assertEquals(transactionMonitoringException.getErrorDetails().getHttpStatusCode(), 500);
  }

}
