package com.tenx.fraudamlmanager.paymentsv3.fincrimecheckresult.domain;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenx.fraudamlmanager.cases.domain.CaseDetails;
import com.tenx.fraudamlmanager.cases.domain.CaseProcessingService;
import com.tenx.fraudamlmanager.cases.domain.FinCrimeCheckCase;
import com.tenx.fraudamlmanager.cases.domain.FinCrimeCheckCaseStatus;
import com.tenx.fraudamlmanager.payments.fincrimecheckresult.domain.FinCrimeCheckResultException;
import com.tenx.fraudamlmanager.paymentsv3.domain.ExternalCaseDetailsV3;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootConfiguration
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@SpringBootTest
class FinCrimeCheckResultServiceV3ImplTest {

  private FinCrimeCheckResultServiceV3Impl finCrimeCheckResultServiceV3Impl;

  @MockBean
  private CaseProcessingService paymentCaseService;

  private ObjectMapper mapper = new ObjectMapper();


  @BeforeEach
  public void beforeEach() {
    finCrimeCheckResultServiceV3Impl = new FinCrimeCheckResultServiceV3Impl(paymentCaseService);
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @Test
  public void checkFinCrimeProcess() throws FinCrimeCheckResultException {
    FinCrimeCheckResultV3 finCrimeCheckResult = new FinCrimeCheckResultV3("transactionId",
        FinCrimeCheckResultResponseCodeV3.REFERRED);
    FinCrimeCheckCase finCrimeCheckCase = new FinCrimeCheckCase("transactionId",
        FinCrimeCheckCaseStatus.REFERRED);

    finCrimeCheckResultServiceV3Impl.updateFinCrimeCheck(finCrimeCheckResult);
    verify(paymentCaseService, times(1)).processCaseForFinCrimeCheckResult(eq(finCrimeCheckCase));
  }

  @Test
  public void checkFinCrimeProcessCaseEvent() throws FinCrimeCheckResultException {
    List<ExternalCaseDetailsV3> externalCases = new ArrayList<>();
    externalCases.add(new ExternalCaseDetailsV3());
    FinCrimeCheckResultV3 finCrimeCheckResult = new FinCrimeCheckResultV3("transactionId",
        FinCrimeCheckResultResponseCodeV3.REFERRED,
        externalCases);
    FinCrimeCheckCase finCrimeCheckCase = new FinCrimeCheckCase("transactionId",
        FinCrimeCheckCaseStatus.REFERRED);
    finCrimeCheckCase.getExternalCases().add(new CaseDetails());

    finCrimeCheckResultServiceV3Impl.updateFinCrimeCheckFromEvent(finCrimeCheckResult);
    verify(paymentCaseService, times(1)).cleanupCaseWithFinalOutcome(eq(finCrimeCheckCase));
  }

}
