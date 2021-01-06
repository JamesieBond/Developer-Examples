package com.tenx.fraudamlmanager.cases.update.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Attribute {

  private static final String ATTRIBUTE_NAME_TRANSACTIONID = "transactionId";
  private static final String ATTRIBUTE_NAME_PROVISIONALOUTCOME = "provisionalOutcome";

  private String attributeName;

  private String attributeValue;

  public boolean isAttributeNameTransactionId() {
    return ATTRIBUTE_NAME_TRANSACTIONID.equals(attributeName);
  }

  public boolean isAttributeNameProvisionalOutcome() {
    return ATTRIBUTE_NAME_PROVISIONALOUTCOME.equals(attributeName);
  }

}
