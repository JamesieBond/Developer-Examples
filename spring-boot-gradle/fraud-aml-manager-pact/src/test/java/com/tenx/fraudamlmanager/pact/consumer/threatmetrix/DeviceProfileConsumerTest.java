package com.tenx.fraudamlmanager.pact.consumer.threatmetrix;

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
import com.tenx.fraudamlmanager.deviceprofile.infrastructure.ThreatMetrixAdapterClient;
import feign.FeignException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections4.MapUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

/**
 * @author Niall O'Connell
 */
@ExtendWith(PactConsumerTestExt.class)
public class DeviceProfileConsumerTest extends SpringBootTestBase {

    private static final String ENDPOINT = "/v1/session-query/TEST";
    private static final String STATE_DEVICEPROFILE_EXISTS = "SessionId Exists in TMX";
    private static final String STATE_DEVICEPROFILE_500 = "That Internal Server Error Occurs for Device Profile Request";
    private static final String DEVICEPROFILE_200 = "Device Profile: Status 200 request";
    private static final String DEVICEPROFILE_500 = "Device Profile: Status 500 request";

    private String sessionId = "TEST";

    private Map<String, String> headers = MapUtils
        .putAll(new HashMap<>(), new String[]{"Content-Type", "application/json"});

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ThreatMetrixAdapterClient threatMetrixAdapterClient;

    @Pact(provider = "threatmetrixadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseOk(PactDslWithProvider builder)
        throws JsonProcessingException {

      HashMap<String, String> headers = new HashMap<String, String>();
      headers.put("content-type", MediaType.APPLICATION_JSON_VALUE);

      HashMap<String, String> body = new HashMap<String, String>();
        body.put("device_id", "test");

        return builder
            .given(STATE_DEVICEPROFILE_EXISTS)
            .uponReceiving(DEVICEPROFILE_200)
            .path(ENDPOINT)
            .method("GET")
            .willRespondWith()
            .body(objectMapper.writeValueAsString(body))
            .headers(headers)
            .status(200)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "ResponseOk", port = "1234")
    void testResponseOk(MockServer mockServer) throws IOException {
      HashMap<String, Object> response = threatMetrixAdapterClient.getThreatmetrixData(sessionId);
        assertEquals("test", (String) response.get("device_id"));
    }

    @Pact(provider = "threatmetrixadapter.fraud", consumer = "fraudamlmanager.fraud")
    public RequestResponsePact ResponseInternalServerError(PactDslWithProvider builder)
        throws JsonProcessingException {
        return builder
            .given(STATE_DEVICEPROFILE_500)
            .uponReceiving(DEVICEPROFILE_500)
            .path(ENDPOINT)
            .method("GET")
            .willRespondWith() /**/
            .status(500)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "ResponseInternalServerError", port = "1234")
    void testResponseInternalServerError(MockServer mockServer) throws IOException {
      FeignException feignException = assertThrows(
          FeignException.class, () ->
              threatMetrixAdapterClient.getThreatmetrixData(sessionId)
      );
      assertEquals(feignException.status(), 500);

    }
}
