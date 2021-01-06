package com.tenx.fraudamlmanager.externalriskscore.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenx.fraudamlmanager.externalriskscore.domain.ExternalRiskScore;
import com.tenx.fraudamlmanager.externalriskscore.domain.ExternalRiskScoreService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

/**
 * @author James Spencer
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(ExternalRiskScoreController.class)
public class ExternalRiskScoreControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ExternalRiskScoreService externalRiskScoreService;

    private static final String EXTERNAL_RISK_SCORE_URL = "/v1/externalRiskScore";

    @Autowired
    ObjectMapper objectMapString;
    @Captor
    ArgumentCaptor<ExternalRiskScore> externalRiskScoreArgumentCaptor;

    @Test
    public void externalRiskScoreSuccessfulRequest() throws Exception {
        ExternalRiskScoreRequest externalRiskScoreRequest = new ExternalRiskScoreRequest(
            "b5c19440-5ad3-465d-8d32-e341fe2cc534", "80", "James");

        doNothing().when(externalRiskScoreService).generateAndStoreRiskScoreEvent(any());
        mockMvc.perform(post(EXTERNAL_RISK_SCORE_URL)
            .content(objectMapString.writeValueAsString(externalRiskScoreRequest))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is2xxSuccessful());

        Mockito.verify(externalRiskScoreService, times(1))
            .generateAndStoreRiskScoreEvent(externalRiskScoreArgumentCaptor.capture());

        ExternalRiskScore externalRiskScore = externalRiskScoreArgumentCaptor.getValue();

        assertEquals(externalRiskScore.getPartyKey(), externalRiskScoreRequest.getPartyKey());
        assertEquals(externalRiskScore.getProvider(), externalRiskScoreRequest.getProvider());
        assertEquals(externalRiskScore.getRiskScore(), externalRiskScoreRequest.getRiskScore());
    }

    @Test
    public void externalRiskScoreRequest4xxError() throws Exception {
        ExternalRiskScoreRequest externalRiskScoreRequest = new ExternalRiskScoreRequest("", "", "");

        mockMvc.perform(post(EXTERNAL_RISK_SCORE_URL)
            .content(objectMapString.writeValueAsString(externalRiskScoreRequest))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is4xxClientError());
    }


    @Test
    public void externalRiskScoreRequest5xxError() throws Exception {
        ExternalRiskScoreRequest externalRiskScoreRequest = new ExternalRiskScoreRequest(
            "b5c19440-5ad3-465d-8d32-e341fe2cc534", "80", "James");

        doThrow(new ExternalRiskScoreAPIException(500, "mocked exception")).when(externalRiskScoreService)
            .generateAndStoreRiskScoreEvent(any(ExternalRiskScore.class));
        mockMvc.perform(post(EXTERNAL_RISK_SCORE_URL)
            .content(objectMapString.writeValueAsString(externalRiskScoreRequest))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is5xxServerError());
    }
}
