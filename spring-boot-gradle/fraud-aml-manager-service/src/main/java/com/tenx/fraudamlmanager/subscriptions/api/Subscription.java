package com.tenx.fraudamlmanager.subscriptions.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor(force = true)
public class Subscription {

  @JsonIgnore
  private String activeStatus = "ACTIVE";

  private String accountNumber;

  private String createdDate;

  private String updatedDate;

  private String parentSubscription;

  private String parentSubscriptionKey;

  private String productKey;

  private String productName;

  private String productType;

  private int productVersion;

  private String sortCode;

  private String subscriptionKey;

  private String subscriptionStatus;

  private String tenantKey;

  @Setter(AccessLevel.NONE)
  private String partyKey;

  private ProductDetails productDetails;

  private List<PartyRoles> partyRoles;

  public void setPartyRoles(List<PartyRoles> partyRoles) {
    this.partyRoles = partyRoles;

    if (partyRoles != null && partyRoles.size() > 0) {
      partyKey = partyRoles.get(0).getPartyKey();
    }
  }

  @JsonIgnore
  public boolean isSubscriptionActive() {
    return subscriptionStatus.equals(activeStatus);
  }

}
