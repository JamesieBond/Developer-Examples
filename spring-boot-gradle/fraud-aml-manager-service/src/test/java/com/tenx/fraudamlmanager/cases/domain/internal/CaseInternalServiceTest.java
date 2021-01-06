package com.tenx.fraudamlmanager.cases.domain.internal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraudamlmanager.cases.domain.CaseDetails;
import com.tenx.fraudamlmanager.cases.domain.CaseGovernorConnector;
import com.tenx.fraudamlmanager.cases.domain.FinCrimeCheckCase;
import com.tenx.fraudamlmanager.cases.domain.FinCrimeCheckCaseStatus;
import com.tenx.fraudamlmanager.cases.domain.PaymentCaseException;
import com.tenx.fraudamlmanager.cases.infrastructure.governor.CaseGovernorException;
import com.tenx.fraudamlmanager.cases.v2.domain.CaseCreationResult;
import com.tenx.fraudamlmanager.cases.v2.domain.CaseV2;
import com.tenx.fraudamlmanager.cases.v2.domain.PaymentCaseDataStore;
import com.tenx.fraudamlmanager.cases.v2.infrastructure.TransactionCaseDataStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class CaseInternalServiceTest {

  private InternalCasesService internalCasesService;

  @MockBean
  private TransactionCaseDataStore transactionCaseDataStore;
  @MockBean
  private PaymentCaseDataStore paymentCaseDataStore;
  @MockBean
  private CaseGovernorConnector caseGovernorConnector;

  @Captor
  private ArgumentCaptor<InternalCases> internalCasesArgumentCaptor;

  @BeforeEach
  public void beforeEach() {
    this.internalCasesService = new InternalCasesService(transactionCaseDataStore, paymentCaseDataStore,
        caseGovernorConnector);
  }

  @Test
  void referredFinCrimeCheckCaseInternal()
      throws PaymentCaseException, CaseGovernorException {
    FinCrimeCheckCase finCrimeCheckCase = new FinCrimeCheckCase("transactionId", FinCrimeCheckCaseStatus.REFERRED);
    CaseCreationResult caseCreationResult = new CaseCreationResult("caseId", "partyKeyId");
    List<CaseCreationResult> caseCreationResults = new ArrayList<>();
    caseCreationResults.add(caseCreationResult);

    given(caseGovernorConnector.createInternalCases(any(InternalCases.class))).willReturn(caseCreationResults);
    given(paymentCaseDataStore.findCaseByTransactionId("transactionId")).willReturn(Optional.ofNullable(new CaseV2()));

    internalCasesService.processCaseForFinCrimeCheckResult(finCrimeCheckCase);

    verify(paymentCaseDataStore, times(1)).findCaseByTransactionId(eq("transactionId"));
    verify(caseGovernorConnector, times(1)).createInternalCases(internalCasesArgumentCaptor.capture());
    verify(transactionCaseDataStore, times(1)).saveTransactionCase(eq("transactionId"), eq("caseId"));

    InternalCases internalCases = internalCasesArgumentCaptor.getValue();
  }

  @Test
  void nonReferredExternalCaseDetailsFinCrimeCheckCaseInternal()
      throws PaymentCaseException, CaseGovernorException {
    FinCrimeCheckCase finCrimeCheckCase = new FinCrimeCheckCase("transactionId", FinCrimeCheckCaseStatus.PASSED);
    finCrimeCheckCase.getExternalCases()
        .add(new CaseDetails("sourceSystem", "FRAUD_EXCEPTION", "sourceCaseId", "comments", true));

    CaseCreationResult caseCreationResult = new CaseCreationResult("caseId", "partyKeyId");
    List<CaseCreationResult> caseCreationResults = new ArrayList<>();
    caseCreationResults.add(caseCreationResult);

    given(caseGovernorConnector.createInternalCases(any(InternalCases.class))).willReturn(caseCreationResults);
    given(paymentCaseDataStore.findCaseByTransactionId("transactionId")).willReturn(Optional.ofNullable(new CaseV2()));

    internalCasesService.processCaseForFinCrimeCheckResult(finCrimeCheckCase);

    verify(paymentCaseDataStore, times(1)).findCaseByTransactionId(eq("transactionId"));
    verify(caseGovernorConnector, times(1)).createInternalCases(internalCasesArgumentCaptor.capture());
    verify(transactionCaseDataStore, times(1)).saveTransactionCase(eq("transactionId"), eq("caseId"));

    InternalCases internalCases = internalCasesArgumentCaptor.getValue();
  }

  private static Stream<Arguments> finCrimeCheckStatusesForProcessCase() {
    return Stream.of(
        Arguments.of(FinCrimeCheckCaseStatus.REJECTED),
        Arguments.of(FinCrimeCheckCaseStatus.BLOCKED),
        Arguments.of(FinCrimeCheckCaseStatus.PASSED),
        Arguments.of(FinCrimeCheckCaseStatus.CANCELLED)
    );
  }

  private static Stream<Arguments> finCrimeCheckStatusesForDeleteCase() {
    return Stream.of(
        Arguments.of(FinCrimeCheckCaseStatus.REFERRED, 0),
        Arguments.of(FinCrimeCheckCaseStatus.BLOCKED, 0),
        Arguments.of(FinCrimeCheckCaseStatus.REJECTED, 1),
        Arguments.of(FinCrimeCheckCaseStatus.PASSED, 1),
        Arguments.of(FinCrimeCheckCaseStatus.CANCELLED, 1)
    );
  }

  @ParameterizedTest
  @MethodSource("finCrimeCheckStatusesForProcessCase")
  public void finCrimeCheckCaseInternalTest(FinCrimeCheckCaseStatus caseStatus)
      throws PaymentCaseException, CaseGovernorException {

    FinCrimeCheckCase finCrimeCheckCase = new FinCrimeCheckCase("transactionId", caseStatus);

    internalCasesService.processCaseForFinCrimeCheckResult(finCrimeCheckCase);

    verify(paymentCaseDataStore, times(0)).findCaseByTransactionId(anyString());
    verify(caseGovernorConnector, times(0)).createInternalCases(any(InternalCases.class));
    verify(transactionCaseDataStore, times(0)).saveTransactionCase(anyString(), anyString());
    verify(paymentCaseDataStore, times(1)).deleteCaseByTransactionId(eq("transactionId"));
  }

  @ParameterizedTest
  @MethodSource("finCrimeCheckStatusesForDeleteCase")
  public void cleanupCaseWithOutcomeTest(FinCrimeCheckCaseStatus caseStatus, int paymentDataStoreInteractions)
      throws PaymentCaseException {

    FinCrimeCheckCase finCrimeCheckCase = new FinCrimeCheckCase("transactionId", caseStatus);

    internalCasesService.cleanupCaseWithFinalOutcome(finCrimeCheckCase);
    verify(paymentCaseDataStore, times(paymentDataStoreInteractions)).deleteCaseByTransactionId(eq("transactionId"));
  }

}