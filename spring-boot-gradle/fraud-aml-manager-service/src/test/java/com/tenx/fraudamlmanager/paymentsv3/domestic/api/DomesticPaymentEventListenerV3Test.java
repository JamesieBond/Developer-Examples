package com.tenx.fraudamlmanager.paymentsv3.domestic.api;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraud.payments.AccountDetails;
import com.tenx.fraud.payments.Address;
import com.tenx.fraud.payments.Amount;
import com.tenx.fraud.payments.ChargesInformation;
import com.tenx.fraud.payments.fpsin.FPSInTransaction;
import com.tenx.fraud.payments.fpsin.FPSInboundPaymentFraudCheck;
import com.tenx.fraud.payments.onus.FPSOutReturnTransaction;
import com.tenx.fraud.payments.onus.FPSOutboundReturnPaymentFraudCheck;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.paymentsv3.domestic.domain.DomesticEventService;
import com.tenx.fraudamlmanager.paymentsv3.domestic.domain.DomesticInPaymentV3;
import com.tenx.fraudamlmanager.paymentsv3.domestic.domain.DomesticOutReturnPaymentV3;
import java.text.ParseException;
import java.util.ArrayList;
import org.assertj.core.api.Assertions;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class DomesticPaymentEventListenerV3Test {

  @Captor
  ArgumentCaptor<DomesticInPaymentV3> domesticInPaymentV3ArgumentCaptor;

  @Captor
  ArgumentCaptor<DomesticOutReturnPaymentV3> domesticOutReturnPaymentV3ArgumentCaptor;

  @Mock
  private DomesticEventService domesticEventService;

  @Mock
  private Acknowledgment acknowledgment;

  private DomesticPaymentEventListenerV3 domesticPaymentEventListenerV3;

  @BeforeEach
  public void beforeEach() {
    domesticPaymentEventListenerV3 = new DomesticPaymentEventListenerV3(domesticEventService);
  }

  @Test
  public void testHandleInboundPaymentEventSuccess() throws TransactionMonitoringException {

    FPSInboundPaymentFraudCheck inboundPaymentFraudCheck = FPSInboundPaymentFraudCheck.newBuilder()
        .setTransaction(FPSInTransaction.newBuilder()
            .setId("TSID")
            .setAmount(new Amount("eu", "50", "usd", "50"))
            .setDate("2019-11-21T11:35:24.584+0000")
            .setMessageDate("2019-11-21T12:35:24.584+0000")
            .setPaymentTypeInformation("SOP:30")
            .setPaymentIdentification("paymentIdentification")
            .setEndToEndIdentification("endToEnd")
            .setInstructingAgentMemberIdentification("instructingAgentMemberIdentification")
            .setInterbankSettlementDate("2020-04-05T19:52:13.584+0000")
            .setOriginatingCreditInstitution("CreditInstitution")
            .setChargeInformation(new ChargesInformation())
            .setExchangeRate("1.00")
            .setRemittenceInformation("remittence")
            .setInstructedAmount(Amount.newBuilder()
                .setBaseValue("10000")
                .setBaseCurrency("EUR")
                .setCurrency("EUR")
                .setValue("10000")
                .build())
            .setStatus("st").setReference("rf").build())
        .setPartyKey("partyKey")
        .setBalanceBefore((new Amount("cny", "100", "czk", "100")))
        .setCreditor(new AccountDetails("creACN", "creBID", "creBID", "cnn", new Address("test"), "creACN"))
        .setDebtor(new AccountDetails("debACN", "debBID", "debBID", "cnn", new Address("test"), ""))
        .build();

    Assertions.assertThatCode(() -> domesticPaymentEventListenerV3
        .handleInboundPaymentEvent(inboundPaymentFraudCheck, acknowledgment)).doesNotThrowAnyException();

    verify(domesticEventService, times(1)).produceEventForFinCrime(domesticInPaymentV3ArgumentCaptor.capture());
    verify(acknowledgment, times(1)).acknowledge();
    DomesticInPaymentV3 domesticInPaymentV3 = domesticInPaymentV3ArgumentCaptor.getValue();
    Assert.assertEquals("TSID", domesticInPaymentV3.getTransactionId());
    Assert.assertEquals("st", domesticInPaymentV3.getTransactionStatus());
    Assert.assertEquals("rf", domesticInPaymentV3.getTransactionReference());
    Assert.assertEquals("usd", domesticInPaymentV3.getAmount().getBaseCurrency());
    Assert.assertEquals(50.0, domesticInPaymentV3.getAmount().getBaseValue(), 0);
    Assert.assertEquals("czk", domesticInPaymentV3.getBalanceBefore().getBaseCurrency());
    Assert.assertEquals(100.0, domesticInPaymentV3.getBalanceBefore().getBaseValue(), 0);
    Assert.assertEquals("creACN", domesticInPaymentV3.getCreditorAccountDetails().getAccountNumber());
    Assert.assertEquals("creBID", domesticInPaymentV3.getCreditorAccountDetails().getBankId());
    Assert.assertEquals("debACN", domesticInPaymentV3.getDebtorAccountDetails().getAccountNumber());
    Assert.assertEquals("debBID", domesticInPaymentV3.getDebtorAccountDetails().getBankId());
    Assert.assertEquals(DateTime.parse("2019-11-21T12:35:24.584+0000").toDate(), domesticInPaymentV3.getMessageDate());
    Assert.assertEquals(DateTime.parse("2019-11-21T11:35:24.584+0000").toDate(),
        domesticInPaymentV3.getTransactionDate());
  }

  @Test
  public void testHandleOutboundReturnPaymentEventSuccess() throws TransactionMonitoringException {

    FPSOutboundReturnPaymentFraudCheck fpsOutboundReturnPaymentFraudCheck = FPSOutboundReturnPaymentFraudCheck
        .newBuilder()
        .setTransaction(FPSOutReturnTransaction.newBuilder()
            .setId("TSID")
            .setAmount(new Amount("eu", "50", "usd", "50"))
            .setDate("2019-11-21T11:35:24.584+0000")
            .setTags(new ArrayList<>())
            .setMessageDate("2019-11-21T12:35:24.584+0000")
            .setPaymentTypeInformation("SOP:30")
            .setStatus("st").setReference("rf").build())
        .setPartyKey("partyKey")
        .setBalanceBefore((new Amount("cny", "100", "czk", "100")))
        .setCreditor(new AccountDetails("creACN", "creBID", "creBID", "cnn", new Address("test"), "creACN"))
        .setDebtor(new AccountDetails("debACN", "debBID", "debBID", "cnn", new Address("test"), ""))
        .build();

    Assertions.assertThatCode(() -> domesticPaymentEventListenerV3
        .handleOutboundReturnPaymentEvent(fpsOutboundReturnPaymentFraudCheck, acknowledgment))
        .doesNotThrowAnyException();

    verify(domesticEventService, times(1))
        .produceEventForFinCrime(domesticOutReturnPaymentV3ArgumentCaptor.capture());
    verify(acknowledgment, times(1)).acknowledge();
    DomesticOutReturnPaymentV3 domesticOutReturnPaymentV3 = domesticOutReturnPaymentV3ArgumentCaptor.getValue();
    Assert.assertEquals("TSID", domesticOutReturnPaymentV3.getTransactionId());
    Assert.assertEquals("st", domesticOutReturnPaymentV3.getTransactionStatus());
    Assert.assertEquals("rf", domesticOutReturnPaymentV3.getTransactionReference());
    Assert.assertEquals("usd", domesticOutReturnPaymentV3.getAmount().getBaseCurrency());
    Assert.assertEquals(50.0, domesticOutReturnPaymentV3.getAmount().getBaseValue(), 0);
    Assert.assertEquals("czk", domesticOutReturnPaymentV3.getBalanceBefore().getBaseCurrency());
    Assert.assertEquals(100.0, domesticOutReturnPaymentV3.getBalanceBefore().getBaseValue(), 0);
    Assert.assertEquals("creACN", domesticOutReturnPaymentV3.getCreditorAccountDetails().getAccountNumber());
    Assert.assertEquals("creBID", domesticOutReturnPaymentV3.getCreditorAccountDetails().getBankId());
    Assert.assertEquals("debACN", domesticOutReturnPaymentV3.getDebtorAccountDetails().getAccountNumber());
    Assert.assertEquals("debBID", domesticOutReturnPaymentV3.getDebtorAccountDetails().getBankId());
    Assert.assertEquals(DateTime.parse("2019-11-21T12:35:24.584+0000").toDate(),
        domesticOutReturnPaymentV3.getMessageDate());
    Assert.assertEquals(DateTime.parse("2019-11-21T11:35:24.584+0000").toDate(),
        domesticOutReturnPaymentV3.getTransactionDate());
  }

  @Test
  public void testHandleInboundPaymentEventParseException() {

    FPSInboundPaymentFraudCheck inboundPaymentFraudCheck = FPSInboundPaymentFraudCheck.newBuilder()
        .setTransaction(FPSInTransaction.newBuilder()
            .setId("TSID")
            .setAmount(new Amount("eu", "50", "usd", "50"))
            .setDate("2019-11-21T11:35:24")
            .setMessageDate("2019-11-21")
            .setPaymentTypeInformation("SOP:30")
            .setPaymentIdentification("paymentIdentification")
            .setEndToEndIdentification("endToEnd")
            .setInstructingAgentMemberIdentification("instructingAgentMemberIdentification")
            .setInterbankSettlementDate("2020-04-05T19:52:13.584+0000")
            .setOriginatingCreditInstitution("CreditInstitution")
            .setChargeInformation(new ChargesInformation())
            .setExchangeRate("1.00")
            .setRemittenceInformation("remittence")
            .setInstructedAmount(Amount.newBuilder()
                .setBaseValue("10000")
                .setBaseCurrency("EUR")
                .setCurrency("EUR")
                .setValue("10000")
                .build())
            .setStatus("st").setReference("rf").build())
        .setPartyKey("partyKey")
        .setBalanceBefore((new Amount("cny", "100", "czk", "100")))
        .setCreditor(new AccountDetails("creACN", "creACN", "creBID", "cnn", new Address(), "creACN"))
        .setDebtor(new AccountDetails("debBID", "debACN", "debBID", "cnn", new Address(), ""))
        .build();

    assertThrows(
        ParseException.class, () ->
            domesticPaymentEventListenerV3
                .handleInboundPaymentEvent(inboundPaymentFraudCheck, acknowledgment));

    verifyZeroInteractions(domesticEventService);

    verify(acknowledgment, times(0)).acknowledge();

  }

  @Test
  public void testHandleOutboundReturnPaymentEventParseException() {

    FPSOutboundReturnPaymentFraudCheck fpsOutboundReturnPaymentFraudCheck = FPSOutboundReturnPaymentFraudCheck
        .newBuilder()
        .setTransaction(FPSOutReturnTransaction.newBuilder()
            .setId("TSID")
            .setAmount(new Amount("eu", "50", "usd", "50"))
            .setDate("2019-11-21T11:35:24.584Z")
            .setTags(new ArrayList<>())
            .setPaymentTypeInformation("SOP:30")
            .setMessageDate("2019-11-21T12:35:24.584Z")
            .setStatus("st").setReference("rf").build())
        .setPartyKey("partyKey")
        .setBalanceBefore((new Amount("cny", "100", "czk", "100")))
        .setCreditor(new AccountDetails("creACN", "creACN", "creBID", "cnn", new Address(), "creACN"))
        .setDebtor(new AccountDetails("debBID", "debACN", "debBID", "cnn", new Address(), ""))
        .build();

    assertThrows(
        ParseException.class, () ->
            domesticPaymentEventListenerV3
                .handleOutboundReturnPaymentEvent(fpsOutboundReturnPaymentFraudCheck, acknowledgment));

    verifyZeroInteractions(domesticEventService);

    verify(acknowledgment, times(0)).acknowledge();

  }

  @Test
  public void testHandleInboundPaymentEventTMAException() throws TransactionMonitoringException {

    FPSInboundPaymentFraudCheck inboundPaymentFraudCheck = FPSInboundPaymentFraudCheck.newBuilder()
        .setTransaction(FPSInTransaction.newBuilder()
            .setId("TSID")
            .setAmount(new Amount("eu", "50", "usd", "50"))
            .setDate("2019-11-21T11:35:24.584+0000")
            .setMessageDate("2019-11-21T12:35:24.584+0000")
            .setPaymentTypeInformation("SOP:30")
            .setEndToEndIdentification("endToEnd")
            .setPaymentIdentification("paymentIdentification")
            .setInstructingAgentMemberIdentification("instructingAgentMemberIdentification")
            .setInterbankSettlementDate("2020-04-05T19:52:13.584+0000")
            .setOriginatingCreditInstitution("CreditInstitution")
            .setChargeInformation(new ChargesInformation())
            .setExchangeRate("1.00")
            .setRemittenceInformation("remittence")
            .setInstructedAmount(Amount.newBuilder()
                .setBaseValue("10000")
                .setBaseCurrency("EUR")
                .setCurrency("EUR")
                .setValue("10000")
                .build())
            .setStatus("st").setReference("rf").build())
        .setPartyKey("partyKey")
        .setBalanceBefore((new Amount("cny", "100", "czk", "100")))
        .setCreditor(new AccountDetails("creACN", "creACN", "creBID", "cnn", new Address(), "creACN"))
        .setDebtor(new AccountDetails("debBID", "debACN", "debBID", "cnn", new Address(), ""))
        .build();

    doThrow(new TransactionMonitoringException(500, "TMA issue"))
        .when(domesticEventService)
        .produceEventForFinCrime(any(DomesticInPaymentV3.class));

    assertThrows(
        TransactionMonitoringException.class, () ->
            domesticPaymentEventListenerV3
                .handleInboundPaymentEvent(inboundPaymentFraudCheck, acknowledgment));

    verify(domesticEventService, times(1)).produceEventForFinCrime(any(DomesticInPaymentV3.class));
    verify(acknowledgment, times(0)).acknowledge();

  }

  @Test
  public void testHandleOutboundReturnPaymentEventTMAException() throws TransactionMonitoringException {

    FPSOutboundReturnPaymentFraudCheck fpsOutboundReturnPaymentFraudCheck = FPSOutboundReturnPaymentFraudCheck
        .newBuilder()
        .setTransaction(FPSOutReturnTransaction.newBuilder()
            .setId("TSID")
            .setAmount(new Amount("eu", "50", "usd", "50"))
            .setDate("2019-11-21T11:35:24.584+0000")
            .setTags(new ArrayList<>())
            .setMessageDate("2019-11-21T12:35:24.584+0000")
            .setPaymentTypeInformation("SOP:30")
            .setStatus("st").setReference("rf").build())
        .setPartyKey("partyKey")
        .setBalanceBefore((new Amount("cny", "100", "czk", "100")))
        .setCreditor(new AccountDetails("creACN", "creACN", "creBID", "cnn", new Address(), "creACN"))
        .setDebtor(new AccountDetails("debBID", "debACN", "debBID", "cnn", new Address(), ""))
        .build();

    doThrow(new TransactionMonitoringException(500, "TMA issue"))
        .when(domesticEventService)
        .produceEventForFinCrime(any(DomesticOutReturnPaymentV3.class));

    assertThrows(
        TransactionMonitoringException.class, () ->
            domesticPaymentEventListenerV3
                .handleOutboundReturnPaymentEvent(fpsOutboundReturnPaymentFraudCheck, acknowledgment));

    verify(domesticEventService, times(1)).produceEventForFinCrime(any(DomesticOutReturnPaymentV3.class));
    verify(acknowledgment, times(0)).acknowledge();

  }

}
