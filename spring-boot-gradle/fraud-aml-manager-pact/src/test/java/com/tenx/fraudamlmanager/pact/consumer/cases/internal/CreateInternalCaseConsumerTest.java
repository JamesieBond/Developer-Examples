package com.tenx.fraudamlmanager.pact.consumer.cases.internal;

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
import com.tenx.fraudamlmanager.cases.infrastructure.CaseCreationResponse;
import com.tenx.fraudamlmanager.cases.infrastructure.governor.CaseAttributeRequest;
import com.tenx.fraudamlmanager.cases.infrastructure.governor.CaseGovernorClient;
import com.tenx.fraudamlmanager.cases.infrastructure.governor.CaseGovernorException;
import com.tenx.fraudamlmanager.cases.infrastructure.governor.internal.InternalCaseRequest;
import com.tenx.fraudamlmanager.cases.infrastructure.governor.internal.InternalCasesRequest;
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
public class CreateInternalCaseConsumerTest extends SpringBootTestBase {

  private static final String ENDPOINT = "/case-governor/v1/cases";
  private static final String STATE_INTERNAL_CREATED = "That Internal Case was created for Create Internal Case event";
  private static final String STATE_INTERNAL_400 = "A BAD REQUEST Error occurs for Create Internal Case event";
  private static final String CASEGOVERNOR_INTERNAL_200 = "V1 CaseGovernor: Status 200 request";
  private static final String CASEGOVERNOR_INTERNAL_400 = "V1 CaseGovernor: Status 400 request";
  private static InternalCasesRequest internalCasesRequest = new InternalCasesRequest();
  private static InternalCasesRequest internalCasesRequestBadRequest = new InternalCasesRequest();

  @Autowired
  private ObjectMapper objectMapString;

  @Autowired
  private CaseGovernorClient caseGovernorClient;

  @BeforeAll
  public static void init() {
    internalCasesRequest =
        new InternalCasesRequest(
            new ArrayList<>(
                List.of(new InternalCaseRequest(
                    "QUERY_TRANSACTION",
                    new ArrayList<>(List.of(new CaseAttributeRequest(
                            "transactionId",
                            "013e7c73-5714-4e80-ba51-46297d90e1d8663256994707"
                        )
                    )),
                    "b6c4b4d2-22f4-4e89-82a4-b63b17b153f0",
                    "b6c4b4d2-22f4-4e89-82a4-b63b17b153f1",
                    "a30d6796-149c-4b81-9e43-072cfb2ae57e"
                ))
            )
        );

    internalCasesRequestBadRequest =
        new InternalCasesRequest(
            new ArrayList<>(
                List.of(new InternalCaseRequest(
                    "caseType",
                    new ArrayList<>(List.of(new CaseAttributeRequest(
                            "transactionId",
                            "013e7c73-5714-4e80-ba51-46297d90e1d8663256994707"
                        )
                    )),
                    "",
                    "b6c4b4d2-22f4-4e89-82a4-b63b17b153f0",
                    "b6c4b4d2-22f4-4e89-82a4-b63b17b153f9"
                ))
            )
        );
  }

  @Pact(provider = "casegovernor.interaction", consumer = "fraudamlmanager.fraud")
  public RequestResponsePact ResponseOk(PactDslWithProvider builder)
    throws JsonProcessingException {
    HashMap<String, String> headers = new HashMap<String, String>();
    headers.put("content-type", MediaType.APPLICATION_JSON_VALUE);

    HashMap<String, String> body = new HashMap<String, String>();
    List<CaseCreationResponse> responses = new ArrayList<>();
    responses.add(new CaseCreationResponse(
        "b6c4b4d2-22f4-4e89-82a4-b63b17b164f9",
        "b6c4b4d2-22f4-4e89-82a4-b63b17b153f0"
    ));
    return builder
        .given(STATE_INTERNAL_CREATED)
        .uponReceiving(CASEGOVERNOR_INTERNAL_200)
        .headers(headers)
        .path(ENDPOINT)
        .method("POST")
        .body(objectMapString.writeValueAsString(internalCasesRequest))
        .willRespondWith() /**/
        .headers(headers)
        .body(objectMapString.writeValueAsString(responses))
        .status(201)
        .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "ResponseOk", port = "1234")
  void testResponseOk(MockServer mockServer) throws  CaseGovernorException {
    caseGovernorClient.createInternalCase(internalCasesRequest);
  }

  @Pact(provider = "casegovernor.interaction", consumer = "fraudamlmanager.fraud")
  public RequestResponsePact ResponseBadRequest(PactDslWithProvider builder)
      throws JsonProcessingException {
    HashMap<String, String> headers = new HashMap<String, String>();
    headers.put("content-type", MediaType.APPLICATION_JSON_VALUE);

    String response = "{\"timestamp\":\"2020-10-16T09:31:59.492+0000\",\"status\": 400,\"error\": \"BAD_REQUEST\",\"errors\": "
        + "[{\"field\":\"primaryPartyKey\", \"message\":\"primaryPartyKey cannot be null or empty\"}], \"message\":\"Missing mandatory field in request object\"}";
    return builder
        .given(STATE_INTERNAL_400)
        .uponReceiving(CASEGOVERNOR_INTERNAL_400)
        .path(ENDPOINT)
        .method("POST")
        .headers(headers)
        .body(objectMapString.writeValueAsString(internalCasesRequestBadRequest))
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
                caseGovernorClient.createInternalCase(internalCasesRequestBadRequest));
    assertEquals(feignException.status(), 400);
  }

}
