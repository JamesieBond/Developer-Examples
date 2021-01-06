package com.tenx.fraudamlmanager.cases.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaseAttribute {

  private String attributeName;

  private String attributeValue;

}