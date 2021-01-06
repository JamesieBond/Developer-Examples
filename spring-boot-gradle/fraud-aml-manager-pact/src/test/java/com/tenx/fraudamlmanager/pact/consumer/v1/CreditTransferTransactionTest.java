package com.tenx.fraudamlmanager.pact.consumer.v1;

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
import com.tenx.fraudamlmanager.infrastructure.feedzaimanager.FeedzaiManagerClient;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.CreditTransferTransactionException;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.AccountIdentification4Choice;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.ActiveCurrencyAndAmount;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.BranchAndFinancialInstitutionIdentification6;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.BranchData3;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.BusinessApplicationHeader;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.CashAccount38;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.ClearingSystemIdentification2Choice;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.ClearingSystemMemberIdentification2;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.CreditTransferTransaction39;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.DateAndPlaceOfBirth1;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.FIToFIPaymentStatusReportV11;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.FinancialInstitutionIdentification18;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.FraudCheckRequestV1;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.FraudCheckResponseV1;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.GenericAccountIdentification1;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.GenericOrganisationIdentification1;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.GenericPersonIdentification1;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.GroupHeader93;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.IsoCreditTransferFraudCheckRequestV01;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.OrganisationIdentification29;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.Pacs002;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.Pacs008;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.Party38Choice;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.Party44Choice;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.PartyIdentification135;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.PaymentIdentification7;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.PaymentTransaction110;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.PersonIdentification13;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.PostalAddress24;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.SettlementDateTimeIndication1;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.SettlementTimeRequest2;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.StatusReason6Choice;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.StatusReasonInformation12;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.SupplementaryData1;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.SupplementaryDataEnvelope1;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections4.MapUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(PactConsumerTestExt.class)
public class CreditTransferTransactionTest extends SpringBootTestBase {

  private static final String STATE_V1_SENT= "That Credit Transfer Request was sent to FZM";
  private static final String STATE_400 = "A BAD REQUEST Error occurs for a Credit Transfer Request";
  private static final String STATE_500 = "A Internal Server Error occurs for a Credit Transfer Request";
  private static final String ENDPOINT = "/v1/iso/payments/customer-credit-transfer";
  private static final String CTT_200 = "creditTransferTransaction: Status 200 request";
  private static final String CTT_400 = "creditTransferTransaction: Status 400 request";
  private static final String CTT_500 = "creditTransferTransaction: Status 400 request";
  private static Pacs008 pacs008 = new Pacs008();
  private static Pacs008 pacs008BadRequest = new Pacs008();
  private static Pacs002 pacs002 = new Pacs002();
  private Map<String, String> headers = MapUtils.putAll(new HashMap<>(), new String[]{"Content-Type", "application/json"});
  private Map<String, String> responseHeaders = MapUtils.putAll(new HashMap<>(), new String[]{"Content-Type", "application/json"});

  @Autowired
  private ObjectMapper objectMapString;

  @Autowired
  private FeedzaiManagerClient feedzaiManagerClient;

  @BeforeAll
  public static void init() throws ParseException {
    pacs008 = createRequest();
    pacs008BadRequest = createBadRequest();
    pacs002 = createResponse();
  }

