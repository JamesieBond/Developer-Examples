package com.tenx.fraudamlmanager.payments.core.credittransfer.infrastructure;

import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.Pacs002;

public interface CreditTransferResponseProducer {

  void publishFraudCheckResponse(Pacs002 pacs002) throws CreditTransferPublishException;
}
