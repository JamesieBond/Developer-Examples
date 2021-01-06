package com.tenx.fraudamlmanager.paymentsv3.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExternalCaseDetailsRequestV3 {

    private String sourceSystem;

    private String caseType;

    private String sourceCaseID;

    private String caseComments;

    private Boolean notifyCustomer;
}
