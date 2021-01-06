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
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain.AccountDetailsDomesticOutReturnV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain.BalanceBeforeDomesticOutReturnV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain.DomesticOutReturnPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain.PaymentAmountDomesticOutReturnV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.infrastructure.DomesticOutReturnTransactionMonitoringClientV2;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(PactConsumerTestExt.class)
public class DomesticOutReturnConsumerTest extends SpringBootTestBase {

  private static final String STATE_V2_EXISTS = "That FeatureSpace client exists for V2 Payments";
  private static final String STATE_V2_400 = "A BAD REQUEST Error occurs for a V2 payment";
  private static final String STATE_500 = "That Internal Server Error Occurs";
  private static final String ENDPOINT = "/v2/payments/domesticPaymentOutboundReturnFinCrimeCheck";
  private static final String DomesticOutReturn_200 = "V2 DomesticOutReturn: Status 200 request";
  private static final String DomesticOutReturn_200_RF = "V2 DomesticOutReturn: Status 200 request with Required Fields";
  private static final String DomesticOutReturn_400 = "V2 DomesticOutReturn: Status 400 request";
  private static final String DomesticOutReturn_500 = "V2 DomesticOutReturn: Status 500 request";
  private static DomesticOutReturnPaymentV2 domesticOutReturnPaymentV2;
  private static DomesticOutReturnPaymentV2 domesticOutReturnPaymentRequiredFieldsV2;
  private static DomesticOutReturnPaymentV2 domesticOutReturnPaymentBadRequestV2;

  @Autowired
  private ObjectMapper objectMapString;
  @Autowired
  private DomesticOutReturnTransactionMonitoringClientV2 domesticOutReturnTransactionMonitoringClientV2;

  @BeforeAll
  public static void init() {

    domesticOutReturnPaymentV2 = new DomesticOutReturnPaymentV2(
      new AccountDetailsDomesticOutReturnV2("AccountNumber", "BankID"), "CreditorFirst CreditorSecond",
      new AccountDetailsDomesticOutReturnV2("AccountNumber", "BankID"), "DebtorFirst DebtorSecond",
      new PaymentAmountDomesticOutReturnV2("GBP", 30, "GBP", 30), "TranID", new Date(), new Date(),
      "TransactionStatus", "TranRef",
      "TranNotes", new ArrayList<String>(), true, new BalanceBeforeDomesticOutReturnV2("GBP", 500.00, "GBP", 500.00),
      "PartyKey");

    domesticOutReturnPaymentRequiredFieldsV2 = new DomesticOutReturnPaymentV2(
      new AccountDetailsDomesticOutReturnV2("Required", "Required"), "Required",
      new AccountDetailsDomesticOutReturnV2("Required", "Required"),
      "Required", new PaymentAmountDomesticOutReturnV2("Required", 30, "Required", 30), "TranID", new Date(), null,
      null, null,
      null, null, null, new BalanceBeforeDomesticOutReturnV2("GBP", 500.00, "GBP", 500.00), "PartyKey");

    domesticOutReturnPaymentBadRequestV2 = new DomesticOutReturnPaymentV2(
      new AccountDetailsDomesticOutReturnV2(null, null), null, new AccountDetailsDomesticOutReturnV2("1", "1"),
      "DebtorFirst DebtorSecond", new PaymentAmountDomesticOutReturnV2("GBP", 30, "GBP", 30), "TranID", new Date(),
      new Date(),
      null, "TranRef", "TranNotes", null, true,
      new BalanceBeforeDomesticOutReturnV2("GBP", 500.00, "GBP", 500.00), null);
  }

  @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
  public RequestResponsePact ResponseOk(PactDslWithProvider builder) throws JsonProcessingException {
    return builder
      .given(STATE_V2_EXISTS)
      .uponReceiving(DomesticOutReturn_200)
      .path(ENDPOINT)
      .method("POST")
      .body(objectMapString.writeValueAsString(domesticOutReturnPaymentV2))
      .willRespondWith()
      .status(200)
      .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "ResponseOk", port = "1234")
  void testResponseOk(MockServer mockServer) throws TransactionMonitoringException {
    domesticOutReturnTransactionMonitoringClientV2.postReturnPayment(domesticOutReturnPaymentV2);
  }

  @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
  public RequestResponsePact ResponseOkRequiredFields(PactDslWithProvider builder) throws JsonProcessingException {
    return builder
      .given(STATE_V2_EXISTS)
      .uponReceiving(DomesticOutReturn_200_RF)
      .path(ENDPOINT)
      .method("POST")
      .body(objectMapString.writeValueAsString(domesticOutReturnPaymentRequiredFieldsV2))
      .willRespondWith()
      .status(200)
      .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "ResponseOkRequiredFields", port = "1234")
  void testResponseOkRequiredFields(MockServer mockServer) throws TransactionMonitoringException {
    domesticOutReturnTransactionMonitoringClientV2.postReturnPayment(domesticOutReturnPaymentRequiredFieldsV2);
  }

  @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
  public RequestResponsePact ResponseBadRequest(PactDslWithProvider builder) throws JsonProcessingException {
    return builder
      .given(STATE_V2_400)
      .uponReceiving(DomesticOutReturn_400)
      .path(ENDPOINT)
      .method("POST")
      .body(objectMapString.writeValueAsString(domesticOutReturnPaymentBadRequestV2))
      .willRespondWith()
      .status(400)
      .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "ResponseBadRequest", port = "1234")
  void testResponseBadRequest(MockServer mockServer) throws IOException {
    TransactionMonitoringException transactionMonitoringException = assertThrows(
      TransactionMonitoringException.class, () ->
        domesticOutReturnTransactionMonitoringClientV2.postReturnPayment(domesticOutReturnPaymentBadRequestV2));
    assertEquals(transactionMonitoringException.getErrorDetails().getHttpStatusCode(), 400);

  }

  @Pact(provider = "transactionmonitoringadapter.fraud", consumer = "fraudamlmanager.fraud")
  public RequestResponsePact ResponseInternalServerError(PactDslWithProvider builder) throws JsonProcessingException {
    return builder
      .given(STATE_500)
      .uponReceiving(DomesticOutReturn_500)
      .path(ENDPOINT)
      .method("POST")
      .body(objectMapString.writeValueAsString(domesticOutReturnPaymentV2))
      .willRespondWith()
      .status(500)
      .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "ResponseInternalServerError", port = "1234")
  void testResponseInternalServerError(MockServer mockServer) throws IOException {
    TransactionMonitoringException transactionMonitoringException = assertThrows(
      TransactionMonitoringException.class, () ->
        domesticOutReturnTransactionMonitoringClientV2.postReturnPayment(domesticOutReturnPaymentV2));
    assertEquals(transactionMonitoringException.getErrorDetails().getHttpStatusCode(), 500);

  }
}
