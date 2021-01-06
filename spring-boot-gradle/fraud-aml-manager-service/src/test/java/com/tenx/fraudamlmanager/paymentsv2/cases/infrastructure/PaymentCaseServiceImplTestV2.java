package com.tenx.fraudamlmanager.paymentsv2.cases.infrastructure;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.tenx.fraudamlmanager.cases.domain.CaseAttribute;
import com.tenx.fraudamlmanager.cases.domain.PaymentCaseException;
import com.tenx.fraudamlmanager.cases.infrastructure.CaseCreationResponse;
import com.tenx.fraudamlmanager.cases.v2.domain.CaseAssemblerV2;
import com.tenx.fraudamlmanager.cases.v2.domain.CaseV2;
import com.tenx.fraudamlmanager.cases.v2.domain.CasesListV2;
import com.tenx.fraudamlmanager.cases.v2.infrastructure.PaymentCaseEntityV2;
import com.tenx.fraudamlmanager.cases.v2.infrastructure.PaymentCaseRepositoryV2;
import com.tenx.fraudamlmanager.cases.v2.infrastructure.TransactionCaseDataStore;
import com.tenx.fraudamlmanager.payments.cases.infrastructure.CaseGovernorClient;
import com.tenx.fraudamlmanager.paymentsv2.domestic.out.domain.DomesticOutPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.onus.domain.AccountDetailsOnUsV2;
import com.tenx.fraudamlmanager.paymentsv2.onus.domain.BalanceBeforeOnUsV2;
import com.tenx.fraudamlmanager.paymentsv2.onus.domain.OnUsPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.onus.domain.PaymentAmountOnUsV2;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class PaymentCaseServiceImplTestV2 {

  @MockBean
  private PaymentCaseRepositoryV2 paymentCaseRepositoryV2;

  @MockBean
  private CaseAssemblerV2 caseAssemblerV2;

  @MockBean
  private CaseGovernorClient caseGovernorClient;

  @MockBean
  private TransactionCaseDataStore transactionCaseDataStore;

  private PaymentCaseServiceImplV2 paymentCaseServiceImplV2;

  @BeforeEach
  public void beforeEach() {
    this.paymentCaseServiceImplV2 = new PaymentCaseServiceImplV2(paymentCaseRepositoryV2, transactionCaseDataStore,
      caseAssemblerV2, caseGovernorClient);
  }

  @Test
  public void checkSavePaymentEventOnUs() {
    OnUsPaymentV2 onUsPaymentV2 = new OnUsPaymentV2();
    onUsPaymentV2.setDebtorName("test");
    onUsPaymentV2.setTransactionId("test");
    onUsPaymentV2.setDebtorName("test");
    onUsPaymentV2.setBalanceBefore(new BalanceBeforeOnUsV2("Test", 500.00, "Test", 500.00));
    onUsPaymentV2.setDebtorAccountDetails(new AccountDetailsOnUsV2("Test", "1234"));
    onUsPaymentV2.setDebtorPartyKey("test");
    onUsPaymentV2.setCreditorName("test");
    onUsPaymentV2.setCreditorAccountDetails(new AccountDetailsOnUsV2("Test", "1234"));
    onUsPaymentV2.setCreditorPartyKey("test");
    onUsPaymentV2.setTransactionId("test");
    onUsPaymentV2.setAmount(new PaymentAmountOnUsV2("test", 1.00, "test", 2.00));
    onUsPaymentV2.setDebtorPartyKey("test");
    onUsPaymentV2.setTransactionDate(new Date());

    CaseAttribute caseAttribute = new CaseAttribute("attributeName", "attributeValue");
    List<CaseAttribute> attributes = new ArrayList<CaseAttribute>();
    attributes.add(caseAttribute);

    CaseV2 onUsPaymentCase = new CaseV2();
    onUsPaymentCase.setCaseType("CaseType");
    onUsPaymentCase.setAttributes(attributes);
    onUsPaymentCase.setPrimaryPartyKey("PrimaryPartyKey");
    onUsPaymentCase.setSecondaryPartyKey("SecondaryPartyKey");
    onUsPaymentCase.setSubscriptionKey("SubscriptionKey");

    PaymentCaseEntityV2 paymentCaseEntity = new PaymentCaseEntityV2();
    paymentCaseEntity.setTransactionId("test");
    paymentCaseEntity.setPaymentType(OnUsPaymentV2.class.getSimpleName());
    paymentCaseEntity.setPaymentCase(onUsPaymentCase);

    given(paymentCaseRepositoryV2.save(paymentCaseEntity)).willReturn(paymentCaseEntity);
    given(caseAssemblerV2.assembleCase(onUsPaymentV2)).willReturn(onUsPaymentCase);

    paymentCaseServiceImplV2.createSavePaymentCase(onUsPaymentV2);
    verify(paymentCaseRepositoryV2, times(1)).save(paymentCaseEntity);
    verify(caseAssemblerV2, times(1)).assembleCase(onUsPaymentV2);

  }

  @Test
  public void checkRetrieveAndSendCase() throws PaymentCaseException {

    CaseCreationResponse caseCreationResponse = new CaseCreationResponse("caseId", "partyKey");
    List<CaseCreationResponse> caseResponse = new ArrayList<CaseCreationResponse>();
    caseResponse.add(caseCreationResponse);

    CaseAttribute caseAttribute = new CaseAttribute("attributeName", "attributeValue");
    List<CaseAttribute> attributes = new ArrayList<CaseAttribute>();
    attributes.add(caseAttribute);

    CaseV2 newCase = new CaseV2();
    newCase.setCaseType("CaseType");
    newCase.setAttributes(attributes);
    newCase.setPrimaryPartyKey("PrimaryPartyKey");
    newCase.setSecondaryPartyKey("SecondaryPartyKey");
    newCase.setSubscriptionKey("SubscriptionKey");

    PaymentCaseEntityV2 paymentCaseEntity = new PaymentCaseEntityV2();
    paymentCaseEntity.setTransactionId("transactionId");
    paymentCaseEntity.setPaymentType(DomesticOutPaymentV2.class.getSimpleName());
    paymentCaseEntity.setPaymentCase(newCase);

    CasesListV2 newCaselist = new CasesListV2();
    newCaselist.add(newCase);

    given(paymentCaseRepositoryV2.findByTransactionId("transactionId")).willReturn(paymentCaseEntity);
    given(caseGovernorClient.createCasesV2(any())).willReturn(caseResponse);
    paymentCaseServiceImplV2.createCase("transactionId", anyString());
    verify(paymentCaseRepositoryV2, times(1)).findByTransactionId("transactionId");
    verify(caseGovernorClient, times(1)).createCasesV2(any());
    verify(transactionCaseDataStore, times(1)).saveTransactionCase("transactionId", "caseId");

  }

  @Test
  public void checkCaseServiceException() throws PaymentCaseException {

    CaseCreationResponse caseCreationResponse = new CaseCreationResponse("caseId", "partyKey");
    List<CaseCreationResponse> caseResponse = new ArrayList<CaseCreationResponse>();
    caseResponse.add(caseCreationResponse);

    CaseAttribute caseAttribute = new CaseAttribute("attributeName", "attributeValue");
    List<CaseAttribute> attributes = new ArrayList<CaseAttribute>();
    attributes.add(caseAttribute);

    CaseV2 newCase = new CaseV2();
    newCase.setCaseType("CaseType");
    newCase.setAttributes(attributes);
    newCase.setPrimaryPartyKey("PrimaryPartyKey");
    newCase.setSecondaryPartyKey("SecondaryPartyKey");
    newCase.setSubscriptionKey("SubscriptionKey");

    PaymentCaseEntityV2 paymentCaseEntity = new PaymentCaseEntityV2();
    paymentCaseEntity.setTransactionId("transactionId");
    paymentCaseEntity.setPaymentType(DomesticOutPaymentV2.class.getSimpleName());
    paymentCaseEntity.setPaymentCase(newCase);

    CasesListV2 newCaselist = new CasesListV2();
    newCaselist.add(newCase);

    given(paymentCaseRepositoryV2.findByTransactionId("transactionId")).willThrow(new RuntimeException("abcd4321"));
    assertThrows(
      PaymentCaseException.class, () ->
        paymentCaseServiceImplV2.createCase("transactionId", anyString()));
    verify(paymentCaseRepositoryV2, times(1)).findByTransactionId("transactionId");
    verify(caseGovernorClient, times(0)).createCasesV2(any());

  }

}