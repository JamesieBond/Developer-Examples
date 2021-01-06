package com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExternalCaseDetailsV2 {

  private String sourceSystem;

  private String caseType;

  private String sourceCaseID;

  private String caseComments;

  private Boolean notifyCustomer;

}
