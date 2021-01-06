package com.tenx.fraudamlmanager.customer.paymentinfo.domain;

import com.tenx.fraudamlmanager.customer.paymentinfo.infrastucture.CustomerCreditTransferException;
import com.tenxbanking.events.lib.CustomerCreditTransferInitiationCompletedEvent;

public interface CustomerCreditTransferEventService {


  public void processCustomerCreditTransferEvent(CustomerCreditTransferInitiationCompletedEvent event,
      String transactioTraceIdentification) throws CustomerCreditTransferException;


}
