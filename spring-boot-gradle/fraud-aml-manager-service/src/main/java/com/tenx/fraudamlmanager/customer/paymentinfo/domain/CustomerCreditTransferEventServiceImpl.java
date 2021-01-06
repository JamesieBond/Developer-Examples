package com.tenx.fraudamlmanager.customer.paymentinfo.domain;

import com.tenx.fraudamlmanager.customer.paymentinfo.api.GenericCustomerCreditTransfMapper;
import com.tenx.fraudamlmanager.customer.paymentinfo.infrastucture.CustomerCreditTransfFeedzaiConnector;
import com.tenx.fraudamlmanager.customer.paymentinfo.infrastucture.CustomerCreditTransferException;
import com.tenx.fraudamlmanager.deviceprofile.domain.PaymentDeviceProfile;
import com.tenx.fraudamlmanager.deviceprofile.domain.PaymentsDeviceProfileService;
import com.tenxbanking.events.lib.CustomerCreditTransferInitiationCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerCreditTransferEventServiceImpl implements CustomerCreditTransferEventService {

  private final PaymentsDeviceProfileService paymentsDeviceProfileService;

  private final CustomerCreditTransfFeedzaiConnector feedzaiConnector;

  private static final String PROCESSING_CCTI_MESSAGE = "Processing credit transfer message with transaction identifier {}";


  @Override
  public void processCustomerCreditTransferEvent(CustomerCreditTransferInitiationCompletedEvent event,
      String transactionTraceIdentification) throws CustomerCreditTransferException {

    String groupStatus = GenericCustomerCreditTransfMapper
        .extractGroupStatus(event);

    if (!CustomerCreditTransferStrategy.isKnownType(groupStatus)) {
      log.warn("Received message with unknown group status {}",
          groupStatus);
      return;
    }
    log.info(PROCESSING_CCTI_MESSAGE,
        transactionTraceIdentification);

    GenericCustomerCreditTransfType type = CustomerCreditTransferStrategy.mapperForGroupStatus(groupStatus)
        .apply(event);
    type.setGroupStatus(groupStatus);
    type.setTransactionTraceIdentification(transactionTraceIdentification);
    type.setDeviceId(fetchDeviceKeyIdByPartyKey(type.getPartyKey()));
    CustomerCreditTransferStrategy.feedzaiConsumerFor(feedzaiConnector, type).accept(type);
  }


  private String fetchDeviceKeyIdByPartyKey(String partyKey) {
    PaymentDeviceProfile deviceProfile = paymentsDeviceProfileService.fetchThreatMetrixResultUsingPartyKey(partyKey);
    return deviceProfile.getDeviceKeyId();
  }


}