  private static Pacs002 createResponse() throws ParseException {
    Pacs002 pacs002 = new Pacs002();
    pacs002.setCustomerCreditTransferResponse(new FIToFIPaymentStatusReportV11());
    pacs002.getCustomerCreditTransferResponse().setApplicationHeader(new BusinessApplicationHeader());
    pacs002.getCustomerCreditTransferResponse().getApplicationHeader().setBusinessMessageIdentifier("bussines id");
    pacs002.getCustomerCreditTransferResponse().getApplicationHeader().setCreationDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z").parse("2009-12-31 23:59:59.999 +0100"));
    pacs002.getCustomerCreditTransferResponse().getApplicationHeader().setMessageDefinitionIdentifier("message");
    pacs002.getCustomerCreditTransferResponse().getApplicationHeader().setTo(new Party44Choice());
    pacs002.getCustomerCreditTransferResponse().getApplicationHeader().getTo().setOrganisationIdentification(new PartyIdentification135());
    pacs002.getCustomerCreditTransferResponse().getApplicationHeader().getTo().getOrganisationIdentification()
        .setIdentification(new Party38Choice());
    pacs002.getCustomerCreditTransferResponse().getApplicationHeader().getTo().getOrganisationIdentification().setName("string");
    pacs002.getCustomerCreditTransferResponse().getApplicationHeader().getTo().getOrganisationIdentification()
        .getIdentification().setPrivateIdentification(new PersonIdentification13());
    pacs002.getCustomerCreditTransferResponse().getApplicationHeader().getTo().getOrganisationIdentification()
        .getIdentification().getPrivateIdentification().setDateAndPlaceOfBirth(new DateAndPlaceOfBirth1());
    pacs002.getCustomerCreditTransferResponse().getApplicationHeader().getTo().getOrganisationIdentification()
        .getIdentification().getPrivateIdentification().getDateAndPlaceOfBirth().setBirthDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z").parse("2009-12-31 23:59:59.999 +0100"));
    pacs002.getCustomerCreditTransferResponse().getApplicationHeader().getTo().getOrganisationIdentification()
        .getIdentification().getPrivateIdentification().getDateAndPlaceOfBirth().setCityOfBirth("city of birth");
    pacs002.getCustomerCreditTransferResponse().getApplicationHeader().getTo().getOrganisationIdentification()
        .getIdentification().getPrivateIdentification().getDateAndPlaceOfBirth().setCountryOfBirth("country of birth");

    pacs002.getCustomerCreditTransferResponse().getApplicationHeader().setFrom(new Party44Choice());
    pacs002.getCustomerCreditTransferResponse().getApplicationHeader().getFrom().setOrganisationIdentification(new PartyIdentification135());
    pacs002.getCustomerCreditTransferResponse().getApplicationHeader().getFrom().getOrganisationIdentification()
        .setIdentification(new Party38Choice());
    pacs002.getCustomerCreditTransferResponse().getApplicationHeader().getFrom().getOrganisationIdentification().setName("string");
    pacs002.getCustomerCreditTransferResponse().getApplicationHeader().getFrom().getOrganisationIdentification()
        .getIdentification().setPrivateIdentification(new PersonIdentification13());
    pacs002.getCustomerCreditTransferResponse().getApplicationHeader().getFrom().getOrganisationIdentification()
        .getIdentification().getPrivateIdentification().setDateAndPlaceOfBirth(new DateAndPlaceOfBirth1());
    pacs002.getCustomerCreditTransferResponse().getApplicationHeader().getFrom().getOrganisationIdentification()
        .getIdentification().getPrivateIdentification().getDateAndPlaceOfBirth().setBirthDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z").parse("2009-12-31 23:59:59.999 +0100"));
    pacs002.getCustomerCreditTransferResponse().getApplicationHeader().getFrom().getOrganisationIdentification()
        .getIdentification().getPrivateIdentification().getDateAndPlaceOfBirth().setCityOfBirth("city of birth");
    pacs002.getCustomerCreditTransferResponse().getApplicationHeader().getFrom().getOrganisationIdentification()
        .getIdentification().getPrivateIdentification().getDateAndPlaceOfBirth().setCountryOfBirth("country of birth");
    pacs002.getCustomerCreditTransferResponse().setFraudCheckResponse(new FraudCheckResponseV1());
    pacs002.getCustomerCreditTransferResponse().getFraudCheckResponse().setTransactionInformationAndStatus(new ArrayList<>());
    pacs002.getCustomerCreditTransferResponse().getFraudCheckResponse().getTransactionInformationAndStatus().add(new PaymentTransaction110());
    pacs002.getCustomerCreditTransferResponse().getFraudCheckResponse().getTransactionInformationAndStatus()
        .get(0).setSupplementaryData(new ArrayList<>());
    pacs002.getCustomerCreditTransferResponse().getFraudCheckResponse().getTransactionInformationAndStatus()
        .get(0).getSupplementaryData().add(new SupplementaryData1());
    pacs002.getCustomerCreditTransferResponse().getFraudCheckResponse().getTransactionInformationAndStatus()
        .get(0).getSupplementaryData().get(0).setEnvelope(new SupplementaryDataEnvelope1());
    pacs002.getCustomerCreditTransferResponse().getFraudCheckResponse().getTransactionInformationAndStatus()
        .get(0).getSupplementaryData().get(0).getEnvelope().setTransactionTraceIdentification("traceIdentification");
    pacs002.getCustomerCreditTransferResponse().getFraudCheckResponse().getTransactionInformationAndStatus()
        .get(0).setStatusReasonInformation(new ArrayList<>());
    pacs002.getCustomerCreditTransferResponse().getFraudCheckResponse().getTransactionInformationAndStatus()
        .get(0).getStatusReasonInformation().add(new StatusReasonInformation12());
    pacs002.getCustomerCreditTransferResponse().getFraudCheckResponse().getTransactionInformationAndStatus()
        .get(0).getStatusReasonInformation().get(0).setReason(new StatusReason6Choice());
    pacs002.getCustomerCreditTransferResponse().getFraudCheckResponse().getTransactionInformationAndStatus()
        .get(0).getStatusReasonInformation().get(0).getReason().setCode("AM21");
    pacs002.getCustomerCreditTransferResponse().getFraudCheckResponse().getTransactionInformationAndStatus()
        .get(0).getStatusReasonInformation().get(0).setAdditionalInformation(Arrays.asList("transaction limit exceeded"));
    pacs002.getCustomerCreditTransferResponse().getFraudCheckResponse().getTransactionInformationAndStatus()
        .get(0).setTransactionStatus(PaymentTransaction110.TransactionStatusEnum.APPV);

    return pacs002;
  }

