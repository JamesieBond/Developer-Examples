package com.tenx.fraudamlmanager.cases.domain.external;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
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
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class CaseExternalServiceTest {

  private ExternalCasesService externalCasesService;

  @MockBean
  private TransactionCaseDataStore transactionCaseDataStore;
  @MockBean
  private PaymentCaseDataStore paymentCaseDataStore;
  @MockBean
  private CaseGovernorConnector caseGovernorConnector;

  @Captor
  private ArgumentCaptor<ExternalCase> externalCaseArgumentCaptor;

  @BeforeEach
  public void beforeEach() {
    this.externalCasesService = new ExternalCasesService(transactionCaseDataStore, paymentCaseDataStore,
        caseGovernorConnector);
  }

  @Test
  void referredFinCrimeCheckCaseExternal()
      throws PaymentCaseException, CaseGovernorException {
    FinCrimeCheckCase finCrimeCheckCase = new FinCrimeCheckCase("transactionId",
        FinCrimeCheckCaseStatus.REFERRED);
    finCrimeCheckCase.getExternalCases()
        .add(new CaseDetails("sourceSystem", "FRAUD_EXCEPTION", "sourceCaseId", "comments", true));

    ExternalCaseCreationResult externalCaseCreationResult = new ExternalCaseCreationResult(
        "sourceCaseId", "tenxCaseId", "partyKey", "sourceSystem");

    given(caseGovernorConnector.createExternalCase(any(ExternalCase.class)))
        .willReturn(externalCaseCreationResult);
    given(paymentCaseDataStore.findCaseByTransactionId(anyString()))
        .willReturn(Optional.ofNullable(new CaseV2()));

    externalCasesService.processCaseForFinCrimeCheckResult(finCrimeCheckCase);

    verify(paymentCaseDataStore, times(1)).findCaseByTransactionId(eq("transactionId"));
    verify(caseGovernorConnector, times(1)).createExternalCase(externalCaseArgumentCaptor.capture());
    verify(caseGovernorConnector, times(0))
        .updateExternalCase(eq("caseId"), externalCaseArgumentCaptor.capture());
    verify(transactionCaseDataStore, times(1))
        .saveTransactionCase(eq("transactionId"), eq("sourceCaseId"));

    ExternalCase externalCase = externalCaseArgumentCaptor.getValue();
  }

  @Test
  void referredFinCrimeCheckCaseExternalWithoutCaseDetails()
      throws PaymentCaseException, CaseGovernorException {
    FinCrimeCheckCase finCrimeCheckCase = new FinCrimeCheckCase("transactionId",
        FinCrimeCheckCaseStatus.REFERRED);

    ExternalCaseCreationResult externalCaseCreationResult = new ExternalCaseCreationResult(
        "sourceCaseId", "tenxCaseId", "partyKey", "sourceSystem");

    given(caseGovernorConnector.createExternalCase(any(ExternalCase.class)))
        .willReturn(externalCaseCreationResult);
    given(paymentCaseDataStore.findCaseByTransactionId(anyString()))
        .willReturn(Optional.ofNullable(new CaseV2()));

    externalCasesService.processCaseForFinCrimeCheckResult(finCrimeCheckCase);

    verify(paymentCaseDataStore, times(0)).findCaseByTransactionId(eq("transactionId"));
    verify(caseGovernorConnector, times(0)).createExternalCase(externalCaseArgumentCaptor.capture());
    verify(transactionCaseDataStore, times(0)).saveTransactionCase(eq("transactionId"), eq("caseId"));

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
        Arguments.of(FinCrimeCheckCaseStatus.REJECTED, 1),
        Arguments.of(FinCrimeCheckCaseStatus.BLOCKED, 1),
        Arguments.of(FinCrimeCheckCaseStatus.PASSED, 1),
        Arguments.of(FinCrimeCheckCaseStatus.CANCELLED, 1)
    );
  }

  @ParameterizedTest
  @MethodSource("finCrimeCheckStatusesForProcessCase")
  public void updateExternalCaseTest(FinCrimeCheckCaseStatus caseStatus)
      throws PaymentCaseException, CaseGovernorException {

    FinCrimeCheckCase finCrimeCheckCase = new FinCrimeCheckCase("transactionId", caseStatus);
    finCrimeCheckCase.getExternalCases()
        .add(new CaseDetails("sourceSystem", "FRAUD_EXCEPTION", "sourceCaseId", "comments", true));

    CaseCreationResult caseCreationResult = new CaseCreationResult("caseId", "partyKeyId");
    given(transactionCaseDataStore.findCaseIdByTransactionId("transactionId")).willReturn("caseId");
    given(paymentCaseDataStore.findCaseByTransactionId("transactionId"))
        .willReturn(Optional.ofNullable(new CaseV2()));

    externalCasesService.processCaseForFinCrimeCheckResult(finCrimeCheckCase);

    verify(transactionCaseDataStore, times(1)).findCaseIdByTransactionId(eq("transactionId"));
    verify(paymentCaseDataStore, times(1)).findCaseByTransactionId(eq("transactionId"));
    verify(caseGovernorConnector, times(1))
        .updateExternalCase(eq("caseId"), externalCaseArgumentCaptor.capture());
    verify(paymentCaseDataStore, times(1)).deleteCaseByTransactionId(eq("transactionId"));

    ExternalCase externalCaseUpdate = externalCaseArgumentCaptor.getValue();
    assertThat(externalCaseUpdate.getBpmSystemCaseId(), equalTo("caseId"));
    assertThat(externalCaseUpdate.getStatus(), equalTo(caseStatus.name()));
    assertThat(externalCaseUpdate.getOutcome(), notNullValue());
  }

  @ParameterizedTest
  @MethodSource("finCrimeCheckStatusesForProcessCase")
  public void skipUpdateExternalCaseGivenSourceCaseIdIsNotProvided(
      FinCrimeCheckCaseStatus caseStatus)
      throws PaymentCaseException, CaseGovernorException {

    FinCrimeCheckCase finCrimeCheckCase = new FinCrimeCheckCase("transactionId", caseStatus);
    finCrimeCheckCase.getExternalCases()
        .add(new CaseDetails("sourceSystem", "FRAUD_EXCEPTION", "", "comments", true));

    CaseCreationResult caseCreationResult = new CaseCreationResult("caseId", "partyKeyId");
    given(transactionCaseDataStore.findCaseIdByTransactionId("transactionId")).willReturn("caseId");
    given(paymentCaseDataStore.findCaseByTransactionId("transactionId"))
        .willReturn(Optional.ofNullable(new CaseV2()));

    externalCasesService.processCaseForFinCrimeCheckResult(finCrimeCheckCase);

    verify(transactionCaseDataStore, times(0)).findCaseIdByTransactionId(any());
    verify(paymentCaseDataStore, times(0)).findCaseByTransactionId(any());
    verify(caseGovernorConnector, times(0))
        .updateExternalCase(any(), any());
    verify(paymentCaseDataStore, times(0)).deleteCaseByTransactionId(any());

  }

  @ParameterizedTest
  @MethodSource("finCrimeCheckStatusesForProcessCase")
  public void updateExternalCaseTestGivenTransactionCaseIsNotCreated(
      FinCrimeCheckCaseStatus caseStatus) throws PaymentCaseException, CaseGovernorException {

    FinCrimeCheckCase finCrimeCheckCase = new FinCrimeCheckCase("transactionId", caseStatus);
    finCrimeCheckCase.getExternalCases()
        .add(new CaseDetails("sourceSystem", "FRAUD_EXCEPTION", "sourceCaseId", "comments", true));
    doThrow(new PaymentCaseException("transaction case id not found"))
        .when(transactionCaseDataStore).findCaseIdByTransactionId("transactionId");

    assertDoesNotThrow(
        () -> externalCasesService.processCaseForFinCrimeCheckResult(finCrimeCheckCase));
    verify(transactionCaseDataStore, times(1)).findCaseIdByTransactionId(eq("transactionId"));
    verify(paymentCaseDataStore, times(0)).findCaseByTransactionId(eq("transactionId"));
    verify(caseGovernorConnector, times(0))
        .updateExternalCase(eq("caseId"), externalCaseArgumentCaptor.capture());
    verify(paymentCaseDataStore, times(0)).deleteCaseByTransactionId(eq("transactionId"));
  }

  @ParameterizedTest
  @MethodSource("finCrimeCheckStatusesForProcessCase")
  public void updateExternalCaseTestGivenPaymentCaseIsNotCreated(FinCrimeCheckCaseStatus caseStatus)
      throws PaymentCaseException, CaseGovernorException {

    FinCrimeCheckCase finCrimeCheckCase = new FinCrimeCheckCase("transactionId", caseStatus);
    finCrimeCheckCase.getExternalCases()
        .add(new CaseDetails("sourceSystem", "FRAUD_EXCEPTION", "sourceCaseId", "comments", true));
    doThrow(new PaymentCaseException("payment case id not found")).when(paymentCaseDataStore)
        .findCaseByTransactionId("transactionId");

    assertDoesNotThrow(
        () -> externalCasesService.processCaseForFinCrimeCheckResult(finCrimeCheckCase));
    verify(transactionCaseDataStore, times(1)).findCaseIdByTransactionId(eq("transactionId"));
    verify(paymentCaseDataStore, times(1)).findCaseByTransactionId(eq("transactionId"));
    verify(caseGovernorConnector, times(0))
        .updateExternalCase(eq("caseId"), externalCaseArgumentCaptor.capture());
    verify(paymentCaseDataStore, times(0)).deleteCaseByTransactionId(eq("transactionId"));
  }

  @ParameterizedTest
  @MethodSource("finCrimeCheckStatusesForProcessCase")
  public void updateExternalCaseTestGivenCaseGovernorIsDown(FinCrimeCheckCaseStatus caseStatus)
      throws PaymentCaseException, CaseGovernorException {

    FinCrimeCheckCase finCrimeCheckCase = new FinCrimeCheckCase("transactionId", caseStatus);
    finCrimeCheckCase.getExternalCases()
        .add(new CaseDetails("sourceSystem", "FRAUD_EXCEPTION", "sourceCaseId", "comments", true));

    given(transactionCaseDataStore.findCaseIdByTransactionId("transactionId")).willReturn("caseId");
    given(paymentCaseDataStore.findCaseByTransactionId("transactionId"))
        .willReturn(Optional.ofNullable(new CaseV2()));
    doThrow(new CaseGovernorException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
        "case governor is down")).when(caseGovernorConnector)
        .updateExternalCase(anyString(), any());

    assertDoesNotThrow(
        () -> externalCasesService.processCaseForFinCrimeCheckResult(finCrimeCheckCase));
    verify(transactionCaseDataStore, times(1)).findCaseIdByTransactionId(eq("transactionId"));
    verify(paymentCaseDataStore, times(1)).findCaseByTransactionId(eq("transactionId"));
    verify(caseGovernorConnector, times(1))
        .updateExternalCase(eq("caseId"), externalCaseArgumentCaptor.capture());
    verify(paymentCaseDataStore, times(0)).deleteCaseByTransactionId(eq("transactionId"));
  }

  @ParameterizedTest
  @MethodSource("finCrimeCheckStatusesForDeleteCase")
  public void cleanupCaseWithOutcomeTest(FinCrimeCheckCaseStatus caseStatus, int paymentDataStoreInteractions)
      throws PaymentCaseException {

    FinCrimeCheckCase finCrimeCheckCase = new FinCrimeCheckCase("transactionId", caseStatus);

    externalCasesService.cleanupCaseWithFinalOutcome(finCrimeCheckCase);
    verify(paymentCaseDataStore, times(paymentDataStoreInteractions)).deleteCaseByTransactionId(eq("transactionId"));
  }

}