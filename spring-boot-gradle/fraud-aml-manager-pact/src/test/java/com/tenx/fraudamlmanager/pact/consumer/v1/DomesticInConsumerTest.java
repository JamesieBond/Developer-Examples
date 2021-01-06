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
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.DomesticInPayment;
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
public class DomesticInConsumerTest extends SpringBootTestBase {

  private static final String STATE_V1_EXISTS = "That FeatureSpace client exists for V1 Payments";
  private static final String STATE_V1_400 = "A BAD REQUEST Error occurs for a V1 payment";
  private static final String STATE_500 = "That Internal Server Error Occurs";
  private static final String ENDPOINT = "/v1/payments/domesticPaymentInboundFinCrimeCheck";
  private static final String DomesticIn_200 = "checkFinCrime DomesticIn: Status 200 request";
  private static final String DomestinIn_200_RF = "checkFinCrime DomesticIn: Status 200 request with Required Fields";
  private static final String DomesticIn_400 = "checkFinCrime DomesticIn: Status 400 request";
  private static final String DomesticIn_500 = "checkFinCrime DomesticIn: Status 500 request";
  private static DomesticInPayment domesticInPayment = new DomesticInPayment();
  private static DomesticInPayment domesticInPaymentBadRequest = new DomesticInPayment();
  private static DomesticInPayment domesticInPaymentRequiredFields = new DomesticInPayment();
  private Map<String, String> headers = MapUtils.putAll(new HashMap<>(), new String[]{
    "Content-Type", "application/json"
  });
  @Autowired
  private ObjectMapper objectMapString;
  @Autowired
  private TransactionMonitoringClient transactionMonitoringClient;

  @BeforeAll
  public static void init() {

    domesticInPayment = new DomesticInPayment(new AccountDetails("AccountNumber", "BankID"),
      "CreditorFirst CreditorSecond", new AccountDetails("AccountNumber", "BankID"),
      "DebtorFirst DebtorSecond",
      new PaymentAmount("EUR", 10, "EUR", 10),
      new BalanceBefore("GBP", 500.00, "GBP", 500.00),
      "TranID",
      new Date(), new Date(),
      "TranStatus", "TranRef");

    domesticInPaymentRequiredFields = new DomesticInPayment(new AccountDetails("Required", "Required"),
      "Required", new AccountDetails("Required", "Required"),
      "Required",
      new PaymentAmount("Required", 10, "Required", 10),
      new BalanceBefore("GBP", 500.00, "GBP", 500.00),
      "Required",
      new Date(), null,
      null, null);

    domesticInPaymentBadRequest = new DomesticInPayment(new AccountDetails("1", "1"),
      null, new AccountDetails("AccountNum", "BankID"),
      "DebtorFirst DebtorSecond",
      new PaymentAmount("EUR", 10, "EUR", 10),
      new BalanceBefore("GBP", 500.00, "GBP", 500.00),
      "TranID",
      new Date(), new Date(),
      "TranStatus", "TranRef");
  }

  @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
  public RequestResponsePact fraudCheckResponse(PactDslWithProvider builder) throws JsonProcessingException {
    return builder
      .given(STATE_V1_EXISTS)
      .uponReceiving(DomesticIn_200)
      .path(ENDPOINT)
      .method("POST")
      .body(objectMapString.writeValueAsString(domesticInPayment))
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
      .checkFinCrime(domesticInPayment);
    assertThat(fraudCheckResponse.getStatus(), not(isEmptyOrNullString()));
    assertThat(fraudCheckResponse.getTransactionId(), not(isEmptyOrNullString()));
  }

  @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
  public RequestResponsePact fraudCheckResponseRequiredFields(PactDslWithProvider builder)
    throws JsonProcessingException {
    return builder
      .given(STATE_V1_EXISTS)
      .uponReceiving(DomestinIn_200_RF)
      .path(ENDPOINT)
      .method("POST")
      .body(objectMapString.writeValueAsString(domesticInPaymentRequiredFields))
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
      .checkFinCrime(domesticInPaymentRequiredFields);
    assertThat(fraudCheckResponse.getStatus(), not(isEmptyOrNullString()));
    assertThat(fraudCheckResponse.getTransactionId(), not(isEmptyOrNullString()));
  }

  @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
  public RequestResponsePact fraudCheckResponseBadRequest(PactDslWithProvider builder)
    throws JsonProcessingException {
    return builder
      .given(STATE_V1_400)
      .uponReceiving(DomesticIn_400)
      .path(ENDPOINT)
      .method("POST")
      .body(objectMapString.writeValueAsString(domesticInPaymentBadRequest))
      .willRespondWith()
      .status(400)
      .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "fraudCheckResponseBadRequest", port = "1234")
  void testFraudCheckResponseBadRequest(MockServer mockServer) throws TransactionMonitoringException {
    TransactionMonitoringException transactionMonitoringException = assertThrows(
      TransactionMonitoringException.class, () ->
        transactionMonitoringClient.checkFinCrime(domesticInPaymentBadRequest));
    assertEquals(transactionMonitoringException.getErrorDetails().getHttpStatusCode(), 400);

  }

  @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
  public RequestResponsePact fraudCheckResponseInternalServerError(PactDslWithProvider builder)
    throws JsonProcessingException {
    return builder
      .given(STATE_500)
      .uponReceiving(DomesticIn_500)
      .path(ENDPOINT)
      .method("POST")
      .body(objectMapString.writeValueAsString(domesticInPayment))
      .willRespondWith()
      .status(500)
      .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "fraudCheckResponseInternalServerError", port = "1234")
  void testFraudCheckResponseInternalServerError(MockServer mockServer) throws IOException {
    TransactionMonitoringException transactionMonitoringException = assertThrows(
      TransactionMonitoringException.class, () ->
        transactionMonitoringClient.checkFinCrime(domesticInPayment));
    assertEquals(transactionMonitoringException.getErrorDetails().getHttpStatusCode(), 500);

  }

}
