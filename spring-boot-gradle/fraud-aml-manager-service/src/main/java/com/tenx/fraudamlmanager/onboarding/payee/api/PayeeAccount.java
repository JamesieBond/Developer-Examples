package com.tenx.fraudamlmanager.onboarding.payee.api;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PayeeAccount {
    private String id;

    private String identification;

    private String identificationType;

    private String name;
}
