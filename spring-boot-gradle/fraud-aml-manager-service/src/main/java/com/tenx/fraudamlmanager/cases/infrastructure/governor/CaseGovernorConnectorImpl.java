package com.tenx.fraudamlmanager.cases.infrastructure.governor;

import com.tenx.fraudamlmanager.cases.domain.CaseGovernorConnector;
import com.tenx.fraudamlmanager.cases.domain.external.ExternalCase;
import com.tenx.fraudamlmanager.cases.domain.external.ExternalCaseCreationResult;
import com.tenx.fraudamlmanager.cases.domain.external.ExternalCaseUpdateResult;
import com.tenx.fraudamlmanager.cases.domain.internal.InternalCase;
import com.tenx.fraudamlmanager.cases.domain.internal.InternalCases;
import com.tenx.fraudamlmanager.cases.infrastructure.CaseCreationResponse;
import com.tenx.fraudamlmanager.cases.v2.domain.CaseCreationResult;
import feign.FeignException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CaseGovernorConnectorImpl implements CaseGovernorConnector {

  private static final String CASE_GOVERNOR_REQUEST = "Case Governor Fraud Case created for partyKey: {}";
  private static final String CASE_GOVERNOR_UPDATE_REQUEST = "Case Governor Fraud Case updated for caseId: {}";
  private static final String INTERNAL_CASE_CREATE_FAILED = "Internal Case Created Governor Request Failed.";
  private static final String EXTERNAL_CASE_CREATE_FAILED = "External Case Created Governor Request Failed.";
  private static final String EXTERNAL_CASE_UPDATE_FAILED = "External Case Updated Governor Request Failed.";

  private final CaseMetrics caseMetrics;
  private final CaseGovernorClient caseGovernorClient;

  public List<CaseCreationResult> createInternalCases(InternalCases internalCases) throws CaseGovernorException {
    try {
      log.info(CASE_GOVERNOR_REQUEST, extractInternalCasePartyKey(internalCases));
      List<CaseCreationResponse> response = caseGovernorClient
          .createInternalCase(DomainInfrastructureCaseGovernorMapper.MAPPER.toCaseGovernorRequest(internalCases));
      caseMetrics.incrementCasesRequestsToCaseGovernorSuccess();
      return CaseGovernorResponseMapper.MAPPER.toCaseCreationResult(response);
    } catch (FeignException e) {
      caseMetrics.incrementCasesRequestsToCaseGovernorFailed();
      log.error(INTERNAL_CASE_CREATE_FAILED);
      throw new CaseGovernorException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
          INTERNAL_CASE_CREATE_FAILED, e);
    }
  }

  public ExternalCaseCreationResult createExternalCase(ExternalCase externalCase)
      throws CaseGovernorException {
    try {
      log.info(CASE_GOVERNOR_REQUEST, externalCase.getPrimaryPartyKey());
      ExternalCaseCreationResult result = caseGovernorClient
          .createExternalCase(DomainInfrastructureCaseGovernorMapper.MAPPER.toCaseGovernorExternal(externalCase));
      caseMetrics.incrementCasesRequestsToCaseGovernorSuccess();
      return result;
    } catch (FeignException e) {
      caseMetrics.incrementCasesRequestsToCaseGovernorFailed();
      log.error(EXTERNAL_CASE_CREATE_FAILED);
      throw new CaseGovernorException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
          EXTERNAL_CASE_CREATE_FAILED, e);
    }

  }

  public ExternalCaseUpdateResult updateExternalCase(String caseId, ExternalCase externalCase)
      throws CaseGovernorException {
    try {
      log.info(CASE_GOVERNOR_UPDATE_REQUEST, caseId);
      ExternalCaseUpdateResult result = caseGovernorClient.
          updateExternalCase(caseId,DomainInfrastructureCaseGovernorMapper.MAPPER.toCaseGovernorExternalUpdate(externalCase));
      caseMetrics.incrementCasesRequestsToCaseGovernorSuccess();
      return result;
    } catch (FeignException e) {
      caseMetrics.incrementCasesRequestsToCaseGovernorFailed();
      log.error(EXTERNAL_CASE_UPDATE_FAILED, e);
      throw new CaseGovernorException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
          EXTERNAL_CASE_UPDATE_FAILED, e);
    }
  }

  private String extractInternalCasePartyKey(InternalCases internalCases) {
    return internalCases.getCases().stream().findFirst().map(InternalCase::getPrimaryPartyKey).orElse("unknown");
  }
}