  @Pact(provider = "feedzaimanager.fraud", consumer = "fraudamlmanager.fraud")
  public RequestResponsePact creditTransferTransactionRequest(PactDslWithProvider builder) throws JsonProcessingException {
    return builder
        .given(STATE_V1_SENT)
        .uponReceiving(CTT_200)
        .path(ENDPOINT)
        .method("POST")
        .body(objectMapString.writeValueAsString(pacs008))
        .willRespondWith()
        .headers(responseHeaders)
        .body(objectMapString.writeValueAsString(pacs002))
        .status(200)
        .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "creditTransferTransactionRequest", port = "1234")
  void testCreditTransferTransactionRequest(MockServer mockServer) throws CreditTransferTransactionException {
    Pacs002 creditTransferResponse = feedzaiManagerClient.checkFinCrime(pacs008);
  }

  @Pact(provider = "feedzaimanager.fraud", consumer = "fraudamlmanager.fraud")
  public RequestResponsePact creditTransferTransactionResponseBadRequest(PactDslWithProvider builder)
      throws JsonProcessingException {
    return builder
        .given(STATE_400)
        .uponReceiving(CTT_400)
        .path(ENDPOINT)
        .headers(headers)
        .method("POST")
        .body(objectMapString.writeValueAsString(pacs008BadRequest))
        .willRespondWith()/**/
          .headers(responseHeaders)
        .status(400)
        .toPact();
  }


  @Test
  @PactTestFor(pactMethod = "creditTransferTransactionResponseBadRequest", port = "1234")
  void testCreditTransferTransactionResponseBadRequest(MockServer mockServer) throws IOException {
    CreditTransferTransactionException feignException = assertThrows(
        CreditTransferTransactionException.class,
        () -> feedzaiManagerClient.checkFinCrime(pacs008BadRequest));
  }

  @Pact(provider = "feedzaimanager.fraud", consumer = "fraudamlmanager.fraud")
  public RequestResponsePact creditTransferTransactionResponseInternalError(PactDslWithProvider builder)
      throws JsonProcessingException {
    return builder
        .given(STATE_500)
        .uponReceiving(CTT_500)
        .path(ENDPOINT)
        .headers(headers)
        .method("POST")
        .body(objectMapString.writeValueAsString(pacs008))
        .willRespondWith()/**/
        .headers(responseHeaders)
        .status(500)
        .toPact();
  }


  @Test
  @PactTestFor(pactMethod = "creditTransferTransactionResponseInternalError", port = "1234")
  void testCreditTransferTransactionResponseInternalError(MockServer mockServer) throws IOException {
    CreditTransferTransactionException feignException = assertThrows(
        CreditTransferTransactionException.class,
        () -> feedzaiManagerClient.checkFinCrime(pacs008));
  }


