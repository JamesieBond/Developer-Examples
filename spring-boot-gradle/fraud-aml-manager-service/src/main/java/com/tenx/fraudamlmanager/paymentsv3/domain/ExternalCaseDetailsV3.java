package com.tenx.fraudamlmanager.paymentsv3.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Niall O'Connell
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExternalCaseDetailsV3 {

    private String sourceSystem;

    private String caseType;

    private String sourceCaseID;

    private String caseComments;

    private Boolean notifyCustomer;

}
