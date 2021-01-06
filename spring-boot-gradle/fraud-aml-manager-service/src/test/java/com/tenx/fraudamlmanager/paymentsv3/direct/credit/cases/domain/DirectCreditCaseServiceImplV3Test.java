package com.tenx.fraudamlmanager.paymentsv3.direct.credit.cases.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraudamlmanager.cases.v2.domain.CaseHandlerV2;
import com.tenx.fraudamlmanager.cases.v2.domain.CaseV2;
import com.tenx.fraudamlmanager.cases.v2.domain.CasesListV2;
import com.tenx.fraudamlmanager.cases.v2.domain.PaymentCaseDataStore;
import com.tenx.fraudamlmanager.paymentsv3.direct.credit.domain.DirectCreditPaymentV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudAMLSanctionsCheckResponseCodeV3;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class DirectCreditCaseServiceImplV3Test {

    private DirectCreditCaseServiceV3 directCreditCaseServiceV3;

    @MockBean
    private CaseHandlerV2 caseHandlerV2;

    @MockBean
    private PaymentCaseDataStore paymentCaseDataStore;


    @MockBean
    private DirectCreditCaseAssemblerV3 caseAssembler;

    @Captor
    private ArgumentCaptor<DirectCreditPaymentV3> directCreditPaymentForCaseV3ArgumentCaptor;
    @Captor
    private ArgumentCaptor<CasesListV2> casesListV2ArgumentCaptor;

    @BeforeEach
    public void beforeEach() {
        this.directCreditCaseServiceV3 = new DirectCreditCaseServiceImplV3(caseHandlerV2, caseAssembler);
    }

    @Test
    void processPaymentCase() {
        DirectCreditPaymentV3 directCreditPaymentV3 = mockDirectCredit();
        CaseV2 caseV2 = new CaseV2();
        caseV2.setCaseType("FRAUD_EXCEPTION");
        when(caseAssembler.assembleCase(any())).thenReturn(caseV2);
        directCreditCaseServiceV3.processPaymentForCase(FraudAMLSanctionsCheckResponseCodeV3.pending, directCreditPaymentV3);
        verify(caseAssembler, times(1)).assembleCase(directCreditPaymentForCaseV3ArgumentCaptor.capture());
        verify(caseHandlerV2, times(1)).processPaymentCase(eq(FraudAMLSanctionsCheckResponseCodeV3.pending), eq("transactionId"), anyString(), eq(caseV2));
        verifyDirectCreditForCase(directCreditPaymentForCaseV3ArgumentCaptor.getValue());
    }


    private void verifyDirectCreditForCase(DirectCreditPaymentV3 directCreditPaymentV3) {
        assertEquals("transactionId", directCreditPaymentV3.getTransactionId());
        assertEquals("creditor", directCreditPaymentV3.getCreditorName());
        assertEquals("partyKey", directCreditPaymentV3.getPartyKey());
    }

    private DirectCreditPaymentV3 mockDirectCredit() {
        DirectCreditPaymentV3 directCreditPaymentV3 = mock(DirectCreditPaymentV3.class);
        when(directCreditPaymentV3.getPartyKey()).thenReturn("partyKey");
        when(directCreditPaymentV3.getCreditorName()).thenReturn("creditor");
        when(directCreditPaymentV3.getTransactionId()).thenReturn("transactionId");
        return directCreditPaymentV3;
    }


}