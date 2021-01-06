package com.tenx.fraudamlmanager.payments.core.credittransfer.infrastructure;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraudamlmanager.infrastructure.feedzaimanager.FeedzaiManagerClient;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.CreditTransferTransactionException;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.CreditTransferTransactionHandler;
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
import feign.FeignException;
import java.util.ArrayList;
import java.util.Arrays;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class CreditTransferTransactionHandlerImplTest {
  @MockBean
  FeedzaiManagerClient feedzaiManagerClient;

  private CreditTransferTransactionHandler creditTransferTransactionHandler;

  @Captor
  private ArgumentCaptor<Pacs008> pacs008ArgumentCaptor;

  @BeforeEach
  public void beforeEach() {
    this.creditTransferTransactionHandler =
        new CreditTransferTransactionHandlerImpl(feedzaiManagerClient);
  }

  @Test
  void checkCreditTransferTransaction() throws CreditTransferTransactionException {
    Pacs008 pacs008 = createPact008();

    Mockito.when(feedzaiManagerClient.checkFinCrime(pacs008)).thenReturn(new Pacs002());

    Assertions.assertThatCode(() -> creditTransferTransactionHandler.checkCreditTransferTransaction(pacs008))
        .doesNotThrowAnyException();

    Mockito.verify(feedzaiManagerClient, times(1))
        .checkFinCrime(pacs008ArgumentCaptor.capture());
  }

  @Test
  void checkCreditTransferTransactionFailure() throws CreditTransferTransactionException {
    Pacs008 pacs008 = createPact008();
    doThrow(FeignException.class).when(feedzaiManagerClient)
        .checkFinCrime(any(Pacs008.class));
    assertThrows(
        FeignException.class, ()->
            creditTransferTransactionHandler.checkCreditTransferTransaction(pacs008));
    verify(feedzaiManagerClient, times(1))
        .checkFinCrime(pacs008ArgumentCaptor.capture());
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
