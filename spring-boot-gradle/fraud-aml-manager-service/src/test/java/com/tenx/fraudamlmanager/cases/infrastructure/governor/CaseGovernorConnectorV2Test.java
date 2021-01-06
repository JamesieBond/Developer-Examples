package com.tenx.fraudamlmanager.cases.infrastructure.governor;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraudamlmanager.cases.domain.CaseGovernorConnector;
import com.tenx.fraudamlmanager.cases.domain.external.ExternalCase;
import com.tenx.fraudamlmanager.cases.domain.internal.InternalCases;
import com.tenx.fraudamlmanager.cases.infrastructure.CaseCreationResponse;
import com.tenx.fraudamlmanager.cases.infrastructure.governor.external.ExternalCaseRequest;
import com.tenx.fraudamlmanager.cases.infrastructure.governor.internal.InternalCasesRequest;
import feign.FeignException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class CaseGovernorConnectorV2Test {

  private CaseGovernorConnector caseGovernorConnector;

  @MockBean
  private CaseMetrics caseMetrics;

  @MockBean
  private CaseGovernorClient caseGovernorClient;

  @Captor
  private ArgumentCaptor<InternalCasesRequest> internalCasesRequestArgumentCaptor;

  @Captor
  private ArgumentCaptor<ExternalCaseRequest> externalCaseRequestArgumentCaptor;

  @BeforeEach
  public void beforeEach() {
    this.caseGovernorConnector = new CaseGovernorConnectorImpl(caseMetrics, caseGovernorClient);
  }

  @Test
  void createInternalCaseSuccess() throws CaseGovernorException {
    InternalCases internalCases = new InternalCases();

    CaseCreationResponse CaseCreationResponse = new CaseCreationResponse("caseId", "partyKeyId");
    List<CaseCreationResponse> CaseCreationResponses = new ArrayList<>();
    CaseCreationResponses.add(CaseCreationResponse);

    given(caseGovernorClient.createInternalCase(any(InternalCasesRequest.class))).willReturn(CaseCreationResponses);

    caseGovernorConnector.createInternalCases(internalCases);

    verify(caseGovernorClient, times(1)).createInternalCase(internalCasesRequestArgumentCaptor.capture());

    InternalCasesRequest internalCasesRequest = internalCasesRequestArgumentCaptor.getValue();
    Mockito.verify(caseMetrics, times(1)).incrementCasesRequestsToCaseGovernorSuccess();
  }


  @Test
  void createInternalCaseFailure() throws CaseGovernorException {
    InternalCases internalCases = new InternalCases();

    CaseCreationResponse CaseCreationResponse = new CaseCreationResponse("caseId", "partyKeyId");
    List<CaseCreationResponse> CaseCreationResponses = new ArrayList<>();
    CaseCreationResponses.add(CaseCreationResponse);

    given(caseGovernorClient.createInternalCase(any(InternalCasesRequest.class)))
        .willThrow(FeignException.class);

    assertThrows(
        CaseGovernorException.class, () ->
            caseGovernorConnector.createInternalCases(internalCases));

    verify(caseGovernorClient, times(1)).createInternalCase(internalCasesRequestArgumentCaptor.capture());

    InternalCasesRequest internalCasesRequest = internalCasesRequestArgumentCaptor.getValue();
    Mockito.verify(caseMetrics, times(1)).incrementCasesRequestsToCaseGovernorFailed();
  }


  @Test
  void createExternalCaseSuccess() throws CaseGovernorException {
    ExternalCase externalCase = new ExternalCase();
    caseGovernorConnector.createExternalCase(externalCase);
    verify(caseGovernorClient, times(1)).createExternalCase(externalCaseRequestArgumentCaptor.capture());

    ExternalCaseRequest externalCaseRequest = externalCaseRequestArgumentCaptor.getValue();
    Mockito.verify(caseMetrics, times(1)).incrementCasesRequestsToCaseGovernorSuccess();
  }


  @Test
  void createExternalCaseFailure() throws CaseGovernorException {
    ExternalCase externalCase = new ExternalCase();

    CaseCreationResponse CaseCreationResponse = new CaseCreationResponse("caseId", "partyKeyId");
    List<CaseCreationResponse> CaseCreationResponses = new ArrayList<>();
    CaseCreationResponses.add(CaseCreationResponse);

    given(caseGovernorClient.createExternalCase(any(ExternalCaseRequest.class)))
        .willThrow(FeignException.class);

    assertThrows(
        CaseGovernorException.class, () ->
            caseGovernorConnector.createExternalCase(externalCase));

    verify(caseGovernorClient, times(1)).createExternalCase(externalCaseRequestArgumentCaptor.capture());

    ExternalCaseRequest externalCaseRequest = externalCaseRequestArgumentCaptor.getValue();
    Mockito.verify(caseMetrics, times(1)).incrementCasesRequestsToCaseGovernorFailed();
  }


}
