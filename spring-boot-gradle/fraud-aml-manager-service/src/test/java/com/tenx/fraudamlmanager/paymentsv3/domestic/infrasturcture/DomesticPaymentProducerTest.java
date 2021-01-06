package com.tenx.fraudamlmanager.paymentsv3.domestic.infrasturcture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.tenx.fraud.payments.fps.FPSFraudCheckResponse;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudAMLSanctionsCheckResponseCodeV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudCheckV3;
import com.tenx.fraudamlmanager.paymentsv3.domestic.domain.DomesticPaymentProducer;
import com.tenx.fraudamlmanager.paymentsv3.domestic.infrastructure.DomesticPaymentProducerImpl;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class DomesticPaymentProducerTest {

    @Captor
    ArgumentCaptor<FPSFraudCheckResponse> fpsFraudCheckResponseArgumentCaptor;
    @MockBean
    private KafkaTemplate<String, FPSFraudCheckResponse> kafkaProducerTemplate;
    private DomesticPaymentProducer domesticPaymentProducer;

    @BeforeEach
    public void setUp() {
        domesticPaymentProducer = new DomesticPaymentProducerImpl(kafkaProducerTemplate);
    }

    @Test
    void testPublishFraudCheckInboundResponseEvent() {

        FraudCheckV3 fraudCheckV3 = new FraudCheckV3(FraudAMLSanctionsCheckResponseCodeV3.passed);

        domesticPaymentProducer.publishDomesticInResponseEvent(fraudCheckV3, "transactionId");

        verify(kafkaProducerTemplate, times(1)).send(any(), eq("transactionId"), fpsFraudCheckResponseArgumentCaptor.capture());

        Assert.assertEquals("transactionId", fpsFraudCheckResponseArgumentCaptor.getValue().getTransactionId());
        Assert.assertEquals("domesticPaymentInboundFinCrimeCheck", fpsFraudCheckResponseArgumentCaptor.getValue().getPaymentType());
        Assert.assertEquals("PASSED", fpsFraudCheckResponseArgumentCaptor.getValue().getStatus());
    }

    @Test
    void testPublishFraudCheckOutboundResponseEvent() {

        FraudCheckV3 fraudCheckV3 = new FraudCheckV3(FraudAMLSanctionsCheckResponseCodeV3.passed);

        domesticPaymentProducer.publishDomesticOutResponseEvent(fraudCheckV3, "transactionId");

        verify(kafkaProducerTemplate, times(1)).send(any(), eq("transactionId"), fpsFraudCheckResponseArgumentCaptor.capture());

        Assert.assertEquals("transactionId", fpsFraudCheckResponseArgumentCaptor.getValue().getTransactionId());
        Assert.assertEquals("domesticPaymentOutboundFinCrimeCheck", fpsFraudCheckResponseArgumentCaptor.getValue().getPaymentType());
        Assert.assertEquals("PASSED", fpsFraudCheckResponseArgumentCaptor.getValue().getStatus());
    }


    @Test
    void testPublishFraudCheckOutboundReturnResponseEvent() {

        FraudCheckV3 fraudCheckV3 = new FraudCheckV3(FraudAMLSanctionsCheckResponseCodeV3.passed);

        domesticPaymentProducer.publishDomesticOutReturnResponseEvent(fraudCheckV3, "transactionId");

        verify(kafkaProducerTemplate, times(1)).send(any(), eq("transactionId"), fpsFraudCheckResponseArgumentCaptor.capture());

        Assert.assertEquals("transactionId", fpsFraudCheckResponseArgumentCaptor.getValue().getTransactionId());
        Assert.assertEquals("domesticPaymentOutboundReturnFinCrimeCheck", fpsFraudCheckResponseArgumentCaptor.getValue().getPaymentType());
        Assert.assertEquals("PASSED", fpsFraudCheckResponseArgumentCaptor.getValue().getStatus());
    }

}
