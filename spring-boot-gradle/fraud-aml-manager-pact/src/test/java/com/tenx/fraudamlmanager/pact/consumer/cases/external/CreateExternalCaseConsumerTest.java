package com.tenx.fraudamlmanager.pact.consumer.cases.external;

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
import com.tenx.fraudamlmanager.cases.domain.external.ExternalCaseCreationResult;
import com.tenx.fraudamlmanager.cases.infrastructure.governor.CaseAttributeRequest;
import com.tenx.fraudamlmanager.cases.infrastructure.governor.CaseGovernorClient;
import com.tenx.fraudamlmanager.cases.infrastructure.governor.CaseGovernorException;
import com.tenx.fraudamlmanager.cases.infrastructure.governor.external.ExternalCaseRequest;
import feign.FeignException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(PactConsumerTestExt.class)
public class CreateExternalCaseConsumerTest extends SpringBootTestBase {

  private static final String ENDPOINT = "/case-governor/v1/cases";
  private static final String STATE_EXTERNAL_CREATED = "That External Case was created for Create External Case event";
  private static final String STATE_EXTERNAL_400 = "A BAD REQUEST Error occurs for Create External Case event";
  private static final String CASEGOVERNOR_EXTERNAL_200 = "V1 CaseGovernor: Status 200 request";
  private static final String CASEGOVERNOR_EXTERNAL_400 = "V1 CaseGovernor: Status 400 request";
  private static ExternalCaseRequest externalCasesRequest = new ExternalCaseRequest();
  private static ExternalCaseRequest externalCaseRequestBadRequest = new ExternalCaseRequest();

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private CaseGovernorClient caseGovernorClient;

  @BeforeAll
  public static void init() {
    externalCasesRequest =
        new ExternalCaseRequest(
            "ONBOARDING_EXCEPTION",
            new ArrayList<>(List.of(new CaseAttributeRequest(
                    "transactionId",
                    "013e7c73-5714-4e80-ba51-46297d90e1d8663256994707"
                )
            )),            "dc8fa31e-4eea-47d7-a8de-8f91cbe3d4b5",
            "e2962e5f-bf1d-47e3-af9a-6aca0b963207",
            "e2962e5f-bf1d-47e3-af9a-6aca0b963210",
            "SALESFORCE",
            "e2962e5f-bf1d-47e3-af9a-6aca0b963209",
            "e2962e5f-cr7m-47e3-af9a-6aca0b963456",
            "true",
            "NEW"
        );

    externalCaseRequestBadRequest =
        new ExternalCaseRequest(
            "ONBOARDING_EXCEPTION",
            new ArrayList<>(List.of(new CaseAttributeRequest(
                    "transactionId",
                    "013e7c73-5714-4e80-ba51-46297d90e1d8663256994707"
                )
            )),
            "",
            "e2962e5f-bf1d-47e3-af9a-6aca0b963208",
            "e2962e5f-bf1d-47e3-af9a-6aca0b963232",
            "SALESFORCE",
            "e2962e5f-bf1d-47e3-af9a-6aca0b963209",
            "c3962e5f-ag4t-47e3-af9a-6aca0b963209",
            "true",
            "NEW"
        );
  }

  @Pact(provider = "casegovernor.interaction", consumer = "fraudamlmanager.fraud")
  public RequestResponsePact ResponseOK(PactDslWithProvider builder)
      throws JsonProcessingException {

    HashMap<String, String> headers = new HashMap<String, String>();
    headers.put("content-type", "application/vnd.external+json");

    HashMap<String, String> responseHeaders = new HashMap<String, String>();
    responseHeaders.put("content-type", "application/json");

    ExternalCaseCreationResult externalCaseCreateResult =
        new ExternalCaseCreationResult();
    externalCaseCreateResult.setBpmSystemCaseId("e2962e5f-bf1d-47e3-af9a-6aca0b963209");
    externalCaseCreateResult.setTenxCaseId("e2962e5f-bf1d-47e3-af9a-6aca0b963204");
    externalCaseCreateResult.setPartyKey("dc8fa31e-4eea-47d7-a8de-8f91cbe3d4b5");
    externalCaseCreateResult.setBpmSystem("SALESFORCE");


    return builder
        .given(STATE_EXTERNAL_CREATED)
        .uponReceiving(CASEGOVERNOR_EXTERNAL_200)
        .path(ENDPOINT)
        .headers(headers)
        .method("POST")
        .body(objectMapper.writeValueAsString(externalCasesRequest))
        .willRespondWith()
        .headers(responseHeaders)
        .body(objectMapper.writeValueAsString(externalCaseCreateResult))
        .status(201)
        .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "ResponseOK", port = "1234")
  void testResponseOk(MockServer mockServer) throws IOException, CaseGovernorException {
    caseGovernorClient.createExternalCase(externalCasesRequest);
  }

  @Pact(provider = "casegovernor.interaction", consumer = "fraudamlmanager.fraud")
  public RequestResponsePact ResponseBadRequest(PactDslWithProvider builder)
      throws JsonProcessingException {

    HashMap<String, String> headers = new HashMap<String, String>();
    headers.put("content-type", "application/vnd.external+json");

    HashMap<String, String> responseHeaders = new HashMap<String, String>();
    responseHeaders.put("content-type", "application/json");

    String response = "{\"timestamp\":\"2020-10-16T09:31:59.492+0000\",\"status\": 400,\"error\": \"BAD_REQUEST\",\"errors\": "
        + "[{\"field\":\"primaryPartyKey\", \"message\":\"primaryPartyKey cannot be null or empty\"}], \"message\":\"Missing mandatory field in request object\"}";
    return builder
        .given(STATE_EXTERNAL_400)
        .uponReceiving(CASEGOVERNOR_EXTERNAL_400)
        .path(ENDPOINT)
        .headers(headers)
        .method("POST")
        .body(objectMapper.writeValueAsString(externalCaseRequestBadRequest))
        .willRespondWith() /**/
        .headers(responseHeaders)
        .body(response)
        .status(400)
        .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "ResponseBadRequest", port = "1234")
  void testResponseBadRequest(MockServer mockServer) throws IOException {
    FeignException feignException =
        assertThrows(
            FeignException.class,
            () ->
                caseGovernorClient.createExternalCase(externalCaseRequestBadRequest));
    assertEquals(feignException.status(), 400);
  }
}
