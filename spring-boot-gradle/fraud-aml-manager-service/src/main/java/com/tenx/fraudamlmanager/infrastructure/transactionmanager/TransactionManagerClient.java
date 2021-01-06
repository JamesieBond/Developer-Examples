package com.tenx.fraudamlmanager.infrastructure.transactionmanager;

import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.exceptions.TransactionManagerException;
import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.model.FinCrimeCheckTM;
import com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.infrastructure.transactionmanager.FinCrimeCheckTMV2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "transaction-manager", url = "${transactionmanager.url}", configuration =
  TransactionManagerConfig.class)
@ConditionalOnProperty(value = "transactionmanager.enableMock", havingValue = "false", matchIfMissing = true)
public interface TransactionManagerClient {

  /**
   * @param finCrimeCheckTM the payload to pass
   */
  @PostMapping(value = "/v1/Fraud-AML-Sanctions-notification", consumes = "application/json")
  void postFraudAMLSanctionsNotification(@RequestBody FinCrimeCheckTM finCrimeCheckTM)
    throws TransactionManagerException;

  /**
   * @param finCrimeCheckTM the payload to pass
   */
  @PostMapping(value = "/v1/Fraud-AML-Sanctions-notification", consumes = "application/json")
  void postFraudAMLSanctionsNotification(@RequestBody FinCrimeCheckTMV2 finCrimeCheckTM)
    throws TransactionManagerException;


}