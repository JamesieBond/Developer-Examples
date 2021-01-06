package com.tenx.fraudamlmanager.payments.core.credittransfer.domain;

import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.Pacs002;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.Pacs008;

public interface CreditTransferTransactionHandler {
    Pacs002 checkCreditTransferTransaction(Pacs008 pacs008) throws CreditTransferTransactionException;
}
