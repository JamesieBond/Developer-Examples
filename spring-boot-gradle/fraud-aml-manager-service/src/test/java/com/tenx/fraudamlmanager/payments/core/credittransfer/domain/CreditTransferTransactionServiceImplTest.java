package com.tenx.fraudamlmanager.payments.core.credittransfer.domain;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraudamlmanager.deviceprofile.domain.PaymentsDeviceProfileService;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.AccountIdentification4Choice;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.ActiveCurrencyAndAmount;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.BranchAndFinancialInstitutionIdentification6;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.CashAccount38;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.CreditTransferTransaction39;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.FraudCheckRequestV1;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.Pacs002;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.Pacs008;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.PartyIdentification135;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.PaymentIdentification7;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.SupplementaryData1;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.SupplementaryDataEnvelope1;
import com.tenx.fraudamlmanager.payments.core.credittransfer.infrastructure.CreditTransferPublishException;
import com.tenx.fraudamlmanager.payments.core.credittransfer.infrastructure.CreditTransferResponseProducer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class CreditTransferTransactionServiceImplTest {

  @MockBean
  CreditTransferTransactionHandler creditTransferTransactionHandler;

  @MockBean
  PaymentsDeviceProfileService paymentsDeviceProfileService;

  @MockBean
  private CreditTransferResponseProducer creditTransferResponseProducer;

  private CreditTransferTransactionServiceImpl creditTransferTransactionServiceImpl;

  @Captor
  private ArgumentCaptor<Pacs008> pacs008Captor;

  @Captor
  private ArgumentCaptor<Pacs002> pacs002ArgumentCaptor;

  private static Stream<Arguments> payloadsSendtoFZM() {
    return Stream.of(Arguments.of(createPact008()));
  }


  @BeforeEach
  public void beforeEach() {
    this.creditTransferTransactionServiceImpl =
        new CreditTransferTransactionServiceImpl(paymentsDeviceProfileService, creditTransferTransactionHandler,
            creditTransferResponseProducer);
  }

  @ParameterizedTest
  @MethodSource("payloadsSendtoFZM")
  public void checkCreditTransferTransactionServiceIml(Pacs008 input)
      throws CreditTransferTransactionException, CreditTransferPublishException {

    Mockito.when(paymentsDeviceProfileService
        .getThreatmetrixDataForPayment("partykey", null))
        .thenReturn(new HashMap<>());

    Assertions.assertThatCode(() -> creditTransferTransactionServiceImpl.creditTransferFinCrimeCheck(input))
        .doesNotThrowAnyException();

    Mockito.verify(creditTransferTransactionHandler, times(1))
        .checkCreditTransferTransaction(pacs008Captor.capture());

    Pacs008 capturedDetails = pacs008Captor.getValue();

    assertEquals(capturedDetails.getCustomerCreditTransfer().getCreditTransferFraudCheckRequest()
            .getCreditTransferTransactionInformation()
            .get(0).getSupplementaryData().get(0).getEnvelope().getTransactionTraceIdentification(),
        input.getCustomerCreditTransfer().getCreditTransferFraudCheckRequest().getCreditTransferTransactionInformation()
            .get(0).getSupplementaryData().get(0).getEnvelope().getTransactionTraceIdentification());

    assertEquals(capturedDetails.getCustomerCreditTransfer().getCreditTransferFraudCheckRequest()
            .getCreditTransferTransactionInformation()
            .get(0).getSupplementaryData().get(0).getEnvelope().getSchema(),
        input.getCustomerCreditTransfer().getCreditTransferFraudCheckRequest().getCreditTransferTransactionInformation()
            .get(0).getSupplementaryData().get(0).getEnvelope().getSchema());

    assertEquals(capturedDetails.getCustomerCreditTransfer().getCreditTransferFraudCheckRequest()
            .getCreditTransferTransactionInformation()
            .get(0).getSupplementaryData().get(0).getEnvelope().getRoutingDestination(),
        input.getCustomerCreditTransfer().getCreditTransferFraudCheckRequest().getCreditTransferTransactionInformation()
            .get(0).getSupplementaryData().get(0).getEnvelope().getRoutingDestination());

    assertEquals(capturedDetails.getCustomerCreditTransfer().getCreditTransferFraudCheckRequest()
            .getCreditTransferTransactionInformation()
            .get(0).getDebtorAccount().getName(),
        input.getCustomerCreditTransfer().getCreditTransferFraudCheckRequest().getCreditTransferTransactionInformation()
            .get(0).getCreditorAccount().getName());

    verify(creditTransferResponseProducer, Mockito.times(1))
        .publishFraudCheckResponse(pacs002ArgumentCaptor.capture());
  }

  private static Pacs008 createPact008() {

    CashAccount38 cashAccount38 = new CashAccount38();
    cashAccount38.setIdentification(new AccountIdentification4Choice());
    cashAccount38.setCurrency("AU");
    cashAccount38.setName("cashAccount");

    SupplementaryDataEnvelope1 envelope = new SupplementaryDataEnvelope1();
    envelope.setPartyKey("partykey");
    envelope.setSchema("schema");
    envelope.setRoutingDestination("routingDestination");
    envelope.setTransactionTraceIdentification("traceIdentification");

    SupplementaryData1 supplementaryData = new SupplementaryData1();
    supplementaryData.setEnvelope(envelope);
    supplementaryData.setPlaceAndName("placeAndName");

    CreditTransferTransaction39 creditTransferTransaction39 = new CreditTransferTransaction39();
    creditTransferTransaction39.setDebtor(new PartyIdentification135());
    creditTransferTransaction39.setDebtorAgent(new BranchAndFinancialInstitutionIdentification6());
    creditTransferTransaction39.setCreditor(new PartyIdentification135());
    creditTransferTransaction39.setCreditorAgent(new BranchAndFinancialInstitutionIdentification6());
    creditTransferTransaction39.setSettlementAmount(new ActiveCurrencyAndAmount());
    creditTransferTransaction39.setPaymentIdentification(new PaymentIdentification7());
    creditTransferTransaction39.setExchangeRate(1.0);
    creditTransferTransaction39.setCreditorAccount(cashAccount38);
    creditTransferTransaction39.setCreditorAgentAccount(cashAccount38);
    creditTransferTransaction39.setDebtorAccount(cashAccount38);
    creditTransferTransaction39.setDebtorAgentAccount(cashAccount38);
    creditTransferTransaction39.setSupplementaryData(Arrays.asList(supplementaryData));

    Pacs008 pacs008 = new Pacs008();
    pacs008.setCustomerCreditTransfer(
        new com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.IsoCreditTransferFraudCheckRequestV01());
    pacs008.getCustomerCreditTransfer().setApplicationHeader(
        new com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.BusinessApplicationHeader());
    pacs008.getCustomerCreditTransfer().setCreditTransferFraudCheckRequest(new FraudCheckRequestV1());
    pacs008.getCustomerCreditTransfer().getCreditTransferFraudCheckRequest()
        .setCreditTransferTransactionInformation(new ArrayList<>());
    pacs008.getCustomerCreditTransfer().getCreditTransferFraudCheckRequest()
        .getCreditTransferTransactionInformation().add(creditTransferTransaction39);

    return pacs008;
  }
}
