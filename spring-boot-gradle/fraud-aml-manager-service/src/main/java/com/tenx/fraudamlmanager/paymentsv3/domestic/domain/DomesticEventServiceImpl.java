package com.tenx.fraudamlmanager.paymentsv3.domestic.domain;

import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudAMLSanctionsCheckResponseCodeV3;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class DomesticEventServiceImpl implements DomesticEventService {

  @Autowired
  private DomesticFinCrimeCheckServiceV3 domesticFinCrimeCheckServiceV3;

  @Autowired
  private DomesticPaymentProducer domesticPaymentProducer;

  @Override
  public void produceEventForFinCrime(DomesticInPaymentV3 domesticInPaymentV3) throws TransactionMonitoringException {
    Optional.of(domesticFinCrimeCheckServiceV3.checkFinCrimeV3(domesticInPaymentV3))
      .filter(fraudCheckV3 -> fraudCheckV3.getStatus() != FraudAMLSanctionsCheckResponseCodeV3.pending)
      .ifPresent(fraudCheckV3 -> domesticPaymentProducer
        .publishDomesticInResponseEvent(fraudCheckV3, domesticInPaymentV3.getTransactionId()));
  }

  @Override
  public void produceEventForFinCrime(DomesticOutPaymentV3 domesticOutPaymentV3, String deviceId)
    throws TransactionMonitoringException {
    Optional.of(domesticFinCrimeCheckServiceV3.checkFinCrimeV3(domesticOutPaymentV3, "" /*TODO: add device key Id*/))
      .filter(fraudCheckV3 -> fraudCheckV3.getStatus() != FraudAMLSanctionsCheckResponseCodeV3.pending)
      .ifPresent(fraudCheckV3 -> domesticPaymentProducer
        .publishDomesticOutResponseEvent(fraudCheckV3, domesticOutPaymentV3.getTransactionId()));
  }

  @Override
  public void produceEventForFinCrime(DomesticOutReturnPaymentV3 domesticOutReturnPaymentV3)
    throws TransactionMonitoringException {
    Optional.of(domesticFinCrimeCheckServiceV3.checkFinCrimeV3(domesticOutReturnPaymentV3))
      .filter(fraudCheckV3 -> fraudCheckV3.getStatus() != FraudAMLSanctionsCheckResponseCodeV3.pending)
      .ifPresent(fraudCheckV3 -> domesticPaymentProducer
        .publishDomesticOutReturnResponseEvent(fraudCheckV3, domesticOutReturnPaymentV3.getTransactionId()));
  }
}
