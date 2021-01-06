package com.tenx.fraudamlmanager.cases.infrastructure.governor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaseAttributeRequest {

  String attributeName;

  String attributeValue;

}