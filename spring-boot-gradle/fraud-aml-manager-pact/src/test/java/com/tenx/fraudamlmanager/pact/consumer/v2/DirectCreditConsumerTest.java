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
import com.tenx.fraudamlmanager.paymentsv2.direct.credit.domain.AccountDetailsDirectCreditV2;
import com.tenx.fraudamlmanager.paymentsv2.direct.credit.domain.DirectCreditBacsPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.direct.credit.domain.PaymentAmountDirectCreditV2;
import com.tenx.fraudamlmanager.paymentsv2.direct.credit.infrastructure.DirectCreditTransactionMonitoringClientV2;
import java.io.IOException;
import java.util.Date;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(PactConsumerTestExt.class)
public class DirectCreditConsumerTest extends SpringBootTestBase {

  private static final String STATE_V2_EXISTS = "That FeatureSpace client exists for V2 Payments";
  private static final String STATE_V2_400 = "A BAD REQUEST Error occurs for a V2 payment";
  private static final String STATE_500 = "That Internal Server Error Occurs";
  private static final String ENDPOINT = "/v2/payments/directCreditFinCrimeCheck";
  private static final String DC_200 = "V2 DirectCredit: Status 200 request";
  private static final String DC_200_RF = "V2 DirectCredit: Status 200 request with Required Fields";
  private static final String DC_400 = "V2 DirectCredit: Status 400 request";
  private static final String DC_500 = "V2 DirectCredit: Status 500 request";
  private static DirectCreditBacsPaymentV2 directCreditBacsPaymentV2;
  private static DirectCreditBacsPaymentV2 directCreditBacsPaymentBadRequestV2;
  private static DirectCreditBacsPaymentV2 directCreditBacsPaymentRequiredFieldsV2;

  @Autowired
  private ObjectMapper objectMapString;
  @Autowired
  private DirectCreditTransactionMonitoringClientV2 directCreditTransactionMonitoringClientV2;

  @BeforeAll
  public static void init() {

    directCreditBacsPaymentRequiredFieldsV2 = new DirectCreditBacsPaymentV2(
      new AccountDetailsDirectCreditV2("Required", "Required"),
      "Required",
      new AccountDetailsDirectCreditV2("Required", "Required"),
      "Required",
      new PaymentAmountDirectCreditV2("Required", 10, "Required", 10),
      "RequiredPK", "Required",
      new Date(), new Date(),
      null, null);

    directCreditBacsPaymentV2 = new DirectCreditBacsPaymentV2(
      new AccountDetailsDirectCreditV2("AccountNumber", "BankID"),
      "CreditorFirst CreditorSecond",
      new AccountDetailsDirectCreditV2("AccountNumber", "BankID"),
      "DebtorFirst DebtorSecond",
      new PaymentAmountDirectCreditV2("EUR", 10, "EUR", 10),
      "PartyKey1234", "TranID",
      new Date(), new Date(),
      "TranStatus", "TranReference");

    directCreditBacsPaymentBadRequestV2 = new DirectCreditBacsPaymentV2(
      new AccountDetailsDirectCreditV2("AccountNumber", "BankID"),
      null,
      new AccountDetailsDirectCreditV2("AccountNumber", "BankID"),
      "DebtorFirst DebtorSecond",
      new PaymentAmountDirectCreditV2("EUR", 10, "EUR", 10),
      null, "TranID",
      new Date(), null,
      "TranStatus", "TranReference");
  }

  @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
  public RequestResponsePact ResponseOk(PactDslWithProvider builder) throws JsonProcessingException {

    return builder
      .given(STATE_V2_EXISTS)
      .uponReceiving(DC_200)
      .path(ENDPOINT)
      .method("POST")
      .body(objectMapString.writeValueAsString(directCreditBacsPaymentV2))
      .willRespondWith()
      .status(200)
      .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "ResponseOk", port = "1234")
  void testResponseOk(MockServer mockServer) throws TransactionMonitoringException {
    directCreditTransactionMonitoringClientV2.checkFinCrimeV2DirectCredit(directCreditBacsPaymentV2);
  }

  @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
  public RequestResponsePact ResponseOkRequiredFields(PactDslWithProvider builder) throws JsonProcessingException {

    return builder
      .given(STATE_V2_EXISTS)
      .uponReceiving(DC_200_RF)
      .path(ENDPOINT)
      .method("POST")
      .body(objectMapString.writeValueAsString(directCreditBacsPaymentRequiredFieldsV2))
      .willRespondWith()
      .status(200)
      .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "ResponseOkRequiredFields", port = "1234")
  void testResponseOkRequiredFields(MockServer mockServer) throws TransactionMonitoringException {
    directCreditTransactionMonitoringClientV2.checkFinCrimeV2DirectCredit(directCreditBacsPaymentRequiredFieldsV2);
  }

  @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
  public RequestResponsePact ResponseBadRequest(PactDslWithProvider builder) throws JsonProcessingException {
    return builder
      .given(STATE_V2_400)
      .uponReceiving(DC_400)
      .path(ENDPOINT)
      .method("POST")
      .body(objectMapString.writeValueAsString(directCreditBacsPaymentBadRequestV2))
      .willRespondWith()
      .status(400)
      .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "ResponseBadRequest", port = "1234")
  void testResponseBadRequest(MockServer mockServer) throws IOException {
    TransactionMonitoringException transactionMonitoringException = assertThrows(
      TransactionMonitoringException.class, () ->
        directCreditTransactionMonitoringClientV2
          .checkFinCrimeV2DirectCredit(directCreditBacsPaymentBadRequestV2));
    assertEquals(transactionMonitoringException.getErrorDetails().getHttpStatusCode(), 400);

  }

  @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
  public RequestResponsePact ResponseInternalServerError(PactDslWithProvider builder) throws JsonProcessingException {
    return builder
      .given(STATE_500)
      .uponReceiving(DC_500)
      .path(ENDPOINT)
      .method("POST")
      .body(objectMapString.writeValueAsString(directCreditBacsPaymentV2))
      .willRespondWith()
      .status(500)
      .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "ResponseInternalServerError", port = "1234")
  void testResponseInternalServerError(MockServer mockServer) throws IOException {
    TransactionMonitoringException transactionMonitoringException = assertThrows(
      TransactionMonitoringException.class, () ->
        directCreditTransactionMonitoringClientV2.checkFinCrimeV2DirectCredit(directCreditBacsPaymentV2));
    assertEquals(transactionMonitoringException.getErrorDetails().getHttpStatusCode(), 500);

  }

}
