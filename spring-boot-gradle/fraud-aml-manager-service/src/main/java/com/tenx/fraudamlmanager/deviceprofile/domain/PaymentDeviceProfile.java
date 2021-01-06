package com.tenx.fraudamlmanager.deviceprofile.domain;

import java.util.HashMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDeviceProfile {

  private String partyKey;

  private String deviceKeyId;

  private String eventType;

  private String sessionId;

  private HashMap<String, Object> deviceProfile;

  public boolean isDeviceKeyIdOrPartyKeyNotPresent(){
	return (partyKey == null || partyKey.isEmpty() || deviceKeyId== null || deviceKeyId.isEmpty());
  }

}
