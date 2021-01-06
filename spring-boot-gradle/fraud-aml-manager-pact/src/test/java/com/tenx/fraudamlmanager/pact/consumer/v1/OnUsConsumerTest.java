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
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.BalanceBefore;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.FraudAMLSanctionsCheckResponse;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.OnUsPayment;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.PaymentAmount;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections4.MapUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(PactConsumerTestExt.class)
public class OnUsConsumerTest extends SpringBootTestBase {

  private static final String STATE_V1_EXISTS = "That FeatureSpace client exists for V1 Payments";
  private static final String STATE_V1_400 = "A BAD REQUEST Error occurs for a V1 payment";
  private static final String STATE_500 = "That Internal Server Error Occurs";
  private static final String ENDPOINT = "/v1/payments/onUsFinCrimeCheck";
  private static final String OnUS_200 = "checkFinCrime OnUs: Status 200 request";
  private static final String OnUs_200_RF = "checkFinCrime OnUs: Status 200 request with Required Fields";
  private static final String OnUs_400 = "checkFinCrime OnUs: Status 400 request";
  private static final String OnUs_500 = "checkFinCrime OnUs: Status 500 request";
  private static OnUsPayment onUsPayment;
  private static OnUsPayment onUsPaymentBadRequest;
  private static OnUsPayment onUsPaymentRequiredFields;
  private Map<String, String> headers = MapUtils.putAll(new HashMap<>(), new String[]{
    "Content-Type", "application/json"
  });

  @Autowired
  private ObjectMapper objectMapString;
  @Autowired
  private TransactionMonitoringClient transactionMonitoringClient;

  @BeforeAll
  public static void init() {
    onUsPayment = new OnUsPayment(new AccountDetails("AccountNumber", "BankID"),
      "CreditorFirstName CreditorSecondName", new AccountDetails("AccountNumber", "BankID"),
      "debitor name",
      new PaymentAmount("EUR", 10, "EUR", 10),
      new BalanceBefore("GBP", 500.00, "GBP", 500.00),
      "CredPartyKey", "DebtorPartyKey", "TranID",
      new Date(), new Date(),
      "TransactionStatus", "TranRef", "testNotes",
      new ArrayList<String>(), true);

    onUsPaymentBadRequest = new OnUsPayment(new AccountDetails("AccountNumber", "BankID"),
      null, new AccountDetails(null, null),
      "debitor name",
      new PaymentAmount("EUR", 10, "EUR", 10),
      new BalanceBefore("GBP", 500.00, "GBP", 500.00),
      "CredPartyKey", "DebtorPartyKey", "TranID",
      new Date(), new Date(),
      "TransactionStatus", "TranRef", "testNotes",
      new ArrayList<String>(), true);

    onUsPaymentRequiredFields = new OnUsPayment(new AccountDetails("Required", "Required"),
      "Required", new AccountDetails("Required", "Required"),
      "Required",
      new PaymentAmount("Required", 10, "Required", 10),
      new BalanceBefore("Required", 500.00, "Required", 500.00),
      null, "Required", "Required",
      new Date(), null,
      null, null, null,
      null, null);
  }

  @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
  public RequestResponsePact fraudCheckResponse(PactDslWithProvider builder) throws JsonProcessingException {
    return builder
      .given(STATE_V1_EXISTS)
      .uponReceiving(OnUS_200)
      .path(ENDPOINT)
      .method("POST")
      .body(objectMapString.writeValueAsString(onUsPayment))
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
    FraudAMLSanctionsCheckResponse fraudCheckResponse = transactionMonitoringClient.checkFinCrime(onUsPayment);
    assertThat(fraudCheckResponse.getStatus(), not(isEmptyOrNullString()));
    assertThat(fraudCheckResponse.getTransactionId(), not(isEmptyOrNullString()));
  }

  @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
  public RequestResponsePact fraudCheckResponseRequiredFields(PactDslWithProvider builder)
    throws JsonProcessingException {
    return builder

      .given(STATE_V1_EXISTS)
      .uponReceiving(OnUs_200_RF)
      .path(ENDPOINT)
      .method("POST")
      .body(objectMapString.writeValueAsString(onUsPaymentRequiredFields))
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
  void testFraudCheckResponseRequiredFields(MockServer mockServer) throws TransactionMonitoringException {
    FraudAMLSanctionsCheckResponse fraudCheckResponse = transactionMonitoringClient
      .checkFinCrime(onUsPaymentRequiredFields);
    assertThat(fraudCheckResponse.getStatus(), not(isEmptyOrNullString()));
    assertThat(fraudCheckResponse.getTransactionId(), not(isEmptyOrNullString()));
  }

  @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
  public RequestResponsePact fraudCheckResponseBadRequest(PactDslWithProvider builder)
    throws JsonProcessingException {
    return builder
      .given(STATE_V1_400)
      .uponReceiving(OnUs_400)
      .path(ENDPOINT)
      .method("POST")
      .body(objectMapString.writeValueAsString(onUsPaymentBadRequest))
      .willRespondWith()/**/
      .status(400)
      .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "fraudCheckResponseBadRequest", port = "1234")
  void testFraudCheckResponseBadRequest(MockServer mockServer) throws IOException {
    TransactionMonitoringException transactionMonitoringException = assertThrows(
      TransactionMonitoringException.class, () ->
        transactionMonitoringClient.checkFinCrime(onUsPaymentBadRequest));
    assertEquals(transactionMonitoringException.getErrorDetails().getHttpStatusCode(), 400);
  }

  @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
  public RequestResponsePact fraudCheckResponseInternalServerError(PactDslWithProvider builder)
    throws JsonProcessingException {
    return builder
      .given(STATE_500)
      .uponReceiving(OnUs_500)
      .path(ENDPOINT)
      .method("POST")
      .body(objectMapString.writeValueAsString(onUsPayment))
      .willRespondWith()/**/
      .status(500)
      .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "fraudCheckResponseInternalServerError", port = "1234")
  void testfraudCheckResponseInternalServerError(MockServer mockServer) throws IOException {
    TransactionMonitoringException transactionMonitoringException = assertThrows(
      TransactionMonitoringException.class, () -> transactionMonitoringClient.checkFinCrime(onUsPayment));
    assertEquals(transactionMonitoringException.getErrorDetails().getHttpStatusCode(), 500);
  }

}
