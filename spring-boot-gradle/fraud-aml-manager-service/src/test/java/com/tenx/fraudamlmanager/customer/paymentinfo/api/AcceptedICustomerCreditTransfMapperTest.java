package com.tenx.fraudamlmanager.customer.paymentinfo.api;

import static junit.framework.TestCase.assertEquals;

import com.tenx.fraudamlmanager.customer.paymentinfo.CustomerCreditTransferHelper;
import com.tenx.fraudamlmanager.customer.paymentinfo.domain.AcceptedCustomerCreditTransfType;
import com.tenxbanking.events.lib.CustomerCreditTransferInitiationCompletedEvent;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.TimeZone;
import org.junit.jupiter.api.Test;

class AcceptedICustomerCreditTransfMapperTest {


  @Test
  void mapAcceptedTypeTest() throws IOException {
    CustomerCreditTransferInitiationCompletedEvent event = CustomerCreditTransferHelper.readEventFromFile();
    event.getCustomerPaymentStatusReport().getOriginalGroupInformationAndStatus().setGroupStatus("ACSC");

    AcceptedCustomerCreditTransfType acceptedCustomerCreditTransfType = AcceptedCustomerCreditTransfMapper.MAPPER
        .toAcceptedType(event.getCustomerPaymentStatusReport());

    assertEquals(Integer.valueOf(2), acceptedCustomerCreditTransfType.getNumberOfTransactions());
    assertEquals(LocalDateTime.ofInstant(Instant.ofEpochMilli(1604310181820L),
        TimeZone.getDefault().toZoneId()),
        acceptedCustomerCreditTransfType.getCreationDateTime());
    assertEquals(LocalDate.of(2020, 9, 29), acceptedCustomerCreditTransfType.getSettlementDate());
  }
}