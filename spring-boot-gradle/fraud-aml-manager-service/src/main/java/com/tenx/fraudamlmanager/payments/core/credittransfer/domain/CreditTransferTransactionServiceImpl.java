package com.tenx.fraudamlmanager.payments.core.credittransfer.domain;

import com.tenx.fraudamlmanager.deviceprofile.domain.PaymentsDeviceProfileService;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.CreditTransferTransaction39;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.Pacs008;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.SupplementaryData1;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.SupplementaryDataEnvelope1;
import com.tenx.fraudamlmanager.payments.core.credittransfer.infrastructure.CreditTransferPublishException;
import com.tenx.fraudamlmanager.payments.core.credittransfer.infrastructure.CreditTransferResponseProducer;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreditTransferTransactionServiceImpl implements CreditTransferTransactionService {

  private final PaymentsDeviceProfileService paymentsDeviceProfileService;

  private final CreditTransferTransactionHandler creditTransferTransactionHandler;

  private final CreditTransferResponseProducer creditTransferResponseProducer;

  @Override
  public void creditTransferFinCrimeCheck(
      Pacs008 pacs008) throws CreditTransferTransactionException, CreditTransferPublishException {

    String partyKey = pacs008.getCustomerCreditTransfer().getCreditTransferFraudCheckRequest()
        .getCreditTransferTransactionInformation().stream()
        .map(CreditTransferTransaction39::getSupplementaryData)
        .flatMap(Collection::stream)
        .map(SupplementaryData1::getEnvelope)
        .map(SupplementaryDataEnvelope1::getPartyKey)
        .findFirst().orElse("");

    pacs008.getCustomerCreditTransfer().getCreditTransferFraudCheckRequest().getCreditTransferTransactionInformation()
        .get(0).getSupplementaryData().get(0).getEnvelope().setThreatmetrixData(paymentsDeviceProfileService
        .getThreatmetrixDataForPayment(partyKey, null));

    creditTransferResponseProducer.publishFraudCheckResponse(creditTransferTransactionHandler.checkCreditTransferTransaction(pacs008));
  }
}
