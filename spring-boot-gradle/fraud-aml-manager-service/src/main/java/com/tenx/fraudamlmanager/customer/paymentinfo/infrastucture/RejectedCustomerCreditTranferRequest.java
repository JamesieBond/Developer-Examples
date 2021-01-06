package com.tenx.fraudamlmanager.customer.paymentinfo.infrastucture;

import com.tenx.fraudamlmanager.customer.paymentinfo.infrastucture.types.CurrencyAndAmount;
import com.tenx.fraudamlmanager.customer.paymentinfo.infrastucture.types.User;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class RejectedCustomerCreditTranferRequest {

  private String transactionTraceIdentification;

  private String groupStatus;

  private String channelType;

  private String routingDestination;

  private LocalDateTime creationDateTime;

  private String infoType;

  private String deviceId;

  private String partyKey;

  private CurrencyAndAmount instructedAmount;

  private CurrencyAndAmount settlementAmount;

  private User debtor;

  private User creditor;

  private String creditorName;

}
