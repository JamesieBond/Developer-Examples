package com.tenx.fraudamlmanager.cases.domain.external;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.tenx.fraudamlmanager.SpringBootTestBase;
import com.tenx.fraudamlmanager.cases.infrastructure.governor.CaseAttributeRequest;
import com.tenx.fraudamlmanager.cases.infrastructure.governor.external.ExternalCaseRequest;
import com.tenx.fraudamlmanager.cases.infrastructure.governor.external.ExternalCaseUpdateRequest;
import com.tenx.fraudamlmanager.cases.infrastructure.governor.external.ExternalCaseUpdateRequestDetails;
import com.tenx.fraudamlmanager.cases.v2.domain.CaseV2;
import com.tenx.fraudamlmanager.cases.v2.infrastructure.PaymentCaseEntityV2;
import com.tenx.fraudamlmanager.cases.v2.infrastructure.PaymentCaseRepositoryV2;
import com.tenx.fraudamlmanager.cases.v2.infrastructure.TransactionCaseEntity;
import com.tenx.fraudamlmanager.cases.v2.infrastructure.TransactionCaseRepository;
import com.tenx.fraudamlmanager.infrastructure.transactionmanager.TransactionManagerClient;
import com.tenx.fraudamlmanager.paymentsv2.domestic.out.domain.DomesticOutPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.api.ExternalCaseDetailsRequestV2;
import com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.api.FinCrimeCheckResultRequestV2;
import com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.api.FraudAMLSanctionsCheckResponseCodeV2;
import java.util.Arrays;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@Sql("/schema-hsqldb.sql")
@AutoConfigureMockMvc
@TestPropertySource(properties = "CASE_CREATION_TYPE=EXTERNAL")
public class ExternalCaseIntegrationTest extends SpringBootTestBase {

  private static final String FIN_CRIME_CHECK_RESULT = "/v2/payments/finCrimeCheckResult";

  private static WireMockServer wireMockServer;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private PaymentCaseRepositoryV2 paymentCaseRepositoryV2;

  @Autowired
  private TransactionCaseRepository transactionCaseRepository;

  @MockBean
  private TransactionManagerClient transactionManagerClient;

  @BeforeAll
  public static void beforeEach() {
    wireMockServer = new WireMockServer(wireMockConfig().port(1234));
    wireMockServer.start();
    WireMock.configureFor("localhost", wireMockServer.port());
  }

  @AfterAll
  public static void teardown() {
    wireMockServer.stop();
  }

  private void resetData() {
    paymentCaseRepositoryV2.deleteAll();
    transactionCaseRepository.deleteAll();
  }

  private void initExternalCaseCreationData() {
    resetData();
    PaymentCaseEntityV2 paymentCaseEntityV2 = new PaymentCaseEntityV2();
    paymentCaseEntityV2.setTransactionId("txnId");
    paymentCaseEntityV2.setPaymentType(DomesticOutPaymentV2.class.getSimpleName());
    CaseV2 caseV2 = new CaseV2();
    caseV2.setCaseType("caseType");
    caseV2.add("key1", "val1");
    caseV2.setPrimaryPartyKey("pKey");
    caseV2.setSecondaryPartyKey("spKey");
    caseV2.setSubscriptionKey("sKey");
    paymentCaseEntityV2.setPaymentCase(caseV2);
    paymentCaseRepositoryV2.save(paymentCaseEntityV2);
  }

  private void initExternalCaseUpdateData() {
    resetData();
    PaymentCaseEntityV2 paymentCaseEntityV2Update = new PaymentCaseEntityV2();
    paymentCaseEntityV2Update.setTransactionId("txnCaseUpdateId");
    paymentCaseEntityV2Update.setPaymentType(DomesticOutPaymentV2.class.getSimpleName());
    CaseV2 caseV2Update = new CaseV2();
    caseV2Update.setCaseType("caseType");
    caseV2Update.add("key1", "val1");
    caseV2Update.setPrimaryPartyKey("pKey");
    caseV2Update.setSecondaryPartyKey("spKey");
    caseV2Update.setSubscriptionKey("sKey");
    paymentCaseEntityV2Update.setPaymentCase(caseV2Update);
    paymentCaseRepositoryV2.save(paymentCaseEntityV2Update);

    TransactionCaseEntity transactionCaseEntity = new TransactionCaseEntity();
    transactionCaseEntity.setCaseId("caseUpdateIdInDb");
    transactionCaseEntity.setTransactionId("txnCaseUpdateId");
    transactionCaseRepository.save(transactionCaseEntity);
  }

