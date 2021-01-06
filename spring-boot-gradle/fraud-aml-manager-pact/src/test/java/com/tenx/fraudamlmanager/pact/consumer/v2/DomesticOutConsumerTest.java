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
import com.tenx.fraudamlmanager.paymentsv2.domestic.out.domain.DomesticOutPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.out.infrastructure.DomesticOutTransactionMonitoringClientV2;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(PactConsumerTestExt.class)
public class DomesticOutConsumerTest extends SpringBootTestBase {

  private static final String STATE_V2_EXISTS = "That FeatureSpace client exists for V2 Payments";
  private static final String STATE_V2_400 = "A BAD REQUEST Error occurs for a V2 payment";
  private static final String STATE_500 = "That Internal Server Error Occurs";
  private static final String ENDPOINT = "/v2/payments/domesticPaymentOutboundFinCrimeCheck";
  private static final String DomesticOut_200 = "V2 DomesticOut: Status 200 request";
  private static final String DomesticOut_200_RF = "V2 DomesticOut: Status 200 request with Required Fields";
  private static final String DomesticOut_400 = "V2 DomesticOut: Status 400 request";
  private static final String DomesticOut_500 = "V2 DomesticOut: Status 500 request";
  private static DomesticOutPaymentV2 domesticOutPaymentV2;
  private static DomesticOutPaymentV2 domesticOutPaymentBadRequestV2;
  private static DomesticOutPaymentV2 domesticOutPaymentRequiredFieldsV2;

  @Autowired
  private ObjectMapper objectMapString;
  @Autowired
  private DomesticOutTransactionMonitoringClientV2 transactionMonitoringClient;

  @BeforeAll
  public static void init() {

    domesticOutPaymentV2 = new DomesticOutPaymentV2(
      new AccountDetailsV2("accountName", "accountNumber", "AccountNumber", "bankId",
        "financialInstitutionIdentification", "IbanTest"),
      "CreditorFirst CreditorSecond",
      "creditorPostalAddress",
      new AccountDetailsV2("accountName", "accountNumber", "AccountNumber", "bankId",
        "financialInstitutionIdentification", "IbanTest"),
      "DebtorFirst DebtorSecond",
      "debtorPostalAddress",
      new PaymentAmountV2("GBP", 30, "GBP", 30),
      "TranID",
      new Date(),
      new BalanceBeforeV2("GBP", 500.00, "GBP", 500.00),
      new Date(),
      "TransactionStatus", "TranRef", "TranNotes",
      new ArrayList<String>(), true, "PartyKey", new HashMap<>(),
      "paymentType");

    domesticOutPaymentRequiredFieldsV2 = new DomesticOutPaymentV2(
      new AccountDetailsV2("", "accountNumber", "", "bankId",
        "", ""),
      "Required",
      "",
      new AccountDetailsV2("", "accountNumber", "", "bankId",
        "", ""),
      "Required",
      "",
      new PaymentAmountV2("Required", 30, "Required", 30),
      "bea4581a-46ba-4f8f-b0c2-be14419aa4f2",
      new Date(),
      new BalanceBeforeV2("GBP", 500.00, "GBP", 500.00),
      null,
      null, null, null,
      null, null, "bea4581a-46ba-4f8f-b0c2-be14419aa4f2", new HashMap<>(),
      "paymentType");

    domesticOutPaymentBadRequestV2 = new DomesticOutPaymentV2(
      new AccountDetailsV2("", "accountNumber", "", "bankId",
        "", ""),
      null,
      null, new AccountDetailsV2("", "accountNumber", "", "bankId",
      "", ""),
      "DebtorFirst DebtorSecond",
      null,
      new PaymentAmountV2("GBP", 30, "GBP", 30),
      "TranID",
      new Date(),
      new BalanceBeforeV2("GBP", 500.00, "GBP", 500.00),
      new Date(),
      "TransactionStatus", "TranRef", "TranNotes",
      new ArrayList<String>(), true, null, null,
      null);
  }

  @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
  public RequestResponsePact ResponseOk(PactDslWithProvider builder) throws JsonProcessingException {
    return builder
      .given(STATE_V2_EXISTS)
      .uponReceiving(DomesticOut_200)
      .path(ENDPOINT)
      .method("POST")
      .body(objectMapString.writeValueAsString(domesticOutPaymentV2))
      .willRespondWith()
      .status(200)
      .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "ResponseOk", port = "1234")
  void testResponseOk(MockServer mockServer) throws TransactionMonitoringException {
    transactionMonitoringClient.checkFinCrimeV2(domesticOutPaymentV2);
  }

  @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
  public RequestResponsePact ResponseOkRequiredFields(PactDslWithProvider builder) throws JsonProcessingException {
    return builder
      .given(STATE_V2_EXISTS)
      .uponReceiving(DomesticOut_200_RF)
      .path(ENDPOINT)
      .method("POST")
      .body(objectMapString.writeValueAsString(domesticOutPaymentRequiredFieldsV2))
      .willRespondWith()
      .status(200)
      .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "ResponseOkRequiredFields", port = "1234")
  void testResponseOkRequiredFields(MockServer mockServer) throws TransactionMonitoringException {
    transactionMonitoringClient.checkFinCrimeV2(domesticOutPaymentRequiredFieldsV2);
  }

  @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
  public RequestResponsePact ResponseBadRequest(PactDslWithProvider builder) throws JsonProcessingException {
    return builder
      .given(STATE_V2_400)
      .uponReceiving(DomesticOut_400)
      .path(ENDPOINT)
      .method("POST")
      .body(objectMapString.writeValueAsString(domesticOutPaymentBadRequestV2))
      .willRespondWith()
      .status(400)
      .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "ResponseBadRequest", port = "1234")
  void testResponseBadRequest(MockServer mockServer) throws IOException {
    TransactionMonitoringException transactionMonitoringException = assertThrows(
      TransactionMonitoringException.class, () ->
        transactionMonitoringClient.checkFinCrimeV2(domesticOutPaymentBadRequestV2));
    assertEquals(transactionMonitoringException.getErrorDetails().getHttpStatusCode(), 400);

  }

  @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
  public RequestResponsePact ResponseInternalServerError(PactDslWithProvider builder) throws JsonProcessingException {
    return builder
      .given(STATE_500)
      .uponReceiving(DomesticOut_500)
      .path(ENDPOINT)
      .method("POST")
      .body(objectMapString.writeValueAsString(domesticOutPaymentV2))
      .willRespondWith()
      .status(500)
      .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "ResponseInternalServerError", port = "1234")
  void testResponseInternalServerError(MockServer mockServer) throws IOException {
    TransactionMonitoringException transactionMonitoringException = assertThrows(
      TransactionMonitoringException.class, () ->
        transactionMonitoringClient.checkFinCrimeV2(domesticOutPaymentV2));
    assertEquals(transactionMonitoringException.getErrorDetails().getHttpStatusCode(), 500);

  }

}
