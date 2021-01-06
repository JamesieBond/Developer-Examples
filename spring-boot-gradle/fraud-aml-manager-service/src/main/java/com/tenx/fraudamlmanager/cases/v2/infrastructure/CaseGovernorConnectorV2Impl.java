package com.tenx.fraudamlmanager.cases.v2.infrastructure;

import com.tenx.fraudamlmanager.cases.infrastructure.CaseCreationResponse;
import com.tenx.fraudamlmanager.cases.infrastructure.governor.CaseMetrics;
import com.tenx.fraudamlmanager.cases.v2.domain.CaseCreationResult;
import com.tenx.fraudamlmanager.cases.v2.domain.CaseGovernorConnectorV2;
import com.tenx.fraudamlmanager.cases.v2.domain.CasesListV2;
import com.tenx.fraudamlmanager.payments.cases.infrastructure.CaseGovernorClient;
import feign.FeignException;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CaseGovernorConnectorV2Impl implements CaseGovernorConnectorV2 {

    private static final String CASE_CREATION_BLOCKED_FAILED_MSG = "Case creation for blocked payment failed: {}";
    private static final String CASE_CREATED_MSG = "Case created for caseId: {}";

    private final CaseMetrics caseMetrics;
    private final CaseGovernorClient caseGovernorClient;

    public List<CaseCreationResult> createInternalCases(CasesListV2 casesListV2) {
        try {
            List<CaseCreationResponse> response = caseGovernorClient.createCasesV2(CaseListMapperV2.MAPPER.toCaseGovernorRequest(casesListV2));
            log.info(CASE_CREATED_MSG, getCaseId(response));
            caseMetrics.incrementCasesRequestsToCaseGovernorSuccess();
            return CaseGovernorResponseMapperV2.MAPPER.toCaseCreationResult(response);

        } catch (NoSuchElementException | FeignException e) {
            caseMetrics.incrementCasesRequestsToCaseGovernorFailed();
            log.error(CASE_CREATION_BLOCKED_FAILED_MSG, e.getMessage(), e);
            return null;
        }
    }


    private String getCaseId(List<CaseCreationResponse> response) {

        return response.stream()
                .findFirst()
                .map(CaseCreationResponse::getCaseId)
                .orElse("unknown");
    }

}


