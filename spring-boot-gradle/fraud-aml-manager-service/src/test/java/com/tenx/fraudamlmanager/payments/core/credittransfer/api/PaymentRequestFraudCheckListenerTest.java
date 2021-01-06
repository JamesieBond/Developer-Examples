package com.tenx.fraudamlmanager.payments.core.credittransfer.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.CreditTransferTransactionException;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.CreditTransferTransactionService;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.Pacs008;
import com.tenx.fraudamlmanager.payments.core.credittransfer.infrastructure.CreditTransferPublishException;
import com.tenxbanking.iso.lib.AccountIdentification4Choice;
import com.tenxbanking.iso.lib.ActiveCurrencyAndAmount;
import com.tenxbanking.iso.lib.BranchAndFinancialInstitutionIdentification6;
import com.tenxbanking.iso.lib.BusinessApplicationHeader;
import com.tenxbanking.iso.lib.CashAccount38;
import com.tenxbanking.iso.lib.ChargeBearerType1Code;
import com.tenxbanking.iso.lib.CreditTransferFraudCheckRequestV01;
import com.tenxbanking.iso.lib.CreditTransferTransaction39;
import com.tenxbanking.iso.lib.GroupHeader93;
import com.tenxbanking.iso.lib.IsoCreditTransferFraudCheckRequestV01;
import com.tenxbanking.iso.lib.PartyIdentification135;
import com.tenxbanking.iso.lib.PaymentIdentification7;
import com.tenxbanking.iso.lib.SupplementaryData;
import com.tenxbanking.iso.lib.SupplementaryDataEnvelope;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class PaymentRequestFraudCheckListenerTest {

  private PaymentRequestFraudCheckListener paymentRequestFraudCheckListener;

  @Mock
  private Acknowledgment acknowledgment;

  @MockBean
  private CreditTransferTransactionService creditTransferTransactionService;

  @Captor
  private ArgumentCaptor<Pacs008> creditTransferCaptor;

  private static Stream<Arguments> payloads() {

    return Stream.of(Arguments.of(createIsoCreditTransferFraudCheckRequestV01()));
  }

  @BeforeEach
  public void initTest() { paymentRequestFraudCheckListener = new PaymentRequestFraudCheckListener(creditTransferTransactionService); }

  @ParameterizedTest
  @MethodSource("payloads")
  public void checkPaymentRequestFraudCheckListener(IsoCreditTransferFraudCheckRequestV01 input)
      throws CreditTransferTransactionException, CreditTransferPublishException {

    ConsumerRecord<String, IsoCreditTransferFraudCheckRequestV01> consumerRecord =
        new ConsumerRecord<String, IsoCreditTransferFraudCheckRequestV01>("topic", 0, 0, "key", input);

    paymentRequestFraudCheckListener.processPaymentRequestEvent(consumerRecord, acknowledgment);

    verify(creditTransferTransactionService, VerificationModeFactory.times(1))
        .creditTransferFinCrimeCheck(creditTransferCaptor.capture());

    Pacs008 pacs008 = creditTransferCaptor.getValue();

    assertEquals(pacs008.getCustomerCreditTransfer().getCreditTransferFraudCheckRequest().getCreditTransferTransactionInformation()
            .get(0).getSupplementaryData().get(0).getEnvelope().getTransactionTraceIdentification(),
        input.getCreditTransferFraudCheckRequest().getCreditTransferTransactionInformation()
            .get(0).getSupplementaryData().get(0).getEnvelope().getTransactionTraceIdentification());

    assertEquals(pacs008.getCustomerCreditTransfer().getCreditTransferFraudCheckRequest().getCreditTransferTransactionInformation()
            .get(0).getSupplementaryData().get(0).getEnvelope().getSchema(),
        input.getCreditTransferFraudCheckRequest().getCreditTransferTransactionInformation()
            .get(0).getSupplementaryData().get(0).getEnvelope().getSchema$());

    assertEquals(pacs008.getCustomerCreditTransfer().getCreditTransferFraudCheckRequest().getCreditTransferTransactionInformation()
            .get(0).getSupplementaryData().get(0).getEnvelope().getRoutingDestination(),
        input.getCreditTransferFraudCheckRequest().getCreditTransferTransactionInformation()
            .get(0).getSupplementaryData().get(0).getEnvelope().getRoutingDestination());

    assertEquals(pacs008.getCustomerCreditTransfer().getCreditTransferFraudCheckRequest().getCreditTransferTransactionInformation()
            .get(0).getDebtorAccount().getName(),
        input.getCreditTransferFraudCheckRequest().getCreditTransferTransactionInformation()
            .get(0).getCreditorAccount().getName());
  }

  private static IsoCreditTransferFraudCheckRequestV01 createIsoCreditTransferFraudCheckRequestV01(){
    CashAccount38 cashAccount38 = CashAccount38.newBuilder()
        .setIdentification(new AccountIdentification4Choice())
        .setCurrency("AU")
        .setName("cashAccount")
        .build();

    SupplementaryDataEnvelope envelope = SupplementaryDataEnvelope.newBuilder().setPartyKey("partykey")
        .setSchema$("schema")
        .setRoutingDestination("routingDestination")
        .setTransactionTraceIdentification("traceIdentification")
        .build();

    SupplementaryData supplementaryData = SupplementaryData.newBuilder().setEnvelope(envelope)
        .setPlaceAndName("placeAndName")
        .build();

    CreditTransferTransaction39 creditTransferTransaction39 = CreditTransferTransaction39.newBuilder()
        .setDebtor(new PartyIdentification135())
        .setDebtorAgent(new BranchAndFinancialInstitutionIdentification6())
        .setCreditor(new PartyIdentification135())
        .setCreditorAgent(new BranchAndFinancialInstitutionIdentification6())
        .setSettlementAmount(new ActiveCurrencyAndAmount())
        .setChargeBearer(ChargeBearerType1Code.CRED)
        .setPaymentIdentification(new PaymentIdentification7())
        .setExchangeRate(1.0)
        .setCreditorAccount(cashAccount38)
        .setCreditorAgentAccount(cashAccount38)
        .setDebtorAccount(cashAccount38)
        .setDebtorAgentAccount(cashAccount38)
        .setSupplementaryData(Arrays.asList(supplementaryData))
        .build();

    IsoCreditTransferFraudCheckRequestV01 creditTransferRequest = new IsoCreditTransferFraudCheckRequestV01();
    creditTransferRequest.setApplicationHeader(new BusinessApplicationHeader());
    creditTransferRequest.setCreditTransferFraudCheckRequest(new CreditTransferFraudCheckRequestV01());
    creditTransferRequest.getCreditTransferFraudCheckRequest().setCreditTransferTransactionInformation(new ArrayList<>());
    creditTransferRequest.getCreditTransferFraudCheckRequest().setGroupHeader(new GroupHeader93());
    creditTransferRequest.getCreditTransferFraudCheckRequest().setSupplementaryData(new ArrayList<>());
    creditTransferRequest.getCreditTransferFraudCheckRequest().getSupplementaryData().add(supplementaryData);
    creditTransferRequest.getCreditTransferFraudCheckRequest().getCreditTransferTransactionInformation()
        .add(creditTransferTransaction39);

    return creditTransferRequest;
  }
}
