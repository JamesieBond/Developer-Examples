package com.tenx.fraudamlmanager.payments.model.transactionmonitoring;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExternalCaseDetails {
    private String sourceSystem;

    private String caseType;

    private String sourceCaseID;

    private String caseComments;

    private Boolean notifyCustomer;

}