  private void stubCaseGovernorCaseCreationRequest() throws Exception {
    ExternalCaseCreationResult externalCaseCreationResult = new ExternalCaseCreationResult();
    externalCaseCreationResult.setBpmSystem("DYNAMO_FRAUD_ENGINE");
    externalCaseCreationResult.setBpmSystemCaseId("sourceCaseId");
    externalCaseCreationResult.setTenxCaseId("tenxCaseId");
    externalCaseCreationResult.setPartyKey("pKey");

    stubFor(WireMock.post(urlEqualTo("/case-governor/v1/cases"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(objectMapper.writeValueAsString(externalCaseCreationResult))));
  }

  private void stubCaseGovernorCaseUpdateRequest(String caseId) throws Exception {
    ExternalCaseUpdateResult externalCaseUpdateResult = new ExternalCaseUpdateResult(
        new ExternalCaseUpdateResultDetails(caseId, "tenxCaseId"));
    stubFor(WireMock.post(urlEqualTo("/case-governor/v1/cases/" + caseId))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(objectMapper.writeValueAsString(externalCaseUpdateResult))
        ));
  }

  private void verifyExternalCaseCreateRequestIsPosted() throws Exception {
    ExternalCaseRequest externalCaseRequest = new ExternalCaseRequest();
    externalCaseRequest.setAttributes(Arrays.asList(new CaseAttributeRequest("key1", "val1")));
    externalCaseRequest.setBpmSystem("DYNAMO_FRAUD_ENGINE");
    externalCaseRequest.setBpmSystemCaseId("sourceCaseId");
    externalCaseRequest.setCaseType("FRAUD_EXCEPTION_EXTERNAL");
    externalCaseRequest.setPrimaryPartyKey("pKey");
    externalCaseRequest.setSecondaryPartyKey("spKey");
    externalCaseRequest.setSubscriptionKey("sKey");
    externalCaseRequest.setDisplayToCustomer("true");
    externalCaseRequest.setStatus(FraudAMLSanctionsCheckResponseCodeV2.REFERRED.name());

    //check external case request created as expected
    WireMock.verify(postRequestedFor(urlEqualTo("/case-governor/v1/cases"))
        .withHeader("Content-Type", equalTo("application/vnd.external+json"))
        .withRequestBody(equalTo(objectMapper.writeValueAsString(externalCaseRequest)))
    );
  }

  private void verifyExternalCaseUpdateRequestIsPosted(String caseId) throws Exception {
    ExternalCaseUpdateRequest externalCaseUpdateRequest = new ExternalCaseUpdateRequest();
    ExternalCaseUpdateRequestDetails externalCaseUpdateRequestDetails = new ExternalCaseUpdateRequestDetails();
    externalCaseUpdateRequestDetails
        .setAttributes(Arrays.asList(new CaseAttributeRequest("key1", "val1")));
    externalCaseUpdateRequestDetails.setOutcome("PAYMENT SENT TO BENEFICIARY");
    externalCaseUpdateRequestDetails.setStatus(FraudAMLSanctionsCheckResponseCodeV2.PASSED.name());
    externalCaseUpdateRequestDetails.setSubscriptionKey("sKey");
    externalCaseUpdateRequest.setExternalCaseUpdateRequestDetails(externalCaseUpdateRequestDetails);

    //check external case update request created as expected
    WireMock.verify(postRequestedFor(urlEqualTo("/case-governor/v1/cases/" + caseId))
        .withHeader("Content-Type", equalTo("application/json"))
        .withRequestBody(equalTo(objectMapper.writeValueAsString(externalCaseUpdateRequest)))
    );
  }

