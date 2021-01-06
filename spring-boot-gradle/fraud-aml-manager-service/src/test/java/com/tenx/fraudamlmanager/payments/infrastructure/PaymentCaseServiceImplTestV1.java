package com.tenx.fraudamlmanager.payments.infrastructure;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.tenx.fraudamlmanager.cases.domain.CaseAttribute;
import com.tenx.fraudamlmanager.cases.domain.PaymentCaseException;
import com.tenx.fraudamlmanager.cases.infrastructure.CaseCreationResponse;
import com.tenx.fraudamlmanager.cases.v1.domain.Case;
import com.tenx.fraudamlmanager.cases.v1.domain.CaseAssembler;
import com.tenx.fraudamlmanager.payments.cases.infrastructure.CaseGovernorClient;
import com.tenx.fraudamlmanager.payments.cases.infrastructure.PaymentCaseServiceImplV1;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.AccountDetails;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.BalanceBefore;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.DomesticOutPayment;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.OnUsPayment;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.PaymentAmount;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class PaymentCaseServiceImplTestV1 {

  @MockBean
  private CaseAssembler caseAssembler;

  @MockBean
  private CaseGovernorClient caseGovernorClient;

  private PaymentCaseServiceImplV1 paymentCaseServiceImplV1;

  @BeforeEach
  public void beforeEach() {
    this.paymentCaseServiceImplV1 = new PaymentCaseServiceImplV1(caseAssembler, caseGovernorClient);
  }

  @Test
  public void checkCreateCaseDomesticOut() throws PaymentCaseException {

    CaseCreationResponse caseCreationResponse = new CaseCreationResponse("caseId", "partyKey");
    List<CaseCreationResponse> caseResponse = new ArrayList<CaseCreationResponse>();
    caseResponse.add(caseCreationResponse);
    DomesticOutPayment domesticOutPayment = new DomesticOutPayment(
      new AccountDetails("123", "abc"),
      "creditor name",
      new AccountDetails("098", "zyx"),
      "debtor name",
      new PaymentAmount("EUR", 30.00, "EUR", 30.00),
      new BalanceBefore("GBP", 500.00, "GBP", 500.00),
      "123key",
      new Date(), new Date(), "Review",
      "Reference Test", "Domestic notes",
      new ArrayList<>(), true, "456key");

    Case domesticOutPaymentCase = new Case();
    CaseAttribute caseAttribute = new CaseAttribute("attributeName", "attributeValue");
    List<CaseAttribute> attributes = new ArrayList<CaseAttribute>();
    attributes.add(caseAttribute);
    domesticOutPaymentCase.setAttributes(attributes);
    domesticOutPaymentCase.setCaseType(Case.CaseType.FRAUD_EXCEPTION.name());
    domesticOutPaymentCase.setPrimaryPartyKey("PrimaryPartyKey");
    domesticOutPaymentCase.setSecondaryPartyKey("SecondaryPartyKey");
    domesticOutPaymentCase.setSubscriptionKey("SubscriptionKey");

    given(caseGovernorClient.createCases(any())).willReturn(caseResponse);
    given(caseAssembler.assembleCase(domesticOutPayment)).willReturn(domesticOutPaymentCase);

    paymentCaseServiceImplV1.createBlockedPaymentCase(domesticOutPayment);
    verify(caseAssembler, times(1)).assembleCase(domesticOutPayment);


  }

  @Test
  public void checkCreateCaseOnUs() throws PaymentCaseException {
    CaseCreationResponse caseCreationResponse = new CaseCreationResponse("caseId", "partyKey");
    List<CaseCreationResponse> caseResponse = new ArrayList<CaseCreationResponse>();
    caseResponse.add(caseCreationResponse);
    OnUsPayment onUsPayment = new OnUsPayment();
    onUsPayment.setDebtorName("test");
    onUsPayment.setTransactionId("test");
    onUsPayment.setDebtorName("test");
    onUsPayment.setBalanceBefore(
      new com.tenx.fraudamlmanager.payments.model.transactionmonitoring.BalanceBefore("Test", 500.00, "Test", 500.00));
    onUsPayment.setDebtorAccountDetails(new AccountDetails("1234", "5678"));
    onUsPayment.setDebtorPartyKey("test");
    onUsPayment.setCreditorName("test");
    onUsPayment.setCreditorAccountDetails(new AccountDetails("1234", "5678"));
    onUsPayment.setCreditorPartyKey("test");
    onUsPayment.setTransactionId("test");
    onUsPayment.setAmount(
      new com.tenx.fraudamlmanager.payments.model.transactionmonitoring.PaymentAmount("test", 1.00, "test", 2.00));
    onUsPayment.setDebtorPartyKey("test");
    onUsPayment.setTransactionDate(new Date());

    CaseAttribute caseAttribute = new CaseAttribute("attributeName", "attributeValue");
    List<CaseAttribute> attributes = new ArrayList<CaseAttribute>();
    attributes.add(caseAttribute);

    Case onUsPaymentCase = new Case();
    onUsPaymentCase.setCaseType(Case.CaseType.FRAUD_EXCEPTION.name());
    onUsPaymentCase.setAttributes(attributes);
    onUsPaymentCase.setPrimaryPartyKey("PrimaryPartyKey");
    onUsPaymentCase.setSecondaryPartyKey("SecondaryPartyKey");
    onUsPaymentCase.setSubscriptionKey("SubscriptionKey");

    given(caseGovernorClient.createCases(any())).willReturn(caseResponse);
    given(caseAssembler.assembleCase(onUsPayment)).willReturn(onUsPaymentCase);

    paymentCaseServiceImplV1.createBlockedPaymentCase(onUsPayment);
    verify(caseAssembler, times(1)).assembleCase(onUsPayment);

  }

}