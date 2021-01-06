package com.tenx.fraudamlmanager.onboarding.payee.domain;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.TransactionMonitoringClient;
import com.tenx.fraudamlmanager.onboarding.payee.api.PayeeData;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.payeemanager.event.payee.PayeeAccount;
import com.tenx.payeemanager.event.payee.PayeesCreate;
import com.tenx.payeemanager.event.payee.PayeesDelete;
import com.tenx.payeemanager.event.payee.PayeesUpdate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author Shrtuti Gupta
 */
@ExtendWith(SpringExtension.class)
class PayeesEventServiceImplTest {

  @MockBean
  private PayeesEventMetrics payeesEventMetrics;

  @MockBean
  PayeeEventMapperServiceImpl payeeCreateEventMapperServiceImpl;
  @MockBean
  private TransactionMonitoringClient transactionMonitoringClient;
  private PayeesEventServiceImpl payeeCreateEventServiceImpl;

  @BeforeEach
  public void beforeEach() {
    this.payeeCreateEventServiceImpl = new PayeesEventServiceImpl(payeesEventMetrics,
        transactionMonitoringClient,
        payeeCreateEventMapperServiceImpl);
  }

  /**
   * @throws Exception Generic exception
   */
  @Test
  void checkPayeeCreateEventService() throws TransactionMonitoringException {

    PayeesCreate payeesCreate = createPayeeCreateEvent();
    PayeeData payeeData = new PayeeData();

    given(payeeCreateEventMapperServiceImpl.mapPayeeCreateDetails(any()))
        .willReturn(payeeData);
    doNothing().when(transactionMonitoringClient).sendPayeeEvent(payeeData);
    payeeCreateEventServiceImpl.processPayeeCreateEvent(payeesCreate);
    Mockito.verify(payeeCreateEventMapperServiceImpl, times(1))
        .mapPayeeCreateDetails(any());
    Mockito.verify(transactionMonitoringClient, times(1))
        .sendPayeeEvent(payeeData);
    Mockito.verify(payeesEventMetrics, VerificationModeFactory.times(1))
        .incrementFAMPayeesRequestsToTMASuccess();
  }

  @Test
  void checkPayeeCreateEventServiceFailure() throws TransactionMonitoringException {
    PayeesCreate payeesCreate = createPayeeCreateEvent();
    PayeeData payeeData = new PayeeData();

    given(payeeCreateEventMapperServiceImpl.mapPayeeCreateDetails(any()))
        .willReturn(payeeData);
    doThrow(TransactionMonitoringException.class).when(transactionMonitoringClient)
        .sendPayeeEvent(any(PayeeData.class));
    assertThrows(
        TransactionMonitoringException.class, () ->
            payeeCreateEventServiceImpl.processPayeeCreateEvent(payeesCreate));
    verify(payeesEventMetrics, VerificationModeFactory.times(1))
        .incrementFAMPayeesRequestsToTMAFailed();
  }

  private PayeesCreate createPayeeCreateEvent() {

    PayeeAccount payeeAccount = PayeeAccount.newBuilder()
        .setId("id")
        .setIdentification("identification")
        .setIdentificationType("identificationType")
        .setName("name")
        .build();
    List<PayeeAccount> payeeAccountList = new ArrayList<PayeeAccount>();
    payeeAccountList.add(payeeAccount);

    PayeesCreate payeesCreate = PayeesCreate.newBuilder()
        .setPartyKey("partyKey")
        .setPayeeId("payeeId")
        .setPayeeName("payeeName")
        .setCategory("category")
        .setReference("reference")
        .setUpdatedDateTime("2019-08-08")
        .setCreatedDateTime("2019-08-08")
        .setPayeeAccounts(payeeAccountList)
        .setPayerPartyKey("payerPartyKey")
        .setPayerPartyType("type")
        .setPayerPartyStatus("status")
        .build();
    return payeesCreate;
  }

