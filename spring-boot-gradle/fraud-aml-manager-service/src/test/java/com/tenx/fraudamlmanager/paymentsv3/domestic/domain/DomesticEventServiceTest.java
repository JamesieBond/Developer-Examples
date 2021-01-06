package com.tenx.fraudamlmanager.paymentsv3.domestic.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudAMLSanctionsCheckResponseCodeV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudCheckV3;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author Niall O'Connell
 */
@ExtendWith(SpringExtension.class)
public class DomesticEventServiceTest {

    @MockBean
    DomesticFinCrimeCheckServiceV3 domesticFinCrimeCheckServiceV3;
    @MockBean
    DomesticPaymentProducer domesticPaymentProducer;
    @Captor
    ArgumentCaptor<FraudCheckV3> fraudCheckV3ArgumentCaptor;

    private DomesticEventService domesticEventService;

    @BeforeEach
    public void beforeEach() {
        this.domesticEventService = new DomesticEventServiceImpl(domesticFinCrimeCheckServiceV3, domesticPaymentProducer);
    }

    /**
     * @throws TransactionMonitoringException Generic exception
     */
    @Test
    public void testInboundPaymentSuccess() throws TransactionMonitoringException {

        DomesticInPaymentV3 domesticInPaymentV3 = new DomesticInPaymentV3();
        domesticInPaymentV3.setTransactionId("TransactionId");

        FraudCheckV3 fraudCheckV3 = new FraudCheckV3(FraudAMLSanctionsCheckResponseCodeV3.passed);
        when(domesticFinCrimeCheckServiceV3.checkFinCrimeV3(any(DomesticInPaymentV3.class))).thenReturn(fraudCheckV3);

        Assertions.assertThatCode(() -> domesticEventService.produceEventForFinCrime(domesticInPaymentV3))
            .doesNotThrowAnyException();

        verify(domesticFinCrimeCheckServiceV3, times(1)).checkFinCrimeV3(any(DomesticInPaymentV3.class));

        verify(domesticPaymentProducer, times(1))
            .publishDomesticInResponseEvent(fraudCheckV3ArgumentCaptor.capture(), eq("TransactionId"));

        FraudCheckV3 fraudCheckV3ArgumentCaptorValue = fraudCheckV3ArgumentCaptor.getValue();
        assertEquals(FraudAMLSanctionsCheckResponseCodeV3.passed, fraudCheckV3ArgumentCaptorValue.getStatus());
    }

    @Test
    public void testOutboundPaymentSuccess() throws TransactionMonitoringException {

        DomesticOutPaymentV3 domesticOutPaymentV3 = new DomesticOutPaymentV3();

        domesticOutPaymentV3.setTransactionId("TransactionId");

        FraudCheckV3 fraudCheckV3 = new FraudCheckV3(FraudAMLSanctionsCheckResponseCodeV3.passed);
        when(domesticFinCrimeCheckServiceV3.checkFinCrimeV3(any(DomesticOutPaymentV3.class), anyString()))
            .thenReturn(fraudCheckV3);

        Assertions.assertThatCode(() -> domesticEventService.produceEventForFinCrime(domesticOutPaymentV3, ""))
            .doesNotThrowAnyException();

        verify(domesticFinCrimeCheckServiceV3, times(1)).checkFinCrimeV3(any(DomesticOutPaymentV3.class), anyString());

        verify(domesticPaymentProducer, times(1))
            .publishDomesticOutResponseEvent(fraudCheckV3ArgumentCaptor.capture(), eq("TransactionId"));

        FraudCheckV3 fraudCheckV3ArgumentCaptorValue = fraudCheckV3ArgumentCaptor.getValue();
        assertEquals(FraudAMLSanctionsCheckResponseCodeV3.passed, fraudCheckV3ArgumentCaptorValue.getStatus());
    }


    @Test
    public void testOutboundReturnPaymentSuccess() throws TransactionMonitoringException {

        DomesticOutReturnPaymentV3 domesticOutReturnPaymentV3 = new DomesticOutReturnPaymentV3();
        domesticOutReturnPaymentV3.setTransactionId("TransactionId");

        FraudCheckV3 fraudCheckV3 = new FraudCheckV3(FraudAMLSanctionsCheckResponseCodeV3.passed);
        when(domesticFinCrimeCheckServiceV3.checkFinCrimeV3(any(DomesticOutReturnPaymentV3.class))).thenReturn(fraudCheckV3);

        Assertions.assertThatCode(() -> domesticEventService.produceEventForFinCrime(domesticOutReturnPaymentV3))
            .doesNotThrowAnyException();

        verify(domesticFinCrimeCheckServiceV3, times(1)).checkFinCrimeV3(any(DomesticOutReturnPaymentV3.class));

        verify(domesticPaymentProducer, times(1))
            .publishDomesticOutReturnResponseEvent(fraudCheckV3ArgumentCaptor.capture(), eq("TransactionId"));

        FraudCheckV3 fraudCheckV3ArgumentCaptorValue = fraudCheckV3ArgumentCaptor.getValue();
        assertEquals(FraudAMLSanctionsCheckResponseCodeV3.passed, fraudCheckV3ArgumentCaptorValue.getStatus());
    }

