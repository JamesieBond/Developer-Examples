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
import com.tenx.fraudamlmanager.paymentsv2.direct.debit.domain.AccountDetailsDirectDebitV2;
import com.tenx.fraudamlmanager.paymentsv2.direct.debit.domain.DirectDebitBacsPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.direct.debit.domain.PaymentAmountDirectDebitV2;
import com.tenx.fraudamlmanager.paymentsv2.direct.debit.infrastructure.DirectDebitTransactionMonitoringClientV2;
import java.io.IOException;
import java.util.Date;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(PactConsumerTestExt.class)
public class DirectDebitConsumerTest extends SpringBootTestBase {

  private static final String STATE_V2_EXISTS = "That FeatureSpace client exists for V2 Payments";
  private static final String STATE_V2_400 = "A BAD REQUEST Error occurs for a V2 payment";
  private static final String STATE_500 = "That Internal Server Error Occurs";
  private static final String ENDPOINT = "/v2/payments/directDebitFinCrimeCheck";
  private static final String DD_200 = "V2 DirectDebit: Status 200 request";
  private static final String DD_200_RF = "V2 DirectDebit: Status 200 request with Required Fields";
  private static final String DD_400 = "V2 DirectDebit: Status 400 request";
  private static final String DD_500 = "V2 DirectDebit: Status 500 request";
  private static DirectDebitBacsPaymentV2 directDebitBacsPaymentV2;
  private static DirectDebitBacsPaymentV2 directDebitBacsPaymentBadRequestV2;
  private static DirectDebitBacsPaymentV2 directDebitBacsPaymentRequiredFieldsV2;

  @Autowired
  private ObjectMapper objectMapString;
  @Autowired
  private DirectDebitTransactionMonitoringClientV2 directDebitTransactionMonitoringClientV2;

  @BeforeAll
  public static void init() {

    directDebitBacsPaymentV2 = new DirectDebitBacsPaymentV2(
      new AccountDetailsDirectDebitV2("AccountNumber", "BankID"),
      "CreditorFirst CreditorSecond", new AccountDetailsDirectDebitV2("AccountNumber", "BankID"),
      "DebtorFirst DebtorSecond", new PaymentAmountDirectDebitV2("EUR", 10, "EUR", 10),
      "PartyKeyMustBeLong", "TranID",
      new Date(), new Date(),
      "TranStatus", "TranRef");

    directDebitBacsPaymentRequiredFieldsV2 = new DirectDebitBacsPaymentV2(
      new AccountDetailsDirectDebitV2("Required", "Required"),
      "Required", new AccountDetailsDirectDebitV2("Required", "Required"),
      "Required", new PaymentAmountDirectDebitV2("Required", 10, "Required", 10),
      "Required123", "Required",
      new Date(), null,
      null, null);

    directDebitBacsPaymentBadRequestV2 = new DirectDebitBacsPaymentV2(null,
      null, new AccountDetailsDirectDebitV2("AccountNumber", "BankID"),
      "DebtorFirst DebtorSecond", new PaymentAmountDirectDebitV2("EUR", 10, "EUR", 10),
      null, "TranID",
      new Date(), null,
      "TranStatus", "TranRef");

  }


  @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
  public RequestResponsePact ResponseOkRequiredFields(PactDslWithProvider builder) throws JsonProcessingException {

    return builder
      .given(STATE_V2_EXISTS)
      .uponReceiving(DD_200_RF)
      .path(ENDPOINT)
      .method("POST")
      .body(objectMapString.writeValueAsString(directDebitBacsPaymentRequiredFieldsV2))
      .willRespondWith()
      .status(200)
      .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "ResponseOkRequiredFields", port = "1234")
  void testResponseOkRequiredFields(MockServer mockServer) throws TransactionMonitoringException {
    directDebitTransactionMonitoringClientV2.checkFinCrimeV2DirectDebit(directDebitBacsPaymentRequiredFieldsV2);
  }

  @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
  public RequestResponsePact ResponseOk(PactDslWithProvider builder) throws JsonProcessingException {

    return builder
      .given(STATE_V2_EXISTS)
      .uponReceiving(DD_200)
      .path(ENDPOINT)
      .method("POST")
      .body(objectMapString.writeValueAsString(directDebitBacsPaymentV2))
      .willRespondWith()
      .status(200)
      .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "ResponseOk", port = "1234")
  void testResponseOk(MockServer mockServer) throws TransactionMonitoringException {
    directDebitTransactionMonitoringClientV2.checkFinCrimeV2DirectDebit(directDebitBacsPaymentV2);
  }

  @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
  public RequestResponsePact ResponseBadRequest(PactDslWithProvider builder) throws JsonProcessingException {
    return builder
      .given(STATE_V2_400)
      .uponReceiving(DD_400)
      .path(ENDPOINT)
      .method("POST")
      .body(objectMapString.writeValueAsString(directDebitBacsPaymentBadRequestV2))
      .willRespondWith()
      .status(400)
      .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "ResponseBadRequest", port = "1234")
  void testResponseBadRequest(MockServer mockServer) throws IOException {
    TransactionMonitoringException transactionMonitoringException = assertThrows(
      TransactionMonitoringException.class, () ->
        directDebitTransactionMonitoringClientV2
          .checkFinCrimeV2DirectDebit(directDebitBacsPaymentBadRequestV2));
    assertEquals(transactionMonitoringException.getErrorDetails().getHttpStatusCode(), 400);

  }

  @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
  public RequestResponsePact ResponseInternalServerError(PactDslWithProvider builder) throws JsonProcessingException {
    return builder
      .given(STATE_500)
      .uponReceiving(DD_500)
      .path(ENDPOINT)
      .method("POST")
      .body(objectMapString.writeValueAsString(directDebitBacsPaymentV2))
      .willRespondWith()
      .status(500)
      .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "ResponseInternalServerError", port = "1234")
  void testResponseInternalServerError(MockServer mockServer) throws IOException {
    TransactionMonitoringException transactionMonitoringException = assertThrows(
      TransactionMonitoringException.class, () ->
        directDebitTransactionMonitoringClientV2.checkFinCrimeV2DirectDebit(directDebitBacsPaymentV2));
    assertEquals(transactionMonitoringException.getErrorDetails().getHttpStatusCode(), 500);

  }

}