  @Test
  void checkPayeeDeleteEventService() throws TransactionMonitoringException {

    PayeesDelete payeesDelete = createPayeeDeleteEvent();
    PayeeData payeeData = new PayeeData();

    given(payeeCreateEventMapperServiceImpl.mapPayeeDeleteDetails(any()))
        .willReturn(payeeData);
    doNothing().when(transactionMonitoringClient).sendPayeeEvent(payeeData);
    payeeCreateEventServiceImpl.processPayeeDeleteEvent(payeesDelete);
    Mockito.verify(payeeCreateEventMapperServiceImpl, times(1))
        .mapPayeeDeleteDetails(any());
    Mockito.verify(transactionMonitoringClient, times(1))
        .sendPayeeEvent(payeeData);
    Mockito.verify(payeesEventMetrics, VerificationModeFactory.times(1))
        .incrementFAMPayeesRequestsToTMASuccess();
  }

  @Test
  void checkPayeeDeleteEventServiceFailure() throws TransactionMonitoringException {

    PayeesDelete payeesDelete = createPayeeDeleteEvent();
    PayeeData payeeData = new PayeeData();

    given(payeeCreateEventMapperServiceImpl.mapPayeeDeleteDetails(any()))
        .willReturn(payeeData);
    doThrow(TransactionMonitoringException.class).when(transactionMonitoringClient)
        .sendPayeeEvent(any(PayeeData.class));
    assertThrows(
        TransactionMonitoringException.class, () ->
            payeeCreateEventServiceImpl.processPayeeDeleteEvent(payeesDelete));
    verify(payeesEventMetrics, VerificationModeFactory.times(1))
        .incrementFAMPayeesRequestsToTMAFailed();
  }

  private PayeesDelete createPayeeDeleteEvent() {

    PayeeAccount payeeAccount = PayeeAccount.newBuilder()
        .setId("id")
        .setIdentification("identification")
        .setIdentificationType("identificationType")
        .setName("name")
        .build();
    List<PayeeAccount> payeeAccountList = new ArrayList<PayeeAccount>();
    payeeAccountList.add(payeeAccount);

    PayeesDelete payeesDelete = PayeesDelete.newBuilder()
        .setPartyKey("partyKey")
        .setPayeeId("payeeId")
        .setPayeeName("payeeName")
        .setCategory("category")
        .setReference("reference")
        .setUpdatedDateTime("2019-08-08")
        .setCreatedDateTime("2019-08-08")
        .setPayeeAccounts(payeeAccountList)
        .setPayerPartyKey("payerPartyKey")
        .setPayerPartyType("type")
        .setPayerPartyStatus("status")
        .build();
    return payeesDelete;
  }

  @Test
  void checkPayeeUpdateEventService() throws TransactionMonitoringException {

    PayeesUpdate payeesUpdate = payeeUpdateEvent();
    PayeeData payeeData = new PayeeData();

    given(payeeCreateEventMapperServiceImpl.mapPayeeUpdateDetails(any()))
        .willReturn(payeeData);
    doNothing().when(transactionMonitoringClient).sendPayeeEvent(payeeData);
    payeeCreateEventServiceImpl.processPayeeUpdateEvent(payeesUpdate);
    Mockito.verify(payeeCreateEventMapperServiceImpl, times(1))
        .mapPayeeUpdateDetails(any());
    Mockito.verify(transactionMonitoringClient, times(1))
        .sendPayeeEvent(payeeData);
    Mockito.verify(payeesEventMetrics, VerificationModeFactory.times(1))
        .incrementFAMPayeesRequestsToTMASuccess();
  }

  @Test
  void checkPayeeUpdateEventServiceFailure() throws TransactionMonitoringException {

    PayeesUpdate payeesUpdate = payeeUpdateEvent();
    PayeeData payeeData = new PayeeData();

    given(payeeCreateEventMapperServiceImpl.mapPayeeUpdateDetails(any()))
        .willReturn(payeeData);
    doThrow(TransactionMonitoringException.class).when(transactionMonitoringClient)
        .sendPayeeEvent(any(PayeeData.class));
    assertThrows(
        TransactionMonitoringException.class, () ->
            payeeCreateEventServiceImpl.processPayeeUpdateEvent(payeesUpdate));
    verify(payeesEventMetrics, VerificationModeFactory.times(1))
        .incrementFAMPayeesRequestsToTMAFailed();
  }

  private PayeesUpdate payeeUpdateEvent() {

    PayeesUpdate payeesUpdate = PayeesUpdate.newBuilder()
        .setHeaderPartyKey("partyKey")
        .setPayeeId("payeeId")
        .setName("payeeName")
        .setAccountId("category")
        .build();
    return payeesUpdate;
  }


}
