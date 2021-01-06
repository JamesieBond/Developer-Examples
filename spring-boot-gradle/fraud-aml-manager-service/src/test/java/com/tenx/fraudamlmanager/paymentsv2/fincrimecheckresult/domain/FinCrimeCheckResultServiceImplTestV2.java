package com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.domain;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraudamlmanager.SpringBootTestBase;
import com.tenx.fraudamlmanager.cases.domain.CaseDetails;
import com.tenx.fraudamlmanager.cases.domain.CaseProcessingService;
import com.tenx.fraudamlmanager.cases.domain.FinCrimeCheckCase;
import com.tenx.fraudamlmanager.cases.domain.FinCrimeCheckCaseStatus;
import com.tenx.fraudamlmanager.cases.domain.PaymentCaseException;
import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.exceptions.TransactionManagerException;
import com.tenx.fraudamlmanager.payments.fincrimecheckresult.domain.FinCrimeCheckResultException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(SpringExtension.class)
class FinCrimeCheckResultServiceImplTestV2 extends SpringBootTestBase{
  @Autowired
  private FinCrimeCheckResultServiceImplV2 finCrimeCheckResultServiceImpl;

  @MockBean
  private CaseProcessingService caseProcessingService;

  @MockBean
  private TransactionManagerConnector transactionManagerConnector;

  @MockBean
  private DomesticFinCrimeCheckResultNotificationService domesticFinCrimeCheckResultNotificationService;

  @Test
  public void checkFinCrimeProcessGivenReceiverIsEmpty()
      throws FinCrimeCheckResultException, TransactionManagerException, FinCrimeCheckResultNotificationException, PaymentCaseException {
    FinCrimeCheckResultV2 finCrimeCheckResult = new FinCrimeCheckResultV2("transactionId",
        FinCrimeCheckResultResponseCodeV2.REFERRED);
    FinCrimeCheckCase finCrimeCheckCase = new FinCrimeCheckCase("transactionId",
        FinCrimeCheckCaseStatus.REFERRED);

    finCrimeCheckResultServiceImpl.updateFinCrimeCheck(finCrimeCheckResult);
    verify(transactionManagerConnector, times(1)).notifyTransactionManager(finCrimeCheckResult);
    verify(domesticFinCrimeCheckResultNotificationService, times(0))
        .notifyDomesticFinCrimeCheckResult(finCrimeCheckResult);
    verify(caseProcessingService, times(1))
        .processCaseForFinCrimeCheckResult(eq(finCrimeCheckCase));
  }

  @Test
  public void checkFinCrimeProcessGivenReceiverIsTransactionManager()
      throws FinCrimeCheckResultException, TransactionManagerException, FinCrimeCheckResultNotificationException, PaymentCaseException {
    ReflectionTestUtils
        .setField(finCrimeCheckResultServiceImpl, "finCrimeCheckResultReceiver",
            FinCrimeCheckResultServiceImplV2.FinCrimeCheckResultReceiver.TRANSACTION_MANAGER);
    FinCrimeCheckResultV2 finCrimeCheckResult = new FinCrimeCheckResultV2("transactionId",
        FinCrimeCheckResultResponseCodeV2.REFERRED);
    FinCrimeCheckCase finCrimeCheckCase = new FinCrimeCheckCase("transactionId",
        FinCrimeCheckCaseStatus.REFERRED);

    finCrimeCheckResultServiceImpl.updateFinCrimeCheck(finCrimeCheckResult);
    verify(transactionManagerConnector, times(1)).notifyTransactionManager(finCrimeCheckResult);
    verify(domesticFinCrimeCheckResultNotificationService, times(0))
        .notifyDomesticFinCrimeCheckResult(finCrimeCheckResult);
    verify(caseProcessingService, times(1))
        .processCaseForFinCrimeCheckResult(eq(finCrimeCheckCase));
  }

  @Test
  public void checkFinCrimeProcessGivenReceiverIsFPSRails()
      throws FinCrimeCheckResultException, TransactionManagerException, FinCrimeCheckResultNotificationException, PaymentCaseException {
    ReflectionTestUtils.setField(finCrimeCheckResultServiceImpl, "finCrimeCheckResultReceiver",
        FinCrimeCheckResultServiceImplV2.FinCrimeCheckResultReceiver.FPSRAILS);
    FinCrimeCheckResultV2 finCrimeCheckResult = new FinCrimeCheckResultV2("transactionId",
        FinCrimeCheckResultResponseCodeV2.REFERRED);
    FinCrimeCheckCase finCrimeCheckCase = new FinCrimeCheckCase("transactionId",
        FinCrimeCheckCaseStatus.REFERRED);

    finCrimeCheckResultServiceImpl.updateFinCrimeCheck(finCrimeCheckResult);
    verify(transactionManagerConnector, times(0)).notifyTransactionManager(finCrimeCheckResult);
    verify(domesticFinCrimeCheckResultNotificationService, times(1))
        .notifyDomesticFinCrimeCheckResult(finCrimeCheckResult);
    verify(caseProcessingService, times(1))
        .processCaseForFinCrimeCheckResult(eq(finCrimeCheckCase));
  }