  private static Pacs008 createRequest() throws ParseException {
    PaymentIdentification7 paymentIdentification7 = new PaymentIdentification7();
    paymentIdentification7.setEndToEndIdentification("string");

    ActiveCurrencyAndAmount activeCurrencyAndAmount = new ActiveCurrencyAndAmount();
    activeCurrencyAndAmount.setCurrency("AU");
    activeCurrencyAndAmount.setValue(BigDecimal.valueOf(1));

    CashAccount38 cashAccount38 = new CashAccount38();
    cashAccount38.setIdentification(new AccountIdentification4Choice());
    cashAccount38.setCurrency("AU");
    cashAccount38.setName("cashAccount");

    SupplementaryDataEnvelope1 envelope = new SupplementaryDataEnvelope1();
    envelope.setPartyKey("partykey");
    envelope.setSchema("schema");
    envelope.setRoutingDestination("routingDestination");
    envelope.setTransactionTraceIdentification("traceIdentification");

    SupplementaryData1 supplementaryData = new SupplementaryData1();
    supplementaryData.setEnvelope(envelope);
    supplementaryData.setPlaceAndName("placeAndName");

    CreditTransferTransaction39 creditTransferTransaction39 = new CreditTransferTransaction39();
    creditTransferTransaction39.setSettlementDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z").parse("2009-12-31 23:59:59.999 +0100"));
    creditTransferTransaction39.setAcceptanceDateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z").parse("2009-12-31 23:59:59.999 +0100"));
    creditTransferTransaction39.setSettlementAmount(activeCurrencyAndAmount);
    creditTransferTransaction39.getSettlementAmount().setCurrency("UA");
    creditTransferTransaction39.getSettlementAmount().setValue(BigDecimal.valueOf(10));
    creditTransferTransaction39.settlementTimeIndication(new SettlementDateTimeIndication1());
    creditTransferTransaction39.settlementTimeRequest(new SettlementTimeRequest2());
    creditTransferTransaction39.setPaymentIdentification(paymentIdentification7);
    creditTransferTransaction39.getPaymentIdentification().setTransactionIdentification("string");
    creditTransferTransaction39.getPaymentIdentification().setInstructionIdentification("string");
    creditTransferTransaction39.setDebtor(new PartyIdentification135());
    creditTransferTransaction39.getDebtor().setName("string");
    creditTransferTransaction39.getDebtor().setIdentification(new Party38Choice());
    creditTransferTransaction39.getDebtor().getIdentification().setPrivateIdentification(new PersonIdentification13());
    creditTransferTransaction39.getDebtor().getIdentification().getPrivateIdentification()
        .setDateAndPlaceOfBirth(new DateAndPlaceOfBirth1());
    creditTransferTransaction39.getDebtor().getIdentification().getPrivateIdentification()
        .getDateAndPlaceOfBirth().setBirthDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z").parse("2009-12-31 23:59:59.999 +0100"));
    creditTransferTransaction39.getDebtor().getIdentification().getPrivateIdentification()
        .getDateAndPlaceOfBirth().setCityOfBirth("city");
    creditTransferTransaction39.getDebtor().getIdentification().getPrivateIdentification()
        .getDateAndPlaceOfBirth().setCountryOfBirth("country");
    creditTransferTransaction39.getDebtor().getIdentification().getPrivateIdentification()
        .getDateAndPlaceOfBirth().setBirthDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z").parse("2009-12-31 23:59:59.999 +0100"));
    creditTransferTransaction39.getDebtor().getIdentification().getPrivateIdentification().setOther(new ArrayList<>());
    creditTransferTransaction39.getDebtor().getIdentification().getPrivateIdentification()
        .getOther().add(new GenericPersonIdentification1());
    creditTransferTransaction39.getDebtor().getIdentification().getPrivateIdentification()
        .getOther().get(0).setIdentification("identification");
    creditTransferTransaction39.getDebtor().setCountryOfResidence("UA");
    creditTransferTransaction39.setDebtorAgent(new BranchAndFinancialInstitutionIdentification6());
    creditTransferTransaction39.getDebtorAgent().setFinancialInstitutionIdentification(new FinancialInstitutionIdentification18());
    creditTransferTransaction39.getDebtorAgent().getFinancialInstitutionIdentification().setName("name");
    creditTransferTransaction39.getDebtorAgent().getFinancialInstitutionIdentification().setBICFI("BICFI");
    creditTransferTransaction39.getDebtorAgent().getFinancialInstitutionIdentification().setLEI("LEI");
    creditTransferTransaction39.getDebtorAgent().getFinancialInstitutionIdentification()
        .setClearingSystemMemberIdentification(new ClearingSystemMemberIdentification2());
    creditTransferTransaction39.getDebtorAgent().getFinancialInstitutionIdentification()
        .getClearingSystemMemberIdentification().setMemberIdentification("member identification");
    creditTransferTransaction39.getDebtorAgent().getFinancialInstitutionIdentification()
        .getClearingSystemMemberIdentification().setClearingSystemIdentification(new ClearingSystemIdentification2Choice());
    creditTransferTransaction39.getDebtorAgent().setBranchIdentification(new BranchData3());
    creditTransferTransaction39.getDebtorAgent().getBranchIdentification().setIdentification("string");
    creditTransferTransaction39.getDebtorAgent().getBranchIdentification().setLEI("lei");
    creditTransferTransaction39.getDebtorAgent().getBranchIdentification().setName("postalAddress");
    creditTransferTransaction39.getDebtorAgent().getBranchIdentification().setPostalAddress(new PostalAddress24());
    creditTransferTransaction39.getDebtorAgent().getBranchIdentification().getPostalAddress().setCountry("UA");
    creditTransferTransaction39.setCreditor(new PartyIdentification135());
    creditTransferTransaction39.getCreditor().setIdentification(new Party38Choice());
    creditTransferTransaction39.getCreditor().getIdentification().setPrivateIdentification(new PersonIdentification13());
    creditTransferTransaction39.getCreditor().getIdentification().getPrivateIdentification()
        .setDateAndPlaceOfBirth(new DateAndPlaceOfBirth1());
    creditTransferTransaction39.getCreditor().getIdentification().getPrivateIdentification()
        .getDateAndPlaceOfBirth().setBirthDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z").parse("2009-12-31 23:59:59.999 +0100"));
    creditTransferTransaction39.getCreditor().getIdentification().getPrivateIdentification()
        .getDateAndPlaceOfBirth().setCityOfBirth("city of birth");
    creditTransferTransaction39.getCreditor().getIdentification().getPrivateIdentification()
        .getDateAndPlaceOfBirth().setCountryOfBirth("country of birth");
    creditTransferTransaction39.getCreditor().getIdentification().setOrganisationIdentification(new OrganisationIdentification29());
    creditTransferTransaction39.getCreditor().getIdentification().getOrganisationIdentification().setOther(new ArrayList<>());
    creditTransferTransaction39.getCreditor().getIdentification().getOrganisationIdentification().getOther().add(new GenericOrganisationIdentification1());
    creditTransferTransaction39.getCreditor().getIdentification().getOrganisationIdentification().getOther().get(0).setIdentification("identification");
    creditTransferTransaction39.getCreditor().setName("name");
    creditTransferTransaction39.setCreditorAgent(new BranchAndFinancialInstitutionIdentification6());
    creditTransferTransaction39.getCreditorAgent().setFinancialInstitutionIdentification(new FinancialInstitutionIdentification18());
    creditTransferTransaction39.getCreditorAgent().getFinancialInstitutionIdentification().setName("name");
    creditTransferTransaction39.getCreditorAgent().getFinancialInstitutionIdentification().setBICFI("BICFI");
    creditTransferTransaction39.getCreditorAgent().getFinancialInstitutionIdentification().setLEI("LEI");
    creditTransferTransaction39.getCreditorAgent().getFinancialInstitutionIdentification()
        .setClearingSystemMemberIdentification(new ClearingSystemMemberIdentification2());
    creditTransferTransaction39.getCreditorAgent().getFinancialInstitutionIdentification()
        .getClearingSystemMemberIdentification().setMemberIdentification("member identification");
    creditTransferTransaction39.getCreditorAgent().getFinancialInstitutionIdentification()
        .getClearingSystemMemberIdentification().setClearingSystemIdentification(new ClearingSystemIdentification2Choice());
    creditTransferTransaction39.getCreditorAgent().setBranchIdentification(new BranchData3());
    creditTransferTransaction39.getCreditorAgent().getBranchIdentification().setName("string");
    creditTransferTransaction39.getCreditorAgent().getBranchIdentification().setIdentification("identification");
    creditTransferTransaction39.setExchangeRate(1.0);
    creditTransferTransaction39.setCreditorAccount(cashAccount38);
    creditTransferTransaction39.getCreditorAccount().setIdentification(new AccountIdentification4Choice());
    creditTransferTransaction39.getCreditorAccount().getIdentification().setOther(new GenericAccountIdentification1());
    creditTransferTransaction39.getCreditorAccount().getIdentification().getOther().setIdentification("identification");
    creditTransferTransaction39.setCreditorAgentAccount(cashAccount38);
    creditTransferTransaction39.setDebtorAccount(cashAccount38);
    creditTransferTransaction39.getDebtorAccount().setIdentification(new AccountIdentification4Choice());
    creditTransferTransaction39.getDebtorAccount().getIdentification().setOther(new GenericAccountIdentification1());
    creditTransferTransaction39.getDebtorAccount().getIdentification().getOther().setIdentification("identification");
    creditTransferTransaction39.setDebtorAgentAccount(cashAccount38);
    creditTransferTransaction39.setSupplementaryData(Arrays.asList(supplementaryData));
    creditTransferTransaction39.setChargeBearer(CreditTransferTransaction39.ChargeBearerEnum.CRED);

    Pacs008 pacs008 = new Pacs008();
    pacs008.setCustomerCreditTransfer(
        new com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.IsoCreditTransferFraudCheckRequestV01());
    pacs008.getCustomerCreditTransfer().setApplicationHeader(
        new com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.BusinessApplicationHeader());
    pacs008.getCustomerCreditTransfer().setCreditTransferFraudCheckRequest(new FraudCheckRequestV1());
    pacs008.getCustomerCreditTransfer().getCreditTransferFraudCheckRequest()
        .setCreditTransferTransactionInformation(new ArrayList<>());
    pacs008.getCustomerCreditTransfer().getCreditTransferFraudCheckRequest()
        .getCreditTransferTransactionInformation().add(creditTransferTransaction39);
    pacs008.getCustomerCreditTransfer().getCreditTransferFraudCheckRequest()
        .setSupplementaryData(Arrays.asList(supplementaryData));
    pacs008.getCustomerCreditTransfer().getCreditTransferFraudCheckRequest().setGroupHeader(new GroupHeader93());
    pacs008.getCustomerCreditTransfer().getCreditTransferFraudCheckRequest().getGroupHeader().setNumberOfTransactions("1");
    pacs008.getCustomerCreditTransfer().getCreditTransferFraudCheckRequest().getGroupHeader().setMessageIdentification("message");
    pacs008.getCustomerCreditTransfer().getCreditTransferFraudCheckRequest().getGroupHeader().setCreationDateTime(new Date());
    pacs008.getCustomerCreditTransfer().getApplicationHeader().setMessageDefinitionIdentifier("message");
    pacs008.getCustomerCreditTransfer().getApplicationHeader().setBusinessMessageIdentifier("bussines id");
    pacs008.getCustomerCreditTransfer().getApplicationHeader().setCreationDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z").parse("2009-12-31 23:59:59.999 +0100"));
    pacs008.getCustomerCreditTransfer().getApplicationHeader().setFrom(new Party44Choice());
    pacs008.getCustomerCreditTransfer().getApplicationHeader().getFrom().setOrganisationIdentification(new PartyIdentification135());
    pacs008.getCustomerCreditTransfer().getApplicationHeader().getFrom().getOrganisationIdentification().setName("string");
    pacs008.getCustomerCreditTransfer().getApplicationHeader().getFrom().getOrganisationIdentification()
        .setIdentification(new Party38Choice());
    pacs008.getCustomerCreditTransfer().getApplicationHeader().getFrom().getOrganisationIdentification()
        .getIdentification().setPrivateIdentification(new PersonIdentification13());
    pacs008.getCustomerCreditTransfer().getApplicationHeader().getFrom().getOrganisationIdentification()
        .getIdentification().getPrivateIdentification().setDateAndPlaceOfBirth(new DateAndPlaceOfBirth1());
    pacs008.getCustomerCreditTransfer().getApplicationHeader().getFrom().getOrganisationIdentification()
        .getIdentification().getPrivateIdentification().getDateAndPlaceOfBirth().setBirthDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z").parse("2009-12-31 23:59:59.999 +0100"));
    pacs008.getCustomerCreditTransfer().getApplicationHeader().getFrom().getOrganisationIdentification()
        .getIdentification().getPrivateIdentification().getDateAndPlaceOfBirth().setCountryOfBirth("country of birth");
    pacs008.getCustomerCreditTransfer().getApplicationHeader().getFrom().getOrganisationIdentification()
        .getIdentification().getPrivateIdentification().getDateAndPlaceOfBirth().setCityOfBirth("city of birth");
    pacs008.getCustomerCreditTransfer().getApplicationHeader().setTo(new Party44Choice());
    pacs008.getCustomerCreditTransfer().getApplicationHeader().getTo().setOrganisationIdentification(new PartyIdentification135());
    pacs008.getCustomerCreditTransfer().getApplicationHeader().getTo().getOrganisationIdentification().setName("string");
    pacs008.getCustomerCreditTransfer().getApplicationHeader().getTo().getOrganisationIdentification()
        .setIdentification(new Party38Choice());
    pacs008.getCustomerCreditTransfer().getApplicationHeader().getTo().getOrganisationIdentification()
        .getIdentification().setPrivateIdentification(new PersonIdentification13());
    pacs008.getCustomerCreditTransfer().getApplicationHeader().getTo().getOrganisationIdentification()
        .getIdentification().getPrivateIdentification().setDateAndPlaceOfBirth(new DateAndPlaceOfBirth1());
    pacs008.getCustomerCreditTransfer().getApplicationHeader().getTo().getOrganisationIdentification()
        .getIdentification().getPrivateIdentification().getDateAndPlaceOfBirth()
        .setBirthDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z").parse("2009-12-31 23:59:59.999 +0100"));
    pacs008.getCustomerCreditTransfer().getApplicationHeader().getTo().getOrganisationIdentification()
        .getIdentification().getPrivateIdentification().getDateAndPlaceOfBirth().setCountryOfBirth("country of birth");
    pacs008.getCustomerCreditTransfer().getApplicationHeader().getTo().getOrganisationIdentification()
        .getIdentification().getPrivateIdentification().getDateAndPlaceOfBirth().setCityOfBirth("city of birth");
    pacs008.getCustomerCreditTransfer().getCreditTransferFraudCheckRequest()
        .getCreditTransferTransactionInformation().get(0).getSettlementTimeIndication().setCreditDateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z").parse("2009-12-31 23:59:59.999 +0100"));
    pacs008.getCustomerCreditTransfer().getCreditTransferFraudCheckRequest()
        .getCreditTransferTransactionInformation().get(0).getSettlementTimeIndication().setDebitDateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z").parse("2009-12-31 23:59:59.999 +0100"));

    return pacs008;
  }

  private static Pacs008 createBadRequest(){
    Pacs008 pacs008 = new Pacs008();
    pacs008.setCustomerCreditTransfer(new IsoCreditTransferFraudCheckRequestV01());
    pacs008.getCustomerCreditTransfer().setApplicationHeader(new BusinessApplicationHeader());
    pacs008.getCustomerCreditTransfer().setCreditTransferFraudCheckRequest(new FraudCheckRequestV1());
    pacs008.getCustomerCreditTransfer().getCreditTransferFraudCheckRequest().setCreditTransferTransactionInformation(new ArrayList<>());
    pacs008.getCustomerCreditTransfer().getCreditTransferFraudCheckRequest().setGroupHeader(new GroupHeader93());
    pacs008.getCustomerCreditTransfer().getCreditTransferFraudCheckRequest().getCreditTransferTransactionInformation()
        .add(new CreditTransferTransaction39());

    return  pacs008;
  }


}
