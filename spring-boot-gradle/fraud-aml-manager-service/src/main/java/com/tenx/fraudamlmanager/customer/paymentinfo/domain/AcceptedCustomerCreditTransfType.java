package com.tenx.fraudamlmanager.customer.paymentinfo.domain;


import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AcceptedCustomerCreditTransfType extends GenericCustomerCreditTransfType {

  private Integer numberOfTransactions;

  private LocalDate settlementDate;

  private LocalDateTime creationDateTime;


}
