package com.tenx.fraudamlmanager.payments.core.credittransfer.infrastructure;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.BusinessApplicationHeader;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.FIToFIPaymentStatusReportV11;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.GroupHeader91;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.Pacs002;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.Party44Choice;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.FraudCheckResponseV1;
import com.tenxbanking.iso.lib.IsoFraudCheckResponseV01;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.PaymentTransaction110;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class CreditTransferResponseProducerImplTest {

  @Captor
  ArgumentCaptor<IsoFraudCheckResponseV01> isoFraudCheckResponseV01ArgumentCaptor;

  @MockBean
  private KafkaTemplate<String, IsoFraudCheckResponseV01> kafkaProducerTemplate;

  private CreditTransferResponseProducerImpl creditTransferResponseProducerImpl;

  @BeforeEach
  public void initTest() {
    creditTransferResponseProducerImpl = new CreditTransferResponseProducerImpl(kafkaProducerTemplate);
  }

  @Test
  void testPublishFraudCheckResponse() throws CreditTransferPublishException {

    Pacs002 pacs002 = getPacs002();

    creditTransferResponseProducerImpl.publishFraudCheckResponse(pacs002);
    verify(kafkaProducerTemplate, times(1)).send(isNull(), isoFraudCheckResponseV01ArgumentCaptor.capture());

    IsoFraudCheckResponseV01 isoFraudCheckResponseV01 = isoFraudCheckResponseV01ArgumentCaptor.getValue();
    assertEquals(isoFraudCheckResponseV01.getApplicationHeader().getBusinessMessageIdentifier(), pacs002.getCustomerCreditTransferResponse().getApplicationHeader().getBusinessMessageIdentifier());
    assertEquals(isoFraudCheckResponseV01.getApplicationHeader().getMessageDefinitionIdentifier(), pacs002.getCustomerCreditTransferResponse().getApplicationHeader().getMessageDefinitionIdentifier());
    assertEquals(isoFraudCheckResponseV01.getFraudCheckResponse().getGroupHeader().getMessageIdentification(), pacs002.getCustomerCreditTransferResponse().getFraudCheckResponse().getGroupHeader().getMessageId());
  }

  private Pacs002 getPacs002() {
    Pacs002 pacs002 = new Pacs002();
    FIToFIPaymentStatusReportV11 fiToFIPaymentStatusReportV11 = new FIToFIPaymentStatusReportV11();
    BusinessApplicationHeader businessApplicationHeader = new BusinessApplicationHeader();
    businessApplicationHeader.setBusinessMessageIdentifier("testMsgID");
    businessApplicationHeader.setCreationDate(new Date(1604940538797L));
    businessApplicationHeader.setFrom(new Party44Choice());
    businessApplicationHeader.setTo(new Party44Choice());
    businessApplicationHeader.setMessageDefinitionIdentifier("testMsgDefID");

    PaymentTransaction110 paymentTransaction110 = new PaymentTransaction110();
    List<PaymentTransaction110> paymentTransaction110List = new ArrayList<>();
    paymentTransaction110List.add(paymentTransaction110);


    FraudCheckResponseV1 fraudCheckResponseV1 = new FraudCheckResponseV1();

    GroupHeader91 groupHeader91 = new GroupHeader91();
    groupHeader91.setMessageId("123");
    groupHeader91.setCreationDateAndTime(new Date());
    fraudCheckResponseV1.setGroupHeader(groupHeader91);
    fraudCheckResponseV1.setSupplementaryData(new ArrayList<>());
    fraudCheckResponseV1.setTransactionInformationAndStatus(paymentTransaction110List);

    fiToFIPaymentStatusReportV11.setApplicationHeader(businessApplicationHeader);
    fiToFIPaymentStatusReportV11.setFraudCheckResponse(fraudCheckResponseV1);

    pacs002.setCustomerCreditTransferResponse(fiToFIPaymentStatusReportV11);

    return pacs002;
  }
}