  @Test
  public void checkFinCrimeProcessCaseEventGivenReceiverIsTransactionManager()
      throws FinCrimeCheckResultException, TransactionManagerException, FinCrimeCheckResultNotificationException, PaymentCaseException {
    ReflectionTestUtils.setField(finCrimeCheckResultServiceImpl, "finCrimeCheckResultReceiver",
        FinCrimeCheckResultServiceImplV2.FinCrimeCheckResultReceiver.TRANSACTION_MANAGER);
    List<ExternalCaseDetailsV2> externalCases = new ArrayList<>();
    externalCases.add(new ExternalCaseDetailsV2());
    FinCrimeCheckResultV2 finCrimeCheckResult = new FinCrimeCheckResultV2("transactionId",
        FinCrimeCheckResultResponseCodeV2.REFERRED,
        externalCases);
    FinCrimeCheckCase finCrimeCheckCase = new FinCrimeCheckCase("transactionId",
        FinCrimeCheckCaseStatus.REFERRED);
    finCrimeCheckCase.getExternalCases().add(new CaseDetails());

    finCrimeCheckResultServiceImpl.updateFinCrimeCheckFromEvent(finCrimeCheckResult);
    verify(transactionManagerConnector, times(1)).notifyTransactionManager(finCrimeCheckResult);
    verify(domesticFinCrimeCheckResultNotificationService, times(0))
        .notifyDomesticFinCrimeCheckResult(finCrimeCheckResult);
    verify(caseProcessingService, times(1))
        .cleanupCaseWithFinalOutcome(eq(finCrimeCheckCase));
  }

  @Test
  public void checkFinCrimeProcessCaseEventGivenReceiverIsFPSRails()
      throws FinCrimeCheckResultException, TransactionManagerException, FinCrimeCheckResultNotificationException, PaymentCaseException {
    ReflectionTestUtils.setField(finCrimeCheckResultServiceImpl, "finCrimeCheckResultReceiver",
        FinCrimeCheckResultServiceImplV2.FinCrimeCheckResultReceiver.FPSRAILS);
    List<ExternalCaseDetailsV2> externalCases = new ArrayList<>();
    externalCases.add(new ExternalCaseDetailsV2());
    FinCrimeCheckResultV2 finCrimeCheckResult = new FinCrimeCheckResultV2("transactionId",
        FinCrimeCheckResultResponseCodeV2.REFERRED,
        externalCases);
    FinCrimeCheckCase finCrimeCheckCase = new FinCrimeCheckCase("transactionId",
        FinCrimeCheckCaseStatus.REFERRED);
    finCrimeCheckCase.getExternalCases().add(new CaseDetails());

    finCrimeCheckResultServiceImpl.updateFinCrimeCheckFromEvent(finCrimeCheckResult);
    verify(transactionManagerConnector, times(0)).notifyTransactionManager(finCrimeCheckResult);
    verify(domesticFinCrimeCheckResultNotificationService, times(1))
        .notifyDomesticFinCrimeCheckResult(finCrimeCheckResult);
    verify(caseProcessingService, times(1))
        .cleanupCaseWithFinalOutcome(eq(finCrimeCheckCase));
  }

  @Test
  public void checkFailedFinCrimeProcessGivenReceiverIsTransactionManager() throws TransactionManagerException{
    ReflectionTestUtils
        .setField(finCrimeCheckResultServiceImpl, "finCrimeCheckResultReceiver",
            FinCrimeCheckResultServiceImplV2.FinCrimeCheckResultReceiver.TRANSACTION_MANAGER);
    FinCrimeCheckResultV2 finCrimeCheckResult = new FinCrimeCheckResultV2("transactionId",
        FinCrimeCheckResultResponseCodeV2.REFERRED);
    FinCrimeCheckCase finCrimeCheckCase = new FinCrimeCheckCase("transactionId",
        FinCrimeCheckCaseStatus.REFERRED);

    willThrow(new TransactionManagerException(500, "Internal Server Error"))
      .given(transactionManagerConnector)
      .notifyTransactionManager(eq(finCrimeCheckResult));

    assertThrows(FinCrimeCheckResultException.class, () ->{
      finCrimeCheckResultServiceImpl.updateFinCrimeCheck(finCrimeCheckResult);
    });

    verify(caseProcessingService, times(1))
        .processCaseForFinCrimeCheckResult(eq(finCrimeCheckCase));
  }

