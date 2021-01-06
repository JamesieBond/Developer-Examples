package com.tenx.fraudamlmanager.cases.v2.infrastructure;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaseAttributeV2Request {
    String attributeName;

    String attributeValue;

}