package com.tenx.fraudamlmanager.payments.model.transactionmonitoringV2;

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