  @Test
  public void checkFailedFinCrimeProcessGivenReceiverIsFPSRails() throws FinCrimeCheckResultNotificationException,
      PaymentCaseException, TransactionManagerException{
    ReflectionTestUtils.setField(finCrimeCheckResultServiceImpl, "finCrimeCheckResultReceiver",
        FinCrimeCheckResultServiceImplV2.FinCrimeCheckResultReceiver.FPSRAILS);
    FinCrimeCheckResultV2 finCrimeCheckResult = new FinCrimeCheckResultV2("transactionId",
        FinCrimeCheckResultResponseCodeV2.REFERRED);
    FinCrimeCheckCase finCrimeCheckCase = new FinCrimeCheckCase("transactionId",
        FinCrimeCheckCaseStatus.REFERRED);


    willThrow(new PaymentCaseException("Internal Server Error"))
        .given(domesticFinCrimeCheckResultNotificationService)
        .notifyDomesticFinCrimeCheckResult(eq(finCrimeCheckResult));

    assertThrows(FinCrimeCheckResultException.class, () -> {
      finCrimeCheckResultServiceImpl.updateFinCrimeCheck(finCrimeCheckResult);
    });

    verify(caseProcessingService, times(1))
        .processCaseForFinCrimeCheckResult(eq(finCrimeCheckCase));
  }

  @Test
  public void checkFailedFinCrimeProcessCaseEventGivenReceiverIsTransactionManager() throws TransactionManagerException{
    ReflectionTestUtils.setField(finCrimeCheckResultServiceImpl, "finCrimeCheckResultReceiver",
        FinCrimeCheckResultServiceImplV2.FinCrimeCheckResultReceiver.TRANSACTION_MANAGER);
    List<ExternalCaseDetailsV2> externalCases = new ArrayList<>();
    externalCases.add(new ExternalCaseDetailsV2());
    FinCrimeCheckResultV2 finCrimeCheckResult = new FinCrimeCheckResultV2("transactionId",
        FinCrimeCheckResultResponseCodeV2.REFERRED,
        externalCases);
    FinCrimeCheckCase finCrimeCheckCase = new FinCrimeCheckCase("transactionId",
        FinCrimeCheckCaseStatus.REFERRED);
    finCrimeCheckCase.getExternalCases().add(new CaseDetails());

    willThrow(new TransactionManagerException(500, "Internal Server Error"))
        .given(transactionManagerConnector)
        .notifyTransactionManager(eq(finCrimeCheckResult));

    assertThrows(FinCrimeCheckResultException.class, () -> {
      finCrimeCheckResultServiceImpl.updateFinCrimeCheckFromEvent(finCrimeCheckResult);
    });

    verify(caseProcessingService, times(1))
        .cleanupCaseWithFinalOutcome(eq(finCrimeCheckCase));
  }

  @Test
  public void checkFailedFinCrimeProcessCaseEventGivenReceiverIsFPSRails() throws FinCrimeCheckResultNotificationException,
      TransactionManagerException, PaymentCaseException{
    ReflectionTestUtils.setField(finCrimeCheckResultServiceImpl, "finCrimeCheckResultReceiver",
        FinCrimeCheckResultServiceImplV2.FinCrimeCheckResultReceiver.FPSRAILS);
    List<ExternalCaseDetailsV2> externalCases = new ArrayList<>();
    externalCases.add(new ExternalCaseDetailsV2());
    FinCrimeCheckResultV2 finCrimeCheckResult = new FinCrimeCheckResultV2("transactionId",
        FinCrimeCheckResultResponseCodeV2.REFERRED,
        externalCases);
    FinCrimeCheckCase finCrimeCheckCase = new FinCrimeCheckCase("transactionId",
        FinCrimeCheckCaseStatus.REFERRED);
    finCrimeCheckCase.getExternalCases().add(new CaseDetails());

    willThrow(new PaymentCaseException("Internal Server Error"))
          .given(domesticFinCrimeCheckResultNotificationService)
          .notifyDomesticFinCrimeCheckResult(eq(finCrimeCheckResult));

    assertThrows(FinCrimeCheckResultException.class, () -> {
      finCrimeCheckResultServiceImpl.updateFinCrimeCheckFromEvent(finCrimeCheckResult);
    });

    verify(caseProcessingService, times(1))
        .cleanupCaseWithFinalOutcome(eq(finCrimeCheckCase));
  }

}
