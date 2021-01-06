package com.tenx.fraudamlmanager.payments.core.credittransfer.domain;

import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.Pacs008;
import com.tenx.fraudamlmanager.payments.core.credittransfer.infrastructure.CreditTransferPublishException;

public interface CreditTransferTransactionService {

  void creditTransferFinCrimeCheck(Pacs008 pacs008) throws CreditTransferTransactionException, CreditTransferPublishException;
}