    /**
     * @throws TransactionMonitoringException Generic exception
     */
    @Test
    public void testInboundPaymentPending() throws TransactionMonitoringException {

        DomesticInPaymentV3 domesticInPaymentV3 = new DomesticInPaymentV3();
        domesticInPaymentV3.setTransactionId("TransactionId");

        FraudCheckV3 fraudCheckV3 = new FraudCheckV3(FraudAMLSanctionsCheckResponseCodeV3.pending);
        when(domesticFinCrimeCheckServiceV3.checkFinCrimeV3(any(DomesticInPaymentV3.class))).thenReturn(fraudCheckV3);

        Assertions.assertThatCode(() -> domesticEventService.produceEventForFinCrime(domesticInPaymentV3))
            .doesNotThrowAnyException();

        verify(domesticFinCrimeCheckServiceV3, times(1)).checkFinCrimeV3(any(DomesticInPaymentV3.class));

        verify(domesticPaymentProducer, times(0))
            .publishDomesticInResponseEvent(any(FraudCheckV3.class), eq("TransactionId"));

    }

    @Test
    public void testOutboundPaymentPending() throws TransactionMonitoringException {

        DomesticOutPaymentV3 domesticOutPaymentV3 = new DomesticOutPaymentV3();
        domesticOutPaymentV3.setTransactionId("TransactionId");

        FraudCheckV3 fraudCheckV3 = new FraudCheckV3(FraudAMLSanctionsCheckResponseCodeV3.pending);
        when(domesticFinCrimeCheckServiceV3.checkFinCrimeV3(any(DomesticOutPaymentV3.class), anyString()))
            .thenReturn(fraudCheckV3);

        Assertions.assertThatCode(() -> domesticEventService.produceEventForFinCrime(domesticOutPaymentV3, ""))
            .doesNotThrowAnyException();

        verify(domesticFinCrimeCheckServiceV3, times(1)).checkFinCrimeV3(any(DomesticOutPaymentV3.class), anyString());

        verify(domesticPaymentProducer, times(0))
            .publishDomesticOutResponseEvent(any(FraudCheckV3.class), eq("TransactionId"));

    }


    @Test
    public void testOutboundReturnPaymentPending() throws TransactionMonitoringException {

        DomesticOutReturnPaymentV3 domesticOutReturnPaymentV3 = new DomesticOutReturnPaymentV3();
        domesticOutReturnPaymentV3.setTransactionId("TransactionId");

        FraudCheckV3 fraudCheckV3 = new FraudCheckV3(FraudAMLSanctionsCheckResponseCodeV3.pending);
        when(domesticFinCrimeCheckServiceV3.checkFinCrimeV3(any(DomesticOutReturnPaymentV3.class))).thenReturn(fraudCheckV3);

        Assertions.assertThatCode(() -> domesticEventService.produceEventForFinCrime(domesticOutReturnPaymentV3))
            .doesNotThrowAnyException();

        verify(domesticFinCrimeCheckServiceV3, times(1)).checkFinCrimeV3(any(DomesticOutReturnPaymentV3.class));

        verify(domesticPaymentProducer, times(0))
            .publishDomesticOutReturnResponseEvent(any(FraudCheckV3.class), eq("TransactionId"));

    }

    @Test
    public void testInboundPaymentTMAException() throws TransactionMonitoringException {

        DomesticInPaymentV3 domesticInPaymentV3 = new DomesticInPaymentV3();

        when(domesticFinCrimeCheckServiceV3.checkFinCrimeV3(any(DomesticInPaymentV3.class)))
            .thenThrow(new TransactionMonitoringException(500, "TMA error"));

        assertThrows(TransactionMonitoringException.class,
            () -> domesticEventService.produceEventForFinCrime(domesticInPaymentV3));

        verify(domesticFinCrimeCheckServiceV3, times(1)).checkFinCrimeV3(eq(domesticInPaymentV3));

        verifyZeroInteractions(domesticPaymentProducer);
    }

    @Test
    public void testOutboundReturnPaymentTMAException() throws TransactionMonitoringException {

        DomesticOutReturnPaymentV3 domesticOutReturnPaymentV3 = new DomesticOutReturnPaymentV3();

        when(domesticFinCrimeCheckServiceV3.checkFinCrimeV3(any(DomesticOutReturnPaymentV3.class)))
            .thenThrow(new TransactionMonitoringException(500, "TMA error"));

        assertThrows(
            TransactionMonitoringException.class, () ->
                domesticEventService.produceEventForFinCrime(domesticOutReturnPaymentV3));

        verify(domesticFinCrimeCheckServiceV3, times(1)).checkFinCrimeV3(eq(domesticOutReturnPaymentV3));

        verifyZeroInteractions(domesticPaymentProducer);
    }
}

