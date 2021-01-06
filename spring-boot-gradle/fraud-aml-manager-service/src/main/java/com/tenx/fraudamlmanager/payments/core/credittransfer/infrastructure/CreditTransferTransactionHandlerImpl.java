package com.tenx.fraudamlmanager.payments.core.credittransfer.infrastructure;

import com.tenx.fraudamlmanager.infrastructure.feedzaimanager.FeedzaiManagerClient;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.CreditTransferTransactionException;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.CreditTransferTransactionHandler;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.CreditTransferTransaction39;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.Pacs002;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.Pacs008;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.SupplementaryData1;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.SupplementaryDataEnvelope1;
import java.util.Collection;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreditTransferTransactionHandlerImpl implements CreditTransferTransactionHandler {

  private static final String FEEDZAI_MANAGER_FAILED_MESSAGE =
      "Failed to call Feedzai Manager for {} with ID: {}";
  private static final String FEEDZAI_MANAGER_CALLED_LOG =
      "Calling Feedzai Manager for {} with ID: {}";

  private final FeedzaiManagerClient feedzaiManagerClient;

  @Override
  public Pacs002 checkCreditTransferTransaction(Pacs008 pacs008) throws CreditTransferTransactionException {
    String transactionTraceIdentification = pacs008.getCustomerCreditTransfer().getCreditTransferFraudCheckRequest()
        .getCreditTransferTransactionInformation().stream()
        .filter(Objects::nonNull)
        .map(CreditTransferTransaction39::getSupplementaryData)
        .flatMap(Collection::stream)
        .map(SupplementaryData1::getEnvelope)
        .filter(Objects::nonNull)
        .map(SupplementaryDataEnvelope1::getTransactionTraceIdentification)
        .findFirst().orElse("transaction identification not found");

    try {
      log.info(FEEDZAI_MANAGER_CALLED_LOG,
          Pacs008.class.getSimpleName(),
          transactionTraceIdentification);

      return feedzaiManagerClient.checkFinCrime(pacs008);
    } catch (CreditTransferTransactionException e) {
      log.error("Error sending payment with key {}", transactionTraceIdentification);
      throw new CreditTransferTransactionException(
            CreditTransferTransactionException.Error.GENERAL_SERVICE_ERROR,
          transactionTraceIdentification);
    }
  }
}
