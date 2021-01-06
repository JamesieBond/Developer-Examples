package com.tenx.fraudamlmanager.customer.paymentinfo.domain;

import lombok.Data;

@Data
public abstract class GenericCustomerCreditTransfType {

  private String transactionTraceIdentification;

  private String groupStatus;

  private String deviceId;

  private String partyKey;

}
