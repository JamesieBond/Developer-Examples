package com.tenx.fraudamlmanager.deviceprofile.infrastructure;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeviceProfileEntityId implements Serializable {
  private static final long serialVersionUID = 1;
  private String partyKey;
  private String deviceKeyId;
}
