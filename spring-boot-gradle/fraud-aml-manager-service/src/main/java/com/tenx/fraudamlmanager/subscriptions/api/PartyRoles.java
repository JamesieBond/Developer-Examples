package com.tenx.fraudamlmanager.subscriptions.api;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force = true)
public class PartyRoles {

  private String createdDate;

  private String partyKey;

  private String partyRoleKey;

  private String tenantKey;

  private String role;

  private String updatedDate;

}
