package com.tenx.fraudamlmanager.paymentsv3.domestic.cases.domain;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraudamlmanager.cases.v2.domain.CaseHandlerV2;
import com.tenx.fraudamlmanager.cases.v2.domain.CaseV2;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudAMLSanctionsCheckResponseCodeV3;
import com.tenx.fraudamlmanager.paymentsv3.domestic.domain.DomesticInPaymentV3;
import com.tenx.fraudamlmanager.paymentsv3.domestic.domain.DomesticOutPaymentV3;
import com.tenx.fraudamlmanager.paymentsv3.domestic.domain.DomesticOutReturnPaymentV3;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class DomesticCaseServiceImplV3Test {

    private DomesticCaseServiceV3 domesticCaseServiceV3;

    @MockBean
    CaseHandlerV2 caseHandlerV2;

    @MockBean
    DomesticInCaseAssemblerV3 domesticInCaseAssemblerV3;
    @MockBean
    DomesticOutCaseAssemblerV3 domesticOutCaseAssemblerV3;
    @MockBean
    DomesticOutReturnCaseAssemblerV3 domesticOutReturnCaseAssemblerV3;

    @BeforeEach
    public void beforeEach() {
        this.domesticCaseServiceV3 = new DomesticCaseServiceV3Impl(domesticInCaseAssemblerV3,
            domesticOutCaseAssemblerV3, domesticOutReturnCaseAssemblerV3, caseHandlerV2);
    }

    @Test
    void processDomesticInCase() {
        DomesticInPaymentV3 domesticInPayment = mockDomesticIn();
        CaseV2 caseV2 = new CaseV2();
        caseV2.setCaseType("FRAUD_EXCEPTION");
        when(domesticInCaseAssemblerV3.assembleCase(any())).thenReturn(caseV2);
        domesticCaseServiceV3.processCaseForDomesticIn(FraudAMLSanctionsCheckResponseCodeV3.pending, domesticInPayment);
        verify(domesticInCaseAssemblerV3, times(1)).assembleCase(eq(domesticInPayment));
        verify(caseHandlerV2, times(1)).processPaymentCase(eq(FraudAMLSanctionsCheckResponseCodeV3.pending), eq("DomesticInTransactionId"), anyString(), eq(caseV2));

    }

    @Test
    void saveDomesticOutPaymentCaseForPendingCases() {
        DomesticOutPaymentV3 domesticOutPayment = mockDomesticOut();
        CaseV2 caseV2 = new CaseV2();
        caseV2.setCaseType("FRAUD_EXCEPTION");
        when(domesticOutCaseAssemblerV3.assembleCase(any())).thenReturn(caseV2);
        domesticCaseServiceV3.processCaseForDomesticOut(FraudAMLSanctionsCheckResponseCodeV3.pending, domesticOutPayment);
        verify(domesticOutCaseAssemblerV3, times(1)).assembleCase(eq(domesticOutPayment));
        verify(caseHandlerV2, times(1)).processPaymentCase(eq(FraudAMLSanctionsCheckResponseCodeV3.pending), eq("DomesticOutTransactionId"), anyString(), eq(caseV2));
    }

    @Test
    void saveDomesticOutReturnPaymentCaseForPendingCases() {
        DomesticOutReturnPaymentV3 domesticOutReturnPayment = mockDomesticOutReturn();
        CaseV2 caseV2 = new CaseV2();
        caseV2.setCaseType("FRAUD_EXCEPTION");
        when(domesticOutReturnCaseAssemblerV3.assembleCase(any())).thenReturn(caseV2);
        domesticCaseServiceV3.processCaseForDomesticOutReturn(FraudAMLSanctionsCheckResponseCodeV3.pending, domesticOutReturnPayment);
        verify(caseHandlerV2, times(1)).processPaymentCase(eq(FraudAMLSanctionsCheckResponseCodeV3.pending), eq("DomesticOutReturnTransactionId"), anyString(), eq(caseV2));

    }


    private DomesticInPaymentV3 mockDomesticIn() {
        DomesticInPaymentV3 domesticInPaymentV3 = mock(DomesticInPaymentV3.class);
        when(domesticInPaymentV3.getCreditorPartyKey()).thenReturn("CreditorPartyKey");
        when(domesticInPaymentV3.getCreditorName()).thenReturn("creditor");
        when(domesticInPaymentV3.getTransactionId()).thenReturn("DomesticInTransactionId");
        return domesticInPaymentV3;
    }

    private DomesticOutPaymentV3 mockDomesticOut() {
        DomesticOutPaymentV3 domesticOutPaymentV3 = mock(DomesticOutPaymentV3.class);
        when(domesticOutPaymentV3.getDebtorPartyKey()).thenReturn("debtorPartyKey");
        when(domesticOutPaymentV3.getDebtorName()).thenReturn("debitor");
        when(domesticOutPaymentV3.getTransactionId()).thenReturn("DomesticOutTransactionId");
        return domesticOutPaymentV3;
    }

    private DomesticOutReturnPaymentV3 mockDomesticOutReturn() {
        DomesticOutReturnPaymentV3 domesticOutPaymentV3 = mock(DomesticOutReturnPaymentV3.class);
        when(domesticOutPaymentV3.getPartyKey()).thenReturn("partyKey");
        when(domesticOutPaymentV3.getDebtorName()).thenReturn("debitor");
        when(domesticOutPaymentV3.getCreditorName()).thenReturn("creditor");
        when(domesticOutPaymentV3.getTransactionId()).thenReturn("DomesticOutReturnTransactionId");
        return domesticOutPaymentV3;
    }

}