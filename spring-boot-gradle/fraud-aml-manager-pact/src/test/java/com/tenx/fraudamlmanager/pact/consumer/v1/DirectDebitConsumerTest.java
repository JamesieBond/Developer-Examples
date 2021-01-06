package com.tenx.fraudamlmanager.pact.consumer.v1;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.model.RequestResponsePact;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenx.fraudamlmanager.SpringBootTestBase;
import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.TransactionMonitoringClient;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.AccountDetails;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.DirectDebitBacsPayment;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.FraudAMLSanctionsCheckResponse;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.PaymentAmount;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections4.MapUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(PactConsumerTestExt.class)
public class DirectDebitConsumerTest extends SpringBootTestBase {

  private static final String STATE_V1_EXISTS = "That FeatureSpace client exists for V1 Payments";
  private static final String STATE_V1_400 = "A BAD REQUEST Error occurs for a V1 payment";
  private static final String STATE_500 = "That Internal Server Error Occurs";
  private static final String ENDPOINT = "/v1/payments/directDebitFinCrimeCheck";
  private static final String DD_200 = "checkFinCrime DirectDebit: Status 200 request";
  private static final String DD_200_RF = "checkFinCrime DirectDebit: Status 200 request for Minimum Required Fields";
  private static final String DD_400 = "checkFinCrime DirectDebit: Status 400 request";
  private static final String DD_500 = "checkFinCrime DirectDebit: Status 500 request";
  private static DirectDebitBacsPayment directDebitBacsPayment = new DirectDebitBacsPayment();
  private static DirectDebitBacsPayment directDebitBacsPaymentBadRequest = new DirectDebitBacsPayment();
  private static DirectDebitBacsPayment directDebitBacsPaymentRequiredFields = new DirectDebitBacsPayment();
  private Map<String, String> headers = MapUtils.putAll(new HashMap<>(), new String[]{
    "Content-Type", "application/json"
  });
  @Autowired
  private ObjectMapper objectMapString;
  @Autowired
  private TransactionMonitoringClient transactionMonitoringClient;

  @BeforeAll
  public static void init() {

    directDebitBacsPayment = new DirectDebitBacsPayment(new AccountDetails("AccountNumber", "BankID"),
      "CreditorFirst CreditorSecond", new AccountDetails("AccountNumber", "BankID"),
      "DebtorFirst DebtorSecond", new PaymentAmount("EUR", 10, "EUR", 10),
      "PartyKeyMustBeLong", "TranID",
      new Date(), new Date(),
      "TranStatus", "TranRef");

    directDebitBacsPaymentRequiredFields = new DirectDebitBacsPayment(new AccountDetails("Required", "Required"),
      "Required", new AccountDetails("Required", "Required"),
      "Required", new PaymentAmount("Required", 10, "Required", 10),
      "Required123", "Required",
      new Date(), null,
      null, null);

    directDebitBacsPaymentBadRequest = new DirectDebitBacsPayment(new AccountDetails("AccountNumber", "BankID"),
      null, new AccountDetails("AccountNumber", "BankID"),
      "DebtorFirst DebtorSecond", new PaymentAmount("EUR", 10, "EUR", 10),
      null, "TranID",
      new Date(), new Date(),
      "TranStatus", "TranRef");

  }

  @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
  public RequestResponsePact fraudCheckResponse(PactDslWithProvider builder) throws JsonProcessingException {
    return builder
      .given(STATE_V1_EXISTS)
      .uponReceiving(DD_200)
      .path(ENDPOINT)
      .method("POST")
      .body(objectMapString.writeValueAsString(directDebitBacsPayment))
      .willRespondWith()
      .status(200)
      .body(
        new PactDslJsonBody()
          .stringType("status")
          .stringType("transactionId")
          .eachLike("externalCases")

      )
      .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "fraudCheckResponse", port = "1234")
  void testFraudCheckResponse(MockServer mockServer) throws TransactionMonitoringException {
    FraudAMLSanctionsCheckResponse fraudCheckResponse = transactionMonitoringClient
      .checkFinCrimeDirectDebit(directDebitBacsPayment);
    assertThat(fraudCheckResponse.getTransactionId(), not(isEmptyOrNullString()));
    assertThat(fraudCheckResponse.getStatus(), not(isEmptyOrNullString()));

  }

  @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
  public RequestResponsePact fraudCheckResponseRequiredFields(PactDslWithProvider builder)
    throws JsonProcessingException {

    return builder
      .given(STATE_V1_EXISTS)
      .uponReceiving(DD_200_RF)
      .path(ENDPOINT)
      .method("POST")
      .body(objectMapString.writeValueAsString(directDebitBacsPaymentRequiredFields))
      .willRespondWith()
      .status(200)
      .body(
        new PactDslJsonBody()
          .stringType("status")
          .stringType("transactionId")
          .eachLike("externalCases")

      )
      .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "fraudCheckResponseRequiredFields", port = "1234")
  void testfraudCheckResponseRequiredFields(MockServer mockServer) throws TransactionMonitoringException {
    FraudAMLSanctionsCheckResponse fraudCheckResponse = transactionMonitoringClient
      .checkFinCrimeDirectDebit(directDebitBacsPaymentRequiredFields);
    assertThat(fraudCheckResponse.getStatus(), not(isEmptyOrNullString()));
    assertThat(fraudCheckResponse.getTransactionId(), not(isEmptyOrNullString()));

  }


  @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
  public RequestResponsePact fraudCheckResponseBadRequest(PactDslWithProvider builder)
    throws JsonProcessingException {
    return builder
      .given(STATE_V1_400)
      .uponReceiving(DD_400)
      .path(ENDPOINT)
      .body(objectMapString.writeValueAsString(directDebitBacsPaymentBadRequest))
      .method("POST")
      .willRespondWith()/**/
      .status(400)
      .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "fraudCheckResponseBadRequest", port = "1234")
  void testFraudCheckResponseBadRequest(MockServer mockServer) throws IOException {
    TransactionMonitoringException transactionMonitoringException = assertThrows(
      TransactionMonitoringException.class, () ->
        transactionMonitoringClient.checkFinCrimeDirectDebit(directDebitBacsPaymentBadRequest));
    assertEquals(transactionMonitoringException.getErrorDetails().getHttpStatusCode(), 400);

  }

  @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
  public RequestResponsePact fraudCheckResponseInternalServerError(PactDslWithProvider builder)
    throws JsonProcessingException {
    return builder
      .given(STATE_500)
      .uponReceiving(DD_500)
      .path(ENDPOINT)
      .method("POST")
      .body(objectMapString.writeValueAsString(directDebitBacsPayment))
      .willRespondWith()/**/
      .status(500)
      .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "fraudCheckResponseInternalServerError", port = "1234")
  void testFraudCheckResponseInternalServerError(MockServer mockServer) throws IOException {
    TransactionMonitoringException transactionMonitoringException = assertThrows(
      TransactionMonitoringException.class, () ->
        transactionMonitoringClient.checkFinCrimeDirectDebit(directDebitBacsPayment));
    assertEquals(transactionMonitoringException.getErrorDetails().getHttpStatusCode(), 500);

  }

}

