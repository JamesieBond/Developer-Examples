package com.tenx.fraudamlmanager.payments.model.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Niall O'Connell
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FraudCheckResponse {
    private boolean clear;

    private String responseCode;

}
