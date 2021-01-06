package com.tenx.fraudamlmanager.customer.paymentinfo.api;

import static junit.framework.TestCase.assertEquals;

import com.tenx.fraudamlmanager.customer.paymentinfo.CustomerCreditTransferHelper;
import com.tenx.fraudamlmanager.customer.paymentinfo.domain.RejectedCustomerCreditTransfType;
import com.tenx.fraudamlmanager.customer.paymentinfo.domain.types.ActiveCurrencyAndAmount;
import com.tenxbanking.events.lib.CustomerCreditTransferInitiationCompletedEvent;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;
import org.junit.jupiter.api.Test;

class RejectedICustomerCreditTransfMapperTest {


  @Test
  void mapRejectedTypeTest() throws IOException {
    CustomerCreditTransferInitiationCompletedEvent event = CustomerCreditTransferHelper.readEventFromFile();
    event.getCustomerPaymentStatusReport().getOriginalGroupInformationAndStatus().setGroupStatus("ACSC");

    RejectedCustomerCreditTransfType rejectedCustomerCreditTransfType = RejectedCustomerCreditTransfMapper.MAPPER
        .toRejectedType(event.getCustomerPaymentStatusReport());

    assertEquals("internal_transfer", rejectedCustomerCreditTransfType.getRoutingDestination());
    assertEquals("037886", rejectedCustomerCreditTransfType.getDebtorAgentMemberIdentification());

    assertEquals(new ActiveCurrencyAndAmount(new BigDecimal("1.00"), "AUD"),
        rejectedCustomerCreditTransfType.getInstructedAmount());

    assertEquals("c5b520dd38464ccf94000f4e32c27fc9",
        rejectedCustomerCreditTransfType.getDebtorAccountIdentification());

    assertEquals(new ActiveCurrencyAndAmount(BigDecimal.valueOf(0.97), "EUR"),
        rejectedCustomerCreditTransfType.getSettlementAmount());

    assertEquals("040016497922050",
        rejectedCustomerCreditTransfType.getCreditorAccountIdentification());

    assertEquals("testPartyName",
        rejectedCustomerCreditTransfType.getCreditorName());

    assertEquals("testPartyDebtorName",
        rejectedCustomerCreditTransfType.getDebtorName());

    assertEquals("040016",
        rejectedCustomerCreditTransfType.getCreditorAgentMemberIdentification());

    assertEquals(LocalDateTime.ofInstant(Instant.ofEpochMilli(1604310181820L),
        TimeZone.getDefault().toZoneId()),
        rejectedCustomerCreditTransfType.getCreationDateTime());

    assertEquals("13tr8028-9825-4341-8600-4a2e159ff43b",
        rejectedCustomerCreditTransfType.getPartyKey());

  }
}