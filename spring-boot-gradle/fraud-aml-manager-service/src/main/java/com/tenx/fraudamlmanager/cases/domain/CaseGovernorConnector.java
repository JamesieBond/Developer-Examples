package com.tenx.fraudamlmanager.cases.domain;

import com.tenx.fraudamlmanager.cases.domain.external.ExternalCase;
import com.tenx.fraudamlmanager.cases.domain.external.ExternalCaseCreationResult;
import com.tenx.fraudamlmanager.cases.domain.external.ExternalCaseUpdateResult;
import com.tenx.fraudamlmanager.cases.domain.internal.InternalCases;
import com.tenx.fraudamlmanager.cases.infrastructure.governor.CaseGovernorException;
import com.tenx.fraudamlmanager.cases.v2.domain.CaseCreationResult;
import java.util.List;

public interface CaseGovernorConnector {

  List<CaseCreationResult> createInternalCases(InternalCases internalCases) throws CaseGovernorException;

  ExternalCaseCreationResult createExternalCase(ExternalCase externalCase)
      throws CaseGovernorException;

  ExternalCaseUpdateResult updateExternalCase(String caseId, ExternalCase externalCase)
      throws CaseGovernorException;
}


