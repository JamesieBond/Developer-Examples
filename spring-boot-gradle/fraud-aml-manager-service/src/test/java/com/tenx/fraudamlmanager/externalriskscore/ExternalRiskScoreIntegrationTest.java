package com.tenx.fraudamlmanager.externalriskscore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenx.fraud.ExternalRiskScoreEvent;
import com.tenx.fraudamlmanager.SpringBootTestBase;
import com.tenx.fraudamlmanager.externalriskscore.api.ExternalRiskScoreRequest;
import com.tenx.fraudamlmanager.externalriskscore.infrastructure.ExternalRiskScoreEntity;
import com.tenx.fraudamlmanager.externalriskscore.infrastructure.ExternalRiskScoreRepository;
import org.hibernate.HibernateException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.servlet.MockMvc;

/**
 * @author James Spencer
 */
@AutoConfigureMockMvc
public class ExternalRiskScoreIntegrationTest extends SpringBootTestBase {

    private static final String EXTERNAL_RISK_SCORE_URL = "/v1/externalRiskScore";
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;
    @MockBean
    ExternalRiskScoreRepository externalRiskScoreRepository;
    @Captor
    ArgumentCaptor<ExternalRiskScoreEvent> externalRiskScoreEventArgumentCaptor;
    @Captor
    ArgumentCaptor<ExternalRiskScoreEntity> externalRiskScoreEntityArgumentCaptor;
    @MockBean
    private KafkaTemplate<String, ExternalRiskScoreEvent> kafkaProducerTemplate;

    @Test
    public void externalRiskScoreSuccessfulIntegrationRequest() throws Exception {
        ExternalRiskScoreRequest externalRiskScoreRequest = new ExternalRiskScoreRequest(
            "b5c19440-5ad3-465d-8d32-e341fe2cc534", "80", "James");

        mockMvc.perform(post(EXTERNAL_RISK_SCORE_URL)
            .content(objectMapper.writeValueAsString(externalRiskScoreRequest))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is2xxSuccessful());

        verify(kafkaProducerTemplate, times(1))
            .send(any(), externalRiskScoreEventArgumentCaptor.capture());
        ExternalRiskScoreEvent externalRiskScoreEvent = externalRiskScoreEventArgumentCaptor.getValue();
        assertEquals(externalRiskScoreRequest.getPartyKey(), externalRiskScoreEvent.getPartyKey());
        assertEquals(externalRiskScoreRequest.getProvider(), externalRiskScoreEvent.getProvider());
        assertEquals(externalRiskScoreRequest.getRiskScore(), externalRiskScoreEvent.getRiskScore());

        verify(externalRiskScoreRepository, times(1)).save(externalRiskScoreEntityArgumentCaptor.capture());
        ExternalRiskScoreEntity externalRiskScoreEntity = externalRiskScoreEntityArgumentCaptor.getValue();
        assertEquals(externalRiskScoreRequest.getPartyKey(), externalRiskScoreEntity.getPartyKey());
        assertEquals(externalRiskScoreRequest.getProvider(), externalRiskScoreEntity.getProvider());
        assertEquals(externalRiskScoreRequest.getRiskScore(), externalRiskScoreEntity.getRiskScore());
    }

    @Test
    public void externalRiskScoreEntityIntegrationRequest5xxError() throws Exception {
        ExternalRiskScoreRequest externalRiskScoreRequest = new ExternalRiskScoreRequest(
            "b5c19440-5ad3-465d-8d32-e341fe2cc534", "80", "James");

        doThrow(new HibernateException("Failed to save")).when(externalRiskScoreRepository)
            .save(any(ExternalRiskScoreEntity.class));
        mockMvc.perform(post(EXTERNAL_RISK_SCORE_URL)
            .content(objectMapper.writeValueAsString(externalRiskScoreRequest))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is5xxServerError());
    }


    @Test
    public void externalRiskScoreEventIntegrationRequest5xxError() throws Exception {
        ExternalRiskScoreRequest externalRiskScoreRequest = new ExternalRiskScoreRequest(
            "b5c19440-5ad3-465d-8d32-e341fe2cc534", "80", "James");

        doThrow(new KafkaException("Failed to produce Event")).when(kafkaProducerTemplate)
            .send(any(), any(ExternalRiskScoreEvent.class));
        mockMvc.perform(post(EXTERNAL_RISK_SCORE_URL)
            .content(objectMapper.writeValueAsString(externalRiskScoreRequest))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is5xxServerError());
    }
}
