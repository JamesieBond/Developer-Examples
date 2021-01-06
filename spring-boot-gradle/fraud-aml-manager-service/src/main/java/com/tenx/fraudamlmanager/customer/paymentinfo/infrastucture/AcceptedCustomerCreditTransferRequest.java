package com.tenx.fraudamlmanager.customer.paymentinfo.infrastucture;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class AcceptedCustomerCreditTransferRequest {

  private String transactionTraceIdentification;

  private String groupStatus;

  private String channelType;

  private LocalDateTime creationDateTime;

  private Integer numberOfTransactions;

  private boolean senderUrgency;

  private boolean recurringPayment;

  private LocalDate settlementDate;

  private String deviceId;

  private String partyKey;

}

