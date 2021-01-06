package com.tenx.fraudamlmanager.customer.paymentinfo.domain;

import com.tenx.fraudamlmanager.customer.paymentinfo.domain.types.ActiveCurrencyAndAmount;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RejectedCustomerCreditTransfType extends GenericCustomerCreditTransfType {

  private String routingDestination;

  private String debtorAgentMemberIdentification;
  private String debtorAccountIdentification;
  private String debtorName;

  private ActiveCurrencyAndAmount instructedAmount;

  private String creditorAgentMemberIdentification;
  private String creditorAccountIdentification;
  private String creditorName;

  private ActiveCurrencyAndAmount settlementAmount;

  private LocalDateTime creationDateTime;


}
