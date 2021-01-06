package com.tenx.fraudamlmanager.cases.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaseDetails {

  private String sourceSystem;

  private String caseType;

  private String sourceCaseID;

  private String caseComments;

  private Boolean notifyCustomer;

}
