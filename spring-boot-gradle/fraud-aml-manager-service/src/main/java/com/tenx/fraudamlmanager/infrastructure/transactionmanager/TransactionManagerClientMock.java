package com.tenx.fraudamlmanager.infrastructure.transactionmanager;

import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.exceptions.TransactionManagerException;
import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.model.FinCrimeCheckTM;
import com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.infrastructure.transactionmanager.FinCrimeCheckTMV2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(value = "transactionmanager.enableMock", havingValue = "true")
public class TransactionManagerClientMock implements TransactionManagerClient {

  /**
   * @param finCrimeCheckTM the payload to pass
   */
  @Override
  public void postFraudAMLSanctionsNotification(FinCrimeCheckTM finCrimeCheckTM) throws TransactionManagerException {
    log.info("CheckFinCrime for transactionId {} mocked", finCrimeCheckTM.getTransactionId());
  }

  /**
   * @param finCrimeCheckTM the payload to pass
   */
  @Override
  public void postFraudAMLSanctionsNotification(FinCrimeCheckTMV2 finCrimeCheckTM)
    throws TransactionManagerException {
    log.info("CheckFinCrime for transactionId {} mocked", finCrimeCheckTM.getTransactionId());
  }
}

