package com.tenx.fraudamlmanager.cases.domain.external;

import com.tenx.fraudamlmanager.cases.domain.CaseAttribute;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExternalCase {

  public String caseType;
  public List<CaseAttribute> attributes = new ArrayList<>();

  private String bpmSystem;
  private String bpmSystemCaseId;
  private String colleagueId;
  private boolean displayToCustomer;
  private String status;
  private String outcome;

  private String primaryPartyKey;
  private String secondaryPartyKey;
  private String subscriptionKey;

  public void add(String name, String val) {

    if (val != null && !val.isEmpty()) {
      attributes.add(new CaseAttribute(name, val));
    }
  }

  public void add(String name, Boolean val) {

    if (val != null) {
      attributes.add(new CaseAttribute(name, Boolean.toString(val)));
    }
  }

  public void add(String name, Double val) {

    if (val != null) {
      attributes.add(new CaseAttribute(name, Double.toString(val)));
    }
  }

}