  private void verifyTransactionCaseIsCreated() {
    //check transaction case is created when Case Governor returns a valid response
    await().until(() -> transactionCaseRepository.findByCaseId("sourceCaseId") != null);
    TransactionCaseEntity transactionCaseEntity = transactionCaseRepository
        .findByCaseId("sourceCaseId");
    assertThat(transactionCaseEntity.getTransactionId(), Matchers.equalTo("txnId"));
  }

  private void verifyPaymentCaseIsDeleted(String transactionId) {
    //check payment case is deleted
    await().until(() -> paymentCaseRepositoryV2.findByTransactionId(transactionId) == null);
  }

  private String buildFinCrimeCheckResultPayload(String transactionId,
      FraudAMLSanctionsCheckResponseCodeV2 status) throws Exception {
    ExternalCaseDetailsRequestV2 externalCaseDetailsRequestV2 = new ExternalCaseDetailsRequestV2(
        "DYNAMO_FRAUD_ENGINE", "FRAUD_EXCEPTION_EXTERNAL", "sourceCaseId", "comments", true);
    FinCrimeCheckResultRequestV2 finCrimeCheckResult = new FinCrimeCheckResultRequestV2(
        transactionId,
        status, Arrays.asList(externalCaseDetailsRequestV2));

    return objectMapper.writeValueAsString(finCrimeCheckResult);
  }

  @Test
  public void externalCaseCreation() throws Exception {
    initExternalCaseCreationData();
    stubCaseGovernorCaseCreationRequest();

    mockMvc.perform(post(FIN_CRIME_CHECK_RESULT)
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .content(buildFinCrimeCheckResultPayload("txnId",
            FraudAMLSanctionsCheckResponseCodeV2.REFERRED)))
        .andExpect(status().is2xxSuccessful());

    verifyExternalCaseCreateRequestIsPosted();
    verifyTransactionCaseIsCreated();
  }

  @Test
  public void externalCaseUpdate() throws Exception {
    initExternalCaseUpdateData();
    stubCaseGovernorCaseUpdateRequest("caseUpdateIdInDb");
    mockMvc.perform(post(FIN_CRIME_CHECK_RESULT)
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .content(buildFinCrimeCheckResultPayload("txnCaseUpdateId",
            FraudAMLSanctionsCheckResponseCodeV2.PASSED)))
        .andExpect(status().is2xxSuccessful());

    verifyExternalCaseUpdateRequestIsPosted("caseUpdateIdInDb");
    verifyPaymentCaseIsDeleted("txnCaseUpdateId");
  }

  @Test
  public void externalCaseCreationAndUpdate() throws Exception {
    initExternalCaseCreationData();
    stubCaseGovernorCaseCreationRequest();
    stubCaseGovernorCaseUpdateRequest("sourceCaseId");
    mockMvc.perform(post(FIN_CRIME_CHECK_RESULT)
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .content(buildFinCrimeCheckResultPayload("txnId",
            FraudAMLSanctionsCheckResponseCodeV2.REFERRED)))
        .andExpect(status().is2xxSuccessful());

    verifyExternalCaseCreateRequestIsPosted();
    verifyTransactionCaseIsCreated();

    mockMvc.perform(post(FIN_CRIME_CHECK_RESULT)
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .content(
            buildFinCrimeCheckResultPayload("txnId", FraudAMLSanctionsCheckResponseCodeV2.PASSED)))
        .andExpect(status().is2xxSuccessful());

    verifyExternalCaseUpdateRequestIsPosted("sourceCaseId");
    verifyPaymentCaseIsDeleted("txnId");
  }

  @Test
  public void exceptionHandlingExternalCaseUpdateGivenCaseCreationNotProcessed() throws Exception {
    resetData();
    mockMvc.perform(post(FIN_CRIME_CHECK_RESULT)
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .content(
            buildFinCrimeCheckResultPayload("txnId", FraudAMLSanctionsCheckResponseCodeV2.PASSED)))
        .andExpect(status().is2xxSuccessful());
  }
}
