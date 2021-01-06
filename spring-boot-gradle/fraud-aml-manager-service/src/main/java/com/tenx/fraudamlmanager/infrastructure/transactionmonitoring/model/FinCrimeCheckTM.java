package com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FinCrimeCheckTM {

    private String transactionId;

    private ResponseCodeV2 status;
}
