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
import com.tenx.fraudamlmanager.paymentsv2.domestic.domain.AccountDetailsV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.domain.BalanceBeforeV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.domain.PaymentAmountV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.in.infrastructure.DomesticInTransactionMonitoringClientV2;
import java.io.IOException;
import java.util.Date;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(PactConsumerTestExt.class)
public class DomesticInConsumerTest extends SpringBootTestBase {

  private static final String STATE_V2_EXISTS = "That FeatureSpace client exists for V2 Payments";
  private static final String STATE_V2_400 = "A BAD REQUEST Error occurs for a V2 payment";
  private static final String STATE_500 = "That Internal Server Error Occurs";
  private static final String ENDPOINT = "/v2/payments/domesticPaymentInboundFinCrimeCheck";
  private static final String DomesticIn_200 = "V2 DomesticIn: Status 200 request";
  private static final String DomestinIn_200_RF = "V2 DomesticIn: Status 200 request with Required Fields";
  private static final String DomesticIn_400 = "V2 DomesticIn: Status 400 request";
  private static final String DomesticIn_500 = "V2 DomesticIn: Status 500 request";
  private static com.tenx.fraudamlmanager.paymentsv2.domestic.in.domain.DomesticInPaymentV2 domesticInPaymentV2;
  private static com.tenx.fraudamlmanager.paymentsv2.domestic.in.domain.DomesticInPaymentV2 domesticInPaymentBadRequestV2;
  private static com.tenx.fraudamlmanager.paymentsv2.domestic.in.domain.DomesticInPaymentV2 domesticInPaymentRequiredFieldsV2;

  @Autowired
  private ObjectMapper objectMapString;
  @Autowired
  private DomesticInTransactionMonitoringClientV2 transactionMonitoringClient;

  @BeforeAll
  public static void init() {

    domesticInPaymentV2 = new com.tenx.fraudamlmanager.paymentsv2.domestic.in.domain.DomesticInPaymentV2(
      new AccountDetailsV2("accountName", "accountNumber", "AccountNumber", "bankId",
        "financialInstitutionIdentification", "IbanTest"),
      "CreditorFirst CreditorSecond",
      "test",
      new AccountDetailsV2("accountName", "accountNumber", "AccountNumber", "bankId",
        "financialInstitutionIdentification", "IbanTest"),
      "DebtorFirst DebtorSecond",
      "test",
      new com.tenx.fraudamlmanager.paymentsv2.domestic.domain.PaymentAmountV2("EUR", 10, "EUR", 10),
      "TranID",
      new Date(),
      new com.tenx.fraudamlmanager.paymentsv2.domestic.domain.BalanceBeforeV2("GBP", 500.00, "GBP", 500.00),
      new Date(),
      "TranStatus", "TranRef", "test",
      "test", "partyKey");

    domesticInPaymentRequiredFieldsV2 = new com.tenx.fraudamlmanager.paymentsv2.domestic.in.domain.DomesticInPaymentV2(
      new AccountDetailsV2("accountName", "accountNumber", "AccountNumber", "bankId",
        "financialInstitutionIdentification", "IbanTest"),
      "Required",
      "test",
      new AccountDetailsV2("accountName", "accountNumber", "AccountNumber", "bankId",
        "financialInstitutionIdentification", "IbanTest"),
      "Required",
      "test",
      new com.tenx.fraudamlmanager.paymentsv2.domestic.domain.PaymentAmountV2("Required", 10, "Required", 10),
      "Required",
      new Date(),
      new com.tenx.fraudamlmanager.paymentsv2.domestic.domain.BalanceBeforeV2("GBP", 500.00, "GBP", 500.00),
      null,
      null, null, null, null, null);

    domesticInPaymentBadRequestV2 = new com.tenx.fraudamlmanager.paymentsv2.domestic.in.domain.DomesticInPaymentV2(null,
      null, null, new AccountDetailsV2("accountName", "accountNumber", "AccountNumber", "bankId",
      "financialInstitutionIdentification", "IbanTest"),
      "DebtorFirst DebtorSecond",
      "test",
      new PaymentAmountV2("EUR", 10, "EUR", 10),
      "TranID",
      new Date(),
      new BalanceBeforeV2("GBP", 500.00, "GBP", 500.00), null,
      null, "TranRef", null, null, null);
  }

  @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
  public RequestResponsePact ResponseOk(PactDslWithProvider builder) throws JsonProcessingException {

    return builder
      .given(STATE_V2_EXISTS)
      .uponReceiving(DomesticIn_200)
      .path(ENDPOINT)
      .method("POST")
      .body(objectMapString.writeValueAsString(domesticInPaymentV2))
      .willRespondWith()
      .status(200)
      .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "ResponseOk", port = "1234")
  void testResponseOk(MockServer mockServer) throws TransactionMonitoringException {
    transactionMonitoringClient.checkFinCrimeV2(domesticInPaymentV2);
  }


  @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
  public RequestResponsePact ResponseOkRequiredFields(PactDslWithProvider builder) throws JsonProcessingException {

    return builder
      .given(STATE_V2_EXISTS)
      .uponReceiving(DomestinIn_200_RF)
      .path(ENDPOINT)
      .method("POST")
      .body(objectMapString.writeValueAsString(domesticInPaymentRequiredFieldsV2))
      .willRespondWith()
      .status(200)
      .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "ResponseOkRequiredFields", port = "1234")
  void testResponseOkRequiredFields(MockServer mockServer) throws TransactionMonitoringException {
    transactionMonitoringClient.checkFinCrimeV2(domesticInPaymentRequiredFieldsV2);
  }


  @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
  public RequestResponsePact ResponseBadRequest(PactDslWithProvider builder) throws JsonProcessingException {
    return builder
      .given(STATE_V2_400)
      .uponReceiving(DomesticIn_400)
      .path(ENDPOINT)
      .method("POST")
      .body(objectMapString.writeValueAsString(domesticInPaymentBadRequestV2))
      .willRespondWith()
      .status(400)
      .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "ResponseBadRequest", port = "1234")
  void testResponseBadRequest(MockServer mockServer) throws IOException {
    TransactionMonitoringException transactionMonitoringException = assertThrows(
      TransactionMonitoringException.class, () ->
        transactionMonitoringClient.checkFinCrimeV2(domesticInPaymentBadRequestV2));
    assertEquals(transactionMonitoringException.getErrorDetails().getHttpStatusCode(), 400);

  }

  @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
  public RequestResponsePact ResponseInternalServerError(PactDslWithProvider builder) throws JsonProcessingException {
    return builder
      .given(STATE_500)
      .uponReceiving(DomesticIn_500)
      .path(ENDPOINT)
      .method("POST")
      .body(objectMapString.writeValueAsString(domesticInPaymentV2))
      .willRespondWith()
      .status(500)
      .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "ResponseInternalServerError", port = "1234")
  void testResponseInternalServerError(MockServer mockServer) throws IOException {
    TransactionMonitoringException transactionMonitoringException = assertThrows(
      TransactionMonitoringException.class, () ->
        transactionMonitoringClient.checkFinCrimeV2(domesticInPaymentV2));
    assertEquals(transactionMonitoringException.getErrorDetails().getHttpStatusCode(), 500);

  }

}
