package com.tenx.fraudamlmanager.cards.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class IndividualPartyInfo {

  public final static String STATUS_PROVISIONED = "PROVISIONED";

  private String partyKey;
  private String status;
  private List<IndividualAddress> individualAddressList = new ArrayList<>();

  public boolean isStatusProvisioned() {
    return STATUS_PROVISIONED.equals(status);
  }
}
