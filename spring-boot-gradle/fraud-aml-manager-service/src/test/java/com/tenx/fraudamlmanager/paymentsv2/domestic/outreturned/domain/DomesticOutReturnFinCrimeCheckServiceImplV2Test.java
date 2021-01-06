package com.tenx.fraudamlmanager.paymentsv2.domestic.outreturned.domain;

import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraudamlmanager.paymentsv2.cases.domain.PaymentCaseServiceV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain.AccountDetailsDomesticOutReturnV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain.BalanceBeforeDomesticOutReturnV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain.DomesticOutReturnFinCrimeCheckServiceV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain.DomesticOutReturnFinCrimeCheckServiceV2Impl;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain.DomesticOutReturnPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain.DomesticOutReturnTransactionMonitoringExceptionV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain.DomesticOutReturnTransactionMonitoringServiceV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain.PaymentAmountDomesticOutReturnV2;
import java.util.ArrayList;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class DomesticOutReturnFinCrimeCheckServiceImplV2Test {

  private DomesticOutReturnFinCrimeCheckServiceV2 domesticOutReturnFinCrimeCheckServiceV2;

  @MockBean
  private DomesticOutReturnTransactionMonitoringServiceV2 domesticOutReturnTransactionMonitoringServiceV2;

  @MockBean
  private PaymentCaseServiceV2 paymentCaseService;

  @BeforeEach
  public void beforeEach() {
    this.domesticOutReturnFinCrimeCheckServiceV2 = new DomesticOutReturnFinCrimeCheckServiceV2Impl(
            domesticOutReturnTransactionMonitoringServiceV2, paymentCaseService);
  }

  @Test
  public void checkDomestiInPayment() throws DomesticOutReturnTransactionMonitoringExceptionV2 {
    DomesticOutReturnPaymentV2 domesticOutReturnPaymentV2 = new DomesticOutReturnPaymentV2(
        new AccountDetailsDomesticOutReturnV2("AccountNumber",
            "BankID"), "CreditorFirst CreditorSecond",
        new AccountDetailsDomesticOutReturnV2("AccountNumber", "BankID"),
        "DebtorFirst DebtorSecond",
        new PaymentAmountDomesticOutReturnV2("GBP", 30, "GBP", 30),
        "TranID", new Date(), new Date(), "TransactionStatus",
            "TranRef",
            "TranNotes", new ArrayList<String>(), true,
            new BalanceBeforeDomesticOutReturnV2("GBP", 500.00, "GBP", 500.00),
            "PartyKey"
    );

    domesticOutReturnFinCrimeCheckServiceV2.checkFinCrimeV2(domesticOutReturnPaymentV2);

    Mockito.verify(domesticOutReturnTransactionMonitoringServiceV2, times(1))
            .checkDomesticOutReturnPaymentV2(domesticOutReturnPaymentV2);

    Mockito.verify(paymentCaseService, times(1))
            .createSavePaymentCase(domesticOutReturnPaymentV2);
  }

}
