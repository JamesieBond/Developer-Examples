package com.tenx.fraudamlmanager.pact.consumer.cases.update;

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
import com.tenx.fraudamlmanager.cases.domain.external.ExternalCaseUpdateResult;
import com.tenx.fraudamlmanager.cases.domain.external.ExternalCaseUpdateResultDetails;
import com.tenx.fraudamlmanager.cases.infrastructure.governor.CaseAttributeRequest;
import com.tenx.fraudamlmanager.cases.infrastructure.governor.CaseGovernorClient;
import com.tenx.fraudamlmanager.cases.infrastructure.governor.CaseGovernorException;
import com.tenx.fraudamlmanager.cases.infrastructure.governor.external.ExternalCaseUpdateRequest;
import com.tenx.fraudamlmanager.cases.infrastructure.governor.external.ExternalCaseUpdateRequestDetails;
import feign.FeignException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

@ExtendWith(PactConsumerTestExt.class)
public class UpdateCaseConsumerTest extends SpringBootTestBase {

  private static final String ENDPOINT = "/case-governor/v1/cases/";
  private static final String STATE_UPDATE_EXISTS = "That Case exists and was updated for Update Case event";
  private static final String STATE_UPDATE_400 = "A BAD REQUEST Error occurs for Update Case event";
  private static final String CASEGOVERNOR_UPDATE_200 = "V1 External Case Update: Status 200 request";
  private static final String CASEGOVERNOR_UPDATE_400 = "V1 External Case Update: Status 400 request";

  private static ExternalCaseUpdateRequest externalCaseUpdateRequest = new ExternalCaseUpdateRequest();
  private static ExternalCaseUpdateRequest externalCaseUpdateRequestBadRequest = new ExternalCaseUpdateRequest();

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private CaseGovernorClient caseGovernorClient;

  @BeforeAll
  public static void init() {
    externalCaseUpdateRequest =
        new ExternalCaseUpdateRequest(
            new ExternalCaseUpdateRequestDetails(
                new ArrayList<>(List.of(new CaseAttributeRequest(
                        "transactionId",
                        "013e7c73-5714-4e80-ba51-46297d90e1d8663256994707"
                    )
                )),
                "e2962f7t-bf1d-47e3-af9a-6aca0b9632390",
                "string",
                "IN_PROGRESS",
                "e2962e5f-bf1d-47e3-af9a-6aca0b963204"
            )
        );

    externalCaseUpdateRequestBadRequest =
        new ExternalCaseUpdateRequest(
            new ExternalCaseUpdateRequestDetails(
                new ArrayList<>(List.of(new CaseAttributeRequest(
                        "transactionId",
                        "013e7c73-5714-4e80-ba51-46297d90e1d8663256994707"
                    )
                )),
                "e2962f7t-bf1d-47e3-af9a-6aca0b9632390",
                "outcome",
                "",
                "e2962e5f-bf1d-47e3-af9a-6aca0b963459"
            )
        );


  }

  @Pact(provider = "casegovernor.interaction", consumer = "fraudamlmanager.fraud")
  public RequestResponsePact ResponseOK(PactDslWithProvider builder)
    throws JsonProcessingException {
    HashMap<String, String> headers = new HashMap<String, String>();
    headers.put("content-type", MediaType.APPLICATION_JSON_VALUE);
    ExternalCaseUpdateResult externalCaseUpdateResult =
        new ExternalCaseUpdateResult(
            new ExternalCaseUpdateResultDetails(
                "e2962e5f-bf1d-47e3-af9a-6aca0b963456",
                "e2962e5f-bf1d-47e3-af9a-6aca0b963123"
            )
        );

    return builder
        .given(STATE_UPDATE_EXISTS)
        .uponReceiving(CASEGOVERNOR_UPDATE_200)
        .path(ENDPOINT + "e2962e5f-bf1d-47e3-af9a-6aca0b963456")
        .method("POST")
        .headers(headers)
        .body(objectMapper.writeValueAsString(externalCaseUpdateRequest))
        .willRespondWith()
        .body(objectMapper.writeValueAsString(externalCaseUpdateResult))
        .headers(headers)
        .status(200)
        .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "ResponseOK", port = "1234")
  void testResponseOk(MockServer mockServer) throws IOException, CaseGovernorException {
    caseGovernorClient.updateExternalCase("e2962e5f-bf1d-47e3-af9a-6aca0b963456",externalCaseUpdateRequest);
  }

  @Pact(provider = "casegovernor.interaction", consumer = "fraudamlmanager.fraud")
  public RequestResponsePact ResponseBadRequest (PactDslWithProvider builder)
      throws JsonProcessingException {
    HashMap<String, String> headers = new HashMap<String, String>();
    headers.put("content-type", "application/json");

    String response = "{\"timestamp\":\"2020-10-16T09:31:59.492+0000\",\"status\": 400,\"error\": \"BAD_REQUEST\",\"errors\": "
        + "[{\"field\":\"status\", \"message\":\"status cannot be null or empty\"}], \"message\":\"Missing mandatory field in request object\"}";
    return builder
        .given(STATE_UPDATE_400)
        .uponReceiving(CASEGOVERNOR_UPDATE_400)
        .path(ENDPOINT + "e2962e5f-bf1d-47e3-af9a-6aca0b963456")
        .method("POST")
        .headers(headers)
        .body(objectMapper.writeValueAsString(externalCaseUpdateRequestBadRequest))
        .willRespondWith() /**/
        .headers(headers)
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
                caseGovernorClient.updateExternalCase("e2962e5f-bf1d-47e3-af9a-6aca0b963456", externalCaseUpdateRequestBadRequest));
    assertEquals(feignException.status(), 400);
  }
}
